package com.microsoft.onlineid.internal.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import com.microsoft.onlineid.SecurityScope;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiRequest.Extras;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.NetworkConnectivity;
import com.microsoft.onlineid.internal.PackageInfoHelper;
import com.microsoft.onlineid.internal.exception.AccountNotFoundException;
import com.microsoft.onlineid.internal.exception.PromptNeededException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;
import com.microsoft.onlineid.internal.ui.PropertyBag.Key;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.KnownEnvironment;
import com.microsoft.onlineid.sts.exception.InlineFlowException;
import com.microsoft.onlineid.sts.exception.InvalidResponseException;
import com.microsoft.onlineid.sts.exception.StsException;
import com.microsoft.onlineid.userdata.AccountManagerReader;
import com.microsoft.onlineid.userdata.SignUpData;

public class JavaScriptBridge {
    private static final String PPCRL_REQUEST_E_USER_CANCELED = "80048842";
    private IWebPropertyProvider _assetBundlePropertyProvider;
    private boolean _isOutOfBandInterrupt;
    private final PropertyBag _propertyBag;
    private final ServerConfig _serverConfig;
    private final WebTelemetryRecorder _telemetryRecorder;
    private final TicketManager _ticketManager;
    private final TypedStorage _typedStorage;
    private final WebFlowActivity _webFlowActivity;

    @Deprecated
    public JavaScriptBridge() {
        this._webFlowActivity = null;
        this._telemetryRecorder = null;
        this._propertyBag = null;
        this._serverConfig = null;
        this._typedStorage = null;
        this._ticketManager = null;
    }

    public JavaScriptBridge(WebFlowActivity webFlowActivity, WebTelemetryRecorder webTelemetryRecorder, WebFlowTelemetryData webFlowTelemetryData) {
        this._webFlowActivity = webFlowActivity;
        this._telemetryRecorder = webTelemetryRecorder;
        this._propertyBag = new PropertyBag();
        Context applicationContext = this._webFlowActivity.getApplicationContext();
        this._serverConfig = new ServerConfig(applicationContext);
        this._typedStorage = new TypedStorage(applicationContext);
        this._ticketManager = new TicketManager(applicationContext);
        populatePropertyBag();
        populateTelemetryData(webFlowTelemetryData);
    }

    private static Key getKeyForName(String str) {
        Key key = null;
        if (str == null) {
            Assertion.check(false);
        } else {
            try {
                key = Key.valueOf(str);
            } catch (IllegalArgumentException e) {
            }
        }
        return key;
    }

    private void populateTelemetryData(WebFlowTelemetryData webFlowTelemetryData) {
        try {
            Context applicationContext = this._webFlowActivity.getApplicationContext();
            boolean isCurrentApp = PackageInfoHelper.isCurrentApp(webFlowTelemetryData.getCallingAppPackageName(), applicationContext);
            this._propertyBag.set(Key.TelemetryAppVersion, webFlowTelemetryData.getCallingAppVersionName());
            this._propertyBag.set(Key.TelemetryIsRequestorMaster, Boolean.toString(isCurrentApp));
            this._propertyBag.set(Key.TelemetryNetworkType, NetworkConnectivity.getNetworkTypeForServerTelemetry(applicationContext));
            this._propertyBag.set(Key.TelemetryPrecaching, Boolean.toString(webFlowTelemetryData.getWasPrecachingEnabled()));
        } catch (Throwable e) {
            Logger.error("Encountered error setting telemetry items in property bag.", e);
        }
    }

    @JavascriptInterface
    public void FinalBack() {
        this._webFlowActivity.cancel();
    }

    @JavascriptInterface
    public void FinalNext() {
        String action = this._webFlowActivity.getIntent().getAction();
        Object obj = this._propertyBag.get(Key.ErrorCode);
        try {
            if (TextUtils.isEmpty(obj)) {
                if (WebFlowActivity.ActionSignIn.equals(action) || WebFlowActivity.ActionSignUp.equals(action)) {
                    handleSignInResult();
                } else if (WebFlowActivity.ActionResolveInterrupt.equals(action)) {
                    handleInterruptResult();
                } else {
                    throw new InternalException("Unknown Action: " + action);
                }
            } else if (this._isOutOfBandInterrupt) {
                this._webFlowActivity.cancel();
            } else {
                String str = this._propertyBag.get(Key.ExtendedErrorString);
                if (str == null || !str.contains(PPCRL_REQUEST_E_USER_CANCELED)) {
                    throw new InlineFlowException(this._propertyBag.get(Key.ErrorString), this._propertyBag.get(Key.ErrorURL), obj, str);
                }
                FinalBack();
            }
        } catch (Throwable e) {
            ClientAnalytics.get().logException(e);
            Logger.error("Web flow with action " + action + " failed.", e);
            this._webFlowActivity.sendResult(1, new ApiResult().setException(e).asBundle());
        }
    }

    @JavascriptInterface
    public String Property(String str) {
        Key keyForName = getKeyForName(str);
        return keyForName == null ? null : (this._assetBundlePropertyProvider == null || !this._assetBundlePropertyProvider.handlesProperty(keyForName)) ? this._propertyBag.get(keyForName) : this._assetBundlePropertyProvider.getProperty(keyForName);
    }

