package org.mozilla.javascript;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.mozilla.javascript.ast.Comment;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;

public class Node implements Iterable<Node> {
    public static final int ARROW_FUNCTION_PROP = 27;
    public static final int ATTRIBUTE_FLAG = 2;
    public static final int BOTH = 0;
    public static final int CASEARRAY_PROP = 5;
    public static final int CATCH_SCOPE_PROP = 14;
    public static final int CONTROL_BLOCK_PROP = 18;
    public static final int DECR_FLAG = 1;
    public static final int DESCENDANTS_FLAG = 4;
    public static final int DESTRUCTURING_ARRAY_LENGTH = 21;
    public static final int DESTRUCTURING_NAMES = 22;
    public static final int DESTRUCTURING_PARAMS = 23;
    public static final int DESTRUCTURING_SHORTHAND = 26;
    public static final int DIRECTCALL_PROP = 9;
    public static final int END_DROPS_OFF = 1;
    public static final int END_RETURNS = 2;
    public static final int END_RETURNS_VALUE = 4;
    public static final int END_UNREACHED = 0;
    public static final int END_YIELDS = 8;
    public static final int EXPRESSION_CLOSURE_PROP = 25;
    public static final int FUNCTION_PROP = 1;
    public static final int GENERATOR_END_PROP = 20;
    public static final int INCRDECR_PROP = 13;
    public static final int ISNUMBER_PROP = 8;
    public static final int JSDOC_PROP = 24;
    public static final int LABEL_ID_PROP = 15;
    public static final int LAST_PROP = 27;
    public static final int LEFT = 1;
    public static final int LOCAL_BLOCK_PROP = 3;
    public static final int LOCAL_PROP = 2;
    public static final int MEMBER_TYPE_PROP = 16;
    public static final int NAME_PROP = 17;
    public static final int NON_SPECIALCALL = 0;
    private static final Node NOT_SET = new Node(-1);
    public static final int OBJECT_IDS_PROP = 12;
    public static final int PARENTHESIZED_PROP = 19;
    public static final int POST_FLAG = 2;
    public static final int PROPERTY_FLAG = 1;
    public static final int REGEXP_PROP = 4;
    public static final int RIGHT = 2;
    public static final int SKIP_INDEXES_PROP = 11;
    public static final int SPECIALCALL_EVAL = 1;
    public static final int SPECIALCALL_PROP = 10;
    public static final int SPECIALCALL_WITH = 2;
    public static final int TARGETBLOCK_PROP = 6;
    public static final int VARIABLE_PROP = 7;
    protected Node first;
    protected Node last;
    protected int lineno;
    protected Node next;
    protected PropListItem propListHead;
    protected int type;

    public class NodeIterator implements Iterator<Node> {
        private Node cursor;
        private Node prev = Node.NOT_SET;
        private Node prev2;
        private boolean removed = false;

        public NodeIterator() {
            this.cursor = Node.this.first;
        }

        public boolean hasNext() {
            return this.cursor != null;
        }

        public Node next() {
            if (this.cursor == null) {
                throw new NoSuchElementException();
            }
            this.removed = false;
            this.prev2 = this.prev;
            this.prev = this.cursor;
            this.cursor = this.cursor.next;
            return this.prev;
        }

        public void remove() {
            if (this.prev == Node.NOT_SET) {
                throw new IllegalStateException("next() has not been called");
            } else if (this.removed) {
                throw new IllegalStateException("remove() already called for current element");
            } else if (this.prev == Node.this.first) {
                Node.this.first = this.prev.next;
            } else if (this.prev == Node.this.last) {
                this.prev2.next = null;
                Node.this.last = this.prev2;
            } else {
                this.prev2.next = this.cursor;
            }
        }
    }

    private static class PropListItem {
        int intValue;
        PropListItem next;
        Object objectValue;
        int type;

        private PropListItem() {
        }
    }

    public Node(int i) {
        this.type = -1;
        this.lineno = -1;
        this.type = i;
    }

    public Node(int i, int i2) {
        this.type = -1;
        this.lineno = -1;
        this.type = i;
        this.lineno = i2;
    }

    public Node(int i, Node node) {
        this.type = -1;
        this.lineno = -1;
        this.type = i;
        this.last = node;
        this.first = node;
        node.next = null;
    }

    public Node(int i, Node node, int i2) {
        this(i, node);
        this.lineno = i2;
    }

