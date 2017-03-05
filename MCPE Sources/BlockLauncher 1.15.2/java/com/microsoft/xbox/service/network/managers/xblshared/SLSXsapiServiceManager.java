package com.microsoft.xbox.service.network.managers.xblshared;

import android.util.Log;
import android.util.Pair;
import com.integralblue.httpresponsecache.compat.libcore.net.http.HttpEngine;
import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import com.microsoft.xbox.idp.util.HttpCall;
import com.microsoft.xbox.idp.util.HttpCall.Callback;
import com.microsoft.xbox.idp.util.HttpUtil;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.FriendsFinderStateResult;
import com.microsoft.xbox.service.model.friendfinder.LinkedAccountHelpers.LinkedAccountType;
import com.microsoft.xbox.service.model.friendfinder.OptInStatus;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileRequest;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileResponse;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.UploadPhoneContactsRequest;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.UploadPhoneContactsResponse;
import com.microsoft.xbox.service.model.friendfinder.UpdateThirdPartyTokenRequest;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySetting;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySettingId;
import com.microsoft.xbox.service.model.privacy.PrivacySettingsResult;
import com.microsoft.xbox.service.network.managers.AddFollowingUserResponseContainer.AddFollowingUserResponse;
import com.microsoft.xbox.service.network.managers.FamilySettings;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.PeopleHubPeopleSummary;
import com.microsoft.xbox.service.network.managers.IUserProfileResult.UserProfileResult;
import com.microsoft.xbox.service.network.managers.MutedListResultContainer.MutedListResult;
import com.microsoft.xbox.service.network.managers.NeverListResultContainer.NeverListResult;
import com.microsoft.xbox.service.network.managers.ProfilePreferredColor;
import com.microsoft.xbox.service.network.managers.ProfileSummaryResultContainer.ProfileSummaryResult;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.TcuiHttpUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.FriendFinderSettings;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import net.hockeyapp.android.BuildConfig;

public class SLSXsapiServiceManager implements ISLSServiceManager {
    private static final String TAG = SLSXsapiServiceManager.class.getSimpleName();

