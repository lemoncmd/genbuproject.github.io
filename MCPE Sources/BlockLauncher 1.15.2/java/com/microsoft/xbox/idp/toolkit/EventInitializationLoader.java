package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.Interop.EventInitializationCallback;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.ResultListener;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker;
import com.microsoft.xbox.idp.util.ResultCache;

public class EventInitializationLoader extends WorkerLoader<Result> {

    private static class MyWorker implements Worker<Result> {
        private final ResultCache<Result> cache;
        private final Object resultKey;
        private final String rpsTicket;
        private final long userPtr;

        private MyWorker(long j, String str, ResultCache<Result> resultCache, Object obj) {
            this.userPtr = j;
            this.rpsTicket = str;
            this.cache = resultCache;
            this.resultKey = obj;
        }

        private boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void cancel() {
        }

        public void start(final ResultListener<Result> resultListener) {
            if (hasCache()) {
                Result result;
                synchronized (this.cache) {
                    result = (Result) this.cache.get(this.resultKey);
                }
                if (result != null) {
                    resultListener.onResult(result);
                    return;
                }
            }
            Interop.InvokeEventInitialization(this.userPtr, this.rpsTicket, new EventInitializationCallback() {
                public void onError(int i, int i2, String str) {
                    Result result = new Result(new HttpError(i2, i, str));
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    resultListener.onResult(result);
                }

                public void onSuccess() {
                    Result result = new Result(null);
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    resultListener.onResult(result);
                }
            });
        }
    }

    public static class Result extends LoaderResult<Void> {
        protected Result(HttpError httpError) {
            super(null, httpError);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    public EventInitializationLoader(Context context, long j, String str, ResultCache<Result> resultCache, Object obj) {
        super(context, new MyWorker(j, str, resultCache, obj));
    }

    protected boolean isDataReleased(Result result) {
        return true;
    }

    protected void releaseData(Result result) {
    }
}
