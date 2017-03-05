package com.microsoft.xbox.toolkit.system;

import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.net.NetworkInterface;
import java.util.Collections;
import net.hockeyapp.android.BuildConfig;

public class SystemUtil {
    private static final int MAX_SD_SCREEN_PIXELS = 384000;

    public static int DIPtoPixels(float f) {
        return (int) TypedValue.applyDimension(1, f, XboxTcuiSdk.getResources().getDisplayMetrics());
    }

    public static int SPtoPixels(float f) {
        return (int) TypedValue.applyDimension(2, f, XboxTcuiSdk.getResources().getDisplayMetrics());
    }

    public static boolean TEST_randomFalseOutOf(int i) {
        XLEAssert.assertTrue(false);
        return true;
    }

    public static void TEST_randomSleep(int i) {
        XLEAssert.assertTrue(false);
    }

    public static int getColorDepth() {
        PixelFormat.getPixelFormatInfo(1, null);
        return null.bitsPerPixel;
    }

    public static String getDeviceId() {
        return Secure.getString(XboxTcuiSdk.getContentResolver(), "android_id");
    }

    public static String getDeviceModelName() {
        return Build.MODEL;
    }

    public static String getDeviceType() {
        XLEAssert.assertTrue(false);
        return BuildConfig.FLAVOR;
    }

    private static Display getDisplay() {
        return ((WindowManager) XboxTcuiSdk.getSystemService("window")).getDefaultDisplay();
    }

    public static String getMACAddress(String str) {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (str != null) {
                    if (networkInterface.getName().equalsIgnoreCase(str)) {
                    }
                }
                byte[] hardwareAddress = networkInterface.getHardwareAddress();
                if (hardwareAddress == null) {
                    return BuildConfig.FLAVOR;
                }
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < hardwareAddress.length; i++) {
                    stringBuilder.append(String.format("%02X:", new Object[]{Byte.valueOf(hardwareAddress[i])}));
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                }
                return stringBuilder.toString();
            }
        } catch (Exception e) {
        }
        return BuildConfig.FLAVOR;
    }

    public static int getOrientation() {
        int rotation = getRotation();
        return (rotation == 0 || rotation == 2) ? 1 : 2;
    }

    public static int getRotation() {
        return getDisplay().getRotation();
    }

    public static int getScreenHeight() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().heightPixels;
    }

    public static float getScreenHeightInches() {
        return ((float) getScreenHeight()) / XboxTcuiSdk.getResources().getDisplayMetrics().ydpi;
    }

    public static int getScreenWidth() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().widthPixels;
    }

    public static float getScreenWidthHeightAspectRatio() {
        int screenWidth = getScreenWidth();
        int screenHeight = getScreenHeight();
        return (screenWidth <= 0 || screenHeight <= 0) ? 0.0f : screenWidth > screenHeight ? ((float) screenWidth) / ((float) screenHeight) : ((float) screenHeight) / ((float) screenWidth);
    }

    public static float getScreenWidthInches() {
        return ((float) getScreenWidth()) / XboxTcuiSdk.getResources().getDisplayMetrics().xdpi;
    }

    public static int getSdkInt() {
        return VERSION.SDK_INT;
    }

    public static float getYDPI() {
        return XboxTcuiSdk.getResources().getDisplayMetrics().ydpi;
    }

    public static boolean isHDScreen() {
        return getScreenHeight() * getScreenWidth() > MAX_SD_SCREEN_PIXELS;
    }

    public static boolean isKindle() {
        String str = Build.MANUFACTURER;
        return str != null && "AMAZON".compareToIgnoreCase(str) == 0;
    }

    public static boolean isSDCardAvailable() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean isSlate() {
        return Math.sqrt(Math.pow((double) getScreenHeightInches(), 2.0d) + Math.pow((double) getScreenWidthInches(), 2.0d)) > 6.0d;
    }
}
