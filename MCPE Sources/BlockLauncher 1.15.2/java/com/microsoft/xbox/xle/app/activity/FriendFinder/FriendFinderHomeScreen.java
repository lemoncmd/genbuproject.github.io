package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.toolkit.ProjectSpecificDataProvider;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.R;

public class FriendFinderHomeScreen extends ActivityBase {
    public FriendFinderHomeScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    protected String getActivityName() {
        return "FriendFinderHome";
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FriendFinderHomeScreenViewModel(this);
    }

    public void onCreateContentView() {
        setContentView(R.layout.friend_finder_home_screen);
    }

    public void onStart() {
        super.onStart();
        UTCFriendFinder.trackFriendFinderView(getActivityName(), ProjectSpecificDataProvider.getInstance().getXuidString());
    }
}
