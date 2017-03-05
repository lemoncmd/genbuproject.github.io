package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.internal.ui.AccountPickerActivity;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import java.util.HashSet;
import java.util.Set;

public class GetAccountPickerOperation extends ServiceOperation {
    public GetAccountPickerOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager) {
        super(context, bundle, authenticatorAccountManager, ticketManager);
    }

    public Bundle call() {
        Object stringArrayList = getParameters().getStringArrayList(BundleMarshaller.CidExclusionListKey);
        String string = getParameters().getString(BundleMarshaller.PreferredMembernameTypeKey);
        String string2 = getParameters().getString(BundleMarshaller.CobrandingIdKey);
        Set hashSet = new HashSet();
        if (stringArrayList != null) {
            hashSet.addAll(stringArrayList);
        }
        return !getAccountManager().getFilteredAccounts(hashSet).isEmpty() ? BundleMarshaller.pendingIntentToBundle(getPendingIntentBuilder(AccountPickerActivity.getAccountPickerIntent(getContext(), stringArrayList, string, string2, getCallingPackage(), getCallerStateBundle())).setContext(getContext()).buildActivity()) : new GetSignInIntentOperation(getContext(), getParameters(), getAccountManager(), getTicketManager()).call();
    }
}
