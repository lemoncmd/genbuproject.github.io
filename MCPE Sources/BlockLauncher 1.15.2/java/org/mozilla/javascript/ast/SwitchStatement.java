package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mozilla.javascript.Token;

public class SwitchStatement extends Jump {
    private static final List<SwitchCase> NO_CASES = Collections.unmodifiableList(new ArrayList());
    private List<SwitchCase> cases;
    private AstNode expression;
    private int lp;
    private int rp;

    public SwitchStatement() {
        this.lp = -1;
        this.rp = -1;
        this.type = Token.SWITCH;
    }

    public SwitchStatement(int i) {
        this.lp = -1;
        this.rp = -1;
        this.type = Token.SWITCH;
        this.position = i;
    }

    public SwitchStatement(int i, int i2) {
        this.lp = -1;
        this.rp = -1;
        this.type = Token.SWITCH;
        this.position = i;
        this.length = i2;
    }

    public void addCase(SwitchCase switchCase) {
        assertNotNull(switchCase);
        if (this.cases == null) {
            this.cases = new ArrayList();
        }
        this.cases.add(switchCase);
        switchCase.setParent(this);
    }

    public List<SwitchCase> getCases() {
        return this.cases != null ? this.cases : NO_CASES;
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public int getLp() {
        return this.lp;
    }

    public int getRp() {
        return this.rp;
    }

    public void setCases(List<SwitchCase> list) {
        if (list == null) {
            this.cases = null;
            return;
        }
        if (this.cases != null) {
            this.cases.clear();
        }
        for (SwitchCase addCase : list) {
            addCase(addCase);
        }
    }

    public void setExpression(AstNode astNode) {
        assertNotNull(astNode);
        this.expression = astNode;
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

    public String toSource(int i) {
        String makeIndent = makeIndent(i);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent);
        stringBuilder.append("switch (");
        stringBuilder.append(this.expression.toSource(0));
        stringBuilder.append(") {\n");
        if (this.cases != null) {
            for (SwitchCase toSource : this.cases) {
                stringBuilder.append(toSource.toSource(i + 1));
            }
        }
        stringBuilder.append(makeIndent);
        stringBuilder.append("}\n");
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            this.expression.visit(nodeVisitor);
            for (SwitchCase visit : getCases()) {
                visit.visit(nodeVisitor);
            }
        }
    }
}
