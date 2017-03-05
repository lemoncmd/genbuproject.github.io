package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

public class XmlMemberGet extends InfixExpression {
    public XmlMemberGet() {
        this.type = Token.DOTDOT;
    }

    public XmlMemberGet(int i) {
        super(i);
        this.type = Token.DOTDOT;
    }

    public XmlMemberGet(int i, int i2) {
        super(i, i2);
        this.type = Token.DOTDOT;
    }

    public XmlMemberGet(int i, int i2, AstNode astNode, XmlRef xmlRef) {
        super(i, i2, astNode, (AstNode) xmlRef);
        this.type = Token.DOTDOT;
    }

    public XmlMemberGet(AstNode astNode, XmlRef xmlRef) {
        super(astNode, (AstNode) xmlRef);
        this.type = Token.DOTDOT;
    }

    public XmlMemberGet(AstNode astNode, XmlRef xmlRef, int i) {
        super((int) Token.DOTDOT, astNode, (AstNode) xmlRef, i);
        this.type = Token.DOTDOT;
    }

    public XmlRef getMemberRef() {
        return (XmlRef) getRight();
    }

    public AstNode getTarget() {
        return getLeft();
    }

    public void setProperty(XmlRef xmlRef) {
        setRight(xmlRef);
    }

    public void setTarget(AstNode astNode) {
        setLeft(astNode);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append(getLeft().toSource(0));
        stringBuilder.append(AstNode.operatorToString(getType()));
        stringBuilder.append(getRight().toSource(0));
        return stringBuilder.toString();
    }
}
