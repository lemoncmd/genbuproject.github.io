package org.mozilla.javascript.json;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Token;

public class JsonParser {
    static final /* synthetic */ boolean $assertionsDisabled = (!JsonParser.class.desiredAssertionStatus());
    private Context cx;
    private int length;
    private int pos;
    private Scriptable scope;
    private String src;

    public static class ParseException extends Exception {
        static final long serialVersionUID = 4804542791749920772L;

        ParseException(Exception exception) {
            super(exception);
        }

        ParseException(String str) {
            super(str);
        }
    }

    public JsonParser(Context context, Scriptable scriptable) {
        this.cx = context;
        this.scope = scriptable;
    }

    private void consume(char c) throws ParseException {
        consumeWhitespace();
        if (this.pos >= this.length) {
            throw new ParseException("Expected " + c + " but reached end of stream");
        }
        String str = this.src;
        int i = this.pos;
        this.pos = i + 1;
        char charAt = str.charAt(i);
        if (charAt != c) {
            throw new ParseException("Expected " + c + " found " + charAt);
        }
    }

    private void consumeWhitespace() {
        while (this.pos < this.length) {
            switch (this.src.charAt(this.pos)) {
                case Token.BITOR /*9*/:
                case Token.BITXOR /*10*/:
                case Token.NE /*13*/:
                case Token.TYPEOF /*32*/:
                    this.pos++;
                default:
                    return;
            }
        }
    }

    private int fromHex(char c) {
        return (c < '0' || c > '9') ? (c < 'A' || c > 'F') ? (c < 'a' || c > 'f') ? -1 : (c - 97) + 10 : (c - 65) + 10 : c - 48;
    }

    private char nextOrNumberError(int i) throws ParseException {
        if (this.pos >= this.length) {
            throw numberError(i, this.length);
        }
        String str = this.src;
        int i2 = this.pos;
        this.pos = i2 + 1;
        return str.charAt(i2);
    }

    private ParseException numberError(int i, int i2) {
        return new ParseException("Unsupported number format: " + this.src.substring(i, i2));
    }

    private Object readArray() throws ParseException {
        consumeWhitespace();
        if (this.pos >= this.length || this.src.charAt(this.pos) != ']') {
            List arrayList = new ArrayList();
            int i = 0;
            while (this.pos < this.length) {
                switch (this.src.charAt(this.pos)) {
                    case Token.FALSE /*44*/:
                        if (i != 0) {
                            this.pos++;
                            i = 0;
                            break;
                        }
                        throw new ParseException("Unexpected comma in array literal");
                    case Token.ASSIGN_BITXOR /*93*/:
                        if (i == 0) {
                            throw new ParseException("Unexpected comma in array literal");
                        }
                        this.pos++;
                        return this.cx.newArray(this.scope, arrayList.toArray());
                    default:
                        if (i == 0) {
                            arrayList.add(readValue());
                            i = 1;
                            break;
                        }
                        throw new ParseException("Missing comma in array literal");
                }
                consumeWhitespace();
            }
            throw new ParseException("Unterminated array literal");
        }
        this.pos++;
        return this.cx.newArray(this.scope, 0);
    }

    private void readDigits() {
        while (this.pos < this.length) {
            char charAt = this.src.charAt(this.pos);
            if (charAt >= '0' && charAt <= '9') {
                this.pos++;
            } else {
                return;
            }
        }
    }

    private Boolean readFalse() throws ParseException {
        if (this.length - this.pos >= 4 && this.src.charAt(this.pos) == 'a' && this.src.charAt(this.pos + 1) == 'l' && this.src.charAt(this.pos + 2) == 's' && this.src.charAt(this.pos + 3) == 'e') {
            this.pos += 4;
            return Boolean.FALSE;
        }
        throw new ParseException("Unexpected token: f");
    }

    private Object readNull() throws ParseException {
        if (this.length - this.pos >= 3 && this.src.charAt(this.pos) == 'u' && this.src.charAt(this.pos + 1) == 'l' && this.src.charAt(this.pos + 2) == 'l') {
            this.pos += 3;
            return null;
        }
        throw new ParseException("Unexpected token: n");
    }

