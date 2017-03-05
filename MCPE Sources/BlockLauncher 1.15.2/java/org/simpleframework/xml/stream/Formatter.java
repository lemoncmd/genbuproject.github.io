package org.simpleframework.xml.stream;

import java.io.BufferedWriter;
import java.io.Writer;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import org.mozilla.javascript.Token;

class Formatter {
    private static final char[] AND = new char[]{'&', 'a', 'm', 'p', ';'};
    private static final char[] CLOSE = new char[]{' ', '-', '-', '>'};
    private static final char[] DOUBLE = new char[]{'&', 'q', 'u', 'o', 't', ';'};
    private static final char[] GREATER = new char[]{'&', 'g', 't', ';'};
    private static final char[] LESS = new char[]{'&', 'l', 't', ';'};
    private static final char[] NAMESPACE = new char[]{'x', 'm', 'l', 'n', 's'};
    private static final char[] OPEN = new char[]{'<', '!', '-', '-', ' '};
    private static final char[] SINGLE = new char[]{'&', 'a', 'p', 'o', 's', ';'};
    private OutputBuffer buffer = new OutputBuffer();
    private Indenter indenter;
    private Tag last;
    private String prolog;
    private Writer result;

    private enum Tag {
        COMMENT,
        START,
        TEXT,
        END
    }

    public Formatter(Writer writer, Format format) {
        this.result = new BufferedWriter(writer, EnchantType.pickaxe);
        this.indenter = new Indenter(format);
        this.prolog = format.getProlog();
    }

    private void append(char c) throws Exception {
        this.buffer.append(c);
    }

    private void append(String str) throws Exception {
        this.buffer.append(str);
    }

    private void append(char[] cArr) throws Exception {
        this.buffer.append(cArr);
    }

    private void data(String str) throws Exception {
        write("<![CDATA[");
        write(str);
        write("]]>");
    }

    private void escape(char c) throws Exception {
        char[] symbol = symbol(c);
        if (symbol != null) {
            write(symbol);
        } else {
            write(c);
        }
    }

    private void escape(String str) throws Exception {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            escape(str.charAt(i));
        }
    }

    private boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    private boolean isText(char c) {
        switch (c) {
            case Token.BITOR /*9*/:
            case Token.BITXOR /*10*/:
            case Token.NE /*13*/:
            case Token.TYPEOF /*32*/:
                break;
            default:
                if (c <= ' ' || c > '~' || c == '\u00f7') {
                    return false;
                }
        }
        return true;
    }

    private char[] symbol(char c) {
        switch (c) {
            case Token.GETPROPNOWARN /*34*/:
                return DOUBLE;
            case Token.CALL /*38*/:
                return AND;
            case Token.NAME /*39*/:
                return SINGLE;
            case Token.ENUM_INIT_ARRAY /*60*/:
                return LESS;
            case Token.ENUM_NEXT /*62*/:
                return GREATER;
            default:
                return null;
        }
    }

    private String unicode(char c) {
        return Integer.toString(c);
    }

    private void write(char c) throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        this.result.write(c);
    }

    private void write(String str) throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        this.result.write(str);
    }

    private void write(String str, String str2) throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        if (!isEmpty(str2)) {
            this.result.write(str2);
            this.result.write(58);
        }
        this.result.write(str);
    }

    private void write(char[] cArr) throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        this.result.write(cArr);
    }

    public void flush() throws Exception {
        this.buffer.write(this.result);
        this.buffer.clear();
        this.result.flush();
    }

    public void writeAttribute(String str, String str2, String str3) throws Exception {
        if (this.last != Tag.START) {
            throw new NodeException("Start element required");
        }
        write(' ');
        write(str, str3);
        write('=');
        write('\"');
        escape(str2);
        write('\"');
    }

    public void writeComment(String str) throws Exception {
        String top = this.indenter.top();
        if (this.last == Tag.START) {
            append('>');
        }
        if (top != null) {
            append(top);
            append(OPEN);
            append(str);
            append(CLOSE);
        }
        this.last = Tag.COMMENT;
    }

    public void writeEnd(String str, String str2) throws Exception {
        String pop = this.indenter.pop();
        if (this.last == Tag.START) {
            write('/');
            write('>');
        } else {
            if (this.last != Tag.TEXT) {
                write(pop);
            }
            if (this.last != Tag.START) {
                write('<');
                write('/');
                write(str, str2);
                write('>');
            }
        }
        this.last = Tag.END;
    }

    public void writeNamespace(String str, String str2) throws Exception {
        if (this.last != Tag.START) {
            throw new NodeException("Start element required");
        }
        write(' ');
        write(NAMESPACE);
        if (!isEmpty(str2)) {
            write(':');
            write(str2);
        }
        write('=');
        write('\"');
        escape(str);
        write('\"');
    }

    public void writeProlog() throws Exception {
        if (this.prolog != null) {
            write(this.prolog);
            write("\n");
        }
    }

    public void writeStart(String str, String str2) throws Exception {
        String push = this.indenter.push();
        if (this.last == Tag.START) {
            append('>');
        }
        flush();
        append(push);
        append('<');
        if (!isEmpty(str2)) {
            append(str2);
            append(':');
        }
        append(str);
        this.last = Tag.START;
    }

    public void writeText(String str) throws Exception {
        writeText(str, Mode.ESCAPE);
    }

    public void writeText(String str, Mode mode) throws Exception {
        if (this.last == Tag.START) {
            write('>');
        }
        if (mode == Mode.DATA) {
            data(str);
        } else {
            escape(str);
        }
        this.last = Tag.TEXT;
    }
}
