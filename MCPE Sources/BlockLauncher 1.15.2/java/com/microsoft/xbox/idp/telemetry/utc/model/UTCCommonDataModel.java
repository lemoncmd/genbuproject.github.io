package com.microsoft.xbox.idp.telemetry.utc.model;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.os.Build;
import android.view.accessibility.AccessibilityManager;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;
import com.microsoft.xbox.idp.telemetry.utc.CommonData;
import com.mojang.minecraftpe.MainActivity;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.hockeyapp.android.BuildConfig;

public class UTCCommonDataModel {
    static final String DEFAULTSERVICES = "none";
    static final String EVENTVERSION = "1.1";
    static final String UNKNOWNAPP = "UNKNOWN";
    static final String UNKNOWNUSER = "UNKNOWN";
    static UTCAccessibilityInfoModel accessibilityInfo = null;
    static String appName = UNKNOWNUSER;
    static UUID applicationSession = null;
    static String deviceModel = null;
    static NetworkType netType = NetworkType.UNKNOWN;
    static String osLocale = null;
    static String userId = UNKNOWNUSER;

    private enum NetworkType {
        UNKNOWN(0),
        WIFI(1),
        CELLULAR(2),
        WIRED(3);
        
        private int value;

        private NetworkType(int i) {
            this.value = 0;
            setValue(i);
        }

        public int getValue() {
            return this.value;
        }

        public void setValue(int i) {
            this.value = i;
        }
    }

    private static UTCAccessibilityInfoModel getAccessibilityInfo() {
        if (accessibilityInfo != null) {
            return accessibilityInfo;
        }
        accessibilityInfo = new UTCAccessibilityInfoModel();
        try {
            Context applicationContext = Interop.getApplicationContext();
            if (applicationContext != null) {
                AccessibilityManager accessibilityManager = (AccessibilityManager) applicationContext.getSystemService("accessibility");
                accessibilityInfo.addValue("isenabled", Boolean.valueOf(accessibilityManager.isEnabled()));
                List<AccessibilityServiceInfo> enabledAccessibilityServiceList = accessibilityManager.getEnabledAccessibilityServiceList(-1);
                String str = DEFAULTSERVICES;
                String str2 = str;
                for (AccessibilityServiceInfo accessibilityServiceInfo : enabledAccessibilityServiceList) {
                    if (str2.equals(DEFAULTSERVICES)) {
                        str2 = accessibilityServiceInfo.getId();
                    } else {
                        str2 = str2 + String.format(";%s", new Object[]{accessibilityServiceInfo.getId()});
                    }
                }
                accessibilityInfo.addValue("enabledservices", str2);
            }
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
        }
        return accessibilityInfo;
    }

    private static String getAppName() {
        try {
            Context applicationContext = Interop.getApplicationContext();
            if (appName == UNKNOWNUSER && applicationContext != null) {
                appName = applicationContext.getApplicationInfo().packageName;
            }
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
            appName = UNKNOWNUSER;
        }
        return appName;
    }

    private static String getAppSessionId() {
        if (applicationSession == null) {
            applicationSession = UUID.randomUUID();
        }
        return applicationSession.toString();
    }

    public static CommonData getCommonData(int i) {
        return getCommonData(i, new UTCAdditionalInfoModel());
    }

    public static CommonData getCommonData(int i, UTCAdditionalInfoModel uTCAdditionalInfoModel) {
        CommonData commonData = new CommonData();
        commonData.setEventVersion(String.format("%s.%s", new Object[]{EVENTVERSION, Integer.valueOf(i)}));
        commonData.setDeviceModel(getDeviceModel());
        commonData.setXsapiVersion(MainActivity.HALF_SUPPORT_VERSION);
        commonData.setAppName(getAppName());
        commonData.setClientLanguage(getDeviceLocale());
        commonData.setNetwork(getNetworkConnection().getValue());
        commonData.setSandboxId(getSandboxId());
        commonData.setAppSessionId(getAppSessionId());
        commonData.setUserId(getUserId());
        if (uTCAdditionalInfoModel == null) {
            uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
        }
        commonData.setAdditionalInfo(uTCAdditionalInfoModel.toJson());
        commonData.setAccessibilityInfo(getAccessibilityInfo().toJson());
        commonData.setTitleDeviceId(Interop.getTitleDeviceId());
        commonData.setTitleSessionId(Interop.getTitleSessionId());
        return commonData;
    }

