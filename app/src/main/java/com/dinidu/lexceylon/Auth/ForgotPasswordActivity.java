package com.dinidu.lexceylon.Auth;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dinidu.lexceylon.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText forgotEmailInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        forgotEmailInput = findViewById(R.id.forgotEmailInput);
        CardView resetPasswordBtn = findViewById(R.id.resetPasswordBtn);
        TextView backToLogin = findViewById(R.id.backToLogin);

        resetPasswordBtn.setOnClickListener(v -> {
            String email = forgotEmailInput.getText().toString().trim();

            if (email.isEmpty()) {
                forgotEmailInput.setError("Email required");
            } else {
                resetPassword(email);
            }
        });

        backToLogin.setOnClickListener(v -> {
            finish(); // Go back to previous screen
        });
    }

    private void resetPassword(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Reset link sent to " + email,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Failed to send reset email",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
