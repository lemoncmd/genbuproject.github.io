package com.microsoft.xboxtcui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import com.microsoft.xbox.toolkit.XLEAssert;

public final class XboxTcuiSdk {
    private static Activity activity;
    private static Context applicationContext;
    private static AssetManager assetManager;
    private static ContentResolver contentResolver;
    private static Resources resources;

    public static Activity getActivity() {
        XLEAssert.assertNotNull(activity);
        return activity;
    }

    public static Context getApplicationContext() {
        XLEAssert.assertNotNull(activity);
        if (applicationContext == null) {
            applicationContext = activity.getApplicationContext();
        }
        return applicationContext;
    }

    public static AssetManager getAssetManager() {
        XLEAssert.assertNotNull(activity);
        if (assetManager == null) {
            assetManager = activity.getAssets();
        }
        return assetManager;
    }

    public static ContentResolver getContentResolver() {
        XLEAssert.assertNotNull(activity);
        if (contentResolver == null) {
            contentResolver = activity.getContentResolver();
        }
        return contentResolver;
    }

    public static boolean getIsTablet() {
        return false;
    }

    public static Resources getResources() {
        XLEAssert.assertNotNull(activity);
        if (resources == null) {
            resources = activity.getResources();
        }
        return resources;
    }

    public static Object getSystemService(String str) {
        XLEAssert.assertNotNull(activity);
        return activity.getSystemService(str);
    }

    public static void sdkInitialize(Activity activity) {
        synchronized (XboxTcuiSdk.class) {
            try {
                XLEAssert.assertNotNull(activity);
                activity = activity;
            } finally {
                Class cls = XboxTcuiSdk.class;
            }
        }
    }
}
