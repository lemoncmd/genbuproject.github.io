package com.microsoft.xbox.xle.app.activity.Profile;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer.AddFollowingUserResponse;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer.MutedListResult;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer.NeverListResult;
import com.microsoft.xbox.service.network.managers.PeopleResponseError;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.ScreenLayout;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.activity.ReportUserScreen;
import com.microsoft.xbox.xle.app.adapter.ProfileScreenAdapter;
import com.microsoft.xbox.xle.telemetry.helpers.UTCChangeRelationship;
import com.microsoft.xbox.xle.telemetry.helpers.UTCPeopleHub;
import com.microsoft.xbox.xle.viewmodel.ChangeFriendshipDialogViewModel;
import com.microsoft.xbox.xle.viewmodel.ViewModelBase;
import com.microsoft.xbox.xle.viewmodel.XLEGlobalData;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxAppDeepLinker;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class ProfileScreenViewModel extends ViewModelBase {
    private static final String TAG = ProfileScreenViewModel.class.getSimpleName();
    private AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask;
    private AddUserToMutedListAsyncTask addUserToMutedListAsyncTask;
    private AddUserToNeverListAsyncTask addUserToNeverListAsyncTask;
    private AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask;
    private FollowersData basicData;
    private ChangeFriendshipDialogViewModel changeFriendshipDialogViewModel;
    private HashSet<ChangeFriendshipFormOptions> changeFriendshipForm;
    private boolean isAddingUserToBlockList;
    private boolean isAddingUserToFollowingList;
    private boolean isAddingUserToMutedList;
    private boolean isAddingUserToShareIdentityList;
    private boolean isBlocked;
    private boolean isFavorite;
    private boolean isFollowing;
    private boolean isLoadingUserMutedList;
    private boolean isLoadingUserNeverList;
    private boolean isLoadingUserProfile;
    private boolean isMuted;
    private boolean isRemovingUserFromBlockList;
    private boolean isRemovingUserFromMutedList;
    private boolean isShowingFailureDialog;
    private LoadUserProfileAsyncTask loadMeProfileTask;
    private LoadUserMutedListAsyncTask loadUserMutedListTask;
    private LoadUserNeverListAsyncTask loadUserNeverListTask;
    private LoadUserProfileAsyncTask loadUserProfileTask;
    protected ProfileModel model;
    private RemoveUserFromMutedListAsyncTask removeUserFromMutedListAsyncTask;
    private RemoveUserToNeverListAsyncTask removeUserToNeverListAsyncTask;

    static /* synthetic */ class AnonymousClass4 {
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

    private class AddUserToFollowingListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String followingUserXuid;
        private boolean isFollowingUser = false;

        public AddUserToFollowingListAsyncTask(String str) {
            this.followingUserXuid = str;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            if (meProfileModel == null) {
                return AsyncActionStatus.FAIL;
            }
            AsyncActionStatus status = meProfileModel.addUserToFollowingList(this.forceLoad, this.followingUserXuid).getStatus();
            if (!AsyncActionStatus.getIsFail(status)) {
                AddFollowingUserResponse addUserToFollowingResult = meProfileModel.getAddUserToFollowingResult();
                if (addUserToFollowingResult != null && !addUserToFollowingResult.getAddFollowingRequestStatus() && addUserToFollowingResult.code == PeopleResponseError.MAX_FOLLOWING_LIMIT_REACHED) {
                    return AsyncActionStatus.FAIL;
                }
                ProfileScreenViewModel.this.model.loadProfileSummary(true);
                meProfileModel.loadProfileSummary(true);
                ArrayList followingData = meProfileModel.getFollowingData();
                if (followingData != null) {
                    Iterator it = followingData.iterator();
                    while (it.hasNext()) {
                        if (((FollowersData) it.next()).xuid.equals(this.followingUserXuid)) {
                            this.isFollowingUser = true;
                            return status;
                        }
                    }
                }
            }
            return status;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onAddUserToFollowingListCompleted(AsyncActionStatus.NO_CHANGE, this.isFollowingUser);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onAddUserToFollowingListCompleted(asyncActionStatus, this.isFollowingUser);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isAddingUserToFollowingList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    private class AddUserToMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String mutedUserXuid;

        public AddUserToMutedListAsyncTask(String str) {
            this.mutedUserXuid = str;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.addUserToMutedList(this.forceLoad, this.mutedUserXuid).getStatus() : AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onAddUserToMutedListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onAddUserToMutedListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isAddingUserToMutedList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    private class AddUserToNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String blockUserXuid;

        public AddUserToNeverListAsyncTask(String str) {
            this.blockUserXuid = str;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.addUserToNeverList(this.forceLoad, this.blockUserXuid).getStatus() : AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onAddUserToBlockListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onAddUserToBlockListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isAddingUserToBlockList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    private class AddUserToShareIdentityListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> usersToAdd;

        public AddUserToShareIdentityListAsyncTask(ArrayList<String> arrayList) {
            this.usersToAdd = arrayList;
        }

        protected boolean checkShouldExecute() {
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.addUserToShareIdentity(this.forceLoad, this.usersToAdd).getStatus() : AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onAddUseToShareIdentityListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isAddingUserToShareIdentityList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    public enum ChangeFriendshipFormOptions {
        ShouldAddUserToFriendList,
        ShouldRemoveUserFromFriendList,
        ShouldAddUserToFavoriteList,
        ShouldRemoveUserFromFavoriteList,
        ShouldAddUserToShareIdentityList,
        ShouldRemoveUserFromShareIdentityList
    }

    private class LoadUserMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserMutedListAsyncTask(ProfileModel profileModel) {
            this.model = profileModel;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return this.model.shouldRefresh();
        }

        protected AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(this.model);
            AsyncActionStatus status = this.model.loadSync(this.forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? this.model.loadUserMutedList(true).getStatus() : status;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onLoadUserMutedListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isLoadingUserMutedList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    private class LoadUserNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserNeverListAsyncTask(ProfileModel profileModel) {
            this.model = profileModel;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return this.model.shouldRefresh();
        }

        protected AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(this.model);
            AsyncActionStatus status = this.model.loadSync(this.forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? this.model.loadUserNeverList(true).getStatus() : status;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onLoadUserNeverListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isLoadingUserNeverList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    private class LoadUserProfileAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ProfileModel model;

        private LoadUserProfileAsyncTask(ProfileModel profileModel) {
            this.model = profileModel;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return this.model.shouldRefresh() || this.model.shouldRefreshProfileSummary();
        }

        protected AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(this.model);
            AsyncActionStatus status = this.model.loadSync(this.forceLoad).getStatus();
            return (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) ? this.model.loadProfileSummary(this.forceLoad).getStatus() : status;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onLoadUserProfileCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onLoadUserProfileCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isLoadingUserProfile = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    private class RemoveUserFromMutedListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String mutedUserXuid;

        public RemoveUserFromMutedListAsyncTask(String str) {
            this.mutedUserXuid = str;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.removeUserFromMutedList(this.forceLoad, this.mutedUserXuid).getStatus() : AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onRemoveUserFromMutedListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onRemoveUserFromMutedListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isRemovingUserFromMutedList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    private class RemoveUserToNeverListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String unblockUserXuid;

        public RemoveUserToNeverListAsyncTask(String str) {
            this.unblockUserXuid = str;
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.removeUserFromNeverList(this.forceLoad, this.unblockUserXuid).getStatus() : AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.onRemoveUserFromBlockListCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ProfileScreenViewModel.this.onRemoveUserFromBlockListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ProfileScreenViewModel.this.isRemovingUserFromBlockList = true;
            ProfileScreenViewModel.this.updateAdapter();
        }
    }

    public ProfileScreenViewModel(ScreenLayout screenLayout) {
        super(screenLayout);
        this.changeFriendshipForm = new HashSet();
        this.isFollowing = false;
        this.isFavorite = false;
        this.isBlocked = false;
        this.isMuted = false;
        this.model = ProfileModel.getProfileModel(NavigationManager.getInstance().getActivityParameters().getSelectedProfile());
        this.adapter = new ProfileScreenAdapter(this);
    }

    private void notifyDialogAsyncTaskCompleted() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskCompleted();
    }

    private void notifyDialogAsyncTaskFailed(String str) {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskFailed(str);
    }

    private void onAddUseToShareIdentityListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isAddingUserToShareIdentityList = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                notifyDialogAsyncTaskCompleted();
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                break;
        }
        updateAdapter();
    }

    private void onAddUserToBlockListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isAddingUserToBlockList = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
                if (meProfileModel != null) {
                    this.isBlocked = false;
                    NeverListResult neverListData = meProfileModel.getNeverListData();
                    if (neverListData != null) {
                        this.isBlocked = neverListData.contains(this.model.getXuid());
                    }
                    this.isFollowing = false;
                    break;
                }
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Messages_Error_FailedToBlockUser);
                break;
        }
        updateAdapter();
    }

    private void onAddUserToFollowingListCompleted(AsyncActionStatus asyncActionStatus, boolean z) {
        this.isAddingUserToFollowingList = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.isFollowing = z;
                XLEGlobalData.getInstance().AddForceRefresh(ProfileScreenViewModel.class);
                notifyDialogAsyncTaskCompleted();
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                AddFollowingUserResponse addFollowingUserResponse = null;
                ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
                if (meProfileModel != null) {
                    addFollowingUserResponse = meProfileModel.getAddUserToFollowingResult();
                }
                if (addFollowingUserResponse != null && !addFollowingUserResponse.getAddFollowingRequestStatus() && addFollowingUserResponse.code == PeopleResponseError.MAX_FOLLOWING_LIMIT_REACHED) {
                    notifyDialogAsyncTaskFailed(addFollowingUserResponse.description);
                    break;
                } else {
                    notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorAddingFriend));
                    break;
                }
                break;
        }
        updateAdapter();
    }

    private void onAddUserToMutedListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isAddingUserToMutedList = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                if (ProfileModel.getMeProfileModel() != null) {
                    this.isMuted = true;
                    break;
                }
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Messages_Error_FailedToMuteUser);
                break;
        }
        updateAdapter();
    }

    private void onRemoveUserFromBlockListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isRemovingUserFromBlockList = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
                if (meProfileModel != null) {
                    this.isBlocked = false;
                    NeverListResult neverListData = meProfileModel.getNeverListData();
                    if (neverListData != null) {
                        this.isBlocked = neverListData.contains(this.model.getXuid());
                        break;
                    }
                }
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Messages_Error_FailedToUnblockUser);
                break;
        }
        updateAdapter();
    }

    private void onRemoveUserFromMutedListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isRemovingUserFromMutedList = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                if (ProfileModel.getMeProfileModel() != null) {
                    this.isMuted = false;
                    break;
                }
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                showError(R.string.Messages_Error_FailedToUnmuteUser);
                break;
        }
        updateAdapter();
    }

    public void addFollowingUser() {
        if (ProfileModel.hasPrivilegeToAddFriend()) {
            if (this.addUserToFollowingListAsyncTask != null) {
                this.addUserToFollowingListAsyncTask.cancel();
            }
            this.addUserToFollowingListAsyncTask = new AddUserToFollowingListAsyncTask(this.model.getXuid());
            this.addUserToFollowingListAsyncTask.load(true);
            return;
        }
        showError(R.string.Global_MissingPrivilegeError_DialogBody);
    }

    public void addUserToShareIdentityList() {
        if (this.addUserToShareIdentityListAsyncTask != null) {
            this.addUserToShareIdentityListAsyncTask.cancel();
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.model.getXuid());
        this.addUserToShareIdentityListAsyncTask = new AddUserToShareIdentityListAsyncTask(arrayList);
        this.addUserToShareIdentityListAsyncTask.load(true);
    }

    public void blockUser() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogTitle), XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogBody), XboxTcuiSdk.getResources().getString(R.string.OK_Text), new Runnable() {
            public void run() {
                ProfileScreenViewModel.this.blockUserInternal();
            }
        }, XboxTcuiSdk.getResources().getString(R.string.MessageDialog_Cancel), null);
        updateAdapter();
    }

    public void blockUserInternal() {
        UTCPeopleHub.trackBlockDialogComplete();
        if (this.addUserToNeverListAsyncTask != null) {
            this.addUserToNeverListAsyncTask.cancel();
        }
        this.addUserToNeverListAsyncTask = new AddUserToNeverListAsyncTask(this.model.getXuid());
        this.addUserToNeverListAsyncTask.load(true);
    }

    public String getGamerPicUrl() {
        return this.model.getGamerPicImageUrl();
    }

    public String getGamerScore() {
        return this.model.getGamerScore();
    }

    public String getGamerTag() {
        return this.model.getGamerTag();
    }

    public boolean getIsAddingUserToBlockList() {
        return this.isAddingUserToBlockList;
    }

    public boolean getIsAddingUserToMutedList() {
        return this.isAddingUserToMutedList;
    }

    public boolean getIsBlocked() {
        return this.isBlocked;
    }

    public boolean getIsFavorite() {
        return this.isFavorite;
    }

    public boolean getIsMuted() {
        return this.isMuted;
    }

    public boolean getIsRemovingUserFromBlockList() {
        return this.isRemovingUserFromBlockList;
    }

    public boolean getIsRemovingUserFromMutedList() {
        return this.isRemovingUserFromMutedList;
    }

    public int getPreferredColor() {
        return this.model.getPreferedColor();
    }

    public String getRealName() {
        return this.model.getRealName();
    }

    public String getXuid() {
        return this.model.getXuid();
    }

    public boolean isBusy() {
        return this.isLoadingUserProfile || this.isLoadingUserNeverList || this.isLoadingUserMutedList || this.isAddingUserToFollowingList || this.isAddingUserToShareIdentityList || this.isRemovingUserFromBlockList || this.isAddingUserToBlockList || this.isAddingUserToMutedList || this.isRemovingUserFromMutedList;
    }

    public boolean isCallerFollowingTarget() {
        return this.isFollowing;
    }

    public boolean isFacebookFriend() {
        return false;
    }

    public boolean isMeProfile() {
        return this.model.isMeProfile();
    }

    public void launchXboxApp() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        showOkCancelDialog(XboxTcuiSdk.getResources().getString(R.string.Messages_BlockUserConfirmation_DialogTitle), XboxTcuiSdk.getResources().getString(R.string.Messages_ViewInXboxApp_DialogBody), XboxTcuiSdk.getResources().getString(R.string.ConnectDialog_ContinueAsGuest), new Runnable() {
            public void run() {
                UTCPeopleHub.trackViewInXboxAppDialogComplete();
                XboxAppDeepLinker.showUserProfile(XboxTcuiSdk.getActivity(), ProfileScreenViewModel.this.model.getXuid());
            }
        }, XboxTcuiSdk.getResources().getString(R.string.MessageDialog_Cancel), null);
        updateAdapter();
    }

    public void load(boolean z) {
        if (this.loadUserProfileTask != null) {
            this.loadUserProfileTask.cancel();
        }
        this.loadMeProfileTask = new LoadUserProfileAsyncTask(ProfileModel.getMeProfileModel());
        this.loadMeProfileTask.load(true);
        if (!isMeProfile()) {
            if (this.loadUserNeverListTask != null) {
                this.loadUserNeverListTask.cancel();
            }
            this.loadUserNeverListTask = new LoadUserNeverListAsyncTask(ProfileModel.getMeProfileModel());
            this.loadUserNeverListTask.load(true);
            if (this.loadUserMutedListTask != null) {
                this.loadUserMutedListTask.cancel();
            }
            this.loadUserMutedListTask = new LoadUserMutedListAsyncTask(ProfileModel.getMeProfileModel());
            this.loadUserMutedListTask.load(true);
            this.loadUserProfileTask = new LoadUserProfileAsyncTask(this.model);
            this.loadUserProfileTask.load(true);
        }
    }

    public void muteUser() {
        if (this.addUserToMutedListAsyncTask != null) {
            this.addUserToMutedListAsyncTask.cancel();
        }
        this.addUserToMutedListAsyncTask = new AddUserToMutedListAsyncTask(this.model.getXuid());
        this.addUserToMutedListAsyncTask.load(true);
    }

    public void navigateToChangeRelationship() {
        if (ProfileModel.hasPrivilegeToAddFriend()) {
            UTCChangeRelationship.trackChangeRelationshipAction(getScreen().getName(), getXuid(), isCallerFollowingTarget(), isFacebookFriend());
            showChangeFriendshipDialog();
            return;
        }
        showError(R.string.Global_MissingPrivilegeError_DialogBody);
    }

    public void onLoadUserMutedListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isLoadingUserMutedList = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
                if (!(isMeProfile() || meProfileModel == null)) {
                    this.isMuted = false;
                    MutedListResult mutedList = meProfileModel.getMutedList();
                    if (mutedList != null) {
                        this.isMuted = mutedList.contains(this.model.getXuid());
                        break;
                    }
                }
                break;
        }
        updateAdapter();
    }

    public void onLoadUserNeverListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isLoadingUserNeverList = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
                if (!(isMeProfile() || meProfileModel == null)) {
                    this.isBlocked = false;
                    NeverListResult neverListData = meProfileModel.getNeverListData();
                    if (neverListData != null) {
                        this.isBlocked = neverListData.contains(this.model.getXuid());
                        break;
                    }
                }
                break;
        }
        updateAdapter();
    }

    public void onLoadUserProfileCompleted(AsyncActionStatus asyncActionStatus) {
        this.isLoadingUserProfile = false;
        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                if (!(isMeProfile() || ProfileModel.getMeProfileModel() == null)) {
                    this.isFollowing = this.model.isCallerFollowingTarget();
                    break;
                }
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                if (!this.isShowingFailureDialog) {
                    this.isShowingFailureDialog = true;
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
                    break;
                }
                break;
        }
        updateAdapter();
    }

    public void onRehydrate() {
        this.adapter = new ProfileScreenAdapter(this);
    }

    protected void onStartOverride() {
        this.isShowingFailureDialog = false;
    }

    protected void onStopOverride() {
        if (this.loadMeProfileTask != null) {
            this.loadMeProfileTask.cancel();
        }
        if (this.loadUserNeverListTask != null) {
            this.loadUserNeverListTask.cancel();
        }
        if (this.loadUserMutedListTask != null) {
            this.loadUserMutedListTask.cancel();
        }
        if (this.loadUserProfileTask != null) {
            this.loadUserProfileTask.cancel();
        }
        if (this.addUserToFollowingListAsyncTask != null) {
            this.addUserToFollowingListAsyncTask.cancel();
        }
        if (this.addUserToShareIdentityListAsyncTask != null) {
            this.addUserToShareIdentityListAsyncTask.cancel();
        }
        if (this.addUserToNeverListAsyncTask != null) {
            this.addUserToNeverListAsyncTask.cancel();
        }
        if (this.removeUserToNeverListAsyncTask != null) {
            this.removeUserToNeverListAsyncTask.cancel();
        }
        if (this.addUserToMutedListAsyncTask != null) {
            this.addUserToMutedListAsyncTask.cancel();
        }
        if (this.removeUserFromMutedListAsyncTask != null) {
            this.removeUserFromMutedListAsyncTask.cancel();
        }
    }

    public void showChangeFriendshipDialog() {
        if (this.changeFriendshipDialogViewModel == null) {
            this.changeFriendshipDialogViewModel = new ChangeFriendshipDialogViewModel(this.model);
        }
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).showChangeFriendshipDialog(this.changeFriendshipDialogViewModel, this);
    }

    public void showReportDialog() {
        try {
            NavigationManager.getInstance().PopScreensAndReplace(0, ReportUserScreen.class, false, false, false, NavigationManager.getInstance().getActivityParameters());
        } catch (XLEException e) {
        }
    }

    public void unblockUser() {
        if (this.removeUserToNeverListAsyncTask != null) {
            this.removeUserToNeverListAsyncTask.cancel();
        }
        this.removeUserToNeverListAsyncTask = new RemoveUserToNeverListAsyncTask(this.model.getXuid());
        this.removeUserToNeverListAsyncTask.load(true);
    }

    public void unmuteUser() {
        if (this.removeUserFromMutedListAsyncTask != null) {
            this.removeUserFromMutedListAsyncTask.cancel();
        }
        this.removeUserFromMutedListAsyncTask = new RemoveUserFromMutedListAsyncTask(this.model.getXuid());
        this.removeUserFromMutedListAsyncTask.load(true);
    }
}
