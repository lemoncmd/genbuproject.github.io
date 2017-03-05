package com.microsoft.xbox.service.network.managers.xblshared;

import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.FriendsFinderStateResult;
import com.microsoft.xbox.service.model.friendfinder.LinkedAccountHelpers.LinkedAccountType;
import com.microsoft.xbox.service.model.friendfinder.OptInStatus;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileRequest;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.ShortCircuitProfileResponse;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.UploadPhoneContactsRequest;
import com.microsoft.xbox.service.model.friendfinder.ShortCircuitProfileMessage.UploadPhoneContactsResponse;
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
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.xle.app.FriendFinderSettings;

public interface ISLSServiceManager {
    UserProfileResult SearchGamertag(String str) throws XLEException;

    boolean addFriendToShareIdentitySetting(String str, String str2) throws XLEException;

    boolean addUserToFavoriteList(String str) throws XLEException;

    AddFollowingUserResponse addUserToFollowingList(String str) throws XLEException;

    boolean addUserToMutedList(String str, String str2) throws XLEException;

    boolean addUserToNeverList(String str, String str2) throws XLEException;

    FamilySettings getFamilySettings(String str) throws XLEException;

    FriendFinderSettings getFriendFinderSettings() throws XLEException;

    MutedListResult getMutedListInfo(String str) throws XLEException;

    ShortCircuitProfileResponse getMyShortCircuitProfile() throws XLEException;

    NeverListResult getNeverListInfo(String str) throws XLEException;

    FriendsFinderStateResult getPeopleHubFriendFinderState() throws XLEException;

    PeopleHubPeopleSummary getPeopleHubRecommendations() throws XLEException;

    PrivacySetting getPrivacySetting(PrivacySettingId privacySettingId) throws XLEException;

    ProfilePreferredColor getProfilePreferredColor(String str) throws XLEException;

    ProfileSummaryResult getProfileSummaryInfo(String str) throws XLEException;

    UserProfileResult getUserProfileInfo(String str) throws XLEException;

    PrivacySettingsResult getUserProfilePrivacySettings() throws XLEException;

    int[] getXTokenPrivileges() throws XLEException;

    boolean removeFriendFromShareIdentitySetting(String str, String str2) throws XLEException;

    boolean removeUserFromFavoriteList(String str) throws XLEException;

    boolean removeUserFromFollowingList(String str) throws XLEException;

    boolean removeUserFromMutedList(String str, String str2) throws XLEException;

    boolean removeUserFromNeverList(String str, String str2) throws XLEException;

    ShortCircuitProfileResponse sendShortCircuitProfile(ShortCircuitProfileRequest shortCircuitProfileRequest) throws XLEException;

    boolean setFriendFinderOptInStatus(LinkedAccountType linkedAccountType, OptInStatus optInStatus) throws XLEException;

    boolean setPrivacySettings(PrivacySettingsResult privacySettingsResult) throws XLEException;

    boolean submitFeedback(String str, String str2) throws XLEException;

    UploadPhoneContactsResponse updatePhoneContacts(UploadPhoneContactsRequest uploadPhoneContactsRequest) throws XLEException;

    boolean updateThirdPartyToken(LinkedAccountType linkedAccountType, String str) throws XLEException;
}
