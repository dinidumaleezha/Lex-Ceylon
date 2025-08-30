package com.dinidu.lexceylon.Activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dinidu.lexceylon.Class.LanguageHelper;
import com.dinidu.lexceylon.Model.History;
import com.dinidu.lexceylon.R;
import com.dinidu.lexceylon.Utils.GeminiClient;
import com.dinidu.lexceylon.Utils.GeminiLegalResponse;
import com.dinidu.lexceylon.Utils.GeminiResponseParser;
import com.dinidu.lexceylon.Utils.PromptBuilder;
import com.dinidu.lexceylon.Utils.PromptBuilder_ENG;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdviceResultActivity extends AppCompatActivity {

    private ProgressBar progress;
    private LinearLayout container;
    private CardView copyButton;
    private GeminiLegalResponse lastResponse;

    // Firebase
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advice_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progress = findViewById(R.id.loadingProgress);
        container = findViewById(R.id.container);
        copyButton = findViewById(R.id.copyButton);

        // Firebase init
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("UserHistory");

        String q = getIntent().getStringExtra("user_input");
        askGemini(q);

        copyButton.setOnClickListener(v -> {
            if (lastResponse != null) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Legal Advice", getFullAdviceText(lastResponse));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, getString(R.string.copy_advice), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void askGemini(String userQuestion) {
        progress.setVisibility(ProgressBar.VISIBLE);
        container.removeAllViews();

        String prompt = null; // method-level variableString prompt = null;

        if (LanguageHelper.isSinhala(this)) {
            prompt = PromptBuilder.build(userQuestion);
        } else if (LanguageHelper.isEnglish(this)) {
            prompt = PromptBuilder_ENG.build(userQuestion);
        } else {
            Toast.makeText(this, "Other language", Toast.LENGTH_SHORT).show();
        }

        GeminiClient.ask(this, prompt, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progress.setVisibility(ProgressBar.GONE);
                    Toast.makeText(AdviceResultActivity.this,
                            String.format(getString(R.string.network_error), e.getMessage()),
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();
                try {
                    GeminiLegalResponse data = GeminiResponseParser.parse(body);
                    lastResponse = data;
                    runOnUiThread(() -> {
                        progress.setVisibility(ProgressBar.GONE);
                        renderResult(data);
                        // ✅ Save to Firebase History
                        saveHistoryToFirebase(userQuestion, getFullAdviceText(data));
                    });
                } catch (Exception ex) {
                    runOnUiThread(() -> {
                        progress.setVisibility(ProgressBar.GONE);
                        Toast.makeText(AdviceResultActivity.this,
                                String.format(getString(R.string.parse_error), ex), Toast.LENGTH_LONG).show();
                        finish();
                    });
                }
            }
        });
    }

    // 🔥 Save History to Firebase
    private void saveHistoryToFirebase(String userInput, String aiGeneratedMessage) {
        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference userHistoryRef = databaseReference.child(uid);

        String key = userHistoryRef.push().getKey();
        History history = new History(userInput, aiGeneratedMessage, System.currentTimeMillis());

        if (key != null) {
            userHistoryRef.child(key).setValue(history)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Saved to history!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    // ------------------- UI Builder -------------------
    private void renderResult(GeminiLegalResponse data) {
        container.removeAllViews();

        // Question
        addCardSection(getString(R.string.question), nonNull(data.question_si), R.color.card_question_bg);

        // Laws
        if (data.relevant_laws != null && !data.relevant_laws.isEmpty()) {
            for (GeminiLegalResponse.RelevantLaw law : data.relevant_laws) {
                String head = (law.act != null ? law.act : "") +
                        (TextUtils.isEmpty(law.year) ? "" : " (" + law.year + ")");
                addCardSection(getString(R.string.law), head, R.color.card_law_bg);
                if (law.sections != null) {
                    for (GeminiLegalResponse.Section s : law.sections) {
                        addBulletItem(getString(R.string.section_prefix) + nonNull(s.number), "📄");
                        addBulletItem(getString(R.string.title_prefix) + nonNull(s.title), "📝");
                        addBulletItem(getString(R.string.summary_prefix) + nonNull(s.summary), "📌");
                    }
                }
                addImportant(getString(R.string.confidence_prefix) + law.confidence);
            }
        } else {
            addCardSection(getString(R.string.law), "To be verified", R.color.card_law_bg);
        }

        // Advice
        if (data.advice != null) {
            addCardSection(getString(R.string.summary), nonNull(data.advice.summary), R.color.card_advice_bg);
            if (data.advice.steps != null && !data.advice.steps.isEmpty()) {
                int i = 1;
                for (String step : data.advice.steps) {
                    addStepItem(i, step);
                    i++;
                }
            }
            if (data.advice.warnings != null) {
                for (String w : data.advice.warnings) addBulletItem(getString(R.string.warning_prefix) + w, "⚠️");
            }
        }

        // Where to file
        if (data.where_to_file != null) {
            for (GeminiLegalResponse.WhereToFile w : data.where_to_file) {
                addCardSection(getString(R.string.authority), nonNull(w.authority), R.color.card_authority_bg);
                addBulletItem(getString(R.string.admin_level_prefix) + nonNull(w.office_level), "🏢");
                addBulletItem(getString(R.string.how_to_file_prefix) + nonNull(w.how_to_file), "📝");
                addBulletItem(getString(R.string.fee_prefix) + nonNull(w.fee), "💰");
                addBulletItem(getString(R.string.deadline_prefix) + nonNull(w.deadline), "⏰");
                if (w.documents != null) {
                    for (String d : w.documents) addBulletItem(getString(R.string.documents_prefix) + d, "📄");
                }
                addSmall(getString(R.string.contact_prefix) + nonNull(w.contact));
                addSmall(getString(R.string.location_prefix) + nonNull(w.location));
                addSmall(getString(R.string.online_url_prefix) + nonNull(w.online_url));
            }
        }

        // Disclaimer
        addCardSection(getString(R.string.disclaimer), nonNull(data.disclaimer), R.color.card_disclaimer_bg);
    }

    // ------------------- UI Builder -------------------
    private void addCardSection(String title, String content, int colorResId) {
        // Create CardView
        CardView card = new CardView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(16, 12, 16, 12);
        card.setLayoutParams(lp);
        card.setRadius(24); // smoother corners
        card.setCardElevation(10); // shadow
        //card.setCardBackgroundColor(ContextCompat.getColor(this, colorResId));
        card.setBackground(ContextCompat.getDrawable(this, R.drawable.input_bg));


        // Inner layout
        LinearLayout inner = new LinearLayout(this);
        inner.setOrientation(LinearLayout.VERTICAL);
        inner.setPadding(32, 24, 32, 24);

        // Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(20);
        tvTitle.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.textTitle));

        // Content
        TextView tvContent = new TextView(this);
        tvContent.setText(content);
        tvContent.setTextSize(16);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
        tvContent.setPadding(0, 12, 0, 0);

        // Add views
        inner.addView(tvTitle);
        inner.addView(tvContent);
        card.addView(inner);
        container.addView(card);
    }

    private void addBulletItem(String text, String icon) {
        TextView tv = new TextView(this);
        tv.setText(icon + " " + text);
        tv.setTextSize(15);
        tv.setTextColor(Color.DKGRAY);
        tv.setPadding(32, 8, 0, 8);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8);
        tv.setLayoutParams(params);

        container.addView(tv);
    }

    private void addStepItem(int number, String text) {
        TextView tv = new TextView(this);
        tv.setText("✅ " + number + ". " + text);
        tv.setTextSize(15);
        tv.setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
        tv.setPadding(32, 8, 0, 8);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8);
        tv.setLayoutParams(params);

        container.addView(tv);
    }

    private void addSmall(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(13);
        tv.setTextColor(Color.RED);
        tv.setPadding(32, 6, 0, 6);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 6, 16, 6);
        tv.setLayoutParams(params);

        container.addView(tv);
    }

    private void addImportant(String text) {
        TextView tv = new TextView(this);
        tv.setText(Html.fromHtml("<b>" + text + "</b>"));
        tv.setTextSize(14);
        //tv.setTextColor(Color.parseColor("#D32F2F"));
        tv.setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
        tv.setPadding(32, 8, 0, 8);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(16, 8, 16, 8);
        tv.setLayoutParams(params);

        container.addView(tv);
    }

    private String nonNull(String s) { return s == null ? "" : s; }

    private String getFullAdviceText(GeminiLegalResponse data) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.question)).append(": ").append(nonNull(data.question_si)).append("\n\n");

        if (data.relevant_laws != null) {
            for (GeminiLegalResponse.RelevantLaw law : data.relevant_laws) {
                sb.append(getString(R.string.law)).append(": ").append(nonNull(law.act)).append("\n");
                if (law.sections != null) {
                    for (GeminiLegalResponse.Section s : law.sections) {
                        sb.append("  - ").append(getString(R.string.section_prefix)).append(nonNull(s.number)).append("\n");
                        sb.append("  - ").append(getString(R.string.title_prefix)).append(nonNull(s.title)).append("\n");
                        sb.append("  - ").append(getString(R.string.summary_prefix)).append(nonNull(s.summary)).append("\n");
                    }
                }
                sb.append(getString(R.string.confidence_prefix)).append(law.confidence).append("\n\n");
            }
        }

        if (data.advice != null) {
            sb.append(getString(R.string.summary)).append(": ").append(nonNull(data.advice.summary)).append("\n");
            if (data.advice.steps != null) {
                int i = 1;
                for (String step : data.advice.steps) {
                    sb.append(i).append(". ").append(step).append("\n");
                    i++;
                }
            }
            if (data.advice.warnings != null) {
                for (String w : data.advice.warnings) sb.append(getString(R.string.warning_prefix)).append(w).append("\n");
            }
            sb.append("\n");
        }

        if (data.where_to_file != null) {
            for (GeminiLegalResponse.WhereToFile w : data.where_to_file) {
                sb.append(getString(R.string.authority)).append(": ").append(nonNull(w.authority)).append("\n");
                sb.append(getString(R.string.admin_level_prefix)).append(nonNull(w.office_level)).append("\n");
                sb.append(getString(R.string.how_to_file_prefix)).append(nonNull(w.how_to_file)).append("\n");
                sb.append(getString(R.string.fee_prefix)).append(nonNull(w.fee)).append("\n");
                sb.append(getString(R.string.deadline_prefix)).append(nonNull(w.deadline)).append("\n");
                if (w.documents != null) {
                    for (String d : w.documents) sb.append(getString(R.string.documents_prefix)).append(d).append("\n");
                }
                sb.append(getString(R.string.contact_prefix)).append(nonNull(w.contact)).append("\n");
                sb.append(getString(R.string.location_prefix)).append(nonNull(w.location)).append("\n");
                sb.append(getString(R.string.online_url_prefix)).append(nonNull(w.online_url)).append("\n\n");
            }
        }

        sb.append(getString(R.string.disclaimer)).append(": ").append(nonNull(data.disclaimer)).append("\n");
        return sb.toString();
    }
}
