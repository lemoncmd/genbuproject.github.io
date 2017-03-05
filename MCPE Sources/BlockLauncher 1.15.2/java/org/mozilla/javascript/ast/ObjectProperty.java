package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class ObjectProperty extends InfixExpression {
    public ObjectProperty() {
        this.type = Token.COLON;
    }

    public ObjectProperty(int i) {
        super(i);
        this.type = Token.COLON;
    }

    public ObjectProperty(int i, int i2) {
        super(i, i2);
        this.type = Token.COLON;
    }

    public boolean isGetterMethod() {
        return this.type == Token.GET;
    }

    public boolean isMethod() {
        return isGetterMethod() || isSetterMethod() || isNormalMethod();
    }

    public boolean isNormalMethod() {
        return this.type == Token.METHOD;
    }

    public boolean isSetterMethod() {
        return this.type == Token.SET;
    }

    public void setIsGetterMethod() {
        this.type = Token.GET;
    }

    public void setIsNormalMethod() {
        this.type = Token.METHOD;
    }

    public void setIsSetterMethod() {
        this.type = Token.SET;
    }

    public void setNodeType(int i) {
        if (i == Token.COLON || i == Token.GET || i == Token.SET || i == Token.METHOD) {
            setType(i);
            return;
        }
        throw new IllegalArgumentException("invalid node type: " + i);
    }

    public String toSource(int i) {
        int i2 = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(makeIndent(i + 1));
        if (isGetterMethod()) {
            stringBuilder.append("get ");
        } else if (isSetterMethod()) {
            stringBuilder.append("set ");
        }
        stringBuilder.append(this.left.toSource(getType() == Token.COLON ? 0 : i));
        if (this.type == Token.COLON) {
            stringBuilder.append(": ");
        }
        AstNode astNode = this.right;
        if (getType() != Token.COLON) {
            i2 = i + 1;
        }
        stringBuilder.append(astNode.toSource(i2));
        return stringBuilder.toString();
    }
}
