package com.microsoft.onlineid.sts.exception;

import java.util.Locale;

public class StsSignatureException extends StsParseException {
    private static final long serialVersionUID = 1;

    public StsSignatureException(String str, Throwable th, Object... objArr) {
        super(String.format(Locale.US, str, objArr), th, new Object[0]);
    }

    public StsSignatureException(String str, Object... objArr) {
        super(String.format(Locale.US, str, objArr), new Object[0]);
    }

    public StsSignatureException(Throwable th) {
        super(th);
    }
}
