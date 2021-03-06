package ot.screenshot.capture.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.github.angads25.filepicker.view.FilePickerPreference;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ot.screenshot.capture.R;
import ot.screenshot.capture.Util.SharedPreferencesManager;
import pt.content.helper.RSHelper;

public class SettingActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences prefs;
    private String defaultLocation;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        setTheme();

        //Setting up actionbar
        settingUpActionBar();


        addPreferencesFromResource(R.xml.setting);

        prefs = getPreferenceScreen().getSharedPreferences();
        ListPreference listCountDown = (ListPreference) findPreference(getString(ot.screenshot.capture.R.string.countdownValues_key));
        ListPreference listThemes = (ListPreference) findPreference(getString(ot.screenshot.capture.R.string.theme_key));
        ListPreference listFileName = (ListPreference) findPreference(getString(ot.screenshot.capture.R.string.filename_key));
        FilePickerPreference preference = (FilePickerPreference) findPreference(getString(ot.screenshot.capture.R.string.savelocation_key));
//        Preference preferenceAds = findPreference(getString(ot.screenshot.capture.R.string.key_ads));


        //Set show default
        listCountDown.setSummary(prefs.getString(getString(ot.screenshot.capture.R.string.countdownValues_key), getResources().getStringArray(ot.screenshot.capture.R.array.countdownValues)[0]));
        listThemes.setSummary(prefs.getString(getString(ot.screenshot.capture.R.string.theme_key),getResources().getStringArray(ot.screenshot.capture.R.array.themeValues)[0]));

        String value = prefs.getString(getString(ot.screenshot.capture.R.string.filename_key),getResources().getStringArray(ot.screenshot.capture.R.array.filename_values)[0]);
        int position = Integer.parseInt(value);
        Log.d("Test", position + "");
        listFileName.setSummary(getResources().getStringArray(ot.screenshot.capture.R.array.filename)[position]);

        //check location
        defaultLocation = new SharedPreferencesManager(this).getSaveLocation();

        //set default for file location
        preference.setSummary(prefs.getString(getString(ot.screenshot.capture.R.string.savelocation_key), defaultLocation));

//        Add ADS
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        mAdView.setAdUnitId(getString(R.string.banner2));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                AbsListView.LayoutParams.FILL_PARENT,
                AbsListView.LayoutParams.FILL_PARENT
        );
        params.setMargins(0, 50, 0, 50); //left,top,right,bottom

        ListView v = getListView();
        v.setLayoutParams(params);
        v.setForegroundGravity(Gravity.CENTER_HORIZONTAL);
        v.addHeaderView(mAdView);
        if (!RSHelper.isPremium(this)) {
            MobileAds.initialize(this, getString(ot.screenshot.capture.R.string.appID));
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
//            Log.d("S7", "onCreate: ");
        }

    }

    private void settingUpActionBar() {
        getLayoutInflater().inflate(ot.screenshot.capture.R.layout.toolbar, (ViewGroup)findViewById(android.R.id.content));
        Toolbar toolbar = (Toolbar)findViewById(ot.screenshot.capture.R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setActionBar(toolbar);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        int topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (int) getResources().getDimension(ot.screenshot.capture.R.dimen.padding20dp) - 18, getResources().getDisplayMetrics());
        getListView().setPadding(0, topMargin, 0, 0);
    }


    private void setTheme() {
        String[] arrayTheme = getResources().getStringArray(ot.screenshot.capture.R.array.themeValues);

        if (sharedPreferencesManager.getThemeType().equals(arrayTheme[0])) {
            setTheme(ot.screenshot.capture.R.style.LightTheme);
        } else if (sharedPreferencesManager.getThemeType().equals(arrayTheme[1])) {
            setTheme(ot.screenshot.capture.R.style.DarkTheme);
        } else {
            if (sharedPreferencesManager.getThemeType().equals(arrayTheme[2])) {
                setTheme(ot.screenshot.capture.R.style.BlackTheme);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference pref = findPreference(s);
        if (pref == null) return;
        switch (pref.getTitleRes()) {
            case ot.screenshot.capture.R.string.theme:
                pref.setSummary(prefs.getString(getResources().getString(ot.screenshot.capture.R.string.theme_key),getResources().getStringArray(ot.screenshot.capture.R.array.themeArray)[0]));
                break;
            case ot.screenshot.capture.R.string.countdownvaluetittle:
                ListPreference listCountDown = (ListPreference) findPreference(getString(ot.screenshot.capture.R.string.countdownValues_key));
                listCountDown.setSummary(prefs.getString(getString(ot.screenshot.capture.R.string.countdownValues_key),getResources().getStringArray(ot.screenshot.capture.R.array.countdownArray)[0]));
                break;
            case ot.screenshot.capture.R.string.saveLocation:
                pref.setSummary(prefs.getString(getString(ot.screenshot.capture.R.string.savelocation_key),defaultLocation));
                break;
            case ot.screenshot.capture.R.string.fileName:
                String value = prefs.getString(getString(ot.screenshot.capture.R.string.filename_key),getResources().getStringArray(ot.screenshot.capture.R.array.filename_values)[0]);
                int position = Integer.parseInt(value);
                pref.setSummary(getResources().getStringArray(ot.screenshot.capture.R.array.filename)[position]);
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
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
        if (RSHelper.showOnBackpress(this))
            super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        RSHelper.showOnAction(this);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RSHelper.onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RSHelper.onStop(this);
        finish();
    }
}
