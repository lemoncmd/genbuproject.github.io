package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;

public class GetSignOutIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final String _cid;

    public GetSignOutIntentRequest(Context context, Bundle bundle, String str) {
        super(context, bundle);
        this._cid = str;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle defaultCallingParams = getDefaultCallingParams();
        defaultCallingParams.putString(BundleMarshaller.UserCidKey, this._cid);
        defaultCallingParams = this._msaSsoService.getSignOutIntent(defaultCallingParams);
        SingleSsoRequest.checkForErrors(defaultCallingParams);
        return BundleMarshaller.pendingIntentFromBundle(defaultCallingParams);
    }
}
