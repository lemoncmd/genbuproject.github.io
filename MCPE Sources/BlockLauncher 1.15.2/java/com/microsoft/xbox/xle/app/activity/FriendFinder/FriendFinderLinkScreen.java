package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xboxtcui.R;

public class FriendFinderLinkScreen extends ActivityBase {
    private FriendFinderType linkType = FriendFinderType.UNKNOWN;

    public FriendFinderLinkScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    protected String getActivityName() {
        return "Friend Finder Facebook Link";
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        ActivityParameters activityParameters = NavigationManager.getInstance().getActivityParameters();
        XLEAssert.assertNotNull(activityParameters);
        if (activityParameters != null) {
            this.linkType = activityParameters.getFriendFinderType();
            XLEAssert.assertFalse("Expected link type", this.linkType == FriendFinderType.UNKNOWN);
        }
        if (this.linkType == FriendFinderType.UNKNOWN) {
            try {
                NavigationManager.getInstance().PopScreen();
            } catch (XLEException e) {
            }
        } else if (this.linkType == FriendFinderType.FACEBOOK) {
            this.viewModel = new LinkFacebookAccountViewModel(this);
        }
    }

    public void onCreateContentView() {
        setContentView(R.layout.friendfinder_link_screen);
    }
}
