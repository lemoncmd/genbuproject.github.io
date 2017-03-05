package com.microsoft.xbox.toolkit.network;

import com.microsoft.xbox.toolkit.XLEAssert;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class XboxLiveEnvironment {
    public static final String NEVER_LIST_CONTRACT_VERSION = "1";
    public static final String SHARE_IDENTITY_CONTRACT_VERSION = "4";
    public static final String SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION = "1";
    public static final String USER_PROFILE_CONTRACT_VERSION = "2";
    public static final String USER_PROFILE_PRIVACY_SETTINGS_CONTRACT_VERSION = "4";
    private static XboxLiveEnvironment instance = new XboxLiveEnvironment();
    private Environment environment = Environment.PROD;
    private final boolean useProxy = false;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment = new int[Environment.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[Environment.VINT.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[Environment.DNET.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[Environment.PARTNERNET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[Environment.PROD.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    public enum Environment {
        STUB,
        VINT,
        CERTNET,
        PARTNERNET,
        PROD,
        DNET
    }

    public static XboxLiveEnvironment Instance() {
        return instance;
    }

    public String getAddFriendsToShareIdentityUrlFormat() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[this.environment.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return "https://social.dnet.xboxlive.com/users/xuid(%s)/people/identityshared/xuids?method=add";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "https://social.xboxlive.com/users/xuid(%s)/people/identityshared/xuids?method=add";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public String getFriendFinderSettingsUrl() {
        return "https://settings.xboxlive.com/settings/feature/friendfinder/settings";
    }

    public String getGamertagSearchUrlFormat() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[this.environment.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return "https://profile.dnet.xboxlive.com/users/gt(%s)/profile/settings?settings=AppDisplayName,DisplayPic,Gamerscore,Gamertag,PublicGamerpic,XboxOneRep";
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return "https://profile.dnet.xboxlive.com/users/gt(%s)/profile/settings?settings=AppDisplayName,DisplayPic,Gamerscore,Gamertag,PublicGamerpic,XboxOneRep";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "https://profile.xboxlive.com/users/gt(%s)/profile/settings?settings=AppDisplayName,DisplayPic,Gamerscore,Gamertag,PublicGamerpic,XboxOneRep";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getMutedServiceUrlFormat() {
        return "https://privacy.xboxlive.com/users/xuid(%s)/people/mute";
    }

    public String getPeopleHubFriendFinderStateUrlFormat() {
        return "https://peoplehub.xboxlive.com/users/me/friendfinder";
    }

    public String getPeopleHubRecommendationsUrlFormat() {
        return "https://peoplehub.xboxlive.com/users/me/people/recommendations";
    }

    public String getProfileFavoriteListUrl() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[this.environment.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return "https://social.dnet.xboxlive.com/users/me/people/favorites/xuids?method=%s";
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return "https://social.dnet.xboxlive.com/users/me/people/favorites/xuids?method=%s";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "https://social.xboxlive.com/users/me/people/favorites/xuids?method=%s";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getProfileNeverListUrlFormat() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[this.environment.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return "https://privacy.dnet.xboxlive.com/users/xuid(%s)/people/never";
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return "https://privacy.dnet.xboxlive.com/users/xuid(%s)/people/never";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "https://privacy.xboxlive.com/users/xuid(%s)/people/never";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getProfileSettingUrlFormat() {
        return "https://privacy.xboxlive.com/users/me/privacy/settings/%s";
    }

    public String getProfileSummaryUrlFormat() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[this.environment.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return "https://social.dnet.xboxlive.com/users/xuid(%s)/summary";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "https://social.xboxlive.com/users/xuid(%s)/summary";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public boolean getProxyEnabled() {
        return false;
    }

    public String getRemoveUsersFromShareIdentityUrlFormat() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[this.environment.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return "https://social.dnet.xboxlive.com/users/xuid(%s)/people/identityshared/xuids?method=remove";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "https://social.xboxlive.com/users/xuid(%s)/people/identityshared/xuids?method=remove";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getSetFriendFinderOptInStatusUrlFormat() {
        return "https://friendfinder.xboxlive.com/users/me/networks/%s/optin";
    }

    public String getShortCircuitProfileUrlFormat() {
        return "https://pf.directory.live.com/profile/mine/System.ShortCircuitProfile.json";
    }

    public String getSubmitFeedbackUrlFormat() {
        return "https://reputation.xboxlive.com/users/xuid(%s)/feedback";
    }

    public String getTenureWatermarkUrlFormat() {
        return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/tenure/%s.png";
    }

    public String getUpdateThirdPartyTokenUrlFormat() {
        return "https://thirdpartytokens.xboxlive.com/users/me/networks/%s/token";
    }

    public String getUploadingPhoneContactsUrlFormat() {
        return "https://people.directory.live.com/people/ExternalSCDLookup";
    }

    public String getUserProfileInfoUrl() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[this.environment.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return "https://profile.dnet.xboxlive.com/users/batch/profile/settings";
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return "https://profile.dnet.xboxlive.com/users/batch/profile/settings";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "https://profile.xboxlive.com/users/batch/profile/settings";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getUserProfileSettingUrlFormat() {
        return "https://privacy.xboxlive.com/users/me/privacy/settings";
    }

    public String getWatermarkUrl(String str) {
        String toLowerCase = str.toLowerCase();
        Object obj = -1;
        switch (toLowerCase.hashCode()) {
            case -1921480520:
                if (toLowerCase.equals("nxeteam")) {
                    obj = 4;
                    break;
                }
                break;
            case -69693424:
                if (toLowerCase.equals("xboxoneteam")) {
                    obj = 6;
                    break;
                }
                break;
            case 467871267:
                if (toLowerCase.equals("kinectteam")) {
                    obj = 5;
                    break;
                }
                break;
            case 547378320:
                if (toLowerCase.equals("launchteam")) {
                    obj = 3;
                    break;
                }
                break;
            case 742262976:
                if (toLowerCase.equals("cheater")) {
                    obj = null;
                    break;
                }
                break;
            case 949652176:
                if (toLowerCase.equals("xboxnxoeteam")) {
                    obj = 7;
                    break;
                }
                break;
            case 1584505217:
                if (toLowerCase.equals("xboxoriginalteam")) {
                    obj = 1;
                    break;
                }
                break;
            case 2056113039:
                if (toLowerCase.equals("xboxlivelaunchteam")) {
                    obj = 2;
                    break;
                }
                break;
        }
        switch (obj) {
            case NativeRegExp.TEST /*0*/:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/cheater.png";
            case NativeRegExp.MATCH /*1*/:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/xboxoriginalteam.png";
            case NativeRegExp.PREFIX /*2*/:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/xboxlivelaunchteam.png";
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/launchteam.png";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/nxeteam.png";
            case Token.GOTO /*5*/:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/kinectteam.png";
            case Token.IFEQ /*6*/:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/xboxoneteam.png";
            case Token.IFNE /*7*/:
                return "http://dlassets.xboxlive.com/public/content/ppl/watermarks/launch/xboxnxoeteam.png";
            default:
                XLEAssert.fail("Unsupported watermark value: " + str);
                return BuildConfig.FLAVOR;
        }
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String updateProfileFollowingListUrl() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[this.environment.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.PREFIX /*2*/:
                return "https://social.dnet.xboxlive.com/users/me/people/xuids?method=%s";
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return "https://social.dnet.xboxlive.com/users/me/people/xuids?method=%s";
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "https://social.xboxlive.com/users/me/people/xuids?method=%s";
            default:
                throw new UnsupportedOperationException();
        }
    }
}
