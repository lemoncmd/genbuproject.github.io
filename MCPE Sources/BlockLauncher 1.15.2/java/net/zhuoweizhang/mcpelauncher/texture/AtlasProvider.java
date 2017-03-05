package net.zhuoweizhang.mcpelauncher.texture;

import com.microsoft.onlineid.internal.profile.DownloadProfileImageTask;
import com.mojang.minecraftpe.MainActivity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.hockeyapp.android.UpdateActivity;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.TexturePack;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AtlasProvider implements TexturePack {
    public List<String[]> addedTextureNames = new ArrayList();
    public boolean hasChanges = false;
    public String importDir;
    public String metaName;
    public JSONObject metaObj;
    public String textureNamePrefix;

    public AtlasProvider(String metaName, String importDir, String textureNamePrefix) {
        this.metaName = metaName;
        this.importDir = importDir;
        this.textureNamePrefix = textureNamePrefix;
    }

    public InputStream getInputStream(String fileName) throws IOException {
        if (this.hasChanges && fileName.equals(this.metaName)) {
            return new ByteArrayInputStream(this.metaObj.toString().getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        }
        return null;
    }

    public void dumpAtlas() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File("/sdcard/bl_dump_" + this.textureNamePrefix + UpdateActivity.EXTRA_JSON));
        fos.write(this.metaObj.toString().getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        fos.close();
    }

    public List<String> listFiles() throws IOException {
        return new ArrayList();
    }

    public void initAtlas(MainActivity activity) throws Exception {
        this.hasChanges = false;
        loadAtlas(activity);
        this.hasChanges = addAllToMeta(activity);
    }

    private void loadAtlas(MainActivity activity) throws Exception {
        InputStream metaIs = activity.getInputStreamForAsset(this.metaName);
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

    private boolean addAllToMeta(MainActivity activity) throws Exception {
        List<String> pathsForMeta = TextureUtils.getAllFilesFilter(activity.textureOverrides, this.importDir);
        if (pathsForMeta.size() == 0) {
            return false;
        }
        Object[] nameParts = new Object[2];
        for (int i = pathsForMeta.size() - 1; i >= 0; i--) {
            String filePath = (String) pathsForMeta.get(i);
            if (filePath.toLowerCase().endsWith(DownloadProfileImageTask.UserTileExtension)) {
                parseNameParts(filePath, nameParts);
                if (nameParts[0] != null) {
                    addFileIntoObj(filePath, nameParts);
                }
            }
        }
        return true;
    }

    private void parseNameParts(String filePath, Object[] nameParts) {
        nameParts[0] = null;
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
        int underscoreIndex = fileName.lastIndexOf("_");
        if (underscoreIndex >= 0) {
            String name = fileName.substring(0, underscoreIndex);
            try {
                nameParts[1] = Integer.valueOf(Integer.parseInt(fileName.substring(underscoreIndex + 1)));
                nameParts[0] = name;
            } catch (NumberFormatException e) {
            }
        }
    }

    private void addFileIntoObj(String filePath, Object[] nameParts) throws JSONException {
        String textureResName;
        filePath = TextureUtils.removeExtraDotsFromPath(filePath);
        String texName = nameParts[0];
        int texIndex = ((Integer) nameParts[1]).intValue();
        int index = filePath.lastIndexOf(".");
        if (index != -1) {
            textureResName = filePath.substring(0, index);
        } else {
            textureResName = filePath;
        }
        JSONObject obj = this.metaObj.getJSONObject("texture_data").optJSONObject(texName);
        if (obj == null) {
            obj = new JSONObject();
            this.metaObj.getJSONObject("texture_data").put(texName, obj);
        }
        JSONArray arr = obj.optJSONArray("textures");
        if (arr == null) {
            arr = new JSONArray();
            obj.put("textures", arr);
        }
        if (texIndex < arr.length()) {
            arr.put(texIndex, textureResName);
        } else {
            for (int i = arr.length(); i <= texIndex; i++) {
                arr.put(i, textureResName);
            }
        }
        this.addedTextureNames.add(new String[]{textureResName, filePath});
    }

    public boolean hasIcon(String name, int index) {
        boolean z = true;
        try {
            JSONObject obj = this.metaObj.getJSONObject("texture_data").optJSONObject(name);
            if (obj == null) {
                return false;
            }
            JSONArray arr = obj.optJSONArray("textures");
            if (arr == null) {
                if (index != 0 || obj.optString("textures") == null) {
                    z = false;
                }
                return z;
            }
            if (index >= arr.length()) {
                z = false;
            }
            return z;
        } catch (JSONException je) {
            je.printStackTrace();
            return false;
        }
    }

    public String getIcon(String name, int index) {
        try {
            JSONObject obj = this.metaObj.getJSONObject("texture_data").optJSONObject(name);
            if (obj == null) {
                return null;
            }
            JSONArray arr = obj.optJSONArray("textures");
            if (arr != null) {
                return arr.optString(index);
            }
            if (index == 0) {
                return obj.optString("textures");
            }
            return null;
        } catch (JSONException je) {
            je.printStackTrace();
            return null;
        }
    }

    public void setIcon(String texName, String[] textures) throws JSONException {
        JSONObject obj = this.metaObj.getJSONObject("texture_data").optJSONObject(texName);
        if (obj == null) {
            obj = new JSONObject();
            this.metaObj.getJSONObject("texture_data").put(texName, obj);
        }
        obj.put("textures", new JSONArray(Arrays.asList(textures)));
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
