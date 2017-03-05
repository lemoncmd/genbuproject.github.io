package com.microsoft.onlineid.sts;

import android.content.Context;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import java.util.Date;

public class ClockSkewManager {
    private final TypedStorage _storage;

    public ClockSkewManager(Context context) {
        this._storage = new TypedStorage(context);
    }

    protected ClockSkewManager(TypedStorage typedStorage) {
        this._storage = typedStorage;
    }

    private void setSkewMilliseconds(long j) {
        this._storage.writeClockSkew(j);
    }

    protected Date getCurrentClientTime() {
        return new Date();
    }

    public Date getCurrentServerTime() {
        return toServerTime(getCurrentClientTime());
    }

    public long getSkewMilliseconds() {
        return this._storage.readClockSkew();
    }

    public void onTimestampReceived(long j) {
        setSkewMilliseconds(getCurrentClientTime().getTime() - j);
    }

    public Date toClientTime(Date date) {
        return new Date(date.getTime() + getSkewMilliseconds());
    }

    public Date toServerTime(Date date) {
        return new Date(date.getTime() - getSkewMilliseconds());
    }
}
