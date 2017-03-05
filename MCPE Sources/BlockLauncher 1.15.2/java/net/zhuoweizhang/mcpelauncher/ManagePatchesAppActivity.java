package net.zhuoweizhang.mcpelauncher;

import android.os.Bundle;
import com.google.android.gms.ads.AdView;
import net.zhuoweizhang.mcpelauncher.ui.ManagePatchesActivity;

public class ManagePatchesAppActivity extends ManagePatchesActivity {
    private AdView adView;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.adView = (AdView) findViewById(R.id.ad);
        this.adView.loadAd(AdConfiguration.buildRequest());
    }
}
