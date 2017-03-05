package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.util.HashSet;
import java.util.Set;

public class GetAllAccountsRequest extends SingleSsoRequest<Set<AuthenticatorUserAccount>> {
    public GetAllAccountsRequest(Context context, Bundle bundle) {
        super(context, bundle);
    }

    public Set<AuthenticatorUserAccount> performRequestTask() throws RemoteException, AuthenticationException {
        Bundle allAccounts = this._msaSsoService.getAllAccounts(getDefaultCallingParams());
        SingleSsoRequest.checkForErrors(allAccounts);
        Set<AuthenticatorUserAccount> hashSet = new HashSet();
        for (Bundle allAccounts2 : allAccounts2.getParcelableArrayList(BundleMarshaller.AllUsersKey)) {
            try {
                hashSet.add(BundleMarshaller.limitedUserAccountFromBundle(allAccounts2));
            } catch (Throwable e) {
                Logger.error("Encountered an error while trying to unbundle accounts.", e);
                ClientAnalytics.get().logException(e);
            }
        }
        return hashSet;
    }
}
