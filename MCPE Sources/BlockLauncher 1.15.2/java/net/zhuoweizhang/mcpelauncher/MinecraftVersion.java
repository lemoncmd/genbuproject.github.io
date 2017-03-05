package net.zhuoweizhang.mcpelauncher;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public final class MinecraftVersion {
    public static final boolean FUZZY_VERSION = true;
    public static MinecraftVersion amazonVer;
    public static Context context;
    public static Map<Integer, MinecraftVersion> versions = new HashMap();
    public byte[] guiBlocksPatch;
    public byte[] guiBlocksUnpatch;
    public int ipAddressOffset;
    public int libLoadOffset;
    public int libLoadOffsetBegin;
    public boolean needsWarning;
    public byte[] noAnimationPatch;
    public byte[] noAnimationUnpatch;
    public int portOffset;
    public PatchTranslator translator;
    public int versionCode;

    public static abstract class PatchTranslator {
        public abstract int get(int i);
    }

    public static class AmazonTranslator080 extends PatchTranslator {
        public int get(int addr) {
            return addr - 40;
        }
    }

    public static class AmazonTranslator extends PatchTranslator {
        public int get(int addr) {
            if (addr < 896608) {
                return addr + 64;
            }
            return addr + 24;
        }
    }

    static {
        add(new MinecraftVersion(MinecraftConstants.MINECRAFT_VERSION_CODE, false, MinecraftConstants.LIB_LOAD_OFFSET_BEGIN, EnchantType.fishingRod, null, -1, null, null, null, null, -1));
        add(new MinecraftVersion(300801011, false, MinecraftConstants.LIB_LOAD_OFFSET_BEGIN, EnchantType.fishingRod, null, -1, null, null, null, null, -1));
        add(new MinecraftVersion(400801011, false, MinecraftConstants.LIB_LOAD_OFFSET_BEGIN, EnchantType.fishingRod, new AmazonTranslator080(), -1, null, null, null, null, -1));
    }

    public MinecraftVersion(int versionCode, boolean needsWarning, int libLoadOffsetBegin, int libLoadOffset, PatchTranslator translator, int ipAddressOffset, byte[] guiBlocksPatch, byte[] guiBlocksUnpatch, byte[] noAnimationPatch, byte[] noAnimationUnpatch, int portOffset) {
        this.versionCode = versionCode;
        this.needsWarning = needsWarning;
        this.libLoadOffsetBegin = libLoadOffsetBegin;
        this.libLoadOffset = libLoadOffset;
        this.ipAddressOffset = ipAddressOffset;
        this.guiBlocksPatch = guiBlocksPatch;
        this.guiBlocksUnpatch = guiBlocksUnpatch;
        this.noAnimationPatch = noAnimationPatch;
        this.noAnimationUnpatch = noAnimationUnpatch;
        this.portOffset = portOffset;
        this.translator = translator;
    }

    public static void add(MinecraftVersion version) {
        versions.put(Integer.valueOf(version.versionCode), version);
    }

    public static MinecraftVersion getRaw(int versionCode) {
        MinecraftVersion ver = (MinecraftVersion) versions.get(Integer.valueOf(versionCode));
        if (ver == null) {
            ver = getDefault();
        }
        if (ver != null && ver.versionCode == MinecraftConstants.MINECRAFT_VERSION_CODE && isAmazon()) {
            return amazonVer;
        }
        return ver;
    }

    public static MinecraftVersion get(int versionCode) {
        MinecraftVersion ver = (MinecraftVersion) versions.get(Integer.valueOf(versionCode));
        if (ver == null) {
            ver = getDefault();
        }
        if (ver.versionCode == MinecraftConstants.MINECRAFT_VERSION_CODE && isAmazon()) {
            return amazonVer;
        }
        return ver;
    }

    public static MinecraftVersion get(Context context) {
        try {
            return get(context.getPackageManager().getPackageInfo("com.mojang.minecraftpe", 0).versionCode);
        } catch (Exception e) {
            return getDefault();
        }
    }

    public static MinecraftVersion getDefault() {
        MinecraftVersion ver = (MinecraftVersion) versions.get(Integer.valueOf(MinecraftConstants.MINECRAFT_VERSION_CODE));
        if (isAmazon()) {
            return amazonVer;
        }
        return ver;
    }

    public static boolean isAmazon() {
        return false;
    }
}
