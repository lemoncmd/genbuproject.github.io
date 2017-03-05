package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

public class VariableDeclaration extends AstNode {
    private boolean isStatement;
    private List<VariableInitializer> variables;

    public VariableDeclaration() {
        this.variables = new ArrayList();
        this.type = Token.VAR;
    }

    public VariableDeclaration(int i) {
        super(i);
        this.variables = new ArrayList();
        this.type = Token.VAR;
    }

    public VariableDeclaration(int i, int i2) {
        super(i, i2);
        this.variables = new ArrayList();
        this.type = Token.VAR;
    }

    private String declTypeName() {
        return Token.typeToName(this.type).toLowerCase();
    }

    public void addVariable(VariableInitializer variableInitializer) {
        assertNotNull(variableInitializer);
        this.variables.add(variableInitializer);
        variableInitializer.setParent(this);
    }

    public List<VariableInitializer> getVariables() {
        return this.variables;
    }

    public boolean isConst() {
        return this.type == Token.CONST;
    }

    public boolean isLet() {
        return this.type == Token.LET;
    }

    public boolean isStatement() {
        return this.isStatement;
    }

    public boolean isVar() {
        return this.type == Token.VAR;
    }

    public void setIsStatement(boolean z) {
        this.isStatement = z;
    }

    public Node setType(int i) {
        if (i == Token.VAR || i == Token.CONST || i == Token.LET) {
            return super.setType(i);
        }
        throw new IllegalArgumentException("invalid decl type: " + i);
    }

    public void setVariables(List<VariableInitializer> list) {
        assertNotNull(list);
        this.variables.clear();
        for (VariableInitializer addVariable : list) {
            addVariable(addVariable);
        }
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(makeIndent(i));
        stringBuilder.append(declTypeName());
        stringBuilder.append(" ");
        printList(this.variables, stringBuilder);
        if (isStatement()) {
            stringBuilder.append(";\n");
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            for (AstNode visit : this.variables) {
                visit.visit(nodeVisitor);
            }
        }
    }
}
