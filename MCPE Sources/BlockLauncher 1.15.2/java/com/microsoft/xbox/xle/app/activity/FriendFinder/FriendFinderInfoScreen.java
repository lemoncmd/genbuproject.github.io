package com.microsoft.xbox.xle.app.activity.FriendFinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.service.model.privacy.PrivacySettings.PrivacySettingValue;
import com.microsoft.xbox.service.network.managers.friendfinder.FacebookManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEException;
import com.microsoft.xbox.toolkit.ui.ActivityParameters;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.toolkit.ui.NavigationManager;
import com.microsoft.xbox.toolkit.ui.XLEButton;
import com.microsoft.xbox.xle.app.activity.ActivityBase;
import com.microsoft.xbox.xle.telemetry.helpers.UTCFriendFinder;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class FriendFinderInfoScreen extends ActivityBase {
    private FriendFinderType infoType = FriendFinderType.UNKNOWN;
    private XLEButton nextButton;
    private CustomTypefaceTextView subtitleTextView;
    private CustomTypefaceTextView titleTextView;

    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType = new int[FriendFinderType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$service$model$privacy$PrivacySettings$PrivacySettingValue = new int[PrivacySettingValue.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$service$model$privacy$PrivacySettings$PrivacySettingValue[PrivacySettingValue.NotSet.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$privacy$PrivacySettings$PrivacySettingValue[PrivacySettingValue.Blocked.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$privacy$PrivacySettings$PrivacySettingValue[PrivacySettingValue.FriendCategoryShareIdentity.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$privacy$PrivacySettings$PrivacySettingValue[PrivacySettingValue.Everyone.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$privacy$PrivacySettings$PrivacySettingValue[PrivacySettingValue.PeopleOnMyList.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[FriendFinderType.FACEBOOK.ordinal()] = 1;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[FriendFinderType.PHONE.ordinal()] = 2;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public FriendFinderInfoScreen(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    private String getFacebookText() {
        switch (AnonymousClass3.$SwitchMap$com$microsoft$xbox$service$model$privacy$PrivacySettings$PrivacySettingValue[PrivacySettingValue.getPrivacySettingValue(ProfileModel.getMeProfileModel().getShareRealNameStatus()).ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return multiLineText(R.string.FriendFinder_LinkFacebook_Dialog_Text_Default, R.string.FriendFinder_LinkFacebook_Dialog_Text_NotSet_LineTwo, R.string.FriendFinder_LinkFacebook_Dialog_Text_NotSet_LineThree);
            case NativeRegExp.PREFIX /*2*/:
                return multiLineText(R.string.FriendFinder_LinkFacebook_Dialog_Text_Default, R.string.FriendFinder_LinkFacebook_Dialog_Text_Blocked_LineTwo, R.string.FriendFinder_LinkFacebook_Dialog_Text_Blocked_LineThree);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return multiLineText(R.string.FriendFinder_LinkFacebook_Dialog_Text_Default, R.string.FriendFinder_LinkFacebook_Dialog_Text_PeopleIChoose_LineTwo);
            default:
                return XboxTcuiSdk.getResources().getString(R.string.FriendFinder_LinkFacebook_Dialog_Text_Default);
        }
    }

    private String getPhoneText() {
        switch (AnonymousClass3.$SwitchMap$com$microsoft$xbox$service$model$privacy$PrivacySettings$PrivacySettingValue[PrivacySettingValue.getPrivacySettingValue(ProfileModel.getMeProfileModel().getShareRealNameStatus()).ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return multiLineText(R.string.FriendFinder_LinkPhone_Dialog_Text_LineOne, R.string.FriendFinder_LinkPhone_Dialog_Text_LineTwo, R.string.FriendFinder_LinkPhone_Dialog_Text_RealNameSharedWithContacts, R.string.FriendFinder_LinkPhone_Dialog_Text_LineThree);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return multiLineText(R.string.FriendFinder_LinkPhone_Dialog_Text_LineOne, R.string.FriendFinder_LinkPhone_Dialog_Text_LineTwo, R.string.FriendFinder_LinkPhone_Dialog_Text_LineThree);
            case Token.GOTO /*5*/:
                return multiLineText(R.string.FriendFinder_LinkPhone_Dialog_Text_LineOne, R.string.FriendFinder_LinkPhone_Dialog_Text_LineTwo, R.string.FriendFinder_LinkPhone_Dialog_Text_RealNameSharedWithContacts, R.string.FriendFinder_LinkPhone_Dialog_Text_ACoupleNotes, R.string.FriendFinder_LinkPhone_Dialog_Text_LineThree);
            default:
                return multiLineText(R.string.FriendFinder_LinkPhone_Dialog_Text_LineOne, R.string.FriendFinder_LinkPhone_Dialog_Text_LineTwo);
        }
    }

    private String multiLineText(int... iArr) {
        if (iArr.length == 0) {
            return BuildConfig.FLAVOR;
        }
        String string = XboxTcuiSdk.getResources().getString(iArr[0]);
        for (int i = 1; i < iArr.length; i++) {
            string = string + "\n\n" + XboxTcuiSdk.getResources().getString(iArr[i]);
        }
        return string;
    }

    protected String getActivityName() {
        switch (AnonymousClass3.$SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[this.infoType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return "Friend finder facebook info";
            case NativeRegExp.PREFIX /*2*/:
                return "Friend finder phone info";
            default:
                return "Friend finder info";
        }
    }

    public boolean onBackButtonPressed() {
        UTCFriendFinder.trackBackButtonPressed(getName(), this.infoType);
        return super.onBackButtonPressed();
    }

    public void onCreate() {
        super.onCreate();
        onCreateContentView();
        ActivityParameters activityParameters = NavigationManager.getInstance().getActivityParameters();
        XLEAssert.assertNotNull(activityParameters);
        if (activityParameters != null) {
            this.infoType = activityParameters.getFriendFinderType();
            XLEAssert.assertFalse("Expected info type", this.infoType == FriendFinderType.UNKNOWN);
        }
    }

    public void onCreateContentView() {
        setContentView(R.layout.friend_finder_info_screen);
        this.titleTextView = (CustomTypefaceTextView) findViewById(R.id.friendfinder_info_title);
        this.subtitleTextView = (CustomTypefaceTextView) findViewById(R.id.friendfinder_info_subtitle);
        this.nextButton = (XLEButton) findViewById(R.id.friendfinder_info_next);
        XLEAssert.assertNotNull(this.titleTextView);
        XLEAssert.assertNotNull(this.subtitleTextView);
        XLEAssert.assertNotNull(this.nextButton);
    }

    public void onStart() {
        super.onStart();
        switch (AnonymousClass3.$SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[this.infoType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                this.titleTextView.setText(R.string.FriendFinder_LinkFacebook_Dialog_Title);
                this.subtitleTextView.setText(getFacebookText());
                this.nextButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        UTCFriendFinder.trackFacebookOptInNext(FriendFinderInfoScreen.this.getName());
                        XLEAssert.assertTrue(FacebookManager.getFacebookManagerReady().getIsReady());
                        FacebookManager.getInstance().login();
                    }
                });
                UTCFriendFinder.trackFacebookOptInView(getName());
                return;
            case NativeRegExp.PREFIX /*2*/:
                this.titleTextView.setText(R.string.FriendFinder_LinkPhone_Dialog_Title);
                this.subtitleTextView.setText(getPhoneText());
                this.nextButton.setOnClickListener(new OnClickListener() {
                    public void onClick(View view) {
                        UTCFriendFinder.trackPhoneContactsNext(FriendFinderInfoScreen.this.getName());
                        try {
                            NavigationManager.getInstance().PushScreen(FriendFinderAddPhoneScreen.class);
                        } catch (XLEException e) {
                        }
                    }
                });
                UTCFriendFinder.trackContactsOptInView(getName());
                return;
            default:
                return;
        }
    }
}
