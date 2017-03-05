package com.microsoft.onlineid.internal.ui;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.ActivityResultHandler;
import com.microsoft.onlineid.internal.ActivityResultSender;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiRequestResultReceiver;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import org.mozilla.javascript.Parser;

public class InterruptResolutionActivity extends Activity {
    private static final int PendingActivityRequestCode = 1;
    private static final int WebFlowRequestCode = 2;
    private String _accountCid;
    private String _accountPuid;
    private String _clientPackageName;
    private Bundle _clientState;
    private String _cobrandingId;
    private ISecurityScope _requestedScope;
    private ActivityResultSender _resultSender;
    private TypedStorage _storage;
    private TicketResultReceiver _ticketReceiver;

    private static abstract class DelegatedResultReceiver extends ApiRequestResultReceiver {
        protected InterruptResolutionActivity _activity;

        public DelegatedResultReceiver() {
            super(new Handler());
        }

        protected void onFailure(Exception exception) {
            if (this._activity != null) {
                this._activity.onFailure(exception);
            }
        }

        protected void onUINeeded(PendingIntent pendingIntent) {
            if (this._activity != null) {
                this._activity.onUiNeeded(pendingIntent);
            }
        }

        protected void onUserCancel() {
            if (this._activity != null) {
                this._activity.onUserCancel();
            }
        }

        public void setActivity(InterruptResolutionActivity interruptResolutionActivity) {
            this._activity = interruptResolutionActivity;
        }
    }

    private static class TicketResultReceiver extends DelegatedResultReceiver {
        private TicketResultReceiver() {
        }

        protected void onSuccess(ApiResult apiResult) {
            if (this._activity != null) {
                this._activity.onTicketAcquired(apiResult.getTicket());
            }
        }
    }

    private class WebFlowResultHandler extends ActivityResultHandler {
        private WebFlowResultHandler() {
        }

        protected void onFailure(Exception exception) {
            InterruptResolutionActivity.this.onFailure(exception);
        }

        protected void onSuccess(ApiResult apiResult) {
            InterruptResolutionActivity.this.onWebFlowSucceeded(apiResult.getFlowToken());
        }

        protected void onUINeeded(PendingIntent pendingIntent) {
            InterruptResolutionActivity.this.onUiNeeded(pendingIntent);
        }

        protected void onUserCancel() {
            InterruptResolutionActivity.this.onUserCancel();
        }
    }

