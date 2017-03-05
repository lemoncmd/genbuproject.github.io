package com.microsoft.onlineid.sts;

import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import java.util.Locale;

public class StringCodeServerError {
    private final String _error;
    private final int _subError;

    public StringCodeServerError(String str, int i) {
        Strings.verifyArgumentNotNullOrEmpty(str, "error");
        this._error = str;
        this._subError = i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof StringCodeServerError)) {
            return false;
        }
        StringCodeServerError stringCodeServerError = (StringCodeServerError) obj;
        return Objects.equals(this._error, stringCodeServerError._error) && this._subError == stringCodeServerError._subError;
    }

    public String getError() {
        return this._error;
    }

    public int getSubError() {
        return this._subError;
    }

    public int hashCode() {
        return Objects.hashCode(this._error) + this._subError;
    }

    public String toString() {
        return String.format(Locale.US, "Server Error: %s SubError: %s", new Object[]{this._error, StsErrorCode.getFriendlyHRDescription(this._subError)});
    }
}
