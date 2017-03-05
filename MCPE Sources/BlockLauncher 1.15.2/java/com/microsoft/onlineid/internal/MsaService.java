package com.microsoft.onlineid.internal;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.exception.InternalException;
import com.microsoft.onlineid.internal.exception.PromptNeededException;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.profile.ProfileManager;
import com.microsoft.onlineid.internal.sso.client.BackupService;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import com.microsoft.onlineid.internal.sts.TicketManager;

public class MsaService extends IntentService {
    public static final String ActionGetTicket = "com.microsoft.onlineid.internal.GET_TICKET";
    public static final String ActionSignOut = "com.microsoft.onlineid.internal.SIGN_OUT";
    public static final String ActionSignOutAllApps = "com.microsoft.onlineid.internal.SIGN_OUT_ALL_APPS";
    public static final String ActionUpdateProfile = "com.microsoft.onlineid.internal.UPDATE_PROFILE";
    private ProfileManager _profileManager;
    private TicketManager _ticketManager;
    private TypedStorage _typedStorage;

    public MsaService() {
        super(MsaService.class.getName());
    }

    public void onCreate() {
        super.onCreate();
        Context applicationContext = getApplicationContext();
        this._profileManager = new ProfileManager(applicationContext);
        this._ticketManager = new TicketManager(applicationContext);
        this._typedStorage = new TypedStorage(applicationContext);
    }

    protected void onHandleIntent(Intent intent) {
        ApiRequest apiRequest = new ApiRequest(getApplicationContext(), intent);
        String action = intent.getAction();
        try {
            String accountPuid = apiRequest.getAccountPuid();
            if (ActionGetTicket.equals(action)) {
                apiRequest.sendSuccess(new ApiResult().setAccountPuid(accountPuid).addTicket(this._ticketManager.getTicket(accountPuid, apiRequest.getScope(), apiRequest.getClientPackageName(), apiRequest.getFlowToken(), false, apiRequest.getCobrandingId(), apiRequest.getIsWebFlowTelemetryRequested(), apiRequest.getClientStateBundle())));
            } else if (ActionUpdateProfile.equals(action)) {
                this._profileManager.updateProfile(apiRequest.getAccountPuid(), apiRequest.getFlowToken());
                apiRequest.sendSuccess(new ApiResult().setAccountPuid(accountPuid));
            } else if (ActionSignOut.equals(action)) {
                apiRequest.sendSuccess(new ApiResult());
            } else if (ActionSignOutAllApps.equals(action)) {
                this._typedStorage.removeAccount(accountPuid);
                BackupService.pushBackup(getApplicationContext());
                apiRequest.sendSuccess(new ApiResult());
            } else {
                throw new InternalException("Unknown action: " + action);
            }
        } catch (PromptNeededException e) {
            Logger.info("ApiRequest with action " + action + " requires UI to complete.");
            apiRequest.sendUINeeded(new PendingIntentBuilder(e.getRequest().setResultReceiver(apiRequest.getResultReceiver()).setIsSdkRequest(apiRequest.isSdkRequest()).setContinuation(apiRequest)).buildActivity());
        } catch (Throwable e2) {
            ClientAnalytics.get().logException(e2);
            Logger.error("ApiRequest with action " + action + " failed.", e2);
            apiRequest.sendFailure(e2);
        }
    }
}
