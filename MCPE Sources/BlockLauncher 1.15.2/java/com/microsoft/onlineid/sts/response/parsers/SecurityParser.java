package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SecurityParser extends BasePullParser {
    private byte[] _encKeyNonce;
    private Date _expires;
    private final SignatureValidator _validator;

    public SecurityParser(XmlPullParser xmlPullParser) {
        this(xmlPullParser, null);
    }

    public SecurityParser(XmlPullParser xmlPullParser, SignatureValidator signatureValidator) {
        super(xmlPullParser, AbstractSoapRequest.WsseNamespace, "Security");
        this._validator = signatureValidator;
    }

    public byte[] getEncKeyNonce() {
        verifyParseCalled();
        return this._encKeyNonce;
    }

    public Date getResponseExpiry() {
        verifyParseCalled();
        return this._expires;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("wsu:Timestamp")) {
                TimeListParser timeListParser = new TimeListParser(this._validator != null ? this._validator.computeNodeDigest(this) : this._parser);
                timeListParser.parse();
                this._expires = timeListParser.getExpires();
            } else if (prefixedTagName.equals("wssc:DerivedKeyToken")) {
                prefixedTagName = this._parser.getAttributeValue(AbstractSoapRequest.WsuNamespace, "Id");
                DerivedKeyTokenParser derivedKeyTokenParser = new DerivedKeyTokenParser(this._parser);
                derivedKeyTokenParser.parse();
                if ("EncKey".equals(prefixedTagName)) {
                    this._encKeyNonce = derivedKeyTokenParser.getNonce();
                } else if ("SignKey".equals(prefixedTagName) && this._validator != null) {
                    this._validator.setSignKeyNonce(derivedKeyTokenParser.getNonce());
                }
            } else if (!"Signature".equals(prefixedTagName) || this._validator == null) {
                skipElement();
            } else {
                this._validator.parseSignatureNode(this);
            }
        }
        if (this._expires == null) {
            throw new StsParseException("wsu:Timestamp node not found.", new Object[0]);
        }
    }
}
