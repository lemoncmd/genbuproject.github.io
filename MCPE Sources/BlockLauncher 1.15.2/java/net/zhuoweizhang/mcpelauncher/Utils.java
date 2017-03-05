package net.zhuoweizhang.mcpelauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry;
import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.ui.MainMenuOptionsActivity;
import org.mozilla.javascript.regexp.NativeRegExp;

public class Utils {
    protected static Context mContext = null;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static void clearDirectory(File dir) {
        File[] fileList = dir.listFiles();
        if (fileList != null) {
            for (File f : fileList) {
                if (f.isDirectory()) {
                    clearDirectory(f);
                }
                f.delete();
            }
        }
    }

    public static Field getDeclaredFieldRecursive(Class<?> clazz, String name) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return getDeclaredFieldRecursive(clazz.getSuperclass(), name);
        }
    }

    public static void setLanguageOverride() {
        requireInit();
        String override = getPrefs(0).getString("zz_language_override", BuildConfig.FLAVOR);
        if (override.length() != 0) {
            String[] overrideSplit = override.split("_");
            String langName = overrideSplit[0];
            String countryName = overrideSplit.length > 1 ? overrideSplit[1] : BuildConfig.FLAVOR;
            Resources rez = mContext.getResources();
            Configuration config = new Configuration(rez.getConfiguration());
            DisplayMetrics metrics = rez.getDisplayMetrics();
            config.locale = new Locale(langName, countryName);
            rez.updateConfiguration(config, metrics);
        }
    }

    public static String join(Collection<?> list, String replacement) {
        StringBuilder b = new StringBuilder();
        for (Object item : list) {
            b.append(replacement).append(item.toString());
        }
        String r = b.toString();
        if (r.length() >= replacement.length()) {
            return r.substring(replacement.length());
        }
        return r;
    }

    public static String joinArray(Object[] arr, String sep) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            if (i != 0) {
                b.append(sep);
            }
            b.append(arr[i] == null ? "null" : arr[i].toString());
        }
        return b.toString();
    }

    public static boolean hasTooManyPatches() {
        int maxPatchCount = getMaxPatches();
        return maxPatchCount >= 0 && getEnabledPatches().size() >= maxPatchCount;
    }

    public static boolean hasTooManyScripts() {
        int maxPatchCount = getMaxScripts();
        return maxPatchCount >= 0 && getEnabledScripts().size() >= maxPatchCount;
    }

    public static int getMaxPatches() {
        return mContext.getResources().getInteger(R.integer.max_num_patches);
    }

    public static int getMaxScripts() {
        return mContext.getResources().getInteger(R.integer.max_num_scripts);
    }

    public static Set<String> getEnabledPatches() {
        String theStr = getPrefs(1).getString("enabledPatches", BuildConfig.FLAVOR);
        if (theStr.equals(BuildConfig.FLAVOR)) {
            return new HashSet();
        }
        return new HashSet(Arrays.asList(theStr.split(";")));
    }

    public static Set<String> getEnabledScripts() {
        String theStr = getPrefs(1).getString("enabledScripts", BuildConfig.FLAVOR);
        if (theStr.equals(BuildConfig.FLAVOR)) {
            return new HashSet();
        }
        return new HashSet(Arrays.asList(theStr.split(";")));
    }

    public static boolean isSafeMode() {
        return (MainActivity.libLoaded && MainActivity.tempSafeMode) || getPrefs(0).getBoolean("zz_safe_mode", false);
    }

    public static boolean isPro() {
        return mContext.getPackageName().equals(MainMenuOptionsActivity.PRO_APP_ID);
    }

    public static SharedPreferences getPrefs(int type) {
        requireInit();
        switch (type) {
            case NativeRegExp.TEST /*0*/:
                return PreferenceManager.getDefaultSharedPreferences(mContext);
            case NativeRegExp.MATCH /*1*/:
                return mContext.getSharedPreferences(MainMenuOptionsActivity.PREFERENCES_NAME, 0);
            case NativeRegExp.PREFIX /*2*/:
                return mContext.getSharedPreferences("safe_mode_counter", 0);
            default:
                return null;
        }
    }

    public static boolean hasExtrasPackage(Context context) {
        return context.getPackageName().equals(MainMenuOptionsActivity.PRO_APP_ID);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static long parseMemInfo() throws java.io.IOException {
        /*
        r3 = 0;
        r4 = new java.io.BufferedReader;	 Catch:{ all -> 0x0058 }
        r6 = new java.io.FileReader;	 Catch:{ all -> 0x0058 }
        r7 = "/proc/meminfo";
        r6.<init>(r7);	 Catch:{ all -> 0x0058 }
        r4.<init>(r6);	 Catch:{ all -> 0x0058 }
    L_0x000d:
        r0 = r4.readLine();	 Catch:{ all -> 0x0065 }
        if (r0 == 0) goto L_0x004d;
    L_0x0013:
        r6 = ":";
        r6 = r0.contains(r6);	 Catch:{ all -> 0x0065 }
        if (r6 == 0) goto L_0x000d;
    L_0x001b:
        r6 = ":";
        r2 = r0.split(r6);	 Catch:{ all -> 0x0065 }
        r6 = 0;
        r6 = r2[r6];	 Catch:{ all -> 0x0065 }
        r1 = r6.trim();	 Catch:{ all -> 0x0065 }
        r6 = 1;
        r6 = r2[r6];	 Catch:{ all -> 0x0065 }
        r6 = r6.trim();	 Catch:{ all -> 0x0065 }
        r7 = " ";
        r5 = r6.split(r7);	 Catch:{ all -> 0x0065 }
        r6 = "MemTotal";
        r6 = r1.equals(r6);	 Catch:{ all -> 0x0065 }
        if (r6 == 0) goto L_0x000d;
    L_0x003d:
        r6 = 0;
        r6 = r5[r6];	 Catch:{ all -> 0x0065 }
        r6 = java.lang.Long.parseLong(r6);	 Catch:{ all -> 0x0065 }
        r8 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        r6 = r6 * r8;
        if (r4 == 0) goto L_0x004c;
    L_0x0049:
        r4.close();	 Catch:{ IOException -> 0x005f }
    L_0x004c:
        return r6;
    L_0x004d:
        if (r4 == 0) goto L_0x0052;
    L_0x004f:
        r4.close();	 Catch:{ IOException -> 0x0061 }
    L_0x0052:
        r6 = 17179869184; // 0x400000000 float:0.0 double:8.487983164E-314;
        goto L_0x004c;
    L_0x0058:
        r6 = move-exception;
    L_0x0059:
        if (r3 == 0) goto L_0x005e;
    L_0x005b:
        r3.close();	 Catch:{ IOException -> 0x0063 }
    L_0x005e:
        throw r6;
    L_0x005f:
        r8 = move-exception;
        goto L_0x004c;
    L_0x0061:
        r6 = move-exception;
        goto L_0x0052;
    L_0x0063:
        r7 = move-exception;
        goto L_0x005e;
    L_0x0065:
        r6 = move-exception;
        r3 = r4;
        goto L_0x0059;
        */
        throw new UnsupportedOperationException("Method not decompiled: net.zhuoweizhang.mcpelauncher.Utils.parseMemInfo():long");
    }

    public static int getElfArch(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] header = new byte[2];
        fis.skip(18);
        fis.read(header, 0, 2);
        int arch = header[0] | (header[1] << 8);
        fis.close();
        if (arch == 40) {
            return 0;
        }
        if (arch == 3) {
            return 1;
        }
        System.err.println(file + " has unknown architecture 0x" + Integer.toString(arch, 16));
        return 0;
    }

    public static String getArchName(int arch) {
        switch (arch) {
            case NativeRegExp.TEST /*0*/:
                return "ARM";
            case NativeRegExp.MATCH /*1*/:
                return "Intel";
            default:
                return UTCTelemetry.UNKNOWNPAGE;
        }
    }

    public static void setupTheme(Context context, boolean fullscreen) {
        if (getPrefs(0).getBoolean("zz_theme_dark", false)) {
            context.setTheme(fullscreen ? R.style.FullscreenDarkTheme : R.style.BlockLauncherDarkTheme);
        }
    }

    protected static void requireInit() {
        if (mContext == null) {
            throw new RuntimeException("Tried to work with Utils class without context");
        }
    }
}
