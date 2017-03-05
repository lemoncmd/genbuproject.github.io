package com.microsoft.onlineid.exception;

public class NetworkException extends AuthenticationException {
    private static final long serialVersionUID = 1;

    public NetworkException() {
        this("No internet connection");
    }

    public NetworkException(String str) {
        super(str);
    }

    public NetworkException(String str, Throwable th) {
        super(str, th);
    }

    public NetworkException(Throwable th) {
        super(th);
    }
}
