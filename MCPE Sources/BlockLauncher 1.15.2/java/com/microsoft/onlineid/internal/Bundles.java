package com.microsoft.onlineid.internal;

import android.os.Bundle;
import com.microsoft.onlineid.internal.log.Logger;

public class Bundles {
    public static void log(String str, Bundle bundle) {
        if (bundle != null) {
            Logger.info(str);
            for (String str2 : bundle.keySet()) {
                if (bundle.get(str2) != null) {
                    Logger.info(String.format("%s: %s (%s)", new Object[]{(String) r1.next(), bundle.get(str2).toString(), bundle.get(str2).getClass().getName()}));
                } else {
                    Logger.info(String.format("%s: null", new Object[]{(String) r1.next()}));
                }
            }
            return;
        }
        Logger.info(str + " (bundle was null)");
    }
}
