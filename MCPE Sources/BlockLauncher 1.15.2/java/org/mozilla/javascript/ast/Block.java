package org.mozilla.javascript.ast;

import java.util.Iterator;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

public class Block extends AstNode {
    public Block() {
        this.type = Token.BLOCK;
    }

    public Block(int i) {
        super(i);
        this.type = Token.BLOCK;
    }

    public Block(int i, int i2) {
        super(i, i2);
        this.type = Token.BLOCK;
    }

    public void addStatement(AstNode astNode) {
        addChild(astNode);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("{\n");
        Iterator it = iterator();
        while (it.hasNext()) {
            stringBuilder.append(((AstNode) ((Node) it.next())).toSource(i + 1));
        }
        stringBuilder.append(makeIndent(i));
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            Iterator it = iterator();
            while (it.hasNext()) {
                ((AstNode) ((Node) it.next())).visit(nodeVisitor);
            }
        }
    }
}
