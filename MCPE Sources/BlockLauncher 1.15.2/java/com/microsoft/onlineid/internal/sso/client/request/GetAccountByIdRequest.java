package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;

public class GetAccountByIdRequest extends SingleSsoRequest<AuthenticatorUserAccount> {
    private final String _cid;

    public GetAccountByIdRequest(Context context, Bundle bundle, String str) {
        super(context, bundle);
        this._cid = str;
    }

    public AuthenticatorUserAccount performRequestTask() throws RemoteException, AuthenticationException {
        Bundle defaultCallingParams = getDefaultCallingParams();
        defaultCallingParams.putString(BundleMarshaller.UserCidKey, this._cid);
        defaultCallingParams = this._msaSsoService.getAccountById(defaultCallingParams);
        SingleSsoRequest.checkForErrors(defaultCallingParams);
        return BundleMarshaller.limitedUserAccountFromBundle(defaultCallingParams);
    }
}
