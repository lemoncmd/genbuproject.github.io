package net.hockeyapp.android.metrics;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class Sender {
    static final String DEFAULT_ENDPOINT_URL = "https://gate.hockeyapp.net/v2/track";
    static final int DEFAULT_SENDER_CONNECT_TIMEOUT = 15000;
    static final int DEFAULT_SENDER_READ_TIMEOUT = 10000;
    static final int MAX_REQUEST_COUNT = 10;
    private static final String TAG = "HockeyApp-Metrics";
    private String mCustomServerURL;
    private AtomicInteger mRequestCount = new AtomicInteger(0);
    protected WeakReference<Persistence> mWeakPersistence;

    protected Sender() {
    }

    private void logRequest(HttpURLConnection httpURLConnection, String str) {
        Writer writer = null;
        if (!(httpURLConnection == null || str == null)) {
            try {
                HockeyLog.debug(TAG, "Sending payload:\n" + str);
                HockeyLog.debug(TAG, "Using URL:" + httpURLConnection.getURL().toString());
                writer = getWriter(httpURLConnection);
                writer.write(str);
                writer.flush();
            } catch (IOException e) {
                HockeyLog.debug(TAG, "Couldn't log data with: " + e.toString());
                if (writer != null) {
                    try {
                        writer.close();
                        return;
                    } catch (IOException e2) {
                        HockeyLog.error(TAG, "Couldn't close writer with: " + e2.toString());
                        return;
                    }
                }
                return;
            } catch (Throwable th) {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e3) {
                        HockeyLog.error(TAG, "Couldn't close writer with: " + e3.toString());
                    }
                }
            }
        }
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e22) {
                HockeyLog.error(TAG, "Couldn't close writer with: " + e22.toString());
            }
        }
    }

    protected HttpURLConnection createConnection() {
        HttpURLConnection httpURLConnection;
        Throwable e;
        try {
            URL url;
            if (getCustomServerURL() == null) {
                url = new URL(DEFAULT_ENDPOINT_URL);
            } else {
                url = new URL(this.mCustomServerURL);
                if (url == null) {
                    url = new URL(DEFAULT_ENDPOINT_URL);
                }
            }
            httpURLConnection = (HttpURLConnection) url.openConnection();
            try {
                httpURLConnection.setReadTimeout(DEFAULT_SENDER_READ_TIMEOUT);
                httpURLConnection.setConnectTimeout(DEFAULT_SENDER_CONNECT_TIMEOUT);
                httpURLConnection.setRequestMethod(HttpEngine.POST);
                httpURLConnection.setRequestProperty("Content-Type", "application/x-json-stream");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setUseCaches(false);
            } catch (IOException e2) {
                e = e2;
                HockeyLog.error(TAG, "Could not open connection for provided URL with exception: ", e);
                return httpURLConnection;
            }
        } catch (Throwable e3) {
            Throwable th = e3;
            httpURLConnection = null;
            e = th;
            HockeyLog.error(TAG, "Could not open connection for provided URL with exception: ", e);
            return httpURLConnection;
        }
        return httpURLConnection;
    }

    protected String getCustomServerURL() {
        return this.mCustomServerURL;
    }

    protected Persistence getPersistence() {
        return this.mWeakPersistence != null ? (Persistence) this.mWeakPersistence.get() : null;
    }

    @TargetApi(19)
    protected Writer getWriter(HttpURLConnection httpURLConnection) throws IOException {
        if (VERSION.SDK_INT < 19) {
            return new OutputStreamWriter(httpURLConnection.getOutputStream(), HttpURLConnectionBuilder.DEFAULT_CHARSET);
        }
        httpURLConnection.addRequestProperty("Content-Encoding", "gzip");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-json-stream");
        return new OutputStreamWriter(new GZIPOutputStream(httpURLConnection.getOutputStream(), true), HttpURLConnectionBuilder.DEFAULT_CHARSET);
    }

    protected boolean isExpected(int i) {
        return 199 < i && i <= 203;
    }

    protected boolean isRecoverableError(int i) {
        return Arrays.asList(new Integer[]{Integer.valueOf(408), Integer.valueOf(429), Integer.valueOf(500), Integer.valueOf(503), Integer.valueOf(511)}).contains(Integer.valueOf(i));
    }

    protected String loadData(File file) {
        String str = null;
        if (!(getPersistence() == null || file == null)) {
            str = getPersistence().load(file);
            if (str != null && str.isEmpty()) {
                getPersistence().deleteFile(file);
            }
        }
        return str;
    }

    protected void onResponse(HttpURLConnection httpURLConnection, int i, String str, File file) {
        this.mRequestCount.getAndDecrement();
        HockeyLog.debug(TAG, "response code " + Integer.toString(i));
        if (isRecoverableError(i)) {
            HockeyLog.debug(TAG, "Recoverable error (probably a server error), persisting data:\n" + str);
            if (getPersistence() != null) {
                getPersistence().makeAvailable(file);
                return;
            }
            return;
        }
        if (getPersistence() != null) {
            getPersistence().deleteFile(file);
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (isExpected(i)) {
            triggerSending();
        } else {
            onUnexpected(httpURLConnection, i, stringBuilder);
        }
    }

    protected void onUnexpected(HttpURLConnection httpURLConnection, int i, StringBuilder stringBuilder) {
        String format = String.format(Locale.ROOT, "Unexpected response code: %d", new Object[]{Integer.valueOf(i)});
        stringBuilder.append(format);
        stringBuilder.append("\n");
        HockeyLog.error(TAG, format);
        readResponse(httpURLConnection, stringBuilder);
    }

    protected void readResponse(HttpURLConnection httpURLConnection, StringBuilder stringBuilder) {
        StringBuffer stringBuffer = new StringBuffer();
        InputStream inputStream = null;
        try {
            Object stringBuffer2;
            inputStream = httpURLConnection.getErrorStream();
            if (inputStream == null) {
                inputStream = httpURLConnection.getInputStream();
            }
            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, HttpURLConnectionBuilder.DEFAULT_CHARSET));
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    }
                    stringBuffer.append(readLine);
                }
                stringBuffer2 = stringBuffer.toString();
            } else {
                stringBuffer2 = httpURLConnection.getResponseMessage();
            }
            if (TextUtils.isEmpty(stringBuffer2)) {
                HockeyLog.verbose(TAG, "Couldn't log response, result is null or empty string");
            } else {
                HockeyLog.verbose(stringBuffer2);
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    HockeyLog.error(TAG, e.toString());
                }
            }
        } catch (IOException e2) {
            HockeyLog.error(TAG, e2.toString());
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e22) {
                    HockeyLog.error(TAG, e22.toString());
                }
            }
        } catch (Throwable th) {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e3) {
                    HockeyLog.error(TAG, e3.toString());
                }
            }
        }
    }

    protected int requestCount() {
        return this.mRequestCount.get();
    }

    protected void send() {
        if (getPersistence() != null) {
            File nextAvailableFileInDirectory = getPersistence().nextAvailableFileInDirectory();
            String loadData = loadData(nextAvailableFileInDirectory);
            HttpURLConnection createConnection = createConnection();
            if (loadData != null && createConnection != null) {
                send(createConnection, nextAvailableFileInDirectory, loadData);
            }
        }
    }

    protected void send(HttpURLConnection httpURLConnection, File file, String str) {
        logRequest(httpURLConnection, str);
        if (httpURLConnection != null && file != null && str != null) {
            try {
                httpURLConnection.connect();
                onResponse(httpURLConnection, httpURLConnection.getResponseCode(), str, file);
            } catch (IOException e) {
                HockeyLog.debug(TAG, "Couldn't send data with IOException: " + e.toString());
                if (getPersistence() != null) {
                    HockeyLog.debug(TAG, "Persisting because of IOException: We're probably offline.");
                    getPersistence().makeAvailable(file);
                }
            }
        }
    }

    protected void setCustomServerURL(String str) {
        this.mCustomServerURL = str;
    }

    protected void setPersistence(Persistence persistence) {
        this.mWeakPersistence = new WeakReference(persistence);
    }

    protected void triggerSending() {
        if (requestCount() < MAX_REQUEST_COUNT) {
            this.mRequestCount.getAndIncrement();
            AsyncTaskUtils.execute(new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... voidArr) {
                    Sender.this.send();
                    return null;
                }
            });
            return;
        }
        HockeyLog.debug(TAG, "We have already 10 pending requests, not sending anything.");
    }

    protected void triggerSendingForTesting(final HttpURLConnection httpURLConnection, final File file, final String str) {
        if (requestCount() < MAX_REQUEST_COUNT) {
            this.mRequestCount.getAndIncrement();
            AsyncTaskUtils.execute(new AsyncTask<Void, Void, Void>() {
                protected Void doInBackground(Void... voidArr) {
                    Sender.this.send(httpURLConnection, file, str);
                    return null;
                }
            });
        }
    }
}
