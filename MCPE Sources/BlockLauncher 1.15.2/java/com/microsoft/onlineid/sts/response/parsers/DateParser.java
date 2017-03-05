package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DateParser extends BasePullParser {
    private Date _date;
    private final DateType _type;

    enum DateType {
        SecondsSinceEpoch {
            public Date parse(String str) throws StsParseException {
                try {
                    return new Date(1000 * Long.parseLong(str));
                } catch (Throwable e) {
                    throw new StsParseException("Cannot parse date node: %s", e, str);
                }
            }
        },
        Iso8601DateTimeIgnoreTimeZone {
            public Date parse(String str) throws StsParseException {
                try {
                    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ROOT);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    return simpleDateFormat.parse(str);
                } catch (Throwable e) {
                    throw new StsParseException(e);
                }
            }
        };

        public abstract Date parse(String str) throws StsParseException;
    }

    public DateParser(XmlPullParser xmlPullParser, DateType dateType) throws XmlPullParserException {
        super(xmlPullParser, null, null);
        this._type = dateType;
    }

    Date getDate() {
        verifyParseCalled();
        return this._date;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        this._date = this._type.parse(nextRequiredText());
    }
}
