package com.example.ninja.Settings;


import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.preference.PreferenceFragmentCompat;

import com.example.ninja.R;

public class SettingsFragment extends PreferenceFragmentCompat {

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.preferences);
//    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);
    }


}
