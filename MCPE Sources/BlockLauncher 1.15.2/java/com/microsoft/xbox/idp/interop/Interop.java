package com.microsoft.xbox.idp.interop;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.microsoft.cll.android.AndroidCll;
import com.microsoft.cll.android.EventEnums;
import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import com.microsoft.cll.android.ITicketCallback;
import com.microsoft.cll.android.Verbosity;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.UserAccount;
import com.microsoft.xbox.idp.jobs.DelegatedAuthJob;
import com.microsoft.xbox.idp.jobs.DelegatedAuthJob.Callbacks;
import com.microsoft.xbox.idp.jobs.JobSilentSignIn;
import com.microsoft.xbox.idp.jobs.MSAJob;
import com.microsoft.xbox.idp.model.gcm.RegistrationIntentService;
import com.microsoft.xbox.idp.services.Config;
import com.microsoft.xbox.idp.services.Endpoints.Type;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.ui.AuthFlowActivity;
import com.microsoft.xbox.idp.ui.AuthFlowActivity.StaticCallbacks;
import com.microsoft.xbox.idp.ui.MSAFragment;
import com.microsoft.xbox.idp.util.CacheUtil;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import org.mozilla.javascript.regexp.NativeRegExp;

public class Interop {
    private static final String DNET_SCOPE = "user.auth.dnet.xboxlive.com";
    private static final String PACKAGE_NAME_TO_REMOVE = "com.microsoft.onlineid.sample";
    private static final String POLICY = "mbi_ssl";
    private static final String PROD_SCOPE = "user.auth.xboxlive.com";
    private static final String TAG = Interop.class.getSimpleName();
    private static final Callbacks brokeredSignInCallbacks = new Callbacks() {
        public void onFailure(DelegatedAuthJob delegatedAuthJob, Exception exception) {
            Log.d(Interop.TAG, "DelegatedAuthJob Failure");
            Interop.MSACallback(BuildConfig.FLAVOR, 0, MSAError.OTHER.id, "There was a problem acquiring an account: " + exception);
        }

        public void onTicketAcquired(DelegatedAuthJob delegatedAuthJob, String str) {
            Log.d(Interop.TAG, "Ticket Acquired");
            Interop.MSACallback(str, 0, MSAError.NONE.id, "Got ticket");
        }

        public void onUiNeeded(DelegatedAuthJob delegatedAuthJob) {
            Log.d(Interop.TAG, "DelegatedAuthJob UI Needed");
            Interop.MSACallback(BuildConfig.FLAVOR, 0, MSAError.UI_INTERACTION_REQUIRED.id, "Must show UI to acquire an account.");
        }
    };
    private static CllWrapper s_cll = null;
    private static final MSAJob.Callbacks silentSignInCallbacks = new MSAJob.Callbacks() {
        public void onAccountAcquired(MSAJob mSAJob, UserAccount userAccount) {
            Log.d(Interop.TAG, "Java - Ticket Acquired");
        }

        public void onFailure(MSAJob mSAJob, Exception exception) {
            Log.d(Interop.TAG, "Java - onFailure");
            Interop.MSACallback(BuildConfig.FLAVOR, 0, MSAError.OTHER.id, "There was a problem acquiring an account: " + exception);
        }

        public void onSignedOut(MSAJob mSAJob) {
            Log.d(Interop.TAG, "Java - onSignedOut");
            Interop.MSACallback(BuildConfig.FLAVOR, 0, MSAError.OTHER.id, "Signed out during silent sign in - should not be here");
        }

        public void onTicketAcquired(MSAJob mSAJob, Ticket ticket) {
            Log.d(Interop.TAG, "Java - Ticket Acquired");
            Interop.MSACallback(ticket.getValue(), 0, MSAError.NONE.id, "Got ticket");
        }

        public void onUiNeeded(MSAJob mSAJob) {
            Log.d(Interop.TAG, "Java - onUiNeeded");
            Interop.MSACallback(BuildConfig.FLAVOR, 0, MSAError.UI_INTERACTION_REQUIRED.id, "Must show UI to acquire an account.");
        }

        public void onUserCancel(MSAJob mSAJob) {
            Log.d(Interop.TAG, "Java - onUserCancel");
            Interop.MSACallback(BuildConfig.FLAVOR, 0, MSAError.USER_CANCEL.id, "The user cancelled the UI to acquire a ticket.");
        }
    };

    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$idp$interop$Interop$MSAPurpose = new int[MSAPurpose.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$idp$interop$Interop$MSAPurpose[MSAPurpose.OPPORTUNISTIC_SIGN_IN.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$idp$interop$Interop$MSAPurpose[MSAPurpose.SIGN_OUT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum AuthFlowScreenStatus {
        NO_ERROR(0),
        ERROR_USER_CANCEL(1),
        PROVIDER_ERROR(2);
        
        private final int id;

        private AuthFlowScreenStatus(int i) {
            this.id = i;
        }

        public int getId() {
            return this.id;
        }
    }

    public interface ErrorCallback {
        void onError(int i, int i2, String str);
    }

    public interface Callback extends ErrorCallback {
        void onXTokenAcquired(long j);
    }

    private static class CllWrapper {
        private final Context appContext;
        private final AndroidCll cll;

        public CllWrapper(AndroidCll androidCll, Context context) {
            this.cll = androidCll;
            this.appContext = context;
        }

        public Context getAppContext() {
            return this.appContext;
        }

        public AndroidCll getCll() {
            return this.cll;
        }
    }

    public enum ErrorStatus {
        TRY_AGAIN(0),
        CLOSE(1);
        
        private final int id;

        private ErrorStatus(int i) {
            this.id = i;
        }

        public int getId() {
            return this.id;
        }
    }

    public enum ErrorType {
        BAN(0),
        CREATION(1),
        OFFLINE(2),
        CATCHALL(3);
        
        private final int id;

        private ErrorType(int i) {
            this.id = i;
        }

        public int getId() {
            return this.id;
        }
    }

    public interface EventInitializationCallback extends ErrorCallback {
        void onSuccess();
    }

    public enum MSAError {
        NONE(0),
        UI_INTERACTION_REQUIRED(1),
        USER_CANCEL(2),
        OTHER(3);
        
        public final int id;

        private MSAError(int i) {
            this.id = i;
        }
    }

    public enum MSAPurpose {
        NONE(0),
        OPPORTUNISTIC_SIGN_IN(1),
        EXPLICIT_SIGN_IN(2),
        REACQUIRE_PREVIOUS_ACCOUNT(3),
        GET_TICKET(4),
        GET_VORTEX_TICKET(5),
        SIGN_OUT(6);
        
        public final int id;

        private MSAPurpose(int i) {
            this.id = i;
        }

        public static MSAPurpose fromId(int i) {
            MSAPurpose[] values = values();
            return (i < 0 || values.length <= i) ? null : values[i];
        }
    }

    public interface XBLoginCallback extends ErrorCallback {
        void onLogin(long j, boolean z);
    }

    public interface XBLogoutCallback {
        void onLoggedOut();
    }

    public static void ClearIntent() {
        DelegatedAuthJob.clearXboxAppLaunchIntent();
    }

    public static String GetLiveXTokenCallback(boolean z) {
        return get_uploader_x_token_callback(z);
    }

    public static String GetLocalStoragePath(Context context) {
        return context.getFilesDir().getPath();
    }

    public static String GetXTokenCallback(String str) {
        return get_supporting_x_token_callback(str);
    }

    public static void InitCLL(Context context, String str) {
        if (s_cll == null) {
            Log.i("XSAPI.Android", "Init CLL");
            s_cll = new CllWrapper(new AndroidCll(str, context), context.getApplicationContext());
            ITicketCallback cLLCallback = new CLLCallback(context, null);
            AndroidCll cll = s_cll.getCll();
            cll.setXuidCallback(cLLCallback);
            cll.setDebugVerbosity(Verbosity.INFO);
            cll.start();
        }
    }

    public static void InvokeAuthFlow(long j, Activity activity, boolean z) {
        Log.d(TAG, "InvokeAuthFlow");
        if (!z) {
            Config.endpointType = Type.DNET;
        }
        AuthFlowActivity.setStaticCallbacks(new StaticCallbacks() {
            public void onAuthFlowFinished(long j, AuthFlowScreenStatus authFlowScreenStatus, String str) {
                AuthFlowActivity.setStaticCallbacks(null);
                Log.d(Interop.TAG, "onAuthFlowFinished: " + authFlowScreenStatus);
                CacheUtil.clearCaches();
                Interop.auth_flow_callback(j, authFlowScreenStatus.getId(), str);
            }
        });
        Intent intent = new Intent(activity, AuthFlowActivity.class);
        intent.putExtra(MSAFragment.ARG_SECURITY_SCOPE, z ? PROD_SCOPE : DNET_SCOPE);
        intent.putExtra(MSAFragment.ARG_SECURITY_POLICY, POLICY);
        intent.putExtra(AuthFlowActivity.ARG_USER_PTR, j);
        activity.startActivity(intent);
    }

    public static void InvokeBrokeredMSA(Context context, boolean z) {
        Log.d(TAG, "InvokeAuthFlow");
        if (!z) {
            Config.endpointType = Type.DNET;
        }
        new DelegatedAuthJob(context, brokeredSignInCallbacks).start();
    }

    public static void InvokeEventInitialization(long j, String str, EventInitializationCallback eventInitializationCallback) {
        Log.d(TAG, "InvokeEventInitialization");
        invoke_event_initialization(j, str, eventInitializationCallback);
    }

    public static void InvokeLatestIntent(Activity activity, Object obj) {
        Log.i(TAG, "InvokeLatestIntent");
        Intent xboxAppLaunchIntent = DelegatedAuthJob.getXboxAppLaunchIntent();
        if (xboxAppLaunchIntent == null) {
            Log.d(TAG, "Xbox App launch intent was null");
        } else if (obj instanceof Intent) {
            xboxAppLaunchIntent.putExtra("com.microsoft.xbox.extra.RELAUNCH_INTENT", (Intent) obj);
            Log.d(TAG, "Invoking the launch intent...");
            String str = xboxAppLaunchIntent.getPackage();
            String action = xboxAppLaunchIntent.getAction();
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("packageName", str);
            uTCAdditionalInfoModel.addValue("action", action);
            UTCPageAction.track("DeepLink - GearVR SignIn", "Minecraft GearVR SignIn", uTCAdditionalInfoModel);
            activity.startActivity(xboxAppLaunchIntent);
        } else {
            Log.d(TAG, "Minecraft relaunch intent was null");
        }
    }

    public static void InvokeMSA(Context context, int i, boolean z, String str) {
        Log.i("XSAPI.Android", "Invoking MSA");
        if (!z) {
            Config.endpointType = Type.DNET;
        }
        MSAPurpose fromId = MSAPurpose.fromId(i);
        if (fromId != null) {
            switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$idp$interop$Interop$MSAPurpose[fromId.ordinal()]) {
                case NativeRegExp.MATCH /*1*/:
                    Log.i(TAG, "InvokeMSA OPPORTUNISTIC_SIGN_IN cid: " + str);
                    if (TextUtils.isEmpty(str)) {
                        MSACallback(BuildConfig.FLAVOR, i, MSAError.UI_INTERACTION_REQUIRED.id, "Must show UI to acquire an account.");
                        return;
                    } else {
                        new JobSilentSignIn(context, null, silentSignInCallbacks, z ? PROD_SCOPE : DNET_SCOPE, POLICY, str).start();
                        return;
                    }
                case NativeRegExp.PREFIX /*2*/:
                    Log.i(TAG, "InvokeMSA SIGN_OUT");
                    CacheUtil.clearCaches();
                    sign_out_callback();
                    return;
                default:
                    MSACallback(BuildConfig.FLAVOR, i, MSAError.OTHER.id, "Invalid requestCode: " + i);
                    return;
            }
        }
        MSACallback(BuildConfig.FLAVOR, i, MSAError.OTHER.id, "Invalid requestCode: " + i);
    }

    public static void InvokeXBLogin(long j, String str, XBLoginCallback xBLoginCallback) {
        Log.d(TAG, "InvokeXBLogin");
        invoke_xb_login(j, str, xBLoginCallback);
    }

    public static void InvokeXBLogout(long j, XBLogoutCallback xBLogoutCallback) {
        Log.d(TAG, "InvokeSignOut");
        invoke_xb_logout(j, xBLogoutCallback);
    }

    public static void InvokeXTokenCallback(long j, Callback callback) {
        Log.i(TAG, "InvokeXTokenCallback");
        invoke_x_token_acquisition(j, callback);
    }

    public static void LogCLL(String str, String str2, String str3) {
        Log.i("XSAPI.Android", "Log CLL");
        List arrayList = new ArrayList();
        arrayList.add(str);
        if (s_cll == null) {
            Log.i("XSAPI.Android", "Log CLL null");
            return;
        }
        s_cll.getCll().log(str2, str3, Latency.LatencyRealtime, Persistence.PersistenceCritical, EnumSet.of(Sensitivity.SensitivityNone), EventEnums.SampleRate_NoSampling, arrayList);
    }

    public static void LogTelemetrySignIn(String str, String str2) {
        Log.i("XSAPI.Android", "LogTelemetrySignIn");
        UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
        CharSequence charSequence = str + str2;
        UTCPageAction.track(charSequence, charSequence, uTCAdditionalInfoModel);
    }

    public static void MSACallback(String str, int i, int i2, String str2) {
        Log.i(TAG, "MSA Callback");
        ticket_callback(str, i, i2, str2);
    }

    public static void NotificationRegisterCallback(String str, boolean z) {
        Log.i(TAG, "callback");
        try {
            notificiation_registration_callback(str, z);
        } catch (UnsatisfiedLinkError e) {
            Log.i(TAG, "Token refreshed while process was not running");
        }
    }

    public static String ReadConfigFile(Context context) {
        InputStream openRawResource = context.getResources().openRawResource(context.getResources().getIdentifier("xboxservices", "raw", context.getPackageName()));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[EnchantType.pickaxe];
        while (true) {
            try {
                int read = openRawResource.read(bArr);
                if (read == -1) {
                    break;
                }
                byteArrayOutputStream.write(bArr, 0, read);
            } catch (IOException e) {
            }
        }
        byteArrayOutputStream.close();
        openRawResource.close();
        return byteArrayOutputStream.toString();
    }

    public static void RegisterWithGNS(Context context) {
        Log.i(TAG, "trying to register..");
        context.startService(new Intent(context, RegistrationIntentService.class));
    }

    private static native void auth_flow_callback(long j, int i, String str);

    public static native boolean deinitializeInterop();

    public static Context getApplicationContext() {
        return s_cll == null ? null : s_cll.getAppContext();
    }

    public static AndroidCll getCll() {
        return s_cll.getCll();
    }

    public static String getLocale() {
        String locale = Locale.getDefault().toString();
        Log.i(TAG, "locale is: " + locale);
        return locale;
    }

    public static String getSystemProxy() {
        String property = System.getProperty("http.proxyHost");
        if (property != null) {
            String property2 = System.getProperty("http.proxyPort");
            if (property2 != null) {
                property = "http://" + property + ":" + property2;
                Log.i(TAG, property);
                return property;
            }
        }
        return BuildConfig.FLAVOR;
    }

    public static String getTitleDeviceId() {
        return get_title_telemetry_device_id();
    }

    public static String getTitleSessionId() {
        return get_title_telemetry_session_id();
    }

    private static native String get_supporting_x_token_callback(String str);

    private static native String get_title_telemetry_device_id();

    private static native String get_title_telemetry_session_id();

    private static native String get_uploader_x_token_callback(boolean z);

    public static native boolean initializeInterop(Context context);

    private static native void invoke_event_initialization(long j, String str, EventInitializationCallback eventInitializationCallback);

    private static native void invoke_x_token_acquisition(long j, Callback callback);

    private static native void invoke_xb_login(long j, String str, XBLoginCallback xBLoginCallback);

    private static native void invoke_xb_logout(long j, XBLogoutCallback xBLogoutCallback);

    private static native void notificiation_registration_callback(String str, boolean z);

    private static native void sign_out_callback();

    private static native void ticket_callback(String str, int i, int i2, String str2);
}
