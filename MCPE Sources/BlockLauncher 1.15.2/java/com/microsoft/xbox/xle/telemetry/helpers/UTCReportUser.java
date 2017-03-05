package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker.UTCEventDelegate;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.PeopleHub;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView;
import net.hockeyapp.android.BuildConfig;

public class UTCReportUser {
    private static CharSequence currentActivityTitle = BuildConfig.FLAVOR;
    private static String currentXUID = BuildConfig.FLAVOR;

    private static UTCAdditionalInfoModel getBaseInfoModel(String str) {
        UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
        uTCAdditionalInfoModel.addValue(UTCDeepLink.TARGET_XUID_KEY, "x:" + str);
        return uTCAdditionalInfoModel;
    }

    public static void trackReportDialogOK(final CharSequence charSequence, final String str, final String str2) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel access$200 = UTCReportUser.getBaseInfoModel(str);
                access$200.addValue("reason", str2);
                UTCPageAction.track(PeopleHub.ReportOK, charSequence, access$200);
            }
        });
    }

    public static void trackReportDialogOK(String str) {
        verifyTrackedDefaults();
        trackReportDialogOK(currentActivityTitle, currentXUID, str);
    }

    public static void trackReportView(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCReportUser.currentActivityTitle = charSequence;
                UTCReportUser.currentXUID = str;
                UTCPageView.track(PageView.PeopleHub.ReportView, UTCReportUser.currentActivityTitle, UTCReportUser.getBaseInfoModel(str));
            }
        });
    }

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(BuildConfig.FLAVOR));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(BuildConfig.FLAVOR));
    }
}