    private static String getDeviceLocale() {
        if (osLocale == null) {
            try {
                Locale locale = Locale.getDefault();
                osLocale = String.format("%s-%s", new Object[]{locale.getLanguage(), locale.getCountry()});
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
            }
        }
        return osLocale;
    }

    private static String getDeviceModel() {
        if (deviceModel == null) {
            String str = Build.MODEL;
            deviceModel = UNKNOWNUSER;
            if (!(str == null || str.isEmpty())) {
                deviceModel = removePipes(str);
            }
        }
        return deviceModel;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType getNetworkConnection() {
        /*
        r0 = netType;
        r1 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.UNKNOWN;
        if (r0 != r1) goto L_0x0034;
    L_0x0006:
        r0 = com.microsoft.xbox.idp.interop.Interop.getApplicationContext();
        if (r0 == 0) goto L_0x0034;
    L_0x000c:
        r0 = com.microsoft.xbox.idp.interop.Interop.getApplicationContext();	 Catch:{ Exception -> 0x0044 }
        r1 = "connectivity";
        r0 = r0.getSystemService(r1);	 Catch:{ Exception -> 0x0044 }
        r0 = (android.net.ConnectivityManager) r0;	 Catch:{ Exception -> 0x0044 }
        r0 = r0.getActiveNetworkInfo();	 Catch:{ Exception -> 0x0044 }
        if (r0 != 0) goto L_0x0021;
    L_0x001e:
        r0 = netType;	 Catch:{ Exception -> 0x0044 }
    L_0x0020:
        return r0;
    L_0x0021:
        r1 = r0.getState();	 Catch:{ Exception -> 0x0044 }
        r2 = android.net.NetworkInfo.State.CONNECTED;	 Catch:{ Exception -> 0x0044 }
        if (r1 != r2) goto L_0x0034;
    L_0x0029:
        r0 = r0.getType();	 Catch:{ Exception -> 0x0044 }
        switch(r0) {
            case 0: goto L_0x0037;
            case 1: goto L_0x003b;
            case 6: goto L_0x0037;
            case 9: goto L_0x003f;
            default: goto L_0x0030;
        };	 Catch:{ Exception -> 0x0044 }
    L_0x0030:
        r0 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.UNKNOWN;	 Catch:{ Exception -> 0x0044 }
        netType = r0;	 Catch:{ Exception -> 0x0044 }
    L_0x0034:
        r0 = netType;
        goto L_0x0020;
    L_0x0037:
        r0 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.CELLULAR;	 Catch:{ Exception -> 0x0044 }
        netType = r0;	 Catch:{ Exception -> 0x0044 }
    L_0x003b:
        r0 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.WIFI;	 Catch:{ Exception -> 0x0044 }
        netType = r0;	 Catch:{ Exception -> 0x0044 }
    L_0x003f:
        r0 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.WIRED;	 Catch:{ Exception -> 0x0044 }
        netType = r0;	 Catch:{ Exception -> 0x0044 }
        goto L_0x0030;
    L_0x0044:
        r0 = move-exception;
        r0 = r0.getMessage();
        r1 = 0;
        r1 = new java.lang.Object[r1];
        com.microsoft.xbox.idp.telemetry.helpers.UTCLog.log(r0, r1);
        r0 = com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.NetworkType.UNKNOWN;
        netType = r0;
        goto L_0x0034;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel.getNetworkConnection():com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel$NetworkType");
    }

    private static String getSandboxId() {
        return BuildConfig.FLAVOR;
    }

    public static String getUserId() {
        return userId == null ? UNKNOWNUSER : userId;
    }

    private static String removePipes(String str) {
        return str != null ? str.replace("|", BuildConfig.FLAVOR) : str;
    }

    public static void setUserId(String str) {
        if (str != null) {
            userId = "x:" + str;
        }
    }
}
