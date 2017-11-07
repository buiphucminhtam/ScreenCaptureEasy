package com.minhtam.screencaptureeasy;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.io.File;

import safety.com.br.android_shake_detector.core.ShakeCallback;
import safety.com.br.android_shake_detector.core.ShakeDetector;
import safety.com.br.android_shake_detector.core.ShakeOptions;

public class ServiceCapture extends Service {
    private View rootView;


    public static ScreenshotManager screenshotManager;

    private View overlayIcon;
    private WindowManager mWindowManager;

    private ShakeDetector shakeDetector;

    private boolean saveSilently = false;
    private long countDownValue = 0;
    private final long delayOverlayIcon = 2000;
    private String fileName = "yyyyMMdd_hhmmss";
    private String filePath = Const.defaultLocation;
    private String fileType = "PNG";

    public ServiceCapture() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("ServiceCapture", "SERVICE CREATE");
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        if(intent == null || intent.getAction() == null) return START_STICKY;

        if (intent.getAction().equals(Const.ACTION_INIT)) {
            initScreenShoot(intent);
        }else
            if (intent.getAction().equals(Const.ACTION_SCREEN_CAPTURE_NOTIFICATION)) {
                startCaptureScreen();
            }
        else
            if (intent.getAction().equals(Const.ACTION_RUN_MAINACTIVITY)) {
                startActivity(new Intent(this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }




        return START_STICKY;
    }

    private void initScreenShoot(Intent intent) {

        //get values
        saveSilently = intent.getBooleanExtra(getString(R.string.savesilently_key), false);
        countDownValue = Long.parseLong(intent.getStringExtra(getString(R.string.countdownValues_key)));
        fileName = intent.getStringExtra(getString(R.string.filename_key));
        filePath = intent.getStringExtra(getString(R.string.savelocation_key));
        filePath = intent.getStringExtra(getString(R.string.filetype_key));

        //check icon
        if (intent.getBooleanExtra(getApplicationContext().getString(R.string.save_notification_icon), false)) {
            showForegroundNotificationMode1(getApplicationContext().getString(R.string.taptoscreenshot));
        } else {
            showForegroundNotificationMode2(getApplicationContext().getString(R.string.running));
        }

        if (intent.getBooleanExtra(getApplicationContext().getString(R.string.save_overlay_icon), false)) {
            showOverlayIcon();
        }

        if (intent.getBooleanExtra(getApplicationContext().getString(R.string.save_shake), false)) {
            ShakeOptions options = new ShakeOptions()
                    .background(true)
                    .interval(1000)
                    .shakeCount(1)
                    .sensibility(2.0f);

            shakeDetector = new ShakeDetector(options).start(this, new ShakeCallback() {
                @Override
                public void onShake() {
//                    if (mWindowManager != null) {
//                        mWindowManager.removeView(overlayIcon);
//                        screenshotManager.takeScreenshot(getApplicationContext(),fileName,filePath,fileType);
//                        new CountDownTimer(delayOverlayIcon, 1000) {
//                            @Override
//                            public void onTick(long l) {
//
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                mWindowManager.addView(overlayIcon,params);
//                            }
//                        }.start();
//                    }

                    startCaptureScreen();
                }
            });
        }


        if (intent.getBooleanExtra(getApplicationContext().getString(R.string.save_camera_button), false)) {
            //register broadcast receiver
            IntentFilter filter = new IntentFilter(Intent.ACTION_CAMERA_BUTTON);
            filter.addAction(Intent.ACTION_PACKAGE_ADDED);
            registerReceiver(receiver, filter);
        }
    }

    private void startCaptureScreen() {
        if (mWindowManager != null) {
            mWindowManager.removeView(overlayIcon);
        }


        new CountDownTimer(countDownValue, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                screenshotManager.takeScreenshot(getApplicationContext(),fileName,filePath,fileType);
            }
        }.start();


        new CountDownTimer(delayOverlayIcon, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                mWindowManager.addView(overlayIcon,params);
            }
        }.start();
        Log.d("ServiceCapture", Const.ACTION_SCREEN_CAPTURE_NOTIFICATION);
    }

//    public void takeScreenShot() {
////        rootView = ((Activity)getApplicationContext()).getWindow().getDecorView().findViewById(android.R.id.content).getRootView();
//
//
//        // RootView
//        Log.d("ServiceCapture", "rootView !=null");
//        Bitmap bitmap_rootview = ScreenShott.getInstance().takeScreenShotOfView(rootView);
//
//        try {
//            File file = ScreenShott.getInstance().saveScreenshotToPicturesFolder(getApplicationContext(), bitmap_rootview, "my_screenshot_filename");
//            Log.d("ServiceCapture", "File != null");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    private void showForegroundNotificationMode1(String contentText) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), ServiceCapture.class);
        showTaskIntent.setAction(Const.ACTION_SCREEN_CAPTURE_NOTIFICATION);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getService(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(1, notification);
    }


    private void showForegroundNotificationMode2(String contentText) {
        // Create intent that will bring our app to the front, as if it was tapped in the app
        // launcher
        Intent showTaskIntent = new Intent(getApplicationContext(), ServiceCapture.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getService(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText(contentText)
                .setSmallIcon(android.R.mipmap.sym_def_app_icon)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(1, notification);
    }

    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    //Add the view to the window.
    final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    private void showOverlayIcon() {
        overlayIcon = LayoutInflater.from(this).inflate(R.layout.chathead_layout, null);



        //Specify the view position
        params.gravity = Gravity.TOP | Gravity.LEFT;        //Initially view will be added to top-left corner
        params.x = 0;
        params.y = 100;

        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(overlayIcon, params);



        overlayIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (motionEvent.getRawX() - initialTouchX);
                        int Ydiff = (int) (motionEvent.getRawY() - initialTouchY);


                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
//                            mWindowManager.removeView(overlayIcon);
//                            screenshotManager.takeScreenshot(getApplicationContext());
//                            new CountDownTimer(2000, 1000) {
//                                @Override
//                                public void onTick(long l) {
//
//                                }
//
//                                @Override
//                                public void onFinish() {
//                                    mWindowManager.addView(overlayIcon,params);
//                                }
//                            }.start();

                            startCaptureScreen();
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        params.y = initialY + (int) (motionEvent.getRawY() - initialTouchY);


                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(overlayIcon, params);
                        return true;
                }
                return false;
            }
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(intent.ACTION_CAMERA_BUTTON)){
                startCaptureScreen();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        if(overlayIcon!=null)
            mWindowManager.removeView(overlayIcon);
    }
}
