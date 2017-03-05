package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.response.AbstractSoapResponse;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractSoapRequest<ResponseType extends AbstractSoapResponse> extends AbstractStsRequest<ResponseType> {
    public static final String MsaAppGuid = "{F501FD64-9070-46AB-993C-6F7B71D8D883}";
    public static final String PSNamespace = "http://schemas.microsoft.com/Passport/SoapServices/PPCRL";
    public static final String PsfNamespace = "http://schemas.microsoft.com/Passport/SoapServices/SOAPFault";
    private static final int RequestExpiryMilliseconds = 300000;
    public static final String SamlNamespace = "urn:oasis:names:tc:SAML:1.0:assertion";
    public static final String SoapNamespace = "http://www.w3.org/2003/05/soap-envelope";
    public static final String WsaNamespace = "http://www.w3.org/2005/08/addressing";
    public static final String WspNamespace = "http://schemas.xmlsoap.org/ws/2004/09/policy";
    public static final String WsscNamespace = "http://schemas.xmlsoap.org/ws/2005/02/sc";
    public static final String WsseNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    public static final String WstNamespace = "http://schemas.xmlsoap.org/ws/2005/02/trust";
    public static final String WsuNamespace = "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";

    protected void addEnvelopeNamespaces(Element element) {
        element.setAttribute("xmlns:ps", PSNamespace);
        element.setAttribute("xmlns:wsse", WsseNamespace);
        element.setAttribute("xmlns:saml", SamlNamespace);
        element.setAttribute("xmlns:wsp", WspNamespace);
        element.setAttribute("xmlns:wsu", WsuNamespace);
        element.setAttribute("xmlns:wsa", WsaNamespace);
        element.setAttribute("xmlns:wssc", WsscNamespace);
        element.setAttribute("xmlns:wst", WstNamespace);
    }

    protected final void appendDeviceDAToken(Element element, DAToken dAToken) {
        Element appendElement = Requests.appendElement(element, "wsse:BinarySecurityToken", dAToken.getOneTimeSignedCredential(getClockSkewManager().getCurrentServerTime(), MsaAppGuid));
        appendElement.setAttribute("ValueType", "urn:liveid:sha1device");
        appendElement.setAttribute("Id", "DeviceDAToken");
    }

    protected final void appendTimestamp(Element element) {
        Object appendElement = Requests.appendElement(element, "wsu:Timestamp");
        appendElement.setAttribute("wsu:Id", "Timestamp");
        appendElement.setAttribute("xmlns:wsu", WsuNamespace);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date currentServerTime = getClockSkewManager().getCurrentServerTime();
        Date date = new Date(300000 + currentServerTime.getTime());
        Requests.appendElement(appendElement, "wsu:Created", simpleDateFormat.format(currentServerTime));
        Requests.appendElement(appendElement, "wsu:Expires", simpleDateFormat.format(date));
        if (this instanceof ISignableRequest) {
            ((ISignableRequest) this).getXmlSigner().addElementToSign(appendElement);
        }
    }

    protected void buildAuthInfo(Element element) {
        Requests.appendElement(element, "ps:BinaryVersion", AbstractStsRequest.StsBinaryVersion);
        Requests.appendElement(element, "ps:DeviceType", AbstractStsRequest.DeviceType);
    }

    public Document buildRequest() {
        Document createBlankDocument = createBlankDocument(SoapNamespace, "s:Envelope");
        Node documentElement = createBlankDocument.getDocumentElement();
        addEnvelopeNamespaces(documentElement);
        buildSoapHeader(Requests.appendElement(documentElement, "s:Header"));
        buildSoapBody(Requests.appendElement(documentElement, "s:Body"));
        if (this instanceof ISignableRequest) {
            ISignableRequest iSignableRequest = (ISignableRequest) this;
            iSignableRequest.getXmlSigner().sign(iSignableRequest);
        }
        return createBlankDocument;
    }

    protected abstract void buildSecurityNode(Element element);

    protected abstract void buildSoapBody(Element element);

    protected void buildSoapHeader(Element element) {
        Requests.appendElement(element, "wsa:Action", "http://schemas.xmlsoap.org/ws/2005/02/trust/RST/Issue").setAttribute("s:mustUnderstand", XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        Requests.appendElement(element, "wsa:To", getDestination().toString()).setAttribute("s:mustUnderstand", XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        Requests.appendElement(element, "wsa:MessageID", String.valueOf(System.currentTimeMillis()));
        Element appendElement = Requests.appendElement(element, "ps:AuthInfo");
        appendElement.setAttribute("xmlns:ps", PSNamespace);
        appendElement.setAttribute("Id", "PPAuthInfo");
        buildAuthInfo(appendElement);
        if (this instanceof ISignableRequest) {
            ((ISignableRequest) this).getXmlSigner().addElementToSign(appendElement);
        }
        buildSecurityNode(Requests.appendElement(element, "wsse:Security"));
    }
}
