package com.microsoft.onlineid.sts.request;

import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.configuration.Experiment;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.XmlSigner;
import com.microsoft.onlineid.sts.exception.CorruptedUserDATokenException;
import com.microsoft.onlineid.sts.response.ServiceResponse;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import java.util.ArrayList;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ServiceRequest extends AbstractTokenRequest<ServiceResponse> implements ISignableRequest {
    private String _clientAppUri;
    private DAToken _deviceDA;
    private String _flowToken;
    private Element _parentOfSignatureNode;
    private boolean _requestFlights = false;
    protected List<ISecurityScope> _requestedScopes = new ArrayList();
    private XmlSigner _signer;
    private String _telemetry;
    private DAToken _userDA;

    public ServiceRequest() {
        this._requestedScopes.add(DAToken.Scope);
    }

    public void addRequest(ISecurityScope iSecurityScope) {
        boolean z = false;
        if (iSecurityScope == null) {
            throw new IllegalArgumentException("Cannot request a null scope.");
        }
        Assertion.check(this._requestedScopes.size() < 2);
        if (!iSecurityScope.equals(DAToken.Scope)) {
            z = true;
        }
        Assertion.check(z);
        if (!this._requestedScopes.contains(iSecurityScope)) {
            this._requestedScopes.add(iSecurityScope);
        }
    }

    protected void buildAuthInfo(Element element) {
        super.buildAuthInfo(element);
        Requests.appendElement(element, "ps:InlineUX", AbstractStsRequest.DeviceType);
        Requests.appendElement(element, "ps:ConsentFlags", XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        Requests.appendElement(element, "ps:IsConnected", XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        if (this._requestFlights) {
            Requests.appendElement(element, "ps:Experiments", Experiment.getExperimentList());
        }
        if (this._flowToken != null) {
            Requests.appendElement(element, "ps:InlineFT", this._flowToken);
        }
        Requests.appendElement(element, "ps:ClientAppURI", this._clientAppUri);
        if (!TextUtils.isEmpty(this._telemetry)) {
            Requests.appendElement(element, "ps:Telemetry", this._telemetry);
        }
    }

    protected void buildSecurityNode(Element element) {
        try {
            element.appendChild(element.getOwnerDocument().importNode(Requests.xmlStringToElement(this._userDA.getToken()), true));
            appendDeviceDAToken(element, this._deviceDA);
            Node appendElement = Requests.appendElement(element, "wssc:DerivedKeyToken");
            appendElement.setAttribute("wsu:Id", "SignKey");
            appendElement.setAttribute("Algorithm", "urn:liveid:SP800-108CTR-HMAC-SHA256");
            Node appendElement2 = Requests.appendElement(appendElement, "wsse:RequestedTokenReference");
            Requests.appendElement(appendElement2, "wsse:KeyIdentifier").setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/XX/oasis-2004XX-wss-saml-token-profile-1.0#SAMLAssertionID");
            Requests.appendElement(appendElement2, "wsse:Reference").setAttribute("URI", BuildConfig.FLAVOR);
            Requests.appendElement(appendElement, "wssc:Nonce", this._signer.getEncodedNonce());
            appendTimestamp(element);
            this._parentOfSignatureNode = element;
        } catch (Throwable e) {
            throw new CorruptedUserDATokenException("Unable to parse user DAToken blob into XML, possibly corrupt.", e);
        }
    }

    public Endpoint getEndpoint() {
        return Endpoint.Sts;
    }

    public Element getParentOfSignatureNode() {
        return this._parentOfSignatureNode;
    }

    protected List<ISecurityScope> getRequestedScopes() {
        return this._requestedScopes;
    }

    public byte[] getSigningSessionKey() {
        return this._userDA.getSessionKey();
    }

    public XmlSigner getXmlSigner() {
        return this._signer;
    }

    public ServiceResponse instantiateResponse() {
        Assertion.check(getRequestedScopes().size() == 2);
        for (ISecurityScope iSecurityScope : getRequestedScopes()) {
            if (!iSecurityScope.equals(DAToken.Scope)) {
                break;
            }
        }
        ISecurityScope iSecurityScope2 = null;
        return new ServiceResponse(getSigningSessionKey(), iSecurityScope2, getClockSkewManager());
    }

    public void setClientAppUri(String str) {
        this._clientAppUri = str;
    }

    public void setDeviceDA(DAToken dAToken) {
        this._deviceDA = dAToken;
    }

    public void setFlowToken(String str) {
        this._flowToken = str;
    }

    public void setRequestFlights(boolean z) {
        this._requestFlights = z;
    }

    public void setTelemetry(String str) {
        this._telemetry = str;
    }

    public void setUserDA(DAToken dAToken) {
        this._userDA = dAToken;
    }

    public void setXmlSigner(XmlSigner xmlSigner) {
        this._signer = xmlSigner;
    }
}
