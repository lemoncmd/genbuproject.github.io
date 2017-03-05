package com.microsoft.onlineid.sts.exception;

import com.microsoft.onlineid.exception.InternalException;

public class InvalidResponseException extends InternalException {
    private static final long serialVersionUID = 1;

    public InvalidResponseException(String str) {
        super(str);
    }

    public InvalidResponseException(String str, Throwable th) {
        super(str, th);
    }

    public InvalidResponseException(Throwable th) {
        super(th);
    }
}
