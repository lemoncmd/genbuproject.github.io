package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.Interop.XBLoginCallback;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.ResultListener;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker;
import com.microsoft.xbox.idp.util.AuthFlowResult;
import com.microsoft.xbox.idp.util.ResultCache;

public class XBLoginLoader extends WorkerLoader<Result> {

    public static class Data {
        private final AuthFlowResult authFlowResult;
        private final boolean createAccount;

        public Data(AuthFlowResult authFlowResult, boolean z) {
            this.authFlowResult = authFlowResult;
            this.createAccount = z;
        }

        public AuthFlowResult getAuthFlowResult() {
            return this.authFlowResult;
        }

        public boolean isCreateAccount() {
            return this.createAccount;
        }
    }

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
            Interop.InvokeXBLogin(this.userPtr, this.rpsTicket, new XBLoginCallback() {
                public void onError(int i, int i2, String str) {
                    Result result = new Result(null, new HttpError(i2, i, str));
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    resultListener.onResult(result);
                }

                public void onLogin(long j, boolean z) {
                    Result result = new Result(new Data(new AuthFlowResult(j), z), null);
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

    public XBLoginLoader(Context context, long j, String str) {
        this(context, j, str, null, null);
    }

    public XBLoginLoader(Context context, long j, String str, ResultCache<Result> resultCache, Object obj) {
        super(context, new MyWorker(j, str, resultCache, obj));
    }

    protected boolean isDataReleased(Result result) {
        return result.isReleased();
    }

    protected void releaseData(Result result) {
        result.release();
    }
}
