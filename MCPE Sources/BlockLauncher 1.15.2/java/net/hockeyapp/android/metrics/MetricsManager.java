package net.hockeyapp.android.metrics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import net.hockeyapp.android.metrics.model.Data;
import net.hockeyapp.android.metrics.model.Domain;
import net.hockeyapp.android.metrics.model.SessionState;
import net.hockeyapp.android.metrics.model.SessionStateData;
import net.hockeyapp.android.metrics.model.TelemetryData;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

public class MetricsManager {
    protected static final AtomicInteger ACTIVITY_COUNT = new AtomicInteger(0);
    protected static final AtomicLong LAST_BACKGROUND = new AtomicLong(getTime());
    private static final Object LOCK = new Object();
    private static final Integer SESSION_RENEWAL_INTERVAL = Integer.valueOf(20000);
    private static final String TAG = "HA-MetricsManager";
    private static volatile MetricsManager instance;
    private static Channel sChannel;
    private static Sender sSender;
    private static TelemetryContext sTelemetryContext;
    private static WeakReference<Application> sWeakApplication;
    private volatile boolean mSessionTrackingDisabled;
    private TelemetryLifecycleCallbacks mTelemetryLifecycleCallbacks;

    @TargetApi(14)
    private class TelemetryLifecycleCallbacks implements ActivityLifecycleCallbacks {
        private TelemetryLifecycleCallbacks() {
        }

        public void onActivityCreated(Activity activity, Bundle bundle) {
        }

        public void onActivityDestroyed(Activity activity) {
        }

        public void onActivityPaused(Activity activity) {
            MetricsManager.LAST_BACKGROUND.set(MetricsManager.getTime());
        }

        public void onActivityResumed(Activity activity) {
            MetricsManager.this.updateSession();
        }

        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }

        public void onActivityStarted(Activity activity) {
        }

