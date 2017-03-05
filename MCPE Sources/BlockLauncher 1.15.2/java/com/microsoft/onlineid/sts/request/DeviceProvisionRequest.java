package com.microsoft.onlineid.sts.request;

import com.microsoft.onlineid.sts.DeviceCredentials;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.response.DeviceProvisionResponse;
import com.mojang.minecraftpe.MainActivity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DeviceProvisionRequest extends AbstractStsRequest<DeviceProvisionResponse> {
    private DeviceCredentials _credentials;

    public Document buildRequest() {
        Document createBlankDocument = createBlankDocument(null, "DeviceAddRequest");
        Node documentElement = createBlankDocument.getDocumentElement();
        Element appendElement = Requests.appendElement(documentElement, "ClientInfo");
        appendElement.setAttribute("name", AbstractStsRequest.AppIdentifier);
        appendElement.setAttribute("version", MainActivity.HALF_SUPPORT_VERSION);
        documentElement = Requests.appendElement(documentElement, "Authentication");
        Requests.appendElement(documentElement, "Membername", this._credentials.getUsername());
        Requests.appendElement(documentElement, "Password", this._credentials.getPassword());
        return createBlankDocument;
    }

    public Endpoint getEndpoint() {
        return Endpoint.DeviceProvision;
    }

    public DeviceProvisionResponse instantiateResponse() {
        return new DeviceProvisionResponse();
    }

    public void setDeviceCredentials(DeviceCredentials deviceCredentials) {
        this._credentials = deviceCredentials;
    }
}
