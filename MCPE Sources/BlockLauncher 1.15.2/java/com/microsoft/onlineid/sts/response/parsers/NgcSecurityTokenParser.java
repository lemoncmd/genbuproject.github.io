package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class NgcSecurityTokenParser extends BasePullParser {
    private String _tokenBlob;

    public NgcSecurityTokenParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, AbstractSoapRequest.WstNamespace, "RequestedSecurityToken");
    }

    public String getTokenBlob() {
        verifyParseCalled();
        return this._tokenBlob;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        nextStartTag("EncryptedData");
        EncryptedSoapNodeParser encryptedSoapNodeParser = new EncryptedSoapNodeParser(this._parser);
        encryptedSoapNodeParser.parse();
        this._tokenBlob = encryptedSoapNodeParser.getCipherValue();
    }
}
