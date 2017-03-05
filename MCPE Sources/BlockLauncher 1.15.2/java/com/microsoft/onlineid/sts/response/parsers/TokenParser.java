package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.IntegerCodeServerError;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import java.util.Date;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TokenParser extends BasePullParser {
    private DAToken _daToken;
    private String _inlineAuthUrl;
    private final SecurityTokenMode _securityTokenMode;
    private Ticket _ticket;
    private StsError _ticketError;
    private final ISecurityScope _ticketScope;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$onlineid$sts$response$parsers$TokenParser$SecurityTokenMode = new int[SecurityTokenMode.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$onlineid$sts$response$parsers$TokenParser$SecurityTokenMode[SecurityTokenMode.ServiceRequest.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$onlineid$sts$response$parsers$TokenParser$SecurityTokenMode[SecurityTokenMode.NgcAuthentication.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public enum SecurityTokenMode {
        ServiceRequest,
        NgcAuthentication
    }

    public TokenParser(XmlPullParser xmlPullParser, ISecurityScope iSecurityScope, SecurityTokenMode securityTokenMode) {
        super(xmlPullParser, AbstractSoapRequest.WstNamespace, "RequestSecurityTokenResponse");
        this._ticketScope = iSecurityScope;
        this._securityTokenMode = securityTokenMode;
    }

    public TokenParser(XmlPullParser xmlPullParser, SecurityTokenMode securityTokenMode) {
        this(xmlPullParser, null, securityTokenMode);
    }

    public DAToken getDAToken() {
        verifyParseCalled();
        return this._daToken;
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
        return this._inlineAuthUrl;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        Date date = null;
        byte[] bArr = null;
        String str = null;
        Object obj = null;
        Object obj2 = null;
        while (nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("wst:TokenType")) {
                obj2 = nextRequiredText();
            } else if (prefixedTagName.equals("wsp:AppliesTo")) {
                NodeScope location = getLocation();
                location.nextStartTag("wsa:EndpointReference");
                getLocation().nextStartTag("wsa:Address");
                obj = nextRequiredText();
                location.finish();
            } else if (prefixedTagName.equals("wst:Lifetime")) {
                TimeListParser timeListParser = new TimeListParser(this._parser);
                timeListParser.parse();
                date = timeListParser.getExpires();
            } else if (prefixedTagName.equals("wst:RequestedSecurityToken")) {
                switch (AnonymousClass1.$SwitchMap$com$microsoft$onlineid$sts$response$parsers$TokenParser$SecurityTokenMode[this._securityTokenMode.ordinal()]) {
                    case NativeRegExp.MATCH /*1*/:
                        SecurityTokenParser securityTokenParser = new SecurityTokenParser(this._parser);
                        securityTokenParser.parse();
                        str = securityTokenParser.getTokenBlob();
                        break;
                    case NativeRegExp.PREFIX /*2*/:
                        NgcSecurityTokenParser ngcSecurityTokenParser = new NgcSecurityTokenParser(this._parser);
                        ngcSecurityTokenParser.parse();
                        str = ngcSecurityTokenParser.getTokenBlob();
                        break;
                    default:
                        break;
                }
            } else if (prefixedTagName.equals("wst:RequestedProofToken")) {
                ProofTokenParser proofTokenParser = new ProofTokenParser(this._parser);
                proofTokenParser.parse();
                bArr = proofTokenParser.getSessionKey();
            } else if (prefixedTagName.equals("psf:pp")) {
                PassportParser passportParser = new PassportParser(this._parser);
                passportParser.parse();
                this._ticketError = new StsError(new IntegerCodeServerError(passportParser.getReqStatus()));
                this._inlineAuthUrl = passportParser.getInlineAuthUrl();
            } else {
                skipElement();
            }
        }
        if (this._ticketError == null && obj2 == null) {
            throw new StsParseException("wst:TokenType node is missing", new Object[0]);
        }
        try {
            if (Objects.equals(obj2, "urn:passport:legacy") && bArr != null) {
                Assertion.check(DAToken.Scope.getTarget().equals(obj));
                this._daToken = new DAToken(str, bArr);
            } else if ((Objects.equals(obj2, "urn:passport:compact") || Objects.equals(obj2, "urn:passport:loginprooftoken")) && this._ticketError == null) {
                boolean z = this._ticketScope != null && this._ticketScope.getTarget().equals(obj);
                Assertion.check(z);
                this._ticket = new Ticket(this._ticketScope, date, str);
            }
        } catch (Throwable e) {
            throw new StsParseException(e);
        }
    }
}
