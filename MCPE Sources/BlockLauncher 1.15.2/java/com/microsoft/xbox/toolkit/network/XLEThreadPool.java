package com.microsoft.xbox.toolkit.network;

import com.microsoft.xbox.toolkit.XLEThread;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class XLEThreadPool {
    public static XLEThreadPool biOperationsThreadPool = new XLEThreadPool(false, 1, "XLEPerfMarkerOperationsPool");
    public static XLEThreadPool nativeOperationsThreadPool = new XLEThreadPool(true, 4, "XLENativeOperationsPool");
    public static XLEThreadPool networkOperationsThreadPool = new XLEThreadPool(false, 3, "XLENetworkOperationsPool");
    public static XLEThreadPool textureThreadPool = new XLEThreadPool(false, 1, "XLETexturePool");
    private ExecutorService executor;
    private String name;

    public XLEThreadPool(boolean z, final int i, String str) {
        this.name = str;
        ThreadFactory anonymousClass1 = new ThreadFactory() {
            public Thread newThread(Runnable runnable) {
                Thread xLEThread = new XLEThread(runnable, XLEThreadPool.this.name);
                xLEThread.setDaemon(true);
                xLEThread.setPriority(i);
                return xLEThread;
            }
        };
        if (z) {
            this.executor = Executors.newSingleThreadExecutor(anonymousClass1);
        } else {
            this.executor = Executors.newCachedThreadPool(anonymousClass1);
        }
    }

    public void run(Runnable runnable) {
        this.executor.execute(runnable);
    }
}
