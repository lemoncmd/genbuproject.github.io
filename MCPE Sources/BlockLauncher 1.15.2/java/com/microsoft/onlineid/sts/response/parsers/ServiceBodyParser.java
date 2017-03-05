package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import com.microsoft.onlineid.sts.response.parsers.TokenParser.SecurityTokenMode;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ServiceBodyParser extends BasePullParser {
    private DAToken _daToken;
    private String _encryptedBody;
    private StsError _error;

    public ServiceBodyParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, AbstractSoapRequest.SoapNamespace, "Body");
    }

    public DAToken getDAToken() {
        verifyParseCalled();
        return this._daToken;
    }

    public String getEncryptedBody() {
        verifyParseCalled();
        return this._encryptedBody;
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("S:Fault")) {
                FaultParser faultParser = new FaultParser(this._parser);
                faultParser.parse();
                this._error = faultParser.getError();
            } else if (prefixedTagName.equals("EncryptedData")) {
                EncryptedSoapNodeParser encryptedSoapNodeParser = new EncryptedSoapNodeParser(this._parser);
                encryptedSoapNodeParser.parse();
                this._encryptedBody = encryptedSoapNodeParser.getCipherValue();
            } else if (prefixedTagName.equals("wst:RequestSecurityTokenResponse")) {
                TokenParser tokenParser = new TokenParser(this._parser, SecurityTokenMode.ServiceRequest);
                tokenParser.parse();
                this._daToken = tokenParser.getDAToken();
            } else {
                skipElement();
            }
        }
    }
}
