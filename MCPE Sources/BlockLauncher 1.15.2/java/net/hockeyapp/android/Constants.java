package net.hockeyapp.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.mozilla.javascript.Token;

public class Constants {
    public static String ANDROID_BUILD = null;
    public static String ANDROID_VERSION = null;
    public static String APP_PACKAGE = null;
    public static String APP_VERSION = null;
    public static String APP_VERSION_NAME = null;
    public static final String BASE_URL = "https://sdk.hockeyapp.net/";
    private static final String BUNDLE_BUILD_NUMBER = "buildNumber";
    public static String CRASH_IDENTIFIER = null;
    public static String DEVICE_IDENTIFIER = null;
    public static final String FILES_DIRECTORY_NAME = "HockeyApp";
    public static String FILES_PATH = null;
    public static String PHONE_MANUFACTURER = null;
    public static String PHONE_MODEL = null;
    public static final String SDK_NAME = "HockeySDK";
    public static final int UPDATE_PERMISSIONS_REQUEST = 1;

    private static String bytesToHex(byte[] bArr) {
        char[] toCharArray = "0123456789ABCDEF".toCharArray();
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i += UPDATE_PERMISSIONS_REQUEST) {
            int i2 = bArr[i] & 255;
            cArr[i * 2] = toCharArray[i2 >>> 4];
            cArr[(i * 2) + UPDATE_PERMISSIONS_REQUEST] = toCharArray[i2 & 15];
        }
        return new String(cArr).replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
    }

    @SuppressLint({"InlinedApi"})
    private static String createSalt(Context context) {
        String str = "HA" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + ((VERSION.SDK_INT >= 21 ? Build.SUPPORTED_ABIS[0] : Build.CPU_ABI).length() % 10) + (Build.PRODUCT.length() % 10);
        String str2 = BuildConfig.FLAVOR;
        try {
            str2 = Build.class.getField("SERIAL").get(null).toString();
        } catch (Throwable th) {
        }
        return str + ":" + str2;
    }

    public static File getHockeyAppStorageDir() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FILES_DIRECTORY_NAME);
        Object obj = (file.exists() || file.mkdirs()) ? UPDATE_PERMISSIONS_REQUEST : null;
        if (obj == null) {
            HockeyLog.warn("Couldn't create HockeyApp Storage dir");
        }
        return file;
    }

    private static int loadBuildNumber(Context context, PackageManager packageManager) {
        int i = 0;
        try {
            Bundle bundle = packageManager.getApplicationInfo(context.getPackageName(), Token.RESERVED).metaData;
            if (bundle != null) {
                i = bundle.getInt(BUNDLE_BUILD_NUMBER, 0);
            }
        } catch (NameNotFoundException e) {
            HockeyLog.error("Exception thrown when accessing the application info:");
            e.printStackTrace();
        }
        return i;
    }

    private static void loadCrashIdentifier(Context context) {
        Object string = Secure.getString(context.getContentResolver(), "android_id");
        if (!TextUtils.isEmpty(APP_PACKAGE) && !TextUtils.isEmpty(string)) {
            String str = APP_PACKAGE + ":" + string + ":" + createSalt(context);
            try {
                MessageDigest instance = MessageDigest.getInstance("SHA-1");
                byte[] bytes = str.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET);
                instance.update(bytes, 0, bytes.length);
                CRASH_IDENTIFIER = bytesToHex(instance.digest());
            } catch (Throwable th) {
                HockeyLog.error("Couldn't create CrashIdentifier with Exception:" + th.toString());
            }
        }
    }

    private static void loadDeviceIdentifier(Context context) {
        String string = Secure.getString(context.getContentResolver(), "android_id");
        if (string != null) {
            string = tryHashStringSha256(context, string);
            if (string == null) {
                string = UUID.randomUUID().toString();
            }
            DEVICE_IDENTIFIER = string;
        }
    }

    private static void loadFilesPath(Context context) {
        if (context != null) {
            try {
                File filesDir = context.getFilesDir();
                if (filesDir != null) {
                    FILES_PATH = filesDir.getAbsolutePath();
                }
            } catch (Exception e) {
                HockeyLog.error("Exception thrown when accessing the files dir:");
                e.printStackTrace();
            }
        }
    }

    public static void loadFromContext(Context context) {
        ANDROID_VERSION = VERSION.RELEASE;
        ANDROID_BUILD = Build.DISPLAY;
        PHONE_MODEL = Build.MODEL;
        PHONE_MANUFACTURER = Build.MANUFACTURER;
        loadFilesPath(context);
        loadPackageData(context);
        loadCrashIdentifier(context);
        loadDeviceIdentifier(context);
    }

    private static void loadPackageData(Context context) {
        if (context != null) {
            try {
                PackageManager packageManager = context.getPackageManager();
                PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
                APP_PACKAGE = packageInfo.packageName;
                APP_VERSION = BuildConfig.FLAVOR + packageInfo.versionCode;
                APP_VERSION_NAME = packageInfo.versionName;
                int loadBuildNumber = loadBuildNumber(context, packageManager);
                if (loadBuildNumber != 0 && loadBuildNumber > packageInfo.versionCode) {
                    APP_VERSION = BuildConfig.FLAVOR + loadBuildNumber;
                }
            } catch (NameNotFoundException e) {
                HockeyLog.error("Exception thrown when accessing the package info:");
                e.printStackTrace();
            }
        }
    }

    private static String tryHashStringSha256(Context context, String str) {
        String createSalt = createSalt(context);
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.reset();
            instance.update(str.getBytes());
            instance.update(createSalt.getBytes());
            return bytesToHex(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }
}
