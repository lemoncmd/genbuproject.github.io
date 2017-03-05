package net.zhuoweizhang.mcpelauncher.texture;

import com.mojang.minecraftpe.MainActivity;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.TexturePack;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class TextureListProvider implements TexturePack {
    public List<String> addedFiles = new ArrayList();
    public Set<String> addedFilesSet = new HashSet();
    public List<String> files;
    public boolean hasChanges = false;
    public String manifestPath;

    public TextureListProvider(String manifestPath) {
        this.manifestPath = manifestPath;
    }

    public InputStream getInputStream(String fileName) throws IOException {
        if (!this.hasChanges || !fileName.equals(this.manifestPath)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : this.files) {
            builder.append(s);
            builder.append('\n');
        }
        return new ByteArrayInputStream(builder.toString().getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
    }

    public void dumpAtlas() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File("/sdcard/bl_dump_textures_list.txt"));
        StringBuilder builder = new StringBuilder();
        for (String s : this.files) {
            builder.append(s);
            builder.append('\n');
        }
        fos.write(builder.toString().getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
        fos.close();
    }

    public List<String> listFiles() throws IOException {
        return new ArrayList();
    }

    public void init(MainActivity activity) throws Exception {
        this.hasChanges = false;
        loadTextureList(activity);
        addExtraTextures(activity);
    }

    private void loadTextureList(MainActivity activity) throws Exception {
        InputStream metaIs = activity.getInputStreamForAsset(this.manifestPath);
        if (metaIs == null) {
            this.files = new ArrayList();
            return;
        }
        byte[] a = new byte[EnchantType.fishingRod];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while (true) {
            int p = metaIs.read(a);
            if (p == -1) {
                break;
            }
            bos.write(a, 0, p);
        }
        metaIs.close();
        String[] strs = new String(bos.toByteArray(), HttpURLConnectionBuilder.DEFAULT_CHARSET).split("\n");
        this.files = new ArrayList(strs.length);
        for (String str : strs) {
            if (str.length() > 0) {
                this.files.add(str);
            }
        }
    }

    private void addExtraTextures(MainActivity activity) throws IOException {
        Set<String> theSet = new HashSet(this.files);
        for (TexturePack pack : activity.textureOverrides) {
            for (String rawS : pack.listFiles()) {
                String rawS2 = TextureUtils.removeExtraDotsFromPath(rawS2);
                this.addedFiles.add(rawS2);
                this.addedFilesSet.add(rawS2.substring(rawS2.lastIndexOf("/") + 1));
                int lastIndex = rawS2.lastIndexOf(46);
                if (lastIndex != -1) {
                    String s = rawS2.substring(0, lastIndex);
                    if (theSet.add(s)) {
                        this.files.add(s);
                        this.hasChanges = true;
                    }
                }
            }
        }
    }

    public boolean containsFile(String file) {
        return this.addedFilesSet.contains(file.substring(file.lastIndexOf("/") + 1));
    }

    public Set<String> listDir(String dirPath) {
        String prefix = dirPath + "/";
        Set<String> outList = new HashSet();
        for (String path : this.addedFiles) {
            if (path.startsWith(prefix) && path.indexOf("/", prefix.length()) == -1) {
                outList.add(path.substring(path.lastIndexOf("/")));
            }
        }
        return outList;
    }

    public void close() throws IOException {
        this.files = null;
        this.hasChanges = false;
    }

    public long getSize(String name) {
        return 0;
    }
}
