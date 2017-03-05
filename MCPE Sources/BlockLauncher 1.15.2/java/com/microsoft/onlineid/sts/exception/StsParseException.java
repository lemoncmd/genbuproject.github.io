package com.microsoft.onlineid.sts.exception;

import java.util.Locale;

public class StsParseException extends InvalidResponseException {
    private static final long serialVersionUID = 1;

    public StsParseException(String str, Throwable th, Object... objArr) {
        super(String.format(Locale.US, str, objArr), th);
    }

    public StsParseException(String str, Object... objArr) {
        super(String.format(Locale.US, str, objArr));
    }

    public StsParseException(Throwable th) {
        super(th);
    }
}
