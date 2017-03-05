package com.microsoft.xbox.toolkit;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Date;
import net.hockeyapp.android.BuildConfig;

public class XLEUnhandledExceptionHandler implements UncaughtExceptionHandler {
    public static XLEUnhandledExceptionHandler Instance = new XLEUnhandledExceptionHandler();
    private UncaughtExceptionHandler oldExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();

    private void printStackTrace(String str, Throwable th) {
        Date date = new Date();
        String str2 = BuildConfig.FLAVOR;
        String str3 = str2;
        for (StackTraceElement stackTraceElement : th.getStackTrace()) {
            str3 = str3 + String.format("\t%s\n", new Object[]{stackTraceElement.toString()});
        }
    }

    public void uncaughtException(Thread thread, Throwable th) {
        th.toString();
        if (th.getCause() != null) {
            printStackTrace("CAUSE STACK TRACE", th.getCause());
        }
        printStackTrace("MAIN THREAD STACK TRACE", th);
        this.oldExceptionHandler.uncaughtException(thread, th);
    }
}
