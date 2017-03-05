package com.microsoft.xbox.service.model.sls;

import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public enum FeedbackType {
    Unknown,
    FairPlayKillsTeammates,
    FairPlayCheater,
    FairPlayTampering,
    FairPlayQuitter,
    FairPlayKicked,
    FairPlayBlock,
    FairPlayUnblock,
    FairPlayUserBanRequest,
    FairPlayConsoleBanRequest,
    FairplayUnsporting,
    FairplayIdler,
    CommsTextMessage,
    CommsVoiceMessage,
    CommsPictureMessage,
    CommsInappropriateVideo,
    CommsAbusiveVoice,
    CommsSpam,
    CommsPhishing,
    CommsMuted,
    CommsUnmuted,
    Comms911,
    UserContentActivityFeed,
    UserContentGameDVR,
    UserContentGamertag,
    UserContentRealName,
    UserContentGamerpic,
    UserContentPersonalInfo,
    UserContentInappropriateUGC,
    UserContentReviewRequest,
    UserContentScreenshot,
    PositiveSkilledPlayer,
    PositiveHelpfulPlayer,
    PositiveHighQualityUGC,
    InternalReputationUpdated,
    InternalAmbassadorScoreUpdated,
    InternalReputationReset,
    InternalEnforcementDataUpdated;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType = null;

        static {
            $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType = new int[FeedbackType.values().length];
            try {
                $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[FeedbackType.UserContentPersonalInfo.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[FeedbackType.FairPlayCheater.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[FeedbackType.UserContentRealName.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[FeedbackType.UserContentGamertag.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[FeedbackType.UserContentGamerpic.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[FeedbackType.FairPlayQuitter.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[FeedbackType.FairplayUnsporting.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[FeedbackType.CommsAbusiveVoice.ordinal()] = 8;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    public String getTitle() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$service$model$sls$FeedbackType[ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_BioLoc);
            case NativeRegExp.PREFIX /*2*/:
                return XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_Cheating);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_PlayerName);
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_PlayerName);
            case Token.GOTO /*5*/:
                return XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_PlayerPic);
            case Token.IFEQ /*6*/:
                return XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_QuitEarly);
            case Token.IFNE /*7*/:
                return XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_Unsporting);
            case Token.SETNAME /*8*/:
                return XboxTcuiSdk.getResources().getString(R.string.ProfileCard_Report_VoiceComm);
            default:
                XLEAssert.fail("No title implementation.");
                return BuildConfig.FLAVOR;
        }
    }
}
