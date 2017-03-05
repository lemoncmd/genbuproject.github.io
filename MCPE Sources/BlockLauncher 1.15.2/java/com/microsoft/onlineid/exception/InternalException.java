package com.microsoft.onlineid.exception;

public class InternalException extends AuthenticationException {
    private static final long serialVersionUID = 1;

    public InternalException(String str) {
        super(str);
    }

    public InternalException(String str, Throwable th) {
        super(str, th);
    }

    public InternalException(Throwable th) {
        super(th);
    }
}
