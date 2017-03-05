package com.microsoft.onlineid.sts;

import java.util.Locale;

public class IntegerCodeServerError {
    private final int _error;
    private final String _message;
    private final int _subError;

    public IntegerCodeServerError(int i) {
        this(i, 0, null);
    }

    public IntegerCodeServerError(int i, int i2) {
        this(i, i2, null);
    }

    public IntegerCodeServerError(int i, int i2, String str) {
        this._error = i;
        this._subError = i2;
        this._message = str;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof IntegerCodeServerError)) {
            return false;
        }
        IntegerCodeServerError integerCodeServerError = (IntegerCodeServerError) obj;
        return this._error == integerCodeServerError._error && this._subError == integerCodeServerError._subError;
    }

    public int getError() {
        return this._error;
    }

    public int getSubError() {
        return this._subError;
    }

    public int hashCode() {
        return this._error + this._subError;
    }

    public String toString() {
        return String.format(Locale.US, "Server Error: %s SubError: %s Message: %s", new Object[]{StsErrorCode.getFriendlyHRDescription(this._error), StsErrorCode.getFriendlyHRDescription(this._subError), this._message});
    }
}
