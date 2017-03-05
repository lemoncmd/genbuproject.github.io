package com.microsoft.cll.android;

import Microsoft.Android.LoggingLibrary.Snapshot;
import Ms.Telemetry.CllHeartBeat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;

public class ClientTelemetry {
    private ArrayList<Integer> settingsCallLatencies = new ArrayList();
    protected CllHeartBeat snapshot = new CllHeartBeat();
    private ArrayList<Integer> vortexCallLatencies = new ArrayList();

    public ClientTelemetry() {
        Reset();
    }

    protected Snapshot GetEvent() {
        Snapshot snapshot = new Snapshot();
        snapshot.setBaseData(this.snapshot);
        return snapshot;
    }

    protected void IncrementEventsDroppedDueToQuota() {
        this.snapshot.setQuotaDropCount(this.snapshot.getQuotaDropCount() + 1);
    }

    protected void IncrementEventsQueuedForUpload() {
        IncrementEventsQueuedForUpload(1);
    }

    protected void IncrementEventsQueuedForUpload(int i) {
        this.snapshot.setEventsQueued(this.snapshot.getEventsQueued() + i);
    }

    protected void IncrementLogFailures() {
        this.snapshot.setLogFailures(this.snapshot.getLogFailures() + 1);
    }

    protected void IncrementRejectDropCount(int i) {
        this.snapshot.setRejectDropCount(this.snapshot.getRejectDropCount() + i);
    }

    protected void IncrementSettingsHttpAttempts() {
        this.snapshot.setSettingsHttpAttempts(this.snapshot.getSettingsHttpAttempts() + 1);
    }

    protected void IncrementSettingsHttpFailures(int i) {
        this.snapshot.setSettingsHttpFailures(this.snapshot.getSettingsHttpFailures() + 1);
        if (i >= 400 && i < 500) {
            this.snapshot.setSettingsFailures4xx(this.snapshot.getSettingsFailures4xx() + 1);
        }
        if (i >= 500 && i < 600) {
            this.snapshot.setSettingsFailures5xx(this.snapshot.getSettingsFailures5xx() + 1);
        }
        if (i == -1) {
            this.snapshot.setSettingsFailuresTimeout(this.snapshot.getSettingsFailuresTimeout() + 1);
        }
    }

    protected void IncrementVortexHttpAttempts() {
        this.snapshot.setVortexHttpAttempts(this.snapshot.getVortexHttpAttempts() + 1);
    }

    protected void IncrementVortexHttpFailures(int i) {
        this.snapshot.setVortexHttpFailures(this.snapshot.getVortexHttpFailures() + 1);
        if (i >= 400 && i < 500) {
            this.snapshot.setVortexFailures4xx(this.snapshot.getVortexFailures4xx() + 1);
        }
        if (i >= 500 && i < 600) {
            this.snapshot.setVortexFailures5xx(this.snapshot.getVortexFailures5xx() + 1);
        }
        if (i == -1) {
            this.snapshot.setVortexFailuresTimeout(this.snapshot.getVortexFailuresTimeout() + 1);
        }
    }

    protected void Reset() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        this.snapshot.setLastHeartBeat(simpleDateFormat.format(new Date()).toString());
        this.snapshot.setEventsQueued(0);
        this.snapshot.setLogFailures(0);
        this.snapshot.setQuotaDropCount(0);
        this.snapshot.setRejectDropCount(0);
        this.snapshot.setVortexHttpAttempts(0);
        this.snapshot.setVortexHttpFailures(0);
        this.snapshot.setCacheUsagePercent(0.0d);
        this.snapshot.setAvgVortexLatencyMs(0);
        this.snapshot.setMaxVortexLatencyMs(0);
        this.snapshot.setSettingsHttpAttempts(0);
        this.snapshot.setSettingsHttpFailures(0);
        this.snapshot.setAvgSettingsLatencyMs(0);
        this.snapshot.setMaxSettingsLatencyMs(0);
        this.snapshot.setVortexFailures4xx(0);
        this.snapshot.setVortexFailures5xx(0);
        this.snapshot.setVortexFailuresTimeout(0);
        this.snapshot.setSettingsFailures4xx(0);
        this.snapshot.setSettingsFailures5xx(0);
        this.snapshot.setSettingsFailuresTimeout(0);
        this.settingsCallLatencies.clear();
        this.vortexCallLatencies.clear();
    }

    protected void SetAvgSettingsLatencyMs(int i) {
        this.settingsCallLatencies.add(Integer.valueOf(i));
        Iterator it = this.settingsCallLatencies.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            i2 = ((Integer) it.next()).intValue() + i2;
        }
        this.snapshot.setAvgSettingsLatencyMs(i2 / this.settingsCallLatencies.size());
    }

    protected void SetAvgVortexLatencyMs(int i) {
        this.vortexCallLatencies.add(Integer.valueOf(i));
        Iterator it = this.vortexCallLatencies.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            i2 = ((Integer) it.next()).intValue() + i2;
        }
        this.snapshot.setAvgVortexLatencyMs(i2 / this.vortexCallLatencies.size());
    }

    protected void SetCacheUsagePercent(double d) {
        this.snapshot.setCacheUsagePercent(d);
    }

    protected void SetMaxSettingsLatencyMs(int i) {
        if (this.snapshot.getMaxSettingsLatencyMs() < i) {
            this.snapshot.setMaxSettingsLatencyMs(i);
        }
    }

    protected void SetMaxVortexLatencyMs(int i) {
        if (this.snapshot.getMaxVortexLatencyMs() < i) {
            this.snapshot.setMaxVortexLatencyMs(i);
        }
    }
}
