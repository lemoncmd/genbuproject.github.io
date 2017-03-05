package net.zhuoweizhang.mcpelauncher;

import android.content.Context;
import android.os.Build.VERSION;
import android.util.Log;
import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Scanner;
import net.zhuoweizhang.pokerface.PokerFace;

public final class MaraudersMap {
    public static ByteBuffer minecraftTextBuffer = null;
    private static boolean patchingInitialized = false;

    private static native long remapText(long j, long j2, String str);

    private static native void setTranslationFunction(String str);

    public static boolean initPatching(Context context, long minecraftLibLength) throws Exception {
        if (patchingInitialized) {
            return true;
        }
        PokerFace.init();
        boolean useOldCode = Utils.getPrefs(0).getBoolean("zz_legacy_live_patch", false) || ScriptManager.nativeGetArch() == 1 || new File("/sdcard/blocklauncher_marauders_map_legacy").exists() || VERSION.SDK_INT >= 23;
        System.out.println("Live patching is running in " + (useOldCode ? "legacy" : "normal") + " mode");
        boolean success = true;
        patchingInitialized = true;
        Scanner scanner = new Scanner(new File("/proc/self/maps"));
        File patchedDir = context.getDir("patched", 0);
        while (scanner.hasNextLine()) {
            String[] parts = scanner.nextLine().split(" ");
            if (parts[parts.length - 1].indexOf("libminecraftpe.so") >= 0) {
                long loc = Long.parseLong(parts[0].substring(0, parts[0].indexOf("-")), 16);
                long len = Long.parseLong(parts[0].substring(parts[0].indexOf("-") + 1), 16) - loc;
                if (parts[1].indexOf("x") >= 0) {
                    long newLoc;
                    if (useOldCode) {
                        newLoc = loc;
                        if (PokerFace.mprotect(loc, len, 7) < 0) {
                            success = false;
                        }
                    } else {
                        newLoc = remapText(loc, len, new File(patchedDir, "libminecraftpe_text_section").getAbsolutePath());
                    }
                    success = success && newLoc >= 0;
                    if (newLoc > 0) {
                        MainActivity.minecraftLibBuffer = PokerFace.createDirectByteBuffer(loc, minecraftLibLength);
                        minecraftTextBuffer = PokerFace.createDirectByteBuffer(newLoc, len);
                        Log.i(KamcordConstants.GAME_NAME, "libminecraftpe.so mapped at 0x" + Long.toString(loc, 16));
                    }
                } else if (PokerFace.mprotect(loc, len, 3) < 0) {
                    success = false;
                }
            }
        }
        scanner.close();
        setTranslationFunction(new File(patchedDir, "tempXXXXXX").getAbsolutePath());
        return success;
    }
}
