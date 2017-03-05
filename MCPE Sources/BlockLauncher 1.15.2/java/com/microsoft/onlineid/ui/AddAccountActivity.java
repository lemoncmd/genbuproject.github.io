package com.microsoft.onlineid.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Handler;
import com.microsoft.onlineid.RequestOptions;
import com.microsoft.onlineid.SignInOptions;
import com.microsoft.onlineid.SignUpOptions;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ActivityResultSender;
import com.microsoft.onlineid.internal.ActivityResultSender.ResultType;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Applications;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Intents.DataBuilder;
import com.microsoft.onlineid.internal.NetworkConnectivity;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.Uris;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.client.BackupService;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.ui.WebFlowActivity;
import com.microsoft.onlineid.internal.ui.WebFlowTelemetryData;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import org.mozilla.javascript.Parser;

public class AddAccountActivity extends Activity {
    protected static final int AccountAddedRequest = 2;
    public static final String ActionAddAccount = "com.microsoft.onlineid.internal.ADD_ACCOUNT";
    public static final String ActionSignUpAccount = "com.microsoft.onlineid.internal.SIGN_UP_ACCOUNT";
    protected static final int AddPendingRequest = 1;
    private static final String AppIdLabel = "client_id";
    private static final String ClientFlightLabel = "client_flight";
    public static final String CobrandingIdLabel = "cobrandid";
    protected static final int NoRequest = -1;
    public static final String PlatformLabel = "platform";
    public static final String PlatformName = "android";
    private static final String PrefillUsernameLabel = "username";
    public static final String SignInOptionsLabel = (AddAccountActivity.class.getName() + ".SignInOptions");
    protected static final int SignInWebFlowRequest = 0;
    public static final String SignUpFlowLabel = "fl";
    public static final String SignUpOptionsLabel = (AddAccountActivity.class.getName() + ".SignUpOptions");
    private static final String UnauthenticatedSessionIdLabel = "uaid";
    private static final String WReplyLabel = "wreply";
    protected String _accountPuid;
    protected Handler _handler;
    protected int _pendingChildRequest = NoRequest;
    private ActivityResultSender _resultSender;
    protected TypedStorage _typedStorage;

    private void appendOptions(RequestOptions requestOptions, Builder builder) {
        String prefillUsername = requestOptions.getPrefillUsername();
        if (prefillUsername != null) {
            builder.appendQueryParameter(PrefillUsernameLabel, prefillUsername);
        }
        prefillUsername = requestOptions.getUnauthenticatedSessionId();
        if (prefillUsername != null) {
            builder.appendQueryParameter(UnauthenticatedSessionIdLabel, prefillUsername);
        }
        prefillUsername = requestOptions.getFlightConfiguration();
        if (prefillUsername != null) {
            builder.appendQueryParameter(ClientFlightLabel, prefillUsername);
        }
    }

