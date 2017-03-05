package net.hockeyapp.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import java.util.Date;
import net.hockeyapp.android.tasks.CheckUpdateTask;
import net.hockeyapp.android.tasks.CheckUpdateTaskWithUI;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.Util;

public class UpdateManager {
    public static final String INSTALLER_ADB = "adb";
    private static UpdateManagerListener lastListener = null;
    private static CheckUpdateTask updateTask = null;

    private static boolean checkExpiryDate(WeakReference<Activity> weakReference, UpdateManagerListener updateManagerListener) {
        boolean z = false;
        boolean checkExpiryDateForBackground = checkExpiryDateForBackground(updateManagerListener);
        if (checkExpiryDateForBackground) {
            z = updateManagerListener.onBuildExpired();
        }
        if (checkExpiryDateForBackground && r0) {
            startExpiryInfoIntent(weakReference);
        }
        return checkExpiryDateForBackground;
    }

    private static boolean checkExpiryDateForBackground(UpdateManagerListener updateManagerListener) {
        if (updateManagerListener == null) {
            return false;
        }
        Date expiryDate = updateManagerListener.getExpiryDate();
        return expiryDate != null && new Date().compareTo(expiryDate) > 0;
    }

    @TargetApi(11)
    private static boolean dialogShown(WeakReference<Activity> weakReference) {
        if (weakReference != null) {
            Activity activity = (Activity) weakReference.get();
            if (!(activity == null || activity.getFragmentManager().findFragmentByTag("hockey_update_dialog") == null)) {
                return true;
            }
        }
        return false;
    }

    public static UpdateManagerListener getLastListener() {
        return lastListener;
    }

    private static boolean installedFromMarket(WeakReference<? extends Context> weakReference) {
        Context context = (Context) weakReference.get();
        if (context == null) {
            return false;
        }
        try {
            CharSequence installerPackageName = context.getPackageManager().getInstallerPackageName(context.getPackageName());
            return (TextUtils.isEmpty(installerPackageName) && (installerPackageName == null || TextUtils.equals(installerPackageName, INSTALLER_ADB))) ? false : true;
        } catch (Throwable th) {
            return false;
        }
    }

    public static void register(Activity activity) {
        Object appIdentifier = Util.getAppIdentifier(activity);
        if (TextUtils.isEmpty(appIdentifier)) {
            throw new IllegalArgumentException("HockeyApp app identifier was not configured correctly in manifest or build configuration.");
        }
        register(activity, appIdentifier);
    }

    public static void register(Activity activity, String str) {
        register(activity, str, true);
    }

    public static void register(Activity activity, String str, String str2, UpdateManagerListener updateManagerListener) {
        register(activity, str, str2, updateManagerListener, true);
    }

    public static void register(Activity activity, String str, String str2, UpdateManagerListener updateManagerListener, boolean z) {
        String sanitizeAppIdentifier = Util.sanitizeAppIdentifier(str2);
        lastListener = updateManagerListener;
        WeakReference weakReference = new WeakReference(activity);
        if ((!Util.fragmentsSupported().booleanValue() || !dialogShown(weakReference)) && !checkExpiryDate(weakReference, updateManagerListener)) {
            if ((updateManagerListener != null && updateManagerListener.canUpdateInMarket()) || !installedFromMarket(weakReference)) {
                startUpdateTask(weakReference, str, sanitizeAppIdentifier, updateManagerListener, z);
            }
        }
    }

    public static void register(Activity activity, String str, UpdateManagerListener updateManagerListener) {
        register(activity, Constants.BASE_URL, str, updateManagerListener, true);
    }

    public static void register(Activity activity, String str, UpdateManagerListener updateManagerListener, boolean z) {
        register(activity, Constants.BASE_URL, str, updateManagerListener, z);
    }

    public static void register(Activity activity, String str, boolean z) {
        register(activity, str, null, z);
    }

    public static void registerForBackground(Context context, String str, String str2, UpdateManagerListener updateManagerListener) {
        String sanitizeAppIdentifier = Util.sanitizeAppIdentifier(str2);
        lastListener = updateManagerListener;
        WeakReference weakReference = new WeakReference(context);
        if (!checkExpiryDateForBackground(updateManagerListener)) {
            if ((updateManagerListener != null && updateManagerListener.canUpdateInMarket()) || !installedFromMarket(weakReference)) {
                startUpdateTaskForBackground(weakReference, str, sanitizeAppIdentifier, updateManagerListener);
            }
        }
    }

    public static void registerForBackground(Context context, String str, UpdateManagerListener updateManagerListener) {
        registerForBackground(context, Constants.BASE_URL, str, updateManagerListener);
    }

    private static void startExpiryInfoIntent(WeakReference<Activity> weakReference) {
        if (weakReference != null) {
            Activity activity = (Activity) weakReference.get();
            if (activity != null) {
                activity.finish();
                Intent intent = new Intent(activity, ExpiryInfoActivity.class);
                intent.addFlags(335544320);
                activity.startActivity(intent);
            }
        }
    }

    private static void startUpdateTask(WeakReference<Activity> weakReference, String str, String str2, UpdateManagerListener updateManagerListener, boolean z) {
        if (updateTask == null || updateTask.getStatus() == Status.FINISHED) {
            updateTask = new CheckUpdateTaskWithUI(weakReference, str, str2, updateManagerListener, z);
            AsyncTaskUtils.execute(updateTask);
            return;
        }
        updateTask.attach(weakReference);
    }

    private static void startUpdateTaskForBackground(WeakReference<Context> weakReference, String str, String str2, UpdateManagerListener updateManagerListener) {
        if (updateTask == null || updateTask.getStatus() == Status.FINISHED) {
            updateTask = new CheckUpdateTask(weakReference, str, str2, updateManagerListener);
            AsyncTaskUtils.execute(updateTask);
            return;
        }
        updateTask.attach(weakReference);
    }

    public static void unregister() {
        if (updateTask != null) {
            updateTask.cancel(true);
            updateTask.detach();
            updateTask = null;
        }
        lastListener = null;
    }
}
