package com.microsoft.onlineid.internal.sso;

import com.microsoft.onlineid.exception.InternalException;

public class BundleMarshallerException extends InternalException {
    private static final long serialVersionUID = 1;

    public BundleMarshallerException(String str) {
        super(str);
    }

    public BundleMarshallerException(String str, Throwable th) {
        super(str, th);
    }

    public BundleMarshallerException(Throwable th) {
        super(th);
    }
}
