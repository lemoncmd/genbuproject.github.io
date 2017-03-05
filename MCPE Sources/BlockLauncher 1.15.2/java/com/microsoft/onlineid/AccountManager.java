package com.microsoft.onlineid;

import android.content.Context;
import android.content.Intent;
import android.os.BadParcelableException;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.AuthenticationException;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Bundles;
import com.microsoft.onlineid.internal.IFailureCallback;
import com.microsoft.onlineid.internal.IUserInteractionCallback;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.MsaSsoClient;
import com.microsoft.onlineid.internal.sso.client.SsoResponse;
import com.microsoft.onlineid.internal.sso.client.SsoRunnable;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AccountManager {
    private IAccountCallback _accountCallback;
    private IAccountCollectionCallback _accountCollectionCallback;
    private final Context _applicationContext;
    private final OnlineIdConfiguration _onlineIdConfiguration;
    private final MsaSsoClient _ssoClient;
    private ITelemetryCallback _telemetryCallback;
    private ITicketCallback _ticketCallback;

    public AccountManager(Context context) {
        this(context, new OnlineIdConfiguration());
    }

    public AccountManager(Context context, OnlineIdConfiguration onlineIdConfiguration) {
        if (context.getApplicationContext() != null) {
            context = context.getApplicationContext();
        }
        this._applicationContext = context;
        if (onlineIdConfiguration == null) {
            onlineIdConfiguration = new OnlineIdConfiguration();
        }
        this._onlineIdConfiguration = onlineIdConfiguration;
        this._ssoClient = new MsaSsoClient(this._applicationContext);
        ClientAnalytics.initialize(this._applicationContext);
        Logger.initialize(this._applicationContext);
    }

    private AccountManager getAccountManager() {
        return this;
    }

    private void verifyCallback(Object obj, String str) {
        if (obj == null) {
            throw new IllegalStateException("You must specify an " + str + " before invoking this method.");
        }
    }

    public void getAccount(Bundle bundle) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getAccountRunnable(bundle)).start();
    }

    public void getAccountById(String str, Bundle bundle) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getAccountByIdRunnable(str, bundle)).start();
    }

    protected SsoRunnable getAccountByIdRunnable(String str, Bundle bundle) {
        final String str2 = str;
        final Bundle bundle2 = bundle;
        return new SsoRunnable(this._accountCallback, bundle) {
            public void performRequest() throws AuthenticationException {
                try {
                    Strings.verifyArgumentNotNullOrEmpty(str2, "cid");
                    AccountManager.this._accountCallback.onAccountAcquired(new UserAccount(AccountManager.this.getAccountManager(), AccountManager.this._ssoClient.getAccountById(str2, bundle2)), bundle2);
                } catch (AccountNotFoundException e) {
                    AccountManager.this._accountCallback.onAccountSignedOut(str2, false, bundle2);
                }
            }
        };
    }

    public void getAccountPickerIntent(Iterable<String> iterable, Bundle bundle) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getAccountPickerIntentRunnable(iterable, bundle)).start();
    }

    protected SsoRunnable getAccountPickerIntentRunnable(Iterable<String> iterable, Bundle bundle) {
        final Iterable<String> iterable2 = iterable;
        final Bundle bundle2 = bundle;
        return new SsoRunnable(this._accountCallback, bundle) {
            public void performRequest() throws AuthenticationException {
                ArrayList arrayList = new ArrayList();
                if (iterable2 != null) {
                    for (String add : iterable2) {
                        arrayList.add(add);
                    }
                }
                AccountManager.this._accountCallback.onUINeeded(AccountManager.this._ssoClient.getAccountPickerIntent(arrayList, AccountManager.this._onlineIdConfiguration, bundle2), bundle2);
            }
        };
    }

    protected SsoRunnable getAccountRunnable(final Bundle bundle) {
        return new SsoRunnable(this._accountCallback, bundle) {
            public void performRequest() throws AuthenticationException {
                SsoResponse account = AccountManager.this._ssoClient.getAccount(AccountManager.this._onlineIdConfiguration, bundle);
                if (account.hasData()) {
                    AccountManager.this._accountCallback.onAccountAcquired(new UserAccount(AccountManager.this.getAccountManager(), (AuthenticatorUserAccount) account.getData()), bundle);
                } else {
                    AccountManager.this._accountCallback.onUINeeded(account.getPendingIntent(), bundle);
                }
            }
        };
    }

    public void getAllAccounts(Bundle bundle) {
        verifyCallback(this._accountCollectionCallback, IAccountCollectionCallback.class.getSimpleName());
        new Thread(getAllAccountsRunnable(bundle)).start();
    }

    protected SsoRunnable getAllAccountsRunnable(final Bundle bundle) {
        return new SsoRunnable(this._accountCollectionCallback, bundle) {
            public void performRequest() throws AuthenticationException {
                Set<AuthenticatorUserAccount> allAccounts = AccountManager.this._ssoClient.getAllAccounts(bundle);
                Set hashSet = new HashSet();
                for (AuthenticatorUserAccount userAccount : allAccounts) {
                    hashSet.add(new UserAccount(AccountManager.this.getAccountManager(), userAccount));
                }
                AccountManager.this._accountCollectionCallback.onAccountCollectionAcquired(hashSet, bundle);
            }
        };
    }

    public void getSignInIntent(SignInOptions signInOptions, Bundle bundle) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getSignInIntentRunnable(signInOptions, bundle)).start();
    }

    protected SsoRunnable getSignInIntentRunnable(SignInOptions signInOptions, Bundle bundle) {
        final SignInOptions signInOptions2 = signInOptions;
        final Bundle bundle2 = bundle;
        return new SsoRunnable(this._accountCallback, bundle) {
            public void performRequest() throws AuthenticationException {
                AccountManager.this._accountCallback.onUINeeded(AccountManager.this._ssoClient.getSignInIntent(signInOptions2, AccountManager.this._onlineIdConfiguration, bundle2), bundle2);
            }
        };
    }

    public void getSignOutIntent(UserAccount userAccount, Bundle bundle) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getSignOutIntentRunnable(userAccount, bundle)).start();
    }

    protected SsoRunnable getSignOutIntentRunnable(UserAccount userAccount, Bundle bundle) {
        final UserAccount userAccount2 = userAccount;
        final Bundle bundle2 = bundle;
        return new SsoRunnable(this._accountCallback, bundle) {
            public void performRequest() throws AuthenticationException {
                try {
                    AccountManager.this._accountCallback.onUINeeded(AccountManager.this._ssoClient.getSignOutIntent(userAccount2.getCid(), bundle2), bundle2);
                } catch (AccountNotFoundException e) {
                    AccountManager.this._accountCallback.onAccountSignedOut(userAccount2.getCid(), false, bundle2);
                }
            }
        };
    }

    public void getSignUpIntent(Bundle bundle) {
        getSignUpIntent(null, bundle);
    }

    public void getSignUpIntent(SignUpOptions signUpOptions, Bundle bundle) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        new Thread(getSignUpIntentRunnable(signUpOptions, bundle)).start();
    }

    protected SsoRunnable getSignUpIntentRunnable(SignUpOptions signUpOptions, Bundle bundle) {
        final SignUpOptions signUpOptions2 = signUpOptions;
        final Bundle bundle2 = bundle;
        return new SsoRunnable(this._accountCallback, bundle) {
            public void performRequest() throws AuthenticationException {
                AccountManager.this._accountCallback.onUINeeded(AccountManager.this._ssoClient.getSignUpIntent(signUpOptions2, AccountManager.this._onlineIdConfiguration, bundle2), bundle2);
            }
        };
    }

    void getTicket(UserAccount userAccount, ISecurityScope iSecurityScope, Bundle bundle) {
        verifyCallback(this._accountCallback, IAccountCallback.class.getSimpleName());
        verifyCallback(this._ticketCallback, ITicketCallback.class.getSimpleName());
        new Thread(getTicketRunnable(userAccount, iSecurityScope, bundle)).start();
    }

    protected SsoRunnable getTicketRunnable(UserAccount userAccount, ISecurityScope iSecurityScope, Bundle bundle) {
        final UserAccount userAccount2 = userAccount;
        final ISecurityScope iSecurityScope2 = iSecurityScope;
        final Bundle bundle2 = bundle;
        return new SsoRunnable(this._ticketCallback, bundle) {
            public void performRequest() throws AuthenticationException {
                try {
                    SsoResponse ticket = AccountManager.this._ssoClient.getTicket(userAccount2.getCid(), iSecurityScope2, AccountManager.this._onlineIdConfiguration, bundle2);
                    if (ticket.hasData()) {
                        AccountManager.this._ticketCallback.onTicketAcquired((Ticket) ticket.getData(), userAccount2, bundle2);
                    } else {
                        AccountManager.this._ticketCallback.onUINeeded(ticket.getPendingIntent(), bundle2);
                    }
                } catch (AccountNotFoundException e) {
                    AccountManager.this._accountCallback.onAccountSignedOut(userAccount2.getCid(), false, bundle2);
                }
            }
        };
    }

    public boolean onActivityResult(int i, int i2, Intent intent) {
        Bundle bundle;
        Bundle bundle2;
        String str = null;
        boolean z = false;
        if (intent != null) {
            try {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    Bundle bundle3 = extras.getBundle(BundleMarshaller.ClientStateBundleKey);
                    String string = extras.getString(BundleMarshaller.ActivityResultTypeKey);
                    ResultType fromString = ResultType.fromString(string);
                    bundle = bundle3;
                    str = string;
                    bundle2 = extras;
                } else {
                    bundle = null;
                    bundle2 = extras;
                    Object obj = null;
                }
            } catch (BadParcelableException e) {
                Logger.info("Caught BadParcelableException when checking extras, ignoring: " + e);
                return false;
            } catch (RuntimeException e2) {
                if (e2.getCause() == null || !(e2.getCause() instanceof ClassNotFoundException)) {
                    throw e2;
                }
                Logger.info("Caught RuntimeException when checking extras, ignoring: " + e2);
                return false;
            }
        }
        bundle = null;
        bundle2 = null;
        obj = null;
        if (Settings.isDebugBuild()) {
            Logger.info("Activity result: request: " + i + ", result: " + i2);
            Bundles.log("With extras:", bundle2);
        }
        if (fromString == null) {
            Logger.info("Unknown result type (" + str + ") encountered, ignoring.");
            return false;
        }
        IUserInteractionCallback iUserInteractionCallback = fromString == ResultType.Ticket ? this._ticketCallback : this._accountCallback;
        IFailureCallback iFailureCallback = fromString == ResultType.Ticket ? this._ticketCallback : this._accountCallback;
        if (!(bundle2 == null || this._telemetryCallback == null)) {
            Iterable stringArrayList = bundle2.getStringArrayList(BundleMarshaller.WebFlowTelemetryEventsKey);
            boolean z2 = bundle2.getBoolean(BundleMarshaller.WebFlowTelemetryAllEventsCapturedKey, false);
            if (!(stringArrayList == null || stringArrayList.isEmpty())) {
                this._telemetryCallback.webTelemetryEventsReceived(stringArrayList, z2);
            }
        }
        if (i2 == 0) {
            iUserInteractionCallback.onUserCancel(bundle);
        } else if (i2 == -1) {
            try {
                if (BundleMarshaller.hasError(bundle2)) {
                    AuthenticationException exceptionFromBundle = BundleMarshaller.exceptionFromBundle(bundle2);
                    if (exceptionFromBundle instanceof AccountNotFoundException) {
                        str = bundle2.getString(BundleMarshaller.UserCidKey);
                        if (str != null) {
                            z = true;
                        }
                        Assertion.check(z, "Expect to find a CID for sign-out notification.");
                        this._accountCallback.onAccountSignedOut(str, bundle2.getBoolean(BundleMarshaller.IsSignedOutOfThisAppOnlyKey), bundle);
                    } else {
                        iFailureCallback.onFailure(exceptionFromBundle, bundle);
                    }
                } else if (BundleMarshaller.hasPendingIntent(bundle2)) {
                    iUserInteractionCallback.onUINeeded(BundleMarshaller.pendingIntentFromBundle(bundle2), bundle);
                } else if (fromString == ResultType.Ticket && BundleMarshaller.hasTicket(bundle2)) {
                    this._ticketCallback.onTicketAcquired(BundleMarshaller.ticketFromBundle(bundle2), new UserAccount(this, BundleMarshaller.limitedUserAccountFromBundle(bundle2)), bundle);
                } else if (fromString == ResultType.Account && BundleMarshaller.hasLimitedUserAccount(bundle2)) {
                    this._accountCallback.onAccountAcquired(new UserAccount(this, BundleMarshaller.limitedUserAccountFromBundle(bundle2)), bundle);
                } else {
                    iFailureCallback.onFailure(new InternalException("Unexpected onActivityResult found."), bundle);
                }
            } catch (Throwable e3) {
                iFailureCallback.onFailure(new InternalException(e3), bundle);
            }
        }
        return true;
    }

    public AccountManager setAccountCallback(IAccountCallback iAccountCallback) {
        this._accountCallback = iAccountCallback;
        return this;
    }

    public AccountManager setAccountCollectionCallback(IAccountCollectionCallback iAccountCollectionCallback) {
        this._accountCollectionCallback = iAccountCollectionCallback;
        return this;
    }

    public AccountManager setTelemetryCallback(ITelemetryCallback iTelemetryCallback) {
        this._telemetryCallback = iTelemetryCallback;
        return this;
    }

    public AccountManager setTicketCallback(ITicketCallback iTicketCallback) {
        this._ticketCallback = iTicketCallback;
        return this;
    }
}
