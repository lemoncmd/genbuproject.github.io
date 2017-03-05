package com.microsoft.xbox.service.network.managers.friendfinder;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import com.microsoft.xbox.service.model.XPrivilegeConstants;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.network.XboxLiveEnvironment;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Set;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.texture.tga.TGAImage.Header;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Token;

public class PhoneContactInfo {
    public static final int MinimumPhoneLength = 7;
    private static PhoneContactInfo instance = new PhoneContactInfo();
    private ArrayList<Contact> contacts;
    private final String[][] countryCodes;
    private boolean isXboxContactsUpdated;
    private String phoneNumberFromSim;
    private String profilePhoneNumber;
    private String region;
    private String userEnteredNumber;

    public class Contact {
        public String displayName;
        public String id;
        public boolean isOnXbox;
        public boolean isSelected;
        public ArrayList<String> phoneNumbers;

        public Contact(String str, String str2) {
            this.id = str;
            this.displayName = str2;
        }

        public void addPhoneNumber(String str) {
            if (this.phoneNumbers == null) {
                this.phoneNumbers = new ArrayList();
            }
            this.phoneNumbers.add(str);
        }
    }

    private PhoneContactInfo() {
        int i = 0;
        String[][] strArr = new String[206][];
        strArr[0] = new String[]{"93", "AF", BuildConfig.FLAVOR};
        strArr[1] = new String[]{"355", "AL", BuildConfig.FLAVOR};
        strArr[2] = new String[]{"213", "DZ", BuildConfig.FLAVOR};
        strArr[3] = new String[]{"376", "AD", BuildConfig.FLAVOR};
        strArr[4] = new String[]{"244", "AO", BuildConfig.FLAVOR};
        strArr[5] = new String[]{"672", "AQ", BuildConfig.FLAVOR};
        strArr[6] = new String[]{"54", "AR", BuildConfig.FLAVOR};
        strArr[MinimumPhoneLength] = new String[]{"374", "AM", BuildConfig.FLAVOR};
        strArr[8] = new String[]{"297", "AW", BuildConfig.FLAVOR};
        strArr[9] = new String[]{"61", "AU", BuildConfig.FLAVOR};
        strArr[10] = new String[]{"43", "AT", BuildConfig.FLAVOR};
        strArr[11] = new String[]{"994", "AZ", BuildConfig.FLAVOR};
        strArr[12] = new String[]{"973", "BH", BuildConfig.FLAVOR};
        strArr[13] = new String[]{"880", "BD", BuildConfig.FLAVOR};
        strArr[14] = new String[]{"375", "BY", BuildConfig.FLAVOR};
        strArr[15] = new String[]{"32", "BE", BuildConfig.FLAVOR};
        strArr[16] = new String[]{"501", "BZ", BuildConfig.FLAVOR};
        strArr[17] = new String[]{"229", "BJ", BuildConfig.FLAVOR};
        strArr[18] = new String[]{"975", "BT", BuildConfig.FLAVOR};
        strArr[19] = new String[]{"591", "BO", BuildConfig.FLAVOR};
        strArr[20] = new String[]{"387", "BA", BuildConfig.FLAVOR};
        strArr[21] = new String[]{"267", "BW", BuildConfig.FLAVOR};
        strArr[22] = new String[]{"55", "BR", BuildConfig.FLAVOR};
        strArr[23] = new String[]{"673", "BN", BuildConfig.FLAVOR};
        strArr[24] = new String[]{"359", "BG", BuildConfig.FLAVOR};
        strArr[25] = new String[]{"226", "BF", BuildConfig.FLAVOR};
        strArr[26] = new String[]{"95", "MM", BuildConfig.FLAVOR};
        strArr[27] = new String[]{"257", "BI", BuildConfig.FLAVOR};
        strArr[28] = new String[]{"855", "KH", BuildConfig.FLAVOR};
        strArr[29] = new String[]{"237", "CM", BuildConfig.FLAVOR};
        strArr[30] = new String[]{XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION, "CA", BuildConfig.FLAVOR};
        strArr[31] = new String[]{"238", "CV", BuildConfig.FLAVOR};
        strArr[32] = new String[]{"236", "CF", BuildConfig.FLAVOR};
        strArr[33] = new String[]{"235", "TD", BuildConfig.FLAVOR};
        strArr[34] = new String[]{"56", "CL", BuildConfig.FLAVOR};
        strArr[35] = new String[]{"86", "CN", BuildConfig.FLAVOR};
        strArr[36] = new String[]{"61", "CX", BuildConfig.FLAVOR};
        strArr[37] = new String[]{"61", "CC", BuildConfig.FLAVOR};
        strArr[38] = new String[]{"57", "CO", BuildConfig.FLAVOR};
        strArr[39] = new String[]{"269", "KM", BuildConfig.FLAVOR};
        strArr[40] = new String[]{"242", "CG", BuildConfig.FLAVOR};
        strArr[41] = new String[]{"243", "CD", BuildConfig.FLAVOR};
        strArr[42] = new String[]{"682", "CK", BuildConfig.FLAVOR};
        strArr[43] = new String[]{"506", "CR", BuildConfig.FLAVOR};
        strArr[44] = new String[]{"385", "HR", BuildConfig.FLAVOR};
        strArr[45] = new String[]{"53", "CU", BuildConfig.FLAVOR};
        strArr[46] = new String[]{"357", "CY", BuildConfig.FLAVOR};
        strArr[47] = new String[]{"420", "CZ", BuildConfig.FLAVOR};
        strArr[48] = new String[]{"45", "DK", BuildConfig.FLAVOR};
        strArr[49] = new String[]{"253", "DJ", BuildConfig.FLAVOR};
        strArr[50] = new String[]{"670", "TL", BuildConfig.FLAVOR};
        strArr[51] = new String[]{"593", "EC", BuildConfig.FLAVOR};
        strArr[52] = new String[]{"20", "EG", BuildConfig.FLAVOR};
        strArr[53] = new String[]{"503", "SV", BuildConfig.FLAVOR};
        strArr[54] = new String[]{"240", "GQ", BuildConfig.FLAVOR};
        strArr[55] = new String[]{"291", "ER", BuildConfig.FLAVOR};
        strArr[56] = new String[]{"372", "EE", BuildConfig.FLAVOR};
        strArr[57] = new String[]{"251", "ET", BuildConfig.FLAVOR};
        strArr[58] = new String[]{"500", "FK", BuildConfig.FLAVOR};
        strArr[59] = new String[]{"298", "FO", BuildConfig.FLAVOR};
        strArr[60] = new String[]{"679", "FJ", BuildConfig.FLAVOR};
        strArr[61] = new String[]{"358", "FI", BuildConfig.FLAVOR};
        strArr[62] = new String[]{"33", "FR", BuildConfig.FLAVOR};
        strArr[63] = new String[]{"689", "PF", BuildConfig.FLAVOR};
        strArr[64] = new String[]{"241", "GA", BuildConfig.FLAVOR};
        strArr[65] = new String[]{"220", "GM", BuildConfig.FLAVOR};
        strArr[66] = new String[]{"995", "GE", BuildConfig.FLAVOR};
        strArr[67] = new String[]{"49", "DE", BuildConfig.FLAVOR};
        strArr[68] = new String[]{"233", "GH", BuildConfig.FLAVOR};
        strArr[69] = new String[]{"350", "GI", BuildConfig.FLAVOR};
        strArr[70] = new String[]{"30", "GR", BuildConfig.FLAVOR};
        strArr[71] = new String[]{"299", "GL", BuildConfig.FLAVOR};
        strArr[72] = new String[]{"502", "GT", BuildConfig.FLAVOR};
        strArr[73] = new String[]{"224", "GN", BuildConfig.FLAVOR};
        strArr[74] = new String[]{XPrivilegeConstants.XPRIVILEGE_PURCHASE_CONTENT, "GW", BuildConfig.FLAVOR};
        strArr[75] = new String[]{"592", "GY", BuildConfig.FLAVOR};
        strArr[76] = new String[]{"509", "HT", BuildConfig.FLAVOR};
        strArr[77] = new String[]{"504", "HN", BuildConfig.FLAVOR};
        strArr[78] = new String[]{"852", "HK", BuildConfig.FLAVOR};
        strArr[79] = new String[]{"36", "HU", BuildConfig.FLAVOR};
        strArr[80] = new String[]{"91", "IN", BuildConfig.FLAVOR};
        strArr[81] = new String[]{"62", "ID", BuildConfig.FLAVOR};
        strArr[82] = new String[]{"98", "IR", BuildConfig.FLAVOR};
        strArr[83] = new String[]{"964", "IQ", BuildConfig.FLAVOR};
        strArr[84] = new String[]{"353", "IE", BuildConfig.FLAVOR};
        strArr[85] = new String[]{"44", "IM", BuildConfig.FLAVOR};
        strArr[86] = new String[]{"972", "IL", BuildConfig.FLAVOR};
        strArr[87] = new String[]{"39", "IT", BuildConfig.FLAVOR};
        strArr[88] = new String[]{"225", "CI", BuildConfig.FLAVOR};
        strArr[89] = new String[]{"81", "JP", BuildConfig.FLAVOR};
        strArr[90] = new String[]{"962", "JO", BuildConfig.FLAVOR};
        strArr[91] = new String[]{"7", "KZ", BuildConfig.FLAVOR};
        strArr[92] = new String[]{"254", "KE", BuildConfig.FLAVOR};
        strArr[93] = new String[]{"686", "KI", BuildConfig.FLAVOR};
        strArr[94] = new String[]{"965", "KW", BuildConfig.FLAVOR};
        strArr[95] = new String[]{"996", "KG", BuildConfig.FLAVOR};
        strArr[96] = new String[]{"856", "LA", BuildConfig.FLAVOR};
        strArr[97] = new String[]{"371", "LV", BuildConfig.FLAVOR};
        strArr[98] = new String[]{"961", "LB", BuildConfig.FLAVOR};
        strArr[99] = new String[]{"266", "LS", BuildConfig.FLAVOR};
        strArr[100] = new String[]{"231", "LR", BuildConfig.FLAVOR};
        strArr[Token.ASSIGN_DIV] = new String[]{"218", "LY", BuildConfig.FLAVOR};
        strArr[Token.LAST_ASSIGN] = new String[]{"423", "LI", BuildConfig.FLAVOR};
        strArr[Token.HOOK] = new String[]{"370", "LT", BuildConfig.FLAVOR};
        strArr[Token.COLON] = new String[]{"352", "LU", BuildConfig.FLAVOR};
        strArr[Token.OR] = new String[]{"853", "MO", BuildConfig.FLAVOR};
        strArr[Token.AND] = new String[]{"389", "MK", BuildConfig.FLAVOR};
        strArr[Token.INC] = new String[]{"261", "MG", BuildConfig.FLAVOR};
        strArr[Token.DEC] = new String[]{"265", "MW", BuildConfig.FLAVOR};
        strArr[Token.DOT] = new String[]{"60", "MY", BuildConfig.FLAVOR};
        strArr[Token.FUNCTION] = new String[]{"960", "MV", BuildConfig.FLAVOR};
        strArr[Token.EXPORT] = new String[]{"223", "ML", BuildConfig.FLAVOR};
        strArr[Token.IMPORT] = new String[]{"356", "MT", BuildConfig.FLAVOR};
        strArr[Token.IF] = new String[]{"692", "MH", BuildConfig.FLAVOR};
        strArr[Token.ELSE] = new String[]{"222", "MR", BuildConfig.FLAVOR};
        strArr[Token.SWITCH] = new String[]{"230", "MU", BuildConfig.FLAVOR};
        strArr[Token.CASE] = new String[]{"262", "YT", BuildConfig.FLAVOR};
        strArr[Token.DEFAULT] = new String[]{"52", "MX", BuildConfig.FLAVOR};
        strArr[Token.WHILE] = new String[]{"691", "FM", BuildConfig.FLAVOR};
        strArr[Token.DO] = new String[]{"373", "MD", BuildConfig.FLAVOR};
        strArr[Token.FOR] = new String[]{"377", "MC", BuildConfig.FLAVOR};
        strArr[Token.BREAK] = new String[]{"976", "MN", BuildConfig.FLAVOR};
        strArr[Token.CONTINUE] = new String[]{"382", "ME", BuildConfig.FLAVOR};
        strArr[Token.VAR] = new String[]{"212", "MA", BuildConfig.FLAVOR};
        strArr[Token.WITH] = new String[]{"258", "MZ", BuildConfig.FLAVOR};
        strArr[Token.CATCH] = new String[]{"264", "NA", BuildConfig.FLAVOR};
        strArr[Token.FINALLY] = new String[]{"674", "NR", BuildConfig.FLAVOR};
        strArr[Token.VOID] = new String[]{"977", "NP", BuildConfig.FLAVOR};
        strArr[Token.RESERVED] = new String[]{"31", "NL", BuildConfig.FLAVOR};
        strArr[Token.EMPTY] = new String[]{"599", "AN", BuildConfig.FLAVOR};
        strArr[Token.BLOCK] = new String[]{"687", "NC", BuildConfig.FLAVOR};
        strArr[Token.LABEL] = new String[]{"64", "NZ", BuildConfig.FLAVOR};
        strArr[Token.TARGET] = new String[]{"505", "NI", BuildConfig.FLAVOR};
        strArr[Token.LOOP] = new String[]{"227", "NE", BuildConfig.FLAVOR};
        strArr[Token.EXPR_VOID] = new String[]{"234", "NG", BuildConfig.FLAVOR};
        strArr[Token.EXPR_RESULT] = new String[]{"683", "NU", BuildConfig.FLAVOR};
        strArr[Token.JSR] = new String[]{"850", "KP", BuildConfig.FLAVOR};
        strArr[Token.SCRIPT] = new String[]{"47", "NO", BuildConfig.FLAVOR};
        strArr[Token.TYPEOFNAME] = new String[]{"968", "OM", BuildConfig.FLAVOR};
        strArr[Token.USE_STACK] = new String[]{"92", "PK", BuildConfig.FLAVOR};
        strArr[Token.SETPROP_OP] = new String[]{"680", "PW", BuildConfig.FLAVOR};
        strArr[Token.SETELEM_OP] = new String[]{"507", "PA", BuildConfig.FLAVOR};
        strArr[Token.LOCAL_BLOCK] = new String[]{"675", "PG", BuildConfig.FLAVOR};
        strArr[Token.SET_REF_OP] = new String[]{"595", "PY", BuildConfig.FLAVOR};
        strArr[Token.DOTDOT] = new String[]{"51", "PE", BuildConfig.FLAVOR};
        strArr[Token.COLONCOLON] = new String[]{"63", "PH", BuildConfig.FLAVOR};
        strArr[Token.XML] = new String[]{"870", "PN", BuildConfig.FLAVOR};
        strArr[Token.DOTQUERY] = new String[]{"48", "PL", BuildConfig.FLAVOR};
        strArr[Token.XMLATTR] = new String[]{"351", "PT", BuildConfig.FLAVOR};
        strArr[Token.XMLEND] = new String[]{XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION, "PR", BuildConfig.FLAVOR};
        strArr[Token.TO_OBJECT] = new String[]{"974", "QA", BuildConfig.FLAVOR};
        strArr[Token.TO_DOUBLE] = new String[]{"40", "RO", BuildConfig.FLAVOR};
        strArr[Token.GET] = new String[]{"7", "RU", BuildConfig.FLAVOR};
        strArr[Token.SET] = new String[]{"250", "RW", BuildConfig.FLAVOR};
        strArr[Token.LET] = new String[]{"590", "BL", BuildConfig.FLAVOR};
        strArr[Token.CONST] = new String[]{"685", "WS", BuildConfig.FLAVOR};
        strArr[Token.SETCONST] = new String[]{"378", "SM", BuildConfig.FLAVOR};
        strArr[Token.SETCONSTVAR] = new String[]{"239", "ST", BuildConfig.FLAVOR};
        strArr[Token.ARRAYCOMP] = new String[]{"966", "SA", BuildConfig.FLAVOR};
        strArr[Token.LETEXPR] = new String[]{XPrivilegeConstants.XPRIVILEGE_PII_ACCESS, "SN", BuildConfig.FLAVOR};
        strArr[Token.WITHEXPR] = new String[]{"381", "RS", BuildConfig.FLAVOR};
        strArr[Token.DEBUGGER] = new String[]{"248", "SC", BuildConfig.FLAVOR};
        strArr[Token.COMMENT] = new String[]{"232", "SL", BuildConfig.FLAVOR};
        strArr[Token.GENEXPR] = new String[]{"65", "SG", BuildConfig.FLAVOR};
        strArr[Token.METHOD] = new String[]{"421", "SK", BuildConfig.FLAVOR};
        strArr[Token.ARROW] = new String[]{"386", "SI", BuildConfig.FLAVOR};
        strArr[Token.LAST_TOKEN] = new String[]{"677", "SB", BuildConfig.FLAVOR};
        strArr[167] = new String[]{XPrivilegeConstants.XPRIVILEGE_COMMUNICATIONS, "SO", BuildConfig.FLAVOR};
        strArr[168] = new String[]{"27", "ZA", BuildConfig.FLAVOR};
        strArr[169] = new String[]{"82", "KR", BuildConfig.FLAVOR};
        strArr[Context.VERSION_1_7] = new String[]{"34", "ES", BuildConfig.FLAVOR};
        strArr[171] = new String[]{"94", "LK", BuildConfig.FLAVOR};
        strArr[172] = new String[]{"290", "SH", BuildConfig.FLAVOR};
        strArr[173] = new String[]{"508", "PM", BuildConfig.FLAVOR};
        strArr[174] = new String[]{XPrivilegeConstants.XPRIVILEGE_PROFILE_VIEWING, "SD", BuildConfig.FLAVOR};
        strArr[175] = new String[]{"597", "SR", BuildConfig.FLAVOR};
        strArr[176] = new String[]{"268", "SZ", BuildConfig.FLAVOR};
        strArr[177] = new String[]{"46", "SE", BuildConfig.FLAVOR};
        strArr[178] = new String[]{"41", "CH", BuildConfig.FLAVOR};
        strArr[179] = new String[]{"963", "SY", BuildConfig.FLAVOR};
        strArr[Context.VERSION_1_8] = new String[]{"886", "TW", BuildConfig.FLAVOR};
        strArr[181] = new String[]{"992", "TJ", BuildConfig.FLAVOR};
        strArr[182] = new String[]{XPrivilegeConstants.XPRIVILEGE_ADD_FRIEND, "TZ", BuildConfig.FLAVOR};
        strArr[183] = new String[]{"66", "TH", BuildConfig.FLAVOR};
        strArr[184] = new String[]{"228", "TG", BuildConfig.FLAVOR};
        strArr[185] = new String[]{"690", "TK", BuildConfig.FLAVOR};
        strArr[186] = new String[]{"676", "TO", BuildConfig.FLAVOR};
        strArr[187] = new String[]{"216", "TN", BuildConfig.FLAVOR};
        strArr[188] = new String[]{"90", "TR", BuildConfig.FLAVOR};
        strArr[189] = new String[]{"993", "TM", BuildConfig.FLAVOR};
        strArr[190] = new String[]{"688", "TV", BuildConfig.FLAVOR};
        strArr[191] = new String[]{"971", "AE", BuildConfig.FLAVOR};
        strArr[Header.ID_INTERLEAVE] = new String[]{"256", "UG", BuildConfig.FLAVOR};
        strArr[193] = new String[]{"44", "GB", BuildConfig.FLAVOR};
        strArr[194] = new String[]{"380", "UA", BuildConfig.FLAVOR};
        strArr[195] = new String[]{"598", "UY", BuildConfig.FLAVOR};
        strArr[196] = new String[]{XboxLiveEnvironment.SOCIAL_SERVICE_GENERAL_CONTRACT_VERSION, "US", BuildConfig.FLAVOR};
        strArr[197] = new String[]{"998", "UZ", BuildConfig.FLAVOR};
        strArr[198] = new String[]{"678", "VU", BuildConfig.FLAVOR};
        strArr[199] = new String[]{"39", "VA", BuildConfig.FLAVOR};
        strArr[Context.VERSION_ES6] = new String[]{"58", "VE", BuildConfig.FLAVOR};
        strArr[201] = new String[]{"84", "VN", BuildConfig.FLAVOR};
        strArr[202] = new String[]{"681", "WF", BuildConfig.FLAVOR};
        strArr[203] = new String[]{"967", "YE", BuildConfig.FLAVOR};
        strArr[204] = new String[]{"260", "ZM", BuildConfig.FLAVOR};
        strArr[205] = new String[]{"263", "ZW", BuildConfig.FLAVOR};
        this.countryCodes = strArr;
        while (i < this.countryCodes.length) {
            String displayCountry = new Locale(BuildConfig.FLAVOR, this.countryCodes[i][1]).getDisplayCountry();
            XLEAssert.assertFalse("Failed to get country name : " + this.countryCodes[i][1], JavaUtil.isNullOrEmpty(displayCountry));
            this.countryCodes[i][2] = displayCountry;
            i++;
        }
    }

