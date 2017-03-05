package com.microsoft.onlineid.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.util.ArrayList;

public class ActivityResultSender {
    protected final Activity _activity;
    protected final Bundle _bundle = new Bundle();
    protected int _resultCode;

    public enum ResultType {
        Account("account"),
        Ticket("ticket");
        
        private final String _value;

        private ResultType(String str) {
            this._value = str;
        }

        public static ResultType fromString(String str) {
            for (ResultType resultType : values()) {
                if (resultType.getValue().equals(str)) {
                    return resultType;
                }
            }
            return null;
        }

        public String getValue() {
            return this._value;
        }
    }

    public ActivityResultSender(Activity activity, ResultType resultType) {
        this._activity = activity;
        this._bundle.putString(BundleMarshaller.ActivityResultTypeKey, resultType.getValue());
        this._bundle.putBundle(BundleMarshaller.ClientStateBundleKey, this._activity.getIntent().getBundleExtra(BundleMarshaller.ClientStateBundleKey));
        this._resultCode = 0;
        set();
    }

    private ActivityResultSender putWebFlowTelemetryFields(ArrayList<String> arrayList, boolean z) {
        this._bundle.putStringArrayList(BundleMarshaller.WebFlowTelemetryEventsKey, arrayList);
        return putWereAllWebFlowTelemetryEventsCaptured(z);
    }

    public ActivityResultSender putException(Exception exception) {
        this._bundle.putAll(BundleMarshaller.exceptionToBundle(exception));
        this._resultCode = -1;
        return this;
    }

    public ActivityResultSender putLimitedUserAccount(AuthenticatorUserAccount authenticatorUserAccount) {
        this._bundle.putAll(BundleMarshaller.limitedUserAccountToBundle(authenticatorUserAccount));
        this._resultCode = -1;
        return this;
    }

    public ActivityResultSender putSignedOutCid(String str, boolean z) {
        this._bundle.putString(BundleMarshaller.UserCidKey, str);
        this._bundle.putBoolean(BundleMarshaller.IsSignedOutOfThisAppOnlyKey, z);
        return putException(new AccountNotFoundException("The account was signed out."));
    }

    public ActivityResultSender putTicket(Ticket ticket) {
        this._bundle.putAll(BundleMarshaller.ticketToBundle(ticket));
        this._resultCode = -1;
        return this;
    }

    public ActivityResultSender putWebFlowTelemetryFields(ApiResult apiResult) {
        return putWebFlowTelemetryFields(apiResult.getWebFlowTelemetryEvents(), apiResult.getWereAllWebFlowTelemetryEventsCaptured());
    }

    public ActivityResultSender putWereAllWebFlowTelemetryEventsCaptured(boolean z) {
        this._bundle.putBoolean(BundleMarshaller.WebFlowTelemetryAllEventsCapturedKey, z);
        return this;
    }

    public void set() {
        this._activity.setResult(this._resultCode, new Intent().putExtras(this._bundle));
    }
}
