package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore.Settings;
import java.util.Random;
import net.hockeyapp.android.BuildConfig;

public class CorrelationVector {
    private final String base64CharSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private String baseVector;
    private int currentVector;
    private final int id0Length = 16;
    boolean isInitialized = false;

    private boolean CanExtend() {
        return ((((int) Math.floor(1.0d + Math.log10((double) this.currentVector))) + (this.baseVector.length() + 1)) + 1) + 1 <= SettingsStore.getCllSettingsAsInt(Settings.MAXCORRELATIONVECTORLENGTH);
    }

    private boolean CanIncrement(int i) {
        return i + -1 != Integer.MAX_VALUE && (((int) Math.floor(1.0d + Math.log10((double) i))) + this.baseVector.length()) + 1 <= SettingsStore.getCllSettingsAsInt(Settings.MAXCORRELATIONVECTORLENGTH);
    }

    private String SeedCorrelationVector() {
        String str = BuildConfig.FLAVOR;
        Random random = new Random();
        for (int i = 0; i < 16; i++) {
            str = str + "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".charAt(random.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".length()));
        }
        return str;
    }

    public String Extend() {
        String GetValue;
        synchronized (this) {
            if (!this.isInitialized) {
                Init();
            }
            if (CanExtend()) {
                this.baseVector = GetValue();
                this.currentVector = 1;
            }
            GetValue = GetValue();
        }
        return GetValue;
    }

    public String GetValue() {
        return !this.isInitialized ? null : this.baseVector + "." + this.currentVector;
    }

    public String Increment() {
        String GetValue;
        synchronized (this) {
            if (!this.isInitialized) {
                Init();
            }
            int i = this.currentVector + 1;
            if (CanIncrement(i)) {
                this.currentVector = i;
            }
            GetValue = GetValue();
        }
        return GetValue;
    }

    public void Init() {
        this.baseVector = SeedCorrelationVector();
        this.currentVector = 1;
        this.isInitialized = true;
    }

    boolean IsValid(String str) {
        return str.length() <= SettingsStore.getCllSettingsAsInt(Settings.MAXCORRELATIONVECTORLENGTH) && str.matches("^[ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/]{16}(.[0-9]+)+$");
    }

    public void SetValue(String str) {
        synchronized (this) {
            if (IsValid(str)) {
                int lastIndexOf = str.lastIndexOf(".");
                this.baseVector = str.substring(0, lastIndexOf);
                this.currentVector = Integer.parseInt(str.substring(lastIndexOf + 1));
                this.isInitialized = true;
            } else {
                throw new IllegalArgumentException("Cannot set invalid correlation vector value");
            }
        }
    }
}
