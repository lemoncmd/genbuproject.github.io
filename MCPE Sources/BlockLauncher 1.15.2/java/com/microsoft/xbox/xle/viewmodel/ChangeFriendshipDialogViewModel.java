package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer.AddFollowingUserResponse;
import com.microsoft.xbox.service.network.managers.PeopleResponseError;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.DialogManager;
import com.microsoft.xbox.toolkit.NetworkAsyncTask;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.network.ListState;
import com.microsoft.xbox.xle.app.SGProjectSpecificDialogManager;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreenViewModel.ChangeFriendshipFormOptions;
import com.microsoft.xbox.xle.telemetry.helpers.UTCChangeRelationship;
import com.microsoft.xbox.xle.telemetry.helpers.UTCChangeRelationship.FavoriteStatus;
import com.microsoft.xbox.xle.telemetry.helpers.UTCChangeRelationship.GamerType;
import com.microsoft.xbox.xle.telemetry.helpers.UTCChangeRelationship.RealNameStatus;
import com.microsoft.xbox.xle.telemetry.helpers.UTCChangeRelationship.Relationship;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class ChangeFriendshipDialogViewModel {
    private static final String TAG = ChangeFriendshipDialogViewModel.class.getSimpleName();
    private AddUserToFavoriteListAsyncTask addUserToFavoriteListAsyncTask;
    private AddUserToFollowingListAsyncTask addUserToFollowingListAsyncTask;
    private AddUserToShareIdentityListAsyncTask addUserToShareIdentityListAsyncTask;
    private HashSet<ChangeFriendshipFormOptions> changeFriendshipForm = new HashSet();
    private boolean isAddingUserToFavoriteList;
    private boolean isAddingUserToFollowingList;
    private boolean isAddingUserToShareIdentityList;
    private boolean isFavorite = false;
    private boolean isFollowing = false;
    private boolean isLoadingUserProfile;
    private boolean isRemovingUserFromFavoriteList;
    private boolean isRemovingUserFromFollowingList;
    private boolean isRemovingUserFromShareIdentityList;
    private boolean isSharingRealNameEnd;
    private boolean isSharingRealNameStart;
    private LoadPersonDataAsyncTask loadProfileAsyncTask;
    private ProfileModel model;
    private RemoveUserFromFavoriteListAsyncTask removeUserFromFavoriteListAsyncTask;
    private RemoveUserFromFollowingListAsyncTask removeUserFromFollowingListAsyncTask;
    private RemoveUserFromShareIdentityListAsyncTask removeUserFromShareIdentityListAsyncTask;
    private ListState viewModelState = ListState.LoadingState;

    static /* synthetic */ class AnonymousClass1 {
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

    private class AddUserToFavoriteListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private boolean favoriteUser = false;
        private String favoriteUserXuid;

        public AddUserToFavoriteListAsyncTask(String str) {
            this.favoriteUserXuid = str;
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
            AsyncActionStatus status = meProfileModel.addUserToFavoriteList(this.forceLoad, this.favoriteUserXuid).getStatus();
            if (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) {
                ArrayList favorites = meProfileModel.getFavorites();
                if (favorites != null) {
                    Iterator it = favorites.iterator();
                    while (it.hasNext()) {
                        FollowersData followersData = (FollowersData) it.next();
                        if (followersData.xuid.equals(this.favoriteUserXuid)) {
                            this.favoriteUser = followersData.isFavorite;
                            break;
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
            ChangeFriendshipDialogViewModel.this.onAddUserToFavoriteListCompleted(AsyncActionStatus.NO_CHANGE, this.favoriteUser);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ChangeFriendshipDialogViewModel.this.onAddUserToFavoriteListCompleted(asyncActionStatus, this.favoriteUser);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.isAddingUserToFavoriteList = true;
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
                ChangeFriendshipDialogViewModel.this.model.loadProfileSummary(true);
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
            ChangeFriendshipDialogViewModel.this.onAddUserToFollowingListCompleted(AsyncActionStatus.NO_CHANGE, this.isFollowingUser);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ChangeFriendshipDialogViewModel.this.onAddUserToFollowingListCompleted(asyncActionStatus, this.isFollowingUser);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.isAddingUserToFollowingList = true;
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
            ChangeFriendshipDialogViewModel.this.onAddUseToShareIdentityListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.isAddingUserToShareIdentityList = true;
        }
    }

    private class LoadPersonDataAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private LoadPersonDataAsyncTask() {
        }

        protected boolean checkShouldExecute() {
            XLEAssert.assertIsUIThread();
            return false;
        }

        protected AsyncActionStatus loadDataInBackground() {
            XLEAssert.assertNotNull(ChangeFriendshipDialogViewModel.this.model);
            return ChangeFriendshipDialogViewModel.this.model.loadProfileSummary(this.forceLoad).getStatus();
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.onLoadPersonDataCompleted(AsyncActionStatus.NO_CHANGE);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ChangeFriendshipDialogViewModel.this.onLoadPersonDataCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.isLoadingUserProfile = true;
        }
    }

    private class RemoveUserFromFavoriteListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private boolean favoriteUser = false;
        private String favoriteUserXuid;

        public RemoveUserFromFavoriteListAsyncTask(String str) {
            this.favoriteUserXuid = str;
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
            AsyncActionStatus status = meProfileModel.removeUserFromFavoriteList(this.forceLoad, this.favoriteUserXuid).getStatus();
            if (status == AsyncActionStatus.SUCCESS || status == AsyncActionStatus.NO_CHANGE || status == AsyncActionStatus.NO_OP_SUCCESS) {
                ArrayList favorites = meProfileModel.getFavorites();
                if (favorites != null) {
                    Iterator it = favorites.iterator();
                    while (it.hasNext()) {
                        FollowersData followersData = (FollowersData) it.next();
                        if (followersData.xuid.equals(this.favoriteUserXuid)) {
                            this.favoriteUser = followersData.isFavorite;
                            break;
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
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromFavoriteListCompleted(AsyncActionStatus.NO_CHANGE, this.favoriteUser);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromFavoriteListCompleted(asyncActionStatus, this.favoriteUser);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.isRemovingUserFromFavoriteList = true;
        }
    }

    private class RemoveUserFromFollowingListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private String followingUserXuid;
        private boolean isFollowingUser = true;

        public RemoveUserFromFollowingListAsyncTask(String str) {
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
            AsyncActionStatus status = meProfileModel.removeUserFromFollowingList(this.forceLoad, this.followingUserXuid).getStatus();
            if (AsyncActionStatus.getIsFail(status)) {
                return status;
            }
            ChangeFriendshipDialogViewModel.this.model.loadProfileSummary(true);
            meProfileModel.loadProfileSummary(true);
            this.isFollowingUser = false;
            return status;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromFollowingListCompleted(AsyncActionStatus.NO_CHANGE, this.isFollowingUser);
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromFollowingListCompleted(asyncActionStatus, this.isFollowingUser);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.isRemovingUserFromFollowingList = true;
        }
    }

    private class RemoveUserFromShareIdentityListAsyncTask extends NetworkAsyncTask<AsyncActionStatus> {
        private ArrayList<String> usersToAdd;

        public RemoveUserFromShareIdentityListAsyncTask(ArrayList<String> arrayList) {
            this.usersToAdd = arrayList;
        }

        protected boolean checkShouldExecute() {
            return true;
        }

        protected AsyncActionStatus loadDataInBackground() {
            ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
            return meProfileModel != null ? meProfileModel.removeUserFromShareIdentity(this.forceLoad, this.usersToAdd).getStatus() : AsyncActionStatus.FAIL;
        }

        protected AsyncActionStatus onError() {
            return AsyncActionStatus.FAIL;
        }

        protected void onNoAction() {
        }

        protected void onPostExecute(AsyncActionStatus asyncActionStatus) {
            ChangeFriendshipDialogViewModel.this.onRemoveUserFromShareIdentityListCompleted(asyncActionStatus);
        }

        protected void onPreExecute() {
            XLEAssert.assertIsUIThread();
            ChangeFriendshipDialogViewModel.this.isRemovingUserFromShareIdentityList = true;
        }
    }

    public ChangeFriendshipDialogViewModel(ProfileModel profileModel) {
        boolean z = false;
        if (!ProfileModel.isMeXuid(profileModel.getXuid())) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        this.model = profileModel;
    }

    private void notifyDialogAsyncTaskCompleted() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskCompleted();
    }

    private void notifyDialogAsyncTaskFailed(String str) {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogAsyncTaskFailed(str);
    }

    private void notifyDialogUpdateView() {
        ((SGProjectSpecificDialogManager) DialogManager.getInstance().getManager()).notifyChangeFriendshipDialogUpdateView();
    }

    private void onAddUseToShareIdentityListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isAddingUserToShareIdentityList = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                notifyDialogAsyncTaskCompleted();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    private void onAddUserToFavoriteListCompleted(AsyncActionStatus asyncActionStatus, boolean z) {
        this.isAddingUserToFavoriteList = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.isFavorite = z;
                notifyDialogAsyncTaskCompleted();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    private void onAddUserToFollowingListCompleted(AsyncActionStatus asyncActionStatus, boolean z) {
        this.isAddingUserToFollowingList = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.isFollowing = z;
                notifyDialogAsyncTaskCompleted();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                AddFollowingUserResponse addFollowingUserResponse = null;
                ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
                if (meProfileModel != null) {
                    addFollowingUserResponse = meProfileModel.getAddUserToFollowingResult();
                }
                if (addFollowingUserResponse == null || addFollowingUserResponse.getAddFollowingRequestStatus() || addFollowingUserResponse.code != PeopleResponseError.MAX_FOLLOWING_LIMIT_REACHED) {
                    notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorAddingFriend));
                    return;
                } else {
                    notifyDialogAsyncTaskFailed(addFollowingUserResponse.description);
                    return;
                }
            default:
                return;
        }
    }

    private void onLoadPersonDataCompleted(AsyncActionStatus asyncActionStatus) {
        this.isLoadingUserProfile = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                if (this.model.getProfileSummaryData() == null) {
                    this.viewModelState = ListState.ErrorState;
                    break;
                } else {
                    this.viewModelState = ListState.ValidContentState;
                    break;
                }
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                this.viewModelState = ListState.ErrorState;
                break;
        }
        notifyDialogUpdateView();
    }

    private void onRemoveUserFromFavoriteListCompleted(AsyncActionStatus asyncActionStatus, boolean z) {
        this.isRemovingUserFromFavoriteList = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.isFavorite = z;
                notifyDialogAsyncTaskCompleted();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    private void onRemoveUserFromFollowingListCompleted(AsyncActionStatus asyncActionStatus, boolean z) {
        this.isRemovingUserFromFollowingList = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                this.isFollowing = z;
                if (this.isFavorite && !this.isFollowing) {
                    this.isFavorite = false;
                }
                notifyDialogAsyncTaskCompleted();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    private void onRemoveUserFromShareIdentityListCompleted(AsyncActionStatus asyncActionStatus) {
        this.isRemovingUserFromShareIdentityList = false;
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$AsyncActionStatus[asyncActionStatus.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                notifyDialogAsyncTaskCompleted();
                return;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
                notifyDialogAsyncTaskFailed(XboxTcuiSdk.getResources().getString(R.string.RealNameSharing_ErrorChangeRemove));
                return;
            default:
                return;
        }
    }

    private void showError(int i) {
        DialogManager.getInstance().showToast(i);
    }

    public void addFavoriteUser() {
        if (this.addUserToFavoriteListAsyncTask != null) {
            this.addUserToFavoriteListAsyncTask.cancel();
        }
        this.addUserToFavoriteListAsyncTask = new AddUserToFavoriteListAsyncTask(this.model.getXuid());
        this.addUserToFavoriteListAsyncTask.load(true);
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

    public void clearChangeFriendshipForm() {
        this.changeFriendshipForm.clear();
    }

    public String getCallerGamerTag() {
        ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
        return meProfileModel != null ? meProfileModel.getGamerTag() : BuildConfig.FLAVOR;
    }

    public boolean getCallerMarkedTargetAsIdentityShared() {
        return this.model.hasCallerMarkedTargetAsIdentityShared();
    }

    public String getCallerShareRealNameStatus() {
        ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
        return meProfileModel != null ? meProfileModel.getShareRealNameStatus() : BuildConfig.FLAVOR;
    }

    public String getDialogButtonText() {
        return this.isFollowing ? XboxTcuiSdk.getResources().getString(R.string.TextInput_Confirm) : XboxTcuiSdk.getResources().getString(R.string.OK_Text);
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

    public boolean getIsFavorite() {
        return this.model.hasCallerMarkedTargetAsFavorite();
    }

    public boolean getIsFollowing() {
        return this.model.isCallerFollowingTarget();
    }

    public boolean getIsSharingRealNameEnd() {
        return this.isSharingRealNameEnd;
    }

    public boolean getIsSharingRealNameStart() {
        return this.isSharingRealNameStart;
    }

    public int getPreferredColor() {
        return this.model.getPreferedColor();
    }

    public String getRealName() {
        return this.model.getRealName();
    }

    public ListState getViewModelState() {
        return this.viewModelState;
    }

    public String getXuid() {
        return this.model.getXuid();
    }

    public boolean isBusy() {
        return this.isLoadingUserProfile || this.isAddingUserToFavoriteList || this.isRemovingUserFromFavoriteList || this.isAddingUserToFollowingList || this.isRemovingUserFromFollowingList || this.isAddingUserToShareIdentityList || this.isRemovingUserFromShareIdentityList;
    }

    public void load() {
        if (this.loadProfileAsyncTask != null) {
            this.loadProfileAsyncTask.cancel();
        }
        this.loadProfileAsyncTask = new LoadPersonDataAsyncTask();
        this.loadProfileAsyncTask.load(true);
    }

    public void onChangeRelationshipCompleted() {
        Object obj = 1;
        Object obj2 = null;
        Relationship relationship = this.model.isCallerFollowingTarget() ? Relationship.EXISTINGFRIEND : Relationship.NOTCHANGED;
        FavoriteStatus favoriteStatus = this.model.hasCallerMarkedTargetAsFavorite() ? FavoriteStatus.EXISTINGFAVORITE : FavoriteStatus.EXISTINGNOTFAVORITED;
        RealNameStatus realNameStatus = this.model.hasCallerMarkedTargetAsIdentityShared() ? RealNameStatus.EXISTINGSHARED : RealNameStatus.EXISTINGNOTSHARED;
        GamerType gamerType = GamerType.NORMAL;
        if (this.changeFriendshipForm.contains(ChangeFriendshipFormOptions.ShouldAddUserToFriendList)) {
            relationship = Relationship.ADDFRIEND;
            addFollowingUser();
            obj2 = 1;
        }
        if (this.changeFriendshipForm.contains(ChangeFriendshipFormOptions.ShouldRemoveUserFromFriendList)) {
            relationship = Relationship.REMOVEFRIEND;
            removeFollowingUser();
            obj2 = 1;
        }
        if (this.changeFriendshipForm.contains(ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList)) {
            favoriteStatus = FavoriteStatus.FAVORITED;
            addFavoriteUser();
            obj2 = 1;
        }
        if (this.changeFriendshipForm.contains(ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList)) {
            favoriteStatus = FavoriteStatus.UNFAVORITED;
            removeFavoriteUser();
            obj2 = 1;
        }
        if (this.changeFriendshipForm.contains(ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList)) {
            realNameStatus = RealNameStatus.SHARINGON;
            addUserToShareIdentityList();
            obj2 = 1;
        }
        if (this.changeFriendshipForm.contains(ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList)) {
            realNameStatus = RealNameStatus.SHARINGOFF;
            removeUserFromShareIdentityList();
        } else {
            obj = obj2;
        }
        if (obj == null) {
            notifyDialogAsyncTaskCompleted();
        } else {
            UTCChangeRelationship.trackChangeRelationshipDone(relationship, realNameStatus, favoriteStatus, gamerType);
        }
    }

    public void removeFavoriteUser() {
        if (this.removeUserFromFavoriteListAsyncTask != null) {
            this.removeUserFromFavoriteListAsyncTask.cancel();
        }
        this.removeUserFromFavoriteListAsyncTask = new RemoveUserFromFavoriteListAsyncTask(this.model.getXuid());
        this.removeUserFromFavoriteListAsyncTask.load(true);
    }

    public void removeFollowingUser() {
        if (this.removeUserFromFollowingListAsyncTask != null) {
            this.removeUserFromFollowingListAsyncTask.cancel();
        }
        this.removeUserFromFollowingListAsyncTask = new RemoveUserFromFollowingListAsyncTask(this.model.getXuid());
        this.removeUserFromFollowingListAsyncTask.load(true);
    }

    public void removeUserFromShareIdentityList() {
        if (this.removeUserFromFollowingListAsyncTask != null) {
            this.removeUserFromFavoriteListAsyncTask.cancel();
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(this.model.getXuid());
        this.removeUserFromShareIdentityListAsyncTask = new RemoveUserFromShareIdentityListAsyncTask(arrayList);
        this.removeUserFromShareIdentityListAsyncTask.load(true);
    }

    public void setInitialRealNameSharingState(boolean z) {
        this.isSharingRealNameStart = z;
        this.isSharingRealNameEnd = z;
    }

    public void setIsSharingRealNameEnd(boolean z) {
        this.isSharingRealNameEnd = z;
    }

    public void setShouldAddUserToFavoriteList(boolean z) {
        if (z) {
            this.changeFriendshipForm.add(ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList);
        } else {
            this.changeFriendshipForm.remove(ChangeFriendshipFormOptions.ShouldAddUserToFavoriteList);
        }
    }

    public void setShouldAddUserToFriendList(boolean z) {
        if (z) {
            this.changeFriendshipForm.add(ChangeFriendshipFormOptions.ShouldAddUserToFriendList);
        } else {
            this.changeFriendshipForm.remove(ChangeFriendshipFormOptions.ShouldAddUserToFriendList);
        }
    }

    public void setShouldAddUserToShareIdentityList(boolean z) {
        if (z) {
            this.changeFriendshipForm.add(ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList);
        } else {
            this.changeFriendshipForm.remove(ChangeFriendshipFormOptions.ShouldAddUserToShareIdentityList);
        }
    }

    public void setShouldRemoveUserFroShareIdentityList(boolean z) {
        if (z) {
            this.changeFriendshipForm.add(ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList);
        } else {
            this.changeFriendshipForm.remove(ChangeFriendshipFormOptions.ShouldRemoveUserFromShareIdentityList);
        }
    }

    public void setShouldRemoveUserFromFavoriteList(boolean z) {
        if (z) {
            this.changeFriendshipForm.add(ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList);
        } else {
            this.changeFriendshipForm.remove(ChangeFriendshipFormOptions.ShouldRemoveUserFromFavoriteList);
        }
    }
}
