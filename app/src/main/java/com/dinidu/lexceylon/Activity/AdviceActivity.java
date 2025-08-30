package com.dinidu.lexceylon.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dinidu.lexceylon.R;

public class AdviceActivity extends AppCompatActivity {

    private EditText emailBody;
    private CardView analyzeButtonCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advice);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RelativeLayout backBtnContainer = findViewById(R.id.backBtnContainer);
        backBtnContainer.setOnClickListener(v -> finish());

        emailBody = findViewById(R.id.email_body);
        analyzeButtonCard = findViewById(R.id.analyzeButtonCard);

        analyzeButtonCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = emailBody.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    Intent intent = new Intent(AdviceActivity.this, AdviceResultActivity.class);
                    intent.putExtra("user_input", userInput);
                    startActivity(intent);
                } else {
                    emailBody.setError("Please describe your issue");
                }
            }
        });
    }
}