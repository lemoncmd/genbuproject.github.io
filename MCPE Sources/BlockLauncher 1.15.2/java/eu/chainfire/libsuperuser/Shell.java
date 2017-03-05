package eu.chainfire.libsuperuser;

import android.os.Handler;
import android.os.Looper;
import eu.chainfire.libsuperuser.StreamGobbler.OnLineListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class Shell {

    public static class Builder {
        private boolean autoHandler = true;
        private List<Command> commands = new LinkedList();
        private Map<String, String> environment = new HashMap();
        private Handler handler = null;
        private OnLineListener onSTDERRLineListener = null;
        private OnLineListener onSTDOUTLineListener = null;
        private String shell = "sh";
        private boolean wantSTDERR = false;

        public Builder setHandler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public Builder setAutoHandler(boolean autoHandler) {
            this.autoHandler = autoHandler;
            return this;
        }

        public Builder setShell(String shell) {
            this.shell = shell;
            return this;
        }

        public Builder useSH() {
            return setShell("sh");
        }

        public Builder useSU() {
            return setShell("su");
        }

        public Builder setWantSTDERR(boolean wantSTDERR) {
            this.wantSTDERR = wantSTDERR;
            return this;
        }

        public Builder addEnvironment(String key, String value) {
            this.environment.put(key, value);
            return this;
        }

        public Builder addEnvironment(Map<String, String> addEnvironment) {
            this.environment.putAll(addEnvironment);
            return this;
        }

        public Builder addCommand(String command) {
            return addCommand(command, 0, null);
        }

        public Builder addCommand(String command, int code, OnCommandResultListener onCommandResultListener) {
            return addCommand(new String[]{command}, code, onCommandResultListener);
        }

        public Builder addCommand(List<String> commands) {
            return addCommand((List) commands, 0, null);
        }

        public Builder addCommand(List<String> commands, int code, OnCommandResultListener onCommandResultListener) {
            return addCommand((String[]) commands.toArray(new String[commands.size()]), code, onCommandResultListener);
        }

        public Builder addCommand(String[] commands) {
            return addCommand(commands, 0, null);
        }

        public Builder addCommand(String[] commands, int code, OnCommandResultListener onCommandResultListener) {
            this.commands.add(new Command(commands, code, onCommandResultListener));
            return this;
        }

        public Builder setOnSTDOUTLineListener(OnLineListener onLineListener) {
            this.onSTDOUTLineListener = onLineListener;
            return this;
        }

        public Builder setOnSTDERRLineListener(OnLineListener onLineListener) {
            this.onSTDERRLineListener = onLineListener;
            return this;
        }

        public Interactive open() {
            return new Interactive();
        }
    }

    private static class Command {
        private static int commandCounter = 0;
        private final int code;
        private final String[] commands;
        private final String marker;
        private final OnCommandResultListener onCommandResultListener;

        public Command(String[] commands, int code, OnCommandResultListener onCommandResultListener) {
            this.commands = commands;
            this.code = code;
            this.onCommandResultListener = onCommandResultListener;
            StringBuilder append = new StringBuilder().append(UUID.randomUUID().toString());
            Object[] objArr = new Object[1];
            int i = commandCounter + 1;
            commandCounter = i;
            objArr[0] = Integer.valueOf(i);
            this.marker = append.append(String.format("-%08x", objArr)).toString();
        }
    }

    public static class Interactive {
        private StreamGobbler STDERR;
        private DataOutputStream STDIN;
        private StreamGobbler STDOUT;
        private final boolean autoHandler;
        private volatile List<String> buffer;
        private Object callbackSync;
        private volatile int callbacks;
        private volatile boolean closed;
        private volatile Command command;
        private final List<Command> commands;
        private final Map<String, String> environment;
        private final Handler handler;
        private volatile boolean idle;
        private Object idleSync;
        private volatile int lastExitCode;
        private volatile String lastMarkerSTDERR;
        private volatile String lastMarkerSTDOUT;
        private final OnLineListener onSTDERRLineListener;
        private final OnLineListener onSTDOUTLineListener;
        private Process process;
        private volatile boolean running;
        private final String shell;
        private final boolean wantSTDERR;

        private Interactive(Builder builder) {
            this.process = null;
            this.STDIN = null;
            this.STDOUT = null;
            this.STDERR = null;
            this.running = false;
            this.idle = true;
            this.closed = true;
            this.callbacks = 0;
            this.idleSync = new Object();
            this.callbackSync = new Object();
            this.lastExitCode = 0;
            this.lastMarkerSTDOUT = null;
            this.lastMarkerSTDERR = null;
            this.command = null;
            this.buffer = null;
            this.autoHandler = builder.autoHandler;
            this.shell = builder.shell;
            this.wantSTDERR = builder.wantSTDERR;
            this.commands = builder.commands;
            this.environment = builder.environment;
            this.onSTDOUTLineListener = builder.onSTDOUTLineListener;
            this.onSTDERRLineListener = builder.onSTDERRLineListener;
            if (Looper.myLooper() != null && builder.handler == null && this.autoHandler) {
                this.handler = new Handler();
            } else {
                this.handler = builder.handler;
            }
            open();
        }

        protected void finalize() throws Throwable {
            if (this.closed) {
                super.finalize();
            } else {
                super.finalize();
            }
        }

        public void addCommand(String command) {
            addCommand(command, 0, null);
        }

        public void addCommand(String command, int code, OnCommandResultListener onCommandResultListener) {
            addCommand(new String[]{command}, code, onCommandResultListener);
        }

        public void addCommand(List<String> commands) {
            addCommand((List) commands, 0, null);
        }

        public void addCommand(List<String> commands, int code, OnCommandResultListener onCommandResultListener) {
            addCommand((String[]) commands.toArray(new String[commands.size()]), code, onCommandResultListener);
        }

        public void addCommand(String[] commands) {
            addCommand(commands, 0, null);
        }

        public synchronized void addCommand(String[] commands, int code, OnCommandResultListener onCommandResultListener) {
            if (this.running) {
                this.commands.add(new Command(commands, code, onCommandResultListener));
                runNextCommand();
            }
        }

        private void runNextCommand() {
            runNextCommand(true);
        }

        private void runNextCommand(boolean notifyIdle) {
            boolean running = isRunning();
            if (!running) {
                this.idle = true;
            }
            if (running && this.idle && this.commands.size() > 0) {
                Command command = (Command) this.commands.get(0);
                this.commands.remove(0);
                this.buffer = null;
                this.lastExitCode = 0;
                this.lastMarkerSTDOUT = null;
                this.lastMarkerSTDERR = null;
                if (command.commands.length > 0) {
                    try {
                        if (command.onCommandResultListener != null) {
                            this.buffer = Collections.synchronizedList(new ArrayList());
                        }
                        this.idle = false;
                        this.command = command;
                        for (String write : command.commands) {
                            this.STDIN.writeBytes(write + "\n");
                        }
                        this.STDIN.writeBytes("echo " + command.marker + " $?\n");
                        this.STDIN.writeBytes("echo " + command.marker + " >&2\n");
                        this.STDIN.flush();
                    } catch (IOException e) {
                    }
                } else {
                    runNextCommand(false);
                }
            }
            if (this.idle && notifyIdle) {
                synchronized (this.idleSync) {
                    this.idleSync.notifyAll();
                }
            }
        }

        private synchronized void processMarker() {
            if (this.command.marker.equals(this.lastMarkerSTDOUT) && this.command.marker.equals(this.lastMarkerSTDERR)) {
                if (!(this.command.onCommandResultListener == null || this.buffer == null)) {
                    if (this.handler != null) {
                        final List<String> fBuffer = this.buffer;
                        final int fExitCode = this.lastExitCode;
                        final Command fCommand = this.command;
                        startCallback();
                        this.handler.post(new Runnable() {
                            public void run() {
                                try {
                                    fCommand.onCommandResultListener.onCommandResult(fCommand.code, fExitCode, fBuffer);
                                } finally {
                                    Interactive.this.endCallback();
                                }
                            }
                        });
                    } else {
                        this.command.onCommandResultListener.onCommandResult(this.command.code, this.lastExitCode, this.buffer);
                    }
                }
                this.command = null;
                this.buffer = null;
                this.idle = true;
                runNextCommand();
            }
        }

        private synchronized void processLine(String line, OnLineListener listener) {
            if (listener != null) {
                if (this.handler != null) {
                    final String fLine = line;
                    final OnLineListener fListener = listener;
                    startCallback();
                    this.handler.post(new Runnable() {
                        public void run() {
                            try {
                                fListener.onLine(fLine);
                            } finally {
                                Interactive.this.endCallback();
                            }
                        }
                    });
                } else {
                    listener.onLine(line);
                }
            }
        }

        private synchronized void addBuffer(String line) {
            if (this.buffer != null) {
                this.buffer.add(line);
            }
        }

        private void startCallback() {
            synchronized (this.callbackSync) {
                this.callbacks++;
            }
        }

        private void endCallback() {
            synchronized (this.callbackSync) {
                this.callbacks--;
                if (this.callbacks == 0) {
                    this.callbackSync.notifyAll();
                }
            }
        }

        private synchronized boolean open() {
            boolean z;
            try {
                if (this.environment.size() == 0) {
                    this.process = Runtime.getRuntime().exec(this.shell);
                } else {
                    Map<String, String> newEnvironment = new HashMap();
                    newEnvironment.putAll(System.getenv());
                    newEnvironment.putAll(this.environment);
                    int i = 0;
                    String[] env = new String[newEnvironment.size()];
                    for (Entry<String, String> entry : newEnvironment.entrySet()) {
                        env[i] = ((String) entry.getKey()) + "=" + ((String) entry.getValue());
                        i++;
                    }
                    this.process = Runtime.getRuntime().exec(this.shell, env);
                }
                this.STDIN = new DataOutputStream(this.process.getOutputStream());
                this.STDOUT = new StreamGobbler(this.shell.toUpperCase() + "-", this.process.getInputStream(), new OnLineListener() {
                    public void onLine(String line) {
                        if (line.startsWith(Interactive.this.command.marker)) {
                            try {
                                Interactive.this.lastExitCode = Integer.valueOf(line.substring(Interactive.this.command.marker.length() + 1), 10).intValue();
                            } catch (Exception e) {
                            }
                            Interactive.this.lastMarkerSTDOUT = Interactive.this.command.marker;
                            Interactive.this.processMarker();
                            return;
                        }
                        Interactive.this.addBuffer(line);
                        Interactive.this.processLine(line, Interactive.this.onSTDOUTLineListener);
                    }
                });
                this.STDERR = new StreamGobbler(this.shell.toUpperCase() + "*", this.process.getErrorStream(), new OnLineListener() {
                    public void onLine(String line) {
                        if (line.startsWith(Interactive.this.command.marker)) {
                            Interactive.this.lastMarkerSTDERR = Interactive.this.command.marker;
                            Interactive.this.processMarker();
                            return;
                        }
                        if (Interactive.this.wantSTDERR) {
                            Interactive.this.addBuffer(line);
                        }
                        Interactive.this.processLine(line, Interactive.this.onSTDERRLineListener);
                    }
                });
                this.STDOUT.start();
                this.STDERR.start();
                this.running = true;
                this.closed = false;
                runNextCommand();
                z = true;
            } catch (IOException e) {
                z = false;
            }
            return z;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void close() {
            /*
            r3 = this;
            r0 = r3.isIdle();
            monitor-enter(r3);
            r1 = r3.running;	 Catch:{ all -> 0x0041 }
            if (r1 != 0) goto L_0x000b;
        L_0x0009:
            monitor-exit(r3);	 Catch:{ all -> 0x0041 }
        L_0x000a:
            return;
        L_0x000b:
            r1 = 0;
            r3.running = r1;	 Catch:{ all -> 0x0041 }
            r1 = 1;
            r3.closed = r1;	 Catch:{ all -> 0x0041 }
            monitor-exit(r3);	 Catch:{ all -> 0x0041 }
            if (r0 != 0) goto L_0x0014;
        L_0x0014:
            if (r0 != 0) goto L_0x0019;
        L_0x0016:
            r3.waitForIdle();
        L_0x0019:
            r1 = r3.STDIN;	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r2 = "exit\n";
            r1.writeBytes(r2);	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1 = r3.STDIN;	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1.flush();	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1 = r3.process;	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1.waitFor();	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1 = r3.STDIN;	 Catch:{ IOException -> 0x0046, InterruptedException -> 0x0044 }
            r1.close();	 Catch:{ IOException -> 0x0046, InterruptedException -> 0x0044 }
        L_0x002f:
            r1 = r3.STDOUT;	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1.join();	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1 = r3.STDERR;	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1.join();	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1 = r3.process;	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            r1.destroy();	 Catch:{ IOException -> 0x003f, InterruptedException -> 0x0044 }
            goto L_0x000a;
        L_0x003f:
            r1 = move-exception;
            goto L_0x000a;
        L_0x0041:
            r1 = move-exception;
            monitor-exit(r3);	 Catch:{ all -> 0x0041 }
            throw r1;
        L_0x0044:
            r1 = move-exception;
            goto L_0x000a;
        L_0x0046:
            r1 = move-exception;
            goto L_0x002f;
            */
            throw new UnsupportedOperationException("Method not decompiled: eu.chainfire.libsuperuser.Shell.Interactive.close():void");
        }

        public boolean isRunning() {
            try {
                this.process.exitValue();
                return false;
            } catch (IllegalThreadStateException e) {
                return true;
            }
        }

        public synchronized boolean isIdle() {
            if (!isRunning()) {
                this.idle = true;
                synchronized (this.idleSync) {
                    this.idleSync.notifyAll();
                }
            }
            return this.idle;
        }

        public boolean waitForIdle() {
            if (isRunning()) {
                synchronized (this.idleSync) {
                    while (!this.idle) {
                        try {
                            this.idleSync.wait();
                        } catch (InterruptedException e) {
                            return false;
                        }
                    }
                }
                if (!(this.handler == null || this.handler.getLooper() == null || this.handler.getLooper() == Looper.myLooper())) {
                    synchronized (this.callbackSync) {
                        while (this.callbacks > 0) {
                            try {
                                this.callbackSync.wait();
                            } catch (InterruptedException e2) {
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }

        public boolean hasHandler() {
            return this.handler != null;
        }
    }

    public interface OnCommandResultListener {
        void onCommandResult(int i, int i2, List<String> list);
    }

    public static class SH {
        public static List<String> run(String command) {
            return Shell.run("sh", new String[]{command}, null, false);
        }

        public static List<String> run(List<String> commands) {
            return Shell.run("sh", (String[]) commands.toArray(new String[commands.size()]), null, false);
        }

        public static List<String> run(String[] commands) {
            return Shell.run("sh", commands, null, false);
        }
    }

    public static class SU {
        public static List<String> run(String command) {
            return Shell.run("su", new String[]{command}, null, false);
        }

        public static List<String> run(List<String> commands) {
            return Shell.run("su", (String[]) commands.toArray(new String[commands.size()]), null, false);
        }

        public static List<String> run(String[] commands) {
            return Shell.run("su", commands, null, false);
        }

        public static boolean available() {
            List<String> ret = run(new String[]{Name.MARK, "echo -EOC-"});
            if (ret == null) {
                return false;
            }
            for (String line : ret) {
                if (line.contains("uid=")) {
                    return line.contains("uid=0");
                }
                if (line.contains("-EOC-")) {
                    return true;
                }
            }
            return false;
        }

        public static String version(boolean internal) {
            String str = "sh";
            String[] strArr = new String[2];
            strArr[0] = internal ? "su -V" : "su -v";
            strArr[1] = "exit";
            List<String> ret = Shell.run(str, strArr, null, false);
            if (ret == null) {
                return null;
            }
            for (String line : ret) {
                if (internal) {
                    try {
                        if (Integer.parseInt(line) > 0) {
                            return line;
                        }
                    } catch (NumberFormatException e) {
                    }
                } else if (line.contains(".")) {
                    return line;
                }
            }
            return null;
        }
    }

    @Deprecated
    public static List<String> run(String shell, String[] commands, boolean wantSTDERR) {
        return run(shell, commands, wantSTDERR);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.lang.String> run(java.lang.String r22, java.lang.String[] r23, java.lang.String[] r24, boolean r25) {
        /*
        r16 = r22.toUpperCase();
        r19 = new java.util.ArrayList;
        r19.<init>();
        r15 = java.util.Collections.synchronizedList(r19);
        if (r24 == 0) goto L_0x009a;
    L_0x000f:
        r13 = new java.util.HashMap;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r13.<init>();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = java.lang.System.getenv();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r13.putAll(r0);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r6 = r24;
        r12 = r6.length;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r11 = 0;
    L_0x0021:
        if (r11 >= r12) goto L_0x004b;
    L_0x0023:
        r8 = r6[r11];	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = "=";
        r0 = r19;
        r17 = r8.indexOf(r0);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        if (r17 < 0) goto L_0x0048;
    L_0x002f:
        r19 = 0;
        r0 = r19;
        r1 = r17;
        r19 = r8.substring(r0, r1);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = r17 + 1;
        r0 = r20;
        r20 = r8.substring(r0);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r1 = r20;
        r13.put(r0, r1);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
    L_0x0048:
        r11 = r11 + 1;
        goto L_0x0021;
    L_0x004b:
        r10 = 0;
        r19 = r13.size();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r0 = new java.lang.String[r0];	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r24 = r0;
        r19 = r13.entrySet();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r11 = r19.iterator();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
    L_0x005e:
        r19 = r11.hasNext();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        if (r19 == 0) goto L_0x009a;
    L_0x0064:
        r9 = r11.next();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r9 = (java.util.Map.Entry) r9;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20.<init>();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = r9.getKey();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = (java.lang.String) r19;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r20;
        r1 = r19;
        r19 = r0.append(r1);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = "=";
        r20 = r19.append(r20);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = r9.getValue();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = (java.lang.String) r19;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r20;
        r1 = r19;
        r19 = r0.append(r1);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = r19.toString();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r24[r10] = r19;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r10 = r10 + 1;
        goto L_0x005e;
    L_0x009a:
        r19 = java.lang.Runtime.getRuntime();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r1 = r22;
        r2 = r24;
        r14 = r0.exec(r1, r2);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r4 = new java.io.DataOutputStream;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = r14.getOutputStream();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r4.<init>(r0);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r5 = new eu.chainfire.libsuperuser.StreamGobbler;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19.<init>();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r1 = r16;
        r19 = r0.append(r1);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = "-";
        r19 = r19.append(r20);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = r19.toString();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = r14.getInputStream();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r1 = r20;
        r5.<init>(r0, r1, r15);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r3 = new eu.chainfire.libsuperuser.StreamGobbler;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19.<init>();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r1 = r16;
        r19 = r0.append(r1);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = "*";
        r19 = r19.append(r20);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = r19.toString();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r21 = r14.getErrorStream();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        if (r25 == 0) goto L_0x0131;
    L_0x00f6:
        r19 = r15;
    L_0x00f8:
        r0 = r20;
        r1 = r21;
        r2 = r19;
        r3.<init>(r0, r1, r2);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r5.start();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r3.start();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r6 = r23;
        r12 = r6.length;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r11 = 0;
    L_0x010b:
        if (r11 >= r12) goto L_0x0134;
    L_0x010d:
        r18 = r6[r11];	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19.<init>();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r1 = r18;
        r19 = r0.append(r1);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = "\n";
        r19 = r19.append(r20);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = r19.toString();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r0 = r19;
        r4.writeBytes(r0);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r4.flush();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r11 = r11 + 1;
        goto L_0x010b;
    L_0x0131:
        r19 = 0;
        goto L_0x00f8;
    L_0x0134:
        r19 = "exit\n";
        r0 = r19;
        r4.writeBytes(r0);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r4.flush();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r14.waitFor();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r4.close();	 Catch:{ IOException -> 0x016d, InterruptedException -> 0x016a }
    L_0x0144:
        r5.join();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r3.join();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r14.destroy();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r19 = "su";
        r0 = r22;
        r1 = r19;
        r19 = r0.equals(r1);	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        if (r19 == 0) goto L_0x0166;
    L_0x0159:
        r19 = r14.exitValue();	 Catch:{ IOException -> 0x0167, InterruptedException -> 0x016a }
        r20 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r0 = r19;
        r1 = r20;
        if (r0 != r1) goto L_0x0166;
    L_0x0165:
        r15 = 0;
    L_0x0166:
        return r15;
    L_0x0167:
        r7 = move-exception;
        r15 = 0;
        goto L_0x0166;
    L_0x016a:
        r7 = move-exception;
        r15 = 0;
        goto L_0x0166;
    L_0x016d:
        r19 = move-exception;
        goto L_0x0144;
        */
        throw new UnsupportedOperationException("Method not decompiled: eu.chainfire.libsuperuser.Shell.run(java.lang.String, java.lang.String[], java.lang.String[], boolean):java.util.List<java.lang.String>");
    }
}
