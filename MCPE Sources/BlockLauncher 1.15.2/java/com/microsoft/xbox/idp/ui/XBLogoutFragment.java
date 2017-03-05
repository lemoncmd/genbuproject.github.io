package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.toolkit.XBLogoutLoader;
import com.microsoft.xbox.idp.toolkit.XBLogoutLoader.Result;

public class XBLogoutFragment extends BaseFragment {
    static final /* synthetic */ boolean $assertionsDisabled = (!XBLogoutFragment.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    private static final int LOADER_XB_LOGOUT = 1;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onComplete(Status status) {
        }
    };
    private static final String TAG = XBLogoutFragment.class.getSimpleName();
    private Callbacks callbacks = NO_OP_CALLBACKS;
    private final LoaderCallbacks<Result> xbLogoutCallbacks = new LoaderCallbacks<Result>() {
        public Loader<Result> onCreateLoader(int i, Bundle bundle) {
            Log.d(XBLogoutFragment.TAG, "Creating LOADER_XB_LOGOUT");
            return new XBLogoutLoader(XBLogoutFragment.this.getActivity(), bundle.getLong(AuthFlowActivity.ARG_USER_PTR));
        }

        public void onLoadFinished(Loader<Result> loader, Result result) {
            Log.d(XBLogoutFragment.TAG, "LOADER_XB_LOGOUT finished");
            XBLogoutFragment.this.callbacks.onComplete(Status.SUCCESS);
        }

        public void onLoaderReset(Loader<Result> loader) {
            Log.d(XBLogoutFragment.TAG, "LOADER_XB_LOGOUT reset");
        }
    };

    public interface Callbacks {
        void onComplete(Status status);
    }

    public enum Status {
        SUCCESS,
        ERROR
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
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.d(TAG, "No arguments");
            this.callbacks.onComplete(Status.ERROR);
        } else if (!arguments.containsKey(AuthFlowActivity.ARG_USER_PTR)) {
            Log.d(TAG, "No ARG_USER_PTR");
            this.callbacks.onComplete(Status.ERROR);
        }
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
        getLoaderManager().initLoader(LOADER_XB_LOGOUT, getArguments(), this.xbLogoutCallbacks);
    }
}
