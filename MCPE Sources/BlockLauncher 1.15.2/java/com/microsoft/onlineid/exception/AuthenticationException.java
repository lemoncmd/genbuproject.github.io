package com.microsoft.onlineid.exception;

public abstract class AuthenticationException extends Exception {
    private static final long serialVersionUID = 1;

    public AuthenticationException(String str) {
        super(str);
    }

    public AuthenticationException(String str, Throwable th) {
        super(str, th);
    }

    public AuthenticationException(Throwable th) {
        super(th);
    }
}
