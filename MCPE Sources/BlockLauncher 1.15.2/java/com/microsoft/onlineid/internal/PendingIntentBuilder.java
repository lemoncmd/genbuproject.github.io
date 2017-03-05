package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class PendingIntentBuilder {
    private Context _context;
    private final Intent _intent;

    public PendingIntentBuilder(Intent intent) {
        this._intent = intent;
    }

    public PendingIntentBuilder(ApiRequest apiRequest) {
        this(apiRequest.asIntent());
        setContext(apiRequest.getContext());
    }

    public PendingIntent buildActivity() {
        return buildActivity(134217728);
    }

    public PendingIntent buildActivity(int i) {
        Objects.verifyArgumentNotNull(this._context, "context");
        Objects.verifyArgumentNotNull(this._intent, "intent");
        return PendingIntent.getActivity(this._context, 0, this._intent, i);
    }

    public PendingIntentBuilder setContext(Context context) {
        this._context = context;
        return this;
    }
}
