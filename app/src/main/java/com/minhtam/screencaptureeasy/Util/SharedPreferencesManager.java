package com.minhtam.screencaptureeasy.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.minhtam.screencaptureeasy.Const;
import com.minhtam.screencaptureeasy.R;

/**
 * Created by Tam on 10/19/2017.
 */

public class SharedPreferencesManager {
    private Context activity;
    private SharedPreferences sharedPreferences;

    private SharedPreferences settingPreferences;

    public SharedPreferencesManager(Context activity) {
        super();
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("savesetting",Context.MODE_PRIVATE);
        settingPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }


    public void saveSetting(boolean notification_mode, boolean overlayicon_mode, boolean camerabutton_mode, boolean shake_mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(activity.getString(R.string.save_notification_icon), notification_mode);
        editor.putBoolean(activity.getString(R.string.save_overlay_icon), overlayicon_mode);
        editor.putBoolean(activity.getString(R.string.save_camera_button), camerabutton_mode);
        editor.putBoolean(activity.getString(R.string.save_shake), shake_mode);
        editor.apply();
    }


    public boolean getNotificationMode() {
        return sharedPreferences.getBoolean(activity.getString(R.string.save_notification_icon),false);
    }

    public boolean getOverlayIconMode() {
        return sharedPreferences.getBoolean(activity.getString(R.string.save_overlay_icon),false);
    }

    public boolean getCameraButtonMode() {
        return sharedPreferences.getBoolean(activity.getString(R.string.save_camera_button),false);
    }

    public boolean getShakeMode() {
        return sharedPreferences.getBoolean(activity.getString(R.string.save_shake),false);
    }

    public boolean getSaveSilently() {
        return settingPreferences.getBoolean(activity.getString(R.string.savesilently_key), false);
    }

    public int getCountDown() {
        if (settingPreferences.getBoolean(activity.getString(R.string.countdown_key), false)) {
            return Integer.parseInt(settingPreferences.getString(activity.getString(R.string.countdownValues_key),"0"));
        } else {
            return 0;
        }
    }


    public String getSaveLocation() {
        return settingPreferences.getString(activity.getString(R.string.savelocation_key), Const.defaultLocationSDCard);
    }

    public String getFileName() {
        return settingPreferences.getString(activity.getString(R.string.filename_key),activity.getResources().getStringArray(R.array.filename)[0]);
    }

    public String getThemeType() {
        return settingPreferences.getString(activity.getString(R.string.theme_key),activity.getResources().getStringArray(R.array.themeValues)[0]);
    }
}
