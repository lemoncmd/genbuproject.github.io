package net.zhuoweizhang.mcpelauncher.api;

import android.os.Bundle;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.ScriptManager;
import net.zhuoweizhang.mcpelauncher.patch.PatchUtils;

public class ImportScriptActivity extends ImportActivity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.installConfirmText.setText(R.string.script_import_confirm);
    }

    protected void startImport() {
        try {
            File to = new File(getDir(ScriptManager.SCRIPTS_DIR, 0), this.mFile.getName());
            PatchUtils.copy(this.mFile, to);
            ScriptManager.androidContext = getApplicationContext();
            ScriptManager.loadEnabledScriptsNames(getApplicationContext());
            ScriptManager.setOriginalLocation(this.mFile, to);
            ScriptManager.setEnabled(to, true);
            Toast.makeText(this, R.string.script_imported, 1).show();
            setResult(-1);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.manage_patches_import_error, 1).show();
            setResult(0);
        }
        finish();
    }
}
