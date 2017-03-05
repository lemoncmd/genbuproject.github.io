package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.ast.ArrayComprehension;
import org.mozilla.javascript.ast.ArrayComprehensionLoop;
import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.Assignment;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.Block;
import org.mozilla.javascript.ast.BreakStatement;
import org.mozilla.javascript.ast.CatchClause;
import org.mozilla.javascript.ast.ConditionalExpression;
import org.mozilla.javascript.ast.ContinueStatement;
import org.mozilla.javascript.ast.DestructuringForm;
import org.mozilla.javascript.ast.DoLoop;
import org.mozilla.javascript.ast.ElementGet;
import org.mozilla.javascript.ast.EmptyExpression;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.ForInLoop;
import org.mozilla.javascript.ast.ForLoop;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.GeneratorExpression;
import org.mozilla.javascript.ast.GeneratorExpressionLoop;
import org.mozilla.javascript.ast.IfStatement;
import org.mozilla.javascript.ast.InfixExpression;
import org.mozilla.javascript.ast.Jump;
import org.mozilla.javascript.ast.Label;
import org.mozilla.javascript.ast.LabeledStatement;
import org.mozilla.javascript.ast.LetNode;
import org.mozilla.javascript.ast.Loop;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NewExpression;
import org.mozilla.javascript.ast.NumberLiteral;
import org.mozilla.javascript.ast.ObjectLiteral;
import org.mozilla.javascript.ast.ObjectProperty;
import org.mozilla.javascript.ast.ParenthesizedExpression;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.RegExpLiteral;
import org.mozilla.javascript.ast.ReturnStatement;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;
import org.mozilla.javascript.ast.StringLiteral;
import org.mozilla.javascript.ast.SwitchCase;
import org.mozilla.javascript.ast.SwitchStatement;
import org.mozilla.javascript.ast.Symbol;
import org.mozilla.javascript.ast.ThrowStatement;
import org.mozilla.javascript.ast.TryStatement;
import org.mozilla.javascript.ast.UnaryExpression;
import org.mozilla.javascript.ast.VariableDeclaration;
import org.mozilla.javascript.ast.VariableInitializer;
import org.mozilla.javascript.ast.WhileLoop;
import org.mozilla.javascript.ast.WithStatement;
import org.mozilla.javascript.ast.XmlDotQuery;
import org.mozilla.javascript.ast.XmlElemRef;
import org.mozilla.javascript.ast.XmlExpression;
import org.mozilla.javascript.ast.XmlFragment;
import org.mozilla.javascript.ast.XmlLiteral;
import org.mozilla.javascript.ast.XmlMemberGet;
import org.mozilla.javascript.ast.XmlPropRef;
import org.mozilla.javascript.ast.XmlRef;
import org.mozilla.javascript.ast.XmlString;
import org.mozilla.javascript.ast.Yield;
import org.mozilla.javascript.regexp.NativeRegExp;

public final class IRFactory extends Parser {
    private static final int ALWAYS_FALSE_BOOLEAN = -1;
    private static final int ALWAYS_TRUE_BOOLEAN = 1;
    private static final int LOOP_DO_WHILE = 0;
    private static final int LOOP_FOR = 2;
    private static final int LOOP_WHILE = 1;
    private Decompiler decompiler;

    public IRFactory() {
        this.decompiler = new Decompiler();
    }

    public IRFactory(CompilerEnvirons compilerEnvirons) {
        this(compilerEnvirons, compilerEnvirons.getErrorReporter());
    }

    public IRFactory(CompilerEnvirons compilerEnvirons, ErrorReporter errorReporter) {
        super(compilerEnvirons, errorReporter);
        this.decompiler = new Decompiler();
    }

    private void addSwitchCase(Node node, Node node2, Node node3) {
        if (node.getType() != Token.BLOCK) {
            throw Kit.codeBug();
        }
        Jump jump = (Jump) node.getFirstChild();
        if (jump.getType() != Token.SWITCH) {
            throw Kit.codeBug();
        }
        Node newTarget = Node.newTarget();
        if (node2 != null) {
            Node jump2 = new Jump((int) Token.CASE, node2);
            jump2.target = newTarget;
            jump.addChildToBack(jump2);
        } else {
            jump.setDefault(newTarget);
        }
        node.addChildToBack(newTarget);
        node.addChildToBack(node3);
    }

