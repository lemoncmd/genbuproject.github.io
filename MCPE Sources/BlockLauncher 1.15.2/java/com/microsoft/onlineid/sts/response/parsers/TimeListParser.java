package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.util.Date;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TimeListParser extends BasePullParser {
    private Date _expires;

    public TimeListParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, null, null);
    }

    public Date getExpires() {
        verifyParseCalled();
        return this._expires;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        nextStartTag("wsu:Expires");
        DateParser dateParser = new DateParser(this._parser, DateType.Iso8601DateTimeIgnoreTimeZone);
        dateParser.parse();
        this._expires = dateParser.getDate();
    }
}
