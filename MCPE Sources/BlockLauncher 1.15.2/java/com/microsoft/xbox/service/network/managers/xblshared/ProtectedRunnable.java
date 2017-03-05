package com.microsoft.xbox.service.network.managers.xblshared;

public class ProtectedRunnable implements Runnable {
    private static final String TAG = ProtectedRunnable.class.getSimpleName();
    private final Runnable runnable;

    public ProtectedRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public void run() {
        Object obj = null;
        int i = 0;
        while (obj == null && i < 10) {
            try {
                this.runnable.run();
                obj = 1;
            } catch (LinkageError e) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e2) {
                }
            }
            i++;
        }
        if (obj != null) {
        }
    }
}
