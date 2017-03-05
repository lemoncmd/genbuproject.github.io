package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class VariableInitializer extends AstNode {
    private AstNode initializer;
    private AstNode target;

    public VariableInitializer() {
        this.type = Token.VAR;
    }

    public VariableInitializer(int i) {
        super(i);
        this.type = Token.VAR;
    }

    public VariableInitializer(int i, int i2) {
        super(i, i2);
        this.type = Token.VAR;
    }

    public AstNode getInitializer() {
        return this.initializer;
    }

    public AstNode getTarget() {
        return this.target;
    }

    public boolean isDestructuring() {
        return !(this.target instanceof Name);
    }

    public void setInitializer(AstNode astNode) {
        this.initializer = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
    }

    public void setNodeType(int i) {
        if (i == Token.VAR || i == Token.CONST || i == Token.LET) {
            setType(i);
            return;
        }
        throw new IllegalArgumentException("invalid node type");
    }

    public void setTarget(AstNode astNode) {
        if (astNode == null) {
            throw new IllegalArgumentException("invalid target arg");
        }
        this.target = astNode;
        astNode.setParent(this);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append(this.target.toSource(0));
        if (this.initializer != null) {
            stringBuilder.append(" = ");
            stringBuilder.append(this.initializer.toSource(0));
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.target.visit(nodeVisitor);
            if (this.initializer != null) {
                this.initializer.visit(nodeVisitor);
            }
        }
    }
}
