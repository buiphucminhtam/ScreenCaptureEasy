package com.minhtam.screencaptureeasy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private ScreenshotManager screenshotManager;

    private Button btnStart;
    private Switch swNotificationIcon, swOverlayIcon, swCameraButton, swShake;

    private SharedPreferencesManager sharedPreferencesManager;

    private boolean isStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AddControl();
        AddEvent();
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

        sharedPreferencesManager = new SharedPreferencesManager(this);

        swNotificationIcon.setChecked(sharedPreferencesManager.getNotificationMode());
        swOverlayIcon.setChecked(sharedPreferencesManager.getOverlayIconMode());
        swCameraButton.setChecked(sharedPreferencesManager.getCameraButtonMode());
        swShake.setChecked(sharedPreferencesManager.getShakeMode());

        isStart = sharedPreferencesManager.getIsStart();
        if (isStart) {
            btnStart.setText(getString(R.string.stop));
        } else {
            btnStart.setText(getString(R.string.start));
        }

        screenshotManager = ScreenshotManager.getInstance();

    }

    private void AddEvent() {


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStart) {
                    isStart = true;

                    screenshotManager.requestScreenshotPermission(MainActivity.this, 1);

                    Intent i = new Intent(MainActivity.this, ServiceCapture.class);
                    i.putExtra(getString(R.string.save_notification_icon), swNotificationIcon.isChecked());
                    i.putExtra(getString(R.string.save_overlay_icon), swOverlayIcon.isChecked());
                    i.putExtra(getString(R.string.save_camera_button), swCameraButton.isChecked());
                    i.putExtra(getString(R.string.save_shake), swShake.isChecked());
                    i.putExtra(getString(R.string.savesilently_key), sharedPreferencesManager.getSaveSilently());
                    i.putExtra(getString(R.string.countdownValues_key), sharedPreferencesManager.getCountDown());
                    i.putExtra(getString(R.string.filename_key), sharedPreferencesManager.getFileName());
                    i.putExtra(getString(R.string.savelocation_key), sharedPreferencesManager.getSaveLocation());
                    i.putExtra(getString(R.string.filetype_key), "PNG");
                    startService(i);
                    ServiceCapture.screenshotManager = screenshotManager;

                    btnStart.setText(getString(R.string.stop));
                } else {
                    isStart = false;
                    btnStart.setText(getString(R.string.start));

                    //stop service
                    stopService(new Intent(MainActivity.this, ServiceCapture.class));
                }



            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        screenshotManager.onActivityResult(resultCode,data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        sharedPreferencesManager.saveSetting(swNotificationIcon.isChecked(),swOverlayIcon.isChecked(),swCameraButton.isChecked(),swShake.isChecked(),isStart);
    }
}
