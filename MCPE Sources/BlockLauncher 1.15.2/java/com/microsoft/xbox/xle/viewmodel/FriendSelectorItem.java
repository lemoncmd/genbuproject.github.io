package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.service.model.FollowersData;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.UserProfileData;

public final class FriendSelectorItem extends FollowersData {
    private static final long serialVersionUID = 5799344980951867134L;
    private boolean selected;

    public FriendSelectorItem(FollowersData followersData) {
        super(followersData);
        this.selected = false;
    }

    public FriendSelectorItem(ProfileModel profileModel) {
        this.xuid = profileModel.getXuid();
        this.userProfileData = new UserProfileData();
        this.userProfileData.gamerTag = profileModel.getGamerTag();
        this.userProfileData.xuid = profileModel.getXuid();
        this.userProfileData.profileImageUrl = profileModel.getGamerPicImageUrl();
        this.userProfileData.gamerScore = profileModel.getGamerScore();
        this.userProfileData.appDisplayName = profileModel.getAppDisplayName();
        this.userProfileData.accountTier = profileModel.getAccountTier();
        this.userProfileData.gamerRealName = profileModel.getRealName();
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            FriendSelectorItem friendSelectorItem = (FriendSelectorItem) obj;
            if (this.userProfileData == null || this.userProfileData.gamerTag == null) {
                if (friendSelectorItem.userProfileData != null) {
                    return false;
                }
                if (friendSelectorItem.userProfileData.gamerTag != null) {
                    return false;
                }
            } else if (!this.userProfileData.gamerTag.equals(friendSelectorItem.userProfileData.gamerTag)) {
                return false;
            }
        }
        return true;
    }

    public boolean getIsSelected() {
        return this.selected;
    }

    public int hashCode() {
        int hashCode = (this.userProfileData == null || this.userProfileData.gamerTag == null) ? 0 : this.userProfileData.gamerTag.hashCode();
        return hashCode + 31;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public void toggleSelection() {
        this.selected = !this.selected;
    }
}
