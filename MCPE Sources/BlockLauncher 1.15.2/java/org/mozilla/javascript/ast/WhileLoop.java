package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class WhileLoop extends Loop {
    private AstNode condition;

    public WhileLoop() {
        this.type = Token.WHILE;
    }

    public WhileLoop(int i) {
        super(i);
        this.type = Token.WHILE;
    }

    public WhileLoop(int i, int i2) {
        super(i, i2);
        this.type = Token.WHILE;
    }

    public AstNode getCondition() {
        return this.condition;
    }

    public void setCondition(AstNode astNode) {
        assertNotNull(astNode);
        this.condition = astNode;
        astNode.setParent(this);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("while (");
        stringBuilder.append(this.condition.toSource(0));
        stringBuilder.append(") ");
        if (this.body.getType() == Token.BLOCK) {
            stringBuilder.append(this.body.toSource(i).trim());
            stringBuilder.append("\n");
        } else {
            stringBuilder.append("\n").append(this.body.toSource(i + 1));
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.condition.visit(nodeVisitor);
            this.body.visit(nodeVisitor);
        }
    }
}
