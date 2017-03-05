package com.microsoft.xbox.service.model;

import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.ModelData;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEObservable;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.XLEUtil;
import java.util.Date;

public abstract class ModelBase<T> extends XLEObservable<UpdateData> implements ModelData<T> {
    protected static final long MilliSecondsInADay = 86400000;
    protected static final long MilliSecondsInAnHour = 3600000;
    protected static final long MilliSecondsInHalfHour = 1800000;
    protected boolean isLoading = false;
    protected long lastInvalidatedTick = 0;
    protected Date lastRefreshTime;
    protected long lifetime = MilliSecondsInADay;
    protected IDataLoaderRunnable<T> loaderRunnable;
    private SingleEntryLoadingStatus loadingStatus = new SingleEntryLoadingStatus();

    public boolean getIsLoading() {
        return this.loadingStatus.getIsLoading();
    }

    public boolean hasValidData() {
        return this.lastRefreshTime != null;
    }

    public void invalidateData() {
        this.lastRefreshTime = null;
    }

    protected boolean isLoaded() {
        return this.lastRefreshTime != null;
    }

    protected AsyncResult<T> loadData(boolean z, IDataLoaderRunnable<T> iDataLoaderRunnable) {
        XLEAssert.assertIsNotUIThread();
        return DataLoadUtil.Load(z, this.lifetime, this.lastRefreshTime, this.loadingStatus, iDataLoaderRunnable);
    }

    protected void loadInternal(boolean z, UpdateType updateType, IDataLoaderRunnable<T> iDataLoaderRunnable) {
        loadInternal(z, updateType, iDataLoaderRunnable, this.lastRefreshTime);
    }

    protected void loadInternal(boolean z, UpdateType updateType, IDataLoaderRunnable<T> iDataLoaderRunnable, Date date) {
        boolean z2 = false;
        XLEAssert.assertIsUIThread();
        if (getIsLoading() || !(z || shouldRefresh(date))) {
            if (!getIsLoading()) {
                z2 = true;
            }
            notifyObservers(new AsyncResult(new UpdateData(updateType, z2), this, null));
            return;
        }
        DataLoadUtil.StartLoadFromUI(z, this.lifetime, this.lastRefreshTime, this.loadingStatus, iDataLoaderRunnable);
        notifyObservers(new AsyncResult(new UpdateData(updateType, false), this, null));
    }

    public boolean shouldRefresh() {
        return shouldRefresh(this.lastRefreshTime);
    }

    protected boolean shouldRefresh(Date date) {
        return XLEUtil.shouldRefresh(date, this.lifetime);
    }

    public void updateWithNewData(AsyncResult<T> asyncResult) {
        this.isLoading = false;
        if (asyncResult.getException() == null && asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.lastRefreshTime = new Date();
        }
    }
}
