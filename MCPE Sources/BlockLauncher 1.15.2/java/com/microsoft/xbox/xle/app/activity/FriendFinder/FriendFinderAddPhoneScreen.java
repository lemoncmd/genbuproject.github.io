package com.microsoft.xbox.xle.app.activity.FriendFinder;

import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.R;

public class FriendFinderAddPhoneScreen extends ActivityBase {
    protected String getActivityName() {
        return "Friend Finder Add Phone";
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FriendFinderAddPhoneScreenViewModel(this);
    }

    public void onCreateContentView() {
        setContentView(R.layout.friendfinder_add_phone_screen);
    }

    public void onStart() {
        super.onStart();
        UTCFriendFinder.trackContactsAddPhoneView(getName());
    }
}
