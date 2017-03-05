package com.microsoft.onlineid.internal.exception;

import com.microsoft.onlineid.exception.AuthenticationException;

public class AccountNotFoundException extends AuthenticationException {
    private static final long serialVersionUID = 1;

    public AccountNotFoundException(String str) {
        super(str);
    }

    public AccountNotFoundException(String str, Throwable th) {
        super(str, th);
    }

    public AccountNotFoundException(Throwable th) {
        super(th);
    }
}
