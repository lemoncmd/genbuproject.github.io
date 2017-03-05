package com.microsoft.onlineid.internal.sts;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.MsaService;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.exception.PromptNeededException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.client.BackupService;
import com.microsoft.onlineid.internal.storage.TicketStorage;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.ui.InterruptResolutionActivity;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ConfigManager;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.DeviceIdentityManager;
import com.microsoft.onlineid.sts.FlightManager;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;
import com.microsoft.onlineid.sts.request.StsRequestFactory;
import com.microsoft.onlineid.sts.response.ServiceResponse;
import com.microsoft.onlineid.ui.AddAccountActivity;

public class TicketManager {
    private final Context _applicationContext;
    private final ConfigManager _configManager;
    private final DeviceIdentityManager _deviceManager;
    private final FlightManager _flightManager;
    private final StsRequestFactory _stsRequestFactory;
    private final TicketStorage _ticketStorage;
    private final TypedStorage _typedStorage;

    @Deprecated
    public TicketManager() {
        this._applicationContext = null;
        this._configManager = null;
        this._deviceManager = null;
        this._stsRequestFactory = null;
        this._typedStorage = null;
        this._ticketStorage = null;
        this._flightManager = null;
    }

    public TicketManager(Context context) {
        this._applicationContext = context;
        this._configManager = new ConfigManager(context);
        this._deviceManager = new DeviceIdentityManager(context);
        this._stsRequestFactory = new StsRequestFactory(context);
        this._typedStorage = new TypedStorage(context);
        this._ticketStorage = new TicketStorage(context);
        this._flightManager = new FlightManager(context);
    }

    private void updateAccountDetails(String str, ServiceResponse serviceResponse, boolean z) {
        Assertion.check(serviceResponse.succeeded());
        DAToken dAToken = serviceResponse.getDAToken();
        if (dAToken != null) {
            AuthenticatorUserAccount readAccount = this._typedStorage.readAccount(str);
            if (readAccount != null) {
                if (z) {
                    readAccount.setFlights(serviceResponse.getFlights());
                }
                readAccount.setDAToken(dAToken);
                this._typedStorage.writeAccount(readAccount);
            }
        }
    }

    public ApiRequest createTicketRequest(String str, ISecurityScope iSecurityScope, String str2, String str3, Bundle bundle) {
        return new ApiRequest(this._applicationContext, new Intent(this._applicationContext, MsaService.class).setAction(MsaService.ActionGetTicket)).setAccountPuid(str).setScope(iSecurityScope).setClientPackageName(str2).setCobrandingId(str3).setClientStateBundle(bundle);
    }

    protected String getSdkVersion() {
        return Resources.getSdkVersion(this._applicationContext);
    }

    public Ticket getTicket(String str, ISecurityScope iSecurityScope, String str2) throws AccountNotFoundException, InvalidResponseException, StsException, NetworkException, PromptNeededException {
        return getTicket(str, iSecurityScope, this._applicationContext.getPackageName(), str2, false, null, false, null);
    }

    public Ticket getTicket(String str, ISecurityScope iSecurityScope, String str2, String str3, String str4) throws AccountNotFoundException, InvalidResponseException, StsException, NetworkException, PromptNeededException {
        return getTicket(str, iSecurityScope, str2, str3, false, str4, false, null);
    }

    public Ticket getTicket(String str, ISecurityScope iSecurityScope, String str2, String str3, boolean z, String str4, boolean z2, Bundle bundle) throws AccountNotFoundException, InvalidResponseException, StsException, NetworkException, PromptNeededException {
        Strings.verifyArgumentNotNullOrEmpty(str, "accountPuid");
        Objects.verifyArgumentNotNull(iSecurityScope, "scope");
        Strings.verifyArgumentNotNullOrEmpty(str2, "packageName");
        Ticket ticket = this._ticketStorage.getTicket(str, str2, iSecurityScope);
        if (ticket != null) {
            Logger.info("Ticket request serviced from cache: " + iSecurityScope.toString());
        } else {
            AuthenticatorUserAccount readAccount = this._typedStorage.readAccount(str);
            if (readAccount == null) {
                throw new AccountNotFoundException("The account was deleted.");
            }
            Logger.info("Attempting to get ticket from server: " + iSecurityScope.toString());
            ServiceResponse performServiceRequest = performServiceRequest(readAccount, iSecurityScope, str2, str3, z, str4, z2, bundle);
            Assertion.check(performServiceRequest.succeeded(), "Service request failure not handled by performServiceRequest");
            updateAccountDetails(str, performServiceRequest, z);
            if (str3 != null) {
                BackupService.pushBackup(this._applicationContext);
            } else {
                BackupService.pushBackupIfNeeded(this._applicationContext);
            }
            if (z) {
                this._flightManager.enrollInFlights();
            }
            ticket = performServiceRequest.getTicket();
            Assertion.check(ticket != null);
            this._ticketStorage.storeTicket(str, str2, ticket);
        }
        return ticket;
    }

