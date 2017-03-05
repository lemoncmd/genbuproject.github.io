package net.zhuoweizhang.mcpelauncher.api;

import android.os.Bundle;
import android.widget.Toast;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.Utils;

public class ImportSkinActivity extends ImportActivity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.installConfirmText.setText(R.string.skin_import_confirm);
    }

    protected void startImport() {
        Utils.getPrefs(1).edit().putString("player_skin", this.mFile.getAbsolutePath()).apply();
        Utils.getPrefs(0).edit().putBoolean("zz_skin_enable", true).apply();
        Toast.makeText(this, R.string.skin_imported, 1).show();
        finish();
    }
}
