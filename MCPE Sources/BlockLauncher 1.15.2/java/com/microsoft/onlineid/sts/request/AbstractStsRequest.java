package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.analytics.ITimedAnalyticsEvent;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.log.RedactableXml;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.response.AbstractStsResponse;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URL;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

public abstract class AbstractStsRequest<ResponseType extends AbstractStsResponse> {
    public static final String AppIdentifier = "MSAAndroidApp";
    public static final String DeviceType = "Android";
    public static final String StsBinaryVersion = "11";
    private ClockSkewManager _clockSkewManager;
    private URL _destination;
    private int _msaAppVersionCode;
    private TransportFactory _transportFactory;

    private void updateClockSkew(long j) {
        if (j != 0) {
            getClockSkewManager().onTimestampReceived(j);
            ClientAnalytics.get().logClockSkew(getClockSkewManager().getSkewMilliseconds());
        }
    }

    public abstract Document buildRequest();

    protected final Document createBlankDocument(String str, String str2) {
        DocumentBuilderFactory newInstance = DocumentBuilderFactory.newInstance();
        newInstance.setNamespaceAware(true);
        try {
            return newInstance.newDocumentBuilder().getDOMImplementation().createDocument(str, str2, null);
        } catch (Throwable e) {
            Assertion.check(false);
            throw new RuntimeException("Invalid parser configuration.", e);
        }
    }

    protected String getAnalyticsRequestType() {
        return "(none)";
    }

    protected ClockSkewManager getClockSkewManager() {
        return this._clockSkewManager;
    }

    public URL getDestination() {
        return this._destination;
    }

    public abstract Endpoint getEndpoint();

    public int getMsaAppVersionCode() {
        return this._msaAppVersionCode;
    }

    protected abstract ResponseType instantiateResponse();

    public ResponseType send() throws NetworkException, InvalidResponseException {
        ResponseType instantiateResponse = instantiateResponse();
        Transport createTransport = this._transportFactory.createTransport();
        createTransport.openPostRequest(getDestination());
        OutputStream requestStream = createTransport.getRequestStream();
        ITimedAnalyticsEvent createTimedEvent = ClientAnalytics.get().createTimedEvent(ClientAnalytics.StsRequestCategory, getClass().getSimpleName(), getAnalyticsRequestType());
        createTimedEvent.start();
        try {
            Transformer newTransformer = TransformerFactory.newInstance().newTransformer();
            if (Settings.isDebugBuild()) {
                Writer charArrayWriter = new CharArrayWriter();
                newTransformer.transform(new DOMSource(buildRequest()), new StreamResult(charArrayWriter));
                String charArrayWriter2 = charArrayWriter.toString();
                Logger.info(new RedactableXml(String.format(Locale.US, "%s: %s", new Object[]{getClass().getSimpleName(), charArrayWriter2}), new String[0]));
                requestStream.write(charArrayWriter2.getBytes(Strings.Utf8Charset));
            } else {
                newTransformer.transform(new DOMSource(buildRequest()), new StreamResult(requestStream));
            }
            requestStream.close();
            InputStream responseStream = createTransport.getResponseStream();
            updateClockSkew(createTransport.getResponseDate());
            try {
                instantiateResponse.parse(responseStream);
                createTimedEvent.end();
                responseStream.close();
                createTransport.closeConnection();
                return instantiateResponse;
            } catch (Throwable e) {
                Logger.error("Unable to parse stream.", e);
                throw new NetworkException("Unable to parse stream.", e);
            } catch (Throwable th) {
                createTimedEvent.end();
                responseStream.close();
            }
        } catch (Throwable e2) {
            Logger.error("Unable to configure Transformer", e2);
            throw new RuntimeException("Unable to configure Transformer", e2);
        } catch (Throwable e22) {
            Logger.error("Problem occurred transforming XML document", e22);
            throw new RuntimeException("Problem occurred transforming XML document", e22);
        } catch (Throwable e222) {
            Logger.error("Unable to close stream", e222);
            throw new NetworkException("Unable to close stream", e222);
        } catch (Throwable th2) {
            createTransport.closeConnection();
        }
    }

    public void setClockSkewManager(ClockSkewManager clockSkewManager) {
        this._clockSkewManager = clockSkewManager;
    }

    public void setDestination(URL url) {
        this._destination = url;
    }

    public void setMsaAppVersionCode(int i) {
        this._msaAppVersionCode = i;
    }

    void setTransportFactory(TransportFactory transportFactory) {
        this._transportFactory = transportFactory;
    }
}
