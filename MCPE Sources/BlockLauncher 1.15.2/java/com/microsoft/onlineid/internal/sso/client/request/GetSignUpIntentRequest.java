package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class GetSignUpIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final SignUpOptions _signUpOptions;

    public GetSignUpIntentRequest(Context context, Bundle bundle, SignUpOptions signUpOptions, OnlineIdConfiguration onlineIdConfiguration) {
        super(context, bundle);
        this._signUpOptions = signUpOptions;
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle defaultCallingParams = getDefaultCallingParams();
        if (this._signUpOptions != null) {
            defaultCallingParams.putAll(this._signUpOptions.asBundle());
        }
        if (this._onlineIdConfiguration != null) {
            defaultCallingParams.putAll(BundleMarshaller.onlineIdConfigurationToBundle(this._onlineIdConfiguration));
        }
        defaultCallingParams = this._msaSsoService.getSignUpIntent(defaultCallingParams);
        SingleSsoRequest.checkForErrors(defaultCallingParams);
        return BundleMarshaller.pendingIntentFromBundle(defaultCallingParams);
    }
}
