package com.microsoft.xbox.idp.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.idp.ui.AccountProvisioningResult;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.Signup;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView;

public class UTCSignup {
    public static void trackClaimGamerTag(AccountProvisioningResult accountProvisioningResult, CharSequence charSequence) {
        if (accountProvisioningResult != null) {
            try {
                UTCCommonDataModel.setUserId(accountProvisioningResult.getXuid());
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track(Signup.ClaimGamerTag, charSequence);
    }

    public static void trackClearGamerTag(AccountProvisioningResult accountProvisioningResult, CharSequence charSequence) {
        if (accountProvisioningResult != null) {
            try {
                UTCCommonDataModel.setUserId(accountProvisioningResult.getXuid());
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track(Signup.ClearGamerTagText, charSequence);
    }

    public static void trackPageView(CharSequence charSequence) {
        try {
            UTCPageView.track(PageView.Signup.View, charSequence);
        } catch (Exception e) {
            UTCLog.log(e.getMessage(), new Object[0]);
        }
    }

    public static void trackSearchGamerTag(AccountProvisioningResult accountProvisioningResult, CharSequence charSequence) {
        if (accountProvisioningResult != null) {
            try {
                UTCCommonDataModel.setUserId(accountProvisioningResult.getXuid());
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track(Signup.SearchGamertag, charSequence);
    }

    public static void trackSignInWithDifferentUser(AccountProvisioningResult accountProvisioningResult, CharSequence charSequence) {
        if (accountProvisioningResult != null) {
            try {
                UTCCommonDataModel.setUserId(accountProvisioningResult.getXuid());
            } catch (Exception e) {
                UTCLog.log(e.getMessage(), new Object[0]);
                return;
            }
        }
        UTCPageAction.track(Signup.ChangeUser, charSequence);
    }
}
