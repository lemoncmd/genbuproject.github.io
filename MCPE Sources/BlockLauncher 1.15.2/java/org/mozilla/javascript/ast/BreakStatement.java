package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class BreakStatement extends Jump {
    private Name breakLabel;
    private AstNode target;

    public BreakStatement() {
        this.type = Token.BREAK;
    }

    public BreakStatement(int i) {
        this.type = Token.BREAK;
        this.position = i;
    }

    public BreakStatement(int i, int i2) {
        this.type = Token.BREAK;
        this.position = i;
        this.length = i2;
    }

    public Name getBreakLabel() {
        return this.breakLabel;
    }

    public AstNode getBreakTarget() {
        return this.target;
    }

    public void setBreakLabel(Name name) {
        this.breakLabel = name;
        if (name != null) {
            name.setParent(this);
        }
    }

    public void setBreakTarget(Jump jump) {
        assertNotNull(jump);
        this.target = jump;
        setJumpStatement(jump);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("break");
        if (this.breakLabel != null) {
            stringBuilder.append(" ");
            stringBuilder.append(this.breakLabel.toSource(0));
        }
        stringBuilder.append(";\n");
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this) && this.breakLabel != null) {
            this.breakLabel.visit(nodeVisitor);
        }
    }
}
