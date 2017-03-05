package com.microsoft.onlineid.internal.sso.client;

import android.content.Context;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.Resources;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.SsoService;
import com.microsoft.onlineid.internal.sso.client.request.RetrieveBackupRequest;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import java.util.List;

public class MigrationManager {
    public static final String InitialSdkVersion = "0";
    private final String _appSdkVersion;
    private final Context _applicationContext;
    private final ServiceFinder _serviceFinder;
    private List<SsoService> _ssoServices;
    private final TypedStorage _typedStorage;

    public MigrationManager(Context context) {
        this._applicationContext = context;
        this._typedStorage = new TypedStorage(context);
        this._serviceFinder = new ServiceFinder(context);
        this._appSdkVersion = Resources.getSdkVersion(context);
    }

    protected RetrieveBackupRequest createRetrieveBackupRequest(Context context) {
        return new RetrieveBackupRequest(context);
    }

    public void migrateAndUpgradeStorageIfNeeded() {
        String readSdkVersion = this._typedStorage.readSdkVersion();
        if (readSdkVersion == null) {
            this._typedStorage.writeSdkVersion(InitialSdkVersion);
            this._ssoServices = this._serviceFinder.getOrderedSsoServices();
            if (!this._ssoServices.isEmpty()) {
                migrateStorage();
            }
        }
        if (readSdkVersion == null || !readSdkVersion.equals(this._appSdkVersion)) {
            upgradeStorage(readSdkVersion, this._appSdkVersion);
            this._typedStorage.writeSdkVersion(this._appSdkVersion);
        }
    }

    protected void migrateStorage() {
        String packageName = this._applicationContext.getPackageName();
        int i = 0;
        for (SsoService ssoService : this._ssoServices) {
            String packageName2 = ssoService.getPackageName();
            if (!packageName2.equals(packageName)) {
                i++;
                try {
                    Bundle bundle = (Bundle) createRetrieveBackupRequest(this._applicationContext).performRequest(ssoService);
                    if (!bundle.isEmpty()) {
                        this._typedStorage.storeBackup(bundle);
                        Logger.info(packageName + " migrated backup data from " + packageName2);
                        break;
                    }
                } catch (Throwable e) {
                    Logger.error("Encountered an error attempting to migrate storage from " + packageName2, e);
                    ClientAnalytics.get().logException(e);
                }
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.MigrationCategory, ClientAnalytics.MigrationAttempts, String.valueOf(i));
    }

    protected void upgradeStorage(String str, String str2) {
    }
}
