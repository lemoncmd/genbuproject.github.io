package net.zhuoweizhang.mcpelauncher;

import android.app.Application;
import android.os.Build.VERSION;

public class BlockLauncher extends Application {
    public void onCreate() {
        Utils.setContext(getApplicationContext());
        super.onCreate();
        if (VERSION.SDK_INT >= 14) {
            registerActivityLifecycleCallbacks(new ThemeLifecycleCallbacks());
        }
    }
}
