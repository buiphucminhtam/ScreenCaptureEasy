package com.minhtam.screencaptureeasy;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tam on 11/7/2017.
 */

public class ImageSaver {
    private static final ImageSaver imageSaver = new ImageSaver();

    public static ImageSaver getInstance() {
        return imageSaver;
    }

    public File saveScreenshotToPicturesFolder(Context context, Bitmap image, String filename, String filePath, String imageFormat)
            throws Exception {
        File bitmapFile = getOutputMediaFile(filename,filePath,imageFormat);
        if (bitmapFile == null) {
            throw new NullPointerException("Error creating media file, check storage permissions!");
        }
        FileOutputStream fos = new FileOutputStream(bitmapFile);
        if (imageFormat.equals("PNG")) {
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
        } else {
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        }

        fos.close();

        // Initiate media scanning to make the image available in gallery apps
        MediaScannerConnection.scanFile(context, new String[] { bitmapFile.getPath() },
                new String[] { "image/jpeg" }, null);
        return bitmapFile;
    }

    private File getOutputMediaFile(String filename, String path, String imageFormat) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
//        File mediaStorageDirectory = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//                        + File.separator);

        File mediaStorageDirectory = new File(path);
        // Create the storage directory if it does not exist
        if (!mediaStorageDirectory.exists()) {
            if (!mediaStorageDirectory.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile;
        String mImageName;
        if (imageFormat.equals("PNG")) {
            mImageName = filename +  ".png";
        } else {
            mImageName = filename +  ".jpg";
        }


        mediaFile = new File(mediaStorageDirectory.getPath() + File.separator + mImageName);
        return mediaFile;
    }
}
