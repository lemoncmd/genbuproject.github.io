package net.zhuoweizhang.mcpelauncher;

import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AddonOverrideTexturePack implements TexturePack {
    private final MainActivity activity;
    private final Map<String, ZipFile> assets = new HashMap();
    private final String prefix;
    private final Map<String, ZipFile> zipsByPackage = new HashMap();

    public AddonOverrideTexturePack(MainActivity activity, String prefix) {
        this.activity = activity;
        this.prefix = prefix;
        initAddons();
    }

    private void initAddons() {
        MainActivity mainActivity = this.activity;
        for (String packageName : MainActivity.loadedAddons) {
            System.out.println("Addon textures: " + packageName);
            try {
                addPackage(new File(this.activity.getPackageManager().getPackageInfo(packageName, 0).applicationInfo.publicSourceDir), packageName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<String, ZipFile> getZipsByPackage() {
        return this.zipsByPackage;
    }

    public void addPackage(File myZip, String packageName) throws IOException {
        ZipFile zipFile = new ZipFile(myZip);
        Enumeration<? extends ZipEntry> i = zipFile.entries();
        while (i.hasMoreElements()) {
            String name = ((ZipEntry) i.nextElement()).getName();
            if (!(name.contains("__MACOSX") || name.indexOf("assets/") != 0 || name.charAt(name.length() - 1) == '/')) {
                this.assets.put(name, zipFile);
            }
        }
        this.zipsByPackage.put(packageName, zipFile);
    }

    public InputStream getInputStream(String fileName) throws IOException {
        if (fileName.startsWith(this.prefix)) {
            fileName = fileName.substring(this.prefix.length());
        }
        String name = "assets/" + fileName;
        ZipFile file = (ZipFile) this.assets.get(name);
        if (file == null) {
            return null;
        }
        return file.getInputStream(file.getEntry(name));
    }

    public long getSize(String fileName) throws IOException {
        if (fileName.startsWith(this.prefix)) {
            fileName = fileName.substring(this.prefix.length());
        }
        String name = "assets/" + fileName;
        ZipFile file = (ZipFile) this.assets.get(name);
        if (file == null) {
            return -1;
        }
        return file.getEntry(name).getSize();
    }

    public void close() throws IOException {
    }

    public List<String> listFiles() throws IOException {
        List<String> list = new ArrayList();
        for (Entry<String, ZipFile> e : this.assets.entrySet()) {
            list.add(((String) e.getKey()).substring("assets/".length()));
        }
        return list;
    }
}
