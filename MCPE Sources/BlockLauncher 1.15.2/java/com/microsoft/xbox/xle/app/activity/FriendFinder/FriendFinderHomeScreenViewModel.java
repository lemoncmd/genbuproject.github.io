package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.xbox.idp.interop.LocalConfig;
import com.microsoft.xbox.idp.jobs.JobSilentSignIn;
import com.microsoft.xbox.idp.jobs.MSAJob;
import com.microsoft.xbox.idp.jobs.MSAJob.Callbacks;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.UpdateData;
import com.microsoft.xbox.service.model.UpdateType;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderModel.LoadFailedCallback;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.FriendsFinderStateResult;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.LinkedAccountOptInStatus;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.RecommendationType;
import com.microsoft.xbox.service.network.managers.IUserProfileResult.ProfileUser;
import com.microsoft.xbox.service.network.managers.IUserProfileResult.UserProfileResult;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.service.network.managers.friendfinder.UploadContactsAsyncTask;
import com.microsoft.xbox.service.network.managers.friendfinder.UploadContactsAsyncTask.UploadContactsCompleted;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.FriendFinderSettings;
import com.microsoft.xbox.xle.app.FriendFinderSettings.IconImageSize;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreen;
import com.microsoft.xbox.xle.app.adapter.FriendFinderHomeScreenAdapter;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.Date;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FriendFinderHomeScreenViewModel extends ViewModelBase implements Callbacks, LoadFailedCallback {
    private static final int MAX_SEARCH_LENGTH = 256;
    private static final String MSA_TAG = "FriendFinder.MSA";
    private static final String POLICY = "mbi_ssl";
    private static final String SCOPE = "ssl.live.com";
    private FBSettingsAsyncTask fbSettingsAsyncTask;
    private FBManagerAndModelInitTask initFBandModelTask;
    private boolean isLoadingFriendFinderState;
    private boolean isSearchGamertagTaskRunning;
    private boolean isUploadingContacts;
    private SingleEntryLoadingStatus searchGamertagLoadingStatus;
    private NetworkAsyncTask searchGamertagTask;
    private Boolean shouldShowDone;
    private UploadContactsAsyncTask uploadContactsAsyncTask;

    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus = new int[AsyncActionStatus.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.SUCCESS.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_CHANGE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_OP_SUCCESS.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.FAIL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[AsyncActionStatus.NO_OP_FAIL.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    protected class FBManagerAndModelInitTask extends NetworkAsyncTask<AsyncActionStatus> {
        protected FBManagerAndModelInitTask() {
        }

        protected boolean checkShouldExecute() {
            return !FacebookManager.getFacebookManagerReady().getIsReady() || FriendFinderModel.getInstance().shouldRefresh();
        }

        protected AsyncActionStatus loadDataInBackground() {
            FacebookManager.getInstance();
            FacebookManager.getFacebookManagerReady().waitForReady();
            ProfileModel.getMeProfileModel().loadSync(true);
            return FacebookManager.getFacebookManagerReady().getIsReady() ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            if (FacebookManager.getFacebookManagerReady().getIsReady()) {
                FriendFinderHomeScreenViewModel.this.onFacebookInitCompleted(AsyncActionStatus.SUCCESS);
            }
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderHomeScreenViewModel.this.onFacebookInitCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            FriendFinderHomeScreenViewModel.this.isLoadingFriendFinderState = true;
        }
    }

    protected class FBSettingsAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        protected FBSettingsAsyncTask() {
        }

        protected boolean checkShouldExecute() {
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            FriendFinderSettings friendFinderSettings = null;
            try {
                friendFinderSettings = ServiceManagerFactory.getInstance().getSLSServiceManager().getFriendFinderSettings();
                friendFinderSettings.getIconsFromJson(friendFinderSettings.ICONS);
            } catch (XLEException e) {
            }
            return friendFinderSettings != null ? AsyncActionStatus.SUCCESS : AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            FriendFinderHomeScreenViewModel.this.onFacebookSettingsCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            FriendFinderHomeScreenViewModel.this.isLoadingFriendFinderState = true;
        }
    }

    private class SearchGamertagRunner extends IDataLoaderRunnable<UserProfileResult> {
        private String gamerXuid;
        private String gamertag;

        public SearchGamertagRunner(String str) {
            this.gamertag = str;
        }

        public UserProfileResult buildData() throws XLEException {
            UserProfileResult SearchGamertag = ServiceManagerFactory.getInstance().getSLSServiceManager().SearchGamertag(this.gamertag);
            this.gamerXuid = ((ProfileUser) SearchGamertag.profileUsers.get(0)).id;
            if (!JavaUtil.isNullOrEmpty(this.gamerXuid)) {
                return SearchGamertag;
            }
            throw new XLEException(getDefaultErrorCode(), "Invalid gamertag returned from search service");
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_USER_PROFILE_INFO;
        }

        public void onPostExcute(AsyncResult<UserProfileResult> asyncResult) {
            FriendFinderHomeScreenViewModel.this.onSearchGamertagCompleted(asyncResult.getStatus(), this.gamerXuid);
        }

        public void onPreExecute() {
        }
    }

    public FriendFinderHomeScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.isSearchGamertagTaskRunning = false;
        this.adapter = new FriendFinderHomeScreenAdapter(this);
        this.searchGamertagLoadingStatus = new SingleEntryLoadingStatus();
    }

    private void cancelActiveTasks() {
        if (this.initFBandModelTask != null) {
            this.initFBandModelTask.cancel();
            this.initFBandModelTask = null;
        }
        if (this.fbSettingsAsyncTask != null) {
            this.fbSettingsAsyncTask.cancel();
            this.fbSettingsAsyncTask = null;
        }
        if (this.searchGamertagTask != null) {
            this.searchGamertagTask.cancel();
            this.searchGamertagTask = null;
        }
        if (this.uploadContactsAsyncTask != null) {
            this.uploadContactsAsyncTask.cancel();
            this.uploadContactsAsyncTask = null;
        }
    }

    private boolean hasReadContactsPermission() {
        return ContextCompat.checkSelfPermission(XboxTcuiSdk.getActivity(), "android.permission.READ_CONTACTS") == 0;
    }

    private void navigateToInfo(FriendFinderType friendFinderType) {
        ActivityParameters activityParameters = new ActivityParameters();
        activityParameters.putFriendFinderType(friendFinderType);
        try {
            NavigationManager.getInstance().PushScreen(FriendFinderInfoScreen.class, activityParameters);
        } catch (XLEException e) {
        }
    }

    private void navigateToSuggestions(FriendFinderType friendFinderType) {
        try {
            ActivityParameters activityParameters = new ActivityParameters();
            activityParameters.putFriendFinderType(friendFinderType);
            NavigationManager.getInstance().PushScreen(FriendFinderSuggestionsScreen.class, activityParameters);
        } catch (XLEException e) {
        }
    }

    private void onSearchGamertagCompleted(AsyncActionStatus asyncActionStatus, String str) {
        this.isSearchGamertagTaskRunning = false;
        switch (AnonymousClass3.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                try {
                    if (!JavaUtil.isNullOrEmpty(str)) {
                        ActivityParameters activityParameters = new ActivityParameters();
                        activityParameters.putSelectedProfile(str);
                        UTCFriendFinder.trackGamertagSearchSuccess(getScreen().getName(), str);
                        NavigationManager.getInstance().PushScreen(ProfileScreen.class, activityParameters);
                        break;
                    }
                } catch (XLEException e) {
                    break;
                }
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.FriendsHub_CouldNotFindGamer);
                break;
        }
        updateAdapter();
    }

    private void showContactsPermissionDialog() {
        Context activity = XboxTcuiSdk.getActivity();
        String string = activity.getString(activity.getApplicationInfo().labelRes);
        CharSequence format = String.format(activity.getString(R.string.Contacts_Permission_Denied_Android), new Object[]{string, string});
        Builder builder = new Builder(activity);
        builder.setTitle(R.string.Contacts_Permission_Denied_Header);
        builder.setMessage(format);
        builder.create().show();
    }

    private boolean validSearchGamertag(String str) {
        return (JavaUtil.isNullOrEmpty(str) || str.length() > MAX_SEARCH_LENGTH || JavaUtil.urlEncode(str) == null) ? false : true;
    }

    public boolean facebookLinked() {
        return FacebookManager.getFacebookManagerReady().getIsReady() ? FacebookManager.getInstance().isFacebookFriendFinderOptedIn() : false;
    }

    public void finishFriendFinder() {
        try {
            NavigationManager.getInstance().PopAllScreens();
        } catch (XLEException e) {
        }
    }

    public String getFacebookIconUri() {
        return FriendFinderSettings.getIconBySize(RecommendationType.FacebookFriend.name(), IconImageSize.MEDIUM);
    }

    public boolean isBusy() {
        return this.isLoadingFriendFinderState || this.isUploadingContacts;
    }

    public void load(boolean z) {
        cancelActiveTasks();
        if (JavaUtil.isNullOrEmpty(ProjectSpecificDataProvider.getInstance().getSCDRpsTicket())) {
            new JobSilentSignIn(XboxTcuiSdk.getActivity(), "FriendFinderHome", this, SCOPE, POLICY, new LocalConfig().getCid()).start();
        }
        if (FacebookManager.getFacebookManagerReady().getIsReady()) {
            FriendFinderModel.getInstance().loadAsync(true, this);
        } else {
            this.initFBandModelTask = new FBManagerAndModelInitTask();
            this.initFBandModelTask.load(z);
        }
        this.fbSettingsAsyncTask = new FBSettingsAsyncTask();
        this.fbSettingsAsyncTask.load(z);
    }

    public void navigateToFacebookSuggestions() {
        navigateToSuggestions(FriendFinderType.FACEBOOK);
    }

    public void navigateToLinkFacebook() {
        navigateToInfo(FriendFinderType.FACEBOOK);
    }

    public void navigateToLinkPhone() {
        if (hasReadContactsPermission()) {
            navigateToInfo(FriendFinderType.PHONE);
        } else {
            showContactsPermissionDialog();
        }
    }

    public void navigateToPhoneSuggestions() {
        if (hasReadContactsPermission()) {
            if (this.uploadContactsAsyncTask != null) {
                this.uploadContactsAsyncTask.cancel();
                this.uploadContactsAsyncTask = null;
            }
            this.uploadContactsAsyncTask = new UploadContactsAsyncTask(new UploadContactsCompleted() {
                public void onResult(AsyncActionStatus asyncActionStatus) {
                    FriendFinderHomeScreenViewModel.this.isUploadingContacts = false;
                    FriendFinderHomeScreenViewModel.this.navigateToSuggestions(FriendFinderType.PHONE);
                }
            });
            this.isUploadingContacts = true;
            updateAdapter();
            this.uploadContactsAsyncTask.load(true);
            return;
        }
        showContactsPermissionDialog();
    }

    public void onAccountAcquired(MSAJob mSAJob, UserAccount userAccount) {
    }

    protected void onFacebookInitCompleted(AsyncActionStatus asyncActionStatus) {
        switch (AnonymousClass3.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                FriendFinderModel.getInstance().loadAsync(true, this);
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Service_ErrorText);
                return;
            default:
                return;
        }
    }

    protected void onFacebookSettingsCompleted(AsyncActionStatus asyncActionStatus) {
        updateAdapter();
    }

    public void onFailure(MSAJob mSAJob, Exception exception) {
        Log.i(MSA_TAG, "onFailure - ignoring and will fail phone finder if invoked. " + Log.getStackTraceString(exception));
    }

    public void onFriendFinderLoadFailed() {
        Builder builder = new Builder(XboxTcuiSdk.getActivity());
        builder.setMessage(R.string.Service_ErrorText);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.OK_Text, new OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    NavigationManager.getInstance().PopAllScreens();
                } catch (XLEException e) {
                }
            }
        });
        builder.create().show();
    }

    public void onRehydrate() {
        this.adapter = new FriendFinderHomeScreenAdapter(this);
    }

    public void onSignedOut(MSAJob mSAJob) {
    }

    protected void onStartOverride() {
        FriendFinderModel.getInstance().addUniqueObserver(this);
    }

    protected void onStopOverride() {
        cancelActiveTasks();
        FriendFinderModel.getInstance().removeObserver(this);
    }

    public void onTicketAcquired(MSAJob mSAJob, Ticket ticket) {
        Log.i(MSA_TAG, "onTicketAcquired - " + ticket.getValue());
        ProjectSpecificDataProvider.getInstance().setSCDRpsTicket(ticket.getValue());
    }

    public void onUiNeeded(MSAJob mSAJob) {
        Log.i(MSA_TAG, "onUiNeeded - ignoring and will fail phone finder if invoked.");
    }

    public void onUserCancel(MSAJob mSAJob) {
    }

    public boolean phoneLinked() {
        FriendsFinderStateResult result = FriendFinderModel.getInstance().getResult();
        return result != null && result.getPhoneAccountOptInStatus() == LinkedAccountOptInStatus.OptedIn;
    }

    public void searchGamertag(String str) {
        if (!this.isSearchGamertagTaskRunning) {
            if (this.searchGamertagTask != null) {
                this.searchGamertagTask.cancel();
                this.searchGamertagTask = null;
            }
            if (validSearchGamertag(str)) {
                this.isSearchGamertagTaskRunning = true;
                this.searchGamertagTask = DataLoadUtil.StartLoadFromUI(true, new Date().getTime(), null, this.searchGamertagLoadingStatus, new SearchGamertagRunner(str.trim()));
                return;
            }
            showError(R.string.FriendsHub_CouldNotFindGamer);
        }
    }

    public boolean shouldShowDone() {
        if (this.shouldShowDone == null) {
            ActivityParameters activityParameters = NavigationManager.getInstance().getActivityParameters();
            boolean z = activityParameters != null && activityParameters.getFriendFinderDone();
            this.shouldShowDone = Boolean.valueOf(z);
        }
        return this.shouldShowDone.booleanValue();
    }

    protected void updateOverride(AsyncResult<UpdateData> asyncResult) {
        if (((UpdateData) asyncResult.getResult()).getUpdateType() == UpdateType.FriendFinderFacebook && ((UpdateData) asyncResult.getResult()).getIsFinal()) {
            FriendsFinderStateResult result = FriendFinderModel.getInstance().getResult();
            this.isLoadingFriendFinderState = false;
            if (FacebookManager.getInstance().getFacebookFriendFinderState() == null || (result != null && FacebookManager.getInstance().getFacebookFriendFinderState().isFacebookStateChanged(result))) {
                FacebookManager.getInstance().setFacebookFriendFinderState(result);
            }
            updateAdapter();
        }
    }
}
