package net.zhuoweizhang.mcpelauncher;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.mojang.minecraftpe.MainActivity;
import net.zhuoweizhang.mcpelauncher.ui.LauncherActivity;

public class LauncherAppActivity extends LauncherActivity {
    private static final int MESSAGE_AD_TIMEOUT = 609;
    private static final int MESSAGE_SHOW_AD = 608;
    private boolean adError = false;
    private Handler adHandler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == LauncherAppActivity.MESSAGE_SHOW_AD) {
                LauncherAppActivity.this.actuallyShowAdvertisement();
            } else if (message.what == LauncherAppActivity.MESSAGE_AD_TIMEOUT) {
                LauncherAppActivity.this.adTimedOut();
            }
        }
    };
    private boolean hasCalledShowAdvertisement = false;
    private InterstitialAd interstitial;
    private boolean needsShowAd = false;
    private PopupWindow shadePopup;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        loadInterstitialAdvertisement();
        TextView text = new TextView(this);
        text.setText("Please wait...");
        this.shadePopup = new PopupWindow(text, -1, -1);
        this.shadePopup.setBackgroundDrawable(new ColorDrawable(-16777216));
    }

    public void onPrepareDialog(int dialogId, Dialog dialog) {
        switch (dialogId) {
            case MainActivity.DIALOG_RUNTIME_OPTIONS /*4097*/:
                prepareRuntimeOptionsDialog(dialog);
                break;
        }
        super.onPrepareDialog(dialogId, dialog);
    }

    protected void prepareRuntimeOptionsDialog(Dialog dialog) {
        FrameLayout view = (FrameLayout) dialog.findViewById(16908331);
        view.setVisibility(0);
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((View) parent).setVisibility(0);
        }
        AdView adView = (AdView) view.findViewById(-1091584273);
        if (adView == null) {
            adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(AdConfiguration.AD_UNIT_ID);
            adView.setId(-1091584273);
            view.addView(adView);
        }
        adView.loadAd(AdConfiguration.buildRequest());
    }

    public void leaveGameCallback() {
        super.leaveGameCallback();
        this.hasCalledShowAdvertisement = false;
        runOnUiThread(new Runnable() {
            public void run() {
                LauncherAppActivity.this.showAdvertisement();
                LauncherAppActivity.this.hasCalledShowAdvertisement = true;
            }
        });
        while (!this.hasCalledShowAdvertisement) {
            try {
                Thread.sleep(20);
            } catch (Exception e) {
            }
        }
        try {
            Thread.sleep(100);
        } catch (Exception e2) {
        }
    }

    protected void loadInterstitialAdvertisement() {
        this.adError = false;
        this.interstitial = new InterstitialAd(this);
        this.interstitial.setAdUnitId("ca-app-pub-2652482030334356/8558350222");
        this.interstitial.setAdListener(new AdListener() {
            public void onAdLoaded() {
                System.out.println("Ad loaded!");
                LauncherAppActivity.this.adError = false;
                if (LauncherAppActivity.this.needsShowAd) {
                    LauncherAppActivity.this.actuallyShowAdvertisement();
                }
            }

            public void onAdFailedToLoad(int reason) {
                LauncherAppActivity.this.adError = true;
                if (LauncherAppActivity.this.needsShowAd) {
                    LauncherAppActivity.this.needsShowAd = false;
                    LauncherAppActivity.this.adOver();
                }
            }

            public void onAdClosed() {
                LauncherAppActivity.this.adOver();
            }
        });
        this.interstitial.loadAd(AdConfiguration.buildRequest());
    }

    public void showAdvertisement() {
        if (this.adError) {
            loadInterstitialAdvertisement();
            return;
        }
        this.shadePopup.showAtLocation(getWindow().getDecorView(), 51, 0, 0);
        ScriptManager.nativeSetExitEnabled(false);
        this.adHandler.removeMessages(MESSAGE_AD_TIMEOUT);
        this.adHandler.sendEmptyMessageDelayed(MESSAGE_SHOW_AD, 500);
    }

    private void actuallyShowAdvertisement() {
        ScriptManager.nativeSetExitEnabled(true);
        if (!this.shadePopup.isShowing()) {
            System.out.println("No longer ready to show ad.");
            this.needsShowAd = false;
        } else if (this.interstitial.isLoaded()) {
            this.needsShowAd = false;
            this.interstitial.show();
            this.adHandler.removeMessages(MESSAGE_AD_TIMEOUT);
            loadInterstitialAdvertisement();
        } else if (this.adError) {
            this.needsShowAd = false;
            this.adHandler.removeMessages(MESSAGE_AD_TIMEOUT);
            adOver();
        } else {
            this.needsShowAd = true;
            this.adHandler.sendEmptyMessageDelayed(MESSAGE_AD_TIMEOUT, 5000);
        }
    }

    private void adOver() {
        this.needsShowAd = false;
        this.shadePopup.dismiss();
        ScriptManager.nativeSetExitEnabled(true);
    }

    private void adTimedOut() {
        adOver();
    }
}
