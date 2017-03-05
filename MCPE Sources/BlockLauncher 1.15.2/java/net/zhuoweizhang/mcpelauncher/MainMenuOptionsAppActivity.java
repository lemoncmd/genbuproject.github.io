package net.zhuoweizhang.mcpelauncher;

import android.os.Bundle;
import android.view.ViewParent;
import android.widget.LinearLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import net.zhuoweizhang.mcpelauncher.ui.MainMenuOptionsActivity;

public class MainMenuOptionsAppActivity extends MainMenuOptionsActivity {
    private AdView adView;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        try {
            addAds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addAds() {
        ViewParent parentOfListView = findViewById(16908298).getParent();
        if (parentOfListView == null) {
            System.out.println("Main menu options: no parent (is this device Batman?)");
            return;
        }
        ViewParent parentOfParent = parentOfListView.getParent();
        if (parentOfParent == null) {
            System.out.println("Main menu options: no parent of parent");
            return;
        }
        ViewParent parentOfParentOfParent = parentOfParent.getParent();
        if (parentOfParentOfParent == null || !(parentOfParentOfParent instanceof LinearLayout)) {
            System.out.println("Main menu options: no parent of parent of parent");
            return;
        }
        LinearLayout content = (LinearLayout) parentOfParentOfParent;
        this.adView = new AdView(this);
        this.adView.setAdUnitId(AdConfiguration.AD_UNIT_ID);
        this.adView.setAdSize(AdSize.SMART_BANNER);
        AdRequest adRequest = AdConfiguration.buildRequest();
        content.addView(this.adView, 0);
        this.adView.loadAd(adRequest);
    }

    public void onPause() {
        if (this.adView != null) {
            this.adView.pause();
        }
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        if (this.adView != null) {
            this.adView.resume();
        }
    }

    public void onDestroy() {
        if (this.adView != null) {
            this.adView.destroy();
        }
        super.onDestroy();
    }
}
