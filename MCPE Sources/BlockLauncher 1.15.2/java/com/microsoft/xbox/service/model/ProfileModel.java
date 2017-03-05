package com.microsoft.xbox.service.model;

import android.util.Log;
import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import com.microsoft.xbox.service.model.FollowersData.DummyType;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.FriendsFinderStateResult;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.LinkedAccountOptInStatus;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.model.sls.AddShareIdentityRequest;
import com.microsoft.xbox.service.model.sls.FavoriteListRequest;
import com.microsoft.xbox.service.model.sls.FeedbackType;
import com.microsoft.xbox.service.model.sls.MutedListRequest;
import com.microsoft.xbox.service.model.sls.NeverListRequest;
import com.microsoft.xbox.service.model.sls.SubmitFeedbackRequest;
import com.microsoft.xbox.service.model.sls.UserProfileRequest;
import com.microsoft.xbox.service.model.sls.UserProfileSetting;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer.AddFollowingUserResponse;
import com.microsoft.xbox.service.network.managers.FamilySettings;
import com.microsoft.xbox.service.network.managers.FamilyUser;
import com.microsoft.xbox.service.network.managers.FollowingSummaryResult.People;
import com.microsoft.xbox.service.network.managers.IFollowerPresenceResult.UserPresence;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.PeopleHubPeopleSummary;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.PeopleHubPersonSummary;
import com.microsoft.xbox.service.network.managers.IUserProfileResult.ProfileUser;
import com.microsoft.xbox.service.network.managers.IUserProfileResult.Settings;
import com.microsoft.xbox.service.network.managers.IUserProfileResult.UserProfileResult;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer.MutedListResult;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer.NeverListResult;
import com.microsoft.xbox.service.network.managers.PeopleResponseError;
import com.microsoft.xbox.service.network.managers.ProfileSummaryResultContainer.ProfileSummaryResult;
import com.microsoft.xbox.service.network.managers.ServiceManagerFactory;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.service.network.managers.xblshared.ISLSServiceManager;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xbox.xle.viewmodel.ShareRealNameSettingFilter;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;

public class ProfileModel extends ModelBase<ProfileData> {
    private static final int MAX_PROFILE_MODELS = 20;
    private static final long friendsDataLifetime = 180000;
    private static ProfileModel meProfileInstance = null;
    private static ThreadSafeFixedSizeHashtable<String, ProfileModel> profileModelCache = new ThreadSafeFixedSizeHashtable(MAX_PROFILE_MODELS);
    private static final long profilePresenceDataLifetime = 180000;
    private AddFollowingUserResponse addUserToFollowingResponse;
    private SingleEntryLoadingStatus addingUserToFavoriteListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToFollowingListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToMutedListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToNeverListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus addingUserToShareIdentityListLoadingStatus = new SingleEntryLoadingStatus();
    private ArrayList<FollowersData> favorites;
    private String firstName;
    private ArrayList<FollowersData> following;
    private ArrayList<People> followingSummaries;
    private String lastName;
    private Date lastRefreshMutedList;
    private Date lastRefreshNeverList;
    private Date lastRefreshPeopleHubRecommendations;
    private Date lastRefreshPresenceData;
    private Date lastRefreshProfileSummary;
    private MutedListResult mutedList;
    private SingleEntryLoadingStatus mutedListLoadingStatus = new SingleEntryLoadingStatus();
    private NeverListResult neverList;
    private SingleEntryLoadingStatus neverListLoadingStatus = new SingleEntryLoadingStatus();
    private PeopleHubPersonSummary peopleHubPersonSummary;
    private ArrayList<FollowersData> peopleHubRecommendations;
    private PeopleHubPeopleSummary peopleHubRecommendationsRaw;
    private UserPresence presenceData;
    private SingleEntryLoadingStatus presenceDataLoadingStatus;
    private String profileImageUrl;
    private ProfileSummaryResult profileSummary;
    private SingleEntryLoadingStatus profileSummaryLoadingStatus;
    private ProfileUser profileUser;
    private SingleEntryLoadingStatus removingUserFromFavoriteListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromFollowingListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromMutedListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromNeverListLoadingStatus = new SingleEntryLoadingStatus();
    private SingleEntryLoadingStatus removingUserFromShareIdentityListLoadingStatus = new SingleEntryLoadingStatus();
    private boolean shareRealName;
    private String shareRealNameStatus;
    private boolean sharingRealNameTransitively;
    private SingleEntryLoadingStatus submitFeedbackForUserLoadingStatus = new SingleEntryLoadingStatus();
    private String xuid;

    private class AddUserToFavoriteListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String favoriteUserXuid;

