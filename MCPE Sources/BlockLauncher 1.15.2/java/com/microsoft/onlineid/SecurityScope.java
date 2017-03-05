package com.microsoft.onlineid;

import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.util.Locale;

public class SecurityScope implements ISecurityScope {
    private static final long serialVersionUID = 1;
    private String _oAuthString;
    private final String _policy;
    private final String _target;

    public SecurityScope(String str, String str2) {
        Strings.verifyArgumentNotNullOrEmpty(str, "target");
        this._target = str;
        this._policy = str2;
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (!(obj instanceof ISecurityScope)) {
                return false;
            }
            ISecurityScope iSecurityScope = (ISecurityScope) obj;
            if (!getTarget().equalsIgnoreCase(iSecurityScope.getTarget())) {
                return false;
            }
            if (!Strings.equalsIgnoreCase(getPolicy(), iSecurityScope.getPolicy())) {
                return false;
            }
        }
        return true;
    }

    public String getPolicy() {
        return this._policy;
    }

    public String getTarget() {
        return this._target;
    }

    public int hashCode() {
        return Objects.hashCode(toString());
    }

    public String toString() {
        if (this._oAuthString == null) {
            this._oAuthString = String.format(Locale.US, "service::%s::%s", new Object[]{this._target, this._policy});
        }
        return this._oAuthString;
    }
}
