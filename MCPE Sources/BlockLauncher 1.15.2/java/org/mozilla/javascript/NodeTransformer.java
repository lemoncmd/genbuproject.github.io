package org.mozilla.javascript;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Scope;
import org.mozilla.javascript.ast.ScriptNode;

public class NodeTransformer {
    private boolean hasFinally;
    private ObjArray loopEnds;
    private ObjArray loops;

    private static Node addBeforeCurrent(Node node, Node node2, Node node3, Node node4) {
        if (node2 == null) {
            if (node3 != node.getFirstChild()) {
                Kit.codeBug();
            }
            node.addChildToFront(node4);
        } else {
            if (node3 != node2.getNext()) {
                Kit.codeBug();
            }
            node.addChildAfter(node4, node2);
        }
        return node4;
    }

    private static Node replaceCurrent(Node node, Node node2, Node node3, Node node4) {
        if (node2 == null) {
            if (node3 != node.getFirstChild()) {
                Kit.codeBug();
            }
            node.replaceChild(node3, node4);
        } else if (node2.next == node3) {
            node.replaceChildAfter(node2, node4);
        } else {
            node.replaceChild(node3, node4);
        }
        return node4;
    }

    private void transformCompilationUnit(ScriptNode scriptNode, boolean z) {
        this.loops = new ObjArray();
        this.loopEnds = new ObjArray();
        this.hasFinally = false;
        boolean z2 = scriptNode.getType() != Token.FUNCTION || ((FunctionNode) scriptNode).requiresActivation();
        scriptNode.flattenSymbolTable(!z2);
        transformCompilationUnit_r(scriptNode, scriptNode, scriptNode, z2, z);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void transformCompilationUnit_r(org.mozilla.javascript.ast.ScriptNode r10, org.mozilla.javascript.Node r11, org.mozilla.javascript.ast.Scope r12, boolean r13, boolean r14) {
        /*
        r9 = this;
        r0 = 0;
    L_0x0001:
        r5 = 0;
        if (r0 != 0) goto L_0x000b;
    L_0x0004:
        r1 = r11.getFirstChild();
    L_0x0008:
        if (r1 != 0) goto L_0x0011;
    L_0x000a:
        return;
    L_0x000b:
        r1 = r0.getNext();
        r5 = r0;
        goto L_0x0008;
    L_0x0011:
        r2 = r1.getType();
        if (r13 == 0) goto L_0x03a7;
    L_0x0017:
        r0 = 130; // 0x82 float:1.82E-43 double:6.4E-322;
        if (r2 == r0) goto L_0x0023;
    L_0x001b:
        r0 = 133; // 0x85 float:1.86E-43 double:6.57E-322;
        if (r2 == r0) goto L_0x0023;
    L_0x001f:
        r0 = 158; // 0x9e float:2.21E-43 double:7.8E-322;
        if (r2 != r0) goto L_0x03a7;
    L_0x0023:
        r0 = r1 instanceof org.mozilla.javascript.ast.Scope;
        if (r0 == 0) goto L_0x03a7;
    L_0x0027:
        r0 = r1;
        r0 = (org.mozilla.javascript.ast.Scope) r0;
        r3 = r0.getSymbolTable();
        if (r3 == 0) goto L_0x03a7;
    L_0x0030:
        r3 = new org.mozilla.javascript.Node;
        r4 = 158; // 0x9e float:2.21E-43 double:7.8E-322;
        if (r2 != r4) goto L_0x0067;
    L_0x0036:
        r2 = 159; // 0x9f float:2.23E-43 double:7.86E-322;
    L_0x0038:
        r3.<init>(r2);
        r4 = new org.mozilla.javascript.Node;
        r2 = 154; // 0x9a float:2.16E-43 double:7.6E-322;
        r4.<init>(r2);
        r3.addChildToBack(r4);
        r2 = r0.getSymbolTable();
        r2 = r2.keySet();
        r6 = r2.iterator();
    L_0x0051:
        r2 = r6.hasNext();
        if (r2 == 0) goto L_0x006a;
    L_0x0057:
        r2 = r6.next();
        r2 = (java.lang.String) r2;
        r7 = 39;
        r2 = org.mozilla.javascript.Node.newString(r7, r2);
        r4.addChildToBack(r2);
        goto L_0x0051;
    L_0x0067:
        r2 = 154; // 0x9a float:2.16E-43 double:7.6E-322;
        goto L_0x0038;
    L_0x006a:
        r2 = 0;
        r0.setSymbolTable(r2);
        r2 = replaceCurrent(r11, r5, r1, r3);
        r0 = r2.getType();
        r3.addChildToBack(r1);
        r6 = r0;
        r1 = r2;
    L_0x007b:
        switch(r6) {
            case 3: goto L_0x00d4;
            case 4: goto L_0x00f8;
            case 7: goto L_0x02ac;
            case 8: goto L_0x0313;
            case 30: goto L_0x020f;
            case 31: goto L_0x031a;
            case 32: goto L_0x02ac;
            case 38: goto L_0x0209;
            case 39: goto L_0x031a;
            case 73: goto L_0x00f0;
            case 82: goto L_0x00bc;
            case 115: goto L_0x0091;
            case 121: goto L_0x01a0;
            case 122: goto L_0x01a0;
            case 123: goto L_0x023c;
            case 124: goto L_0x00a2;
            case 131: goto L_0x0091;
            case 132: goto L_0x00d4;
            case 133: goto L_0x0091;
            case 138: goto L_0x029c;
            case 154: goto L_0x0215;
            case 155: goto L_0x023c;
            case 156: goto L_0x031a;
            case 159: goto L_0x0215;
            default: goto L_0x007e;
        };
    L_0x007e:
        r2 = r1;
    L_0x007f:
        r0 = r2 instanceof org.mozilla.javascript.ast.Scope;
        if (r0 == 0) goto L_0x039b;
    L_0x0083:
        r0 = r2;
        r0 = (org.mozilla.javascript.ast.Scope) r0;
        r3 = r0;
    L_0x0087:
        r0 = r9;
        r1 = r10;
        r4 = r13;
        r5 = r14;
        r0.transformCompilationUnit_r(r1, r2, r3, r4, r5);
        r0 = r2;
        goto L_0x0001;
    L_0x0091:
        r0 = r9.loops;
        r0.push(r1);
        r2 = r9.loopEnds;
        r0 = r1;
        r0 = (org.mozilla.javascript.ast.Jump) r0;
        r0 = r0.target;
        r2.push(r0);
        r2 = r1;
        goto L_0x007f;
    L_0x00a2:
        r0 = r9.loops;
        r0.push(r1);
        r0 = r1.getNext();
        r2 = r0.getType();
        r3 = 3;
        if (r2 == r3) goto L_0x00b5;
    L_0x00b2:
        org.mozilla.javascript.Kit.codeBug();
    L_0x00b5:
        r2 = r9.loopEnds;
        r2.push(r0);
        r2 = r1;
        goto L_0x007f;
    L_0x00bc:
        r0 = r1;
        r0 = (org.mozilla.javascript.ast.Jump) r0;
        r0 = r0.getFinally();
        if (r0 == 0) goto L_0x007e;
    L_0x00c5:
        r2 = 1;
        r9.hasFinally = r2;
        r2 = r9.loops;
        r2.push(r1);
        r2 = r9.loopEnds;
        r2.push(r0);
        r2 = r1;
        goto L_0x007f;
    L_0x00d4:
        r0 = r9.loopEnds;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x007e;
    L_0x00dc:
        r0 = r9.loopEnds;
        r0 = r0.peek();
        if (r0 != r1) goto L_0x007e;
    L_0x00e4:
        r0 = r9.loopEnds;
        r0.pop();
        r0 = r9.loops;
        r0.pop();
        r2 = r1;
        goto L_0x007f;
    L_0x00f0:
        r0 = r10;
        r0 = (org.mozilla.javascript.ast.FunctionNode) r0;
        r0.addResumptionPoint(r1);
        r2 = r1;
        goto L_0x007f;
    L_0x00f8:
        r0 = r10.getType();
        r2 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r0 != r2) goto L_0x011a;
    L_0x0100:
        r0 = r10;
        r0 = (org.mozilla.javascript.ast.FunctionNode) r0;
        r0 = r0.isGenerator();
        if (r0 == 0) goto L_0x011a;
    L_0x0109:
        r0 = 1;
        r6 = r0;
    L_0x010b:
        if (r6 == 0) goto L_0x0113;
    L_0x010d:
        r0 = 20;
        r2 = 1;
        r1.putIntProp(r0, r2);
    L_0x0113:
        r0 = r9.hasFinally;
        if (r0 != 0) goto L_0x011d;
    L_0x0117:
        r2 = r1;
        goto L_0x007f;
    L_0x011a:
        r0 = 0;
        r6 = r0;
        goto L_0x010b;
    L_0x011d:
        r3 = 0;
        r0 = r9.loops;
        r0 = r0.size();
        r0 = r0 + -1;
        r4 = r0;
    L_0x0127:
        if (r4 < 0) goto L_0x016d;
    L_0x0129:
        r0 = r9.loops;
        r0 = r0.get(r4);
        r0 = (org.mozilla.javascript.Node) r0;
        r2 = r0.getType();
        r7 = 82;
        if (r2 == r7) goto L_0x013d;
    L_0x0139:
        r7 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        if (r2 != r7) goto L_0x0161;
    L_0x013d:
        r7 = 82;
        if (r2 != r7) goto L_0x0165;
    L_0x0141:
        r2 = new org.mozilla.javascript.ast.Jump;
        r7 = 136; // 0x88 float:1.9E-43 double:6.7E-322;
        r2.<init>(r7);
        r0 = (org.mozilla.javascript.ast.Jump) r0;
        r0 = r0.getFinally();
        r2.target = r0;
    L_0x0150:
        if (r3 != 0) goto L_0x03a4;
    L_0x0152:
        r0 = new org.mozilla.javascript.Node;
        r3 = 130; // 0x82 float:1.82E-43 double:6.4E-322;
        r7 = r1.getLineno();
        r0.<init>(r3, r7);
    L_0x015d:
        r0.addChildToBack(r2);
        r3 = r0;
    L_0x0161:
        r0 = r4 + -1;
        r4 = r0;
        goto L_0x0127;
    L_0x0165:
        r0 = new org.mozilla.javascript.Node;
        r2 = 3;
        r0.<init>(r2);
        r2 = r0;
        goto L_0x0150;
    L_0x016d:
        if (r3 == 0) goto L_0x007e;
    L_0x016f:
        r0 = r1.getFirstChild();
        r7 = replaceCurrent(r11, r5, r1, r3);
        if (r0 == 0) goto L_0x017b;
    L_0x0179:
        if (r6 == 0) goto L_0x0181;
    L_0x017b:
        r3.addChildToBack(r1);
        r0 = r7;
        goto L_0x0001;
    L_0x0181:
        r2 = new org.mozilla.javascript.Node;
        r1 = 135; // 0x87 float:1.89E-43 double:6.67E-322;
        r2.<init>(r1, r0);
        r3.addChildToFront(r2);
        r0 = new org.mozilla.javascript.Node;
        r1 = 65;
        r0.<init>(r1);
        r3.addChildToBack(r0);
        r0 = r9;
        r1 = r10;
        r3 = r12;
        r4 = r13;
        r5 = r14;
        r0.transformCompilationUnit_r(r1, r2, r3, r4, r5);
        r0 = r7;
        goto L_0x0001;
    L_0x01a0:
        r0 = r1;
        r0 = (org.mozilla.javascript.ast.Jump) r0;
        r7 = r0.getJumpStatement();
        if (r7 != 0) goto L_0x01ac;
    L_0x01a9:
        org.mozilla.javascript.Kit.codeBug();
    L_0x01ac:
        r2 = r9.loops;
        r2 = r2.size();
        r3 = r5;
    L_0x01b3:
        if (r2 != 0) goto L_0x01ba;
    L_0x01b5:
        r0 = org.mozilla.javascript.Kit.codeBug();
        throw r0;
    L_0x01ba:
        r4 = r2 + -1;
        r2 = r9.loops;
        r2 = r2.get(r4);
        r2 = (org.mozilla.javascript.Node) r2;
        if (r2 != r7) goto L_0x01d5;
    L_0x01c6:
        r2 = 121; // 0x79 float:1.7E-43 double:6.0E-322;
        if (r6 != r2) goto L_0x0202;
    L_0x01ca:
        r2 = r7.target;
        r0.target = r2;
    L_0x01ce:
        r2 = 5;
        r0.setType(r2);
        r2 = r1;
        goto L_0x007f;
    L_0x01d5:
        r5 = r2.getType();
        r8 = 124; // 0x7c float:1.74E-43 double:6.13E-322;
        if (r5 != r8) goto L_0x01ea;
    L_0x01dd:
        r2 = new org.mozilla.javascript.Node;
        r5 = 3;
        r2.<init>(r5);
        r2 = addBeforeCurrent(r11, r3, r1, r2);
    L_0x01e7:
        r3 = r2;
        r2 = r4;
        goto L_0x01b3;
    L_0x01ea:
        r8 = 82;
        if (r5 != r8) goto L_0x03a1;
    L_0x01ee:
        r2 = (org.mozilla.javascript.ast.Jump) r2;
        r5 = new org.mozilla.javascript.ast.Jump;
        r8 = 136; // 0x88 float:1.9E-43 double:6.7E-322;
        r5.<init>(r8);
        r2 = r2.getFinally();
        r5.target = r2;
        r2 = addBeforeCurrent(r11, r3, r1, r5);
        goto L_0x01e7;
    L_0x0202:
        r2 = r7.getContinue();
        r0.target = r2;
        goto L_0x01ce;
    L_0x0209:
        r9.visitCall(r1, r10);
        r2 = r1;
        goto L_0x007f;
    L_0x020f:
        r9.visitNew(r1, r10);
        r2 = r1;
        goto L_0x007f;
    L_0x0215:
        r0 = r1.getFirstChild();
        r0 = r0.getType();
        r2 = 154; // 0x9a float:2.16E-43 double:7.6E-322;
        if (r0 != r2) goto L_0x023c;
    L_0x0221:
        r0 = r10.getType();
        r2 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        if (r0 != r2) goto L_0x0232;
    L_0x0229:
        r0 = r10;
        r0 = (org.mozilla.javascript.ast.FunctionNode) r0;
        r0 = r0.requiresActivation();
        if (r0 == 0) goto L_0x023a;
    L_0x0232:
        r0 = 1;
    L_0x0233:
        r1 = r9.visitLet(r0, r11, r5, r1);
        r2 = r1;
        goto L_0x007f;
    L_0x023a:
        r0 = 0;
        goto L_0x0233;
    L_0x023c:
        r7 = new org.mozilla.javascript.Node;
        r0 = 130; // 0x82 float:1.82E-43 double:6.4E-322;
        r7.<init>(r0);
        r3 = r1.getFirstChild();
    L_0x0247:
        if (r3 == 0) goto L_0x0295;
    L_0x0249:
        r4 = r3.getNext();
        r0 = r3.getType();
        r2 = 39;
        if (r0 != r2) goto L_0x0288;
    L_0x0255:
        r0 = r3.hasChildren();
        if (r0 != 0) goto L_0x025d;
    L_0x025b:
        r3 = r4;
        goto L_0x0247;
    L_0x025d:
        r8 = r3.getFirstChild();
        r3.removeChild(r8);
        r0 = 49;
        r3.setType(r0);
        r2 = new org.mozilla.javascript.Node;
        r0 = 155; // 0x9b float:2.17E-43 double:7.66E-322;
        if (r6 != r0) goto L_0x0285;
    L_0x026f:
        r0 = 156; // 0x9c float:2.19E-43 double:7.7E-322;
    L_0x0271:
        r2.<init>(r0, r3, r8);
        r0 = r2;
    L_0x0275:
        r2 = new org.mozilla.javascript.Node;
        r3 = 134; // 0x86 float:1.88E-43 double:6.6E-322;
        r8 = r1.getLineno();
        r2.<init>(r3, r0, r8);
        r7.addChildToBack(r2);
        r3 = r4;
        goto L_0x0247;
    L_0x0285:
        r0 = 8;
        goto L_0x0271;
    L_0x0288:
        r0 = r3.getType();
        r2 = 159; // 0x9f float:2.23E-43 double:7.86E-322;
        if (r0 == r2) goto L_0x039e;
    L_0x0290:
        r0 = org.mozilla.javascript.Kit.codeBug();
        throw r0;
    L_0x0295:
        r1 = replaceCurrent(r11, r5, r1, r7);
        r2 = r1;
        goto L_0x007f;
    L_0x029c:
        r0 = r1.getString();
        r0 = r12.getDefiningScope(r0);
        if (r0 == 0) goto L_0x02a9;
    L_0x02a6:
        r1.setScope(r0);
    L_0x02a9:
        r2 = r1;
        goto L_0x007f;
    L_0x02ac:
        r0 = r1.getFirstChild();
        r2 = 7;
        if (r6 != r2) goto L_0x02ed;
    L_0x02b3:
        r2 = r0.getType();
        r3 = 26;
        if (r2 != r3) goto L_0x02c0;
    L_0x02bb:
        r0 = r0.getFirstChild();
        goto L_0x02b3;
    L_0x02c0:
        r2 = r0.getType();
        r3 = 12;
        if (r2 == r3) goto L_0x02d0;
    L_0x02c8:
        r2 = r0.getType();
        r3 = 13;
        if (r2 != r3) goto L_0x02ed;
    L_0x02d0:
        r3 = r0.getFirstChild();
        r2 = r0.getLastChild();
        r4 = r3.getType();
        r5 = 39;
        if (r4 != r5) goto L_0x02fd;
    L_0x02e0:
        r4 = r3.getString();
        r5 = "undefined";
        r4 = r4.equals(r5);
        if (r4 == 0) goto L_0x02fd;
    L_0x02ec:
        r0 = r2;
    L_0x02ed:
        r2 = r0.getType();
        r3 = 33;
        if (r2 != r3) goto L_0x007e;
    L_0x02f5:
        r2 = 34;
        r0.setType(r2);
        r2 = r1;
        goto L_0x007f;
    L_0x02fd:
        r4 = r2.getType();
        r5 = 39;
        if (r4 != r5) goto L_0x02ed;
    L_0x0305:
        r2 = r2.getString();
        r4 = "undefined";
        r2 = r2.equals(r4);
        if (r2 == 0) goto L_0x02ed;
    L_0x0311:
        r0 = r3;
        goto L_0x02ed;
    L_0x0313:
        if (r14 == 0) goto L_0x031a;
    L_0x0315:
        r0 = 74;
        r1.setType(r0);
    L_0x031a:
        if (r13 == 0) goto L_0x031f;
    L_0x031c:
        r2 = r1;
        goto L_0x007f;
    L_0x031f:
        r0 = 39;
        if (r6 != r0) goto L_0x032d;
    L_0x0323:
        r0 = r1;
    L_0x0324:
        r2 = r0.getScope();
        if (r2 == 0) goto L_0x0345;
    L_0x032a:
        r2 = r1;
        goto L_0x007f;
    L_0x032d:
        r0 = r1.getFirstChild();
        r2 = r0.getType();
        r3 = 49;
        if (r2 == r3) goto L_0x0324;
    L_0x0339:
        r0 = 31;
        if (r6 != r0) goto L_0x0340;
    L_0x033d:
        r2 = r1;
        goto L_0x007f;
    L_0x0340:
        r0 = org.mozilla.javascript.Kit.codeBug();
        throw r0;
    L_0x0345:
        r2 = r0.getString();
        r2 = r12.getDefiningScope(r2);
        if (r2 == 0) goto L_0x007e;
    L_0x034f:
        r0.setScope(r2);
        r2 = 39;
        if (r6 != r2) goto L_0x035e;
    L_0x0356:
        r0 = 55;
        r1.setType(r0);
        r2 = r1;
        goto L_0x007f;
    L_0x035e:
        r2 = 8;
        if (r6 == r2) goto L_0x0366;
    L_0x0362:
        r2 = 74;
        if (r6 != r2) goto L_0x0373;
    L_0x0366:
        r2 = 56;
        r1.setType(r2);
        r2 = 41;
        r0.setType(r2);
        r2 = r1;
        goto L_0x007f;
    L_0x0373:
        r2 = 156; // 0x9c float:2.19E-43 double:7.7E-322;
        if (r6 != r2) goto L_0x0384;
    L_0x0377:
        r2 = 157; // 0x9d float:2.2E-43 double:7.76E-322;
        r1.setType(r2);
        r2 = 41;
        r0.setType(r2);
        r2 = r1;
        goto L_0x007f;
    L_0x0384:
        r0 = 31;
        if (r6 != r0) goto L_0x0396;
    L_0x0388:
        r0 = new org.mozilla.javascript.Node;
        r2 = 44;
        r0.<init>(r2);
        r1 = replaceCurrent(r11, r5, r1, r0);
        r2 = r1;
        goto L_0x007f;
    L_0x0396:
        r0 = org.mozilla.javascript.Kit.codeBug();
        throw r0;
    L_0x039b:
        r3 = r12;
        goto L_0x0087;
    L_0x039e:
        r0 = r3;
        goto L_0x0275;
    L_0x03a1:
        r2 = r3;
        goto L_0x01e7;
    L_0x03a4:
        r0 = r3;
        goto L_0x015d;
    L_0x03a7:
        r6 = r2;
        goto L_0x007b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NodeTransformer.transformCompilationUnit_r(org.mozilla.javascript.ast.ScriptNode, org.mozilla.javascript.Node, org.mozilla.javascript.ast.Scope, boolean, boolean):void");
    }

    public final void transform(ScriptNode scriptNode) {
        transform(scriptNode, false);
    }

    public final void transform(ScriptNode scriptNode, boolean z) {
        int i = 0;
        boolean z2 = z || scriptNode.isInStrictMode();
        transformCompilationUnit(scriptNode, z2);
        while (i != scriptNode.getFunctionCount()) {
            transform(scriptNode.getFunctionNode(i), z2);
            i++;
        }
    }

    protected void visitCall(Node node, ScriptNode scriptNode) {
    }

    protected Node visitLet(boolean z, Node node, Node node2, Node node3) {
        Node firstChild = node3.getFirstChild();
        Node next = firstChild.getNext();
        node3.removeChild(firstChild);
        node3.removeChild(next);
        Object obj = node3.getType() == Token.LETEXPR ? 1 : null;
        Node node4;
        Node firstChild2;
        if (z) {
            Node replaceCurrent = replaceCurrent(node, node2, node3, new Node(obj != null ? Token.WITHEXPR : Token.BLOCK));
            ArrayList arrayList = new ArrayList();
            node4 = new Node(67);
            firstChild2 = firstChild.getFirstChild();
            firstChild = next;
            for (next = firstChild2; next != null; next = next.getNext()) {
                if (next.getType() == Token.LETEXPR) {
                    List list = (List) next.getProp(22);
                    Node firstChild3 = next.getFirstChild();
                    if (firstChild3.getType() != Token.LET) {
                        throw Kit.codeBug();
                    }
                    Node node5 = obj != null ? new Node(90, firstChild3.getNext(), firstChild) : new Node((int) Token.BLOCK, new Node((int) Token.EXPR_VOID, firstChild3.getNext()), firstChild);
                    if (list != null) {
                        arrayList.addAll(list);
                        for (int i = 0; i < list.size(); i++) {
                            node4.addChildToBack(new Node((int) Token.VOID, Node.newNumber(0.0d)));
                        }
                    }
                    firstChild2 = firstChild3.getFirstChild();
                    firstChild = node5;
                } else {
                    firstChild2 = next;
                }
                if (firstChild2.getType() != 39) {
                    throw Kit.codeBug();
                }
                arrayList.add(ScriptRuntime.getIndexObject(firstChild2.getString()));
                firstChild2 = firstChild2.getFirstChild();
                if (firstChild2 == null) {
                    firstChild2 = new Node((int) Token.VOID, Node.newNumber(0.0d));
                }
                node4.addChildToBack(firstChild2);
            }
            node4.putProp(12, arrayList.toArray());
            replaceCurrent.addChildToBack(new Node(2, node4));
            replaceCurrent.addChildToBack(new Node((int) Token.WITH, firstChild));
            replaceCurrent.addChildToBack(new Node(3));
            return replaceCurrent;
        }
        replaceCurrent = replaceCurrent(node, node2, node3, new Node(obj != null ? 90 : Token.BLOCK));
        Node node6 = new Node(90);
        firstChild = firstChild.getFirstChild();
        while (firstChild != null) {
            if (firstChild.getType() == Token.LETEXPR) {
                node4 = firstChild.getFirstChild();
                if (node4.getType() != Token.LET) {
                    throw Kit.codeBug();
                }
                node5 = obj != null ? new Node(90, node4.getNext(), next) : new Node((int) Token.BLOCK, new Node((int) Token.EXPR_VOID, node4.getNext()), next);
                Scope.joinScopes((Scope) firstChild, (Scope) node3);
                next = node4.getFirstChild();
            } else {
                node5 = next;
                next = firstChild;
            }
            if (next.getType() != 39) {
                throw Kit.codeBug();
            }
            node4 = Node.newString(next.getString());
            node4.setScope((Scope) node3);
            firstChild2 = next.getFirstChild();
            if (firstChild2 == null) {
                firstChild2 = new Node((int) Token.VOID, Node.newNumber(0.0d));
            }
            node6.addChildToBack(new Node(56, node4, firstChild2));
            firstChild = firstChild.getNext();
            next = node5;
        }
        if (obj != null) {
            replaceCurrent.addChildToBack(node6);
            node3.setType(90);
            replaceCurrent.addChildToBack(node3);
            node3.addChildToBack(next);
            if (next instanceof Scope) {
                Scope parentScope = ((Scope) next).getParentScope();
                ((Scope) next).setParentScope((Scope) node3);
                ((Scope) node3).setParentScope(parentScope);
                return replaceCurrent;
            }
        }
        replaceCurrent.addChildToBack(new Node((int) Token.EXPR_VOID, node6));
        node3.setType(Token.BLOCK);
        replaceCurrent.addChildToBack(node3);
        node3.addChildrenToBack(next);
        if (next instanceof Scope) {
            parentScope = ((Scope) next).getParentScope();
            ((Scope) next).setParentScope((Scope) node3);
            ((Scope) node3).setParentScope(parentScope);
        }
        return replaceCurrent;
    }

    protected void visitNew(Node node, ScriptNode scriptNode) {
    }
}
