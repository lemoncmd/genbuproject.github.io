package com.microsoft.onlineid.internal.sso.client;

import android.app.PendingIntent;
import junit.framework.Assert;

public class SsoResponse<T> {
    private T _data;
    private PendingIntent _pendingIntent;

    public T getData() {
        return this._data;
    }

    public PendingIntent getPendingIntent() {
        return this._pendingIntent;
    }

    public boolean hasData() {
        return this._pendingIntent == null;
    }

    public boolean hasPendingIntent() {
        return this._pendingIntent != null;
    }

    public SsoResponse<T> setData(T t) {
        this._data = t;
        Assert.assertNull(this._pendingIntent);
        return this;
    }

    public SsoResponse<T> setPendingIntent(PendingIntent pendingIntent) {
        this._pendingIntent = pendingIntent;
        Assert.assertNull(this._data);
        return this;
    }
}
