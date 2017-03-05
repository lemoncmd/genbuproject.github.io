package org.mozilla.javascript.ast;

import org.mozilla.javascript.ScriptRuntime;

public class StringLiteral extends AstNode {
    private char quoteChar;
    private String value;

    public StringLiteral() {
        this.type = 41;
    }

    public StringLiteral(int i) {
        super(i);
        this.type = 41;
    }

    public StringLiteral(int i, int i2) {
        super(i, i2);
        this.type = 41;
    }

    public char getQuoteCharacter() {
        return this.quoteChar;
    }

    public String getValue() {
        return this.value;
    }

    public String getValue(boolean z) {
        return !z ? this.value : this.quoteChar + this.value + this.quoteChar;
    }

    public void setQuoteCharacter(char c) {
        this.quoteChar = c;
    }

    public void setValue(String str) {
        assertNotNull(str);
        this.value = str;
    }

    public String toSource(int i) {
        return new StringBuilder(makeIndent(i)).append(this.quoteChar).append(ScriptRuntime.escapeString(this.value, this.quoteChar)).append(this.quoteChar).toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }
}