    public static PhoneContactInfo getInstance() {
        return instance;
    }

    public static String normalizePhoneNumber(String str) {
        if (str != null && str.length() >= MinimumPhoneLength) {
            String toLowerCase = str.toLowerCase();
            if (toLowerCase.indexOf("ext") < 0 && toLowerCase.indexOf("x") < 0) {
                StringBuffer stringBuffer = new StringBuffer(toLowerCase.length());
                for (int i = 0; i < toLowerCase.length(); i++) {
                    char charAt = toLowerCase.charAt(i);
                    if (Character.isDigit(charAt)) {
                        stringBuffer.append(charAt);
                    }
                }
                if (stringBuffer.length() >= MinimumPhoneLength) {
                    return stringBuffer.toString();
                }
            }
        }
        return null;
    }

    public static String sha2Encryption(String str) {
        if (JavaUtil.isNullOrEmpty(str)) {
            return str;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.reset();
            byte[] digest = instance.digest(str.getBytes(HttpURLConnectionBuilder.DEFAULT_CHARSET));
            return Base64.encodeToString(digest, 0, digest.length, 10);
        } catch (NoSuchAlgorithmException e) {
            return null;
        } catch (UnsupportedEncodingException e2) {
            return null;
        }
    }

    public ArrayList<Contact> getContacts() {
        if (this.contacts == null) {
            try {
                ContentResolver contentResolver = XboxTcuiSdk.getContentResolver();
                Cursor query = contentResolver.query(Contacts.CONTENT_URI, null, null, null, null);
                if (query == null) {
                    return null;
                }
                while (query.moveToNext()) {
                    String string = query.getString(query.getColumnIndex("_id"));
                    String string2 = query.getString(query.getColumnIndex("display_name"));
                    if (Integer.parseInt(query.getString(query.getColumnIndex("has_phone_number"))) > 0) {
                        Cursor query2 = contentResolver.query(Phone.CONTENT_URI, null, "contact_id = ?", new String[]{string}, null);
                        String countryCode = getCountryCode();
                        Contact contact = null;
                        while (query2.moveToNext()) {
                            String string3 = query2.getString(query2.getColumnIndex("data1"));
                            if (!JavaUtil.isNullOrEmpty(string3)) {
                                string3 = normalizePhoneNumber(string3);
                                if (!JavaUtil.isNullOrEmpty(string3)) {
                                    if (contact == null) {
                                        contact = new Contact(string, string2);
                                    }
                                    contact.addPhoneNumber(string3);
                                    if (!(JavaUtil.isNullOrEmpty(countryCode) || string3.startsWith(countryCode))) {
                                        contact.addPhoneNumber(countryCode + string3);
                                    }
                                }
                            }
                        }
                        if (!(contact == null || XLEUtil.isNullOrEmpty(contact.phoneNumbers))) {
                            if (this.contacts == null) {
                                this.contacts = new ArrayList();
                            }
                            this.contacts.add(contact);
                        }
                        query2.close();
                    }
                }
                query.close();
            } catch (SecurityException e) {
                return null;
            }
        }
        return this.contacts;
    }

