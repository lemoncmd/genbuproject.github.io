package com.microsoft.onlineid.internal.sso;

import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.Strings;

public class MasterRedirectException extends InternalException {
    private static final long serialVersionUID = 1;
    private final String _redirectRequestTo;

    public MasterRedirectException(String str, String str2) {
        super(str + ": " + str2);
        Strings.verifyArgumentNotNullOrEmpty(str2, "redirectRequestTo");
        this._redirectRequestTo = str2;
    }

    public String getRedirectRequestTo() {
        return this._redirectRequestTo;
    }
}
