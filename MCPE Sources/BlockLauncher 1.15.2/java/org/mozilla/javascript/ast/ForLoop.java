package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class ForLoop extends Loop {
    private AstNode condition;
    private AstNode increment;
    private AstNode initializer;

    public ForLoop() {
        this.type = Token.FOR;
    }

    public ForLoop(int i) {
        super(i);
        this.type = Token.FOR;
    }

    public ForLoop(int i, int i2) {
        super(i, i2);
        this.type = Token.FOR;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public AstNode getIncrement() {
        return this.increment;
    }

    public AstNode getInitializer() {
        return this.initializer;
    }

    public void setCondition(AstNode astNode) {
        assertNotNull(astNode);
        this.condition = astNode;
        astNode.setParent(this);
    }

    public void setIncrement(AstNode astNode) {
        assertNotNull(astNode);
        this.increment = astNode;
        astNode.setParent(this);
    }

    public void setInitializer(AstNode astNode) {
        assertNotNull(astNode);
        this.initializer = astNode;
        astNode.setParent(this);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("for (");
        stringBuilder.append(this.initializer.toSource(0));
        stringBuilder.append("; ");
        stringBuilder.append(this.condition.toSource(0));
        stringBuilder.append("; ");
        stringBuilder.append(this.increment.toSource(0));
        stringBuilder.append(") ");
        if (this.body.getType() == Token.BLOCK) {
            stringBuilder.append(this.body.toSource(i).trim()).append("\n");
        } else {
            stringBuilder.append("\n").append(this.body.toSource(i + 1));
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.initializer.visit(nodeVisitor);
            this.condition.visit(nodeVisitor);
            this.increment.visit(nodeVisitor);
            this.body.visit(nodeVisitor);
        }
    }
}
