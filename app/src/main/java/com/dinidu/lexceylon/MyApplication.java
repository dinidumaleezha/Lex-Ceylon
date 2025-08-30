package com.dinidu.lexceylon;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDelegate;

import com.dinidu.lexceylon.Class.LanguageHelper;

import java.util.Locale;

public class MyApplication extends Application {

    //public ImageView darkModeIcon;

    @Override
    public void onCreate() {
        super.onCreate();
        LanguageHelper.loadLocale(this);

        // Read saved dark mode preference
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean isDarkMode = preferences.getBoolean("dark_mode", false);

        // Set theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
           // darkModeIcon.setBackgroundResource(R.drawable.light_mode_icon);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
           // darkModeIcon.setBackgroundResource(R.drawable.dark_mode_icon);
        }
    }
}