    public String getContryCodeFromRegion(String str) {
        for (int i = 0; i < this.countryCodes.length; i++) {
            if (TextUtils.equals(str, this.countryCodes[i][1])) {
                return this.countryCodes[i][0];
            }
        }
        return null;
    }

    public String getCountryCode() {
        return getContryCodeFromRegion(getRegion());
    }

    public String getCountryNameFromRegion(String str) {
        for (int i = 0; i < this.countryCodes.length; i++) {
            if (TextUtils.equals(str, this.countryCodes[i][1])) {
                return this.countryCodes[i][2];
            }
        }
        return null;
    }

    public ArrayList<String> getCountryNames() {
        Object arrayList = new ArrayList();
        for (Object[] objArr : this.countryCodes) {
            arrayList.add(objArr[2]);
        }
        Collections.sort(arrayList);
        return arrayList;
    }

    public String getPhoneNumberFromSim() {
        if (this.phoneNumberFromSim == null) {
            try {
                String line1Number = ((TelephonyManager) XboxTcuiSdk.getSystemService("phone")).getLine1Number();
                String region = getRegion();
                if (!(JavaUtil.isNullOrEmpty(line1Number) || JavaUtil.isNullOrEmpty(region))) {
                    String countryCode = getCountryCode();
                    if (line1Number.startsWith(countryCode)) {
                        this.region = region;
                        this.phoneNumberFromSim = line1Number.substring(countryCode.length());
                    }
                }
            } catch (SecurityException e) {
                this.phoneNumberFromSim = BuildConfig.FLAVOR;
            }
        }
        return this.phoneNumberFromSim;
    }

