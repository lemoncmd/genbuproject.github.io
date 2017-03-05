package com.microsoft.onlineid.sts.request;

import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.sts.response.AbstractSoapResponse;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractTokenRequest<ResponseType extends AbstractSoapResponse> extends AbstractSoapRequest<ResponseType> {
    private Element appendTokenRequestElement(Element element, ISecurityScope iSecurityScope) {
        Object appendElement = Requests.appendElement(element, "wst:RequestSecurityToken");
        appendElement.setAttribute("xmlns:wst", AbstractSoapRequest.WstNamespace);
        Requests.appendElement(appendElement, "wst:RequestType", "http://schemas.xmlsoap.org/ws/2005/02/trust/Issue");
        Node appendElement2 = Requests.appendElement(appendElement, "wsp:AppliesTo");
        appendElement2.setAttribute("xmlns:wsp", AbstractSoapRequest.WspNamespace);
        appendElement2 = Requests.appendElement(appendElement2, "wsa:EndpointReference");
        appendElement2.setAttribute("xmlns:wsa", AbstractSoapRequest.WsaNamespace);
        Requests.appendElement(appendElement2, "wsa:Address", iSecurityScope.getTarget());
        Object policy = iSecurityScope.getPolicy();
        if (!TextUtils.isEmpty(policy)) {
            Element appendElement3 = Requests.appendElement(appendElement, "wsp:PolicyReference");
            appendElement3.setAttribute("xmlns:wsp", AbstractSoapRequest.WspNamespace);
            appendElement3.setAttribute("URI", policy);
        }
        return appendElement;
    }

    protected void buildAuthInfo(Element element) {
        super.buildAuthInfo(element);
        Requests.appendElement(element, "ps:HostingApp", AbstractSoapRequest.MsaAppGuid);
    }

    protected void buildSoapBody(Element element) {
        List<ISecurityScope> requestedScopes = getRequestedScopes();
        if (requestedScopes.size() > 1) {
            element = Requests.appendElement(element, "ps:RequestMultipleSecurityTokens");
            element.setAttribute("xmlns:ps", AbstractSoapRequest.PSNamespace);
            element.setAttribute("Id", "RSTS");
            if (this instanceof ISignableRequest) {
                ((ISignableRequest) this).getXmlSigner().addElementToSign(element);
            }
        }
        int i = 0;
        for (ISecurityScope appendTokenRequestElement : requestedScopes) {
            appendTokenRequestElement(element, appendTokenRequestElement).setAttribute("Id", "RST" + i);
            i++;
        }
    }

    protected abstract List<ISecurityScope> getRequestedScopes();
}
