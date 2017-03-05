package com.microsoft.onlineid.internal.ui;

import android.os.Bundle;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class WebFlowTelemetryData {
    protected final Bundle _values;

    public WebFlowTelemetryData() {
        this(null);
    }

    public WebFlowTelemetryData(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this._values = bundle;
    }

    public Bundle asBundle() {
        return this._values;
    }

    public String getCallingAppPackageName() {
        return this._values.getString(BundleMarshaller.ClientPackageNameKey);
    }

    public String getCallingAppVersionName() {
        return this._values.getString(BundleMarshaller.ClientAppVersionNameKey);
    }

    public boolean getIsWebTelemetryRequested() {
        return this._values.getBoolean(BundleMarshaller.WebFlowTelemetryRequestedKey, false);
    }

    public boolean getWasPrecachingEnabled() {
        return this._values.getBoolean(BundleMarshaller.WebFlowTelemetryPrecachingEnabledKey);
    }

    public WebFlowTelemetryData setCallingAppPackageName(String str) {
        this._values.putString(BundleMarshaller.ClientPackageNameKey, str);
        return this;
    }

    public WebFlowTelemetryData setCallingAppVersionName(String str) {
        this._values.putString(BundleMarshaller.ClientAppVersionNameKey, str);
        return this;
    }

    public WebFlowTelemetryData setIsWebTelemetryRequested(boolean z) {
        this._values.putBoolean(BundleMarshaller.WebFlowTelemetryRequestedKey, z);
        return this;
    }

    public WebFlowTelemetryData setWasPrecachingEnabled(boolean z) {
        this._values.putBoolean(BundleMarshaller.WebFlowTelemetryPrecachingEnabledKey, z);
        return this;
    }
}
