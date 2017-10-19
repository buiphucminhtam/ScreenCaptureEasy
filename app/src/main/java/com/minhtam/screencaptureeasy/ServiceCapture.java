package com.minhtam.screencaptureeasy;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;

import github.nisrulz.screenshott.ScreenShott;

public class ServiceCapture extends Service {
    private View rootView;
    private final String ACTION_SCREEN_CAPTURE_NOTIFICATION = "NOTIFICATION";

    public static ScreenshotManager screenshotManager;

    private View overlayIcon;
    private WindowManager mWindowManager;

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

        //check icon
        if (intent.getBooleanExtra(getApplicationContext().getString(R.string.save_notification_icon), false)) {
            showForegroundNotificationMode1(getApplicationContext().getString(R.string.taptoscreenshot));
        } else {
            showForegroundNotificationMode2(getApplicationContext().getString(R.string.running));
        }

        if (intent.getBooleanExtra(getApplicationContext().getString(R.string.save_overlay_icon), false)) {
            showOverlayIcon();
        }


        Log.d("ServiceCapture", "TAKE SCREEN SHOT");
        if (intent.getAction() != null) {
            if (intent.getAction().equals(ACTION_SCREEN_CAPTURE_NOTIFICATION)) {
                new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long l) {

                    }

                    @Override
                    public void onFinish() {
                        screenshotManager.takeScreenshot(getApplicationContext());
                    }
                }.start();

                Log.d("ServiceCapture", ACTION_SCREEN_CAPTURE_NOTIFICATION);
            }
        }


        return super.onStartCommand(intent, flags, startId);
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
        showTaskIntent.setAction(ACTION_SCREEN_CAPTURE_NOTIFICATION);
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
        Intent showTaskIntent = new Intent(getApplicationContext(), MainActivity.class);
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

    private void showOverlayIcon() {
        overlayIcon = LayoutInflater.from(this).inflate(R.layout.chathead_layout, null);

        //Add the view to the window.
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

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
                            overlayIcon.setVisibility(View.GONE);
                            screenshotManager.takeScreenshot(getApplicationContext());
                            new CountDownTimer(2000, 1000) {
                                @Override
                                public void onTick(long l) {

                                }

                                @Override
                                public void onFinish() {
                                    overlayIcon.setVisibility(View.VISIBLE);
                                }
                            }.start();
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
}
