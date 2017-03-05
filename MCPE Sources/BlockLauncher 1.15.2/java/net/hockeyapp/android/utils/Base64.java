package net.hockeyapp.android.utils;

import java.io.UnsupportedEncodingException;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import net.zhuoweizhang.mcpelauncher.texture.tga.TGAImage.Header;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Token;

public class Base64 {
    public static final int CRLF = 4;
    public static final int DEFAULT = 0;
    public static final int NO_PADDING = 1;
    public static final int NO_WRAP = 2;
    private static final String TAG = "BASE64";
    public static final int URL_SAFE = 8;

    static abstract class Coder {
        public int op;
        public byte[] output;

        Coder() {
        }

        public abstract int maxOutputSize(int i);

        public abstract boolean process(byte[] bArr, int i, int i2, boolean z);
    }

    static class Decoder extends Coder {
        private static final int[] DECODE;
        private static final int[] DECODE_WEBSAFE;
        private static final int EQUALS = -2;
        private static final int SKIP = -1;
        private final int[] alphabet;
        private int state;
        private int value;

        static {
            int[] iArr = new int[EnchantType.flintAndSteel];
            iArr[Base64.DEFAULT] = SKIP;
            iArr[Base64.NO_PADDING] = SKIP;
            iArr[Base64.NO_WRAP] = SKIP;
            iArr[3] = SKIP;
            iArr[Base64.CRLF] = SKIP;
            iArr[5] = SKIP;
            iArr[6] = SKIP;
            iArr[7] = SKIP;
            iArr[Base64.URL_SAFE] = SKIP;
            iArr[9] = SKIP;
            iArr[10] = SKIP;
            iArr[11] = SKIP;
            iArr[12] = SKIP;
            iArr[13] = SKIP;
            iArr[14] = SKIP;
            iArr[15] = SKIP;
            iArr[16] = SKIP;
            iArr[17] = SKIP;
            iArr[18] = SKIP;
            iArr[19] = SKIP;
            iArr[20] = SKIP;
            iArr[21] = SKIP;
            iArr[22] = SKIP;
            iArr[23] = SKIP;
            iArr[24] = SKIP;
            iArr[25] = SKIP;
            iArr[26] = SKIP;
            iArr[27] = SKIP;
            iArr[28] = SKIP;
            iArr[29] = SKIP;
            iArr[30] = SKIP;
            iArr[31] = SKIP;
            iArr[32] = SKIP;
            iArr[33] = SKIP;
            iArr[34] = SKIP;
            iArr[35] = SKIP;
            iArr[36] = SKIP;
            iArr[37] = SKIP;
            iArr[38] = SKIP;
            iArr[39] = SKIP;
            iArr[40] = SKIP;
            iArr[41] = SKIP;
            iArr[42] = SKIP;
            iArr[43] = 62;
            iArr[44] = SKIP;
            iArr[45] = SKIP;
            iArr[46] = SKIP;
            iArr[47] = 63;
            iArr[48] = 52;
            iArr[49] = 53;
            iArr[50] = 54;
            iArr[51] = 55;
            iArr[52] = 56;
            iArr[53] = 57;
            iArr[54] = 58;
            iArr[55] = 59;
            iArr[56] = 60;
            iArr[57] = 61;
            iArr[58] = SKIP;
            iArr[59] = SKIP;
            iArr[60] = SKIP;
            iArr[61] = EQUALS;
            iArr[62] = SKIP;
            iArr[63] = SKIP;
            iArr[64] = SKIP;
            iArr[65] = Base64.DEFAULT;
            iArr[66] = Base64.NO_PADDING;
            iArr[67] = Base64.NO_WRAP;
            iArr[68] = 3;
            iArr[69] = Base64.CRLF;
            iArr[70] = 5;
            iArr[71] = 6;
            iArr[72] = 7;
            iArr[73] = Base64.URL_SAFE;
            iArr[74] = 9;
            iArr[75] = 10;
            iArr[76] = 11;
            iArr[77] = 12;
            iArr[78] = 13;
            iArr[79] = 14;
            iArr[80] = 15;
            iArr[81] = 16;
            iArr[82] = 17;
            iArr[83] = 18;
            iArr[84] = 19;
            iArr[85] = 20;
            iArr[86] = 21;
            iArr[87] = 22;
            iArr[88] = 23;
            iArr[89] = 24;
            iArr[90] = 25;
            iArr[91] = SKIP;
            iArr[92] = SKIP;
            iArr[93] = SKIP;
            iArr[94] = SKIP;
            iArr[95] = SKIP;
            iArr[96] = SKIP;
            iArr[97] = 26;
            iArr[98] = 27;
            iArr[99] = 28;
            iArr[100] = 29;
            iArr[Token.ASSIGN_DIV] = 30;
            iArr[Token.LAST_ASSIGN] = 31;
            iArr[Token.HOOK] = 32;
            iArr[Token.COLON] = 33;
            iArr[Token.OR] = 34;
            iArr[Token.AND] = 35;
            iArr[Token.INC] = 36;
            iArr[Token.DEC] = 37;
            iArr[Token.DOT] = 38;
            iArr[Token.FUNCTION] = 39;
            iArr[Token.EXPORT] = 40;
            iArr[Token.IMPORT] = 41;
            iArr[Token.IF] = 42;
            iArr[Token.ELSE] = 43;
            iArr[Token.SWITCH] = 44;
            iArr[Token.CASE] = 45;
            iArr[Token.DEFAULT] = 46;
            iArr[Token.WHILE] = 47;
            iArr[Token.DO] = 48;
            iArr[Token.FOR] = 49;
            iArr[Token.BREAK] = 50;
            iArr[Token.CONTINUE] = 51;
            iArr[Token.VAR] = SKIP;
            iArr[Token.WITH] = SKIP;
            iArr[Token.CATCH] = SKIP;
            iArr[Token.FINALLY] = SKIP;
            iArr[Token.VOID] = SKIP;
            iArr[Token.RESERVED] = SKIP;
            iArr[Token.EMPTY] = SKIP;
            iArr[Token.BLOCK] = SKIP;
            iArr[Token.LABEL] = SKIP;
            iArr[Token.TARGET] = SKIP;
            iArr[Token.LOOP] = SKIP;
            iArr[Token.EXPR_VOID] = SKIP;
            iArr[Token.EXPR_RESULT] = SKIP;
            iArr[Token.JSR] = SKIP;
            iArr[Token.SCRIPT] = SKIP;
            iArr[Token.TYPEOFNAME] = SKIP;
            iArr[Token.USE_STACK] = SKIP;
            iArr[Token.SETPROP_OP] = SKIP;
            iArr[Token.SETELEM_OP] = SKIP;
            iArr[Token.LOCAL_BLOCK] = SKIP;
            iArr[Token.SET_REF_OP] = SKIP;
            iArr[Token.DOTDOT] = SKIP;
            iArr[Token.COLONCOLON] = SKIP;
            iArr[Token.XML] = SKIP;
            iArr[Token.DOTQUERY] = SKIP;
            iArr[Token.XMLATTR] = SKIP;
            iArr[Token.XMLEND] = SKIP;
            iArr[Token.TO_OBJECT] = SKIP;
            iArr[Token.TO_DOUBLE] = SKIP;
            iArr[Token.GET] = SKIP;
            iArr[Token.SET] = SKIP;
            iArr[Token.LET] = SKIP;
            iArr[Token.CONST] = SKIP;
            iArr[Token.SETCONST] = SKIP;
            iArr[Token.SETCONSTVAR] = SKIP;
            iArr[Token.ARRAYCOMP] = SKIP;
            iArr[Token.LETEXPR] = SKIP;
            iArr[Token.WITHEXPR] = SKIP;
            iArr[Token.DEBUGGER] = SKIP;
            iArr[Token.COMMENT] = SKIP;
            iArr[Token.GENEXPR] = SKIP;
            iArr[Token.METHOD] = SKIP;
            iArr[Token.ARROW] = SKIP;
            iArr[Token.LAST_TOKEN] = SKIP;
            iArr[167] = SKIP;
            iArr[168] = SKIP;
            iArr[169] = SKIP;
            iArr[Context.VERSION_1_7] = SKIP;
            iArr[171] = SKIP;
            iArr[172] = SKIP;
            iArr[173] = SKIP;
            iArr[174] = SKIP;
            iArr[175] = SKIP;
            iArr[176] = SKIP;
            iArr[177] = SKIP;
            iArr[178] = SKIP;
            iArr[179] = SKIP;
            iArr[Context.VERSION_1_8] = SKIP;
            iArr[181] = SKIP;
            iArr[182] = SKIP;
            iArr[183] = SKIP;
            iArr[184] = SKIP;
            iArr[185] = SKIP;
            iArr[186] = SKIP;
            iArr[187] = SKIP;
            iArr[188] = SKIP;
            iArr[189] = SKIP;
            iArr[190] = SKIP;
            iArr[191] = SKIP;
            iArr[Header.ID_INTERLEAVE] = SKIP;
            iArr[193] = SKIP;
            iArr[194] = SKIP;
            iArr[195] = SKIP;
            iArr[196] = SKIP;
            iArr[197] = SKIP;
            iArr[198] = SKIP;
            iArr[199] = SKIP;
            iArr[Context.VERSION_ES6] = SKIP;
            iArr[201] = SKIP;
            iArr[202] = SKIP;
            iArr[203] = SKIP;
            iArr[204] = SKIP;
            iArr[205] = SKIP;
            iArr[206] = SKIP;
            iArr[207] = SKIP;
            iArr[208] = SKIP;
            iArr[209] = SKIP;
            iArr[210] = SKIP;
            iArr[211] = SKIP;
            iArr[212] = SKIP;
            iArr[213] = SKIP;
            iArr[214] = SKIP;
            iArr[215] = SKIP;
            iArr[216] = SKIP;
            iArr[217] = SKIP;
            iArr[218] = SKIP;
            iArr[219] = SKIP;
            iArr[220] = SKIP;
            iArr[221] = SKIP;
            iArr[222] = SKIP;
            iArr[223] = SKIP;
            iArr[224] = SKIP;
            iArr[225] = SKIP;
            iArr[226] = SKIP;
            iArr[227] = SKIP;
            iArr[228] = SKIP;
            iArr[229] = SKIP;
            iArr[230] = SKIP;
            iArr[231] = SKIP;
            iArr[232] = SKIP;
            iArr[233] = SKIP;
            iArr[234] = SKIP;
            iArr[235] = SKIP;
            iArr[236] = SKIP;
            iArr[237] = SKIP;
            iArr[238] = SKIP;
            iArr[239] = SKIP;
            iArr[240] = SKIP;
            iArr[241] = SKIP;
            iArr[242] = SKIP;
            iArr[243] = SKIP;
            iArr[244] = SKIP;
            iArr[245] = SKIP;
            iArr[246] = SKIP;
            iArr[247] = SKIP;
            iArr[248] = SKIP;
            iArr[249] = SKIP;
            iArr[250] = SKIP;
            iArr[251] = SKIP;
            iArr[252] = SKIP;
            iArr[253] = SKIP;
            iArr[254] = SKIP;
            iArr[255] = SKIP;
            DECODE = iArr;
            iArr = new int[EnchantType.flintAndSteel];
            iArr[Base64.DEFAULT] = SKIP;
            iArr[Base64.NO_PADDING] = SKIP;
            iArr[Base64.NO_WRAP] = SKIP;
            iArr[3] = SKIP;
            iArr[Base64.CRLF] = SKIP;
            iArr[5] = SKIP;
            iArr[6] = SKIP;
            iArr[7] = SKIP;
            iArr[Base64.URL_SAFE] = SKIP;
            iArr[9] = SKIP;
            iArr[10] = SKIP;
            iArr[11] = SKIP;
            iArr[12] = SKIP;
            iArr[13] = SKIP;
            iArr[14] = SKIP;
            iArr[15] = SKIP;
            iArr[16] = SKIP;
            iArr[17] = SKIP;
            iArr[18] = SKIP;
            iArr[19] = SKIP;
            iArr[20] = SKIP;
            iArr[21] = SKIP;
            iArr[22] = SKIP;
            iArr[23] = SKIP;
            iArr[24] = SKIP;
            iArr[25] = SKIP;
            iArr[26] = SKIP;
            iArr[27] = SKIP;
            iArr[28] = SKIP;
            iArr[29] = SKIP;
            iArr[30] = SKIP;
            iArr[31] = SKIP;
            iArr[32] = SKIP;
            iArr[33] = SKIP;
            iArr[34] = SKIP;
            iArr[35] = SKIP;
            iArr[36] = SKIP;
            iArr[37] = SKIP;
            iArr[38] = SKIP;
            iArr[39] = SKIP;
            iArr[40] = SKIP;
            iArr[41] = SKIP;
            iArr[42] = SKIP;
            iArr[43] = SKIP;
            iArr[44] = SKIP;
            iArr[45] = 62;
            iArr[46] = SKIP;
            iArr[47] = SKIP;
            iArr[48] = 52;
            iArr[49] = 53;
            iArr[50] = 54;
            iArr[51] = 55;
            iArr[52] = 56;
            iArr[53] = 57;
            iArr[54] = 58;
            iArr[55] = 59;
            iArr[56] = 60;
            iArr[57] = 61;
            iArr[58] = SKIP;
            iArr[59] = SKIP;
            iArr[60] = SKIP;
            iArr[61] = EQUALS;
            iArr[62] = SKIP;
            iArr[63] = SKIP;
            iArr[64] = SKIP;
            iArr[65] = Base64.DEFAULT;
            iArr[66] = Base64.NO_PADDING;
            iArr[67] = Base64.NO_WRAP;
            iArr[68] = 3;
            iArr[69] = Base64.CRLF;
            iArr[70] = 5;
            iArr[71] = 6;
            iArr[72] = 7;
            iArr[73] = Base64.URL_SAFE;
            iArr[74] = 9;
            iArr[75] = 10;
            iArr[76] = 11;
            iArr[77] = 12;
            iArr[78] = 13;
            iArr[79] = 14;
            iArr[80] = 15;
            iArr[81] = 16;
            iArr[82] = 17;
            iArr[83] = 18;
            iArr[84] = 19;
            iArr[85] = 20;
            iArr[86] = 21;
            iArr[87] = 22;
            iArr[88] = 23;
            iArr[89] = 24;
            iArr[90] = 25;
            iArr[91] = SKIP;
            iArr[92] = SKIP;
            iArr[93] = SKIP;
            iArr[94] = SKIP;
            iArr[95] = 63;
            iArr[96] = SKIP;
            iArr[97] = 26;
            iArr[98] = 27;
            iArr[99] = 28;
            iArr[100] = 29;
            iArr[Token.ASSIGN_DIV] = 30;
            iArr[Token.LAST_ASSIGN] = 31;
            iArr[Token.HOOK] = 32;
            iArr[Token.COLON] = 33;
            iArr[Token.OR] = 34;
            iArr[Token.AND] = 35;
            iArr[Token.INC] = 36;
            iArr[Token.DEC] = 37;
            iArr[Token.DOT] = 38;
            iArr[Token.FUNCTION] = 39;
            iArr[Token.EXPORT] = 40;
            iArr[Token.IMPORT] = 41;
            iArr[Token.IF] = 42;
            iArr[Token.ELSE] = 43;
            iArr[Token.SWITCH] = 44;
            iArr[Token.CASE] = 45;
            iArr[Token.DEFAULT] = 46;
            iArr[Token.WHILE] = 47;
            iArr[Token.DO] = 48;
            iArr[Token.FOR] = 49;
            iArr[Token.BREAK] = 50;
            iArr[Token.CONTINUE] = 51;
            iArr[Token.VAR] = SKIP;
            iArr[Token.WITH] = SKIP;
            iArr[Token.CATCH] = SKIP;
            iArr[Token.FINALLY] = SKIP;
            iArr[Token.VOID] = SKIP;
            iArr[Token.RESERVED] = SKIP;
            iArr[Token.EMPTY] = SKIP;
            iArr[Token.BLOCK] = SKIP;
            iArr[Token.LABEL] = SKIP;
            iArr[Token.TARGET] = SKIP;
            iArr[Token.LOOP] = SKIP;
            iArr[Token.EXPR_VOID] = SKIP;
            iArr[Token.EXPR_RESULT] = SKIP;
            iArr[Token.JSR] = SKIP;
            iArr[Token.SCRIPT] = SKIP;
            iArr[Token.TYPEOFNAME] = SKIP;
            iArr[Token.USE_STACK] = SKIP;
            iArr[Token.SETPROP_OP] = SKIP;
            iArr[Token.SETELEM_OP] = SKIP;
            iArr[Token.LOCAL_BLOCK] = SKIP;
            iArr[Token.SET_REF_OP] = SKIP;
            iArr[Token.DOTDOT] = SKIP;
            iArr[Token.COLONCOLON] = SKIP;
            iArr[Token.XML] = SKIP;
            iArr[Token.DOTQUERY] = SKIP;
            iArr[Token.XMLATTR] = SKIP;
            iArr[Token.XMLEND] = SKIP;
            iArr[Token.TO_OBJECT] = SKIP;
            iArr[Token.TO_DOUBLE] = SKIP;
            iArr[Token.GET] = SKIP;
            iArr[Token.SET] = SKIP;
            iArr[Token.LET] = SKIP;
            iArr[Token.CONST] = SKIP;
            iArr[Token.SETCONST] = SKIP;
            iArr[Token.SETCONSTVAR] = SKIP;
            iArr[Token.ARRAYCOMP] = SKIP;
            iArr[Token.LETEXPR] = SKIP;
            iArr[Token.WITHEXPR] = SKIP;
            iArr[Token.DEBUGGER] = SKIP;
            iArr[Token.COMMENT] = SKIP;
            iArr[Token.GENEXPR] = SKIP;
            iArr[Token.METHOD] = SKIP;
            iArr[Token.ARROW] = SKIP;
            iArr[Token.LAST_TOKEN] = SKIP;
            iArr[167] = SKIP;
            iArr[168] = SKIP;
            iArr[169] = SKIP;
            iArr[Context.VERSION_1_7] = SKIP;
            iArr[171] = SKIP;
            iArr[172] = SKIP;
            iArr[173] = SKIP;
            iArr[174] = SKIP;
            iArr[175] = SKIP;
            iArr[176] = SKIP;
            iArr[177] = SKIP;
            iArr[178] = SKIP;
            iArr[179] = SKIP;
            iArr[Context.VERSION_1_8] = SKIP;
            iArr[181] = SKIP;
            iArr[182] = SKIP;
            iArr[183] = SKIP;
            iArr[184] = SKIP;
            iArr[185] = SKIP;
            iArr[186] = SKIP;
            iArr[187] = SKIP;
            iArr[188] = SKIP;
            iArr[189] = SKIP;
            iArr[190] = SKIP;
            iArr[191] = SKIP;
            iArr[Header.ID_INTERLEAVE] = SKIP;
            iArr[193] = SKIP;
            iArr[194] = SKIP;
            iArr[195] = SKIP;
            iArr[196] = SKIP;
            iArr[197] = SKIP;
            iArr[198] = SKIP;
            iArr[199] = SKIP;
            iArr[Context.VERSION_ES6] = SKIP;
            iArr[201] = SKIP;
            iArr[202] = SKIP;
            iArr[203] = SKIP;
            iArr[204] = SKIP;
            iArr[205] = SKIP;
            iArr[206] = SKIP;
            iArr[207] = SKIP;
            iArr[208] = SKIP;
            iArr[209] = SKIP;
            iArr[210] = SKIP;
            iArr[211] = SKIP;
            iArr[212] = SKIP;
            iArr[213] = SKIP;
            iArr[214] = SKIP;
            iArr[215] = SKIP;
            iArr[216] = SKIP;
            iArr[217] = SKIP;
            iArr[218] = SKIP;
            iArr[219] = SKIP;
            iArr[220] = SKIP;
            iArr[221] = SKIP;
            iArr[222] = SKIP;
            iArr[223] = SKIP;
            iArr[224] = SKIP;
            iArr[225] = SKIP;
            iArr[226] = SKIP;
            iArr[227] = SKIP;
            iArr[228] = SKIP;
            iArr[229] = SKIP;
            iArr[230] = SKIP;
            iArr[231] = SKIP;
            iArr[232] = SKIP;
            iArr[233] = SKIP;
            iArr[234] = SKIP;
            iArr[235] = SKIP;
            iArr[236] = SKIP;
            iArr[237] = SKIP;
            iArr[238] = SKIP;
            iArr[239] = SKIP;
            iArr[240] = SKIP;
            iArr[241] = SKIP;
            iArr[242] = SKIP;
            iArr[243] = SKIP;
            iArr[244] = SKIP;
            iArr[245] = SKIP;
            iArr[246] = SKIP;
            iArr[247] = SKIP;
            iArr[248] = SKIP;
            iArr[249] = SKIP;
            iArr[250] = SKIP;
            iArr[251] = SKIP;
            iArr[252] = SKIP;
            iArr[253] = SKIP;
            iArr[254] = SKIP;
            iArr[255] = SKIP;
            DECODE_WEBSAFE = iArr;
        }

