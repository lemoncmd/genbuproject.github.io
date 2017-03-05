package com.microsoft.onlineid.userdata;

import android.content.Context;
import android.text.TextUtils;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.userdata.MeContactReader.FullName;

public class SignUpData {
    private final AccountManagerReader _accountManagerReader;
    private final FullName _fullName;
    private final MeContactReader _meContactReader;
    private final TelephonyManagerReader _telephonyManagerReader;

    public SignUpData(Context context) {
        this(new TelephonyManagerReader(context), new AccountManagerReader(context), new MeContactReader(context));
    }

    SignUpData(TelephonyManagerReader telephonyManagerReader, AccountManagerReader accountManagerReader, MeContactReader meContactReader) {
        this._telephonyManagerReader = telephonyManagerReader;
        this._accountManagerReader = accountManagerReader;
        this._meContactReader = meContactReader;
        this._fullName = this._meContactReader.getFullName();
        Assertion.check(this._fullName != null);
    }

    public String getCountryCode() {
        return this._telephonyManagerReader.getIsoCountryCode();
    }

    public String getDeviceEmail() {
        return this._accountManagerReader.getDeviceEmail();
    }

    public String getFirstName() {
        return this._fullName.getFirstName();
    }

    public String getLastName() {
        return this._fullName.getLastName();
    }

    public String getPhone() {
        Object phoneNumber = this._telephonyManagerReader.getPhoneNumber();
        return TextUtils.isEmpty(phoneNumber) ? this._meContactReader.getPhoneNumber() : phoneNumber;
    }
}
