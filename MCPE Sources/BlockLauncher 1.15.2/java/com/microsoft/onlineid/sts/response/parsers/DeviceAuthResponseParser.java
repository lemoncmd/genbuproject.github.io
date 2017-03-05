package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceAuthResponseParser extends BasePullParser {
    private int _authState;
    private final ClockSkewManager _clockSkewManager;
    private String _configVersion;
    private DAToken _daToken;
    private StsError _error;
    private Date _expires;
    private int _reqStatus;

    public DeviceAuthResponseParser(XmlPullParser xmlPullParser, ClockSkewManager clockSkewManager) {
        super(xmlPullParser, AbstractSoapRequest.SoapNamespace, "Envelope");
        this._clockSkewManager = clockSkewManager;
    }

    public int getAuthState() {
        verifyParseCalled();
        return this._authState;
    }

    public String getConfigVersion() {
        verifyParseCalled();
        return this._configVersion;
    }

    public DAToken getDAToken() {
        verifyParseCalled();
        return this._daToken;
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }

    public int getReqStatus() {
        verifyParseCalled();
        return this._reqStatus;
    }

    Date getResponseExpiry() {
        verifyParseCalled();
        return this._expires;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("S:Header")) {
                ServiceHeaderParser serviceHeaderParser = new ServiceHeaderParser(this._parser);
                serviceHeaderParser.parse();
                this._expires = serviceHeaderParser.getResponseExpiry();
                PassportParser passportParser = serviceHeaderParser.getPassportParser();
                if (passportParser == null) {
                    throw new StsParseException("Missing passport node in device auth response.", new Object[0]);
                }
                this._authState = passportParser.getAuthState();
                this._reqStatus = passportParser.getReqStatus();
                this._configVersion = passportParser.getConfigVersion();
            } else if (prefixedTagName.equals("S:Body")) {
                ServiceBodyParser serviceBodyParser = new ServiceBodyParser(this._parser);
                serviceBodyParser.parse();
                this._error = serviceBodyParser.getError();
                this._daToken = serviceBodyParser.getDAToken();
            } else {
                skipElement();
            }
        }
        if (this._error == null && this._expires == null) {
            throw new StsParseException("S:Header tag not found", new Object[0]);
        } else if (this._error == null && this._daToken == null) {
            throw new StsParseException("S:Body tag not found", new Object[0]);
        } else {
            Date currentServerTime = this._clockSkewManager.getCurrentServerTime();
            if (this._expires != null && currentServerTime.after(this._expires)) {
                throw new StsParseException("Response is expired. Current time: %s Expiry Time: %s", currentServerTime.toString(), this._expires.toString());
            }
        }
    }
}
