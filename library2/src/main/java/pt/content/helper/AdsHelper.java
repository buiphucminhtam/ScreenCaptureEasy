package pt.content.helper;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.firebase.analytics.FirebaseAnalytics;


@SuppressWarnings("MissingPermission")
public class AdsHelper {
    public static void init(Context context, String appId) {
        MobileAds.initialize(context, appId);
    }


    public static void loadAds(final Context context, final NativeExpressAdView adView, final String position, final AdLoadFailListener listener) {
        adView.setVisibility(View.VISIBLE);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Bundle bundle = new Bundle();
                bundle.putString("load", "fail");
                bundle.putString("position", position);
                FirebaseAnalytics.getInstance(context).logEvent("ads", bundle);
                adView.setVisibility(View.GONE);
                if (listener != null)
                    listener.onAdLoadFail();
                Log.d("AdsHelper","onAdFailedToLoad :");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Bundle bundle = new Bundle();
                bundle.putString("load", "success");
                bundle.putString("position", position);
                FirebaseAnalytics.getInstance(context).logEvent("ads", bundle);
                Log.d("AdsHelper","onAdLoaded :");
            }

        });
        adView.loadAd(request);
    }
    public static void loadAdsWithoutGone(final Context context, final NativeExpressAdView adView, final String position, final AdLoadFailListener listener) {
        adView.setVisibility(View.VISIBLE);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Bundle bundle = new Bundle();
                bundle.putString("load", "fail");
                bundle.putString("position", position);
                FirebaseAnalytics.getInstance(context).logEvent("ads", bundle);
                adView.setVisibility(View.INVISIBLE);
                if (listener != null)
                    listener.onAdLoadFail();
                Log.d("AdsHelper","onAdFailedToLoad :");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Bundle bundle = new Bundle();
                bundle.putString("load", "success");
                bundle.putString("position", position);
                FirebaseAnalytics.getInstance(context).logEvent("ads", bundle);
                Log.d("AdsHelper","onAdLoaded :");
            }

        });
        adView.loadAd(request);
    }

    public static void loadAds(final Context context, final AdView adView, final String position, final AdLoadFailListener listener) {
        adView.setVisibility(View.VISIBLE);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Bundle bundle = new Bundle();
                bundle.putString("load", "fail");
                bundle.putString("position", position);
                FirebaseAnalytics.getInstance(context).logEvent("ads", bundle);
                adView.setVisibility(View.GONE);
                if (listener != null)
                    listener.onAdLoadFail();
                Log.d("AdsHelper","onAdFailedToLoad :");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Bundle bundle = new Bundle();
                bundle.putString("load", "success");
                bundle.putString("position", position);
                FirebaseAnalytics.getInstance(context).logEvent("ads", bundle);
                Log.d("AdsHelper","onAdLoaded :");
            }

        });
        adView.loadAd(request);
    }
    public static void loadAdsWithoutGone(final Context context, final AdView adView, final String position, final AdLoadFailListener listener) {
        adView.setVisibility(View.VISIBLE);
        AdRequest request = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Bundle bundle = new Bundle();
                bundle.putString("load", "fail");
                bundle.putString("position", position);
                FirebaseAnalytics.getInstance(context).logEvent("ads", bundle);
                adView.setVisibility(View.INVISIBLE);
                if (listener != null)
                    listener.onAdLoadFail();
                Log.d("AdsHelper","onAdFailedToLoad :");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Bundle bundle = new Bundle();
                bundle.putString("load", "success");
                bundle.putString("position", position);
                FirebaseAnalytics.getInstance(context).logEvent("ads", bundle);
                Log.d("AdsHelper","onAdLoaded :");
                listener.onLoadAds(adView);
            }

        });
        adView.loadAd(request);
    }

    public static void showFull(View container, View adsContainer) {
        container.setVisibility(View.INVISIBLE);
        adsContainer.setVisibility(View.VISIBLE);
        TranslateAnimation a = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 100, Animation.RELATIVE_TO_SELF, 0);
        a.setDuration(500);
        a.setFillAfter(true); //HERE
        adsContainer.setAnimation(a);
        adsContainer.animate();

    }

    public static interface AdLoadFailListener {
        void onAdLoadFail();
        void  onLoadAds(View adView);
    }
}
