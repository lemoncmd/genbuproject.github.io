package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Persistence;
import java.io.IOException;
import java.util.List;

public class CriticalEventHandler extends AbstractHandler {
    private final String TAG = "AndroidCll-CriticalEventHandler";

    public CriticalEventHandler(ILogger iLogger, String str, ClientTelemetry clientTelemetry) {
        super(iLogger, str, clientTelemetry);
        this.fileStorage = new FileStorage(".crit.cllevent", iLogger, str, this);
    }

    public void add(String str, List<String> list) throws IOException, FileFullException {
        synchronized (this) {
            Tuple tuple = new Tuple(str, list);
            if (!ensureCanAdd(tuple, Persistence.PersistenceCritical)) {
                this.clientTelemetry.IncrementEventsDroppedDueToQuota();
                this.logger.warn("AndroidCll-CriticalEventHandler", "Out of storage space for critical events. Logged event was dropped.");
            }
            if (!this.fileStorage.canAdd(tuple)) {
                this.logger.info("AndroidCll-CriticalEventHandler", "Closing full file and opening a new one");
                this.fileStorage.close();
                this.fileStorage = new FileStorage(".crit.cllevent", this.logger, this.filePath, this);
            }
            this.fileStorage.add(tuple);
            totalStorageUsed.getAndAdd((long) str.length());
            this.fileStorage.flush();
        }
    }

    public void close() {
        this.logger.info("AndroidCll-CriticalEventHandler", "Closing critical file");
        this.fileStorage.close();
    }

    public void dispose(IStorage iStorage) {
        totalStorageUsed.getAndAdd(-1 * iStorage.size());
    }

    public List<IStorage> getFilesForDraining() {
        List<IStorage> filesByExtensionForDraining;
        synchronized (this) {
            if (this.fileStorage.size() > 0) {
                this.fileStorage.close();
                filesByExtensionForDraining = getFilesByExtensionForDraining(".crit.cllevent");
                this.fileStorage = new FileStorage(".crit.cllevent", this.logger, this.filePath, this);
            } else {
                filesByExtensionForDraining = getFilesByExtensionForDraining(".crit.cllevent");
            }
        }
        return filesByExtensionForDraining;
    }
}
