package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.model.Profile.User;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.Introducing;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView;

public class UTCIntroducing {
    public static void trackDone(User user, CharSequence charSequence) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCIntroducing.trackDone");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track(Introducing.Done, charSequence);
    }

    public static void trackPageView(User user, CharSequence charSequence) {
        if (user != null) {
            try {
                UTCCommonDataModel.setUserId(user.id);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCIntroducing.trackPageView");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageView.track(PageView.Introducing.View, charSequence);
    }
}
