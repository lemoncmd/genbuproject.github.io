package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Context;
import android.util.AttributeSet;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.R;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FriendFinderSuggestionsScreen extends ActivityBase {

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType = new int[FriendFinderType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[FriendFinderType.FACEBOOK.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[FriendFinderType.PHONE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public FriendFinderSuggestionsScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    protected String getActivityName() {
        return "Friend Finder Suggestions";
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        this.viewModel = new FriendFinderSuggestionsScreenViewModel(this);
    }

    public void onCreateContentView() {
        setContentView(R.layout.friendfinder_suggestions_screen);
    }

    public void onStart() {
        super.onStart();
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[NavigationManager.getInstance().getActivityParameters().getFriendFinderType().ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                UTCFriendFinder.trackFacebookAddFriendView(getActivityName());
                return;
            case NativeRegExp.PREFIX /*2*/:
                UTCFriendFinder.trackContactsFindFriendsView(getActivityName());
                return;
            default:
                return;
        }
    }
}
