package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;

public class GetAccountRequest extends SingleSsoRequest<SsoResponse<AuthenticatorUserAccount>> {
    private final OnlineIdConfiguration _onlineIdConfiguration;

    public GetAccountRequest(Context context, Bundle bundle, OnlineIdConfiguration onlineIdConfiguration) {
        super(context, bundle);
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public SsoResponse<AuthenticatorUserAccount> performRequestTask() throws RemoteException, AuthenticationException {
        Bundle defaultCallingParams = getDefaultCallingParams();
        if (this._onlineIdConfiguration != null) {
            defaultCallingParams.putAll(BundleMarshaller.onlineIdConfigurationToBundle(this._onlineIdConfiguration));
        }
        defaultCallingParams = this._msaSsoService.getAccount(defaultCallingParams);
        SingleSsoRequest.checkForErrors(defaultCallingParams);
        return BundleMarshaller.hasPendingIntent(defaultCallingParams) ? new SsoResponse().setPendingIntent(BundleMarshaller.pendingIntentFromBundle(defaultCallingParams)) : new SsoResponse().setData(BundleMarshaller.limitedUserAccountFromBundle(defaultCallingParams));
    }
}
