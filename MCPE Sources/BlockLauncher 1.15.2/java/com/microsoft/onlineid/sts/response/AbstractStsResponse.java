package com.microsoft.onlineid.sts.response;

import android.util.Xml;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.log.RedactableResponse;
import com.microsoft.onlineid.sts.StsError;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Locale;
import org.xmlpull.v1.XmlPullParser;

public abstract class AbstractStsResponse {
    public abstract StsError getError();

    public void parse(InputStream inputStream) throws IOException, StsParseException {
        try {
            XmlPullParser newPullParser = Xml.newPullParser();
            String fromStream = Strings.fromStream(inputStream, Strings.Utf8Charset);
            Logger.info(new RedactableResponse(String.format(Locale.US, "%s: %s", new Object[]{getClass().getSimpleName(), fromStream})));
            newPullParser.setInput(new StringReader(fromStream));
            parse(newPullParser);
            if (getError() != null) {
                StsError error = getError();
                ClientAnalytics.get().logEvent("Server errors", error.getCode().name(), getClass().getSimpleName() + ": " + error.getOriginalErrorMessage());
            }
        } catch (Throwable e) {
            throw new StsParseException("XML response could not be properly parsed.", e, new Object[0]);
        }
    }

    protected abstract void parse(XmlPullParser xmlPullParser) throws IOException, StsParseException;

    public boolean succeeded() {
        return getError() == null;
    }
}
