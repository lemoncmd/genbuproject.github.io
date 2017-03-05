package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public abstract class NetworkAsyncTask<T> extends XLEAsyncTask<T> {
    protected boolean forceLoad = true;
    private boolean shouldExecute = true;

    public NetworkAsyncTask() {
        super(XLEThreadPool.networkOperationsThreadPool);
    }

    public NetworkAsyncTask(XLEThreadPool xLEThreadPool) {
        super(XLEThreadPool.networkOperationsThreadPool);
    }

    protected abstract boolean checkShouldExecute();

    protected final T doInBackground() {
        try {
            return loadDataInBackground();
        } catch (Exception e) {
            return onError();
        }
    }

    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (this.cancelled) {
            this.shouldExecute = checkShouldExecute();
        } else {
            this.shouldExecute = checkShouldExecute();
        }
        if (this.shouldExecute || this.forceLoad) {
            this.isBusy = true;
            onPreExecute();
            super.executeBackground();
            return;
        }
        onNoAction();
        this.isBusy = false;
    }

    public void load(boolean z) {
        this.forceLoad = z;
        execute();
    }

    protected abstract T loadDataInBackground();

    protected abstract T onError();

    protected abstract void onNoAction();
}
