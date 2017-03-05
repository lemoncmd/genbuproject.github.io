package com.microsoft.onlineid.internal.sso.service.operation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.PendingIntentBuilder;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorAccountManager;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;

public abstract class ServiceOperation {
    private final AuthenticatorAccountManager _accountManager;
    private final Context _applicationContext;
    private final Bundle _parameters;
    private final TicketManager _ticketManager;

    public ServiceOperation(Context context, Bundle bundle, AuthenticatorAccountManager authenticatorAccountManager, TicketManager ticketManager) {
        this._applicationContext = context;
        this._parameters = bundle;
        this._accountManager = authenticatorAccountManager;
        this._ticketManager = ticketManager;
    }

    public abstract Bundle call() throws AccountNotFoundException, InvalidResponseException, NetworkException, StsException, InternalException;

    protected AuthenticatorAccountManager getAccountManager() {
        return this._accountManager;
    }

    public long getCallerConfigLastDownloadedTime() {
        return this._parameters.getLong(BundleMarshaller.ClientConfigLastDownloadedTimeKey);
    }

    public String getCallerConfigVersion() {
        return this._parameters.getString(BundleMarshaller.ClientConfigVersionKey);
    }

    public String getCallerSdkVersion() {
        return this._parameters.getString(BundleMarshaller.ClientSdkVersionKey);
    }

    public int getCallerSsoVersion() {
        return this._parameters.getInt(BundleMarshaller.ClientSsoVersionKey);
    }

    public Bundle getCallerStateBundle() {
        return this._parameters.getBundle(BundleMarshaller.ClientStateBundleKey);
    }

    public String getCallingPackage() {
        return this._parameters.getString(BundleMarshaller.ClientPackageNameKey);
    }

    protected Context getContext() {
        return this._applicationContext;
    }

    public Bundle getParameters() {
        return this._parameters;
    }

    protected PendingIntentBuilder getPendingIntentBuilder(Intent intent) {
        return new PendingIntentBuilder(intent);
    }

    protected TicketManager getTicketManager() {
        return this._ticketManager;
    }

    public void verifyStandardArguments() {
        Strings.verifyArgumentNotNullOrEmpty(getCallingPackage(), "Package name");
        Strings.verifyArgumentNotNullOrEmpty(getCallerSdkVersion(), "SDK version");
        Strings.verifyArgumentNotNullOrEmpty(getCallerConfigVersion(), "Config version");
        if (getCallerSsoVersion() == 0) {
            throw new IllegalArgumentException("SSO version must not be empty.");
        }
    }
}
