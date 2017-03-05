package net.zhuoweizhang.mcpelauncher;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

public class ThemeLifecycleCallbacks implements ActivityLifecycleCallbacks {
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Utils.setupTheme(activity, false);
    }

    public void onActivityDestroyed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
    }
}