    public Node(int i, Node node, Node node2) {
        this.type = -1;
        this.lineno = -1;
        this.type = i;
        this.first = node;
        this.last = node2;
        node.next = node2;
        node2.next = null;
    }

    public Node(int i, Node node, Node node2, int i2) {
        this(i, node, node2);
        this.lineno = i2;
    }

    public Node(int i, Node node, Node node2, Node node3) {
        this.type = -1;
        this.lineno = -1;
        this.type = i;
        this.first = node;
        this.last = node3;
        node.next = node2;
        node2.next = node3;
        node3.next = null;
    }

    public Node(int i, Node node, Node node2, Node node3, int i2) {
        this(i, node, node2, node3);
        this.lineno = i2;
    }

    private static void appendPrintId(Node node, ObjToIntMap objToIntMap, StringBuilder stringBuilder) {
    }

    private int endCheck() {
        switch (this.type) {
            case REGEXP_PROP /*4*/:
                return this.first != null ? REGEXP_PROP : SPECIALCALL_WITH;
            case Token.THROW /*50*/:
            case Token.CONTINUE /*122*/:
                return NON_SPECIALCALL;
            case Token.YIELD /*73*/:
                return ISNUMBER_PROP;
            case Token.BREAK /*121*/:
                return endCheckBreak();
            case Token.BLOCK /*130*/:
            case Token.LOCAL_BLOCK /*142*/:
                if (this.first == null) {
                    return SPECIALCALL_EVAL;
                }
                switch (this.first.type) {
                    case VARIABLE_PROP /*7*/:
                        return this.first.endCheckIf();
                    case Token.TRY /*82*/:
                        return this.first.endCheckTry();
                    case Token.SWITCH /*115*/:
                        return this.first.endCheckSwitch();
                    case Token.LABEL /*131*/:
                        return this.first.endCheckLabel();
                    default:
                        return endCheckBlock();
                }
            case Token.TARGET /*132*/:
                return this.next != null ? this.next.endCheck() : SPECIALCALL_EVAL;
            case Token.LOOP /*133*/:
                return endCheckLoop();
            case Token.EXPR_VOID /*134*/:
                return this.first != null ? this.first.endCheck() : SPECIALCALL_EVAL;
            default:
                return SPECIALCALL_EVAL;
        }
    }

    private int endCheckBlock() {
        int i = SPECIALCALL_EVAL;
        Node node = this.first;
        while ((i & SPECIALCALL_EVAL) != 0 && node != null) {
            i = (i & -2) | node.endCheck();
            node = node.next;
        }
        return i;
    }

    private int endCheckBreak() {
        ((Jump) this).getJumpStatement().putIntProp(CONTROL_BLOCK_PROP, SPECIALCALL_EVAL);
        return NON_SPECIALCALL;
    }

    private int endCheckIf() {
        Node node = this.next;
        Node node2 = ((Jump) this).target;
        int endCheck = node.endCheck();
        return node2 != null ? endCheck | node2.endCheck() : endCheck | SPECIALCALL_EVAL;
    }

    private int endCheckLabel() {
        return this.next.endCheck() | getIntProp(CONTROL_BLOCK_PROP, NON_SPECIALCALL);
    }

    private int endCheckLoop() {
        Node node = this.first;
        while (node.next != this.last) {
            node = node.next;
        }
        if (node.type != TARGETBLOCK_PROP) {
            return SPECIALCALL_EVAL;
        }
        int endCheck = ((Jump) node).target.next.endCheck();
        if (node.first.type == 45) {
            endCheck &= -2;
        }
        return endCheck | getIntProp(CONTROL_BLOCK_PROP, NON_SPECIALCALL);
    }

    private int endCheckSwitch() {
        return NON_SPECIALCALL;
    }

    private int endCheckTry() {
        return NON_SPECIALCALL;
    }

    private PropListItem ensureProperty(int i) {
        PropListItem lookupProperty = lookupProperty(i);
        if (lookupProperty != null) {
            return lookupProperty;
        }
        lookupProperty = new PropListItem();
        lookupProperty.type = i;
        lookupProperty.next = this.propListHead;
        this.propListHead = lookupProperty;
        return lookupProperty;
    }

    private static void generatePrintIds(Node node, ObjToIntMap objToIntMap) {
    }

    private PropListItem lookupProperty(int i) {
        PropListItem propListItem = this.propListHead;
        while (propListItem != null && i != propListItem.type) {
            propListItem = propListItem.next;
        }
        return propListItem;
    }

