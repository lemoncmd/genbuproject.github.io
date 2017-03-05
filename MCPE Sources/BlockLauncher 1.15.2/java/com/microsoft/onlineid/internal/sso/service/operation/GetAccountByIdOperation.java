package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;

public class GetAccountByIdOperation extends ServiceOperation {
    public GetAccountByIdOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager) {
        super(context, bundle, authenticatorAccountManager, ticketManager);
    }

    public Bundle call() throws AccountNotFoundException {
        String string = getParameters().getString(BundleMarshaller.UserCidKey);
        Strings.verifyArgumentNotNullOrEmpty(string, BundleMarshaller.UserCidKey);
        AuthenticatorUserAccount accountByCid = getAccountManager().getAccountByCid(string);
        if (accountByCid != null) {
            return BundleMarshaller.limitedUserAccountToBundle(accountByCid);
        }
        throw new AccountNotFoundException("No account was found with the specified ID.");
    }
}
