package com.microsoft.xboxtcui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.telemetry.helpers.UTCDeepLink;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.DeepLink;
import net.hockeyapp.android.BuildConfig;

public class XboxAppDeepLinker {
    public static final String ACTION_FIND_PEOPLE = "com.microsoft.xbox.action.ACTION_FIND_PEOPLE";
    private static final String ACTION_VIEW_ACHIEVEMENTS = "com.microsoft.xbox.action.ACTION_VIEW_ACHIEVEMENTS";
    private static final String ACTION_VIEW_GAME_PROFILE = "com.microsoft.xbox.action.ACTION_VIEW_GAME_PROFILE";
    private static final String ACTION_VIEW_SETTINGS = "com.microsoft.xbox.action.ACTION_VIEW_SETTINGS";
    private static final String ACTION_VIEW_USER_PROFILE = "com.microsoft.xbox.action.ACTION_VIEW_USER_PROFILE";
    private static final String AMAZON_FIRE_TV_MODEL_PREFIX = "AFT";
    private static final String AMAZON_MANUFACTURER = "Amazon";
    private static final String AMAZON_STORE_URI = "amzn://apps/android?p=";
    private static final String AMAZON_TABLET_STORE_PACKAGE = "com.amazon.venezia";
    private static final String AMAZON_UNDERGROUND_PACKAGE = "com.amazon.mShop.android";
    private static final String EXTRA_IS_XBOX360_GAME = "com.microsoft.xbox.extra.IS_XBOX360_GAME";
    private static final String EXTRA_TITLEID = "com.microsoft.xbox.extra.TITLEID";
    private static final String EXTRA_XUID = "com.microsoft.xbox.extra.XUID";
    private static final String OCULUS_STORE_WEB_URI = "oculus.store://link/products?referrer=manual&item_id=";
    private static final String OCULUS_XBOXAPP_APP_ID = "1193603937358048";
    private static final String PLAY_STORE_PACKAGE = "com.android.vending";
    private static final String PLAY_STORE_URI = "market://details?id=";
    private static final String PLAY_STORE_WEB_URI = "https://play.google.com/store/apps/details?id=";
    private static final String XBOXAPP_BETA_PACKAGE = "com.microsoft.xboxone.smartglass.beta";
    private static final String XBOXAPP_PACKAGE = "com.microsoft.xboxone.smartglass";
    private static boolean betaAppInstalled;
    private static boolean mainAppInstalled;

    private XboxAppDeepLinker() {
    }

    public static boolean appDeeplinkingSupported() {
        boolean z = Build.MANUFACTURER.equalsIgnoreCase(AMAZON_MANUFACTURER) && Build.MODEL.startsWith(AMAZON_FIRE_TV_MODEL_PREFIX);
        return !z;
    }

    private static String getActivityTitle() {
        return "DeepLink";
    }