    private Node arrayCompTransformHelper(ArrayComprehension arrayComprehension, String str) {
        Throwable th;
        this.decompiler.addToken(84);
        int lineno = arrayComprehension.getLineno();
        Node transform = transform(arrayComprehension.getResult());
        List loops = arrayComprehension.getLoops();
        int size = loops.size();
        Node[] nodeArr = new Node[size];
        Node[] nodeArr2 = new Node[size];
        int i = LOOP_DO_WHILE;
        while (i < size) {
            String string;
            ArrayComprehensionLoop arrayComprehensionLoop = (ArrayComprehensionLoop) loops.get(i);
            this.decompiler.addName(" ");
            this.decompiler.addToken(Token.FOR);
            if (arrayComprehensionLoop.isForEach()) {
                this.decompiler.addName("each ");
            }
            this.decompiler.addToken(88);
            Node iterator = arrayComprehensionLoop.getIterator();
            if (iterator.getType() == 39) {
                string = iterator.getString();
                this.decompiler.addName(string);
                iterator = transform;
            } else {
                decompile(iterator);
                string = this.currentScriptOrFn.getNextTempName();
                defineSymbol(88, string, false);
                iterator = createBinary(90, createAssignment(91, iterator, createName(string)), transform);
            }
            Node createName = createName(string);
            defineSymbol(Token.LET, string, false);
            nodeArr[i] = createName;
            if (arrayComprehensionLoop.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            nodeArr2[i] = transform(arrayComprehensionLoop.getIteratedObject());
            this.decompiler.addToken(89);
            i += LOOP_WHILE;
            transform = iterator;
        }
        Node createCallOrNew = createCallOrNew(38, createPropertyGet(createName(str), null, "push", LOOP_DO_WHILE));
        Node node = new Node((int) Token.EXPR_VOID, createCallOrNew, lineno);
        if (arrayComprehension.getFilter() != null) {
            this.decompiler.addName(" ");
            this.decompiler.addToken(Token.IF);
            this.decompiler.addToken(88);
            node = createIf(transform(arrayComprehension.getFilter()), node, null, lineno);
            this.decompiler.addToken(89);
        }
        int i2 = size + ALWAYS_FALSE_BOOLEAN;
        Node node2 = node;
        int i3 = LOOP_DO_WHILE;
        while (i2 >= 0) {
            try {
                arrayComprehensionLoop = (ArrayComprehensionLoop) loops.get(i2);
                iterator = createLoopNode(null, arrayComprehensionLoop.getLineno());
                pushScope(iterator);
                int i4 = i3 + LOOP_WHILE;
                try {
                    node2 = createForIn(Token.LET, iterator, nodeArr[i2], nodeArr2[i2], node2, arrayComprehensionLoop.isForEach(), arrayComprehensionLoop.isForOf());
                    i2 += ALWAYS_FALSE_BOOLEAN;
                    i3 = i4;
                } catch (Throwable th2) {
                    th = th2;
                    i3 = i4;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
        for (int i5 = LOOP_DO_WHILE; i5 < i3; i5 += LOOP_WHILE) {
            popScope();
        }
        this.decompiler.addToken(85);
        createCallOrNew.addChildToBack(transform);
        return node2;
        for (int i6 = LOOP_DO_WHILE; i6 < i3; i6 += LOOP_WHILE) {
            popScope();
        }
        throw th;
    }

    private void closeSwitch(Node node) {
        if (node.getType() != Token.BLOCK) {
            throw Kit.codeBug();
        }
        Jump jump = (Jump) node.getFirstChild();
        if (jump.getType() != Token.SWITCH) {
            throw Kit.codeBug();
        }
        Node newTarget = Node.newTarget();
        jump.target = newTarget;
        Node node2 = jump.getDefault();
        if (node2 == null) {
            node2 = newTarget;
        }
        node.addChildAfter(makeJump(5, node2), jump);
        node.addChildToBack(newTarget);
    }

    private Node createAssignment(int i, Node node, Node node2) {
        Node makeReference = makeReference(node);
        if (makeReference != null) {
            int i2;
            switch (i) {
                case Token.FIRST_ASSIGN /*91*/:
                    return simpleAssignment(makeReference, node2);
                case Token.ASSIGN_BITOR /*92*/:
                    i2 = 9;
                    break;
                case Token.ASSIGN_BITXOR /*93*/:
                    i2 = 10;
                    break;
                case Token.ASSIGN_BITAND /*94*/:
                    i2 = 11;
                    break;
                case Token.ASSIGN_LSH /*95*/:
                    i2 = 18;
                    break;
                case Token.ASSIGN_RSH /*96*/:
                    i2 = 19;
                    break;
                case Token.ASSIGN_URSH /*97*/:
                    i2 = 20;
                    break;
                case Token.ASSIGN_ADD /*98*/:
                    i2 = 21;
                    break;
                case Token.ASSIGN_SUB /*99*/:
                    i2 = 22;
                    break;
                case Token.ASSIGN_MUL /*100*/:
                    i2 = 23;
                    break;
                case Token.ASSIGN_DIV /*101*/:
                    i2 = 24;
                    break;
                case Token.LAST_ASSIGN /*102*/:
                    i2 = 25;
                    break;
                default:
                    throw Kit.codeBug();
            }
            int type = makeReference.getType();
            switch (type) {
                case Token.GETPROP /*33*/:
                case Token.GETELEM /*36*/:
                    return new Node(type == 33 ? Token.SETPROP_OP : Token.SETELEM_OP, makeReference.getFirstChild(), makeReference.getLastChild(), new Node(i2, new Node(Token.USE_STACK), node2));
                case Token.NAME /*39*/:
                    return new Node(8, Node.newString(49, makeReference.getString()), new Node(i2, makeReference, node2));
                case Token.GET_REF /*68*/:
                    makeReference = makeReference.getFirstChild();
                    checkMutableReference(makeReference);
                    return new Node((int) Token.SET_REF_OP, makeReference, new Node(i2, new Node(Token.USE_STACK), node2));
                default:
                    throw Kit.codeBug();
            }
        } else if (node.getType() != 66 && node.getType() != 67) {
            reportError("msg.bad.assign.left");
            return node2;
        } else if (i == 91) {
            return createDestructuringAssignment(ALWAYS_FALSE_BOOLEAN, node, node2);
        } else {
            reportError("msg.bad.destruct.op");
            return node2;
        }
    }

    private Node createBinary(int i, Node node, Node node2) {
        double d;
        int isAlwaysDefinedBoolean;
        switch (i) {
            case Token.ADD /*21*/:
                if (node.type == 41) {
                    String string;
                    if (node2.type == 41) {
                        string = node2.getString();
                    } else if (node2.type == 40) {
                        string = ScriptRuntime.numberToString(node2.getDouble(), 10);
                    }
                    node.setString(node.getString().concat(string));
                    return node;
                } else if (node.type == 40) {
                    if (node2.type == 40) {
                        node.setDouble(node.getDouble() + node2.getDouble());
                        return node;
                    } else if (node2.type == 41) {
                        node2.setString(ScriptRuntime.numberToString(node.getDouble(), 10).concat(node2.getString()));
                        return node2;
                    }
                }
                break;
            case Token.SUB /*22*/:
                if (node.type == 40) {
                    d = node.getDouble();
                    if (node2.type == 40) {
                        node.setDouble(d - node2.getDouble());
                        return node;
                    } else if (d == 0.0d) {
                        return new Node(29, node2);
                    }
                } else if (node2.type == 40 && node2.getDouble() == 0.0d) {
                    return new Node(28, node);
                }
                break;
            case Token.MUL /*23*/:
                if (node.type == 40) {
                    d = node.getDouble();
                    if (node2.type == 40) {
                        node.setDouble(d * node2.getDouble());
                        return node;
                    } else if (d == 1.0d) {
                        return new Node(28, node2);
                    }
                } else if (node2.type == 40 && node2.getDouble() == 1.0d) {
                    return new Node(28, node);
                }
                break;
            case Token.DIV /*24*/:
                if (node2.type == 40) {
                    d = node2.getDouble();
                    if (node.type == 40) {
                        node.setDouble(node.getDouble() / d);
                        return node;
                    } else if (d == 1.0d) {
                        return new Node(28, node);
                    }
                }
                break;
            case Token.OR /*105*/:
                isAlwaysDefinedBoolean = isAlwaysDefinedBoolean(node);
                if (isAlwaysDefinedBoolean == LOOP_WHILE) {
                    return node;
                }
                if (isAlwaysDefinedBoolean == ALWAYS_FALSE_BOOLEAN) {
                    return node2;
                }
                break;
            case Token.AND /*106*/:
                isAlwaysDefinedBoolean = isAlwaysDefinedBoolean(node);
                if (isAlwaysDefinedBoolean == ALWAYS_FALSE_BOOLEAN) {
                    return node;
                }
                if (isAlwaysDefinedBoolean == LOOP_WHILE) {
                    return node2;
                }
                break;
        }
        return new Node(i, node, node2);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private org.mozilla.javascript.Node createCallOrNew(int r5, org.mozilla.javascript.Node r6) {
        /*
        r4 = this;
        r0 = 1;
        r1 = 0;
        r2 = r6.getType();
        r3 = 39;
        if (r2 != r3) goto L_0x0030;
    L_0x000a:
        r2 = r6.getString();
        r3 = "eval";
        r3 = r2.equals(r3);
        if (r3 == 0) goto L_0x0026;
    L_0x0016:
        r1 = new org.mozilla.javascript.Node;
        r1.<init>(r5, r6);
        if (r0 == 0) goto L_0x0025;
    L_0x001d:
        r4.setRequiresActivation();
        r2 = 10;
        r1.putIntProp(r2, r0);
    L_0x0025:
        return r1;
    L_0x0026:
        r0 = "With";
        r0 = r2.equals(r0);
        if (r0 == 0) goto L_0x0048;
    L_0x002e:
        r0 = 2;
        goto L_0x0016;
    L_0x0030:
        r2 = r6.getType();
        r3 = 33;
        if (r2 != r3) goto L_0x0048;
    L_0x0038:
        r2 = r6.getLastChild();
        r2 = r2.getString();
        r3 = "eval";
        r2 = r2.equals(r3);
        if (r2 != 0) goto L_0x0016;
    L_0x0048:
        r0 = r1;
        goto L_0x0016;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.IRFactory.createCallOrNew(int, org.mozilla.javascript.Node):org.mozilla.javascript.Node");
    }

    private Node createCatch(String str, Node node, Node node2, int i) {
        return new Node(Token.CATCH, createName(str), node == null ? new Node(Token.EMPTY) : node, node2, i);
    }

    private Node createCondExpr(Node node, Node node2, Node node3) {
        int isAlwaysDefinedBoolean = isAlwaysDefinedBoolean(node);
        return isAlwaysDefinedBoolean == LOOP_WHILE ? node2 : isAlwaysDefinedBoolean == ALWAYS_FALSE_BOOLEAN ? node3 : new Node((int) Token.HOOK, node, node2, node3);
    }

    private Node createElementGet(Node node, String str, Node node2, int i) {
        if (str != null || i != 0) {
            return createMemberRefGet(node, str, node2, i);
        }
        if (node != null) {
            return new Node(36, node, node2);
        }
        throw Kit.codeBug();
    }

    private Node createExprStatementNoReturn(Node node, int i) {
        return new Node((int) Token.EXPR_VOID, node, i);
    }

    private Node createFor(Scope scope, Node node, Node node2, Node node3, Node node4) {
        if (node.getType() != Token.LET) {
            return createLoop(scope, LOOP_FOR, node4, node2, node, node3);
        }
        Node splitScope = Scope.splitScope(scope);
        splitScope.setType(Token.LET);
        splitScope.addChildrenToBack(node);
        splitScope.addChildToBack(createLoop(scope, LOOP_FOR, node4, node2, new Node(Token.EMPTY), node3));
        return splitScope;
    }

    private Node createForIn(int i, Node node, Node node2, Node node3, Node node4, boolean z, boolean z2) {
        Node lastChild;
        int type;
        int i2;
        Node node5;
        int i3 = LOOP_DO_WHILE;
        int type2 = node2.getType();
        if (type2 == Token.VAR || type2 == Token.LET) {
            lastChild = node2.getLastChild();
            type = lastChild.getType();
            if (type == 66 || type == 67) {
                if (lastChild instanceof ArrayLiteral) {
                    type2 = ((ArrayLiteral) lastChild).getDestructuringLength();
                    i3 = type;
                } else {
                    type2 = LOOP_DO_WHILE;
                    i3 = type;
                }
            } else if (type == 39) {
                lastChild = Node.newString(39, lastChild.getString());
                type = ALWAYS_FALSE_BOOLEAN;
                i3 = type2;
                type2 = LOOP_DO_WHILE;
            } else {
                reportError("msg.bad.for.in.lhs");
                return null;
            }
            i2 = i3;
            i3 = type2;
            type2 = type;
        } else if (type2 == 66 || type2 == 67) {
            i3 = LOOP_DO_WHILE;
            if (node2 instanceof ArrayLiteral) {
                i3 = ((ArrayLiteral) node2).getDestructuringLength();
                i2 = type2;
                lastChild = node2;
            } else {
                i2 = type2;
                lastChild = node2;
            }
        } else {
            lastChild = makeReference(node2);
            if (lastChild == null) {
                reportError("msg.bad.for.in.lhs");
                return null;
            }
            i2 = type2;
            type2 = ALWAYS_FALSE_BOOLEAN;
        }
        Node node6 = new Node(Token.LOCAL_BLOCK);
        type = z ? 59 : z2 ? 61 : type2 != ALWAYS_FALSE_BOOLEAN ? 60 : 58;
        Node node7 = new Node(type, node3);
        node7.putProp(3, node6);
        Node node8 = new Node(62);
        node8.putProp(3, node6);
        Node node9 = new Node(63);
        node9.putProp(3, node6);
        Node node10 = new Node(Token.BLOCK);
        if (type2 != ALWAYS_FALSE_BOOLEAN) {
            lastChild = createDestructuringAssignment(i, lastChild, node9);
            if (z || z2 || (type2 != 67 && r1 == LOOP_FOR)) {
                node5 = lastChild;
            } else {
                reportError("msg.bad.for.in.destruct");
                node5 = lastChild;
            }
        } else {
            node5 = simpleAssignment(lastChild, node9);
        }
        node10.addChildToBack(new Node((int) Token.EXPR_VOID, node5));
        node10.addChildToBack(node4);
        node5 = createLoop((Jump) node, LOOP_WHILE, node10, node8, null, null);
        node5.addChildToFront(node7);
        if (i2 == 123 || i2 == 154) {
            node5.addChildToFront(node2);
        }
        node6.addChildToBack(node5);
        return node6;
    }

    private Node createIf(Node node, Node node2, Node node3, int i) {
        int isAlwaysDefinedBoolean = isAlwaysDefinedBoolean(node);
        if (isAlwaysDefinedBoolean == LOOP_WHILE) {
            return node2;
        }
        if (isAlwaysDefinedBoolean == ALWAYS_FALSE_BOOLEAN) {
            return node3 != null ? node3 : new Node((int) Token.BLOCK, i);
        } else {
            Node node4 = new Node((int) Token.BLOCK, i);
            Node newTarget = Node.newTarget();
            Node jump = new Jump(7, node);
            jump.target = newTarget;
            node4.addChildToBack(jump);
            node4.addChildrenToBack(node2);
            if (node3 != null) {
                jump = Node.newTarget();
                node4.addChildToBack(makeJump(5, jump));
                node4.addChildToBack(newTarget);
                node4.addChildrenToBack(node3);
                node4.addChildToBack(jump);
            } else {
                node4.addChildToBack(newTarget);
            }
            return node4;
        }
    }

    private Node createIncDec(int i, boolean z, Node node) {
        Node makeReference = makeReference(node);
        switch (makeReference.getType()) {
            case Token.GETPROP /*33*/:
            case Token.GETELEM /*36*/:
            case Token.NAME /*39*/:
            case Token.GET_REF /*68*/:
                Node node2 = new Node(i, makeReference);
                int i2 = LOOP_DO_WHILE;
                if (i == Token.DEC) {
                    i2 = LOOP_WHILE;
                }
                if (z) {
                    i2 |= LOOP_FOR;
                }
                node2.putIntProp(13, i2);
                return node2;
            default:
                throw Kit.codeBug();
        }
    }

    private Node createLoop(Jump jump, int i, Node node, Node node2, Node node3, Node node4) {
        Node newTarget = Node.newTarget();
        Node newTarget2 = Node.newTarget();
        if (i == LOOP_FOR && node2.getType() == Token.EMPTY) {
            node2 = new Node(45);
        }
        Node jump2 = new Jump(6, node2);
        jump2.target = newTarget;
        Node newTarget3 = Node.newTarget();
        jump.addChildToBack(newTarget);
        jump.addChildrenToBack(node);
        if (i == LOOP_WHILE || i == LOOP_FOR) {
            jump.addChildrenToBack(new Node((int) Token.EMPTY, jump.getLineno()));
        }
        jump.addChildToBack(newTarget2);
        jump.addChildToBack(jump2);
        jump.addChildToBack(newTarget3);
        jump.target = newTarget3;
        if (i == LOOP_WHILE || i == LOOP_FOR) {
            jump.addChildToFront(makeJump(5, newTarget2));
            if (i == LOOP_FOR) {
                int type = node3.getType();
                if (type != Token.EMPTY) {
                    if (!(type == Token.VAR || type == Token.LET)) {
                        node3 = new Node((int) Token.EXPR_VOID, node3);
                    }
                    jump.addChildToFront(node3);
                }
                newTarget2 = Node.newTarget();
                jump.addChildAfter(newTarget2, node);
                if (node4.getType() != Token.EMPTY) {
                    jump.addChildAfter(new Node((int) Token.EXPR_VOID, node4), newTarget2);
                }
            }
        }
        jump.setContinue(newTarget2);
        return jump;
    }

    private Scope createLoopNode(Node node, int i) {
        Jump createScopeNode = createScopeNode(Token.LOOP, i);
        if (node != null) {
            ((Jump) node).setLoop(createScopeNode);
        }
        return createScopeNode;
    }

    private Node createMemberRefGet(Node node, String str, Node node2, int i) {
        Node node3 = str != null ? str.equals("*") ? new Node(42) : createName(str) : null;
        Node node4 = node == null ? str == null ? new Node(80, node2) : new Node(81, node3, node2) : str == null ? new Node(78, node, node2) : new Node(79, node, node3, node2);
        if (i != 0) {
            node4.putIntProp(16, i);
        }
        return new Node(68, node4);
    }

    private Node createPropertyGet(Node node, String str, String str2, int i) {
        if (str != null || i != 0) {
            return createMemberRefGet(node, str, Node.newString(str2), i | LOOP_WHILE);
        }
        if (node == null) {
            return createName(str2);
        }
        checkActivationName(str2, 33);
        if (!ScriptRuntime.isSpecialProperty(str2)) {
            return new Node(33, node, Node.newString(str2));
        }
        Node node2 = new Node(72, node);
        node2.putProp(17, str2);
        return new Node(68, node2);
    }

    private Node createString(String str) {
        return Node.newString(str);
    }

    private Node createTryCatchFinally(Node node, Node node2, Node node3, int i) {
        Object obj = (node3 == null || (node3.getType() == Token.BLOCK && !node3.hasChildren())) ? null : LOOP_WHILE;
        if (node.getType() == Token.BLOCK && !node.hasChildren() && obj == null) {
            return node;
        }
        boolean hasChildren = node2.hasChildren();
        if (obj == null && !hasChildren) {
            return node;
        }
        Node newTarget;
        Node node4 = new Node(Token.LOCAL_BLOCK);
        Node jump = new Jump(82, node, i);
        jump.putProp(3, node4);
        if (hasChildren) {
            Node newTarget2 = Node.newTarget();
            jump.addChildToBack(makeJump(5, newTarget2));
            newTarget = Node.newTarget();
            jump.target = newTarget;
            jump.addChildToBack(newTarget);
            Node node5 = new Node(Token.LOCAL_BLOCK);
            Object obj2 = null;
            Node firstChild = node2.getFirstChild();
            int i2 = LOOP_DO_WHILE;
            while (firstChild != null) {
                int lineno = firstChild.getLineno();
                Node firstChild2 = firstChild.getFirstChild();
                Node next = firstChild2.getNext();
                newTarget = next.getNext();
                firstChild.removeChild(firstChild2);
                firstChild.removeChild(next);
                firstChild.removeChild(newTarget);
                newTarget.addChildToBack(new Node(3));
                newTarget.addChildToBack(makeJump(5, newTarget2));
                if (next.getType() == Token.EMPTY) {
                    obj2 = LOOP_WHILE;
                } else {
                    newTarget = createIf(next, newTarget, null, lineno);
                }
                next = new Node(57, firstChild2, createUseLocal(node4));
                next.putProp(3, node5);
                next.putIntProp(14, i2);
                node5.addChildToBack(next);
                node5.addChildToBack(createWith(createUseLocal(node5), newTarget, lineno));
                firstChild = firstChild.getNext();
                i2 += LOOP_WHILE;
            }
            jump.addChildToBack(node5);
            if (obj2 == null) {
                newTarget = new Node(51);
                newTarget.putProp(3, node4);
                jump.addChildToBack(newTarget);
            }
            jump.addChildToBack(newTarget2);
        }
        if (obj != null) {
            Node newTarget3 = Node.newTarget();
            jump.setFinally(newTarget3);
            jump.addChildToBack(makeJump(Token.JSR, newTarget3));
            newTarget = Node.newTarget();
            jump.addChildToBack(makeJump(5, newTarget));
            jump.addChildToBack(newTarget3);
            newTarget3 = new Node((int) Token.FINALLY, node3);
            newTarget3.putProp(3, node4);
            jump.addChildToBack(newTarget3);
            jump.addChildToBack(newTarget);
        }
        node4.addChildToBack(jump);
        return node4;
    }

    private Node createUnary(int i, Node node) {
        int type = node.getType();
        switch (i) {
            case Token.NOT /*26*/:
                int isAlwaysDefinedBoolean = isAlwaysDefinedBoolean(node);
                if (isAlwaysDefinedBoolean != 0) {
                    isAlwaysDefinedBoolean = isAlwaysDefinedBoolean == LOOP_WHILE ? 44 : 45;
                    if (type != 45 && type != 44) {
                        return new Node(isAlwaysDefinedBoolean);
                    }
                    node.setType(isAlwaysDefinedBoolean);
                    return node;
                }
                break;
            case Token.BITNOT /*27*/:
                if (type == 40) {
                    node.setDouble((double) (ScriptRuntime.toInt32(node.getDouble()) ^ ALWAYS_FALSE_BOOLEAN));
                    return node;
                }
                break;
            case Token.NEG /*29*/:
                if (type == 40) {
                    node.setDouble(-node.getDouble());
                    return node;
                }
                break;
            case Token.DELPROP /*31*/:
                Node node2;
                if (type == 39) {
                    node.setType(49);
                    node2 = new Node(i, node, Node.newString(node.getString()));
                } else if (type == 33 || type == 36) {
                    r1 = node.getFirstChild();
                    Node lastChild = node.getLastChild();
                    node.removeChild(r1);
                    node.removeChild(lastChild);
                    node2 = new Node(i, r1, lastChild);
                } else if (type == 68) {
                    r1 = node.getFirstChild();
                    node.removeChild(r1);
                    node2 = new Node(70, r1);
                } else {
                    node2 = new Node(i, new Node(45), node);
                }
                return node2;
            case Token.TYPEOF /*32*/:
                if (type == 39) {
                    node.setType(Token.TYPEOFNAME);
                    return node;
                }
                break;
        }
        return new Node(i, node);
    }

    private Node createUseLocal(Node node) {
        if (Token.LOCAL_BLOCK != node.getType()) {
            throw Kit.codeBug();
        }
        Node node2 = new Node(54);
        node2.putProp(3, node);
        return node2;
    }

    private Node createWith(Node node, Node node2, int i) {
        setRequiresActivation();
        Node node3 = new Node((int) Token.BLOCK, i);
        node3.addChildToBack(new Node((int) LOOP_FOR, node));
        node3.addChildrenToBack(new Node((int) Token.WITH, node2, i));
        node3.addChildToBack(new Node(3));
        return node3;
    }

    private Node genExprTransformHelper(GeneratorExpression generatorExpression) {
        Throwable th;
        this.decompiler.addToken(88);
        int lineno = generatorExpression.getLineno();
        Node transform = transform(generatorExpression.getResult());
        List loops = generatorExpression.getLoops();
        int size = loops.size();
        Node[] nodeArr = new Node[size];
        Node[] nodeArr2 = new Node[size];
        for (int i = LOOP_DO_WHILE; i < size; i += LOOP_WHILE) {
            String string;
            GeneratorExpressionLoop generatorExpressionLoop = (GeneratorExpressionLoop) loops.get(i);
            this.decompiler.addName(" ");
            this.decompiler.addToken(Token.FOR);
            this.decompiler.addToken(88);
            Node iterator = generatorExpressionLoop.getIterator();
            if (iterator.getType() == 39) {
                string = iterator.getString();
                this.decompiler.addName(string);
            } else {
                decompile(iterator);
                string = this.currentScriptOrFn.getNextTempName();
                defineSymbol(88, string, false);
                transform = createBinary(90, createAssignment(91, iterator, createName(string)), transform);
            }
            iterator = createName(string);
            defineSymbol(Token.LET, string, false);
            nodeArr[i] = iterator;
            if (generatorExpressionLoop.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            nodeArr2[i] = transform(generatorExpressionLoop.getIteratedObject());
            this.decompiler.addToken(89);
        }
        Node node = new Node((int) Token.EXPR_VOID, new Node(73, transform, generatorExpression.getLineno()), lineno);
        if (generatorExpression.getFilter() != null) {
            this.decompiler.addName(" ");
            this.decompiler.addToken(Token.IF);
            this.decompiler.addToken(88);
            node = createIf(transform(generatorExpression.getFilter()), node, null, lineno);
            this.decompiler.addToken(89);
        }
        int i2 = size + ALWAYS_FALSE_BOOLEAN;
        Node node2 = node;
        int i3 = LOOP_DO_WHILE;
        while (i2 >= 0) {
            try {
                generatorExpressionLoop = (GeneratorExpressionLoop) loops.get(i2);
                transform = createLoopNode(null, generatorExpressionLoop.getLineno());
                pushScope(transform);
                int i4 = i3 + LOOP_WHILE;
                try {
                    node2 = createForIn(Token.LET, transform, nodeArr[i2], nodeArr2[i2], node2, generatorExpressionLoop.isForEach(), generatorExpressionLoop.isForOf());
                    i2 += ALWAYS_FALSE_BOOLEAN;
                    i3 = i4;
                } catch (Throwable th2) {
                    th = th2;
                    i3 = i4;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
        for (int i5 = LOOP_DO_WHILE; i5 < i3; i5 += LOOP_WHILE) {
            popScope();
        }
        this.decompiler.addToken(89);
        return node2;
        for (int i6 = LOOP_DO_WHILE; i6 < i3; i6 += LOOP_WHILE) {
            popScope();
        }
        throw th;
    }

    private Object getPropKey(Node node) {
        String identifier;
        if (node instanceof Name) {
            identifier = ((Name) node).getIdentifier();
            this.decompiler.addName(identifier);
            return ScriptRuntime.getIndexObject(identifier);
        } else if (node instanceof StringLiteral) {
            identifier = ((StringLiteral) node).getValue();
            this.decompiler.addString(identifier);
            return ScriptRuntime.getIndexObject(identifier);
        } else if (node instanceof NumberLiteral) {
            double number = ((NumberLiteral) node).getNumber();
            this.decompiler.addNumber(number);
            return ScriptRuntime.getIndexObject(number);
        } else {
            throw Kit.codeBug();
        }
    }

    private Node initFunction(FunctionNode functionNode, int i, Node node, int i2) {
        functionNode.setFunctionType(i2);
        functionNode.addChildToBack(node);
        if (functionNode.getFunctionCount() != 0) {
            functionNode.setRequiresActivation();
        }
        if (i2 == LOOP_FOR) {
            Name functionName = functionNode.getFunctionName();
            if (!(functionName == null || functionName.length() == 0 || functionNode.getSymbol(functionName.getIdentifier()) != null)) {
                functionNode.putSymbol(new Symbol(Token.FUNCTION, functionName.getIdentifier()));
                node.addChildrenToFront(new Node((int) Token.EXPR_VOID, new Node(8, Node.newString(49, functionName.getIdentifier()), new Node(64))));
            }
        }
        Node lastChild = node.getLastChild();
        if (lastChild == null || lastChild.getType() != 4) {
            node.addChildToBack(new Node(4));
        }
        lastChild = Node.newString(Token.FUNCTION, functionNode.getName());
        lastChild.putIntProp(LOOP_WHILE, i);
        return lastChild;
    }

    private static int isAlwaysDefinedBoolean(Node node) {
        switch (node.getType()) {
            case Token.NUMBER /*40*/:
                double d = node.getDouble();
                return (d != d || d == 0.0d) ? ALWAYS_FALSE_BOOLEAN : LOOP_WHILE;
            case Token.NULL /*42*/:
            case Token.FALSE /*44*/:
                return ALWAYS_FALSE_BOOLEAN;
            case Token.TRUE /*45*/:
                return LOOP_WHILE;
            default:
                return LOOP_DO_WHILE;
        }
    }

    private Jump makeJump(int i, Node node) {
        Jump jump = new Jump(i);
        jump.target = node;
        return jump;
    }

    private Node makeReference(Node node) {
        switch (node.getType()) {
            case Token.GETPROP /*33*/:
            case Token.GETELEM /*36*/:
            case Token.NAME /*39*/:
            case Token.GET_REF /*68*/:
                return node;
            case Token.CALL /*38*/:
                node.setType(71);
                return new Node(68, node);
            default:
                return null;
        }
    }

    private Node transformArrayComp(ArrayComprehension arrayComprehension) {
        int lineno = arrayComprehension.getLineno();
        Node createScopeNode = createScopeNode(Token.ARRAYCOMP, lineno);
        String nextTempName = this.currentScriptOrFn.getNextTempName();
        pushScope(createScopeNode);
        try {
            defineSymbol(Token.LET, nextTempName, false);
            Node node = new Node((int) Token.BLOCK, lineno);
            node.addChildToBack(new Node((int) Token.EXPR_VOID, createAssignment(91, createName(nextTempName), createCallOrNew(30, createName("Array"))), lineno));
            node.addChildToBack(arrayCompTransformHelper(arrayComprehension, nextTempName));
            createScopeNode.addChildToBack(node);
            createScopeNode.addChildToBack(createName(nextTempName));
            return createScopeNode;
        } finally {
            popScope();
        }
    }

    private Node transformArrayLiteral(ArrayLiteral arrayLiteral) {
        int i = LOOP_DO_WHILE;
        if (arrayLiteral.isDestructuring()) {
            return arrayLiteral;
        }
        this.decompiler.addToken(84);
        List elements = arrayLiteral.getElements();
        Node node = new Node(66);
        List list = null;
        for (int i2 = LOOP_DO_WHILE; i2 < elements.size(); i2 += LOOP_WHILE) {
            AstNode astNode = (AstNode) elements.get(i2);
            if (astNode.getType() != Token.EMPTY) {
                node.addChildToBack(transform(astNode));
            } else {
                List arrayList = list == null ? new ArrayList() : list;
                arrayList.add(Integer.valueOf(i2));
                list = arrayList;
            }
            if (i2 < elements.size() + ALWAYS_FALSE_BOOLEAN) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(85);
        node.putIntProp(21, arrayLiteral.getDestructuringLength());
        if (list != null) {
            Object obj = new int[list.size()];
            while (i < list.size()) {
                obj[i] = ((Integer) list.get(i)).intValue();
                i += LOOP_WHILE;
            }
            node.putProp(11, obj);
        }
        return node;
    }

    private Node transformAssignment(Assignment assignment) {
        Node removeParens = removeParens(assignment.getLeft());
        if (isDestructuring(removeParens)) {
            decompile(removeParens);
        } else {
            removeParens = transform(removeParens);
        }
        this.decompiler.addToken(assignment.getType());
        return createAssignment(assignment.getType(), removeParens, transform(assignment.getRight()));
    }

    private Node transformBlock(AstNode astNode) {
        if (astNode instanceof Scope) {
            pushScope((Scope) astNode);
        }
        try {
            List<Node> arrayList = new ArrayList();
            Iterator it = astNode.iterator();
            while (it.hasNext()) {
                arrayList.add(transform((AstNode) ((Node) it.next())));
            }
            astNode.removeChildren();
            for (Node addChildToBack : arrayList) {
                astNode.addChildToBack(addChildToBack);
            }
            return astNode;
        } finally {
            if (astNode instanceof Scope) {
                popScope();
            }
        }
    }

    private Node transformBreak(BreakStatement breakStatement) {
        this.decompiler.addToken(Token.BREAK);
        if (breakStatement.getBreakLabel() != null) {
            this.decompiler.addName(breakStatement.getBreakLabel().getIdentifier());
        }
        this.decompiler.addEOL(83);
        return breakStatement;
    }

    private Node transformCondExpr(ConditionalExpression conditionalExpression) {
        Node transform = transform(conditionalExpression.getTestExpression());
        this.decompiler.addToken(Token.HOOK);
        Node transform2 = transform(conditionalExpression.getTrueExpression());
        this.decompiler.addToken(Token.COLON);
        return createCondExpr(transform, transform2, transform(conditionalExpression.getFalseExpression()));
    }

    private Node transformContinue(ContinueStatement continueStatement) {
        this.decompiler.addToken(Token.CONTINUE);
        if (continueStatement.getLabel() != null) {
            this.decompiler.addName(continueStatement.getLabel().getIdentifier());
        }
        this.decompiler.addEOL(83);
        return continueStatement;
    }

    private Node transformDefaultXmlNamepace(UnaryExpression unaryExpression) {
        this.decompiler.addToken(Token.DEFAULT);
        this.decompiler.addName(" xml");
        this.decompiler.addName(" namespace");
        this.decompiler.addToken(91);
        return createUnary(75, transform(unaryExpression.getOperand()));
    }

    private Node transformDoLoop(DoLoop doLoop) {
        doLoop.setType(Token.LOOP);
        pushScope(doLoop);
        try {
            this.decompiler.addToken(Token.DO);
            this.decompiler.addEOL(86);
            Node transform = transform(doLoop.getBody());
            this.decompiler.addToken(87);
            this.decompiler.addToken(Token.WHILE);
            this.decompiler.addToken(88);
            Node transform2 = transform(doLoop.getCondition());
            this.decompiler.addToken(89);
            this.decompiler.addEOL(83);
            Node createLoop = createLoop(doLoop, LOOP_DO_WHILE, transform, transform2, null, null);
            return createLoop;
        } finally {
            popScope();
        }
    }

    private Node transformElementGet(ElementGet elementGet) {
        Node transform = transform(elementGet.getTarget());
        this.decompiler.addToken(84);
        Node transform2 = transform(elementGet.getElement());
        this.decompiler.addToken(85);
        return new Node(36, transform, transform2);
    }

    private Node transformExprStmt(ExpressionStatement expressionStatement) {
        Node transform = transform(expressionStatement.getExpression());
        this.decompiler.addEOL(83);
        return new Node(expressionStatement.getType(), transform, expressionStatement.getLineno());
    }

    private Node transformForInLoop(ForInLoop forInLoop) {
        this.decompiler.addToken(Token.FOR);
        if (forInLoop.isForEach()) {
            this.decompiler.addName("each ");
        }
        this.decompiler.addToken(88);
        forInLoop.setType(Token.LOOP);
        pushScope(forInLoop);
        int i = ALWAYS_FALSE_BOOLEAN;
        try {
            AstNode iterator = forInLoop.getIterator();
            if (iterator instanceof VariableDeclaration) {
                i = ((VariableDeclaration) iterator).getType();
            }
            Node transform = transform(iterator);
            if (forInLoop.isForOf()) {
                this.decompiler.addName("of ");
            } else {
                this.decompiler.addToken(52);
            }
            Node transform2 = transform(forInLoop.getIteratedObject());
            this.decompiler.addToken(89);
            this.decompiler.addEOL(86);
            Node transform3 = transform(forInLoop.getBody());
            this.decompiler.addEOL(87);
            Node createForIn = createForIn(i, forInLoop, transform, transform2, transform3, forInLoop.isForEach(), forInLoop.isForOf());
            return createForIn;
        } finally {
            popScope();
        }
    }

    private Node transformForLoop(ForLoop forLoop) {
        this.decompiler.addToken(Token.FOR);
        this.decompiler.addToken(88);
        forLoop.setType(Token.LOOP);
        Scope scope = this.currentScope;
        this.currentScope = forLoop;
        try {
            Node transform = transform(forLoop.getInitializer());
            this.decompiler.addToken(83);
            Node transform2 = transform(forLoop.getCondition());
            this.decompiler.addToken(83);
            Node transform3 = transform(forLoop.getIncrement());
            this.decompiler.addToken(89);
            this.decompiler.addEOL(86);
            Node transform4 = transform(forLoop.getBody());
            this.decompiler.addEOL(87);
            Node createFor = createFor(forLoop, transform, transform2, transform3, transform4);
            return createFor;
        } finally {
            this.currentScope = scope;
        }
    }

    private Node transformFunction(FunctionNode functionNode) {
        int functionType = functionNode.getFunctionType();
        int markFunctionStart = this.decompiler.markFunctionStart(functionType);
        Node decompileFunctionHeader = decompileFunctionHeader(functionNode);
        int addFunction = this.currentScriptOrFn.addFunction(functionNode);
        PerFunctionVariables perFunctionVariables = new PerFunctionVariables(functionNode);
        try {
            Node node = (Node) functionNode.getProp(23);
            functionNode.removeProp(23);
            int lineno = functionNode.getBody().getLineno();
            this.nestingOfFunction += LOOP_WHILE;
            Node transform = transform(functionNode.getBody());
            if (!functionNode.isExpressionClosure()) {
                this.decompiler.addToken(87);
            }
            functionNode.setEncodedSourceBounds(markFunctionStart, this.decompiler.markFunctionEnd(markFunctionStart));
            if (!(functionType == LOOP_FOR || functionNode.isExpressionClosure())) {
                this.decompiler.addToken(LOOP_WHILE);
            }
            if (node != null) {
                transform.addChildToFront(new Node((int) Token.EXPR_VOID, node, lineno));
            }
            functionType = functionNode.getFunctionType();
            node = initFunction(functionNode, addFunction, transform, functionType);
            if (decompileFunctionHeader != null) {
                node = createAssignment(91, decompileFunctionHeader, node);
                if (functionType != LOOP_FOR) {
                    node = createExprStatementNoReturn(node, functionNode.getLineno());
                }
            }
            this.nestingOfFunction += ALWAYS_FALSE_BOOLEAN;
            perFunctionVariables.restore();
            return node;
        } catch (Throwable th) {
            this.nestingOfFunction += ALWAYS_FALSE_BOOLEAN;
            perFunctionVariables.restore();
        }
    }

    private Node transformFunctionCall(FunctionCall functionCall) {
        Node createCallOrNew = createCallOrNew(38, transform(functionCall.getTarget()));
        createCallOrNew.setLineno(functionCall.getLineno());
        this.decompiler.addToken(88);
        List arguments = functionCall.getArguments();
        for (int i = LOOP_DO_WHILE; i < arguments.size(); i += LOOP_WHILE) {
            createCallOrNew.addChildToBack(transform((AstNode) arguments.get(i)));
            if (i < arguments.size() + ALWAYS_FALSE_BOOLEAN) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(89);
        return createCallOrNew;
    }

    private Node transformGenExpr(GeneratorExpression generatorExpression) {
        FunctionNode functionNode = new FunctionNode();
        functionNode.setSourceName(this.currentScriptOrFn.getNextTempName());
        functionNode.setIsGenerator();
        functionNode.setFunctionType(LOOP_FOR);
        functionNode.setRequiresActivation();
        int functionType = functionNode.getFunctionType();
        int markFunctionStart = this.decompiler.markFunctionStart(functionType);
        Node decompileFunctionHeader = decompileFunctionHeader(functionNode);
        int addFunction = this.currentScriptOrFn.addFunction(functionNode);
        PerFunctionVariables perFunctionVariables = new PerFunctionVariables(functionNode);
        try {
            Node node = (Node) functionNode.getProp(23);
            functionNode.removeProp(23);
            int i = generatorExpression.lineno;
            this.nestingOfFunction += LOOP_WHILE;
            Node genExprTransformHelper = genExprTransformHelper(generatorExpression);
            if (!functionNode.isExpressionClosure()) {
                this.decompiler.addToken(87);
            }
            functionNode.setEncodedSourceBounds(markFunctionStart, this.decompiler.markFunctionEnd(markFunctionStart));
            if (!(functionType == LOOP_FOR || functionNode.isExpressionClosure())) {
                this.decompiler.addToken(LOOP_WHILE);
            }
            if (node != null) {
                genExprTransformHelper.addChildToFront(new Node((int) Token.EXPR_VOID, node, i));
            }
            functionType = functionNode.getFunctionType();
            node = initFunction(functionNode, addFunction, genExprTransformHelper, functionType);
            if (decompileFunctionHeader != null) {
                node = createAssignment(91, decompileFunctionHeader, node);
                if (functionType != LOOP_FOR) {
                    node = createExprStatementNoReturn(node, functionNode.getLineno());
                }
            }
            this.nestingOfFunction += ALWAYS_FALSE_BOOLEAN;
            perFunctionVariables.restore();
            node = createCallOrNew(38, node);
            node.setLineno(generatorExpression.getLineno());
            this.decompiler.addToken(88);
            this.decompiler.addToken(89);
            return node;
        } catch (Throwable th) {
            this.nestingOfFunction += ALWAYS_FALSE_BOOLEAN;
            perFunctionVariables.restore();
        }
    }

    private Node transformIf(IfStatement ifStatement) {
        this.decompiler.addToken(Token.IF);
        this.decompiler.addToken(88);
        Node transform = transform(ifStatement.getCondition());
        this.decompiler.addToken(89);
        this.decompiler.addEOL(86);
        Node transform2 = transform(ifStatement.getThenPart());
        Node node = null;
        if (ifStatement.getElsePart() != null) {
            this.decompiler.addToken(87);
            this.decompiler.addToken(Token.ELSE);
            this.decompiler.addEOL(86);
            node = transform(ifStatement.getElsePart());
        }
        this.decompiler.addEOL(87);
        return createIf(transform, transform2, node, ifStatement.getLineno());
    }

    private Node transformInfix(InfixExpression infixExpression) {
        Node transform = transform(infixExpression.getLeft());
        this.decompiler.addToken(infixExpression.getType());
        Node transform2 = transform(infixExpression.getRight());
        if (infixExpression instanceof XmlDotQuery) {
            this.decompiler.addToken(89);
        }
        return createBinary(infixExpression.getType(), transform, transform2);
    }

    private Node transformLabeledStatement(LabeledStatement labeledStatement) {
        Node firstLabel = labeledStatement.getFirstLabel();
        List labels = labeledStatement.getLabels();
        this.decompiler.addName(firstLabel.getName());
        if (labels.size() > LOOP_WHILE) {
            for (Label label : labels.subList(LOOP_WHILE, labels.size())) {
                this.decompiler.addEOL(Token.COLON);
                this.decompiler.addName(label.getName());
            }
        }
        if (labeledStatement.getStatement().getType() == Token.BLOCK) {
            this.decompiler.addToken(67);
            this.decompiler.addEOL(86);
        } else {
            this.decompiler.addEOL(Token.COLON);
        }
        Node transform = transform(labeledStatement.getStatement());
        if (labeledStatement.getStatement().getType() == Token.BLOCK) {
            this.decompiler.addEOL(87);
        }
        Node newTarget = Node.newTarget();
        Node node = new Node((int) Token.BLOCK, firstLabel, transform, newTarget);
        firstLabel.target = newTarget;
        return node;
    }

    private Node transformLetNode(LetNode letNode) {
        pushScope(letNode);
        try {
            this.decompiler.addToken(Token.LET);
            this.decompiler.addToken(88);
            Node transformVariableInitializers = transformVariableInitializers(letNode.getVariables());
            this.decompiler.addToken(89);
            letNode.addChildToBack(transformVariableInitializers);
            Object obj = letNode.getType() == Token.LETEXPR ? LOOP_WHILE : null;
            if (letNode.getBody() != null) {
                if (obj != null) {
                    this.decompiler.addName(" ");
                } else {
                    this.decompiler.addEOL(86);
                }
                letNode.addChildToBack(transform(letNode.getBody()));
                if (obj == null) {
                    this.decompiler.addEOL(87);
                }
            }
            popScope();
            return letNode;
        } catch (Throwable th) {
            popScope();
        }
    }

    private Node transformLiteral(AstNode astNode) {
        this.decompiler.addToken(astNode.getType());
        return astNode;
    }

    private Node transformName(Name name) {
        this.decompiler.addName(name.getIdentifier());
        return name;
    }

    private Node transformNewExpr(NewExpression newExpression) {
        this.decompiler.addToken(30);
        Node createCallOrNew = createCallOrNew(30, transform(newExpression.getTarget()));
        createCallOrNew.setLineno(newExpression.getLineno());
        List arguments = newExpression.getArguments();
        this.decompiler.addToken(88);
        for (int i = LOOP_DO_WHILE; i < arguments.size(); i += LOOP_WHILE) {
            createCallOrNew.addChildToBack(transform((AstNode) arguments.get(i)));
            if (i < arguments.size() + ALWAYS_FALSE_BOOLEAN) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(89);
        if (newExpression.getInitializer() != null) {
            createCallOrNew.addChildToBack(transformObjectLiteral(newExpression.getInitializer()));
        }
        return createCallOrNew;
    }

    private Node transformNumber(NumberLiteral numberLiteral) {
        this.decompiler.addNumber(numberLiteral.getNumber());
        return numberLiteral;
    }

    private Node transformObjectLiteral(ObjectLiteral objectLiteral) {
        Node node;
        if (!objectLiteral.isDestructuring()) {
            Object obj;
            this.decompiler.addToken(86);
            List<ObjectProperty> elements = objectLiteral.getElements();
            node = new Node(67);
            if (elements.isEmpty()) {
                obj = ScriptRuntime.emptyArgs;
            } else {
                int size = elements.size();
                Object obj2 = new Object[size];
                int i = LOOP_DO_WHILE;
                for (ObjectProperty objectProperty : elements) {
                    if (objectProperty.isGetterMethod()) {
                        this.decompiler.addToken(Token.GET);
                    } else if (objectProperty.isSetterMethod()) {
                        this.decompiler.addToken(Token.SET);
                    } else if (objectProperty.isNormalMethod()) {
                        this.decompiler.addToken(Token.METHOD);
                    }
                    int i2 = i + LOOP_WHILE;
                    obj2[i] = getPropKey(objectProperty.getLeft());
                    if (!objectProperty.isMethod()) {
                        this.decompiler.addToken(67);
                    }
                    Node transform = transform(objectProperty.getRight());
                    Node createUnary = objectProperty.isGetterMethod() ? createUnary(Token.GET, transform) : objectProperty.isSetterMethod() ? createUnary(Token.SET, transform) : objectProperty.isNormalMethod() ? createUnary(Token.METHOD, transform) : transform;
                    node.addChildToBack(createUnary);
                    if (i2 < size) {
                        this.decompiler.addToken(90);
                    }
                    i = i2;
                }
                obj = obj2;
            }
            this.decompiler.addToken(87);
            node.putProp(12, obj);
        }
        return node;
    }

    private Node transformParenExpr(ParenthesizedExpression parenthesizedExpression) {
        AstNode expression = parenthesizedExpression.getExpression();
        this.decompiler.addToken(88);
        AstNode astNode = expression;
        int i = LOOP_WHILE;
        while (astNode instanceof ParenthesizedExpression) {
            this.decompiler.addToken(88);
            i += LOOP_WHILE;
            astNode = ((ParenthesizedExpression) astNode).getExpression();
        }
        Node transform = transform(astNode);
        for (int i2 = LOOP_DO_WHILE; i2 < i; i2 += LOOP_WHILE) {
            this.decompiler.addToken(89);
        }
        transform.putProp(19, Boolean.TRUE);
        return transform;
    }

    private Node transformPropertyGet(PropertyGet propertyGet) {
        Node transform = transform(propertyGet.getTarget());
        String identifier = propertyGet.getProperty().getIdentifier();
        this.decompiler.addToken(Token.DOT);
        this.decompiler.addName(identifier);
        return createPropertyGet(transform, null, identifier, LOOP_DO_WHILE);
    }

    private Node transformRegExp(RegExpLiteral regExpLiteral) {
        this.decompiler.addRegexp(regExpLiteral.getValue(), regExpLiteral.getFlags());
        this.currentScriptOrFn.addRegExp(regExpLiteral);
        return regExpLiteral;
    }

    private Node transformReturn(ReturnStatement returnStatement) {
        boolean equals = Boolean.TRUE.equals(returnStatement.getProp(25));
        boolean equals2 = Boolean.TRUE.equals(returnStatement.getProp(27));
        if (!equals) {
            this.decompiler.addToken(4);
        } else if (!equals2) {
            this.decompiler.addName(" ");
        }
        AstNode returnValue = returnStatement.getReturnValue();
        Node transform = returnValue == null ? null : transform(returnValue);
        if (!equals) {
            this.decompiler.addEOL(83);
        }
        return returnValue == null ? new Node(4, returnStatement.getLineno()) : new Node(4, transform, returnStatement.getLineno());
    }

    private Node transformScript(ScriptNode scriptNode) {
        this.decompiler.addToken(Token.SCRIPT);
        if (this.currentScope != null) {
            Kit.codeBug();
        }
        this.currentScope = scriptNode;
        Node node = new Node(Token.BLOCK);
        Iterator it = scriptNode.iterator();
        while (it.hasNext()) {
            node.addChildToBack(transform((AstNode) ((Node) it.next())));
        }
        scriptNode.removeChildren();
        Node firstChild = node.getFirstChild();
        if (firstChild != null) {
            scriptNode.addChildrenToBack(firstChild);
        }
        return scriptNode;
    }

    private Node transformString(StringLiteral stringLiteral) {
        this.decompiler.addString(stringLiteral.getValue());
        return Node.newString(stringLiteral.getValue());
    }

    private Node transformSwitch(SwitchStatement switchStatement) {
        this.decompiler.addToken(Token.SWITCH);
        this.decompiler.addToken(88);
        Node transform = transform(switchStatement.getExpression());
        this.decompiler.addToken(89);
        switchStatement.addChildToBack(transform);
        Node node = new Node((int) Token.BLOCK, (Node) switchStatement, switchStatement.getLineno());
        this.decompiler.addEOL(86);
        for (SwitchCase switchCase : switchStatement.getCases()) {
            AstNode expression = switchCase.getExpression();
            Node node2 = null;
            if (expression != null) {
                this.decompiler.addToken(Token.CASE);
                node2 = transform(expression);
            } else {
                this.decompiler.addToken(Token.DEFAULT);
            }
            this.decompiler.addEOL(Token.COLON);
            List<AstNode> statements = switchCase.getStatements();
            Node block = new Block();
            if (statements != null) {
                for (AstNode transform2 : statements) {
                    block.addChildToBack(transform(transform2));
                }
            }
            addSwitchCase(node, node2, block);
        }
        this.decompiler.addEOL(87);
        closeSwitch(node);
        return node;
    }

    private Node transformThrow(ThrowStatement throwStatement) {
        this.decompiler.addToken(50);
        Node transform = transform(throwStatement.getExpression());
        this.decompiler.addEOL(83);
        return new Node(50, transform, throwStatement.getLineno());
    }

    private Node transformTry(TryStatement tryStatement) {
        this.decompiler.addToken(82);
        this.decompiler.addEOL(86);
        Node transform = transform(tryStatement.getTryBlock());
        this.decompiler.addEOL(87);
        Node block = new Block();
        for (CatchClause catchClause : tryStatement.getCatchClauses()) {
            Node transform2;
            this.decompiler.addToken(Token.CATCH);
            this.decompiler.addToken(88);
            String identifier = catchClause.getVarName().getIdentifier();
            this.decompiler.addName(identifier);
            AstNode catchCondition = catchClause.getCatchCondition();
            if (catchCondition != null) {
                this.decompiler.addName(" ");
                this.decompiler.addToken(Token.IF);
                transform2 = transform(catchCondition);
            } else {
                transform2 = new EmptyExpression();
            }
            this.decompiler.addToken(89);
            this.decompiler.addEOL(86);
            Node transform3 = transform(catchClause.getBody());
            this.decompiler.addEOL(87);
            block.addChildToBack(createCatch(identifier, transform2, transform3, catchClause.getLineno()));
        }
        Node node = null;
        if (tryStatement.getFinallyBlock() != null) {
            this.decompiler.addToken(Token.FINALLY);
            this.decompiler.addEOL(86);
            node = transform(tryStatement.getFinallyBlock());
            this.decompiler.addEOL(87);
        }
        return createTryCatchFinally(transform, block, node, tryStatement.getLineno());
    }

    private Node transformUnary(UnaryExpression unaryExpression) {
        int type = unaryExpression.getType();
        if (type == 75) {
            return transformDefaultXmlNamepace(unaryExpression);
        }
        if (unaryExpression.isPrefix()) {
            this.decompiler.addToken(type);
        }
        Node transform = transform(unaryExpression.getOperand());
        if (unaryExpression.isPostfix()) {
            this.decompiler.addToken(type);
        }
        return (type == Token.INC || type == Token.DEC) ? createIncDec(type, unaryExpression.isPostfix(), transform) : createUnary(type, transform);
    }

    private Node transformVariableInitializers(VariableDeclaration variableDeclaration) {
        List<VariableInitializer> variables = variableDeclaration.getVariables();
        int size = variables.size();
        int i = LOOP_DO_WHILE;
        for (VariableInitializer variableInitializer : variables) {
            Node node;
            Node target = variableInitializer.getTarget();
            AstNode initializer = variableInitializer.getInitializer();
            if (variableInitializer.isDestructuring()) {
                decompile(target);
                node = target;
            } else {
                node = transform(target);
            }
            target = null;
            if (initializer != null) {
                this.decompiler.addToken(91);
                target = transform(initializer);
            }
            if (!variableInitializer.isDestructuring()) {
                if (target != null) {
                    node.addChildToBack(target);
                }
                variableDeclaration.addChildToBack(node);
            } else if (target == null) {
                variableDeclaration.addChildToBack(node);
            } else {
                variableDeclaration.addChildToBack(createDestructuringAssignment(variableDeclaration.getType(), node, target));
            }
            int i2 = i + LOOP_WHILE;
            if (i < size + ALWAYS_FALSE_BOOLEAN) {
                this.decompiler.addToken(90);
            }
            i = i2;
        }
        return variableDeclaration;
    }

    private Node transformVariables(VariableDeclaration variableDeclaration) {
        this.decompiler.addToken(variableDeclaration.getType());
        transformVariableInitializers(variableDeclaration);
        AstNode parent = variableDeclaration.getParent();
        if (!((parent instanceof Loop) || (parent instanceof LetNode))) {
            this.decompiler.addEOL(83);
        }
        return variableDeclaration;
    }

    private Node transformWhileLoop(WhileLoop whileLoop) {
        this.decompiler.addToken(Token.WHILE);
        whileLoop.setType(Token.LOOP);
        pushScope(whileLoop);
        try {
            this.decompiler.addToken(88);
            Node transform = transform(whileLoop.getCondition());
            this.decompiler.addToken(89);
            this.decompiler.addEOL(86);
            Node transform2 = transform(whileLoop.getBody());
            this.decompiler.addEOL(87);
            Node createLoop = createLoop(whileLoop, LOOP_WHILE, transform2, transform, null, null);
            return createLoop;
        } finally {
            popScope();
        }
    }

    private Node transformWith(WithStatement withStatement) {
        this.decompiler.addToken(Token.WITH);
        this.decompiler.addToken(88);
        Node transform = transform(withStatement.getExpression());
        this.decompiler.addToken(89);
        this.decompiler.addEOL(86);
        Node transform2 = transform(withStatement.getStatement());
        this.decompiler.addEOL(87);
        return createWith(transform, transform2, withStatement.getLineno());
    }

    private Node transformXmlLiteral(XmlLiteral xmlLiteral) {
        Node node = new Node(30, xmlLiteral.getLineno());
        List<XmlFragment> fragments = xmlLiteral.getFragments();
        node.addChildToBack(createName(((XmlString) fragments.get(LOOP_DO_WHILE)).getXml().trim().startsWith("<>") ? "XMLList" : "XML"));
        Node node2 = null;
        for (XmlFragment xmlFragment : fragments) {
            Node createString;
            if (xmlFragment instanceof XmlString) {
                String xml = ((XmlString) xmlFragment).getXml();
                this.decompiler.addName(xml);
                createString = node2 == null ? createString(xml) : createBinary(21, node2, createString(xml));
            } else {
                XmlExpression xmlExpression = (XmlExpression) xmlFragment;
                boolean isXmlAttribute = xmlExpression.isXmlAttribute();
                this.decompiler.addToken(86);
                createString = xmlExpression.getExpression() instanceof EmptyExpression ? createString(BuildConfig.FLAVOR) : transform(xmlExpression.getExpression());
                this.decompiler.addToken(87);
                createString = createBinary(21, node2, isXmlAttribute ? createBinary(21, createBinary(21, createString("\""), createUnary(76, createString)), createString("\"")) : createUnary(77, createString));
            }
            node2 = createString;
        }
        node.addChildToBack(node2);
        return node;
    }

    private Node transformXmlMemberGet(XmlMemberGet xmlMemberGet) {
        XmlRef memberRef = xmlMemberGet.getMemberRef();
        Node transform = transform(xmlMemberGet.getLeft());
        int i = memberRef.isAttributeAccess() ? LOOP_FOR : LOOP_DO_WHILE;
        if (xmlMemberGet.getType() == Token.DOTDOT) {
            i |= 4;
            this.decompiler.addToken(Token.DOTDOT);
        } else {
            this.decompiler.addToken(Token.DOT);
        }
        return transformXmlRef(transform, memberRef, i);
    }

    private Node transformXmlRef(Node node, XmlRef xmlRef, int i) {
        if ((i & LOOP_FOR) != 0) {
            this.decompiler.addToken(Token.XMLATTR);
        }
        Name namespace = xmlRef.getNamespace();
        String identifier = namespace != null ? namespace.getIdentifier() : null;
        if (identifier != null) {
            this.decompiler.addName(identifier);
            this.decompiler.addToken(Token.COLONCOLON);
        }
        if (xmlRef instanceof XmlPropRef) {
            String identifier2 = ((XmlPropRef) xmlRef).getPropName().getIdentifier();
            this.decompiler.addName(identifier2);
            return createPropertyGet(node, identifier, identifier2, i);
        }
        this.decompiler.addToken(84);
        Node transform = transform(((XmlElemRef) xmlRef).getExpression());
        this.decompiler.addToken(85);
        return createElementGet(node, identifier, transform, i);
    }

    private Node transformXmlRef(XmlRef xmlRef) {
        return transformXmlRef(null, xmlRef, xmlRef.isAttributeAccess() ? LOOP_FOR : LOOP_DO_WHILE);
    }

    private Node transformYield(Yield yield) {
        this.decompiler.addToken(73);
        Node transform = yield.getValue() == null ? null : transform(yield.getValue());
        return transform != null ? new Node(73, transform, yield.getLineno()) : new Node(73, yield.getLineno());
    }

    void decompile(AstNode astNode) {
        switch (astNode.getType()) {
            case Token.GETPROP /*33*/:
                decompilePropertyGet((PropertyGet) astNode);
                return;
            case Token.GETELEM /*36*/:
                decompileElementGet((ElementGet) astNode);
                return;
            case Token.NAME /*39*/:
                this.decompiler.addName(((Name) astNode).getIdentifier());
                return;
            case Token.NUMBER /*40*/:
                this.decompiler.addNumber(((NumberLiteral) astNode).getNumber());
                return;
            case Token.STRING /*41*/:
                this.decompiler.addString(((StringLiteral) astNode).getValue());
                return;
            case Token.THIS /*43*/:
                this.decompiler.addToken(astNode.getType());
                return;
            case Token.ARRAYLIT /*66*/:
                decompileArrayLiteral((ArrayLiteral) astNode);
                return;
            case Token.OBJECTLIT /*67*/:
                decompileObjectLiteral((ObjectLiteral) astNode);
                return;
            case Token.EMPTY /*129*/:
                return;
            default:
                Kit.codeBug("unexpected token: " + Token.typeToName(astNode.getType()));
                return;
        }
    }

    void decompileArrayLiteral(ArrayLiteral arrayLiteral) {
        this.decompiler.addToken(84);
        List elements = arrayLiteral.getElements();
        int size = elements.size();
        for (int i = LOOP_DO_WHILE; i < size; i += LOOP_WHILE) {
            decompile((AstNode) elements.get(i));
            if (i < size + ALWAYS_FALSE_BOOLEAN) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(85);
    }

    void decompileElementGet(ElementGet elementGet) {
        decompile(elementGet.getTarget());
        this.decompiler.addToken(84);
        decompile(elementGet.getElement());
        this.decompiler.addToken(85);
    }

    Node decompileFunctionHeader(FunctionNode functionNode) {
        Node node;
        int i = LOOP_WHILE;
        int i2 = LOOP_DO_WHILE;
        if (functionNode.getFunctionName() != null) {
            this.decompiler.addName(functionNode.getName());
            node = null;
        } else {
            node = functionNode.getMemberExprNode() != null ? transform(functionNode.getMemberExprNode()) : null;
        }
        int i3 = functionNode.getFunctionType() == 4 ? LOOP_WHILE : LOOP_DO_WHILE;
        if (i3 == 0 || functionNode.getLp() != ALWAYS_FALSE_BOOLEAN) {
            i = LOOP_DO_WHILE;
        }
        if (i == 0) {
            this.decompiler.addToken(88);
        }
        List params = functionNode.getParams();
        while (i2 < params.size()) {
            decompile((AstNode) params.get(i2));
            if (i2 < params.size() + ALWAYS_FALSE_BOOLEAN) {
                this.decompiler.addToken(90);
            }
            i2 += LOOP_WHILE;
        }
        if (i == 0) {
            this.decompiler.addToken(89);
        }
        if (i3 != 0) {
            this.decompiler.addToken(Token.ARROW);
        }
        if (!functionNode.isExpressionClosure()) {
            this.decompiler.addEOL(86);
        }
        return node;
    }

    void decompileObjectLiteral(ObjectLiteral objectLiteral) {
        this.decompiler.addToken(86);
        List elements = objectLiteral.getElements();
        int size = elements.size();
        for (int i = LOOP_DO_WHILE; i < size; i += LOOP_WHILE) {
            ObjectProperty objectProperty = (ObjectProperty) elements.get(i);
            boolean equals = Boolean.TRUE.equals(objectProperty.getProp(26));
            decompile(objectProperty.getLeft());
            if (!equals) {
                this.decompiler.addToken(Token.COLON);
                decompile(objectProperty.getRight());
            }
            if (i < size + ALWAYS_FALSE_BOOLEAN) {
                this.decompiler.addToken(90);
            }
        }
        this.decompiler.addToken(87);
    }

    void decompilePropertyGet(PropertyGet propertyGet) {
        decompile(propertyGet.getTarget());
        this.decompiler.addToken(Token.DOT);
        decompile(propertyGet.getProperty());
    }

    boolean isDestructuring(Node node) {
        return (node instanceof DestructuringForm) && ((DestructuringForm) node).isDestructuring();
    }

    public Node transform(AstNode astNode) {
        switch (astNode.getType()) {
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                return transformReturn((ReturnStatement) astNode);
            case Token.NEW /*30*/:
                return transformNewExpr((NewExpression) astNode);
            case Token.GETPROP /*33*/:
                return transformPropertyGet((PropertyGet) astNode);
            case Token.GETELEM /*36*/:
                return transformElementGet((ElementGet) astNode);
            case Token.CALL /*38*/:
                return transformFunctionCall((FunctionCall) astNode);
            case Token.NAME /*39*/:
                return transformName((Name) astNode);
            case Token.NUMBER /*40*/:
                return transformNumber((NumberLiteral) astNode);
            case Token.STRING /*41*/:
                return transformString((StringLiteral) astNode);
            case Token.NULL /*42*/:
            case Token.THIS /*43*/:
            case Token.FALSE /*44*/:
            case Token.TRUE /*45*/:
            case Token.DEBUGGER /*161*/:
                return transformLiteral(astNode);
            case Token.REGEXP /*48*/:
                return transformRegExp((RegExpLiteral) astNode);
            case Token.THROW /*50*/:
                return transformThrow((ThrowStatement) astNode);
            case Token.ARRAYLIT /*66*/:
                return transformArrayLiteral((ArrayLiteral) astNode);
            case Token.OBJECTLIT /*67*/:
                return transformObjectLiteral((ObjectLiteral) astNode);
            case Token.YIELD /*73*/:
                return transformYield((Yield) astNode);
            case Token.TRY /*82*/:
                return transformTry((TryStatement) astNode);
            case Token.HOOK /*103*/:
                return transformCondExpr((ConditionalExpression) astNode);
            case Token.FUNCTION /*110*/:
                return transformFunction((FunctionNode) astNode);
            case Token.IF /*113*/:
                return transformIf((IfStatement) astNode);
            case Token.SWITCH /*115*/:
                return transformSwitch((SwitchStatement) astNode);
            case Token.WHILE /*118*/:
                return transformWhileLoop((WhileLoop) astNode);
            case Token.DO /*119*/:
                return transformDoLoop((DoLoop) astNode);
            case Token.FOR /*120*/:
                return astNode instanceof ForInLoop ? transformForInLoop((ForInLoop) astNode) : transformForLoop((ForLoop) astNode);
            case Token.BREAK /*121*/:
                return transformBreak((BreakStatement) astNode);
            case Token.CONTINUE /*122*/:
                return transformContinue((ContinueStatement) astNode);
            case Token.WITH /*124*/:
                return transformWith((WithStatement) astNode);
            case Token.EMPTY /*129*/:
                return astNode;
            case Token.BLOCK /*130*/:
                return transformBlock(astNode);
            case Token.SCRIPT /*137*/:
                return transformScript((ScriptNode) astNode);
            case Token.ARRAYCOMP /*158*/:
                return transformArrayComp((ArrayComprehension) astNode);
            case Token.GENEXPR /*163*/:
                return transformGenExpr((GeneratorExpression) astNode);
            default:
                if (astNode instanceof ExpressionStatement) {
                    return transformExprStmt((ExpressionStatement) astNode);
                }
                if (astNode instanceof Assignment) {
                    return transformAssignment((Assignment) astNode);
                }
                if (astNode instanceof UnaryExpression) {
                    return transformUnary((UnaryExpression) astNode);
                }
                if (astNode instanceof XmlMemberGet) {
                    return transformXmlMemberGet((XmlMemberGet) astNode);
                }
                if (astNode instanceof InfixExpression) {
                    return transformInfix((InfixExpression) astNode);
                }
                if (astNode instanceof VariableDeclaration) {
                    return transformVariables((VariableDeclaration) astNode);
                }
                if (astNode instanceof ParenthesizedExpression) {
                    return transformParenExpr((ParenthesizedExpression) astNode);
                }
                if (astNode instanceof LabeledStatement) {
                    return transformLabeledStatement((LabeledStatement) astNode);
                }
                if (astNode instanceof LetNode) {
                    return transformLetNode((LetNode) astNode);
                }
                if (astNode instanceof XmlRef) {
                    return transformXmlRef((XmlRef) astNode);
                }
                if (astNode instanceof XmlLiteral) {
                    return transformXmlLiteral((XmlLiteral) astNode);
                }
                throw new IllegalArgumentException("Can't transform: " + astNode);
        }
    }

    public ScriptNode transformTree(AstRoot astRoot) {
        this.currentScriptOrFn = astRoot;
        this.inUseStrictDirective = astRoot.isInStrictMode();
        ScriptNode scriptNode = (ScriptNode) transform(astRoot);
        scriptNode.setEncodedSourceBounds(this.decompiler.getCurrentOffset(), this.decompiler.getCurrentOffset());
        if (this.compilerEnv.isGeneratingSource()) {
            scriptNode.setEncodedSource(this.decompiler.getEncodedSource());
        }
        this.decompiler = null;
        return scriptNode;
    }
}
