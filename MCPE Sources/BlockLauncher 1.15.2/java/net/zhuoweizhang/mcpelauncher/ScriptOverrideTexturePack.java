package net.zhuoweizhang.mcpelauncher;

import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ScriptOverrideTexturePack implements TexturePack {
    private Context context;

    public ScriptOverrideTexturePack(Context ctx) {
        this.context = ctx;
    }

    public InputStream getInputStream(String fileName) throws IOException {
        if (ScriptManager.androidContext == null) {
            return null;
        }
        File file = ScriptManager.getTextureOverrideFile(fileName);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        return null;
    }

    public long getSize(String fileName) throws IOException {
        if (ScriptManager.androidContext == null) {
            return -1;
        }
        File file = ScriptManager.getTextureOverrideFile(fileName);
        if (file.exists()) {
            return file.length();
        }
        return -1;
    }

    public void close() throws IOException {
    }

    public List<String> listFiles() {
        return new ArrayList();
    }
}
