package ot.screenshot.capture.Util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by st on 3/4/2018.
 */

public class ToastManager {
    private static ToastManager toastManager = null;
    private static Toast toast;
    private ToastManager() {
    }

    public static ToastManager getInstanse() {
        if (toastManager == null) {
            toastManager = new ToastManager();
        }
        return toastManager;
    }

    public void showToast(Context context, String text, int time) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        toast = Toast.makeText(context, text, time);
        toast.show();
    }
}
