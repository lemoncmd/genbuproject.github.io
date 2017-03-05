package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore.Settings;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.mozilla.javascript.Context;

public class EventQueueWriter implements Runnable {
    private static int backoffSeconds = 0;
    protected static ScheduledFuture future;
    protected static AtomicBoolean running = new AtomicBoolean(false);
    private static AtomicInteger s_threadCount = new AtomicInteger(0);
    private final String TAG = "AndroidCll-EventQueueWriter";
    private final EventBatcher batcher;
    private final ClientTelemetry clientTelemetry;
    private final List<ICllEvents> cllEvents;
    private EventCompressor compressor;
    private URL endpoint;
    private final SerializedEvent event;
    private final ScheduledExecutorService executorService;
    private EventHandler handler;
    private final List<String> ids;
    private final ILogger logger;
    private final Random random = new Random();
    private List<IStorage> removedStorages;
    private EventSender sender;
    private final List<IStorage> storages;
    private final ITicketCallback ticketCallback;
    private final TicketManager ticketManager;

    enum SendResult {
        SUCCESS,
        ERROR
    }

    public EventQueueWriter(URL url, SerializedEvent serializedEvent, List<String> list, ClientTelemetry clientTelemetry, List<ICllEvents> list2, ILogger iLogger, ScheduledExecutorService scheduledExecutorService, EventHandler eventHandler, ITicketCallback iTicketCallback) {
        this.cllEvents = list2;
        this.event = serializedEvent;
        this.ids = list;
        this.logger = iLogger;
        this.ticketCallback = iTicketCallback;
        this.sender = new EventSender(url, clientTelemetry, iLogger);
        this.batcher = null;
        this.storages = null;
        this.executorService = scheduledExecutorService;
        this.clientTelemetry = clientTelemetry;
        this.handler = eventHandler;
        this.endpoint = url;
        this.ticketManager = new TicketManager(iTicketCallback, iLogger);
        clientTelemetry.IncrementEventsQueuedForUpload();
    }

    public EventQueueWriter(URL url, List<IStorage> list, ClientTelemetry clientTelemetry, List<ICllEvents> list2, ILogger iLogger, ScheduledExecutorService scheduledExecutorService, ITicketCallback iTicketCallback) {
        this.cllEvents = list2;
        this.storages = list;
        this.logger = iLogger;
        this.ticketCallback = iTicketCallback;
        this.batcher = new EventBatcher();
        this.sender = new EventSender(url, clientTelemetry, iLogger);
        this.compressor = new EventCompressor(iLogger);
        this.event = null;
        this.ids = null;
        this.executorService = scheduledExecutorService;
        this.clientTelemetry = clientTelemetry;
        this.endpoint = url;
        this.removedStorages = new ArrayList();
        this.ticketManager = new TicketManager(iTicketCallback, iLogger);
    }

    private void cancelBackoff() {
        future = null;
        backoffSeconds = 0;
    }

