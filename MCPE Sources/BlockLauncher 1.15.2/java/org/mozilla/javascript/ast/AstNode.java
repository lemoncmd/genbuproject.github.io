package org.mozilla.javascript.ast;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mozilla.javascript.Kit;
import org.mozilla.javascript.Node;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.regexp.NativeRegExp;

public abstract class AstNode extends Node implements Comparable<AstNode> {
    private static Map<Integer, String> operatorNames = new HashMap();
    protected int length;
    protected AstNode parent;
    protected int position;

    protected static class DebugPrintVisitor implements NodeVisitor {
        private static final int DEBUG_INDENT = 2;
        private StringBuilder buffer;

        public DebugPrintVisitor(StringBuilder stringBuilder) {
            this.buffer = stringBuilder;
        }

        private String makeIndent(int i) {
            StringBuilder stringBuilder = new StringBuilder(i * DEBUG_INDENT);
            for (int i2 = 0; i2 < i * DEBUG_INDENT; i2++) {
                stringBuilder.append(" ");
            }
            return stringBuilder.toString();
        }

        public String toString() {
            return this.buffer.toString();
        }

        public boolean visit(AstNode astNode) {
            int type = astNode.getType();
            String typeToName = Token.typeToName(type);
            this.buffer.append(astNode.getAbsolutePosition()).append("\t");
            this.buffer.append(makeIndent(astNode.depth()));
            this.buffer.append(typeToName).append(" ");
            this.buffer.append(astNode.getPosition()).append(" ");
            this.buffer.append(astNode.getLength());
            if (type == 39) {
                this.buffer.append(" ").append(((Name) astNode).getIdentifier());
            }
            this.buffer.append("\n");
            return true;
        }
    }

    public static class PositionComparator implements Comparator<AstNode>, Serializable {
        private static final long serialVersionUID = 1;

        public int compare(AstNode astNode, AstNode astNode2) {
            return astNode.position - astNode2.position;
        }
    }

    static {
        operatorNames.put(Integer.valueOf(52), "in");
        operatorNames.put(Integer.valueOf(32), "typeof");
        operatorNames.put(Integer.valueOf(53), "instanceof");
        operatorNames.put(Integer.valueOf(31), "delete");
        operatorNames.put(Integer.valueOf(90), ",");
        operatorNames.put(Integer.valueOf(Token.COLON), ":");
        operatorNames.put(Integer.valueOf(Token.OR), "||");
        operatorNames.put(Integer.valueOf(Token.AND), "&&");
        operatorNames.put(Integer.valueOf(Token.INC), "++");
        operatorNames.put(Integer.valueOf(Token.DEC), "--");
        operatorNames.put(Integer.valueOf(9), "|");
        operatorNames.put(Integer.valueOf(10), "^");
        operatorNames.put(Integer.valueOf(11), "&");
        operatorNames.put(Integer.valueOf(12), "==");
        operatorNames.put(Integer.valueOf(13), "!=");
        operatorNames.put(Integer.valueOf(14), "<");
        operatorNames.put(Integer.valueOf(16), ">");
        operatorNames.put(Integer.valueOf(15), "<=");
        operatorNames.put(Integer.valueOf(17), ">=");
        operatorNames.put(Integer.valueOf(18), "<<");
        operatorNames.put(Integer.valueOf(19), ">>");
        operatorNames.put(Integer.valueOf(20), ">>>");
        operatorNames.put(Integer.valueOf(21), "+");
        operatorNames.put(Integer.valueOf(22), "-");
        operatorNames.put(Integer.valueOf(23), "*");
        operatorNames.put(Integer.valueOf(24), "/");
        operatorNames.put(Integer.valueOf(25), "%");
        operatorNames.put(Integer.valueOf(26), "!");
        operatorNames.put(Integer.valueOf(27), "~");
        operatorNames.put(Integer.valueOf(28), "+");
        operatorNames.put(Integer.valueOf(29), "-");
        operatorNames.put(Integer.valueOf(46), "===");
        operatorNames.put(Integer.valueOf(47), "!==");
        operatorNames.put(Integer.valueOf(91), "=");
        operatorNames.put(Integer.valueOf(92), "|=");
        operatorNames.put(Integer.valueOf(94), "&=");
        operatorNames.put(Integer.valueOf(95), "<<=");
        operatorNames.put(Integer.valueOf(96), ">>=");
        operatorNames.put(Integer.valueOf(97), ">>>=");
        operatorNames.put(Integer.valueOf(98), "+=");
        operatorNames.put(Integer.valueOf(99), "-=");
        operatorNames.put(Integer.valueOf(100), "*=");
        operatorNames.put(Integer.valueOf(Token.ASSIGN_DIV), "/=");
        operatorNames.put(Integer.valueOf(Token.LAST_ASSIGN), "%=");
        operatorNames.put(Integer.valueOf(93), "^=");
        operatorNames.put(Integer.valueOf(Token.VOID), "void");
    }

