package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;

public class SerializedEvent {
    private String deviceId;
    private Latency latency;
    private Persistence persistence;
    private double sampleRate;
    private String serializedData;

    public String getDeviceId() {
        return this.deviceId;
    }

    public Latency getLatency() {
        return this.latency;
    }

    public Persistence getPersistence() {
        return this.persistence;
    }

    public double getSampleRate() {
        return this.sampleRate;
    }

    public String getSerializedData() {
        return this.serializedData;
    }

    public void setDeviceId(String str) {
        this.deviceId = str;
    }

    public void setLatency(Latency latency) {
        this.latency = latency;
    }

    public void setPersistence(Persistence persistence) {
        this.persistence = persistence;
    }

    public void setSampleRate(double d) {
        this.sampleRate = d;
    }

    public void setSerializedData(String str) {
        this.serializedData = str;
    }
}
