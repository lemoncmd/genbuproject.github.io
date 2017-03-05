package com.microsoft.onlineid.sts.response.parsers;

import android.text.TextUtils;
import com.microsoft.onlineid.sts.exception.StsParseException;
import java.io.IOException;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public abstract class BasePullParser {
    private boolean _parseCalled = false;
    protected final XmlPullParser _parser;
    protected final String _parserTag;
    protected final String _parserTagNamespace;
    private final NodeScope _scope;

    public BasePullParser(XmlPullParser xmlPullParser, String str, String str2) {
        this._parser = xmlPullParser;
        this._parserTag = str2;
        this._parserTagNamespace = str;
        this._scope = getLocation();
    }

    private void finish() throws XmlPullParserException, IOException {
        this._scope.finish();
    }

    private String getPrefixedAttributeName(int i) throws XmlPullParserException {
        int eventType = this._parser.getEventType();
        if (eventType != 2 && eventType != 3) {
            throw new XmlPullParserException("Attribute should only be retrieved on a start or end tag.");
        } else if (i < 0 || i >= this._parser.getAttributeCount()) {
            throw new XmlPullParserException("Invalid attribute location.");
        } else {
            Object attributePrefix = this._parser.getAttributePrefix(i);
            String attributeName = this._parser.getAttributeName(i);
            return TextUtils.isEmpty(attributePrefix) ? attributeName : attributePrefix + ":" + attributeName;
        }
    }

    private String getPrefixedNamespaceName(int i) throws XmlPullParserException {
        int eventType = this._parser.getEventType();
        if (eventType == 2 || eventType == 3) {
            eventType = this._parser.getDepth();
            if (i < this._parser.getNamespaceCount(eventType - 1) || i >= this._parser.getNamespaceCount(eventType)) {
                throw new XmlPullParserException("Invalid namespace location.");
            }
            Object namespacePrefix = this._parser.getNamespacePrefix(i);
            return TextUtils.isEmpty(namespacePrefix) ? "xmlns" : "xmlns:" + namespacePrefix;
        } else {
            throw new XmlPullParserException("Namespace name should only be retrieved on a start or end tag.");
        }
    }

    protected static String getPrefixedTagName(XmlPullParser xmlPullParser) throws XmlPullParserException {
        int eventType = xmlPullParser.getEventType();
        if (eventType == 2 || eventType == 3) {
            Object prefix = xmlPullParser.getPrefix();
            String name = xmlPullParser.getName();
            return TextUtils.isEmpty(prefix) ? name : prefix + ":" + name;
        } else {
            throw new XmlPullParserException("Tag name should only be retrieved on a start or end tag.");
        }
    }

    protected NodeScope getLocation() {
        return new NodeScope(this._parser);
    }

    protected String getPrefixedTagName() throws XmlPullParserException {
        return getPrefixedTagName(this._parser);
    }

    protected boolean hasMore() throws XmlPullParserException {
        return this._scope.hasMore();
    }

    protected String nextRequiredText() throws XmlPullParserException, IOException, StsParseException {
        return this._scope.nextRequiredText();
    }

    protected void nextStartTag(String str) throws XmlPullParserException, IOException, StsParseException {
        this._scope.nextStartTag(str);
    }

    protected boolean nextStartTagNoThrow() throws XmlPullParserException, IOException {
        return this._scope.nextStartTagNoThrow();
    }

    protected boolean nextStartTagNoThrow(String str) throws XmlPullParserException, IOException {
        return this._scope.nextStartTagNoThrow(str);
    }

    protected abstract void onParse() throws XmlPullParserException, IOException, StsParseException;

    public final void parse() throws IOException, StsParseException {
        try {
            if (this._parseCalled) {
                throw new IllegalStateException("Parse has already been called.");
            }
            this._parseCalled = true;
            if (this._scope.getDepth() == 0) {
                this._parser.require(0, null, null);
                this._parser.next();
            }
            this._parser.require(2, this._parserTagNamespace, this._parserTag);
            onParse();
            finish();
        } catch (Throwable e) {
            throw new StsParseException("XML was either invalid or failed to parse.", e, new Object[0]);
        }
    }

    protected String readRawOuterXml() throws XmlPullParserException, IOException {
        StringBuilder stringBuilder = new StringBuilder();
        NodeScope location = getLocation();
        while (location.hasMore()) {
            switch (this._parser.getEventType()) {
                case NativeRegExp.PREFIX /*2*/:
                    int namespaceCount;
                    stringBuilder.append('<').append(getPrefixedTagName());
                    int namespaceCount2 = this._parser.getNamespaceCount(this._parser.getDepth());
                    for (namespaceCount = this._parser.getNamespaceCount(this._parser.getDepth() - 1); namespaceCount < namespaceCount2; namespaceCount++) {
                        stringBuilder.append(' ').append(getPrefixedNamespaceName(namespaceCount)).append("=\"").append(this._parser.getNamespaceUri(namespaceCount)).append('\"');
                    }
                    for (namespaceCount = 0; namespaceCount < this._parser.getAttributeCount(); namespaceCount++) {
                        stringBuilder.append(' ').append(getPrefixedAttributeName(namespaceCount)).append("=\"").append(this._parser.getAttributeValue(namespaceCount)).append('\"');
                    }
                    stringBuilder.append('>');
                    break;
                case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                    stringBuilder.append("</").append(getPrefixedTagName(this._parser)).append('>');
                    break;
                case NativeRegExp.JSREG_MULTILINE /*4*/:
                    stringBuilder.append(this._parser.getText());
                    break;
                default:
                    break;
            }
            this._parser.next();
        }
        stringBuilder.append("</").append(getPrefixedTagName(this._parser)).append('>');
        return stringBuilder.toString();
    }

    protected void skipElement() throws XmlPullParserException, IOException {
        this._scope.skipElement();
    }

    protected void verifyParseCalled() {
        if (!this._parseCalled) {
            throw new IllegalStateException("Cannot call this method without calling parse.");
        }
    }
}
