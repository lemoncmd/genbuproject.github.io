package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker.UTCEventDelegate;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.PeopleHub;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView;
import net.hockeyapp.android.BuildConfig;

public class UTCPeopleHub {
    private static CharSequence currentActivityTitle = BuildConfig.FLAVOR;
    private static String currentXUID = BuildConfig.FLAVOR;

    private static UTCAdditionalInfoModel getBaseInfoModel(String str) {
        UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
        uTCAdditionalInfoModel.addValue(UTCDeepLink.TARGET_XUID_KEY, "x:" + str);
        return uTCAdditionalInfoModel;
    }

    public static void trackBlock() {
        verifyTrackedDefaults();
        trackBlock(currentActivityTitle, currentXUID);
    }

    public static void trackBlock(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCPageAction.track(PeopleHub.Block, charSequence, UTCPeopleHub.getBaseInfoModel(str));
            }
        });
    }

    public static void trackBlockDialogComplete() {
        verifyTrackedDefaults();
        trackBlockDialogComplete(currentActivityTitle, currentXUID);
    }

    public static void trackBlockDialogComplete(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCPageAction.track(PeopleHub.BlockOK, charSequence, UTCPeopleHub.getBaseInfoModel(str));
            }
        });
    }

    public static void trackMute(final CharSequence charSequence, final String str, final boolean z) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel access$200 = UTCPeopleHub.getBaseInfoModel(str);
                access$200.addValue("isMuted", Boolean.valueOf(z));
                UTCPageAction.track(PeopleHub.Mute, charSequence, access$200);
            }
        });
    }

    public static void trackMute(boolean z) {
        verifyTrackedDefaults();
        trackMute(currentActivityTitle, currentXUID, z);
    }

    public static void trackPeopleHubView(final CharSequence charSequence, final String str, final boolean z) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCPeopleHub.currentXUID = str;
                UTCPeopleHub.currentActivityTitle = charSequence;
                UTCPageView.track(z ? PageView.PeopleHub.PeopleHubMeView : PageView.PeopleHub.PeopleHubYouView, UTCPeopleHub.currentActivityTitle, UTCPeopleHub.getBaseInfoModel(str));
            }
        });
    }

    public static void trackReport() {
        verifyTrackedDefaults();
        trackReport(currentActivityTitle, currentXUID);
    }

    public static void trackReport(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCPageAction.track(PeopleHub.Report, charSequence, UTCPeopleHub.getBaseInfoModel(str));
            }
        });
    }

    public static void trackUnblock() {
        verifyTrackedDefaults();
        trackUnblock(currentActivityTitle, currentXUID);
    }

    public static void trackUnblock(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCPageAction.track(PeopleHub.Unblock, charSequence, UTCPeopleHub.getBaseInfoModel(str));
            }
        });
    }

    public static void trackViewInXboxApp() {
        verifyTrackedDefaults();
        trackViewInXboxApp(currentActivityTitle, currentXUID);
    }

    public static void trackViewInXboxApp(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCPageAction.track(PeopleHub.ViewXboxApp, charSequence, UTCPeopleHub.getBaseInfoModel(str));
            }
        });
    }

    public static void trackViewInXboxAppDialogComplete() {
        verifyTrackedDefaults();
        trackViewInXboxAppDialogComplete(currentActivityTitle, currentXUID);
    }

    public static void trackViewInXboxAppDialogComplete(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCPageAction.track(PeopleHub.ViewXboxAppOK, charSequence, UTCPeopleHub.getBaseInfoModel(str));
            }
        });
    }

    private static void verifyTrackedDefaults() {
        XLEAssert.assertFalse("Called trackPeopleHubView without set currentXUID", currentXUID.equals(BuildConfig.FLAVOR));
        XLEAssert.assertFalse("Called trackPeopleHubView without set activityTitle", currentActivityTitle.toString().equals(BuildConfig.FLAVOR));
    }
}
