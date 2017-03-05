package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class InfixExpression extends AstNode {
    protected AstNode left;
    protected int operatorPosition = -1;
    protected AstNode right;

    public InfixExpression(int i) {
        super(i);
    }

    public InfixExpression(int i, int i2) {
        super(i, i2);
    }

    public InfixExpression(int i, int i2, AstNode astNode, AstNode astNode2) {
        super(i, i2);
        setLeft(astNode);
        setRight(astNode2);
    }

    public InfixExpression(int i, AstNode astNode, AstNode astNode2, int i2) {
        setType(i);
        setOperatorPosition(i2 - astNode.getPosition());
        setLeftAndRight(astNode, astNode2);
    }

    public InfixExpression(AstNode astNode, AstNode astNode2) {
        setLeftAndRight(astNode, astNode2);
    }

    public AstNode getLeft() {
        return this.left;
    }

    public int getOperator() {
        return getType();
    }

    public int getOperatorPosition() {
        return this.operatorPosition;
    }

    public AstNode getRight() {
        return this.right;
    }

    public boolean hasSideEffects() {
        boolean z = false;
        switch (getType()) {
            case Token.COMMA /*90*/:
                return this.right != null && this.right.hasSideEffects();
            case Token.OR /*105*/:
            case Token.AND /*106*/:
                if ((this.left != null && this.left.hasSideEffects()) || (this.right != null && this.right.hasSideEffects())) {
                    z = true;
                }
                return z;
            default:
                return super.hasSideEffects();
        }
    }

    public void setLeft(AstNode astNode) {
        assertNotNull(astNode);
        this.left = astNode;
        setLineno(astNode.getLineno());
        astNode.setParent(this);
    }

    public void setLeftAndRight(AstNode astNode, AstNode astNode2) {
        assertNotNull(astNode);
        assertNotNull(astNode2);
        setBounds(astNode.getPosition(), astNode2.getPosition() + astNode2.getLength());
        setLeft(astNode);
        setRight(astNode2);
    }

    public void setOperator(int i) {
        if (Token.isValidToken(i)) {
            setType(i);
            return;
        }
        throw new IllegalArgumentException("Invalid token: " + i);
    }

    public void setOperatorPosition(int i) {
        this.operatorPosition = i;
    }

    public void setRight(AstNode astNode) {
        assertNotNull(astNode);
        this.right = astNode;
        astNode.setParent(this);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append(this.left.toSource());
        stringBuilder.append(" ");
        stringBuilder.append(AstNode.operatorToString(getType()));
        stringBuilder.append(" ");
        stringBuilder.append(this.right.toSource());
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.left.visit(nodeVisitor);
            this.right.visit(nodeVisitor);
        }
    }
}
