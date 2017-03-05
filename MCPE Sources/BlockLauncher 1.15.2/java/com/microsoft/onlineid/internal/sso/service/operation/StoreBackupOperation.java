package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.sso.BundleMarshallerException;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;

public class StoreBackupOperation extends ServiceOperation {
    private final TypedStorage _storage;

    public StoreBackupOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager, TypedStorage typedStorage) {
        super(context, bundle, authenticatorAccountManager, ticketManager);
        this._storage = typedStorage;
    }

    public Bundle call() throws BundleMarshallerException {
        this._storage.storeBackup(getParameters());
        return new Bundle();
    }
}
