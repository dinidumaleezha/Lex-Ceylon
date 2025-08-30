package com.dinidu.lexceylon.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "LexCeylonPrefs";
    private static final String KEY_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String KEY_UID = "user_uid";
    private static final String KEY_LOGGED_IN = "is_logged_in";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // ------------------- Set Methods -------------------
    public void setName(String name) {
        editor.putString(KEY_NAME, name).apply();
    }

    public void setEmail(String email) {
        editor.putString(KEY_EMAIL, email).apply();
    }

    public void setUID(String uid) {
        editor.putString(KEY_UID, uid).apply();
    }

    public void setLoggedIn(boolean loggedIn) {
        editor.putBoolean(KEY_LOGGED_IN, loggedIn).apply();
    }

    // ------------------- Get Methods -------------------
    public String getName() {
        return sharedPreferences.getString(KEY_NAME, null);
    }

    public String getEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    public String getUID() {
        return sharedPreferences.getString(KEY_UID, null);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    // ------------------- Clear Method -------------------
    public void clearAll() {
        editor.clear().apply();
    }
}
