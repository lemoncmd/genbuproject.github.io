package net.zhuoweizhang.mcpelauncher;

import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class RedirectPackageManager extends WrappedPackageManager {
    protected String nativeLibraryDir;

    public RedirectPackageManager(PackageManager mgr, String nativeLibraryDir) {
        super(mgr);
        this.nativeLibraryDir = nativeLibraryDir;
    }

    public ActivityInfo getActivityInfo(ComponentName className, int flags) throws NameNotFoundException {
        ActivityInfo retval = this.wrapped.getActivityInfo(className, flags);
        retval.applicationInfo.nativeLibraryDir = this.nativeLibraryDir;
        return retval;
    }
}
