package com.microsoft.onlineid.sts.exception;

public class CorruptedUserDATokenException extends RuntimeException {
    private static final long serialVersionUID = 1;

    public CorruptedUserDATokenException(String str) {
        super(str);
    }

    public CorruptedUserDATokenException(String str, Throwable th) {
        super(str, th);
    }

    public CorruptedUserDATokenException(Throwable th) {
        super(th);
    }
}
