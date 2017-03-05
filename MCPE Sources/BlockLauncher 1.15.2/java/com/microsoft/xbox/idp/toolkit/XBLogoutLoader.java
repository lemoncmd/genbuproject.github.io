package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import com.microsoft.xbox.idp.interop.Interop;
import com.microsoft.xbox.idp.interop.Interop.XBLogoutCallback;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.ResultListener;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker;

public class XBLogoutLoader extends WorkerLoader<Result> {

    private static class MyWorker implements Worker<Result> {
        private final long userPtr;

        private MyWorker(long j) {
            this.userPtr = j;
        }

        public void cancel() {
        }

        public void start(final ResultListener<Result> resultListener) {
            Interop.InvokeXBLogout(this.userPtr, new XBLogoutCallback() {
                public void onLoggedOut() {
                    resultListener.onResult(new Result());
                }
            });
        }
    }

    public static class Result extends LoaderResult<Void> {
        protected Result() {
            super(null, null);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    public XBLogoutLoader(Context context, long j) {
        super(context, new MyWorker(j));
    }

    protected boolean isDataReleased(Result result) {
        return result.isReleased();
    }

    protected void releaseData(Result result) {
        result.release();
    }
}
