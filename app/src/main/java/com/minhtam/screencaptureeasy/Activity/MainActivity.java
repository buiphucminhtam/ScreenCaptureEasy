package com.minhtam.screencaptureeasy.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.minhtam.screencaptureeasy.Const;
import com.minhtam.screencaptureeasy.R;
import com.minhtam.screencaptureeasy.Service.ServiceCapture;
import com.minhtam.screencaptureeasy.Util.ScreenshotManager;
import com.minhtam.screencaptureeasy.Util.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_PERMISSION = 786;

    private ScreenshotManager screenshotManager;

    private Button btnStart;
    private Switch swNotificationIcon, swOverlayIcon, swCameraButton, swShake;

    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        setTheme();
        setContentView(R.layout.activity_main);

        requestPermission();
        AddControl();
        AddEvent();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(1, 1, 1, "Setting");

        item.setIcon(R.drawable.ic_settings_white_24dp);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void AddControl() {
        btnStart = (Button) findViewById(R.id.btnStart);
        swNotificationIcon = (Switch) findViewById(R.id.swnotificationicon);
        swOverlayIcon = (Switch) findViewById(R.id.swoverlayicon);
        swCameraButton = (Switch) findViewById(R.id.swcamerabutton);
        swShake = (Switch) findViewById(R.id.swshake);

        swNotificationIcon.setChecked(sharedPreferencesManager.getNotificationMode());
        swOverlayIcon.setChecked(sharedPreferencesManager.getOverlayIconMode());
        swCameraButton.setChecked(sharedPreferencesManager.getCameraButtonMode());
        swShake.setChecked(sharedPreferencesManager.getShakeMode());

        if (isServiceRunning(ServiceCapture.class)) {
            btnStart.setText(getString(R.string.stop));
        } else {
            btnStart.setText(getString(R.string.start));
        }
//        isStart = sharedPreferencesManager.getIsStart();
//        if (isStart) {
//            btnStart.setText(getString(R.string.stop));
//        } else {
//            btnStart.setText(getString(R.string.start));
//        }

        screenshotManager = ScreenshotManager.getInstance();

    }

    private void AddEvent() {


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isServiceRunning(ServiceCapture.class)) {

                    sharedPreferencesManager.saveSetting(swNotificationIcon.isChecked(),swOverlayIcon.isChecked(),swCameraButton.isChecked(),swShake.isChecked());

                    screenshotManager.requestScreenshotPermission(MainActivity.this, 1);
                    startServiceCapture();
                    btnStart.setText(getString(R.string.stop));
                } else {
                    btnStart.setText(getString(R.string.start));

                    //stop service
                    stopService(new Intent(MainActivity.this, ServiceCapture.class));
                }



            }
        });
    }

    private void startServiceCapture() {
        Intent i = new Intent(MainActivity.this, ServiceCapture.class);
        i.putExtra(getString(R.string.save_notification_icon), swNotificationIcon.isChecked());
        i.putExtra(getString(R.string.save_overlay_icon), swOverlayIcon.isChecked());
        i.putExtra(getString(R.string.save_camera_button), swCameraButton.isChecked());
        i.putExtra(getString(R.string.save_shake), swShake.isChecked());
        i.putExtra(getString(R.string.savesilently_key), sharedPreferencesManager.getSaveSilently());
        i.putExtra(getString(R.string.countdownValues_key), sharedPreferencesManager.getCountDown());
        i.putExtra(getString(R.string.filename_key), sharedPreferencesManager.getFileName());
        //check location internal or external
        i.putExtra(getString(R.string.savelocation_key), sharedPreferencesManager.getSaveLocation());
        i.putExtra(getString(R.string.filetype_key), "PNG");

        //set action
        i.setAction(Const.ACTION_INIT);
        startService(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        screenshotManager.onActivityResult(resultCode,data);
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED )
             requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            Log.d("Permission","not granted");
        } else {
            Log.d("Permission","granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Log.d("Permission","granted");
        }
    }




    @Override
    protected void onStop() {
        super.onStop();
        finish();
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
}
