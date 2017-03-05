package org.mozilla.javascript.ast;

public abstract class Loop extends Scope {
    protected AstNode body;
    protected int lp = -1;
    protected int rp = -1;

    public Loop(int i) {
        super(i);
    }

    public Loop(int i, int i2) {
        super(i, i2);
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

    public void setBody(AstNode astNode) {
        this.body = astNode;
        setLength((astNode.getPosition() + astNode.getLength()) - getPosition());
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
}
