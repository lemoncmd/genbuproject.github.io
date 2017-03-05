package com.microsoft.onlineid.internal.sso.client.request;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import java.util.ArrayList;

public class GetAccountPickerIntentRequest extends SingleSsoRequest<PendingIntent> {
    private final ArrayList<String> _cidExclusionList;
    private final OnlineIdConfiguration _onlineIdConfiguration;

    public GetAccountPickerIntentRequest(Context context, Bundle bundle, ArrayList<String> arrayList, OnlineIdConfiguration onlineIdConfiguration) {
        super(context, bundle);
        this._cidExclusionList = arrayList;
        this._onlineIdConfiguration = onlineIdConfiguration;
    }

    public PendingIntent performRequestTask() throws RemoteException, AuthenticationException {
        Bundle defaultCallingParams = getDefaultCallingParams();
        defaultCallingParams.putStringArrayList(BundleMarshaller.CidExclusionListKey, this._cidExclusionList);
        if (this._onlineIdConfiguration != null) {
            defaultCallingParams.putAll(BundleMarshaller.onlineIdConfigurationToBundle(this._onlineIdConfiguration));
        }
        defaultCallingParams = this._msaSsoService.getAccountPickerIntent(defaultCallingParams);
        SingleSsoRequest.checkForErrors(defaultCallingParams);
        return BundleMarshaller.pendingIntentFromBundle(defaultCallingParams);
    }
}
