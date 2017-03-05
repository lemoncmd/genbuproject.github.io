package net.hockeyapp.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import com.ipaulpro.afilechooser.utils.MimeTypeParser;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import net.hockeyapp.android.tasks.LoginTask;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.Util;

public class LoginManager {
    static final String LOGIN_EXIT_KEY = "net.hockeyapp.android.EXIT";
    public static final int LOGIN_MODE_ANONYMOUS = 0;
    public static final int LOGIN_MODE_EMAIL_ONLY = 1;
    public static final int LOGIN_MODE_EMAIL_PASSWORD = 2;
    public static final int LOGIN_MODE_VALIDATE = 3;
    private static String identifier = null;
    static LoginManagerListener listener;
    static Class<?> mainActivity;
    private static int mode;
    private static String secret = null;
    private static String urlString = null;
    private static Handler validateHandler = null;

    private static class LoginHandler extends Handler {
        private final WeakReference<Context> mWeakContext;

        public LoginHandler(Context context) {
            this.mWeakContext = new WeakReference(context);
        }

        public void handleMessage(Message message) {
            boolean z = message.getData().getBoolean(LoginTask.BUNDLE_SUCCESS);
            Context context = (Context) this.mWeakContext.get();
            if (context != null) {
                if (z) {
                    HockeyLog.verbose("HockeyAuth", "We authenticated or verified successfully");
                } else {
                    LoginManager.startLoginActivity(context);
                }
            }
        }
    }

    private static String getURLString(int i) {
        String str = BuildConfig.FLAVOR;
        if (i == LOGIN_MODE_EMAIL_PASSWORD) {
            str = "authorize";
        } else if (i == LOGIN_MODE_EMAIL_ONLY) {
            str = "check";
        } else if (i == LOGIN_MODE_VALIDATE) {
            str = "validate";
        }
        return urlString + "api/3/apps/" + identifier + "/identity/" + str;
    }

    public static void register(Context context, String str, int i) {
        String appIdentifier = Util.getAppIdentifier(context);
        if (TextUtils.isEmpty(appIdentifier)) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(context, appIdentifier, str, i, (Class) null);
    }

    public static void register(Context context, String str, String str2, int i, Class<?> cls) {
        register(context, str, str2, Constants.BASE_URL, i, cls);
    }

    public static void register(Context context, String str, String str2, int i, LoginManagerListener loginManagerListener) {
        listener = loginManagerListener;
        register(context, str, str2, i, (Class) null);
    }

    public static void register(Context context, String str, String str2, String str3, int i, Class<?> cls) {
        if (context != null) {
            identifier = Util.sanitizeAppIdentifier(str);
            secret = str2;
            urlString = str3;
            mode = i;
            mainActivity = cls;
            if (validateHandler == null) {
                validateHandler = new LoginHandler(context);
            }
            Constants.loadFromContext(context);
        }
    }

    private static void startLoginActivity(Context context) {
        Intent intent = new Intent();
        int i = Boolean.valueOf(mode == LOGIN_MODE_VALIDATE).booleanValue() ? LOGIN_MODE_EMAIL_PASSWORD : mode;
        intent.setFlags(1342177280);
        intent.setClass(context, LoginActivity.class);
        intent.putExtra(UpdateFragment.FRAGMENT_URL, getURLString(i));
        intent.putExtra(LoginActivity.EXTRA_MODE, i);
        intent.putExtra(LoginActivity.EXTRA_SECRET, secret);
        context.startActivity(intent);
    }

    public static void verifyLogin(Activity activity, Intent intent) {
        boolean z = true;
        if (intent != null && intent.getBooleanExtra(LOGIN_EXIT_KEY, false)) {
            activity.finish();
        } else if (activity != null && mode != 0) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences("net.hockeyapp.android.login", LOGIN_MODE_ANONYMOUS);
            if (sharedPreferences.getInt(LoginActivity.EXTRA_MODE, -1) != mode) {
                HockeyLog.verbose("HockeyAuth", "Mode has changed, require re-auth.");
                sharedPreferences.edit().remove("auid").remove("iuid").putInt(LoginActivity.EXTRA_MODE, mode).apply();
            }
            String string = sharedPreferences.getString("auid", null);
            String string2 = sharedPreferences.getString("iuid", null);
            boolean z2 = (string == null && string2 == null) ? LOGIN_MODE_EMAIL_ONLY : false;
            boolean z3 = (string == null && (mode == LOGIN_MODE_EMAIL_PASSWORD || mode == LOGIN_MODE_VALIDATE)) ? LOGIN_MODE_EMAIL_ONLY : false;
            if (!(string2 == null && mode == LOGIN_MODE_EMAIL_ONLY)) {
                z = false;
            }
            if (z2 || z3 || r0) {
                HockeyLog.verbose("HockeyAuth", "Not authenticated or correct ID missing, re-authenticate.");
                startLoginActivity(activity);
            } else if (mode == LOGIN_MODE_VALIDATE) {
                HockeyLog.verbose("HockeyAuth", "LOGIN_MODE_VALIDATE, Validate the user's info!");
                Map hashMap = new HashMap();
                if (string != null) {
                    hashMap.put(MimeTypeParser.TAG_TYPE, "auid");
                    hashMap.put(Name.MARK, string);
                } else if (string2 != null) {
                    hashMap.put(MimeTypeParser.TAG_TYPE, "iuid");
                    hashMap.put(Name.MARK, string2);
                }
                AsyncTask loginTask = new LoginTask(activity, validateHandler, getURLString(LOGIN_MODE_VALIDATE), LOGIN_MODE_VALIDATE, hashMap);
                loginTask.setShowProgressDialog(false);
                AsyncTaskUtils.execute(loginTask);
            }
        }
    }
}
