package com.dinidu.lexceylon.Fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dinidu.lexceylon.Auth.LoginActivity;
import com.dinidu.lexceylon.Class.LanguageHelper;
import com.dinidu.lexceylon.MainActivity;
import com.dinidu.lexceylon.R;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SettingFragment extends Fragment {

   private MaterialSwitch switchDarkMode;
    private ImageView darkModeIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        applyFontId(root);

        switchDarkMode = root.findViewById(R.id.switchDarkMode);
        darkModeIcon = root.findViewById(R.id.darkModeIcon);

        SharedPreferences preferences = requireContext().getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);

        // Set initial switch and icon state
        switchDarkMode.setChecked(isDarkMode);
        updateIcon(isDarkMode);

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save preference
                SharedPreferences.Editor editor = requireContext().getSharedPreferences("settings", MODE_PRIVATE).edit();
                editor.putBoolean("dark_mode", isChecked);
                editor.apply();

                // Apply dark mode
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                // Update icon
                updateIcon(isChecked);

                // Recreate activity to apply theme
                requireActivity().recreate();
            }
        });

        LinearLayout logoutButton = root.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            // Go back to login screen
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        RelativeLayout languageBtn = root.findViewById(R.id.languageBtn);
        languageBtn.setOnClickListener(v -> showLanguageDialog());
        return root;
    }

    private void updateIcon(boolean isDark) {
        if (isDark) {
            darkModeIcon.setImageResource(R.drawable.light_mode_n); // sun
        } else {
            darkModeIcon.setImageResource(R.drawable.dark_mode_n); // moon
        }
    }
    private void showLanguageDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_language_picker);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        Typeface sinhalaFont = Typeface.createFromAsset(getResources().getAssets(), "fonts/FM-Abhaya.TTF");


        TextView btnEnglish = dialog.findViewById(R.id.btnEnglish);
        TextView btnSinhala = dialog.findViewById(R.id.btnSinhala);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        btnSinhala.setTypeface(sinhalaFont);

        btnEnglish.setOnClickListener(v -> {
            LanguageHelper.setLocale(requireActivity(), "en");
            restartApp();
            dialog.dismiss();
        });

        btnSinhala.setOnClickListener(v -> {
            LanguageHelper.setLocale(requireActivity(), "si");
            restartApp();
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void restartApp() {
        Intent intent = new Intent(requireActivity().getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void applyFontId(View root) {
        TextView textView = root.findViewById(R.id.tvSinhala);
        TextView textView1 = root.findViewById(R.id.tv1);
        TextView textView2 = root.findViewById(R.id.appearanceLabel);
        TextView textView3 = root.findViewById(R.id.tvUpdate);
        TextView textView4 = root.findViewById(R.id.tvPrivacy);
        //applyFontToTextViews(textView);
        //applyFontToTextViews(textView1);
        //applyFontToTextViews(textView2);
        //applyFontToTextViews(textView3);
        //applyFontToTextViews(textView4);
    }

    public void applyFontToTextViews(TextView tv) {
        Typeface sinhalaFont = Typeface.createFromAsset(getResources().getAssets(), "fonts/isi_malithi.TTF");
        Typeface englishFont = Typeface.createFromAsset(getResources().getAssets(), "fonts/main_font.ttf");

        Locale currentLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            currentLocale = getResources().getConfiguration().getLocales().get(0);
        } else {
            currentLocale = getResources().getConfiguration().locale;
        }

        if ("si".equals(currentLocale.getLanguage())) {
            tv.setTypeface(sinhalaFont);
        } else {
            tv.setTypeface(englishFont);
        }
    }
}
