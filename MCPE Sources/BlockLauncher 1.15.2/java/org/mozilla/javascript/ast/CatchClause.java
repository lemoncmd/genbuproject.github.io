package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class CatchClause extends AstNode {
    private Block body;
    private AstNode catchCondition;
    private int ifPosition;
    private int lp;
    private int rp;
    private Name varName;

    public CatchClause() {
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = Token.CATCH;
    }

    public CatchClause(int i) {
        super(i);
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = Token.CATCH;
    }

    public CatchClause(int i, int i2) {
        super(i, i2);
        this.ifPosition = -1;
        this.lp = -1;
        this.rp = -1;
        this.type = Token.CATCH;
    }

    public Block getBody() {
        return this.body;
    }

    public AstNode getCatchCondition() {
        return this.catchCondition;
    }

    public int getIfPosition() {
        return this.ifPosition;
    }

    public int getLp() {
        return this.lp;
    }

    public int getRp() {
        return this.rp;
    }

    public Name getVarName() {
        return this.varName;
    }

    public void setBody(Block block) {
        assertNotNull(block);
        this.body = block;
        block.setParent(this);
    }

    public void setCatchCondition(AstNode astNode) {
        this.catchCondition = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
    }

    public void setIfPosition(int i) {
        this.ifPosition = i;
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

    public void setVarName(Name name) {
        assertNotNull(name);
        this.varName = name;
        name.setParent(this);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("catch (");
        stringBuilder.append(this.varName.toSource(0));
        if (this.catchCondition != null) {
            stringBuilder.append(" if ");
            stringBuilder.append(this.catchCondition.toSource(0));
        }
        stringBuilder.append(") ");
        stringBuilder.append(this.body.toSource(0));
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.varName.visit(nodeVisitor);
            if (this.catchCondition != null) {
                this.catchCondition.visit(nodeVisitor);
            }
            this.body.visit(nodeVisitor);
        }
    }
}
