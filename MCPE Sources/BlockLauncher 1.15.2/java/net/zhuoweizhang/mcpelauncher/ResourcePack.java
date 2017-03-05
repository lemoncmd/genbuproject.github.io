package net.zhuoweizhang.mcpelauncher;

import android.os.Environment;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import org.json.JSONObject;

public class ResourcePack {
    public File file;

    public ResourcePack(File file) {
        this.file = file;
    }

    public InputStream getInputStream(String name) throws IOException {
        File theFile = new File(this.file, name);
        if (theFile.exists()) {
            return new FileInputStream(theFile);
        }
        return null;
    }

    public String getName() {
        return this.file.getName();
    }

    public static List<String> readAllIds() throws IOException {
        List<String> list = new ArrayList();
        File configFile = new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftpe/resource_packs.txt");
        if (configFile.exists()) {
            byte[] b = new byte[((int) configFile.length())];
            FileInputStream fis = new FileInputStream(configFile);
            fis.read(b);
            fis.close();
            for (String s : new String(b, Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET)).split("\n")) {
                if (s.length() != 0) {
                    list.add(s);
                }
            }
        }
        return list;
    }

    public static List<ResourcePack> getAllResourcePacks() throws IOException {
        List<ResourcePack> list = new ArrayList();
        File resPackDir = new File(Environment.getExternalStorageDirectory(), "games/com.mojang/resource_packs");
        if (resPackDir.exists()) {
            List<String> ids = readAllIds();
            if (ids.size() != 0) {
                File f;
                Map<String, File> mapping = new HashMap();
                Charset utf8Charset = Charset.forName(HttpURLConnectionBuilder.DEFAULT_CHARSET);
                for (File f2 : resPackDir.listFiles()) {
                    File manifestFile = new File(f2, "resources.json");
                    if (manifestFile.exists()) {
                        try {
                            byte[] b = new byte[((int) manifestFile.length())];
                            FileInputStream fis = new FileInputStream(manifestFile);
                            fis.read(b);
                            fis.close();
                            mapping.put(new JSONObject(new String(b, utf8Charset)).getString("pack_id"), f2);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                for (String id : ids) {
                    f2 = (File) mapping.get(id);
                    if (f2 != null) {
                        list.add(new ResourcePack(f2));
                    }
                }
            }
        }
        return list;
    }
}
