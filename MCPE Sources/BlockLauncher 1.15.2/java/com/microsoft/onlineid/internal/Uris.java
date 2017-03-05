package com.microsoft.onlineid.internal;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.userdata.AccountManagerReader;
import com.microsoft.onlineid.userdata.TelephonyManagerReader;
import net.hockeyapp.android.BuildConfig;

public class Uris {
    static final String EmailDelimiter = ",";
    static final String EmailParam = "email";
    static final String MktParam = "mkt";
    static final String PhoneParam = "phone";

    public static Uri appendEmails(AccountManagerReader accountManagerReader, Uri uri) {
        if (TextUtils.isEmpty(uri.getQueryParameter(EmailParam))) {
            Iterable emails = accountManagerReader.getEmails();
            return uri.buildUpon().appendQueryParameter(EmailParam, emails.isEmpty() ? BuildConfig.FLAVOR : TextUtils.join(EmailDelimiter, emails)).build();
        }
        Logger.warning("Given URL already has email parameter set.");
        return uri;
    }

    public static Uri appendMarketQueryString(Context context, Uri uri) {
        if (TextUtils.isEmpty(uri.getQueryParameter(MktParam))) {
            String string = Resources.getString(context, "app_market");
            Builder buildUpon = uri.buildUpon();
            if (TextUtils.isEmpty(string)) {
                string = "en";
            }
            return buildUpon.appendQueryParameter(MktParam, string).build();
        }
        Logger.warning("Given URL already has mkt parameter set.");
        return uri;
    }

    public static Uri appendPhoneDigits(TelephonyManagerReader telephonyManagerReader, Uri uri) {
        if (TextUtils.isEmpty(uri.getQueryParameter(PhoneParam))) {
            Object phoneNumber = telephonyManagerReader.getPhoneNumber();
            return uri.buildUpon().appendQueryParameter(PhoneParam, TextUtils.isEmpty(phoneNumber) ? BuildConfig.FLAVOR : phoneNumber.replaceAll("[^\\d]+", BuildConfig.FLAVOR)).build();
        }
        Logger.warning("Given URL already has phone parameter set.");
        return uri;
    }
}
