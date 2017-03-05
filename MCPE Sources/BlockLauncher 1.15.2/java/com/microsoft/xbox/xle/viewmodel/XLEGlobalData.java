package com.microsoft.xbox.xle.viewmodel;

import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.MultiSelection;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;

public class XLEGlobalData {
    private static final int MAX_SEARCH_TEXT_LENGTH = 120;
    private boolean autoLoginStarted;
    private Class<? extends ActivityBase> defaultScreenClass;
    private long errorCodeForLogin;
    private boolean forceRefreshProfile;
    private HashSet<Class<? extends ViewModelBase>> forceRefreshVMs;
    private boolean friendListUpdated;
    private boolean hideCollectionFilter;
    private boolean isLoggedIn;
    private boolean isOffline;
    private boolean launchTitleIsBrowser;
    private String pivotTitle;
    private Class<? extends ViewModelBase> searchFilterSetterClass;
    private String searchTag;
    private String selectedAchievementKey;
    private String selectedDataSource;
    private String selectedGamertag;
    private ArrayList<URI> selectedImages;
    private MultiSelection<FriendSelectorItem> selectedRecipients;
    private String selectedXuid;
    private boolean showLoginError;
    private long titleId;

    private static class XLEGlobalDataHolder {
        public static final XLEGlobalData instance = new XLEGlobalData();

        private XLEGlobalDataHolder() {
        }
    }

    private XLEGlobalData() {
        this.isOffline = true;
        this.friendListUpdated = false;
        this.launchTitleIsBrowser = false;
        this.hideCollectionFilter = false;
    }

    public static XLEGlobalData getInstance() {
        return XLEGlobalDataHolder.instance;
    }

    public void AddForceRefresh(Class<? extends ViewModelBase> cls) {
        XLEAssert.assertIsUIThread();
        if (this.forceRefreshVMs == null) {
            this.forceRefreshVMs = new HashSet();
        }
        this.forceRefreshVMs.add(cls);
    }

    public boolean CheckDrainShouldRefresh(Class<? extends ViewModelBase> cls) {
        return this.forceRefreshVMs != null && this.forceRefreshVMs.remove(cls);
    }

    public boolean getAutoLoginStarted() {
        return this.autoLoginStarted;
    }

    public Class<? extends ActivityBase> getDefaultScreenClass() {
        return this.defaultScreenClass;
    }

    public boolean getForceRefreshProfile() {
        return this.forceRefreshProfile;
    }

    public boolean getFriendListUpdated() {
        return this.friendListUpdated;
    }

    public boolean getHideCollectionFilter() {
        return this.hideCollectionFilter;
    }

    public boolean getIsLoggedIn() {
        return this.isLoggedIn;
    }

    public boolean getIsOffline() {
        return this.isOffline;
    }

    public boolean getIsOnline() {
        return !this.isOffline;
    }

    public long getLastLoginError() {
        long j = this.errorCodeForLogin;
        this.errorCodeForLogin = 0;
        return j;
    }

    public boolean getLaunchTitleIsBrowser() {
        return this.launchTitleIsBrowser;
    }

    public String getPivotTitle() {
        return this.pivotTitle;
    }

    public Class<? extends ViewModelBase> getSearchFilterSetterClass() {
        return this.searchFilterSetterClass;
    }

    public String getSearchTag() {
        return this.searchTag;
    }

    public String getSelectedAchievementKey() {
        return this.selectedAchievementKey;
    }

    public String getSelectedDataSource() {
        return this.selectedDataSource;
    }

    public String getSelectedGamertag() {
        return this.selectedGamertag;
    }

    public ArrayList<URI> getSelectedImages() {
        return this.selectedImages;
    }

    public MultiSelection<FriendSelectorItem> getSelectedRecipients() {
        if (this.selectedRecipients == null) {
            this.selectedRecipients = new MultiSelection();
        }
        return this.selectedRecipients;
    }

    public long getSelectedTitleId() {
        return this.titleId;
    }

    public String getSelectedXuid() {
        return JavaUtil.isNullOrEmpty(this.selectedXuid) ? ProjectSpecificDataProvider.getInstance().getXuidString() : this.selectedXuid;
    }

    public boolean getShowLoginError() {
        boolean z = this.showLoginError;
        this.showLoginError = false;
        return z;
    }

    public void resetGlobalParameters() {
        this.selectedGamertag = null;
        this.selectedAchievementKey = null;
        this.selectedDataSource = null;
        this.isLoggedIn = false;
        this.showLoginError = false;
        this.isOffline = true;
        this.searchTag = null;
        this.selectedImages = null;
        this.titleId = 0;
        this.forceRefreshVMs = null;
    }

    public void setAutoLoginStarted(boolean z) {
        this.autoLoginStarted = z;
    }

    public void setDefaultScreenClass(Class<? extends ActivityBase> cls) {
        this.defaultScreenClass = cls;
    }

    public void setForceRefreshProfile(boolean z) {
        this.forceRefreshProfile = z;
    }

    public void setFriendListUpdated(boolean z) {
        this.friendListUpdated = z;
    }

    public void setHideCollectionFilter(boolean z) {
        this.hideCollectionFilter = z;
    }

    public void setIsOffline(boolean z) {
        this.isOffline = z;
    }

    public void setLaunchTitleIsBrowser(boolean z) {
        this.launchTitleIsBrowser = z;
    }

    public void setLoggedIn(boolean z) {
        this.isLoggedIn = z;
    }

    public void setLoginErrorCode(long j) {
        this.errorCodeForLogin = j;
    }

    public void setPivotTitle(String str) {
        this.pivotTitle = str;
    }

    public void setSearchTag(String str) {
        if (str == null || str.length() <= MAX_SEARCH_TEXT_LENGTH) {
            this.searchTag = str;
        } else {
            this.searchTag = str.substring(0, MAX_SEARCH_TEXT_LENGTH);
        }
    }

    public void setSelectedAchievementKey(String str) {
        this.selectedAchievementKey = str;
    }

    public void setSelectedDataSource(String str) {
        this.selectedDataSource = str;
    }

    public void setSelectedGamertag(String str) {
        this.selectedGamertag = str;
    }

    public void setSelectedImages(ArrayList<URI> arrayList) {
        this.selectedImages = arrayList;
    }

    public void setSelectedTitleId(long j) {
        this.titleId = j;
    }

    public void setSelectedXuid(String str) {
        this.selectedXuid = str;
    }

    public void setShowLoginError(boolean z) {
        this.showLoginError = z;
    }
}
