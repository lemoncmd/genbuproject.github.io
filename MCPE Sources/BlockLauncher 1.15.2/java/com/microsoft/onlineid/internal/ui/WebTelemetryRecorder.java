package com.microsoft.onlineid.internal.ui;

import android.os.Bundle;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import java.util.ArrayList;
import java.util.Iterator;

public class WebTelemetryRecorder {
    private static final int MAX_CHAR_COUNT = 15000;
    private int _charCount = 0;
    private ArrayList<String> _events;
    private boolean _shouldRecord;
    private boolean _wereAllEventsCaptured;

    public WebTelemetryRecorder(boolean z, Bundle bundle) {
        this._shouldRecord = z;
        if (bundle == null || !bundle.containsKey(BundleMarshaller.WebFlowTelemetryEventsKey)) {
            this._events = new ArrayList();
            this._wereAllEventsCaptured = true;
            return;
        }
        this._events = bundle.getStringArrayList(BundleMarshaller.WebFlowTelemetryEventsKey);
        this._wereAllEventsCaptured = bundle.getBoolean(BundleMarshaller.WebFlowTelemetryAllEventsCapturedKey, false);
        Iterator it = this._events.iterator();
        while (it.hasNext()) {
            String str = (String) it.next();
            this._charCount = str.length() + this._charCount;
        }
    }

    private boolean canFitEvent(String str) {
        return this._charCount + str.length() <= MAX_CHAR_COUNT;
    }

    public ArrayList<String> getEvents() {
        return this._events;
    }

    public boolean hasEvents() {
        return this._shouldRecord && !this._events.isEmpty();
    }

    public boolean isRequested() {
        return this._shouldRecord;
    }

    public void recordEvent(String str) {
        if (!this._shouldRecord) {
            return;
        }
        if (canFitEvent(str)) {
            this._events.add(str);
            this._charCount += str.length();
            return;
        }
        this._wereAllEventsCaptured = false;
        Logger.warning("Dropped web telemetry event of size: " + str.length());
    }

    public void saveInstanceState(Bundle bundle) {
        bundle.putStringArrayList(BundleMarshaller.WebFlowTelemetryEventsKey, getEvents());
        bundle.putBoolean(BundleMarshaller.WebFlowTelemetryAllEventsCapturedKey, wereAllEventsCaptured());
    }

    public boolean wereAllEventsCaptured() {
        return this._wereAllEventsCaptured;
    }
}
