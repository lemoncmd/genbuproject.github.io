package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore.Settings;
import java.util.Arrays;
import java.util.zip.Deflater;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;

public class EventCompressor {
    private final String TAG = "AndroidCll-EventCompressor";
    private final ILogger logger;

    public EventCompressor(ILogger iLogger) {
        this.logger = iLogger;
    }

    public byte[] compress(String str) {
        byte[] bArr = null;
        try {
            byte[] bytes = str.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET);
            byte[] bArr2 = new byte[SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSIZEINBYTES)];
            Deflater deflater = new Deflater(-1, true);
            deflater.setInput(bytes);
            deflater.finish();
            int deflate = deflater.deflate(bArr2);
            if (deflate >= SettingsStore.getCllSettingsAsInt(Settings.MAXEVENTSIZEINBYTES)) {
                this.logger.error("AndroidCll-EventCompressor", "Compression resulted in a string of at least the max event buffer size of Vortex. Most likely this means we lost part of the string.");
            } else {
                bArr = Arrays.copyOfRange(bArr2, 0, deflate);
            }
        } catch (Exception e) {
            this.logger.error("AndroidCll-EventCompressor", "Could not compress events");
        }
        return bArr;
    }
}
