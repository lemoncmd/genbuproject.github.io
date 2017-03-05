package com.microsoft.onlineid.userdata;

import android.content.Context;
import android.telephony.TelephonyManager;

public class TelephonyManagerReader implements IPhoneNumberReader {
    public TelephonyManagerReader(Context ctx) {
    }

    public TelephonyManagerReader(TelephonyManager tm) {
    }

    public String getIsoCountryCode() {
        return null;
    }

    public String getPhoneNumber() {
        return null;
    }
}
