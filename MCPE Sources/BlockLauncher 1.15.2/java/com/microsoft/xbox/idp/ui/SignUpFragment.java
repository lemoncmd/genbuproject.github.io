package com.microsoft.xbox.idp.ui;

import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.microsoft.xbox.idp.R;
import com.microsoft.xbox.idp.compat.BaseFragment;
import com.microsoft.xbox.idp.model.Const;
import com.microsoft.xbox.idp.model.GamerTag.Request;
import com.microsoft.xbox.idp.model.GamerTag.ReservationRequest;
import com.microsoft.xbox.idp.model.GamerTag.Response;
import com.microsoft.xbox.idp.model.Suggestions;
import com.microsoft.xbox.idp.services.EndpointsFactory;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.helpers.UTCSignup;
import com.microsoft.xbox.idp.telemetry.helpers.UTCUser;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.idp.toolkit.ObjectLoader;
import com.microsoft.xbox.idp.toolkit.ObjectLoader.Result;
import com.microsoft.xbox.idp.ui.AccountProvisioningResult.AgeGroup;
import com.microsoft.xbox.idp.ui.ErrorActivity.ErrorScreen;
import com.microsoft.xbox.idp.util.ErrorHelper;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityContext;
import com.microsoft.xbox.idp.util.ErrorHelper.ActivityResult;
import com.microsoft.xbox.idp.util.ErrorHelper.LoaderInfo;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.idp.util.ObjectLoaderInfo;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView.Signup;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.Service.Errors;
import java.util.Locale;
import net.hockeyapp.android.BuildConfig;

public class SignUpFragment extends BaseFragment implements OnClickListener, ActivityContext {
    static final /* synthetic */ boolean $assertionsDisabled = (!SignUpFragment.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    public static final String ARG_ACCOUNT_PROVISIONING_RESULT = "ARG_ACCOUNT_PROVISIONING_RESULT";
    private static final String KEY_STATE = "KEY_STATE";
    private static final int LOADER_CLAIM_GAMERTAG = 1;
    private static final int LOADER_RESERVE_GAMERTAG = 2;
    private static final int LOADER_SUGGESTIONS = 3;
    private static final Callbacks NO_OP_CALLBACKS = new Callbacks() {
        public void onCloseWithStatus(Status status) {
        }
    };
    private static final String TAG = SignUpFragment.class.getSimpleName();
    private View bottomBarShadow;
    private Callbacks callbacks = NO_OP_CALLBACKS;
    private Button claimItButton;
    private View clearTextButton;
    private EditText editTextGamerTag;
    private View editTextGamerTagContainer;
    private final TextWatcher gamerTagChangeListener = new TextWatcher() {
        public void afterTextChanged(Editable editable) {
            SignUpFragment.this.resetGamerTagState(editable);
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    };
    private final LoaderCallbacks<Result<Response>> gamerTagClaimCallbacks = new LoaderCallbacks<Result<Response>>() {
        public Loader<Result<Response>> onCreateLoader(int i, Bundle bundle) {
            Log.d(SignUpFragment.TAG, "Creating LOADER_CLAIM_GAMERTAG");
            HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, EndpointsFactory.get().accounts(), "/users/current/profile/gamertag"), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION);
            Request request = new Request();
            request.gamertag = SignUpFragment.this.state.gamerTag;
            request.preview = SignUpFragment.$assertionsDisabled;
            request.reservationId = SignUpFragment.this.provisioningResult.getXuid();
            appendCommonParameters.setRequestBody(new Gson().toJson(request, Request.class));
            return new ObjectLoader(SignUpFragment.this.getActivity(), Response.class, new Gson(), appendCommonParameters);
        }

        public void onLoadFinished(Loader<Result<Response>> loader, Result<Response> result) {
            Log.d(SignUpFragment.TAG, "LOADER_CLAIM_GAMERTAG finished");
            if (!result.hasData()) {
                Log.e(SignUpFragment.TAG, "Error getting GamerTag.Response");
                SignUpFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
            } else if (((Response) result.getData()).hasFree) {
                Log.i(SignUpFragment.TAG, "Gamertag claimed successfully");
                SignUpFragment.this.state.gamerTag = SignUpFragment.this.editTextGamerTag.getText().toString();
                SignUpFragment.this.callbacks.onCloseWithStatus(Status.NO_ERROR);
            } else {
                Log.e(SignUpFragment.TAG, "Gamertag is not free");
                SignUpFragment.this.state.errorHelper.startErrorActivity(ErrorScreen.CATCHALL);
            }
            SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
        }

        public void onLoaderReset(Loader<Result<Response>> loader) {
        }
    };
    private final LoaderCallbacks<Result<Void>> gamerTagReservationCallbacks = new LoaderCallbacks<Result<Void>>() {
        public Loader<Result<Void>> onCreateLoader(int i, Bundle bundle) {
            Log.d(SignUpFragment.TAG, "creating LOADER_RESERVE_GAMERTAG");
            HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, EndpointsFactory.get().userManagement(), "/gamertags/reserve"), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
            appendCommonParameters.setRequestBody(new Gson().toJson(new ReservationRequest(SignUpFragment.this.editTextGamerTag.getText().toString(), SignUpFragment.this.provisioningResult.getXuid()), ReservationRequest.class));
            return new ObjectLoader(SignUpFragment.this.getActivity(), Void.class, new Gson(), appendCommonParameters);
        }

