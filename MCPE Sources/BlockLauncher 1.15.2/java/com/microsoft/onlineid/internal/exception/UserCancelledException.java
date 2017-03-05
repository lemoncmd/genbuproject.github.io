package com.microsoft.onlineid.internal.exception;

import com.microsoft.onlineid.exception.InternalException;

public class UserCancelledException extends InternalException {
    private static final long serialVersionUID = 1;

    public UserCancelledException(String str) {
        super(str);
    }

    public UserCancelledException(String str, Throwable th) {
        super(str, th);
    }

    public UserCancelledException(Throwable th) {
        super(th);
    }
}
