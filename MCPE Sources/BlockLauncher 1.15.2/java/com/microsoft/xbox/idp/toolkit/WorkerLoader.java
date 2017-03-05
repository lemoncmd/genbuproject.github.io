package com.microsoft.xbox.idp.toolkit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Loader;
import android.os.Build.VERSION;
import android.os.Handler;

public abstract class WorkerLoader<D> extends Loader<D> {
    private final Handler dispatcher = new Handler();
    private final Object lock = new Object();
    private D result;
    private ResultListener<D> resultListener;
    private final Worker<D> worker;

    public interface Worker<D> {
        void cancel();

        void start(ResultListener<D> resultListener);
    }

    public interface ResultListener<D> {
        void onResult(D d);
    }

    private class ResultListenerImpl implements ResultListener<D> {
        private ResultListenerImpl() {
        }

        public void onResult(final D d) {
            synchronized (WorkerLoader.this.lock) {
                final boolean z = this != WorkerLoader.this.resultListener;
                WorkerLoader.this.resultListener = null;
                WorkerLoader.this.dispatcher.post(new Runnable() {
                    public void run() {
                        if (z) {
                            WorkerLoader.this.onCanceled(d);
                        } else {
                            WorkerLoader.this.deliverResult(d);
                        }
                    }
                });
            }
        }
    }

    public WorkerLoader(Context context, Worker<D> worker) {
        super(context);
        this.worker = worker;
    }

    @SuppressLint({"NewApi"})
    private boolean cancelLoadCompat() {
        return VERSION.SDK_INT < 16 ? onCancelLoad() : cancelLoad();
    }

    public void deliverResult(D d) {
        if (!isReset()) {
            D d2 = this.result;
            this.result = d;
            if (isStarted()) {
                super.deliverResult(d);
            }
            if (d2 != null && d2 != d && !isDataReleased(d2)) {
                releaseData(d2);
            }
        } else if (d != null) {
            releaseData(d);
        }
    }

    protected abstract boolean isDataReleased(D d);

    protected boolean onCancelLoad() {
        boolean z;
        synchronized (this.lock) {
            if (this.resultListener != null) {
                this.worker.cancel();
                this.resultListener = null;
                z = true;
            } else {
                z = false;
            }
        }
        return z;
    }

    public void onCanceled(D d) {
        if (d != null && !isDataReleased(d)) {
            releaseData(d);
        }
    }

    protected void onForceLoad() {
        super.onForceLoad();
        cancelLoadCompat();
        synchronized (this.lock) {
            this.resultListener = new ResultListenerImpl();
            this.worker.start(this.resultListener);
        }
    }

    protected void onReset() {
        cancelLoadCompat();
        if (!(this.result == null || isDataReleased(this.result))) {
            releaseData(this.result);
        }
        this.result = null;
    }

    protected void onStartLoading() {
        if (this.result != null) {
            deliverResult(this.result);
        }
        if (takeContentChanged() || this.result == null) {
            forceLoad();
        }
    }

    protected void onStopLoading() {
        cancelLoadCompat();
    }

    protected abstract void releaseData(D d);
}