    private Number readNumber(char c) throws ParseException {
        if ($assertionsDisabled || c == '-' || (c >= '0' && c <= '9')) {
            char nextOrNumberError;
            int i = this.pos - 1;
            if (c == '-') {
                c = nextOrNumberError(i);
                if (c < '0' || c > '9') {
                    throw numberError(i, this.pos);
                }
            }
            if (c != '0') {
                readDigits();
            }
            if (this.pos < this.length && this.src.charAt(this.pos) == '.') {
                this.pos++;
                nextOrNumberError = nextOrNumberError(i);
                if (nextOrNumberError < '0' || nextOrNumberError > '9') {
                    throw numberError(i, this.pos);
                }
                readDigits();
            }
            if (this.pos < this.length) {
                nextOrNumberError = this.src.charAt(this.pos);
                if (nextOrNumberError == 'e' || nextOrNumberError == 'E') {
                    this.pos++;
                    nextOrNumberError = nextOrNumberError(i);
                    if (nextOrNumberError == '-' || nextOrNumberError == '+') {
                        nextOrNumberError = nextOrNumberError(i);
                    }
                    if (nextOrNumberError < '0' || nextOrNumberError > '9') {
                        throw numberError(i, this.pos);
                    }
                    readDigits();
                }
            }
            double parseDouble = Double.parseDouble(this.src.substring(i, this.pos));
            int i2 = (int) parseDouble;
            return ((double) i2) == parseDouble ? Integer.valueOf(i2) : Double.valueOf(parseDouble);
        } else {
            throw new AssertionError();
        }
    }

    private Object readObject() throws ParseException {
        consumeWhitespace();
        Scriptable newObject = this.cx.newObject(this.scope);
        if (this.pos >= this.length || this.src.charAt(this.pos) != '}') {
            Object obj = null;
            while (this.pos < this.length) {
                String str = this.src;
                int i = this.pos;
                this.pos = i + 1;
                switch (str.charAt(i)) {
                    case Token.GETPROPNOWARN /*34*/:
                        if (obj == null) {
                            String readString = readString();
                            consume(':');
                            Object readValue = readValue();
                            long indexFromString = ScriptRuntime.indexFromString(readString);
                            if (indexFromString < 0) {
                                newObject.put(readString, newObject, readValue);
                            } else {
                                newObject.put((int) indexFromString, newObject, readValue);
                            }
                            obj = 1;
                            break;
                        }
                        throw new ParseException("Missing comma in object literal");
                    case Token.FALSE /*44*/:
                        if (obj != null) {
                            obj = null;
                            break;
                        }
                        throw new ParseException("Unexpected comma in object literal");
                    case Token.CATCH /*125*/:
                        if (obj != null) {
                            return newObject;
                        }
                        throw new ParseException("Unexpected comma in object literal");
                    default:
                        throw new ParseException("Unexpected token in object literal");
                }
                consumeWhitespace();
            }
            throw new ParseException("Unterminated object literal");
        }
        this.pos++;
        return newObject;
    }

