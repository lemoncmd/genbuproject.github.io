package org.mozilla.javascript;

import com.microsoft.cll.android.EventEnums;
import org.mozilla.javascript.typedarrays.Conversions;

final class NativeMath extends IdScriptableObject {
    private static final int Id_E = 30;
    private static final int Id_LN10 = 32;
    private static final int Id_LN2 = 33;
    private static final int Id_LOG10E = 35;
    private static final int Id_LOG2E = 34;
    private static final int Id_PI = 31;
    private static final int Id_SQRT1_2 = 36;
    private static final int Id_SQRT2 = 37;
    private static final int Id_abs = 2;
    private static final int Id_acos = 3;
    private static final int Id_asin = 4;
    private static final int Id_atan = 5;
    private static final int Id_atan2 = 6;
    private static final int Id_cbrt = 20;
    private static final int Id_ceil = 7;
    private static final int Id_cos = 8;
    private static final int Id_cosh = 21;
    private static final int Id_exp = 9;
    private static final int Id_expm1 = 22;
    private static final int Id_floor = 10;
    private static final int Id_hypot = 23;
    private static final int Id_imul = 28;
    private static final int Id_log = 11;
    private static final int Id_log10 = 25;
    private static final int Id_log1p = 24;
    private static final int Id_max = 12;
    private static final int Id_min = 13;
    private static final int Id_pow = 14;
    private static final int Id_random = 15;
    private static final int Id_round = 16;
    private static final int Id_sin = 17;
    private static final int Id_sinh = 26;
    private static final int Id_sqrt = 18;
    private static final int Id_tan = 19;
    private static final int Id_tanh = 27;
    private static final int Id_toSource = 1;
    private static final int Id_trunc = 29;
    private static final int LAST_METHOD_ID = 29;
    private static final Object MATH_TAG = "Math";
    private static final int MAX_ID = 37;
    static final long serialVersionUID = -8838847185801131569L;

    private NativeMath() {
    }

    static void init(Scriptable scriptable, boolean z) {
        NativeMath nativeMath = new NativeMath();
        nativeMath.activatePrototypeMap(MAX_ID);
        nativeMath.setPrototype(ScriptableObject.getObjectPrototype(scriptable));
        nativeMath.setParentScope(scriptable);
        if (z) {
            nativeMath.sealObject();
        }
        ScriptableObject.defineProperty(scriptable, "Math", nativeMath, Id_abs);
    }

    private double js_hypot(Object[] objArr) {
        if (objArr == null) {
            return 0.0d;
        }
        int length = objArr.length;
        double d = 0.0d;
        for (int i = 0; i < length; i += Id_toSource) {
            double toNumber = ScriptRuntime.toNumber(objArr[i]);
            if (toNumber == ScriptRuntime.NaN) {
                return toNumber;
            }
            if (toNumber == Double.POSITIVE_INFINITY || toNumber == Double.NEGATIVE_INFINITY) {
                return Double.POSITIVE_INFINITY;
            }
            d += toNumber * toNumber;
        }
        return Math.sqrt(d);
    }

    private Object js_imul(Object[] objArr) {
        if (objArr == null || objArr.length < Id_abs) {
            return ScriptRuntime.wrapNumber(ScriptRuntime.NaN);
        }
        long toUint32 = (Conversions.toUint32(objArr[0]) * Conversions.toUint32(objArr[Id_toSource])) % 4294967296L;
        if (toUint32 >= 2147483648L) {
            toUint32 -= 4294967296L;
        }
        return Double.valueOf(ScriptRuntime.toNumber(Long.valueOf(toUint32)));
    }

