package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class ContinueStatement extends Jump {
    private Name label;
    private Loop target;

    public ContinueStatement() {
        this.type = Token.CONTINUE;
    }

    public ContinueStatement(int i) {
        this(i, -1);
    }

    public ContinueStatement(int i, int i2) {
        this.type = Token.CONTINUE;
        this.position = i;
        this.length = i2;
    }

    public ContinueStatement(int i, int i2, Name name) {
        this(i, i2);
        setLabel(name);
    }

    public ContinueStatement(int i, Name name) {
        this(i);
        setLabel(name);
    }

    public ContinueStatement(Name name) {
        this.type = Token.CONTINUE;
        setLabel(name);
    }

    public Name getLabel() {
        return this.label;
    }

    public Loop getTarget() {
        return this.target;
    }

    public void setLabel(Name name) {
        this.label = name;
        if (name != null) {
            name.setParent(this);
        }
    }

    public void setTarget(Loop loop) {
        assertNotNull(loop);
        this.target = loop;
        setJumpStatement(loop);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("continue");
        if (this.label != null) {
            stringBuilder.append(" ");
            stringBuilder.append(this.label.toSource(0));
        }
        stringBuilder.append(";\n");
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this) && this.label != null) {
            this.label.visit(nodeVisitor);
        }
    }
}