    private String readString() throws ParseException {
        int i = this.pos;
        while (this.pos < this.length) {
            String str = this.src;
            int i2 = this.pos;
            this.pos = i2 + 1;
            char charAt = str.charAt(i2);
            if (charAt <= '\u001f') {
                throw new ParseException("String contains control character");
            } else if (charAt == '\\') {
                break;
            } else if (charAt == '\"') {
                return this.src.substring(i, this.pos - 1);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (this.pos < this.length) {
            if ($assertionsDisabled || this.src.charAt(this.pos - 1) == '\\') {
                stringBuilder.append(this.src, i, this.pos - 1);
                if (this.pos >= this.length) {
                    throw new ParseException("Unterminated string");
                }
                String str2 = this.src;
                i2 = this.pos;
                this.pos = i2 + 1;
                char charAt2 = str2.charAt(i2);
                switch (charAt2) {
                    case Token.GETPROPNOWARN /*34*/:
                        stringBuilder.append('\"');
                        break;
                    case Token.SHNE /*47*/:
                        stringBuilder.append('/');
                        break;
                    case Token.ASSIGN_BITOR /*92*/:
                        stringBuilder.append('\\');
                        break;
                    case Token.ASSIGN_ADD /*98*/:
                        stringBuilder.append('\b');
                        break;
                    case Token.LAST_ASSIGN /*102*/:
                        stringBuilder.append('\f');
                        break;
                    case Token.FUNCTION /*110*/:
                        stringBuilder.append('\n');
                        break;
                    case Token.ELSE /*114*/:
                        stringBuilder.append('\r');
                        break;
                    case Token.CASE /*116*/:
                        stringBuilder.append('\t');
                        break;
                    case Token.DEFAULT /*117*/:
                        if (this.length - this.pos >= 5) {
                            i = (((fromHex(this.src.charAt(this.pos + 0)) << 12) | (fromHex(this.src.charAt(this.pos + 1)) << 8)) | (fromHex(this.src.charAt(this.pos + 2)) << 4)) | fromHex(this.src.charAt(this.pos + 3));
                            if (i >= 0) {
                                this.pos += 4;
                                stringBuilder.append((char) i);
                                break;
                            }
                            throw new ParseException("Invalid character code: " + this.src.substring(this.pos, this.pos + 4));
                        }
                        throw new ParseException("Invalid character code: \\u" + this.src.substring(this.pos));
                    default:
                        throw new ParseException("Unexpected character in string: '\\" + charAt2 + "'");
                }
                i = this.pos;
                while (this.pos < this.length) {
                    String str3 = this.src;
                    int i3 = this.pos;
                    this.pos = i3 + 1;
                    char charAt3 = str3.charAt(i3);
                    if (charAt3 <= '\u001f') {
                        throw new ParseException("String contains control character");
                    } else if (charAt3 == '\\') {
                        continue;
                    } else if (charAt3 == '\"') {
                        stringBuilder.append(this.src, i, this.pos - 1);
                        return stringBuilder.toString();
                    }
                }
            }
            throw new AssertionError();
        }
        throw new ParseException("Unterminated string literal");
    }

    private Boolean readTrue() throws ParseException {
        if (this.length - this.pos >= 3 && this.src.charAt(this.pos) == 'r' && this.src.charAt(this.pos + 1) == 'u' && this.src.charAt(this.pos + 2) == 'e') {
            this.pos += 3;
            return Boolean.TRUE;
        }
        throw new ParseException("Unexpected token: t");
    }

    private Object readValue() throws ParseException {
        consumeWhitespace();
        if (this.pos < this.length) {
            String str = this.src;
            int i = this.pos;
            this.pos = i + 1;
            char charAt = str.charAt(i);
            switch (charAt) {
                case Token.GETPROPNOWARN /*34*/:
                    return readString();
                case Token.TRUE /*45*/:
                case Token.REGEXP /*48*/:
                case Token.BINDNAME /*49*/:
                case Token.THROW /*50*/:
                case Token.RETHROW /*51*/:
                case Token.IN /*52*/:
                case Token.INSTANCEOF /*53*/:
                case Token.LOCAL_LOAD /*54*/:
                case Token.GETVAR /*55*/:
                case Token.SETVAR /*56*/:
                case Token.CATCH_SCOPE /*57*/:
                    return readNumber(charAt);
                case Token.FIRST_ASSIGN /*91*/:
                    return readArray();
                case Token.LAST_ASSIGN /*102*/:
                    return readFalse();
                case Token.FUNCTION /*110*/:
                    return readNull();
                case Token.CASE /*116*/:
                    return readTrue();
                case Token.VAR /*123*/:
                    return readObject();
                default:
                    throw new ParseException("Unexpected token: " + charAt);
            }
        }
        throw new ParseException("Empty JSON string");
    }

    public synchronized Object parseValue(String str) throws ParseException {
        Object readValue;
        if (str == null) {
            throw new ParseException("Input string may not be null");
        }
        this.pos = 0;
        this.length = str.length();
        this.src = str;
        readValue = readValue();
        consumeWhitespace();
        if (this.pos < this.length) {
            throw new ParseException("Expected end of stream at char " + this.pos);
        }
        return readValue;
    }
}
