package com.microsoft.onlineid.internal;

import android.app.PendingIntent;
import android.os.Bundle;
import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.ui.WebTelemetryRecorder;
import java.util.ArrayList;
import java.util.Locale;

public class ApiResult {
    public static final int ResultException = 1;
    public static final int ResultUINeeded = 2;
    private final Bundle _bundle;

    public enum Extras {
        Exception,
        UINeededIntent,
        WebFlowTelemetryEvents,
        WebFlowTelemetryAllEventsCaptured;

        public String getKey() {
            return "com.microsoft.msa.authenticator." + name();
        }
    }

    public ApiResult() {
        this(new Bundle());
    }

    public ApiResult(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        this._bundle = bundle;
    }

    public ApiResult addTicket(Ticket ticket) {
        setScope(ticket.getScope());
        this._bundle.putSerializable(getTicketKey(ticket.getScope()), ticket);
        return this;
    }

    public Bundle asBundle() {
        return this._bundle;
    }

    public String getAccountPuid() {
        return this._bundle.getString(com.microsoft.onlineid.internal.ApiRequest.Extras.AccountPuid.getKey());
    }

    public Exception getException() {
        return (Exception) this._bundle.getSerializable(Extras.Exception.getKey());
    }

    public String getFlowToken() {
        return this._bundle.getString(com.microsoft.onlineid.internal.ApiRequest.Extras.FlowToken.getKey());
    }

    public ISecurityScope getScope() {
        return (ISecurityScope) this._bundle.getSerializable(com.microsoft.onlineid.internal.ApiRequest.Extras.Scope.getKey());
    }

    public Ticket getTicket() {
        return getTicket(getScope());
    }

    public Ticket getTicket(ISecurityScope iSecurityScope) {
        return iSecurityScope == null ? null : (Ticket) this._bundle.getSerializable(getTicketKey(iSecurityScope));
    }

    protected String getTicketKey(ISecurityScope iSecurityScope) {
        return TextUtils.join(".", new Object[]{PackageInfoHelper.AuthenticatorPackageName, "Ticket", iSecurityScope.getTarget().toLowerCase(Locale.US), iSecurityScope.getPolicy().toLowerCase(Locale.US)});
    }

    public PendingIntent getUINeededIntent() {
        return (PendingIntent) this._bundle.getParcelable(Extras.UINeededIntent.getKey());
    }

    public ArrayList<String> getWebFlowTelemetryEvents() {
        return this._bundle.getStringArrayList(Extras.WebFlowTelemetryEvents.getKey());
    }

    public boolean getWereAllWebFlowTelemetryEventsCaptured() {
        return this._bundle.getBoolean(Extras.WebFlowTelemetryAllEventsCaptured.getKey(), false);
    }

    public boolean hasWebFlowTelemetryEvents() {
        ArrayList webFlowTelemetryEvents = getWebFlowTelemetryEvents();
        return (webFlowTelemetryEvents == null || webFlowTelemetryEvents.isEmpty()) ? false : true;
    }

    public ApiResult setAccountPuid(String str) {
        this._bundle.putString(com.microsoft.onlineid.internal.ApiRequest.Extras.AccountPuid.getKey(), str);
        return this;
    }

    public ApiResult setException(Exception exception) {
        this._bundle.putSerializable(Extras.Exception.getKey(), exception);
        return this;
    }

    public ApiResult setFlowToken(String str) {
        this._bundle.putString(com.microsoft.onlineid.internal.ApiRequest.Extras.FlowToken.getKey(), str);
        return this;
    }

    public ApiResult setScope(ISecurityScope iSecurityScope) {
        this._bundle.putSerializable(com.microsoft.onlineid.internal.ApiRequest.Extras.Scope.getKey(), iSecurityScope);
        return this;
    }

    public ApiResult setUINeededIntent(PendingIntent pendingIntent) {
        this._bundle.putParcelable(Extras.UINeededIntent.getKey(), pendingIntent);
        return this;
    }

    public ApiResult setWebFlowTelemetryFields(WebTelemetryRecorder webTelemetryRecorder) {
        this._bundle.putStringArrayList(Extras.WebFlowTelemetryEvents.getKey(), webTelemetryRecorder.getEvents());
        this._bundle.putBoolean(Extras.WebFlowTelemetryAllEventsCaptured.getKey(), webTelemetryRecorder.wereAllEventsCaptured());
        return this;
    }
}
