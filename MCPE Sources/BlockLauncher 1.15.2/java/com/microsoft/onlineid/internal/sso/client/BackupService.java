package com.microsoft.onlineid.internal.sso.client;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.SsoService;
import com.microsoft.onlineid.internal.sso.client.request.RetrieveBackupRequest;
import com.microsoft.onlineid.internal.sso.client.request.StoreBackupRequest;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Int;
import java.util.List;

public class BackupService extends IntentService {
    public static final String ActionPushBackup = "com.microsoft.onlineid.internal.sso.client.PUSH_BACKUP";
    public static final String ActionPushBackupIfNeeded = "com.microsoft.onlineid.internal.sso.client.PUSH_BACKUP_IF_NEEDED";
    private Context _applicationContext;
    private ServerConfig _config;
    private MsaSsoClient _msaSsoClient;
    private ServiceFinder _serviceFinder;
    private TypedStorage _storage;

    public BackupService() {
        super(BackupService.class.getName());
    }

    public static void pushBackup(Context context) {
        context.startService(new Intent(context, BackupService.class).setAction(ActionPushBackup));
    }

    public static void pushBackupIfNeeded(Context context) {
        context.startService(new Intent(context, BackupService.class).setAction(ActionPushBackupIfNeeded));
    }

    public void onCreate() {
        super.onCreate();
        this._applicationContext = getApplicationContext();
        this._config = new ServerConfig(this._applicationContext);
        this._storage = new TypedStorage(this._applicationContext);
        this._msaSsoClient = new MsaSsoClient(this._applicationContext);
        this._serviceFinder = new ServiceFinder(this._applicationContext);
    }

    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(ActionPushBackup)) {
            retrieveAndPushBackup();
        } else if (action.equals(ActionPushBackupIfNeeded)) {
            retrieveAndPushBackupIfNeeded();
        } else {
            Throwable internalException = new InternalException("Unknown action: " + action);
            ClientAnalytics.get().logException(internalException);
            Logger.error("Backup failed.", internalException);
        }
    }

    protected void retrieveAndPushBackup() {
        List orderedSsoServices = this._serviceFinder.getOrderedSsoServices();
        if (orderedSsoServices.size() > 0 && this._applicationContext.getPackageName().equals(((SsoService) orderedSsoServices.get(0)).getPackageName())) {
            Bundle retrieveBackup = retrieveBackup((SsoService) orderedSsoServices.get(0));
            if (retrieveBackup != null && !retrieveBackup.isEmpty()) {
                int min = Math.min(orderedSsoServices.size() - 1, this._config.getInt(Int.BackupSlaveCount));
                for (int i = 1; i <= min; i++) {
                    storeBackup((SsoService) orderedSsoServices.get(i), retrieveBackup);
                }
                this._storage.writeLastBackupPushedTime();
            }
        }
    }

    protected void retrieveAndPushBackupIfNeeded() {
        if ((System.currentTimeMillis() - this._storage.readLastBackupPushedTime()) / 1000 >= ((long) this._config.getInt(Int.MaxSecondsBetweenBackups))) {
            retrieveAndPushBackup();
        }
    }

    protected Bundle retrieveBackup(SsoService ssoService) {
        try {
            return (Bundle) this._msaSsoClient.performRequestWithFallback(new RetrieveBackupRequest(this._applicationContext));
        } catch (Throwable e) {
            Logger.error("Retrieve backup failed.", e);
            ClientAnalytics.get().logException(e);
            return null;
        }
    }

    protected void storeBackup(SsoService ssoService, Bundle bundle) {
        try {
            new StoreBackupRequest(this._applicationContext, bundle).performRequest(ssoService);
        } catch (Throwable e) {
            Logger.error("Store backup failed.", e);
            ClientAnalytics.get().logException(e);
        }
    }
}
