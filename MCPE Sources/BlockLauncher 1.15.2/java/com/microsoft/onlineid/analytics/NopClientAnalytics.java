package com.microsoft.onlineid.analytics;

import java.util.Map;

public class NopClientAnalytics implements IClientAnalytics {
    public ITimedAnalyticsEvent createTimedEvent(String str, String str2) {
        return new NopTimedAnalyticsEvent();
    }

    public ITimedAnalyticsEvent createTimedEvent(String str, String str2, String str3) {
        return new NopTimedAnalyticsEvent();
    }

    public IClientAnalytics logCertificates(Map<String, byte[]> map) {
        return this;
    }

    public IClientAnalytics logClockSkew(long j) {
        return this;
    }

    public IClientAnalytics logEvent(String str, String str2) {
        return this;
    }

    public IClientAnalytics logEvent(String str, String str2, String str3) {
        return this;
    }

    public IClientAnalytics logEvent(String str, String str2, String str3, Long l) {
        return this;
    }

    public IClientAnalytics logException(Throwable th) {
        return this;
    }

    public IClientAnalytics logScreenView(String str) {
        return this;
    }

    public IClientAnalytics logTotalAccountsEvent(String str, int i, int i2) {
        return this;
    }

    public IClientAnalytics send(Map<String, String> map) {
        return this;
    }

    public void setTestMode() {
    }
}
