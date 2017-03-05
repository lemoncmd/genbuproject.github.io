package com.microsoft.onlineid;

import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.io.Serializable;
import java.util.Date;
import org.mozilla.javascript.ES6Iterator;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1;
    private final Date _expiry;
    private final ISecurityScope _scope;
    private final String _value;

    public Ticket(ISecurityScope iSecurityScope, Date date, String str) {
        Objects.verifyArgumentNotNull(iSecurityScope, "scope");
        Objects.verifyArgumentNotNull(date, "expiry");
        Strings.verifyArgumentNotNullOrEmpty(str, ES6Iterator.VALUE_PROPERTY);
        this._scope = iSecurityScope;
        this._expiry = date;
        this._value = str;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Ticket)) {
            return false;
        }
        Ticket ticket = (Ticket) obj;
        return Objects.equals(this._scope, ticket._scope) && Objects.equals(this._expiry, ticket._expiry) && Objects.equals(this._value, ticket._value);
    }

    public Date getExpiry() {
        return this._expiry;
    }

    public ISecurityScope getScope() {
        return this._scope;
    }

    public String getValue() {
        return this._value;
    }

    public int hashCode() {
        return (Objects.hashCode(this._scope) + Objects.hashCode(this._expiry)) + Objects.hashCode(this._value);
    }

    public String toString() {
        return "Ticket{scope: " + this._scope + ", expiry: " + this._expiry + "}";
    }
}
