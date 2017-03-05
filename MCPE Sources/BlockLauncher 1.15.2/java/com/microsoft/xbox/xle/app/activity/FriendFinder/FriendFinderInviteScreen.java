package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.network.managers.IPeopleHubResult.RecommendationType;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.FriendFinderSettings;
import com.microsoft.xbox.xle.app.FriendFinderSettings.IconImageSize;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xbox.xle.ui.IconFontSubTextButton;
import com.microsoft.xbox.xle.ui.ImageTitleSubtitleButton;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FriendFinderInviteScreen extends ActivityBase {
    private FriendFinderType inviteType = FriendFinderType.UNKNOWN;

    static /* synthetic */ class AnonymousClass4 {
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

    public FriendFinderInviteScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private String getFacebookIconUri() {
        return FriendFinderSettings.getIconBySize(RecommendationType.FacebookFriend.name(), IconImageSize.MEDIUM);
    }

    protected String getActivityName() {
        return null;
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getName(), this.inviteType);
        return super.onBackButtonPressed();
    }

    public void onCreate() {
        super.onCreate();
        XLEAssert.assertTrue(FacebookManager.getFacebookManagerReady().getIsReady());
        onCreateContentView();
        ActivityParameters activityParameters = NavigationManager.getInstance().getActivityParameters();
        XLEAssert.assertNotNull(activityParameters);
        if (activityParameters != null) {
            this.inviteType = activityParameters.getFriendFinderType();
            XLEAssert.assertFalse("Expected invite type", this.inviteType == FriendFinderType.UNKNOWN);
        }
    }

    public void onCreateContentView() {
        setContentView(R.layout.friendfinder_invite_screen);
    }

    public void onStart() {
        super.onStart();
        CustomTypefaceTextView customTypefaceTextView = (CustomTypefaceTextView) findViewById(R.id.friendfinder_invite_title);
        CustomTypefaceTextView customTypefaceTextView2 = (CustomTypefaceTextView) findViewById(R.id.friendfinder_invite_subtitle);
        if (this.inviteType == FriendFinderType.FACEBOOK) {
            customTypefaceTextView.setText(R.string.FriendFinder_Invite_Facebook_Title);
            customTypefaceTextView2.setText(R.string.FriendFinder_Facebook_Upsell_Description_Default_LineTwo);
            ImageTitleSubtitleButton imageTitleSubtitleButton = (ImageTitleSubtitleButton) findViewById(R.id.friendfinder_invite_facebook);
            if (imageTitleSubtitleButton != null) {
                imageTitleSubtitleButton.setVisibility(0);
                imageTitleSubtitleButton.setImageUri(getFacebookIconUri());
                imageTitleSubtitleButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        UTCFriendFinder.trackShareFacebookLinkToFeed(FriendFinderInviteScreen.this.getName());
                        FacebookManager.getInstance().shareToFacebook();
                    }
                });
            }
            UTCFriendFinder.trackFacebookShareView(getActivityName());
        } else if (this.inviteType == FriendFinderType.PHONE) {
            customTypefaceTextView.setText(R.string.FriendFinder_PhoneInviteFriends_Dialog_Title);
            customTypefaceTextView2.setText(XboxTcuiSdk.getResources().getString(R.string.FriendFinder_PhoneInviteFriends_Dialog_Text).replace("-", "\n\n"));
            IconFontSubTextButton iconFontSubTextButton = (IconFontSubTextButton) findViewById(R.id.friendfinder_invite_phone);
            if (iconFontSubTextButton != null) {
                iconFontSubTextButton.setVisibility(0);
                iconFontSubTextButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        try {
                            UTCFriendFinder.trackPhoneContactsSendInvite(FriendFinderInviteScreen.this.getName());
                            NavigationManager.getInstance().PushScreen(FriendFinderPhoneInviteScreen.class);
                        } catch (XLEException e) {
                        }
                    }
                });
            }
            UTCFriendFinder.trackContactsInviteFriendsView(getActivityName());
        }
        XLEButton xLEButton = (XLEButton) findViewById(R.id.friendfinder_invite_next);
        if (xLEButton != null) {
            xLEButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    try {
                        switch (AnonymousClass4.$SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[FriendFinderInviteScreen.this.inviteType.ordinal()]) {
                            case NativeRegExp.MATCH /*1*/:
                                UTCFriendFinder.trackSkipFacebookSharing(FriendFinderInviteScreen.this.getName());
                                break;
                            case NativeRegExp.PREFIX /*2*/:
                                UTCFriendFinder.trackPhoneContactsNext(FriendFinderInviteScreen.this.getName());
                                break;
                        }
                        ActivityParameters activityParameters = new ActivityParameters();
                        activityParameters.putFriendFinderDone(true);
                        NavigationManager.getInstance().PushScreen(FriendFinderHomeScreen.class, activityParameters);
                    } catch (XLEException e) {
                    }
                }
            });
        }
    }
}
