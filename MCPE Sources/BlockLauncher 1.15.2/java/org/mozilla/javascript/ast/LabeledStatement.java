package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Token;

public class LabeledStatement extends AstNode {
    private List<Label> labels;
    private AstNode statement;

    public LabeledStatement() {
        this.labels = new ArrayList();
        this.type = Token.EXPR_VOID;
    }

    public LabeledStatement(int i) {
        super(i);
        this.labels = new ArrayList();
        this.type = Token.EXPR_VOID;
    }

    public LabeledStatement(int i, int i2) {
        super(i, i2);
        this.labels = new ArrayList();
        this.type = Token.EXPR_VOID;
    }

    public void addLabel(Label label) {
        assertNotNull(label);
        this.labels.add(label);
        label.setParent(this);
    }

    public Label getFirstLabel() {
        return (Label) this.labels.get(0);
    }

    public Label getLabelByName(String str) {
        for (Label label : this.labels) {
            if (str.equals(label.getName())) {
                return label;
            }
        }
        return null;
    }

    public List<Label> getLabels() {
        return this.labels;
    }

    public AstNode getStatement() {
        return this.statement;
    }

    public boolean hasSideEffects() {
        return true;
    }

    public void setLabels(List<Label> list) {
        assertNotNull(list);
        if (this.labels != null) {
            this.labels.clear();
        }
        for (Label addLabel : list) {
            addLabel(addLabel);
        }
    }

    public void setStatement(AstNode astNode) {
        assertNotNull(astNode);
        this.statement = astNode;
        astNode.setParent(this);
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Label toSource : this.labels) {
            stringBuilder.append(toSource.toSource(i));
        }
        stringBuilder.append(this.statement.toSource(i + 1));
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            for (AstNode visit : this.labels) {
                visit.visit(nodeVisitor);
            }
            this.statement.visit(nodeVisitor);
        }
    }
}
