package pt.content.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ApiHelper {

    public static String get(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (Exception e) {
            return "";
        }
    }


    public static void start(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String response = get("http://45.77.28.5/api.php?local=" + getCurrentLocate(context) + "&p=" + context.getPackageName().trim().toLowerCase());
                if (!response.trim().isEmpty())
                    save(context, response);
                RateHelper.loaded = true;
                Log.d("ApiHelper","run :load ok");
            }
        }).start();

    }

    public static void clear(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.contains("api_list"))
            return;
        try {
            JSONArray list = new JSONArray(preferences.getString("api_list", ""));
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            for (int i = 0; i < list.length(); i++) {
                editor.remove(list.getString(i));
            }
            editor.remove("api_list");
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("ApiHelper","clear :");


    }

    private static void save(Context context, String response) {
        try {
            JSONArray arr = new JSONArray(response);
            SharedPreferences.Editor preferences = PreferenceManager.getDefaultSharedPreferences(context).edit();
            JSONArray list = new JSONArray();
            for (int i = 0; i < arr.length(); i++) {
                JSONArray array = arr.getJSONArray(i);
                switch (array.getString(0)) {
                    case "boolean":
                        preferences.putBoolean(array.getString(1), Boolean.parseBoolean(array.getString(2)));
                        Log.d("ApiHelper", "save boolean :" + array.getString(1) + "-----" + array.getString(2));
                        break;
                    case "string":
                        preferences.putString(array.getString(1), array.getString(2));
                        Log.d("ApiHelper", "save string :" + array.getString(1) + "-----" + array.getString(2));
                        break;
                    case "int":
                        preferences.putInt(array.getString(1), array.getInt(2));
                        Log.d("ApiHelper", "save int :" + array.getString(1) + "-----" + array.getInt(2));
                        break;
                }
                list.put(array.getString(1));
            }
            preferences.putString("api_list", list.toString());
            preferences.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private static String getCurrentLocate(Context context) {
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String countryCode = manager.getNetworkCountryIso().toUpperCase();
        Log.d("LocateHelper", "getCurrentLocate:init " + countryCode);
        return countryCode;
    }
}