        public void onActivityStopped(Activity activity) {
        }
    }

    protected MetricsManager(Context context, TelemetryContext telemetryContext, Sender sender, Persistence persistence, Channel channel) {
        sTelemetryContext = telemetryContext;
        if (sender == null) {
            sender = new Sender();
        }
        sSender = sender;
        if (persistence == null) {
            persistence = new Persistence(context, sender);
        } else {
            persistence.setSender(sender);
        }
        sSender.setPersistence(persistence);
        if (channel == null) {
            sChannel = new Channel(sTelemetryContext, persistence);
        } else {
            sChannel = channel;
        }
    }

    private static Application getApplication() {
        return sWeakApplication != null ? (Application) sWeakApplication.get() : null;
    }

    protected static Channel getChannel() {
        return sChannel;
    }

    protected static MetricsManager getInstance() {
        return instance;
    }

    protected static Sender getSender() {
        return sSender;
    }

    private static long getTime() {
        return new Date().getTime();
    }

    public static void register(Context context, Application application) {
        String appIdentifier = Util.getAppIdentifier(context);
        if (appIdentifier == null || appIdentifier.length() == 0) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, application, appIdentifier);
    }

    public static void register(Context context, Application application, String str) {
        register(context, application, str, null, null, null);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected static void register(android.content.Context r8, android.app.Application r9, java.lang.String r10, net.hockeyapp.android.metrics.Sender r11, net.hockeyapp.android.metrics.Persistence r12, net.hockeyapp.android.metrics.Channel r13) {
        /*
        r6 = 0;
        r0 = instance;
        if (r0 != 0) goto L_0x0048;
    L_0x0005:
        r7 = LOCK;
        monitor-enter(r7);
        r0 = instance;	 Catch:{ all -> 0x0049 }
        if (r0 != 0) goto L_0x004c;
    L_0x000c:
        r0 = r9.getApplicationContext();	 Catch:{ all -> 0x0050 }
        net.hockeyapp.android.Constants.loadFromContext(r0);	 Catch:{ all -> 0x0050 }
        r0 = new net.hockeyapp.android.metrics.MetricsManager;	 Catch:{ all -> 0x0050 }
        r1 = r9.getApplicationContext();	 Catch:{ all -> 0x0050 }
        r2 = new net.hockeyapp.android.metrics.TelemetryContext;	 Catch:{ all -> 0x0050 }
        r3 = r9.getApplicationContext();	 Catch:{ all -> 0x0050 }
        r2.<init>(r3, r10);	 Catch:{ all -> 0x0050 }
        r3 = r11;
        r4 = r12;
        r5 = r13;
        r0.<init>(r1, r2, r3, r4, r5);	 Catch:{ all -> 0x0050 }
        r1 = new java.lang.ref.WeakReference;	 Catch:{ all -> 0x0049 }
        r1.<init>(r9);	 Catch:{ all -> 0x0049 }
        sWeakApplication = r1;	 Catch:{ all -> 0x0049 }
        r1 = r0;
    L_0x0030:
        r0 = net.hockeyapp.android.utils.Util.sessionTrackingSupported();	 Catch:{ all -> 0x0049 }
        if (r0 != 0) goto L_0x004e;
    L_0x0036:
        r0 = 1;
    L_0x0037:
        r1.mSessionTrackingDisabled = r0;	 Catch:{ all -> 0x0049 }
        instance = r1;	 Catch:{ all -> 0x0049 }
        r0 = r1.mSessionTrackingDisabled;	 Catch:{ all -> 0x0049 }
        if (r0 != 0) goto L_0x0047;
    L_0x003f:
        r0 = 0;
        r0 = java.lang.Boolean.valueOf(r0);	 Catch:{ all -> 0x0049 }
        setSessionTrackingDisabled(r0);	 Catch:{ all -> 0x0049 }
    L_0x0047:
        monitor-exit(r7);	 Catch:{ all -> 0x0049 }
    L_0x0048:
        return;
    L_0x0049:
        r0 = move-exception;
    L_0x004a:
        monitor-exit(r7);	 Catch:{ all -> 0x0049 }
        throw r0;
    L_0x004c:
        r1 = r0;
        goto L_0x0030;
    L_0x004e:
        r0 = r6;
        goto L_0x0037;
    L_0x0050:
        r0 = move-exception;
        goto L_0x004a;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.metrics.MetricsManager.register(android.content.Context, android.app.Application, java.lang.String, net.hockeyapp.android.metrics.Sender, net.hockeyapp.android.metrics.Persistence, net.hockeyapp.android.metrics.Channel):void");
    }

    @TargetApi(14)
    private void registerTelemetryLifecycleCallbacks() {
        if (this.mTelemetryLifecycleCallbacks == null) {
            this.mTelemetryLifecycleCallbacks = new TelemetryLifecycleCallbacks();
        }
        getApplication().registerActivityLifecycleCallbacks(this.mTelemetryLifecycleCallbacks);
    }

    public static boolean sessionTrackingEnabled() {
        return !instance.mSessionTrackingDisabled;
    }

    public static void setCustomServerURL(String str) {
        if (sSender != null) {
            sSender.setCustomServerURL(str);
        } else {
            HockeyLog.warn(TAG, "HockeyApp couldn't set the custom server url. Please register(...) the MetricsManager before setting the server URL.");
        }
    }

    protected static void setSender(Sender sender) {
        sSender = sender;
    }

    public static void setSessionTrackingDisabled(Boolean bool) {
        if (instance == null) {
            HockeyLog.warn(TAG, "MetricsManager hasn't been registered. No Metrics will be collected!");
            return;
        }
        synchronized (LOCK) {
            if (Util.sessionTrackingSupported()) {
                instance.mSessionTrackingDisabled = bool.booleanValue();
                if (!bool.booleanValue()) {
                    instance.registerTelemetryLifecycleCallbacks();
                }
            } else {
                instance.mSessionTrackingDisabled = true;
                instance.unregisterTelemetryLifecycleCallbacks();
            }
        }
    }

    private void trackSessionState(final SessionState sessionState) {
        AsyncTaskUtils.execute(new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... voidArr) {
                TelemetryData sessionStateData = new SessionStateData();
                sessionStateData.setState(sessionState);
                MetricsManager.sChannel.enqueueData(MetricsManager.this.createData(sessionStateData));
                return null;
            }
        });
    }

    @TargetApi(14)
    private void unregisterTelemetryLifecycleCallbacks() {
        getApplication().unregisterActivityLifecycleCallbacks(this.mTelemetryLifecycleCallbacks);
        this.mTelemetryLifecycleCallbacks = null;
    }

    private void updateSession() {
        if (ACTIVITY_COUNT.getAndIncrement() != 0) {
            long time = getTime();
            long andSet = LAST_BACKGROUND.getAndSet(getTime());
            Object obj = time - andSet >= ((long) SESSION_RENEWAL_INTERVAL.intValue()) ? 1 : null;
            HockeyLog.debug(TAG, "Checking if we have to renew a session, time difference is: " + (time - andSet));
            if (obj != null && sessionTrackingEnabled()) {
                HockeyLog.debug(TAG, "Renewing session");
                renewSession();
            }
        } else if (sessionTrackingEnabled()) {
            HockeyLog.debug(TAG, "Starting & tracking session");
            renewSession();
        } else {
            HockeyLog.debug(TAG, "Session management disabled by the developer");
        }
    }

    protected Data<Domain> createData(TelemetryData telemetryData) {
        Data<Domain> data = new Data();
        data.setBaseData(telemetryData);
        data.setBaseType(telemetryData.getBaseType());
        data.QualifiedName = telemetryData.getEnvelopeName();
        return data;
    }

    protected void renewSession() {
        sTelemetryContext.renewSessionContext(UUID.randomUUID().toString());
        trackSessionState(SessionState.START);
    }

    protected void setChannel(Channel channel) {
        sChannel = channel;
    }
}
