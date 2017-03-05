package com.microsoft.cll.android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import com.microsoft.onlineid.sts.request.AbstractStsRequest;
import java.util.Locale;
import net.hockeyapp.android.BuildConfig;

public class AndroidPartA extends PartA {
    private final String DeviceTypePC = "Android.PC";
    private final String DeviceTypePhone = "Android.Phone";
    protected final String TAG = "AndroidCll-AndroidPartA";
    protected Context appContext;

    public AndroidPartA(ILogger iLogger, String str, Context context, CorrelationVector correlationVector) {
        super(iLogger, str, correlationVector);
        this.appContext = context;
        PopulateConstantValues();
    }

    @TargetApi(14)
    private boolean testRadioVersion() {
        return VERSION.SDK_INT >= 14 && Build.getRadioVersion() != null;
    }

    protected void PopulateConstantValues() {
        setDeviceInfo();
        setUserId();
        setAppInfo();
        setOs();
    }

    double getDeviceScreenSize(int i, int i2, int i3) {
        return Math.sqrt(Math.pow(((double) i2) / ((double) i3), 2.0d) + Math.pow(((double) i) / ((double) i3), 2.0d));
    }

    protected void setAppInfo() {
        try {
            PackageInfo packageInfo = this.appContext.getPackageManager().getPackageInfo(this.appContext.getPackageName(), 0);
            this.appVer = packageInfo.versionName;
            this.appId = "A:" + packageInfo.packageName;
        } catch (NameNotFoundException e) {
            this.logger.error("AndroidCll-AndroidPartA", "Could not get package name");
        }
    }

    @SuppressLint({"MissingPermission"})
    protected void setDeviceInfo() {
        this.deviceExt.setLocalId(BuildConfig.FLAVOR);
        try {
            if (this.appContext != null && this.uniqueId == null) {
                this.uniqueId = Secure.getString(this.appContext.getContentResolver(), "android_id");
                if (this.uniqueId == null) {
                    this.uniqueId = ((WifiManager) this.appContext.getSystemService("wifi")).getConnectionInfo().getMacAddress().replace(":", BuildConfig.FLAVOR);
                    this.deviceExt.setLocalId("m:" + this.uniqueId);
                } else {
                    this.deviceExt.setLocalId("a:" + this.uniqueId);
                }
            }
        } catch (SecurityException e) {
            this.logger.info("AndroidCll-AndroidPartA", "Access Wifi State permission was not Provided. DeviceID will be blank");
        }
        if (testRadioVersion()) {
            this.deviceExt.setDeviceClass("Android.Phone");
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((WindowManager) this.appContext.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
            if (getDeviceScreenSize(displayMetrics.heightPixels, displayMetrics.widthPixels, displayMetrics.densityDpi) >= 8.0d) {
                this.deviceExt.setDeviceClass("Android.PC");
            } else {
                this.deviceExt.setDeviceClass("Android.Phone");
            }
        }
        this.osVer = String.format("%s", new Object[]{VERSION.RELEASE});
        this.osExt.setLocale(Locale.getDefault().toString().replaceAll("_", "-"));
    }

    protected void setOs() {
        this.osName = AbstractStsRequest.DeviceType;
    }

    @SuppressLint({"MissingPermission"})
    protected void setUserId() {
        if (this.appContext != null) {
            try {
                Account[] accountsByType = AccountManager.get(this.appContext).getAccountsByType("com.google");
                if (accountsByType.length > 0) {
                    this.userExt.setLocalId("g:" + HashStringSha256(accountsByType[0].name));
                    return;
                }
            } catch (SecurityException e) {
                this.logger.info("AndroidCll-AndroidPartA", "Get_Accounts permission was not provided. UserID will be blank");
            }
        }
        this.userExt.setLocalId(BuildConfig.FLAVOR);
    }
}