    private double js_pow(double d, double d2) {
        if (d2 != d2) {
            return d2;
        }
        if (d2 == 0.0d) {
            return 1.0d;
        }
        long j;
        if (d != 0.0d) {
            double pow = Math.pow(d, d2);
            if (pow == pow) {
                return pow;
            }
            if (d2 == Double.POSITIVE_INFINITY) {
                return (d < EventEnums.SampleRate_Unspecified || 1.0d < d) ? Double.POSITIVE_INFINITY : (EventEnums.SampleRate_Unspecified >= d || d >= 1.0d) ? pow : 0.0d;
            } else {
                if (d2 == Double.NEGATIVE_INFINITY) {
                    return (d < EventEnums.SampleRate_Unspecified || 1.0d < d) ? 0.0d : (EventEnums.SampleRate_Unspecified >= d || d >= 1.0d) ? pow : Double.POSITIVE_INFINITY;
                } else {
                    if (d == Double.POSITIVE_INFINITY) {
                        return d2 > 0.0d ? Double.POSITIVE_INFINITY : 0.0d;
                    } else {
                        if (d != Double.NEGATIVE_INFINITY) {
                            return pow;
                        }
                        j = (long) d2;
                        return (((double) j) != d2 || (j & 1) == 0) ? d2 > 0.0d ? Double.POSITIVE_INFINITY : 0.0d : d2 > 0.0d ? Double.NEGATIVE_INFINITY : -0.0d;
                    }
                }
            }
        } else if (1.0d / d > 0.0d) {
            return d2 > 0.0d ? 0.0d : Double.POSITIVE_INFINITY;
        } else {
            j = (long) d2;
            return (((double) j) != d2 || (j & 1) == 0) ? d2 > 0.0d ? 0.0d : Double.POSITIVE_INFINITY : d2 > 0.0d ? -0.0d : Double.NEGATIVE_INFINITY;
        }
    }

