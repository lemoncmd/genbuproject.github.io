package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class ForInLoop extends Loop {
    protected int eachPosition;
    protected int inPosition;
    protected boolean isForEach;
    protected boolean isForOf;
    protected AstNode iteratedObject;
    protected AstNode iterator;

    public ForInLoop() {
        this.inPosition = -1;
        this.eachPosition = -1;
        this.type = Token.FOR;
    }

    public ForInLoop(int i) {
        super(i);
        this.inPosition = -1;
        this.eachPosition = -1;
        this.type = Token.FOR;
    }

    public ForInLoop(int i, int i2) {
        super(i, i2);
        this.inPosition = -1;
        this.eachPosition = -1;
        this.type = Token.FOR;
    }

    public int getEachPosition() {
        return this.eachPosition;
    }

    public int getInPosition() {
        return this.inPosition;
    }

    public AstNode getIteratedObject() {
        return this.iteratedObject;
    }

    public AstNode getIterator() {
        return this.iterator;
    }

    public boolean isForEach() {
        return this.isForEach;
    }

    public boolean isForOf() {
        return this.isForOf;
    }

    public void setEachPosition(int i) {
        this.eachPosition = i;
    }

    public void setInPosition(int i) {
        this.inPosition = i;
    }

    public void setIsForEach(boolean z) {
        this.isForEach = z;
    }

    public void setIsForOf(boolean z) {
        this.isForOf = z;
    }

    public void setIteratedObject(AstNode astNode) {
        assertNotNull(astNode);
        this.iteratedObject = astNode;
        astNode.setParent(this);
    }

    public void setIterator(AstNode astNode) {
        assertNotNull(astNode);
        this.iterator = astNode;
        astNode.setParent(this);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("for ");
        if (isForEach()) {
            stringBuilder.append("each ");
        }
        stringBuilder.append("(");
        stringBuilder.append(this.iterator.toSource(0));
        if (this.isForOf) {
            stringBuilder.append(" of ");
        } else {
            stringBuilder.append(" in ");
        }
        stringBuilder.append(this.iteratedObject.toSource(0));
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
            this.iterator.visit(nodeVisitor);
            this.iteratedObject.visit(nodeVisitor);
            this.body.visit(nodeVisitor);
        }
    }
}
