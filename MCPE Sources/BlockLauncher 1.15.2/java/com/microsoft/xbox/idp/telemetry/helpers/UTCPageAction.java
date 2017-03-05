package com.microsoft.xbox.idp.telemetry.helpers;

import Microsoft.Telemetry.Base;
import com.microsoft.xbox.idp.telemetry.utc.PageAction;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;

public class UTCPageAction {
    private static final int PAGEACTIONVERSION = 1;

    public static void track(String str, CharSequence charSequence) {
        track(str, UTCPageView.getCurrentPage(), charSequence, new UTCAdditionalInfoModel());
    }

    public static void track(String str, CharSequence charSequence, UTCAdditionalInfoModel uTCAdditionalInfoModel) {
        track(str, UTCPageView.getCurrentPage(), charSequence, uTCAdditionalInfoModel);
    }

    public static void track(String str, String str2, CharSequence charSequence, UTCAdditionalInfoModel uTCAdditionalInfoModel) {
        if (charSequence != null) {
            try {
                uTCAdditionalInfoModel.addValue("activityTitle", charSequence);
            } catch (Exception e) {
                UTCError.trackException(e, "UTCPageAction.track");
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        Base pageAction = new PageAction();
        pageAction.setActionName(str);
        pageAction.setPageName(str2);
        pageAction.setBaseData(UTCCommonDataModel.getCommonData(PAGEACTIONVERSION, uTCAdditionalInfoModel));
        UTCLog.log("pageActions:%s, onPage:%s, additionalInfo:%s", str, str2, uTCAdditionalInfoModel);
        UTCTelemetry.LogEvent(pageAction);
    }
}
