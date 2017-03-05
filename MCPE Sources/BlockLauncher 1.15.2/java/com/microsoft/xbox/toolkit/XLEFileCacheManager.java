package com.microsoft.xbox.toolkit;

import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.File;
import java.util.HashMap;

public class XLEFileCacheManager {
    public static XLEFileCache emptyFileCache = new XLEFileCache();
    private static HashMap<String, XLEFileCache> sAllCaches = new HashMap();
    private static HashMap<XLEFileCache, File> sCacheRootDirMap = new HashMap();

    public static XLEFileCache createCache(String str, int i) {
        synchronized (XLEFileCacheManager.class) {
            try {
                XLEFileCache createCache = createCache(str, i, true);
                return createCache;
            } finally {
                Object obj = XLEFileCacheManager.class;
            }
        }
    }

    public static XLEFileCache createCache(String str, int i, boolean z) {
        XLEFileCache xLEFileCache;
        synchronized (XLEFileCacheManager.class) {
            if (i <= 0) {
                try {
                    throw new IllegalArgumentException("maxFileNumber must be > 0");
                } catch (Throwable th) {
                    Class cls = XLEFileCacheManager.class;
                }
            } else {
                if (str != null) {
                    if (str.length() > 0) {
                        xLEFileCache = (XLEFileCache) sAllCaches.get(str);
                        if (xLEFileCache == null) {
                            if (!z) {
                                xLEFileCache = emptyFileCache;
                            } else if (SystemUtil.isSDCardAvailable()) {
                                xLEFileCache = new XLEFileCache(str, i);
                                File file = new File(XboxTcuiSdk.getActivity().getCacheDir(), str);
                                if (!file.exists()) {
                                    file.mkdirs();
                                }
                                xLEFileCache.size = file.list().length;
                                sAllCaches.put(str, xLEFileCache);
                                sCacheRootDirMap.put(xLEFileCache, file);
                            } else {
                                xLEFileCache = emptyFileCache;
                            }
                        } else if (xLEFileCache.maxFileNumber != i) {
                            throw new IllegalArgumentException("The same subDirectory with different maxFileNumber already exist.");
                        }
                    }
                }
                throw new IllegalArgumentException("subDirectory must be not null and at least one character length");
            }
        }
        return xLEFileCache;
    }

    static File getCacheRootDir(XLEFileCache xLEFileCache) {
        return (File) sCacheRootDirMap.get(xLEFileCache);
    }

    public static String getCacheStatus() {
        return sAllCaches.values().toString();
    }
}
