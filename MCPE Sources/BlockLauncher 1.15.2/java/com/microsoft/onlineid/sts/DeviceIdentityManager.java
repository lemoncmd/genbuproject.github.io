package com.microsoft.onlineid.sts;

import android.content.Context;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.RequestThrottledException;
import com.microsoft.onlineid.sts.exception.StsException;
import com.microsoft.onlineid.sts.request.DeviceProvisionRequest;
import com.microsoft.onlineid.sts.request.StsRequestFactory;
import com.microsoft.onlineid.sts.response.DeviceAuthResponse;
import com.microsoft.onlineid.sts.response.DeviceProvisionResponse;
import org.mozilla.javascript.regexp.NativeRegExp;

public class DeviceIdentityManager {
    static final int MaxProvisionAttemptsPerCall = 3;
    private final Context _applicationContext;
    private DeviceCredentialGenerator _credentialGenerator;
    private StsRequestFactory _requestFactory;
    private final TypedStorage _storage;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode = new int[StsErrorCode.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_K_ERROR_DB_MEMBER_DOES_NOT_EXIST.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PPCRL_REQUEST_E_BAD_MEMBER_NAME_OR_PASSWORD.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[StsErrorCode.PP_E_K_ERROR_DB_MEMBER_EXISTS.ordinal()] = DeviceIdentityManager.MaxProvisionAttemptsPerCall;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public DeviceIdentityManager(Context context) {
        this._applicationContext = context;
        this._storage = new TypedStorage(context);
        this._requestFactory = null;
        this._credentialGenerator = null;
    }

    DeviceIdentityManager(TypedStorage typedStorage, DeviceCredentialGenerator deviceCredentialGenerator, StsRequestFactory stsRequestFactory) {
        this._applicationContext = null;
        this._storage = typedStorage;
        this._credentialGenerator = deviceCredentialGenerator;
        this._requestFactory = stsRequestFactory;
    }

    private boolean checkProvisionResponse(int i, DeviceProvisionResponse deviceProvisionResponse) throws RequestThrottledException, StsException {
        if (deviceProvisionResponse.succeeded()) {
            return true;
        }
        StsError error = deviceProvisionResponse.getError();
        switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[error.getCode().ordinal()]) {
            case NativeRegExp.PREFIX /*2*/:
            case MaxProvisionAttemptsPerCall /*3*/:
                if (i == MaxProvisionAttemptsPerCall) {
                    Logger.error("provisionNewDevice() exceeded allowable number of retry attempts.");
                    throw new RequestThrottledException("provisionNewDevice() exceeded allowable number of retry attempts.");
                }
                Logger.warning("Device provision request failed due to invalid credentials. Trying again.");
                return false;
            default:
                throw new StsException("Unable to provision device", error);
        }
    }

    private DeviceCredentialGenerator getCredentialGenerator() {
        if (this._credentialGenerator == null) {
            this._credentialGenerator = new DeviceCredentialGenerator();
        }
        return this._credentialGenerator;
    }

    private StsRequestFactory getRequestFactory() {
        if (this._requestFactory == null) {
            this._requestFactory = new StsRequestFactory(this._applicationContext);
        }
        return this._requestFactory;
    }

    private DeviceIdentity provisionNewDevice() throws NetworkException, InvalidResponseException, StsException {
        this._storage.deleteDeviceIdentity();
        int i = 1;
        DeviceProvisionRequest deviceProvisionRequest = null;
        while (i <= MaxProvisionAttemptsPerCall) {
            DeviceProvisionRequest createDeviceProvisionRequest;
            DeviceCredentials generate = getCredentialGenerator().generate();
            if (deviceProvisionRequest == null) {
                createDeviceProvisionRequest = getRequestFactory().createDeviceProvisionRequest(generate);
            } else {
                deviceProvisionRequest.setDeviceCredentials(generate);
                createDeviceProvisionRequest = deviceProvisionRequest;
            }
            DeviceProvisionResponse deviceProvisionResponse = (DeviceProvisionResponse) createDeviceProvisionRequest.send();
            if (checkProvisionResponse(i, deviceProvisionResponse)) {
                DeviceIdentity deviceIdentity = new DeviceIdentity(generate, deviceProvisionResponse.getPuid(), null);
                this._storage.writeDeviceIdentity(deviceIdentity);
                return deviceIdentity;
            }
            i++;
            deviceProvisionRequest = createDeviceProvisionRequest;
        }
        return null;
    }

    public DeviceIdentity getDeviceIdentity(boolean z) throws NetworkException, InvalidResponseException, StsException {
        DeviceIdentity readDeviceIdentity = this._storage.readDeviceIdentity();
        if (readDeviceIdentity != null && readDeviceIdentity.getDAToken() != null && !z) {
            return readDeviceIdentity;
        }
        DeviceAuthResponse deviceAuthResponse;
        if (readDeviceIdentity != null) {
            deviceAuthResponse = (DeviceAuthResponse) getRequestFactory().createDeviceAuthRequest(readDeviceIdentity).send();
            if (deviceAuthResponse.succeeded()) {
                readDeviceIdentity.setDAToken(deviceAuthResponse.getDAToken());
                this._storage.writeDeviceIdentity(readDeviceIdentity);
                return readDeviceIdentity;
            }
            StsError error = deviceAuthResponse.getError();
            switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$StsErrorCode[error.getCode().ordinal()]) {
                case NativeRegExp.MATCH /*1*/:
                case NativeRegExp.PREFIX /*2*/:
                    break;
                default:
                    throw new StsException("Failed to authenticate device", error);
            }
        }
        readDeviceIdentity = provisionNewDevice();
        deviceAuthResponse = (DeviceAuthResponse) getRequestFactory().createDeviceAuthRequest(readDeviceIdentity).send();
        if (deviceAuthResponse.succeeded()) {
            readDeviceIdentity.setDAToken(deviceAuthResponse.getDAToken());
            this._storage.writeDeviceIdentity(readDeviceIdentity);
            return readDeviceIdentity;
        }
        throw new StsException("Failed to authenticate device", deviceAuthResponse.getError());
    }
}
