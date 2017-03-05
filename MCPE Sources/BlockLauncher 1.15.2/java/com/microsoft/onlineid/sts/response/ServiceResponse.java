package com.microsoft.onlineid.sts.response;

import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.sts.ClockSkewManager;
import com.microsoft.onlineid.sts.DAToken;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.UserProperties;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.response.parsers.ServiceResponseParser;
import java.io.IOException;
import java.util.Date;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;

public class ServiceResponse extends AbstractSoapResponse {
    private final ClockSkewManager _clockSkewManager;
    private final byte[] _decryptionSessionKey;
    private ServiceResponseParser _parser;
    private final ISecurityScope _ticketScope;

    public ServiceResponse(byte[] bArr, ISecurityScope iSecurityScope, ClockSkewManager clockSkewManager) {
        this._decryptionSessionKey = bArr;
        this._ticketScope = iSecurityScope;
        this._clockSkewManager = clockSkewManager;
    }

    public ServiceResponse(byte[] bArr, ClockSkewManager clockSkewManager) {
        this(bArr, null, clockSkewManager);
    }

    public String getConfigVersion() {
        return this._parser.getConfigVersion();
    }

    public DAToken getDAToken() {
        return this._parser.getDAToken();
    }

    public StsError getError() {
        StsError error = this._parser.getError();
        return error == null ? this._parser.getTicketError() : error;
    }

    public Set<Integer> getFlights() {
        return this._parser.getFlights();
    }

    public String getInlineAuthUrl() {
        Object inlineAuthUrl = this._parser.getInlineAuthUrl();
        return TextUtils.isEmpty(inlineAuthUrl) ? this._parser.getTicketInlineAuthUrl() : inlineAuthUrl;
    }

    public String getPuid() {
        return this._parser.getPuid();
    }

    public Ticket getTicket() {
        return this._parser.getTicket();
    }

    public StsError getTicketError() {
        return this._parser.getTicketError();
    }

    public UserProperties getUserProperties() {
        return this._parser.getUserProperties();
    }

    protected void parse(XmlPullParser xmlPullParser) throws StsParseException, IOException {
        if (this._parser != null) {
            throw new IllegalStateException("Each response object may only parse its respone once.");
        }
        this._parser = new ServiceResponseParser(xmlPullParser, this._decryptionSessionKey, this._ticketScope);
        this._parser.parse();
        validateExpirationTime();
    }

    protected void validateExpirationTime() throws StsParseException {
        Date currentServerTime = this._clockSkewManager.getCurrentServerTime();
        Date responseExpiry = this._parser.getResponseExpiry();
        if (responseExpiry != null && currentServerTime.after(responseExpiry)) {
            throw new StsParseException("Response is expired. Current time: %s Expiry Time: %s", currentServerTime.toString(), responseExpiry.toString());
        }
    }
}
