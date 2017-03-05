package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import com.microsoft.onlineid.sts.response.parsers.TokenParser.SecurityTokenMode;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TokenCollectionParser extends BasePullParser {
    private DAToken _daToken;
    private Ticket _ticket;
    private StsError _ticketError;
    private String _ticketInlineAuthUrl;
    private final ISecurityScope _ticketScope;

    public TokenCollectionParser(XmlPullParser xmlPullParser) {
        this(xmlPullParser, null);
    }

    public TokenCollectionParser(XmlPullParser xmlPullParser, ISecurityScope iSecurityScope) {
        super(xmlPullParser, AbstractSoapRequest.WstNamespace, "RequestSecurityTokenResponseCollection");
        this._ticketScope = iSecurityScope;
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
        return this._ticketInlineAuthUrl;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow("wst:RequestSecurityTokenResponse")) {
            TokenParser tokenParser = new TokenParser(this._parser, this._ticketScope, SecurityTokenMode.ServiceRequest);
            tokenParser.parse();
            if (tokenParser.getDAToken() != null) {
                Assertion.check(this._daToken == null);
                this._daToken = tokenParser.getDAToken();
            }
            if (tokenParser.getTicketError() != null) {
                Assertion.check(this._ticketError == null);
                this._ticketError = tokenParser.getTicketError();
                this._ticketInlineAuthUrl = tokenParser.getTicketInlineAuthUrl();
            }
            if (tokenParser.getTicket() != null) {
                Assertion.check(this._ticket == null);
                this._ticket = tokenParser.getTicket();
            }
        }
        if (this._ticketScope != null && this._ticketError == null && this._ticket == null) {
            throw new StsParseException("No ticket or ticket error found.", new Object[0]);
        }
    }
}
