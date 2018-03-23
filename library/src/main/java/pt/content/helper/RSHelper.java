package pt.content.helper;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
//out of date

public class RSHelper {
    public static boolean loaded = false;

    public static void showOnAction(AppCompatActivity context) {
    }
    public static void showOnAction(PreferenceActivity context) {
    }

    public static boolean showOnBackpress(AppCompatActivity context) {
            return true;
    }
    public static boolean showOnBackpress(PreferenceActivity context) {
            return true;
    }

    public static void onStart(Context context) {
    }

    public static void onStop(Context context) {

    }

    public static void actionStart(Context context) {
    }

    public static boolean isPremium(Context context) {
        return false;
    }

}
