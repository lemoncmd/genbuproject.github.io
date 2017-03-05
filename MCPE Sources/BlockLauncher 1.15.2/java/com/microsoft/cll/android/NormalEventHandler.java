package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.SettingsStore.Settings;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class NormalEventHandler extends AbstractHandler {
    private final String TAG = "AndroidCll-NormalEventHandler";
    private final int queueSize = SettingsStore.getCllSettingsAsInt(Settings.NORMALEVENTMEMORYQUEUESIZE);
    private ArrayBlockingQueue<Tuple<String, List<String>>> queueStorage;

    public NormalEventHandler(ILogger iLogger, String str, ClientTelemetry clientTelemetry) {
        super(iLogger, str, clientTelemetry);
        this.fileStorage = new FileStorage(".norm.cllevent", iLogger, str, this);
        this.queueStorage = new ArrayBlockingQueue(this.queueSize);
    }

    public void add(String str, List<String> list) {
        synchronized (this) {
            Tuple tuple = new Tuple(str, list);
            if (!this.queueStorage.offer(tuple)) {
                writeQueueToDisk();
                this.queueStorage.offer(tuple);
            }
        }
    }

    public void close() {
        this.logger.info("AndroidCll-NormalEventHandler", "Closing normal file");
        writeQueueToDisk();
        this.fileStorage.close();
    }

    public void dispose(IStorage iStorage) {
        totalStorageUsed.getAndAdd(-1 * iStorage.size());
    }

    public List<IStorage> getFilesForDraining() {
        List<IStorage> filesByExtensionForDraining;
        synchronized (this) {
            if (this.queueStorage.size() > 0) {
                writeQueueToDisk();
            }
            if (this.fileStorage.size() > 0) {
                this.fileStorage.close();
                filesByExtensionForDraining = getFilesByExtensionForDraining(".norm.cllevent");
                this.fileStorage = new FileStorage(".norm.cllevent", this.logger, this.filePath, this);
            } else {
                filesByExtensionForDraining = getFilesByExtensionForDraining(".norm.cllevent");
            }
        }
        return filesByExtensionForDraining;
    }

    void writeQueueToDisk() {
        synchronized (this) {
            try {
                List<Tuple> arrayList = new ArrayList(this.queueSize);
                this.queueStorage.drainTo(arrayList);
                this.logger.info("AndroidCll-NormalEventHandler", "Writing " + arrayList.size() + " events to disk");
                for (Tuple tuple : arrayList) {
                    if (ensureCanAdd(tuple, Persistence.PersistenceNormal)) {
                        if (!this.fileStorage.canAdd(tuple)) {
                            this.logger.info("AndroidCll-NormalEventHandler", "Closing full file and opening a new one");
                            this.fileStorage.close();
                            this.fileStorage = new FileStorage(".norm.cllevent", this.logger, this.filePath, this);
                        }
                        this.fileStorage.add(tuple);
                        totalStorageUsed.getAndAdd((long) ((String) tuple.a).length());
                    } else {
                        this.clientTelemetry.IncrementEventsDroppedDueToQuota();
                        this.logger.warn("AndroidCll-NormalEventHandler", "Out of storage space for normal events. Logged event was dropped.");
                    }
                }
            } catch (Exception e) {
                this.logger.error("AndroidCll-NormalEventHandler", "Could not write events to disk");
            }
            this.fileStorage.flush();
        }
    }
}
