package com.microsoft.onlineid.internal.transport;

import android.content.Context;
import android.text.TextUtils;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.Resources;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class Transport {
    private static final String GetMethod = "GET";
    private static final String PostMethod = "POST";
    public static final String SdkIdentifier = "MsaAndroidSdk";
    private int _connectionTimeoutMilliseconds = 60000;
    private String _customUserAgentString;
    private HttpsURLConnectionWrapper _httpsURLConnectionWrapper;
    private int _readTimeoutMilliseconds = 30000;

    public static String buildUserAgentString(Context context) {
        return mergeUserAgentStrings(context.getPackageName() + "/" + PackageInfoHelper.getCurrentAppVersionName(context), "MsaAndroidSdk/" + Resources.getSdkVersion(context));
    }

    private void initializeConnection(URL url) throws NetworkException {
        if (this._httpsURLConnectionWrapper == null) {
            this._httpsURLConnectionWrapper = new HttpsURLConnectionWrapper(url);
        } else {
            this._httpsURLConnectionWrapper.setUrl(url);
        }
        try {
            this._httpsURLConnectionWrapper.openConnection();
            this._httpsURLConnectionWrapper.setConnectTimeout(this._connectionTimeoutMilliseconds);
            this._httpsURLConnectionWrapper.setReadTimeout(this._readTimeoutMilliseconds);
            this._httpsURLConnectionWrapper.setDoInput(true);
            this._httpsURLConnectionWrapper.setUseCaches(false);
            setUserAgent();
        } catch (Throwable e) {
            throw new NetworkException(e.getMessage(), e);
        }
    }

    public static String mergeUserAgentStrings(String str, String str2) {
        return TextUtils.isEmpty(str) ? str2 : !TextUtils.isEmpty(str2) ? str + "; " + str2 : str;
    }

    private void setUserAgent() {
        Assertion.check(!TextUtils.isEmpty(this._customUserAgentString));
        this._httpsURLConnectionWrapper.addRequestProperty("User-Agent", mergeUserAgentStrings(System.getProperty("http.agent"), this._customUserAgentString));
    }

    public void addRequestProperty(String str, String str2) {
        this._httpsURLConnectionWrapper.addRequestProperty(str, str2);
    }

    void appendCustomUserAgentString(String str) {
        this._customUserAgentString = mergeUserAgentStrings(this._customUserAgentString, str);
    }

    public void closeConnection() {
        this._httpsURLConnectionWrapper.disconnect();
    }

    public int getConnectionTimeoutMilliseconds() {
        return this._connectionTimeoutMilliseconds;
    }

    public int getReadTimeoutMilliseconds() {
        return this._readTimeoutMilliseconds;
    }

    public OutputStream getRequestStream() throws NetworkException {
        if (this._httpsURLConnectionWrapper.getRequestMethod().equals(GetMethod)) {
            throw new NetworkException("A GET request cannot have an OutputStream");
        }
        try {
            return this._httpsURLConnectionWrapper.getOutputStream();
        } catch (Throwable e) {
            throw new NetworkException(e);
        }
    }

    public int getResponseCode() throws NetworkException {
        try {
            return this._httpsURLConnectionWrapper.getResponseCode();
        } catch (Throwable e) {
            throw new NetworkException(e);
        }
    }

    public long getResponseDate() {
        return this._httpsURLConnectionWrapper.getDate();
    }

    public InputStream getResponseStream() throws NetworkException {
        try {
            return this._httpsURLConnectionWrapper.getResponseCode() == org.mozilla.javascript.Context.VERSION_ES6 ? this._httpsURLConnectionWrapper.getInputStream() : this._httpsURLConnectionWrapper.getErrorStream();
        } catch (Throwable e) {
            throw new NetworkException(e);
        }
    }

    public void openGetRequest(URL url) throws NetworkException {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        try {
            initializeConnection(url);
            this._httpsURLConnectionWrapper.setRequestMethod(GetMethod);
        } catch (Throwable e) {
            throw new NetworkException(e.getMessage(), e);
        }
    }

    public void openPostRequest(URL url) throws NetworkException {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        try {
            initializeConnection(url);
            this._httpsURLConnectionWrapper.setRequestMethod(PostMethod);
            this._httpsURLConnectionWrapper.setDoOutput(true);
        } catch (Throwable e) {
            throw new NetworkException(e.getMessage(), e);
        }
    }

    public void setConnectionTimeoutMilliseconds(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Connection timeout value is out of range");
        }
        this._connectionTimeoutMilliseconds = i;
    }

    void setHttpsURLConnectionWrapper(HttpsURLConnectionWrapper httpsURLConnectionWrapper) {
        this._httpsURLConnectionWrapper = httpsURLConnectionWrapper;
    }

    public void setReadTimeoutMilliseconds(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Read timeout value is out of range");
        }
        this._readTimeoutMilliseconds = i;
    }

    public void setUseCaches(boolean z) {
        this._httpsURLConnectionWrapper.setUseCaches(z);
    }
}
