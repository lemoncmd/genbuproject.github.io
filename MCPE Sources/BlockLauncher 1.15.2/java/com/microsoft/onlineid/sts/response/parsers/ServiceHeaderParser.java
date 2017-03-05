package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ServiceHeaderParser extends BasePullParser {
    private byte[] _encKeyNonce;
    private String _encryptedHeader;
    private Date _expires;
    private PassportParser _passportParser;
    private final SignatureValidator _validator;

    public ServiceHeaderParser(XmlPullParser xmlPullParser) {
        this(xmlPullParser, null);
    }

    public ServiceHeaderParser(XmlPullParser xmlPullParser, SignatureValidator signatureValidator) {
        super(xmlPullParser, AbstractSoapRequest.SoapNamespace, "Header");
        this._validator = signatureValidator;
    }

    public byte[] getEncKeyNonce() {
        verifyParseCalled();
        return this._encKeyNonce;
    }

    public String getEncryptedHeader() {
        verifyParseCalled();
        return this._encryptedHeader;
    }

    public PassportParser getPassportParser() {
        verifyParseCalled();
        return this._passportParser;
    }

    public Date getResponseExpiry() {
        verifyParseCalled();
        return this._expires;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            CharSequence attributeValue = this._parser.getAttributeValue(AbstractSoapRequest.WsuNamespace, "Id");
            if (prefixedTagName.equals("wsse:Security")) {
                SecurityParser securityParser = new SecurityParser(this._parser, this._validator);
                securityParser.parse();
                this._expires = securityParser.getResponseExpiry();
                this._encKeyNonce = securityParser.getEncKeyNonce();
            } else if (prefixedTagName.equals("psf:pp")) {
                this._passportParser = new PassportParser(this._parser);
                this._passportParser.parse();
            } else if (prefixedTagName.equals("psf:EncryptedPP")) {
                NodeScope location = getLocation();
                location.nextStartTag("EncryptedData");
                EncryptedSoapNodeParser encryptedSoapNodeParser = new EncryptedSoapNodeParser(this._validator != null ? this._validator.computeNodeDigest(this) : this._parser);
                encryptedSoapNodeParser.parse();
                this._encryptedHeader = encryptedSoapNodeParser.getCipherValue();
                location.finish();
            } else if (this._validator == null || TextUtils.isEmpty(attributeValue)) {
                skipElement();
            } else {
                this._validator.computeNodeDigest(this);
            }
        }
    }
}
