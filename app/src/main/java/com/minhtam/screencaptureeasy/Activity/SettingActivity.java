package com.minhtam.screencaptureeasy.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.github.angads25.filepicker.view.FilePickerPreference;
import com.minhtam.screencaptureeasy.R;
import com.minhtam.screencaptureeasy.Util.SharedPreferencesManager;

public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;
    private String defaultLocation;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        setTheme();

        addPreferencesFromResource(R.xml.setting);
        prefs = getPreferenceScreen().getSharedPreferences();
        ListPreference listCountDown = (ListPreference) findPreference(getString(R.string.countdownValues_key));
        ListPreference listThemes = (ListPreference) findPreference(getString(R.string.theme_key));
        ListPreference listFileName = (ListPreference) findPreference(getString(R.string.filename_key));
        FilePickerPreference preference = (FilePickerPreference) findPreference(getString(R.string.savelocation_key));
        Preference preferenceAds = findPreference(getString(R.string.key_ads));


        //Set show default
        listCountDown.setSummary(prefs.getString(getString(R.string.countdownValues_key), getResources().getStringArray(R.array.countdownValues)[0]));
        listThemes.setSummary(prefs.getString(getString(R.string.theme_key),getResources().getStringArray(R.array.themeValues)[0]));

        String value = prefs.getString(getString(R.string.filename_key),getResources().getStringArray(R.array.filename_values)[0]);
        int position = Integer.parseInt(value);
        Log.d("Test", position + "");
        listFileName.setSummary(getResources().getStringArray(R.array.filename)[position]);

        //check location
        defaultLocation = new SharedPreferencesManager(this).getSaveLocation();

        //set default for file location
        preference.setSummary(prefs.getString(getString(R.string.savelocation_key), defaultLocation));

    }

    private void setTheme() {
        String[] arrayTheme = getResources().getStringArray(R.array.themeValues);

        if (sharedPreferencesManager.getThemeType().equals(arrayTheme[0])) {
            setTheme(R.style.LightTheme);
        } else if (sharedPreferencesManager.getThemeType().equals(arrayTheme[1])) {
            setTheme(R.style.DarkTheme);
        } else {
            if (sharedPreferencesManager.getThemeType().equals(arrayTheme[2])) {
                setTheme(R.style.BlackTheme);
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference pref = findPreference(s);
        if (pref == null) return;
        switch (pref.getTitleRes()) {
            case R.string.theme:
                pref.setSummary(prefs.getString(getResources().getString(R.string.theme_key),getResources().getStringArray(R.array.themeArray)[0]));
                break;
            case R.string.countdownvaluetittle:
                ListPreference listCountDown = (ListPreference) findPreference(getString(R.string.countdownValues_key));
                listCountDown.setSummary(prefs.getString(getString(R.string.countdownValues_key),getResources().getStringArray(R.array.countdownArray)[0]));
                break;
            case R.string.saveLocation:
                pref.setSummary(prefs.getString(getString(R.string.savelocation_key),defaultLocation));
                break;
            case R.string.fileName:
                String value = prefs.getString(getString(R.string.filename_key),getResources().getStringArray(R.array.filename_values)[0]);
                int position = Integer.parseInt(value);
                pref.setSummary(getResources().getStringArray(R.array.filename)[position]);
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

    //Method to check if the service is running
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}
