package com.microsoft.xbox.xle.model;

import android.os.Build.VERSION;
import com.microsoft.xbox.service.model.ModelBase;
import com.microsoft.xbox.service.model.serialization.Version;
import com.microsoft.xbox.toolkit.AsyncActionStatus;
import com.microsoft.xbox.toolkit.AsyncResult;
import com.microsoft.xbox.toolkit.DataLoadUtil;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.SingleEntryLoadingStatus;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEErrorCode;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.network.IDataLoaderRunnable;
import com.microsoft.xbox.xle.app.FriendFinderSettings;
import com.microsoft.xbox.xle.app.SmartglassSettings;
import java.util.HashSet;

public class SystemSettingsModel extends ModelBase<Version> {
    private FriendFinderSettings friendFinderSettings;
    private final SingleEntryLoadingStatus friendFinderSettingsLoadingStatus;
    private final HashSet<String> hiddenMruItems;
    private int latestVersion;
    private String marketUrl;
    private int minRequiredOSVersion;
    private int minVersion;
    private int[] remoteControlSpecialTitleIds;
    private SmartglassSettings smartglassSettings;
    private final SingleEntryLoadingStatus smartglassSettingsLoadingStatus;
    private OnUpdateExistListener updateExistListener;

    private class GetFriendFinderSettingsRunner extends IDataLoaderRunnable<FriendFinderSettings> {
        private final SystemSettingsModel caller;

        public GetFriendFinderSettingsRunner(SystemSettingsModel systemSettingsModel) {
            this.caller = systemSettingsModel;
        }

        public FriendFinderSettings buildData() throws XLEException {
            return null;
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_SETTINGS;
        }

        public void onPostExcute(AsyncResult<FriendFinderSettings> asyncResult) {
            this.caller.onGetFriendFinderSettingsCompleted(asyncResult);
        }

        public void onPreExecute() {
        }
    }

    private class GetSmartglassSettingsRunner extends IDataLoaderRunnable<SmartglassSettings> {
        private final SystemSettingsModel caller;

        public GetSmartglassSettingsRunner(SystemSettingsModel systemSettingsModel) {
            this.caller = systemSettingsModel;
        }

        public SmartglassSettings buildData() throws XLEException {
            return null;
        }

        public long getDefaultErrorCode() {
            return XLEErrorCode.FAILED_TO_GET_SETTINGS;
        }

        public void onPostExcute(AsyncResult<SmartglassSettings> asyncResult) {
            this.caller.onGetSmartglassSettingsCompleted(asyncResult);
        }

        public void onPreExecute() {
        }
    }

    public interface OnUpdateExistListener {
        void onMustUpdate();

        void onOptionalUpdate();
    }

    private static class SystemSettingsModelContainer {
        private static SystemSettingsModel instance = new SystemSettingsModel();

        private SystemSettingsModelContainer() {
        }
    }

    private SystemSettingsModel() {
        this.minRequiredOSVersion = 0;
        this.minVersion = 0;
        this.latestVersion = 0;
        this.hiddenMruItems = new HashSet();
        this.smartglassSettingsLoadingStatus = new SingleEntryLoadingStatus();
        this.friendFinderSettingsLoadingStatus = new SingleEntryLoadingStatus();
    }

    public static SystemSettingsModel getInstance() {
        return SystemSettingsModelContainer.instance;
    }

    private int getMinimumVersion() {
        return this.minVersion;
    }

    private void onGetFriendFinderSettingsCompleted(AsyncResult<FriendFinderSettings> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.friendFinderSettings = (FriendFinderSettings) asyncResult.getResult();
            if (this.friendFinderSettings != null) {
                this.friendFinderSettings.getIconsFromJson(this.friendFinderSettings.ICONS);
            }
        }
    }

    private void onGetSmartglassSettingsCompleted(AsyncResult<SmartglassSettings> asyncResult) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (asyncResult.getStatus() == AsyncActionStatus.SUCCESS) {
            this.smartglassSettings = (SmartglassSettings) asyncResult.getResult();
            if (this.smartglassSettings != null) {
                this.minRequiredOSVersion = this.smartglassSettings.ANDROID_VERSIONMINOS;
                this.minVersion = this.smartglassSettings.ANDROID_VERSIONMIN;
                this.latestVersion = this.smartglassSettings.ANDROID_VERSIONLATEST;
                this.marketUrl = this.smartglassSettings.ANDROID_VERSIONURL;
                populateHiddenMruItems(this.smartglassSettings.HIDDEN_MRU_ITEMS);
                populateRemoteControlSpecialTitleIds(this.smartglassSettings.REMOTE_CONTROL_SPECIALS);
                if (this.updateExistListener == null) {
                    return;
                }
                if (getMustUpdate(ProjectSpecificDataProvider.getInstance().getVersionCode())) {
                    this.updateExistListener.onMustUpdate();
                } else if (getHasUpdate(ProjectSpecificDataProvider.getInstance().getVersionCode())) {
                    this.updateExistListener.onOptionalUpdate();
                }
            }
        }
    }

    private void populateHiddenMruItems(String str) {
        this.hiddenMruItems.clear();
        if (str != null) {
            String[] split = str.split(",");
            if (split != null) {
                for (Object add : split) {
                    this.hiddenMruItems.add(add);
                }
            }
        }
    }

    private void populateRemoteControlSpecialTitleIds(String str) {
        if (str != null) {
            String[] split = str.split(",");
            if (split != null) {
                this.remoteControlSpecialTitleIds = new int[split.length];
                int i = 0;
                for (String parseInt : split) {
                    int parseInt2;
                    try {
                        parseInt2 = Integer.parseInt(parseInt);
                    } catch (NumberFormatException e) {
                        parseInt2 = 0;
                    }
                    int i2 = i + 1;
                    this.remoteControlSpecialTitleIds[i] = parseInt2;
                    i = i2;
                }
            }
        }
    }

    public boolean getHasUpdate(int i) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return VERSION.SDK_INT >= this.minRequiredOSVersion && getLatestVersion() > i;
    }

    public int getLatestVersion() {
        return this.latestVersion;
    }

    public String getMarketUrl() {
        return this.marketUrl;
    }

    public boolean getMustUpdate(int i) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        return VERSION.SDK_INT >= this.minRequiredOSVersion && getMinimumVersion() > i;
    }

    public int[] getRemoteControlSpecialTitleIds() {
        return this.remoteControlSpecialTitleIds;
    }

    public boolean isInHiddenMruItems(String str) {
        return this.hiddenMruItems.contains(str);
    }

    public void loadAsync(boolean z) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        DataLoadUtil.StartLoadFromUI(z, this.lifetime, null, this.smartglassSettingsLoadingStatus, new GetSmartglassSettingsRunner(this));
        DataLoadUtil.StartLoadFromUI(z, this.lifetime, null, this.friendFinderSettingsLoadingStatus, new GetFriendFinderSettingsRunner(this));
    }

    public AsyncResult<FriendFinderSettings> loadFriendFinderSettings(boolean z) {
        return DataLoadUtil.Load(z, this.lifetime, null, this.friendFinderSettingsLoadingStatus, new GetFriendFinderSettingsRunner(this));
    }

    public AsyncResult<SmartglassSettings> loadSystemSettings(boolean z) {
        return DataLoadUtil.Load(z, this.lifetime, null, this.smartglassSettingsLoadingStatus, new GetSmartglassSettingsRunner(this));
    }

    public void setOnUpdateExistListener(OnUpdateExistListener onUpdateExistListener) {
        this.updateExistListener = onUpdateExistListener;
    }
}
