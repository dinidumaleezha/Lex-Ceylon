package com.dinidu.lexceylon.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dinidu.lexceylon.Class.ShaderSpan;
import com.dinidu.lexceylon.Fragment.HomeFragment;
import com.dinidu.lexceylon.Fragment.LawAiFragment;
import com.dinidu.lexceylon.Fragment.NotificationFragment;
import com.dinidu.lexceylon.Fragment.SettingFragment;
import com.dinidu.lexceylon.MainActivity;
import com.dinidu.lexceylon.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class HomeActivity extends AppCompatActivity {
    TextView headerTitleView;
    ImageView profileBtn, menuBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        replaceFragment(new HomeFragment(), false);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profileBtn = findViewById(R.id.profileBtn);
        profileBtn.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        });

        menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MenuActivity.class));
        });

        setupNavButtons();
    }

    public void setupNavButtons() {
        RelativeLayout homeBtn = findViewById(R.id.homeBtn);
        RelativeLayout notificationsBtn = findViewById(R.id.notificationBtn);
        RelativeLayout lawAiBtn = findViewById(R.id.lawAiBtn);
        RelativeLayout settingsBtn = findViewById(R.id.settingBtn);
        RelativeLayout homeBg = findViewById(R.id.homeBg);
        RelativeLayout notificationsBg = findViewById(R.id.notificationBg);
        RelativeLayout lawAiBg = findViewById(R.id.lawAiBg);
        RelativeLayout settingsBg = findViewById(R.id.settingBg);

        ImageView homeIcon = findViewById(R.id.homeIcon);
        ImageView notiIcon = findViewById(R.id.notiIcon);
        ImageView lawIcon = findViewById(R.id.lawIcon);
        ImageView settingsIcon = findViewById(R.id.settingsIcon);

        headerTitleView = findViewById(R.id.headerTitleView);

        homeBtn.setOnClickListener(v -> {
            replaceFragment(new HomeFragment(), false);
            resetAllBg(homeBg, notificationsBg, lawAiBg, settingsBg, homeBg);
            updateIconColors(homeIcon, notiIcon, lawIcon, settingsIcon, homeIcon);
            updateHeaderTitle(" ");
        });

        notificationsBtn.setOnClickListener(v -> {
            replaceFragment(new NotificationFragment(), false);
            resetAllBg(homeBg, notificationsBg, lawAiBg, settingsBg, notificationsBg);
            updateIconColors(homeIcon, notiIcon, lawIcon, settingsIcon, notiIcon);
            updateHeaderTitle(getString(R.string.notification));
        });

        lawAiBtn.setOnClickListener(v -> {
            replaceFragment(new LawAiFragment(), false);
            resetAllBg(homeBg, notificationsBg, lawAiBg, settingsBg, lawAiBg);
            updateIconColors(homeIcon, notiIcon, lawIcon, settingsIcon, lawIcon);
            updateHeaderTitle("Law-Ai Assistant");
        });

        settingsBtn.setOnClickListener(v -> {
            replaceFragment(new SettingFragment(), false);
            resetAllBg(homeBg, notificationsBg, lawAiBg, settingsBg, settingsBg);
            updateIconColors(homeIcon, notiIcon, lawIcon, settingsIcon, settingsIcon);
            updateHeaderTitle(getString(R.string.settings));
        });
    }

    private void resetAllBg(RelativeLayout home, RelativeLayout noti, RelativeLayout law, RelativeLayout settings, RelativeLayout active) {
        RelativeLayout[] all = {home, noti, law, settings};

        for (RelativeLayout layout : all) {
            if (layout == active) {
                layout.setBackgroundResource(R.drawable.nav_active_bg);
                setCustomBackgroundAndSize(layout, 70, 40);
            } else {
                layout.setBackgroundResource(R.drawable.nav_circle_bg);
                setCustomBackgroundAndSize(layout, 40, 40);
            }
        }
    }

    public void setCustomBackgroundAndSize(RelativeLayout layout, int widthDp, int heightDp) {
        float scale = layout.getResources().getDisplayMetrics().density;
        int widthPx = (int) (widthDp * scale + 0.5f);
        int heightPx = (int) (heightDp * scale + 0.5f);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(widthPx, heightPx);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        layout.setLayoutParams(params);
    }

    private void updateIconColors(ImageView home, ImageView noti, ImageView law, ImageView settings, ImageView active) {
        ImageView[] all = {home, noti, law, settings};
        for (ImageView icon : all) {
            if (icon == active) {
                icon.setColorFilter(ContextCompat.getColor(this, R.color.buttonBackground), PorterDuff.Mode.SRC_IN);
            } else {
                icon.setColorFilter(ContextCompat.getColor(this, R.color.textPrimary), PorterDuff.Mode.SRC_IN);
            }
        }
    }

    private void updateHeaderTitle(String headerTitle) {
        headerTitleView.setText(headerTitle);
    }

    private void replaceFragment(Fragment fragment, boolean addToBackStack) {
        if (fragment == null) return;
        FragmentManager fragmentManager = HomeActivity.this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutHaome, fragment);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }
}