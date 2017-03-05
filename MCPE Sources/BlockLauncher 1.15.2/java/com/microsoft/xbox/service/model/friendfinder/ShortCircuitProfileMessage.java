package com.microsoft.xbox.service.model.friendfinder;

import android.provider.Settings.Secure;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo;
import com.microsoft.xbox.service.network.managers.friendfinder.PhoneContactInfo.Contact;
import com.microsoft.xbox.toolkit.JavaUtil;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.utils.HttpURLConnectionBuilder;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class ShortCircuitProfileMessage {

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$service$model$friendfinder$ShortCircuitProfileMessage$MsgType = new int[MsgType.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$ShortCircuitProfileMessage$MsgType[MsgType.Add.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$ShortCircuitProfileMessage$MsgType[MsgType.AddXbox.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$ShortCircuitProfileMessage$MsgType[MsgType.Edit.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$ShortCircuitProfileMessage$MsgType[MsgType.Delete.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$service$model$friendfinder$ShortCircuitProfileMessage$MsgType[MsgType.PhoneVerification.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

    public static class Application {
        public String name;
    }

    public static class ErrorReturn {
        public String code;
        public int httpResult;
        public String message;
        public String phoneCountry;
        public String phoneNumber;
        public String subCode;

        public static ErrorReturn parseJson(JSONObject jSONObject) {
            XLEAssert.assertNotNull(jSONObject);
            ErrorReturn errorReturn = new ErrorReturn();
            try {
                if (!jSONObject.isNull("Code")) {
                    errorReturn.code = jSONObject.getString("Code");
                }
                if (!jSONObject.isNull("HttpResult")) {
                    errorReturn.httpResult = jSONObject.getInt("HttpResult");
                }
                if (!jSONObject.isNull("Message")) {
                    errorReturn.message = jSONObject.getString("Message");
                }
                if (!jSONObject.isNull("PhoneCountry")) {
                    errorReturn.phoneCountry = jSONObject.getString("PhoneCountry");
                }
                if (!jSONObject.isNull("PhoneNumber")) {
                    errorReturn.phoneNumber = jSONObject.getString("PhoneNumber");
                }
                if (!jSONObject.isNull("SubCode")) {
                    errorReturn.subCode = jSONObject.getString("SubCode");
                }
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
            }
            return errorReturn;
        }
    }

    public enum MsgType {
        Add,
        AddXbox,
        Delete,
        PhoneVerification,
        Edit
    }

    public static class PhoneId {
        public String cid;
        public String puid;

        public static PhoneId parseJson(JSONObject jSONObject) {
            JSONException jSONException;
            PhoneId phoneId;
            JSONException jSONException2;
            XLEAssert.assertNotNull(jSONObject);
            PhoneId phoneId2 = null;
            try {
                if (jSONObject.isNull("Cid")) {
                    phoneId2 = new PhoneId();
                    try {
                        phoneId2.cid = jSONObject.getString("Cid");
                    } catch (JSONException e) {
                        jSONException = e;
                        phoneId = phoneId2;
                        jSONException2 = jSONException;
                        XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException2.getMessage(), false);
                        return phoneId;
                    }
                }
                try {
                    if (!jSONObject.isNull("Puid")) {
                        return phoneId2;
                    }
                    if (phoneId2 == null) {
                        phoneId2 = new PhoneId();
                    }
                    try {
                        phoneId2.puid = jSONObject.getString("Puid");
                        return phoneId2;
                    } catch (JSONException e2) {
                        jSONException = e2;
                        phoneId = phoneId2;
                        jSONException2 = jSONException;
                    }
                } catch (JSONException e22) {
                    jSONException = e22;
                    phoneId = phoneId2;
                    jSONException2 = jSONException;
                    XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException2.getMessage(), false);
                    return phoneId;
                }
            } catch (JSONException e222) {
                jSONException = e222;
                phoneId = null;
                jSONException2 = jSONException;
                XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException2.getMessage(), false);
                return phoneId;
            }
        }
    }

    public static class PhoneInfo {
        public ArrayList<Application> addSearchableApplications;
        public String country;
        public String countryName;
        public ArrayList<Application> deleteSearchableApplications;
        public boolean hasSearchableApplications;
        public String label;
        public String name;
        public boolean searchable;
        public ArrayList<Application> searchableApplications;
        public String source;
        public String state;
        public String suggestedVerifyMethod;
        public String type;

        public static PhoneInfo parseJson(JSONObject jSONObject) {
            boolean z;
            JSONException e;
            XLEAssert.assertNotNull(jSONObject);
            PhoneInfo phoneInfo = new PhoneInfo();
            try {
                if (jSONObject.isNull("_type")) {
                    z = false;
                } else {
                    phoneInfo.type = jSONObject.getString("_type");
                    z = true;
                }
                try {
                    if (!jSONObject.isNull("Country")) {
                        phoneInfo.country = jSONObject.getString("Country");
                        z = true;
                    }
                    if (!jSONObject.isNull("CountryName")) {
                        phoneInfo.countryName = jSONObject.getString("CountryName");
                        z = true;
                    }
                    if (!jSONObject.isNull("Label")) {
                        phoneInfo.label = jSONObject.getString("Label");
                        z = true;
                    }
                    if (!jSONObject.isNull("Source")) {
                        phoneInfo.source = jSONObject.getString("Source");
                        z = true;
                    }
                    if (!jSONObject.isNull("State")) {
                        phoneInfo.state = jSONObject.getString("State");
                        z = true;
                    }
                    if (!jSONObject.isNull("SuggestedVerifyMethod")) {
                        phoneInfo.suggestedVerifyMethod = jSONObject.getString("SuggestedVerifyMethod");
                        z = true;
                    }
                    if (!jSONObject.isNull("Name")) {
                        phoneInfo.name = jSONObject.getString("Name");
                        z = true;
                    }
                    if (!jSONObject.isNull("SearchableApplications")) {
                        JSONArray jSONArray = jSONObject.getJSONArray("SearchableApplications");
                        phoneInfo.searchableApplications = new ArrayList();
                        for (int i = 0; i < jSONArray.length(); i++) {
                            JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                            if (jSONObject2 != null) {
                                String string = jSONObject2.getString("Name");
                                if (!JavaUtil.isNullOrEmpty(string)) {
                                    Application application = new Application();
                                    application.name = string;
                                    phoneInfo.searchableApplications.add(application);
                                }
                            }
                        }
                    }
                } catch (JSONException e2) {
                    e = e2;
                    XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                    return z ? null : phoneInfo;
                }
            } catch (JSONException e3) {
                e = e3;
                z = false;
                XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                if (z) {
                }
            }
            if (z) {
            }
        }

        public PhoneState isVerified(String str) {
            this.name = this.name.replace("+", BuildConfig.FLAVOR);
            Object replace = str.replace("+", BuildConfig.FLAVOR);
            if (this.name == null) {
                return null;
            }
            if (!this.name.contains(replace) && !replace.contains(this.name)) {
                return null;
            }
            PhoneState phoneState = new PhoneState();
            phoneState.isVerified = this.state.equalsIgnoreCase("Verified");
            Iterator it = this.searchableApplications.iterator();
            while (it.hasNext()) {
                if (((Application) it.next()).name.equalsIgnoreCase("XBOX")) {
                    phoneState.hasXboxApplication = true;
                }
            }
            return phoneState;
        }
    }

    public static class PhoneInfoAttribute {
        public Integer intValue;
        public String name;
        public String strValue;
        public ArrayList<PhoneInfo> value;

        public static PhoneInfoAttribute parseJson(JSONObject jSONObject) {
            XLEAssert.assertNotNull(jSONObject);
            PhoneInfoAttribute phoneInfoAttribute = new PhoneInfoAttribute();
            try {
                if (!jSONObject.isNull("Name")) {
                    phoneInfoAttribute.name = jSONObject.getString("Name");
                }
                if (!jSONObject.isNull("Value")) {
                    if (phoneInfoAttribute.value == null) {
                        phoneInfoAttribute.value = new ArrayList();
                    }
                    JSONArray optJSONArray = jSONObject.optJSONArray("Value");
                    int i;
                    if (optJSONArray != null) {
                        phoneInfoAttribute.value = new ArrayList();
                        for (i = 0; i < optJSONArray.length(); i++) {
                            PhoneInfo parseJson = PhoneInfo.parseJson(optJSONArray.getJSONObject(i));
                            if (parseJson != null) {
                                phoneInfoAttribute.value.add(parseJson);
                            }
                        }
                    } else {
                        i = jSONObject.optInt("Value", -1);
                        if (i >= 0) {
                            phoneInfoAttribute.intValue = Integer.valueOf(i);
                        } else {
                            String optString = jSONObject.optString("Value");
                            if (optString != null) {
                                phoneInfoAttribute.strValue = optString;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
            }
            return phoneInfoAttribute;
        }

        public PhoneState isVerified(String str) {
            if (this.value != null) {
                Iterator it = this.value.iterator();
                while (it.hasNext()) {
                    PhoneState isVerified = ((PhoneInfo) it.next()).isVerified(str);
                    if (isVerified != null) {
                        return isVerified;
                    }
                }
            }
            return null;
        }
    }

    public static class PhoneInfoView {
        public ArrayList<PhoneInfoAttribute> attributes;
        public PhoneId id;

        public static PhoneInfoView parseJson(JSONObject jSONObject) {
            JSONException jSONException;
            PhoneInfoView phoneInfoView;
            JSONException jSONException2;
            XLEAssert.assertNotNull(jSONObject);
            PhoneInfoView phoneInfoView2 = null;
            try {
                if (!jSONObject.isNull("Id")) {
                    phoneInfoView2 = new PhoneInfoView();
                    try {
                        phoneInfoView2.id = PhoneId.parseJson(jSONObject.getJSONObject("Id"));
                    } catch (JSONException e) {
                        jSONException = e;
                        phoneInfoView = phoneInfoView2;
                        jSONException2 = jSONException;
                        XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException2.getMessage(), false);
                        return phoneInfoView;
                    }
                }
                try {
                    if (jSONObject.isNull("Attributes")) {
                        return phoneInfoView2;
                    }
                    if (phoneInfoView2 == null) {
                        phoneInfoView2 = new PhoneInfoView();
                    }
                    try {
                        JSONArray jSONArray = jSONObject.getJSONArray("Attributes");
                        phoneInfoView2.attributes = new ArrayList();
                        for (int i = 0; i < jSONArray.length(); i++) {
                            PhoneInfoAttribute parseJson = PhoneInfoAttribute.parseJson(jSONArray.getJSONObject(i));
                            if (parseJson != null) {
                                phoneInfoView2.attributes.add(parseJson);
                            }
                        }
                        return phoneInfoView2;
                    } catch (JSONException e2) {
                        jSONException = e2;
                        phoneInfoView = phoneInfoView2;
                        jSONException2 = jSONException;
                        XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException2.getMessage(), false);
                        return phoneInfoView;
                    }
                } catch (JSONException e22) {
                    jSONException = e22;
                    phoneInfoView = phoneInfoView2;
                    jSONException2 = jSONException;
                    XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException2.getMessage(), false);
                    return phoneInfoView;
                }
            } catch (JSONException e222) {
                jSONException = e222;
                phoneInfoView = null;
                jSONException2 = jSONException;
                XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException2.getMessage(), false);
                return phoneInfoView;
            }
        }

        public PhoneState isVerified(String str) {
            if (this.attributes != null) {
                Iterator it = this.attributes.iterator();
                while (it.hasNext()) {
                    PhoneState isVerified = ((PhoneInfoAttribute) it.next()).isVerified(str);
                    if (isVerified != null) {
                        return isVerified;
                    }
                }
            }
            return null;
        }
    }

    public static class PhoneState {
        public boolean hasXboxApplication;
        public boolean isVerified;
    }

    public static class ShortCircuitProfileRequest {
        private String country;
        private MsgType msgType;
        private String phoneNumber;
        private String token;
        private boolean viaVoiceCall;

        public ShortCircuitProfileRequest(MsgType msgType, String str, String str2) {
            this.msgType = msgType;
            this.phoneNumber = str;
            this.country = str2;
        }

        public ShortCircuitProfileRequest(MsgType msgType, String str, String str2, String str3) {
            this(msgType, str, str2);
            this.token = str3;
        }

        public ShortCircuitProfileRequest(MsgType msgType, String str, String str2, boolean z) {
            this.msgType = msgType;
            this.phoneNumber = str;
            this.country = str2;
            this.viaVoiceCall = z;
        }

        private JSONArray getAddMessageContent() {
            try {
                JSONArray jSONArray = new JSONArray();
                JSONObject jSONObject = new JSONObject();
                jSONArray.put(jSONObject);
                jSONObject.put("Country", this.country);
                jSONObject.put("Label", "Phone_Other");
                jSONObject.put("Name", this.phoneNumber);
                jSONObject.put("Searchable", true);
                jSONObject.put("VerifyLanguage", Locale.getDefault().toString());
                if (this.viaVoiceCall) {
                    jSONObject.put("VerifyMethod", "VOICE");
                } else {
                    jSONObject.put("VerifyMethod", "SMS");
                }
                JSONArray jSONArray2 = new JSONArray();
                jSONObject.put("AddSearchableApplications", jSONArray2);
                jSONObject = new JSONObject();
                jSONArray2.put(jSONObject);
                jSONObject.put("Name", "XBOX");
                return jSONArray;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getAddXboxMessageContent() {
            try {
                JSONArray jSONArray = new JSONArray();
                JSONObject jSONObject = new JSONObject();
                jSONArray.put(jSONObject);
                jSONObject.put("Country", this.country);
                jSONObject.put("Name", this.phoneNumber);
                JSONArray jSONArray2 = new JSONArray();
                jSONObject.put("AddSearchableApplications", jSONArray2);
                jSONObject = new JSONObject();
                jSONArray2.put(jSONObject);
                jSONObject.put("Name", "XBOX");
                return jSONArray;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getDeleteMessageContent() {
            try {
                JSONArray jSONArray = new JSONArray();
                JSONObject jSONObject = new JSONObject();
                jSONArray.put(jSONObject);
                jSONObject.put("Country", this.country);
                jSONObject.put("Name", this.phoneNumber);
                return jSONArray;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getEditMessageContent() {
            try {
                JSONArray jSONArray = new JSONArray();
                JSONObject jSONObject = new JSONObject();
                jSONArray.put(jSONObject);
                jSONObject.put("Country", this.country);
                jSONObject.put("Name", this.phoneNumber);
                jSONObject.put("Searchable", true);
                jSONObject.put("VerifyLanguage", Locale.getDefault().toString());
                if (this.viaVoiceCall) {
                    jSONObject.put("VerifyMethod", "VOICE");
                } else {
                    jSONObject.put("VerifyMethod", "SMS");
                }
                JSONArray jSONArray2 = new JSONArray();
                jSONObject.put("AddSearchableApplications", jSONArray2);
                jSONObject = new JSONObject();
                jSONArray2.put(jSONObject);
                jSONObject.put("Name", "XBOX");
                return jSONArray;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        private JSONArray getPhoneVerificationMessageContent() {
            try {
                JSONArray jSONArray = new JSONArray();
                JSONObject jSONObject = new JSONObject();
                jSONArray.put(jSONObject);
                jSONObject.put("Country", this.country);
                jSONObject.put("Name", this.phoneNumber);
                jSONObject.put("Token", this.token);
                return jSONArray;
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }

        public String toString() {
            try {
                JSONObject jSONObject = new JSONObject();
                JSONArray jSONArray = new JSONArray();
                jSONObject.put("Attributes", jSONArray);
                JSONObject jSONObject2 = new JSONObject();
                jSONArray.put(jSONObject2);
                jSONObject2.put("Name", "PersonalContactProfile.Phones");
                switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$service$model$friendfinder$ShortCircuitProfileMessage$MsgType[this.msgType.ordinal()]) {
                    case NativeRegExp.MATCH /*1*/:
                        jSONObject2.put("Add", getAddMessageContent());
                        break;
                    case NativeRegExp.PREFIX /*2*/:
                        jSONObject2.put("Edit", getAddXboxMessageContent());
                        break;
                    case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                        jSONObject2.put("Edit", getEditMessageContent());
                        break;
                    case NativeRegExp.JSREG_MULTILINE /*4*/:
                        jSONObject2.put("Delete", getDeleteMessageContent());
                        break;
                    case Token.GOTO /*5*/:
                        jSONObject2.put("Edit", getPhoneVerificationMessageContent());
                        break;
                }
                return jSONObject.toString();
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }
    }

    public static class ShortCircuitProfileResponse {
        public ErrorReturn error;
        public ArrayList<PhoneInfoView> views;

        public static ShortCircuitProfileResponse parseJson(String str) {
            JSONException jSONException;
            JSONException e;
            JSONException jSONException2;
            ShortCircuitProfileResponse shortCircuitProfileResponse = new ShortCircuitProfileResponse();
            if (JavaUtil.isNullOrEmpty(str)) {
                return shortCircuitProfileResponse;
            }
            ShortCircuitProfileResponse shortCircuitProfileResponse2;
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.length() <= 0) {
                    return shortCircuitProfileResponse;
                }
                JSONArray jSONArray = jSONObject.getJSONArray("Views");
                if (jSONArray == null || jSONArray.length() <= 0) {
                    shortCircuitProfileResponse2 = shortCircuitProfileResponse;
                } else {
                    shortCircuitProfileResponse2 = new ShortCircuitProfileResponse();
                    try {
                        shortCircuitProfileResponse2.views = new ArrayList();
                        for (int i = 0; i < jSONArray.length(); i++) {
                            PhoneInfoView parseJson = PhoneInfoView.parseJson(jSONArray.getJSONObject(i));
                            if (parseJson != null) {
                                shortCircuitProfileResponse2.views.add(parseJson);
                            }
                        }
                    } catch (JSONException e2) {
                        jSONException = e2;
                        shortCircuitProfileResponse = shortCircuitProfileResponse2;
                        jSONException2 = jSONException;
                        XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException2.getMessage(), false);
                        return shortCircuitProfileResponse;
                    }
                }
                if (!jSONObject.isNull("Errors")) {
                    JSONArray jSONArray2 = jSONObject.getJSONArray("Errors");
                    if (jSONArray2 != null && jSONArray2.length() > 0) {
                        shortCircuitProfileResponse = new ShortCircuitProfileResponse();
                        try {
                            shortCircuitProfileResponse.error = ErrorReturn.parseJson(jSONArray2.getJSONObject(0));
                            return shortCircuitProfileResponse;
                        } catch (JSONException jSONException22) {
                            jSONException = jSONException22;
                            shortCircuitProfileResponse2 = shortCircuitProfileResponse;
                            e2 = jSONException;
                            jSONException = e2;
                            shortCircuitProfileResponse = shortCircuitProfileResponse2;
                            jSONException22 = jSONException;
                            XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException22.getMessage(), false);
                            return shortCircuitProfileResponse;
                        }
                    }
                }
                return shortCircuitProfileResponse2;
            } catch (JSONException jSONException222) {
                jSONException = jSONException222;
                shortCircuitProfileResponse2 = shortCircuitProfileResponse;
                e2 = jSONException;
                jSONException = e2;
                shortCircuitProfileResponse = shortCircuitProfileResponse2;
                jSONException222 = jSONException;
                XLEAssert.assertTrue("Failed to parse JSON string - " + jSONException222.getMessage(), false);
                return shortCircuitProfileResponse;
            }
        }

        public String getXboxNumber() {
            if (this.views != null) {
                Iterator it = this.views.iterator();
                while (it.hasNext()) {
                    PhoneInfoView phoneInfoView = (PhoneInfoView) it.next();
                    if (phoneInfoView.attributes != null) {
                        Iterator it2 = phoneInfoView.attributes.iterator();
                        while (it2.hasNext()) {
                            PhoneInfoAttribute phoneInfoAttribute = (PhoneInfoAttribute) it2.next();
                            if (phoneInfoAttribute.value != null) {
                                Iterator it3 = phoneInfoAttribute.value.iterator();
                                while (it3.hasNext()) {
                                    PhoneInfo phoneInfo = (PhoneInfo) it3.next();
                                    String str = phoneInfo.name;
                                    if (phoneInfo.name != null) {
                                        Iterator it4 = phoneInfo.searchableApplications.iterator();
                                        while (it4.hasNext()) {
                                            if (((Application) it4.next()).name.equalsIgnoreCase("XBOX")) {
                                                return str;
                                            }
                                        }
                                        continue;
                                    }
                                }
                                continue;
                            }
                        }
                        continue;
                    }
                }
            }
            return null;
        }

        public PhoneState isVerified(String str) {
            if (this.views != null) {
                Iterator it = this.views.iterator();
                while (it.hasNext()) {
                    PhoneState isVerified = ((PhoneInfoView) it.next()).isVerified(str);
                    if (isVerified != null) {
                        return isVerified;
                    }
                }
            }
            return null;
        }
    }

    public static class UploadPhoneContactsRequest {
        private ArrayList<Contact> contacts;
        private String phoneNumberNormalized;

        public UploadPhoneContactsRequest(ArrayList<Contact> arrayList, String str) {
            this.contacts = arrayList;
            this.phoneNumberNormalized = PhoneContactInfo.sha2Encryption(str);
        }

        public String toString() {
            String string = Secure.getString(XboxTcuiSdk.getContentResolver(), "android_id");
            try {
                JSONObject jSONObject = new JSONObject();
                JSONArray jSONArray = new JSONArray();
                jSONObject.put("Aliases", jSONArray);
                Iterator it = this.contacts.iterator();
                while (it.hasNext()) {
                    Contact contact = (Contact) it.next();
                    JSONObject jSONObject2 = new JSONObject();
                    jSONArray.put(jSONObject2);
                    jSONObject2.put("Type", "phone");
                    JSONArray jSONArray2 = new JSONArray();
                    jSONObject2.put("Alias", jSONArray2);
                    Iterator it2 = contact.phoneNumbers.iterator();
                    while (it2.hasNext()) {
                        jSONArray2.put(PhoneContactInfo.sha2Encryption((String) it2.next()));
                    }
                    JSONObject jSONObject3 = new JSONObject();
                    jSONObject2.put("ContactHandle", jSONObject3);
                    jSONObject3.put("SourceId", "DCON");
                    jSONObject3.put("ObjectId", contact.id);
                    jSONObject3.put("AccountName", string + "-" + contact.displayName);
                }
                return jSONObject.toString();
            } catch (JSONException e) {
                XLEAssert.assertTrue("Failed to create JSON object - " + e.getMessage(), false);
                return null;
            }
        }
    }

    public static class UploadPhoneContactsResponse {
        private Set<String> aliases;
        public boolean isErrorResponse;

        private void foundAlias(String str) {
            if (this.aliases == null) {
                this.aliases = new HashSet();
            }
            this.aliases.add(str);
        }

        public static UploadPhoneContactsResponse parseJson(String str) {
            UploadPhoneContactsResponse uploadPhoneContactsResponse = new UploadPhoneContactsResponse();
            if (!JavaUtil.isNullOrEmpty(str)) {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (jSONObject.length() > 0) {
                        JSONArray jSONArray = jSONObject.getJSONArray("FoundAliases");
                        if (jSONArray != null && jSONArray.length() > 0) {
                            for (int i = 0; i < jSONArray.length(); i++) {
                                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                                if (!jSONObject2.isNull("ContactHandle")) {
                                    jSONObject2 = jSONObject2.getJSONObject("ContactHandle");
                                    if (jSONObject2 != null) {
                                        String optString = jSONObject2.optString("ObjectId");
                                        if (!JavaUtil.isNullOrEmpty(optString)) {
                                            uploadPhoneContactsResponse.foundAlias(optString);
                                        }
                                    }
                                }
                            }
                        }
                        if (!jSONObject.isNull("error")) {
                            uploadPhoneContactsResponse.isErrorResponse = true;
                        }
                    }
                } catch (JSONException e) {
                    XLEAssert.assertTrue("Failed to parse JSON string - " + e.getMessage(), false);
                }
            }
            return uploadPhoneContactsResponse;
        }

        public Set<String> getXboxPhoneContacts() {
            return this.aliases;
        }
    }

    public static String getMessage(XLEHttpStatusAndStream xLEHttpStatusAndStream) {
        String str = null;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(xLEHttpStatusAndStream.stream, HttpURLConnectionBuilder.DEFAULT_CHARSET), EnchantType.fishingRod);
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                String readLine = bufferedReader.readLine();
                if (readLine == null) {
                    break;
                }
                stringBuilder.append(readLine + "\n");
            }
            str = stringBuilder.toString();
        } catch (IOException e) {
            XLEAssert.assertTrue("Failed to read ShortCircuitProfileMessage string - " + e.getMessage(), false);
        }
        return str;
    }
}
