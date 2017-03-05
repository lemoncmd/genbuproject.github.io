package com.microsoft.xbox.idp.util;

import android.app.LoaderManager.LoaderCallbacks;
import com.microsoft.xbox.idp.toolkit.ObjectLoader.Cache;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;

public class ObjectLoaderInfo implements LoaderInfo {
    private final LoaderCallbacks<?> callbacks;

    public ObjectLoaderInfo(LoaderCallbacks<?> loaderCallbacks) {
        this.callbacks = loaderCallbacks;
    }

    public void clearCache(Object obj) {
        Cache objectLoaderCache = CacheUtil.getObjectLoaderCache();
        synchronized (objectLoaderCache) {
            objectLoaderCache.remove(obj);
        }
    }

    public LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    public boolean hasCachedData(Object obj) {
        boolean z;
        Cache objectLoaderCache = CacheUtil.getObjectLoaderCache();
        synchronized (objectLoaderCache) {
            z = objectLoaderCache.get(obj) != null;
        }
        return z;
    }
}
