package com.microsoft.onlineid.authenticator;

import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.ui.ProgressView;
import com.microsoft.onlineid.sdk.R;
import com.microsoft.onlineid.ui.MsaSdkActivity;

public class AccountAddPendingActivity extends MsaSdkActivity {
    public void onBackPressed() {
    }

    protected void onCreate(Bundle bundle) {
        setContentView(R.layout.static_page);
        super.onCreate(bundle);
        ProgressView progressView = (ProgressView) findViewById(R.id.progressView);
        progressView.setVisibility(0);
        progressView.startAnimation();
        findViewById(R.id.static_page_header).setVisibility(8);
        findViewById(R.id.static_page_body_first).setVisibility(8);
        findViewById(R.id.static_page_body_second).setVisibility(8);
    }

    protected void onStart() {
        super.onStart();
        ClientAnalytics.get().logScreenView(ClientAnalytics.AccountAddPendingScreen);
    }
}
