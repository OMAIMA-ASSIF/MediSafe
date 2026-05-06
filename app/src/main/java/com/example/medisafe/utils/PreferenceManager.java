package com.example.medisafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "medisafe_prefs";
    private static final String KEY_ONBOARDING = "onboarding_completed";
    private static PreferenceManager instance;
    private final SharedPreferences prefs;

    private PreferenceManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) instance = new PreferenceManager(context);
        return instance;
    }

    public void setOnboardingCompleted(boolean completed) {
        prefs.edit().putBoolean(KEY_ONBOARDING, completed).apply();
    }

    public boolean isOnboardingCompleted() {
        return prefs.getBoolean(KEY_ONBOARDING, false);
    }
}