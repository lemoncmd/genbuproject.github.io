package com.microsoft.xbox.xle.app.activity.FriendFinder;

import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.R;

public class FriendFinderVerifyCodeScreen extends ActivityBase {
    protected String getActivityName() {
        return "Friend Finder Verify Code";
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FriendFinderVerifyCodeScreenViewModel(this);
    }

    public void onCreateContentView() {
        setContentView(R.layout.friendfinder_verify_code_screen);
    }

    public void onStart() {
        super.onStart();
        UTCFriendFinder.trackContactsVerifyPhoneView(getName());
    }
}
