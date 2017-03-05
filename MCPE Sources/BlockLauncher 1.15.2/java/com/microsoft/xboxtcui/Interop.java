package com.microsoft.xboxtcui;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import com.microsoft.xbox.service.model.XPrivilegeConstants;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderHomeScreen;
import com.microsoft.xbox.xle.app.activity.Profile.ProfileScreen;
import com.microsoft.xboxtcui.XboxTcuiWindowDialog.DetachedCallback;
import java.lang.reflect.Field;
import java.util.Map;

public class Interop {
    private static final String TAG = Interop.class.getSimpleName();
    private static final DetachedCallback detachedCallback = new DetachedCallback() {
        public void onDetachedFromWindow() {
            Interop.tcui_completed_callback(0);
        }
    };

    public static void ShowAddFriends(Context context) {
        Log.i(TAG, "Deeplink - ShowAddFriends");
        if (XboxAppDeepLinker.showAddFriends(context)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    public static void ShowFriendFinder(Activity activity, String str, String str2) {
        Log.i(TAG, "TCUI- ShowFriendFinder - meXuid:" + str);
        Log.i(TAG, "TCUI- ShowFriendFinder: privileges:" + str2);
        if (str2.contains(XPrivilegeConstants.XPRIVILEGE_ADD_FRIEND)) {
            Activity foregroundActivity = getForegroundActivity();
            if (foregroundActivity != null) {
                activity = foregroundActivity;
            }
            final ActivityParameters activityParameters = new ActivityParameters();
            activityParameters.putMeXuid(str);
            activityParameters.putPrivileges(str2);
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        XboxTcuiWindowDialog xboxTcuiWindowDialog = new XboxTcuiWindowDialog(activity, FriendFinderHomeScreen.class, activityParameters);
                        xboxTcuiWindowDialog.setDetachedCallback(Interop.detachedCallback);
                        xboxTcuiWindowDialog.show();
                    } catch (Throwable e) {
                        Log.i(Interop.TAG, Log.getStackTraceString(e));
                        Interop.tcui_completed_callback(1);
                    }
                }
            });
            return;
        }
        tcui_completed_callback(1);
    }

    public static void ShowProfileCardUI(Activity activity, String str, String str2, String str3) {
        Log.i(TAG, "TCUI- ShowProfileCardUI: meXuid:" + str);
        Log.i(TAG, "TCUI- ShowProfileCardUI: targeProfileXuid:" + str2);
        Log.i(TAG, "TCUI- ShowProfileCardUI: privileges:" + str3);
        Activity foregroundActivity = getForegroundActivity();
        if (foregroundActivity == null) {
            foregroundActivity = activity;
        }
        final ActivityParameters activityParameters = new ActivityParameters();
        activityParameters.putMeXuid(str);
        activityParameters.putSelectedProfile(str2);
        activityParameters.putPrivileges(str3);
        activity.runOnUiThread(new Runnable() {
            public void run() {
                try {
                    XboxTcuiWindowDialog xboxTcuiWindowDialog = new XboxTcuiWindowDialog(foregroundActivity, ProfileScreen.class, activityParameters);
                    xboxTcuiWindowDialog.setDetachedCallback(Interop.detachedCallback);
                    xboxTcuiWindowDialog.show();
                } catch (Throwable e) {
                    Log.i(Interop.TAG, Log.getStackTraceString(e));
                    Interop.tcui_completed_callback(1);
                }
            }
        });
    }

    public static void ShowTitleAchievements(Context context, String str) {
        Log.i(TAG, "Deeplink - ShowTitleAchievements");
        if (XboxAppDeepLinker.showTitleAchievements(context, str)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    public static void ShowTitleHub(Context context, String str) {
        Log.i(TAG, "Deeplink - ShowTitleHub");
        if (XboxAppDeepLinker.showTitleHub(context, str)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    public static void ShowUserProfile(Context context, String str) {
        Log.i(TAG, "Deeplink - ShowUserProfile");
        if (XboxAppDeepLinker.showUserProfile(context, str)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    public static void ShowUserSettings(Context context) {
        Log.i(TAG, "Deeplink - ShowUserSettings");
        if (XboxAppDeepLinker.showUserSettings(context)) {
            tcui_completed_callback(0);
        } else {
            tcui_completed_callback(1);
        }
    }

    private static Activity getForegroundActivity() {
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Object invoke = cls.getMethod("currentActivityThread", new Class[0]).invoke(null, new Object[0]);
            Field declaredField = cls.getDeclaredField("mActivities");
            declaredField.setAccessible(true);
            for (Object invoke2 : ((Map) declaredField.get(invoke2)).values()) {
                Class cls2 = invoke2.getClass();
                Field declaredField2 = cls2.getDeclaredField("paused");
                declaredField2.setAccessible(true);
                if (!declaredField2.getBoolean(invoke2)) {
                    declaredField = cls2.getDeclaredField("activity");
                    declaredField.setAccessible(true);
                    return (Activity) declaredField.get(invoke2);
                }
            }
        } catch (Throwable e) {
            Log.i(TAG, Log.getStackTraceString(e));
        }
        return null;
    }

    private static native void tcui_completed_callback(int i);
}
