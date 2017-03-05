package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class EncryptedSoapNodeParser extends BasePullParser {
    private String _cipherValue;

    public EncryptedSoapNodeParser(XmlPullParser xmlPullParser) {
        this(xmlPullParser, "EncryptedData");
    }

    public EncryptedSoapNodeParser(XmlPullParser xmlPullParser, String str) {
        super(xmlPullParser, "http://www.w3.org/2001/04/xmlenc#", str);
    }

    public String getCipherValue() {
        verifyParseCalled();
        return this._cipherValue;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        nextStartTag("CipherData");
        NodeScope location = getLocation();
        location.nextStartTag("CipherValue");
        this._cipherValue = nextRequiredText();
        location.finish();
    }
}
