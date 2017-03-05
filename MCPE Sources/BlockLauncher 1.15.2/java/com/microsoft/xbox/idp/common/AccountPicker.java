package com.microsoft.xbox.idp.common;

import android.content.Context;
import android.content.Intent;
import com.microsoft.xbox.idp.ui.AuthFlowActivity;
import com.microsoft.xbox.idp.ui.MSAFragment;
import com.microsoft.xbox.idp.ui.SignOutActivity;

public class AccountPicker {
    public static Intent newSignInIntent(Context context, String str, String str2) {
        Intent intent = new Intent(context, AuthFlowActivity.class);
        intent.putExtra(MSAFragment.ARG_SECURITY_SCOPE, str);
        intent.putExtra(MSAFragment.ARG_SECURITY_POLICY, str2);
        return intent;
    }

    public static Intent newSignInIntent(Context context, String str, String str2, String str3) {
        Intent intent = new Intent(context, AuthFlowActivity.class);
        intent.putExtra(MSAFragment.ARG_SECURITY_SCOPE, str);
        intent.putExtra(MSAFragment.ARG_SECURITY_POLICY, str2);
        intent.putExtra(AuthFlowActivity.ARG_ALT_BUTTON_TEXT, str3);
        return intent;
    }

    public static Intent newSignOutIntent(Context context) {
        return new Intent(context, SignOutActivity.class);
    }
}
