package org.mozilla.javascript;

public class Decompiler {
    public static final int CASE_GAP_PROP = 3;
    private static final int FUNCTION_END = 167;
    public static final int INDENT_GAP_PROP = 2;
    public static final int INITIAL_INDENT_PROP = 1;
    public static final int ONLY_BODY_FLAG = 1;
    public static final int TO_SOURCE_FLAG = 2;
    private static final boolean printSource = false;
    private char[] sourceBuffer = new char[Token.RESERVED];
    private int sourceTop;

    private void append(char c) {
        if (this.sourceTop == this.sourceBuffer.length) {
            increaseSourceCapacity(this.sourceTop + ONLY_BODY_FLAG);
        }
        this.sourceBuffer[this.sourceTop] = c;
        this.sourceTop += ONLY_BODY_FLAG;
    }

    private void appendString(String str) {
        int length = str.length();
        int i = ONLY_BODY_FLAG;
        if (length >= 32768) {
            i = TO_SOURCE_FLAG;
        }
        i = (i + this.sourceTop) + length;
        if (i > this.sourceBuffer.length) {
            increaseSourceCapacity(i);
        }
        if (length >= 32768) {
            this.sourceBuffer[this.sourceTop] = (char) ((length >>> 16) | 32768);
            this.sourceTop += ONLY_BODY_FLAG;
        }
        this.sourceBuffer[this.sourceTop] = (char) length;
        this.sourceTop += ONLY_BODY_FLAG;
        str.getChars(0, length, this.sourceBuffer, this.sourceTop);
        this.sourceTop = i;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String decompile(java.lang.String r17, int r18, org.mozilla.javascript.UintMap r19) {
        /*
        r10 = r17.length();
        if (r10 != 0) goto L_0x0009;
    L_0x0006:
        r1 = "";
    L_0x0008:
        return r1;
    L_0x0009:
        r1 = 1;
        r2 = 0;
        r0 = r19;
        r2 = r0.getInt(r1, r2);
        if (r2 >= 0) goto L_0x0019;
    L_0x0013:
        r1 = new java.lang.IllegalArgumentException;
        r1.<init>();
        throw r1;
    L_0x0019:
        r1 = 2;
        r3 = 4;
        r0 = r19;
        r7 = r0.getInt(r1, r3);
        if (r7 >= 0) goto L_0x0029;
    L_0x0023:
        r1 = new java.lang.IllegalArgumentException;
        r1.<init>();
        throw r1;
    L_0x0029:
        r1 = 3;
        r3 = 2;
        r0 = r19;
        r11 = r0.getInt(r1, r3);
        if (r11 >= 0) goto L_0x0039;
    L_0x0033:
        r1 = new java.lang.IllegalArgumentException;
        r1.<init>();
        throw r1;
    L_0x0039:
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r1 = r18 & 1;
        if (r1 == 0) goto L_0x006c;
    L_0x0042:
        r1 = 1;
        r9 = r1;
    L_0x0044:
        r1 = r18 & 2;
        if (r1 == 0) goto L_0x006f;
    L_0x0048:
        r1 = 1;
    L_0x0049:
        r5 = 0;
        r4 = 0;
        r6 = 0;
        r0 = r17;
        r3 = r0.charAt(r6);
        r8 = 137; // 0x89 float:1.92E-43 double:6.77E-322;
        if (r3 != r8) goto L_0x0071;
    L_0x0056:
        r6 = 1;
        r3 = -1;
        r8 = r3;
        r3 = r6;
    L_0x005a:
        if (r1 != 0) goto L_0x007b;
    L_0x005c:
        r6 = 10;
        r12.append(r6);
        r6 = 0;
    L_0x0062:
        if (r6 >= r2) goto L_0x0083;
    L_0x0064:
        r13 = 32;
        r12.append(r13);
        r6 = r6 + 1;
        goto L_0x0062;
    L_0x006c:
        r1 = 0;
        r9 = r1;
        goto L_0x0044;
    L_0x006f:
        r1 = 0;
        goto L_0x0049;
    L_0x0071:
        r3 = 1;
        r0 = r17;
        r3 = r0.charAt(r3);
        r8 = r3;
        r3 = r6;
        goto L_0x005a;
    L_0x007b:
        r6 = 2;
        if (r8 != r6) goto L_0x0083;
    L_0x007e:
        r6 = 40;
        r12.append(r6);
    L_0x0083:
        if (r3 >= r10) goto L_0x061e;
    L_0x0085:
        r0 = r17;
        r6 = r0.charAt(r3);
        switch(r6) {
            case 1: goto L_0x01d4;
            case 2: goto L_0x008e;
            case 3: goto L_0x008e;
            case 4: goto L_0x0340;
            case 5: goto L_0x008e;
            case 6: goto L_0x008e;
            case 7: goto L_0x008e;
            case 8: goto L_0x008e;
            case 9: goto L_0x046e;
            case 10: goto L_0x047a;
            case 11: goto L_0x0486;
            case 12: goto L_0x04aa;
            case 13: goto L_0x04b6;
            case 14: goto L_0x04ce;
            case 15: goto L_0x04c2;
            case 16: goto L_0x04e6;
            case 17: goto L_0x04da;
            case 18: goto L_0x04fe;
            case 19: goto L_0x050a;
            case 20: goto L_0x0516;
            case 21: goto L_0x059a;
            case 22: goto L_0x05a6;
            case 23: goto L_0x05b2;
            case 24: goto L_0x05be;
            case 25: goto L_0x05ca;
            case 26: goto L_0x0552;
            case 27: goto L_0x055e;
            case 28: goto L_0x056a;
            case 29: goto L_0x0576;
            case 30: goto L_0x024a;
            case 31: goto L_0x0256;
            case 32: goto L_0x0522;
            case 33: goto L_0x008e;
            case 34: goto L_0x008e;
            case 35: goto L_0x008e;
            case 36: goto L_0x008e;
            case 37: goto L_0x008e;
            case 38: goto L_0x008e;
            case 39: goto L_0x00ea;
            case 40: goto L_0x00fe;
            case 41: goto L_0x00f4;
            case 42: goto L_0x011e;
            case 43: goto L_0x0129;
            case 44: goto L_0x0113;
            case 45: goto L_0x0108;
            case 46: goto L_0x0492;
            case 47: goto L_0x049e;
            case 48: goto L_0x00ea;
            case 49: goto L_0x008e;
            case 50: goto L_0x02da;
            case 51: goto L_0x008e;
            case 52: goto L_0x0286;
            case 53: goto L_0x04f2;
            case 54: goto L_0x008e;
            case 55: goto L_0x008e;
            case 56: goto L_0x008e;
            case 57: goto L_0x008e;
            case 58: goto L_0x008e;
            case 59: goto L_0x008e;
            case 60: goto L_0x008e;
            case 61: goto L_0x008e;
            case 62: goto L_0x008e;
            case 63: goto L_0x008e;
            case 64: goto L_0x008e;
            case 65: goto L_0x008e;
            case 66: goto L_0x008e;
            case 67: goto L_0x0429;
            case 68: goto L_0x008e;
            case 69: goto L_0x008e;
            case 70: goto L_0x008e;
            case 71: goto L_0x008e;
            case 72: goto L_0x008e;
            case 73: goto L_0x0546;
            case 74: goto L_0x008e;
            case 75: goto L_0x008e;
            case 76: goto L_0x008e;
            case 77: goto L_0x008e;
            case 78: goto L_0x008e;
            case 79: goto L_0x008e;
            case 80: goto L_0x008e;
            case 81: goto L_0x008e;
            case 82: goto L_0x02b6;
            case 83: goto L_0x0373;
            case 84: goto L_0x01bc;
            case 85: goto L_0x01c8;
            case 86: goto L_0x0152;
            case 87: goto L_0x016a;
            case 88: goto L_0x0195;
            case 89: goto L_0x01a1;
            case 90: goto L_0x0147;
            case 91: goto L_0x038d;
            case 92: goto L_0x03d5;
            case 93: goto L_0x03e1;
            case 94: goto L_0x03ed;
            case 95: goto L_0x03f9;
            case 96: goto L_0x0405;
            case 97: goto L_0x0411;
            case 98: goto L_0x0399;
            case 99: goto L_0x03a5;
            case 100: goto L_0x03b1;
            case 101: goto L_0x03bd;
            case 102: goto L_0x03c9;
            case 103: goto L_0x041d;
            case 104: goto L_0x0435;
            case 105: goto L_0x0456;
            case 106: goto L_0x0462;
            case 107: goto L_0x0582;
            case 108: goto L_0x058e;
            case 109: goto L_0x023e;
            case 110: goto L_0x0134;
            case 111: goto L_0x008e;
            case 112: goto L_0x008e;
            case 113: goto L_0x0262;
            case 114: goto L_0x026e;
            case 115: goto L_0x02e6;
            case 116: goto L_0x0328;
            case 117: goto L_0x0334;
            case 118: goto L_0x029e;
            case 119: goto L_0x02aa;
            case 120: goto L_0x027a;
            case 121: goto L_0x02f2;
            case 122: goto L_0x030d;
            case 123: goto L_0x035b;
            case 124: goto L_0x0292;
            case 125: goto L_0x02c2;
            case 126: goto L_0x02ce;
            case 127: goto L_0x052e;
            case 128: goto L_0x008e;
            case 129: goto L_0x008e;
            case 130: goto L_0x008e;
            case 131: goto L_0x008e;
            case 132: goto L_0x008e;
            case 133: goto L_0x008e;
            case 134: goto L_0x008e;
            case 135: goto L_0x008e;
            case 136: goto L_0x008e;
            case 137: goto L_0x008e;
            case 138: goto L_0x008e;
            case 139: goto L_0x008e;
            case 140: goto L_0x008e;
            case 141: goto L_0x008e;
            case 142: goto L_0x008e;
            case 143: goto L_0x008e;
            case 144: goto L_0x05e2;
            case 145: goto L_0x05d6;
            case 146: goto L_0x008e;
            case 147: goto L_0x05ee;
            case 148: goto L_0x05fa;
            case 149: goto L_0x008e;
            case 150: goto L_0x008e;
            case 151: goto L_0x008e;
            case 152: goto L_0x00b1;
            case 153: goto L_0x00b1;
            case 154: goto L_0x0367;
            case 155: goto L_0x053a;
            case 156: goto L_0x008e;
            case 157: goto L_0x008e;
            case 158: goto L_0x008e;
            case 159: goto L_0x008e;
            case 160: goto L_0x008e;
            case 161: goto L_0x0606;
            case 162: goto L_0x008e;
            case 163: goto L_0x008e;
            case 164: goto L_0x00b1;
            case 165: goto L_0x0612;
            case 166: goto L_0x008e;
            case 167: goto L_0x0141;
            default: goto L_0x008e;
        };
    L_0x008e:
        r1 = new java.lang.RuntimeException;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r4 = "Token: ";
        r2 = r2.append(r4);
        r0 = r17;
        r3 = r0.charAt(r3);
        r3 = org.mozilla.javascript.Token.name(r3);
        r2 = r2.append(r3);
        r2 = r2.toString();
        r1.<init>(r2);
        throw r1;
    L_0x00b1:
        r0 = r17;
        r6 = r0.charAt(r3);
        r13 = 152; // 0x98 float:2.13E-43 double:7.5E-322;
        if (r6 != r13) goto L_0x00da;
    L_0x00bb:
        r6 = "get ";
        r12.append(r6);
    L_0x00c0:
        r3 = r3 + 1;
        r3 = r3 + 1;
        r6 = 0;
        r0 = r17;
        r3 = printSourceString(r0, r3, r6, r12);
        r3 = r3 + 1;
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
    L_0x00d2:
        r2 = r2 + 1;
        r15 = r2;
        r2 = r5;
        r5 = r4;
        r4 = r3;
        r3 = r15;
        goto L_0x0083;
    L_0x00da:
        r0 = r17;
        r6 = r0.charAt(r3);
        r13 = 153; // 0x99 float:2.14E-43 double:7.56E-322;
        if (r6 != r13) goto L_0x00c0;
    L_0x00e4:
        r6 = "set ";
        r12.append(r6);
        goto L_0x00c0;
    L_0x00ea:
        r3 = r3 + 1;
        r6 = 0;
        r0 = r17;
        r3 = printSourceString(r0, r3, r6, r12);
        goto L_0x0083;
    L_0x00f4:
        r3 = r3 + 1;
        r6 = 1;
        r0 = r17;
        r3 = printSourceString(r0, r3, r6, r12);
        goto L_0x0083;
    L_0x00fe:
        r3 = r3 + 1;
        r0 = r17;
        r3 = printSourceNumber(r0, r3, r12);
        goto L_0x0083;
    L_0x0108:
        r6 = "true";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0113:
        r6 = "false";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x011e:
        r6 = "null";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0129:
        r6 = "this";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0134:
        r3 = r3 + 1;
        r6 = "function ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0141:
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0147:
        r6 = ", ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0152:
        r5 = r5 + 1;
        r6 = 1;
        r0 = r17;
        r13 = getNext(r0, r10, r3);
        if (r6 != r13) goto L_0x015e;
    L_0x015d:
        r2 = r2 + r7;
    L_0x015e:
        r6 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x016a:
        r5 = r5 + -1;
        if (r9 == 0) goto L_0x0177;
    L_0x016e:
        if (r5 != 0) goto L_0x0177;
    L_0x0170:
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0177:
        r6 = 125; // 0x7d float:1.75E-43 double:6.2E-322;
        r12.append(r6);
        r0 = r17;
        r6 = getNext(r0, r10, r3);
        switch(r6) {
            case 1: goto L_0x018c;
            case 114: goto L_0x018e;
            case 118: goto L_0x018e;
            case 167: goto L_0x018c;
            default: goto L_0x0185;
        };
    L_0x0185:
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x018c:
        r2 = r2 - r7;
        goto L_0x0185;
    L_0x018e:
        r2 = r2 - r7;
        r6 = 32;
        r12.append(r6);
        goto L_0x0185;
    L_0x0195:
        r6 = 40;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x01a1:
        r6 = 41;
        r12.append(r6);
        r6 = 86;
        r0 = r17;
        r13 = getNext(r0, r10, r3);
        if (r6 != r13) goto L_0x0636;
    L_0x01b0:
        r6 = 32;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x01bc:
        r6 = 91;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x01c8:
        r6 = 93;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x01d4:
        if (r1 == 0) goto L_0x01dd;
    L_0x01d6:
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x01dd:
        r6 = 1;
        if (r4 != 0) goto L_0x0647;
    L_0x01e0:
        r4 = 1;
        if (r9 == 0) goto L_0x0647;
    L_0x01e3:
        r6 = 0;
        r12.setLength(r6);
        r6 = r2 - r7;
        r2 = 0;
        r15 = r2;
        r2 = r4;
        r4 = r6;
        r6 = r15;
    L_0x01ee:
        if (r6 == 0) goto L_0x01f5;
    L_0x01f0:
        r6 = 10;
        r12.append(r6);
    L_0x01f5:
        r6 = r3 + 1;
        if (r6 >= r10) goto L_0x063d;
    L_0x01f9:
        r6 = 0;
        r13 = r3 + 1;
        r0 = r17;
        r13 = r0.charAt(r13);
        r14 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r13 == r14) goto L_0x020a;
    L_0x0206:
        r14 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r13 != r14) goto L_0x0216;
    L_0x020a:
        r6 = r7 - r11;
    L_0x020c:
        if (r6 >= r4) goto L_0x0234;
    L_0x020e:
        r13 = 32;
        r12.append(r13);
        r6 = r6 + 1;
        goto L_0x020c;
    L_0x0216:
        r14 = 87;
        if (r13 != r14) goto L_0x021c;
    L_0x021a:
        r6 = r7;
        goto L_0x020c;
    L_0x021c:
        r14 = 39;
        if (r13 != r14) goto L_0x020c;
    L_0x0220:
        r13 = r3 + 2;
        r0 = r17;
        r13 = getSourceStringEnd(r0, r13);
        r0 = r17;
        r13 = r0.charAt(r13);
        r14 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r13 != r14) goto L_0x020c;
    L_0x0232:
        r6 = r7;
        goto L_0x020c;
    L_0x0234:
        r15 = r3;
        r3 = r2;
        r2 = r15;
        r16 = r5;
        r5 = r4;
        r4 = r16;
        goto L_0x00d2;
    L_0x023e:
        r6 = 46;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x024a:
        r6 = "new ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0256:
        r6 = "delete ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0262:
        r6 = "if ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x026e:
        r6 = "else ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x027a:
        r6 = "for ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0286:
        r6 = " in ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0292:
        r6 = "with ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x029e:
        r6 = "while ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x02aa:
        r6 = "do ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x02b6:
        r6 = "try ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x02c2:
        r6 = "catch ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x02ce:
        r6 = "finally ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x02da:
        r6 = "throw ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x02e6:
        r6 = "switch ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x02f2:
        r6 = "break";
        r12.append(r6);
        r6 = 39;
        r0 = r17;
        r13 = getNext(r0, r10, r3);
        if (r6 != r13) goto L_0x0636;
    L_0x0301:
        r6 = 32;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x030d:
        r6 = "continue";
        r12.append(r6);
        r6 = 39;
        r0 = r17;
        r13 = getNext(r0, r10, r3);
        if (r6 != r13) goto L_0x0636;
    L_0x031c:
        r6 = 32;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0328:
        r6 = "case ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0334:
        r6 = "default";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0340:
        r6 = "return";
        r12.append(r6);
        r6 = 83;
        r0 = r17;
        r13 = getNext(r0, r10, r3);
        if (r6 == r13) goto L_0x0636;
    L_0x034f:
        r6 = 32;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x035b:
        r6 = "var ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0367:
        r6 = "let ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0373:
        r6 = 59;
        r12.append(r6);
        r6 = 1;
        r0 = r17;
        r13 = getNext(r0, r10, r3);
        if (r6 == r13) goto L_0x0636;
    L_0x0381:
        r6 = 32;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x038d:
        r6 = " = ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0399:
        r6 = " += ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x03a5:
        r6 = " -= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x03b1:
        r6 = " *= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x03bd:
        r6 = " /= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x03c9:
        r6 = " %= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x03d5:
        r6 = " |= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x03e1:
        r6 = " ^= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x03ed:
        r6 = " &= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x03f9:
        r6 = " <<= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0405:
        r6 = " >>= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0411:
        r6 = " >>>= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x041d:
        r6 = " ? ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0429:
        r6 = ": ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0435:
        r6 = 1;
        r0 = r17;
        r13 = getNext(r0, r10, r3);
        if (r6 != r13) goto L_0x044a;
    L_0x043e:
        r6 = 58;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x044a:
        r6 = " : ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0456:
        r6 = " || ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0462:
        r6 = " && ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x046e:
        r6 = " | ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x047a:
        r6 = " ^ ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0486:
        r6 = " & ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0492:
        r6 = " === ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x049e:
        r6 = " !== ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x04aa:
        r6 = " == ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x04b6:
        r6 = " != ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x04c2:
        r6 = " <= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x04ce:
        r6 = " < ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x04da:
        r6 = " >= ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x04e6:
        r6 = " > ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x04f2:
        r6 = " instanceof ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x04fe:
        r6 = " << ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x050a:
        r6 = " >> ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0516:
        r6 = " >>> ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0522:
        r6 = "typeof ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x052e:
        r6 = "void ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x053a:
        r6 = "const ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0546:
        r6 = "yield ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0552:
        r6 = 33;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x055e:
        r6 = 126; // 0x7e float:1.77E-43 double:6.23E-322;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x056a:
        r6 = 43;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0576:
        r6 = 45;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0582:
        r6 = "++";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x058e:
        r6 = "--";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x059a:
        r6 = " + ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x05a6:
        r6 = " - ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x05b2:
        r6 = " * ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x05be:
        r6 = " / ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x05ca:
        r6 = " % ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x05d6:
        r6 = "::";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x05e2:
        r6 = "..";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x05ee:
        r6 = ".(";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x05fa:
        r6 = 64;
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0606:
        r6 = "debugger;\n";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x0612:
        r6 = " => ";
        r12.append(r6);
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x061e:
        if (r1 != 0) goto L_0x062d;
    L_0x0620:
        if (r9 != 0) goto L_0x0627;
    L_0x0622:
        r1 = 10;
        r12.append(r1);
    L_0x0627:
        r1 = r12.toString();
        goto L_0x0008;
    L_0x062d:
        r1 = 2;
        if (r8 != r1) goto L_0x0627;
    L_0x0630:
        r1 = 41;
        r12.append(r1);
        goto L_0x0627;
    L_0x0636:
        r15 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r2;
        r2 = r15;
        goto L_0x00d2;
    L_0x063d:
        r15 = r3;
        r3 = r2;
        r2 = r15;
        r16 = r5;
        r5 = r4;
        r4 = r16;
        goto L_0x00d2;
    L_0x0647:
        r15 = r4;
        r4 = r2;
        r2 = r15;
        goto L_0x01ee;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.Decompiler.decompile(java.lang.String, int, org.mozilla.javascript.UintMap):java.lang.String");
    }

    private static int getNext(String str, int i, int i2) {
        return i2 + ONLY_BODY_FLAG < i ? str.charAt(i2 + ONLY_BODY_FLAG) : 0;
    }

    private static int getSourceStringEnd(String str, int i) {
        return printSourceString(str, i, false, null);
    }

    private void increaseSourceCapacity(int i) {
        if (i <= this.sourceBuffer.length) {
            Kit.codeBug();
        }
        int length = this.sourceBuffer.length * TO_SOURCE_FLAG;
        if (length >= i) {
            i = length;
        }
        Object obj = new char[i];
        System.arraycopy(this.sourceBuffer, 0, obj, 0, this.sourceTop);
        this.sourceBuffer = obj;
    }

    private static int printSourceNumber(String str, int i, StringBuilder stringBuilder) {
        int i2;
        double d = 0.0d;
        char charAt = str.charAt(i);
        int i3 = i + ONLY_BODY_FLAG;
        if (charAt == 'S') {
            if (stringBuilder != null) {
                d = (double) str.charAt(i3);
            }
            i2 = i3 + ONLY_BODY_FLAG;
        } else if (charAt == 'J' || charAt == 'D') {
            if (stringBuilder != null) {
                long charAt2 = (((((long) str.charAt(i3)) << 48) | (((long) str.charAt(i3 + ONLY_BODY_FLAG)) << 32)) | (((long) str.charAt(i3 + TO_SOURCE_FLAG)) << 16)) | ((long) str.charAt(i3 + CASE_GAP_PROP));
                d = charAt == 'J' ? (double) charAt2 : Double.longBitsToDouble(charAt2);
            }
            i2 = i3 + 4;
        } else {
            throw new RuntimeException();
        }
        if (stringBuilder != null) {
            stringBuilder.append(ScriptRuntime.numberToString(d, 10));
        }
        return i2;
    }

    private static int printSourceString(String str, int i, boolean z, StringBuilder stringBuilder) {
        int charAt = str.charAt(i);
        int i2 = i + ONLY_BODY_FLAG;
        if ((32768 & charAt) != 0) {
            charAt = ((charAt & 32767) << 16) | str.charAt(i2);
            i2 += ONLY_BODY_FLAG;
        }
        if (stringBuilder != null) {
            String substring = str.substring(i2, i2 + charAt);
            if (z) {
                stringBuilder.append('\"');
                stringBuilder.append(ScriptRuntime.escapeString(substring));
                stringBuilder.append('\"');
            } else {
                stringBuilder.append(substring);
            }
        }
        return charAt + i2;
    }

    private String sourceToString(int i) {
        if (i < 0 || this.sourceTop < i) {
            Kit.codeBug();
        }
        return new String(this.sourceBuffer, i, this.sourceTop - i);
    }

    void addEOL(int i) {
        if (i < 0 || i > Token.LAST_TOKEN) {
            throw new IllegalArgumentException();
        }
        append((char) i);
        append('\u0001');
    }

    void addName(String str) {
        addToken(39);
        appendString(str);
    }

    void addNumber(double d) {
        addToken(40);
        long j = (long) d;
        if (((double) j) != d) {
            j = Double.doubleToLongBits(d);
            append('D');
            append((char) ((int) (j >> 48)));
            append((char) ((int) (j >> 32)));
            append((char) ((int) (j >> 16)));
            append((char) ((int) j));
            return;
        }
        if (j < 0) {
            Kit.codeBug();
        }
        if (j <= 65535) {
            append('S');
            append((char) ((int) j));
            return;
        }
        append('J');
        append((char) ((int) (j >> 48)));
        append((char) ((int) (j >> 32)));
        append((char) ((int) (j >> 16)));
        append((char) ((int) j));
    }

    void addRegexp(String str, String str2) {
        addToken(48);
        appendString('/' + str + '/' + str2);
    }

    void addString(String str) {
        addToken(41);
        appendString(str);
    }

    void addToken(int i) {
        if (i < 0 || i > Token.LAST_TOKEN) {
            throw new IllegalArgumentException();
        }
        append((char) i);
    }

    int getCurrentOffset() {
        return this.sourceTop;
    }

    String getEncodedSource() {
        return sourceToString(0);
    }

    int markFunctionEnd(int i) {
        int currentOffset = getCurrentOffset();
        append('\u00a7');
        return currentOffset;
    }

    int markFunctionStart(int i) {
        int currentOffset = getCurrentOffset();
        if (i != 4) {
            addToken(Token.FUNCTION);
            append((char) i);
        }
        return currentOffset;
    }
}
