package com.microsoft.xbox.service.model.privacy;

import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySetting;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySettingId;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySettingValue;
import com.microsoft.xbox.toolkit.GsonUtil;
import java.util.ArrayList;
import java.util.Iterator;

public class PrivacySettingsResult {
    public ArrayList<PrivacySetting> settings;

    public PrivacySettingsResult(ArrayList<PrivacySetting> arrayList) {
        this.settings = new ArrayList(arrayList);
    }

    public static PrivacySettingsResult deserialize(String str) {
        return (PrivacySettingsResult) GsonUtil.deserializeJson(str, PrivacySettingsResult.class);
    }

    public static String getPrivacySettingRequestBody(PrivacySettingsResult privacySettingsResult) {
        try {
            return GsonUtil.toJsonString(privacySettingsResult);
        } catch (Exception e) {
            return null;
        }
    }

    public String getShareRealNameStatus() {
        Iterator it = this.settings.iterator();
        while (it.hasNext()) {
            PrivacySetting privacySetting = (PrivacySetting) it.next();
            if (privacySetting.getPrivacySettingId() == PrivacySettingId.ShareIdentity) {
                return privacySetting.value;
            }
        }
        return PrivacySettingValue.PeopleOnMyList.name();
    }

    public boolean getSharingRealNameTransitively() {
        Iterator it = this.settings.iterator();
        while (it.hasNext()) {
            PrivacySetting privacySetting = (PrivacySetting) it.next();
            if (privacySetting.getPrivacySettingId() == PrivacySettingId.ShareIdentityTransitively) {
                return privacySetting.value.equalsIgnoreCase(PrivacySettingValue.Everyone.name());
            }
        }
        return false;
    }
}
