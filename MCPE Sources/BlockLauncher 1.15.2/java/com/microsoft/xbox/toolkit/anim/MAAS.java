package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.XMLHelper;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

public class MAAS {
    private static MAAS instance = new MAAS();
    private final String ASSET_FILENAME = "animation/%sAnimation.xml";
    private final String SDCARD_FILENAME = "/sdcard/bishop/maas/%sAnimation.xml";
    private Hashtable<String, MAASAnimation> maasFileCache = new Hashtable();
    private boolean usingSdcard = false;

    public enum MAASAnimationType {
        ANIMATE_IN,
        ANIMATE_OUT
    }

    public static MAAS getInstance() {
        return instance;
    }

    private MAASAnimation getMAASFile(String str) {
        if (!this.maasFileCache.containsKey(str)) {
            MAASAnimation loadMAASFile = loadMAASFile(str);
            if (loadMAASFile != null) {
                this.maasFileCache.put(str, loadMAASFile);
            }
        }
        return (MAASAnimation) this.maasFileCache.get(str);
    }

    private MAASAnimation loadMAASFile(String str) {
        try {
            InputStream fileInputStream;
            if (this.usingSdcard) {
                fileInputStream = new FileInputStream(new File(String.format("/sdcard/bishop/maas/%sAnimation.xml", new Object[]{str})));
            } else {
                fileInputStream = XboxTcuiSdk.getAssetManager().open(String.format("animation/%sAnimation.xml", new Object[]{str}));
            }
            return (MAASAnimation) XMLHelper.instance().load(fileInputStream, MAASAnimation.class);
        } catch (Exception e) {
            return null;
        }
    }

    public MAASAnimation getAnimation(String str) {
        if (str != null) {
            return getMAASFile(str);
        }
        throw new IllegalArgumentException();
    }
}
