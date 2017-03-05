package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import android.util.Xml;
import com.microsoft.onlineid.sts.XmlSigner;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.exception.StsSignatureException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SignatureValidator {
    private final Map<String, String> _computedDigests = new HashMap();
    private final Map<String, String> _parsedDigests = new HashMap();
    private final byte[] _sessionKey;
    private byte[] _signKeyNonce;
    private String _signatureValue;
    private String _signedInfoXml;
    private final XmlSigner _signer = new XmlSigner();

    public SignatureValidator(byte[] bArr) {
        this._sessionKey = bArr;
    }

    private void parseSignedInfoNode(BasePullParser basePullParser) throws StsParseException, IOException, XmlPullParserException {
        this._signedInfoXml = basePullParser.readRawOuterXml();
        XmlPullParser newPullParser = Xml.newPullParser();
        newPullParser.setInput(new StringReader(this._signedInfoXml));
        new BasePullParser(newPullParser, null, "SignedInfo") {
            protected void onParse() throws XmlPullParserException, IOException, StsParseException {
                while (nextStartTagNoThrow("Reference")) {
                    String attributeValue = this._parser.getAttributeValue(null, "URI");
                    NodeScope location = getLocation();
                    if (location.nextStartTagNoThrow("DigestValue")) {
                        Object nextRequiredText = location.nextRequiredText();
                        if (TextUtils.isEmpty(attributeValue) || !attributeValue.startsWith("#")) {
                            throw new StsSignatureException("Invalid digest URI: " + attributeValue, new Object[0]);
                        } else if (TextUtils.isEmpty(nextRequiredText)) {
                            throw new StsSignatureException("Invalid digest: " + nextRequiredText, new Object[0]);
                        } else {
                            SignatureValidator.this._parsedDigests.put(attributeValue.substring(1), nextRequiredText);
                        }
                    } else {
                        throw new StsSignatureException("Missing DigestValue for URI " + attributeValue, new Object[0]);
                    }
                }
            }
        }.parse();
    }

    public boolean canValidate() {
        return (this._sessionKey == null || TextUtils.isEmpty(this._signedInfoXml) || this._signKeyNonce == null || TextUtils.isEmpty(this._signatureValue)) ? false : true;
    }

    public XmlPullParser computeNodeDigest(BasePullParser basePullParser) throws XmlPullParserException, IOException, StsParseException {
        XmlPullParser xmlPullParser = basePullParser._parser;
        xmlPullParser.require(2, null, null);
        Object attributeValue = xmlPullParser.getAttributeValue(null, "Id");
        if (TextUtils.isEmpty(attributeValue)) {
            return xmlPullParser;
        }
        String readRawOuterXml = basePullParser.readRawOuterXml();
        String computeDigest = this._signer.computeDigest(readRawOuterXml);
        if (this._computedDigests.containsKey(attributeValue)) {
            throw new StsSignatureException("Duplicate element for Id=\"" + attributeValue + "\"", new Object[0]);
        }
        this._computedDigests.put(attributeValue, computeDigest);
        xmlPullParser = Xml.newPullParser();
        xmlPullParser.setInput(new StringReader(readRawOuterXml));
        return xmlPullParser;
    }

    public void parseSignatureNode(BasePullParser basePullParser) throws StsParseException, IOException, XmlPullParserException {
        new BasePullParser(basePullParser._parser, XmlSigner.SignatureNamespace, "Signature") {
            protected void onParse() throws XmlPullParserException, IOException, StsParseException {
                while (nextStartTagNoThrow()) {
                    String prefixedTagName = getPrefixedTagName();
                    if ("SignedInfo".equals(prefixedTagName)) {
                        SignatureValidator.this.parseSignedInfoNode(this);
                    } else if ("SignatureValue".equals(prefixedTagName)) {
                        SignatureValidator.this._signatureValue = nextRequiredText();
                    } else {
                        skipElement();
                    }
                }
                if (TextUtils.isEmpty(SignatureValidator.this._signatureValue)) {
                    throw new StsSignatureException("<SignatureValue> node was missing.", new Object[0]);
                } else if (TextUtils.isEmpty(SignatureValidator.this._signedInfoXml)) {
                    throw new StsSignatureException("<SignedInfo> node was missing.", new Object[0]);
                }
            }
        }.parse();
    }

    public void setSignKeyNonce(byte[] bArr) {
        this._signKeyNonce = bArr;
    }

    public void validate() throws StsSignatureException {
        for (Entry entry : this._computedDigests.entrySet()) {
            if (this._parsedDigests.containsKey(entry.getKey())) {
                if (!((String) this._parsedDigests.remove(entry.getKey())).equals(entry.getValue())) {
                    throw new StsSignatureException(String.format(Locale.US, "Digest mismatch: id=\"%s\", expected=\"%s\", actual=\"%s\"", new Object[]{entry.getKey(), r1, entry.getValue()}), new Object[0]);
                }
            }
        }
        if (!this._parsedDigests.isEmpty()) {
            throw new StsSignatureException("Failed to compute digests for element ids " + Arrays.toString(this._parsedDigests.keySet().toArray()), new Object[0]);
        } else if (TextUtils.isEmpty(this._signedInfoXml)) {
            throw new StsSignatureException("<SignedInfo> node was missing.", new Object[0]);
        } else if (this._signKeyNonce == null || this._signKeyNonce.length == 0) {
            throw new StsSignatureException("SignKey nonce was missing or invalid.", new Object[0]);
        } else {
            if (!this._signatureValue.equals(this._signer.computeSignatureForResponse(this._sessionKey, this._signKeyNonce, this._signedInfoXml))) {
                throw new StsSignatureException(String.format(Locale.US, "Signature mismatch: expected=\"%s\", actual=\"%s\"", new Object[]{this._signatureValue, r0}), new Object[0]);
            }
        }
    }
}
