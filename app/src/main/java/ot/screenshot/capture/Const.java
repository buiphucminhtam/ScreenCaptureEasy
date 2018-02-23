package ot.screenshot.capture;

import android.os.Environment;

import java.io.File;

/**
 * Created by Tam on 11/7/2017.
 */

public class Const {
    public static final String ACTION_INIT = "initService";
    public static final String ACTION_SCREEN_CAPTURE_NOTIFICATION = "NOTIFICATION";
    public static final String ACTION_RUN_MAINACTIVITY = "mainactivity";
    public static final String defaultLocationSDCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator;

}