    public static Intent getSignInIntent(Context context, SignInOptions signInOptions, String str, String str2, boolean z, String str3, Bundle bundle) {
        Intent data = new Intent(context, AddAccountActivity.class).setAction(ActionAddAccount).putExtra(SignUpFlowLabel, str).putExtra(BundleMarshaller.CobrandingIdKey, str2).putExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, z).putExtra(BundleMarshaller.ClientPackageNameKey, str3).putExtra(BundleMarshaller.ClientStateBundleKey, bundle).setData(new DataBuilder().add((RequestOptions) signInOptions).add(str).add(str2).add(str3).build());
        if (signInOptions != null) {
            data.putExtra(SignInOptionsLabel, signInOptions.asBundle());
        }
        return data;
    }

    public static Intent getSignUpIntent(Context context, SignUpOptions signUpOptions, String str, String str2, boolean z, String str3, Bundle bundle) {
        Intent data = new Intent(context, AddAccountActivity.class).setAction(ActionSignUpAccount).putExtra(SignUpFlowLabel, str).putExtra(BundleMarshaller.CobrandingIdKey, str2).putExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, z).putExtra(BundleMarshaller.ClientPackageNameKey, str3).putExtra(BundleMarshaller.ClientStateBundleKey, bundle).setData(new DataBuilder().add((RequestOptions) signUpOptions).add(str).add(str2).add(str3).build());
        if (signUpOptions != null) {
            data.putExtra(SignUpOptionsLabel, signUpOptions.asBundle());
        }
        return data;
    }

    protected void addCommonQueryStringParams(Builder builder) {
        builder.appendQueryParameter(PlatformLabel, PlatformName + Resources.getSdkVersion(getApplicationContext()));
        builder.appendQueryParameter(AppIdLabel, Applications.buildClientAppUri(getApplicationContext(), getIntent().getStringExtra(BundleMarshaller.ClientPackageNameKey)));
        String stringExtra = getIntent().getStringExtra(BundleMarshaller.CobrandingIdKey);
        if (stringExtra != null) {
            builder.appendQueryParameter(CobrandingIdLabel, stringExtra);
        }
    }

    protected void addTelemetryToResult(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            ApiResult apiResult = new ApiResult(intent.getExtras());
            if (apiResult.hasWebFlowTelemetryEvents()) {
                this._resultSender.putWebFlowTelemetryFields(apiResult).set();
            }
        }
    }

    public void finish() {
        if (this._pendingChildRequest != NoRequest) {
            finishActivity(this._pendingChildRequest);
            this._pendingChildRequest = NoRequest;
        }
        super.finish();
    }

    protected Uri getLoginUri(ServerConfig serverConfig, boolean z, boolean z2) {
        Endpoint endpoint = z ? z2 ? Endpoint.SignupWReplyMsa : Endpoint.ConnectMsa : z2 ? Endpoint.SignupWReplyPartner : Endpoint.ConnectPartner;
        Builder buildUpon = Uri.parse(serverConfig.getUrl(endpoint).toExternalForm()).buildUpon();
        addCommonQueryStringParams(buildUpon);
        Bundle bundleExtra = getIntent().getBundleExtra(SignInOptionsLabel);
        if (bundleExtra != null) {
            appendOptions(new SignInOptions(bundleExtra), buildUpon);
        }
        return z2 ? Uris.appendMarketQueryString(getApplicationContext(), buildUpon.build()) : buildUpon.build();
    }

    protected Uri getSignupUri(ServerConfig serverConfig, boolean z) {
        Builder buildUpon = Uri.parse(serverConfig.getUrl(z ? Endpoint.SignupMsa : Endpoint.SignupPartner).toExternalForm()).buildUpon();
        addCommonQueryStringParams(buildUpon);
        Bundle bundleExtra = getIntent().getBundleExtra(SignUpOptionsLabel);
        if (bundleExtra != null) {
            appendOptions(new SignUpOptions(bundleExtra), buildUpon);
        }
        String stringExtra = getIntent().getStringExtra(SignUpFlowLabel);
        if (stringExtra != null) {
            buildUpon.appendQueryParameter(SignUpFlowLabel, stringExtra);
        }
        buildUpon.appendQueryParameter(WReplyLabel, getLoginUri(serverConfig, z, true).toString());
        return buildUpon.build();
    }

    protected void onActivityResult(int i, int i2, Intent intent) {
        if (i == this._pendingChildRequest) {
            this._pendingChildRequest = NoRequest;
        }
        switch (i) {
            case SignInWebFlowRequest /*0*/:
                addTelemetryToResult(intent);
                switch (i2) {
                    case NoRequest /*-1*/:
                        if (intent == null || intent.getExtras() == null) {
                            sendFailureResult("Sign in flow finished successfully with no extras set.");
                            return;
                        } else {
                            onSetupSuccessful(new ApiResult(intent.getExtras()).getAccountPuid());
                            return;
                        }
                    case SignInWebFlowRequest /*0*/:
                        sendCancelledResult();
                        return;
                    case AddPendingRequest /*1*/:
                        sendFailureResult(new ApiResult(intent.getExtras()).getException());
                        return;
                    default:
                        sendFailureResult("Sign in activity finished with unexpected result code: " + i2);
                        return;
                }
            case AddPendingRequest /*1*/:
                return;
            case AccountAddedRequest /*2*/:
                switch (i2) {
                    case NoRequest /*-1*/:
                    case SignInWebFlowRequest /*0*/:
                        sendSuccessResult(this._accountPuid);
                        return;
                    default:
                        sendFailureResult("Account added activity finished with unexpected result code: " + i2);
                        return;
                }
            default:
                Logger.error("Received activity result for unknown request code: " + i);
                sendFailureResult("Received activity result for unknown request code: " + i);
                return;
        }
    }

    protected void onCreate(Bundle bundle) {
        boolean wasPrecachingEnabled;
        super.onCreate(bundle);
        ServerConfig serverConfig = new ServerConfig(getApplicationContext());
        String stringExtra = getIntent().getStringExtra(BundleMarshaller.ClientPackageNameKey);
        boolean isAuthenticatorApp = PackageInfoHelper.isAuthenticatorApp(stringExtra);
        this._resultSender = new ActivityResultSender(this, ResultType.Account);
        String action = getIntent().getAction();
        Bundle bundleExtra;
        if (ActionSignUpAccount.equals(action)) {
            bundleExtra = getIntent().getBundleExtra(SignUpOptionsLabel);
            if (bundleExtra != null) {
                wasPrecachingEnabled = new SignUpOptions(bundleExtra).getWasPrecachingEnabled();
            }
            wasPrecachingEnabled = false;
        } else {
            if (ActionAddAccount.equals(action)) {
                bundleExtra = getIntent().getBundleExtra(SignInOptionsLabel);
                if (bundleExtra != null) {
                    wasPrecachingEnabled = new SignInOptions(bundleExtra).getWasPrecachingEnabled();
                }
            }
            wasPrecachingEnabled = false;
        }
        Intent asIntent = WebFlowActivity.getFlowRequest(getApplicationContext(), ActionSignUpAccount.equals(action) ? getSignupUri(serverConfig, isAuthenticatorApp) : getLoginUri(serverConfig, isAuthenticatorApp, false), ActionSignUpAccount.equals(action) ? WebFlowActivity.ActionSignUp : WebFlowActivity.ActionSignIn, isAuthenticatorApp, new WebFlowTelemetryData().setIsWebTelemetryRequested(getIntent().getBooleanExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, false)).setCallingAppPackageName(stringExtra).setCallingAppVersionName(PackageInfoHelper.getAppVersionName(getApplicationContext(), stringExtra)).setWasPrecachingEnabled(wasPrecachingEnabled)).asIntent();
        asIntent.addFlags(Parser.ARGC_LIMIT);
        this._pendingChildRequest = SignInWebFlowRequest;
        if (NetworkConnectivity.hasInternetConnectivity(getApplicationContext())) {
            startActivityForResult(asIntent, SignInWebFlowRequest);
            this._handler = new Handler();
            return;
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.NoNetworkConnectivity, ClientAnalytics.AtStartOfWebFlow);
        sendFailureResult(new NetworkException());
    }

    protected void onSetupFailure(Exception exception) {
        sendFailureResult(exception);
    }

    protected void onSetupSuccessful(String str) {
        BackupService.pushBackup(getApplicationContext());
        if (!isFinishing()) {
            finishActivity(AddPendingRequest);
            sendSuccessResult(str);
        }
    }

    protected void sendCancelledResult() {
        ApiRequest apiRequest = new ApiRequest(getApplicationContext(), getIntent());
        if (apiRequest.hasResultReceiver()) {
            apiRequest.sendUserCanceled();
        }
        finish();
    }

    protected void sendFailureResult(Exception exception) {
        Assertion.check(exception != null);
        Logger.error("Failed to add account.", exception);
        ClientAnalytics.get().logException(exception);
        ApiRequest apiRequest = new ApiRequest(getApplicationContext(), getIntent());
        if (apiRequest.hasResultReceiver()) {
            apiRequest.sendFailure(exception);
        } else {
            this._resultSender.putException(exception).set();
        }
        finish();
    }

    protected void sendFailureResult(String str) {
        sendFailureResult(new InternalException(str));
    }

    protected void sendSuccessResult(String str) {
        Assertion.check(str != null);
        ApiRequest apiRequest = new ApiRequest(getApplicationContext(), getIntent());
        if (apiRequest.hasResultReceiver()) {
            apiRequest.sendSuccess(new ApiResult().setAccountPuid(str));
        } else {
            AuthenticatorUserAccount readAccount = new TypedStorage(getApplicationContext()).readAccount(str);
            if (readAccount == null) {
                sendFailureResult(new InternalException("AddAccountActivity could not acquire newly added account."));
                return;
            }
            this._resultSender.putLimitedUserAccount(readAccount).set();
        }
        finish();
    }
}
