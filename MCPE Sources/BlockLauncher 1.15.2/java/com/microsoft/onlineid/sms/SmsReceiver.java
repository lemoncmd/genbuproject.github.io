package com.microsoft.onlineid.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.ui.JavaScriptBridge;
import com.microsoft.onlineid.internal.ui.PropertyBag.Key;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    public static final Pattern OneTimeCodePattern = Pattern.compile("\\s*(\\d+).+\\Qhttp://aka.ms/smscode\\E");
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private final JavaScriptBridge _javaScriptBridge;

    public SmsReceiver(JavaScriptBridge javaScriptBridge) {
        Objects.verifyArgumentNotNull(javaScriptBridge, "javaScriptBridge");
        this._javaScriptBridge = javaScriptBridge;
    }

    static String parseOneTimeCodeFromBody(String str) {
        if (str == null) {
            return null;
        }
        Matcher matcher = OneTimeCodePattern.matcher(str);
        return (matcher.matches() && matcher.groupCount() == 1) ? matcher.group(1) : null;
    }

    protected void abortBroadcastWrapper() {
        abortBroadcast();
    }

    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Object[] objArr = (Object[]) extras.get("pdus");
            if (objArr.length >= 1) {
                Object parseOneTimeCodeFromBody = parseOneTimeCodeFromBody(SmsMessage.createFromPdu((byte[]) objArr[0]).getMessageBody());
                if (!TextUtils.isEmpty(parseOneTimeCodeFromBody)) {
                    ClientAnalytics.get().logEvent(ClientAnalytics.SmsVerificationCategory, ClientAnalytics.Verified);
                    this._javaScriptBridge.Property(Key.SmsCode.name(), parseOneTimeCodeFromBody);
                    abortBroadcastWrapper();
                }
            }
        }
    }
}
