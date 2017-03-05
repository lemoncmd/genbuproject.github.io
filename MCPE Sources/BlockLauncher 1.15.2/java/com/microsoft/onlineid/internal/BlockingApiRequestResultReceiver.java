package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import com.microsoft.onlineid.internal.exception.UserCancelledException;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BlockingApiRequestResultReceiver<ResultType> extends ApiRequestResultReceiver {
    private final BlockingQueue<Result> _queue = new LinkedBlockingQueue();

    public class Result {
        private final Exception _exception;
        private final SsoResponse<ResultType> _result;

        private Result(PendingIntent pendingIntent) {
            this._result = new SsoResponse().setPendingIntent(pendingIntent);
            this._exception = null;
        }

        private Result(Exception exception) {
            this._result = null;
            this._exception = exception;
        }

        private Result(ResultType resultType) {
            this._result = new SsoResponse().setData(resultType);
            this._exception = null;
        }

        private Exception getException() {
            return this._exception;
        }

        public SsoResponse<ResultType> getSsoResponse() {
            return this._result;
        }
    }

    public BlockingApiRequestResultReceiver() {
        super(null);
    }

    public SsoResponse<ResultType> blockForResult() throws Exception {
        Result result = (Result) this._queue.take();
        if (result == null) {
            throw new IllegalStateException("Expect a result to be available.");
        } else if (result.getException() == null) {
            return result.getSsoResponse();
        } else {
            throw result.getException();
        }
    }

    protected void onFailure(Exception exception) {
        this._queue.add(new Result(exception));
    }

    protected void onUINeeded(PendingIntent pendingIntent) {
        this._queue.add(new Result(pendingIntent));
    }

    protected void onUserCancel() {
        this._queue.add(new Result(new UserCancelledException()));
    }

    protected void setResult(ResultType resultType) {
        this._queue.add(new Result((Object) resultType));
    }
}