        public void onLoadFinished(Loader<Result<Void>> loader, Result<Void> result) {
            Log.d(SignUpFragment.TAG, "LOADER_RESERVE_GAMERTAG finished");
            if (!result.hasError()) {
                SignUpFragment.this.state.gamerTag = SignUpFragment.this.editTextGamerTag.getText().toString();
                SignUpFragment.this.state.reserved = true;
                SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
            } else if (result.getError().getHttpStatus() == 409) {
                SignUpFragment.this.state.gamerTagWithSuggestions = SignUpFragment.this.editTextGamerTag.getText().toString();
                SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
                SignUpFragment.this.getLoaderManager().restartLoader(SignUpFragment.LOADER_SUGGESTIONS, null, SignUpFragment.this.suggestionsCallbacks);
            } else {
                Log.e(SignUpFragment.TAG, result.getError().toString());
                UTCError.trackServiceFailure(Errors.ReserveGamerTag, Signup.View, result.getError());
                SignUpFragment.this.setGamerTagState(GamerTagState.ERROR);
            }
        }

        public void onLoaderReset(Loader<Result<Void>> loader) {
            SignUpFragment.this.state.reserved = SignUpFragment.$assertionsDisabled;
            SignUpFragment.this.state.gamerTagWithSuggestions = null;
            SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
        }
    };
    private GamerTagState gamerTagState;
    private final SparseArray<LoaderInfo> loaderMap = new SparseArray();
    private final OnItemClickListener onSuggestionClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
            SignUpFragment.this.editTextGamerTag.setText((CharSequence) SignUpFragment.this.suggestionsListAdapter.getItem(i));
        }
    };
    private TextView privacyDetailsText;
    private TextView privacyText;
    private AccountProvisioningResult provisioningResult;
    private ScrollView scrollView;
    private View searchButton;
    private State state;
    private final LoaderCallbacks<Result<Suggestions.Response>> suggestionsCallbacks = new LoaderCallbacks<Result<Suggestions.Response>>() {
        public Loader<Result<Suggestions.Response>> onCreateLoader(int i, Bundle bundle) {
            Log.d(SignUpFragment.TAG, "Creating LOADER_SUGGESTIONS");
            HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, EndpointsFactory.get().userManagement(), "/gamertags/generate"), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
            Suggestions.Request request = new Suggestions.Request();
            request.Algorithm = SignUpFragment.LOADER_CLAIM_GAMERTAG;
            request.Count = SignUpFragment.LOADER_SUGGESTIONS;
            request.Locale = Locale.getDefault().toString().replace("_", "-");
            request.Seed = SignUpFragment.this.editTextGamerTag.getText().toString();
            Log.d(SignUpFragment.TAG, "getting suggestions for " + request.Seed);
            appendCommonParameters.setRequestBody(new Gson().toJson(request, Suggestions.Request.class));
            return new ObjectLoader(SignUpFragment.this.getActivity(), Suggestions.Response.class, new Gson(), appendCommonParameters);
        }

        public void onLoadFinished(Loader<Result<Suggestions.Response>> loader, Result<Suggestions.Response> result) {
            Log.d(SignUpFragment.TAG, "LOADER_SUGGESTIONS finished");
            if (result.hasData()) {
                Log.d(SignUpFragment.TAG, "Got suggestions");
                SignUpFragment.this.state.suggestions = (Suggestions.Response) result.getData();
                SignUpFragment.this.resetGamerTagState(SignUpFragment.this.editTextGamerTag.getText());
                return;
            }
            Log.d(SignUpFragment.TAG, "Error getting suggestions: " + result.getError());
            UTCError.trackServiceFailure(Errors.LoadSuggestions, Signup.View, result.getError());
        }

        public void onLoaderReset(Loader<Result<Suggestions.Response>> loader) {
            SignUpFragment.this.state.suggestions = null;
        }
    };
    private AbsListView suggestionsList;
    private ArrayAdapter<String> suggestionsListAdapter;
    private TextView textGamerTagComment;
    private final ClickableSpan xboxDotComLauncher = new ClickableSpan() {
        public void onClick(View view) {
            Log.d(SignUpFragment.TAG, "xboxDotComLauncher.onClick");
            try {
                SignUpFragment.this.startActivity(new Intent("android.intent.action.VIEW", Const.URL_XBOX_COM));
            } catch (ActivityNotFoundException e) {
                Log.e(SignUpFragment.TAG, e.getMessage());
            }
        }
    };

    public interface Callbacks {
        void onCloseWithStatus(Status status);
    }

    private enum GamerTagState {
        UNINITIALIZED(R.string.xbid_tools_empty),
        INITIAL(R.string.xbid_gamertag_available),
        EMPTY(R.string.xbid_tools_empty),
        AVAILABLE(R.string.xbid_gamertag_available),
        UNAVAILABLE(R.string.xbid_gamertag_not_available_no_suggestions_android),
        UNAVAILABLE_WITH_SUGGESTIONS(R.string.xbid_gamertag_not_available_android),
        UNKNOWN(R.string.xbid_gamertag_check_availability),
        CHECKING(R.string.xbid_gamertag_checking_android),
        ERROR(R.string.xbid_gamertag_checking_error);
        
        private final int stringId;

        private GamerTagState(int i) {
            this.stringId = i;
        }

        public int getStringId() {
            return this.stringId;
        }
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
        public String gamerTag;
        public String gamerTagWithSuggestions;
        public boolean reserved;
        public Suggestions.Response suggestions;

        public State() {
            this.gamerTag = null;
            this.reserved = SignUpFragment.$assertionsDisabled;
            this.gamerTagWithSuggestions = null;
            this.suggestions = null;
            this.errorHelper = new ErrorHelper();
        }

        protected State(Parcel parcel) {
            this.gamerTag = parcel.readString();
            this.reserved = parcel.readByte() != (byte) 0 ? true : SignUpFragment.$assertionsDisabled;
            this.gamerTagWithSuggestions = parcel.readString();
            this.suggestions = (Suggestions.Response) parcel.readParcelable(Suggestions.Response.class.getClassLoader());
            this.errorHelper = (ErrorHelper) parcel.readParcelable(ErrorHelper.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }

        public boolean hasSuggestions() {
            return (this.suggestions == null || this.suggestions.Gamertags == null || this.suggestions.Gamertags.isEmpty()) ? SignUpFragment.$assertionsDisabled : true;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.gamerTag);
            parcel.writeByte((byte) (this.reserved ? SignUpFragment.LOADER_CLAIM_GAMERTAG : 0));
            parcel.writeString(this.gamerTagWithSuggestions);
            parcel.writeParcelable(this.suggestions, i);
            parcel.writeParcelable(this.errorHelper, i);
        }
    }

    public enum Status {
        NO_ERROR,
        ERROR_USER_CANCEL,
        ERROR_SWITCH_USER,
        PROVIDER_ERROR
    }

    public SignUpFragment() {
        this.loaderMap.put(LOADER_RESERVE_GAMERTAG, new ObjectLoaderInfo(this.gamerTagReservationCallbacks));
        this.loaderMap.put(LOADER_CLAIM_GAMERTAG, new ObjectLoaderInfo(this.gamerTagClaimCallbacks));
        this.loaderMap.put(LOADER_SUGGESTIONS, new ObjectLoaderInfo(this.suggestionsCallbacks));
    }

    private void resetGamerTagState(CharSequence charSequence) {
        if (TextUtils.isEmpty(this.state.gamerTag)) {
            setGamerTagState(GamerTagState.UNINITIALIZED);
        } else if (TextUtils.isEmpty(charSequence)) {
            setGamerTagState(GamerTagState.EMPTY);
        } else if (TextUtils.equals(charSequence, this.state.gamerTag)) {
            if (TextUtils.equals(this.state.gamerTag, this.provisioningResult.getGamerTag())) {
                setGamerTagState(GamerTagState.INITIAL);
            } else if (this.state.reserved) {
                setGamerTagState(GamerTagState.AVAILABLE);
            } else if (TextUtils.equals(charSequence, this.state.gamerTagWithSuggestions)) {
                setGamerTagState(this.state.hasSuggestions() ? GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS : GamerTagState.UNAVAILABLE);
            } else {
                setGamerTagState(GamerTagState.UNKNOWN);
            }
        } else if (TextUtils.equals(charSequence, this.state.gamerTagWithSuggestions)) {
            setGamerTagState(this.state.hasSuggestions() ? GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS : GamerTagState.UNAVAILABLE);
        } else {
            setGamerTagState(GamerTagState.UNKNOWN);
        }
    }

    private void setGamerTagState(GamerTagState gamerTagState) {
        int i = 0;
        boolean z = true;
        this.textGamerTagComment.setText(gamerTagState.getStringId());
        this.textGamerTagComment.setFocusable(gamerTagState == GamerTagState.UNKNOWN ? true : $assertionsDisabled);
        EditText editText = this.editTextGamerTag;
        boolean z2 = (gamerTagState == GamerTagState.CHECKING || gamerTagState == GamerTagState.UNINITIALIZED) ? $assertionsDisabled : true;
        editText.setEnabled(z2);
        z2 = (gamerTagState == GamerTagState.UNKNOWN || gamerTagState == GamerTagState.ERROR) ? true : $assertionsDisabled;
        this.textGamerTagComment.setEnabled(z2);
        this.searchButton.setEnabled(z2);
        this.searchButton.setVisibility(z2 ? 0 : 8);
        Button button = this.claimItButton;
        if (!(gamerTagState == GamerTagState.AVAILABLE || gamerTagState == GamerTagState.INITIAL)) {
            z = $assertionsDisabled;
        }
        button.setEnabled(z);
        if (gamerTagState == GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS && this.gamerTagState != gamerTagState) {
            this.suggestionsListAdapter.clear();
            if (this.state.hasSuggestions()) {
                this.suggestionsListAdapter.addAll(this.state.suggestions.Gamertags);
            }
            this.suggestionsListAdapter.notifyDataSetChanged();
        } else if (gamerTagState != GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS && this.gamerTagState == GamerTagState.UNAVAILABLE_WITH_SUGGESTIONS) {
            this.suggestionsListAdapter.clear();
            this.suggestionsListAdapter.notifyDataSetChanged();
        }
        View view = this.clearTextButton;
        if (TextUtils.isEmpty(this.editTextGamerTag.getText())) {
            i = 8;
        }
        view.setVisibility(i);
        this.gamerTagState = gamerTagState;
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
        int id = view.getId();
        if (id == R.id.xbid_enter_gamertag_comment || id == R.id.xbid_search) {
            setGamerTagState(GamerTagState.CHECKING);
            Log.d(TAG, "Restarting LOADER_RESERVE_GAMERTAG");
            UTCSignup.trackSearchGamerTag(this.provisioningResult, getActivityTitle());
            getLoaderManager().restartLoader(LOADER_RESERVE_GAMERTAG, null, this.gamerTagReservationCallbacks);
        } else if (id == R.id.xbid_aleady_have_gamer_tag_answer) {
            UTCSignup.trackSignInWithDifferentUser(this.provisioningResult, getActivityTitle());
            UTCUser.setIsSilent($assertionsDisabled);
            this.callbacks.onCloseWithStatus(Status.ERROR_SWITCH_USER);
        } else if (id == R.id.xbid_claim_it) {
            if (this.gamerTagState == GamerTagState.INITIAL) {
                Log.d(TAG, "Interop.SignUpStatus.NO_ERROR");
                this.callbacks.onCloseWithStatus(Status.NO_ERROR);
            } else {
                Log.d(TAG, "Restarting LOADER_CLAIM_GAMERTAG");
                getLoaderManager().restartLoader(LOADER_CLAIM_GAMERTAG, null, this.gamerTagClaimCallbacks);
            }
            UTCSignup.trackClaimGamerTag(this.provisioningResult, getActivityTitle());
        } else if (id == R.id.xbid_clear_text) {
            this.editTextGamerTag.setText(BuildConfig.FLAVOR);
            UTCSignup.trackClearGamerTag(this.provisioningResult, getActivityTitle());
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Bundle arguments = getArguments();
        if (arguments == null) {
            Log.e(TAG, "No arguments provided");
            this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR);
        } else if (!arguments.containsKey(ARG_ACCOUNT_PROVISIONING_RESULT)) {
            Log.e(TAG, "No ARG_ACCOUNT_PROVISIONING_RESULT");
            this.callbacks.onCloseWithStatus(Status.PROVIDER_ERROR);
        }
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        return layoutInflater.inflate(R.layout.xbid_fragment_sign_up, viewGroup, $assertionsDisabled);
    }

    public void onDetach() {
        UTCPageView.removePage();
        super.onDetach();
        this.callbacks = NO_OP_CALLBACKS;
    }

    public void onResume() {
        super.onResume();
        this.state.errorHelper.restartLoader();
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelable(KEY_STATE, this.state);
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.scrollView = (ScrollView) view.findViewById(R.id.xbid_scroll_container);
        this.bottomBarShadow = view.findViewById(R.id.xbid_bottom_bar_shadow);
        this.scrollView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                SignUpFragment.this.bottomBarShadow.setVisibility(UiUtil.canScroll(SignUpFragment.this.scrollView) ? 0 : 4);
            }
        });
        this.editTextGamerTagContainer = view.findViewById(R.id.xbid_enter_gamertag_container);
        this.editTextGamerTag = (EditText) view.findViewById(R.id.xbid_enter_gamertag);
        this.editTextGamerTag.addTextChangedListener(this.gamerTagChangeListener);
        this.editTextGamerTag.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                SignUpFragment.this.editTextGamerTagContainer.setBackgroundResource(z ? R.drawable.xbid_edit_text_state_focused : R.drawable.xbid_edit_text_state_normal);
            }
        });
        this.clearTextButton = view.findViewById(R.id.xbid_clear_text);
        this.clearTextButton.setOnClickListener(this);
        this.searchButton = view.findViewById(R.id.xbid_search);
        this.searchButton.setOnClickListener(this);
        this.textGamerTagComment = (TextView) view.findViewById(R.id.xbid_enter_gamertag_comment);
        this.textGamerTagComment.setOnClickListener(this);
        this.privacyText = (TextView) view.findViewById(R.id.xbid_privacy);
        this.privacyDetailsText = (TextView) view.findViewById(R.id.xbid_privacy_details);
        TextView textView = (TextView) view.findViewById(R.id.xbid_aleady_have_gamer_tag_answer);
        textView.setOnClickListener(this);
        textView.setText(Html.fromHtml("<u>" + getString(R.string.xbid_another_sign_in) + "</u>"));
        this.claimItButton = (Button) view.findViewById(R.id.xbid_claim_it);
        this.claimItButton.setOnClickListener(this);
        this.suggestionsList = (AbsListView) view.findViewById(R.id.xbid_suggestions_list);
        this.suggestionsListAdapter = new ArrayAdapter(getActivity(), R.layout.xbid_row_suggestion, R.id.xbid_suggestion_text);
        this.suggestionsList.setAdapter(this.suggestionsListAdapter);
        this.suggestionsList.setOnItemClickListener(this.onSuggestionClickListener);
        this.provisioningResult = (AccountProvisioningResult) getArguments().getParcelable(ARG_ACCOUNT_PROVISIONING_RESULT);
        if (this.provisioningResult != null) {
            UTCCommonDataModel.setUserId(this.provisioningResult.getXuid());
        }
        UTCSignup.trackPageView(getActivityTitle());
        AgeGroup ageGroup = this.provisioningResult.getAgeGroup();
        if (bundle == null) {
            this.state = new State();
            this.state.gamerTag = this.provisioningResult.getGamerTag();
            this.editTextGamerTag.setText(this.state.gamerTag);
        } else {
            this.state = (State) bundle.getParcelable(KEY_STATE);
            resetGamerTagState(this.editTextGamerTag.getText());
        }
        this.state.errorHelper.setActivityContext(this);
        textView = this.privacyText;
        int i = R.string.xbid_privacy_settings_header_android;
        Object[] objArr = new Object[LOADER_CLAIM_GAMERTAG];
        objArr[0] = getString(ageGroup.resIdAgeGroup);
        textView.setText(getString(i, objArr));
        UiUtil.ensureClickableSpanOnUnderlineSpan(this.privacyDetailsText, ageGroup.resIdAgeGroupDetails, this.xboxDotComLauncher);
    }
}
