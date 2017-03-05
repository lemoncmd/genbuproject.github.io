package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SecurityTokenParser extends BasePullParser {
    private String _tokenBlob;

    public SecurityTokenParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, AbstractSoapRequest.WstNamespace, "RequestedSecurityToken");
    }

    public String getTokenBlob() {
        verifyParseCalled();
        return this._tokenBlob;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("EncryptedData")) {
                Assertion.check(this._tokenBlob == null);
                this._tokenBlob = readRawOuterXml();
            } else if (prefixedTagName.equals("wsse:BinarySecurityToken")) {
                Assertion.check(this._tokenBlob == null);
                this._tokenBlob = nextRequiredText();
            } else {
                skipElement();
            }
        }
    }
}
