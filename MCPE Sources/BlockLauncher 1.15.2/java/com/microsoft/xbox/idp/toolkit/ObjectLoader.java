package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.ResultListener;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpCall.Callback;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

public class ObjectLoader<T> extends WorkerLoader<Result<T>> {
    private static final String TAG = ObjectLoader.class.getSimpleName();

    public interface Cache {
        void clear();

        <T> Result<T> get(Object obj);

        <T> Result<T> put(Object obj, Result<T> result);

        <T> Result<T> remove(Object obj);
    }

    private static class MyWorker<T> implements Worker<Result<T>> {
        private final Cache cache;
        private final Class<T> cls;
        private final Gson gson;
        private final HttpCall httpCall;
        private final Object resultKey;

        private MyWorker(Cache cache, Object obj, Class<T> cls, Gson gson, HttpCall httpCall) {
            this.cache = cache;
            this.resultKey = obj;
            this.cls = cls;
            this.gson = gson;
            this.httpCall = httpCall;
        }

        private boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void cancel() {
        }

        public void start(final ResultListener<Result<T>> resultListener) {
            if (hasCache()) {
                Result result;
                synchronized (this.cache) {
                    result = this.cache.get(this.resultKey);
                }
                if (result != null) {
                    resultListener.onResult(result);
                    return;
                }
            }
            this.httpCall.getResponseAsync(new Callback() {
                public void processHttpError(int i, int i2, String str) {
                    Log.e(ObjectLoader.TAG, "errorCode: " + i + ", httpStatus: " + i2 + ", errorMessage: " + str);
                    Result result = new Result(new HttpError(i, i2, str));
                    if (MyWorker.this.hasCache()) {
                        synchronized (MyWorker.this.cache) {
                            MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                        }
                    }
                    resultListener.onResult(result);
                }

                public void processResponse(InputStream inputStream) throws Exception {
                    if (MyWorker.this.cls == Void.class) {
                        resultListener.onResult(new Result(null));
                        return;
                    }
                    StringWriter stringWriter = new StringWriter();
                    try {
                        Reader inputStreamReader = new InputStreamReader(new BufferedInputStream(inputStream));
                        try {
                            Result result = new Result(MyWorker.this.gson.fromJson(inputStreamReader, MyWorker.this.cls));
                            if (MyWorker.this.hasCache()) {
                                synchronized (MyWorker.this.cache) {
                                    MyWorker.this.cache.put(MyWorker.this.resultKey, result);
                                }
                            }
                            resultListener.onResult(result);
                            inputStreamReader.close();
                        } catch (Throwable th) {
                            inputStreamReader.close();
                        }
                    } finally {
                        stringWriter.close();
                    }
                }
            });
        }
    }

    public static class Result<T> extends LoaderResult<T> {
        protected Result(HttpError httpError) {
            super(null, httpError);
        }

        protected Result(T t) {
            super(t, null);
        }

        public boolean isReleased() {
            return true;
        }

        public void release() {
        }
    }

    public ObjectLoader(Context context, Cache cache, Object obj, Class<T> cls, Gson gson, HttpCall httpCall) {
        super(context, new MyWorker(cache, obj, cls, gson, httpCall));
    }

    public ObjectLoader(Context context, Class<T> cls, Gson gson, HttpCall httpCall) {
        this(context, null, null, cls, gson, httpCall);
    }

    protected boolean isDataReleased(Result<T> result) {
        return result.isReleased();
    }

    protected void releaseData(Result<T> result) {
        result.release();
    }
}
