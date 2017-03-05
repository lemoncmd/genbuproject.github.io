package com.microsoft.onlineid.internal.sso.client;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.OnlineIdConfiguration;
import com.microsoft.onlineid.SignInOptions;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountByIdRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountPickerIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAccountRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetAllAccountsRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignInIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignOutIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetSignUpIntentRequest;
import com.microsoft.onlineid.internal.sso.client.request.GetTicketRequest;
import com.microsoft.onlineid.internal.sso.client.request.SingleSsoRequest;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ConfigManager;
import com.microsoft.onlineid.sts.ServerConfig;
import java.util.ArrayList;
import java.util.Set;

public class MsaSsoClient {
    private final Context _applicationContext;
    private final ServerConfig _config;
    private final ConfigManager _configManager;
    private final MigrationManager _migrationManager;
    private final ServiceFinder _serviceFinder;

    public MsaSsoClient(Context context) {
        this._applicationContext = context;
        this._config = new ServerConfig(context);
        this._serviceFinder = new ServiceFinder(context);
        this._configManager = new ConfigManager(context);
        this._migrationManager = new MigrationManager(context);
    }

    private <T> T performRequestWithSelf(SingleSsoRequest<T> singleSsoRequest) throws AuthenticationException {
        Logger.info("Attempting to self-service request.");
        return singleSsoRequest.performRequest(this._serviceFinder.getSelfSsoService());
    }

    public SsoResponse<AuthenticatorUserAccount> getAccount(OnlineIdConfiguration onlineIdConfiguration, Bundle bundle) throws AuthenticationException {
        return (SsoResponse) performRequestWithFallback(new GetAccountRequest(this._applicationContext, bundle, onlineIdConfiguration));
    }

    public AuthenticatorUserAccount getAccountById(String str, Bundle bundle) throws AuthenticationException {
        return (AuthenticatorUserAccount) performRequestWithFallback(new GetAccountByIdRequest(this._applicationContext, bundle, str));
    }