        public Decoder(int i, byte[] bArr) {
            this.output = bArr;
            this.alphabet = (i & Base64.URL_SAFE) == 0 ? DECODE : DECODE_WEBSAFE;
            this.state = Base64.DEFAULT;
            this.value = Base64.DEFAULT;
        }

        public int maxOutputSize(int i) {
            return ((i * 3) / Base64.CRLF) + 10;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean process(byte[] r11, int r12, int r13, boolean r14) {
            /*
            r10 = this;
            r0 = r10.state;
            r1 = 6;
            if (r0 != r1) goto L_0x0007;
        L_0x0005:
            r0 = 0;
        L_0x0006:
            return r0;
        L_0x0007:
            r4 = r13 + r12;
            r3 = r10.state;
            r1 = r10.value;
            r0 = 0;
            r5 = r10.output;
            r6 = r10.alphabet;
            r2 = r12;
        L_0x0013:
            if (r2 >= r4) goto L_0x0133;
        L_0x0015:
            if (r3 != 0) goto L_0x0067;
        L_0x0017:
            r7 = r2 + 4;
            if (r7 > r4) goto L_0x005a;
        L_0x001b:
            r1 = r11[r2];
            r1 = r1 & 255;
            r1 = r6[r1];
            r1 = r1 << 18;
            r7 = r2 + 1;
            r7 = r11[r7];
            r7 = r7 & 255;
            r7 = r6[r7];
            r7 = r7 << 12;
            r1 = r1 | r7;
            r7 = r2 + 2;
            r7 = r11[r7];
            r7 = r7 & 255;
            r7 = r6[r7];
            r7 = r7 << 6;
            r1 = r1 | r7;
            r7 = r2 + 3;
            r7 = r11[r7];
            r7 = r7 & 255;
            r7 = r6[r7];
            r1 = r1 | r7;
            if (r1 < 0) goto L_0x005a;
        L_0x0044:
            r7 = r0 + 2;
            r8 = (byte) r1;
            r5[r7] = r8;
            r7 = r0 + 1;
            r8 = r1 >> 8;
            r8 = (byte) r8;
            r5[r7] = r8;
            r7 = r1 >> 16;
            r7 = (byte) r7;
            r5[r0] = r7;
            r0 = r0 + 3;
            r2 = r2 + 4;
            goto L_0x0017;
        L_0x005a:
            if (r2 < r4) goto L_0x0067;
        L_0x005c:
            r2 = r1;
        L_0x005d:
            if (r14 != 0) goto L_0x0105;
        L_0x005f:
            r10.state = r3;
            r10.value = r2;
            r10.op = r0;
            r0 = 1;
            goto L_0x0006;
        L_0x0067:
            r12 = r2 + 1;
            r2 = r11[r2];
            r2 = r2 & 255;
            r2 = r6[r2];
            switch(r3) {
                case 0: goto L_0x0076;
                case 1: goto L_0x0086;
                case 2: goto L_0x0097;
                case 3: goto L_0x00b7;
                case 4: goto L_0x00ed;
                case 5: goto L_0x00fc;
                default: goto L_0x0072;
            };
        L_0x0072:
            r2 = r3;
        L_0x0073:
            r3 = r2;
            r2 = r12;
            goto L_0x0013;
        L_0x0076:
            if (r2 < 0) goto L_0x007e;
        L_0x0078:
            r1 = r3 + 1;
            r9 = r2;
            r2 = r1;
            r1 = r9;
            goto L_0x0073;
        L_0x007e:
            r7 = -1;
            if (r2 == r7) goto L_0x0072;
        L_0x0081:
            r0 = 6;
            r10.state = r0;
            r0 = 0;
            goto L_0x0006;
        L_0x0086:
            if (r2 < 0) goto L_0x008e;
        L_0x0088:
            r1 = r1 << 6;
            r1 = r1 | r2;
            r2 = r3 + 1;
            goto L_0x0073;
        L_0x008e:
            r7 = -1;
            if (r2 == r7) goto L_0x0072;
        L_0x0091:
            r0 = 6;
            r10.state = r0;
            r0 = 0;
            goto L_0x0006;
        L_0x0097:
            if (r2 < 0) goto L_0x009f;
        L_0x0099:
            r1 = r1 << 6;
            r1 = r1 | r2;
            r2 = r3 + 1;
            goto L_0x0073;
        L_0x009f:
            r7 = -2;
            if (r2 != r7) goto L_0x00ae;
        L_0x00a2:
            r2 = r0 + 1;
            r3 = r1 >> 4;
            r3 = (byte) r3;
            r5[r0] = r3;
            r0 = 4;
            r9 = r2;
            r2 = r0;
            r0 = r9;
            goto L_0x0073;
        L_0x00ae:
            r7 = -1;
            if (r2 == r7) goto L_0x0072;
        L_0x00b1:
            r0 = 6;
            r10.state = r0;
            r0 = 0;
            goto L_0x0006;
        L_0x00b7:
            if (r2 < 0) goto L_0x00d1;
        L_0x00b9:
            r1 = r1 << 6;
            r1 = r1 | r2;
            r2 = r0 + 2;
            r3 = (byte) r1;
            r5[r2] = r3;
            r2 = r0 + 1;
            r3 = r1 >> 8;
            r3 = (byte) r3;
            r5[r2] = r3;
            r2 = r1 >> 16;
            r2 = (byte) r2;
            r5[r0] = r2;
            r0 = r0 + 3;
            r2 = 0;
            goto L_0x0073;
        L_0x00d1:
            r7 = -2;
            if (r2 != r7) goto L_0x00e4;
        L_0x00d4:
            r2 = r0 + 1;
            r3 = r1 >> 2;
            r3 = (byte) r3;
            r5[r2] = r3;
            r2 = r1 >> 10;
            r2 = (byte) r2;
            r5[r0] = r2;
            r0 = r0 + 2;
            r2 = 5;
            goto L_0x0073;
        L_0x00e4:
            r7 = -1;
            if (r2 == r7) goto L_0x0072;
        L_0x00e7:
            r0 = 6;
            r10.state = r0;
            r0 = 0;
            goto L_0x0006;
        L_0x00ed:
            r7 = -2;
            if (r2 != r7) goto L_0x00f3;
        L_0x00f0:
            r2 = r3 + 1;
            goto L_0x0073;
        L_0x00f3:
            r7 = -1;
            if (r2 == r7) goto L_0x0072;
        L_0x00f6:
            r0 = 6;
            r10.state = r0;
            r0 = 0;
            goto L_0x0006;
        L_0x00fc:
            r7 = -1;
            if (r2 == r7) goto L_0x0072;
        L_0x00ff:
            r0 = 6;
            r10.state = r0;
            r0 = 0;
            goto L_0x0006;
        L_0x0105:
            switch(r3) {
                case 0: goto L_0x0108;
                case 1: goto L_0x010f;
                case 2: goto L_0x0115;
                case 3: goto L_0x011e;
                case 4: goto L_0x012d;
                default: goto L_0x0108;
            };
        L_0x0108:
            r10.state = r3;
            r10.op = r0;
            r0 = 1;
            goto L_0x0006;
        L_0x010f:
            r0 = 6;
            r10.state = r0;
            r0 = 0;
            goto L_0x0006;
        L_0x0115:
            r1 = r0 + 1;
            r2 = r2 >> 4;
            r2 = (byte) r2;
            r5[r0] = r2;
            r0 = r1;
            goto L_0x0108;
        L_0x011e:
            r1 = r0 + 1;
            r4 = r2 >> 10;
            r4 = (byte) r4;
            r5[r0] = r4;
            r0 = r1 + 1;
            r2 = r2 >> 2;
            r2 = (byte) r2;
            r5[r1] = r2;
            goto L_0x0108;
        L_0x012d:
            r0 = 6;
            r10.state = r0;
            r0 = 0;
            goto L_0x0006;
        L_0x0133:
            r2 = r1;
            goto L_0x005d;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.utils.Base64.Decoder.process(byte[], int, int, boolean):boolean");
        }
    }

    static class Encoder extends Coder {
        private static final byte[] ENCODE = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
        private static final byte[] ENCODE_WEBSAFE = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 45, (byte) 95};
        public static final int LINE_GROUPS = 19;
        private final byte[] alphabet;
        private int count;
        public final boolean do_cr;
        public final boolean do_newline;
        public final boolean do_padding;
        private final byte[] tail;
        int tailLen;

        public Encoder(int i, byte[] bArr) {
            boolean z = true;
            this.output = bArr;
            this.do_padding = (i & Base64.NO_PADDING) == 0;
            this.do_newline = (i & Base64.NO_WRAP) == 0;
            if ((i & Base64.CRLF) == 0) {
                z = false;
            }
            this.do_cr = z;
            this.alphabet = (i & Base64.URL_SAFE) == 0 ? ENCODE : ENCODE_WEBSAFE;
            this.tail = new byte[Base64.NO_WRAP];
            this.tailLen = Base64.DEFAULT;
            this.count = this.do_newline ? LINE_GROUPS : -1;
        }

        public int maxOutputSize(int i) {
            return ((i * Base64.URL_SAFE) / 5) + 10;
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean process(byte[] r12, int r13, int r14, boolean r15) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:42)
	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:66)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:173)
*/
            /*
            r11 = this;
            r6 = r11.alphabet;
            r7 = r11.output;
            r1 = 0;
            r0 = r11.count;
            r8 = r14 + r13;
            r2 = -1;
            r3 = r11.tailLen;
            switch(r3) {
                case 1: goto L_0x00a8;
                case 2: goto L_0x00cc;
                default: goto L_0x000f;
            };
        L_0x000f:
            r3 = r2;
            r2 = r13;
        L_0x0011:
            r4 = -1;
            if (r3 == r4) goto L_0x022f;
        L_0x0014:
            r4 = 1;
            r5 = r3 >> 18;
            r5 = r5 & 63;
            r5 = r6[r5];
            r7[r1] = r5;
            r1 = 2;
            r5 = r3 >> 12;
            r5 = r5 & 63;
            r5 = r6[r5];
            r7[r4] = r5;
            r4 = 3;
            r5 = r3 >> 6;
            r5 = r5 & 63;
            r5 = r6[r5];
            r7[r1] = r5;
            r1 = 4;
            r3 = r3 & 63;
            r3 = r6[r3];
            r7[r4] = r3;
            r0 = r0 + -1;
            if (r0 != 0) goto L_0x022f;
        L_0x003a:
            r0 = r11.do_cr;
            if (r0 == 0) goto L_0x0240;
        L_0x003e:
            r0 = 5;
            r3 = 13;
            r7[r1] = r3;
        L_0x0043:
            r4 = r0 + 1;
            r1 = 10;
            r7[r0] = r1;
            r1 = 19;
            r5 = r1;
        L_0x004c:
            r0 = r2 + 3;
            if (r0 > r8) goto L_0x00f2;
        L_0x0050:
            r0 = r12[r2];
            r0 = r0 & 255;
            r0 = r0 << 16;
            r1 = r2 + 1;
            r1 = r12[r1];
            r1 = r1 & 255;
            r1 = r1 << 8;
            r0 = r0 | r1;
            r1 = r2 + 2;
            r1 = r12[r1];
            r1 = r1 & 255;
            r0 = r0 | r1;
            r1 = r0 >> 18;
            r1 = r1 & 63;
            r1 = r6[r1];
            r7[r4] = r1;
            r1 = r4 + 1;
            r3 = r0 >> 12;
            r3 = r3 & 63;
            r3 = r6[r3];
            r7[r1] = r3;
            r1 = r4 + 2;
            r3 = r0 >> 6;
            r3 = r3 & 63;
            r3 = r6[r3];
            r7[r1] = r3;
            r1 = r4 + 3;
            r0 = r0 & 63;
            r0 = r6[r0];
            r7[r1] = r0;
            r0 = r2 + 3;
            r2 = r4 + 4;
            r1 = r5 + -1;
            if (r1 != 0) goto L_0x0233;
        L_0x0092:
            r1 = r11.do_cr;
            if (r1 == 0) goto L_0x009d;
        L_0x0096:
            r1 = r2 + 1;
            r3 = 13;
            r7[r2] = r3;
            r2 = r1;
        L_0x009d:
            r4 = r2 + 1;
            r1 = 10;
            r7[r2] = r1;
            r1 = 19;
            r2 = r0;
            r5 = r1;
            goto L_0x004c;
        L_0x00a8:
            r3 = r13 + 2;
            if (r3 > r8) goto L_0x000f;
        L_0x00ac:
            r2 = r11.tail;
            r3 = 0;
            r2 = r2[r3];
            r2 = r2 & 255;
            r2 = r2 << 16;
            r3 = r13 + 1;
            r4 = r12[r13];
            r4 = r4 & 255;
            r4 = r4 << 8;
            r2 = r2 | r4;
            r13 = r3 + 1;
            r3 = r12[r3];
            r3 = r3 & 255;
            r2 = r2 | r3;
            r3 = 0;
            r11.tailLen = r3;
            r3 = r2;
            r2 = r13;
            goto L_0x0011;
        L_0x00cc:
            r3 = r13 + 1;
            if (r3 > r8) goto L_0x000f;
        L_0x00d0:
            r2 = r11.tail;
            r3 = 0;
            r2 = r2[r3];
            r2 = r2 & 255;
            r2 = r2 << 16;
            r3 = r11.tail;
            r4 = 1;
            r3 = r3[r4];
            r3 = r3 & 255;
            r3 = r3 << 8;
            r2 = r2 | r3;
            r3 = r13 + 1;
            r4 = r12[r13];
            r4 = r4 & 255;
            r2 = r2 | r4;
            r4 = 0;
            r11.tailLen = r4;
            r10 = r2;
            r2 = r3;
            r3 = r10;
            goto L_0x0011;
        L_0x00f2:
            if (r15 == 0) goto L_0x01fb;
        L_0x00f4:
            r0 = r11.tailLen;
            r0 = r2 - r0;
            r1 = r8 + -1;
            if (r0 != r1) goto L_0x0168;
        L_0x00fc:
            r1 = 0;
            r0 = r11.tailLen;
            if (r0 <= 0) goto L_0x0162;
        L_0x0101:
            r0 = r11.tail;
            r1 = 1;
            r3 = 0;
            r0 = r0[r3];
        L_0x0107:
            r0 = r0 & 255;
            r3 = r0 << 4;
            r0 = r11.tailLen;
            r0 = r0 - r1;
            r11.tailLen = r0;
            r1 = r4 + 1;
            r0 = r3 >> 6;
            r0 = r0 & 63;
            r0 = r6[r0];
            r7[r4] = r0;
            r0 = r1 + 1;
            r3 = r3 & 63;
            r3 = r6[r3];
            r7[r1] = r3;
            r1 = r11.do_padding;
            if (r1 == 0) goto L_0x0132;
        L_0x0126:
            r1 = r0 + 1;
            r3 = 61;
            r7[r0] = r3;
            r0 = r1 + 1;
            r3 = 61;
            r7[r1] = r3;
        L_0x0132:
            r1 = r11.do_newline;
            if (r1 == 0) goto L_0x0148;
        L_0x0136:
            r1 = r11.do_cr;
            if (r1 == 0) goto L_0x0141;
        L_0x013a:
            r1 = r0 + 1;
            r3 = 13;
            r7[r0] = r3;
            r0 = r1;
        L_0x0141:
            r1 = r0 + 1;
            r3 = 10;
            r7[r0] = r3;
            r0 = r1;
        L_0x0148:
            r1 = r11.tailLen;
            if (r1 == 0) goto L_0x0153;
        L_0x014c:
            r1 = "BASE64";
            r3 = "Error during encoding";
            net.hockeyapp.android.utils.HockeyLog.error(r1, r3);
        L_0x0153:
            if (r2 == r8) goto L_0x015c;
        L_0x0155:
            r1 = "BASE64";
            r2 = "Error during encoding";
            net.hockeyapp.android.utils.HockeyLog.error(r1, r2);
        L_0x015c:
            r11.op = r0;
            r11.count = r5;
            r0 = 1;
            return r0;
        L_0x0162:
            r3 = r2 + 1;
            r0 = r12[r2];
            r2 = r3;
            goto L_0x0107;
        L_0x0168:
            r0 = r11.tailLen;
            r0 = r2 - r0;
            r1 = r8 + -2;
            if (r0 != r1) goto L_0x01de;
        L_0x0170:
            r1 = 0;
            r0 = r11.tailLen;
            r3 = 1;
            if (r0 <= r3) goto L_0x01d2;
        L_0x0176:
            r0 = r11.tail;
            r1 = 1;
            r3 = 0;
            r0 = r0[r3];
        L_0x017c:
            r0 = r0 & 255;
            r9 = r0 << 10;
            r0 = r11.tailLen;
            if (r0 <= 0) goto L_0x01d8;
        L_0x0184:
            r0 = r11.tail;
            r3 = r1 + 1;
            r0 = r0[r1];
            r1 = r3;
        L_0x018b:
            r0 = r0 & 255;
            r0 = r0 << 2;
            r0 = r0 | r9;
            r3 = r11.tailLen;
            r1 = r3 - r1;
            r11.tailLen = r1;
            r1 = r4 + 1;
            r3 = r0 >> 12;
            r3 = r3 & 63;
            r3 = r6[r3];
            r7[r4] = r3;
            r3 = r1 + 1;
            r4 = r0 >> 6;
            r4 = r4 & 63;
            r4 = r6[r4];
            r7[r1] = r4;
            r1 = r3 + 1;
            r0 = r0 & 63;
            r0 = r6[r0];
            r7[r3] = r0;
            r0 = r11.do_padding;
            if (r0 == 0) goto L_0x023d;
        L_0x01b6:
            r0 = r1 + 1;
            r3 = 61;
            r7[r1] = r3;
        L_0x01bc:
            r1 = r11.do_newline;
            if (r1 == 0) goto L_0x0148;
        L_0x01c0:
            r1 = r11.do_cr;
            if (r1 == 0) goto L_0x023b;
        L_0x01c4:
            r1 = r0 + 1;
            r3 = 13;
            r7[r0] = r3;
        L_0x01ca:
            r0 = r1 + 1;
            r3 = 10;
            r7[r1] = r3;
            goto L_0x0148;
        L_0x01d2:
            r3 = r2 + 1;
            r0 = r12[r2];
            r2 = r3;
            goto L_0x017c;
        L_0x01d8:
            r3 = r2 + 1;
            r0 = r12[r2];
            r2 = r3;
            goto L_0x018b;
        L_0x01de:
            r0 = r11.do_newline;
            if (r0 == 0) goto L_0x0238;
        L_0x01e2:
            if (r4 <= 0) goto L_0x0238;
        L_0x01e4:
            r0 = 19;
            if (r5 == r0) goto L_0x0238;
        L_0x01e8:
            r0 = r11.do_cr;
            if (r0 == 0) goto L_0x01f3;
        L_0x01ec:
            r0 = r4 + 1;
            r1 = 13;
            r7[r4] = r1;
            r4 = r0;
        L_0x01f3:
            r0 = r4 + 1;
            r1 = 10;
            r7[r4] = r1;
            goto L_0x0148;
        L_0x01fb:
            r0 = r8 + -1;
            if (r2 != r0) goto L_0x020e;
        L_0x01ff:
            r0 = r11.tail;
            r1 = r11.tailLen;
            r3 = r1 + 1;
            r11.tailLen = r3;
            r2 = r12[r2];
            r0[r1] = r2;
            r0 = r4;
            goto L_0x015c;
        L_0x020e:
            r0 = r8 + -2;
            if (r2 != r0) goto L_0x022c;
        L_0x0212:
            r0 = r11.tail;
            r1 = r11.tailLen;
            r3 = r1 + 1;
            r11.tailLen = r3;
            r3 = r12[r2];
            r0[r1] = r3;
            r0 = r11.tail;
            r1 = r11.tailLen;
            r3 = r1 + 1;
            r11.tailLen = r3;
            r2 = r2 + 1;
            r2 = r12[r2];
            r0[r1] = r2;
        L_0x022c:
            r0 = r4;
            goto L_0x015c;
        L_0x022f:
            r10 = r2;
            r2 = r1;
            r1 = r0;
            r0 = r10;
        L_0x0233:
            r4 = r2;
            r5 = r1;
            r2 = r0;
            goto L_0x004c;
        L_0x0238:
            r0 = r4;
            goto L_0x0148;
        L_0x023b:
            r1 = r0;
            goto L_0x01ca;
        L_0x023d:
            r0 = r1;
            goto L_0x01bc;
        L_0x0240:
            r0 = r1;
            goto L_0x0043;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.utils.Base64.Encoder.process(byte[], int, int, boolean):boolean");
        }
    }

    private Base64() {
    }

    public static byte[] decode(String str, int i) {
        return decode(str.getBytes(), i);
    }

    public static byte[] decode(byte[] bArr, int i) {
        return decode(bArr, DEFAULT, bArr.length, i);
    }

    public static byte[] decode(byte[] bArr, int i, int i2, int i3) {
        Decoder decoder = new Decoder(i3, new byte[((i2 * 3) / CRLF)]);
        if (!decoder.process(bArr, i, i2, true)) {
            throw new IllegalArgumentException("bad base-64");
        } else if (decoder.op == decoder.output.length) {
            return decoder.output;
        } else {
            Object obj = new byte[decoder.op];
            System.arraycopy(decoder.output, DEFAULT, obj, DEFAULT, decoder.op);
            return obj;
        }
    }

    public static byte[] encode(byte[] bArr, int i) {
        return encode(bArr, DEFAULT, bArr.length, i);
    }

    public static byte[] encode(byte[] bArr, int i, int i2, int i3) {
        Encoder encoder = new Encoder(i3, null);
        int i4 = (i2 / 3) * CRLF;
        if (!encoder.do_padding) {
            switch (i2 % 3) {
                case DEFAULT /*0*/:
                    break;
                case NO_PADDING /*1*/:
                    i4 += NO_WRAP;
                    break;
                case NO_WRAP /*2*/:
                    i4 += 3;
                    break;
                default:
                    break;
            }
        } else if (i2 % 3 > 0) {
            i4 += CRLF;
        }
        if (encoder.do_newline && i2 > 0) {
            i4 += (encoder.do_cr ? NO_WRAP : NO_PADDING) * (((i2 - 1) / 57) + NO_PADDING);
        }
        encoder.output = new byte[i4];
        encoder.process(bArr, i, i2, true);
        if (encoder.op == i4) {
            return encoder.output;
        }
        throw new AssertionError();
    }

    public static String encodeToString(byte[] bArr, int i) {
        try {
            return new String(encode(bArr, i), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static String encodeToString(byte[] bArr, int i, int i2, int i3) {
        try {
            return new String(encode(bArr, i, i2, i3), "US-ASCII");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
