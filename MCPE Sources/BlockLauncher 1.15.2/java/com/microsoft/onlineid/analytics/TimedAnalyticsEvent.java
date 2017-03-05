package com.microsoft.onlineid.analytics;

import com.google.android.gms.analytics.HitBuilders.TimingBuilder;
import com.google.android.gms.analytics.Tracker;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.log.Logger;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TimedAnalyticsEvent implements ITimedAnalyticsEvent {
    private static final long StartTimeNotSet = -1;
    private final TimingBuilder _builder;
    private long _startTime = StartTimeNotSet;
    private final Tracker _tracker;

    TimedAnalyticsEvent(Tracker tracker, String str, String str2, String str3) {
        boolean z = (str == null || str2 == null) ? false : true;
        Assertion.check(z);
        this._tracker = tracker;
        this._builder = new TimingBuilder();
        this._builder.setCategory(str);
        this._builder.setVariable(str2);
        if (str3 != null) {
            this._builder.setLabel(str3);
        }
    }

    public void end() {
        if (this._startTime != StartTimeNotSet) {
            this._builder.setValue(TimeUnit.MILLISECONDS.convert(System.nanoTime() - this._startTime, TimeUnit.NANOSECONDS));
            send(this._builder.build());
            return;
        }
        Logger.error("TimedAnalyticsEvent.end() called before start().");
    }

    protected void send(Map<String, String> map) {
        this._tracker.send(map);
    }

    public TimedAnalyticsEvent setLabel(String str) {
        this._builder.setLabel(str);
        return this;
    }

    public TimedAnalyticsEvent start() {
        this._startTime = System.nanoTime();
        return this;
    }
}
