package net.zhuoweizhang.mcpelauncher.texture;

import android.content.Context;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.TexturePack;
import net.zhuoweizhang.mcpelauncher.ZipTexturePack;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import net.zhuoweizhang.mcpelauncher.ui.MainMenuOptionsActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TexturePackLoader {
    public static final String TYPE_ADDON = "addon";
    public static final String TYPE_MPKG = "mpkg";
    public static final String TYPE_ZIP = "zip";

    public static List<TexturePackDescription> loadDescriptions(Context context) throws JSONException {
        JSONArray arr = new JSONArray(context.getSharedPreferences(MainMenuOptionsActivity.PREFERENCES_NAME, 0).getString("texture_packs", "[]"));
        int arrLength = arr.length();
        List<TexturePackDescription> descs = new ArrayList(arrLength);
        for (int i = 0; i < arrLength; i++) {
            descs.add(TexturePackDescription.fromJson(arr.getJSONObject(i)));
        }
        return descs;
    }

    public static Set<String> metaToSet(byte[] meta) throws Exception {
        JSONArray metaJson = new JSONArray(new String(meta, HttpURLConnectionBuilder.DEFAULT_CHARSET));
        int len = metaJson.length();
        Set<String> mySet = new HashSet();
        for (int i = 0; i < len; i++) {
            mySet.add(metaJson.getJSONObject(i).getString("name"));
        }
        return mySet;
    }

    public static List<String> metaToList(byte[] meta) throws Exception {
        JSONArray metaJson = new JSONArray(new String(meta, HttpURLConnectionBuilder.DEFAULT_CHARSET));
        int len = metaJson.length();
        List<String> list = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            list.add(metaJson.getJSONObject(i).getString("name"));
        }
        return list;
    }

    public static List<TexturePack> loadTexturePacks(Context context, List<String> list, byte[] terrainMeta, byte[] itemsMeta) throws Exception {
        List<TexturePackDescription> descs = loadDescriptions(context);
        List<TexturePack> packs = new ArrayList(descs.size());
        for (TexturePackDescription d : descs) {
            packs.add(loadTexturePack(d));
        }
        return packs;
    }

    public static List<TexturePackDescription> loadDescriptionsWithIcons(Context context) throws JSONException {
        List<TexturePackDescription> descs = loadDescriptions(context);
        for (TexturePackDescription d : descs) {
            try {
                loadIconForDescription(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return descs;
    }

    public static void loadIconForDescription(TexturePackDescription d) throws Exception {
        TexturePack pack = loadTexturePack(d);
        doLoadIcon(pack, d);
        doLoadMeta(pack, d);
    }

    private static void doLoadIcon(TexturePack pack, TexturePackDescription d) throws Exception {
        InputStream is = pack.getInputStream("pack.png");
        if (is != null) {
            d.img = BitmapFactory.decodeStream(is);
            is.close();
        }
    }

    private static void doLoadMeta(TexturePack pack, TexturePackDescription d) throws Exception {
        InputStream is = pack.getInputStream("pack.mcmeta");
        if (is != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] a = new byte[EnchantType.fishingRod];
            while (true) {
                int p = is.read(a);
                if (p != -1) {
                    bos.write(a, 0, p);
                } else {
                    is.close();
                    d.description = new JSONObject(new String(a, HttpURLConnectionBuilder.DEFAULT_CHARSET)).getJSONObject("pack").getString("description");
                    return;
                }
            }
        }
    }

    private static TexturePack loadTexturePack(TexturePackDescription desc) throws Exception {
        if (desc.type.equals(TYPE_ZIP) || desc.type.equals(TYPE_MPKG)) {
            return new ZipTexturePack(new File(desc.path));
        }
        throw new RuntimeException("Unsupported texture pack type: " + desc);
    }

    public static String describeTexturePack(Context context, TexturePackDescription desc) {
        String name = desc.path;
        if (desc.type.equals(TYPE_ZIP) || desc.type.equals(TYPE_MPKG)) {
            return name.substring(name.lastIndexOf("/") + 1);
        }
        return name;
    }

    public static void saveDescriptions(Context context, List<TexturePackDescription> descs) throws JSONException {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < descs.size(); i++) {
            arr.put(i, ((TexturePackDescription) descs.get(i)).toJson());
        }
        context.getSharedPreferences(MainMenuOptionsActivity.PREFERENCES_NAME, 0).edit().putString("texture_packs", arr.toString()).commit();
    }

    public static boolean isCompatible(TexturePack pack, List<String> terrainMeta, List<String> itemsMeta) throws Exception {
        return isCompatibleArray(pack, "assets/images/terrain.meta", terrainMeta) && isCompatibleArray(pack, "assets/images/items.meta", itemsMeta);
    }

    private static boolean isCompatibleArray(TexturePack pack, String name, List<String> realMeta) throws Exception {
        InputStream myMetaIs = pack.getInputStream(name);
        if (myMetaIs == null) {
            return true;
        }
        byte[] myMetaBuffer = new byte[((int) pack.getSize(name))];
        myMetaIs.read(myMetaBuffer);
        myMetaIs.close();
        Set<String> mySet = metaToSet(myMetaBuffer);
        for (String s : realMeta) {
            if (!mySet.contains(s)) {
                return false;
            }
        }
        return true;
    }
}
