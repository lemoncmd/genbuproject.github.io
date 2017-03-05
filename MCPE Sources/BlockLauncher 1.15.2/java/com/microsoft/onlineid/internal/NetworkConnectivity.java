package com.microsoft.onlineid.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build.VERSION;
import android.provider.Settings.Global;
import android.provider.Settings.System;
import android.telephony.TelephonyManager;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class NetworkConnectivity {

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType = new int[NetworkType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.None.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.WiFi.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Mobile2G.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Mobile3G.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Mobile4G.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Ethernet.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Bluetooth.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[NetworkType.Unknown.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public enum NetworkType {
        None,
        WiFi,
        Ethernet,
        Bluetooth,
        Mobile2G,
        Mobile3G,
        Mobile4G,
        Unknown
    }

    private static NetworkInfo getActiveNetworkInfo(Context context) {
        return ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
    }

    private static NetworkType getMobileNetworkType(Context context) {
        switch (getTelephonyManager(context).getNetworkType()) {
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.IFNE /*7*/:
            case Token.BITAND /*11*/:
            case Token.LT /*14*/:
                return NetworkType.Mobile2G;
            case Token.GOTO /*5*/:
            case Token.IFEQ /*6*/:
            case Token.SETNAME /*8*/:
            case Token.BITOR /*9*/:
            case Token.BITXOR /*10*/:
            case Token.EQ /*12*/:
            case Token.LE /*15*/:
                return NetworkType.Mobile3G;
            default:
                return NetworkType.Mobile4G;
        }
    }

    private static NetworkType getNetworkType(Context context) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
            return NetworkType.None;
        }
        switch (activeNetworkInfo.getType()) {
            case NativeRegExp.TEST /*0*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                return getMobileNetworkType(context);
            case NativeRegExp.MATCH /*1*/:
                return NetworkType.WiFi;
            case Token.IFNE /*7*/:
                return NetworkType.Bluetooth;
            case Token.BITOR /*9*/:
                return NetworkType.Ethernet;
            default:
                return NetworkType.Unknown;
        }
    }

    public static String getNetworkTypeForAnalytics(Context context) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[getNetworkType(context).ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return "Not connected";
            case NativeRegExp.PREFIX /*2*/:
                return "WiFi";
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                return "Mobile";
            case Token.IFEQ /*6*/:
                return "Ethernet";
            case Token.IFNE /*7*/:
                return "Bluetooth";
            default:
                return UTCTelemetry.UNKNOWNPAGE;
        }
    }

    public static String getNetworkTypeForServerTelemetry(Context context) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$internal$NetworkConnectivity$NetworkType[getNetworkType(context).ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return "NONE";
            case NativeRegExp.PREFIX /*2*/:
                return "WIFI";
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return "2G";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "3G";
            case Token.GOTO /*5*/:
                return "4G";
            default:
                return "UNKNOWN";
        }
    }

    private static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager) context.getSystemService("phone");
    }

    public static boolean hasInternetConnectivity(Context context) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo(context);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @TargetApi(17)
    public static boolean isAirplaneModeOn(Context context) {
        if (VERSION.SDK_INT < 17) {
            if (System.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 0) {
                return false;
            }
        } else if (Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) == 0) {
            return false;
        }
        return true;
    }
}
