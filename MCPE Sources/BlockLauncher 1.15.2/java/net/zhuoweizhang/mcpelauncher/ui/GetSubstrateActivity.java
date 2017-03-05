package net.zhuoweizhang.mcpelauncher.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import net.zhuoweizhang.mcpelauncher.R;

public class GetSubstrateActivity extends Activity {
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.get_substrate);
    }

    public void downloadClicked(View v) {
        Intent downloadIntent = new Intent("android.intent.action.VIEW", Uri.parse(isPlay() ? "market://details?id=com.saurik.substrate" : "http://www.cydiasubstrate.com/"));
        downloadIntent.addFlags(268435456);
        startActivity(downloadIntent);
        finish();
    }

    private boolean isPlay() {
        try {
            return getPackageManager().getInstallerPackageName(getPackageName()).equals("com.android.vending");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
