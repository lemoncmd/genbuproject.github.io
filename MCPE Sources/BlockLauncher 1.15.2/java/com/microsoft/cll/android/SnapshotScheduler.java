package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import com.microsoft.cll.android.SettingsStore.Settings;
import java.util.EnumSet;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SnapshotScheduler extends ScheduledWorker {
    private final String TAG = "AndroidCll-SnapshotScheduler";
    private final ClientTelemetry clientTelemetry;
    private final ISingletonCll cll;
    private final ILogger logger;

    public SnapshotScheduler(ClientTelemetry clientTelemetry, ILogger iLogger, ISingletonCll iSingletonCll) {
        super(SettingsStore.getCllSettingsAsLong(Settings.SNAPSHOTSCHEDULEINTERVAL));
        this.cll = iSingletonCll;
        this.clientTelemetry = clientTelemetry;
        this.logger = iLogger;
    }

    private void recordStatistics() {
        this.cll.log(this.clientTelemetry.GetEvent(), Latency.LatencyUnspecified, Persistence.PersistenceUnspecified, EnumSet.of(Sensitivity.SensitivityUnspecified), EventEnums.SampleRate_Unspecified, null);
        this.clientTelemetry.Reset();
    }

    public void resume(ScheduledExecutorService scheduledExecutorService) {
        this.executor = scheduledExecutorService;
        this.nextExecution = scheduledExecutorService.scheduleAtFixedRate(this, this.interval, this.interval, TimeUnit.SECONDS);
        this.isPaused = false;
    }

    public void run() {
        this.logger.info("AndroidCll-SnapshotScheduler", "Uploading snapshot");
        if (this.interval != ((long) SettingsStore.getCllSettingsAsInt(Settings.SNAPSHOTSCHEDULEINTERVAL))) {
            this.nextExecution.cancel(false);
            this.interval = (long) SettingsStore.getCllSettingsAsInt(Settings.SNAPSHOTSCHEDULEINTERVAL);
            this.nextExecution = this.executor.scheduleAtFixedRate(this, this.interval, this.interval, TimeUnit.SECONDS);
        }
        recordStatistics();
    }

    public void start(ScheduledExecutorService scheduledExecutorService) {
        this.executor = scheduledExecutorService;
        this.nextExecution = scheduledExecutorService.scheduleAtFixedRate(this, this.interval, this.interval, TimeUnit.SECONDS);
    }
}
