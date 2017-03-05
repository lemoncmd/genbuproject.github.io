package org.fmod;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Build.VERSION;

public class FMOD {
    private static Context gContext = null;

    public static void init(Context context) {
        gContext = context;
    }

    public static void close() {
        gContext = null;
    }

    public static boolean checkInit() {
        return gContext != null;
    }

    public static AssetManager getAssetManager() {
        return gContext != null ? gContext.getAssets() : null;
    }

    public static boolean supportsLowLatency() {
        if (gContext == null || VERSION.SDK_INT < 5) {
            return false;
        }
        return gContext.getPackageManager().hasSystemFeature("android.hardware.audio.low_latency");
    }

    public static int getOutputSampleRate() {
        if (gContext != null && VERSION.SDK_INT >= 17) {
            String property = ((AudioManager) gContext.getSystemService("audio")).getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
            if (property != null) {
                return Integer.parseInt(property);
            }
        }
        return 0;
    }

    public static int getOutputBlockSize() {
        if (gContext != null && VERSION.SDK_INT >= 17) {
            String property = ((AudioManager) gContext.getSystemService("audio")).getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
            if (property != null) {
                return Integer.parseInt(property);
            }
        }
        return 0;
    }
}
