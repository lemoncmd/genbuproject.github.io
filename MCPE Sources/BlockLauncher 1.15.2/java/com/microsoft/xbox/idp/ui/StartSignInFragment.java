package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.toolkit.StartSignInLoader;
import com.microsoft.xbox.idp.toolkit.StartSignInLoader.Result;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.ResultLoaderInfo;

public class StartSignInFragment extends BaseFragment implements ActivityContext {
    static final /* synthetic */ boolean $assertionsDisabled = (!StartSignInFragment.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    private static final String KEY_STATE = "KEY_STATE";
    private static final int LOADER_START_SIGN_IN = 1;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onComplete(Status status) {
        }
    };
    private static final String TAG = StartSignInFragment.class.getSimpleName();
    private Callbacks callbacks = NO_OP_CALLBACKS;
    private final SparseArray<LoaderInfo> loaderMap = new SparseArray();
    private final LoaderCallbacks<Result> startSignInCallbacks = new LoaderCallbacks<Result>() {
        public Loader<Result> onCreateLoader(int i, Bundle bundle) {
            Log.d(StartSignInFragment.TAG, "Creating LOADER_START_SIGN_IN");
            return new StartSignInLoader(StartSignInFragment.this.getActivity(), CacheUtil.getResultCache(Result.class), bundle.get(ErrorHelper.KEY_RESULT_KEY));
        }

        public void onLoadFinished(Loader<Result> loader, Result result) {
            Log.d(StartSignInFragment.TAG, "LOADER_START_SIGN_IN finished");
            if (result.hasError()) {
                Log.d(StartSignInFragment.TAG, "LOADER_START_SIGN_IN: " + result.getError());
                StartSignInFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
                return;
            }
            StartSignInFragment.this.callbacks.onComplete(Status.SUCCESS);
        }

        public void onLoaderReset(Loader<Result> loader) {
            Log.d(StartSignInFragment.TAG, "LOADER_START_SIGN_IN reset");
        }
    };
    private State state;

    public interface Callbacks {
        void onComplete(Status status);
    }

    private static class State implements Parcelable {
        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel parcel) {
                return new State(parcel);
            }

            public State[] newArray(int i) {
                return new State[i];
            }
        };
        public ErrorHelper errorHelper;

        public State() {
            this.errorHelper = new ErrorHelper();
        }

        protected State(Parcel parcel) {
            this.errorHelper = (ErrorHelper) parcel.readParcelable(ErrorHelper.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.errorHelper, i);
        }
    }

    public enum Status {
        SUCCESS,
        ERROR,
        PROVIDER_ERROR
    }

    public StartSignInFragment() {
        this.loaderMap.put(LOADER_START_SIGN_IN, new ResultLoaderInfo(Result.class, this.startSignInCallbacks));
    }

    public LoaderInfo getLoaderInfo(int i) {
        return (LoaderInfo) this.loaderMap.get(i);
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        ActivityResult activityResult = this.state.errorHelper.getActivityResult(i, i2, intent);
        if (activityResult == null) {
            return;
        }
        if (activityResult.isTryAgain()) {
            Log.d(TAG, "Trying again");
            this.state.errorHelper.deleteLoader();
            return;
        }
        this.state.errorHelper = null;
        this.callbacks.onComplete(Status.PROVIDER_ERROR);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ($assertionsDisabled || (activity instanceof Callbacks)) {
            this.callbacks = (Callbacks) activity;
            return;
        }
        throw new AssertionError();
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.state = bundle == null ? new State() : (State) bundle.getParcelable(KEY_STATE);
        this.state.errorHelper.setActivityContext(this);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.xbid_fragment_busy, viewGroup, $assertionsDisabled);
    }

    public void onDetach() {
        super.onDetach();
        this.callbacks = NO_OP_CALLBACKS;
    }

    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        Log.d(TAG, "Initializing LOADER_START_SIGN_IN");
        Bundle bundle = new Bundle(arguments);
        bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(StartSignInFragment.class, LOADER_START_SIGN_IN));
        if (this.state.errorHelper != null) {
            this.state.errorHelper.initLoader(LOADER_START_SIGN_IN, bundle, $assertionsDisabled);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_STATE, this.state);
    }
}
