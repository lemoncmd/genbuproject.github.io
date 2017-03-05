package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import com.microsoft.cll.android.SettingsStore.Settings;
import com.microsoft.telemetry.Base;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import net.hockeyapp.android.BuildConfig;

public class SingletonCll implements ISingletonCll {
    private static SingletonCll Instance;
    private static Object InstanceLock = new Object();
    protected final String TAG = "AndroidCll-SingletonCll";
    protected final ClientTelemetry clientTelemetry;
    protected final List<ICllEvents> cllEvents;
    public CorrelationVector correlationVector;
    protected EventHandler eventHandler;
    private ScheduledExecutorService executor;
    private final AtomicBoolean isChanging;
    private final AtomicBoolean isPaused;
    private final AtomicBoolean isStarted;
    protected ILogger logger;
    protected PartA partA;
    protected SettingsSync settingsSync;
    protected SnapshotScheduler snapshotScheduler;
    private ITicketCallback ticketCallback;

    private SingletonCll(String str, ILogger iLogger, String str2, PartA partA, CorrelationVector correlationVector) {
        if (str == null || str == BuildConfig.FLAVOR) {
            throw new IllegalArgumentException("iKey cannot be null or \"\"");
        }
        iLogger.setVerbosity(Verbosity.NONE);
        this.correlationVector = correlationVector;
        this.logger = iLogger;
        this.partA = partA;
        this.clientTelemetry = new ClientTelemetry();
        this.cllEvents = new ArrayList();
        this.eventHandler = new EventHandler(this.clientTelemetry, this.cllEvents, iLogger, str2);
        this.isChanging = new AtomicBoolean(false);
        this.isStarted = new AtomicBoolean(false);
        this.isPaused = new AtomicBoolean(false);
        this.settingsSync = new SettingsSync(this.clientTelemetry, iLogger, str, partA);
        this.snapshotScheduler = new SnapshotScheduler(this.clientTelemetry, iLogger, this);
        setEndpointUrl(SettingsStore.getCllSettingsAsString(Settings.VORTEXPRODURL));
    }

    public static ISingletonCll getInstance(String str, ILogger iLogger, String str2, PartA partA, CorrelationVector correlationVector) {
        if (Instance == null) {
            synchronized (InstanceLock) {
                if (Instance == null) {
                    Instance = new SingletonCll(str, iLogger, str2, partA, correlationVector);
                }
            }
        }
        return Instance;
    }

    public void SubscribeCllEvents(ICllEvents iCllEvents) {
    }

    public String getAppUserId() {
        return this.partA.getAppUserId();
    }

    public void log(Base base, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list) {
        if (!this.isStarted.get()) {
            this.logger.error("AndroidCll-SingletonCll", "Cll must be started before logging events");
        } else if (list == null || this.ticketCallback != null) {
            this.eventHandler.log(this.partA.populate(base, latency, persistence, enumSet, d, list), list);
        } else {
            this.logger.error("AndroidCll-SingletonCll", "You must set the ticket callback if you want to log ids with your events");
        }
    }

    public void pause() {
        if (this.isChanging.compareAndSet(false, true)) {
            if (this.isStarted.get() && !this.isPaused.get()) {
                this.eventHandler.pause();
                this.settingsSync.pause();
                this.snapshotScheduler.pause();
                this.executor.shutdown();
                this.isPaused.set(true);
            }
            this.isChanging.set(false);
        }
    }

    public void resume() {
        if (this.isChanging.compareAndSet(false, true)) {
            if (this.isStarted.get() && this.isPaused.get()) {
                this.executor = Executors.newScheduledThreadPool(SettingsStore.getCllSettingsAsInt(Settings.THREADSTOUSEWITHEXECUTOR));
                this.snapshotScheduler.resume(this.executor);
                this.eventHandler.resume(this.executor);
                this.settingsSync.resume(this.executor);
                this.isPaused.set(false);
            }
            this.isChanging.set(false);
        }
    }

    public void send() {
        if (this.isStarted.get()) {
            this.eventHandler.send();
        } else {
            this.logger.info("AndroidCll-SingletonCll", "Cannot send while the CLL is stopped.");
        }
    }

    public void setAppUserId(String str) {
        this.partA.setAppUserId(str);
    }

    public void setDebugVerbosity(Verbosity verbosity) {
        this.logger.setVerbosity(verbosity);
    }

    public void setEndpointUrl(String str) {
        this.eventHandler.setEndpointUrl(str);
    }

    protected void setEventSender(EventSender eventSender) {
        this.eventHandler.setSender(eventSender);
    }

    public void setExperimentId(String str) {
        this.partA.setExpId(str);
    }

    public void setXuidCallback(ITicketCallback iTicketCallback) {
        this.ticketCallback = iTicketCallback;
        if (this.isStarted.get() || this.isPaused.get()) {
            this.logger.warn("AndroidCll-SingletonCll", "Xuid callback must be set before start.");
        } else {
            this.eventHandler.setXuidCallback(iTicketCallback);
        }
    }

    public void start() {
        if (this.isChanging.compareAndSet(false, true)) {
            if (!this.isStarted.get()) {
                this.executor = Executors.newScheduledThreadPool(3);
                this.snapshotScheduler.start(this.executor);
                this.eventHandler.start(this.executor);
                this.settingsSync.start(this.executor);
                this.isStarted.set(true);
            }
            this.isChanging.set(false);
        }
    }

    public void stop() {
        if (this.isChanging.compareAndSet(false, true)) {
            if (this.isStarted.get()) {
                this.eventHandler.stop();
                this.settingsSync.stop();
                this.snapshotScheduler.stop();
                this.executor.shutdown();
                this.isStarted.set(false);
            }
            for (ICllEvents stopped : this.cllEvents) {
                stopped.stopped();
            }
            this.isChanging.set(false);
        }
    }

    public void synchronize() {
        this.eventHandler.synchronize();
    }

    public void useLegacyCS(boolean z) {
        this.partA.useLegacyCS(z);
    }
}
