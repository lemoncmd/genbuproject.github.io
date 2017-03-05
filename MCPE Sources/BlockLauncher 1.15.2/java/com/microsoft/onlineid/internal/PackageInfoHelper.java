package com.microsoft.onlineid.internal;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class PackageInfoHelper {
    public static final String AuthenticatorPackageName = "com.microsoft.msa.authenticator";

    public static Signature[] getAppSignatures(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 64).signatures;
        } catch (NameNotFoundException e) {
            Assertion.check(false);
            return new Signature[0];
        }
    }

    public static String getAppVersionName(Context context, String str) {
        try {
            return context.getPackageManager().getPackageInfo(str, 0).versionName;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static Signature[] getCurrentAppSignatures(Context context) {
        return getAppSignatures(context, context.getPackageName());
    }

    public static int getCurrentAppVersionCode(Context context) {
        int i = 0;
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            Assertion.check(i);
            return i;
        }
    }

    public static String getCurrentAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            Assertion.check(false);
            return BuildConfig.FLAVOR;
        }
    }

    public static boolean isAuthenticatorApp(String str) {
        return AuthenticatorPackageName.equalsIgnoreCase(str);
    }

    public static boolean isAuthenticatorAppInstalled(Context context) {
        try {
            context.getPackageManager().getPackageInfo(AuthenticatorPackageName, Token.RESERVED);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isCurrentApp(String str, Context context) {
        return context.getPackageName().equalsIgnoreCase(str);
    }

    public static boolean isRunningInAuthenticatorApp(Context context) {
        return isAuthenticatorApp(context.getPackageName());
    }
}
