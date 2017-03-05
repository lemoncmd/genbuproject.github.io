package com.microsoft.xbox.toolkit;

import android.app.ActivityManager;
import android.os.Debug;
import android.os.Debug.MemoryInfo;
import com.microsoft.xboxtcui.XboxTcuiSdk;

public class MemoryMonitor {
    public static final int KB_TO_BYTES = 1024;
    public static final int MB_TO_BYTES = 1048576;
    public static final int MB_TO_KB = 1024;
    private static MemoryMonitor instance = new MemoryMonitor();
    private MemoryInfo memoryInfo = new MemoryInfo();

    private MemoryMonitor() {
    }

    public static int getTotalPss() {
        synchronized (MemoryMonitor.class) {
            try {
                Debug.getMemoryInfo(instance.memoryInfo);
                int totalPss = instance.memoryInfo.getTotalPss();
                return totalPss;
            } finally {
                Object obj = MemoryMonitor.class;
            }
        }
    }

    public static MemoryMonitor instance() {
        return instance;
    }

    public int getDalvikFreeKb() {
        int memoryClass;
        synchronized (this) {
            Debug.getMemoryInfo(this.memoryInfo);
            memoryClass = (((ActivityManager) XboxTcuiSdk.getSystemService("activity")).getMemoryClass() * MB_TO_KB) - getDalvikUsedKb();
        }
        return memoryClass;
    }

    public int getDalvikFreeMb() {
        int dalvikFreeKb;
        synchronized (this) {
            dalvikFreeKb = getDalvikFreeKb() / MB_TO_KB;
        }
        return dalvikFreeKb;
    }

    public int getDalvikUsedKb() {
        int i;
        synchronized (this) {
            Debug.getMemoryInfo(this.memoryInfo);
            i = this.memoryInfo.dalvikPss;
        }
        return i;
    }

    public int getMemoryClass() {
        return ((ActivityManager) XboxTcuiSdk.getSystemService("activity")).getLargeMemoryClass();
    }

    public int getUsedKb() {
        int i;
        synchronized (this) {
            Debug.getMemoryInfo(this.memoryInfo);
            i = this.memoryInfo.dalvikPss + this.memoryInfo.nativePss;
        }
        return i;
    }
}
