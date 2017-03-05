package net.hockeyapp.android.metrics;

import android.content.Context;
import android.text.TextUtils;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import net.hockeyapp.android.utils.HockeyLog;

class Persistence {
    private static final String BIT_TELEMETRY_DIRECTORY = "/net.hockeyapp.android/telemetry/";
    private static final Object LOCK = new Object();
    private static final Integer MAX_FILE_COUNT = Integer.valueOf(50);
    private static final String TAG = "HA-MetricsPersistence";
    protected ArrayList<File> mServedFiles;
    protected final File mTelemetryDirectory;
    private final WeakReference<Context> mWeakContext;
    protected WeakReference<Sender> mWeakSender;

    protected Persistence(Context context, File file, Sender sender) {
        this.mWeakContext = new WeakReference(context);
        this.mServedFiles = new ArrayList(51);
        this.mTelemetryDirectory = file;
        this.mWeakSender = new WeakReference(sender);
        createDirectoriesIfNecessary();
    }

    protected Persistence(Context context, Sender sender) {
        this(context, new File(context.getFilesDir().getAbsolutePath() + BIT_TELEMETRY_DIRECTORY), null);
        setSender(sender);
    }

    private Context getContext() {
        return this.mWeakContext != null ? (Context) this.mWeakContext.get() : null;
    }

    protected void createDirectoriesIfNecessary() {
        if (this.mTelemetryDirectory != null && !this.mTelemetryDirectory.exists()) {
            if (this.mTelemetryDirectory.mkdirs()) {
                HockeyLog.info(TAG, "Successfully created directory");
            } else {
                HockeyLog.info(TAG, "Error creating directory");
            }
        }
    }

    protected void deleteFile(File file) {
        if (file != null) {
            synchronized (LOCK) {
                if (file.delete()) {
                    HockeyLog.warn(TAG, "Successfully deleted telemetry file at: " + file.toString());
                    this.mServedFiles.remove(file);
                } else {
                    HockeyLog.warn(TAG, "Error deleting telemetry file " + file.toString());
                }
            }
            return;
        }
        HockeyLog.warn(TAG, "Couldn't delete file, the reference to the file was null");
    }

    protected Sender getSender() {
        return this.mWeakSender != null ? (Sender) this.mWeakSender.get() : null;
    }

