package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.gson.GsonBuilder;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.interop.XboxLiveAppConfig;
import com.microsoft.xbox.idp.model.Privacy;
import com.microsoft.xbox.idp.model.Privacy.Key;
import com.microsoft.xbox.idp.model.Privacy.Settings;
import com.microsoft.xbox.idp.model.Privacy.Value;
import com.microsoft.xbox.idp.model.Profile;
import com.microsoft.xbox.idp.model.Profile.GamerpicChangeRequest;
import com.microsoft.xbox.idp.model.Profile.GamerpicChoiceList;
import com.microsoft.xbox.idp.model.Profile.GamerpicListEntry;
import com.microsoft.xbox.idp.model.Profile.GamerpicUpdateResponse;
import com.microsoft.xbox.idp.model.UserAccount;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.Service.Errors;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.toolkit.ObjectLoader.Result;
import com.microsoft.xbox.idp.toolkit.XTokenLoader;
import com.microsoft.xbox.idp.toolkit.XTokenLoader.Data;
import com.microsoft.xbox.idp.ui.AccountProvisioningResult.AgeGroup;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.CacheUtil;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.FragmentLoaderKey;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.idp.util.ObjectLoaderInfo;
import com.microsoft.xbox.idp.util.ResultLoaderInfo;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView.Introducing;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView.Signup;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView.Welcome;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.Service;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class AccountProvisioningFragment extends BaseFragment implements ActivityContext {
    static final /* synthetic */ boolean $assertionsDisabled = (!AccountProvisioningFragment.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    private static final String GAMERPIC_UPDATE_IMAGE_URL_KEY = "GAMERPIC_UPDATE_IMAGE_URL_KEY";
    private static final String KEY_STATE = "KEY_STATE";
    private static final int LOADER_GAMERPIC_CHOICE_LIST = 4;
    private static final int LOADER_GAMERPIC_UPDATE = 5;
    private static final int LOADER_GET_PRIVACY_SETTINGS = 6;
    private static final int LOADER_GET_PROFILE = 1;
    private static final int LOADER_POST_PROFILE = 2;
    private static final int LOADER_SET_PRIVACY_SETTINGS = 7;
    private static final int LOADER_XTOKEN = 3;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onCloseWithStatus(Status status, AccountProvisioningResult accountProvisioningResult) {
        }
    };
    private static final String TAG = AccountProvisioningFragment.class.getSimpleName();
    private Callbacks callbacks;
    private final LoaderCallbacks<Result<GamerpicChoiceList>> gamerpicChoiceListCallbacks = new LoaderCallbacks<Result<GamerpicChoiceList>>() {
        public Loader<Result<GamerpicChoiceList>> onCreateLoader(int i, Bundle bundle) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_GAMERPIC_CHOICE_LIST");
            XboxLiveAppConfig xboxLiveAppConfig = new XboxLiveAppConfig();
            int overrideTitleId = xboxLiveAppConfig.getOverrideTitleId();
            if (overrideTitleId == 0) {
                overrideTitleId = xboxLiveAppConfig.getTitleId();
            }
            Object[] objArr = new Object[AccountProvisioningFragment.LOADER_GET_PROFILE];
            objArr[0] = Integer.valueOf(overrideTitleId);
            return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), bundle.get(ErrorHelper.KEY_RESULT_KEY), GamerpicChoiceList.class, Profile.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, "http://dlassets.xboxlive.com", String.format("/public/content/ppl/gamerpics/gamerpicsautoassign-%08X.json", objArr)), "3"));
        }

        public void onLoadFinished(Loader<Result<GamerpicChoiceList>> loader, Result<GamerpicChoiceList> result) {
            Log.d(AccountProvisioningFragment.TAG, "Finished LOADER_GAMERPIC_CHOICE_LIST");
            if (!result.hasData() || ((GamerpicChoiceList) result.getData()).gamerpics == null) {
                Log.e(AccountProvisioningFragment.TAG, "Failed to get gamerpic choice list");
                UTCError.trackServiceFailure(Errors.GamerPicChoiceList, Welcome.View, result.getError());
                Bundle bundle = new Bundle();
                bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, AccountProvisioningFragment.LOADER_GET_PRIVACY_SETTINGS));
                AccountProvisioningFragment.this.state.errorHelper.initLoader(AccountProvisioningFragment.LOADER_GET_PRIVACY_SETTINGS, bundle);
                return;
            }
            Log.e(AccountProvisioningFragment.TAG, "Got gamerpic choice list");
            List list = ((GamerpicChoiceList) result.getData()).gamerpics;
            if (!list.isEmpty()) {
                Object[] objArr = new Object[AccountProvisioningFragment.LOADER_GET_PROFILE];
                objArr[0] = ((GamerpicListEntry) list.get(new Random().nextInt(list.size()))).id;
                String format = String.format("http://dlassets.xboxlive.com/public/content/ppl/gamerpics/%s-xl.png", objArr);
                Bundle bundle2 = new Bundle();
                bundle2.putString(AccountProvisioningFragment.GAMERPIC_UPDATE_IMAGE_URL_KEY, format);
                AccountProvisioningFragment.this.state.errorHelper.initLoader(AccountProvisioningFragment.LOADER_GAMERPIC_UPDATE, bundle2);
            }
        }

        public void onLoaderReset(Loader<Result<GamerpicChoiceList>> loader) {
        }
    };
    private final LoaderCallbacks<Result<GamerpicUpdateResponse>> gamerpicUpdateCallbacks = new LoaderCallbacks<Result<GamerpicUpdateResponse>>() {
        public Loader<Result<GamerpicUpdateResponse>> onCreateLoader(int i, Bundle bundle) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_GAMERPIC_UPDATE");
            GamerpicChangeRequest gamerpicChangeRequest = new GamerpicChangeRequest(bundle.getString(AccountProvisioningFragment.GAMERPIC_UPDATE_IMAGE_URL_KEY));
            HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, EndpointsFactory.get().profile(), "/users/me/profile/settings/PublicGamerpic"), "3");
            appendCommonParameters.setRequestBody(new GsonBuilder().create().toJson(gamerpicChangeRequest, GamerpicChangeRequest.class));
            return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), null, bundle.get(ErrorHelper.KEY_RESULT_KEY), GamerpicUpdateResponse.class, Profile.registerAdapters(new GsonBuilder()).create(), appendCommonParameters);
        }

        public void onLoadFinished(Loader<Result<GamerpicUpdateResponse>> loader, Result<GamerpicUpdateResponse> result) {
            Log.d(AccountProvisioningFragment.TAG, "Finished LOADER_GAMERPIC_UPDATE");
            if (result.hasError()) {
                UTCError.trackServiceFailure(Errors.GamerPicUpdate, Introducing.View, result.getError());
                Log.e(AccountProvisioningFragment.TAG, "Failed to update gamerpic");
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, AccountProvisioningFragment.LOADER_GET_PRIVACY_SETTINGS));
            AccountProvisioningFragment.this.state.errorHelper.initLoader(AccountProvisioningFragment.LOADER_GET_PRIVACY_SETTINGS, bundle);
        }

        public void onLoaderReset(Loader<Result<GamerpicUpdateResponse>> loader) {
        }
    };
    private final LoaderCallbacks<Result<Settings>> getPrivacySettingsCallbacks = new LoaderCallbacks<Result<Settings>>() {
        public Loader<Result<Settings>> onCreateLoader(int i, Bundle bundle) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_GET_PRIVACY_SETTINGS");
            return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), bundle.get(ErrorHelper.KEY_RESULT_KEY), Settings.class, Privacy.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, EndpointsFactory.get().privacy(), "/users/me/privacy/settings"), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION));
        }

        public void onLoadFinished(Loader<Result<Settings>> loader, Result<Settings> result) {
            Log.d(AccountProvisioningFragment.TAG, "LOADER_GET_PRIVACY_SETTINGS finished");
            if (result.hasData()) {
                Log.d(AccountProvisioningFragment.TAG, "Got privacy settings");
                Settings settings = (Settings) result.getData();
                if (settings.settings == null) {
                    Log.d(AccountProvisioningFragment.TAG, "Privacy settings map is null");
                    AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
                    return;
                } else if (settings.isSettingSet(Key.ShareIdentity) || settings.isSettingSet(Key.ShareIdentityTransitively)) {
                    Log.d(AccountProvisioningFragment.TAG, "ShareIdentity or ShareIdentityTransitively are set");
                    Log.d(AccountProvisioningFragment.TAG, "ShareIdentity: " + settings.settings.get(Key.ShareIdentity));
                    Log.d(AccountProvisioningFragment.TAG, "ShareIdentityTransitively: " + settings.settings.get(Key.ShareIdentityTransitively));
                    AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
                    return;
                } else {
                    Log.d(AccountProvisioningFragment.TAG, "ShareIdentity and ShareIdentityTransitively are NotSet");
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, AccountProvisioningFragment.LOADER_SET_PRIVACY_SETTINGS));
                    AccountProvisioningFragment.this.state.errorHelper.initLoader(AccountProvisioningFragment.LOADER_SET_PRIVACY_SETTINGS, bundle);
                    return;
                }
            }
            Log.e(AccountProvisioningFragment.TAG, "Error getting privacy settings: " + result.getError());
            UTCError.trackServiceFailure(Service.Errors.LoadPrivacySettings, Signup.View, result.getError());
            AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
        }

        public void onLoaderReset(Loader<Result<Settings>> loader) {
        }
    };
    private final SparseArray<LoaderInfo> loaderMap = new SparseArray();
    private final LoaderCallbacks<Result<Void>> setPrivacySettingsCallbacks = new LoaderCallbacks<Result<Void>>() {
        public Loader<Result<Void>> onCreateLoader(int i, Bundle bundle) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_SET_PRIVACY_SETTINGS");
            HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.PUT, EndpointsFactory.get().privacy(), "/users/me/privacy/settings"), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION);
            Settings newWithMap = Settings.newWithMap();
            newWithMap.settings.put(Key.ShareIdentity, Value.PeopleOnMyList);
            newWithMap.settings.put(Key.ShareIdentityTransitively, Value.Everyone);
            appendCommonParameters.setRequestBody(Privacy.registerAdapters(new GsonBuilder()).create().toJson(newWithMap, Settings.class));
            return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), bundle.get(ErrorHelper.KEY_RESULT_KEY), Void.class, Privacy.registerAdapters(new GsonBuilder()).create(), appendCommonParameters);
        }

        public void onLoadFinished(Loader<Result<Void>> loader, Result<Void> result) {
            Log.d(AccountProvisioningFragment.TAG, "LOADER_SET_PRIVACY_SETTINGS finished");
            if (result.hasError()) {
                Log.e(AccountProvisioningFragment.TAG, "Error setting privacy settings: " + result.getError());
                UTCError.trackServiceFailure(Service.Errors.SetPrivacySettings, Signup.View, result.getError());
                AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
                return;
            }
            Log.e(AccountProvisioningFragment.TAG, "Privacy settings set successfully");
            AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
        }

        public void onLoaderReset(Loader<Result<Void>> loader) {
        }
    };
    private State state;
    private UserAccount userAccount;
    private final LoaderCallbacks<Result<UserAccount>> userProfileCallbacks = new LoaderCallbacks<Result<UserAccount>>() {
        public Loader<Result<UserAccount>> onCreateLoader(int i, Bundle bundle) {
            switch (i) {
                case AccountProvisioningFragment.LOADER_GET_PROFILE /*1*/:
                    Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_GET_PROFILE");
                    return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), bundle.get(ErrorHelper.KEY_RESULT_KEY), UserAccount.class, UserAccount.registerAdapters(new GsonBuilder()).create(), HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, EndpointsFactory.get().userAccount(), "/users/current/profile"), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION));
                case AccountProvisioningFragment.LOADER_POST_PROFILE /*2*/:
                    Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_POST_PROFILE");
                    HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, EndpointsFactory.get().userAccount(), "/users/current/profile"), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION);
                    UserAccount access$100 = AccountProvisioningFragment.this.userAccount;
                    access$100.touAcceptanceDate = new Date();
                    access$100.msftOptin = AccountProvisioningFragment.$assertionsDisabled;
                    if (TextUtils.isEmpty(access$100.legalCountry)) {
                        access$100.legalCountry = Locale.getDefault().getCountry();
                    }
                    appendCommonParameters.setRequestBody(UserAccount.registerAdapters(new GsonBuilder()).create().toJson(access$100, UserAccount.class));
                    return new ObjectLoader(AccountProvisioningFragment.this.getActivity(), CacheUtil.getObjectLoaderCache(), bundle.get(ErrorHelper.KEY_RESULT_KEY), UserAccount.class, UserAccount.registerAdapters(new GsonBuilder()).create(), appendCommonParameters);
                default:
                    return null;
            }
        }

        public void onLoadFinished(Loader<Result<UserAccount>> loader, Result<UserAccount> result) {
            Bundle bundle;
            switch (loader.getId()) {
                case AccountProvisioningFragment.LOADER_GET_PROFILE /*1*/:
                    Log.d(AccountProvisioningFragment.TAG, "LOADER_GET_PROFILE finished");
                    if (result.hasData()) {
                        Log.e(AccountProvisioningFragment.TAG, "Got UserAccount");
                        AccountProvisioningFragment.this.userAccount = (UserAccount) result.getData();
                        bundle = new Bundle(AccountProvisioningFragment.this.getArguments());
                        bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, AccountProvisioningFragment.LOADER_POST_PROFILE));
                        AccountProvisioningFragment.this.state.errorHelper.initLoader(AccountProvisioningFragment.LOADER_POST_PROFILE, bundle);
                        return;
                    }
                    Log.e(AccountProvisioningFragment.TAG, "Error getting UserAccount");
                    UTCError.trackServiceFailure("Service Error - Profile Load", Signup.View, result.getError());
                    AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CREATION);
                    return;
                case AccountProvisioningFragment.LOADER_POST_PROFILE /*2*/:
                    Log.d(AccountProvisioningFragment.TAG, "LOADER_POST_PROFILE finished");
                    if (result.hasData()) {
                        Log.e(AccountProvisioningFragment.TAG, "Got UserAccount");
                        AccountProvisioningFragment.this.userAccount = (UserAccount) result.getData();
                        bundle = AccountProvisioningFragment.this.getArguments();
                        Bundle bundle2 = new Bundle();
                        bundle2.putLong(AuthFlowActivity.ARG_USER_PTR, bundle.getLong(AuthFlowActivity.ARG_USER_PTR));
                        bundle2.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, AccountProvisioningFragment.LOADER_XTOKEN));
                        AccountProvisioningFragment.this.state.errorHelper.initLoader(AccountProvisioningFragment.LOADER_XTOKEN, bundle2);
                        return;
                    }
                    Log.e(AccountProvisioningFragment.TAG, "Error getting UserAccount");
                    UTCError.trackServiceFailure("Service Error - Profile Load", Signup.View, result.getError());
                    AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CREATION);
                    return;
                default:
                    return;
            }
        }

        public void onLoaderReset(Loader<Result<UserAccount>> loader) {
        }
    };
    private final LoaderCallbacks<XTokenLoader.Result> xtokenCallbacks = new LoaderCallbacks<XTokenLoader.Result>() {
        public Loader<XTokenLoader.Result> onCreateLoader(int i, Bundle bundle) {
            Log.d(AccountProvisioningFragment.TAG, "Creating LOADER_XTOKEN");
            return new XTokenLoader(AccountProvisioningFragment.this.getActivity(), bundle.getLong(AuthFlowActivity.ARG_USER_PTR), CacheUtil.getResultCache(XTokenLoader.Result.class), bundle.get(ErrorHelper.KEY_RESULT_KEY));
        }

        public void onLoadFinished(Loader<XTokenLoader.Result> loader, XTokenLoader.Result result) {
            Log.d(AccountProvisioningFragment.TAG, "LOADER_XTOKEN finished");
            if (result.hasData()) {
                AccountProvisioningFragment.this.xtokenData = (Data) result.getData();
                AccountProvisioningFragment.this.state.result = new AccountProvisioningResult(AccountProvisioningFragment.this.userAccount.gamerTag, AccountProvisioningFragment.this.userAccount.userXuid);
                AgeGroup fromServiceString = AgeGroup.fromServiceString(AccountProvisioningFragment.this.xtokenData.getAuthFlowResult().getAgeGroup());
                if (fromServiceString != null) {
                    Log.d(AccountProvisioningFragment.TAG, "ageGroup: " + fromServiceString);
                    AccountProvisioningFragment.this.state.result.setAgeGroup(fromServiceString);
                    if (fromServiceString != AgeGroup.Child) {
                        AccountProvisioningFragment.this.state.errorHelper.initLoader(AccountProvisioningFragment.LOADER_GAMERPIC_CHOICE_LIST, new Bundle());
                        return;
                    }
                    AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
                    return;
                }
                Log.e(AccountProvisioningFragment.TAG, "Unknown age group");
                AccountProvisioningFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR, AccountProvisioningFragment.this.state.result);
                return;
            }
            Log.d(AccountProvisioningFragment.TAG, "LOADER_XTOKEN: " + result.getError());
            UTCError.trackServiceFailure(Service.Errors.LoadXTOKEN, Signup.View, result.getError());
            AccountProvisioningFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
        }

        public void onLoaderReset(Loader<XTokenLoader.Result> loader) {
        }
    };
    private Data xtokenData;

    public interface Callbacks {
        void onCloseWithStatus(Status status, AccountProvisioningResult accountProvisioningResult);
    }

    public static class State implements Parcelable {
        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel parcel) {
                return new State(parcel);
            }

            public State[] newArray(int i) {
                return new State[i];
            }
        };
        public ErrorHelper errorHelper;
        public AccountProvisioningResult result;

        public State() {
            this.errorHelper = new ErrorHelper();
        }

        protected State(Parcel parcel) {
            this.errorHelper = (ErrorHelper) parcel.readParcelable(ErrorHelper.class.getClassLoader());
            this.result = (AccountProvisioningResult) parcel.readParcelable(AccountProvisioningResult.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeParcelable(this.errorHelper, i);
            parcel.writeParcelable(this.result, i);
        }
    }

    public enum Status {
        NO_ERROR,
        PROVIDER_ERROR,
        ERROR_USER_CANCEL
    }

    public AccountProvisioningFragment() {
        this.loaderMap.put(LOADER_GET_PROFILE, new ObjectLoaderInfo(this.userProfileCallbacks));
        this.loaderMap.put(LOADER_POST_PROFILE, new ObjectLoaderInfo(this.userProfileCallbacks));
        this.loaderMap.put(LOADER_XTOKEN, new ResultLoaderInfo(XTokenLoader.Result.class, this.xtokenCallbacks));
        this.loaderMap.put(LOADER_GAMERPIC_CHOICE_LIST, new ObjectLoaderInfo(this.gamerpicChoiceListCallbacks));
        this.loaderMap.put(LOADER_GAMERPIC_UPDATE, new ObjectLoaderInfo(this.gamerpicUpdateCallbacks));
        this.loaderMap.put(LOADER_GET_PRIVACY_SETTINGS, new ObjectLoaderInfo(this.getPrivacySettingsCallbacks));
        this.loaderMap.put(LOADER_SET_PRIVACY_SETTINGS, new ObjectLoaderInfo(this.setPrivacySettingsCallbacks));
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
        this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR, null);
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
            Log.e(TAG, "No arguments provided");
            this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR, null);
        } else if (!arguments.containsKey(AuthFlowActivity.ARG_USER_PTR)) {
            Log.e(TAG, "No ARG_USER_PTR");
            this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR, null);
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
        Bundle arguments = getArguments();
        Log.d(TAG, "Initializing LOADER_GET_PROFILE");
        Bundle bundle = new Bundle(arguments);
        bundle.putParcelable(ErrorHelper.KEY_RESULT_KEY, new FragmentLoaderKey(SignUpFragment.class, LOADER_GET_PROFILE));
        if (this.state.errorHelper != null) {
            this.state.errorHelper.initLoader(LOADER_GET_PROFILE, bundle);
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_STATE, this.state);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.state = bundle == null ? new State() : (State) bundle.getParcelable(KEY_STATE);
        this.state.errorHelper.setActivityContext(this);
    }
}
