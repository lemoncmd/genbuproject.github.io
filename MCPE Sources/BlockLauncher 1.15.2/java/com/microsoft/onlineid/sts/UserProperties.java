package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import java.util.HashMap;
import java.util.Map;

public class UserProperties {
    private final Map<UserProperty, String> _userProperties = new HashMap();

    public enum UserProperty {
        CID
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof UserProperties)) {
            return false;
        }
        return Objects.equals(this._userProperties, ((UserProperties) obj)._userProperties);
    }

    public String get(UserProperty userProperty) {
        return (String) this._userProperties.get(userProperty);
    }

    public boolean has(UserProperty userProperty) {
        return this._userProperties.containsKey(userProperty);
    }

    public int hashCode() {
        return Objects.hashCode(this._userProperties);
    }

    public UserProperties put(UserProperty userProperty, String str) {
        this._userProperties.put(userProperty, str);
        return this;
    }
}