    protected Boolean isFreeSpaceAvailable() {
        Boolean valueOf;
        boolean z = false;
        synchronized (LOCK) {
            Context context = getContext();
            if (context.getFilesDir() != null) {
                Object obj = context.getFilesDir().getAbsolutePath() + BIT_TELEMETRY_DIRECTORY;
                if (!TextUtils.isEmpty(obj)) {
                    if (new File(obj).listFiles().length < MAX_FILE_COUNT.intValue()) {
                        z = true;
                    }
                    valueOf = Boolean.valueOf(z);
                }
            }
            valueOf = Boolean.valueOf(false);
        }
        return valueOf;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected java.lang.String load(java.io.File r7) {
        /*
        r6 = this;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        if (r7 == 0) goto L_0x004c;
    L_0x0007:
        r1 = 0;
        r4 = LOCK;	 Catch:{ Exception -> 0x002a }
        monitor-enter(r4);	 Catch:{ Exception -> 0x002a }
        r2 = new java.io.BufferedReader;	 Catch:{ all -> 0x00b9 }
        r0 = new java.io.InputStreamReader;	 Catch:{ all -> 0x00b9 }
        r5 = new java.io.FileInputStream;	 Catch:{ all -> 0x00b9 }
        r5.<init>(r7);	 Catch:{ all -> 0x00b9 }
        r0.<init>(r5);	 Catch:{ all -> 0x00b9 }
        r2.<init>(r0);	 Catch:{ all -> 0x00b9 }
    L_0x001a:
        r0 = r2.read();	 Catch:{ all -> 0x0026 }
        r1 = -1;
        if (r0 == r1) goto L_0x0051;
    L_0x0021:
        r0 = (char) r0;	 Catch:{ all -> 0x0026 }
        r3.append(r0);	 Catch:{ all -> 0x0026 }
        goto L_0x001a;
    L_0x0026:
        r0 = move-exception;
        r1 = r2;
    L_0x0028:
        monitor-exit(r4);	 Catch:{ all -> 0x00b9 }
        throw r0;	 Catch:{ Exception -> 0x002a }
    L_0x002a:
        r0 = move-exception;
        r2 = "HA-MetricsPersistence";
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0094 }
        r4.<init>();	 Catch:{ all -> 0x0094 }
        r5 = "Error reading telemetry data from file with exception message ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0094 }
        r0 = r0.getMessage();	 Catch:{ all -> 0x0094 }
        r0 = r4.append(r0);	 Catch:{ all -> 0x0094 }
        r0 = r0.toString();	 Catch:{ all -> 0x0094 }
        net.hockeyapp.android.utils.HockeyLog.warn(r2, r0);	 Catch:{ all -> 0x0094 }
        if (r1 == 0) goto L_0x004c;
    L_0x0049:
        r1.close();	 Catch:{ IOException -> 0x0076 }
    L_0x004c:
        r0 = r3.toString();
        return r0;
    L_0x0051:
        monitor-exit(r4);	 Catch:{ all -> 0x0026 }
        if (r2 == 0) goto L_0x004c;
    L_0x0054:
        r2.close();	 Catch:{ IOException -> 0x0058 }
        goto L_0x004c;
    L_0x0058:
        r0 = move-exception;
        r1 = "HA-MetricsPersistence";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "Error closing stream.";
        r2 = r2.append(r4);
        r0 = r0.getMessage();
        r0 = r2.append(r0);
        r0 = r0.toString();
        net.hockeyapp.android.utils.HockeyLog.warn(r1, r0);
        goto L_0x004c;
    L_0x0076:
        r0 = move-exception;
        r1 = "HA-MetricsPersistence";
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "Error closing stream.";
        r2 = r2.append(r4);
        r0 = r0.getMessage();
        r0 = r2.append(r0);
        r0 = r0.toString();
        net.hockeyapp.android.utils.HockeyLog.warn(r1, r0);
        goto L_0x004c;
    L_0x0094:
        r0 = move-exception;
        if (r1 == 0) goto L_0x009a;
    L_0x0097:
        r1.close();	 Catch:{ IOException -> 0x009b }
    L_0x009a:
        throw r0;
    L_0x009b:
        r1 = move-exception;
        r2 = "HA-MetricsPersistence";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Error closing stream.";
        r3 = r3.append(r4);
        r1 = r1.getMessage();
        r1 = r3.append(r1);
        r1 = r1.toString();
        net.hockeyapp.android.utils.HockeyLog.warn(r2, r1);
        goto L_0x009a;
    L_0x00b9:
        r0 = move-exception;
        goto L_0x0028;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.metrics.Persistence.load(java.io.File):java.lang.String");
    }

    protected void makeAvailable(File file) {
        synchronized (LOCK) {
            if (file != null) {
                this.mServedFiles.remove(file);
            }
        }
    }

    protected File nextAvailableFileInDirectory() {
        File file;
        synchronized (LOCK) {
            if (this.mTelemetryDirectory != null) {
                File[] listFiles = this.mTelemetryDirectory.listFiles();
                if (listFiles != null && listFiles.length > 0) {
                    for (int i = 0; i <= listFiles.length - 1; i++) {
                        file = listFiles[i];
                        if (!this.mServedFiles.contains(file)) {
                            HockeyLog.info(TAG, "The directory " + file.toString() + " (ADDING TO SERVED AND RETURN)");
                            this.mServedFiles.add(file);
                            break;
                        }
                        HockeyLog.info(TAG, "The directory " + file.toString() + " (WAS ALREADY SERVED)");
                    }
                }
            }
            if (this.mTelemetryDirectory != null) {
                HockeyLog.info(TAG, "The directory " + this.mTelemetryDirectory.toString() + " did not contain any " + "unserved files");
            }
            file = null;
        }
        return file;
    }

    protected void persist(String[] strArr) {
        if (isFreeSpaceAvailable().booleanValue()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String str : strArr) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append('\n');
                }
                stringBuilder.append(str);
            }
            if (Boolean.valueOf(writeToDisk(stringBuilder.toString())).booleanValue()) {
                getSender().triggerSending();
                return;
            }
            return;
        }
        HockeyLog.warn(TAG, "Failed to persist file: Too many files on disk.");
        getSender().triggerSending();
    }

    protected void setSender(Sender sender) {
        this.mWeakSender = new WeakReference(sender);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected boolean writeToDisk(java.lang.String r8) {
        /*
        r7 = this;
        r0 = java.util.UUID.randomUUID();
        r1 = r0.toString();
        r0 = 0;
        r0 = java.lang.Boolean.valueOf(r0);
        r2 = 0;
        r4 = LOCK;	 Catch:{ Exception -> 0x006c }
        monitor-enter(r4);	 Catch:{ Exception -> 0x006c }
        r5 = new java.io.File;	 Catch:{ all -> 0x00ab }
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x00ab }
        r3.<init>();	 Catch:{ all -> 0x00ab }
        r6 = r7.mTelemetryDirectory;	 Catch:{ all -> 0x00ab }
        r3 = r3.append(r6);	 Catch:{ all -> 0x00ab }
        r6 = "/";
        r3 = r3.append(r6);	 Catch:{ all -> 0x00ab }
        r1 = r3.append(r1);	 Catch:{ all -> 0x00ab }
        r1 = r1.toString();	 Catch:{ all -> 0x00ab }
        r5.<init>(r1);	 Catch:{ all -> 0x00ab }
        r3 = new java.io.FileOutputStream;	 Catch:{ all -> 0x00ab }
        r1 = 1;
        r3.<init>(r5, r1);	 Catch:{ all -> 0x00ab }
        r1 = r8.getBytes();	 Catch:{ all -> 0x0068 }
        r3.write(r1);	 Catch:{ all -> 0x0068 }
        r1 = "HA-MetricsPersistence";
        r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0068 }
        r2.<init>();	 Catch:{ all -> 0x0068 }
        r6 = "Saving data to: ";
        r2 = r2.append(r6);	 Catch:{ all -> 0x0068 }
        r5 = r5.toString();	 Catch:{ all -> 0x0068 }
        r2 = r2.append(r5);	 Catch:{ all -> 0x0068 }
        r2 = r2.toString();	 Catch:{ all -> 0x0068 }
        net.hockeyapp.android.utils.HockeyLog.warn(r1, r2);	 Catch:{ all -> 0x0068 }
        monitor-exit(r4);	 Catch:{ all -> 0x0068 }
        r1 = 1;
        r0 = java.lang.Boolean.valueOf(r1);	 Catch:{ Exception -> 0x00a6, all -> 0x00a9 }
        if (r3 == 0) goto L_0x0063;
    L_0x0060:
        r3.close();	 Catch:{ IOException -> 0x0094 }
    L_0x0063:
        r0 = r0.booleanValue();
        return r0;
    L_0x0068:
        r1 = move-exception;
        r2 = r3;
    L_0x006a:
        monitor-exit(r4);	 Catch:{ all -> 0x00ab }
        throw r1;	 Catch:{ Exception -> 0x006c }
    L_0x006c:
        r1 = move-exception;
    L_0x006d:
        r3 = "HA-MetricsPersistence";
        r4 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0099 }
        r4.<init>();	 Catch:{ all -> 0x0099 }
        r5 = "Failed to save data with exception: ";
        r4 = r4.append(r5);	 Catch:{ all -> 0x0099 }
        r1 = r1.toString();	 Catch:{ all -> 0x0099 }
        r1 = r4.append(r1);	 Catch:{ all -> 0x0099 }
        r1 = r1.toString();	 Catch:{ all -> 0x0099 }
        net.hockeyapp.android.utils.HockeyLog.warn(r3, r1);	 Catch:{ all -> 0x0099 }
        if (r2 == 0) goto L_0x0063;
    L_0x008b:
        r2.close();	 Catch:{ IOException -> 0x008f }
        goto L_0x0063;
    L_0x008f:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x0063;
    L_0x0094:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x0063;
    L_0x0099:
        r0 = move-exception;
        r3 = r2;
    L_0x009b:
        if (r3 == 0) goto L_0x00a0;
    L_0x009d:
        r3.close();	 Catch:{ IOException -> 0x00a1 }
    L_0x00a0:
        throw r0;
    L_0x00a1:
        r1 = move-exception;
        r1.printStackTrace();
        goto L_0x00a0;
    L_0x00a6:
        r1 = move-exception;
        r2 = r3;
        goto L_0x006d;
    L_0x00a9:
        r0 = move-exception;
        goto L_0x009b;
    L_0x00ab:
        r1 = move-exception;
        goto L_0x006a;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.metrics.Persistence.writeToDisk(java.lang.String):boolean");
    }
}
