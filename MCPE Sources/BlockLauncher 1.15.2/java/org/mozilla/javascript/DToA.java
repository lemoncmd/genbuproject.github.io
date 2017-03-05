package org.mozilla.javascript;

import com.microsoft.cll.android.EventEnums;
import com.microsoft.onlineid.internal.sso.client.MigrationManager;
import java.math.BigInteger;

class DToA {
    private static final int Bias = 1023;
    private static final int Bletch = 16;
    private static final int Bndry_mask = 1048575;
    static final int DTOSTR_EXPONENTIAL = 3;
    static final int DTOSTR_FIXED = 2;
    static final int DTOSTR_PRECISION = 4;
    static final int DTOSTR_STANDARD = 0;
    static final int DTOSTR_STANDARD_EXPONENTIAL = 1;
    private static final int Exp_11 = 1072693248;
    private static final int Exp_mask = 2146435072;
    private static final int Exp_mask_shifted = 2047;
    private static final int Exp_msk1 = 1048576;
    private static final long Exp_msk1L = 4503599627370496L;
    private static final int Exp_shift = 20;
    private static final int Exp_shift1 = 20;
    private static final int Exp_shiftL = 52;
    private static final int Frac_mask = 1048575;
    private static final int Frac_mask1 = 1048575;
    private static final long Frac_maskL = 4503599627370495L;
    private static final int Int_max = 14;
    private static final int Log2P = 1;
    private static final int P = 53;
    private static final int Quick_max = 14;
    private static final int Sign_bit = Integer.MIN_VALUE;
    private static final int Ten_pmax = 22;
    private static final double[] bigtens = new double[]{1.0E16d, 1.0E32d, 1.0E64d, 1.0E128d, 1.0E256d};
    private static final int[] dtoaModes = new int[]{DTOSTR_STANDARD, DTOSTR_STANDARD, DTOSTR_EXPONENTIAL, DTOSTR_FIXED, DTOSTR_FIXED};
    private static final int n_bigtens = 5;
    private static final double[] tens = new double[]{1.0d, EventEnums.SampleRate_10_percent, EventEnums.SampleRate_NoSampling, 1000.0d, 10000.0d, 100000.0d, 1000000.0d, 1.0E7d, 1.0E8d, 1.0E9d, 1.0E10d, 1.0E11d, 1.0E12d, 1.0E13d, 1.0E14d, 1.0E15d, 1.0E16d, 1.0E17d, 1.0E18d, 1.0E19d, 1.0E20d, 1.0E21d, 1.0E22d};

    DToA() {
    }

