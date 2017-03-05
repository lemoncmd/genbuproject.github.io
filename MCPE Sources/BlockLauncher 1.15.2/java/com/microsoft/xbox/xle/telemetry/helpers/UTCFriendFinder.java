package com.microsoft.xbox.xle.telemetry.helpers;

import com.microsoft.xbox.idp.telemetry.helpers.UTCPageAction;
import com.microsoft.xbox.idp.telemetry.helpers.UTCPageView;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCAdditionalInfoModel;
import com.microsoft.xbox.idp.telemetry.utc.model.UTCCommonDataModel;
import com.microsoft.xbox.service.model.friendfinder.FriendFinderType;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.xle.telemetry.helpers.UTCEventTracker.UTCEventDelegate;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.FriendFinder;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageAction.FriendFinderFacebook;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView;
import com.microsoft.xbox.xle.telemetry.utc.model.UTCNames.PageView.FriendFinderContacts;
import org.mozilla.javascript.regexp.NativeRegExp;

public class UTCFriendFinder {
    private static String currentUserXuid;

    static /* synthetic */ class AnonymousClass36 {
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

    private static String[] formatXuids(String[] strArr) {
        for (int i = 0; i < strArr.length; i++) {
            String str = strArr[i];
            if (!str.startsWith("x:")) {
                strArr[i] = "x:" + str;
            }
        }
        return strArr;
    }

    private static void setCurrentUserXuid(String str) {
        currentUserXuid = str;
        setUserIdForCommonData();
    }

    private static void setUserIdForCommonData() {
        if (!JavaUtil.isNullOrEmpty(currentUserXuid)) {
            UTCCommonDataModel.setUserId(currentUserXuid);
        }
    }

    public static void trackAddFacebookFriend(final CharSequence charSequence, final String[] strArr) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
                uTCAdditionalInfoModel.addValue("selectedXUIDs", UTCFriendFinder.formatXuids(strArr));
                UTCPageAction.track(FriendFinderFacebook.AddFacebookFriend, charSequence, uTCAdditionalInfoModel);
            }
        });
    }

    public static void trackAddFacebookFriendCancel(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinderFacebook.AddFriendCancel, charSequence);
            }
        });
    }

    public static void trackBackButtonPressed(CharSequence charSequence, FriendFinderType friendFinderType) {
        switch (AnonymousClass36.$SwitchMap$com$microsoft$xbox$service$model$friendfinder$FriendFinderType[friendFinderType.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                trackFacebookFriendFinderBack(charSequence);
                return;
            case NativeRegExp.PREFIX /*2*/:
                trackPhoneContactsBack(charSequence);
                return;
            default:
                return;
        }
    }

    public static void trackContactsAddPhoneView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(FriendFinderContacts.AddPhoneView, charSequence);
            }
        });
    }

    public static void trackContactsFindFriendsView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(FriendFinderContacts.FindFriendsView, charSequence);
            }
        });
    }

    public static void trackContactsInviteFriendsView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(FriendFinderContacts.InviteFriendsView, charSequence);
            }
        });
    }

    public static void trackContactsOptInView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(FriendFinderContacts.OptInView, charSequence);
            }
        });
    }

    public static void trackContactsSignUp(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinder.ContactsSignUp, charSequence);
            }
        });
    }

    public static void trackContactsSuggestions(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinder.ContactsSuggestions, charSequence);
            }
        });
    }

    public static void trackContactsVerifyPhoneView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(FriendFinderContacts.VerifyPhoneView, charSequence);
            }
        });
    }

    public static void trackDone(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinder.Done, charSequence);
            }
        });
    }

    public static void trackFacebookAddFriendView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(PageView.FriendFinderFacebook.FindFriendsView, charSequence);
            }
        });
    }

    private static void trackFacebookFriendFinderBack(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinderFacebook.FriendFinderBack, charSequence);
                UTCPageView.removePage();
            }
        });
    }

    public static void trackFacebookLinkAccountView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(PageView.FriendFinderFacebook.LinkAccountView, charSequence);
            }
        });
    }

    public static void trackFacebookLoginCancel(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinderFacebook.LoginCancel, charSequence);
            }
        });
    }

    public static void trackFacebookLoginSuccessful(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinderFacebook.Login, charSequence);
            }
        });
    }

    public static void trackFacebookOptInNext(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinderFacebook.OptInNext, charSequence);
            }
        });
    }

    public static void trackFacebookOptInView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(PageView.FriendFinderFacebook.OptInView, charSequence);
            }
        });
    }

    public static void trackFacebookShareView(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageView.track(PageView.FriendFinderFacebook.ShareView, charSequence);
            }
        });
    }

    public static void trackFacebookSignup(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinder.FacebookSignup, charSequence);
            }
        });
    }

    public static void trackFacebookSuggestions(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinder.FacebookSuggestions, charSequence);
            }
        });
    }

    public static void trackFriendFinderView(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setCurrentUserXuid(str);
                UTCPageView.track(PageView.FriendFinder.FriendFinderView, charSequence);
            }
        });
    }

    public static void trackGamertagSearch(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinder.GamertagSearch, charSequence);
            }
        });
    }

    public static void trackGamertagSearchSubmit(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinder.GamertagSearchSubmit, charSequence);
            }
        });
    }

    public static void trackGamertagSearchSuccess(final CharSequence charSequence, final String str) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
                uTCAdditionalInfoModel.addValue(UTCDeepLink.TARGET_XUID_KEY, "x:" + str);
                UTCPageAction.track(FriendFinder.GamertagSearchSuccess, charSequence, uTCAdditionalInfoModel);
            }
        });
    }

    public static void trackPhoneContactsAddFriends(final CharSequence charSequence, final String[] strArr) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCAdditionalInfoModel uTCAdditionalInfoModel = new UTCAdditionalInfoModel();
                uTCAdditionalInfoModel.addValue("selectedXUIDs", UTCFriendFinder.formatXuids(strArr));
                UTCPageAction.track(PageAction.FriendFinderContacts.AddFriends, charSequence, uTCAdditionalInfoModel);
            }
        });
    }

    private static void trackPhoneContactsBack(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(PageAction.FriendFinderContacts.Close, charSequence);
                UTCPageView.removePage();
            }
        });
    }

    public static void trackPhoneContactsCallMe(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(PageAction.FriendFinderContacts.CallMe, charSequence);
            }
        });
    }

    public static void trackPhoneContactsChangeRegion(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(PageAction.FriendFinderContacts.ChangeRegion, charSequence);
            }
        });
    }

    public static void trackPhoneContactsLinkSuccess(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(PageAction.FriendFinderContacts.LinkSuccess, charSequence);
            }
        });
    }

    public static void trackPhoneContactsNext(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(PageAction.FriendFinderContacts.Next, charSequence);
            }
        });
    }

    public static void trackPhoneContactsResendCode(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(PageAction.FriendFinderContacts.ResendCode, charSequence);
            }
        });
    }

    public static void trackPhoneContactsSendInvite(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(PageAction.FriendFinderContacts.SendInvite, charSequence);
            }
        });
    }

    public static void trackPhoneContactsSkipAddFriends(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(PageAction.FriendFinderContacts.Skip, charSequence);
            }
        });
    }

    public static void trackShareFacebookLinkToFeed(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinderFacebook.ShareSuccess, charSequence);
            }
        });
    }

    public static void trackSkipFacebookSharing(final CharSequence charSequence) {
        UTCEventTracker.callTrackWrapper(new UTCEventDelegate() {
            public void call() {
                UTCFriendFinder.setUserIdForCommonData();
                UTCPageAction.track(FriendFinderFacebook.ShareCancel, charSequence);
            }
        });
    }
}
