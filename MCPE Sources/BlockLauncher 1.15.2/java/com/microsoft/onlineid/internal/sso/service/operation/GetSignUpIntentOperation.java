package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.ui.AddAccountActivity;

public class GetSignUpIntentOperation extends ServiceOperation {
    public GetSignUpIntentOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager) {
        super(context, bundle, authenticatorAccountManager, ticketManager);
    }

    public Bundle call() {
        return BundleMarshaller.pendingIntentToBundle(getPendingIntentBuilder(AddAccountActivity.getSignUpIntent(getContext(), new SignUpOptions(getParameters()), getParameters().getString(BundleMarshaller.PreferredMembernameTypeKey), getParameters().getString(BundleMarshaller.CobrandingIdKey), getParameters().getBoolean(BundleMarshaller.WebFlowTelemetryRequestedKey), getCallingPackage(), getCallerStateBundle())).setContext(getContext()).buildActivity());
    }
}
