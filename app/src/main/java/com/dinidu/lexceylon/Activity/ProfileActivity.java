package com.dinidu.lexceylon.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dinidu.lexceylon.R;
import com.dinidu.lexceylon.Utils.SharedPrefManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileName, profileEmail, userName, phoneText, emailText;
    private RelativeLayout deleteCard, backBtnContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPrefManager prefManager = new SharedPrefManager(ProfileActivity.this);

        profileName = findViewById(R.id.profileName);
        profileEmail = findViewById(R.id.profileEmail);
        emailText = findViewById(R.id.emailText);
        userName = findViewById(R.id.userName);
        phoneText = findViewById(R.id.phoneText);
        deleteCard = findViewById(R.id.deleteCard);
        backBtnContainer = findViewById(R.id.backBtnContainer);

        profileName.setText(prefManager.getName());
        profileEmail.setText(prefManager.getEmail());
        userName.setText(prefManager.getName());
        emailText.setText(prefManager.getEmail());

        backBtnContainer.setOnClickListener(v -> finish());

        deleteCard.setOnClickListener(v -> handleDeleteClick());
    }

    private void handleDeleteClick() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                            finish(); // Optionally navigate elsewhere
                        } else {
                            Toast.makeText(this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user signed in", Toast.LENGTH_SHORT).show();
        }
    }
}
