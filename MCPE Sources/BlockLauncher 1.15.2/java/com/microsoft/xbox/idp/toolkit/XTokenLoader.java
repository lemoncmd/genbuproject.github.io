package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.Interop.Callback;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.ResultListener;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker;
import com.microsoft.xbox.idp.util.AuthFlowResult;
import com.microsoft.xbox.idp.util.ResultCache;

public class XTokenLoader extends WorkerLoader<Result> {

    public static class Data {
        private final AuthFlowResult authFlowResult;

        public Data(AuthFlowResult authFlowResult) {
            this.authFlowResult = authFlowResult;
        }

        public AuthFlowResult getAuthFlowResult() {
            return this.authFlowResult;
        }
    }

    private static class MyWorker implements Worker<Result> {
        private final ResultCache<Result> cache;
        private final Object resultKey;
        private final long userPtr;

        public MyWorker(long j, ResultCache<Result> resultCache, Object obj) {
            this.userPtr = j;
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
            Interop.InvokeXTokenCallback(this.userPtr, new Callback() {
                public void onError(int i, int i2, String str) {
                    Result result = new Result(null, new HttpError(i2, i, str));
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    resultListener.onResult(result);
                }

                public void onXTokenAcquired(long j) {
                    Result result = new Result(new Data(new AuthFlowResult(j)), null);
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

    public static class Result extends LoaderResult<Data> {
        protected Result(Data data, HttpError httpError) {
            super(data, httpError);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    public XTokenLoader(Context context, long j) {
        this(context, j, null, null);
    }

    public XTokenLoader(Context context, long j, ResultCache<Result> resultCache, Object obj) {
        super(context, new MyWorker(j, resultCache, obj));
    }

    protected boolean isDataReleased(Result result) {
        return result.isReleased();
    }

    protected void releaseData(Result result) {
        result.release();
    }
}
