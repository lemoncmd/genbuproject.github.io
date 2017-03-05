package com.microsoft.xbox.idp.toolkit;

public abstract class LoaderResult<T> {
    private final T data;
    private final HttpError error;

    protected LoaderResult(T t, HttpError httpError) {
        this.data = t;
        this.error = httpError;
    }

    public T getData() {
        return this.data;
    }

    public HttpError getError() {
        return this.error;
    }

    public boolean hasData() {
        return this.data != null;
    }

    public boolean hasError() {
        return this.error != null;
    }

    public abstract boolean isReleased();

    public abstract void release();
}
