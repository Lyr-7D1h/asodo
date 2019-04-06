package com.example.ninja.Controllers.Settings;


import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Bundle;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.ninja.Domain.Global;
import com.example.ninja.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Application application;

    public SettingsFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public SettingsFragment(Application application) {
        this.application = application;
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);

        findPreference("cache_size").setOnPreferenceChangeListener((preference, o) -> {
            ((Global) application).setUnSynced();
            return true;
        });
    }

}
