package com.microsoft.xbox.xle.app;

import android.content.res.Configuration;
import android.util.Base64;
import android.util.DisplayMetrics;
import com.microsoft.xbox.service.model.ProfileModel;
import com.microsoft.xbox.toolkit.GsonUtil;
import com.microsoft.xbox.toolkit.IProjectSpecificDataProvider;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment.Environment;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class XleProjectSpecificDataProvider implements IProjectSpecificDataProvider {
    private static final String[][] displayLocales;
    private static XleProjectSpecificDataProvider instance = new XleProjectSpecificDataProvider();
    private String androidId;
    private Set<String> blockFeaturedChild = new HashSet();
    private boolean gotSettings;
    private boolean isMeAdult;
    private String meXuid;
    private Set<String> musicBlocked = new HashSet();
    private String privileges;
    private Set<String> promotionalRestrictedRegions = new HashSet();
    private Set<String> purchaseBlocked = new HashSet();
    private String scdRpsTicket;
    private Hashtable<String, String> serviceLocaleMapTable = new Hashtable();
    private String[][] serviceLocales;
    private Set<String> videoBlocked = new HashSet();

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment = new int[Environment.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[Environment.PROD.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[Environment.VINT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[Environment.DNET.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[Environment.PARTNERNET.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
        }
    }

    private class ContentRestrictions {
        public Data data = new Data();
        public int version = 2;

        public class Data {
            public String geographicRegion;
            public int maxAgeRating;
            public int preferredAgeRating;
            public boolean restrictPromotionalContent;
        }

        public ContentRestrictions(String str, int i, boolean z) {
            this.data.geographicRegion = str;
            Data data = this.data;
            this.data.preferredAgeRating = i;
            data.maxAgeRating = i;
            this.data.restrictPromotionalContent = z;
        }
    }

    static {
        r0 = new String[23][];
        r0[0] = new String[]{"zh_SG", "zh", "CN"};
        r0[1] = new String[]{"zh_CN", "zh", "CN"};
        r0[2] = new String[]{"zh_HK", "zh", "TW"};
        r0[3] = new String[]{"zh_TW", "zh", "TW"};
        r0[4] = new String[]{"da", "da", "DK"};
        r0[5] = new String[]{"nl", "nl", "NL"};
        r0[6] = new String[]{"en", "en", "GB"};
        r0[7] = new String[]{"en_US", "en", "US"};
        r0[8] = new String[]{"fi", "fi", "FI"};
        r0[9] = new String[]{"fr", "fr", "FR"};
        r0[10] = new String[]{"de", "de", "DE"};
        r0[11] = new String[]{"it", "it", "IT"};
        r0[12] = new String[]{"ja", "ja", "JP"};
        r0[13] = new String[]{"ko", "ko", "KR"};
        r0[14] = new String[]{"nb", "nb", "NO"};
        r0[15] = new String[]{"pl", "pl", "PL"};
        r0[16] = new String[]{"pt_PT", "pt", "PT"};
        r0[17] = new String[]{"pt", "pt", "BR"};
        r0[18] = new String[]{"ru", "ru", "RU"};
        r0[19] = new String[]{"es_ES", "es", "ES"};
        r0[20] = new String[]{"es", "es", "MX"};
        r0[21] = new String[]{"sv", "sv", "SE"};
        r0[22] = new String[]{"tr", "tr", "TR"};
        displayLocales = r0;
    }

    private XleProjectSpecificDataProvider() {
        String[][] strArr = new String[89][];
        strArr[0] = new String[]{"es_AR", "es-AR"};
        strArr[1] = new String[]{"AR", "es-AR"};
        strArr[2] = new String[]{"en_AU", "en-AU"};
        strArr[3] = new String[]{"AU", "en-AU"};
        strArr[4] = new String[]{"de_AT", "de-AT"};
        strArr[5] = new String[]{"AT", "de-AT"};
        strArr[6] = new String[]{"fr_BE", "fr-BE"};
        strArr[7] = new String[]{"nl_BE", "nl-BE"};
        strArr[8] = new String[]{"BE", "fr-BE"};
        strArr[9] = new String[]{"pt_BR", "pt-BR"};
        strArr[10] = new String[]{"BR", "pt-BR"};
        strArr[11] = new String[]{"en_CA", "en-CA"};
        strArr[12] = new String[]{"fr_CA", "fr-CA"};
        strArr[13] = new String[]{"CA", "en-CA"};
        strArr[14] = new String[]{"en_CZ", "en-CZ"};
        strArr[15] = new String[]{"CZ", "en-CZ"};
        strArr[16] = new String[]{"da_DK", "da-DK"};
        strArr[17] = new String[]{"DK", "da-DK"};
        strArr[18] = new String[]{"fi_FI", "fi-FI"};
        strArr[19] = new String[]{"FI", "fi-FI"};
        strArr[20] = new String[]{"fr_FR", "fr-FR"};
        strArr[21] = new String[]{"FR", "fr-FR"};
        strArr[22] = new String[]{"de_DE", "de-DE"};
        strArr[23] = new String[]{"DE", "de-DE"};
        strArr[24] = new String[]{"en_GR", "en-GR"};
        strArr[25] = new String[]{"GR", "en-GR"};
        strArr[26] = new String[]{"en_HK", "en-HK"};
        strArr[27] = new String[]{"zh_HK", "zh-HK"};
        strArr[28] = new String[]{"HK", "en-HK"};
        strArr[29] = new String[]{"en_HU", "en-HU"};
        strArr[30] = new String[]{"HU", "en-HU"};
        strArr[31] = new String[]{"en_IN", "en-IN"};
        strArr[32] = new String[]{"IN", "en-IN"};
        strArr[33] = new String[]{"en_GB", "en-GB"};
        strArr[34] = new String[]{"GB", "en-GB"};
        strArr[35] = new String[]{"en_IL", "en-IL"};
        strArr[36] = new String[]{"IL", "en-IL"};
        strArr[37] = new String[]{"it_IT", "it-IT"};
        strArr[38] = new String[]{"IT", "it-IT"};
        strArr[39] = new String[]{"ja_JP", "ja-JP"};
        strArr[40] = new String[]{"JP", "ja-JP"};
        strArr[41] = new String[]{"zh_CN", "zh-CN"};
        strArr[42] = new String[]{"CN", "zh-CN"};
        strArr[43] = new String[]{"es_MX", "es-MX"};
        strArr[44] = new String[]{"MX", "es-MX"};
        strArr[45] = new String[]{"es_CL", "es-CL"};
        strArr[46] = new String[]{"CL", "es-CL"};
        strArr[47] = new String[]{"es_CO", "es-CO"};
        strArr[48] = new String[]{"CO", "es-CO"};
        strArr[49] = new String[]{"nl_NL", "nl-NL"};
        strArr[50] = new String[]{"NL", "nl-NL"};
        strArr[51] = new String[]{"en_NZ", "en-NZ"};
        strArr[52] = new String[]{"NZ", "en-NZ"};
        strArr[53] = new String[]{"nb_NO", "nb-NO"};
        strArr[54] = new String[]{"NO", "nb-NO"};
        strArr[55] = new String[]{"pl_PL", "pl-PL"};
        strArr[56] = new String[]{"PL", "pl-PL"};
        strArr[57] = new String[]{"pt_PT", "pt-PT"};
        strArr[58] = new String[]{"PT", "pt-PT"};
        strArr[59] = new String[]{"ru_RU", "ru-RU"};
        strArr[60] = new String[]{"RU", "ru-RU"};
        strArr[61] = new String[]{"en_SA", "en-SA"};
        strArr[62] = new String[]{"SA", "en-SA"};
        strArr[63] = new String[]{"en_SG", "en-SG"};
        strArr[64] = new String[]{"zh_SG", "zh-SG"};
        strArr[65] = new String[]{"SG", "en-SG"};
        strArr[66] = new String[]{"en_SK", "en-SK"};
        strArr[67] = new String[]{"SK", "en-SK"};
        strArr[68] = new String[]{"en_ZA", "en-ZA"};
        strArr[69] = new String[]{"ZA", "en-ZA"};
        strArr[70] = new String[]{"ko_KR", "ko-KR"};
        strArr[71] = new String[]{"KR", "ko-KR"};
        strArr[72] = new String[]{"es_ES", "es-ES"};
        strArr[73] = new String[]{"es", "es-ES"};
        strArr[74] = new String[]{"de_CH", "de-CH"};
        strArr[75] = new String[]{"fr_CH", "fr-CH"};
        strArr[76] = new String[]{"CH", "fr-CH"};
        strArr[77] = new String[]{"zh_TW", "zh-TW"};
        strArr[78] = new String[]{"TW", "zh-TW"};
        strArr[79] = new String[]{"en_AE", "en-AE"};
        strArr[80] = new String[]{"AE", "en-AE"};
        strArr[81] = new String[]{"en_US", "en-US"};
        strArr[82] = new String[]{"US", "en-US"};
        strArr[83] = new String[]{"sv_SE", "sv-SE"};
        strArr[84] = new String[]{"SE", "sv-SE"};
        strArr[85] = new String[]{"tr_Tr", "tr-TR"};
        strArr[86] = new String[]{"Tr", "tr-TR"};
        strArr[87] = new String[]{"en_IE", "en-IE"};
        strArr[88] = new String[]{"IE", "en-IE"};
        this.serviceLocales = strArr;
        for (int i = 0; i < this.serviceLocales.length; i++) {
            this.serviceLocaleMapTable.put(this.serviceLocales[i][0], this.serviceLocales[i][1]);
        }
        this.serviceLocales = (String[][]) null;
    }

    private void addRegions(String str, Set<String> set) {
        if (!JavaUtil.isNullOrEmpty(str)) {
            Object[] split = str.split("[|]");
            if (!XLEUtil.isNullOrEmpty(split)) {
                set.clear();
                for (String str2 : split) {
                    if (!JavaUtil.isNullOrEmpty(str2)) {
                        set.add(str2);
                    }
                }
            }
        }
    }

    private String getDeviceLocale() {
        Locale locale = Locale.getDefault();
        String locale2 = locale.toString();
        if (this.serviceLocaleMapTable.containsKey(locale2)) {
            return (String) this.serviceLocaleMapTable.get(locale2);
        }
        String country = locale.getCountry();
        return (JavaUtil.isNullOrEmpty(country) || !this.serviceLocaleMapTable.containsKey(country)) ? "en-US" : (String) this.serviceLocaleMapTable.get(country);
    }

    public static XleProjectSpecificDataProvider getInstance() {
        return instance;
    }

    public void ensureDisplayLocale() {
        Locale locale = null;
        Locale locale2 = Locale.getDefault();
        String locale3 = locale2.toString();
        String language = locale2.getLanguage();
        String country = locale2.getCountry();
        int i = 0;
        while (i < displayLocales.length) {
            if (!displayLocales[i][0].equals(locale3)) {
                i++;
            } else if (!displayLocales[i][1].equals(language) || !displayLocales[i][2].equals(country)) {
                locale = new Locale(displayLocales[i][1], displayLocales[i][2]);
                if (locale == null) {
                    for (i = 0; i < displayLocales.length; i++) {
                        if (displayLocales[i][0].equals(language)) {
                            locale2 = new Locale(displayLocales[i][1], displayLocales[i][2]);
                            break;
                        }
                    }
                }
                locale2 = locale;
                if (locale2 != null) {
                    DisplayMetrics displayMetrics = XboxTcuiSdk.getResources().getDisplayMetrics();
                    Configuration configuration = XboxTcuiSdk.getResources().getConfiguration();
                    configuration.locale = locale2;
                    XboxTcuiSdk.getResources().updateConfiguration(configuration, displayMetrics);
                }
            } else {
                return;
            }
        }
        if (locale == null) {
            for (i = 0; i < displayLocales.length; i++) {
                if (displayLocales[i][0].equals(language)) {
                    locale2 = new Locale(displayLocales[i][1], displayLocales[i][2]);
                    break;
                }
            }
        }
        locale2 = locale;
        if (locale2 != null) {
            DisplayMetrics displayMetrics2 = XboxTcuiSdk.getResources().getDisplayMetrics();
            Configuration configuration2 = XboxTcuiSdk.getResources().getConfiguration();
            configuration2.locale = locale2;
            XboxTcuiSdk.getResources().updateConfiguration(configuration2, displayMetrics2);
        }
    }

    public boolean getAllowExplicitContent() {
        return true;
    }

    public String getAutoSuggestdDataSource() {
        return "bbxall2";
    }

    public String getCombinedContentRating() {
        return BuildConfig.FLAVOR;
    }

    public String getConnectedLocale() {
        return getDeviceLocale();
    }

    public String getConnectedLocale(boolean z) {
        return getConnectedLocale();
    }

    public String getContentRestrictions() {
        String region = getRegion();
        int meMaturityLevel = getMeMaturityLevel();
        if (!(JavaUtil.isNullOrEmpty(region) || meMaturityLevel == 255)) {
            region = GsonUtil.toJsonString(new ContentRestrictions(region, meMaturityLevel, isPromotionalRestricted()));
            if (!JavaUtil.isNullOrEmpty(region)) {
                return Base64.encodeToString(region.getBytes(), 2);
            }
        }
        return null;
    }

    public String getCurrentSandboxID() {
        return "PROD";
    }

    public boolean getInitializeComplete() {
        return getXuidString() != null;
    }

    public boolean getIsForXboxOne() {
        return true;
    }

    public boolean getIsFreeAccount() {
        return false;
    }

    public boolean getIsXboxMusicSupported() {
        return true;
    }

    public String getLegalLocale() {
        return getConnectedLocale();
    }

    public int getMeMaturityLevel() {
        ProfileModel meProfileModel = ProfileModel.getMeProfileModel();
        return meProfileModel != null ? meProfileModel.getMaturityLevel() : 0;
    }

    public String getMembershipLevel() {
        return ProfileModel.getMeProfileModel().getAccountTier() == null ? "Gold" : ProfileModel.getMeProfileModel().getAccountTier();
    }

    public String getPrivileges() {
        return this.privileges;
    }

    public String getRegion() {
        return Locale.getDefault().getCountry();
    }

    public String getSCDRpsTicket() {
        return this.scdRpsTicket;
    }

    public String getVersionCheckUrl() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[XboxLiveEnvironment.Instance().getEnvironment().ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "http://www.xbox.com/en-US/Platform/Android/XboxLIVE/sgversion";
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return "http://www.rtm.vint.xbox.com/en-US/Platform/Android/XboxLIVE/sgversion";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public int getVersionCode() {
        return 1;
    }

    public String getWindowsLiveClientId() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$network$XboxLiveEnvironment$Environment[XboxLiveEnvironment.Instance().getEnvironment().ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return "0000000048093EE3";
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return "0000000068036303";
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getXuidString() {
        return this.meXuid;
    }

    public boolean gotSettings() {
        return this.gotSettings;
    }

    public boolean isDeviceLocaleKnown() {
        Locale locale = Locale.getDefault();
        if (this.serviceLocaleMapTable.containsKey(locale.toString())) {
            return true;
        }
        String country = locale.getCountry();
        return !JavaUtil.isNullOrEmpty(country) && this.serviceLocaleMapTable.containsKey(country);
    }

    public boolean isFeaturedBlocked() {
        return !isMeAdult() && this.blockFeaturedChild.contains(getRegion());
    }

    public boolean isMeAdult() {
        return this.isMeAdult;
    }

    public boolean isMusicBlocked() {
        return true;
    }

    public boolean isPromotionalRestricted() {
        return !isMeAdult() && this.promotionalRestrictedRegions.contains(getRegion());
    }

    public boolean isPurchaseBlocked() {
        return this.purchaseBlocked.contains(getRegion());
    }

    public boolean isVideoBlocked() {
        return true;
    }

    public void processContentBlockedList(SmartglassSettings smartglassSettings) {
        addRegions(smartglassSettings.VIDEO_BLOCKED, this.videoBlocked);
        addRegions(smartglassSettings.MUSIC_BLOCKED, this.musicBlocked);
        addRegions(smartglassSettings.PURCHASE_BLOCKED, this.purchaseBlocked);
        addRegions(smartglassSettings.BLOCK_FEATURED_CHILD, this.blockFeaturedChild);
        addRegions(smartglassSettings.PROMOTIONAL_CONTENT_RESTRICTED_REGIONS, this.promotionalRestrictedRegions);
        this.gotSettings = true;
    }

    public void resetModels(boolean z) {
        ProfileModel.reset();
    }

    public void setIsMeAdult(boolean z) {
        this.isMeAdult = z;
    }

    public void setPrivileges(String str) {
        this.privileges = str;
    }

    public void setSCDRpsTicket(String str) {
        this.scdRpsTicket = str;
    }

    public void setXuidString(String str) {
        this.meXuid = str;
    }
}
