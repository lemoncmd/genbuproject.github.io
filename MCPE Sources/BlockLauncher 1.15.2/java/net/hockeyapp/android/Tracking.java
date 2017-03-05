package net.hockeyapp.android;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import net.hockeyapp.android.utils.Util;

public class Tracking {
    protected static final String START_TIME_KEY = "startTime";
    protected static final String USAGE_TIME_KEY = "usageTime";

    private static boolean checkVersion(Context context) {
        if (Constants.APP_VERSION == null) {
            Constants.loadFromContext(context);
            if (Constants.APP_VERSION == null) {
                return false;
            }
        }
        return true;
    }

    protected static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(Util.LOG_IDENTIFIER, 0);
    }

    public static long getUsageTime(Context context) {
        if (!checkVersion(context)) {
            return 0;
        }
        SharedPreferences preferences = getPreferences(context);
        long j = preferences.getLong(USAGE_TIME_KEY + Constants.APP_VERSION, 0);
        if (j >= 0) {
            return j / 1000;
        }
        preferences.edit().remove(USAGE_TIME_KEY + Constants.APP_VERSION).apply();
        return 0;
    }

    public static void startUsage(Activity activity) {
        long currentTimeMillis = System.currentTimeMillis();
        if (activity != null) {
            Editor edit = getPreferences(activity).edit();
            edit.putLong(START_TIME_KEY + activity.hashCode(), currentTimeMillis);
            edit.apply();
        }
    }

    public static void stopUsage(Activity activity) {
        long currentTimeMillis = System.currentTimeMillis();
        if (activity != null && checkVersion(activity)) {
            SharedPreferences preferences = getPreferences(activity);
            long j = preferences.getLong(START_TIME_KEY + activity.hashCode(), 0);
            long j2 = preferences.getLong(USAGE_TIME_KEY + Constants.APP_VERSION, 0);
            if (j > 0) {
                currentTimeMillis -= j;
                j = j2 + currentTimeMillis;
                if (currentTimeMillis > 0 && j >= 0) {
                    Editor edit = preferences.edit();
                    edit.putLong(USAGE_TIME_KEY + Constants.APP_VERSION, j);
                    edit.apply();
                }
            }
        }
    }
}
