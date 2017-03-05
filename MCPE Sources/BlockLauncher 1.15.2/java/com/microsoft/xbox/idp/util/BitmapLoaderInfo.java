package com.microsoft.xbox.idp.util;

import android.app.LoaderManager.LoaderCallbacks;
import com.microsoft.xbox.idp.toolkit.BitmapLoader.Cache;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;

public class BitmapLoaderInfo implements LoaderInfo {
    private final LoaderCallbacks<?> callbacks;

    public BitmapLoaderInfo(LoaderCallbacks<?> loaderCallbacks) {
        this.callbacks = loaderCallbacks;
    }

    public void clearCache(Object obj) {
        Cache bitmapCache = CacheUtil.getBitmapCache();
        synchronized (bitmapCache) {
            bitmapCache.remove(obj);
        }
    }

    public LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    public boolean hasCachedData(Object obj) {
        boolean z;
        Cache bitmapCache = CacheUtil.getBitmapCache();
        synchronized (bitmapCache) {
            z = bitmapCache.get(obj) != null;
        }
        return z;
    }
}
