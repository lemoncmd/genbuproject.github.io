package com.microsoft.xbox.toolkit.network;

import com.microsoft.xbox.toolkit.XLEException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

public abstract class AbstractXLEHttpClient {
    protected abstract HttpResponse execute(HttpUriRequest httpUriRequest) throws ClientProtocolException, IOException;

    public XLEHttpStatusAndStream getHttpStatusAndStreamInternal(HttpUriRequest httpUriRequest, boolean z) throws XLEException {
        XLEHttpStatusAndStream xLEHttpStatusAndStream = new XLEHttpStatusAndStream();
        try {
            HttpResponse execute = execute(httpUriRequest);
            if (!(execute == null || execute.getStatusLine() == null)) {
                xLEHttpStatusAndStream.statusLine = execute.getStatusLine().toString();
                xLEHttpStatusAndStream.statusCode = execute.getStatusLine().getStatusCode();
            }
            if (!(execute == null || execute.getLastHeader("Location") == null)) {
                xLEHttpStatusAndStream.redirectUrl = execute.getLastHeader("Location").getValue();
            }
            if (execute != null) {
                xLEHttpStatusAndStream.headers = execute.getAllHeaders();
            }
            HttpEntity entity = execute == null ? null : execute.getEntity();
            if (entity != null) {
                xLEHttpStatusAndStream.stream = new ByteArrayInputStream(EntityUtils.toByteArray(entity));
                entity.consumeContent();
                Header firstHeader = execute.getFirstHeader("Content-Encoding");
                if (firstHeader != null && firstHeader.getValue().equalsIgnoreCase("gzip")) {
                    xLEHttpStatusAndStream.stream = new GZIPInputStream(xLEHttpStatusAndStream.stream);
                }
            }
            return xLEHttpStatusAndStream;
        } catch (Throwable e) {
            httpUriRequest.abort();
            if (!(xLEHttpStatusAndStream == null || xLEHttpStatusAndStream.stream == null)) {
                xLEHttpStatusAndStream.close();
            }
            throw new XLEException(4, e);
        }
    }
}