        public AddUserToFavoriteListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.favoriteUserXuid = str;
        }

        public Boolean buildData() throws XLEException {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.favoriteUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToFavoriteList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(arrayList))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_USER_TO_FAVORITELIST;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onAddUserToFavoriteListCompleted(asyncResult, this.favoriteUserXuid);
        }

        public void onPreExecute() {
        }
    }

    private class AddUserToFollowingListRunner extends IDataLoaderRunnable<AddFollowingUserResponse> {
        private ProfileModel caller;
        private String followingUserXuid;

        public AddUserToFollowingListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.followingUserXuid = str;
        }

        public AddFollowingUserResponse buildData() throws XLEException {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.followingUserXuid);
            return ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToFollowingList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(arrayList)));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_FRIEND;
        }

        public void onPostExcute(AsyncResult<AddFollowingUserResponse> asyncResult) {
            this.caller.onAddUserToFollowingListCompleted(asyncResult, this.followingUserXuid);
        }

        public void onPreExecute() {
        }
    }

    private class AddUsersToShareIdentityListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private ArrayList<String> userIds;

        public AddUsersToShareIdentityListRunner(ProfileModel profileModel, ArrayList<String> arrayList) {
            this.caller = profileModel;
            this.userIds = arrayList;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addFriendToShareIdentitySetting(this.caller.xuid, AddShareIdentityRequest.getAddShareIdentityRequestBody(new AddShareIdentityRequest(this.userIds))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_ADD_TO_SHARE_IDENTIY;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onAddUserToShareIdentityCompleted(asyncResult, this.userIds);
        }

        public void onPreExecute() {
        }
    }

    private class FollowingAndFavoritesComparator implements Comparator<FollowersData> {
        private FollowingAndFavoritesComparator() {
        }

        public int compare(FollowersData followersData, FollowersData followersData2) {
            return followersData.userProfileData.appDisplayName.compareToIgnoreCase(followersData2.userProfileData.appDisplayName);
        }
    }

    private class GetMutedListRunner extends IDataLoaderRunnable<MutedListResult> {
        private ProfileModel caller;
        private String xuid;

        public GetMutedListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        public MutedListResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getMutedListInfo(this.xuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_MUTED_LIST;
        }

        public void onPostExcute(AsyncResult<MutedListResult> asyncResult) {
            this.caller.onGetMutedListCompleted(asyncResult);
        }

        public void onPreExecute() {
        }
    }

    private class GetNeverListRunner extends IDataLoaderRunnable<NeverListResult> {
        private ProfileModel caller;
        private String xuid;

        public GetNeverListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        public NeverListResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getNeverListInfo(this.xuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_NEVERLIST_DATA;
        }

        public void onPostExcute(AsyncResult<NeverListResult> asyncResult) {
            this.caller.onGetNeverListCompleted(asyncResult);
        }

        public void onPreExecute() {
        }
    }

    private class GetPeopleHubRecommendationRunner extends IDataLoaderRunnable<PeopleHubPeopleSummary> {
        private ProfileModel caller;
        private String xuid;

        public GetPeopleHubRecommendationRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        public PeopleHubPeopleSummary buildData() throws XLEException {
            PeopleHubPeopleSummary peopleHubPeopleSummary = new PeopleHubPeopleSummary();
            return (JavaUtil.isNullOrEmpty(this.xuid) || !this.xuid.equalsIgnoreCase(ProjectSpecificDataProvider.getInstance().getXuidString())) ? peopleHubPeopleSummary : ServiceManagerFactory.getInstance().getSLSServiceManager().getPeopleHubRecommendations();
        }

        public long getDefaultErrorCode() {
            return 11;
        }

        public void onPostExcute(AsyncResult<PeopleHubPeopleSummary> asyncResult) {
            this.caller.onGetPeopleHubRecommendationsCompleted(asyncResult);
        }

        public void onPreExecute() {
        }
    }

    private class GetPresenceDataRunner extends IDataLoaderRunnable<UserPresence> {
        private ProfileModel caller;
        private String xuid;

        public GetPresenceDataRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        public UserPresence buildData() throws XLEException {
            return null;
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_PROFILE_PRESENCE_DATA;
        }

        public void onPostExcute(AsyncResult<UserPresence> asyncResult) {
            this.caller.onGetPresenceDataCompleted(asyncResult);
        }

        public void onPreExecute() {
        }
    }

    private class GetProfileRunner extends IDataLoaderRunnable<ProfileData> {
        private ProfileModel caller;
        private boolean loadEssentialsOnly;
        private String xuid;

        public GetProfileRunner(ProfileModel profileModel, String str, boolean z) {
            this.caller = profileModel;
            this.xuid = str;
            this.loadEssentialsOnly = z;
        }

        public ProfileData buildData() throws XLEException {
            boolean z;
            boolean z2;
            String str;
            final ISLSServiceManager sLSServiceManager = ServiceManagerFactory.getInstance().getSLSServiceManager();
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.xuid);
            UserProfileResult userProfileInfo = sLSServiceManager.getUserProfileInfo(UserProfileRequest.getUserProfileRequestBody(new UserProfileRequest(arrayList, this.loadEssentialsOnly)));
            final ProfileUser profileUser;
            String settingValue;
            if (ProjectSpecificDataProvider.getInstance().getXuidString().equalsIgnoreCase(this.xuid)) {
                if (!(userProfileInfo == null || userProfileInfo.profileUsers == null || userProfileInfo.profileUsers.size() <= 0)) {
                    profileUser = (ProfileUser) userProfileInfo.profileUsers.get(0);
                    profileUser.setPrivilieges(sLSServiceManager.getXTokenPrivileges());
                    try {
                        settingValue = profileUser.getSettingValue(UserProfileSetting.PreferredColor);
                        if (settingValue != null && settingValue.length() > 0) {
                            profileUser.colors = sLSServiceManager.getProfilePreferredColor(settingValue);
                        }
                    } catch (Throwable th) {
                    }
                    XLEThreadPool.networkOperationsThreadPool.run(new Runnable() {
                        public void run() {
                            try {
                                FamilySettings familySettings = sLSServiceManager.getFamilySettings(GetProfileRunner.this.xuid);
                                if (familySettings != null && familySettings.familyUsers != null) {
                                    for (int i = 0; i < familySettings.familyUsers.size(); i++) {
                                        if (((FamilyUser) familySettings.familyUsers.get(i)).xuid.equalsIgnoreCase(GetProfileRunner.this.xuid)) {
                                            profileUser.canViewTVAdultContent = ((FamilyUser) familySettings.familyUsers.get(i)).canViewTVAdultContent;
                                            profileUser.setmaturityLevel(((FamilyUser) familySettings.familyUsers.get(i)).maturityLevel);
                                            return;
                                        }
                                    }
                                }
                            } catch (Throwable th) {
                            }
                        }
                    });
                }
            } else if (!(userProfileInfo == null || userProfileInfo.profileUsers == null || userProfileInfo.profileUsers.size() <= 0)) {
                profileUser = (ProfileUser) userProfileInfo.profileUsers.get(0);
                try {
                    settingValue = profileUser.getSettingValue(UserProfileSetting.PreferredColor);
                    if (settingValue != null && settingValue.length() > 0) {
                        profileUser.colors = sLSServiceManager.getProfilePreferredColor(settingValue);
                    }
                } catch (Throwable th2) {
                }
            }
            String str2 = null;
            if (this.xuid == null || this.xuid.compareToIgnoreCase(ProjectSpecificDataProvider.getInstance().getXuidString()) != 0) {
                z = false;
                z2 = false;
                str = null;
            } else {
                try {
                    PrivacySettingsResult userProfilePrivacySettings = sLSServiceManager.getUserProfilePrivacySettings();
                    String shareRealNameStatus = userProfilePrivacySettings.getShareRealNameStatus();
                    try {
                        boolean z3 = ShareRealNameSettingFilter.Blocked.toString().compareTo(shareRealNameStatus) != 0;
                        try {
                            z2 = userProfilePrivacySettings.getSharingRealNameTransitively();
                            str = shareRealNameStatus;
                            z = z3;
                        } catch (Exception e) {
                            str2 = shareRealNameStatus;
                            z = z3;
                            str = str2;
                            z2 = false;
                            return new ProfileData(userProfileInfo, z, str, z2);
                        }
                    } catch (Exception e2) {
                        str2 = shareRealNameStatus;
                        z = false;
                        str = str2;
                        z2 = false;
                        return new ProfileData(userProfileInfo, z, str, z2);
                    }
                } catch (Exception e3) {
                    z = false;
                    str = str2;
                    z2 = false;
                    return new ProfileData(userProfileInfo, z, str, z2);
                }
            }
            return new ProfileData(userProfileInfo, z, str, z2);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_USER_PROFILE_INFO;
        }

        public void onPostExcute(AsyncResult<ProfileData> asyncResult) {
            this.caller.updateWithProfileData(asyncResult, this.loadEssentialsOnly);
        }

        public void onPreExecute() {
        }
    }

    private class GetProfileSummaryRunner extends IDataLoaderRunnable<ProfileSummaryResult> {
        private ProfileModel caller;
        private String xuid;

        public GetProfileSummaryRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.xuid = str;
        }

        public ProfileSummaryResult buildData() throws XLEException {
            return ServiceManagerFactory.getInstance().getSLSServiceManager().getProfileSummaryInfo(this.xuid);
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_USER_PROFILE_INFO;
        }

        public void onPostExcute(AsyncResult<ProfileSummaryResult> asyncResult) {
            this.caller.onGetProfileSummaryCompleted(asyncResult);
        }

        public void onPreExecute() {
        }
    }

    private class PutUserToMutedListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String mutedUserXuid;
        private String xuid;

        public PutUserToMutedListRunner(ProfileModel profileModel, String str, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.mutedUserXuid = str2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToMutedList(this.xuid, MutedListRequest.getNeverListRequestBody(new MutedListRequest(Long.parseLong(this.mutedUserXuid)))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_MUTE_USER;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onPutUserToMutedListCompleted(asyncResult, this.mutedUserXuid);
        }

        public void onPreExecute() {
        }
    }

    private class PutUserToNeverListRunner extends IDataLoaderRunnable<Boolean> {
        private String blockUserXuid;
        private ProfileModel caller;
        private String xuid;

        public PutUserToNeverListRunner(ProfileModel profileModel, String str, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.blockUserXuid = str2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().addUserToNeverList(this.xuid, NeverListRequest.getNeverListRequestBody(new NeverListRequest(Long.parseLong(this.blockUserXuid)))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_BLOCK_USER;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onPutUserToNeverListCompleted(asyncResult, this.blockUserXuid);
        }

        public void onPreExecute() {
        }
    }

    private class RemoveUserFromFavoriteListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String favoriteUserXuid;

        public RemoveUserFromFavoriteListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.favoriteUserXuid = str;
        }

        public Boolean buildData() throws XLEException {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.favoriteUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromFavoriteList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(arrayList))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_USER_FROM_FAVORITELIST;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromFavoriteListCompleted(asyncResult, this.favoriteUserXuid);
        }

        public void onPreExecute() {
        }
    }

    private class RemoveUserFromFollowingListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String followingUserXuid;

        public RemoveUserFromFollowingListRunner(ProfileModel profileModel, String str) {
            this.caller = profileModel;
            this.followingUserXuid = str;
        }

        public Boolean buildData() throws XLEException {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.followingUserXuid);
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromFollowingList(FavoriteListRequest.getFavoriteListRequestBody(new FavoriteListRequest(arrayList))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_FRIEND;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromFollowingListCompleted(asyncResult, this.followingUserXuid);
        }

        public void onPreExecute() {
        }
    }

    private class RemoveUserFromMutedListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String unmutedUserXuid;
        private String xuid;

        public RemoveUserFromMutedListRunner(ProfileModel profileModel, String str, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.unmutedUserXuid = str2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromMutedList(this.xuid, MutedListRequest.getNeverListRequestBody(new MutedListRequest(Long.parseLong(this.unmutedUserXuid)))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_UNMUTE_USER;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromMutedListCompleted(asyncResult, this.unmutedUserXuid);
        }

        public void onPreExecute() {
        }
    }

    private class RemoveUserFromNeverListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private String unblockUserXuid;
        private String xuid;

        public RemoveUserFromNeverListRunner(ProfileModel profileModel, String str, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.unblockUserXuid = str2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeUserFromNeverList(this.xuid, NeverListRequest.getNeverListRequestBody(new NeverListRequest(Long.parseLong(this.unblockUserXuid)))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_USER_FROM_NEVERLIST;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromNeverListCompleted(asyncResult, this.unblockUserXuid);
        }

        public void onPreExecute() {
        }
    }

    private class RemoveUsersFromShareIdentityListRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private ArrayList<String> userIds;

        public RemoveUsersFromShareIdentityListRunner(ProfileModel profileModel, ArrayList<String> arrayList) {
            this.caller = profileModel;
            this.userIds = arrayList;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().removeFriendFromShareIdentitySetting(this.caller.xuid, AddShareIdentityRequest.getAddShareIdentityRequestBody(new AddShareIdentityRequest(this.userIds))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_REMOVE_FROM_SHARE_IDENTIY;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onRemoveUserFromShareIdentityCompleted(asyncResult, this.userIds);
        }

        public void onPreExecute() {
        }
    }

    private class SubmitFeedbackForUserRunner extends IDataLoaderRunnable<Boolean> {
        private ProfileModel caller;
        private FeedbackType feedbackType;
        private String textReason;
        private String xuid;

        public SubmitFeedbackForUserRunner(ProfileModel profileModel, String str, FeedbackType feedbackType, String str2) {
            this.caller = profileModel;
            this.xuid = str;
            this.feedbackType = feedbackType;
            this.textReason = str2;
        }

        public Boolean buildData() throws XLEException {
            return Boolean.valueOf(ServiceManagerFactory.getInstance().getSLSServiceManager().submitFeedback(this.xuid, SubmitFeedbackRequest.getSubmitFeedbackRequestBody(new SubmitFeedbackRequest(Long.parseLong(this.xuid), null, this.feedbackType, this.textReason, null, null))));
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_SUBMIT_FEEDBACK;
        }

        public void onPostExcute(AsyncResult<Boolean> asyncResult) {
            this.caller.onSubmitFeedbackForUserCompleted(asyncResult);
        }

        public void onPreExecute() {
        }
    }

    private ProfileModel(String str) {
        this.xuid = str;
    }

    private void buildRecommendationsList(boolean z) {
        this.peopleHubRecommendations = new ArrayList();
        if (z) {
            this.peopleHubRecommendations.add(0, new RecommendationsPeopleData(true, DummyType.DUMMY_LINK_TO_FACEBOOK));
        }
        if (this.peopleHubRecommendationsRaw != null && !XLEUtil.isNullOrEmpty(this.peopleHubRecommendationsRaw.people)) {
            Iterator it = this.peopleHubRecommendationsRaw.people.iterator();
            while (it.hasNext()) {
                this.peopleHubRecommendations.add(new RecommendationsPeopleData((PeopleHubPersonSummary) it.next()));
            }
        }
    }

    public static int getDefaultColor() {
        return XboxTcuiSdk.getResources().getColor(XLERValueHelper.getColorRValue("XboxOneGreen"));
    }

    public static ProfileModel getMeProfileModel() {
        if (ProjectSpecificDataProvider.getInstance().getXuidString() == null) {
            return null;
        }
        if (meProfileInstance == null) {
            meProfileInstance = new ProfileModel(ProjectSpecificDataProvider.getInstance().getXuidString());
        }
        return meProfileInstance;
    }

    private String getProfileImageUrl() {
        if (this.profileImageUrl != null) {
            return this.profileImageUrl;
        }
        this.profileImageUrl = getProfileSettingValue(UserProfileSetting.GameDisplayPicRaw);
        return this.profileImageUrl;
    }

    public static ProfileModel getProfileModel(String str) {
        if (JavaUtil.isNullOrEmpty(str)) {
            throw new IllegalArgumentException();
        } else if (JavaUtil.stringsEqualCaseInsensitive(str, ProjectSpecificDataProvider.getInstance().getXuidString())) {
            if (meProfileInstance == null) {
                meProfileInstance = new ProfileModel(str);
            }
            return meProfileInstance;
        } else {
            ProfileModel profileModel = (ProfileModel) profileModelCache.get(str);
            if (profileModel != null) {
                return profileModel;
            }
            profileModel = new ProfileModel(str);
            profileModelCache.put(str, profileModel);
            return profileModel;
        }
    }

    private String getProfileSettingValue(UserProfileSetting userProfileSetting) {
        if (!(this.profileUser == null || this.profileUser.settings == null)) {
            Iterator it = this.profileUser.settings.iterator();
            while (it.hasNext()) {
                Settings settings = (Settings) it.next();
                if (settings.id != null && settings.id.equals(userProfileSetting.toString())) {
                    return settings.value;
                }
            }
        }
        return null;
    }

    private static boolean hasPrivilege(String str) {
        String privileges = ProjectSpecificDataProvider.getInstance().getPrivileges();
        return !JavaUtil.isNullOrEmpty(privileges) && privileges.contains(str);
    }

    public static boolean hasPrivilegeToAddFriend() {
        return hasPrivilege(XPrivilegeConstants.XPRIVILEGE_ADD_FRIEND);
    }

    public static boolean hasPrivilegeToSendMessage() {
        return hasPrivilege(XPrivilegeConstants.XPRIVILEGE_COMMUNICATIONS);
    }

    public static boolean isMeXuid(String str) {
        String xuidString = ProjectSpecificDataProvider.getInstance().getXuidString();
        return (xuidString == null || str == null || str.compareToIgnoreCase(xuidString) != 0) ? false : true;
    }

    private void onAddUserToFavoriteListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue() && this.following != null) {
            Object arrayList = new ArrayList();
            Iterator it = this.following.iterator();
            while (it.hasNext()) {
                FollowersData followersData = (FollowersData) it.next();
                if (followersData.xuid.equals(str)) {
                    followersData.isFavorite = true;
                }
                if (followersData.isFavorite) {
                    arrayList.add(followersData);
                }
            }
            Collections.sort(arrayList, new FollowingAndFavoritesComparator());
            this.favorites = arrayList;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, null));
        }
    }

    private void onAddUserToFollowingListCompleted(AsyncResult<AddFollowingUserResponse> asyncResult, String str) {
        ProfileModel profileModel = getProfileModel(str);
        XLEAssert.assertNotNull(profileModel);
        this.addUserToFollowingResponse = (AddFollowingUserResponse) asyncResult.getResult();
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && this.addUserToFollowingResponse != null && this.addUserToFollowingResponse.getAddFollowingRequestStatus()) {
            boolean z;
            FollowersData followersData;
            Object arrayList = new ArrayList();
            if (this.following != null) {
                Iterator it = this.following.iterator();
                z = false;
                while (it.hasNext()) {
                    followersData = (FollowersData) it.next();
                    arrayList.add(followersData);
                    if (followersData.xuid.equals(str)) {
                        z = true;
                    }
                }
            } else {
                z = false;
            }
            if (!z) {
                followersData = new FollowersData();
                followersData.xuid = str;
                followersData.isFavorite = false;
                followersData.status = UserStatus.Offline;
                followersData.userProfileData = new UserProfileData();
                followersData.userProfileData.accountTier = profileModel.getAccountTier();
                followersData.userProfileData.appDisplayName = profileModel.getAppDisplayName();
                followersData.userProfileData.gamerScore = profileModel.getGamerScore();
                followersData.userProfileData.gamerTag = profileModel.getGamerTag();
                followersData.userProfileData.profileImageUrl = profileModel.getProfileImageUrl();
                arrayList.add(followersData);
                Collections.sort(arrayList, new FollowingAndFavoritesComparator());
            }
            this.following = arrayList;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, null));
        } else if (asyncResult.getStatus() != AsyncActionStatus.SUCCESS || (this.addUserToFollowingResponse.code != PeopleResponseError.MAX_FOLLOWING_LIMIT_REACHED && !this.addUserToFollowingResponse.getAddFollowingRequestStatus())) {
            this.addUserToFollowingResponse = null;
        }
    }

    private void onAddUserToShareIdentityCompleted(AsyncResult<Boolean> asyncResult, ArrayList<String> arrayList) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue()) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ProfileSummaryResult profileSummaryData = getProfileModel((String) it.next()).getProfileSummaryData();
                if (profileSummaryData != null) {
                    profileSummaryData.hasCallerMarkedTargetAsIdentityShared = true;
                }
            }
            ProfileModel meProfileModel = getMeProfileModel();
            Iterable profileFollowingSummaryData = meProfileModel.getProfileFollowingSummaryData();
            if (!XLEUtil.isNullOrEmpty(profileFollowingSummaryData)) {
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    String str = (String) it2.next();
                    Iterator it3 = profileFollowingSummaryData.iterator();
                    while (it3.hasNext()) {
                        People people = (People) it3.next();
                        if (people.xuid.equalsIgnoreCase(str)) {
                            people.isIdentityShared = true;
                            break;
                        }
                    }
                }
                meProfileModel.setProfileFollowingSummaryData(profileFollowingSummaryData);
            }
        }
    }

    private void onGetMutedListCompleted(AsyncResult<MutedListResult> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            MutedListResult mutedListResult = (MutedListResult) asyncResult.getResult();
            this.lastRefreshMutedList = new Date();
            if (mutedListResult != null) {
                this.mutedList = mutedListResult;
            } else {
                this.mutedList = new MutedListResult();
            }
        }
    }

    private void onGetNeverListCompleted(AsyncResult<NeverListResult> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            NeverListResult neverListResult = (NeverListResult) asyncResult.getResult();
            this.lastRefreshNeverList = new Date();
            if (neverListResult != null) {
                this.neverList = neverListResult;
            } else {
                this.neverList = new NeverListResult();
            }
        }
    }

    private void onGetPeopleHubPersonDataCompleted(AsyncResult<PeopleHubPersonSummary> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.peopleHubPersonSummary = (PeopleHubPersonSummary) asyncResult.getResult();
        }
    }

    private void onGetPeopleHubRecommendationsCompleted(AsyncResult<PeopleHubPeopleSummary> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            PeopleHubPeopleSummary peopleHubPeopleSummary = (PeopleHubPeopleSummary) asyncResult.getResult();
            if (peopleHubPeopleSummary == null) {
                this.peopleHubRecommendationsRaw = null;
                this.peopleHubRecommendations = null;
                return;
            }
            this.peopleHubRecommendationsRaw = peopleHubPeopleSummary;
            FriendsFinderStateResult facebookFriendFinderState = FacebookManager.getInstance().getFacebookFriendFinderState();
            boolean z = facebookFriendFinderState != null && facebookFriendFinderState.getLinkedAccountOptInStatus() == LinkedAccountOptInStatus.ShowPrompt;
            buildRecommendationsList(z);
            this.lastRefreshPeopleHubRecommendations = new Date();
        }
    }

    private void onGetPresenceDataCompleted(AsyncResult<UserPresence> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.lastRefreshPresenceData = new Date();
            this.presenceData = (UserPresence) asyncResult.getResult();
        }
    }

    private void onGetProfileSummaryCompleted(AsyncResult<ProfileSummaryResult> asyncResult) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            ProfileSummaryResult profileSummaryResult = (ProfileSummaryResult) asyncResult.getResult();
            this.lastRefreshProfileSummary = new Date();
            this.profileSummary = profileSummaryResult;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.ActivityAlertsSummary, true), this, null));
        }
    }

    private void onPutUserToMutedListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue()) {
            if (this.mutedList == null) {
                this.mutedList = new MutedListResult();
            }
            if (!this.mutedList.contains(str)) {
                this.mutedList.add(str);
            }
        }
    }

    private void onPutUserToNeverListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue()) {
            if (this.neverList == null) {
                this.neverList = new NeverListResult();
            }
            if (!this.neverList.contains(str)) {
                this.neverList.add(str);
            }
        }
    }

    private void onRemoveUserFromFavoriteListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue() && this.following != null) {
            ArrayList arrayList = new ArrayList();
            Iterator it = this.following.iterator();
            while (it.hasNext()) {
                FollowersData followersData = (FollowersData) it.next();
                if (followersData.xuid.equals(str)) {
                    followersData.isFavorite = false;
                }
                if (followersData.isFavorite) {
                    arrayList.add(followersData);
                }
            }
            this.favorites = arrayList;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, null));
        }
    }

    private void onRemoveUserFromFollowingListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue() && this.following != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            Iterator it = this.following.iterator();
            while (it.hasNext()) {
                FollowersData followersData = (FollowersData) it.next();
                if (!followersData.xuid.equals(str)) {
                    arrayList.add(followersData);
                    if (followersData.isFavorite) {
                        arrayList2.add(followersData);
                    }
                }
            }
            this.following = arrayList;
            this.favorites = arrayList2;
            notifyObservers(new AsyncResult(new UpdateData(UpdateType.UpdateFriend, true), this, null));
        }
    }

    private void onRemoveUserFromMutedListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue() && this.mutedList != null && this.mutedList.contains(str)) {
            this.mutedList.remove(str);
        }
    }

    private void onRemoveUserFromNeverListCompleted(AsyncResult<Boolean> asyncResult, String str) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue() && this.neverList != null && this.neverList.contains(str)) {
            this.neverList.remove(str);
        }
    }

    private void onRemoveUserFromShareIdentityCompleted(AsyncResult<Boolean> asyncResult, ArrayList<String> arrayList) {
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS && ((Boolean) asyncResult.getResult()).booleanValue()) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                ProfileSummaryResult profileSummaryData = getProfileModel((String) it.next()).getProfileSummaryData();
                if (profileSummaryData != null) {
                    profileSummaryData.hasCallerMarkedTargetAsIdentityShared = false;
                }
            }
            ProfileModel meProfileModel = getMeProfileModel();
            Iterable profileFollowingSummaryData = meProfileModel.getProfileFollowingSummaryData();
            if (!XLEUtil.isNullOrEmpty(profileFollowingSummaryData)) {
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    String str = (String) it2.next();
                    Iterator it3 = profileFollowingSummaryData.iterator();
                    while (it3.hasNext()) {
                        People people = (People) it3.next();
                        if (people.xuid.equalsIgnoreCase(str)) {
                            people.isIdentityShared = false;
                            break;
                        }
                    }
                }
                meProfileModel.setProfileFollowingSummaryData(profileFollowingSummaryData);
            }
        }
    }

    private void onSubmitFeedbackForUserCompleted(AsyncResult<Boolean> asyncResult) {
    }

    public static void reset() {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        Enumeration elements = profileModelCache.elements();
        while (elements.hasMoreElements()) {
            ((ProfileModel) elements.nextElement()).clearObserver();
        }
        if (meProfileInstance != null) {
            meProfileInstance.clearObserver();
            meProfileInstance = null;
        }
        profileModelCache = new ThreadSafeFixedSizeHashtable(MAX_PROFILE_MODELS);
    }

    private void updateWithProfileData(AsyncResult<ProfileData> asyncResult, boolean z) {
        updateWithNewData(asyncResult);
        if (z) {
            invalidateData();
        }
    }

    public AsyncResult<Boolean> addUserToFavoriteList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.Load(z, this.lifetime, null, this.addingUserToFavoriteListLoadingStatus, new AddUserToFavoriteListRunner(this, str));
    }

    public AsyncResult<AddFollowingUserResponse> addUserToFollowingList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.Load(z, this.lifetime, null, this.addingUserToFollowingListLoadingStatus, new AddUserToFollowingListRunner(this, str));
    }

    public AsyncResult<Boolean> addUserToMutedList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.Load(z, this.lifetime, null, this.addingUserToMutedListLoadingStatus, new PutUserToMutedListRunner(this, this.xuid, str));
    }

    public AsyncResult<Boolean> addUserToNeverList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.Load(z, this.lifetime, null, this.addingUserToNeverListLoadingStatus, new PutUserToNeverListRunner(this, this.xuid, str));
    }

    public AsyncResult<Boolean> addUserToShareIdentity(boolean z, ArrayList<String> arrayList) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.Load(z, this.lifetime, null, this.addingUserToShareIdentityListLoadingStatus, new AddUsersToShareIdentityListRunner(this, arrayList));
    }

    public String getAccountTier() {
        return getProfileSettingValue(UserProfileSetting.AccountTier);
    }

    public AddFollowingUserResponse getAddUserToFollowingResult() {
        return this.addUserToFollowingResponse;
    }

    public String getAppDisplayName() {
        return getProfileSettingValue(UserProfileSetting.AppDisplayName);
    }

    public String getBio() {
        return getProfileSettingValue(UserProfileSetting.Bio);
    }

    public ArrayList<FollowersData> getFavorites() {
        return this.favorites;
    }

    public ArrayList<FollowersData> getFollowingData() {
        return this.following;
    }

    public String getGamerPicImageUrl() {
        return getProfileImageUrl();
    }

    public String getGamerScore() {
        return getProfileSettingValue(UserProfileSetting.Gamerscore);
    }

    public String getGamerTag() {
        return getProfileSettingValue(UserProfileSetting.Gamertag);
    }

    public String getLocation() {
        return getProfileSettingValue(UserProfileSetting.Location);
    }

    public int getMaturityLevel() {
        return this.profileUser != null ? this.profileUser.getMaturityLevel() : 0;
    }

    public MutedListResult getMutedList() {
        return this.mutedList;
    }

    public NeverListResult getNeverListData() {
        return this.neverList;
    }

    public int getNumberOfFollowers() {
        return this.profileSummary != null ? this.profileSummary.targetFollowerCount : 0;
    }

    public int getNumberOfFollowing() {
        return this.profileSummary != null ? this.profileSummary.targetFollowingCount : 0;
    }

    public PeopleHubPersonSummary getPeopleHubPersonSummary() {
        return this.peopleHubPersonSummary;
    }

    public PeopleHubPeopleSummary getPeopleHubRecommendationsRawData() {
        return this.peopleHubRecommendationsRaw;
    }

    public int getPreferedColor() {
        return (this.profileUser == null || this.profileUser.colors == null) ? getDefaultColor() : this.profileUser.colors.getPrimaryColor();
    }

    public UserPresence getPresenceData() {
        return this.presenceData;
    }

    public ArrayList<People> getProfileFollowingSummaryData() {
        return this.followingSummaries;
    }

    public ProfileSummaryResult getProfileSummaryData() {
        return this.profileSummary;
    }

    public String getRealName() {
        return this.shareRealName ? getProfileSettingValue(UserProfileSetting.RealName) : null;
    }

    public String getShareRealNameStatus() {
        return this.shareRealNameStatus;
    }

    public ArrayList<URI> getWatermarkUris() {
        ArrayList<URI> arrayList = new ArrayList();
        String profileSettingValue = getProfileSettingValue(UserProfileSetting.TenureLevel);
        if (!(JavaUtil.isNullOrEmpty(profileSettingValue) || profileSettingValue.equalsIgnoreCase(MigrationManager.InitialSdkVersion))) {
            try {
                String tenureWatermarkUrlFormat = XboxLiveEnvironment.Instance().getTenureWatermarkUrlFormat();
                Object[] objArr = new Object[1];
                if (profileSettingValue.length() == 1) {
                    profileSettingValue = MigrationManager.InitialSdkVersion + profileSettingValue;
                }
                objArr[0] = profileSettingValue;
                arrayList.add(new URI(String.format(tenureWatermarkUrlFormat, objArr)));
            } catch (URISyntaxException e) {
                XLEAssert.fail("Failed to create URI for tenure watermark: " + e.toString());
            }
        }
        profileSettingValue = getProfileSettingValue(UserProfileSetting.Watermarks);
        if (!JavaUtil.isNullOrEmpty(profileSettingValue)) {
            for (String str : profileSettingValue.split("\\|")) {
                try {
                    arrayList.add(new URI(XboxLiveEnvironment.Instance().getWatermarkUrl(str)));
                } catch (URISyntaxException e2) {
                    XLEAssert.fail("Failed to create URI for watermark " + str + " : " + e2.toString());
                }
            }
        }
        return arrayList;
    }

    public String getXuid() {
        return this.xuid;
    }

    public boolean hasCallerMarkedTargetAsFavorite() {
        return this.profileSummary != null && this.profileSummary.hasCallerMarkedTargetAsFavorite;
    }

    public boolean hasCallerMarkedTargetAsIdentityShared() {
        return this.profileSummary != null && this.profileSummary.hasCallerMarkedTargetAsIdentityShared;
    }

    public boolean isCallerFollowingTarget() {
        return this.profileSummary != null && this.profileSummary.isCallerFollowingTarget;
    }

    public boolean isMeProfile() {
        return isMeXuid(this.xuid);
    }

    public boolean isTargetFollowingCaller() {
        return this.profileSummary != null && this.profileSummary.isTargetFollowingCaller;
    }

    public void loadAsync(boolean z) {
        loadInternal(z, UpdateType.MeProfileData, new GetProfileRunner(this, this.xuid, false));
    }

    public AsyncResult<PeopleHubPeopleSummary> loadPeopleHubRecommendations(boolean z) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.Load(z, profilePresenceDataLifetime, this.lastRefreshPeopleHubRecommendations, new SingleEntryLoadingStatus(), new GetPeopleHubRecommendationRunner(this, this.xuid));
    }

    public AsyncResult<UserPresence> loadPresenceData(boolean z) {
        if (this.presenceDataLoadingStatus == null) {
            this.presenceDataLoadingStatus = new SingleEntryLoadingStatus();
        }
        return DataLoadUtil.Load(z, profilePresenceDataLifetime, this.lastRefreshPresenceData, this.presenceDataLoadingStatus, new GetPresenceDataRunner(this, this.xuid));
    }

    public AsyncResult<ProfileSummaryResult> loadProfileSummary(boolean z) {
        if (this.profileSummaryLoadingStatus == null) {
            this.profileSummaryLoadingStatus = new SingleEntryLoadingStatus();
        }
        return DataLoadUtil.Load(z, this.lifetime, this.lastRefreshProfileSummary, this.profileSummaryLoadingStatus, new GetProfileSummaryRunner(this, this.xuid));
    }

    public AsyncResult<ProfileData> loadSync(boolean z) {
        return loadSync(z, false);
    }

    public AsyncResult<ProfileData> loadSync(boolean z, boolean z2) {
        return super.loadData(z, new GetProfileRunner(this, this.xuid, z2));
    }

    public AsyncResult<MutedListResult> loadUserMutedList(boolean z) {
        return DataLoadUtil.Load(z, this.lifetime, this.lastRefreshMutedList, this.mutedListLoadingStatus, new GetMutedListRunner(this, this.xuid));
    }

    public AsyncResult<NeverListResult> loadUserNeverList(boolean z) {
        return DataLoadUtil.Load(z, this.lifetime, this.lastRefreshNeverList, this.neverListLoadingStatus, new GetNeverListRunner(this, this.xuid));
    }

    public AsyncResult<Boolean> removeUserFromFavoriteList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.Load(z, this.lifetime, null, this.removingUserFromFavoriteListLoadingStatus, new RemoveUserFromFavoriteListRunner(this, str));
    }

    public AsyncResult<Boolean> removeUserFromFollowingList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.Load(z, this.lifetime, null, this.removingUserFromFollowingListLoadingStatus, new RemoveUserFromFollowingListRunner(this, str));
    }

    public AsyncResult<Boolean> removeUserFromMutedList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.Load(z, this.lifetime, null, this.removingUserFromMutedListLoadingStatus, new RemoveUserFromMutedListRunner(this, this.xuid, str));
    }

    public AsyncResult<Boolean> removeUserFromNeverList(boolean z, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        XLEAssert.assertNotNull(str);
        return DataLoadUtil.Load(z, this.lifetime, null, this.removingUserFromNeverListLoadingStatus, new RemoveUserFromNeverListRunner(this, this.xuid, str));
    }

    public AsyncResult<Boolean> removeUserFromShareIdentity(boolean z, ArrayList<String> arrayList) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.Load(z, this.lifetime, null, this.removingUserFromShareIdentityListLoadingStatus, new RemoveUsersFromShareIdentityListRunner(this, arrayList));
    }

    public void setFirstName(String str) {
        this.firstName = str;
    }

    public void setLastName(String str) {
        this.lastName = str;
    }

    public void setProfileFollowingSummaryData(ArrayList<People> arrayList) {
        this.followingSummaries = arrayList;
    }

    public boolean shouldRefreshPresenceData() {
        return XLEUtil.shouldRefresh(this.lastRefreshPresenceData, this.lifetime);
    }

    public boolean shouldRefreshProfileSummary() {
        return XLEUtil.shouldRefresh(this.lastRefreshProfileSummary, this.lifetime);
    }

    public AsyncResult<Boolean> submitFeedbackForUser(boolean z, FeedbackType feedbackType, String str) {
        XLEAssert.assertIsNotUIThread();
        XLEAssert.assertNotNull(this.xuid);
        return DataLoadUtil.Load(z, this.lifetime, null, this.submitFeedbackForUserLoadingStatus, new SubmitFeedbackForUserRunner(this, this.xuid, feedbackType, str));
    }

    public void updateWithNewData(AsyncResult<ProfileData> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        super.updateWithNewData(asyncResult);
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            ProfileData profileData = (ProfileData) asyncResult.getResult();
            if (profileData != null) {
                this.shareRealName = isMeProfile() ? profileData.getShareRealName() : true;
                this.shareRealNameStatus = profileData.getShareRealNameStatus();
                Log.i("ProfileModel", "shareRealNameStatus: " + this.shareRealNameStatus);
                this.sharingRealNameTransitively = profileData.getSharingRealNameTransitively();
                UserProfileResult profileResult = profileData.getProfileResult();
                if (!(profileResult == null || profileResult.profileUsers == null)) {
                    this.profileUser = (ProfileUser) profileResult.profileUsers.get(0);
                    this.profileImageUrl = null;
                }
            }
        }
        notifyObservers(new AsyncResult(new UpdateData(UpdateType.ProfileData, true), this, asyncResult.getException()));
    }
}
