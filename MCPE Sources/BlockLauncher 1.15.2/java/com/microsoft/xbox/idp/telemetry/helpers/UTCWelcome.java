package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.model.Profile.User;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.Welcome;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView;

public class UTCWelcome {
    public static void trackChangeUser(User user, CharSequence charSequence) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track(Welcome.ChangeUser, charSequence);
    }

    public static void trackDone(User user, CharSequence charSequence) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track(Welcome.Done, charSequence);
    }

    public static void trackPageView(User user, CharSequence charSequence) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageView.track(PageView.Welcome.View, charSequence);
    }
}
