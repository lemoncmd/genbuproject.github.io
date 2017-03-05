package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.SecurityScope;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

public class DAToken implements Serializable {
    public static final ISecurityScope Scope = new SecurityScope("http://Passport.NET/tb", null);
    private static final long serialVersionUID = 1;
    private final byte[] _sessionKey;
    private final String _token;

    public DAToken(String str, byte[] bArr) {
        Strings.verifyArgumentNotNullOrEmpty(str, "token");
        Objects.verifyArgumentNotNull(bArr, "sessionKey");
        this._token = str;
        this._sessionKey = bArr;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof DAToken)) {
            return false;
        }
        DAToken dAToken = (DAToken) obj;
        return Objects.equals(this._token, dAToken._token) && Arrays.equals(this._sessionKey, dAToken._sessionKey);
    }

    public String getOneTimeSignedCredential(Date date, String str) {
        return new OneTimeCredentialSigner(date, this).generateOneTimeSignedCredential(str);
    }

    public byte[] getSessionKey() {
        return this._sessionKey;
    }

    public String getToken() {
        return this._token;
    }

    public int hashCode() {
        return this._token.hashCode() + Arrays.hashCode(this._sessionKey);
    }
}
