package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ProofTokenParser extends BasePullParser {
    private byte[] _sessionKey;

    public ProofTokenParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, AbstractSoapRequest.WstNamespace, "RequestedProofToken");
    }

    public byte[] getSessionKey() {
        verifyParseCalled();
        return this._sessionKey;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("EncryptedKey")) {
                new EncryptedSoapNodeParser(this._parser, "EncryptedKey").parse();
                Assertion.check(this._sessionKey == null, "Only one of EncryptedKey or wst:BinarySecret is expected");
                this._sessionKey = null;
            } else if (prefixedTagName.equals("wst:BinarySecret")) {
                Assertion.check(this._sessionKey == null, "Only one of EncryptedKey or wst:BinarySecret is expected");
                this._sessionKey = TextParsers.parseBase64(nextRequiredText());
            } else {
                skipElement();
            }
        }
    }
}
