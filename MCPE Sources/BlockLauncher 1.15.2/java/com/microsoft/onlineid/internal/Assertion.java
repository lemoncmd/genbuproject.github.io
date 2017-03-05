package com.microsoft.onlineid.internal;

import com.microsoft.onlineid.internal.configuration.Settings;
import net.hockeyapp.android.BuildConfig;

public class Assertion {
    public static void check(boolean z) throws AssertionError {
        check(z, BuildConfig.FLAVOR);
    }

    public static void check(boolean z, Object obj) throws AssertionError {
        if (!z && Settings.isDebugBuild()) {
            throw new AssertionError(obj);
        }
    }
}
