package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import android.util.Xml;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.log.RedactableResponse;
import com.microsoft.onlineid.sts.Cryptography;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.SharedKeyGenerator;
import com.microsoft.onlineid.sts.SharedKeyGenerator.KeyPurpose;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.UserProperties;
import com.microsoft.onlineid.sts.UserProperties.UserProperty;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ServiceResponseParser extends BasePullParser {
    private int _authState;
    private String _configVersion;
    private DAToken _daToken;
    private byte[] _encKeyNonce;
    private StsError _error;
    private Date _expires;
    private Set<Integer> _flights;
    private String _inlineAuthUrl;
    private String _puid;
    private int _reqStatus;
    private final byte[] _sessionKey;
    private Ticket _ticket;
    private StsError _ticketError;
    private String _ticketInlineAuthUrl;
    private final ISecurityScope _ticketScope;
    private UserProperties _userProperties;

    public ServiceResponseParser(XmlPullParser xmlPullParser, byte[] bArr) {
        this(xmlPullParser, bArr, null);
    }

    public ServiceResponseParser(XmlPullParser xmlPullParser, byte[] bArr, ISecurityScope iSecurityScope) {
        super(xmlPullParser, AbstractSoapRequest.SoapNamespace, "Envelope");
        this._sessionKey = bArr;
        this._ticketScope = iSecurityScope;
    }

    private String decryptEncryptedBlob(String str) throws StsParseException {
        try {
            return new String(Cryptography.decryptWithAesCbcPcs5PaddingCipher(TextParsers.parseBase64(str), new SharedKeyGenerator(this._sessionKey).generateKey(KeyPurpose.STSDigest, this._encKeyNonce)), Strings.Utf8Charset);
        } catch (Throwable e) {
            throw new StsParseException(e);
        } catch (Throwable e2) {
            throw new StsParseException(e2);
        }
    }

    private void parseAndSaveFromPassport(String str) throws StsParseException, IOException, XmlPullParserException {
        XmlPullParser newPullParser = Xml.newPullParser();
        newPullParser.setInput(new StringReader(str));
        PassportParser passportParser = new PassportParser(newPullParser);
        passportParser.parse();
        saveFromPassport(passportParser);
    }

    private void parseAndSaveFromTokenCollection(String str) throws XmlPullParserException, IOException, StsParseException {
        XmlPullParser newPullParser = Xml.newPullParser();
        newPullParser.setInput(new StringReader(str));
        TokenCollectionParser tokenCollectionParser = new TokenCollectionParser(newPullParser, this._ticketScope);
        tokenCollectionParser.parse();
        this._daToken = tokenCollectionParser.getDAToken();
        this._ticket = tokenCollectionParser.getTicket();
        this._ticketError = tokenCollectionParser.getTicketError();
        this._ticketInlineAuthUrl = tokenCollectionParser.getTicketInlineAuthUrl();
    }

    private void saveFromPassport(PassportParser passportParser) throws StsParseException, IOException, XmlPullParserException {
        this._authState = passportParser.getAuthState();
        this._reqStatus = passportParser.getReqStatus();
        this._inlineAuthUrl = passportParser.getInlineAuthUrl();
        this._configVersion = passportParser.getConfigVersion();
        this._puid = passportParser.getPuid();
        this._userProperties = passportParser.getUserProperties();
        this._flights = passportParser.getFlights();
        if (this._userProperties != null && this._userProperties.get(UserProperty.CID) == null) {
            throw new StsParseException("CID not found.", new Object[0]);
        }
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

    public byte[] getEncKeyNonce() {
        verifyParseCalled();
        return this._encKeyNonce;
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }

    public Set<Integer> getFlights() {
        verifyParseCalled();
        return this._flights;
    }

    public String getInlineAuthUrl() {
        verifyParseCalled();
        return this._inlineAuthUrl;
    }

    public String getPuid() {
        verifyParseCalled();
        return this._puid;
    }

    public int getReqStatus() {
        verifyParseCalled();
        return this._reqStatus;
    }

    public Date getResponseExpiry() {
        verifyParseCalled();
        return this._expires;
    }

    public Ticket getTicket() {
        verifyParseCalled();
        return this._ticket;
    }

    public StsError getTicketError() {
        verifyParseCalled();
        return this._ticketError;
    }

    public String getTicketInlineAuthUrl() {
        verifyParseCalled();
        return this._ticketInlineAuthUrl;
    }

    public UserProperties getUserProperties() {
        verifyParseCalled();
        return this._userProperties;
    }

    protected void onParse() throws IOException, StsParseException, XmlPullParserException {
        String str = null;
        SignatureValidator signatureValidator = new SignatureValidator(this._sessionKey);
        Object obj = null;
        PassportParser passportParser = null;
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("S:Header")) {
                ServiceHeaderParser serviceHeaderParser = new ServiceHeaderParser(this._parser, signatureValidator);
                serviceHeaderParser.parse();
                this._expires = serviceHeaderParser.getResponseExpiry();
                this._encKeyNonce = serviceHeaderParser.getEncKeyNonce();
                passportParser = serviceHeaderParser.getPassportParser();
                obj = serviceHeaderParser.getEncryptedHeader();
            } else if (prefixedTagName.equals("S:Body")) {
                ServiceBodyParser serviceBodyParser = new ServiceBodyParser(signatureValidator.computeNodeDigest(this));
                serviceBodyParser.parse();
                this._error = serviceBodyParser.getError();
                str = serviceBodyParser.getEncryptedBody();
            } else {
                skipElement();
            }
        }
        if (this._encKeyNonce != null || signatureValidator.canValidate()) {
            signatureValidator.validate();
        }
        if (!TextUtils.isEmpty(obj)) {
            String decryptEncryptedBlob = decryptEncryptedBlob(obj);
            Logger.info(new RedactableResponse("Decrypted service response header: " + decryptEncryptedBlob));
            parseAndSaveFromPassport(decryptEncryptedBlob);
        } else if (passportParser != null) {
            saveFromPassport(passportParser);
        }
        if (this._error == null) {
            str = decryptEncryptedBlob(str);
            Logger.info(new RedactableResponse("Decrypted service response body: " + str));
            parseAndSaveFromTokenCollection(str);
        }
    }
}
