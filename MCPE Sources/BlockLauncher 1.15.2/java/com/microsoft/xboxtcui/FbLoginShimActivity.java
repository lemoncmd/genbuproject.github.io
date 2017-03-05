package com.microsoft.xboxtcui;

import android.os.Bundle;
import com.facebook.login.LoginManager;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import java.util.Arrays;

public class FbLoginShimActivity extends FbShimActivity {
    public static final String LOGIN_TYPE_KEY = "LoginType";

    public enum LoginType {
        READ,
        PUBLISH
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (bundle == null) {
            LoginType loginType = (LoginType) getIntent().getExtras().getSerializable(LOGIN_TYPE_KEY);
            if (loginType == LoginType.READ) {
                LoginManager.getInstance().setLoginBehavior(FacebookManager.getInstance().getLoginBehavior()).logInWithReadPermissions(this, FacebookManager.getInstance().getFacebookPermission());
            } else if (loginType == LoginType.PUBLISH) {
                LoginManager.getInstance().setLoginBehavior(FacebookManager.getInstance().getLoginBehavior()).logInWithPublishPermissions(this, Arrays.asList(new String[]{"publish_actions"}));
            }
        }
    }
}
