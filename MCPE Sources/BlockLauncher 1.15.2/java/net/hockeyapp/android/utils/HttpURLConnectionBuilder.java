package net.hockeyapp.android.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpURLConnectionBuilder {
    public static final String DEFAULT_CHARSET = "UTF-8";
    private static final int DEFAULT_TIMEOUT = 120000;
    private final Map<String, String> mHeaders;
    private SimpleMultipartEntity mMultipartEntity;
    private String mRequestBody;
    private String mRequestMethod;
    private int mTimeout = DEFAULT_TIMEOUT;
    private final String mUrlString;

    public HttpURLConnectionBuilder(String str) {
        this.mUrlString = str;
        this.mHeaders = new HashMap();
    }

    private static String getFormString(Map<String, String> map, String str) throws UnsupportedEncodingException {
        Iterable arrayList = new ArrayList();
        for (String str2 : map.keySet()) {
            String str3 = (String) map.get(str2);
            String str22 = URLEncoder.encode(str22, str);
            arrayList.add(str22 + "=" + URLEncoder.encode(str3, str));
        }
        return TextUtils.join("&", arrayList);
    }

    public HttpURLConnection build() throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(this.mUrlString).openConnection();
        httpURLConnection.setConnectTimeout(this.mTimeout);
        httpURLConnection.setReadTimeout(this.mTimeout);
        if (VERSION.SDK_INT <= 9) {
            httpURLConnection.setRequestProperty("Connection", "close");
        }
        if (!TextUtils.isEmpty(this.mRequestMethod)) {
            httpURLConnection.setRequestMethod(this.mRequestMethod);
            if (!TextUtils.isEmpty(this.mRequestBody) || this.mRequestMethod.equalsIgnoreCase(HttpEngine.POST) || this.mRequestMethod.equalsIgnoreCase(HttpEngine.PUT)) {
                httpURLConnection.setDoOutput(true);
            }
        }
        for (String str : this.mHeaders.keySet()) {
            httpURLConnection.setRequestProperty(str, (String) this.mHeaders.get(str));
        }
        if (!TextUtils.isEmpty(this.mRequestBody)) {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream(), DEFAULT_CHARSET));
            bufferedWriter.write(this.mRequestBody);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        if (this.mMultipartEntity != null) {
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(this.mMultipartEntity.getContentLength()));
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpURLConnection.getOutputStream());
            bufferedOutputStream.write(this.mMultipartEntity.getOutputStream().toByteArray());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        }
        return httpURLConnection;
    }

    public HttpURLConnectionBuilder setBasicAuthorization(String str, String str2) {
        setHeader("Authorization", "Basic " + Base64.encodeToString((str + ":" + str2).getBytes(), 2));
        return this;
    }

    public HttpURLConnectionBuilder setHeader(String str, String str2) {
        this.mHeaders.put(str, str2);
        return this;
    }

    public HttpURLConnectionBuilder setRequestBody(String str) {
        this.mRequestBody = str;
        return this;
    }

    public HttpURLConnectionBuilder setRequestMethod(String str) {
        this.mRequestMethod = str;
        return this;
    }

    public HttpURLConnectionBuilder setTimeout(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Timeout has to be positive.");
        }
        this.mTimeout = i;
        return this;
    }

    public HttpURLConnectionBuilder writeFormFields(Map<String, String> map) {
        try {
            String formString = getFormString(map, DEFAULT_CHARSET);
            setHeader("Content-Type", "application/x-www-form-urlencoded");
            setRequestBody(formString);
            return this;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public HttpURLConnectionBuilder writeMultipartData(Map<String, String> map, Context context, List<Uri> list) {
        try {
            this.mMultipartEntity = new SimpleMultipartEntity();
            this.mMultipartEntity.writeFirstBoundaryIfNeeds();
            for (String str : map.keySet()) {
                this.mMultipartEntity.addPart(str, (String) map.get(str));
            }
            int i = 0;
            while (i < list.size()) {
                Uri uri = (Uri) list.get(i);
                boolean z = i == list.size() + -1;
                this.mMultipartEntity.addPart("attachment" + i, uri.getLastPathSegment(), context.getContentResolver().openInputStream(uri), z);
                i++;
            }
            this.mMultipartEntity.writeLastBoundaryIfNeeds();
            setHeader("Content-Type", "multipart/form-data; boundary=" + this.mMultipartEntity.getBoundary());
            return this;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
