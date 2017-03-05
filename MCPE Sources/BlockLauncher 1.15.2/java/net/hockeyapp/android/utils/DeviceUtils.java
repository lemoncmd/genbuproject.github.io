package net.hockeyapp.android.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.Constants;

public class DeviceUtils {

    private static class DeviceUtilsHolder {
        public static final DeviceUtils INSTANCE = new DeviceUtils();

        private DeviceUtilsHolder() {
        }
    }

    private DeviceUtils() {
    }

    public static DeviceUtils getInstance() {
        return DeviceUtilsHolder.INSTANCE;
    }

    public String getAppName(Context context) {
        if (context == null) {
            return BuildConfig.FLAVOR;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            return packageManager == null ? BuildConfig.FLAVOR : packageManager.getApplicationLabel(packageManager.getApplicationInfo(context.getPackageName(), 0)).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }

    public int getCurrentVersionCode(Context context) {
        return Integer.parseInt(Constants.APP_VERSION);
    }
}
