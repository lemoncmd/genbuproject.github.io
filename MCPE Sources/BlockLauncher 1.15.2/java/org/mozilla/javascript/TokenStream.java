package org.mozilla.javascript;

import java.io.IOException;
import java.io.Reader;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import org.mozilla.javascript.Token.CommentType;

class TokenStream {
    static final /* synthetic */ boolean $assertionsDisabled = (!TokenStream.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    private static final char BYTE_ORDER_MARK = '\ufeff';
    private static final int EOF_CHAR = -1;
    private ObjToIntMap allStrings = new ObjToIntMap(50);
    private int commentCursor = EOF_CHAR;
    private String commentPrefix = BuildConfig.FLAVOR;
    CommentType commentType;
    int cursor;
    private boolean dirtyLine;
    private boolean hitEOF = $assertionsDisabled;
    private boolean isBinary;
    private boolean isHex;
    private boolean isOctal;
    private boolean isOldOctal;
    private int lineEndChar = EOF_CHAR;
    private int lineStart = 0;
    int lineno;
    private double number;
    private Parser parser;
    private int quoteChar;
    String regExpFlags;
    private char[] sourceBuffer;
    int sourceCursor;
    private int sourceEnd;
    private Reader sourceReader;
    private String sourceString;
    private String string = BuildConfig.FLAVOR;
    private char[] stringBuffer = new char[Token.RESERVED];
    private int stringBufferTop;
    int tokenBeg;
    int tokenEnd;
    private final int[] ungetBuffer = new int[3];
    private int ungetCursor;
    private boolean xmlIsAttribute;
    private boolean xmlIsTagContent;
    private int xmlOpenTagsCount;

    TokenStream(Parser parser, Reader reader, String str, int i) {
        this.parser = parser;
        this.lineno = i;
        if (reader != null) {
            if (str != null) {
                Kit.codeBug();
            }
            this.sourceReader = reader;
            this.sourceBuffer = new char[EnchantType.axe];
            this.sourceEnd = 0;
        } else {
            if (str == null) {
                Kit.codeBug();
            }
            this.sourceString = str;
            this.sourceEnd = str.length();
        }
        this.cursor = 0;
        this.sourceCursor = 0;
    }

    private void addToString(int i) {
        int i2 = this.stringBufferTop;
        if (i2 == this.stringBuffer.length) {
            Object obj = new char[(this.stringBuffer.length * 2)];
            System.arraycopy(this.stringBuffer, 0, obj, 0, i2);
            this.stringBuffer = obj;
        }
        this.stringBuffer[i2] = (char) i;
        this.stringBufferTop = i2 + 1;
    }

    private boolean canUngetChar() {
        return (this.ungetCursor == 0 || this.ungetBuffer[this.ungetCursor + EOF_CHAR] != 10) ? true : $assertionsDisabled;
    }

    private final int charAt(int i) {
        if (i < 0) {
            return EOF_CHAR;
        }
        if (this.sourceString != null) {
            return i < this.sourceEnd ? this.sourceString.charAt(i) : EOF_CHAR;
        } else {
            if (i >= this.sourceEnd) {
                int i2 = this.sourceCursor;
                try {
                    if (!fillSourceBuffer()) {
                        return EOF_CHAR;
                    }
                    i -= i2 - this.sourceCursor;
                } catch (IOException e) {
                    return EOF_CHAR;
                }
            }
            return this.sourceBuffer[i];
        }
    }

    private String convertLastCharToHex(String str) {
        int i = 0;
        int length = str.length() + EOF_CHAR;
        StringBuffer stringBuffer = new StringBuffer(str.substring(0, length));
        stringBuffer.append("\\u");
        String toHexString = Integer.toHexString(str.charAt(length));
        while (i < 4 - toHexString.length()) {
            stringBuffer.append('0');
            i++;
        }
        stringBuffer.append(toHexString);
        return stringBuffer.toString();
    }

    private boolean fillSourceBuffer() throws IOException {
        if (this.sourceString != null) {
            Kit.codeBug();
        }
        if (this.sourceEnd == this.sourceBuffer.length) {
            if (this.lineStart == 0 || isMarkingComment()) {
                Object obj = new char[(this.sourceBuffer.length * 2)];
                System.arraycopy(this.sourceBuffer, 0, obj, 0, this.sourceEnd);
                this.sourceBuffer = obj;
            } else {
                System.arraycopy(this.sourceBuffer, this.lineStart, this.sourceBuffer, 0, this.sourceEnd - this.lineStart);
                this.sourceEnd -= this.lineStart;
                this.sourceCursor -= this.lineStart;
                this.lineStart = 0;
            }
        }
        int read = this.sourceReader.read(this.sourceBuffer, this.sourceEnd, this.sourceBuffer.length - this.sourceEnd);
        if (read < 0) {
            return $assertionsDisabled;
        }
        this.sourceEnd += read;
        return true;
    }

    private int getChar() throws IOException {
        return getChar(true);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getChar(boolean r8) throws java.io.IOException {
        /*
        r7 = this;
        r6 = 13;
        r5 = 1;
        r2 = -1;
        r1 = 10;
        r0 = r7.ungetCursor;
        if (r0 == 0) goto L_0x0039;
    L_0x000a:
        r0 = r7.cursor;
        r0 = r0 + 1;
        r7.cursor = r0;
        r0 = r7.ungetBuffer;
        r1 = r7.ungetCursor;
        r1 = r1 + -1;
        r7.ungetCursor = r1;
        r0 = r0[r1];
    L_0x001a:
        return r0;
    L_0x001b:
        r0 = r7.cursor;
        r0 = r0 + 1;
        r7.cursor = r0;
        r0 = r7.sourceString;
        r3 = r7.sourceCursor;
        r4 = r3 + 1;
        r7.sourceCursor = r4;
        r0 = r0.charAt(r3);
    L_0x002d:
        r3 = r7.lineEndChar;
        if (r3 < 0) goto L_0x0076;
    L_0x0031:
        r3 = r7.lineEndChar;
        if (r3 != r6) goto L_0x0068;
    L_0x0035:
        if (r0 != r1) goto L_0x0068;
    L_0x0037:
        r7.lineEndChar = r1;
    L_0x0039:
        r0 = r7.sourceString;
        if (r0 == 0) goto L_0x0047;
    L_0x003d:
        r0 = r7.sourceCursor;
        r3 = r7.sourceEnd;
        if (r0 != r3) goto L_0x001b;
    L_0x0043:
        r7.hitEOF = r5;
        r0 = r2;
        goto L_0x001a;
    L_0x0047:
        r0 = r7.sourceCursor;
        r3 = r7.sourceEnd;
        if (r0 != r3) goto L_0x0057;
    L_0x004d:
        r0 = r7.fillSourceBuffer();
        if (r0 != 0) goto L_0x0057;
    L_0x0053:
        r7.hitEOF = r5;
        r0 = r2;
        goto L_0x001a;
    L_0x0057:
        r0 = r7.cursor;
        r0 = r0 + 1;
        r7.cursor = r0;
        r0 = r7.sourceBuffer;
        r3 = r7.sourceCursor;
        r4 = r3 + 1;
        r7.sourceCursor = r4;
        r0 = r0[r3];
        goto L_0x002d;
    L_0x0068:
        r7.lineEndChar = r2;
        r3 = r7.sourceCursor;
        r3 = r3 + -1;
        r7.lineStart = r3;
        r3 = r7.lineno;
        r3 = r3 + 1;
        r7.lineno = r3;
    L_0x0076:
        r3 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        if (r0 > r3) goto L_0x0082;
    L_0x007a:
        if (r0 == r1) goto L_0x007e;
    L_0x007c:
        if (r0 != r6) goto L_0x001a;
    L_0x007e:
        r7.lineEndChar = r0;
        r0 = r1;
        goto L_0x001a;
    L_0x0082:
        r3 = 65279; // 0xfeff float:9.1475E-41 double:3.2252E-319;
        if (r0 == r3) goto L_0x001a;
    L_0x0087:
        if (r8 == 0) goto L_0x008f;
    L_0x0089:
        r3 = isJSFormatChar(r0);
        if (r3 != 0) goto L_0x0039;
    L_0x008f:
        r2 = org.mozilla.javascript.ScriptRuntime.isJSLineTerminator(r0);
        if (r2 == 0) goto L_0x001a;
    L_0x0095:
        r7.lineEndChar = r0;
        r0 = r1;
        goto L_0x001a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.TokenStream.getChar(boolean):int");
    }

    private int getCharIgnoreLineEnd() throws IOException {
        if (this.ungetCursor != 0) {
            this.cursor++;
            int[] iArr = this.ungetBuffer;
            int i = this.ungetCursor + EOF_CHAR;
            this.ungetCursor = i;
            return iArr[i];
        }
        int charAt;
        do {
            int i2;
            if (this.sourceString != null) {
                if (this.sourceCursor == this.sourceEnd) {
                    this.hitEOF = true;
                    return EOF_CHAR;
                }
                this.cursor++;
                String str = this.sourceString;
                i2 = this.sourceCursor;
                this.sourceCursor = i2 + 1;
                charAt = str.charAt(i2);
            } else if (this.sourceCursor != this.sourceEnd || fillSourceBuffer()) {
                this.cursor++;
                char[] cArr = this.sourceBuffer;
                i2 = this.sourceCursor;
                this.sourceCursor = i2 + 1;
                charAt = cArr[i2];
            } else {
                this.hitEOF = true;
                return EOF_CHAR;
            }
            if (charAt <= Token.VOID) {
                if (charAt != 10 && charAt != 13) {
                    return charAt;
                }
                this.lineEndChar = charAt;
                return 10;
            } else if (charAt == 65279) {
                return charAt;
            }
        } while (isJSFormatChar(charAt));
        if (!ScriptRuntime.isJSLineTerminator(charAt)) {
            return charAt;
        }
        this.lineEndChar = charAt;
        return 10;
    }

    private String getStringFromBuffer() {
        this.tokenEnd = this.cursor;
        return new String(this.stringBuffer, 0, this.stringBufferTop);
    }

    private static boolean isAlpha(int i) {
        return i <= 90 ? 65 <= i ? true : $assertionsDisabled : (97 > i || i > Token.CONTINUE) ? $assertionsDisabled : true;
    }

    static boolean isDigit(int i) {
        return (48 > i || i > 57) ? $assertionsDisabled : true;
    }

    private static boolean isJSFormatChar(int i) {
        return (i <= Token.VOID || Character.getType((char) i) != 16) ? $assertionsDisabled : true;
    }

    static boolean isJSSpace(int i) {
        return i <= Token.VOID ? (i == 32 || i == 9 || i == 12 || i == 11) ? true : $assertionsDisabled : (i == Token.WITHEXPR || i == 65279 || Character.getType((char) i) == 12) ? true : $assertionsDisabled;
    }

    static boolean isKeyword(String str, int i, boolean z) {
        return stringToKeyword(str, i, z) != 0 ? true : $assertionsDisabled;
    }

    private boolean isMarkingComment() {
        return this.commentCursor != EOF_CHAR ? true : $assertionsDisabled;
    }

    private void markCommentStart() {
        markCommentStart(BuildConfig.FLAVOR);
    }

    private void markCommentStart(String str) {
        if (this.parser.compilerEnv.isRecordingComments() && this.sourceReader != null) {
            this.commentPrefix = str;
            this.commentCursor = this.sourceCursor + EOF_CHAR;
        }
    }

    private boolean matchChar(int i) throws IOException {
        int charIgnoreLineEnd = getCharIgnoreLineEnd();
        if (charIgnoreLineEnd == i) {
            this.tokenEnd = this.cursor;
            return true;
        }
        ungetCharIgnoreLineEnd(charIgnoreLineEnd);
        return $assertionsDisabled;
    }

    private int peekChar() throws IOException {
        int i = getChar();
        ungetChar(i);
        return i;
    }

    private boolean readCDATA() throws IOException {
        int i = getChar();
        while (i != EOF_CHAR) {
            addToString(i);
            if (i == 93 && peekChar() == 93) {
                i = getChar();
                addToString(i);
                if (peekChar() == 62) {
                    addToString(getChar());
                    return true;
                }
            } else {
                i = getChar();
            }
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return $assertionsDisabled;
    }

    private boolean readEntity() throws IOException {
        int i = getChar();
        int i2 = 1;
        while (i != EOF_CHAR) {
            addToString(i);
            switch (i) {
                case Token.ENUM_INIT_ARRAY /*60*/:
                    i2++;
                    break;
                case Token.ENUM_NEXT /*62*/:
                    i2 += EOF_CHAR;
                    if (i2 != 0) {
                        break;
                    }
                    return true;
                default:
                    break;
            }
            i = getChar();
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return $assertionsDisabled;
    }

    private boolean readPI() throws IOException {
        int i = getChar();
        while (i != EOF_CHAR) {
            addToString(i);
            if (i == 63 && peekChar() == 62) {
                addToString(getChar());
                return true;
            }
            i = getChar();
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return $assertionsDisabled;
    }

    private boolean readQuotedString(int i) throws IOException {
        int i2 = getChar();
        while (i2 != EOF_CHAR) {
            addToString(i2);
            if (i2 == i) {
                return true;
            }
            i2 = getChar();
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return $assertionsDisabled;
    }

    private boolean readXmlComment() throws IOException {
        int i = getChar();
        while (i != EOF_CHAR) {
            addToString(i);
            if (i == 45 && peekChar() == 45) {
                i = getChar();
                addToString(i);
                if (peekChar() == 62) {
                    addToString(getChar());
                    return true;
                }
            } else {
                i = getChar();
            }
        }
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return $assertionsDisabled;
    }

    private void skipLine() throws IOException {
        int i;
        do {
            i = getChar();
            if (i == EOF_CHAR) {
                break;
            }
        } while (i != 10);
        ungetChar(i);
        this.tokenEnd = this.cursor;
    }

    private static int stringToKeyword(String str, int i, boolean z) {
        return i < Context.VERSION_ES6 ? stringToKeywordForJS(str) : stringToKeywordForES(str, z);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int stringToKeywordForES(java.lang.String r9, boolean r10) {
        /*
        r1 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        r7 = 2;
        r0 = 0;
        r2 = 128; // 0x80 float:1.8E-43 double:6.3E-322;
        r6 = 1;
        r3 = 0;
        r4 = r9.length();
        switch(r4) {
            case 2: goto L_0x001f;
            case 3: goto L_0x0050;
            case 4: goto L_0x00b5;
            case 5: goto L_0x015d;
            case 6: goto L_0x01d4;
            case 7: goto L_0x022c;
            case 8: goto L_0x0262;
            case 9: goto L_0x0288;
            case 10: goto L_0x02a6;
            default: goto L_0x000f;
        };
    L_0x000f:
        r2 = r3;
        r1 = r0;
    L_0x0011:
        if (r2 == 0) goto L_0x001c;
    L_0x0013:
        if (r2 == r9) goto L_0x001c;
    L_0x0015:
        r2 = r2.equals(r9);
        if (r2 != 0) goto L_0x001c;
    L_0x001b:
        r1 = r0;
    L_0x001c:
        if (r1 != 0) goto L_0x02c4;
    L_0x001e:
        return r0;
    L_0x001f:
        r1 = r9.charAt(r6);
        r2 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r1 != r2) goto L_0x0032;
    L_0x0027:
        r1 = r9.charAt(r0);
        r2 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x002f:
        r1 = 113; // 0x71 float:1.58E-43 double:5.6E-322;
        goto L_0x001c;
    L_0x0032:
        r2 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r1 != r2) goto L_0x0041;
    L_0x0036:
        r1 = r9.charAt(r0);
        r2 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x003e:
        r1 = 52;
        goto L_0x001c;
    L_0x0041:
        r2 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0045:
        r1 = r9.charAt(r0);
        r2 = 100;
        if (r1 != r2) goto L_0x000f;
    L_0x004d:
        r1 = 119; // 0x77 float:1.67E-43 double:5.9E-322;
        goto L_0x001c;
    L_0x0050:
        r2 = r9.charAt(r0);
        switch(r2) {
            case 102: goto L_0x005a;
            case 108: goto L_0x006b;
            case 110: goto L_0x007e;
            case 116: goto L_0x0091;
            case 118: goto L_0x00a3;
            default: goto L_0x0057;
        };
    L_0x0057:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x005a:
        r2 = r9.charAt(r7);
        if (r2 != r1) goto L_0x000f;
    L_0x0060:
        r1 = r9.charAt(r6);
        r2 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0068:
        r1 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        goto L_0x001c;
    L_0x006b:
        r1 = r9.charAt(r7);
        r2 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0073:
        r1 = r9.charAt(r6);
        r2 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x007b:
        r1 = 154; // 0x9a float:2.16E-43 double:7.6E-322;
        goto L_0x001c;
    L_0x007e:
        r1 = r9.charAt(r7);
        r2 = 119; // 0x77 float:1.67E-43 double:5.9E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0086:
        r1 = r9.charAt(r6);
        r2 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x008e:
        r1 = 30;
        goto L_0x001c;
    L_0x0091:
        r2 = r9.charAt(r7);
        r4 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x0099:
        r2 = r9.charAt(r6);
        if (r2 != r1) goto L_0x000f;
    L_0x009f:
        r1 = 82;
        goto L_0x001c;
    L_0x00a3:
        r2 = r9.charAt(r7);
        if (r2 != r1) goto L_0x000f;
    L_0x00a9:
        r1 = r9.charAt(r6);
        r2 = 97;
        if (r1 != r2) goto L_0x000f;
    L_0x00b1:
        r1 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
        goto L_0x001c;
    L_0x00b5:
        r4 = r9.charAt(r0);
        switch(r4) {
            case 99: goto L_0x00c0;
            case 101: goto L_0x00dd;
            case 110: goto L_0x010f;
            case 116: goto L_0x0118;
            case 118: goto L_0x014b;
            case 119: goto L_0x0154;
            default: goto L_0x00bc;
        };
    L_0x00bc:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x00c0:
        r1 = 3;
        r1 = r9.charAt(r1);
        r2 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x00c9:
        r1 = r9.charAt(r7);
        r2 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x00d1:
        r1 = r9.charAt(r6);
        r2 = 97;
        if (r1 != r2) goto L_0x000f;
    L_0x00d9:
        r1 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        goto L_0x001c;
    L_0x00dd:
        r4 = 3;
        r4 = r9.charAt(r4);
        r5 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r4 != r5) goto L_0x00f8;
    L_0x00e6:
        r2 = r9.charAt(r7);
        r4 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x00ee:
        r2 = r9.charAt(r6);
        r4 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x00f6:
        goto L_0x001c;
    L_0x00f8:
        r1 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r4 != r1) goto L_0x000f;
    L_0x00fc:
        r1 = r9.charAt(r7);
        r4 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r1 != r4) goto L_0x000f;
    L_0x0104:
        r1 = r9.charAt(r6);
        r4 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r1 != r4) goto L_0x000f;
    L_0x010c:
        r1 = r2;
        goto L_0x001c;
    L_0x010f:
        r1 = "null";
        r2 = 42;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0118:
        r2 = 3;
        r2 = r9.charAt(r2);
        r4 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r2 != r4) goto L_0x0133;
    L_0x0121:
        r2 = r9.charAt(r7);
        r4 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x0129:
        r2 = r9.charAt(r6);
        if (r2 != r1) goto L_0x000f;
    L_0x012f:
        r1 = 45;
        goto L_0x001c;
    L_0x0133:
        r1 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r2 != r1) goto L_0x000f;
    L_0x0137:
        r1 = r9.charAt(r7);
        r2 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x013f:
        r1 = r9.charAt(r6);
        r2 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0147:
        r1 = 43;
        goto L_0x001c;
    L_0x014b:
        r1 = "void";
        r2 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0154:
        r1 = "with";
        r2 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x015d:
        r1 = r9.charAt(r7);
        switch(r1) {
            case 97: goto L_0x0168;
            case 101: goto L_0x0182;
            case 105: goto L_0x01a0;
            case 108: goto L_0x01a9;
            case 110: goto L_0x01b2;
            case 112: goto L_0x01bb;
            case 114: goto L_0x01c2;
            case 116: goto L_0x01cb;
            default: goto L_0x0164;
        };
    L_0x0164:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x0168:
        r1 = r9.charAt(r0);
        r4 = 99;
        if (r1 != r4) goto L_0x0177;
    L_0x0170:
        r1 = "class";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0177:
        r4 = 97;
        if (r1 != r4) goto L_0x000f;
    L_0x017b:
        r1 = "await";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0182:
        r1 = r9.charAt(r0);
        r2 = 98;
        if (r1 != r2) goto L_0x0193;
    L_0x018a:
        r1 = "break";
        r2 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0193:
        r2 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0197:
        r1 = "yield";
        r2 = 73;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01a0:
        r1 = "while";
        r2 = 118; // 0x76 float:1.65E-43 double:5.83E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01a9:
        r1 = "false";
        r2 = 44;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01b2:
        r1 = "const";
        r2 = 155; // 0x9b float:2.17E-43 double:7.66E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01bb:
        r1 = "super";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01c2:
        r1 = "throw";
        r2 = 50;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01cb:
        r1 = "catch";
        r2 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01d4:
        r4 = r9.charAt(r6);
        switch(r4) {
            case 101: goto L_0x01df;
            case 109: goto L_0x01fa;
            case 116: goto L_0x0201;
            case 117: goto L_0x020a;
            case 119: goto L_0x0213;
            case 120: goto L_0x021c;
            case 121: goto L_0x0223;
            default: goto L_0x01db;
        };
    L_0x01db:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x01df:
        r2 = r9.charAt(r0);
        r4 = 100;
        if (r2 != r4) goto L_0x01f0;
    L_0x01e7:
        r1 = "delete";
        r2 = 31;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01f0:
        if (r2 != r1) goto L_0x000f;
    L_0x01f2:
        r1 = "return";
        r2 = 4;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01fa:
        r1 = "import";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0201:
        if (r10 == 0) goto L_0x020a;
    L_0x0203:
        r1 = "static";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x020a:
        if (r10 == 0) goto L_0x0213;
    L_0x020c:
        r1 = "public";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0213:
        r1 = "switch";
        r2 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x021c:
        r1 = "export";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0223:
        r1 = "typeof";
        r2 = 32;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x022c:
        r1 = r9.charAt(r6);
        switch(r1) {
            case 97: goto L_0x0237;
            case 101: goto L_0x0240;
            case 105: goto L_0x0249;
            case 114: goto L_0x0252;
            case 120: goto L_0x025b;
            default: goto L_0x0233;
        };
    L_0x0233:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x0237:
        if (r10 == 0) goto L_0x0240;
    L_0x0239:
        r1 = "package";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0240:
        r1 = "default";
        r2 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0249:
        r1 = "finally";
        r2 = 126; // 0x7e float:1.77E-43 double:6.23E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0252:
        if (r10 == 0) goto L_0x025b;
    L_0x0254:
        r1 = "private";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x025b:
        r1 = "extends";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0262:
        r1 = r9.charAt(r0);
        switch(r1) {
            case 99: goto L_0x026d;
            case 100: goto L_0x0276;
            case 101: goto L_0x0269;
            case 102: goto L_0x027f;
            default: goto L_0x0269;
        };
    L_0x0269:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x026d:
        r1 = "continue";
        r2 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0276:
        r1 = "debugger";
        r2 = 161; // 0xa1 float:2.26E-43 double:7.95E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x027f:
        r1 = "function";
        r2 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0288:
        r1 = r9.charAt(r0);
        r4 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r1 != r4) goto L_0x0299;
    L_0x0290:
        if (r10 == 0) goto L_0x0299;
    L_0x0292:
        r1 = "interface";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0299:
        r4 = 112; // 0x70 float:1.57E-43 double:5.53E-322;
        if (r1 != r4) goto L_0x000f;
    L_0x029d:
        if (r10 == 0) goto L_0x000f;
    L_0x029f:
        r1 = "protected";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x02a6:
        r1 = r9.charAt(r6);
        r4 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r1 != r4) goto L_0x02b7;
    L_0x02ae:
        if (r10 == 0) goto L_0x02b7;
    L_0x02b0:
        r1 = "implements";
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x02b7:
        r2 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x02bb:
        r1 = "instanceof";
        r2 = 53;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x02c4:
        r0 = r1 & 255;
        goto L_0x001e;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.TokenStream.stringToKeywordForES(java.lang.String, boolean):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int stringToKeywordForJS(java.lang.String r9) {
        /*
        r2 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        r7 = 2;
        r0 = 0;
        r6 = 1;
        r1 = 128; // 0x80 float:1.8E-43 double:6.3E-322;
        r3 = 0;
        r4 = r9.length();
        switch(r4) {
            case 2: goto L_0x001f;
            case 3: goto L_0x0050;
            case 4: goto L_0x00c7;
            case 5: goto L_0x018f;
            case 6: goto L_0x0211;
            case 7: goto L_0x0265;
            case 8: goto L_0x0292;
            case 9: goto L_0x02c0;
            case 10: goto L_0x02dc;
            case 11: goto L_0x000f;
            case 12: goto L_0x02f5;
            default: goto L_0x000f;
        };
    L_0x000f:
        r2 = r3;
        r1 = r0;
    L_0x0011:
        if (r2 == 0) goto L_0x001c;
    L_0x0013:
        if (r2 == r9) goto L_0x001c;
    L_0x0015:
        r2 = r2.equals(r9);
        if (r2 != 0) goto L_0x001c;
    L_0x001b:
        r1 = r0;
    L_0x001c:
        if (r1 != 0) goto L_0x02f9;
    L_0x001e:
        return r0;
    L_0x001f:
        r1 = r9.charAt(r6);
        r2 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r1 != r2) goto L_0x0032;
    L_0x0027:
        r1 = r9.charAt(r0);
        r2 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x002f:
        r1 = 113; // 0x71 float:1.58E-43 double:5.6E-322;
        goto L_0x001c;
    L_0x0032:
        r2 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r1 != r2) goto L_0x0041;
    L_0x0036:
        r1 = r9.charAt(r0);
        r2 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x003e:
        r1 = 52;
        goto L_0x001c;
    L_0x0041:
        r2 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0045:
        r1 = r9.charAt(r0);
        r2 = 100;
        if (r1 != r2) goto L_0x000f;
    L_0x004d:
        r1 = 119; // 0x77 float:1.67E-43 double:5.9E-322;
        goto L_0x001c;
    L_0x0050:
        r4 = r9.charAt(r0);
        switch(r4) {
            case 102: goto L_0x005a;
            case 105: goto L_0x006b;
            case 108: goto L_0x007c;
            case 110: goto L_0x008f;
            case 116: goto L_0x00a3;
            case 118: goto L_0x00b5;
            default: goto L_0x0057;
        };
    L_0x0057:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x005a:
        r1 = r9.charAt(r7);
        if (r1 != r2) goto L_0x000f;
    L_0x0060:
        r1 = r9.charAt(r6);
        r2 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0068:
        r1 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        goto L_0x001c;
    L_0x006b:
        r2 = r9.charAt(r7);
        r4 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x0073:
        r2 = r9.charAt(r6);
        r4 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x007b:
        goto L_0x001c;
    L_0x007c:
        r1 = r9.charAt(r7);
        r2 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0084:
        r1 = r9.charAt(r6);
        r2 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x008c:
        r1 = 154; // 0x9a float:2.16E-43 double:7.6E-322;
        goto L_0x001c;
    L_0x008f:
        r1 = r9.charAt(r7);
        r2 = 119; // 0x77 float:1.67E-43 double:5.9E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0097:
        r1 = r9.charAt(r6);
        r2 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x009f:
        r1 = 30;
        goto L_0x001c;
    L_0x00a3:
        r1 = r9.charAt(r7);
        r4 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        if (r1 != r4) goto L_0x000f;
    L_0x00ab:
        r1 = r9.charAt(r6);
        if (r1 != r2) goto L_0x000f;
    L_0x00b1:
        r1 = 82;
        goto L_0x001c;
    L_0x00b5:
        r1 = r9.charAt(r7);
        if (r1 != r2) goto L_0x000f;
    L_0x00bb:
        r1 = r9.charAt(r6);
        r2 = 97;
        if (r1 != r2) goto L_0x000f;
    L_0x00c3:
        r1 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
        goto L_0x001c;
    L_0x00c7:
        r4 = r9.charAt(r0);
        switch(r4) {
            case 98: goto L_0x00d2;
            case 99: goto L_0x00d6;
            case 101: goto L_0x0107;
            case 103: goto L_0x0139;
            case 108: goto L_0x013d;
            case 110: goto L_0x0141;
            case 116: goto L_0x014a;
            case 118: goto L_0x017d;
            case 119: goto L_0x0186;
            default: goto L_0x00ce;
        };
    L_0x00ce:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x00d2:
        r2 = "byte";
        goto L_0x0011;
    L_0x00d6:
        r4 = 3;
        r4 = r9.charAt(r4);
        r5 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r4 != r5) goto L_0x00f3;
    L_0x00df:
        r1 = r9.charAt(r7);
        r2 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x00e7:
        r1 = r9.charAt(r6);
        r2 = 97;
        if (r1 != r2) goto L_0x000f;
    L_0x00ef:
        r1 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        goto L_0x001c;
    L_0x00f3:
        if (r4 != r2) goto L_0x000f;
    L_0x00f5:
        r2 = r9.charAt(r7);
        r4 = 97;
        if (r2 != r4) goto L_0x000f;
    L_0x00fd:
        r2 = r9.charAt(r6);
        r4 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x0105:
        goto L_0x001c;
    L_0x0107:
        r4 = 3;
        r4 = r9.charAt(r4);
        r5 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r4 != r5) goto L_0x0123;
    L_0x0110:
        r1 = r9.charAt(r7);
        r4 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r1 != r4) goto L_0x000f;
    L_0x0118:
        r1 = r9.charAt(r6);
        r4 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r1 != r4) goto L_0x000f;
    L_0x0120:
        r1 = r2;
        goto L_0x001c;
    L_0x0123:
        r2 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r4 != r2) goto L_0x000f;
    L_0x0127:
        r2 = r9.charAt(r7);
        r4 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x012f:
        r2 = r9.charAt(r6);
        r4 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x0137:
        goto L_0x001c;
    L_0x0139:
        r2 = "goto";
        goto L_0x0011;
    L_0x013d:
        r2 = "long";
        goto L_0x0011;
    L_0x0141:
        r1 = "null";
        r2 = 42;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x014a:
        r1 = 3;
        r1 = r9.charAt(r1);
        r4 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r1 != r4) goto L_0x0165;
    L_0x0153:
        r1 = r9.charAt(r7);
        r4 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r1 != r4) goto L_0x000f;
    L_0x015b:
        r1 = r9.charAt(r6);
        if (r1 != r2) goto L_0x000f;
    L_0x0161:
        r1 = 45;
        goto L_0x001c;
    L_0x0165:
        r2 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0169:
        r1 = r9.charAt(r7);
        r2 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0171:
        r1 = r9.charAt(r6);
        r2 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x0179:
        r1 = 43;
        goto L_0x001c;
    L_0x017d:
        r1 = "void";
        r2 = 127; // 0x7f float:1.78E-43 double:6.27E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0186:
        r1 = "with";
        r2 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x018f:
        r2 = r9.charAt(r7);
        switch(r2) {
            case 97: goto L_0x019a;
            case 98: goto L_0x0196;
            case 99: goto L_0x0196;
            case 100: goto L_0x0196;
            case 101: goto L_0x019e;
            case 102: goto L_0x0196;
            case 103: goto L_0x0196;
            case 104: goto L_0x0196;
            case 105: goto L_0x01bc;
            case 106: goto L_0x0196;
            case 107: goto L_0x0196;
            case 108: goto L_0x01c5;
            case 109: goto L_0x0196;
            case 110: goto L_0x01ce;
            case 111: goto L_0x01e7;
            case 112: goto L_0x01fb;
            case 113: goto L_0x0196;
            case 114: goto L_0x01ff;
            case 115: goto L_0x0196;
            case 116: goto L_0x0208;
            default: goto L_0x0196;
        };
    L_0x0196:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x019a:
        r2 = "class";
        goto L_0x0011;
    L_0x019e:
        r1 = r9.charAt(r0);
        r2 = 98;
        if (r1 != r2) goto L_0x01af;
    L_0x01a6:
        r1 = "break";
        r2 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01af:
        r2 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        if (r1 != r2) goto L_0x000f;
    L_0x01b3:
        r1 = "yield";
        r2 = 73;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01bc:
        r1 = "while";
        r2 = 118; // 0x76 float:1.65E-43 double:5.83E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01c5:
        r1 = "false";
        r2 = 44;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01ce:
        r2 = r9.charAt(r0);
        r4 = 99;
        if (r2 != r4) goto L_0x01df;
    L_0x01d6:
        r1 = "const";
        r2 = 155; // 0x9b float:2.17E-43 double:7.66E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x01df:
        r4 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x01e3:
        r2 = "final";
        goto L_0x0011;
    L_0x01e7:
        r2 = r9.charAt(r0);
        r4 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        if (r2 != r4) goto L_0x01f3;
    L_0x01ef:
        r2 = "float";
        goto L_0x0011;
    L_0x01f3:
        r4 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x01f7:
        r2 = "short";
        goto L_0x0011;
    L_0x01fb:
        r2 = "super";
        goto L_0x0011;
    L_0x01ff:
        r1 = "throw";
        r2 = 50;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0208:
        r1 = "catch";
        r2 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0211:
        r4 = r9.charAt(r6);
        switch(r4) {
            case 97: goto L_0x021c;
            case 101: goto L_0x0220;
            case 104: goto L_0x023b;
            case 109: goto L_0x023f;
            case 111: goto L_0x0243;
            case 116: goto L_0x0247;
            case 117: goto L_0x024b;
            case 119: goto L_0x024f;
            case 120: goto L_0x0258;
            case 121: goto L_0x025c;
            default: goto L_0x0218;
        };
    L_0x0218:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x021c:
        r2 = "native";
        goto L_0x0011;
    L_0x0220:
        r1 = r9.charAt(r0);
        r4 = 100;
        if (r1 != r4) goto L_0x0231;
    L_0x0228:
        r1 = "delete";
        r2 = 31;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0231:
        if (r1 != r2) goto L_0x000f;
    L_0x0233:
        r1 = "return";
        r2 = 4;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x023b:
        r2 = "throws";
        goto L_0x0011;
    L_0x023f:
        r2 = "import";
        goto L_0x0011;
    L_0x0243:
        r2 = "double";
        goto L_0x0011;
    L_0x0247:
        r2 = "static";
        goto L_0x0011;
    L_0x024b:
        r2 = "public";
        goto L_0x0011;
    L_0x024f:
        r1 = "switch";
        r2 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0258:
        r2 = "export";
        goto L_0x0011;
    L_0x025c:
        r1 = "typeof";
        r2 = 32;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0265:
        r2 = r9.charAt(r6);
        switch(r2) {
            case 97: goto L_0x0270;
            case 101: goto L_0x0274;
            case 105: goto L_0x027d;
            case 111: goto L_0x0286;
            case 114: goto L_0x028a;
            case 120: goto L_0x028e;
            default: goto L_0x026c;
        };
    L_0x026c:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x0270:
        r2 = "package";
        goto L_0x0011;
    L_0x0274:
        r1 = "default";
        r2 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x027d:
        r1 = "finally";
        r2 = 126; // 0x7e float:1.77E-43 double:6.23E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x0286:
        r2 = "boolean";
        goto L_0x0011;
    L_0x028a:
        r2 = "private";
        goto L_0x0011;
    L_0x028e:
        r2 = "extends";
        goto L_0x0011;
    L_0x0292:
        r2 = r9.charAt(r0);
        switch(r2) {
            case 97: goto L_0x029d;
            case 99: goto L_0x02a1;
            case 100: goto L_0x02aa;
            case 102: goto L_0x02b3;
            case 118: goto L_0x02bc;
            default: goto L_0x0299;
        };
    L_0x0299:
        r2 = r3;
        r1 = r0;
        goto L_0x0011;
    L_0x029d:
        r2 = "abstract";
        goto L_0x0011;
    L_0x02a1:
        r1 = "continue";
        r2 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x02aa:
        r1 = "debugger";
        r2 = 161; // 0xa1 float:2.26E-43 double:7.95E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x02b3:
        r1 = "function";
        r2 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x02bc:
        r2 = "volatile";
        goto L_0x0011;
    L_0x02c0:
        r2 = r9.charAt(r0);
        r4 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r2 != r4) goto L_0x02cc;
    L_0x02c8:
        r2 = "interface";
        goto L_0x0011;
    L_0x02cc:
        r4 = 112; // 0x70 float:1.57E-43 double:5.53E-322;
        if (r2 != r4) goto L_0x02d4;
    L_0x02d0:
        r2 = "protected";
        goto L_0x0011;
    L_0x02d4:
        r4 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x02d8:
        r2 = "transient";
        goto L_0x0011;
    L_0x02dc:
        r2 = r9.charAt(r6);
        r4 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r2 != r4) goto L_0x02e8;
    L_0x02e4:
        r2 = "implements";
        goto L_0x0011;
    L_0x02e8:
        r1 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r2 != r1) goto L_0x000f;
    L_0x02ec:
        r1 = "instanceof";
        r2 = 53;
        r8 = r1;
        r1 = r2;
        r2 = r8;
        goto L_0x0011;
    L_0x02f5:
        r2 = "synchronized";
        goto L_0x0011;
    L_0x02f9:
        r0 = r1 & 255;
        goto L_0x001e;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.TokenStream.stringToKeywordForJS(java.lang.String):int");
    }

    private final String substring(int i, int i2) {
        if (this.sourceString != null) {
            return this.sourceString.substring(i, i2);
        }
        return new String(this.sourceBuffer, i, i2 - i);
    }

    private void ungetChar(int i) {
        if (this.ungetCursor != 0 && this.ungetBuffer[this.ungetCursor + EOF_CHAR] == 10) {
            Kit.codeBug();
        }
        int[] iArr = this.ungetBuffer;
        int i2 = this.ungetCursor;
        this.ungetCursor = i2 + 1;
        iArr[i2] = i;
        this.cursor += EOF_CHAR;
    }

    private void ungetCharIgnoreLineEnd(int i) {
        int[] iArr = this.ungetBuffer;
        int i2 = this.ungetCursor;
        this.ungetCursor = i2 + 1;
        iArr[i2] = i;
        this.cursor += EOF_CHAR;
    }

    final boolean eof() {
        return this.hitEOF;
    }

    final String getAndResetCurrentComment() {
        if (this.sourceString != null) {
            if (isMarkingComment()) {
                Kit.codeBug();
            }
            return this.sourceString.substring(this.tokenBeg, this.tokenEnd);
        }
        if (!isMarkingComment()) {
            Kit.codeBug();
        }
        StringBuilder stringBuilder = new StringBuilder(this.commentPrefix);
        stringBuilder.append(this.sourceBuffer, this.commentCursor, getTokenLength() - this.commentPrefix.length());
        this.commentCursor = EOF_CHAR;
        return stringBuilder.toString();
    }

    public CommentType getCommentType() {
        return this.commentType;
    }

    public int getCursor() {
        return this.cursor;
    }

    int getFirstXMLToken() throws IOException {
        this.xmlOpenTagsCount = 0;
        this.xmlIsAttribute = $assertionsDisabled;
        this.xmlIsTagContent = $assertionsDisabled;
        if (!canUngetChar()) {
            return EOF_CHAR;
        }
        ungetChar(60);
        return getNextXMLToken();
    }

    final String getLine() {
        int i = this.sourceCursor;
        if (this.lineEndChar >= 0) {
            i += EOF_CHAR;
            if (this.lineEndChar == 10 && charAt(i + EOF_CHAR) == 13) {
                i += EOF_CHAR;
            }
        } else {
            i -= this.lineStart;
            while (true) {
                int charAt = charAt(this.lineStart + i);
                i = (charAt == EOF_CHAR || ScriptRuntime.isJSLineTerminator(charAt)) ? i + this.lineStart : i + 1;
            }
        }
        return substring(this.lineStart, i);
    }

    final String getLine(int i, int[] iArr) {
        if (!$assertionsDisabled && (i < 0 || i > this.cursor)) {
            throw new AssertionError();
        } else if ($assertionsDisabled || iArr.length == 2) {
            int i2 = (this.cursor + this.ungetCursor) - i;
            int i3 = this.sourceCursor;
            if (i2 > i3) {
                return null;
            }
            int charAt;
            int i4 = 0;
            int i5 = 0;
            while (i2 > 0) {
                if ($assertionsDisabled || i3 > 0) {
                    int i6;
                    int i7;
                    charAt = charAt(i3 + EOF_CHAR);
                    if (ScriptRuntime.isJSLineTerminator(charAt)) {
                        if (charAt == 10 && charAt(i3 - 2) == 13) {
                            i2 += EOF_CHAR;
                            i3 += EOF_CHAR;
                        }
                        i5 = i3 + EOF_CHAR;
                        i6 = i4 + 1;
                        i4 = i3;
                        i3 = i6;
                        i7 = i5;
                        i5 = i2;
                        i2 = i7;
                    } else {
                        i6 = i4;
                        i4 = i3;
                        i3 = i6;
                        i7 = i5;
                        i5 = i2;
                        i2 = i7;
                    }
                    i6 = i3;
                    i3 = i4 + EOF_CHAR;
                    i4 = i6;
                    i7 = i2;
                    i2 = i5 + EOF_CHAR;
                    i5 = i7;
                } else {
                    throw new AssertionError();
                }
            }
            charAt = 0;
            while (i3 > 0) {
                if (ScriptRuntime.isJSLineTerminator(charAt(i3 + EOF_CHAR))) {
                    break;
                }
                charAt++;
                i3 += EOF_CHAR;
            }
            i3 = 0;
            iArr[0] = (this.lineEndChar >= 0 ? 1 : 0) + (this.lineno - i4);
            iArr[1] = charAt;
            return i4 == 0 ? getLine() : substring(i3, i5);
        } else {
            throw new AssertionError();
        }
    }

    final int getLineno() {
        return this.lineno;
    }

    int getNextXMLToken() throws IOException {
        this.tokenBeg = this.cursor;
        this.stringBufferTop = 0;
        int i = getChar();
        while (i != EOF_CHAR) {
            if (!this.xmlIsTagContent) {
                switch (i) {
                    case Token.ENUM_INIT_ARRAY /*60*/:
                        addToString(i);
                        switch (peekChar()) {
                            case Token.GETPROP /*33*/:
                                addToString(getChar());
                                switch (peekChar()) {
                                    case Token.TRUE /*45*/:
                                        addToString(getChar());
                                        i = getChar();
                                        if (i == 45) {
                                            addToString(i);
                                            if (readXmlComment()) {
                                                break;
                                            }
                                            return EOF_CHAR;
                                        }
                                        this.stringBufferTop = 0;
                                        this.string = null;
                                        this.parser.addError("msg.XML.bad.form");
                                        return EOF_CHAR;
                                    case Token.FIRST_ASSIGN /*91*/:
                                        addToString(getChar());
                                        if (getChar() == 67 && getChar() == 68 && getChar() == 65 && getChar() == 84 && getChar() == 65 && getChar() == 91) {
                                            addToString(67);
                                            addToString(68);
                                            addToString(65);
                                            addToString(84);
                                            addToString(65);
                                            addToString(91);
                                            if (readCDATA()) {
                                                break;
                                            }
                                            return EOF_CHAR;
                                        }
                                        this.stringBufferTop = 0;
                                        this.string = null;
                                        this.parser.addError("msg.XML.bad.form");
                                        return EOF_CHAR;
                                        break;
                                    default:
                                        if (readEntity()) {
                                            break;
                                        }
                                        return EOF_CHAR;
                                }
                            case Token.SHNE /*47*/:
                                addToString(getChar());
                                if (this.xmlOpenTagsCount != 0) {
                                    this.xmlIsTagContent = true;
                                    this.xmlOpenTagsCount += EOF_CHAR;
                                    break;
                                }
                                this.stringBufferTop = 0;
                                this.string = null;
                                this.parser.addError("msg.XML.bad.form");
                                return EOF_CHAR;
                            case Token.ENUM_ID /*63*/:
                                addToString(getChar());
                                if (readPI()) {
                                    break;
                                }
                                return EOF_CHAR;
                            default:
                                this.xmlIsTagContent = true;
                                this.xmlOpenTagsCount++;
                                break;
                        }
                    case Token.VAR /*123*/:
                        ungetChar(i);
                        this.string = getStringFromBuffer();
                        return Token.XML;
                    default:
                        addToString(i);
                        break;
                }
            }
            switch (i) {
                case Token.BITOR /*9*/:
                case Token.BITXOR /*10*/:
                case Token.NE /*13*/:
                case Token.TYPEOF /*32*/:
                    addToString(i);
                    break;
                case Token.GETPROPNOWARN /*34*/:
                case Token.NAME /*39*/:
                    addToString(i);
                    if (!readQuotedString(i)) {
                        return EOF_CHAR;
                    }
                    break;
                case Token.SHNE /*47*/:
                    addToString(i);
                    if (peekChar() == 62) {
                        addToString(getChar());
                        this.xmlIsTagContent = $assertionsDisabled;
                        this.xmlOpenTagsCount += EOF_CHAR;
                        break;
                    }
                    break;
                case Token.ENUM_INIT_VALUES_IN_ORDER /*61*/:
                    addToString(i);
                    this.xmlIsAttribute = true;
                    break;
                case Token.ENUM_NEXT /*62*/:
                    addToString(i);
                    this.xmlIsTagContent = $assertionsDisabled;
                    this.xmlIsAttribute = $assertionsDisabled;
                    break;
                case Token.VAR /*123*/:
                    ungetChar(i);
                    this.string = getStringFromBuffer();
                    return Token.XML;
                default:
                    addToString(i);
                    this.xmlIsAttribute = $assertionsDisabled;
                    break;
            }
            if (!this.xmlIsTagContent && this.xmlOpenTagsCount == 0) {
                this.string = getStringFromBuffer();
                return Token.XMLEND;
            }
            i = getChar();
        }
        this.tokenEnd = this.cursor;
        this.stringBufferTop = 0;
        this.string = null;
        this.parser.addError("msg.XML.bad.form");
        return EOF_CHAR;
    }

    final double getNumber() {
        return this.number;
    }

    final int getOffset() {
        int i = this.sourceCursor - this.lineStart;
        return this.lineEndChar >= 0 ? i + EOF_CHAR : i;
    }

    final char getQuoteChar() {
        return (char) this.quoteChar;
    }

    final String getSourceString() {
        return this.sourceString;
    }

    final String getString() {
        return this.string;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    final int getToken() throws java.io.IOException {
        /*
        r9 = this;
        r4 = 10;
        r5 = -1;
        r1 = 1;
        r7 = 61;
        r2 = 0;
    L_0x0007:
        r3 = r9.getChar();
        if (r3 != r5) goto L_0x0019;
    L_0x000d:
        r0 = r9.cursor;
        r0 = r0 + -1;
        r9.tokenBeg = r0;
        r0 = r9.cursor;
        r9.tokenEnd = r0;
        r1 = r2;
    L_0x0018:
        return r1;
    L_0x0019:
        if (r3 != r4) goto L_0x0028;
    L_0x001b:
        r9.dirtyLine = r2;
        r0 = r9.cursor;
        r0 = r0 + -1;
        r9.tokenBeg = r0;
        r0 = r9.cursor;
        r9.tokenEnd = r0;
        goto L_0x0018;
    L_0x0028:
        r0 = isJSSpace(r3);
        if (r0 != 0) goto L_0x0007;
    L_0x002e:
        r0 = 45;
        if (r3 == r0) goto L_0x0034;
    L_0x0032:
        r9.dirtyLine = r1;
    L_0x0034:
        r0 = r9.cursor;
        r0 = r0 + -1;
        r9.tokenBeg = r0;
        r0 = r9.cursor;
        r9.tokenEnd = r0;
        r0 = 64;
        if (r3 != r0) goto L_0x0045;
    L_0x0042:
        r1 = 148; // 0x94 float:2.07E-43 double:7.3E-322;
        goto L_0x0018;
    L_0x0045:
        r0 = 92;
        if (r3 != r0) goto L_0x007c;
    L_0x0049:
        r0 = r9.getChar();
        r3 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r0 != r3) goto L_0x0074;
    L_0x0051:
        r9.stringBufferTop = r2;
        r3 = r1;
        r6 = r1;
    L_0x0055:
        if (r6 == 0) goto L_0x014f;
    L_0x0057:
        r4 = r3;
    L_0x0058:
        if (r3 == 0) goto L_0x0094;
    L_0x005a:
        r3 = r2;
        r0 = r2;
    L_0x005c:
        r6 = 4;
        if (r3 == r6) goto L_0x0069;
    L_0x005f:
        r6 = r9.getChar();
        r0 = org.mozilla.javascript.Kit.xDigitToInt(r6, r0);
        if (r0 >= 0) goto L_0x008c;
    L_0x0069:
        if (r0 >= 0) goto L_0x008f;
    L_0x006b:
        r0 = r9.parser;
        r1 = "msg.invalid.escape";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x0074:
        r9.ungetChar(r0);
        r0 = 92;
        r3 = r2;
        r6 = r2;
        goto L_0x0055;
    L_0x007c:
        r0 = (char) r3;
        r0 = java.lang.Character.isJavaIdentifierStart(r0);
        if (r0 == 0) goto L_0x0088;
    L_0x0083:
        r9.stringBufferTop = r2;
        r9.addToString(r3);
    L_0x0088:
        r6 = r0;
        r0 = r3;
        r3 = r2;
        goto L_0x0055;
    L_0x008c:
        r3 = r3 + 1;
        goto L_0x005c;
    L_0x008f:
        r9.addToString(r0);
        r3 = r2;
        goto L_0x0058;
    L_0x0094:
        r0 = r9.getChar();
        r6 = 92;
        if (r0 != r6) goto L_0x00b1;
    L_0x009c:
        r0 = r9.getChar();
        r3 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r0 != r3) goto L_0x00a7;
    L_0x00a4:
        r4 = r1;
        r3 = r1;
        goto L_0x0058;
    L_0x00a7:
        r0 = r9.parser;
        r1 = "msg.illegal.character";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x00b1:
        if (r0 == r5) goto L_0x00bf;
    L_0x00b3:
        r6 = 65279; // 0xfeff float:9.1475E-41 double:3.2252E-319;
        if (r0 == r6) goto L_0x00bf;
    L_0x00b8:
        r6 = (char) r0;
        r6 = java.lang.Character.isJavaIdentifierPart(r6);
        if (r6 != 0) goto L_0x012e;
    L_0x00bf:
        r9.ungetChar(r0);
        r2 = r9.getStringFromBuffer();
        if (r4 != 0) goto L_0x0136;
    L_0x00c8:
        r0 = r9.parser;
        r0 = r0.compilerEnv;
        r0 = r0.getLanguageVersion();
        r1 = r9.parser;
        r1 = r1.inUseStrictDirective();
        r0 = stringToKeyword(r2, r0, r1);
        if (r0 == 0) goto L_0x011f;
    L_0x00dc:
        r1 = 154; // 0x9a float:2.16E-43 double:7.6E-322;
        if (r0 == r1) goto L_0x00e4;
    L_0x00e0:
        r1 = 73;
        if (r0 != r1) goto L_0x0612;
    L_0x00e4:
        r1 = r9.parser;
        r1 = r1.compilerEnv;
        r1 = r1.getLanguageVersion();
        r3 = 170; // 0xaa float:2.38E-43 double:8.4E-322;
        if (r1 >= r3) goto L_0x0612;
    L_0x00f0:
        r1 = 154; // 0x9a float:2.16E-43 double:7.6E-322;
        if (r0 != r1) goto L_0x0133;
    L_0x00f4:
        r0 = "let";
    L_0x00f6:
        r9.string = r0;
        r0 = 39;
        r1 = r0;
    L_0x00fb:
        r0 = r9.allStrings;
        r0 = r0.intern(r2);
        r0 = (java.lang.String) r0;
        r9.string = r0;
        r0 = 128; // 0x80 float:1.8E-43 double:6.3E-322;
        if (r1 != r0) goto L_0x0018;
    L_0x0109:
        r0 = r9.parser;
        r0 = r0.compilerEnv;
        r0 = r0.getLanguageVersion();
        r3 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r0 >= r3) goto L_0x0018;
    L_0x0115:
        r0 = r9.parser;
        r0 = r0.compilerEnv;
        r0 = r0.isReservedKeywordAsIdentifier();
        if (r0 == 0) goto L_0x0018;
    L_0x011f:
        r0 = r2;
    L_0x0120:
        r1 = r9.allStrings;
        r0 = r1.intern(r0);
        r0 = (java.lang.String) r0;
        r9.string = r0;
        r1 = 39;
        goto L_0x0018;
    L_0x012e:
        r9.addToString(r0);
        goto L_0x0058;
    L_0x0133:
        r0 = "yield";
        goto L_0x00f6;
    L_0x0136:
        r0 = r9.parser;
        r0 = r0.compilerEnv;
        r0 = r0.getLanguageVersion();
        r1 = r9.parser;
        r1 = r1.inUseStrictDirective();
        r0 = isKeyword(r2, r0, r1);
        if (r0 == 0) goto L_0x060f;
    L_0x014a:
        r0 = r9.convertLastCharToHex(r2);
        goto L_0x0120;
    L_0x014f:
        r3 = isDigit(r0);
        if (r3 != 0) goto L_0x0163;
    L_0x0155:
        r3 = 46;
        if (r0 != r3) goto L_0x02c7;
    L_0x0159:
        r3 = r9.peekChar();
        r3 = isDigit(r3);
        if (r3 == 0) goto L_0x02c7;
    L_0x0163:
        r9.stringBufferTop = r2;
        r9.isBinary = r2;
        r9.isOctal = r2;
        r9.isOldOctal = r2;
        r9.isHex = r2;
        r3 = r9.parser;
        r3 = r3.compilerEnv;
        r3 = r3.getLanguageVersion();
        r6 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r3 < r6) goto L_0x01a8;
    L_0x0179:
        r3 = r1;
    L_0x017a:
        r6 = 48;
        if (r0 != r6) goto L_0x01df;
    L_0x017e:
        r0 = r9.getChar();
        r6 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        if (r0 == r6) goto L_0x018a;
    L_0x0186:
        r6 = 88;
        if (r0 != r6) goto L_0x01aa;
    L_0x018a:
        r6 = 16;
        r9.isHex = r1;
        r0 = r9.getChar();
    L_0x0192:
        r3 = 16;
        if (r6 != r3) goto L_0x0609;
    L_0x0196:
        r3 = r0;
        r0 = r1;
    L_0x0198:
        r7 = org.mozilla.javascript.Kit.xDigitToInt(r3, r2);
        if (r7 < 0) goto L_0x0604;
    L_0x019e:
        r9.addToString(r3);
        r0 = r9.getChar();
        r3 = r0;
        r0 = r2;
        goto L_0x0198;
    L_0x01a8:
        r3 = r2;
        goto L_0x017a;
    L_0x01aa:
        if (r3 == 0) goto L_0x01bd;
    L_0x01ac:
        r6 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r0 == r6) goto L_0x01b4;
    L_0x01b0:
        r6 = 79;
        if (r0 != r6) goto L_0x01bd;
    L_0x01b4:
        r6 = 8;
        r9.isOctal = r1;
        r0 = r9.getChar();
        goto L_0x0192;
    L_0x01bd:
        if (r3 == 0) goto L_0x01cf;
    L_0x01bf:
        r3 = 98;
        if (r0 == r3) goto L_0x01c7;
    L_0x01c3:
        r3 = 66;
        if (r0 != r3) goto L_0x01cf;
    L_0x01c7:
        r6 = 2;
        r9.isBinary = r1;
        r0 = r9.getChar();
        goto L_0x0192;
    L_0x01cf:
        r3 = isDigit(r0);
        if (r3 == 0) goto L_0x01da;
    L_0x01d5:
        r6 = 8;
        r9.isOldOctal = r1;
        goto L_0x0192;
    L_0x01da:
        r3 = 48;
        r9.addToString(r3);
    L_0x01df:
        r6 = r4;
        goto L_0x0192;
    L_0x01e1:
        r7 = 48;
        if (r7 > r6) goto L_0x022a;
    L_0x01e5:
        r7 = 57;
        if (r6 > r7) goto L_0x022a;
    L_0x01e9:
        r3 = 8;
        if (r0 != r3) goto L_0x0219;
    L_0x01ed:
        r3 = 56;
        if (r6 < r3) goto L_0x0219;
    L_0x01f1:
        r0 = r9.isOldOctal;
        if (r0 == 0) goto L_0x020f;
    L_0x01f5:
        r3 = r9.parser;
        r7 = "msg.bad.octal.literal";
        r0 = 56;
        if (r6 != r0) goto L_0x020c;
    L_0x01fd:
        r0 = "8";
    L_0x01ff:
        r3.addWarning(r7, r0);
        r0 = r4;
    L_0x0203:
        r9.addToString(r6);
        r6 = r9.getChar();
        r3 = r2;
        goto L_0x01e1;
    L_0x020c:
        r0 = "9";
        goto L_0x01ff;
    L_0x020f:
        r0 = r9.parser;
        r1 = "msg.caught.nfe";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x0219:
        r3 = 2;
        if (r0 != r3) goto L_0x0203;
    L_0x021c:
        r3 = 50;
        if (r6 < r3) goto L_0x0203;
    L_0x0220:
        r0 = r9.parser;
        r1 = "msg.caught.nfe";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x022a:
        r8 = r0;
        r0 = r6;
        r6 = r8;
    L_0x022d:
        if (r3 == 0) goto L_0x0245;
    L_0x022f:
        r3 = r9.isBinary;
        if (r3 != 0) goto L_0x023b;
    L_0x0233:
        r3 = r9.isOctal;
        if (r3 != 0) goto L_0x023b;
    L_0x0237:
        r3 = r9.isHex;
        if (r3 == 0) goto L_0x0245;
    L_0x023b:
        r0 = r9.parser;
        r1 = "msg.caught.nfe";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x0245:
        if (r6 != r4) goto L_0x02a0;
    L_0x0247:
        r3 = 46;
        if (r0 == r3) goto L_0x0253;
    L_0x024b:
        r3 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r0 == r3) goto L_0x0253;
    L_0x024f:
        r3 = 69;
        if (r0 != r3) goto L_0x02a0;
    L_0x0253:
        r1 = 46;
        if (r0 != r1) goto L_0x0264;
    L_0x0257:
        r9.addToString(r0);
        r0 = r9.getChar();
        r1 = isDigit(r0);
        if (r1 != 0) goto L_0x0257;
    L_0x0264:
        r1 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r0 == r1) goto L_0x026c;
    L_0x0268:
        r1 = 69;
        if (r0 != r1) goto L_0x029f;
    L_0x026c:
        r9.addToString(r0);
        r0 = r9.getChar();
        r1 = 43;
        if (r0 == r1) goto L_0x027b;
    L_0x0277:
        r1 = 45;
        if (r0 != r1) goto L_0x0282;
    L_0x027b:
        r9.addToString(r0);
        r0 = r9.getChar();
    L_0x0282:
        r1 = isDigit(r0);
        if (r1 != 0) goto L_0x0292;
    L_0x0288:
        r0 = r9.parser;
        r1 = "msg.missing.exponent";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x0292:
        r9.addToString(r0);
        r0 = r9.getChar();
        r1 = isDigit(r0);
        if (r1 != 0) goto L_0x0292;
    L_0x029f:
        r1 = r2;
    L_0x02a0:
        r9.ungetChar(r0);
        r0 = r9.getStringFromBuffer();
        r9.string = r0;
        if (r6 != r4) goto L_0x02c2;
    L_0x02ab:
        if (r1 != 0) goto L_0x02c2;
    L_0x02ad:
        r0 = java.lang.Double.parseDouble(r0);	 Catch:{ NumberFormatException -> 0x02b7 }
    L_0x02b1:
        r9.number = r0;
        r1 = 40;
        goto L_0x0018;
    L_0x02b7:
        r0 = move-exception;
        r0 = r9.parser;
        r1 = "msg.caught.nfe";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x02c2:
        r0 = org.mozilla.javascript.ScriptRuntime.stringToNumber(r0, r2, r6);
        goto L_0x02b1;
    L_0x02c7:
        r3 = 34;
        if (r0 == r3) goto L_0x02cf;
    L_0x02cb:
        r3 = 39;
        if (r0 != r3) goto L_0x03ac;
    L_0x02cf:
        r9.quoteChar = r0;
        r9.stringBufferTop = r2;
        r0 = r9.getChar(r2);
    L_0x02d7:
        r1 = r9.quoteChar;
        if (r0 == r1) goto L_0x039a;
    L_0x02db:
        if (r0 == r4) goto L_0x02df;
    L_0x02dd:
        if (r0 != r5) goto L_0x02f0;
    L_0x02df:
        r9.ungetChar(r0);
        r0 = r9.cursor;
        r9.tokenEnd = r0;
        r0 = r9.parser;
        r1 = "msg.unterminated.string.lit";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x02f0:
        r1 = 92;
        if (r0 != r1) goto L_0x0332;
    L_0x02f4:
        r0 = r9.getChar();
        switch(r0) {
            case 10: goto L_0x0394;
            case 98: goto L_0x033a;
            case 102: goto L_0x033d;
            case 110: goto L_0x0340;
            case 114: goto L_0x0342;
            case 116: goto L_0x0345;
            case 117: goto L_0x034b;
            case 118: goto L_0x0348;
            case 120: goto L_0x036c;
            default: goto L_0x02fb;
        };
    L_0x02fb:
        r1 = 48;
        if (r1 > r0) goto L_0x0332;
    L_0x02ff:
        r1 = 56;
        if (r0 >= r1) goto L_0x0332;
    L_0x0303:
        r0 = r0 + -48;
        r1 = r9.getChar();
        r3 = 48;
        if (r3 > r1) goto L_0x032f;
    L_0x030d:
        r3 = 56;
        if (r1 >= r3) goto L_0x032f;
    L_0x0311:
        r0 = r0 * 8;
        r0 = r0 + r1;
        r0 = r0 + -48;
        r1 = r9.getChar();
        r3 = 48;
        if (r3 > r1) goto L_0x032f;
    L_0x031e:
        r3 = 56;
        if (r1 >= r3) goto L_0x032f;
    L_0x0322:
        r3 = 31;
        if (r0 > r3) goto L_0x032f;
    L_0x0326:
        r0 = r0 * 8;
        r0 = r0 + r1;
        r0 = r0 + -48;
        r1 = r9.getChar();
    L_0x032f:
        r9.ungetChar(r1);
    L_0x0332:
        r9.addToString(r0);
        r0 = r9.getChar(r2);
        goto L_0x02d7;
    L_0x033a:
        r0 = 8;
        goto L_0x0332;
    L_0x033d:
        r0 = 12;
        goto L_0x0332;
    L_0x0340:
        r0 = r4;
        goto L_0x0332;
    L_0x0342:
        r0 = 13;
        goto L_0x0332;
    L_0x0345:
        r0 = 9;
        goto L_0x0332;
    L_0x0348:
        r0 = 11;
        goto L_0x0332;
    L_0x034b:
        r6 = r9.stringBufferTop;
        r0 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        r9.addToString(r0);
        r3 = r2;
        r1 = r2;
    L_0x0354:
        r0 = 4;
        if (r3 == r0) goto L_0x0368;
    L_0x0357:
        r0 = r9.getChar();
        r1 = org.mozilla.javascript.Kit.xDigitToInt(r0, r1);
        if (r1 < 0) goto L_0x02d7;
    L_0x0361:
        r9.addToString(r0);
        r0 = r3 + 1;
        r3 = r0;
        goto L_0x0354;
    L_0x0368:
        r9.stringBufferTop = r6;
        r0 = r1;
        goto L_0x0332;
    L_0x036c:
        r0 = r9.getChar();
        r3 = org.mozilla.javascript.Kit.xDigitToInt(r0, r2);
        if (r3 >= 0) goto L_0x037d;
    L_0x0376:
        r1 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r9.addToString(r1);
        goto L_0x02d7;
    L_0x037d:
        r1 = r9.getChar();
        r3 = org.mozilla.javascript.Kit.xDigitToInt(r1, r3);
        if (r3 >= 0) goto L_0x0392;
    L_0x0387:
        r3 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        r9.addToString(r3);
        r9.addToString(r0);
        r0 = r1;
        goto L_0x02d7;
    L_0x0392:
        r0 = r3;
        goto L_0x0332;
    L_0x0394:
        r0 = r9.getChar();
        goto L_0x02d7;
    L_0x039a:
        r0 = r9.getStringFromBuffer();
        r1 = r9.allStrings;
        r0 = r1.intern(r0);
        r0 = (java.lang.String) r0;
        r9.string = r0;
        r1 = 41;
        goto L_0x0018;
    L_0x03ac:
        switch(r0) {
            case 33: goto L_0x046e;
            case 37: goto L_0x05a1;
            case 38: goto L_0x0430;
            case 40: goto L_0x03cd;
            case 41: goto L_0x03d1;
            case 42: goto L_0x0517;
            case 43: goto L_0x05b3;
            case 44: goto L_0x03d5;
            case 45: goto L_0x05cd;
            case 46: goto L_0x03ed;
            case 47: goto L_0x0525;
            case 58: goto L_0x03dd;
            case 59: goto L_0x03b9;
            case 60: goto L_0x0486;
            case 61: goto L_0x044a;
            case 62: goto L_0x04dd;
            case 63: goto L_0x03d9;
            case 91: goto L_0x03bd;
            case 93: goto L_0x03c1;
            case 94: goto L_0x0423;
            case 123: goto L_0x03c5;
            case 124: goto L_0x0409;
            case 125: goto L_0x03c9;
            case 126: goto L_0x05af;
            default: goto L_0x03af;
        };
    L_0x03af:
        r0 = r9.parser;
        r1 = "msg.illegal.character";
        r0.addError(r1);
        r1 = r5;
        goto L_0x0018;
    L_0x03b9:
        r1 = 83;
        goto L_0x0018;
    L_0x03bd:
        r1 = 84;
        goto L_0x0018;
    L_0x03c1:
        r1 = 85;
        goto L_0x0018;
    L_0x03c5:
        r1 = 86;
        goto L_0x0018;
    L_0x03c9:
        r1 = 87;
        goto L_0x0018;
    L_0x03cd:
        r1 = 88;
        goto L_0x0018;
    L_0x03d1:
        r1 = 89;
        goto L_0x0018;
    L_0x03d5:
        r1 = 90;
        goto L_0x0018;
    L_0x03d9:
        r1 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        goto L_0x0018;
    L_0x03dd:
        r0 = 58;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x03e9;
    L_0x03e5:
        r1 = 145; // 0x91 float:2.03E-43 double:7.16E-322;
        goto L_0x0018;
    L_0x03e9:
        r1 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        goto L_0x0018;
    L_0x03ed:
        r0 = 46;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x03f9;
    L_0x03f5:
        r1 = 144; // 0x90 float:2.02E-43 double:7.1E-322;
        goto L_0x0018;
    L_0x03f9:
        r0 = 40;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x0405;
    L_0x0401:
        r1 = 147; // 0x93 float:2.06E-43 double:7.26E-322;
        goto L_0x0018;
    L_0x0405:
        r1 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        goto L_0x0018;
    L_0x0409:
        r0 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x0415;
    L_0x0411:
        r1 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        goto L_0x0018;
    L_0x0415:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x041f;
    L_0x041b:
        r1 = 92;
        goto L_0x0018;
    L_0x041f:
        r1 = 9;
        goto L_0x0018;
    L_0x0423:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x042d;
    L_0x0429:
        r1 = 93;
        goto L_0x0018;
    L_0x042d:
        r1 = r4;
        goto L_0x0018;
    L_0x0430:
        r0 = 38;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x043c;
    L_0x0438:
        r1 = 106; // 0x6a float:1.49E-43 double:5.24E-322;
        goto L_0x0018;
    L_0x043c:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x0446;
    L_0x0442:
        r1 = 94;
        goto L_0x0018;
    L_0x0446:
        r1 = 11;
        goto L_0x0018;
    L_0x044a:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x045e;
    L_0x0450:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x045a;
    L_0x0456:
        r1 = 46;
        goto L_0x0018;
    L_0x045a:
        r1 = 12;
        goto L_0x0018;
    L_0x045e:
        r0 = 62;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x046a;
    L_0x0466:
        r1 = 165; // 0xa5 float:2.31E-43 double:8.15E-322;
        goto L_0x0018;
    L_0x046a:
        r1 = 91;
        goto L_0x0018;
    L_0x046e:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x0482;
    L_0x0474:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x047e;
    L_0x047a:
        r1 = 47;
        goto L_0x0018;
    L_0x047e:
        r1 = 13;
        goto L_0x0018;
    L_0x0482:
        r1 = 26;
        goto L_0x0018;
    L_0x0486:
        r0 = 33;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x04b9;
    L_0x048e:
        r0 = 45;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x04b4;
    L_0x0496:
        r0 = 45;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x04af;
    L_0x049e:
        r0 = r9.cursor;
        r0 = r0 + -4;
        r9.tokenBeg = r0;
        r9.skipLine();
        r0 = org.mozilla.javascript.Token.CommentType.HTML;
        r9.commentType = r0;
        r1 = 162; // 0xa2 float:2.27E-43 double:8.0E-322;
        goto L_0x0018;
    L_0x04af:
        r0 = 45;
        r9.ungetCharIgnoreLineEnd(r0);
    L_0x04b4:
        r0 = 33;
        r9.ungetCharIgnoreLineEnd(r0);
    L_0x04b9:
        r0 = 60;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x04cf;
    L_0x04c1:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x04cb;
    L_0x04c7:
        r1 = 95;
        goto L_0x0018;
    L_0x04cb:
        r1 = 18;
        goto L_0x0018;
    L_0x04cf:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x04d9;
    L_0x04d5:
        r1 = 15;
        goto L_0x0018;
    L_0x04d9:
        r1 = 14;
        goto L_0x0018;
    L_0x04dd:
        r0 = 62;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x0509;
    L_0x04e5:
        r0 = 62;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x04fb;
    L_0x04ed:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x04f7;
    L_0x04f3:
        r1 = 97;
        goto L_0x0018;
    L_0x04f7:
        r1 = 20;
        goto L_0x0018;
    L_0x04fb:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x0505;
    L_0x0501:
        r1 = 96;
        goto L_0x0018;
    L_0x0505:
        r1 = 19;
        goto L_0x0018;
    L_0x0509:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x0513;
    L_0x050f:
        r1 = 17;
        goto L_0x0018;
    L_0x0513:
        r1 = 16;
        goto L_0x0018;
    L_0x0517:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x0521;
    L_0x051d:
        r1 = 100;
        goto L_0x0018;
    L_0x0521:
        r1 = 23;
        goto L_0x0018;
    L_0x0525:
        r9.markCommentStart();
        r0 = 47;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x0541;
    L_0x0530:
        r0 = r9.cursor;
        r0 = r0 + -2;
        r9.tokenBeg = r0;
        r9.skipLine();
        r0 = org.mozilla.javascript.Token.CommentType.LINE;
        r9.commentType = r0;
        r1 = 162; // 0xa2 float:2.27E-43 double:8.0E-322;
        goto L_0x0018;
    L_0x0541:
        r0 = 42;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x0593;
    L_0x0549:
        r0 = r9.cursor;
        r0 = r0 + -2;
        r9.tokenBeg = r0;
        r0 = 42;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x0573;
    L_0x0557:
        r0 = org.mozilla.javascript.Token.CommentType.JSDOC;
        r9.commentType = r0;
        r0 = r1;
    L_0x055c:
        r3 = r9.getChar();
        if (r3 != r5) goto L_0x0579;
    L_0x0562:
        r0 = r9.cursor;
        r0 = r0 + -1;
        r9.tokenEnd = r0;
        r0 = r9.parser;
        r1 = "msg.unterminated.comment";
        r0.addError(r1);
        r1 = 162; // 0xa2 float:2.27E-43 double:8.0E-322;
        goto L_0x0018;
    L_0x0573:
        r0 = org.mozilla.javascript.Token.CommentType.BLOCK_COMMENT;
        r9.commentType = r0;
        r0 = r2;
        goto L_0x055c;
    L_0x0579:
        r4 = 42;
        if (r3 != r4) goto L_0x057f;
    L_0x057d:
        r0 = r1;
        goto L_0x055c;
    L_0x057f:
        r4 = 47;
        if (r3 != r4) goto L_0x058d;
    L_0x0583:
        if (r0 == 0) goto L_0x055c;
    L_0x0585:
        r0 = r9.cursor;
        r9.tokenEnd = r0;
        r1 = 162; // 0xa2 float:2.27E-43 double:8.0E-322;
        goto L_0x0018;
    L_0x058d:
        r0 = r9.cursor;
        r9.tokenEnd = r0;
        r0 = r2;
        goto L_0x055c;
    L_0x0593:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x059d;
    L_0x0599:
        r1 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        goto L_0x0018;
    L_0x059d:
        r1 = 24;
        goto L_0x0018;
    L_0x05a1:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x05ab;
    L_0x05a7:
        r1 = 102; // 0x66 float:1.43E-43 double:5.04E-322;
        goto L_0x0018;
    L_0x05ab:
        r1 = 25;
        goto L_0x0018;
    L_0x05af:
        r1 = 27;
        goto L_0x0018;
    L_0x05b3:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x05bd;
    L_0x05b9:
        r1 = 98;
        goto L_0x0018;
    L_0x05bd:
        r0 = 43;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x05c9;
    L_0x05c5:
        r1 = 107; // 0x6b float:1.5E-43 double:5.3E-322;
        goto L_0x0018;
    L_0x05c9:
        r1 = 21;
        goto L_0x0018;
    L_0x05cd:
        r0 = r9.matchChar(r7);
        if (r0 == 0) goto L_0x05da;
    L_0x05d3:
        r0 = 99;
    L_0x05d5:
        r9.dirtyLine = r1;
        r1 = r0;
        goto L_0x0018;
    L_0x05da:
        r0 = 45;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x0601;
    L_0x05e2:
        r0 = r9.dirtyLine;
        if (r0 != 0) goto L_0x05fe;
    L_0x05e6:
        r0 = 62;
        r0 = r9.matchChar(r0);
        if (r0 == 0) goto L_0x05fe;
    L_0x05ee:
        r0 = "--";
        r9.markCommentStart(r0);
        r9.skipLine();
        r0 = org.mozilla.javascript.Token.CommentType.HTML;
        r9.commentType = r0;
        r1 = 162; // 0xa2 float:2.27E-43 double:8.0E-322;
        goto L_0x0018;
    L_0x05fe:
        r0 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        goto L_0x05d5;
    L_0x0601:
        r0 = 22;
        goto L_0x05d5;
    L_0x0604:
        r8 = r0;
        r0 = r3;
        r3 = r8;
        goto L_0x022d;
    L_0x0609:
        r3 = r1;
        r8 = r6;
        r6 = r0;
        r0 = r8;
        goto L_0x01e1;
    L_0x060f:
        r0 = r2;
        goto L_0x0120;
    L_0x0612:
        r1 = r0;
        goto L_0x00fb;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.TokenStream.getToken():int");
    }

    public int getTokenBeg() {
        return this.tokenBeg;
    }

    public int getTokenEnd() {
        return this.tokenEnd;
    }

    public int getTokenLength() {
        return this.tokenEnd - this.tokenBeg;
    }

    final boolean isNumberBinary() {
        return this.isBinary;
    }

    final boolean isNumberHex() {
        return this.isHex;
    }

    final boolean isNumberOctal() {
        return this.isOctal;
    }

    final boolean isNumberOldOctal() {
        return this.isOldOctal;
    }

    boolean isXMLAttribute() {
        return this.xmlIsAttribute;
    }

    String readAndClearRegExpFlags() {
        String str = this.regExpFlags;
        this.regExpFlags = null;
        return str;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void readRegExp(int r10) throws java.io.IOException {
        /*
        r9 = this;
        r8 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        r7 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        r6 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        r5 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        r1 = 0;
        r3 = r9.tokenBeg;
        r9.stringBufferTop = r1;
        r0 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
        if (r10 != r0) goto L_0x0044;
    L_0x0011:
        r0 = 61;
        r9.addToString(r0);
    L_0x0016:
        r0 = r1;
    L_0x0017:
        r2 = r9.getChar();
        r4 = 47;
        if (r2 != r4) goto L_0x0021;
    L_0x001f:
        if (r0 == 0) goto L_0x0067;
    L_0x0021:
        r4 = 10;
        if (r2 == r4) goto L_0x0028;
    L_0x0025:
        r4 = -1;
        if (r2 != r4) goto L_0x004c;
    L_0x0028:
        r9.ungetChar(r2);
        r0 = r9.cursor;
        r0 = r0 + -1;
        r9.tokenEnd = r0;
        r0 = new java.lang.String;
        r2 = r9.stringBuffer;
        r3 = r9.stringBufferTop;
        r0.<init>(r2, r1, r3);
        r9.string = r0;
        r0 = r9.parser;
        r1 = "msg.unterminated.re.lit";
        r0.reportError(r1);
    L_0x0043:
        return;
    L_0x0044:
        r0 = 24;
        if (r10 == r0) goto L_0x0016;
    L_0x0048:
        org.mozilla.javascript.Kit.codeBug();
        goto L_0x0016;
    L_0x004c:
        r4 = 92;
        if (r2 != r4) goto L_0x005b;
    L_0x0050:
        r9.addToString(r2);
        r2 = r9.getChar();
    L_0x0057:
        r9.addToString(r2);
        goto L_0x0017;
    L_0x005b:
        r4 = 91;
        if (r2 != r4) goto L_0x0061;
    L_0x005f:
        r0 = 1;
        goto L_0x0057;
    L_0x0061:
        r4 = 93;
        if (r2 != r4) goto L_0x0057;
    L_0x0065:
        r0 = r1;
        goto L_0x0057;
    L_0x0067:
        r0 = r9.stringBufferTop;
    L_0x0069:
        r2 = r9.matchChar(r5);
        if (r2 == 0) goto L_0x0073;
    L_0x006f:
        r9.addToString(r5);
        goto L_0x0069;
    L_0x0073:
        r2 = r9.matchChar(r6);
        if (r2 == 0) goto L_0x007d;
    L_0x0079:
        r9.addToString(r6);
        goto L_0x0069;
    L_0x007d:
        r2 = r9.matchChar(r7);
        if (r2 == 0) goto L_0x0087;
    L_0x0083:
        r9.addToString(r7);
        goto L_0x0069;
    L_0x0087:
        r2 = r9.matchChar(r8);
        if (r2 == 0) goto L_0x0091;
    L_0x008d:
        r9.addToString(r8);
        goto L_0x0069;
    L_0x0091:
        r2 = r9.stringBufferTop;
        r2 = r2 + r3;
        r2 = r2 + 2;
        r9.tokenEnd = r2;
        r2 = r9.peekChar();
        r2 = isAlpha(r2);
        if (r2 == 0) goto L_0x00a9;
    L_0x00a2:
        r2 = r9.parser;
        r3 = "msg.invalid.re.flag";
        r2.reportError(r3);
    L_0x00a9:
        r2 = new java.lang.String;
        r3 = r9.stringBuffer;
        r2.<init>(r3, r1, r0);
        r9.string = r2;
        r1 = new java.lang.String;
        r2 = r9.stringBuffer;
        r3 = r9.stringBufferTop;
        r3 = r3 - r0;
        r1.<init>(r2, r0, r3);
        r9.regExpFlags = r1;
        goto L_0x0043;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.TokenStream.readRegExp(int):void");
    }

    String tokenToString(int i) {
        return BuildConfig.FLAVOR;
    }
}
