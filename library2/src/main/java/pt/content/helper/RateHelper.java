package pt.content.helper;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
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

    public static boolean showOnBackpress(AppCompatActivity context) {

        if (RateHelper.count(context, "backpress") % 5 == 0 && !RateHelper.isRated(context) && enable(context)) {
            if (!RateHelper.show(context, true))
                return true;
            else return false;
        } else
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 0;
                while (count++ <= 30 && !loaded) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d("RateHelper", "run :" + loaded);
                Log.d("RateHelper", "run :" + count);
                if (loaded) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            show(context, false);
                        }
                    });

                }
            }
        }).start();
    }

    private static boolean show(final AppCompatActivity context, final boolean isBackpress) {
        final int level = PreferenceManager.getDefaultSharedPreferences(context).getInt("level", 0);
        if (level == 0)
            return false;
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("SHOW_TIME", true).apply();
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiHelper.get("http://45.77.28.5/tracking/dialog_count.php");
            }
        }).start();
        FancyAlertDialog.Builder alert = new FancyAlertDialog.Builder(context)
                .setBackgroundColor(android.R.color.white)
                .setTextTitle(getString(context, "title", R.string.dialog_title))
                .setTitleColor(R.color.dialog_title)


                .setTextSubTitle(getString(context, "sub_title", R.string.dialog_sub_title))
                .setSubtitleColor(R.color.dialog_sub_title)
                .setBody(getString(context, "body", R.string.dialog_body))
                .setBodyColor(R.color.dialog_body)
                .setNegativeColor(R.color.dialog_negative_button)
                .setNegativeButtonText(getString(context, "nev_bt", R.string.dialog_nev))
                .setOnNegativeClicked(new FancyAlertDialog.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButtonText(getString(context, "pos_bt", R.string.dialog_pos))
                .setPositiveColor(R.color.dialog_positive_button)
                .setOnPositiveClicked(new FancyAlertDialog.OnPositiveClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("RATED", true).apply();
                        if (level == 2)
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("PREMIUM", true).apply();
                        gotoPlay(context);
                        dialog.dismiss();
                    }
                })
                .setOnDismiss(new FancyAlertDialog.OnDismiss() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        if (isBackpress)
                            context.finish();
                        if (count(context, "DISMISS_COUNT") > 3)
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("SHOW_ENABLE", false).apply();

                    }
                })
                .build();
        alert.show();
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
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
    }

    private static String getString(Context context, String key, int defResource) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, context.getString(defResource));
    }


}
