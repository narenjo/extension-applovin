package org.haxe.extension.applovin;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;

public class BannerAppLovin implements MaxAdViewAdListener {

    private static Boolean mustBeShowingBanner = false;
    private static Boolean fail = false;
    private static Boolean loading = false;
    private MaxAdView ad;
    private int heightPx;
    private RelativeLayout rl;

    private String id = null;

    private int gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;


    public BannerAppLovin(String bannerId, int gravityMode) {

        id = bannerId;
        gravity = gravityMode;

        ad = new MaxAdView(id, AppLovin.mainActivity);
        ad.setListener(this);

        initBanner();
    }

    private void initBanner() {
        if (loading) return;
        if (!"".equals(id)) {

            if (rl == null) { // if this is the first time we call this function
                rl = new RelativeLayout(AppLovin.mainActivity);
                rl.setGravity(gravity);
            } else {
                ViewGroup parent = (ViewGroup) rl.getParent();
                parent.removeView(rl);
                rl.removeView(ad);
//                ad.destroy();
            }

            // Load the first ad
            // Stretch to the width of the screen for banners to be fully functional
            int width = ViewGroup.LayoutParams.MATCH_PARENT;

            // Banner height on phones and tablets is 50 and 90, respectively
            int heightDp = (AppLovinSdkUtils.isTablet(AppLovin.mainContext)) ? 90 : 50;
            // heightPx = AppLovin.mainActivity.getResources().getDimensionPixelSize( heightDp );
            heightPx = AppLovinSdkUtils.dpToPx(AppLovin.mainActivity, heightDp);

            ad.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));

//            ViewGroup rootView = AppLovin.mainActivity.findViewById( android.R.id.content );
//            rootView.addView( ad );

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            AppLovin.mainActivity.addContentView(rl, params);
            rl.addView(ad);
            rl.bringToFront();

            loadBanner();
        }
    }

    private void loadBanner() {
        if ("".equals(id)) return;
        if (loading) return;

        loading = true;
        // Load the ad
        ad.loadAd();
        fail = false;

        Log.d("AppLovin", "load Banner");
        AppLovin.reportBannerEvent(AppLovin.LOADING);
    }

    public boolean showBanner() {

        if ("".equals(id)) return false;
        mustBeShowingBanner = true;
        if (fail) {
            loadBanner();
            return false;
        }
        Log.d("AppLovin", "Show Banner");

        // Set background or background color for banners to be fully functional
        ad.setBackgroundColor(Color.BLACK);
        ad.setVisibility(View.VISIBLE);
        ad.startAutoRefresh();

        Log.d("AppLovin", "Show Banner: Complete.");
        return true;
    }

    public void hideBanner() {
        if ("".equals(id)) return;
        mustBeShowingBanner = false;
        Log.d("AppLovin", "Hide Banner");
        ad.setBackgroundColor(Color.TRANSPARENT);
        ad.setVisibility(View.GONE);
        ad.stopAutoRefresh();
    }

    public void onResize() {
        Log.d("AppLovin", "On Resize");
        initBanner();
    }

    public float getBannerHeight() {
        return heightPx;
    }

    @Override
    public void onAdLoaded(MaxAd maxAd) {

        Log.d("AppLovin", "Received Banner!");
        ad.stopAutoRefresh();

        if (mustBeShowingBanner) {
            AppLovin.reportBannerEvent(AppLovin.LOADED);
            showBanner();
        } else {
            hideBanner();
        }
    }

    @Override
    public void onAdDisplayed(MaxAd ad) {
        /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */
    }

    @Override
    public void onAdHidden(MaxAd ad) {
        /* DO NOT USE - THIS IS RESERVED FOR FULLSCREEN ADS ONLY AND WILL BE REMOVED IN A FUTURE SDK RELEASE */
    }

    @Override
    public void onAdClicked(MaxAd ad) {
        AppLovin.reportBannerEvent(AppLovin.CLICKED);
    }

    @Override
    public void onAdLoadFailed(String adUnitId, MaxError error) {
        fail = true;
        loading = false;
        AppLovin.reportBannerEvent(AppLovin.FAILED);
        Log.d("AppLovin", "Fail to get Banner: " + error.getMessage());
    }

    @Override
    public void onAdDisplayFailed(MaxAd maxAd, MaxError error) {
        // Interstitial ad failed to display. We recommend loading the next ad
        ad.loadAd();
        // Called when fullscreen content failed to show.
        Log.d("AppLovin", "The banner failed to show.");
    }

    @Override
    public void onAdExpanded(MaxAd ad) {

    }

    @Override
    public void onAdCollapsed(MaxAd ad) {

    }
}
