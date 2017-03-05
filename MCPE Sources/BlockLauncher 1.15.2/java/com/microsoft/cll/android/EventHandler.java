package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.SettingsStore.Settings;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.mozilla.javascript.regexp.NativeRegExp;

public class EventHandler extends ScheduledWorker {
    static final /* synthetic */ boolean $assertionsDisabled = (!EventHandler.class.desiredAssertionStatus());
    private final String TAG = "AndroidCll-EventHandler";
    private final ClientTelemetry clientTelemetry;
    private final List<ICllEvents> cllEvents;
    final AbstractHandler criticalHandler;
    private URL endpoint;
    private final ILogger logger;
    final AbstractHandler normalHandler;
    private double sampleId;
    private EventSender sender;
    private ITicketCallback ticketCallback;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$cll$android$EventEnums$Persistence = new int[Persistence.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$cll$android$EventEnums$Persistence[Persistence.PersistenceNormal.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$cll$android$EventEnums$Persistence[Persistence.PersistenceCritical.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    protected EventHandler(ClientTelemetry clientTelemetry, List<ICllEvents> list, ILogger iLogger, AbstractHandler abstractHandler, AbstractHandler abstractHandler2) {
        super((long) SettingsStore.getCllSettingsAsInt(Settings.QUEUEDRAININTERVAL));
        this.clientTelemetry = clientTelemetry;
        this.cllEvents = list;
        this.logger = iLogger;
        this.normalHandler = abstractHandler;
        this.criticalHandler = abstractHandler2;
        this.sampleId = EventEnums.SampleRate_Unspecified;
    }

    public EventHandler(ClientTelemetry clientTelemetry, List<ICllEvents> list, ILogger iLogger, String str) {
        super((long) SettingsStore.getCllSettingsAsInt(Settings.QUEUEDRAININTERVAL));
        this.clientTelemetry = clientTelemetry;
        this.cllEvents = list;
        this.logger = iLogger;
        this.criticalHandler = new CriticalEventHandler(iLogger, str, clientTelemetry);
        this.normalHandler = new NormalEventHandler(iLogger, str, clientTelemetry);
        this.sampleId = EventEnums.SampleRate_Unspecified;
    }

    private boolean Filter(SerializedEvent serializedEvent) {
        if (serializedEvent.getSerializedData().length() > SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSIZEINBYTES)) {
            this.logger.info("AndroidCll-EventHandler", "Event is too large");
            return true;
        } else if (IsUploadEnabled() && IsInSample(serializedEvent)) {
            return false;
        } else {
            this.logger.info("AndroidCll-EventHandler", "Filtered event");
            return true;
        }
    }

    private boolean IsInSample(SerializedEvent serializedEvent) {
        if (this.sampleId < -1.0E-5d) {
            this.sampleId = 0.0d;
            String deviceId = serializedEvent.getDeviceId();
            if (deviceId != null && deviceId.length() > 7) {
                try {
                    this.sampleId = ((double) (Long.parseLong(deviceId.substring(deviceId.length() - 7), 16) % 10000)) / EventEnums.SampleRate_NoSampling;
                } catch (NumberFormatException e) {
                }
            }
            this.logger.info("AndroidCll-EventHandler", "Sample Id is " + String.valueOf(this.sampleId) + " based on deviceId of " + deviceId);
        }
        return this.sampleId < EventEnums.SampleRate_Epsilon + serializedEvent.getSampleRate();
    }

    private boolean IsUploadEnabled() {
        return SettingsStore.getCllSettingsAsBoolean(Settings.UPLOADENABLED);
    }

    private boolean startEventQueueWriter(Runnable runnable) {
        if (this.endpoint == null) {
            this.logger.warn("AndroidCll-EventHandler", "No endpoint set");
            return false;
        }
        EventQueueWriter eventQueueWriter = (EventQueueWriter) runnable;
        if (this.sender != null) {
            eventQueueWriter.setSender(this.sender);
        }
        try {
            this.executor.execute(runnable);
        } catch (RejectedExecutionException e) {
            this.logger.warn("AndroidCll-EventHandler", "Could not start new thread for EventQueueWriter");
            return false;
        } catch (NullPointerException e2) {
            this.logger.error("AndroidCll-EventHandler", "Executor is null. Is the cll paused or stopped?");
        }
        return true;
    }

    protected boolean addToStorage(SerializedEvent serializedEvent, List<String> list) {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$cll$android$EventEnums$Persistence[serializedEvent.getPersistence().ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                break;
            case NativeRegExp.PREFIX /*2*/:
                try {
                    this.criticalHandler.add(serializedEvent.getSerializedData(), list);
                    break;
                } catch (IOException e) {
                    this.logger.error("AndroidCll-EventHandler", "Could not add event to normal storage");
                    return false;
                } catch (FileFullException e2) {
                    this.logger.warn("AndroidCll-EventHandler", "No space on disk to store events");
                    return false;
                }
            default:
                this.logger.error("AndroidCll-EventHandler", "Unknown persistence");
                if (!$assertionsDisabled) {
                    throw new AssertionError();
                }
                break;
        }
        try {
            this.normalHandler.add(serializedEvent.getSerializedData(), list);
            return true;
        } catch (IOException e3) {
            this.logger.error("AndroidCll-EventHandler", "Could not add event to normal storage");
            return false;
        } catch (FileFullException e4) {
            this.logger.warn("AndroidCll-EventHandler", "No space on disk to store events");
            return false;
        }
    }

    protected boolean log(SerializedEvent serializedEvent, List<String> list) {
        boolean z = false;
        if (Filter(serializedEvent)) {
            return false;
        }
        if (EventQueueWriter.getRunningThreadCount() >= SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSIZEINBYTES)) {
            z = true;
        }
        if (!(serializedEvent.getLatency() != Latency.LatencyRealtime || this.isPaused || r0)) {
            if (startEventQueueWriter(new EventQueueWriter(this.endpoint, serializedEvent, list, this.clientTelemetry, this.cllEvents, this.logger, this.executor, this, this.ticketCallback))) {
                return true;
            }
        }
        return addToStorage(serializedEvent, list);
    }

    public void run() {
        if (this.interval != ((long) SettingsStore.getCllSettingsAsInt(Settings.QUEUEDRAININTERVAL))) {
            this.nextExecution.cancel(false);
            this.interval = (long) SettingsStore.getCllSettingsAsInt(Settings.QUEUEDRAININTERVAL);
            this.nextExecution = this.executor.scheduleAtFixedRate(this, this.interval, this.interval, TimeUnit.SECONDS);
        }
        if (EventQueueWriter.future != null) {
            this.logger.info("AndroidCll-EventHandler", "Retry logic in progress, skipping normal send");
        } else {
            send();
        }
    }

    protected boolean send() {
        return send(null);
    }

    protected boolean send(Persistence persistence) {
        if (this.isPaused) {
            return false;
        }
        List list = null;
        if (persistence != null) {
            switch (AnonymousClass1.$SwitchMap$com$microsoft$cll$android$EventEnums$Persistence[persistence.ordinal()]) {
                case NativeRegExp.MATCH /*1*/:
                    this.logger.info("AndroidCll-EventHandler", "Draining normal events");
                    list = this.normalHandler.getFilesForDraining();
                    break;
                case NativeRegExp.PREFIX /*2*/:
                    this.logger.info("AndroidCll-EventHandler", "Draining Critical events");
                    list = this.criticalHandler.getFilesForDraining();
                    break;
                default:
                    this.logger.error("AndroidCll-EventHandler", "Unknown persistence");
                    if (!$assertionsDisabled) {
                        throw new AssertionError();
                    }
                    break;
            }
        }
        this.logger.info("AndroidCll-EventHandler", "Draining All events");
        list = this.normalHandler.getFilesForDraining();
        list.addAll(this.criticalHandler.getFilesForDraining());
        return (list == null || list.size() == 0) ? true : startEventQueueWriter(new EventQueueWriter(this.endpoint, list, this.clientTelemetry, this.cllEvents, this.logger, this.executor, this.ticketCallback));
    }

    protected void setEndpointUrl(String str) {
        try {
            this.endpoint = new URL(str);
        } catch (MalformedURLException e) {
            this.logger.error("AndroidCll-EventHandler", "Bad Endpoint URL Form");
        }
    }

    void setSender(EventSender eventSender) {
        this.sender = eventSender;
    }

    void setXuidCallback(ITicketCallback iTicketCallback) {
        this.ticketCallback = iTicketCallback;
    }

    public void stop() {
        super.stop();
        this.normalHandler.close();
        this.criticalHandler.close();
    }

    void synchronize() {
        ((NormalEventHandler) this.normalHandler).writeQueueToDisk();
    }
}
