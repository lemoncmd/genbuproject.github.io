package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.XsapiUser;
import com.microsoft.xbox.idp.interop.XsapiUser.StartSignInCallback;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.ResultListener;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker;
import com.microsoft.xbox.idp.util.ResultCache;

public class StartSignInLoader extends WorkerLoader<Result> {

    public static class Data {
    }

    private static class MyWorker implements Worker<Result> {
        private final ResultCache<Result> cache;
        private final Object resultKey;

        private MyWorker(ResultCache<Result> resultCache, Object obj) {
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
            XsapiUser.getInstance().startSignIn(new StartSignInCallback() {
                public void onError(int i, int i2, String str) {
                    Result result = new Result(null, new HttpError(i2, i, str));
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    resultListener.onResult(result);
                }

                public void onSuccess() {
                    Result result = new Result(new Data(), null);
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

    public StartSignInLoader(Context context, ResultCache<Result> resultCache, Object obj) {
        super(context, new MyWorker(resultCache, obj));
    }

    protected boolean isDataReleased(Result result) {
        return result.isReleased();
    }

    protected void releaseData(Result result) {
        result.release();
    }
}
