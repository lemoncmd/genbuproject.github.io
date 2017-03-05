package com.microsoft.xbox.idp.util;

import android.app.LoaderManager.LoaderCallbacks;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;

public class ResultLoaderInfo<R> implements LoaderInfo {
    private final LoaderCallbacks<?> callbacks;
    private final Class<R> cls;

    public ResultLoaderInfo(Class<R> cls, LoaderCallbacks<?> loaderCallbacks) {
        this.cls = cls;
        this.callbacks = loaderCallbacks;
    }

    public void clearCache(Object obj) {
        ResultCache resultCache = CacheUtil.getResultCache(this.cls);
        synchronized (resultCache) {
            resultCache.remove(obj);
        }
    }

    public LoaderCallbacks<?> getLoaderCallbacks() {
        return this.callbacks;
    }

    public boolean hasCachedData(Object obj) {
        boolean z;
        ResultCache resultCache = CacheUtil.getResultCache(this.cls);
        synchronized (resultCache) {
            z = resultCache.get(obj) != null;
        }
        return z;
    }
}
