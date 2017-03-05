package com.microsoft.onlineid.sts.request;

import android.content.Context;
import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.internal.Applications;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.transport.TransportFactory;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.DeviceCredentials;
import com.microsoft.onlineid.sts.DeviceIdentity;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.XmlSigner;
import net.hockeyapp.android.BuildConfig;

public class StsRequestFactory {
    protected final Context _applicationContext;
    private final ClockSkewManager _clockSkewManager;

    public StsRequestFactory(Context context) {
        this._applicationContext = context;
        this._clockSkewManager = new ClockSkewManager(context);
    }

    public StsRequestFactory(Context context, ClockSkewManager clockSkewManager) {
        this._applicationContext = context;
        this._clockSkewManager = clockSkewManager;
    }

    private String buildTelemetry() {
        Object installerPackageName = this._applicationContext.getPackageManager().getInstallerPackageName(this._applicationContext.getPackageName());
        return TextUtils.isEmpty(installerPackageName) ? BuildConfig.FLAVOR : "PackageMarket=" + installerPackageName;
    }

    public DeviceAuthRequest createDeviceAuthRequest(DeviceIdentity deviceIdentity) {
        AbstractStsRequest deviceAuthRequest = new DeviceAuthRequest();
        initializeRequest(deviceAuthRequest);
        deviceAuthRequest.setDeviceCredentials(deviceIdentity.getCredentials());
        return deviceAuthRequest;
    }

    public DeviceProvisionRequest createDeviceProvisionRequest(DeviceCredentials deviceCredentials) {
        AbstractStsRequest deviceProvisionRequest = new DeviceProvisionRequest();
        initializeRequest(deviceProvisionRequest);
        deviceProvisionRequest.setDeviceCredentials(deviceCredentials);
        return deviceProvisionRequest;
    }

    public ServiceRequest createServiceRequest(AuthenticatorUserAccount authenticatorUserAccount, DeviceIdentity deviceIdentity, ISecurityScope iSecurityScope, String str, String str2) {
        return createServiceRequest(authenticatorUserAccount, deviceIdentity, iSecurityScope, str, str2, false);
    }

    public ServiceRequest createServiceRequest(AuthenticatorUserAccount authenticatorUserAccount, DeviceIdentity deviceIdentity, ISecurityScope iSecurityScope, String str, String str2, boolean z) {
        Objects.verifyArgumentNotNull(authenticatorUserAccount, "userAccount");
        Objects.verifyArgumentNotNull(deviceIdentity, "deviceIdentity");
        Objects.verifyArgumentNotNull(iSecurityScope, "scope");
        AbstractStsRequest serviceRequest = new ServiceRequest();
        initializeRequest(serviceRequest);
        serviceRequest.setRequestFlights(z);
        serviceRequest.setUserDA(authenticatorUserAccount.getDAToken());
        serviceRequest.setDeviceDA(deviceIdentity.getDAToken());
        serviceRequest.addRequest(iSecurityScope);
        serviceRequest.setFlowToken(str2);
        serviceRequest.setClientAppUri(Applications.buildClientAppUri(this._applicationContext, str));
        serviceRequest.setTelemetry(buildTelemetry());
        return serviceRequest;
    }

    public ServiceRequest createServiceRequest(AuthenticatorUserAccount authenticatorUserAccount, DeviceIdentity deviceIdentity, String str, ISecurityScope iSecurityScope) {
        return createServiceRequest(authenticatorUserAccount, deviceIdentity, iSecurityScope, str, null);
    }

    protected ServerConfig getConfig() {
        return new ServerConfig(this._applicationContext);
    }

    protected void initializeRequest(AbstractStsRequest<?> abstractStsRequest) {
        ServerConfig config = getConfig();
        Endpoint endpoint = abstractStsRequest.getEndpoint();
        Assertion.check(endpoint != null);
        abstractStsRequest.setDestination(config.getUrl(endpoint));
        abstractStsRequest.setTransportFactory(new TransportFactory(this._applicationContext));
        abstractStsRequest.setClockSkewManager(this._clockSkewManager);
        abstractStsRequest.setMsaAppVersionCode(PackageInfoHelper.getCurrentAppVersionCode(this._applicationContext));
        if (abstractStsRequest instanceof ISignableRequest) {
            ((ISignableRequest) abstractStsRequest).setXmlSigner(new XmlSigner());
        }
    }
}
