package com.microsoft.cll.android;

import android.util.Log;

public class AndroidLogger implements ILogger {
    private static AndroidLogger INSTANCE;
    private static Object InstanceLock = new Object();
    private Verbosity verbosity;

    private AndroidLogger() {
        setVerbosity(Verbosity.NONE);
    }

    public static ILogger getInstance() {
        if (INSTANCE == null) {
            synchronized (InstanceLock) {
                if (INSTANCE == null) {
                    INSTANCE = new AndroidLogger();
                }
            }
        }
        return INSTANCE;
    }

    public void error(String str, String str2) {
        if (this.verbosity == Verbosity.ERROR || this.verbosity == Verbosity.WARN || this.verbosity == Verbosity.INFO) {
            Log.e(str, str2);
        }
    }

    public Verbosity getVerbosity() {
        return this.verbosity;
    }

    public void info(String str, String str2) {
        if (this.verbosity == Verbosity.INFO) {
            Log.i(str, str2);
        }
    }

    public void setVerbosity(Verbosity verbosity) {
        this.verbosity = verbosity;
    }

    public void warn(String str, String str2) {
        if (this.verbosity == Verbosity.WARN || this.verbosity == Verbosity.INFO) {
            Log.d(str, str2);
        }
    }
}
