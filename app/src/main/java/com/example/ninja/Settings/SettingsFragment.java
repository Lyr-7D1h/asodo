package com.example.ninja.Settings;


import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import com.example.ninja.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        setPreferencesFromResource(R.xml.preferences, s);
    }

}