    private static char BASEDIGIT(int i) {
        return (char) (i >= 10 ? i + 87 : i + 48);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int JS_dtoa(double r34, int r36, boolean r37, int r38, boolean[] r39, java.lang.StringBuilder r40) {
        /*
        r4 = 1;
        r0 = new int[r4];
        r25 = r0;
        r4 = 1;
        r0 = new int[r4];
        r26 = r0;
        r4 = word0(r34);
        r5 = -2147483648; // 0xffffffff80000000 float:-0.0 double:NaN;
        r4 = r4 & r5;
        if (r4 == 0) goto L_0x004a;
    L_0x0013:
        r4 = 0;
        r5 = 1;
        r39[r4] = r5;
        r4 = word0(r34);
        r5 = 2147483647; // 0x7fffffff float:NaN double:1.060997895E-314;
        r4 = r4 & r5;
        r0 = r34;
        r34 = setWord0(r0, r4);
    L_0x0025:
        r4 = word0(r34);
        r5 = 2146435072; // 0x7ff00000 float:NaN double:1.06047983E-314;
        r4 = r4 & r5;
        r5 = 2146435072; // 0x7ff00000 float:NaN double:1.06047983E-314;
        if (r4 != r5) goto L_0x0052;
    L_0x0030:
        r4 = word1(r34);
        if (r4 != 0) goto L_0x004f;
    L_0x0036:
        r4 = word0(r34);
        r5 = 1048575; // 0xfffff float:1.469367E-39 double:5.18065E-318;
        r4 = r4 & r5;
        if (r4 != 0) goto L_0x004f;
    L_0x0040:
        r4 = "Infinity";
    L_0x0042:
        r0 = r40;
        r0.append(r4);
        r4 = 9999; // 0x270f float:1.4012E-41 double:4.94E-320;
    L_0x0049:
        return r4;
    L_0x004a:
        r4 = 0;
        r5 = 0;
        r39[r4] = r5;
        goto L_0x0025;
    L_0x004f:
        r4 = "NaN";
        goto L_0x0042;
    L_0x0052:
        r4 = 0;
        r4 = (r34 > r4 ? 1 : (r34 == r4 ? 0 : -1));
        if (r4 != 0) goto L_0x0067;
    L_0x0058:
        r4 = 0;
        r0 = r40;
        r0.setLength(r4);
        r4 = 48;
        r0 = r40;
        r0.append(r4);
        r4 = 1;
        goto L_0x0049;
    L_0x0067:
        r0 = r34;
        r2 = r25;
        r3 = r26;
        r20 = d2b(r0, r2, r3);
        r4 = word0(r34);
        r4 = r4 >>> 20;
        r4 = r4 & 2047;
        if (r4 == 0) goto L_0x013c;
    L_0x007b:
        r5 = word0(r34);
        r6 = 1048575; // 0xfffff float:1.469367E-39 double:5.18065E-318;
        r5 = r5 & r6;
        r6 = 1072693248; // 0x3ff00000 float:1.875 double:5.299808824E-315;
        r5 = r5 | r6;
        r0 = r34;
        r6 = setWord0(r0, r5);
        r5 = r4 + -1023;
        r4 = 0;
        r24 = r4;
        r32 = r6;
        r6 = r5;
        r4 = r32;
    L_0x0096:
        r8 = 4609434218613702656; // 0x3ff8000000000000 float:0.0 double:1.5;
        r4 = r4 - r8;
        r8 = 4598887322496222049; // 0x3fd287a7636f4361 float:4.413627E21 double:0.289529654602168;
        r4 = r4 * r8;
        r8 = 4595512376519870643; // 0x3fc68a288b60c8b3 float:-4.329182E-32 double:0.1760912590558;
        r4 = r4 + r8;
        r8 = (double) r6;
        r10 = 4599094494223104507; // 0x3fd34413509f79fb float:2.1404572E10 double:0.301029995663981;
        r8 = r8 * r10;
        r8 = r8 + r4;
        r4 = (int) r8;
        r10 = 0;
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 >= 0) goto L_0x00bb;
    L_0x00b4:
        r10 = (double) r4;
        r5 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1));
        if (r5 == 0) goto L_0x00bb;
    L_0x00b9:
        r4 = r4 + -1;
    L_0x00bb:
        r5 = 1;
        if (r4 < 0) goto L_0x074a;
    L_0x00be:
        r7 = 22;
        if (r4 > r7) goto L_0x074a;
    L_0x00c2:
        r5 = tens;
        r8 = r5[r4];
        r5 = (r34 > r8 ? 1 : (r34 == r8 ? 0 : -1));
        if (r5 >= 0) goto L_0x00cc;
    L_0x00ca:
        r4 = r4 + -1;
    L_0x00cc:
        r5 = 0;
        r32 = r5;
        r5 = r4;
        r4 = r32;
    L_0x00d2:
        r7 = 0;
        r7 = r26[r7];
        r6 = r7 - r6;
        r21 = r6 + -1;
        if (r21 < 0) goto L_0x017c;
    L_0x00db:
        r19 = 0;
    L_0x00dd:
        if (r5 < 0) goto L_0x0185;
    L_0x00df:
        r18 = 0;
        r21 = r21 + r5;
        r13 = r5;
    L_0x00e4:
        if (r36 < 0) goto L_0x00ec;
    L_0x00e6:
        r6 = 9;
        r0 = r36;
        if (r0 <= r6) goto L_0x0746;
    L_0x00ec:
        r7 = 0;
    L_0x00ed:
        r6 = 1;
        r8 = 5;
        if (r7 <= r8) goto L_0x0741;
    L_0x00f1:
        r7 = r7 + -4;
        r6 = 0;
        r23 = r7;
        r7 = r6;
    L_0x00f7:
        r6 = 1;
        r12 = 0;
        switch(r23) {
            case 0: goto L_0x018d;
            case 1: goto L_0x018d;
            case 2: goto L_0x0195;
            case 3: goto L_0x01a2;
            case 4: goto L_0x0196;
            case 5: goto L_0x01a3;
            default: goto L_0x00fc;
        };
    L_0x00fc:
        r22 = r6;
        r11 = r12;
    L_0x00ff:
        r10 = 0;
        if (r11 < 0) goto L_0x02bf;
    L_0x0102:
        r6 = 14;
        if (r11 > r6) goto L_0x02bf;
    L_0x0106:
        if (r7 == 0) goto L_0x02bf;
    L_0x0108:
        r8 = 0;
        r7 = 2;
        if (r5 <= 0) goto L_0x01f4;
    L_0x010c:
        r6 = tens;
        r9 = r5 & 15;
        r14 = r6[r9];
        r6 = r5 >> 4;
        r9 = r6 & 16;
        if (r9 == 0) goto L_0x0732;
    L_0x0118:
        r6 = r6 & 15;
        r7 = bigtens;
        r9 = 4;
        r16 = r7[r9];
        r16 = r34 / r16;
        r7 = 3;
        r9 = r6;
        r32 = r14;
        r14 = r8;
        r8 = r7;
        r6 = r32;
    L_0x0129:
        if (r9 == 0) goto L_0x01af;
    L_0x012b:
        r15 = r9 & 1;
        if (r15 == 0) goto L_0x0137;
    L_0x012f:
        r8 = r8 + 1;
        r15 = bigtens;
        r28 = r15[r14];
        r6 = r6 * r28;
    L_0x0137:
        r9 = r9 >> 1;
        r14 = r14 + 1;
        goto L_0x0129;
    L_0x013c:
        r4 = 0;
        r4 = r26[r4];
        r5 = 0;
        r5 = r25[r5];
        r4 = r4 + r5;
        r8 = r4 + 1074;
        r4 = 32;
        if (r8 <= r4) goto L_0x0173;
    L_0x0149:
        r4 = word0(r34);
        r4 = (long) r4;
        r6 = 64 - r8;
        r4 = r4 << r6;
        r6 = word1(r34);
        r7 = r8 + -32;
        r6 = r6 >>> r7;
        r6 = (long) r6;
        r4 = r4 | r6;
    L_0x015a:
        r6 = (double) r4;
        r4 = (double) r4;
        r4 = word0(r4);
        r5 = 32505856; // 0x1f00000 float:8.8162076E-38 double:1.60600267E-316;
        r4 = r4 - r5;
        r6 = setWord0(r6, r4);
        r5 = r8 + -1075;
        r4 = 1;
        r24 = r4;
        r32 = r6;
        r6 = r5;
        r4 = r32;
        goto L_0x0096;
    L_0x0173:
        r4 = word1(r34);
        r4 = (long) r4;
        r6 = 32 - r8;
        r4 = r4 << r6;
        goto L_0x015a;
    L_0x017c:
        r0 = r21;
        r0 = -r0;
        r19 = r0;
        r21 = 0;
        goto L_0x00dd;
    L_0x0185:
        r19 = r19 - r5;
        r0 = -r5;
        r18 = r0;
        r13 = 0;
        goto L_0x00e4;
    L_0x018d:
        r12 = -1;
        r38 = 0;
        r22 = r6;
        r11 = r12;
        goto L_0x00ff;
    L_0x0195:
        r6 = 0;
    L_0x0196:
        if (r38 > 0) goto L_0x019a;
    L_0x0198:
        r38 = 1;
    L_0x019a:
        r22 = r6;
        r12 = r38;
        r11 = r38;
        goto L_0x00ff;
    L_0x01a2:
        r6 = 0;
    L_0x01a3:
        r8 = r38 + r5;
        r11 = r8 + 1;
        r12 = r11 + -1;
        if (r11 > 0) goto L_0x073d;
    L_0x01ab:
        r22 = r6;
        goto L_0x00ff;
    L_0x01af:
        r6 = r16 / r6;
        r32 = r8;
        r8 = r6;
        r7 = r32;
    L_0x01b6:
        if (r4 == 0) goto L_0x0724;
    L_0x01b8:
        r14 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r6 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1));
        if (r6 >= 0) goto L_0x0724;
    L_0x01be:
        if (r11 <= 0) goto L_0x0724;
    L_0x01c0:
        if (r12 > 0) goto L_0x021e;
    L_0x01c2:
        r6 = 1;
        r14 = r8;
        r8 = r11;
        r9 = r7;
        r7 = r5;
    L_0x01c7:
        r0 = (double) r9;
        r16 = r0;
        r16 = r16 * r14;
        r28 = 4619567317775286272; // 0x401c000000000000 float:0.0 double:7.0;
        r16 = r16 + r28;
        r9 = word0(r16);
        r10 = 54525952; // 0x3400000 float:5.642373E-37 double:2.69393997E-316;
        r9 = r9 - r10;
        r0 = r16;
        r28 = setWord0(r0, r9);
        if (r8 != 0) goto L_0x0720;
    L_0x01df:
        r16 = 4617315517961601024; // 0x4014000000000000 float:0.0 double:5.0;
        r16 = r14 - r16;
        r6 = (r16 > r28 ? 1 : (r16 == r28 ? 0 : -1));
        if (r6 <= 0) goto L_0x022b;
    L_0x01e7:
        r4 = 49;
        r0 = r40;
        r0.append(r4);
        r4 = r7 + 1;
        r4 = r4 + 1;
        goto L_0x0049;
    L_0x01f4:
        r6 = -r5;
        if (r6 == 0) goto L_0x072e;
    L_0x01f7:
        r9 = tens;
        r14 = r6 & 15;
        r14 = r9[r14];
        r14 = r14 * r34;
        r6 = r6 >> 4;
        r32 = r6;
        r6 = r7;
        r7 = r32;
        r33 = r8;
        r8 = r14;
        r14 = r33;
    L_0x020b:
        if (r7 == 0) goto L_0x072b;
    L_0x020d:
        r15 = r7 & 1;
        if (r15 == 0) goto L_0x0219;
    L_0x0211:
        r6 = r6 + 1;
        r15 = bigtens;
        r16 = r15[r14];
        r8 = r8 * r16;
    L_0x0219:
        r7 = r7 >> 1;
        r14 = r14 + 1;
        goto L_0x020b;
    L_0x021e:
        r6 = r5 + -1;
        r14 = 4621819117588971520; // 0x4024000000000000 float:0.0 double:10.0;
        r8 = r8 * r14;
        r7 = r7 + 1;
        r14 = r8;
        r8 = r12;
        r9 = r7;
        r7 = r6;
        r6 = r10;
        goto L_0x01c7;
    L_0x022b:
        r0 = r28;
        r14 = -r0;
        r6 = (r16 > r14 ? 1 : (r16 == r14 ? 0 : -1));
        if (r6 >= 0) goto L_0x0242;
    L_0x0232:
        r4 = 0;
        r0 = r40;
        r0.setLength(r4);
        r4 = 48;
        r0 = r40;
        r0.append(r4);
        r4 = 1;
        goto L_0x0049;
    L_0x0242:
        r6 = 1;
    L_0x0243:
        if (r6 != 0) goto L_0x071c;
    L_0x0245:
        r9 = 1;
        if (r22 == 0) goto L_0x02fc;
    L_0x0248:
        r14 = 4602678819172646912; // 0x3fe0000000000000 float:0.0 double:0.5;
        r6 = tens;
        r10 = r8 + -1;
        r30 = r6[r10];
        r14 = r14 / r30;
        r14 = r14 - r28;
        r6 = 0;
    L_0x0255:
        r0 = r16;
        r0 = (long) r0;
        r28 = r0;
        r0 = r28;
        r0 = (double) r0;
        r30 = r0;
        r16 = r16 - r30;
        r30 = 48;
        r28 = r28 + r30;
        r0 = r28;
        r10 = (int) r0;
        r10 = (char) r10;
        r0 = r40;
        r0.append(r10);
        r10 = (r16 > r14 ? 1 : (r16 == r14 ? 0 : -1));
        if (r10 >= 0) goto L_0x0276;
    L_0x0272:
        r4 = r7 + 1;
        goto L_0x0049;
    L_0x0276:
        r28 = 4607182418800017408; // 0x3ff0000000000000 float:0.0 double:1.0;
        r28 = r28 - r16;
        r10 = (r28 > r14 ? 1 : (r28 == r14 ? 0 : -1));
        if (r10 >= 0) goto L_0x02b0;
    L_0x027e:
        r4 = r40.length();
        r4 = r4 + -1;
        r0 = r40;
        r4 = r0.charAt(r4);
        r5 = r40.length();
        r5 = r5 + -1;
        r0 = r40;
        r0.setLength(r5);
        r5 = 57;
        if (r4 == r5) goto L_0x02a5;
    L_0x0299:
        r4 = r4 + 1;
        r4 = (char) r4;
        r0 = r40;
        r0.append(r4);
        r4 = r7 + 1;
        goto L_0x0049;
    L_0x02a5:
        r4 = r40.length();
        if (r4 != 0) goto L_0x027e;
    L_0x02ab:
        r7 = r7 + 1;
        r4 = 48;
        goto L_0x0299;
    L_0x02b0:
        r6 = r6 + 1;
        if (r6 < r8) goto L_0x02f2;
    L_0x02b4:
        r6 = r9;
        r14 = r16;
    L_0x02b7:
        if (r6 == 0) goto L_0x0713;
    L_0x02b9:
        r6 = 0;
        r0 = r40;
        r0.setLength(r6);
    L_0x02bf:
        r6 = 0;
        r6 = r25[r6];
        if (r6 < 0) goto L_0x03e3;
    L_0x02c4:
        r6 = 14;
        if (r5 > r6) goto L_0x03e3;
    L_0x02c8:
        r4 = tens;
        r6 = r4[r5];
        if (r38 >= 0) goto L_0x037b;
    L_0x02ce:
        if (r11 > 0) goto L_0x037b;
    L_0x02d0:
        if (r11 < 0) goto L_0x02e2;
    L_0x02d2:
        r8 = 4617315517961601024; // 0x4014000000000000 float:0.0 double:5.0;
        r8 = r8 * r6;
        r4 = (r34 > r8 ? 1 : (r34 == r8 ? 0 : -1));
        if (r4 < 0) goto L_0x02e2;
    L_0x02d9:
        if (r37 != 0) goto L_0x036e;
    L_0x02db:
        r8 = 4617315517961601024; // 0x4014000000000000 float:0.0 double:5.0;
        r6 = r6 * r8;
        r4 = (r34 > r6 ? 1 : (r34 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x036e;
    L_0x02e2:
        r4 = 0;
        r0 = r40;
        r0.setLength(r4);
        r4 = 48;
        r0 = r40;
        r0.append(r4);
        r4 = 1;
        goto L_0x0049;
    L_0x02f2:
        r28 = 4621819117588971520; // 0x4024000000000000 float:0.0 double:10.0;
        r14 = r14 * r28;
        r28 = 4621819117588971520; // 0x4024000000000000 float:0.0 double:10.0;
        r16 = r16 * r28;
        goto L_0x0255;
    L_0x02fc:
        r6 = tens;
        r10 = r8 + -1;
        r14 = r6[r10];
        r28 = r28 * r14;
        r6 = 1;
    L_0x0305:
        r0 = r16;
        r0 = (long) r0;
        r30 = r0;
        r0 = r30;
        r14 = (double) r0;
        r14 = r16 - r14;
        r16 = 48;
        r16 = r16 + r30;
        r0 = r16;
        r10 = (int) r0;
        r10 = (char) r10;
        r0 = r40;
        r0.append(r10);
        if (r6 != r8) goto L_0x0367;
    L_0x031e:
        r16 = 4602678819172646912; // 0x3fe0000000000000 float:0.0 double:0.5;
        r16 = r16 + r28;
        r6 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r6 <= 0) goto L_0x0358;
    L_0x0326:
        r4 = r40.length();
        r4 = r4 + -1;
        r0 = r40;
        r4 = r0.charAt(r4);
        r5 = r40.length();
        r5 = r5 + -1;
        r0 = r40;
        r0.setLength(r5);
        r5 = 57;
        if (r4 == r5) goto L_0x034d;
    L_0x0341:
        r4 = r4 + 1;
        r4 = (char) r4;
        r0 = r40;
        r0.append(r4);
        r4 = r7 + 1;
        goto L_0x0049;
    L_0x034d:
        r4 = r40.length();
        if (r4 != 0) goto L_0x0326;
    L_0x0353:
        r7 = r7 + 1;
        r4 = 48;
        goto L_0x0341;
    L_0x0358:
        r16 = 4602678819172646912; // 0x3fe0000000000000 float:0.0 double:0.5;
        r16 = r16 - r28;
        r6 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r6 >= 0) goto L_0x0719;
    L_0x0360:
        stripTrailingZeroes(r40);
        r4 = r7 + 1;
        goto L_0x0049;
    L_0x0367:
        r6 = r6 + 1;
        r16 = 4621819117588971520; // 0x4024000000000000 float:0.0 double:10.0;
        r16 = r16 * r14;
        goto L_0x0305;
    L_0x036e:
        r4 = 49;
        r0 = r40;
        r0.append(r4);
        r4 = r5 + 1;
        r4 = r4 + 1;
        goto L_0x0049;
    L_0x037b:
        r4 = 1;
    L_0x037c:
        r8 = r34 / r6;
        r8 = (long) r8;
        r12 = (double) r8;
        r12 = r12 * r6;
        r12 = r34 - r12;
        r14 = 48;
        r14 = r14 + r8;
        r10 = (int) r14;
        r10 = (char) r10;
        r0 = r40;
        r0.append(r10);
        if (r4 != r11) goto L_0x03d6;
    L_0x038f:
        r10 = r12 + r12;
        r4 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
        if (r4 > 0) goto L_0x03a4;
    L_0x0395:
        r4 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x03c7;
    L_0x0399:
        r6 = 1;
        r6 = r6 & r8;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x03a4;
    L_0x03a2:
        if (r37 == 0) goto L_0x03c7;
    L_0x03a4:
        r4 = r40.length();
        r4 = r4 + -1;
        r0 = r40;
        r4 = r0.charAt(r4);
        r6 = r40.length();
        r6 = r6 + -1;
        r0 = r40;
        r0.setLength(r6);
        r6 = 57;
        if (r4 == r6) goto L_0x03cb;
    L_0x03bf:
        r4 = r4 + 1;
        r4 = (char) r4;
        r0 = r40;
        r0.append(r4);
    L_0x03c7:
        r4 = r5 + 1;
        goto L_0x0049;
    L_0x03cb:
        r4 = r40.length();
        if (r4 != 0) goto L_0x03a4;
    L_0x03d1:
        r5 = r5 + 1;
        r4 = 48;
        goto L_0x03bf;
    L_0x03d6:
        r8 = 4621819117588971520; // 0x4024000000000000 float:0.0 double:10.0;
        r34 = r12 * r8;
        r8 = 0;
        r8 = (r34 > r8 ? 1 : (r34 == r8 ? 0 : -1));
        if (r8 == 0) goto L_0x03c7;
    L_0x03e0:
        r4 = r4 + 1;
        goto L_0x037c;
    L_0x03e3:
        r9 = 0;
        if (r22 == 0) goto L_0x0707;
    L_0x03e6:
        r6 = 2;
        r0 = r23;
        if (r0 >= r6) goto L_0x049b;
    L_0x03eb:
        if (r24 == 0) goto L_0x0494;
    L_0x03ed:
        r6 = 0;
        r6 = r25[r6];
        r6 = r6 + 1075;
    L_0x03f2:
        r7 = r18;
        r8 = r19;
        r9 = r6;
        r6 = r13;
    L_0x03f8:
        r13 = r19 + r9;
        r10 = r21 + r9;
        r14 = 1;
        r9 = java.math.BigInteger.valueOf(r14);
        r17 = r6;
        r14 = r18;
        r18 = r7;
        r7 = r8;
        r8 = r10;
    L_0x040a:
        if (r7 <= 0) goto L_0x06fe;
    L_0x040c:
        if (r8 <= 0) goto L_0x06fe;
    L_0x040e:
        if (r7 >= r8) goto L_0x04b3;
    L_0x0410:
        r6 = r7;
    L_0x0411:
        r13 = r13 - r6;
        r7 = r7 - r6;
        r8 = r8 - r6;
        r10 = r8;
        r15 = r7;
    L_0x0416:
        if (r14 <= 0) goto L_0x06f9;
    L_0x0418:
        if (r22 == 0) goto L_0x04b6;
    L_0x041a:
        if (r18 <= 0) goto L_0x06f4;
    L_0x041c:
        r0 = r18;
        r6 = pow5mult(r9, r0);
        r0 = r20;
        r7 = r6.multiply(r0);
    L_0x0428:
        r8 = r14 - r18;
        if (r8 == 0) goto L_0x0430;
    L_0x042c:
        r7 = pow5mult(r7, r8);
    L_0x0430:
        r8 = 1;
        r14 = java.math.BigInteger.valueOf(r8);
        if (r17 <= 0) goto L_0x043e;
    L_0x0438:
        r0 = r17;
        r14 = pow5mult(r14, r0);
    L_0x043e:
        r8 = 0;
        r9 = 2;
        r0 = r23;
        if (r0 >= r9) goto L_0x06f0;
    L_0x0444:
        r9 = word1(r34);
        if (r9 != 0) goto L_0x06f0;
    L_0x044a:
        r9 = word0(r34);
        r16 = 1048575; // 0xfffff float:1.469367E-39 double:5.18065E-318;
        r9 = r9 & r16;
        if (r9 != 0) goto L_0x06f0;
    L_0x0455:
        r9 = word0(r34);
        r16 = 2145386496; // 0x7fe00000 float:NaN double:1.0599617647E-314;
        r9 = r9 & r16;
        if (r9 == 0) goto L_0x06f0;
    L_0x045f:
        r13 = r13 + 1;
        r9 = r10 + 1;
        r8 = 1;
        r10 = r13;
    L_0x0465:
        r18 = r14.toByteArray();
        r16 = 0;
        r13 = 0;
        r32 = r13;
        r13 = r16;
        r16 = r32;
    L_0x0472:
        r19 = 4;
        r0 = r16;
        r1 = r19;
        if (r0 >= r1) goto L_0x04c0;
    L_0x047a:
        r13 = r13 << 8;
        r0 = r18;
        r0 = r0.length;
        r19 = r0;
        r0 = r16;
        r1 = r19;
        if (r0 >= r1) goto L_0x0491;
    L_0x0487:
        r19 = r18[r16];
        r0 = r19;
        r0 = r0 & 255;
        r19 = r0;
        r13 = r13 | r19;
    L_0x0491:
        r16 = r16 + 1;
        goto L_0x0472;
    L_0x0494:
        r6 = 0;
        r6 = r26[r6];
        r6 = 54 - r6;
        goto L_0x03f2;
    L_0x049b:
        r6 = r11 + -1;
        r0 = r18;
        if (r0 < r6) goto L_0x04ab;
    L_0x04a1:
        r7 = r18 - r6;
        r6 = r13;
    L_0x04a4:
        if (r11 >= 0) goto L_0x0702;
    L_0x04a6:
        r8 = r19 - r11;
        r9 = 0;
        goto L_0x03f8;
    L_0x04ab:
        r7 = r6 - r18;
        r6 = r13 + r7;
        r18 = r18 + r7;
        r7 = 0;
        goto L_0x04a4;
    L_0x04b3:
        r6 = r8;
        goto L_0x0411;
    L_0x04b6:
        r0 = r20;
        r6 = pow5mult(r0, r14);
        r7 = r6;
        r6 = r9;
        goto L_0x0430;
    L_0x04c0:
        if (r17 == 0) goto L_0x053d;
    L_0x04c2:
        r13 = hi0bits(r13);
        r13 = 32 - r13;
    L_0x04c8:
        r13 = r13 + r9;
        r13 = r13 & 31;
        if (r13 == 0) goto L_0x04cf;
    L_0x04cd:
        r13 = 32 - r13;
    L_0x04cf:
        r16 = 4;
        r0 = r16;
        if (r13 <= r0) goto L_0x053f;
    L_0x04d5:
        r16 = r13 + -4;
        r13 = r10 + r16;
        r10 = r15 + r16;
        r9 = r9 + r16;
    L_0x04dd:
        if (r13 <= 0) goto L_0x04e3;
    L_0x04df:
        r7 = r7.shiftLeft(r13);
    L_0x04e3:
        if (r9 <= 0) goto L_0x06e9;
    L_0x04e5:
        r9 = r14.shiftLeft(r9);
        r13 = r9;
    L_0x04ea:
        if (r4 == 0) goto L_0x06e2;
    L_0x04ec:
        r4 = r7.compareTo(r13);
        if (r4 >= 0) goto L_0x06e2;
    L_0x04f2:
        r5 = r5 + -1;
        r14 = 10;
        r4 = java.math.BigInteger.valueOf(r14);
        r7 = r7.multiply(r4);
        if (r22 == 0) goto L_0x050a;
    L_0x0500:
        r14 = 10;
        r4 = java.math.BigInteger.valueOf(r14);
        r6 = r6.multiply(r4);
    L_0x050a:
        r11 = r12;
        r32 = r5;
        r5 = r6;
        r6 = r32;
    L_0x0510:
        if (r11 > 0) goto L_0x055b;
    L_0x0512:
        r4 = 2;
        r0 = r23;
        if (r0 <= r4) goto L_0x055b;
    L_0x0517:
        if (r11 < 0) goto L_0x052d;
    L_0x0519:
        r4 = 5;
        r4 = java.math.BigInteger.valueOf(r4);
        r4 = r13.multiply(r4);
        r4 = r7.compareTo(r4);
        if (r4 < 0) goto L_0x052d;
    L_0x0529:
        if (r4 != 0) goto L_0x054e;
    L_0x052b:
        if (r37 != 0) goto L_0x054e;
    L_0x052d:
        r4 = 0;
        r0 = r40;
        r0.setLength(r4);
        r4 = 48;
        r0 = r40;
        r0.append(r4);
        r4 = 1;
        goto L_0x0049;
    L_0x053d:
        r13 = 1;
        goto L_0x04c8;
    L_0x053f:
        r16 = 4;
        r0 = r16;
        if (r13 >= r0) goto L_0x06ec;
    L_0x0545:
        r16 = r13 + 28;
        r13 = r10 + r16;
        r10 = r15 + r16;
        r9 = r9 + r16;
        goto L_0x04dd;
    L_0x054e:
        r4 = 49;
        r0 = r40;
        r0.append(r4);
        r4 = r6 + 1;
        r4 = r4 + 1;
        goto L_0x0049;
    L_0x055b:
        if (r22 == 0) goto L_0x06ac;
    L_0x055d:
        if (r10 <= 0) goto L_0x0563;
    L_0x055f:
        r5 = r5.shiftLeft(r10);
    L_0x0563:
        if (r8 == 0) goto L_0x06df;
    L_0x0565:
        r4 = 1;
        r4 = r5.shiftLeft(r4);
    L_0x056a:
        r8 = 1;
        r9 = r8;
        r32 = r7;
        r7 = r4;
        r4 = r32;
    L_0x0571:
        r4 = r4.divideAndRemainder(r13);
        r8 = 1;
        r8 = r4[r8];
        r10 = 0;
        r4 = r4[r10];
        r4 = r4.intValue();
        r4 = r4 + 48;
        r4 = (char) r4;
        r12 = r8.compareTo(r5);
        r10 = r13.subtract(r7);
        r14 = r10.signum();
        if (r14 > 0) goto L_0x05bb;
    L_0x0590:
        r10 = 1;
    L_0x0591:
        if (r10 != 0) goto L_0x05ce;
    L_0x0593:
        if (r23 != 0) goto L_0x05ce;
    L_0x0595:
        r14 = word1(r34);
        r14 = r14 & 1;
        if (r14 != 0) goto L_0x05ce;
    L_0x059d:
        r5 = 57;
        if (r4 != r5) goto L_0x05c0;
    L_0x05a1:
        r4 = 57;
        r0 = r40;
        r0.append(r4);
        r4 = roundOff(r40);
        if (r4 == 0) goto L_0x06dc;
    L_0x05ae:
        r4 = r6 + 1;
        r5 = 49;
        r0 = r40;
        r0.append(r5);
    L_0x05b7:
        r4 = r4 + 1;
        goto L_0x0049;
    L_0x05bb:
        r10 = r8.compareTo(r10);
        goto L_0x0591;
    L_0x05c0:
        if (r12 <= 0) goto L_0x05c5;
    L_0x05c2:
        r4 = r4 + 1;
        r4 = (char) r4;
    L_0x05c5:
        r0 = r40;
        r0.append(r4);
        r4 = r6 + 1;
        goto L_0x0049;
    L_0x05ce:
        if (r12 < 0) goto L_0x05dc;
    L_0x05d0:
        if (r12 != 0) goto L_0x061d;
    L_0x05d2:
        if (r23 != 0) goto L_0x061d;
    L_0x05d4:
        r12 = word1(r34);
        r12 = r12 & 1;
        if (r12 != 0) goto L_0x061d;
    L_0x05dc:
        if (r10 <= 0) goto L_0x0614;
    L_0x05de:
        r5 = 1;
        r5 = r8.shiftLeft(r5);
        r5 = r5.compareTo(r13);
        if (r5 > 0) goto L_0x05f2;
    L_0x05e9:
        if (r5 != 0) goto L_0x0614;
    L_0x05eb:
        r5 = r4 & 1;
        r7 = 1;
        if (r5 == r7) goto L_0x05f2;
    L_0x05f0:
        if (r37 == 0) goto L_0x0614;
    L_0x05f2:
        r5 = r4 + 1;
        r5 = (char) r5;
        r7 = 57;
        if (r4 != r7) goto L_0x0613;
    L_0x05f9:
        r4 = 57;
        r0 = r40;
        r0.append(r4);
        r4 = roundOff(r40);
        if (r4 == 0) goto L_0x060f;
    L_0x0606:
        r6 = r6 + 1;
        r4 = 49;
        r0 = r40;
        r0.append(r4);
    L_0x060f:
        r4 = r6 + 1;
        goto L_0x0049;
    L_0x0613:
        r4 = r5;
    L_0x0614:
        r0 = r40;
        r0.append(r4);
        r4 = r6 + 1;
        goto L_0x0049;
    L_0x061d:
        if (r10 <= 0) goto L_0x0649;
    L_0x061f:
        r5 = 57;
        if (r4 != r5) goto L_0x063d;
    L_0x0623:
        r4 = 57;
        r0 = r40;
        r0.append(r4);
        r4 = roundOff(r40);
        if (r4 == 0) goto L_0x0639;
    L_0x0630:
        r6 = r6 + 1;
        r4 = 49;
        r0 = r40;
        r0.append(r4);
    L_0x0639:
        r4 = r6 + 1;
        goto L_0x0049;
    L_0x063d:
        r4 = r4 + 1;
        r4 = (char) r4;
        r0 = r40;
        r0.append(r4);
        r4 = r6 + 1;
        goto L_0x0049;
    L_0x0649:
        r0 = r40;
        r0.append(r4);
        if (r9 != r11) goto L_0x0679;
    L_0x0650:
        r5 = r4;
        r4 = r8;
    L_0x0652:
        r7 = 1;
        r4 = r4.shiftLeft(r7);
        r4 = r4.compareTo(r13);
        if (r4 > 0) goto L_0x0666;
    L_0x065d:
        if (r4 != 0) goto L_0x06d5;
    L_0x065f:
        r4 = r5 & 1;
        r5 = 1;
        if (r4 == r5) goto L_0x0666;
    L_0x0664:
        if (r37 == 0) goto L_0x06d5;
    L_0x0666:
        r4 = roundOff(r40);
        if (r4 == 0) goto L_0x06d8;
    L_0x066c:
        r4 = r6 + 1;
        r5 = 49;
        r0 = r40;
        r0.append(r5);
        r4 = r4 + 1;
        goto L_0x0049;
    L_0x0679:
        r14 = 10;
        r4 = java.math.BigInteger.valueOf(r14);
        r8 = r8.multiply(r4);
        if (r5 != r7) goto L_0x0697;
    L_0x0685:
        r4 = 10;
        r4 = java.math.BigInteger.valueOf(r4);
        r4 = r7.multiply(r4);
        r5 = r4;
    L_0x0690:
        r7 = r9 + 1;
        r9 = r7;
        r7 = r4;
        r4 = r8;
        goto L_0x0571;
    L_0x0697:
        r14 = 10;
        r4 = java.math.BigInteger.valueOf(r14);
        r5 = r5.multiply(r4);
        r14 = 10;
        r4 = java.math.BigInteger.valueOf(r14);
        r4 = r7.multiply(r4);
        goto L_0x0690;
    L_0x06ac:
        r4 = 1;
    L_0x06ad:
        r7 = r7.divideAndRemainder(r13);
        r5 = 1;
        r5 = r7[r5];
        r8 = 0;
        r7 = r7[r8];
        r7 = r7.intValue();
        r7 = r7 + 48;
        r7 = (char) r7;
        r0 = r40;
        r0.append(r7);
        if (r4 < r11) goto L_0x06c8;
    L_0x06c5:
        r4 = r5;
        r5 = r7;
        goto L_0x0652;
    L_0x06c8:
        r8 = 10;
        r7 = java.math.BigInteger.valueOf(r8);
        r7 = r5.multiply(r7);
        r4 = r4 + 1;
        goto L_0x06ad;
    L_0x06d5:
        stripTrailingZeroes(r40);
    L_0x06d8:
        r4 = r6 + 1;
        goto L_0x0049;
    L_0x06dc:
        r4 = r6;
        goto L_0x05b7;
    L_0x06df:
        r4 = r5;
        goto L_0x056a;
    L_0x06e2:
        r32 = r6;
        r6 = r5;
        r5 = r32;
        goto L_0x0510;
    L_0x06e9:
        r13 = r14;
        goto L_0x04ea;
    L_0x06ec:
        r13 = r10;
        r10 = r15;
        goto L_0x04dd;
    L_0x06f0:
        r9 = r10;
        r10 = r13;
        goto L_0x0465;
    L_0x06f4:
        r6 = r9;
        r7 = r20;
        goto L_0x0428;
    L_0x06f9:
        r6 = r9;
        r7 = r20;
        goto L_0x0430;
    L_0x06fe:
        r10 = r8;
        r15 = r7;
        goto L_0x0416;
    L_0x0702:
        r8 = r19;
        r9 = r11;
        goto L_0x03f8;
    L_0x0707:
        r17 = r13;
        r8 = r21;
        r7 = r19;
        r14 = r18;
        r13 = r19;
        goto L_0x040a;
    L_0x0713:
        r5 = r7;
        r11 = r8;
        r34 = r14;
        goto L_0x02bf;
    L_0x0719:
        r6 = r9;
        goto L_0x02b7;
    L_0x071c:
        r14 = r16;
        goto L_0x02b7;
    L_0x0720:
        r16 = r14;
        goto L_0x0243;
    L_0x0724:
        r6 = r10;
        r14 = r8;
        r8 = r11;
        r9 = r7;
        r7 = r5;
        goto L_0x01c7;
    L_0x072b:
        r7 = r6;
        goto L_0x01b6;
    L_0x072e:
        r8 = r34;
        goto L_0x01b6;
    L_0x0732:
        r9 = r6;
        r16 = r34;
        r32 = r7;
        r6 = r14;
        r14 = r8;
        r8 = r32;
        goto L_0x0129;
    L_0x073d:
        r22 = r6;
        goto L_0x00ff;
    L_0x0741:
        r23 = r7;
        r7 = r6;
        goto L_0x00f7;
    L_0x0746:
        r7 = r36;
        goto L_0x00ed;
    L_0x074a:
        r32 = r5;
        r5 = r4;
        r4 = r32;
        goto L_0x00d2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.DToA.JS_dtoa(double, int, boolean, int, boolean[], java.lang.StringBuilder):int");
    }

    static String JS_dtobasestr(int i, double d) {
        if (DTOSTR_FIXED > i || i > 36) {
            throw new IllegalArgumentException("Bad base: " + i);
        } else if (Double.isNaN(d)) {
            return "NaN";
        } else {
            if (Double.isInfinite(d)) {
                return d > 0.0d ? "Infinity" : "-Infinity";
            } else {
                if (d == 0.0d) {
                    return MigrationManager.InitialSdkVersion;
                }
                Object obj;
                String l;
                int i2;
                BigInteger valueOf;
                if (d >= 0.0d) {
                    obj = null;
                } else {
                    obj = Log2P;
                    d = -d;
                }
                double floor = Math.floor(d);
                long j = (long) floor;
                if (((double) j) == floor) {
                    l = Long.toString(obj != null ? -j : j, i);
                } else {
                    j = Double.doubleToLongBits(floor);
                    int i3 = ((int) (j >> Exp_shiftL)) & Exp_mask_shifted;
                    j = i3 == 0 ? (j & Frac_maskL) << Log2P : (j & Frac_maskL) | Exp_msk1L;
                    long j2 = obj != null ? -j : j;
                    i2 = i3 - 1075;
                    valueOf = BigInteger.valueOf(j2);
                    if (i2 > 0) {
                        valueOf = valueOf.shiftLeft(i2);
                    } else if (i2 < 0) {
                        valueOf = valueOf.shiftRight(-i2);
                    }
                    l = valueOf.toString(i);
                }
                if (d == floor) {
                    return l;
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(l).append('.');
                double d2 = d - floor;
                j = Double.doubleToLongBits(d);
                int i4 = (int) (j >> 32);
                int i5 = (int) j;
                int[] iArr = new int[Log2P];
                BigInteger d2b = d2b(d2, iArr, new int[Log2P]);
                int i6 = -((i4 >>> Exp_shift1) & Exp_mask_shifted);
                if (i6 == 0) {
                    i6 = -1;
                }
                i6 += 1076;
                BigInteger valueOf2 = BigInteger.valueOf(1);
                if (i5 == 0 && (Frac_mask1 & i4) == 0 && (2145386496 & i4) != 0) {
                    i2 = i6 + Log2P;
                    valueOf = BigInteger.valueOf(2);
                } else {
                    i2 = i6;
                    valueOf = valueOf2;
                }
                BigInteger shiftLeft = d2b.shiftLeft(iArr[DTOSTR_STANDARD] + i2);
                BigInteger shiftLeft2 = BigInteger.valueOf(1).shiftLeft(i2);
                BigInteger valueOf3 = BigInteger.valueOf((long) i);
                BigInteger bigInteger = valueOf2;
                valueOf2 = valueOf;
                obj = DTOSTR_STANDARD;
                do {
                    BigInteger[] divideAndRemainder = shiftLeft.multiply(valueOf3).divideAndRemainder(shiftLeft2);
                    BigInteger bigInteger2 = divideAndRemainder[Log2P];
                    char intValue = (char) divideAndRemainder[DTOSTR_STANDARD].intValue();
                    if (bigInteger == valueOf2) {
                        valueOf2 = bigInteger.multiply(valueOf3);
                        bigInteger = valueOf2;
                    } else {
                        bigInteger = bigInteger.multiply(valueOf3);
                        valueOf2 = valueOf2.multiply(valueOf3);
                    }
                    int compareTo = bigInteger2.compareTo(bigInteger);
                    d2b = shiftLeft2.subtract(valueOf2);
                    int compareTo2 = d2b.signum() <= 0 ? Log2P : bigInteger2.compareTo(d2b);
                    if (compareTo2 == 0 && (i5 & Log2P) == 0) {
                        if (compareTo > 0) {
                            i6 = intValue + Log2P;
                        } else {
                            char c = intValue;
                        }
                        shiftLeft = bigInteger2;
                        i4 = i6;
                        obj = Log2P;
                    } else if (compareTo < 0 || (compareTo == 0 && (i5 & Log2P) == 0)) {
                        if (compareTo2 > 0) {
                            valueOf = bigInteger2.shiftLeft(Log2P);
                            if (valueOf.compareTo(shiftLeft2) > 0) {
                                intValue += Log2P;
                            }
                        } else {
                            valueOf = bigInteger2;
                        }
                        r4 = intValue;
                        shiftLeft = valueOf;
                        i6 = Log2P;
                    } else if (compareTo2 > 0) {
                        int i7 = intValue + Log2P;
                        obj = Log2P;
                        r12 = bigInteger2;
                        i4 = i7;
                        shiftLeft = r12;
                    } else {
                        r12 = bigInteger2;
                        r4 = intValue;
                        shiftLeft = r12;
                    }
                    stringBuilder.append(BASEDIGIT(i4));
                } while (obj == null);
                return stringBuilder.toString();
            }
        }
    }

    static void JS_dtostr(StringBuilder stringBuilder, int i, int i2, double d) {
        boolean[] zArr = new boolean[Log2P];
        if (i == DTOSTR_FIXED && (d >= 1.0E21d || d <= -1.0E21d)) {
            i = DTOSTR_STANDARD;
        }
        int JS_dtoa = JS_dtoa(d, dtoaModes[i], i >= DTOSTR_FIXED, i2, zArr, stringBuilder);
        int length = stringBuilder.length();
        if (JS_dtoa != 9999) {
            int i3;
            int i4;
            switch (i) {
                case DTOSTR_STANDARD /*0*/:
                    if (JS_dtoa >= -5 && JS_dtoa <= 21) {
                        i3 = JS_dtoa;
                        i4 = DTOSTR_STANDARD;
                        break;
                    }
                    i3 = DTOSTR_STANDARD;
                    i4 = Log2P;
                    break;
                case Log2P /*1*/:
                    i2 = DTOSTR_STANDARD;
                    break;
                case DTOSTR_FIXED /*2*/:
                    if (i2 < 0) {
                        i3 = JS_dtoa;
                        i4 = DTOSTR_STANDARD;
                        break;
                    }
                    i3 = i2 + JS_dtoa;
                    i4 = DTOSTR_STANDARD;
                    break;
                case DTOSTR_EXPONENTIAL /*3*/:
                    break;
                case DTOSTR_PRECISION /*4*/:
                    if (JS_dtoa >= -5 && JS_dtoa <= i2) {
                        i3 = i2;
                        i4 = DTOSTR_STANDARD;
                        break;
                    }
                    i3 = i2;
                    i4 = Log2P;
                    break;
                    break;
                default:
                    i3 = DTOSTR_STANDARD;
                    i4 = DTOSTR_STANDARD;
                    break;
            }
            i3 = i2;
            i4 = Log2P;
            if (length < i3) {
                do {
                    stringBuilder.append('0');
                } while (stringBuilder.length() != i3);
            } else {
                i3 = length;
            }
            if (i4 != 0) {
                if (i3 != Log2P) {
                    stringBuilder.insert(Log2P, '.');
                }
                stringBuilder.append('e');
                if (JS_dtoa - 1 >= 0) {
                    stringBuilder.append('+');
                }
                stringBuilder.append(JS_dtoa - 1);
            } else if (JS_dtoa != i3) {
                if (JS_dtoa > 0) {
                    stringBuilder.insert(JS_dtoa, '.');
                } else {
                    for (i3 = DTOSTR_STANDARD; i3 < 1 - JS_dtoa; i3 += Log2P) {
                        stringBuilder.insert(DTOSTR_STANDARD, '0');
                    }
                    stringBuilder.insert(Log2P, '.');
                }
            }
        }
        if (!zArr[DTOSTR_STANDARD]) {
            return;
        }
        if (word0(d) != Sign_bit || word1(d) != 0) {
            if ((word0(d) & Exp_mask) != Exp_mask || (word1(d) == 0 && (word0(d) & Frac_mask1) == 0)) {
                stringBuilder.insert(DTOSTR_STANDARD, '-');
            }
        }
    }

    private static BigInteger d2b(double d, int[] iArr, int[] iArr2) {
        byte[] bArr;
        int lo0bits;
        int i = Log2P;
        long doubleToLongBits = Double.doubleToLongBits(d);
        int i2 = (int) (doubleToLongBits >>> 32);
        int i3 = (int) doubleToLongBits;
        int i4 = Frac_mask1 & i2;
        i2 = (Integer.MAX_VALUE & i2) >>> Exp_shift1;
        if (i2 != 0) {
            i4 |= Exp_msk1;
        }
        int i5;
        if (i3 != 0) {
            bArr = new byte[8];
            lo0bits = lo0bits(i3);
            i3 >>>= lo0bits;
            if (lo0bits != 0) {
                stuffBits(bArr, DTOSTR_PRECISION, i3 | (i4 << (32 - lo0bits)));
                i4 >>= lo0bits;
            } else {
                stuffBits(bArr, DTOSTR_PRECISION, i3);
            }
            stuffBits(bArr, DTOSTR_STANDARD, i4);
            if (i4 != 0) {
                i = DTOSTR_FIXED;
            }
            i5 = lo0bits;
            lo0bits = i;
            i = i5;
        } else {
            bArr = new byte[DTOSTR_PRECISION];
            lo0bits = lo0bits(i4);
            i4 >>>= lo0bits;
            stuffBits(bArr, DTOSTR_STANDARD, i4);
            i5 = lo0bits + 32;
            lo0bits = Log2P;
            i = i5;
        }
        if (i2 != 0) {
            iArr[DTOSTR_STANDARD] = ((i2 - 1023) - 52) + i;
            iArr2[DTOSTR_STANDARD] = 53 - i;
        } else {
            iArr[DTOSTR_STANDARD] = i + (((i2 - 1023) - 52) + Log2P);
            iArr2[DTOSTR_STANDARD] = (lo0bits * 32) - hi0bits(i4);
        }
        return new BigInteger(bArr);
    }

    private static int hi0bits(int i) {
        int i2;
        int i3 = DTOSTR_STANDARD;
        if ((-65536 & i) == 0) {
            i3 = Bletch;
            i2 = i << Bletch;
        } else {
            i2 = i;
        }
        if ((-16777216 & i2) == 0) {
            i3 += 8;
            i2 <<= 8;
        }
        if ((-268435456 & i2) == 0) {
            i3 += DTOSTR_PRECISION;
            i2 <<= DTOSTR_PRECISION;
        }
        if ((-1073741824 & i2) == 0) {
            i3 += DTOSTR_FIXED;
            i2 <<= DTOSTR_FIXED;
        }
        if ((Sign_bit & i2) != 0) {
            return i3;
        }
        return (i2 & 1073741824) == 0 ? 32 : i3 + Log2P;
    }

    private static int lo0bits(int i) {
        if ((i & 7) != 0) {
            return (i & Log2P) != 0 ? DTOSTR_STANDARD : (i & DTOSTR_FIXED) != 0 ? Log2P : DTOSTR_FIXED;
        } else {
            int i2;
            int i3;
            if ((65535 & i) == 0) {
                i2 = Bletch;
                i3 = i >>> Bletch;
            } else {
                i2 = DTOSTR_STANDARD;
                i3 = i;
            }
            if ((i3 & 255) == 0) {
                i2 += 8;
                i3 >>>= 8;
            }
            if ((i3 & 15) == 0) {
                i2 += DTOSTR_PRECISION;
                i3 >>>= DTOSTR_PRECISION;
            }
            int i4;
            if ((i3 & DTOSTR_EXPONENTIAL) == 0) {
                i4 = i3 >>> DTOSTR_FIXED;
                i3 = i2 + DTOSTR_FIXED;
                i2 = i4;
            } else {
                i4 = i3;
                i3 = i2;
                i2 = i4;
            }
            if ((i2 & Log2P) != 0) {
                return i3;
            }
            return ((i2 >>> Log2P) & Log2P) == 0 ? 32 : i3 + Log2P;
        }
    }

    static BigInteger pow5mult(BigInteger bigInteger, int i) {
        return bigInteger.multiply(BigInteger.valueOf(5).pow(i));
    }

    static boolean roundOff(StringBuilder stringBuilder) {
        int length = stringBuilder.length();
        while (length != 0) {
            length--;
            char charAt = stringBuilder.charAt(length);
            if (charAt != '9') {
                stringBuilder.setCharAt(length, (char) (charAt + Log2P));
                stringBuilder.setLength(length + Log2P);
                return false;
            }
        }
        stringBuilder.setLength(DTOSTR_STANDARD);
        return true;
    }

    static double setWord0(double d, int i) {
        return Double.longBitsToDouble((Double.doubleToLongBits(d) & 4294967295L) | (((long) i) << 32));
    }

    private static void stripTrailingZeroes(StringBuilder stringBuilder) {
        int i;
        int length = stringBuilder.length();
        while (true) {
            i = length - 1;
            if (length <= 0 || stringBuilder.charAt(i) != '0') {
                stringBuilder.setLength(i + Log2P);
            } else {
                length = i;
            }
        }
        stringBuilder.setLength(i + Log2P);
    }

    private static void stuffBits(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) (i2 >> 24);
        bArr[i + Log2P] = (byte) (i2 >> Bletch);
        bArr[i + DTOSTR_FIXED] = (byte) (i2 >> 8);
        bArr[i + DTOSTR_EXPONENTIAL] = (byte) i2;
    }

    static int word0(double d) {
        return (int) (Double.doubleToLongBits(d) >> 32);
    }

    static int word1(double d) {
        return (int) Double.doubleToLongBits(d);
    }
}