    public static Node newNumber(double d) {
        Node numberLiteral = new NumberLiteral();
        numberLiteral.setNumber(d);
        return numberLiteral;
    }

    public static Node newString(int i, String str) {
        Node name = new Name();
        name.setIdentifier(str);
        name.setType(i);
        return name;
    }

    public static Node newString(String str) {
        return newString(41, str);
    }

    public static Node newTarget() {
        return new Node(Token.TARGET);
    }

    private static final String propToString(int i) {
        return null;
    }

    private void resetTargets_r() {
        if (this.type == Token.TARGET || this.type == 73) {
            labelId(-1);
        }
        for (Node node = this.first; node != null; node = node.next) {
            node.resetTargets_r();
        }
    }

    private void toString(ObjToIntMap objToIntMap, StringBuilder stringBuilder) {
    }

    private static void toStringTreeHelper(ScriptNode scriptNode, Node node, ObjToIntMap objToIntMap, int i, StringBuilder stringBuilder) {
    }

    public void addChildAfter(Node node, Node node2) {
        if (node.next != null) {
            throw new RuntimeException("newChild had siblings in addChildAfter");
        }
        node.next = node2.next;
        node2.next = node;
        if (this.last == node2) {
            this.last = node;
        }
    }

    public void addChildBefore(Node node, Node node2) {
        if (node.next != null) {
            throw new RuntimeException("newChild had siblings in addChildBefore");
        } else if (this.first == node2) {
            node.next = this.first;
            this.first = node;
        } else {
            addChildAfter(node, getChildBefore(node2));
        }
    }

    public void addChildToBack(Node node) {
        node.next = null;
        if (this.last == null) {
            this.last = node;
            this.first = node;
            return;
        }
        this.last.next = node;
        this.last = node;
    }

    public void addChildToFront(Node node) {
        node.next = this.first;
        this.first = node;
        if (this.last == null) {
            this.last = node;
        }
    }

    public void addChildrenToBack(Node node) {
        if (this.last != null) {
            this.last.next = node;
        }
        this.last = node.getLastSibling();
        if (this.first == null) {
            this.first = node;
        }
    }

    public void addChildrenToFront(Node node) {
        Node lastSibling = node.getLastSibling();
        lastSibling.next = this.first;
        this.first = node;
        if (this.last == null) {
            this.last = lastSibling;
        }
    }

    public Node getChildBefore(Node node) {
        if (node == this.first) {
            return null;
        }
        Node node2 = this.first;
        while (node2.next != node) {
            node2 = node2.next;
            if (node2 == null) {
                throw new RuntimeException("node is not a child");
            }
        }
        return node2;
    }

    public final double getDouble() {
        return ((NumberLiteral) this).getNumber();
    }

    public int getExistingIntProp(int i) {
        PropListItem lookupProperty = lookupProperty(i);
        if (lookupProperty == null) {
            Kit.codeBug();
        }
        return lookupProperty.intValue;
    }

    public Node getFirstChild() {
        return this.first;
    }

    public int getIntProp(int i, int i2) {
        PropListItem lookupProperty = lookupProperty(i);
        return lookupProperty == null ? i2 : lookupProperty.intValue;
    }

    public String getJsDoc() {
        Comment jsDocNode = getJsDocNode();
        return jsDocNode != null ? jsDocNode.getValue() : null;
    }

    public Comment getJsDocNode() {
        return (Comment) getProp(JSDOC_PROP);
    }

    public Node getLastChild() {
        return this.last;
    }

    public Node getLastSibling() {
        while (this.next != null) {
            this = this.next;
        }
        return this;
    }

    public int getLineno() {
        return this.lineno;
    }

    public Node getNext() {
        return this.next;
    }

    public Object getProp(int i) {
        PropListItem lookupProperty = lookupProperty(i);
        return lookupProperty == null ? null : lookupProperty.objectValue;
    }

    public Scope getScope() {
        return ((Name) this).getScope();
    }

    public final String getString() {
        return ((Name) this).getIdentifier();
    }

    public int getType() {
        return this.type;
    }

    public boolean hasChildren() {
        return this.first != null;
    }

    public boolean hasConsistentReturnUsage() {
        int endCheck = endCheck();
        return (endCheck & REGEXP_PROP) == 0 || (endCheck & SKIP_INDEXES_PROP) == 0;
    }

