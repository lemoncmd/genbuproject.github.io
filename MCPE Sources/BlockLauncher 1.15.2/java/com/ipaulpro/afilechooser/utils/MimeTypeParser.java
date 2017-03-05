package com.ipaulpro.afilechooser.utils;

import android.content.res.XmlResourceParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MimeTypeParser {
    public static final String ATTR_EXTENSION = "extension";
    public static final String ATTR_MIMETYPE = "mimetype";
    public static final String TAG_MIMETYPES = "MimeTypes";
    public static final String TAG_TYPE = "type";
    private MimeTypes mMimeTypes;
    private XmlPullParser mXpp;

    public MimeTypes fromXml(InputStream in) throws XmlPullParserException, IOException {
        this.mXpp = XmlPullParserFactory.newInstance().newPullParser();
        this.mXpp.setInput(new InputStreamReader(in));
        return parse();
    }

    public MimeTypes fromXmlResource(XmlResourceParser in) throws XmlPullParserException, IOException {
        this.mXpp = in;
        return parse();
    }

    public MimeTypes parse() throws XmlPullParserException, IOException {
        this.mMimeTypes = new MimeTypes();
        int eventType = this.mXpp.getEventType();
        while (eventType != 1) {
            String tag = this.mXpp.getName();
            if (eventType == 2) {
                if (!tag.equals(TAG_MIMETYPES) && tag.equals(TAG_TYPE)) {
                    addMimeTypeStart();
                }
            } else if (eventType == 3 && tag.equals(TAG_MIMETYPES)) {
            }
            eventType = this.mXpp.next();
        }
        return this.mMimeTypes;
    }

    private void addMimeTypeStart() {
        this.mMimeTypes.put(this.mXpp.getAttributeValue(null, ATTR_EXTENSION), this.mXpp.getAttributeValue(null, ATTR_MIMETYPE));
    }
}
