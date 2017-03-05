package com.microsoft.onlineid.internal.sso.client.request;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;

public class GetTicketRequest extends SingleSsoRequest<SsoResponse<Ticket>> {
    private final String _cid;
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final ISecurityScope _securityScope;

    public GetTicketRequest(Context context, Bundle bundle, String str, ISecurityScope iSecurityScope, OnlineIdConfiguration onlineIdConfiguration) {
        super(context, bundle);
        this._cid = str;
        this._securityScope = iSecurityScope;
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public SsoResponse<Ticket> performRequestTask() throws RemoteException, AuthenticationException {
        Bundle defaultCallingParams = getDefaultCallingParams();
        defaultCallingParams.putString(BundleMarshaller.UserCidKey, this._cid);
        defaultCallingParams.putAll(BundleMarshaller.scopeToBundle(this._securityScope));
        if (this._onlineIdConfiguration != null) {
            defaultCallingParams.putAll(BundleMarshaller.onlineIdConfigurationToBundle(this._onlineIdConfiguration));
        }
        defaultCallingParams = this._msaSsoService.getTicket(defaultCallingParams);
        SingleSsoRequest.checkForErrors(defaultCallingParams);
        return BundleMarshaller.hasPendingIntent(defaultCallingParams) ? new SsoResponse().setPendingIntent(BundleMarshaller.pendingIntentFromBundle(defaultCallingParams)) : new SsoResponse().setData(BundleMarshaller.ticketFromBundle(defaultCallingParams));
    }
}
