package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import java.util.Locale;

public class ApiRequest {
    protected final Context _applicationContext;
    protected final Intent _intent;

    public enum Extras {
        AccountName,
        AccountPuid,
        ClientPackageName,
        ClientStateBundle,
        CobrandingId,
        Continuation,
        FlowToken,
        IsSdkRequest,
        ResultReceiver,
        Scope,
        WebFlowTelemetryRequested;

        public String getKey() {
            return "com.microsoft.msa.authenticator." + name();
        }
    }

    public ApiRequest(Context context, Intent intent) {
        this._applicationContext = context;
        this._intent = intent;
    }

    private void sendResult(int i, ApiResult apiResult) {
        ResultReceiver resultReceiver = getResultReceiver();
        if (resultReceiver != null) {
            resultReceiver.send(i, apiResult.asBundle());
        }
    }

    public ApiRequest addTicket(Ticket ticket) {
        this._intent.putExtra(getTicketKey(ticket.getScope()), ticket);
        return this;
    }

    public Intent asIntent() {
        return this._intent;
    }

    public void executeAsync() {
        getContext().startService(this._intent);
    }

    public String getAccountName() {
        return this._intent.getStringExtra(Extras.AccountName.getKey());
    }

    public String getAccountPuid() {
        return this._intent.getStringExtra(Extras.AccountPuid.getKey());
    }

    public String getClientPackageName() {
        return this._intent.getStringExtra(Extras.ClientPackageName.getKey());
    }

    public Bundle getClientStateBundle() {
        return this._intent.getBundleExtra(Extras.ClientStateBundle.getKey());
    }

    public String getCobrandingId() {
        return this._intent.getStringExtra(Extras.CobrandingId.getKey());
    }

    public Context getContext() {
        return this._applicationContext;
    }

    public Intent getContinuation() {
        return (Intent) this._intent.getParcelableExtra(Extras.Continuation.getKey());
    }

    public String getFlowToken() {
        return this._intent.getStringExtra(Extras.FlowToken.getKey());
    }

    public boolean getIsWebFlowTelemetryRequested() {
        return this._intent.getBooleanExtra(Extras.WebFlowTelemetryRequested.getKey(), false);
    }

    public ResultReceiver getResultReceiver() {
        return (ResultReceiver) this._intent.getParcelableExtra(Extras.ResultReceiver.getKey());
    }

    public ISecurityScope getScope() {
        return (ISecurityScope) this._intent.getSerializableExtra(Extras.Scope.getKey());
    }

    public Ticket getTicket(ISecurityScope iSecurityScope) {
        return iSecurityScope == null ? null : (Ticket) this._intent.getSerializableExtra(getTicketKey(iSecurityScope));
    }

    protected String getTicketKey(ISecurityScope iSecurityScope) {
        return TextUtils.join(".", new Object[]{PackageInfoHelper.AuthenticatorPackageName, "Ticket", iSecurityScope.getTarget().toLowerCase(Locale.US), iSecurityScope.getPolicy().toLowerCase(Locale.US)});
    }

    public boolean hasResultReceiver() {
        return getResultReceiver() != null;
    }

    public boolean isSdkRequest() {
        return this._intent.getBooleanExtra(Extras.IsSdkRequest.getKey(), false);
    }

    public void sendFailure(Exception exception) {
        sendResult(1, new ApiResult().setException(exception));
    }

    public void sendSuccess(ApiResult apiResult) {
        Intent continuation = getContinuation();
        if (continuation != null) {
            continuation.fillIn(new Intent().putExtras(apiResult.asBundle()), 0);
            getContext().startService(continuation);
            return;
        }
        sendResult(-1, apiResult);
    }

    public void sendUINeeded(PendingIntent pendingIntent) {
        sendResult(2, new ApiResult().setUINeededIntent(pendingIntent));
    }

    public void sendUserCanceled() {
        sendResult(0, new ApiResult());
    }

    public ApiRequest setAccountName(String str) {
        this._intent.putExtra(Extras.AccountName.getKey(), str);
        return this;
    }

    public ApiRequest setAccountPuid(String str) {
        this._intent.putExtra(Extras.AccountPuid.getKey(), str);
        return this;
    }

    public ApiRequest setClientPackageName(String str) {
        this._intent.putExtra(Extras.ClientPackageName.getKey(), str);
        return this;
    }

    public ApiRequest setClientStateBundle(Bundle bundle) {
        this._intent.putExtra(Extras.ClientStateBundle.getKey(), bundle);
        return this;
    }

    public ApiRequest setCobrandingId(String str) {
        this._intent.putExtra(Extras.CobrandingId.getKey(), str);
        return this;
    }

    public ApiRequest setContinuation(ApiRequest apiRequest) {
        this._intent.putExtra(Extras.Continuation.getKey(), apiRequest.asIntent());
        return this;
    }

    public ApiRequest setFlowToken(String str) {
        this._intent.putExtra(Extras.FlowToken.getKey(), str);
        return this;
    }

    public ApiRequest setIsSdkRequest(boolean z) {
        this._intent.putExtra(Extras.IsSdkRequest.getKey(), z);
        return this;
    }

    public ApiRequest setIsWebFlowTelemetryRequested(boolean z) {
        this._intent.putExtra(Extras.WebFlowTelemetryRequested.getKey(), z);
        return this;
    }

    public ApiRequest setResultReceiver(ResultReceiver resultReceiver) {
        this._intent.putExtra(Extras.ResultReceiver.getKey(), resultReceiver);
        return this;
    }

    public ApiRequest setScope(ISecurityScope iSecurityScope) {
        this._intent.putExtra(Extras.Scope.getKey(), iSecurityScope);
        return this;
    }
}