    public boolean hasSideEffects() {
        boolean z = false;
        switch (this.type) {
            case Token.ERROR /*-1*/:
            case SPECIALCALL_WITH /*2*/:
            case LOCAL_BLOCK_PROP /*3*/:
            case REGEXP_PROP /*4*/:
            case CASEARRAY_PROP /*5*/:
            case TARGETBLOCK_PROP /*6*/:
            case VARIABLE_PROP /*7*/:
            case ISNUMBER_PROP /*8*/:
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
            case Token.COMMA /*90*/:
            case Token.EXPR_VOID /*134*/:
                return this.last != null ? this.last.hasSideEffects() : true;
            case Token.HOOK /*103*/:
                if (this.first == null || this.first.next == null || this.first.next.next == null) {
                    Kit.codeBug();
                }
                return this.first.next.hasSideEffects() && this.first.next.next.hasSideEffects();
            case Token.OR /*105*/:
            case Token.AND /*106*/:
                if (this.first == null || this.last == null) {
                    Kit.codeBug();
                }
                if (this.first.hasSideEffects() || this.last.hasSideEffects()) {
                    z = true;
                }
                return z;
            default:
                return false;
        }
    }

    public Iterator<Node> iterator() {
        return new NodeIterator();
    }

    public final int labelId() {
        if (!(this.type == Token.TARGET || this.type == 73)) {
            Kit.codeBug();
        }
        return getIntProp(LABEL_ID_PROP, -1);
    }

    public void labelId(int i) {
        if (!(this.type == Token.TARGET || this.type == 73)) {
            Kit.codeBug();
        }
        putIntProp(LABEL_ID_PROP, i);
    }

    public void putIntProp(int i, int i2) {
        ensureProperty(i).intValue = i2;
    }

    public void putProp(int i, Object obj) {
        if (obj == null) {
            removeProp(i);
        } else {
            ensureProperty(i).objectValue = obj;
        }
    }

    public void removeChild(Node node) {
        Node childBefore = getChildBefore(node);
        if (childBefore == null) {
            this.first = this.first.next;
        } else {
            childBefore.next = node.next;
        }
        if (node == this.last) {
            this.last = childBefore;
        }
        node.next = null;
    }

    public void removeChildren() {
        this.last = null;
        this.first = null;
    }

    public void removeProp(int i) {
        PropListItem propListItem = this.propListHead;
        if (propListItem != null) {
            PropListItem propListItem2 = null;
            while (propListItem.type != i) {
                propListItem2 = propListItem.next;
                if (propListItem2 != null) {
                    PropListItem propListItem3 = propListItem;
                    propListItem = propListItem2;
                    propListItem2 = propListItem3;
                } else {
                    return;
                }
            }
            if (propListItem2 == null) {
                this.propListHead = propListItem.next;
            } else {
                propListItem2.next = propListItem.next;
            }
        }
    }

    public void replaceChild(Node node, Node node2) {
        node2.next = node.next;
        if (node == this.first) {
            this.first = node2;
        } else {
            getChildBefore(node).next = node2;
        }
        if (node == this.last) {
            this.last = node2;
        }
        node.next = null;
    }

    public void replaceChildAfter(Node node, Node node2) {
        Node node3 = node.next;
        node2.next = node3.next;
        node.next = node2;
        if (node3 == this.last) {
            this.last = node2;
        }
        node3.next = null;
    }

    public void resetTargets() {
        if (this.type == Token.FINALLY) {
            resetTargets_r();
        } else {
            Kit.codeBug();
        }
    }

    public final void setDouble(double d) {
        ((NumberLiteral) this).setNumber(d);
    }

    public void setJsDocNode(Comment comment) {
        putProp(JSDOC_PROP, comment);
    }

    public void setLineno(int i) {
        this.lineno = i;
    }

    public void setScope(Scope scope) {
        if (scope == null) {
            Kit.codeBug();
        }
        if (this instanceof Name) {
            ((Name) this).setScope(scope);
            return;
        }
        throw Kit.codeBug();
    }

    public final void setString(String str) {
        if (str == null) {
            Kit.codeBug();
        }
        ((Name) this).setIdentifier(str);
    }

    public Node setType(int i) {
        this.type = i;
        return this;
    }

    public String toString() {
        return String.valueOf(this.type);
    }

    public String toStringTree(ScriptNode scriptNode) {
        return null;
    }
}
