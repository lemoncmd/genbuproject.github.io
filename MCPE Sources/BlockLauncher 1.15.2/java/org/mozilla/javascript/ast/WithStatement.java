package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class WithStatement extends AstNode {
    private AstNode expression;
    private int lp;
    private int rp;
    private AstNode statement;

    public WithStatement() {
        this.lp = -1;
        this.rp = -1;
        this.type = Token.WITH;
    }

    public WithStatement(int i) {
        super(i);
        this.lp = -1;
        this.rp = -1;
        this.type = Token.WITH;
    }

    public WithStatement(int i, int i2) {
        super(i, i2);
        this.lp = -1;
        this.rp = -1;
        this.type = Token.WITH;
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public int getLp() {
        return this.lp;
    }

    public int getRp() {
        return this.rp;
    }

    public AstNode getStatement() {
        return this.statement;
    }

    public void setExpression(AstNode astNode) {
        assertNotNull(astNode);
        this.expression = astNode;
        astNode.setParent(this);
    }

    public void setLp(int i) {
        this.lp = i;
    }

    public void setParens(int i, int i2) {
        this.lp = i;
        this.rp = i2;
    }

    public void setRp(int i) {
        this.rp = i;
    }

    public void setStatement(AstNode astNode) {
        assertNotNull(astNode);
        this.statement = astNode;
        astNode.setParent(this);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("with (");
        stringBuilder.append(this.expression.toSource(0));
        stringBuilder.append(") ");
        if (this.statement.getType() == Token.BLOCK) {
            stringBuilder.append(this.statement.toSource(i).trim());
            stringBuilder.append("\n");
        } else {
            stringBuilder.append("\n").append(this.statement.toSource(i + 1));
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.expression.visit(nodeVisitor);
            this.statement.visit(nodeVisitor);
        }
    }
}
