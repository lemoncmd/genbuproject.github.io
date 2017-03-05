package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.IntegerCodeServerError;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class FaultParser extends BasePullParser {
    private StsError _error;

    public FaultParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, AbstractSoapRequest.SoapNamespace, "Fault");
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        String str = null;
        nextStartTag("S:Detail");
        NodeScope location = getLocation();
        location.nextStartTag("psf:error");
        NodeScope location2 = getLocation();
        Integer num = null;
        Integer num2 = null;
        while (location2.nextStartTagNoThrow()) {
            String prefixedTagName = getPrefixedTagName();
            if (prefixedTagName.equals("psf:value")) {
                num2 = Integer.valueOf(TextParsers.parseIntHex(nextRequiredText()));
            } else if (prefixedTagName.equals("psf:internalerror")) {
                NodeScope location3 = getLocation();
                while (location3.nextStartTagNoThrow()) {
                    String prefixedTagName2 = getPrefixedTagName();
                    if (prefixedTagName2.equals("psf:code")) {
                        num = Integer.valueOf(TextParsers.parseIntHex(nextRequiredText()));
                    } else if (prefixedTagName2.equals("psf:text")) {
                        str = this._parser.nextText();
                    } else {
                        skipElement();
                    }
                }
            } else {
                skipElement();
            }
        }
        location.finish();
        if (num2 == null) {
            throw new StsParseException("psf:value node does not exist.", new Object[0]);
        } else if (num == null) {
            throw new StsParseException("psf:code node does not exist.", new Object[0]);
        } else {
            this._error = new StsError(new IntegerCodeServerError(num2.intValue(), num.intValue(), str));
        }
    }
}
