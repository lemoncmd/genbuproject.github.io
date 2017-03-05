package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Typeface;
import java.util.HashMap;

public class FontManager {
    private static FontManager instance = new FontManager();
    private HashMap<String, Typeface> fonts;

    public static FontManager Instance() {
        return instance;
    }

    public Typeface getTypeface(Context context, String str) {
        if (this.fonts == null) {
            this.fonts = new HashMap();
        }
        if (!this.fonts.containsKey(str)) {
            this.fonts.put(str, Typeface.createFromAsset(context.getAssets(), str));
        }
        return (Typeface) this.fonts.get(str);
    }
}
