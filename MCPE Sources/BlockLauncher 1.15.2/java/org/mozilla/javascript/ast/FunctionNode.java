package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;

public class FunctionNode extends ScriptNode {
    public static final int ARROW_FUNCTION = 4;
    public static final int FUNCTION_EXPRESSION = 2;
    public static final int FUNCTION_EXPRESSION_STATEMENT = 3;
    public static final int FUNCTION_STATEMENT = 1;
    private static final List<AstNode> NO_PARAMS = Collections.unmodifiableList(new ArrayList());
    private AstNode body;
    private Form functionForm;
    private Name functionName;
    private int functionType;
    private List<Node> generatorResumePoints;
    private boolean isExpressionClosure;
    private boolean isGenerator;
    private Map<Node, int[]> liveLocals;
    private int lp;
    private AstNode memberExprNode;
    private boolean needsActivation;
    private List<AstNode> params;
    private int rp;

    public enum Form {
        FUNCTION,
        GETTER,
        SETTER,
        METHOD
    }

    public FunctionNode() {
        this.functionForm = Form.FUNCTION;
        this.lp = -1;
        this.rp = -1;
        this.type = Token.FUNCTION;
    }

    public FunctionNode(int i) {
        super(i);
        this.functionForm = Form.FUNCTION;
        this.lp = -1;
        this.rp = -1;
        this.type = Token.FUNCTION;
    }

    public FunctionNode(int i, Name name) {
        super(i);
        this.functionForm = Form.FUNCTION;
        this.lp = -1;
        this.rp = -1;
        this.type = Token.FUNCTION;
        setFunctionName(name);
    }

    public int addFunction(FunctionNode functionNode) {
        int addFunction = super.addFunction(functionNode);
        if (getFunctionCount() > 0) {
            this.needsActivation = true;
        }
        return addFunction;
    }

    public void addLiveLocals(Node node, int[] iArr) {
        if (this.liveLocals == null) {
            this.liveLocals = new HashMap();
        }
        this.liveLocals.put(node, iArr);
    }

    public void addParam(AstNode astNode) {
        assertNotNull(astNode);
        if (this.params == null) {
            this.params = new ArrayList();
        }
        this.params.add(astNode);
        astNode.setParent(this);
    }

    public void addResumptionPoint(Node node) {
        if (this.generatorResumePoints == null) {
            this.generatorResumePoints = new ArrayList();
        }
        this.generatorResumePoints.add(node);
    }

    public AstNode getBody() {
        return this.body;
    }

    public Name getFunctionName() {
        return this.functionName;
    }

    public int getFunctionType() {
        return this.functionType;
    }

    public Map<Node, int[]> getLiveLocals() {
        return this.liveLocals;
    }

    public int getLp() {
        return this.lp;
    }

    public AstNode getMemberExprNode() {
        return this.memberExprNode;
    }

    public String getName() {
        return this.functionName != null ? this.functionName.getIdentifier() : BuildConfig.FLAVOR;
    }

    public List<AstNode> getParams() {
        return this.params != null ? this.params : NO_PARAMS;
    }

    public List<Node> getResumptionPoints() {
        return this.generatorResumePoints;
    }

    public int getRp() {
        return this.rp;
    }

    public boolean isExpressionClosure() {
        return this.isExpressionClosure;
    }

    public boolean isGenerator() {
        return this.isGenerator;
    }

    public boolean isGetterMethod() {
        return this.functionForm == Form.GETTER;
    }

    public boolean isMethod() {
        return this.functionForm == Form.GETTER || this.functionForm == Form.SETTER || this.functionForm == Form.METHOD;
    }

    public boolean isNormalMethod() {
        return this.functionForm == Form.METHOD;
    }

    public boolean isParam(AstNode astNode) {
        return this.params == null ? false : this.params.contains(astNode);
    }

    public boolean isSetterMethod() {
        return this.functionForm == Form.SETTER;
    }

    public boolean requiresActivation() {
        return this.needsActivation;
    }

    public void setBody(AstNode astNode) {
        assertNotNull(astNode);
        this.body = astNode;
        if (Boolean.TRUE.equals(astNode.getProp(25))) {
            setIsExpressionClosure(true);
        }
        int position = astNode.getPosition() + astNode.getLength();
        astNode.setParent(this);
        setLength(position - this.position);
        setEncodedSourceBounds(this.position, position);
    }

    public void setFunctionIsGetterMethod() {
        this.functionForm = Form.GETTER;
    }

    public void setFunctionIsNormalMethod() {
        this.functionForm = Form.METHOD;
    }

    public void setFunctionIsSetterMethod() {
        this.functionForm = Form.SETTER;
    }

    public void setFunctionName(Name name) {
        this.functionName = name;
        if (name != null) {
            name.setParent(this);
        }
    }

    public void setFunctionType(int i) {
        this.functionType = i;
    }

    public void setIsExpressionClosure(boolean z) {
        this.isExpressionClosure = z;
    }

    public void setIsGenerator() {
        this.isGenerator = true;
    }

    public void setLp(int i) {
        this.lp = i;
    }

    public void setMemberExprNode(AstNode astNode) {
        this.memberExprNode = astNode;
        if (astNode != null) {
            astNode.setParent(this);
        }
    }

    public void setParams(List<AstNode> list) {
        if (list == null) {
            this.params = null;
            return;
        }
        if (this.params != null) {
            this.params.clear();
        }
        for (AstNode addParam : list) {
            addParam(addParam);
        }
    }

    public void setParens(int i, int i2) {
        this.lp = i;
        this.rp = i2;
    }

    public void setRequiresActivation() {
        this.needsActivation = true;
    }

    public void setRp(int i) {
        this.rp = i;
    }

    public String toSource(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        int i2 = this.functionType == ARROW_FUNCTION ? FUNCTION_STATEMENT : 0;
        if (!isMethod()) {
            stringBuilder.append(makeIndent(i));
            if (i2 == 0) {
                stringBuilder.append("function");
            }
        }
        if (this.functionName != null) {
            stringBuilder.append(" ");
            stringBuilder.append(this.functionName.toSource(0));
        }
        if (this.params == null) {
            stringBuilder.append("() ");
        } else if (i2 == 0 || this.lp != -1) {
            stringBuilder.append("(");
            printList(this.params, stringBuilder);
            stringBuilder.append(") ");
        } else {
            printList(this.params, stringBuilder);
            stringBuilder.append(" ");
        }
        if (i2 != 0) {
            stringBuilder.append("=> ");
        }
        if (this.isExpressionClosure) {
            AstNode body = getBody();
            if (body.getLastChild() instanceof ReturnStatement) {
                stringBuilder.append(((ReturnStatement) body.getLastChild()).getReturnValue().toSource(0));
                if (this.functionType == FUNCTION_STATEMENT) {
                    stringBuilder.append(";");
                }
            } else {
                stringBuilder.append(" ");
                stringBuilder.append(body.toSource(0));
            }
        } else {
            stringBuilder.append(getBody().toSource(i).trim());
        }
        if (this.functionType == FUNCTION_STATEMENT || isMethod()) {
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void visit(NodeVisitor nodeVisitor) {
        if (nodeVisitor.visit(this)) {
            if (this.functionName != null) {
                this.functionName.visit(nodeVisitor);
            }
            for (AstNode visit : getParams()) {
                visit.visit(nodeVisitor);
            }
            getBody().visit(nodeVisitor);
            if (!this.isExpressionClosure && this.memberExprNode != null) {
                this.memberExprNode.visit(nodeVisitor);
            }
        }
    }
}
