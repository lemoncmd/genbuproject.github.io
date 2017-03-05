package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.BlockingApiRequestResultReceiver;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;

public class GetTicketOperation extends ServiceOperation {
    public GetTicketOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager) {
        super(context, bundle, authenticatorAccountManager, ticketManager);
    }

    public Bundle call() throws AccountNotFoundException, InvalidResponseException, NetworkException, StsException, InternalException {
        String string = getParameters().getString(BundleMarshaller.UserCidKey);
        Strings.verifyArgumentNotNullOrEmpty(string, BundleMarshaller.UserCidKey);
        AuthenticatorUserAccount accountByCid = getAccountManager().getAccountByCid(string);
        if (accountByCid == null) {
            throw new AccountNotFoundException();
        }
        ISecurityScope scopeFromBundle = BundleMarshaller.scopeFromBundle(getParameters());
        String string2 = getParameters().getString(BundleMarshaller.CobrandingIdKey);
        boolean z = getParameters().getBoolean(BundleMarshaller.WebFlowTelemetryRequestedKey);
        ResultReceiver anonymousClass1 = new BlockingApiRequestResultReceiver<Ticket>() {
            protected void onSuccess(ApiResult apiResult) {
                setResult(apiResult.getTicket());
            }
        };
        getContext().startService(new TicketManager(getContext()).createTicketRequest(accountByCid.getPuid(), scopeFromBundle, getCallingPackage(), string2, getCallerStateBundle()).setIsWebFlowTelemetryRequested(z).setIsSdkRequest(true).setResultReceiver(anonymousClass1).asIntent());
        try {
            SsoResponse blockForResult = anonymousClass1.blockForResult();
            if (blockForResult.hasData()) {
                return BundleMarshaller.ticketToBundle((Ticket) blockForResult.getData());
            }
            if (blockForResult.hasPendingIntent()) {
                return BundleMarshaller.pendingIntentToBundle(blockForResult.getPendingIntent());
            }
            Assertion.check(false, "GetTicketOperation did not receive an expected result from MsaService.");
            throw new InternalException("GetTicketOperation did not receive an expected result from MsaService.");
        } catch (Exception e) {
            Assertion.check(false, "Unexpected UserCancelledException caught in GetTicketOperation.");
            return BundleMarshaller.exceptionToBundle(e);
        } catch (Exception e2) {
            return BundleMarshaller.exceptionToBundle(e2);
        }
    }
}