    private double js_trunc(double d) {
        return d < 0.0d ? Math.ceil(d) : Math.floor(d);
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        double d = Double.NaN;
        double d2 = 0.0d;
        if (!idFunctionObject.hasTag(MATH_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        double toNumber;
        switch (methodId) {
            case Id_toSource /*1*/:
                return "Math";
            case Id_abs /*2*/:
                d = ScriptRuntime.toNumber(objArr, 0);
                if (d != 0.0d) {
                    d2 = d < 0.0d ? -d : d;
                }
                d = d2;
                break;
            case Id_acos /*3*/:
            case Id_asin /*4*/:
                d2 = ScriptRuntime.toNumber(objArr, 0);
                if (d2 == d2 && EventEnums.SampleRate_Unspecified <= d2 && d2 <= 1.0d) {
                    d = methodId == Id_acos ? Math.acos(d2) : Math.asin(d2);
                    break;
                }
            case Id_atan /*5*/:
                d = Math.atan(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_atan2 /*6*/:
                d = Math.atan2(ScriptRuntime.toNumber(objArr, 0), ScriptRuntime.toNumber(objArr, Id_toSource));
                break;
            case Id_ceil /*7*/:
                d = Math.ceil(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_cos /*8*/:
                d2 = ScriptRuntime.toNumber(objArr, 0);
                d2 = (d2 == Double.POSITIVE_INFINITY || d2 == Double.NEGATIVE_INFINITY) ? Double.NaN : Math.cos(d2);
                d = d2;
                break;
            case Id_exp /*9*/:
                d = ScriptRuntime.toNumber(objArr, 0);
                if (d == Double.POSITIVE_INFINITY) {
                    d2 = d;
                } else if (d != Double.NEGATIVE_INFINITY) {
                    d2 = Math.exp(d);
                }
                d = d2;
                break;
            case Id_floor /*10*/:
                d = Math.floor(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_log /*11*/:
                toNumber = ScriptRuntime.toNumber(objArr, 0);
                if (toNumber >= 0.0d) {
                    d = Math.log(toNumber);
                    break;
                }
                break;
            case Id_max /*12*/:
            case Id_min /*13*/:
                d = methodId == Id_max ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
                for (int i = 0; i != objArr.length; i += Id_toSource) {
                    toNumber = ScriptRuntime.toNumber(objArr[i]);
                    if (toNumber != toNumber) {
                        d = toNumber;
                        break;
                    }
                    d = methodId == Id_max ? Math.max(d, toNumber) : Math.min(d, toNumber);
                }
                break;
            case Id_pow /*14*/:
                d = js_pow(ScriptRuntime.toNumber(objArr, 0), ScriptRuntime.toNumber(objArr, Id_toSource));
                break;
            case Id_random /*15*/:
                d = Math.random();
                break;
            case Id_round /*16*/:
                d = ScriptRuntime.toNumber(objArr, 0);
                if (!(d != d || d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY)) {
                    long round = Math.round(d);
                    if (round != 0) {
                        d2 = (double) round;
                    } else if (d < 0.0d) {
                        d2 = ScriptRuntime.negativeZero;
                    } else if (d == 0.0d) {
                        d2 = d;
                    }
                    d = d2;
                    break;
                }
            case Id_sin /*17*/:
                d2 = ScriptRuntime.toNumber(objArr, 0);
                if (!(d2 == Double.POSITIVE_INFINITY || d2 == Double.NEGATIVE_INFINITY)) {
                    d = Math.sin(d2);
                    break;
                }
            case Id_sqrt /*18*/:
                d = Math.sqrt(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_tan /*19*/:
                d = Math.tan(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_cbrt /*20*/:
                d = Math.cbrt(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_cosh /*21*/:
                d = Math.cosh(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_expm1 /*22*/:
                d = Math.expm1(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_hypot /*23*/:
                d = js_hypot(objArr);
                break;
            case Id_log1p /*24*/:
                d = Math.log1p(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_log10 /*25*/:
                d = Math.log10(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_sinh /*26*/:
                d = Math.sinh(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_tanh /*27*/:
                d = Math.tanh(ScriptRuntime.toNumber(objArr, 0));
                break;
            case Id_imul /*28*/:
                return js_imul(objArr);
            case LAST_METHOD_ID /*29*/:
                d = js_trunc(ScriptRuntime.toNumber(objArr, 0));
                break;
            default:
                throw new IllegalStateException(String.valueOf(methodId));
        }
        return ScriptRuntime.wrapNumber(d);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findPrototypeId(java.lang.String r9) {
        /*
        r8 = this;
        r6 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        r5 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
        r1 = 0;
        r0 = 2;
        r3 = 1;
        r2 = 0;
        r4 = r9.length();
        switch(r4) {
            case 1: goto L_0x001c;
            case 2: goto L_0x0027;
            case 3: goto L_0x003a;
            case 4: goto L_0x00f4;
            case 5: goto L_0x015d;
            case 6: goto L_0x01cd;
            case 7: goto L_0x01eb;
            case 8: goto L_0x01f4;
            default: goto L_0x000f;
        };
    L_0x000f:
        r0 = r1;
    L_0x0010:
        if (r2 == 0) goto L_0x001b;
    L_0x0012:
        if (r2 == r9) goto L_0x001b;
    L_0x0014:
        r2 = r2.equals(r9);
        if (r2 != 0) goto L_0x001b;
    L_0x001a:
        r0 = r1;
    L_0x001b:
        return r0;
    L_0x001c:
        r0 = r9.charAt(r1);
        r3 = 69;
        if (r0 != r3) goto L_0x000f;
    L_0x0024:
        r0 = 30;
        goto L_0x001b;
    L_0x0027:
        r0 = r9.charAt(r1);
        r4 = 80;
        if (r0 != r4) goto L_0x000f;
    L_0x002f:
        r0 = r9.charAt(r3);
        r3 = 73;
        if (r0 != r3) goto L_0x000f;
    L_0x0037:
        r0 = 31;
        goto L_0x001b;
    L_0x003a:
        r4 = r9.charAt(r1);
        switch(r4) {
            case 76: goto L_0x0043;
            case 97: goto L_0x0056;
            case 99: goto L_0x0067;
            case 101: goto L_0x0078;
            case 108: goto L_0x008b;
            case 109: goto L_0x009c;
            case 112: goto L_0x00be;
            case 115: goto L_0x00d0;
            case 116: goto L_0x00e2;
            default: goto L_0x0041;
        };
    L_0x0041:
        r0 = r1;
        goto L_0x0010;
    L_0x0043:
        r0 = r9.charAt(r0);
        r4 = 50;
        if (r0 != r4) goto L_0x000f;
    L_0x004b:
        r0 = r9.charAt(r3);
        r3 = 78;
        if (r0 != r3) goto L_0x000f;
    L_0x0053:
        r0 = 33;
        goto L_0x001b;
    L_0x0056:
        r4 = r9.charAt(r0);
        r5 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r4 != r5) goto L_0x000f;
    L_0x005e:
        r3 = r9.charAt(r3);
        r4 = 98;
        if (r3 != r4) goto L_0x000f;
    L_0x0066:
        goto L_0x001b;
    L_0x0067:
        r0 = r9.charAt(r0);
        r4 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r0 != r4) goto L_0x000f;
    L_0x006f:
        r0 = r9.charAt(r3);
        if (r0 != r6) goto L_0x000f;
    L_0x0075:
        r0 = 8;
        goto L_0x001b;
    L_0x0078:
        r0 = r9.charAt(r0);
        r4 = 112; // 0x70 float:1.57E-43 double:5.53E-322;
        if (r0 != r4) goto L_0x000f;
    L_0x0080:
        r0 = r9.charAt(r3);
        r3 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        if (r0 != r3) goto L_0x000f;
    L_0x0088:
        r0 = 9;
        goto L_0x001b;
    L_0x008b:
        r0 = r9.charAt(r0);
        r4 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        if (r0 != r4) goto L_0x000f;
    L_0x0093:
        r0 = r9.charAt(r3);
        if (r0 != r6) goto L_0x000f;
    L_0x0099:
        r0 = 11;
        goto L_0x001b;
    L_0x009c:
        r0 = r9.charAt(r0);
        if (r0 != r5) goto L_0x00ae;
    L_0x00a2:
        r0 = r9.charAt(r3);
        r3 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r0 != r3) goto L_0x000f;
    L_0x00aa:
        r0 = 13;
        goto L_0x001b;
    L_0x00ae:
        r4 = 120; // 0x78 float:1.68E-43 double:5.93E-322;
        if (r0 != r4) goto L_0x000f;
    L_0x00b2:
        r0 = r9.charAt(r3);
        r3 = 97;
        if (r0 != r3) goto L_0x000f;
    L_0x00ba:
        r0 = 12;
        goto L_0x001b;
    L_0x00be:
        r0 = r9.charAt(r0);
        r4 = 119; // 0x77 float:1.67E-43 double:5.9E-322;
        if (r0 != r4) goto L_0x000f;
    L_0x00c6:
        r0 = r9.charAt(r3);
        if (r0 != r6) goto L_0x000f;
    L_0x00cc:
        r0 = 14;
        goto L_0x001b;
    L_0x00d0:
        r0 = r9.charAt(r0);
        if (r0 != r5) goto L_0x000f;
    L_0x00d6:
        r0 = r9.charAt(r3);
        r3 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r0 != r3) goto L_0x000f;
    L_0x00de:
        r0 = 17;
        goto L_0x001b;
    L_0x00e2:
        r0 = r9.charAt(r0);
        if (r0 != r5) goto L_0x000f;
    L_0x00e8:
        r0 = r9.charAt(r3);
        r3 = 97;
        if (r0 != r3) goto L_0x000f;
    L_0x00f0:
        r0 = 19;
        goto L_0x001b;
    L_0x00f4:
        r0 = r9.charAt(r3);
        switch(r0) {
            case 78: goto L_0x00fe;
            case 97: goto L_0x0107;
            case 98: goto L_0x0110;
            case 99: goto L_0x0119;
            case 101: goto L_0x0121;
            case 105: goto L_0x0129;
            case 109: goto L_0x0132;
            case 111: goto L_0x013b;
            case 113: goto L_0x0144;
            case 115: goto L_0x014d;
            case 116: goto L_0x0155;
            default: goto L_0x00fb;
        };
    L_0x00fb:
        r0 = r1;
        goto L_0x0010;
    L_0x00fe:
        r0 = "LN10";
        r2 = 32;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0107:
        r0 = "tanh";
        r2 = 27;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0110:
        r0 = "cbrt";
        r2 = 20;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0119:
        r0 = "acos";
        r2 = 3;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0121:
        r0 = "ceil";
        r2 = 7;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0129:
        r0 = "sinh";
        r2 = 26;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0132:
        r0 = "imul";
        r2 = 28;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x013b:
        r0 = "cosh";
        r2 = 21;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0144:
        r0 = "sqrt";
        r2 = 18;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x014d:
        r0 = "asin";
        r2 = 4;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0155:
        r0 = "atan";
        r2 = 5;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x015d:
        r0 = r9.charAt(r1);
        switch(r0) {
            case 76: goto L_0x0167;
            case 83: goto L_0x0170;
            case 97: goto L_0x0179;
            case 101: goto L_0x0181;
            case 102: goto L_0x018a;
            case 104: goto L_0x0193;
            case 108: goto L_0x019c;
            case 114: goto L_0x01bb;
            case 116: goto L_0x01c4;
            default: goto L_0x0164;
        };
    L_0x0164:
        r0 = r1;
        goto L_0x0010;
    L_0x0167:
        r0 = "LOG2E";
        r2 = 34;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0170:
        r0 = "SQRT2";
        r2 = 37;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0179:
        r0 = "atan2";
        r2 = 6;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0181:
        r0 = "expm1";
        r2 = 22;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x018a:
        r0 = "floor";
        r2 = 10;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0193:
        r0 = "hypot";
        r2 = 23;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x019c:
        r0 = 4;
        r0 = r9.charAt(r0);
        r3 = 48;
        if (r0 != r3) goto L_0x01ae;
    L_0x01a5:
        r0 = "log10";
        r2 = 25;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x01ae:
        r3 = 112; // 0x70 float:1.57E-43 double:5.53E-322;
        if (r0 != r3) goto L_0x000f;
    L_0x01b2:
        r0 = "log1p";
        r2 = 24;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x01bb:
        r0 = "round";
        r2 = 16;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x01c4:
        r0 = "trunc";
        r2 = 29;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x01cd:
        r0 = r9.charAt(r1);
        r3 = 76;
        if (r0 != r3) goto L_0x01de;
    L_0x01d5:
        r0 = "LOG10E";
        r2 = 35;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x01de:
        r3 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        if (r0 != r3) goto L_0x000f;
    L_0x01e2:
        r0 = "random";
        r2 = 15;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x01eb:
        r0 = "SQRT1_2";
        r2 = 36;
        r7 = r0;
        r0 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x01f4:
        r0 = "toSource";
        r2 = r0;
        r0 = r3;
        goto L_0x0010;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeMath.findPrototypeId(java.lang.String):int");
    }

    public String getClassName() {
        return "Math";
    }

    protected void initPrototypeId(int i) {
        int i2 = 0;
        if (i <= LAST_METHOD_ID) {
            String str;
            switch (i) {
                case Id_toSource /*1*/:
                    str = "toSource";
                    break;
                case Id_abs /*2*/:
                    str = "abs";
                    i2 = Id_toSource;
                    break;
                case Id_acos /*3*/:
                    str = "acos";
                    i2 = Id_toSource;
                    break;
                case Id_asin /*4*/:
                    str = "asin";
                    i2 = Id_toSource;
                    break;
                case Id_atan /*5*/:
                    str = "atan";
                    i2 = Id_toSource;
                    break;
                case Id_atan2 /*6*/:
                    str = "atan2";
                    i2 = Id_abs;
                    break;
                case Id_ceil /*7*/:
                    str = "ceil";
                    i2 = Id_toSource;
                    break;
                case Id_cos /*8*/:
                    str = "cos";
                    i2 = Id_toSource;
                    break;
                case Id_exp /*9*/:
                    str = "exp";
                    i2 = Id_toSource;
                    break;
                case Id_floor /*10*/:
                    str = "floor";
                    i2 = Id_toSource;
                    break;
                case Id_log /*11*/:
                    str = "log";
                    i2 = Id_toSource;
                    break;
                case Id_max /*12*/:
                    str = "max";
                    i2 = Id_abs;
                    break;
                case Id_min /*13*/:
                    str = "min";
                    i2 = Id_abs;
                    break;
                case Id_pow /*14*/:
                    str = "pow";
                    i2 = Id_abs;
                    break;
                case Id_random /*15*/:
                    str = "random";
                    break;
                case Id_round /*16*/:
                    str = "round";
                    i2 = Id_toSource;
                    break;
                case Id_sin /*17*/:
                    str = "sin";
                    i2 = Id_toSource;
                    break;
                case Id_sqrt /*18*/:
                    str = "sqrt";
                    i2 = Id_toSource;
                    break;
                case Id_tan /*19*/:
                    str = "tan";
                    i2 = Id_toSource;
                    break;
                case Id_cbrt /*20*/:
                    str = "cbrt";
                    i2 = Id_toSource;
                    break;
                case Id_cosh /*21*/:
                    str = "cosh";
                    i2 = Id_toSource;
                    break;
                case Id_expm1 /*22*/:
                    str = "expm1";
                    i2 = Id_toSource;
                    break;
                case Id_hypot /*23*/:
                    str = "hypot";
                    i2 = Id_abs;
                    break;
                case Id_log1p /*24*/:
                    str = "log1p";
                    i2 = Id_toSource;
                    break;
                case Id_log10 /*25*/:
                    str = "log10";
                    i2 = Id_toSource;
                    break;
                case Id_sinh /*26*/:
                    str = "sinh";
                    i2 = Id_toSource;
                    break;
                case Id_tanh /*27*/:
                    str = "tanh";
                    i2 = Id_toSource;
                    break;
                case Id_imul /*28*/:
                    str = "imul";
                    i2 = Id_abs;
                    break;
                case LAST_METHOD_ID /*29*/:
                    str = "trunc";
                    i2 = Id_toSource;
                    break;
                default:
                    throw new IllegalStateException(String.valueOf(i));
            }
            initPrototypeMethod(MATH_TAG, i, str, i2);
            return;
        }
        double d;
        String str2;
        switch (i) {
            case Id_E /*30*/:
                d = 2.718281828459045d;
                str2 = "E";
                break;
            case Id_PI /*31*/:
                d = 3.141592653589793d;
                str2 = "PI";
                break;
            case Id_LN10 /*32*/:
                d = 2.302585092994046d;
                str2 = "LN10";
                break;
            case Id_LN2 /*33*/:
                d = 0.6931471805599453d;
                str2 = "LN2";
                break;
            case Id_LOG2E /*34*/:
                d = 1.4426950408889634d;
                str2 = "LOG2E";
                break;
            case Id_LOG10E /*35*/:
                d = 0.4342944819032518d;
                str2 = "LOG10E";
                break;
            case Id_SQRT1_2 /*36*/:
                d = 0.7071067811865476d;
                str2 = "SQRT1_2";
                break;
            case MAX_ID /*37*/:
                d = 1.4142135623730951d;
                str2 = "SQRT2";
                break;
            default:
                throw new IllegalStateException(String.valueOf(i));
        }
        initPrototypeValue(i, str2, ScriptRuntime.wrapNumber(d), Id_ceil);
    }
}