    public UserProfileResult SearchGamertag(String str) throws XLEException {
        boolean z = false;
        Log.i(TAG, "SearchGamertag");
        if (Thread.currentThread() != ThreadManager.UIThread) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        try {
            UserProfileResult userProfileResult = (UserProfileResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, String.format(XboxLiveEnvironment.Instance().getGamertagSearchUrlFormat(), new Object[]{URLEncoder.encode(str.toLowerCase(), "utf-8")}), BuildConfig.FLAVOR), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION), UserProfileResult.class);
            TcuiHttpUtil.throwIfNullOrFalse(userProfileResult);
            return userProfileResult;
        } catch (Throwable e) {
            throw new XLEException(15, e);
        }
    }

    public boolean addFriendToShareIdentitySetting(String str, String str2) throws XLEException {
        Log.i(TAG, "addFriendToShareIdentitySetting");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, String.format(XboxLiveEnvironment.Instance().getAddFriendsToShareIdentityUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(new Integer[]{Integer.valueOf(204)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public boolean addUserToFavoriteList(String str) throws XLEException {
        Log.i(TAG, "addUserToFavoriteList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, String.format(XboxLiveEnvironment.Instance().getProfileFavoriteListUrl(), new Object[]{"add"}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(new Integer[]{Integer.valueOf(204)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public AddFollowingUserResponse addUserToFollowingList(String str) throws XLEException {
        Log.i(TAG, "addUserToFollowingList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, String.format(XboxLiveEnvironment.Instance().updateProfileFollowingListUrl(), new Object[]{"add"}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str);
        final AddFollowingUserResponse addFollowingUserResponse = new AddFollowingUserResponse();
        final AtomicReference atomicReference = new AtomicReference();
        atomicReference.set(new Pair(Boolean.valueOf(false), null));
        appendCommonParameters.getResponseAsync(new Callback() {
            public void processHttpError(int i, int i2, String str) {
                synchronized (atomicReference) {
                    if (i2 == 204) {
                        addFollowingUserResponse.setAddFollowingRequestStatus(true);
                        atomicReference.set(new Pair(Boolean.valueOf(true), addFollowingUserResponse));
                    } else {
                        atomicReference.set(new Pair(Boolean.valueOf(true), (AddFollowingUserResponse) GsonUtil.deserializeJson(str, AddFollowingUserResponse.class)));
                    }
                    atomicReference.notify();
                }
            }

            public void processResponse(InputStream inputStream) throws Exception {
                synchronized (atomicReference) {
                    addFollowingUserResponse.setAddFollowingRequestStatus(true);
                    atomicReference.set(new Pair(Boolean.valueOf(true), addFollowingUserResponse));
                    atomicReference.notify();
                }
            }
        });
        synchronized (atomicReference) {
            while (!((Boolean) ((Pair) atomicReference.get()).first).booleanValue()) {
                try {
                    atomicReference.wait();
                } catch (InterruptedException e) {
                }
            }
        }
        TcuiHttpUtil.throwIfNullOrFalse(((Pair) atomicReference.get()).second);
        return (AddFollowingUserResponse) ((Pair) atomicReference.get()).second;
    }

    public boolean addUserToMutedList(String str, String str2) throws XLEException {
        Log.i(TAG, "addUserToMutedList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.PUT, String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public boolean addUserToNeverList(String str, String str2) throws XLEException {
        Log.i(TAG, "addUserToNeverList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.PUT, String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public FamilySettings getFamilySettings(String str) throws XLEException {
        return null;
    }

    public FriendFinderSettings getFriendFinderSettings() throws XLEException {
        Log.i(TAG, "getFriendFinderSettings");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        FriendFinderSettings friendFinderSettings = (FriendFinderSettings) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, XboxLiveEnvironment.Instance().getFriendFinderSettingsUrl(), BuildConfig.FLAVOR, false), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION), FriendFinderSettings.class);
        TcuiHttpUtil.throwIfNullOrFalse(friendFinderSettings);
        return friendFinderSettings;
    }

    public MutedListResult getMutedListInfo(String str) throws XLEException {
        Log.i(TAG, "getMutedListInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(str));
        MutedListResult mutedListResult = (MutedListResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION), MutedListResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(mutedListResult);
        return mutedListResult;
    }

    public ShortCircuitProfileResponse getMyShortCircuitProfile() throws XLEException {
        Log.i(TAG, "getMyShortCircuitProfile");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        String sCDRpsTicket = ProjectSpecificDataProvider.getInstance().getSCDRpsTicket();
        XLEAssert.assertFalse("Expected to have acquired a ticket already", JavaUtil.isNullOrEmpty(sCDRpsTicket));
        if (JavaUtil.isNullOrEmpty(sCDRpsTicket)) {
            throw new XLEException(2);
        }
        HttpCall httpCall = new HttpCall(HttpEngine.GET, XboxLiveEnvironment.Instance().getShortCircuitProfileUrlFormat(), BuildConfig.FLAVOR);
        httpCall.setCustomHeader("PS-MSAAuthTicket", sCDRpsTicket);
        httpCall.setCustomHeader("PS-ApplicationId", "44445A65-4A71-4083-8C90-041A22856E69");
        httpCall.setCustomHeader("PS-Scenario", "Minecraft TCUI Friend Finder");
        httpCall.setCustomHeader("Content-Type", "application/x-www-form-urlencoded");
        sCDRpsTicket = TcuiHttpUtil.getResponseBodySync(httpCall);
        if (!JavaUtil.isNullOrEmpty(sCDRpsTicket)) {
            return ShortCircuitProfileResponse.parseJson(sCDRpsTicket);
        }
        throw new XLEException(2);
    }

    public NeverListResult getNeverListInfo(String str) throws XLEException {
        Log.i(TAG, "getNeverListInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(str));
        NeverListResult neverListResult = (NeverListResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION), NeverListResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(neverListResult);
        return neverListResult;
    }

    public FriendsFinderStateResult getPeopleHubFriendFinderState() throws XLEException {
        Log.i(TAG, "getPeopleHubFriendFinderState");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, XboxLiveEnvironment.Instance().getPeopleHubFriendFinderStateUrlFormat(), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setCustomHeader("Accept-Language", ProjectSpecificDataProvider.getInstance().getLegalLocale());
        appendCommonParameters.setCustomHeader("X-XBL-Contract-Version", XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setCustomHeader("X-XBL-Market", ProjectSpecificDataProvider.getInstance().getRegion());
        FriendsFinderStateResult friendsFinderStateResult = (FriendsFinderStateResult) TcuiHttpUtil.getResponseSync(appendCommonParameters, FriendsFinderStateResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(friendsFinderStateResult);
        return friendsFinderStateResult;
    }

    public PeopleHubPeopleSummary getPeopleHubRecommendations() throws XLEException {
        Log.i(TAG, "getPeopleHubRecommendations");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, XboxLiveEnvironment.Instance().getPeopleHubRecommendationsUrlFormat(), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setCustomHeader("Accept-Language", ProjectSpecificDataProvider.getInstance().getLegalLocale());
        appendCommonParameters.setCustomHeader("X-XBL-Contract-Version", XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setCustomHeader("X-XBL-Market", ProjectSpecificDataProvider.getInstance().getRegion());
        PeopleHubPeopleSummary peopleHubPeopleSummary = (PeopleHubPeopleSummary) TcuiHttpUtil.getResponseSync(appendCommonParameters, PeopleHubPeopleSummary.class);
        TcuiHttpUtil.throwIfNullOrFalse(peopleHubPeopleSummary);
        return peopleHubPeopleSummary;
    }

    public PrivacySetting getPrivacySetting(PrivacySettingId privacySettingId) throws XLEException {
        Log.i(TAG, "getPrivacySetting");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        PrivacySetting privacySetting = (PrivacySetting) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, String.format(XboxLiveEnvironment.Instance().getProfileSettingUrlFormat(), new Object[]{privacySettingId.name()}), BuildConfig.FLAVOR), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION), PrivacySetting.class);
        TcuiHttpUtil.throwIfNullOrFalse(privacySetting);
        return privacySetting;
    }

    public ProfilePreferredColor getProfilePreferredColor(String str) throws XLEException {
        Log.i(TAG, "getProfilePreferredColor");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        ProfilePreferredColor profilePreferredColor = (ProfilePreferredColor) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, str, BuildConfig.FLAVOR), BuildConfig.FLAVOR), ProfilePreferredColor.class);
        TcuiHttpUtil.throwIfNullOrFalse(profilePreferredColor);
        return profilePreferredColor;
    }

    public ProfileSummaryResult getProfileSummaryInfo(String str) throws XLEException {
        Log.i(TAG, "getProfileSummaryInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        XLEAssert.assertTrue(!JavaUtil.isNullOrEmpty(str));
        ProfileSummaryResult profileSummaryResult = (ProfileSummaryResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, String.format(XboxLiveEnvironment.Instance().getProfileSummaryUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION), ProfileSummaryResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(profileSummaryResult);
        return profileSummaryResult;
    }

    public UserProfileResult getUserProfileInfo(String str) throws XLEException {
        Log.i(TAG, "getUserProfileInfo");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, XboxLiveEnvironment.Instance().getUserProfileInfoUrl(), BuildConfig.FLAVOR), XboxLiveEnvironment.USER_PROFILE_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str);
        UserProfileResult userProfileResult = (UserProfileResult) TcuiHttpUtil.getResponseSync(appendCommonParameters, UserProfileResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(userProfileResult);
        return userProfileResult;
    }

    public PrivacySettingsResult getUserProfilePrivacySettings() throws XLEException {
        Log.i(TAG, "getUserProfilePrivacySettings");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        PrivacySettingsResult privacySettingsResult = (PrivacySettingsResult) TcuiHttpUtil.getResponseSync(HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.GET, XboxLiveEnvironment.Instance().getUserProfileSettingUrlFormat(), BuildConfig.FLAVOR), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION), PrivacySettingsResult.class);
        TcuiHttpUtil.throwIfNullOrFalse(privacySettingsResult);
        return privacySettingsResult;
    }

    public int[] getXTokenPrivileges() throws XLEException {
        return new int[0];
    }

    public boolean removeFriendFromShareIdentitySetting(String str, String str2) throws XLEException {
        Log.i(TAG, "removeFriendFromShareIdentitySetting");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, String.format(XboxLiveEnvironment.Instance().getRemoveUsersFromShareIdentityUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(new Integer[]{Integer.valueOf(204)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public boolean removeUserFromFavoriteList(String str) throws XLEException {
        Log.i(TAG, "removeUserFromFavoriteList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, String.format(XboxLiveEnvironment.Instance().getProfileFavoriteListUrl(), new Object[]{"remove"}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(new Integer[]{Integer.valueOf(204)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public boolean removeUserFromFollowingList(String str) throws XLEException {
        Log.i(TAG, "removeUserFromFollowingList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, String.format(XboxLiveEnvironment.Instance().updateProfileFollowingListUrl(), new Object[]{"remove"}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(new Integer[]{Integer.valueOf(204)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public boolean removeUserFromMutedList(String str, String str2) throws XLEException {
        Log.i(TAG, "removeUserFromMutedList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.DELETE, String.format(XboxLiveEnvironment.Instance().getMutedServiceUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public boolean removeUserFromNeverList(String str, String str2) throws XLEException {
        Log.i(TAG, "removeUserFromNeverList");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.DELETE, String.format(XboxLiveEnvironment.Instance().getProfileNeverListUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(0));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public ShortCircuitProfileResponse sendShortCircuitProfile(ShortCircuitProfileRequest shortCircuitProfileRequest) throws XLEException {
        Log.i(TAG, "sendShortCircuitProfile");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        String sCDRpsTicket = ProjectSpecificDataProvider.getInstance().getSCDRpsTicket();
        XLEAssert.assertFalse("Expected to have acquired a ticket already", JavaUtil.isNullOrEmpty(sCDRpsTicket));
        if (JavaUtil.isNullOrEmpty(sCDRpsTicket)) {
            throw new XLEException(2);
        }
        HttpCall httpCall = new HttpCall(HttpEngine.POST, XboxLiveEnvironment.Instance().getShortCircuitProfileUrlFormat(), BuildConfig.FLAVOR);
        httpCall.setCustomHeader("PS-MSAAuthTicket", sCDRpsTicket);
        httpCall.setCustomHeader("PS-ApplicationId", "44445A65-4A71-4083-8C90-041A22856E69");
        httpCall.setCustomHeader("PS-Scenario", "Minecraft TCUI Friend Finder");
        httpCall.setCustomHeader("Content-Type", "application/x-www-form-urlencoded");
        httpCall.setRequestBody(shortCircuitProfileRequest.toString());
        sCDRpsTicket = TcuiHttpUtil.getResponseBodySync(httpCall);
        if (!JavaUtil.isNullOrEmpty(sCDRpsTicket)) {
            return ShortCircuitProfileResponse.parseJson(sCDRpsTicket);
        }
        throw new XLEException(2);
    }

    public boolean setFriendFinderOptInStatus(LinkedAccountType linkedAccountType, OptInStatus optInStatus) throws XLEException {
        Log.i(TAG, "setFriendFinderOptInStatus");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.PUT, String.format(XboxLiveEnvironment.Instance().getSetFriendFinderOptInStatusUrlFormat(), new Object[]{linkedAccountType.name()}), optInStatus == OptInStatus.OptedIn ? "?status=OptedIn&waitForUpdate=true" : "?status=OptedOut"), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setCustomHeader("Content-Length", MigrationManager.InitialSdkVersion);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(new Integer[]{Integer.valueOf(204)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public boolean setPrivacySettings(PrivacySettingsResult privacySettingsResult) throws XLEException {
        Log.i(TAG, "setPrivacySettings");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.PUT, XboxLiveEnvironment.Instance().getUserProfileSettingUrlFormat(), BuildConfig.FLAVOR), XboxLiveEnvironment.USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(PrivacySettingsResult.getPrivacySettingRequestBody(privacySettingsResult));
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(new Integer[]{Integer.valueOf(201)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public boolean submitFeedback(String str, String str2) throws XLEException {
        Log.i(TAG, "submitFeedback");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.POST, String.format(XboxLiveEnvironment.Instance().getSubmitFeedbackUrlFormat(), new Object[]{str}), BuildConfig.FLAVOR), "101");
        appendCommonParameters.setRequestBody(str2);
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, new ArrayList(202));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }

    public UploadPhoneContactsResponse updatePhoneContacts(UploadPhoneContactsRequest uploadPhoneContactsRequest) throws XLEException {
        Log.i(TAG, "updatePhoneContacts");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        String sCDRpsTicket = ProjectSpecificDataProvider.getInstance().getSCDRpsTicket();
        XLEAssert.assertFalse("Expected to have acquired a ticket already", JavaUtil.isNullOrEmpty(sCDRpsTicket));
        if (JavaUtil.isNullOrEmpty(sCDRpsTicket)) {
            throw new XLEException(2);
        }
        HttpCall httpCall = new HttpCall(HttpEngine.POST, XboxLiveEnvironment.Instance().getUploadingPhoneContactsUrlFormat(), BuildConfig.FLAVOR);
        httpCall.setCustomHeader("X-TicketToken", sCDRpsTicket);
        httpCall.setCustomHeader("X-AppId", "44445A65-4A71-4083-8C90-041A22856E69");
        httpCall.setCustomHeader("X-Scenario", "Minecraft TCUI Friend Finder");
        httpCall.setCustomHeader("Content-Type", "application/x-www-form-urlencoded");
        httpCall.setRequestBody(uploadPhoneContactsRequest.toString());
        sCDRpsTicket = TcuiHttpUtil.getResponseBodySync(httpCall);
        if (!JavaUtil.isNullOrEmpty(sCDRpsTicket)) {
            return UploadPhoneContactsResponse.parseJson(sCDRpsTicket);
        }
        throw new XLEException(2);
    }

    public boolean updateThirdPartyToken(LinkedAccountType linkedAccountType, String str) throws XLEException {
        Log.i(TAG, "updateThirdPartyToken");
        XLEAssert.assertTrue(Thread.currentThread() != ThreadManager.UIThread);
        HttpCall appendCommonParameters = HttpUtil.appendCommonParameters(new HttpCall(HttpEngine.PUT, String.format(XboxLiveEnvironment.Instance().getUpdateThirdPartyTokenUrlFormat(), new Object[]{linkedAccountType.name()}), BuildConfig.FLAVOR), XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION);
        appendCommonParameters.setRequestBody(UpdateThirdPartyTokenRequest.getUpdateThirdPartyTokenRequestBody(new UpdateThirdPartyTokenRequest(str)));
        boolean responseSyncSucceeded = TcuiHttpUtil.getResponseSyncSucceeded(appendCommonParameters, Arrays.asList(new Integer[]{Integer.valueOf(204)}));
        TcuiHttpUtil.throwIfNullOrFalse(Boolean.valueOf(responseSyncSucceeded));
        return responseSyncSucceeded;
    }
}
