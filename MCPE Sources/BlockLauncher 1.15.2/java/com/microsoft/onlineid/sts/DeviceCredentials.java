package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import java.io.Serializable;

public class DeviceCredentials implements Serializable {
    private static final long serialVersionUID = 1;
    private final String _password;
    private final String _username;

    public DeviceCredentials(String str, String str2) {
        if (str == null || str2 == null) {
            throw new IllegalArgumentException("username and password must not be null.");
        }
        this._username = str;
        this._password = str2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof DeviceCredentials)) {
            return false;
        }
        DeviceCredentials deviceCredentials = (DeviceCredentials) obj;
        return Objects.equals(this._username, deviceCredentials._username) && Objects.equals(this._password, deviceCredentials._password);
    }

    public String getPassword() {
        return this._password;
    }

    public String getUsername() {
        return this._username;
    }

    public int hashCode() {
        return this._username.hashCode() + this._password.hashCode();
    }
}
