package com.microsoft.onlineid.sts.response.parsers;

import com.microsoft.onlineid.sts.UserProperties;
import com.microsoft.onlineid.sts.UserProperties.UserProperty;
import com.microsoft.onlineid.sts.exception.StsParseException;
import com.microsoft.onlineid.sts.request.AbstractSoapRequest;
import java.io.IOException;
import net.hockeyapp.android.BuildConfig;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class UserPropertiesParser extends BasePullParser {
    private final UserProperties _userProperties = new UserProperties();

    public UserPropertiesParser(XmlPullParser xmlPullParser) {
        super(xmlPullParser, AbstractSoapRequest.PsfNamespace, "credProperties");
    }

    public UserProperties getUserProperties() {
        verifyParseCalled();
        return this._userProperties;
    }

    protected void onParse() throws XmlPullParserException, IOException, StsParseException {
        while (nextStartTagNoThrow()) {
            String attributeValue = this._parser.getAttributeValue(BuildConfig.FLAVOR, "Name");
            if (attributeValue == null) {
                skipElement();
            } else {
                try {
                    this._userProperties.put(UserProperty.valueOf(attributeValue), this._parser.nextText());
                } catch (IllegalArgumentException e) {
                    skipElement();
                }
            }
        }
    }
}
