package com.microsoft.onlineid.internal.exception;

import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.Strings;

public class UserOperationException extends AuthenticationException {
    private static final long serialVersionUID = 1;
    private final String _bodyString;
    private final String _headerString;

    public UserOperationException(String str) {
        super(str);
        Strings.verifyArgumentNotNullOrEmpty(str, "bodyMessage");
        this._headerString = null;
        this._bodyString = str;
    }

    public UserOperationException(String str, String str2) {
        super(str + " " + str2);
        Strings.verifyArgumentNotNullOrEmpty(str2, "bodyMessage");
        Strings.verifyArgumentNotNullOrEmpty(str, "headerString");
        this._headerString = str;
        this._bodyString = str2;
    }

    public UserOperationException(String str, String str2, Throwable th) {
        super(str + " " + str2, th);
        Strings.verifyArgumentNotNullOrEmpty(str2, "bodyMessage");
        Strings.verifyArgumentNotNullOrEmpty(str, "headerString");
        this._headerString = str;
        this._bodyString = str2;
    }

    public UserOperationException(String str, Throwable th) {
        super(str, th);
        Strings.verifyArgumentNotNullOrEmpty(str, "bodyMessage");
        this._headerString = null;
        this._bodyString = str;
    }

    public String getBodyMessage() {
        return this._bodyString;
    }

    public String getHeaderString() {
        return this._headerString;
    }
}
