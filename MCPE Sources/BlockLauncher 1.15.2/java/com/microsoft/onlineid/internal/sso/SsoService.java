package com.microsoft.onlineid.internal.sso;

public class SsoService {
    public static final String SsoServiceIntent = "com.microsoft.msa.action.SSO_SERVICE";
    private final long _firstInstallTime;
    private final String _packageName;
    private final String _sdkVersion;
    private final int _ssoVersion;

    public SsoService(String str, int i, String str2) {
        this._packageName = str;
        this._ssoVersion = i;
        this._sdkVersion = str2;
        this._firstInstallTime = -1;
    }

    public SsoService(String str, int i, String str2, long j) {
        this._packageName = str;
        this._ssoVersion = i;
        this._sdkVersion = str2;
        this._firstInstallTime = j;
    }

    public long getFirstInstallTime() {
        return this._firstInstallTime;
    }

    public String getPackageName() {
        return this._packageName;
    }

    public String getSdkVersion() {
        return this._sdkVersion;
    }

    public int getSsoVersion() {
        return this._ssoVersion;
    }

    public String toString() {
        return "[" + this._packageName + ": sso " + this._ssoVersion + ", sdk " + this._sdkVersion + "]";
    }
}
