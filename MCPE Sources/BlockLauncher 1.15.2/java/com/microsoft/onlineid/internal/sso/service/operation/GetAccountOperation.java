package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.util.Collection;

public class GetAccountOperation extends ServiceOperation {
    public GetAccountOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager) {
        super(context, bundle, authenticatorAccountManager, ticketManager);
    }

    public Bundle call() {
        if (!getAccountManager().hasAccounts()) {
            return new GetSignInIntentOperation(getContext(), getParameters(), getAccountManager(), getTicketManager()).call();
        }
        Collection accounts = getAccountManager().getAccounts();
        return accounts.size() == 1 ? BundleMarshaller.limitedUserAccountToBundle((AuthenticatorUserAccount) accounts.iterator().next()) : new GetAccountPickerOperation(getContext(), getParameters(), getAccountManager(), getTicketManager()).call();
    }
}
