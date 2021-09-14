package org.haxe.extension.applovin;

import android.os.Handler;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;

import java.util.concurrent.TimeUnit;

public class InterstitialAppLovin implements MaxAdListener {

    private MaxInterstitialAd ad;

    private int retryAttempt;

    private String id = null;


    public InterstitialAppLovin(String interstitialId) {

        id = interstitialId;

        ad = new MaxInterstitialAd(id, AppLovin.mainActivity);
        ad.setListener(this);

        if (!id.equals("")) {
            // Load the first ad
            ad.loadAd();
            Log.d("AppLovin", "Reload Interstitial");
            AppLovin.reportInterstitialEvent(AppLovin.LOADING);

        }
    }

    public boolean showInterstitial() {
        Log.d("AppLovin", "Show Interstitial: Begins");
        if (!ad.isReady()) return false;

        if (id.equals("")) {
            Log.d("AppLovin", "Show Interstitial: InterstitialID is empty... ignoring.");
            return false;
        }

        if (ad == null) {
            AppLovin.reportInterstitialEvent(AppLovin.FAILED);
            Log.d("AppLovin", "Show Interstitial: Not loaded (THIS SHOULD NEVER BE THE CASE HERE!)... ignoring.");
            return false;
        }

        ad.showAd();
        Log.d("AppLovin", "Show Interstitial: Complete.");
        return true;
    }


    @Override
    public void onAdLoaded(MaxAd ad) {
        // Reset retry attempt
        retryAttempt = 0;
        AppLovin.reportInterstitialEvent(AppLovin.LOADED);
        Log.d("AppLovin", "Received Interstitial!");
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        // Called when fullscreen content is shown.
        // Make sure to set your reference to null so you don't
        // show it a second time.
        AppLovin.reportInterstitialEvent(AppLovin.DISPLAYING);
        Log.d("AppLovin", "Displaying Interstitial");
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        // Interstitial ad is hidden. Pre-load the next ad
        this.ad.loadAd();
        AppLovin.reportInterstitialEvent(AppLovin.CLOSED);
        Log.d("AppLovin", "Dismiss Interstitial");
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        AppLovin.reportInterstitialEvent(AppLovin.CLICKED);
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        AppLovin.reportInterstitialEvent(AppLovin.FAILED);
        Log.d("AppLovin", "Fail to get Interstitial: " + error.getMessage());

        // Interstitial ad failed to load
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis((long) Math.pow(2, Math.min(6, retryAttempt)));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ad.loadAd();
            }
        }, delayMillis);
    }

    @Override
    public void onAdDisplayFailed(MaxAd ad, MaxError error) {
        // Interstitial ad failed to display. We recommend loading the next ad
        this.ad.loadAd();
        // Called when fullscreen content failed to show.
        Log.d("AppLovin", "The ad failed to show.");
    }

}
