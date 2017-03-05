package net.zhuoweizhang.mcpelauncher.api;

import android.os.Bundle;
import android.widget.Toast;
import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.io.IOException;
import net.zhuoweizhang.mcpelauncher.PatchManager;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.Utils;
import net.zhuoweizhang.mcpelauncher.patch.PatchUtils;

public class ImportPatchActivity extends ImportActivity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.installConfirmText.setText(R.string.manage_patches_import_confirm);
    }

    protected void startImport() {
        File to = new File(getDir(MainActivity.PT_PATCHES_DIR, 0), this.mFile.getName());
        try {
            PatchUtils.copy(this.mFile, to);
            setResult(-1);
            boolean hasTooManyPatches = hasTooManyPatches();
            PatchManager.getPatchManager(this).setEnabled(to, false);
            if (hasTooManyPatches) {
                Toast.makeText(this, R.string.manage_patches_too_many, 1).show();
                finish();
                return;
            }
            PatchManager.getPatchManager(this).setEnabled(to, true);
            Utils.getPrefs(1).edit().putBoolean("force_prepatch", true).apply();
            if (MainActivity.libLoaded) {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(100);
                        } catch (Exception e) {
                        }
                        System.exit(0);
                    }
                }).start();
            }
            Toast.makeText(this, R.string.manage_patches_import_done, 0).show();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.manage_patches_import_error, 1).show();
        }
    }

    public boolean hasTooManyPatches() {
        int maxPatchCount = getResources().getInteger(R.integer.max_num_patches);
        return maxPatchCount >= 0 && PatchManager.getPatchManager(this).getEnabledPatches().size() >= maxPatchCount;
    }
}
