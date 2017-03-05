package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.UserProperties;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class PassportParser extends BasePullParser {
    private int _authState;
    private String _configVersion;
    private final Set<Integer> _flights = new HashSet();
    private String _inlineAuthUrl;
    private String _nonce;
    private String _puid;
    private int _reqStatus;
    private UserProperties _userProperties;

    public PassportParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, null, "pp");
    }

    public int getAuthState() {
        verifyParseCalled();
        return this._authState;
    }

    public String getConfigVersion() {
        verifyParseCalled();
        return this._configVersion;
    }

    public Set<Integer> getFlights() {
        verifyParseCalled();
        return this._flights;
    }

    public String getInlineAuthUrl() {
        verifyParseCalled();
        return this._inlineAuthUrl;
    }

    public String getNonce() {
        verifyParseCalled();
        return this._nonce;
    }

    public String getPuid() {
        verifyParseCalled();
        return this._puid;
    }

    public int getReqStatus() {
        verifyParseCalled();
        return this._reqStatus;
    }

    public UserProperties getUserProperties() {
        verifyParseCalled();
        return this._userProperties;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("psf:authstate")) {
                this._authState = TextParsers.parseIntHex(nextRequiredText());
            } else if (prefixedTagName.equals("psf:reqstatus")) {
                this._reqStatus = TextParsers.parseIntHex(nextRequiredText());
            } else if (prefixedTagName.equals("psf:inlineauthurl")) {
                this._inlineAuthUrl = nextRequiredText();
            } else if (prefixedTagName.equals("psf:signChallenge")) {
                this._nonce = nextRequiredText();
            } else if (prefixedTagName.equals("psf:configVersion")) {
                this._configVersion = nextRequiredText();
            } else if (prefixedTagName.equals("psf:PUID")) {
                this._puid = nextRequiredText();
            } else if (prefixedTagName.equals("psf:flights")) {
                for (String parseIntHex : nextRequiredText().split(",")) {
                    this._flights.add(Integer.valueOf(TextParsers.parseIntHex(parseIntHex)));
                }
            } else if (prefixedTagName.equals("psf:credProperties")) {
                UserPropertiesParser userPropertiesParser = new UserPropertiesParser(this._parser);
                userPropertiesParser.parse();
                this._userProperties = userPropertiesParser.getUserProperties();
            } else {
                skipElement();
            }
        }
    }
}
