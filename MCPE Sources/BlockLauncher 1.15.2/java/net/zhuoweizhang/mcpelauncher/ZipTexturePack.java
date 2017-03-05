package net.zhuoweizhang.mcpelauncher;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ZipTexturePack implements TexturePack {
    private HashMap<String, ZipEntry> entries = new HashMap();
    private File file;
    private HashMap<String, ZipEntry> fullNameEntries = new HashMap();
    private ZipFile zipFile;

    public ZipTexturePack(File file) throws ZipException, IOException {
        this.file = file;
        this.zipFile = new ZipFile(file);
        putZipEntriesIntoMap();
    }

    private void putZipEntriesIntoMap() {
        Enumeration<? extends ZipEntry> i = this.zipFile.entries();
        while (i.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) i.nextElement();
            if (!entry.getName().contains("__MACOSX")) {
                this.fullNameEntries.put(entry.getName(), entry);
                this.entries.put(getFilenameOnly(entry.getName()), entry);
            }
        }
    }

    public InputStream getInputStream(String fileName) throws IOException {
        ZipEntry realEntry = getEntry(fileName);
        if (realEntry == null) {
            return null;
        }
        return this.zipFile.getInputStream(realEntry);
    }

    public long getSize(String fileName) throws IOException {
        ZipEntry realEntry = getEntry(fileName);
        if (realEntry == null) {
            return -1;
        }
        return realEntry.getSize();
    }

    private ZipEntry getEntry(String fileName) {
        ZipEntry entry = (ZipEntry) this.fullNameEntries.get(fileName);
        if (entry != null) {
            return entry;
        }
        entry = (ZipEntry) this.fullNameEntries.get("assets/" + fileName);
        if (entry != null) {
            return entry;
        }
        return (ZipEntry) this.entries.get(getFilenameOnly(fileName));
    }

    public void close() throws IOException {
        this.zipFile.close();
    }

    private static String getFilenameOnly(String location) {
        String[] segments = location.split("/");
        return segments[segments.length - 1];
    }

    public List<String> listFiles() throws IOException {
        Enumeration<? extends ZipEntry> i = this.zipFile.entries();
        List<String> list = new ArrayList();
        while (i.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) i.nextElement();
            if (!entry.getName().contains("__MACOSX")) {
                list.add(entry.getName());
            }
        }
        return list;
    }

    public String getZipName() {
        return this.file.getName();
    }
}
