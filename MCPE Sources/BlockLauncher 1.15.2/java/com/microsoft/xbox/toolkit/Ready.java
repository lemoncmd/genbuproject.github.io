package com.microsoft.xbox.toolkit;

public class Ready {
    private boolean ready = false;
    private Object syncObj = new Object();

    public boolean getIsReady() {
        boolean z;
        synchronized (this.syncObj) {
            z = this.ready;
        }
        return z;
    }

    public void reset() {
        synchronized (this.syncObj) {
            this.ready = false;
        }
    }

    public void setReady() {
        synchronized (this.syncObj) {
            this.ready = true;
            this.syncObj.notifyAll();
        }
    }

    public void waitForReady() {
        waitForReady(0);
    }

    public void waitForReady(int i) {
        synchronized (this.syncObj) {
            if (!this.ready) {
                if (i > 0) {
                    try {
                        this.syncObj.wait((long) i);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    this.syncObj.wait();
                }
            }
        }
    }
}
