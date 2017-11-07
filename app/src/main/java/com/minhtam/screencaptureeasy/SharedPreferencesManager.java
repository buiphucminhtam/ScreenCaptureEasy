package com.minhtam.screencaptureeasy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Tam on 10/19/2017.
 */

public class SharedPreferencesManager {
    private Activity activity;
    private SharedPreferences sharedPreferences;

    private SharedPreferences settingPreferences;

    public SharedPreferencesManager(Activity activity) {
        super();
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences("savesetting",Context.MODE_PRIVATE);
        settingPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }


    public void saveSetting(boolean notification_mode, boolean overlayicon_mode, boolean camerabutton_mode, boolean shake_mode, boolean isStart) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(activity.getString(R.string.save_notification_icon), notification_mode);
        editor.putBoolean(activity.getString(R.string.save_overlay_icon), overlayicon_mode);
        editor.putBoolean(activity.getString(R.string.save_camera_button), camerabutton_mode);
        editor.putBoolean(activity.getString(R.string.save_shake), shake_mode);
        editor.putBoolean(activity.getString(R.string.save_state),isStart);
        editor.commit();
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

    public boolean getIsStart() {
        return sharedPreferences.getBoolean(activity.getString(R.string.save_state), false);
    }

    public boolean getSaveSilently() {
        return settingPreferences.getBoolean(activity.getString(R.string.savesilently_key), false);
    }

    public int getCountDown() {
        if (settingPreferences.getBoolean(activity.getString(R.string.countdown_key), false)) {
            return Integer.parseInt(activity.getString(R.string.countdownValues_key));
        } else {
            return 0;
        }
    }


    public String getSaveLocation() {
        return settingPreferences.getString(activity.getString(R.string.savelocation_key),Const.defaultLocationSDCard);
    }

    public String getFileName() {
        return settingPreferences.getString(activity.getString(R.string.filename_key),activity.getResources().getStringArray(R.array.filename)[0]);
    }
}
