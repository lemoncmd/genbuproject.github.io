package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.internal.Integers;
import com.microsoft.onlineid.sts.StringCodeServerError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import net.hockeyapp.android.BuildConfig;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class StringCodeErrorParser extends BasePullParser {
    private String _code;
    private StringCodeServerError _error;
    private Integer _subCode;

    public StringCodeErrorParser(XmlPullParser xmlPullParser) throws XmlPullParserException {
        super(xmlPullParser, null, null);
    }

    public StringCodeServerError getError() {
        verifyParseCalled();
        return this._error;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String name = this._parser.getName();
            if (name.equals("Error")) {
                this._code = this._parser.getAttributeValue(BuildConfig.FLAVOR, "Code");
            } else if (name.equals("ErrorSubcode")) {
                try {
                    this._subCode = Integer.valueOf(Integers.parseIntHex(this._parser.nextText()));
                } catch (Throwable e) {
                    throw new StsParseException("Hex error code could not be parsed: %s.", e, r1);
                }
            } else {
                skipElement();
            }
        }
        if (this._code == null) {
            throw new StsParseException("Required node \"Error\" is missing or empty.", new Object[0]);
        } else if (this._subCode == null) {
            throw new StsParseException("Required node \"ErrorSubcode\" is missing.", new Object[0]);
        } else {
            this._error = new StringCodeServerError(this._code, this._subCode.intValue());
        }
    }
}
