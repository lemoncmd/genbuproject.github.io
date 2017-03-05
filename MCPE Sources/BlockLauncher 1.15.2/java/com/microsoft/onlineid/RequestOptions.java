package com.microsoft.onlineid;

import android.os.Bundle;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public abstract class RequestOptions<B extends RequestOptions<B>> {
    protected final Bundle _values;

    protected RequestOptions() {
        this(new Bundle());
    }

    protected RequestOptions(Bundle bundle) {
        Objects.verifyArgumentNotNull(bundle, "bundle");
        this._values = bundle;
    }

    public Bundle asBundle() {
        return this._values;
    }

    public String getFlightConfiguration() {
        return this._values.getString(BundleMarshaller.ClientFlightsKey);
    }

    public String getPrefillUsername() {
        return this._values.getString(BundleMarshaller.PrefillUsernameKey);
    }

    public String getUnauthenticatedSessionId() {
        return this._values.getString(BundleMarshaller.UnauthenticatedSessionIdKey);
    }

    public boolean getWasPrecachingEnabled() {
        return this._values.getBoolean(BundleMarshaller.WebFlowTelemetryPrecachingEnabledKey, false);
    }

    public B setFlightConfiguration(String str) {
        this._values.putString(BundleMarshaller.ClientFlightsKey, str);
        return this;
    }

    public B setPrefillUsername(String str) {
        this._values.putString(BundleMarshaller.PrefillUsernameKey, str);
        return this;
    }

    public B setUnauthenticatedSessionId(String str) {
        this._values.putString(BundleMarshaller.UnauthenticatedSessionIdKey, str);
        return this;
    }

    public B setWasPrecachingEnabled(boolean z) {
        this._values.putBoolean(BundleMarshaller.WebFlowTelemetryPrecachingEnabledKey, z);
        return this;
    }
}
