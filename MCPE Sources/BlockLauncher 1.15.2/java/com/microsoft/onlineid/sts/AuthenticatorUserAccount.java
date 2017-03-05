package com.microsoft.onlineid.sts;

import android.text.TextUtils;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AuthenticatorUserAccount implements Serializable {
    private static final long serialVersionUID = 1;
    private final String _cid;
    private String _displayName;
    private Set<Integer> _flights;
    private String _gcmRegistrationID;
    private boolean _isSessionApprover = false;
    private boolean _isSessionApproverRegistrationNeeded = true;
    private final String _puid;
    private String _serverKeyIdentifier;
    private long _timeOfLastProfileUpdate;
    private DAToken _token;
    private byte[] _totpKey;
    private String _username;

    public AuthenticatorUserAccount(String str, String str2, String str3, DAToken dAToken) {
        Strings.verifyArgumentNotNullOrEmpty(str3, "username");
        this._puid = str;
        this._cid = str2;
        this._username = str3;
        this._token = dAToken;
        this._totpKey = null;
        this._serverKeyIdentifier = null;
        this._flights = new HashSet();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof AuthenticatorUserAccount)) {
            return false;
        }
        AuthenticatorUserAccount authenticatorUserAccount = (AuthenticatorUserAccount) obj;
        return Objects.equals(this._puid, authenticatorUserAccount._puid) && this._isSessionApprover == authenticatorUserAccount._isSessionApprover && Objects.equals(this._username, authenticatorUserAccount._username) && Objects.equals(this._token, authenticatorUserAccount._token) && Arrays.equals(this._totpKey, authenticatorUserAccount._totpKey);
    }

    public String getCid() {
        return this._cid;
    }

    public DAToken getDAToken() {
        return this._token;
    }

    public String getDisplayName() {
        return this._displayName;
    }

    public Set<Integer> getFlights() {
        return this._flights != null ? this._flights : Collections.emptySet();
    }

    public String getGcmRegistrationID() {
        return this._gcmRegistrationID;
    }

    public String getPuid() {
        return this._puid;
    }

    public String getServerKeyIdentifier() {
        return this._serverKeyIdentifier;
    }

    public long getTimeOfLastProfileUpdate() {
        return this._timeOfLastProfileUpdate;
    }

    public byte[] getTotpKey() {
        return this._totpKey;
    }

    public String getUsername() {
        return this._username;
    }

    public boolean hasNgcRegistrationSucceeded() {
        return this._serverKeyIdentifier != null;
    }

    public int hashCode() {
        return Objects.hashCode(this._puid);
    }

    public boolean isNewAndInOutOfBandInterrupt() {
        return TextUtils.isEmpty(this._puid);
    }

    public boolean isSessionApprover() {
        return this._isSessionApprover;
    }

    public boolean isSessionApproverRegistrationNeeded() {
        return this._isSessionApproverRegistrationNeeded;
    }

    public void setDAToken(DAToken dAToken) {
        Objects.verifyArgumentNotNull(dAToken, "token");
        this._token = dAToken;
    }

    public void setDisplayName(String str) {
        this._displayName = str;
    }

    public void setFlights(Set<Integer> set) {
        this._flights = set;
    }

    public void setGcmRegistrationID(String str) {
        this._gcmRegistrationID = str;
    }

    public void setIsSessionApprover(boolean z) {
        this._isSessionApprover = z;
    }

    public void setIsSessionApproverRegistrationNeeded(boolean z) {
        this._isSessionApproverRegistrationNeeded = z;
    }

    public void setServerKeyIdentifier(String str) {
        this._serverKeyIdentifier = str;
    }

    public void setTimeOfLastProfileUpdate(long j) {
        this._timeOfLastProfileUpdate = j;
    }

    public void setTotpKey(byte[] bArr) {
        this._totpKey = bArr;
    }

    public void setUsername(String str) {
        Strings.verifyArgumentNotNullOrEmpty(str, "username");
        this._username = str;
    }
}
