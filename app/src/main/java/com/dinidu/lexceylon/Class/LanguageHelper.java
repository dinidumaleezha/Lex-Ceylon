package com.dinidu.lexceylon.Class;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageHelper {
    private static final String PREFS_NAME = "app_settings";
    private static final String KEY_LANGUAGE = "language";

    public static void setLocale(Context context, String langCode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, langCode).apply();

        Locale locale = new Locale(langCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
    }

    // Updated loadLocale to return the loaded language code
    public static String loadLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String language = prefs.getString(KEY_LANGUAGE, "en"); // default to English
        setLocale(context, language);
        return language;
    }

    // Convenience method to check if current language is Sinhala
    public static boolean isSinhala(Context context) {
        String lang = loadLocale(context);
        return lang.equals("si");
    }

    // Convenience method to check if current language is English
    public static boolean isEnglish(Context context) {
        String lang = loadLocale(context);
        return lang.equals("en");
    }
}
