package com.minhtam.screencaptureeasy.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.minhtam.screencaptureeasy.Const;
import com.minhtam.screencaptureeasy.R;
import com.minhtam.screencaptureeasy.Service.ServiceCapture;
import com.minhtam.screencaptureeasy.Util.SharedPreferencesManager;

/**
 * Created by Tam on 11/8/2017.
 */

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        sharedPreferencesManager = new SharedPreferencesManager(context);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startServiceCapture(context);
        }
    }

    private void startServiceCapture(Context context) {
        Intent i = new Intent(context, ServiceCapture.class);
        i.putExtra(context.getString(R.string.save_notification_icon), sharedPreferencesManager.getNotificationMode());
        i.putExtra(context.getString(R.string.save_overlay_icon), sharedPreferencesManager.getOverlayIconMode());
        i.putExtra(context.getString(R.string.save_camera_button), sharedPreferencesManager.getCameraButtonMode());
        i.putExtra(context.getString(R.string.save_shake), sharedPreferencesManager.getShakeMode());
        i.putExtra(context.getString(R.string.savesilently_key), sharedPreferencesManager.getSaveSilently());
        i.putExtra(context.getString(R.string.countdownValues_key), sharedPreferencesManager.getCountDown());
        i.putExtra(context.getString(R.string.filename_key), sharedPreferencesManager.getFileName());
        //check location internal or external
        i.putExtra(context.getString(R.string.savelocation_key), sharedPreferencesManager.getSaveLocation());
        i.putExtra(context.getString(R.string.filetype_key), "PNG");

        //set action
        i.setAction(Const.ACTION_INIT);
        context.startService(i);
    }
}