    public static Intent getResolutionIntent(Context context, Uri uri, AuthenticatorUserAccount authenticatorUserAccount, ISecurityScope iSecurityScope, String str, boolean z, String str2, Bundle bundle) {
        Intent putExtra = new Intent().setClass(context, InterruptResolutionActivity.class).setData(uri).putExtra(BundleMarshaller.UserPuidKey, authenticatorUserAccount.getPuid()).putExtra(BundleMarshaller.UserCidKey, authenticatorUserAccount.getCid()).putExtra(BundleMarshaller.CobrandingIdKey, str).putExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, z).putExtra(BundleMarshaller.ClientPackageNameKey, str2).putExtra(BundleMarshaller.ClientStateBundleKey, bundle);
        if (iSecurityScope != null && str2 != null) {
            putExtra.putExtras(BundleMarshaller.scopeToBundle(iSecurityScope));
        } else if (iSecurityScope != null && str2 == null) {
            throw new IllegalArgumentException("A ticket scope requires a client package name to make a request.");
        }
        return putExtra;
    }

    protected void addTelemetryToResult(Intent intent) {
        if (intent != null) {
            ApiResult apiResult = new ApiResult(intent.getExtras());
            if (apiResult.hasWebFlowTelemetryEvents()) {
                this._resultSender.putWebFlowTelemetryFields(apiResult).set();
            }
        }
    }

    protected String getAccountCid() {
        return this._accountCid;
    }

    protected String getAccountPuid() {
        return this._accountPuid;
    }

    protected String getClientPackageName() {
        return this._clientPackageName;
    }

    protected ISecurityScope getRequestedScope() {
        return this._requestedScope;
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        switch (i) {
            case PendingActivityRequestCode /*1*/:
                if (i2 == 0) {
                    onUserCancel();
                    return;
                }
                return;
            case WebFlowRequestCode /*2*/:
                addTelemetryToResult(intent);
                new WebFlowResultHandler().onActivityResult(i2, intent);
                return;
            default:
                return;
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this._storage = new TypedStorage(getApplicationContext());
        this._resultSender = new ActivityResultSender(this, ResultType.Ticket);
        this._ticketReceiver = new TicketResultReceiver();
        this._ticketReceiver.setActivity(this);
        if (BundleMarshaller.hasScope(getIntent().getExtras())) {
            try {
                this._requestedScope = BundleMarshaller.scopeFromBundle(getIntent().getExtras());
            } catch (Exception e) {
                onFailure(e);
            }
        }
        this._accountPuid = getIntent().getExtras().getString(BundleMarshaller.UserPuidKey);
        this._accountCid = getIntent().getExtras().getString(BundleMarshaller.UserCidKey);
        this._clientPackageName = getIntent().getExtras().getString(BundleMarshaller.ClientPackageNameKey);
        this._cobrandingId = getIntent().getStringExtra(BundleMarshaller.CobrandingIdKey);
        this._clientState = getIntent().getBundleExtra(BundleMarshaller.ClientStateBundleKey);
        WebFlowTelemetryData wasPrecachingEnabled = new WebFlowTelemetryData().setIsWebTelemetryRequested(getIntent().getBooleanExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, false)).setCallingAppPackageName(this._clientPackageName).setCallingAppVersionName(PackageInfoHelper.getAppVersionName(getApplicationContext(), this._clientPackageName)).setWasPrecachingEnabled(false);
        if (bundle == null) {
            startActivityForResult(WebFlowActivity.getFlowRequest(getApplicationContext(), getIntent().getData(), WebFlowActivity.ActionResolveInterrupt, true, wasPrecachingEnabled).setAccountPuid(this._accountPuid).asIntent().addFlags(Parser.ARGC_LIMIT), WebFlowRequestCode);
        }
    }

    protected void onDestroy() {
        this._ticketReceiver.setActivity(null);
        super.onDestroy();
    }

    protected void onFailure(Exception exception) {
        ApiRequest apiRequest = new ApiRequest(getApplicationContext(), getIntent());
        if (!apiRequest.isSdkRequest()) {
            apiRequest.sendFailure(exception);
        } else if (exception instanceof AccountNotFoundException) {
            this._resultSender.putSignedOutCid(this._accountCid, false).set();
        } else {
            this._resultSender.putException(exception).set();
        }
        finishActivity(PendingActivityRequestCode);
        finish();
    }

    protected void onTicketAcquired(Ticket ticket) {
        AuthenticatorUserAccount readAccount = this._storage.readAccount(this._accountPuid);
        if (readAccount == null) {
            onFailure(new AccountNotFoundException());
            return;
        }
        this._resultSender.putTicket(ticket).putLimitedUserAccount(readAccount).set();
        finishActivity(PendingActivityRequestCode);
        finish();
    }

    protected void onUiNeeded(PendingIntent pendingIntent) {
        try {
            startIntentSenderForResult(pendingIntent.getIntentSender(), 0, null, 0, 0, 0);
            this._resultSender.putWereAllWebFlowTelemetryEventsCaptured(false).set();
        } catch (Exception e) {
            onFailure(e);
        }
    }

    protected void onUserCancel() {
        ApiRequest apiRequest = new ApiRequest(getApplicationContext(), getIntent());
        if (apiRequest.hasResultReceiver()) {
            apiRequest.sendUserCanceled();
        }
        finishActivity(PendingActivityRequestCode);
        finish();
    }

    protected void onWebFlowSucceeded(String str) {
        ApiRequest apiRequest = new ApiRequest(getApplicationContext(), getIntent());
        if (apiRequest.isSdkRequest()) {
            if (this._requestedScope == null) {
                this._ticketReceiver.onFailure(new IllegalArgumentException("Scope must not be null for SSO ticket request."));
            }
            startService(new TicketManager(getApplicationContext()).createTicketRequest(this._accountPuid, this._requestedScope, this._clientPackageName, this._cobrandingId, this._clientState).setFlowToken(str).setResultReceiver(this._ticketReceiver).asIntent());
            showPendingActivity();
            return;
        }
        apiRequest.sendSuccess(new ApiResult().setAccountPuid(this._accountPuid).setFlowToken(str));
        finish();
    }

    protected void showPendingActivity() {
        startActivityForResult(new Intent().setClassName(getApplicationContext(), "com.microsoft.onlineid.authenticator.AccountAddPendingActivity").addFlags(Parser.ARGC_LIMIT), PendingActivityRequestCode);
    }
}