    public AstNode() {
        super(-1);
        this.position = -1;
        this.length = 1;
    }

    public AstNode(int i) {
        this();
        this.position = i;
    }

    public AstNode(int i, int i2) {
        this();
        this.position = i;
        this.length = i2;
    }

    public static RuntimeException codeBug() throws RuntimeException {
        throw Kit.codeBug();
    }

    public static String operatorToString(int i) {
        String str = (String) operatorNames.get(Integer.valueOf(i));
        if (str != null) {
            return str;
        }
        throw new IllegalArgumentException("Invalid operator: " + i);
    }

    public void addChild(AstNode astNode) {
        assertNotNull(astNode);
        setLength((astNode.getPosition() + astNode.getLength()) - getPosition());
        addChildToBack(astNode);
        astNode.setParent(this);
    }

    protected void assertNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("arg cannot be null");
        }
    }

    public int compareTo(AstNode astNode) {
        if (equals(astNode)) {
            return 0;
        }
        int absolutePosition = getAbsolutePosition();
        int absolutePosition2 = astNode.getAbsolutePosition();
        if (absolutePosition < absolutePosition2) {
            return -1;
        }
        if (absolutePosition2 < absolutePosition) {
            return 1;
        }
        absolutePosition = getLength();
        absolutePosition2 = astNode.getLength();
        return absolutePosition >= absolutePosition2 ? absolutePosition2 < absolutePosition ? 1 : hashCode() - astNode.hashCode() : -1;
    }

    public String debugPrint() {
        Object debugPrintVisitor = new DebugPrintVisitor(new StringBuilder(1000));
        visit(debugPrintVisitor);
        return debugPrintVisitor.toString();
    }

    public int depth() {
        return this.parent == null ? 0 : this.parent.depth() + 1;
    }

    public int getAbsolutePosition() {
        int i = this.position;
        for (AstNode astNode = this.parent; astNode != null; astNode = astNode.getParent()) {
            i += astNode.getPosition();
        }
        return i;
    }

    public AstRoot getAstRoot() {
        AstNode astNode = this;
        while (astNode != null && !(astNode instanceof AstRoot)) {
            astNode = astNode.getParent();
        }
        return (AstRoot) astNode;
    }

    public FunctionNode getEnclosingFunction() {
        AstNode parent = getParent();
        while (parent != null && !(parent instanceof FunctionNode)) {
            parent = parent.getParent();
        }
        return (FunctionNode) parent;
    }

    public Scope getEnclosingScope() {
        AstNode parent = getParent();
        while (parent != null && !(parent instanceof Scope)) {
            parent = parent.getParent();
        }
        return (Scope) parent;
    }

    public int getLength() {
        return this.length;
    }

    public int getLineno() {
        return this.lineno != -1 ? this.lineno : this.parent != null ? this.parent.getLineno() : -1;
    }

    public AstNode getParent() {
        return this.parent;
    }

    public int getPosition() {
        return this.position;
    }

    public boolean hasSideEffects() {
        switch (getType()) {
            case Token.ERROR /*-1*/:
            case NativeRegExp.PREFIX /*2*/:
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
            case NativeRegExp.JSREG_MULTILINE /*4*/:
            case Token.GOTO /*5*/:
            case Token.IFEQ /*6*/:
            case Token.IFNE /*7*/:
            case Token.SETNAME /*8*/:
            case Token.NEW /*30*/:
            case Token.DELPROP /*31*/:
            case Token.SETPROP /*35*/:
            case Token.SETELEM /*37*/:
            case Token.CALL /*38*/:
            case Token.THROW /*50*/:
            case Token.RETHROW /*51*/:
            case Token.SETVAR /*56*/:
            case Token.CATCH_SCOPE /*57*/:
            case Token.RETURN_RESULT /*65*/:
            case Token.SET_REF /*69*/:
            case Token.DEL_REF /*70*/:
            case Token.REF_CALL /*71*/:
            case Token.YIELD /*73*/:
            case Token.TRY /*82*/:
            case Token.SEMI /*83*/:
            case Token.FIRST_ASSIGN /*91*/:
            case Token.ASSIGN_BITOR /*92*/:
            case Token.ASSIGN_BITXOR /*93*/:
            case Token.ASSIGN_BITAND /*94*/:
            case Token.ASSIGN_LSH /*95*/:
            case Token.ASSIGN_RSH /*96*/:
            case Token.ASSIGN_URSH /*97*/:
            case Token.ASSIGN_ADD /*98*/:
            case Token.ASSIGN_SUB /*99*/:
            case Token.ASSIGN_MUL /*100*/:
            case Token.ASSIGN_DIV /*101*/:
            case Token.LAST_ASSIGN /*102*/:
            case Token.INC /*107*/:
            case Token.DEC /*108*/:
            case Token.FUNCTION /*110*/:
            case Token.EXPORT /*111*/:
            case Token.IMPORT /*112*/:
            case Token.IF /*113*/:
            case Token.ELSE /*114*/:
            case Token.SWITCH /*115*/:
            case Token.WHILE /*118*/:
            case Token.DO /*119*/:
            case Token.FOR /*120*/:
            case Token.BREAK /*121*/:
            case Token.CONTINUE /*122*/:
            case Token.VAR /*123*/:
            case Token.WITH /*124*/:
            case Token.CATCH /*125*/:
            case Token.FINALLY /*126*/:
            case Token.BLOCK /*130*/:
            case Token.LABEL /*131*/:
            case Token.TARGET /*132*/:
            case Token.LOOP /*133*/:
            case Token.EXPR_RESULT /*135*/:
            case Token.JSR /*136*/:
            case Token.SETPROP_OP /*140*/:
            case Token.SETELEM_OP /*141*/:
            case Token.LOCAL_BLOCK /*142*/:
            case Token.SET_REF_OP /*143*/:
            case Token.LET /*154*/:
            case Token.CONST /*155*/:
            case Token.LETEXPR /*159*/:
            case Token.WITHEXPR /*160*/:
                return true;
            default:
                return false;
        }
    }

    public String makeIndent(int i) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++) {
            stringBuilder.append("  ");
        }
        return stringBuilder.toString();
    }

    protected <T extends AstNode> void printList(List<T> list, StringBuilder stringBuilder) {
        int size = list.size();
        int i = 0;
        for (T t : list) {
            stringBuilder.append(t.toSource(0));
            int i2 = i + 1;
            if (i < size - 1) {
                stringBuilder.append(", ");
            } else if (t instanceof EmptyExpression) {
                stringBuilder.append(",");
            }
            i = i2;
        }
    }

    public void setBounds(int i, int i2) {
        setPosition(i);
        setLength(i2 - i);
    }

    public void setLength(int i) {
        this.length = i;
    }

    public void setParent(AstNode astNode) {
        if (astNode != this.parent) {
            if (this.parent != null) {
                setRelative(-this.parent.getPosition());
            }
            this.parent = astNode;
            if (astNode != null) {
                setRelative(astNode.getPosition());
            }
        }
    }

    public void setPosition(int i) {
        this.position = i;
    }

    public void setRelative(int i) {
        this.position -= i;
    }

    public String shortName() {
        String name = getClass().getName();
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public String toSource() {
        return toSource(0);
    }

    public abstract String toSource(int i);

    public abstract void visit(NodeVisitor nodeVisitor);
}
