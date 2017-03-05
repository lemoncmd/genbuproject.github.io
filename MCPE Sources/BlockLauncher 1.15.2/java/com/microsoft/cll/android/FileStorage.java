package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore.Settings;
import com.microsoft.telemetry.IJsonSerializable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class FileStorage implements IStorage {
    protected static final SynchronizedArrayList<String> fileLockList = new SynchronizedArrayList();
    private final String TAG;
    private int eventsWritten;
    private String filePathAndName;
    private long fileSize;
    private FileReader inputFile;
    private boolean isOpen;
    private boolean isWritable;
    private final ILogger logger;
    private FileWriter outputFile;
    private AbstractHandler parent;
    private BufferedReader reader;
    private final EventSerializer serializer;

    class FileFullException extends Exception {
        public FileFullException(String str) {
            super(str);
        }
    }

    public FileStorage(ILogger iLogger, String str, AbstractHandler abstractHandler) throws Exception {
        this.TAG = "AndroidCll-FileStorage";
        this.logger = iLogger;
        this.serializer = new EventSerializer(iLogger);
        this.filePathAndName = str;
        this.parent = abstractHandler;
        if (fileLockList.contains(str)) {
            throw new Exception("Could not get lock for file");
        }
    }

    public FileStorage(String str, ILogger iLogger, String str2, AbstractHandler abstractHandler) {
        this.TAG = "AndroidCll-FileStorage";
        this.eventsWritten = 0;
        this.fileSize = 0;
        this.filePathAndName = str2 + File.separator + UUID.randomUUID() + str;
        this.logger = iLogger;
        this.serializer = new EventSerializer(iLogger);
        this.parent = abstractHandler;
        int i = 1;
        while (!openFile()) {
            this.filePathAndName = str2 + "/" + UUID.randomUUID() + str;
            i++;
            if (i >= 5) {
                iLogger.error("AndroidCll-FileStorage", "Could not create a file");
                return;
            }
        }
    }

    private boolean getLock() {
        return fileLockList.add(this.filePathAndName);
    }

    private boolean openFile() {
        if (getLock()) {
            File file = new File(this.filePathAndName);
            if (file.exists()) {
                this.isWritable = false;
                try {
                    this.inputFile = new FileReader(this.filePathAndName);
                    this.reader = new BufferedReader(this.inputFile);
                    this.fileSize = file.length();
                } catch (IOException e) {
                    this.logger.error("AndroidCll-FileStorage", "Event file was not found");
                    return false;
                }
            }
            this.isWritable = true;
            this.logger.info("AndroidCll-FileStorage", "Creating new file");
            try {
                this.outputFile = new FileWriter(this.filePathAndName);
            } catch (IOException e2) {
                this.logger.error("AndroidCll-FileStorage", "Error opening file");
                return false;
            }
            this.isOpen = true;
            return true;
        }
        this.logger.info("AndroidCll-FileStorage", "Could not get lock for file");
        return false;
    }

    public void add(Tuple<String, List<String>> tuple) throws FileFullException, IOException {
        if (!this.isOpen || !this.isWritable) {
            this.logger.warn("AndroidCll-FileStorage", "This file is not open or not writable");
        } else if (canAdd((Tuple) tuple)) {
            if (tuple.b != null) {
                for (String str : (List) tuple.b) {
                    this.outputFile.write("x:" + str + "\r\n");
                }
            }
            this.outputFile.write((String) tuple.a);
            this.eventsWritten++;
            this.fileSize = ((long) ((String) tuple.a).length()) + this.fileSize;
        } else {
            throw new FileFullException("The file is already full!");
        }
    }

    public void add(IJsonSerializable iJsonSerializable) throws FileFullException, IOException {
        add(new Tuple(this.serializer.serialize(iJsonSerializable), null));
    }

    public boolean canAdd(Tuple<String, List<String>> tuple) {
        if (this.isOpen && this.isWritable) {
            return this.eventsWritten < SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSPERPOST) && ((long) ((String) tuple.a).length()) + this.fileSize < ((long) SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSIZEINBYTES));
        } else {
            this.logger.warn("AndroidCll-FileStorage", "This file is not open or not writable");
            return false;
        }
    }

    public boolean canAdd(IJsonSerializable iJsonSerializable) {
        return canAdd(new Tuple(this.serializer.serialize(iJsonSerializable), null));
    }

    public void close() {
        if (this.isOpen) {
            flush();
            fileLockList.remove(this.filePathAndName);
            try {
                if (this.isWritable) {
                    this.outputFile.close();
                } else {
                    this.inputFile.close();
                    this.reader.close();
                }
                this.isOpen = false;
            } catch (Exception e) {
                this.logger.error("AndroidCll-FileStorage", "Error when closing file");
            }
        }
    }

    public void discard() {
        this.logger.info("AndroidCll-FileStorage", "Discarding file");
        close();
        this.parent.dispose(this);
        new File(this.filePathAndName).delete();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.microsoft.cll.android.Tuple<java.lang.String, java.util.List<java.lang.String>>> drain() {
        /*
        r5 = this;
        r2 = new java.util.ArrayList;
        r2.<init>();
        r0 = r5.isOpen;
        if (r0 != 0) goto L_0x001b;
    L_0x0009:
        r0 = r5.openFile();	 Catch:{ Exception -> 0x0010 }
        if (r0 != 0) goto L_0x001b;
    L_0x000f:
        return r2;
    L_0x0010:
        r0 = move-exception;
        r0 = r5.logger;
        r1 = "AndroidCll-FileStorage";
        r3 = "Error opening file";
        r0.error(r1, r3);
        goto L_0x000f;
    L_0x001b:
        r0 = r5.reader;	 Catch:{ Exception -> 0x0053 }
        r1 = r0.readLine();	 Catch:{ Exception -> 0x0053 }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0053 }
        r0.<init>();	 Catch:{ Exception -> 0x0053 }
    L_0x0026:
        if (r1 == 0) goto L_0x005d;
    L_0x0028:
        r3 = "x:";
        r3 = r1.startsWith(r3);	 Catch:{ Exception -> 0x0053 }
        if (r3 == 0) goto L_0x003f;
    L_0x0030:
        r3 = 2;
        r1 = r1.substring(r3);	 Catch:{ Exception -> 0x0053 }
        r0.add(r1);	 Catch:{ Exception -> 0x0053 }
    L_0x0038:
        r1 = r5.reader;	 Catch:{ Exception -> 0x0053 }
        r1 = r1.readLine();	 Catch:{ Exception -> 0x0053 }
        goto L_0x0026;
    L_0x003f:
        r3 = r0.size();	 Catch:{ Exception -> 0x0053 }
        if (r3 <= 0) goto L_0x0082;
    L_0x0045:
        r3 = new com.microsoft.cll.android.Tuple;	 Catch:{ Exception -> 0x0053 }
        r3.<init>(r1, r0);	 Catch:{ Exception -> 0x0053 }
        r2.add(r3);	 Catch:{ Exception -> 0x0053 }
        r0 = new java.util.ArrayList;	 Catch:{ Exception -> 0x0053 }
        r0.<init>();	 Catch:{ Exception -> 0x0053 }
        goto L_0x0038;
    L_0x0053:
        r0 = move-exception;
        r0 = r5.logger;
        r1 = "AndroidCll-FileStorage";
        r3 = "Error reading from input file";
        r0.error(r1, r3);
    L_0x005d:
        r0 = r5.logger;
        r1 = "AndroidCll-FileStorage";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Read ";
        r3 = r3.append(r4);
        r4 = r2.size();
        r3 = r3.append(r4);
        r4 = " events from file";
        r3 = r3.append(r4);
        r3 = r3.toString();
        r0.info(r1, r3);
        goto L_0x000f;
    L_0x0082:
        r3 = new com.microsoft.cll.android.Tuple;	 Catch:{ Exception -> 0x0053 }
        r4 = 0;
        r3.<init>(r1, r4);	 Catch:{ Exception -> 0x0053 }
        r2.add(r3);	 Catch:{ Exception -> 0x0053 }
        goto L_0x0038;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.cll.android.FileStorage.drain():java.util.List<com.microsoft.cll.android.Tuple<java.lang.String, java.util.List<java.lang.String>>>");
    }

    public void flush() {
        if (this.isOpen && this.isWritable) {
            try {
                this.outputFile.flush();
            } catch (Exception e) {
                this.logger.error("AndroidCll-FileStorage", "Could not flush file");
            }
        }
    }

    public long size() {
        return !this.isOpen ? new File(this.filePathAndName).length() : this.fileSize;
    }
}
