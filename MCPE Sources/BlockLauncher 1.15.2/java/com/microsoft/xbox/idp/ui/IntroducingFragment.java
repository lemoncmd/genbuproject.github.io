package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.gson.GsonBuilder;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.model.Profile;
import com.microsoft.xbox.idp.model.Profile.Response;
import com.microsoft.xbox.idp.model.Profile.SettingId;
import com.microsoft.xbox.idp.model.Profile.User;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCIntroducing;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.toolkit.BitmapLoader;
import com.microsoft.xbox.idp.toolkit.BitmapLoader.Result;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.BitmapLoaderInfo;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.idp.util.HttpUtil.ImageSize;
import com.microsoft.xbox.idp.util.ObjectLoaderInfo;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView.Introducing;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.Service.Errors;

public class IntroducingFragment extends BaseFragment implements OnClickListener, ActivityContext {
    static final /* synthetic */ boolean $assertionsDisabled = (!IntroducingFragment.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    private static final String KEY_STATE = "IntroducingFragment.KEY_STATE";
    private static final int LOADER_GAMER_IMAGE = 2;
    private static final int LOADER_GAMER_PROFILE = 1;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onCloseWithStatus(Status status) {
        }
    };
    private static final String TAG = IntroducingFragment.class.getSimpleName();
    private final LoaderCallbacks<Result> bitmapCallbacks = new LoaderCallbacks<Result>() {
        public Loader<Result> onCreateLoader(int i, Bundle bundle) {
            Log.d(IntroducingFragment.TAG, "Creating LOADER_GAMER_IMAGE");
            String string = bundle.getString(ErrorHelper.KEY_RESULT_KEY);
            Uri build = HttpUtil.getImageSizeUrlParams(Uri.parse(string).buildUpon(), ImageSize.MEDIUM).build();
            Log.d(IntroducingFragment.TAG, "uri: " + build);
            return new BitmapLoader(IntroducingFragment.this.getActivity(), CacheUtil.getBitmapCache(), string, new HttpCall(HttpEngine.GET, HttpUtil.getEndpoint(build), HttpUtil.getPathAndQuery(build)));
        }

        public void onLoadFinished(Loader<Result> loader, Result result) {
            if (result.hasError()) {
                UTCError.trackServiceFailure(Errors.LoadBitmap, Introducing.View, result.getError());
            }
            Log.d(IntroducingFragment.TAG, "Finished LOADER_GAMER_IMAGE");
            IntroducingFragment.this.gamerpicView.setImageBitmap((Bitmap) result.getData());
        }

        public void onLoaderReset(Loader<Result> loader) {
            IntroducingFragment.this.gamerpicView.setImageBitmap(null);
        }
    };
    private View bottomBarShadow;
    private Callbacks callbacks = NO_OP_CALLBACKS;
    private TextView displayNameText;
    private TextView gamerTagText;
    private ImageView gamerpicView;
    private final SparseArray<LoaderInfo> loaderMap = new SparseArray();
    private final LoaderCallbacks<ObjectLoader.Result<Response>> profileCallbacks = new LoaderCallbacks<ObjectLoader.Result<Response>>() {
        public Loader<ObjectLoader.Result<Response>> onCreateLoader(int i, Bundle bundle) {
            Log.d(IntroducingFragment.TAG, "Creating LOADER_GAMER_PROFILE");
            return new ObjectLoader(IntroducingFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), bundle.get(ErrorHelper.KEY_RESULT_KEY), Response.class, Profile.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, EndpointsFactory.get().profile(), "/users/me/profile/settings?settings=" + (SettingId.GameDisplayPicRaw + "," + SettingId.Gamerscore + "," + SettingId.Gamertag + "," + SettingId.FirstName + "," + SettingId.LastName)), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION));
        }

        public void onLoadFinished(Loader<ObjectLoader.Result<Response>> loader, ObjectLoader.Result<Response> result) {
            Log.d(IntroducingFragment.TAG, "Finished LOADER_GAMER_PROFILE");
            if (!result.hasData() || ((Response) result.getData()).profileUsers == null || ((Response) result.getData()).profileUsers.length <= 0) {
                UTCError.trackServiceFailure(Errors.LoadProfile, Introducing.View, result.getError());
                Log.e(IntroducingFragment.TAG, "No gamer profile data");
                IntroducingFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
                return;
            }
            Log.e(IntroducingFragment.TAG, "Got gamer profile data");
            IntroducingFragment.this.user = ((Response) result.getData()).profileUsers[0];
            UTCIntroducing.trackPageView(IntroducingFragment.this.user, IntroducingFragment.this.getActivityTitle());
            TextView access$600 = IntroducingFragment.this.displayNameText;
            IntroducingFragment introducingFragment = IntroducingFragment.this;
            int i = R.string.xbid_first_and_last_name_android;
            Object[] objArr = new Object[IntroducingFragment.LOADER_GAMER_IMAGE];
            objArr[0] = IntroducingFragment.this.user.settings.get(SettingId.FirstName);
            objArr[IntroducingFragment.LOADER_GAMER_PROFILE] = IntroducingFragment.this.user.settings.get(SettingId.LastName);
            access$600.setText(introducingFragment.getString(i, objArr));
            IntroducingFragment.this.gamerTagText.setText((CharSequence) IntroducingFragment.this.user.settings.get(SettingId.Gamertag));
            if (!TextUtils.isEmpty((CharSequence) IntroducingFragment.this.user.settings.get(SettingId.GameDisplayPicRaw))) {
                Bundle bundle = new Bundle();
                bundle.putString(ErrorHelper.KEY_RESULT_KEY, (String) IntroducingFragment.this.user.settings.get(SettingId.GameDisplayPicRaw));
                IntroducingFragment.this.state.errorHelper.initLoader(IntroducingFragment.LOADER_GAMER_IMAGE, bundle);
            }
        }

        public void onLoaderReset(Loader<ObjectLoader.Result<Response>> loader) {
        }
    };
    private ScrollView scrollView;
    private State state;
    private User user;

    public interface Callbacks {
        void onCloseWithStatus(Status status);
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
        NO_ERROR,
        ERROR_USER_CANCEL,
        PROVIDER_ERROR
    }

    public IntroducingFragment() {
        this.loaderMap.put(LOADER_GAMER_PROFILE, new ObjectLoaderInfo(this.profileCallbacks));
        this.loaderMap.put(LOADER_GAMER_IMAGE, new BitmapLoaderInfo(this.bitmapCallbacks));
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
        Log.d(TAG, "onActivityResult");
        this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if ($assertionsDisabled || (activity instanceof Callbacks)) {
            this.callbacks = (Callbacks) activity;
            return;
        }
        throw new AssertionError();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.xbid_done) {
            UTCIntroducing.trackDone(this.user, getActivityTitle());
            this.callbacks.onCloseWithStatus(Status.NO_ERROR);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.xbid_fragment_introducing, viewGroup, $assertionsDisabled);
    }

    public void onDetach() {
        UTCPageView.removePage();
        super.onDetach();
        this.callbacks = NO_OP_CALLBACKS;
    }

    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null) {
            Log.d(TAG, "Initializing LOADER_GAMER_PROFILE");
            Bundle bundle = new Bundle(arguments);
            bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(IntroducingFragment.class, LOADER_GAMER_PROFILE));
            if (this.state.errorHelper != null) {
                this.state.errorHelper.initLoader(LOADER_GAMER_PROFILE, bundle);
                return;
            }
            return;
        }
        Log.e(TAG, "No arguments provided");
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_STATE, this.state);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.state = bundle == null ? new State() : (State) bundle.getParcelable(KEY_STATE);
        this.state.errorHelper.setActivityContext(this);
        this.scrollView = (ScrollView) view.findViewById(R.id.xbid_scroll_container);
        this.bottomBarShadow = view.findViewById(R.id.xbid_bottom_bar_shadow);
        this.scrollView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                IntroducingFragment.this.bottomBarShadow.setVisibility(UiUtil.canScroll(IntroducingFragment.this.scrollView) ? 0 : 4);
            }
        });
        this.gamerpicView = (ImageView) view.findViewById(R.id.xbid_gamerpic);
        this.displayNameText = (TextView) view.findViewById(R.id.xbid_display_name);
        this.gamerTagText = (TextView) view.findViewById(R.id.xbid_gamertag);
        Button button = (Button) view.findViewById(R.id.xbid_done);
        button.setOnClickListener(this);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(AuthFlowActivity.ARG_ALT_BUTTON_TEXT)) {
            button.setText(arguments.getString(AuthFlowActivity.ARG_ALT_BUTTON_TEXT));
        }
    }
}
