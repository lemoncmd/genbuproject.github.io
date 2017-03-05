package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.network.XLEThreadPool;

public abstract class XLEAsyncTask<Result> {
    protected boolean cancelled = false;
    private XLEAsyncTask chainedTask = null;
    private Runnable doBackgroundAndPostExecuteRunnable = null;
    protected boolean isBusy = false;
    private XLEThreadPool threadPool = null;

    public XLEAsyncTask(XLEThreadPool xLEThreadPool) {
        this.threadPool = xLEThreadPool;
        this.doBackgroundAndPostExecuteRunnable = new Runnable() {
            public void run() {
                final Object doInBackground = !XLEAsyncTask.this.cancelled ? XLEAsyncTask.this.doInBackground() : null;
                ThreadManager.UIThreadPost(new Runnable() {
                    public void run() {
                        XLEAsyncTask.this.isBusy = false;
                        if (!XLEAsyncTask.this.cancelled) {
                            XLEAsyncTask.this.onPostExecute(doInBackground);
                            if (XLEAsyncTask.this.chainedTask != null) {
                                XLEAsyncTask.this.chainedTask.execute();
                            }
                        }
                    }
                });
            }
        };
    }

    public static void executeAll(XLEAsyncTask... xLEAsyncTaskArr) {
        if (xLEAsyncTaskArr.length > 0) {
            for (int i = 0; i < xLEAsyncTaskArr.length - 1; i++) {
                xLEAsyncTaskArr[i].chainedTask = xLEAsyncTaskArr[i + 1];
            }
            xLEAsyncTaskArr[0].execute();
        }
    }

    public void cancel() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.cancelled = true;
    }

    protected abstract Result doInBackground();

    public void execute() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        this.cancelled = false;
        this.isBusy = true;
        onPreExecute();
        executeBackground();
    }

    protected void executeBackground() {
        this.cancelled = false;
        this.threadPool.run(this.doBackgroundAndPostExecuteRunnable);
    }

    public boolean getIsBusy() {
        return this.isBusy && !this.cancelled;
    }

    protected abstract void onPostExecute(Result result);

    protected abstract void onPreExecute();
}
