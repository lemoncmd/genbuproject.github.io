package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.SignInOptions;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class GetSignInIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final SignInOptions _signInOptions;

    public GetSignInIntentRequest(Context context, Bundle bundle, SignInOptions signInOptions, OnlineIdConfiguration onlineIdConfiguration) {
        super(context, bundle);
        this._signInOptions = signInOptions;
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle defaultCallingParams = getDefaultCallingParams();
        if (this._signInOptions != null) {
            defaultCallingParams.putAll(this._signInOptions.asBundle());
        }
        if (this._onlineIdConfiguration != null) {
            defaultCallingParams.putAll(BundleMarshaller.onlineIdConfigurationToBundle(this._onlineIdConfiguration));
        }
        defaultCallingParams = this._msaSsoService.getSignInIntent(defaultCallingParams);
        SingleSsoRequest.checkForErrors(defaultCallingParams);
        return BundleMarshaller.pendingIntentFromBundle(defaultCallingParams);
    }
}
