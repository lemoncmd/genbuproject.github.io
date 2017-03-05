package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.ui.SignOutActivity;

public class GetSignOutIntentOperation extends ServiceOperation {
    public GetSignOutIntentOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager) {
        super(context, bundle, authenticatorAccountManager, ticketManager);
    }

    public Bundle call() throws AccountNotFoundException {
        String string = getParameters().getString(BundleMarshaller.UserCidKey);
        Strings.verifyArgumentNotNullOrEmpty(string, BundleMarshaller.UserCidKey);
        AuthenticatorUserAccount accountByCid = getAccountManager().getAccountByCid(string);
        if (accountByCid != null) {
            return BundleMarshaller.pendingIntentToBundle(getPendingIntentBuilder(SignOutActivity.getSignOutIntent(getContext(), accountByCid.getPuid(), accountByCid.getCid(), accountByCid.getUsername(), getCallerStateBundle())).setContext(getContext()).buildActivity());
        }
        throw new AccountNotFoundException("No account was found with the specified ID.");
    }
}
