package net.zhuoweizhang.mcpelauncher.api;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import java.util.List;
import net.zhuoweizhang.mcpelauncher.R;
import net.zhuoweizhang.mcpelauncher.Utils;
import net.zhuoweizhang.mcpelauncher.texture.TexturePackDescription;
import net.zhuoweizhang.mcpelauncher.texture.TexturePackLoader;

public class ImportTexturepackActivity extends ImportActivity {
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.installConfirmText.setText(R.string.texturepack_import_confirm);
    }

    protected void startImport() {
        try {
            List<TexturePackDescription> list = TexturePackLoader.loadDescriptionsWithIcons(this);
            TexturePackDescription desc = new TexturePackDescription(TexturePackLoader.TYPE_ZIP, this.mFile.getAbsolutePath());
            int minecraftVersionCode = getPackageManager().getPackageInfo("com.mojang.minecraftpe", 0).versionCode;
            SharedPreferences myprefs = Utils.getPrefs(1);
            boolean needReplaceAll = myprefs.getInt("last_version", -1) != minecraftVersionCode;
            if (needReplaceAll) {
                myprefs.edit().putInt("last_version", minecraftVersionCode).apply();
            }
            boolean replacePack = getIntent().getAction().equals("net.zhuoweizhang.mcpelauncher.action.REPLACE_TEXTUREPACK") || needReplaceAll;
            if (replacePack) {
                list.clear();
                list.add(desc);
            } else {
                boolean already = false;
                for (TexturePackDescription d : list) {
                    if (d.path.equals(desc.path)) {
                        already = true;
                        break;
                    }
                }
                if (!already) {
                    list.add(0, desc);
                }
            }
            TexturePackLoader.saveDescriptions(this, list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.getPrefs(0).edit().putBoolean("zz_texture_pack_enable", true).apply();
        Toast.makeText(this, R.string.texturepack_imported, 1).show();
        finish();
    }
}
