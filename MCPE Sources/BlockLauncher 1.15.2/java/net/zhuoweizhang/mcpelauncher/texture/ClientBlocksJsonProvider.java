package net.zhuoweizhang.mcpelauncher.texture;

import com.mojang.minecraftpe.MainActivity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.ScriptManager;
import net.zhuoweizhang.mcpelauncher.TexturePack;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientBlocksJsonProvider implements TexturePack {
    private static final String[] sidesNames = new String[]{"up", "down", "north", "south", "west", "east"};
    public boolean hasChanges = false;
    public String manifestPath;
    public JSONObject metaObj;

    public ClientBlocksJsonProvider(String manifestPath) {
        this.manifestPath = manifestPath;
    }

    public InputStream getInputStream(String fileName) throws IOException {
        if (this.hasChanges && fileName.equals(this.manifestPath)) {
            return new ByteArrayInputStream(this.metaObj.toString().getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        }
        return null;
    }

    public void dumpAtlas() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File("/sdcard/bl_dump_client_blocks.json"));
        fos.write(this.metaObj.toString().getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        fos.close();
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

    public void setBlockTextures(String blockName, int blockId, String[] textureNames, int[] textureOffsets) throws JSONException {
        int side;
        String blockHash = blockName + "." + blockId;
        for (side = 0; side < 6; side++) {
            String[] textureFiles = new String[16];
            for (int damage = 0; damage < 16; damage++) {
                String tn = textureNames[(damage * 6) + side];
                int to = textureOffsets[(damage * 6) + side];
                String textureFile = ScriptManager.terrainMeta.getIcon(tn, to);
                if (textureFile == null) {
                    throw new RuntimeException("Can't find texture " + tn + ":" + to + " when constructing block " + blockName + " (" + blockId + ")");
                }
                textureFiles[damage] = textureFile;
            }
            ScriptManager.terrainMeta.setIcon(blockHash + "_" + sidesNames[side], textureFiles);
        }
        JSONObject obj = this.metaObj.optJSONObject(blockHash);
        if (obj == null) {
            obj = new JSONObject();
            this.metaObj.put(blockHash, obj);
        }
        JSONObject textureObj = new JSONObject();
        for (side = 0; side < 6; side++) {
            textureObj.put(sidesNames[side], blockHash + "_" + sidesNames[side]);
        }
        obj.put("textures", textureObj);
        this.hasChanges = true;
    }

    public void close() throws IOException {
        this.metaObj = null;
        this.hasChanges = false;
    }

    public long getSize(String name) {
        return 0;
    }
}
