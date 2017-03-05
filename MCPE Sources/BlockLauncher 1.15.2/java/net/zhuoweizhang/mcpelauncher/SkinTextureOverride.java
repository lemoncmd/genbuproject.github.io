package net.zhuoweizhang.mcpelauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SkinTextureOverride implements TexturePack {
    public InputStream getInputStream(String fileName) throws IOException {
        if (!Utils.getPrefs(0).getBoolean("zz_skin_enable", false) || Utils.isSafeMode() || !fileName.equals("images/mob/char.png")) {
            return null;
        }
        String skinPath = Utils.getPrefs(1).getString("player_skin", null);
        if (skinPath == null) {
            return null;
        }
        File file = new File(skinPath);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        return null;
    }

    public void close() throws IOException {
    }

    public List<String> listFiles() throws IOException {
        return new ArrayList();
    }

    public long getSize(String name) throws IOException {
        return 0;
    }
}
