package com.microsoft.onlineid.internal.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebView.WebViewTransport;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.analytics.ITimedAnalyticsEvent;
import com.microsoft.onlineid.exception.NetworkException;
import com.microsoft.onlineid.internal.ApiRequest;
import com.microsoft.onlineid.internal.ApiResult;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.NetworkConnectivity;
import com.microsoft.onlineid.internal.Uris;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.log.SendLogsHandler;
import com.microsoft.onlineid.internal.transport.Transport;
import com.microsoft.onlineid.sms.SmsReceiver;
import com.microsoft.onlineid.userdata.AccountManagerReader;
import com.microsoft.onlineid.userdata.TelephonyManagerReader;
import java.util.Locale;
import org.mozilla.javascript.Parser;

public class WebFlowActivity extends Activity {
    public static final String ActionResolveInterrupt = "com.microsoft.onlineid.internal.RESOLVE_INTERRUPT";
    public static final String ActionSignIn = "com.microsoft.onlineid.internal.SIGN_IN";
    public static final String ActionSignUp = "com.microsoft.onlineid.internal.SIGN_UP";
    public static final String FullScreenTag = "com.microsoft.onlineid.internal.ui.FullScreen";
    private static final String JavaScriptOnBack = "javascript:OnBack()";
    private static final String ScenarioAuthUrl = "auth url";
    private static final String ScenarioSignIn = "sign in";
    private static final String ScenarioSignUp = "sign up";
    private JavaScriptBridge _javaScriptBridge;
    private SendLogsHandler _logHandler;
    private ITimedAnalyticsEvent _pageLoadTimingEvent;
    protected ProgressView _progressView;
    private String _scenario;
    private SmsReceiver _smsReceiver;
    private String _startUrl;
    private WebTelemetryRecorder _webTelemetryRecorder;
    private WebView _webView;

    private class HostedWebChromeClient extends WebChromeClient {
        private HostedWebChromeClient() {
        }

        public boolean onCreateWindow(WebView webView, boolean z, boolean z2, Message message) {
            try {
                ((WebViewTransport) message.obj).setWebView(new WebView(WebFlowActivity.this));
                message.sendToTarget();
                WebFlowActivity.this._javaScriptBridge.setIsOutOfBandInterrupt();
                return true;
            } catch (ClassCastException e) {
                Assertion.check(false, "resultMsg is not a WebViewTransport");
                return false;
            }
        }
    }

    private class HostedWebViewClient extends WebViewClient {
        private long _finished;
        private final BundledAssetVendor _precachedAssetVendor;
        private long _started;

        public HostedWebViewClient() {
            this._precachedAssetVendor = BundledAssetVendor.getInstance(WebFlowActivity.this.getApplicationContext());
        }

        public BundledAssetVendor getAssetVendor() {
            return this._precachedAssetVendor;
        }

        public void onPageFinished(WebView webView, String str) {
            this._finished = SystemClock.elapsedRealtime();
            super.onPageFinished(webView, str);
            WebFlowActivity.this.showLoadingFinished(webView, str);
            if (Settings.isDebugBuild()) {
                Logger.info("Page load time = " + Long.toString(this._finished - this._started));
            }
        }

        public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
            super.onPageStarted(webView, str, bitmap);
            WebFlowActivity.this.showLoadingStarted(webView, str, bitmap);
            this._started = SystemClock.elapsedRealtime();
            Logger.info("New page loaded: " + str);
        }

        public void onReceivedError(WebView webView, int i, String str, String str2) {
            super.onReceivedError(webView, i, str, str2);
            WebFlowActivity.this.onReceivedWebError(webView, i, str, str2);
        }

        @TargetApi(21)
        public WebResourceResponse shouldInterceptRequest(WebView webView, WebResourceRequest webResourceRequest) {
            return shouldInterceptRequest(webView, webResourceRequest.getUrl().toString());
        }

        public WebResourceResponse shouldInterceptRequest(WebView webView, String str) {
            return this._precachedAssetVendor.getAsset(str);
        }

        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            return !WebFlowActivity.this.overrideUrlLoading(webView, str) ? super.shouldOverrideUrlLoading(webView, str) : true;
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void configureWebView(Bundle bundle, WebFlowTelemetryData webFlowTelemetryData) {
        this._webTelemetryRecorder = new WebTelemetryRecorder(webFlowTelemetryData.getIsWebTelemetryRequested(), bundle);
        this._javaScriptBridge = new JavaScriptBridge(this, this._webTelemetryRecorder, webFlowTelemetryData);
        this._webView.addJavascriptInterface(this._javaScriptBridge, "external");
        WebSettings settings = this._webView.getSettings();
        settings.setUserAgentString(Transport.mergeUserAgentStrings(settings.getUserAgentString(), Transport.buildUserAgentString(getApplicationContext())));
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        WebViewClient hostedWebViewClient = new HostedWebViewClient();
        this._javaScriptBridge.setAssetBundlePropertyProvider(hostedWebViewClient.getAssetVendor());
        this._webView.setWebViewClient(hostedWebViewClient);
        this._webView.setWebChromeClient(new HostedWebChromeClient());
    }

    private RelativeLayout createInitialUI() {
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.setLayoutParams(new LayoutParams(-1, -2));
        relativeLayout.setBackgroundColor(-1);
        this._webView = new WebView(this);
        String action = getIntent().getAction();
        action = ActionSignIn.equals(action) ? "msa_sdk_webflow_webview_sign_in" : ActionSignUp.equals(action) ? "msa_sdk_webflow_webview_sign_up" : "msa_sdk_webflow_webview_resolve_interrupt";
        this._webView.setId(getApplicationContext().getResources().getIdentifier(action, Name.MARK, getApplicationContext().getPackageName()));
        ViewGroup.LayoutParams layoutParams = new LayoutParams(-2, -1);
        layoutParams.addRule(10);
        relativeLayout.addView(this._webView, layoutParams);
        this._progressView = new ProgressView(this);
        layoutParams = new LayoutParams(-1, -2);
        layoutParams.addRule(10);
        relativeLayout.addView(this._progressView, layoutParams);
        return relativeLayout;
    }