    private static Intent getXboxAppInStoreIntent(Context context, String str, String str2) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(str + XBOXAPP_PACKAGE));
        for (ResolveInfo resolveInfo : context.getPackageManager().queryIntentActivities(intent, 0)) {
            if (resolveInfo.activityInfo.applicationInfo.packageName.equals(str2)) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                ComponentName componentName = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                intent.setFlags(270532608);
                intent.setComponent(componentName);
                return intent;
            }
        }
        return null;
    }

    private static Intent getXboxAppLaunchIntent(Context context) {
        boolean z = mainAppInstalled || betaAppInstalled;
        XLEAssert.assertTrue(z);
        return betaAppInstalled ? context.getPackageManager().getLaunchIntentForPackage(XBOXAPP_BETA_PACKAGE) : context.getPackageManager().getLaunchIntentForPackage(XBOXAPP_PACKAGE);
    }

    private static void launchXboxAppStorePage(Context context) {
        Intent xboxAppInStoreIntent = getXboxAppInStoreIntent(context, PLAY_STORE_URI, PLAY_STORE_PACKAGE);
        if (xboxAppInStoreIntent == null) {
            xboxAppInStoreIntent = getXboxAppInStoreIntent(context, AMAZON_STORE_URI, AMAZON_UNDERGROUND_PACKAGE);
        }
        if (xboxAppInStoreIntent == null) {
            xboxAppInStoreIntent = getXboxAppInStoreIntent(context, AMAZON_STORE_URI, AMAZON_TABLET_STORE_PACKAGE);
        }
        if (xboxAppInStoreIntent == null) {
            xboxAppInStoreIntent = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.microsoft.xboxone.smartglass"));
        }
        xboxAppInStoreIntent.setFlags(270565376);
        context.startActivity(xboxAppInStoreIntent);
    }

    private static void launchXboxAppStorePageInOculusStore(Context context) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("oculus.store://link/products?referrer=manual&item_id=1193603937358048"));
        intent.setFlags(270565376);
        context.startActivity(intent);
    }

    public static boolean showAddFriends(Context context) {
        if (!appDeeplinkingSupported()) {
            return false;
        }
        String packageName = context == null ? BuildConfig.FLAVOR : context.getPackageName();
        if (xboxAppIsInstalled(context)) {
            String trackFriendSuggestionsLink = UTCDeepLink.trackFriendSuggestionsLink(getActivityTitle(), packageName);
            Intent xboxAppLaunchIntent = getXboxAppLaunchIntent(context);
            xboxAppLaunchIntent.setAction(ACTION_FIND_PEOPLE);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.DEEPLINK_KEY_NAME, trackFriendSuggestionsLink);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.CALLING_APP_KEY, packageName);
            context.startActivity(xboxAppLaunchIntent);
        } else {
            UTCDeepLink.trackUserSendToStore(getActivityTitle(), packageName, DeepLink.FriendSuggestions);
            launchXboxAppStorePageInOculusStore(context);
        }
        return true;
    }

    public static boolean showTitleAchievements(Context context, String str) {
        if (!appDeeplinkingSupported()) {
            return false;
        }
        String packageName = context == null ? BuildConfig.FLAVOR : context.getPackageName();
        if (xboxAppIsInstalled(context)) {
            String trackGameHubAchievementsLink = UTCDeepLink.trackGameHubAchievementsLink(getActivityTitle(), packageName, str);
            Intent xboxAppLaunchIntent = getXboxAppLaunchIntent(context);
            xboxAppLaunchIntent.setAction(ACTION_VIEW_ACHIEVEMENTS);
            xboxAppLaunchIntent.putExtra(EXTRA_TITLEID, str);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.DEEPLINK_KEY_NAME, trackGameHubAchievementsLink);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.CALLING_APP_KEY, packageName);
            context.startActivity(xboxAppLaunchIntent);
        } else {
            UTCDeepLink.trackUserSendToStore(getActivityTitle(), packageName, DeepLink.TitleAchievements);
            launchXboxAppStorePage(context);
        }
        return true;
    }

    public static boolean showTitleHub(Context context, String str) {
        if (!appDeeplinkingSupported()) {
            return false;
        }
        String packageName = context == null ? BuildConfig.FLAVOR : context.getPackageName();
        if (xboxAppIsInstalled(context)) {
            String trackGameHubLink = UTCDeepLink.trackGameHubLink(getActivityTitle(), packageName, str);
            Intent xboxAppLaunchIntent = getXboxAppLaunchIntent(context);
            xboxAppLaunchIntent.setAction(ACTION_VIEW_GAME_PROFILE);
            xboxAppLaunchIntent.putExtra(EXTRA_TITLEID, str);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.DEEPLINK_KEY_NAME, trackGameHubLink);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.CALLING_APP_KEY, packageName);
            context.startActivity(xboxAppLaunchIntent);
        } else {
            UTCDeepLink.trackUserSendToStore(getActivityTitle(), packageName, DeepLink.TitleHub);
            launchXboxAppStorePage(context);
        }
        return true;
    }

    public static boolean showUserProfile(Context context, String str) {
        if (!appDeeplinkingSupported()) {
            return false;
        }
        String packageName = context == null ? BuildConfig.FLAVOR : context.getPackageName();
        if (xboxAppIsInstalled(context)) {
            String trackUserProfileLink = UTCDeepLink.trackUserProfileLink(getActivityTitle(), packageName, str);
            Intent xboxAppLaunchIntent = getXboxAppLaunchIntent(context);
            xboxAppLaunchIntent.setAction(ACTION_VIEW_USER_PROFILE);
            xboxAppLaunchIntent.putExtra(EXTRA_XUID, str);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.DEEPLINK_KEY_NAME, trackUserProfileLink);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.CALLING_APP_KEY, packageName);
            context.startActivity(xboxAppLaunchIntent);
        } else {
            UTCDeepLink.trackUserSendToStore(getActivityTitle(), packageName, DeepLink.UserProfile);
            launchXboxAppStorePage(context);
        }
        return true;
    }

    public static boolean showUserSettings(Context context) {
        if (!appDeeplinkingSupported()) {
            return false;
        }
        String packageName = context == null ? BuildConfig.FLAVOR : context.getPackageName();
        if (xboxAppIsInstalled(context)) {
            String trackUserSettingsLink = UTCDeepLink.trackUserSettingsLink(getActivityTitle(), packageName);
            Intent xboxAppLaunchIntent = getXboxAppLaunchIntent(context);
            xboxAppLaunchIntent.setAction(ACTION_VIEW_SETTINGS);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.DEEPLINK_KEY_NAME, trackUserSettingsLink);
            xboxAppLaunchIntent.putExtra(UTCDeepLink.CALLING_APP_KEY, packageName);
            context.startActivity(xboxAppLaunchIntent);
        } else {
            UTCDeepLink.trackUserSendToStore(getActivityTitle(), packageName, DeepLink.UserSettings);
            launchXboxAppStorePage(context);
        }
        return true;
    }

    private static boolean xboxAppIsInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo(XBOXAPP_PACKAGE, 1);
            mainAppInstalled = true;
        } catch (NameNotFoundException e) {
            mainAppInstalled = false;
        }
        try {
            context.getPackageManager().getPackageInfo(XBOXAPP_BETA_PACKAGE, 1);
            betaAppInstalled = true;
        } catch (NameNotFoundException e2) {
            betaAppInstalled = false;
        }
        return mainAppInstalled || betaAppInstalled;
    }
}
