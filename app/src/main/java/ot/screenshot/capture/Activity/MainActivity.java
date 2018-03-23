package ot.screenshot.capture.Activity;

import android.Manifest;
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
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import ot.screenshot.capture.Const;
import ot.screenshot.capture.R;
import ot.screenshot.capture.Service.ServiceCapture;
import ot.screenshot.capture.Util.ScreenshotManager;
import ot.screenshot.capture.Util.SharedPreferencesManager;
import pt.content.helper.RSHelper;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_PERMISSION = 786;

    private ScreenshotManager screenshotManager;

    private Button btnStart;
    private Switch swNotificationIcon, swOverlayIcon, swCameraButton, swShake;

    private SharedPreferencesManager sharedPreferencesManager;

    private Toast toast;

//    private AdView mAdView;
    private LinearLayout linearLayoutAds;
    private float adsHeight = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        setTheme();
        setContentView(ot.screenshot.capture.R.layout.activity_main);
        //check permission for require
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermission()) {
                requestPermission();
            }
        }
        AddControl();

        AddEvent();

//        load ADS
        if (!RSHelper.isPremium(this)) {
            MobileAds.initialize(this, getString(ot.screenshot.capture.R.string.appID));
            loadAds();
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
//            Log.d("S7", "onCreate: ");
        }
    }

    public void loadAds(){
        final float density  = getResources().getDisplayMetrics().density;
        final ViewTreeObserver observer= linearLayoutAds.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (adsHeight!=-1)return;
                        adsHeight = linearLayoutAds.getHeight()/density;
                        Log.d("Log", "onCreateHeight: "+adsHeight);

                        AdView adViewNE = new AdView(MainActivity.this);
                        Log.d("Log", "onCreate: "+((int)adsHeight));
                        if (adsHeight>252){
                            adViewNE.setAdSize(AdSize.MEDIUM_RECTANGLE);
                        }else if (adsHeight>102){
                            adViewNE.setAdSize(AdSize.LARGE_BANNER);
                        }else{
                            adViewNE.setAdSize(AdSize.SMART_BANNER);
                        }
//                        adViewNE.setAdSize(new AdSize(AdSize.FULL_WIDTH, (int) adsHeight-2));
                        adViewNE.setAdUnitId(getString(R.string.banner1));

                        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
                        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
                        linearLayoutAds.addView(adViewNE);
                        adViewNE.loadAd(adRequestBuilder.build());
                        Log.d("Log", "Width: " + linearLayoutAds.getWidth()/density);
                        Log.d("Log", "Height: " + linearLayoutAds.getHeight()/density);
                    }
                });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem itemList = menu.add(1, 1, 1, "List Image");

        itemList.setIcon(ot.screenshot.capture.R.drawable.ic_photo_16_18px);
        itemList.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        MenuItem item = menu.add(2, 2, 2, "Setting");

        item.setIcon(ot.screenshot.capture.R.drawable.ic_config_16px);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            Intent intent = new Intent(MainActivity.this, ImageViewerActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void AddControl() {
        btnStart = (Button) findViewById(ot.screenshot.capture.R.id.btnStart);
        swNotificationIcon = (Switch) findViewById(ot.screenshot.capture.R.id.swnotificationicon);
        swOverlayIcon = (Switch) findViewById(ot.screenshot.capture.R.id.swoverlayicon);
        swCameraButton = (Switch) findViewById(ot.screenshot.capture.R.id.swcamerabutton);
        swShake = (Switch) findViewById(ot.screenshot.capture.R.id.swshake);

        swNotificationIcon.setChecked(sharedPreferencesManager.getNotificationMode());
        swOverlayIcon.setChecked(sharedPreferencesManager.getOverlayIconMode());
        swCameraButton.setChecked(sharedPreferencesManager.getCameraButtonMode());
        swShake.setChecked(sharedPreferencesManager.getShakeMode());

//        mAdView = (AdView) findViewById(ot.screenshot.capture.R.id.adView);
        linearLayoutAds = (LinearLayout)findViewById(R.id.linearLayoutAds);


        if (isServiceRunning(ServiceCapture.class)) {
            btnStart.setText(getString(ot.screenshot.capture.R.string.stop));
            btnStart.setBackgroundResource(ot.screenshot.capture.R.drawable.roundedbutton_stop);
        } else {
            btnStart.setText(getString(ot.screenshot.capture.R.string.start));
            btnStart.setBackgroundResource(ot.screenshot.capture.R.drawable.roundedbutton_start);
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

                    if (checkNothingSelected()) {
                        //save file
                        sharedPreferencesManager.saveSetting(swNotificationIcon.isChecked(), swOverlayIcon.isChecked(), swCameraButton.isChecked(), swShake.isChecked());

                        screenshotManager.requestScreenshotPermission(MainActivity.this, 1);
                        startServiceCapture();
                        btnStart.setText(getString(ot.screenshot.capture.R.string.stop));
                        sharedPreferencesManager.startPressed(true);

                        //Change color
                        btnStart.setBackgroundResource(ot.screenshot.capture.R.drawable.roundedbutton_stop);
                    } else {
                        if (toast != null) {
                            toast.cancel();
                            toast = null;
                        }
                        toast = Toast.makeText(MainActivity.this,"Nothing has been selected",Toast.LENGTH_LONG);
                        toast.show();
                    }
                } else {
                    //true -> have some thing selected; false - > nothing selected
                    btnStart.setText(getString(ot.screenshot.capture.R.string.start));
                    sharedPreferencesManager.startPressed(false);

                    //stop service
                    stopService(new Intent(MainActivity.this, ServiceCapture.class));

                    //Change color button
                    btnStart.setBackgroundResource(ot.screenshot.capture.R.drawable.roundedbutton_start);
                }

            }
        });
    }

    private boolean checkNothingSelected(){
        if(swCameraButton.isChecked()) return true;
        else if(swNotificationIcon.isChecked()) return true;
        else if(swOverlayIcon.isChecked()) return true;
        else if(swShake.isChecked()) return true;

        return false;
    }

    private void startServiceCapture() {
        Intent i = new Intent(MainActivity.this, ServiceCapture.class);
        i.putExtra(getString(ot.screenshot.capture.R.string.save_notification_icon), swNotificationIcon.isChecked());
        i.putExtra(getString(ot.screenshot.capture.R.string.save_overlay_icon), swOverlayIcon.isChecked());
        i.putExtra(getString(ot.screenshot.capture.R.string.save_camera_button), swCameraButton.isChecked());
        i.putExtra(getString(ot.screenshot.capture.R.string.save_shake), swShake.isChecked());
        i.putExtra(getString(ot.screenshot.capture.R.string.savesilently_key), sharedPreferencesManager.getSaveSilently());
        i.putExtra(getString(ot.screenshot.capture.R.string.countdownValues_key), sharedPreferencesManager.getCountDown());
        i.putExtra(getString(ot.screenshot.capture.R.string.filename_key), sharedPreferencesManager.getFileName());
        //check location internal or external
        i.putExtra(getString(ot.screenshot.capture.R.string.savelocation_key), sharedPreferencesManager.getSaveLocation());
        i.putExtra(getString(ot.screenshot.capture.R.string.filetype_key), "PNG");

        //set action
        i.setAction(Const.ACTION_INIT);
        startService(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        screenshotManager.onActivityResult(resultCode,data);
    }

    private boolean checkPermission() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
            Log.d("Permission","not granted");
        } else {
            Log.d("Permission","granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if(grantResults.length>0)
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                     Log.d("Permission","granted");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RSHelper.showOnAction(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        RSHelper.onStart(this);
    }
    @Override
    public void onBackPressed() {
//        Toast.makeText(this, "Da chay on backpress", Toast.LENGTH_SHORT).show();
        if (RSHelper.showOnBackpress(this))
            super.onBackPressed();

    }
    @Override
    protected void onStop() {
        super.onStop();
        RSHelper.onStop(this);
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
