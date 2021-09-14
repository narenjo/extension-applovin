package org.haxe.extension.applovin;

import android.os.Handler;
import android.util.Log;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;

import java.util.concurrent.TimeUnit;

public class RewardedAppLovin implements MaxRewardedAdListener {
    private MaxRewardedAd ad;
    private int           retryAttempt;

    private String id = null;

    public RewardedAppLovin(String rewardedId) {

        id = rewardedId;

        ad = MaxRewardedAd.getInstance(id, AppLovin.mainActivity);
        ad.setListener(this);

        if (!id.equals("")) {
            // Load the first ad
            ad.loadAd();
            Log.d("AppLovin", "Reload Rewarded");
            AppLovin.reportRewardedEvent(AppLovin.LOADING);

        }
    }

    public boolean showRewarded() {
        Log.d("AppLovin", "Show Rewarded: Begins");
        if (!ad.isReady()) return false;

        if (id.equals("")) {
            Log.d("AppLovin", "Show Rewarded: RewardedID is empty... ignoring.");
            return false;
        }

        if (ad == null) {
            AppLovin.reportRewardedEvent(AppLovin.FAILED);
            Log.d("AppLovin", "Show Rewarded: Not loaded (THIS SHOULD NEVER BE THE CASE HERE!)... ignoring.");
            return false;
        }

        ad.showAd();
        Log.d("AppLovin", "Show Rewarded: Complete.");
        return true;
    }

    // MAX Ad Listener
    @Override
    public void onAdLoaded(final MaxAd maxAd)
    {
        // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'

        // Reset retry attempt
        retryAttempt = 0;
        AppLovin.reportRewardedEvent(AppLovin.LOADED);
        Log.d("AppLovin", "Received Rewarded!");
    }

    @Override
    public void onAdLoadFailed(final String adUnitId, final MaxError error)
    {
        // Rewarded ad failed to load
        AppLovin.reportRewardedEvent(AppLovin.FAILED);
        Log.d("AppLovin", "Fail to get Rewarded: " + error.getMessage());
        // We recommend retrying with exponentially higher delays up to a maximum delay (in this case 64 seconds)

        retryAttempt++;
        long delayMillis = TimeUnit.SECONDS.toMillis( (long) Math.pow( 2, Math.min( 6, retryAttempt ) ) );

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                ad.loadAd();
            }
        }, delayMillis );
    }

    @Override
    public void onAdDisplayFailed(final MaxAd maxAd, final MaxError error)
    {
        // Rewarded ad failed to display. We recommend loading the next ad
        ad.loadAd();
        // Called when fullscreen content failed to show.
        Log.d("AppLovin", "The ad failed to show.");
    }

    @Override
    public void onAdDisplayed(final MaxAd maxAd) {
        AppLovin.reportRewardedEvent(AppLovin.DISPLAYING);
        Log.d("AppLovin", "Displaying Rewarded");
    }

    @Override
    public void onAdClicked(final MaxAd maxAd) {
        AppLovin.reportRewardedEvent(AppLovin.CLICKED);
    }

    @Override
    public void onAdHidden(final MaxAd maxAd)
    {
        // rewarded ad is hidden. Pre-load the next ad
        ad.loadAd();
        AppLovin.reportRewardedEvent(AppLovin.CLOSED);
        Log.d("AppLovin", "Dismiss Rewarded");
    }

    @Override
    public void onRewardedVideoStarted(final MaxAd maxAd) {}

    @Override
    public void onRewardedVideoCompleted(final MaxAd maxAd) {}

    @Override
    public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward)
    {
        maxAd.
        // Rewarded ad was displayed and user should receive the reward
        String data = "{\"type\": \"" + maxReward.getLabel() + "\", \"amount\": \"" + maxReward.getAmount() +"\"}";
        AppLovin.reportRewardedEvent(AppLovin.EARNED_REWARD, data);
        Log.d("AppLovin", "User earned reward " + data);
    }
}
