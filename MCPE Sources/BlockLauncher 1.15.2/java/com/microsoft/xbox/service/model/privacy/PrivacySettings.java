package com.microsoft.xbox.service.model.privacy;

public class PrivacySettings {

    public static class PrivacySetting {
        public String setting;
        private PrivacySettingId settingId;
        private PrivacySettingValue settingValue;
        public String value;

        public PrivacySetting(PrivacySettingId privacySettingId, PrivacySettingValue privacySettingValue) {
            this.setting = privacySettingId.name();
            this.value = privacySettingValue.name();
        }

        public PrivacySettingId getPrivacySettingId() {
            this.settingId = PrivacySettingId.getPrivacySettingId(this.setting);
            return this.settingId;
        }

        public PrivacySettingValue getPrivacySettingValue() {
            this.settingValue = PrivacySettingValue.getPrivacySettingValue(this.value);
            return this.settingValue;
        }

        public void setPrivacySettingId(PrivacySettingId privacySettingId) {
            this.setting = privacySettingId.name();
            this.settingId = privacySettingId;
        }
    }

    public enum PrivacySettingId {
        None,
        ShareFriendList,
        ShareGameHistory,
        CommunicateUsingTextAndVoice,
        SharePresence,
        ShareProfile,
        ShareVideoAndMusicStatus,
        CommunicateUsingVideo,
        CollectVoiceData,
        ShareXboxMusicActivity,
        ShareExerciseInfo,
        ShareIdentity,
        ShareRecordedGameSessions,
        ShareIdentityTransitively,
        CanShareIdentity;

        public static PrivacySettingId getPrivacySettingId(String str) {
            for (PrivacySettingId privacySettingId : values()) {
                if (privacySettingId.name().equalsIgnoreCase(str)) {
                    return privacySettingId;
                }
            }
            return None;
        }
    }

    public enum PrivacySettingValue {
        NotSet,
        Everyone,
        PeopleOnMyList,
        FriendCategoryShareIdentity,
        Blocked;

        public static PrivacySettingValue getPrivacySettingValue(String str) {
            for (PrivacySettingValue privacySettingValue : values()) {
                if (privacySettingValue.name().equalsIgnoreCase(str)) {
                    return privacySettingValue;
                }
            }
            return NotSet;
        }
    }
}
