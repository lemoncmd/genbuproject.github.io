package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import java.util.Locale;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class NodeScope {
    private final int _depth;
    private final XmlPullParser _parser;

    NodeScope(XmlPullParser xmlPullParser) {
        this._parser = xmlPullParser;
        this._depth = xmlPullParser.getDepth();
    }

    void finish() throws XmlPullParserException, IOException {
        while (hasMore()) {
            this._parser.next();
        }
    }

    int getDepth() {
        return this._depth;
    }

    boolean hasMore() throws XmlPullParserException {
        switch (this._parser.getEventType()) {
            case NativeRegExp.MATCH /*1*/:
                return false;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                if (this._depth == this._parser.getDepth()) {
                    return false;
                }
                break;
        }
        return true;
    }

    String nextRequiredText() throws XmlPullParserException, IOException, StsParseException {
        String name = this._parser.getName();
        Object nextText = this._parser.nextText();
        if (!TextUtils.isEmpty(nextText)) {
            return nextText;
        }
        throw new StsParseException(String.format(Locale.US, "Expected text of %s is empty", new Object[]{name}), new Object[0]);
    }

    void nextStartTag(String str) throws XmlPullParserException, IOException, StsParseException {
        if (!nextStartTagNoThrow(str)) {
            throw new StsParseException("Required node \"%s\" is missing.", str);
        }
    }

    boolean nextStartTagNoThrow() throws XmlPullParserException, IOException {
        while (hasMore()) {
            if (this._parser.next() == 2) {
                return true;
            }
        }
        return false;
    }

    boolean nextStartTagNoThrow(String str) throws XmlPullParserException, IOException {
        while (nextStartTagNoThrow()) {
            if (BasePullParser.getPrefixedTagName(this._parser).equals(str)) {
                return true;
            }
            skipElement();
        }
        return false;
    }

    protected void skipElement() throws XmlPullParserException, IOException {
        int depth = this._parser.getDepth();
        if (depth == this._depth) {
            finish();
            return;
        }
        int eventType = this._parser.getEventType();
        while (true) {
            if (depth != this._parser.getDepth() || r0 != 3) {
                eventType = this._parser.next();
            } else {
                return;
            }
        }
    }
}
