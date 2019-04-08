package com.example.ninja.Controllers.Settings;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.util.LocaleUtils;
import com.example.ninja.R;

import java.util.Locale;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Application application;
    private Activity activity;

    public SettingsFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public SettingsFragment(Application application, Activity activity) {
        this.application = application;
        this.activity = activity;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);

        // Set default language
        findPreference("preferred_language").setDefaultValue(Locale.getDefault().getCountry());

        // Set language
        findPreference("preferred_language").setOnPreferenceChangeListener((preference, o) -> {//Change Application level locale
            // Set locale
            LocaleUtils.setNewLocale(activity, (String) o);

            //It is required to recreate the activity to reflect the change in UI.
            activity.recreate();

            // Return
            return true;
        });

        // Resync on settings change
        findPreference("cache_size").setOnPreferenceChangeListener((preference, o) -> {
            ((Global) application).setUnSynced();
            return true;
        });
    }

}
