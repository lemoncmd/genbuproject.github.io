package com.microsoft.xbox.service.network.managers;

import android.text.TextUtils;
import com.google.gson.JsonObject;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.TimeMonitor;
import com.microsoft.xbox.toolkit.UrlUtil;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.apache.http.Header;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.mozilla.javascript.Context;

public class ServiceCommon {
    public static final int MaxBIErrorParamLength = 2048;

    public static void AddWebHeaders(HttpUriRequest httpUriRequest, List<Header> list) {
        if (list != null) {
            for (Header addHeader : list) {
                httpUriRequest.addHeader(addHeader);
            }
        }
    }

    private static void ParseHttpResponseForStatus(String str, int i, String str2) throws XLEException {
        ParseHttpResponseForStatus(str, i, str2, null);
    }

    private static void ParseHttpResponseForStatus(String str, int i, String str2, InputStream inputStream) throws XLEException {
        Object obj = (i < Context.VERSION_ES6 || i >= 400) ? null : 1;
        if (obj != null) {
            return;
        }
        if (i == -1) {
            throw new XLEException(3);
        } else if (i == 401 || i == 403) {
            throw new XLEException(XLEErrorCode.NOT_AUTHORIZED);
        } else if (i == 400) {
            if (inputStream == null) {
                throw new XLEException(15);
            }
            throw new XLEException(15, null, null, StreamUtil.ReadAsString(inputStream));
        } else if (i == 500) {
            throw new XLEException(13);
        } else if (i == 503) {
            throw new XLEException(18);
        } else if (i == 404) {
            throw new XLEException(21);
        } else {
            throw new XLEException(4);
        }
    }

    public static boolean delete(String str, List<Header> list) throws XLEException {
        int deleteWithStatus = deleteWithStatus(str, list);
        return deleteWithStatus == Context.VERSION_ES6 || deleteWithStatus == 204;
    }

