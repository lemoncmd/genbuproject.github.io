package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.SettingsStore.Settings;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractHandler {
    protected static final String criticalEventFileExtension = ".crit.cllevent";
    protected static final String normalEventFileExtension = ".norm.cllevent";
    protected static AtomicLong totalStorageUsed = new AtomicLong(0);
    private final String TAG = "AndroidCll-AbstractHandler";
    protected final ClientTelemetry clientTelemetry;
    protected String filePath;
    protected FileStorage fileStorage;
    protected final ILogger logger;

    public AbstractHandler(ILogger iLogger, String str, ClientTelemetry clientTelemetry) {
        this.filePath = str;
        this.logger = iLogger;
        this.clientTelemetry = clientTelemetry;
        setFileStorageUsed();
    }

    private boolean deleteFile(File file) {
        boolean z = false;
        try {
            z = file.delete();
        } catch (Exception e) {
            this.logger.info("AndroidCll-AbstractHandler", "Exception while deleting the file: " + e.toString());
        }
        return z;
    }

    private void setFileStorageUsed() {
        int i = 0;
        totalStorageUsed.set(0);
        for (File length : findExistingFiles(criticalEventFileExtension)) {
            totalStorageUsed.getAndAdd(length.length());
        }
        File[] findExistingFiles = findExistingFiles(normalEventFileExtension);
        int length2 = findExistingFiles.length;
        while (i < length2) {
            totalStorageUsed.getAndAdd(findExistingFiles[i].length());
            i++;
        }
    }

    public abstract void add(String str, List<String> list) throws IOException, FileFullException;

    public boolean canAdd(Tuple tuple) {
        return ((long) ((String) tuple.a).length()) + totalStorageUsed.get() <= ((long) SettingsStore.getCllSettingsAsInt(Settings.MAXFILESSPACE));
    }

    public abstract void close();

    public abstract void dispose(IStorage iStorage);

    protected boolean dropOldestFile(boolean z) {
        File[] findExistingFiles = findExistingFiles(normalEventFileExtension);
        if (findExistingFiles.length <= 1 && z) {
            findExistingFiles = findExistingFiles(criticalEventFileExtension);
        }
        if (findExistingFiles.length <= 1) {
            this.logger.info("AndroidCll-AbstractHandler", "There are no files to delete");
            return false;
        }
        long lastModified = findExistingFiles[0].lastModified();
        File file = findExistingFiles[0];
        int length = findExistingFiles.length;
        int i = 0;
        while (i < length) {
            long lastModified2;
            File file2 = findExistingFiles[i];
            if (file2.lastModified() < lastModified) {
                lastModified2 = file2.lastModified();
            } else {
                file2 = file;
                lastModified2 = lastModified;
            }
            i++;
            lastModified = lastModified2;
            file = file2;
        }
        lastModified = file.length();
        boolean deleteFile = deleteFile(file);
        if (!deleteFile) {
            return deleteFile;
        }
        totalStorageUsed.getAndAdd(-lastModified);
        return deleteFile;
    }

    protected boolean ensureCanAdd(Tuple<String, List<String>> tuple, Persistence persistence) {
        int cllSettingsAsInt = SettingsStore.getCllSettingsAsInt(Settings.MAXCRITICALCANADDATTEMPTS);
        boolean z = persistence == Persistence.PersistenceCritical;
        int i = 0;
        boolean z2 = true;
        boolean canAdd = canAdd(tuple);
        while (!canAdd && i < cllSettingsAsInt && r2) {
            this.logger.warn("AndroidCll-AbstractHandler", "Out of storage space. Attempting to drop one oldest file.");
            z2 = dropOldestFile(z);
            canAdd = canAdd(tuple);
            i++;
        }
        return canAdd;
    }

    protected File[] findExistingFiles(final String str) {
        File[] listFiles = new File(this.filePath).listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.toLowerCase().endsWith(str);
            }
        });
        return listFiles == null ? new File[0] : listFiles;
    }

    protected List<IStorage> getFilesByExtensionForDraining(String str) {
        List<IStorage> arrayList = new ArrayList();
        for (File file : findExistingFiles(str)) {
            try {
                IStorage fileStorage = new FileStorage(this.logger, file.getAbsolutePath(), this);
                arrayList.add(fileStorage);
                fileStorage.close();
            } catch (Exception e) {
                this.logger.info("AndroidCll-AbstractHandler", "File " + file.getName() + " is in use still");
            }
        }
        return arrayList;
    }

    public abstract List<IStorage> getFilesForDraining();
}
