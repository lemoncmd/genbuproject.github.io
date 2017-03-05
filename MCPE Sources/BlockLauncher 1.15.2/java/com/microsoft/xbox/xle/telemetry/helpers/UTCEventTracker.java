package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;

public class UTCEventTracker {

    public interface UTCEventDelegate {
        void call();
    }

    public interface UTCStringEventDelegate {
        String call();
    }

    public static String callStringTrackWrapper(UTCStringEventDelegate uTCStringEventDelegate) {
        try {
            return uTCStringEventDelegate.call();
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
            return null;
        }
    }

    public static void callTrackWrapper(UTCEventDelegate uTCEventDelegate) {
        try {
            uTCEventDelegate.call();
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }
}
