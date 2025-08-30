package com.dinidu.lexceylon.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.dinidu.lexceylon.R;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupButtons();
    }

    private void setupButtons() {
        findViewById(R.id.backBtnContainer).setOnClickListener(view -> {
            onBackPressed();
            finish();
        });

        findViewById(R.id.rateUs).setOnClickListener(v ->
                openUrl("https://play.google.com/store/apps/details?id=com.boomstudio.capcuttemplate"));

        findViewById(R.id.privacy).setOnClickListener(v ->
                openUrl("https://sites.google.com/view/cap-template-capcut-template/home"));

        findViewById(R.id.contact).setOnClickListener(v ->
                openUrl("https://wa.me/+94765849851"));

        findViewById(R.id.feedback).setOnClickListener(v -> sendFeedback());

        findViewById(R.id.about).setOnClickListener(v ->
                openUrl("https://wa.me/+94765849851"));

        findViewById(R.id.invite).setOnClickListener(v -> shareApp());
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    private void sendFeedback() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String uriText = "mailto:" + Uri.encode("maleezha1975@gmail.com") +
                "?subject=" + Uri.encode("Feedback") + "&body=" + Uri.encode("");
        intent.setData(Uri.parse(uriText));
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    private void shareApp() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        String message = "🔥 Have fun with your friends! 🔥\n" +
                "📱 Try the latest CapCut templates now!\n" +
                "🚀 Download now:\n" +
                "👇👇👇\n" +
                "https://play.google.com/store/apps/details?id=com.boomstudio.capcuttemplate";

        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(shareIntent, "Share Via:"));
    }
}