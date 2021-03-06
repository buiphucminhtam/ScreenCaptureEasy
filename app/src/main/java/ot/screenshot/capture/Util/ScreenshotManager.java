package ot.screenshot.capture.Util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ot.screenshot.capture.R;

/**
 * Created by Tam on 10/18/2017.
 */

public class ScreenshotManager {
    private static final String TAG = "ScreenshotManager";
    private static final String SCREENCAP_NAME = "screencap";
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;;
    public static final ScreenshotManager INSTANCE = new ScreenshotManager();
    private Intent mIntent;
    private onSavedImageListener onSavedImageListener;
    private MediaProjection mediaProjection;
    private ImageReader imageReader;
    private VirtualDisplay virtualDisplay;

    public static ScreenshotManager getInstance() {
        return INSTANCE;
    }

    public void setOnSavedImageListener(ScreenshotManager.onSavedImageListener onSavedImageListener) {
        this.onSavedImageListener = onSavedImageListener;
    }

    public void requestScreenshotPermission(@NonNull Activity activity, int requestId) {
        if (mIntent == null) {
            Log.d(TAG, "requestScreenshotPermission");
            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            activity.startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), requestId);
        }

    }


    public void onActivityResult(int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (resultCode == Activity.RESULT_OK && data != null)
            mIntent = data;
        else mIntent = null;
    }

    @UiThread
    public boolean takeScreenshot(@NonNull final Context context, final String fileName, final String filePath, final String fileType) {
        if (mIntent == null)
            return false;
        final MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) context.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        try {
            mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, mIntent);
        } catch (IllegalStateException e) {
            Log.d(TAG, "takeScreenshot: mediaprojection already started");
        }
        if (mediaProjection == null)
            return false;
        final int density = context.getResources().getDisplayMetrics().densityDpi;
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        final int width = size.x, height = size.y;
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);
        virtualDisplay = mediaProjection.createVirtualDisplay(SCREENCAP_NAME, width, height, density, VIRTUAL_DISPLAY_FLAGS, imageReader.getSurface(), null, null);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onImageAvailable(final ImageReader reader) {
                Log.d("AppLog", "onImageAvailable");
                mediaProjection.stop();
                new AsyncTask<Void, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(final Void... params) {
                        Image image = null;
                        Bitmap bitmap = null;
                        try {
                            image = reader.acquireLatestImage();
                            if (image != null) {
                                Image.Plane[] planes = image.getPlanes();
                                ByteBuffer buffer = planes[0].getBuffer();
                                int pixelStride = planes[0].getPixelStride(), rowStride = planes[0].getRowStride(), rowPadding = rowStride - pixelStride * width;
                                bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                                bitmap.copyPixelsFromBuffer(buffer);
                                return bitmap;
                            }
                        } catch (Exception e) {
                            if (bitmap != null)
                                bitmap.recycle();
                            e.printStackTrace();
                        }
                        if (image != null)
                            image.close();
                        reader.close();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(final Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        Log.d("AppLog", "Got bitmap?" + (bitmap != null));
                        try {

                            DateFormat dateFormat = new SimpleDateFormat(fileName);
                            Date date = new Date();
                            File file = ImageSaver.getInstance().saveScreenshotToPicturesFolder(context, bitmap, dateFormat.format(date),filePath,fileType);

                            ToastManager.getInstanse().showToast(context, context.getString(R.string.saved), Toast.LENGTH_SHORT);

                            //Call back
                            if(onSavedImageListener!=null) onSavedImageListener.onSavedSuccess();
                            Log.d("ServiceCapture", "File != null");
                        } catch (Exception e) {
                            if(onSavedImageListener!=null) onSavedImageListener.onSavedFailed();
                            e.printStackTrace();
                        }
                    }
                }.execute();
            }
        }, null);
        mediaProjection.registerCallback(callback,null);
        return true;
    }

    private MediaProjection.Callback callback = new MediaProjection.Callback() {
        @Override
        public void onStop() {
            super.onStop();
            if (virtualDisplay != null)
                virtualDisplay.release();
            imageReader.setOnImageAvailableListener(null, null);
            mediaProjection.unregisterCallback(this);
        }
    };

    public void stopMediaProjection() {
        Log.d(TAG, "stopMediaProjection");
        if(mediaProjection!=null){
            if(callback!=null)
                mediaProjection.unregisterCallback(callback);
            mediaProjection.stop();
            mIntent = null;
        }
    }

    public interface onSavedImageListener{
        void onSavedSuccess();
        void onSavedFailed();
    }
}

