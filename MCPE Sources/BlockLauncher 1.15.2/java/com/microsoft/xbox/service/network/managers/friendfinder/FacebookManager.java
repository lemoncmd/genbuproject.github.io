package com.microsoft.xbox.service.network.managers.friendfinder;

import android.content.Intent;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.CallbackManager.Factory;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer.Result;
import com.facebook.share.widget.ShareDialog;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.FriendsFinderStateResult;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.LinkedAccountOptInStatus;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderState.LinkedAccountTokenStatus;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.network.managers.xblshared.ProtectedRunnable;
import com.microsoft.xbox.toolkit.Ready;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.activity.FriendFinder.FriendFinderLinkScreen;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.FbLoginShimActivity;
import com.microsoft.xboxtcui.FbLoginShimActivity.LoginType;
import com.microsoft.xboxtcui.FbShareShimActivity;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.Arrays;
import java.util.List;

public class FacebookManager {
    private static Ready facebookManagerReady = new Ready();
    private static FacebookManager instance;
    private CallbackManager callbackManager;
    private List<String> facebookPermission;
    private boolean firstLoginWithReadOnly = false;
    private FriendsFinderStateResult friendsFinderStateResult;
    private LoginBehavior loginBehavior;
    private FacebookCallback<LoginResult> loginResult = new FacebookCallback<LoginResult>() {
        public void onCancel() {
            UTCFriendFinder.trackFacebookLoginCancel(null);
            FacebookManager.this.loadPeopleHubFriendFinderState();
            FacebookManager.this.resetFacebookToken(true);
        }

        public void onError(FacebookException facebookException) {
            FacebookManager.this.loadPeopleHubFriendFinderState();
            FacebookManager.this.resetFacebookToken(true);
        }

        public void onSuccess(LoginResult loginResult) {
            FacebookManager.this.token = loginResult.getAccessToken();
            if (FacebookManager.this.token == null) {
                return;
            }
            if (FacebookManager.this.firstLoginWithReadOnly) {
                FacebookManager.this.firstLoginWithReadOnly = false;
                FacebookManager.this.tokenString = FacebookManager.this.token.getToken();
                ActivityParameters activityParameters = new ActivityParameters();
                activityParameters.putFriendFinderType(FriendFinderType.FACEBOOK);
                try {
                    NavigationManager.getInstance().PushScreen(FriendFinderLinkScreen.class, activityParameters);
                    return;
                } catch (XLEException e) {
                    return;
                }
            }
            FacebookManager.this.showShareDialog();
        }
    };
    private FacebookCallback<Result> shareResult = new FacebookCallback<Result>() {
        public void onCancel() {
        }

        public void onError(FacebookException facebookException) {
        }

        public void onSuccess(Result result) {
        }
    };
    private AccessToken token;
    private String tokenString;

    private FacebookManager() {
        facebookManagerReady.reset();
        new ProtectedRunnable(new Runnable() {
            public void run() {
                try {
                    FacebookManager.this.facebookPermission = Arrays.asList(new String[]{"public_profile", "user_friends"});
                    FacebookSdk.sdkInitialize(XboxTcuiSdk.getApplicationContext());
                    FacebookManager.this.callbackManager = Factory.create();
                    LoginManager.getInstance().registerCallback(FacebookManager.this.callbackManager, FacebookManager.this.loginResult);
                } catch (Exception e) {
                    Log.i("h", e.getMessage());
                }
            }
        }).run();
        this.loginBehavior = LoginBehavior.WEB_ONLY;
        facebookManagerReady.setReady();
    }

    public static Ready getFacebookManagerReady() {
        return facebookManagerReady;
    }

    public static FacebookManager getInstance() {
        synchronized (FacebookManager.class) {
            try {
                if (instance == null) {
                    instance = new FacebookManager();
                }
                FacebookManager facebookManager = instance;
                return facebookManager;
            } finally {
                Object obj = FacebookManager.class;
            }
        }
    }

    private Intent getPublishShimIntent() {
        Intent intent = new Intent(XboxTcuiSdk.getActivity(), FbLoginShimActivity.class);
        intent.putExtra(FbLoginShimActivity.LOGIN_TYPE_KEY, LoginType.PUBLISH);
        return intent;
    }

    private Intent getReadShimIntent() {
        Intent intent = new Intent(XboxTcuiSdk.getActivity(), FbLoginShimActivity.class);
        intent.putExtra(FbLoginShimActivity.LOGIN_TYPE_KEY, LoginType.READ);
        return intent;
    }

    private void showShareDialog() {
        XboxTcuiSdk.getActivity().startActivity(new Intent(XboxTcuiSdk.getActivity(), FbShareShimActivity.class));
    }

    public FriendsFinderStateResult getFacebookFriendFinderState() {
        return this.friendsFinderStateResult;
    }

    public List<String> getFacebookPermission() {
        return this.facebookPermission;
    }

    public LoginBehavior getLoginBehavior() {
        return this.loginBehavior;
    }

    public String getTokenString() {
        return this.tokenString;
    }

    public boolean isFacebookFriendFinderOptedIn() {
        return this.friendsFinderStateResult != null && this.friendsFinderStateResult.getLinkedAccountOptInStatus() == LinkedAccountOptInStatus.OptedIn && this.friendsFinderStateResult.getLinkedAccountTokenStatus() == LinkedAccountTokenStatus.OK;
    }

    public void loadPeopleHubFriendFinderState() {
        FriendFinderModel.getInstance().loadAsync(true);
    }

    public void login() {
        UTCFriendFinder.trackFacebookLinkAccountView(null);
        this.firstLoginWithReadOnly = true;
        XboxTcuiSdk.getActivity().startActivity(getReadShimIntent());
    }

    public void onShimActivityResult(int i, int i2, Intent intent) {
        this.callbackManager.onActivityResult(i, i2, intent);
    }

    public void registerShareCallback(ShareDialog shareDialog) {
        shareDialog.registerCallback(this.callbackManager, this.shareResult);
    }

    public void resetFacebookToken(boolean z) {
        this.token = null;
        this.tokenString = null;
        if (z) {
            LoginManager.getInstance().logOut();
        }
    }

    public void setFacebookFriendFinderState(FriendsFinderStateResult friendsFinderStateResult) {
        this.friendsFinderStateResult = friendsFinderStateResult;
    }

    public void shareToFacebook() {
        this.firstLoginWithReadOnly = false;
        XboxTcuiSdk.getActivity().startActivity(getPublishShimIntent());
    }
}
