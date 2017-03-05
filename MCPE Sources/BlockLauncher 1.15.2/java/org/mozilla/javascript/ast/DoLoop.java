package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class DoLoop extends Loop {
    private AstNode condition;
    private int whilePosition;

    public DoLoop() {
        this.whilePosition = -1;
        this.type = Token.DO;
    }

    public DoLoop(int i) {
        super(i);
        this.whilePosition = -1;
        this.type = Token.DO;
    }

    public DoLoop(int i, int i2) {
        super(i, i2);
        this.whilePosition = -1;
        this.type = Token.DO;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public int getWhilePosition() {
        return this.whilePosition;
    }

    public void setCondition(AstNode astNode) {
        assertNotNull(astNode);
        this.condition = astNode;
        astNode.setParent(this);
    }

    public void setWhilePosition(int i) {
        this.whilePosition = i;
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("do ");
        stringBuilder.append(this.body.toSource(i).trim());
        stringBuilder.append(" while (");
        stringBuilder.append(this.condition.toSource(0));
        stringBuilder.append(");\n");
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.body.visit(nodeVisitor);
            this.condition.visit(nodeVisitor);
        }
    }
}
