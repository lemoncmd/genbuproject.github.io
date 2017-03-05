package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.exception.AuthenticationException;

public class RetrieveBackupRequest extends SingleSsoRequest<Bundle> {
    public RetrieveBackupRequest(Context context) {
        super(context, null);
    }

    public Bundle performRequestTask() throws RemoteException, AuthenticationException {
        Bundle retrieveBackup = this._msaSsoService.retrieveBackup(getDefaultCallingParams());
        SingleSsoRequest.checkForErrors(retrieveBackup);
        return retrieveBackup;
    }
}
