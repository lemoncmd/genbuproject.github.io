package com.microsoft.xbox.idp.util;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import com.microsoft.xbox.idp.ui.ErrorActivity;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;

public final class ErrorHelper implements Parcelable {
    public static final Creator<ErrorHelper> CREATOR = new Creator<ErrorHelper>() {
        public ErrorHelper createFromParcel(Parcel parcel) {
            return new ErrorHelper(parcel);
        }

        public ErrorHelper[] newArray(int i) {
            return new ErrorHelper[i];
        }
    };
    public static final String KEY_RESULT_KEY = "KEY_RESULT_KEY";
    public static final int LOADER_NONE = -1;
    public static final int RC_ERROR_SCREEN = 63;
    private static final String TAG = ErrorHelper.class.getSimpleName();
    private ActivityContext activityContext;
    public Bundle loaderArgs;
    public int loaderId;

    public interface ActivityContext {
        Activity getActivity();

        LoaderInfo getLoaderInfo(int i);

        LoaderManager getLoaderManager();

        void startActivityForResult(Intent intent, int i);
    }

    public interface LoaderInfo {
        void clearCache(Object obj);

        LoaderCallbacks<?> getLoaderCallbacks();

        boolean hasCachedData(Object obj);
    }

    public static class ActivityResult {
        private final boolean tryAgain;

        public ActivityResult(boolean z) {
            this.tryAgain = z;
        }

        public boolean isTryAgain() {
            return this.tryAgain;
        }
    }

    public ErrorHelper() {
        this.loaderId = LOADER_NONE;
        this.loaderArgs = null;
    }

    protected ErrorHelper(Parcel parcel) {
        this.loaderId = parcel.readInt();
        this.loaderArgs = parcel.readBundle();
    }

    private boolean isConnected() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.activityContext.getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void deleteLoader() {
        if (this.loaderId != LOADER_NONE) {
            this.activityContext.getLoaderManager().destroyLoader(this.loaderId);
            Object obj = this.loaderArgs == null ? null : this.loaderArgs.get(KEY_RESULT_KEY);
            if (obj != null) {
                this.activityContext.getLoaderInfo(this.loaderId).clearCache(obj);
            }
            this.loaderId = LOADER_NONE;
            this.loaderArgs = null;
        }
    }

    public int describeContents() {
        return 0;
    }

    public ActivityResult getActivityResult(int i, int i2, Intent intent) {
        boolean z = true;
        if (i != RC_ERROR_SCREEN) {
            return null;
        }
        if (i2 != 1) {
            z = false;
        }
        return new ActivityResult(z);
    }

    public <D> boolean initLoader(int i, Bundle bundle) {
        return initLoader(i, bundle, true);
    }

    public <D> boolean initLoader(int i, Bundle bundle, boolean z) {
        Log.d(TAG, "initLoader");
        if (i != LOADER_NONE) {
            this.loaderId = i;
            this.loaderArgs = bundle;
            LoaderManager loaderManager = this.activityContext.getLoaderManager();
            LoaderInfo loaderInfo = this.activityContext.getLoaderInfo(this.loaderId);
            Object obj = this.loaderArgs == null ? null : this.loaderArgs.get(KEY_RESULT_KEY);
            if ((obj == null ? false : loaderInfo.hasCachedData(obj)) || loaderManager.getLoader(i) != null || !z || isConnected()) {
                Log.d(TAG, "initializing loader #" + this.loaderId);
                loaderManager.initLoader(i, bundle, loaderInfo.getLoaderCallbacks());
                return true;
            }
            Log.e(TAG, "Starting error activity: OFFLINE");
            startErrorActivity(ErrorScreen.OFFLINE);
            return false;
        }
        Log.e(TAG, "LOADER_NONE");
        return false;
    }

    public <D> boolean restartLoader() {
        if (this.loaderId == LOADER_NONE) {
            return false;
        }
        if (isConnected()) {
            this.activityContext.getLoaderManager().restartLoader(this.loaderId, this.loaderArgs, this.activityContext.getLoaderInfo(this.loaderId).getLoaderCallbacks());
            return true;
        }
        startErrorActivity(ErrorScreen.OFFLINE);
        return false;
    }

    public <D> boolean restartLoader(int i, Bundle bundle) {
        if (i == LOADER_NONE) {
            return false;
        }
        this.loaderId = i;
        this.loaderArgs = bundle;
        if (isConnected()) {
            this.activityContext.getLoaderManager().restartLoader(this.loaderId, this.loaderArgs, this.activityContext.getLoaderInfo(this.loaderId).getLoaderCallbacks());
            return true;
        }
        startErrorActivity(ErrorScreen.OFFLINE);
        return false;
    }

    public void setActivityContext(ActivityContext activityContext) {
        this.activityContext = activityContext;
    }

    public void startErrorActivity(ErrorScreen errorScreen) {
        Intent intent = new Intent(this.activityContext.getActivity(), ErrorActivity.class);
        intent.putExtra(ErrorActivity.ARG_ERROR_TYPE, errorScreen.type.getId());
        this.activityContext.startActivityForResult(intent, RC_ERROR_SCREEN);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.loaderId);
        parcel.writeBundle(this.loaderArgs);
    }
}
