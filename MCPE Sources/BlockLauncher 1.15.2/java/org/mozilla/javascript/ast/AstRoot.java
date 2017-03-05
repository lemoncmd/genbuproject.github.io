package org.mozilla.javascript.ast;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.AstNode.PositionComparator;

public class AstRoot extends ScriptNode {
    private SortedSet<Comment> comments;

    public AstRoot() {
        this.type = Token.SCRIPT;
    }

    public AstRoot(int i) {
        super(i);
        this.type = Token.SCRIPT;
    }

    public void addComment(Comment comment) {
        assertNotNull(comment);
        if (this.comments == null) {
            this.comments = new TreeSet(new PositionComparator());
        }
        this.comments.add(comment);
        comment.setParent(this);
    }

    public void checkParentLinks() {
        visit(new NodeVisitor() {
            public boolean visit(AstNode astNode) {
                if (astNode.getType() == Token.SCRIPT || astNode.getParent() != null) {
                    return true;
                }
                throw new IllegalStateException("No parent for node: " + astNode + "\n" + astNode.toSource(0));
            }
        });
    }

    public String debugPrint() {
        Object debugPrintVisitor = new DebugPrintVisitor(new StringBuilder(1000));
        visitAll(debugPrintVisitor);
        return debugPrintVisitor.toString();
    }

    public SortedSet<Comment> getComments() {
        return this.comments;
    }

    public void setComments(SortedSet<Comment> sortedSet) {
        if (sortedSet == null) {
            this.comments = null;
            return;
        }
        if (this.comments != null) {
            this.comments.clear();
        }
        for (Comment addComment : sortedSet) {
            addComment(addComment);
        }
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        Iterator it = iterator();
        while (it.hasNext()) {
            stringBuilder.append(((AstNode) ((Node) it.next())).toSource(i));
        }
        return stringBuilder.toString();
    }

    public void visitAll(NodeVisitor nodeVisitor) {
        visit(nodeVisitor);
        visitComments(nodeVisitor);
    }

    public void visitComments(NodeVisitor nodeVisitor) {
        if (this.comments != null) {
            for (Comment visit : this.comments) {
                nodeVisitor.visit(visit);
            }
        }
    }
}
