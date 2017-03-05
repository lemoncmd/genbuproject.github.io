package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class KeywordLiteral extends AstNode {
    public KeywordLiteral(int i) {
        super(i);
    }

    public KeywordLiteral(int i, int i2) {
        super(i, i2);
    }

    public KeywordLiteral(int i, int i2, int i3) {
        super(i, i2);
        setType(i3);
    }

    public boolean isBooleanLiteral() {
        return this.type == 45 || this.type == 44;
    }

    public KeywordLiteral setType(int i) {
        if (i == 43 || i == 42 || i == 45 || i == 44 || i == Token.DEBUGGER) {
            this.type = i;
            return this;
        }
        throw new IllegalArgumentException("Invalid node type: " + i);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        switch (getType()) {
            case Token.NULL /*42*/:
                stringBuilder.append("null");
                break;
            case Token.THIS /*43*/:
                stringBuilder.append("this");
                break;
            case Token.FALSE /*44*/:
                stringBuilder.append("false");
                break;
            case Token.TRUE /*45*/:
                stringBuilder.append("true");
                break;
            case Token.DEBUGGER /*161*/:
                stringBuilder.append("debugger;\n");
                break;
            default:
                throw new IllegalStateException("Invalid keyword literal type: " + getType());
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }
}
