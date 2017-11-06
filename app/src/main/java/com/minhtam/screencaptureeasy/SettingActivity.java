package com.minhtam.screencaptureeasy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;
    private final String defaultLocation = "/storage/emulated/0/Pictures/Screenshots";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);

        prefs = getPreferenceScreen().getSharedPreferences();

        ListPreference listCountDown = (ListPreference) findPreference(getString(R.string.countdownValues_key));
        ListPreference listThemes = (ListPreference) findPreference(getString(R.string.countdownValues_key));

        //check count down to hide, show list cd
        if (prefs.getBoolean(getString(R.string.countdown_key), false)) {
            listCountDown.setSummary(prefs.getString(getString(R.string.countdownValues_key),getResources().getStringArray(R.array.countdownValues)[0]) + " seconds");
        } else {
            listCountDown.setValueIndex(0);
        }

        listThemes.setSummary(prefs.getString(getString(R.string.theme_key),getResources().getStringArray(R.array.themeArray)[0]));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference pref = findPreference(s);
        if (pref == null) return;
        switch (pref.getTitleRes()) {
            case R.string.theme:
                pref.setSummary(prefs.getString(getResources().getString(R.string.theme_key),getResources().getStringArray(R.array.themeArray)[0]));
                break;
            case R.string.language:
                pref.setSummary(prefs.getString(getResources().getString(R.string.language_key),getResources().getStringArray(R.array.languageArray)[0]));
                break;
            case R.string.countdown:
                pref.setSummary(prefs.getString(getString(R.string.countdownValues_key),getResources().getStringArray(R.array.countdownValues)[0]) + " seconds");
                break;
            case R.string.saveLocation:
                pref.setSummary(prefs.getString(getString(R.string.savelocation_key),defaultLocation));
                break;
            case R.string.fileName:
                pref.setSummary(prefs.getString(getString(R.string.filename_key),getResources().getStringArray(R.array.filename)[0]));
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
}
