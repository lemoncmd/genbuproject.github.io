package com.microsoft.xbox.toolkit;

public class SingleEntryLoadingStatus {
    private boolean isLoading = false;
    private XLEException lastError = null;
    private Object syncObj = new Object();

    public class WaitResult {
        public XLEException error;
        public boolean waited;

        public WaitResult(boolean z, XLEException xLEException) {
            this.waited = z;
            this.error = xLEException;
        }
    }

    private void setDone(XLEException xLEException) {
        synchronized (this.syncObj) {
            this.isLoading = false;
            this.lastError = xLEException;
            this.syncObj.notifyAll();
        }
    }

    public boolean getIsLoading() {
        return this.isLoading;
    }

    public XLEException getLastError() {
        return this.lastError;
    }

    public void reset() {
        synchronized (this.syncObj) {
            this.isLoading = false;
            this.lastError = null;
            this.syncObj.notifyAll();
        }
    }

    public void setFailed(XLEException xLEException) {
        setDone(xLEException);
    }

    public void setSuccess() {
        setDone(null);
    }

    public WaitResult waitForNotLoading() {
        WaitResult waitResult;
        synchronized (this.syncObj) {
            if (this.isLoading) {
                try {
                    this.syncObj.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                waitResult = new WaitResult(true, this.lastError);
            } else {
                this.isLoading = true;
                waitResult = new WaitResult(false, null);
            }
        }
        return waitResult;
    }
}
