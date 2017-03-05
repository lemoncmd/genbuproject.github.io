package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import net.hockeyapp.android.BuildConfig;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class DeviceProvisionResponseParser extends BasePullParser {
    private StsError _error;
    private String _puid;

    public DeviceProvisionResponseParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, BuildConfig.FLAVOR, "DeviceAddResponse");
    }

    public StsError getError() {
        verifyParseCalled();
        return this._error;
    }

    public String getPuid() {
        verifyParseCalled();
        return this._puid;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        if (Strings.equalsIgnoreCase(this._parser.getAttributeValue(BuildConfig.FLAVOR, "Success"), "true")) {
            nextStartTag("puid");
            this._puid = nextRequiredText();
            return;
        }
        StringCodeErrorParser stringCodeErrorParser = new StringCodeErrorParser(this._parser);
        stringCodeErrorParser.parse();
        this._error = new StsError(stringCodeErrorParser.getError());
    }
}
