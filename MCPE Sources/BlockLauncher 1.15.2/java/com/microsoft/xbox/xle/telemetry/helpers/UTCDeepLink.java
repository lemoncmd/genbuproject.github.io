package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker.UTCEventDelegate;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker.UTCStringEventDelegate;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.DeepLink;

public class UTCDeepLink {
    public static final String CALLING_APP_KEY = "deepLinkCaller";
    public static final String DEEPLINK_KEY_NAME = "deepLinkId";
    public static final String INTENDED_ACTION_KEY = "intendedAction";
    public static final String TARGET_TITLE_KEY = "targetTitleId";
    public static final String TARGET_XUID_KEY = "targetXUID";

    private static String generateCorrelationId() {
        return UTCCommonDataModel.getCommonData(1).getAppSessionId();
    }

    private static UTCAdditionalInfoModel getBaseModelInfo(String str) {
        UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
        uTCAdditionalInfoModel.addValue(DEEPLINK_KEY_NAME, generateCorrelationId());
        uTCAdditionalInfoModel.addValue(CALLING_APP_KEY, str);
        return uTCAdditionalInfoModel;
    }

    public static String trackFriendSuggestionsLink(final CharSequence charSequence, final String str) {
        return UTCEventTracker.callStringTrackWrapper(new UTCStringEventDelegate() {
            public String call() {
                UTCAdditionalInfoModel access$000 = UTCDeepLink.getBaseModelInfo(str);
                UTCPageAction.track(DeepLink.FriendSuggestions, charSequence, access$000);
                return access$000.getAdditionalInfo().get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }

    public static String trackGameHubAchievementsLink(final CharSequence charSequence, final String str, final String str2) {
        return UTCEventTracker.callStringTrackWrapper(new UTCStringEventDelegate() {
            public String call() {
                UTCAdditionalInfoModel access$000 = UTCDeepLink.getBaseModelInfo(str);
                access$000.addValue(UTCDeepLink.TARGET_TITLE_KEY, str2);
                UTCPageAction.track(DeepLink.TitleHub, charSequence, access$000);
                return access$000.getAdditionalInfo().get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }

    public static String trackGameHubLink(final CharSequence charSequence, final String str, final String str2) {
        return UTCEventTracker.callStringTrackWrapper(new UTCStringEventDelegate() {
            public String call() {
                UTCAdditionalInfoModel access$000 = UTCDeepLink.getBaseModelInfo(str);
                access$000.addValue(UTCDeepLink.TARGET_XUID_KEY, str2);
                UTCPageAction.track(DeepLink.TitleHub, charSequence, access$000);
                return access$000.getAdditionalInfo().get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }

    public static String trackUserProfileLink(final CharSequence charSequence, final String str, final String str2) {
        return UTCEventTracker.callStringTrackWrapper(new UTCStringEventDelegate() {
            public String call() {
                UTCAdditionalInfoModel access$000 = UTCDeepLink.getBaseModelInfo(str);
                access$000.addValue(UTCDeepLink.TARGET_XUID_KEY, "x:" + str2);
                UTCPageAction.track(DeepLink.UserProfile, charSequence, access$000);
                return access$000.getAdditionalInfo().get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }

    public static void trackUserSendToStore(final CharSequence charSequence, final String str, final String str2) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCAdditionalInfoModel access$000 = UTCDeepLink.getBaseModelInfo(str);
                access$000.addValue(UTCDeepLink.INTENDED_ACTION_KEY, str2);
                UTCPageAction.track(DeepLink.SendToStore, charSequence, access$000);
            }
        });
    }

    public static String trackUserSettingsLink(final CharSequence charSequence, final String str) {
        return UTCEventTracker.callStringTrackWrapper(new UTCStringEventDelegate() {
            public String call() {
                UTCAdditionalInfoModel access$000 = UTCDeepLink.getBaseModelInfo(str);
                UTCPageAction.track(DeepLink.UserSettings, charSequence, access$000);
                return access$000.getAdditionalInfo().get(UTCDeepLink.DEEPLINK_KEY_NAME).toString();
            }
        });
    }
}
