package com.microsoft.onlineid;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.microsoft.onlineid.internal.Uris;
import com.microsoft.onlineid.ui.AddAccountActivity;
import java.util.logging.Logger;

public class ResourcePreloader {
    private static final String INT_PRELOAD_URI = "https://signup.live-int.com/SignupPreload";
    private static final String PROD_PRELOAD_URI = "https://signup.live.com/SignupPreload";
    private static final Logger logger = Logger.getLogger("ResourcePreloader");

    private ResourcePreloader() {
    }

    private static void addWebViewClient(WebView webView) {
        webView.setWebViewClient(new WebViewClient() {
            private long started;

            public void onLoadResource(WebView webView, String str) {
                ResourcePreloader.logger.info("Loading " + str);
            }

            public void onPageFinished(WebView webView, String str) {
                ResourcePreloader.logger.info("Page load for " + str + " finished in " + (SystemClock.elapsedRealtime() - this.started) + "ms");
            }

            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                this.started = SystemClock.elapsedRealtime();
            }
        });
    }

    private static Uri buildUri(Context context, String str) {
        return Uris.appendMarketQueryString(context.getApplicationContext(), Uri.parse(PROD_PRELOAD_URI).buildUpon().appendQueryParameter(AddAccountActivity.CobrandingIdLabel, str).build());
    }

    public static void preloadSignup(Context context, String str) {
        WebView webView = new WebView(context);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(buildUri(context, str).toString());
    }
}
