package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import java.util.HashMap;

public class ActivityParameters extends HashMap<String, Object> {
    private static final String FF_DONE = "FriendFinderDone";
    private static final String FF_INFO_TYPE = "InfoType";
    private static final String FORCE_RELOAD = "ForceReload";
    private static final String FROM_SCREEN = "FromScreen";
    private static final String ME_XUID = "MeXuid";
    private static final String ORIGINATING_PAGE = "OriginatingPage";
    private static final String PRIVILEGES = "Privileges";
    private static final String SELECTED_PROFILE = "SelectedProfile";

    public boolean getFriendFinderDone() {
        Boolean bool = (Boolean) get(FF_DONE);
        return bool != null && bool.booleanValue();
    }

    public FriendFinderType getFriendFinderType() {
        FriendFinderType friendFinderType = (FriendFinderType) get(FF_INFO_TYPE);
        return friendFinderType == null ? FriendFinderType.UNKNOWN : friendFinderType;
    }

    public String getMeXuid() {
        return (String) get(ME_XUID);
    }

    public String getPrivileges() {
        return (String) get(PRIVILEGES);
    }

    public String getSelectedProfile() {
        return (String) get(SELECTED_PROFILE);
    }

    public boolean isForceReload() {
        return containsKey(FORCE_RELOAD) ? ((Boolean) get(FORCE_RELOAD)).booleanValue() : false;
    }

    public void putFriendFinderDone(boolean z) {
        put(FF_DONE, Boolean.valueOf(z));
    }

    public void putFriendFinderType(FriendFinderType friendFinderType) {
        put(FF_INFO_TYPE, friendFinderType);
    }

    public void putFromScreen(ScreenLayout screenLayout) {
        put(FROM_SCREEN, screenLayout);
    }

    public void putMeXuid(String str) {
        put(ME_XUID, str);
    }

    public void putPrivileges(String str) {
        put(PRIVILEGES, str);
    }

    public void putSelectedProfile(String str) {
        put(SELECTED_PROFILE, str);
    }

    public void putSourcePage(String str) {
        put(ORIGINATING_PAGE, str);
    }
}
