package com.microsoft.onlineid.internal.sso.client;

import com.microsoft.onlineid.exception.InternalException;

public class ServiceBindingException extends InternalException {
    private static final long serialVersionUID = 1;

    public ServiceBindingException(String str) {
        super(str);
    }

    public ServiceBindingException(String str, Throwable th) {
        super(str, th);
    }

    public ServiceBindingException(Throwable th) {
        super(th);
    }
}
