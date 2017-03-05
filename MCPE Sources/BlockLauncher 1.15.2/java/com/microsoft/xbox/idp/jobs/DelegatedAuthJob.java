package com.microsoft.xbox.idp.jobs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.microsoft.xbox.authenticate.DelegateRPSTicketResult;
import com.microsoft.xbox.authenticate.IDelegateKeyService;
import com.microsoft.xbox.authenticate.IDelegateKeyService.Stub;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry.CallBackSources;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.util.XboxAppLinker;

public class DelegatedAuthJob {
    private static final int RESULT_INVALID_APPURI = 6;
    private static final int RESULT_INVALID_PACKAGE = 4;
    private static final int RESULT_INVALID_SIGNATURE = 5;
    private static final int RESULT_NOCID = 1;
    private static final int RESULT_SUCCESS = 0;
    private static final int RESULT_UNEXPECTED = 2;
    private static final int RESULT_UNKNOWN_PACKAGE = 3;
    private static final String TAG = DelegatedAuthJob.class.getSimpleName();
    private static Intent launchIntent = null;
    private final String XBOX_BROKER_SERVICE_NAME = "com.microsoft.xbox.authenticate.DelegateKeyService";
    private final Callbacks callbacks;
    private final ServiceConnection connection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i(DelegatedAuthJob.TAG, "Service connected");
            DelegatedAuthJob.this.keyService = Stub.asInterface(iBinder);
            try {
                DelegateRPSTicketResult requestDelegateRPSTicketSilently = DelegatedAuthJob.this.keyService.requestDelegateRPSTicketSilently();
                if (XboxAppLinker.xboxAppIsInstalled(DelegatedAuthJob.this.context)) {
                    DelegatedAuthJob.launchIntent = XboxAppLinker.getXboxAppLaunchIntent(DelegatedAuthJob.this.context);
                    DelegatedAuthJob.launchIntent.setAction("com.microsoft.xbox.action.ACTION_SIGNIN");
                }
                int errorCode = requestDelegateRPSTicketSilently.getErrorCode();
                if (errorCode != 0) {
                    Log.i(DelegatedAuthJob.TAG, "Error getting RPS ticket");
                    if (errorCode == DelegatedAuthJob.RESULT_NOCID || errorCode == DelegatedAuthJob.RESULT_UNEXPECTED) {
                        DelegatedAuthJob.this.callbacks.onUiNeeded(DelegatedAuthJob.this);
                        return;
                    }
                    String str;
                    switch (errorCode) {
                        case DelegatedAuthJob.RESULT_UNKNOWN_PACKAGE /*3*/:
                            str = "RESULT_UNKNOWN_PACKAGE";
                            break;
                        case DelegatedAuthJob.RESULT_INVALID_PACKAGE /*4*/:
                            str = "RESULT_INVALID_PACKAGE";
                            break;
                        case DelegatedAuthJob.RESULT_INVALID_SIGNATURE /*5*/:
                            str = "RESULT_INVALID_SIGNATURE";
                            break;
                        case DelegatedAuthJob.RESULT_INVALID_APPURI /*6*/:
                            str = "RESULT_INVALID_APPURI";
                            break;
                        default:
                            str = "UNKNOWN_ERROR";
                            break;
                    }
                    UTCError.trackFailure(DelegatedAuthJob.TAG, true, CallBackSources.Ticket, new Exception(str));
                    DelegatedAuthJob.this.callbacks.onFailure(DelegatedAuthJob.this, new Exception(str));
                    return;
                }
                DelegatedAuthJob.this.callbacks.onTicketAcquired(DelegatedAuthJob.this, requestDelegateRPSTicketSilently.getTicket());
            } catch (Exception e) {
                Log.i(DelegatedAuthJob.TAG, "Callback failure");
                e.printStackTrace();
                DelegatedAuthJob.this.callbacks.onFailure(DelegatedAuthJob.this, e);
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            Log.i(DelegatedAuthJob.TAG, "Service disconnected");
        }
    };
    private final Context context;
    IDelegateKeyService keyService;
    private final String packageName;

    public interface Callbacks {
        void onFailure(DelegatedAuthJob delegatedAuthJob, Exception exception);

        void onTicketAcquired(DelegatedAuthJob delegatedAuthJob, String str);

        void onUiNeeded(DelegatedAuthJob delegatedAuthJob);
    }

    public DelegatedAuthJob(Context context, Callbacks callbacks) {
        this.context = context;
        this.callbacks = callbacks;
        this.packageName = context.getPackageName();
    }

    public static void clearXboxAppLaunchIntent() {
        launchIntent = null;
    }

    public static Intent getXboxAppLaunchIntent() {
        return launchIntent;
    }

    void launchXboxApp() {
        Log.i(TAG, "check service exists");
        UTCAdditionalInfoModel uTCAdditionalInfoModel;
        Intent intent;
        if (XboxAppLinker.isServiceInstalled(XboxAppLinker.XBOXAPP_BETA_PACKAGE, this.context, "com.microsoft.xbox.authenticate.DelegateKeyService")) {
            uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("launchType", "BETA");
            UTCPageAction.track("SignIn - DelegateRPSTicket", "DelegatedAuthJob", uTCAdditionalInfoModel);
            intent = new Intent();
            intent.setComponent(new ComponentName(XboxAppLinker.XBOXAPP_BETA_PACKAGE, "com.microsoft.xbox.authenticate.DelegateKeyService"));
            this.context.bindService(intent, this.connection, RESULT_NOCID);
        } else if (XboxAppLinker.isServiceInstalled(XboxAppLinker.XBOXAPP_PACKAGE, this.context, "com.microsoft.xbox.authenticate.DelegateKeyService")) {
            uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("launchType", "RETAIL");
            UTCPageAction.track("SignIn - DelegateRPSTicket", "DelegatedAuthJob", uTCAdditionalInfoModel);
            intent = new Intent();
            intent.setComponent(new ComponentName(XboxAppLinker.XBOXAPP_PACKAGE, "com.microsoft.xbox.authenticate.DelegateKeyService"));
            this.context.bindService(intent, this.connection, RESULT_NOCID);
        } else {
            uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("launchType", "STORE");
            UTCPageAction.track("SignIn - DelegateRPSTicket", "DelegatedAuthJob", uTCAdditionalInfoModel);
            launchIntent = XboxAppLinker.getXboxAppInOculusMarketIntent(this.context);
            this.callbacks.onUiNeeded(this);
        }
    }

    public DelegatedAuthJob start() {
        launchXboxApp();
        return this;
    }
}
