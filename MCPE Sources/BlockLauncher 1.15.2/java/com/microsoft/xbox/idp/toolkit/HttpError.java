package com.microsoft.xbox.idp.toolkit;

public class HttpError {
    private final int errorCode;
    private final String errorMessage;
    private final int httpStatus;

    public HttpError(int i, int i2, String str) {
        this.errorCode = i;
        this.httpStatus = i2;
        this.errorMessage = str;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public int getHttpStatus() {
        return this.httpStatus;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("errorCode: ").append(this.errorCode).append(", httpStatus: ").append(this.httpStatus).append(", errorMessage: ").append(this.errorMessage);
        return stringBuffer.toString();
    }
}
