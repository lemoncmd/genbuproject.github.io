package com.microsoft.xbox.toolkit;

public class XLEException extends Exception {
    private long errorCode;
    private boolean isHandled;
    private Object userObject;

    public XLEException(long j) {
        this(j, null, null, null);
    }

    public XLEException(long j, String str) {
        this(j, str, null, null);
    }

    public XLEException(long j, String str, Throwable th) {
        this(j, null, th, null);
    }

    public XLEException(long j, String str, Throwable th, Object obj) {
        super(str, th);
        this.errorCode = j;
        this.userObject = obj;
        this.isHandled = false;
    }

    public XLEException(long j, Throwable th) {
        this(j, null, th, null);
    }

    public long getErrorCode() {
        return this.errorCode;
    }

    public boolean getIsHandled() {
        return this.isHandled;
    }

    public Object getUserObject() {
        return this.userObject;
    }

    public void setIsHandled(boolean z) {
        this.isHandled = z;
    }

    public String toString() {
        int i = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("XLEException ErrorCode: %d; ErrorMessage: %s \n\n", new Object[]{Long.valueOf(this.errorCode), getMessage()}));
        if (getCause() != null) {
            stringBuilder.append(String.format("\t Cause ErrorMessage: %s, StackTrace: ", new Object[]{getCause().toString()}));
            StackTraceElement[] stackTrace = getCause().getStackTrace();
            int length = stackTrace.length;
            while (i < length) {
                stringBuilder.append("\n\n \t " + stackTrace[i].toString());
                i++;
            }
        }
        return stringBuilder.toString();
    }
}
