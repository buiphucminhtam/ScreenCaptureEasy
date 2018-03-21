package pt.content.helper;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import pt.content.R;
import pt.content.lib.FancyAlertDialog;


/**
 * cach su dung
 * trong MainActivity
 *
 * @Override protected void showOnAction() {
 * super.showOnAction();
 * RateHelper.showOnAction(this);
 * }
 * @Override public void onBackPressed() {
 * if (RateHelper.showOnBackpress(this))
 * super.onBackPressed();
 * }
 * vi tri bat dau review
 * RateHelper.actionStart(<context>)
 */


public class RateHelper {
    public static boolean loaded = false;

    private static int count(Context context, String key) {
        int result = get(context, key);
        result++;
        set(context, key, result);
        return result;
    }

    private static void set(Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(key, value).apply();
    }

    private static int get(Context context, String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0);
    }

    public static void showOnAction(AppCompatActivity context) {
        Log.d("RateHelper", "showOnAction :");
        if (!RateHelper.isRated(context) && !RateHelper.hadShow(context) && RateHelper.checkShow(context) && enable(context)) {
            Log.d("RateHelper", "showOnAction : inside");
            RateHelper.waitToShow(context);
        }
    }
    public static void showOnAction(PreferenceActivity context) {
        Log.d("RateHelper", "showOnAction :");
        if (!RateHelper.isRated(context) && !RateHelper.hadShow(context) && RateHelper.checkShow(context) && enable(context)) {
            Log.d("RateHelper", "showOnAction : inside");
            RateHelper.waitToShow(context);
        }
    }

    private static void waitToShow(final PreferenceActivity context) {
    }
    private static boolean show(final PreferenceActivity context, final boolean isBackpress) {
        return true;
    }

    public static boolean showOnBackpress(AppCompatActivity context) {
            return true;
    }
    public static boolean showOnBackpress(PreferenceActivity context) {
            return true;
    }

    public static void onStart(Context context) {

        loaded = false;
        if (!isRated(context) && enable(context))
            ApiHelper.start(context);
    }

    public static void onStop(Context context) {
        if (enable(context))
            ApiHelper.clear(context);
        loaded = false;

    }

    public static void actionStart(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("SHOW_REVIEW", true).apply();
    }

    private static boolean checkShow(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("SHOW_REVIEW", false);
    }

    private static boolean enable(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("SHOW_ENABLE", true);
    }

    private static boolean isRated(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("RATED", false);
    }

    public static boolean isPremium(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("PREMIUM", false);
    }

    private static boolean hadShow(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("SHOW_TIME", false);
    }

    private static void waitToShow(final AppCompatActivity context) {
    }



    private static boolean show(final AppCompatActivity context, final boolean isBackpress) {
        return true;
    }

    private static void gotoPlay(Context context) {
        try {
            Intent rateIntent = rateIntentForUrl(context, "market://details");
            context.startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl(context, "https://play.google.com/store/apps/details");
            context.startActivity(rateIntent);
        }
    }

    private static Intent rateIntentForUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, context.getPackageName())));
        return intent;
    }

    private static String getString(Context context, String key, int defResource) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, context.getString(defResource));
    }


}
