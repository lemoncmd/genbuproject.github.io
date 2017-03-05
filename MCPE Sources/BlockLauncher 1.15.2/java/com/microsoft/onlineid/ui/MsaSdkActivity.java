package com.microsoft.onlineid.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import com.microsoft.onlineid.internal.configuration.Settings;
import com.microsoft.onlineid.internal.log.SendLogsHandler;
import com.microsoft.onlineid.internal.ui.AccountHeaderView;
import com.microsoft.onlineid.sdk.R;

public abstract class MsaSdkActivity extends Activity {
    public static final String AuthenticatorIntentFlagTag = "com.microsoft.msa.authenticator.authenticatorFlags";
    public static final int IntentFlagNoFinishAnimation = 1;
    protected SendLogsHandler _logHandler;

    protected void onCreate(Bundle bundle) {
        AccountHeaderView.applyStyle(this, getResources().getString(R.string.webflow_header));
        super.onCreate(bundle);
        this._logHandler = new SendLogsHandler(this);
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (Settings.isDebugBuild() && this._logHandler != null) {
            this._logHandler.setSendScreenshot(true);
            this._logHandler.trySendLogsOnKeyEvent(i);
        }
        return super.onKeyDown(i, keyEvent);
    }

    protected void onPause() {
        if (isFinishing() && (getIntent().getIntExtra(AuthenticatorIntentFlagTag, 0) & IntentFlagNoFinishAnimation) == IntentFlagNoFinishAnimation) {
            overridePendingTransition(0, 0);
        }
        super.onPause();
    }
}