    @JavascriptInterface
    public void Property(String str, String str2) {
        Key keyForName = getKeyForName(str);
        if (keyForName == null) {
            return;
        }
        if (this._assetBundlePropertyProvider == null || !this._assetBundlePropertyProvider.handlesProperty(keyForName)) {
            this._propertyBag.set(keyForName, str2);
            if (keyForName.equals(Key.IsSignUp)) {
                Logger.info(Key.IsSignUp + "=" + str2);
                ClientAnalytics.get().logEvent(ClientAnalytics.AppAccountsCategory, ClientAnalytics.SignUp);
                return;
            }
            return;
        }
        this._assetBundlePropertyProvider.setProperty(keyForName, str2);
    }

    @JavascriptInterface
    public void ReportTelemetry(String str) {
        if (this._telemetryRecorder != null) {
            this._telemetryRecorder.recordEvent(str);
        }
    }

    protected AuthenticatorUserAccount createAccountFromProperties(PropertyBag propertyBag) throws InternalException {
        String str = propertyBag.get(Key.DAToken);
        String str2 = propertyBag.get(Key.DASessionKey);
        String str3 = propertyBag.get(Key.SigninName);
        String str4 = propertyBag.get(Key.CID);
        String str5 = propertyBag.get(Key.PUID);
        validateProperty(Key.DAToken, str);
        validateProperty(Key.DASessionKey, str2);
        validateProperty(Key.SigninName, str3);
        return new AuthenticatorUserAccount(str5, str4, str3, new DAToken(str, Base64.decode(str2, 2)));
    }

    protected void handleInterruptResult() throws AccountNotFoundException, InternalException {
        AuthenticatorUserAccount readAccount = this._typedStorage.readAccount(new ApiRequest(null, this._webFlowActivity.getIntent()).getAccountPuid());
        if (readAccount == null) {
            throw new AccountNotFoundException("Account was deleted before interrupt could be resolved.");
        }
        Object obj = this._propertyBag.get(Key.DAToken);
        String str = this._propertyBag.get(Key.DASessionKey);
        if (TextUtils.isEmpty(obj) || TextUtils.isEmpty(str)) {
            Logger.warning("WebWizard property bag did not have DAToken/SessionKey");
        } else {
            try {
                readAccount.setDAToken(new DAToken(obj, Base64.decode(str, 2)));
                this._typedStorage.writeAccount(readAccount);
            } catch (IllegalArgumentException e) {
                Logger.error("Could not decode Base64: " + str);
                throw new InternalException("Session Key from interrupt resolution was invalid.");
            }
        }
        Object obj2 = this._propertyBag.get(Key.STSInlineFlowToken);
        if (TextUtils.isEmpty(obj2)) {
            Logger.error("Interrupt resolution did not return a flow token.");
            Assertion.check(false, "Interrupt resolution did not return a flow token.");
        }
        this._webFlowActivity.sendResult(-1, new ApiResult().setFlowToken(obj2).asBundle());
    }

    protected void handleSignInResult() throws InternalException, NetworkException, InvalidResponseException, StsException {
        AuthenticatorUserAccount createAccountFromProperties = createAccountFromProperties(this._propertyBag);
        if (createAccountFromProperties.isNewAndInOutOfBandInterrupt()) {
            try {
                this._ticketManager.getTicketNoCache(createAccountFromProperties, new SecurityScope(KnownEnvironment.Production.getEnvironment().equals(this._serverConfig.getEnvironment()) ? "ssl.live.com" : "ssl.live-int.com", "mbi_ssl"), null);
                return;
            } catch (PromptNeededException e) {
                final Intent asIntent = e.getRequest().asIntent();
                asIntent.removeExtra(Extras.Continuation.getKey());
                asIntent.fillIn(this._webFlowActivity.getIntent(), 0);
                asIntent.setAction(WebFlowActivity.ActionResolveInterrupt);
                asIntent.putExtra(BundleMarshaller.WebFlowTelemetryRequestedKey, this._telemetryRecorder.isRequested());
                this._webFlowActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        JavaScriptBridge.this._webFlowActivity.setIntent(asIntent);
                        JavaScriptBridge.this._webFlowActivity.recreate();
                    }
                });
                return;
            }
        }
        this._typedStorage.writeAccount(createAccountFromProperties);
        this._webFlowActivity.sendResult(-1, new ApiResult().setAccountPuid(createAccountFromProperties.getPuid()).asBundle());
    }

    protected void populatePropertyBag() {
        Context applicationContext = this._webFlowActivity.getApplicationContext();
        SignUpData signUpData = new SignUpData(applicationContext);
        this._propertyBag.set(Key.PfUsernames, new AccountManagerReader(applicationContext).getEmailsAsJsonArray());
        this._propertyBag.set(Key.PfFirstName, signUpData.getFirstName());
        this._propertyBag.set(Key.PfLastName, signUpData.getLastName());
        this._propertyBag.set(Key.PfDeviceEmail, signUpData.getDeviceEmail());
        this._propertyBag.set(Key.PfPhone, signUpData.getPhone());
        this._propertyBag.set(Key.PfCountryCode, signUpData.getCountryCode());
    }

    public void setAssetBundlePropertyProvider(IWebPropertyProvider iWebPropertyProvider) {
        this._assetBundlePropertyProvider = iWebPropertyProvider;
    }

    void setIsOutOfBandInterrupt() {
        this._isOutOfBandInterrupt = true;
    }

    protected void validateProperty(Key key, String str) throws InternalException {
        if (TextUtils.isEmpty(str)) {
            String str2 = "PropertyBag was missing required property: " + key.name();
            Logger.error(str2);
            throw new InternalException(str2);
        }
    }
}
