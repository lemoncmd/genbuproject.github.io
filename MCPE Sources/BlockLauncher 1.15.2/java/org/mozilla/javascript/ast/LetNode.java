package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class LetNode extends Scope {
    private AstNode body;
    private int lp;
    private int rp;
    private VariableDeclaration variables;

    public LetNode() {
        this.lp = -1;
        this.rp = -1;
        this.type = Token.LETEXPR;
    }

    public LetNode(int i) {
        super(i);
        this.lp = -1;
        this.rp = -1;
        this.type = Token.LETEXPR;
    }

    public LetNode(int i, int i2) {
        super(i, i2);
        this.lp = -1;
        this.rp = -1;
        this.type = Token.LETEXPR;
    }

    public AstNode getBody() {
        return this.body;
    }

    public int getLp() {
        return this.lp;
    }

    public int getRp() {
        return this.rp;
    }

    public VariableDeclaration getVariables() {
        return this.variables;
    }

    public void setBody(AstNode astNode) {
        this.body = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
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

    public void setVariables(VariableDeclaration variableDeclaration) {
        assertNotNull(variableDeclaration);
        this.variables = variableDeclaration;
        variableDeclaration.setParent(this);
    }

    public String toSource(int i) {
        String makeIndent = makeIndent(i);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent);
        stringBuilder.append("let (");
        printList(this.variables.getVariables(), stringBuilder);
        stringBuilder.append(") ");
        if (this.body != null) {
            stringBuilder.append(this.body.toSource(i));
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.variables.visit(nodeVisitor);
            if (this.body != null) {
                this.body.visit(nodeVisitor);
            }
        }
    }
}
