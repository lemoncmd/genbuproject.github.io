package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.exception.AuthenticationException;

public class StoreBackupRequest extends SingleSsoRequest<Void> {
    private final Bundle _backup;

    public StoreBackupRequest(Context context, Bundle bundle) {
        super(context, null);
        this._backup = bundle;
    }

    public Void performRequestTask() throws RemoteException, AuthenticationException {
        Bundle defaultCallingParams = getDefaultCallingParams();
        defaultCallingParams.putAll(this._backup);
        SingleSsoRequest.checkForErrors(this._msaSsoService.storeBackup(defaultCallingParams));
        return null;
    }
}