    private byte[] getEventData(String str) {
        return str.getBytes(Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET));
    }

    public static int getRunningThreadCount() {
        return s_threadCount.get();
    }

    private SendResult sendBatch(String str, IStorage iStorage) {
        boolean z = false;
        this.logger.info("AndroidCll-EventQueueWriter", "Sending Batch of events");
        if (str.equals(BuildConfig.FLAVOR)) {
            this.removedStorages.add(iStorage);
            return SendResult.SUCCESS;
        }
        boolean z2;
        this.logger.info("AndroidCll-EventQueueWriter", "Compressing events");
        byte[] compress = this.compressor.compress(str);
        if (compress == null) {
            compress = getEventData(str);
            z2 = false;
        } else {
            z2 = true;
        }
        try {
            int sendEvent;
            int sendEvent2 = this.sender.sendEvent(compress, z2, this.ticketManager.getHeaders(false));
            if (sendEvent2 == 401) {
                this.logger.info("AndroidCll-EventQueueWriter", "We got a 401 while sending the events, refreshing the tokens and trying again");
                sendEvent = this.sender.sendEvent(compress, z2, this.ticketManager.getHeaders(true));
                if (sendEvent == 401) {
                    this.logger.info("AndroidCll-EventQueueWriter", "After refreshing the tokens we still got a 401. Most likely we couldn't get new tokens so we will keep these events on disk and try to get new tokens later");
                }
            } else {
                sendEvent = sendEvent2;
            }
            if (sendEvent == Context.VERSION_ES6 || sendEvent == 400) {
                z = true;
            }
        } catch (IOException e) {
            this.logger.error("AndroidCll-EventQueueWriter", "Cannot send event: " + e.getMessage());
        }
        return z ? SendResult.SUCCESS : SendResult.ERROR;
    }

    private SendResult sendInternal() {
        for (IStorage iStorage : this.storages) {
            if (this.executorService.isShutdown()) {
                return SendResult.SUCCESS;
            }
            SendResult sendBatch;
            this.ticketManager.clean();
            for (Tuple tuple : iStorage.drain()) {
                this.ticketManager.addTickets((List) tuple.b);
                this.clientTelemetry.IncrementEventsQueuedForUpload();
                if (((String) tuple.a).length() > SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSIZEINBYTES)) {
                    this.logger.warn("AndroidCll-EventQueueWriter", "Dropping event because it is too large.");
                    for (ICllEvents eventDropped : this.cllEvents) {
                        eventDropped.eventDropped((String) tuple.a);
                    }
                } else if (this.batcher.tryAddingEventToBatch((String) tuple.a)) {
                    continue;
                } else {
                    this.logger.info("AndroidCll-EventQueueWriter", "Got a full batch, preparing to send");
                    String batchedEvents = this.batcher.getBatchedEvents();
                    if (!this.batcher.tryAddingEventToBatch((String) tuple.a)) {
                        this.logger.error("AndroidCll-EventQueueWriter", "Could not add events to an empty batch");
                    }
                    sendBatch = sendBatch(batchedEvents, iStorage);
                    if (sendBatch == SendResult.ERROR) {
                        iStorage.close();
                        return sendBatch;
                    }
                }
            }
            this.logger.info("AndroidCll-EventQueueWriter", "Preparing to send");
            sendBatch = sendBatch(this.batcher.getBatchedEvents(), iStorage);
            iStorage.close();
            if (sendBatch == SendResult.ERROR) {
                return sendBatch;
            }
            iStorage.discard();
        }
        this.logger.info("AndroidCll-EventQueueWriter", "Sent " + this.clientTelemetry.snapshot.getEventsQueued() + " events.");
        for (ICllEvents sendComplete : this.cllEvents) {
            sendComplete.sendComplete();
        }
        return SendResult.SUCCESS;
    }

    int generateBackoffInterval() {
        int cllSettingsAsInt = SettingsStore.getCllSettingsAsInt(Settings.CONSTANTFORRETRYPERIOD);
        int cllSettingsAsInt2 = SettingsStore.getCllSettingsAsInt(Settings.MAXRETRYPERIOD);
        int cllSettingsAsInt3 = SettingsStore.getCllSettingsAsInt(Settings.BASERETRYPERIOD);
        if (backoffSeconds == 0) {
            backoffSeconds = Math.max(0, cllSettingsAsInt);
        }
        if (this.logger.getVerbosity() == Verbosity.INFO) {
            this.logger.info("AndroidCll-EventQueueWriter", "Generating new backoff interval using \"Random.nextInt(" + (backoffSeconds + 1) + ") seconds\" formula.");
        }
        cllSettingsAsInt = this.random.nextInt(backoffSeconds + 1);
        backoffSeconds = Math.min(cllSettingsAsInt3 * backoffSeconds, cllSettingsAsInt2);
        if (this.logger.getVerbosity() == Verbosity.INFO) {
            this.logger.info("AndroidCll-EventQueueWriter", "The generated backoff interval is " + cllSettingsAsInt + ".");
        }
        return cllSettingsAsInt;
    }

    public void run() {
        try {
            s_threadCount.getAndAdd(1);
            this.logger.info("AndroidCll-EventQueueWriter", "Starting upload");
            if (this.storages == null) {
                sendRealTimeEvent(this.event);
            } else if (running.compareAndSet(false, true)) {
                send();
                running.set(false);
                s_threadCount.getAndAdd(-1);
            } else {
                this.logger.info("AndroidCll-EventQueueWriter", "Skipping send, event sending is already in progress on different thread.");
                s_threadCount.getAndAdd(-1);
            }
        } finally {
            s_threadCount.getAndAdd(-1);
        }
    }

    protected void send() {
        if (sendInternal() == SendResult.SUCCESS) {
            cancelBackoff();
            return;
        }
        int generateBackoffInterval = generateBackoffInterval();
        this.storages.removeAll(this.removedStorages);
        Runnable eventQueueWriter = new EventQueueWriter(this.endpoint, this.storages, this.clientTelemetry, this.cllEvents, this.logger, this.executorService, this.ticketCallback);
        eventQueueWriter.setSender(this.sender);
        future = this.executorService.schedule(eventQueueWriter, (long) generateBackoffInterval, TimeUnit.SECONDS);
    }

    protected void sendRealTimeEvent(SerializedEvent serializedEvent) {
        Object obj = null;
        String serializedData = serializedEvent.getSerializedData();
        if (serializedData.length() <= SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSIZEINBYTES)) {
            try {
                this.ticketManager.clean();
                this.ticketManager.addTickets(this.ids);
                TicketHeaders headers = this.ticketManager.getHeaders(false);
                byte[] eventData = getEventData(serializedData);
                int sendEvent = this.sender.sendEvent(eventData, false, headers);
                if (sendEvent == 401) {
                    sendEvent = this.sender.sendEvent(eventData, false, this.ticketManager.getHeaders(true));
                }
                if (sendEvent == Context.VERSION_ES6 || sendEvent == 400) {
                    obj = 1;
                }
            } catch (IOException e) {
                this.logger.error("AndroidCll-EventQueueWriter", "Cannot send event");
            }
            if (obj != null) {
                cancelBackoff();
                for (ICllEvents sendComplete : this.cllEvents) {
                    sendComplete.sendComplete();
                }
                return;
            }
            this.handler.addToStorage(serializedEvent, this.ids);
        }
    }

    void setSender(EventSender eventSender) {
        this.sender = eventSender;
    }
}