    public PendingIntent getAccountPickerIntent(ArrayList<String> arrayList, OnlineIdConfiguration onlineIdConfiguration, Bundle bundle) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetAccountPickerIntentRequest(this._applicationContext, bundle, arrayList, onlineIdConfiguration));
    }

    public Set<AuthenticatorUserAccount> getAllAccounts(Bundle bundle) throws AuthenticationException {
        return (Set) performRequestWithFallback(new GetAllAccountsRequest(this._applicationContext, bundle));
    }

    public PendingIntent getSignInIntent(SignInOptions signInOptions, OnlineIdConfiguration onlineIdConfiguration, Bundle bundle) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignInIntentRequest(this._applicationContext, bundle, signInOptions, onlineIdConfiguration));
    }

    public PendingIntent getSignOutIntent(String str, Bundle bundle) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignOutIntentRequest(this._applicationContext, bundle, str));
    }

    public PendingIntent getSignUpIntent(SignUpOptions signUpOptions, OnlineIdConfiguration onlineIdConfiguration, Bundle bundle) throws AuthenticationException {
        return (PendingIntent) performRequestWithFallback(new GetSignUpIntentRequest(this._applicationContext, bundle, signUpOptions, onlineIdConfiguration));
    }

    public SsoResponse<Ticket> getTicket(String str, ISecurityScope iSecurityScope, OnlineIdConfiguration onlineIdConfiguration, Bundle bundle) throws AuthenticationException {
        return (SsoResponse) performRequestWithFallback(new GetTicketRequest(this._applicationContext, bundle, str, iSecurityScope, onlineIdConfiguration));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected <T> T performRequestWithFallback(com.microsoft.onlineid.internal.sso.client.request.SingleSsoRequest<T> r14) throws com.microsoft.onlineid.exception.AuthenticationException {
        /*
        r13 = this;
        r7 = 0;
        r3 = 0;
        r2 = 1;
        r0 = r13._configManager;
        r0.updateIfFirstDownloadNeeded();
        r0 = r13._migrationManager;
        r0.migrateAndUpgradeStorageIfNeeded();
        r0 = r13._config;
        r1 = com.microsoft.onlineid.sts.ServerConfig.Int.MaxTriesForSsoRequestWithFallback;
        r0 = r0.getInt(r1);
        if (r0 >= r2) goto L_0x0133;
    L_0x0017:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r4 = "Invalid MaxTriesForSsoRequestWithFallback: ";
        r1 = r1.append(r4);
        r0 = r1.append(r0);
        r0 = r0.toString();
        com.microsoft.onlineid.internal.log.Logger.error(r0);
        r1 = com.microsoft.onlineid.analytics.ClientAnalytics.get();
        r4 = "SDK";
        r5 = "SSO fallback";
        r1.logEvent(r4, r5, r0);
        r1 = r2;
    L_0x0039:
        r0 = r13._serviceFinder;
        r0 = r0.getOrderedSsoServices();
        r4 = r0.iterator();
        r0 = r4.hasNext();
        if (r0 == 0) goto L_0x005a;
    L_0x0049:
        r0 = r4.next();
        r0 = (com.microsoft.onlineid.internal.sso.SsoService) r0;
        r5 = r7;
        r6 = r7;
    L_0x0051:
        if (r6 >= r1) goto L_0x010d;
    L_0x0053:
        if (r0 == 0) goto L_0x010d;
    L_0x0055:
        r0 = r14.performRequest(r0);	 Catch:{ MasterRedirectException -> 0x005e, ServiceBindingException -> 0x00af, ClientNotAuthorizedException -> 0x00bf, UnsupportedClientVersionException -> 0x00c5, ClientConfigUpdateNeededException -> 0x00cb }
    L_0x0059:
        return r0;
    L_0x005a:
        r0 = r3;
        r5 = r7;
        r6 = r7;
        goto L_0x0051;
    L_0x005e:
        r8 = move-exception;
        r0 = r8.getRedirectRequestTo();
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "Redirect to: ";
        r9 = r9.append(r10);
        r9 = r9.append(r0);
        r9 = r9.toString();
        com.microsoft.onlineid.internal.log.Logger.info(r9, r8);
        r10 = com.microsoft.onlineid.analytics.ClientAnalytics.get();
        r11 = "SDK";
        r12 = "SSO fallback";
        r10.logEvent(r11, r12, r9);
        r9 = r13._serviceFinder;
        r0 = r9.getSsoService(r0);
        if (r0 != 0) goto L_0x00aa;
    L_0x008c:
        r0 = "Cannot find redirected master";
        com.microsoft.onlineid.internal.log.Logger.error(r0, r8);
        r0 = com.microsoft.onlineid.analytics.ClientAnalytics.get();
        r8 = "SDK";
        r9 = "SSO fallback";
        r10 = "Cannot find redirected master";
        r0.logEvent(r8, r9, r10);
        r0 = r4.hasNext();
        if (r0 == 0) goto L_0x00ad;
    L_0x00a4:
        r0 = r4.next();
        r0 = (com.microsoft.onlineid.internal.sso.SsoService) r0;
    L_0x00aa:
        r6 = r6 + 1;
        goto L_0x0051;
    L_0x00ad:
        r0 = r3;
        goto L_0x00aa;
    L_0x00af:
        r0 = move-exception;
        r0 = r4.hasNext();
        if (r0 == 0) goto L_0x00bd;
    L_0x00b6:
        r0 = r4.next();
        r0 = (com.microsoft.onlineid.internal.sso.SsoService) r0;
        goto L_0x00aa;
    L_0x00bd:
        r0 = r3;
        goto L_0x00aa;
    L_0x00bf:
        r0 = move-exception;
        r0 = r13.performRequestWithSelf(r14);
        goto L_0x0059;
    L_0x00c5:
        r0 = move-exception;
        r0 = r13.performRequestWithSelf(r14);
        goto L_0x0059;
    L_0x00cb:
        r8 = move-exception;
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r10 = "Client needs config update: ";
        r9 = r9.append(r10);
        r8 = r8.getMessage();
        r8 = r9.append(r8);
        r8 = r8.toString();
        com.microsoft.onlineid.internal.log.Logger.info(r8);
        r8 = r13._configManager;
        r8 = r8.update();
        if (r8 == 0) goto L_0x00aa;
    L_0x00ee:
        r0 = r13._serviceFinder;
        r0 = r0.getOrderedSsoServices();
        r4 = r0.iterator();
        r0 = r4.hasNext();
        if (r0 == 0) goto L_0x010b;
    L_0x00fe:
        r0 = r4.next();
        r0 = (com.microsoft.onlineid.internal.sso.SsoService) r0;
    L_0x0104:
        if (r5 != 0) goto L_0x0131;
    L_0x0106:
        r5 = r6 + -1;
    L_0x0108:
        r6 = r5;
        r5 = r2;
        goto L_0x00aa;
    L_0x010b:
        r0 = r3;
        goto L_0x0104;
    L_0x010d:
        r0 = java.util.Locale.US;
        r1 = new java.lang.Object[r2];
        r2 = java.lang.Integer.valueOf(r6);
        r1[r7] = r2;
        r2 = "SSO request failed after %d tries";
        r0 = java.lang.String.format(r0, r2, r1);
        com.microsoft.onlineid.internal.log.Logger.error(r0);
        r1 = com.microsoft.onlineid.analytics.ClientAnalytics.get();
        r2 = "SDK";
        r3 = "SSO fallback";
        r1.logEvent(r2, r3, r0);
        r0 = r13.performRequestWithSelf(r14);
        goto L_0x0059;
    L_0x0131:
        r5 = r6;
        goto L_0x0108;
    L_0x0133:
        r1 = r0;
        goto L_0x0039;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.onlineid.internal.sso.client.MsaSsoClient.performRequestWithFallback(com.microsoft.onlineid.internal.sso.client.request.SingleSsoRequest):T");
    }
}
