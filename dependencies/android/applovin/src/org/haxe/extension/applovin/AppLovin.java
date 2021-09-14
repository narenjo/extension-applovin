package org.haxe.extension.applovin;

import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;

import org.haxe.extension.Extension;
import org.haxe.lime.HaxeObject;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AppLovin extends Extension {

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	private InterstitialAppLovin interstitial;
	private BannerAppLovin banner;
	private Map<String, RewardedAppLovin> rewardeds;
//	private Map<String, RewardedAd> rewardeds;
//	private AdView banner;
//	private RelativeLayout rl;
//	private AdRequest adReq;


	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	private static String interstitialId=null;

	//private static Boolean failRewarded=false;
	//private static Boolean loadingRewarded=false;
	private static String[] rewardedIds= null;

//	private static Boolean failBanner=false;
//	private static Boolean loadingBanner=false;
//	private static Boolean mustBeShowingBanner=false;
	private static String bannerId=null;
//	private static AdSize bannerSize=null;

	private static AppLovin instance=null;
	private static Boolean testingAds=false;
	private static Boolean tagForChildDirectedTreatment=false;
	private static int gravity=Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

	private static HaxeObject callback=null;

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	public static final String LEAVING = "LEAVING";
	public static final String FAILED = "FAILED";
	public static final String CLOSED = "CLOSED";
	public static final String DISPLAYING = "DISPLAYING";
	public static final String LOADED = "LOADED";
	public static final String LOADING = "LOADING";
	public static final String EARNED_REWARD = "EARNED_REWARD";
	public static final String CLICKED = "CLICKED";

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	public static AppLovin getInstance(){
		if(instance==null && bannerId!=null) instance = new AppLovin();
		if(bannerId==null){
			Log.e("AppLovin","You tried to get Instance without calling INIT first on AppLovin class!");
		}
		return instance;
	}


	public static void init(String bannerId, String interstitialId, String[] rewardedIds, String gravityMode, boolean testingAds, boolean tagForChildDirectedTreatment, HaxeObject callback){
		//testingAds = true;
		AppLovin.bannerId=bannerId;
		AppLovin.interstitialId=interstitialId;
		AppLovin.rewardedIds=rewardedIds;
		AppLovin.testingAds=testingAds;
		AppLovin.callback=callback;
		AppLovin.tagForChildDirectedTreatment=tagForChildDirectedTreatment;
		if(gravityMode.equals("TOP")){
			AppLovin.gravity=Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		}
		mainActivity.runOnUiThread(new Runnable() {
			public void run() { getInstance(); }
		});
	}


	public static void reportInterstitialEvent(final String event){
		if(callback == null) return;
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				callback.call1("_onInterstitialEvent",event);
			}
		});
	}

	public static void reportBannerEvent(final String event){
		if(callback == null) return;
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				callback.call1("_onBannerEvent",event);
			}
		});
	}

	public static void reportRewardedEvent(final String event){
		reportRewardedEvent(event, null);
	}
	public static void reportRewardedEvent(final String event, final String data){
		if(callback == null) return;
		mainActivity.runOnUiThread(new Runnable() {
			public void run() {
				callback.call2("_onRewardedEvent", event, data);
			}
		});
	}

	public static boolean showInterstitial() {
		if(getInstance().interstitial == null) return false;
		return getInstance().interstitial.showInterstitial();
	}

	public static boolean showRewarded(final String rewardedId) {
		if(getInstance().rewardeds == null || getInstance().rewardeds.isEmpty()) return false;
		if(getInstance().rewardeds.get(rewardedId) == null){
			reportRewardedEvent(AppLovin.FAILED);
			Log.d("AppLovin","Show Rewarded: Not loaded (THIS SHOULD NEVER BE THE CASE HERE!)... ignoring.");
			return false;
		}
		getInstance().rewardeds.get(rewardedId).showRewarded();
		return true;
	}

	public static void showBanner() {
		if(getInstance().banner == null) return;
		mainActivity.runOnUiThread(new Runnable() {
			public void run() { getInstance().banner.showBanner(); }
		});
	}

	public static void hideBanner() {
		if(getInstance().banner == null) return;
		mainActivity.runOnUiThread(new Runnable() {
			public void run() { getInstance().banner.hideBanner(); }
		});
	}

	public static void onResize(){
		if(getInstance().banner == null) return;
		mainActivity.runOnUiThread(new Runnable() {
			public void run() { getInstance().banner.onResize(); }
		});
	}

	public static float getBannerHeight(){ return getInstance().banner.getBannerHeight();}


	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	private AppLovin() {

		// Please make sure to set the mediation provider value to "max" to ensure proper functionality
		AppLovinSdk.getInstance( mainActivity ).setMediationProvider( "max" );
		AppLovinSdk.initializeSdk( mainActivity );

		if(!AppLovin.interstitialId.equals("")){
			interstitial = new InterstitialAppLovin(AppLovin.interstitialId);
		}
		if(!AppLovin.bannerId.equals("")){
			banner = new BannerAppLovin(AppLovin.bannerId, AppLovin.gravity);
		}

		if(rewardedIds != null && rewardedIds.length > 0){
			rewardeds = new HashMap<>();
			for(String rewardedId: rewardedIds) {
				rewardeds.put(rewardedId, new RewardedAppLovin(rewardedId));
			}
		}
	}
}