    private void disableSavePasswordInWebView() {
        if (VERSION.SDK_INT < 18) {
            this._webView.getSettings().setSavePassword(false);
        }
    }

    public static ApiRequest getFlowRequest(Context context, Uri uri, String str, boolean z, WebFlowTelemetryData webFlowTelemetryData) {
        return new ApiRequest(context, new Intent().setClass(context, WebFlowActivity.class).setAction(str).setData(uri).putExtra(FullScreenTag, z).putExtras(webFlowTelemetryData.asBundle())) {
            public void executeAsync() {
                getContext().startActivity(asIntent());
            }
        };
    }

    private void initializeSendLogsHandler() {
        if (Settings.isDebugBuild()) {
            this._logHandler = new SendLogsHandler(this);
            this._logHandler.setSendScreenshot(true);
        }
    }

    private void onReceivedWebError(WebView webView, int i, String str, String str2) {
        webView.stopLoading();
        webView.loadUrl("about:blank");
        ClientAnalytics.get().logEvent(ClientAnalytics.PerformanceCategory, ClientAnalytics.NoNetworkConnectivity, ClientAnalytics.DuringWebFlow);
        sendResult(1, new ApiResult().setException(new NetworkException(String.format(Locale.US, "Error code: %d, Error description: %s, Failing url: %s", new Object[]{Integer.valueOf(i), str, str2}))).asBundle());
        finish();
    }

    private boolean overrideUrlLoading(WebView webView, String str) {
        return false;
    }

    private void showLoadingFinished(WebView webView, String str) {
        this._progressView.stopAnimation();
        if (this._pageLoadTimingEvent != null) {
            this._pageLoadTimingEvent.end();
        }
    }

    private void showLoadingStarted(WebView webView, String str, Bitmap bitmap) {
        this._progressView.startAnimation();
        this._pageLoadTimingEvent = ClientAnalytics.get().createTimedEvent(ClientAnalytics.RenderingCategory, "WebWizard page load", this._scenario).start();
    }

    public void cancel() {
        sendResult(0, null);
    }

    public void onBackPressed() {
        if (!this._webView.canGoBack() || this._webView.getUrl().startsWith(this._startUrl)) {
            cancel();
        } else {
            this._webView.loadUrl(JavaScriptOnBack);
        }
    }

    protected final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(createInitialUI());
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().removeAllCookie();
        configureWebView(bundle, new WebFlowTelemetryData(getIntent().getExtras()));
        disableSavePasswordInWebView();
        initializeSendLogsHandler();
        Uri appendMarketQueryString = Uris.appendMarketQueryString(getApplicationContext(), getIntent().getData());
        if (!NetworkConnectivity.isAirplaneModeOn(getApplicationContext())) {
            appendMarketQueryString = Uris.appendPhoneDigits(new TelephonyManagerReader(getApplicationContext()), appendMarketQueryString);
        }
        this._startUrl = Uris.appendEmails(new AccountManagerReader(getApplicationContext()), appendMarketQueryString).toString();
        if (Settings.isDebugBuild()) {
            Logger.info("Web flow starting URL: " + this._startUrl);
        }
        this._webView.loadUrl(this._startUrl);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this._logHandler != null) {
            this._logHandler.trySendLogsOnKeyEvent(i);
        }
        return super.onKeyDown(i, keyEvent);
    }

    public void onPause() {
        super.onPause();
        unregisterReceiver(this._smsReceiver);
    }

    protected void onResume() {
        super.onResume();
        this._smsReceiver = new SmsReceiver(this._javaScriptBridge);
        IntentFilter intentFilter = new IntentFilter(SmsReceiver.SMS_RECEIVED_ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(this._smsReceiver, intentFilter);
    }

    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        this._webTelemetryRecorder.saveInstanceState(bundle);
    }

    public final void onStart() {
        super.onStart();
        Object action = getIntent().getAction();
        if (ActionSignIn.equals(action)) {
            this._scenario = ScenarioSignIn;
        } else if (ActionSignUp.equals(action)) {
            this._scenario = ScenarioSignUp;
        } else if (ActionResolveInterrupt.equals(action)) {
            this._scenario = ScenarioAuthUrl;
        } else if (TextUtils.isEmpty(action)) {
            this._scenario = "not specified";
        } else {
            this._scenario = action;
        }
        ClientAnalytics.get().logScreenView("Web wizard (" + this._scenario + ")");
    }

    public void sendResult(int i, Bundle bundle) {
        ApiRequest apiRequest = new ApiRequest(getApplicationContext(), getIntent());
        Intent continuation = apiRequest.getContinuation();
        ResultReceiver resultReceiver = apiRequest.getResultReceiver();
        if (this._webTelemetryRecorder.hasEvents()) {
            bundle = new ApiResult(bundle).setWebFlowTelemetryFields(this._webTelemetryRecorder).asBundle();
        }
        if (continuation != null && i == -1) {
            apiRequest.sendSuccess(new ApiResult(bundle));
        } else if (resultReceiver != null) {
            resultReceiver.send(i, bundle);
        } else {
            setResult(i, bundle != null ? new Intent().putExtras(bundle) : null);
        }
        finish();
        if (i == -1 && (getIntent().getFlags() & Parser.ARGC_LIMIT) == Parser.ARGC_LIMIT) {
            overridePendingTransition(0, 0);
        }
    }
}
