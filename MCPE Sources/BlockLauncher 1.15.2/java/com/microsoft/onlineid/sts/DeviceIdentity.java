package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import java.io.Serializable;

public class DeviceIdentity implements Serializable {
    private static final long serialVersionUID = 1;
    private final DeviceCredentials _credentials;
    private final String _puid;
    private DAToken _token;

    public DeviceIdentity(DeviceCredentials deviceCredentials, String str, DAToken dAToken) {
        if (deviceCredentials == null || str == null) {
            throw new IllegalArgumentException("credentials and puid must not be null.");
        }
        this._credentials = deviceCredentials;
        this._puid = str;
        this._token = dAToken;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof DeviceIdentity)) {
            return false;
        }
        DeviceIdentity deviceIdentity = (DeviceIdentity) obj;
        return Objects.equals(this._credentials, deviceIdentity._credentials) && Objects.equals(this._puid, deviceIdentity._puid) && Objects.equals(this._token, deviceIdentity._token);
    }

    public DeviceCredentials getCredentials() {
        return this._credentials;
    }

    public DAToken getDAToken() {
        return this._token;
    }

    public String getPuid() {
        return this._puid;
    }

    public int hashCode() {
        return (this._credentials.hashCode() + this._puid.hashCode()) + Objects.hashCode(this._token);
    }

    void setDAToken(DAToken dAToken) {
        this._token = dAToken;
    }
}
