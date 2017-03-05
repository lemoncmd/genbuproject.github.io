package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.ResultListener;
import com.microsoft.xbox.idp.toolkit.WorkerLoader.Worker;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpCall.Callback;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BitmapLoader extends WorkerLoader<Result> {
    private static final String TAG = BitmapLoader.class.getSimpleName();

    public interface Cache {
        void clear();

        byte[] get(Object obj);

        byte[] put(Object obj, byte[] bArr);

        byte[] remove(Object obj);
    }

    private static class MyWorker implements Worker<Result> {
        static final /* synthetic */ boolean $assertionsDisabled = (!BitmapLoader.class.desiredAssertionStatus());
        private final Cache cache;
        private final HttpCall httpCall;
        private final Object resultKey;

        private MyWorker(Cache cache, Object obj, HttpCall httpCall) {
            if ($assertionsDisabled || httpCall != null) {
                this.cache = cache;
                this.resultKey = obj;
                this.httpCall = httpCall;
                return;
            }
            throw new AssertionError();
        }

        private boolean hasCache() {
            return (this.cache == null || this.resultKey == null) ? false : true;
        }

        public void cancel() {
        }

        public void start(final ResultListener<Result> resultListener) {
            if (hasCache()) {
                final byte[] bArr;
                synchronized (this.cache) {
                    bArr = this.cache.get(this.resultKey);
                }
                if (bArr != null) {
                    new Thread(new Runnable() {
                        public void run() {
                            resultListener.onResult(new Result(BitmapFactory.decodeByteArray(bArr, 0, bArr.length)));
                        }
                    }).start();
                    return;
                }
            }
            this.httpCall.getResponseAsync(new Callback() {
                public void processHttpError(int i, int i2, String str) {
                    Log.e(BitmapLoader.TAG, "errorCode: " + i + ", httpStatus: " + i2 + ", errorMessage: " + str);
                    resultListener.onResult(new Result(new HttpError(i, i2, str)));
                }

                public void processResponse(InputStream inputStream) throws Exception {
                    OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    try {
                        InputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        try {
                            BitmapLoader.readStream(bufferedInputStream, byteArrayOutputStream);
                            byte[] toByteArray = byteArrayOutputStream.toByteArray();
                            if (MyWorker.this.hasCache()) {
                                synchronized (MyWorker.this.cache) {
                                    MyWorker.this.cache.put(MyWorker.this.resultKey, toByteArray);
                                }
                            }
                            resultListener.onResult(new Result(BitmapFactory.decodeByteArray(toByteArray, 0, toByteArray.length)));
                            bufferedInputStream.close();
                        } catch (Throwable th) {
                            bufferedInputStream.close();
                        }
                    } finally {
                        byteArrayOutputStream.close();
                    }
                }
            });
        }
    }

    public static class Result extends LoaderResult<Bitmap> {
        protected Result(Bitmap bitmap) {
            super(bitmap, null);
        }

        protected Result(HttpError httpError) {
            super(null, httpError);
        }

        public boolean isReleased() {
            return hasData() && ((Bitmap) getData()).isRecycled();
        }

        public void release() {
            if (hasData()) {
                ((Bitmap) getData()).recycle();
            }
        }
    }

    public BitmapLoader(Context context, Cache cache, Object obj, HttpCall httpCall) {
        super(context, new MyWorker(cache, obj, httpCall));
    }

    public BitmapLoader(Context context, HttpCall httpCall) {
        this(context, null, null, httpCall);
    }

    private static void readStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        while (true) {
            int read = inputStream.read();
            if (read != -1) {
                outputStream.write(read);
            } else {
                return;
            }
        }
    }

    protected boolean isDataReleased(Result result) {
        return result.isReleased();
    }

    protected void releaseData(Result result) {
        result.release();
    }
}
