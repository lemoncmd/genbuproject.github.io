package net.zhuoweizhang.mcpelauncher.texture;

import com.mojang.minecraftpe.MainActivity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.TexturePack;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import org.json.JSONException;
import org.json.JSONObject;

public class ResourcePackManifestProvider implements TexturePack {
    public boolean hasChanges = false;
    public String manifestPath;
    public JSONObject metaObj;

    public ResourcePackManifestProvider(String manifestPath) {
        this.manifestPath = manifestPath;
    }

    public InputStream getInputStream(String fileName) throws IOException {
        if (this.hasChanges && fileName.equals(this.manifestPath)) {
            return new ByteArrayInputStream(this.metaObj.toString().getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        }
        return null;
    }

    public List<String> listFiles() throws IOException {
        return new ArrayList();
    }

    public void init(MainActivity activity) throws Exception {
        this.hasChanges = false;
        loadAtlas(activity);
    }

    private void loadAtlas(MainActivity activity) throws Exception {
        InputStream metaIs = activity.getInputStreamForAsset(this.manifestPath);
        byte[] a = new byte[EnchantType.fishingRod];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (true) {
            int p = metaIs.read(a);
            if (p != -1) {
                bos.write(a, 0, p);
            } else {
                metaIs.close();
                this.metaObj = new JSONObject(new String(bos.toByteArray(), HttpURLConnectionBuilder.DEFAULT_CHARSET));
                return;
            }
        }
    }

    public void addTextures(List<String[]> textures) throws JSONException {
        if (textures.size() != 0) {
            JSONObject textureObject = this.metaObj.getJSONObject("resources").getJSONObject("textures");
            for (String[] pair : textures) {
                textureObject.put(pair[0], pair[1]);
            }
            this.hasChanges = true;
        }
    }

    public void close() throws IOException {
        this.metaObj = null;
        this.hasChanges = false;
    }

    public long getSize(String name) {
        return 0;
    }
}