    public Ticket getTicket(String str, ISecurityScope iSecurityScope, String str2, boolean z) throws AccountNotFoundException, InvalidResponseException, StsException, NetworkException, PromptNeededException {
        return getTicket(str, iSecurityScope, this._applicationContext.getPackageName(), str2, false, null, false, null);
    }

    public Ticket getTicketNoCache(AuthenticatorUserAccount authenticatorUserAccount, ISecurityScope iSecurityScope, String str) throws NetworkException, PromptNeededException, InvalidResponseException, StsException {
        return getTicketNoCache(authenticatorUserAccount, iSecurityScope, this._applicationContext.getPackageName(), str);
    }

    public Ticket getTicketNoCache(AuthenticatorUserAccount authenticatorUserAccount, ISecurityScope iSecurityScope, String str, String str2) throws NetworkException, PromptNeededException, InvalidResponseException, StsException {
        boolean z = false;
        Objects.verifyArgumentNotNull(authenticatorUserAccount, "account");
        Objects.verifyArgumentNotNull(iSecurityScope, "scope");
        Strings.verifyArgumentNotNullOrEmpty(str, "packageName");
        Logger.info("Attempting to get ticket from server: " + iSecurityScope.toString());
        ServiceResponse performServiceRequest = performServiceRequest(authenticatorUserAccount, iSecurityScope, str, str2, false, null, false, null);
        Assertion.check(performServiceRequest.succeeded(), "Service request failure not handled by performServiceRequest");
        Ticket ticket = performServiceRequest.getTicket();
        if (ticket != null) {
            z = true;
        }
        Assertion.check(z);
        return ticket;
    }

    protected ServiceResponse performServiceRequest(AuthenticatorUserAccount authenticatorUserAccount, ISecurityScope iSecurityScope, String str, String str2, boolean z, String str3, boolean z2, Bundle bundle) throws NetworkException, InvalidResponseException, StsException, PromptNeededException {
        ServiceResponse serviceResponse = (ServiceResponse) this._stsRequestFactory.createServiceRequest(authenticatorUserAccount, this._deviceManager.getDeviceIdentity(false), iSecurityScope, str, str2, z).send();
        if (!serviceResponse.succeeded() && serviceResponse.getError().isRetryableDeviceDAErrorForUserAuth()) {
            serviceResponse = (ServiceResponse) this._stsRequestFactory.createServiceRequest(authenticatorUserAccount, this._deviceManager.getDeviceIdentity(true), iSecurityScope, str, str2, z).send();
        }
        this._configManager.updateIfNeeded(serviceResponse.getConfigVersion());
        if (serviceResponse.succeeded()) {
            return serviceResponse;
        }
        StsError error = serviceResponse.getError();
        Logger.error("ServiceRequest failed with error: " + error.getMessage());
        String inlineAuthUrl = serviceResponse.getInlineAuthUrl();
        if (inlineAuthUrl != null) {
            String str4 = AddAccountActivity.PlatformName + getSdkVersion();
            Builder buildUpon = Uri.parse(inlineAuthUrl).buildUpon();
            buildUpon.appendQueryParameter(AddAccountActivity.PlatformLabel, str4);
            if (str3 != null) {
                buildUpon.appendQueryParameter(AddAccountActivity.CobrandingIdLabel, str3);
            }
            throw new PromptNeededException(new ApiRequest(this._applicationContext, InterruptResolutionActivity.getResolutionIntent(this._applicationContext, buildUpon.build(), authenticatorUserAccount, iSecurityScope, str3, z2, str, bundle)));
        }
        throw new StsException("Could not acquire ticket.", error);
    }
}
