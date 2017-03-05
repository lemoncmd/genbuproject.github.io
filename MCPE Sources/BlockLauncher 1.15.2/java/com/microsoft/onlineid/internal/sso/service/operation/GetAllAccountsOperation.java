package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.util.ArrayList;

public class GetAllAccountsOperation extends ServiceOperation {
    public GetAllAccountsOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager) {
        super(context, bundle, authenticatorAccountManager, ticketManager);
    }

    public Bundle call() {
        ArrayList arrayList = new ArrayList();
        for (AuthenticatorUserAccount limitedUserAccountToBundle : getAccountManager().getAccounts()) {
            arrayList.add(BundleMarshaller.limitedUserAccountToBundle(limitedUserAccountToBundle));
        }
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(BundleMarshaller.AllUsersKey, arrayList);
        return bundle;
    }
}
