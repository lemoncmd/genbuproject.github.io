package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry.CallBackSources;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.PageAction.MSA;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.Signout;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.UserCancel;

public class UTCUser {
    private static final boolean DEFAULT = true;
    private static boolean isSilent = DEFAULT;

    public static void setIsSilent(boolean z) {
        isSilent = z;
    }

    public static void trackCancel(CharSequence charSequence) {
        UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
        String currentPage = UTCPageView.getCurrentPage();
        if (currentPage != null) {
            try {
                uTCAdditionalInfoModel.addValue("canceledPage", currentPage);
                UTCPageAction.track(UserCancel.Cancel, charSequence, uTCAdditionalInfoModel);
                return;
            } catch (Exception e) {
                UTCError.trackException(e, "UTCUser.trackCancel");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track(UserCancel.Cancel, charSequence);
    }

    public static void trackMSACancel(CharSequence charSequence, String str, boolean z, CallBackSources callBackSources) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", callBackSources);
            UTCPageAction.track(MSA.Cancel, charSequence, uTCAdditionalInfoModel);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCUser.trackMSACancel");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackSignout(CharSequence charSequence) {
        UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
        uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(isSilent));
        UTCPageAction.track(Signout.Signout, charSequence, uTCAdditionalInfoModel);
        isSilent = DEFAULT;
    }
}
