package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class EmptyExpression extends AstNode {
    public EmptyExpression() {
        this.type = Token.EMPTY;
    }

    public EmptyExpression(int i) {
        super(i);
        this.type = Token.EMPTY;
    }

    public EmptyExpression(int i, int i2) {
        super(i, i2);
        this.type = Token.EMPTY;
    }

    public String toSource(int i) {
        return makeIndent(i);
    }

    public void visit(NodeVisitor nodeVisitor) {
        nodeVisitor.visit(this);
    }
}