    public static boolean delete(String str, List<Header> list, String str2) throws XLEException {
        try {
            return JavaUtil.isNullOrEmpty(str2) ? delete(str, list) : delete(str, (List) list, str2.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        } catch (Throwable e) {
            throw new XLEException(5, e);
        }
    }

    public static boolean delete(String str, List<Header> list, byte[] bArr) throws XLEException {
        boolean z = false;
        URI encodedUri = UrlUtil.getEncodedUri(str);
        String uri = encodedUri.toString();
        TimeMonitor timeMonitor = new TimeMonitor();
        HttpUriRequest httpDeleteWithRequestBody = new HttpDeleteWithRequestBody(encodedUri);
        if (bArr != null && bArr.length > 0) {
            try {
                httpDeleteWithRequestBody.setEntity(new ByteArrayEntity(bArr));
            } catch (Throwable e) {
                throw new XLEException(5, e);
            }
        }
        XLEHttpStatusAndStream excuteHttpRequest = excuteHttpRequest(httpDeleteWithRequestBody, uri, list, false, 0);
        if (excuteHttpRequest.statusCode == Context.VERSION_ES6 || excuteHttpRequest.statusCode == 204) {
            z = true;
        }
        excuteHttpRequest.close();
        return z;
    }

    public static int deleteWithStatus(String str, List<Header> list) throws XLEException {
        URI encodedUri = UrlUtil.getEncodedUri(str);
        String uri = encodedUri.toString();
        TimeMonitor timeMonitor = new TimeMonitor();
        XLEHttpStatusAndStream excuteHttpRequest = excuteHttpRequest(new HttpDelete(encodedUri), uri, list, false, 0);
        excuteHttpRequest.close();
        return excuteHttpRequest.statusCode;
    }

    private static XLEHttpStatusAndStream excuteHttpRequest(HttpUriRequest httpUriRequest, String str, List<Header> list, boolean z, int i) throws XLEException {
        return excuteHttpRequest(httpUriRequest, str, list, z, i, false);
    }

    private static XLEHttpStatusAndStream excuteHttpRequest(HttpUriRequest httpUriRequest, String str, List<Header> list, boolean z, int i, boolean z2) throws XLEException {
        AddWebHeaders(httpUriRequest, list);
        XLEHttpStatusAndStream xLEHttpStatusAndStream = new XLEHttpStatusAndStream();
        XLEHttpStatusAndStream httpStatusAndStreamInternal = HttpClientFactory.networkOperationsFactory.getHttpClient(i).getHttpStatusAndStreamInternal(httpUriRequest, true);
        if (z2) {
            ParseHttpResponseForStatus(str, httpStatusAndStreamInternal.statusCode, httpStatusAndStreamInternal.statusLine, httpStatusAndStreamInternal.stream);
        } else {
            try {
                ParseHttpResponseForStatus(str, httpStatusAndStreamInternal.statusCode, httpStatusAndStreamInternal.statusLine);
            } catch (XLEException e) {
                XLEException xLEException = e;
                JsonObject jsonObject = new JsonObject();
                JsonObject jsonObject2 = new JsonObject();
                String str2 = BuildConfig.FLAVOR;
                int i2 = httpStatusAndStreamInternal == null ? 0 : httpStatusAndStreamInternal.statusCode;
                String str3 = BuildConfig.FLAVOR;
                if (httpUriRequest != null) {
                    httpUriRequest.getMethod();
                }
                if (!(httpStatusAndStreamInternal == null || TextUtils.isEmpty(httpStatusAndStreamInternal.statusLine))) {
                    str2 = httpStatusAndStreamInternal.statusLine.length() > MaxBIErrorParamLength ? httpStatusAndStreamInternal.statusLine.substring(0, MaxBIErrorParamLength) : httpStatusAndStreamInternal.statusLine;
                }
                if (!(httpUriRequest == null || httpUriRequest.getURI() == null)) {
                    str3 = httpUriRequest.getURI().toString();
                }
                if (str3.length() > MaxBIErrorParamLength) {
                    str3 = str3.substring(0, MaxBIErrorParamLength);
                }
                jsonObject.addProperty("Request", str3);
                jsonObject2.addProperty("code", Integer.valueOf(i2));
                jsonObject2.addProperty("description", str2);
                jsonObject.add("Response", jsonObject2);
                throw xLEException;
            }
        }
        if (httpStatusAndStreamInternal.stream != null || !z) {
            return httpStatusAndStreamInternal;
        }
        throw new XLEException(7);
    }

    public static XLEHttpStatusAndStream getStreamAndStatus(String str, List<Header> list) throws XLEException {
        XLEHttpStatusAndStream streamAndStatus = getStreamAndStatus(str, list, true, 0);
        return (streamAndStatus == null || JavaUtil.isNullOrEmpty(streamAndStatus.redirectUrl)) ? streamAndStatus : getStreamAndStatus(streamAndStatus.redirectUrl, list);
    }

    private static XLEHttpStatusAndStream getStreamAndStatus(String str, List<Header> list, boolean z, int i) throws XLEException {
        return getStreamAndStatus(str, list, z, i, false);
    }

    private static XLEHttpStatusAndStream getStreamAndStatus(String str, List<Header> list, boolean z, int i, boolean z2) throws XLEException {
        URI encodedUri;
        if (z) {
            encodedUri = UrlUtil.getEncodedUri(str);
        } else {
            try {
                encodedUri = new URI(str);
            } catch (URISyntaxException e) {
                encodedUri = null;
            }
        }
        return excuteHttpRequest(new HttpGet(encodedUri), encodedUri.toString(), list, true, i, z2);
    }

    public static XLEHttpStatusAndStream postStreamWithStatus(String str, List<Header> list, byte[] bArr) throws XLEException {
        URI encodedUri = UrlUtil.getEncodedUri(str);
        String uri = encodedUri.toString();
        HttpUriRequest httpPost = new HttpPost(encodedUri);
        if (bArr != null && bArr.length > 0) {
            try {
                httpPost.setEntity(new ByteArrayEntity(bArr));
            } catch (Throwable e) {
                throw new XLEException(5, e);
            }
        }
        return excuteHttpRequest(httpPost, uri, list, false, 0);
    }

    public static XLEHttpStatusAndStream postStringWithStatus(String str, List<Header> list, String str2) throws XLEException {
        try {
            return postStreamWithStatus(str, list, str2.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        } catch (Throwable e) {
            throw new XLEException(5, e);
        }
    }

    public static XLEHttpStatusAndStream putStreamWithStatus(String str, List<Header> list, byte[] bArr) throws XLEException {
        URI encodedUri = UrlUtil.getEncodedUri(str);
        String uri = encodedUri.toString();
        HttpUriRequest httpPut = new HttpPut(encodedUri);
        if (bArr != null && bArr.length > 0) {
            try {
                httpPut.setEntity(new ByteArrayEntity(bArr));
            } catch (Throwable e) {
                throw new XLEException(5, e);
            }
        }
        return excuteHttpRequest(httpPut, uri, list, false, 0);
    }

    public static XLEHttpStatusAndStream putStringWithStatus(String str, List<Header> list, String str2) throws XLEException {
        try {
            return putStreamWithStatus(str, list, str2.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        } catch (Throwable e) {
            throw new XLEException(5, e);
        }
    }
}
