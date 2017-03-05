package net.zhuoweizhang.mcpelauncher.texture;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipException;
import net.zhuoweizhang.mcpelauncher.TexturePack;
import net.zhuoweizhang.mcpelauncher.ZipTexturePack;

public class ModPkgTexturePack implements TexturePack {
    static Set<String> filterTheseOut = new HashSet();
    public String prefix;
    public List<ZipTexturePack> subPacks = new ArrayList();

    static {
        filterTheseOut.add("images/terrain.meta");
        filterTheseOut.add("images/items.meta");
        filterTheseOut.add("items.json");
        filterTheseOut.add("images/terrain-atlas.tga");
        filterTheseOut.add("images/terrain-atlas_mip0.tga");
        filterTheseOut.add("images/terrain-atlas_mip1.tga");
        filterTheseOut.add("images/terrain-atlas_mip2.tga");
        filterTheseOut.add("images/terrain-atlas_mip3.tga");
        filterTheseOut.add("images/items-opaque.png");
        filterTheseOut.add("resources.json");
        filterTheseOut.add("items.json");
        filterTheseOut.add("blocks.json");
        filterTheseOut.add("images/terrain_texture.json");
        filterTheseOut.add("images/item_texture.json");
    }

    public ModPkgTexturePack(String prefix) {
        this.prefix = prefix;
    }

    public void addPackage(File file) throws ZipException, IOException {
        this.subPacks.add(new ZipTexturePack(file));
    }

    public void removePackage(String fileName) throws IOException {
        for (int i = this.subPacks.size() - 1; i >= 0; i--) {
            ZipTexturePack pack = (ZipTexturePack) this.subPacks.get(i);
            if (pack.getZipName().equals(fileName)) {
                pack.close();
                this.subPacks.remove(i);
                return;
            }
        }
    }

    public InputStream getInputStream(String fileName) throws IOException {
        if (fileName.startsWith(this.prefix)) {
            fileName = fileName.substring(this.prefix.length());
        }
        if (filterTheseOut.contains(fileName)) {
            return null;
        }
        for (ZipTexturePack pack : this.subPacks) {
            InputStream is = pack.getInputStream(fileName);
            if (is != null) {
                return is;
            }
        }
        return null;
    }

    public long getSize(String fileName) throws IOException {
        if (filterTheseOut.contains(fileName)) {
            return -1;
        }
        for (ZipTexturePack pack : this.subPacks) {
            long size = pack.getSize(fileName);
            if (size != -1) {
                return size;
            }
        }
        return -1;
    }

    public void close() throws IOException {
        for (ZipTexturePack pack : this.subPacks) {
            pack.close();
        }
        this.subPacks.clear();
    }

    public List<String> listFiles() throws IOException {
        List<String> retval = new ArrayList();
        for (ZipTexturePack pack : this.subPacks) {
            retval.addAll(pack.listFiles());
        }
        return retval;
    }
}
