package org.mozilla.javascript.ast;

import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

public class Jump extends AstNode {
    private Jump jumpNode;
    public Node target;
    private Node target2;

    public Jump() {
        this.type = -1;
    }

    public Jump(int i) {
        this.type = i;
    }

    public Jump(int i, int i2) {
        this(i);
        setLineno(i2);
    }

    public Jump(int i, Node node) {
        this(i);
        addChildToBack(node);
    }

    public Jump(int i, Node node, int i2) {
        this(i, node);
        setLineno(i2);
    }

    public Node getContinue() {
        if (this.type != Token.LOOP) {
            AstNode.codeBug();
        }
        return this.target2;
    }

    public Node getDefault() {
        if (this.type != Token.SWITCH) {
            AstNode.codeBug();
        }
        return this.target2;
    }

    public Node getFinally() {
        if (this.type != 82) {
            AstNode.codeBug();
        }
        return this.target2;
    }

    public Jump getJumpStatement() {
        if (!(this.type == Token.BREAK || this.type == Token.CONTINUE)) {
            AstNode.codeBug();
        }
        return this.jumpNode;
    }

    public Jump getLoop() {
        if (this.type != Token.LABEL) {
            AstNode.codeBug();
        }
        return this.jumpNode;
    }

    public void setContinue(Node node) {
        if (this.type != Token.LOOP) {
            AstNode.codeBug();
        }
        if (node.getType() != Token.TARGET) {
            AstNode.codeBug();
        }
        if (this.target2 != null) {
            AstNode.codeBug();
        }
        this.target2 = node;
    }

    public void setDefault(Node node) {
        if (this.type != Token.SWITCH) {
            AstNode.codeBug();
        }
        if (node.getType() != Token.TARGET) {
            AstNode.codeBug();
        }
        if (this.target2 != null) {
            AstNode.codeBug();
        }
        this.target2 = node;
    }

    public void setFinally(Node node) {
        if (this.type != 82) {
            AstNode.codeBug();
        }
        if (node.getType() != Token.TARGET) {
            AstNode.codeBug();
        }
        if (this.target2 != null) {
            AstNode.codeBug();
        }
        this.target2 = node;
    }

    public void setJumpStatement(Jump jump) {
        if (!(this.type == Token.BREAK || this.type == Token.CONTINUE)) {
            AstNode.codeBug();
        }
        if (jump == null) {
            AstNode.codeBug();
        }
        if (this.jumpNode != null) {
            AstNode.codeBug();
        }
        this.jumpNode = jump;
    }

    public void setLoop(Jump jump) {
        if (this.type != Token.LABEL) {
            AstNode.codeBug();
        }
        if (jump == null) {
            AstNode.codeBug();
        }
        if (this.jumpNode != null) {
            AstNode.codeBug();
        }
        this.jumpNode = jump;
    }

    public String toSource(int i) {
        throw new UnsupportedOperationException(toString());
    }

    public void visit(NodeVisitor nodeVisitor) {
        throw new UnsupportedOperationException(toString());
    }
}
