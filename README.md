# extension-applovin

OpenFL extension for "AppLovin Max" on Android.
This extension allows you to easily integrate Google AppLovin Max on your OpenFL (or HaxeFlixel) game / application.


Starting with Android SDK 17.0.0, you must set the app id in your project.xml
```xml
<setenv name="APPLOVIN_SDK_KEY" value="XXXXX123457" if="android"/>
```

### Main Features

* Banners & Interstitial & Rewarded Support.
* Setup your banners to be on top or on the bottom of the screen.
* Allows you to specify min amount of time between interstitial displays (to avoid annoying your users).
* Allows you to specify min amount of calls to interstitial before it actually gets displayed (to avoid annoying your users).
* Callback support for Interstitial Events.

### Simple use Example

```haxe
// This example show a simple use case.

import extension.applovin.AppLovin;
import extension.applovin.GravityMode;

class MainClass {

	function new() {


		// If you want to get instertitial events (LOADING, LOADED, CLOSED, DISPLAYING, ETC), provide
		// some callback function for this.
		AppLovin.onInterstitialEvent = onInterstitialEvent;
		
		// then call init with Android banner IDs in the main method.
		// parameters are (bannerId:String, interstitialId:String, rewardedIds:Array<String>, gravityMode:GravityMode).
		// if you don't have the bannerId and interstitialId, go to https://dash.applovin.com/o/mediation/ad_units/ to create them.

		AppLovin.initAndroid("XXXXX123456","XXXXX123457", ["XXXXX123454", "XXXXX123455"], GravityMode.BOTTOM); // may also be GravityMode.TOP

		// NOTE: If your game allows screen rotation, you should call AppLovin.onResize(); when rotation happens.
	}
	
	function gameOver() {
		// some implementation
		AppLovin.showInterstitial(0);

		/* NOTE:
		showInterstitial function has two parameters you can use to control how often you want to display the interstitial ad.

		public static function showInterstitial(minInterval:Int=60, minCallsBeforeDisplay:Int=0);

		* The banner will not show if it was displayed less than "minInterval" seconds ago.
		* The banner will show only after "#minCallsBeforeDisplay" calls to showInterstitial function.

		- To display an interstitial after every time the game finishes, call:
		AppLovin.showInterstitial(0);
		- To avoid displaying the interstitial if the game was too short (60 seconds), call:
		AppLovin.showInterstitial(60);
		- To display an interstitial every 3 finished games call:
		AppLovin.showInterstitial(0,3);
		- To display an interstitial every 3 finished games (but never before 120 secs since last display), call:
		AppLovin.showInterstitial(120,3); */
	}
	
	function mainMenu() {
		// some implementation
		AppLovin.showBanner(); // this will show the AppLovin banner.
	}

	function beginGame() {
		// some implementation
		AppLovin.hideBanner(); // if you don't want the banner to be on screen while playing... call AppLovin.hideBanner();
	}

	function clickRewardedAd(){
		// If you want to get rewarded events (EARNED_REWARD, LOADING, LOADED, CLOSED, DISPLAYING, ETC), provide
		// some callback function for this.
		AppLovin.onRewardedEvent = onRewardedEvent;

		AppLovin.showRewarded("ca-app-pub-XXXXX123454");
	}
	
	function onInterstitialEvent(event:String) {
		trace("THE INSTERSTITIAL IS "+event);
		/*
		Note that the "event" String will be one of this:
		    AppLovin.LEAVING
		    AppLovin.FAILED
		    AppLovin.CLOSED
		    AppLovin.DISPLAYING
		    AppLovin.LOADED
		    AppLovin.LOADING
		
		So, you can do something like:
		if(event == AppLovin.CLOSED) trace("The player dismissed the ad!");
		else if(event == AppLovin.LEAVING) trace("The player clicked the ad :), and we're leaving to the ad destination");
		else if(event == AppLovin.FAILED) trace("Failed to load the ad... the extension will retry automatically.");
		*/
	}

	function onRewardedEvent(event:String, ?data:RewardItem) {
		trace("THE REWARDED IS "+event);
		if(event == AppLovin.EARNED_REWARD) trace("THE USER WAS REWARDED BY " + data.amount + " " + data.type);
		/*
		Note that the "event" String will be one of this:
		    AppLovin.LEAVING
		    AppLovin.FAILED
		    AppLovin.CLOSED
		    AppLovin.DISPLAYING
		    AppLovin.LOADED
			AppLovin.LOADING
			AppLovin.EARNED_REWARD
		
		In case of an EARNED_REWARD event, data is a reward object :
		typedef RewardItem = {
			var type:String;
			var amount:Int;
		}
		Otherwise data is null
		*/
	}
}

```

### How to Install

To install this library, you can simply get the library from haxelib like this:
```bash
haxelib install extension-applovin
```

Once this is done, you just need to add this to your project.xml
```xml
<haxelib name="extension-applovin" />
```

Also, you may need to set android sdk version to 23 or higher (as some versions of google play services requires that):
```xml
<android target-sdk-version="23" if="android" />
```

### Disclaimer

Google is a registered trademark of Google Inc.
http://unibrander.com/united-states/140279US/google.html

AppLovin is a registrered trademark of Google Inc.
https://unibrander.com/united-states/1138441US/applovin.html

### License

The MIT License (MIT) - [LICENSE.md](LICENSE.md)
