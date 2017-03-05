package com.microsoft.onlineid.sts.exception;

import com.microsoft.onlineid.exception.AuthenticationException;
import java.util.Locale;

public class InlineFlowException extends AuthenticationException {
    private static final long serialVersionUID = 1;
    private final String _errorCode;
    private final String _errorUrl;
    private final String _extendedErrorString;

    public InlineFlowException(String str, String str2, String str3, String str4) {
        super(str);
        this._errorUrl = str2;
        this._errorCode = str3;
        this._extendedErrorString = str4;
    }

    public String getErrorCode() {
        return this._errorCode;
    }

    public String getErrorUrl() {
        return this._errorUrl;
    }

    public String getExtendedErrorString() {
        return this._extendedErrorString;
    }

    public String toString() {
        return String.format(Locale.US, "Inline flow error to be resolved at '%s': %s (code = %s, extended = %s)", new Object[]{this._errorUrl, getMessage(), this._errorCode, this._extendedErrorString});
    }
}
