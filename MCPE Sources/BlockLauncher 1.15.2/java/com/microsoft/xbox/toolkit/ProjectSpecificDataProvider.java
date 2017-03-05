package com.microsoft.xbox.toolkit;

import net.hockeyapp.android.BuildConfig;

public class ProjectSpecificDataProvider implements IProjectSpecificDataProvider {
    private static ProjectSpecificDataProvider instance = new ProjectSpecificDataProvider();
    private IProjectSpecificDataProvider provider;

    private void checkProvider() {
    }

    public static ProjectSpecificDataProvider getInstance() {
        return instance;
    }

    public boolean getAllowExplicitContent() {
        checkProvider();
        return this.provider != null ? this.provider.getAllowExplicitContent() : false;
    }

    public String getAutoSuggestdDataSource() {
        checkProvider();
        return this.provider != null ? this.provider.getAutoSuggestdDataSource() : null;
    }

    public String getCombinedContentRating() {
        checkProvider();
        return this.provider != null ? this.provider.getCombinedContentRating() : null;
    }

    public String getConnectedLocale() {
        checkProvider();
        return this.provider != null ? this.provider.getConnectedLocale() : null;
    }

    public String getConnectedLocale(boolean z) {
        checkProvider();
        return this.provider != null ? this.provider.getConnectedLocale(z) : null;
    }

    public String getContentRestrictions() {
        checkProvider();
        return this.provider != null ? this.provider.getContentRestrictions() : null;
    }

    public String getCurrentSandboxID() {
        checkProvider();
        return this.provider != null ? this.provider.getCurrentSandboxID() : null;
    }

    public boolean getInitializeComplete() {
        checkProvider();
        return this.provider != null ? this.provider.getInitializeComplete() : false;
    }

    public boolean getIsForXboxOne() {
        checkProvider();
        return this.provider != null ? this.provider.getIsForXboxOne() : false;
    }

    public boolean getIsFreeAccount() {
        checkProvider();
        return this.provider != null ? this.provider.getIsFreeAccount() : true;
    }

    public boolean getIsXboxMusicSupported() {
        checkProvider();
        return this.provider != null ? this.provider.getIsXboxMusicSupported() : false;
    }

    public String getLegalLocale() {
        checkProvider();
        return this.provider != null ? this.provider.getLegalLocale() : null;
    }

    public String getMembershipLevel() {
        checkProvider();
        return this.provider != null ? this.provider.getMembershipLevel() : null;
    }

    public String getPrivileges() {
        checkProvider();
        return this.provider != null ? this.provider.getPrivileges() : BuildConfig.FLAVOR;
    }

    public String getRegion() {
        checkProvider();
        return this.provider != null ? this.provider.getRegion() : null;
    }

    public String getSCDRpsTicket() {
        checkProvider();
        return this.provider != null ? this.provider.getSCDRpsTicket() : null;
    }

    public String getVersionCheckUrl() {
        checkProvider();
        return this.provider != null ? this.provider.getVersionCheckUrl() : null;
    }

    public int getVersionCode() {
        checkProvider();
        return this.provider != null ? this.provider.getVersionCode() : 0;
    }

    public String getWindowsLiveClientId() {
        checkProvider();
        return this.provider != null ? this.provider.getWindowsLiveClientId() : null;
    }

    public String getXuidString() {
        checkProvider();
        return this.provider != null ? this.provider.getXuidString() : null;
    }

    public boolean isDeviceLocaleKnown() {
        checkProvider();
        return this.provider != null ? this.provider.isDeviceLocaleKnown() : true;
    }

    public void resetModels(boolean z) {
        checkProvider();
        if (this.provider != null) {
            this.provider.resetModels(z);
        }
    }

    public void setPrivileges(String str) {
        checkProvider();
        if (this.provider != null) {
            this.provider.setPrivileges(str);
        }
    }

    public void setProvider(IProjectSpecificDataProvider iProjectSpecificDataProvider) {
        this.provider = iProjectSpecificDataProvider;
    }

    public void setSCDRpsTicket(String str) {
        checkProvider();
        if (this.provider != null) {
            this.provider.setSCDRpsTicket(str);
        }
    }

    public void setXuidString(String str) {
        checkProvider();
        if (this.provider != null) {
            this.provider.setXuidString(str);
        }
    }
}
