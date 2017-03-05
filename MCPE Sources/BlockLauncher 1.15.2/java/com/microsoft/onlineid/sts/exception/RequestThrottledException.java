package com.microsoft.onlineid.sts.exception;

import com.microsoft.onlineid.exception.NetworkException;

public class RequestThrottledException extends NetworkException {
    private static final long serialVersionUID = 1;

    public RequestThrottledException(String str) {
        super(str);
    }

    public RequestThrottledException(String str, Throwable th) {
        super(str, th);
    }

    public RequestThrottledException(Throwable th) {
        super(th);
    }
}