    public String getProfileNumber() {
        return this.profilePhoneNumber;
    }

    public String getRegion() {
        if (this.region == null) {
            this.region = ((TelephonyManager) XboxTcuiSdk.getSystemService("phone")).getSimCountryIso().toUpperCase();
        }
        if (JavaUtil.isNullOrEmpty(this.region)) {
            this.region = Locale.getDefault().getCountry();
        }
        return this.region;
    }

    public String getRegionFromCountryName(String str) {
        for (int i = 0; i < this.countryCodes.length; i++) {
            if (TextUtils.equals(str, this.countryCodes[i][2])) {
                return this.countryCodes[i][1];
            }
        }
        return null;
    }

    public String getRegionWithCode() {
        String region = getInstance().getRegion();
        String countryCode = getInstance().getCountryCode();
        return (JavaUtil.isNullOrEmpty(region) || JavaUtil.isNullOrEmpty(countryCode)) ? null : region + "-" + countryCode;
    }

    public String getUserEnteredNumber() {
        return this.userEnteredNumber;
    }

    public boolean isXboxContactsUpdated() {
        return this.isXboxContactsUpdated;
    }

    public void setProfileNumber(String str) {
        this.profilePhoneNumber = str;
    }

    public void setUserEnteredNumber(String str) {
        this.userEnteredNumber = str;
    }

    public void updateXboxContacts(Set<String> set) {
        this.isXboxContactsUpdated = true;
        if (!XLEUtil.isNullOrEmpty((Iterable) set)) {
            Enumeration enumeration = Collections.enumeration(this.contacts);
            while (enumeration.hasMoreElements() && !set.isEmpty()) {
                Contact contact = (Contact) enumeration.nextElement();
                if (set.contains(contact.id)) {
                    set.remove(contact.id);
                    contact.isOnXbox = true;
                }
            }
        }
    }
}
