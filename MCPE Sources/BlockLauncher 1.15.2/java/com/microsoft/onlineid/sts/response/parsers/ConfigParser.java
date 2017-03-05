package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import android.text.TextUtils.SimpleStringSplitter;
import com.microsoft.onlineid.internal.configuration.ISetting;
import com.microsoft.onlineid.sts.ServerConfig;
import com.microsoft.onlineid.sts.ServerConfig.Editor;
import com.microsoft.onlineid.sts.ServerConfig.Endpoint;
import com.microsoft.onlineid.sts.ServerConfig.Int;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ConfigParser extends BasePullParser {
    static final String CfgNamespace = "http://schemas.microsoft.com/Passport/PPCRL";
    static final String DefaultNamespace = "http://www.w3.org/2000/09/xmldsig#";
    private final Editor _editor;
    private final Map<String, Endpoint> _endpointSettings;
    private final Map<String, ISetting<Integer>> _intSettings;
    private final Map<String, ISetting<Set<String>>> _stringSetSettings;
    private final Map<String, ISetting<String>> _stringSettings = new HashMap();

    public ConfigParser(XmlPullParser xmlPullParser, Editor editor) {
        int i = 0;
        super(xmlPullParser, DefaultNamespace, "Signature");
        this._editor = editor;
        addSetting(this._stringSettings, ServerConfig.Version);
        this._intSettings = new HashMap();
        for (ISetting addSetting : Int.values()) {
            addSetting(this._intSettings, addSetting);
        }
        this._endpointSettings = new HashMap();
        Endpoint[] values = Endpoint.values();
        int length = values.length;
        while (i < length) {
            addSetting(this._endpointSettings, values[i]);
            i++;
        }
        this._stringSetSettings = new HashMap();
        addSetting(this._stringSetSettings, ServerConfig.AndroidSsoCertificates);
    }

    protected <V, T extends ISetting<V>> void addSetting(Map<String, T> map, T t) {
        map.put(t.getSettingName(), t);
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        nextStartTag("cfg:Configuration");
        NodeScope location = getLocation();
        while (location.nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equalsIgnoreCase("cfg:Settings") || prefixedTagName.equalsIgnoreCase("cfg:ServiceURIs") || prefixedTagName.equalsIgnoreCase("cfg:ServiceURIs1")) {
                parseSettings();
            } else {
                location.skipElement();
            }
        }
    }

    protected void parseSettings() throws IOException, XmlPullParserException, StsParseException {
        NodeScope location = getLocation();
        while (location.nextStartTagNoThrow()) {
            String name = this._parser.getName();
            if (!(tryParseStringSetting(name) || tryParseIntSetting(name) || tryParseEndpointSetting(name) || tryParseStringSetSetting(name))) {
                location.skipElement();
            }
        }
    }

    protected boolean tryParseEndpointSetting(String str) throws StsParseException, XmlPullParserException, IOException {
        Endpoint endpoint = (Endpoint) this._endpointSettings.get(str);
        if (endpoint == null) {
            return false;
        }
        this._editor.setUrl(endpoint, TextParsers.parseUrl(nextRequiredText(), str));
        return true;
    }

    protected boolean tryParseIntSetting(String str) throws StsParseException, XmlPullParserException, IOException {
        ISetting iSetting = (ISetting) this._intSettings.get(str);
        if (iSetting == null) {
            return false;
        }
        this._editor.setInt(iSetting, TextParsers.parseInt(nextRequiredText(), str));
        return true;
    }

    protected boolean tryParseStringSetSetting(String str) throws StsParseException, XmlPullParserException, IOException {
        ISetting iSetting = (ISetting) this._stringSetSettings.get(str);
        if (iSetting == null) {
            return false;
        }
        Object nextRequiredText = nextRequiredText();
        if (!TextUtils.isEmpty(nextRequiredText)) {
            Set hashSet = new HashSet();
            SimpleStringSplitter simpleStringSplitter = new SimpleStringSplitter(',');
            simpleStringSplitter.setString(nextRequiredText);
            Iterator it = simpleStringSplitter.iterator();
            while (it.hasNext()) {
                hashSet.add((String) it.next());
            }
            this._editor.setStringSet(iSetting, hashSet);
        }
        return true;
    }

    protected boolean tryParseStringSetting(String str) throws StsParseException, XmlPullParserException, IOException {
        ISetting iSetting = (ISetting) this._stringSettings.get(str);
        if (iSetting == null) {
            return false;
        }
        this._editor.setString(iSetting, nextRequiredText());
        return true;
    }
}
