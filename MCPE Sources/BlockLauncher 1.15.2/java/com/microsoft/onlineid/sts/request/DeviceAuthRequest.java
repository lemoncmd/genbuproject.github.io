package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.DeviceCredentials;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.response.DeviceAuthResponse;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DeviceAuthRequest extends AbstractTokenRequest<DeviceAuthResponse> {
    private DeviceCredentials _credentials;

    protected void buildSecurityNode(Element element) {
        Node appendElement = Requests.appendElement(element, "wsse:UsernameToken");
        appendElement.setAttribute("wsu:Id", "devicesoftware");
        Requests.appendElement(appendElement, "wsse:Username", this._credentials.getUsername());
        Requests.appendElement(appendElement, "wsse:Password", this._credentials.getPassword());
        appendTimestamp(element);
    }

    public Endpoint getEndpoint() {
        return Endpoint.Sts;
    }

    protected final List<ISecurityScope> getRequestedScopes() {
        return Collections.singletonList(DAToken.Scope);
    }

    public DeviceAuthResponse instantiateResponse() {
        return new DeviceAuthResponse(getClockSkewManager());
    }

    public void setDeviceCredentials(DeviceCredentials deviceCredentials) {
        this._credentials = deviceCredentials;
    }
}
