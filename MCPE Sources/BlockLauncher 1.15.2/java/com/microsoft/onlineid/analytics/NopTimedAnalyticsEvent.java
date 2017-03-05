package com.microsoft.onlineid.analytics;

public class NopTimedAnalyticsEvent implements ITimedAnalyticsEvent {
    public void end() {
    }

    public NopTimedAnalyticsEvent setLabel(String str) {
        return this;
    }

    public NopTimedAnalyticsEvent start() {
        return this;
    }
}
