package com.microsoft.onlineid.internal.transport;

import com.microsoft.onlineid.internal.Assertion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpsURLConnectionWrapper {
    private HttpsURLConnection _connection;
    private URL _url;

    public HttpsURLConnectionWrapper(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        this._url = url;
    }

    private void verifyConnectionIsOpened() {
        if (this._connection == null) {
            throw new IllegalStateException("openConnection should have been called first");
        }
    }

    public void addRequestProperty(String str, String str2) {
        verifyConnectionIsOpened();
        this._connection.addRequestProperty(str, str2);
    }

    public void disconnect() {
        verifyConnectionIsOpened();
        this._connection.disconnect();
    }

    public int getContentLength() {
        verifyConnectionIsOpened();
        return this._connection.getContentLength();
    }

    public long getDate() {
        verifyConnectionIsOpened();
        return this._connection.getDate();
    }

    public InputStream getErrorStream() {
        verifyConnectionIsOpened();
        return this._connection.getErrorStream();
    }

    public InputStream getInputStream() throws IOException {
        verifyConnectionIsOpened();
        return this._connection.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        verifyConnectionIsOpened();
        return this._connection.getOutputStream();
    }

    public String getRequestMethod() {
        verifyConnectionIsOpened();
        return this._connection.getRequestMethod();
    }

    public int getResponseCode() throws IOException {
        verifyConnectionIsOpened();
        return this._connection.getResponseCode();
    }

    public void openConnection() throws IOException {
        this._connection = (HttpsURLConnection) this._url.openConnection();
    }

    public void setConnectTimeout(int i) {
        verifyConnectionIsOpened();
        this._connection.setConnectTimeout(i);
    }

    public void setDoInput(boolean z) {
        verifyConnectionIsOpened();
        this._connection.setDoInput(z);
    }

    public void setDoOutput(boolean z) {
        verifyConnectionIsOpened();
        this._connection.setDoOutput(z);
    }

    public void setReadTimeout(int i) {
        verifyConnectionIsOpened();
        this._connection.setReadTimeout(i);
    }

    public void setRequestMethod(String str) throws ProtocolException {
        verifyConnectionIsOpened();
        this._connection.setRequestMethod(str);
    }

    public void setUrl(URL url) {
        try {
            disconnect();
        } catch (IllegalStateException e) {
            Assertion.check(false);
        }
        this._url = url;
    }

    public void setUseCaches(boolean z) {
        verifyConnectionIsOpened();
        this._connection.setUseCaches(z);
    }
}
