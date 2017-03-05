package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

public class Symbol {
    private Scope containingTable;
    private int declType;
    private int index = -1;
    private String name;
    private Node node;

    public Symbol(int i, String str) {
        setName(str);
        setDeclType(i);
    }

    public Scope getContainingTable() {
        return this.containingTable;
    }

    public int getDeclType() {
        return this.declType;
    }

    public String getDeclTypeName() {
        return Token.typeToName(this.declType);
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public Node getNode() {
        return this.node;
    }

    public void setContainingTable(Scope scope) {
        this.containingTable = scope;
    }

    public void setDeclType(int i) {
        if (i == Token.FUNCTION || i == 88 || i == Token.VAR || i == Token.LET || i == Token.CONST) {
            this.declType = i;
            return;
        }
        throw new IllegalArgumentException("Invalid declType: " + i);
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Symbol (");
        stringBuilder.append(getDeclTypeName());
        stringBuilder.append(") name=");
        stringBuilder.append(this.name);
        if (this.node != null) {
            stringBuilder.append(" line=");
            stringBuilder.append(this.node.getLineno());
        }
        return stringBuilder.toString();
    }
}
