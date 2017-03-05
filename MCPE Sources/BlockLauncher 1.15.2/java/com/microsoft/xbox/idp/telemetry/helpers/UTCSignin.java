package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCTelemetry.CallBackSources;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.PageAction.MSA;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCNames.PageAction.XboxAccount;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.Signin;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView;

public class UTCSignin {
    private static CharSequence activityTitle = null;

    public static CharSequence getCurrentActivity() {
        return activityTitle;
    }

    public static void setCurrentActivity(CharSequence charSequence) {
        activityTitle = charSequence;
    }

    public static void trackAccountAcquired(String str, String str2, boolean z) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("cid", str2);
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", CallBackSources.Account);
            UTCPageAction.track(Signin.AccountSuccess, getCurrentActivity(), uTCAdditionalInfoModel);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackAccountAcquired");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackMSASigninStart(String str, boolean z, CharSequence charSequence) {
        try {
            setCurrentActivity(charSequence);
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("cid", str);
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            UTCPageAction.track(MSA.Start, charSequence, uTCAdditionalInfoModel);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackMSASigninStart");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackMSASigninSuccess(String str, boolean z, CharSequence charSequence) {
        try {
            setCurrentActivity(charSequence);
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("cid", str);
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            UTCPageAction.track(MSA.Success, charSequence, uTCAdditionalInfoModel);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackMSASigninFinish");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackPageView(CharSequence charSequence) {
        try {
            UTCPageView.track(PageView.Signin.View, charSequence);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackPageView");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackSignin(String str, boolean z, CharSequence charSequence) {
        try {
            setCurrentActivity(charSequence);
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("cid", str);
            uTCAdditionalInfoModel.addValue("isSilent", Boolean.valueOf(z));
            UTCPageAction.track(Signin.Signin, charSequence, uTCAdditionalInfoModel);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackSignin");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackTicketAcquired(String str, String str2, boolean z) {
        try {
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("cid", str2);
            uTCAdditionalInfoModel.addValue("job", str);
            uTCAdditionalInfoModel.addValue("source", CallBackSources.Ticket);
            UTCPageAction.track(Signin.TicketSuccess, getCurrentActivity(), uTCAdditionalInfoModel);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackTicketAcquired");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackXBLSigninStart(String str, CharSequence charSequence) {
        try {
            setCurrentActivity(charSequence);
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("cid", str);
            UTCPageAction.track(XboxAccount.Start, charSequence, uTCAdditionalInfoModel);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackXBLSigninStart");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackXBLSigninSuccess(String str, CharSequence charSequence, boolean z) {
        try {
            setCurrentActivity(charSequence);
            UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
            uTCAdditionalInfoModel.addValue("cid", str);
            uTCAdditionalInfoModel.addValue("createdXBLAccount", Boolean.valueOf(z));
            UTCPageAction.track(XboxAccount.Success, charSequence, uTCAdditionalInfoModel);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCSignin.trackXBLSigninSuccess");
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }
}
