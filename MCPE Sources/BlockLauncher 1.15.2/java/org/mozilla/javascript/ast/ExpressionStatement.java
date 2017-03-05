package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class ExpressionStatement extends AstNode {
    private AstNode expr;

    public ExpressionStatement() {
        this.type = Token.EXPR_VOID;
    }

    public ExpressionStatement(int i, int i2) {
        super(i, i2);
        this.type = Token.EXPR_VOID;
    }

    public ExpressionStatement(int i, int i2, AstNode astNode) {
        super(i, i2);
        this.type = Token.EXPR_VOID;
        setExpression(astNode);
    }

    public ExpressionStatement(AstNode astNode) {
        this(astNode.getPosition(), astNode.getLength(), astNode);
    }

    public ExpressionStatement(AstNode astNode, boolean z) {
        this(astNode);
        if (z) {
            setHasResult();
        }
    }

    public AstNode getExpression() {
        return this.expr;
    }

    public boolean hasSideEffects() {
        return this.type == Token.EXPR_RESULT || this.expr.hasSideEffects();
    }

    public void setExpression(AstNode astNode) {
        assertNotNull(astNode);
        this.expr = astNode;
        astNode.setParent(this);
        setLineno(astNode.getLineno());
    }

    public void setHasResult() {
        this.type = Token.EXPR_RESULT;
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.expr.toSource(i));
        stringBuilder.append(";\n");
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.expr.visit(nodeVisitor);
        }
    }
}
