package com.dinidu.lexceylon.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dinidu.lexceylon.Model.History;
import com.dinidu.lexceylon.R;
import com.dinidu.lexceylon.Utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PushEmailActivity extends AppCompatActivity {

    private EditText emailBody;
    private Button sendButton;
    private Spinner contextSelector;

    private boolean emailGenerated = false;
    private String savedSubject = "Customer Inquiry";
    private String historysubject;
    private String lastUserInput = "";

    SharedPrefManager prefManager;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_push_email);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RelativeLayout backBtnContainer = findViewById(R.id.backBtnContainer);
        backBtnContainer.setOnClickListener(v -> finish());

        // Firebase Auth & DB Reference
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserHistory");

        contextSelector = findViewById(R.id.cyber_crime_contexts);
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item_custom,
                getResources().getStringArray(R.array.cyber_crime_contexts)
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom);
        contextSelector.setAdapter(adapter);

        contextSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedText = (TextView) view;
                if (position == 0) {
                    selectedText.setError("Required");
                    selectedText.setTextColor(Color.RED);
                } else {
                    selectedText.setError(null);
                    selectedText.setTextColor(ContextCompat.getColor(PushEmailActivity.this, R.color.textPrimary));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // no action
            }
        });

        prefManager = new SharedPrefManager(PushEmailActivity.this);
        emailBody = findViewById(R.id.email_body);
        sendButton = findViewById(R.id.send_button);

        sendButton.setOnClickListener(v -> {
            if (!emailGenerated) {
                String selectedContext = contextSelector.getSelectedItem().toString();
                if (selectedContext.equals("Select a team")) {
                    Toast.makeText(this, "Please select a valid team", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userInput = emailBody.getText().toString().trim();
                if (userInput.isEmpty()) {
                    Toast.makeText(this, "Please enter your message", Toast.LENGTH_SHORT).show();
                    return;
                }

                lastUserInput = userInput;  // Save for history

                sendButton.setEnabled(false);
                sendButton.setText("Generating email...");

                String prompt = "Write a professional email for the following message related to '" + selectedContext + "': \"" + userInput +
                        "\". Return the subject line first in this format:\nSubject: <subject>\n\n<email body>";

//                String prompt = "Write a professional email for the following message related to '" + "': \"" + userInput +
//                        "\". Return the subject line first in this format:\nSubject: <subject>\n\n<email body>";


                generateEmailFromGemini(prompt);
            } else {
                sendEmailIntent(savedSubject, emailBody.getText().toString());
            }
        });
    }

    private void generateEmailFromGemini(String prompt) {
        new Thread(() -> {
            try {
                URL url = new URL(getString(R.string.gemini_api_key));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject json = new JSONObject();
                JSONArray contents = new JSONArray();
                JSONObject part = new JSONObject();
                part.put("text", prompt);
                JSONObject content = new JSONObject();
                content.put("parts", new JSONArray().put(part));
                contents.put(content);
                json.put("contents", contents);

                OutputStream os = conn.getOutputStream();
                os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                os.close();

                InputStream in = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject responseJson = new JSONObject(result.toString());
                String generatedText = responseJson
                        .getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");

                String subject = "Customer Inquiry";
                String body = generatedText;

                if (generatedText.startsWith("Subject:")) {
                    int endOfSubject = generatedText.indexOf("\n");
                    if (endOfSubject > 0) {
                        historysubject = generatedText.substring(8, endOfSubject).trim();
                        subject = generatedText.substring(8, endOfSubject).trim();
                        body = generatedText.substring(endOfSubject).trim();
                    }
                }

                String finalSubject = subject;
                String finalBody = body;

                runOnUiThread(() -> {
                    emailBody.setText(finalBody);
                    sendButton.setEnabled(true);
                    sendButton.setText("Send Email");
                    emailGenerated = true;
                    savedSubject = finalSubject;
                    saveHistoryToFirebase(historysubject, finalBody);
                    Toast.makeText(this, "Email & subject generated!", Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    sendButton.setEnabled(true);
                    sendButton.setText("Try Again");
                    Toast.makeText(this, "Failed to generate email", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void saveHistoryToFirebase(String historysubject, String aiGeneratedMessage) {
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference userHistoryRef = databaseReference.child(uid);

        String key = userHistoryRef.push().getKey();
        History history = new History(historysubject, aiGeneratedMessage, System.currentTimeMillis());

        if (key != null) {
            userHistoryRef.child(key).setValue(history)
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "History saved!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to save history!", Toast.LENGTH_SHORT).show());
        }
    }

    private void sendEmailIntent(String subject, String body) {
        String selectedContext = contextSelector.getSelectedItem().toString();
        String emailAddress = "support@lexceylon.com"; // default email

        switch (selectedContext) {
            case "Google":
                emailAddress = "support@google.com";
                break;
            case "Facebook":
                emailAddress = "support@facebook.com";
                break;
            case "Twitter / X":
                emailAddress = "support@twitter.com";
                break;
            case "TikTok":
                emailAddress = "support@tiktok.com";
                break;
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(intent, "Send email using..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "No email app found.", Toast.LENGTH_SHORT).show();
        }
    }
}