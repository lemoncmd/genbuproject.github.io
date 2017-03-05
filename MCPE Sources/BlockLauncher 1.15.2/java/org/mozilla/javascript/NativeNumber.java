package org.mozilla.javascript;

import com.microsoft.cll.android.EventEnums;

final class NativeNumber extends IdScriptableObject {
    private static final int ConstructorId_isFinite = -1;
    private static final int ConstructorId_isInteger = -3;
    private static final int ConstructorId_isNaN = -2;
    private static final int ConstructorId_isSafeInteger = -4;
    private static final int ConstructorId_parseFloat = -5;
    private static final int ConstructorId_parseInt = -6;
    private static final int Id_constructor = 1;
    private static final int Id_toExponential = 7;
    private static final int Id_toFixed = 6;
    private static final int Id_toLocaleString = 3;
    private static final int Id_toPrecision = 8;
    private static final int Id_toSource = 4;
    private static final int Id_toString = 2;
    private static final int Id_valueOf = 5;
    private static final int MAX_PRECISION = 100;
    private static final int MAX_PROTOTYPE_ID = 8;
    private static final double MAX_SAFE_INTEGER = (Math.pow(2.0d, 53.0d) - 1.0d);
    private static final double MIN_SAFE_INTEGER = (-MAX_SAFE_INTEGER);
    private static final Object NUMBER_TAG = "Number";
    static final long serialVersionUID = 3504516769741512101L;
    private double doubleValue;

    NativeNumber(double d) {
        this.doubleValue = d;
    }

    private Double doubleVal(Number number) {
        return number instanceof Double ? (Double) number : Double.valueOf(number.doubleValue());
    }

    private Object execConstructorCall(int i, Object[] objArr) {
        switch (i) {
            case ConstructorId_parseInt /*-6*/:
                return NativeGlobal.js_parseInt(objArr);
            case ConstructorId_parseFloat /*-5*/:
                return NativeGlobal.js_parseFloat(objArr);
            case ConstructorId_isSafeInteger /*-4*/:
                return (objArr.length == 0 || Undefined.instance == objArr[0]) ? Boolean.valueOf(false) : objArr[0] instanceof Number ? Boolean.valueOf(isSafeInteger((Number) objArr[0])) : Boolean.valueOf(false);
            case ConstructorId_isInteger /*-3*/:
                return (objArr.length == 0 || Undefined.instance == objArr[0]) ? Boolean.valueOf(false) : objArr[0] instanceof Number ? Boolean.valueOf(isInteger((Number) objArr[0])) : Boolean.valueOf(false);
            case ConstructorId_isNaN /*-2*/:
                return (objArr.length == 0 || Undefined.instance == objArr[0]) ? Boolean.valueOf(false) : objArr[0] instanceof Number ? isNaN((Number) objArr[0]) : Boolean.valueOf(false);
            case ConstructorId_isFinite /*-1*/:
                return (objArr.length == 0 || Undefined.instance == objArr[0]) ? Boolean.valueOf(false) : objArr[0] instanceof Number ? isFinite(objArr[0]) : Boolean.valueOf(false);
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
    }

    static void init(Scriptable scriptable, boolean z) {
        new NativeNumber(MIN_SAFE_INTEGER).exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    private boolean isDoubleInteger(Double d) {
        return (d.isInfinite() || d.isNaN() || Math.floor(d.doubleValue()) != d.doubleValue()) ? false : true;
    }

    private boolean isDoubleNan(Double d) {
        return d.isNaN();
    }

    private boolean isDoubleSafeInteger(Double d) {
        return isDoubleInteger(d) && d.doubleValue() <= MAX_SAFE_INTEGER && d.doubleValue() >= MIN_SAFE_INTEGER;
    }

    static Object isFinite(Object obj) {
        Double valueOf = Double.valueOf(ScriptRuntime.toNumber(obj));
        boolean z = (valueOf.isInfinite() || valueOf.isNaN()) ? false : true;
        return ScriptRuntime.wrapBoolean(z);
    }

    private boolean isInteger(Number number) {
        return ScriptRuntime.toBoolean(Boolean.valueOf(isDoubleInteger(doubleVal(number))));
    }

    private Object isNaN(Number number) {
        return Boolean.valueOf(ScriptRuntime.toBoolean(Boolean.valueOf(isDoubleNan(doubleVal(number)))));
    }

    private boolean isSafeInteger(Number number) {
        return ScriptRuntime.toBoolean(Boolean.valueOf(isDoubleSafeInteger(doubleVal(number))));
    }

    private static String num_to(double d, Object[] objArr, int i, int i2, int i3, int i4) {
        int i5 = 0;
        if (objArr.length != 0) {
            double toInteger = ScriptRuntime.toInteger(objArr[0]);
            if (toInteger < ((double) i3) || toInteger > EventEnums.SampleRate_NoSampling) {
                throw ScriptRuntime.constructError("RangeError", ScriptRuntime.getMessage1("msg.bad.precision", ScriptRuntime.toString(objArr[0])));
            }
            i5 = ScriptRuntime.toInt32(toInteger);
            i = i2;
        }
        StringBuilder stringBuilder = new StringBuilder();
        DToA.JS_dtostr(stringBuilder, i, i5 + i4, d);
        return stringBuilder.toString();
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(NUMBER_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        if (methodId == Id_constructor) {
            double toNumber = objArr.length >= Id_constructor ? ScriptRuntime.toNumber(objArr[0]) : MIN_SAFE_INTEGER;
            return scriptable2 == null ? new NativeNumber(toNumber) : ScriptRuntime.wrapNumber(toNumber);
        } else if (methodId < Id_constructor) {
            return execConstructorCall(methodId, objArr);
        } else {
            if (scriptable2 instanceof NativeNumber) {
                double d = ((NativeNumber) scriptable2).doubleValue;
                switch (methodId) {
                    case Id_toString /*2*/:
                    case Id_toLocaleString /*3*/:
                        methodId = (objArr.length == 0 || objArr[0] == Undefined.instance) ? 10 : ScriptRuntime.toInt32(objArr[0]);
                        return ScriptRuntime.numberToString(d, methodId);
                    case Id_toSource /*4*/:
                        return "(new Number(" + ScriptRuntime.toString(d) + "))";
                    case Id_valueOf /*5*/:
                        return ScriptRuntime.wrapNumber(d);
                    case Id_toFixed /*6*/:
                        return num_to(d, objArr, Id_toString, Id_toString, -20, 0);
                    case Id_toExponential /*7*/:
                        return Double.isNaN(d) ? "NaN" : Double.isInfinite(d) ? d >= MIN_SAFE_INTEGER ? "Infinity" : "-Infinity" : num_to(d, objArr, Id_constructor, Id_toLocaleString, 0, Id_constructor);
                    case MAX_PROTOTYPE_ID /*8*/:
                        return (objArr.length == 0 || objArr[0] == Undefined.instance) ? ScriptRuntime.numberToString(d, 10) : Double.isNaN(d) ? "NaN" : Double.isInfinite(d) ? d >= MIN_SAFE_INTEGER ? "Infinity" : "-Infinity" : num_to(d, objArr, 0, Id_toSource, Id_constructor, 0);
                    default:
                        throw new IllegalArgumentException(String.valueOf(methodId));
                }
            }
            throw IdScriptableObject.incompatibleCallError(idFunctionObject);
        }
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        idFunctionObject.defineProperty("NaN", (Object) ScriptRuntime.NaNobj, (int) Id_toExponential);
        idFunctionObject.defineProperty("POSITIVE_INFINITY", (Object) ScriptRuntime.wrapNumber(Double.POSITIVE_INFINITY), (int) Id_toExponential);
        idFunctionObject.defineProperty("NEGATIVE_INFINITY", (Object) ScriptRuntime.wrapNumber(Double.NEGATIVE_INFINITY), (int) Id_toExponential);
        idFunctionObject.defineProperty("MAX_VALUE", (Object) ScriptRuntime.wrapNumber(Double.MAX_VALUE), (int) Id_toExponential);
        idFunctionObject.defineProperty("MIN_VALUE", (Object) ScriptRuntime.wrapNumber(Double.MIN_VALUE), (int) Id_toExponential);
        idFunctionObject.defineProperty("MAX_SAFE_INTEGER", (Object) ScriptRuntime.wrapNumber(MAX_SAFE_INTEGER), (int) Id_toExponential);
        idFunctionObject.defineProperty("MIN_SAFE_INTEGER", (Object) ScriptRuntime.wrapNumber(MIN_SAFE_INTEGER), (int) Id_toExponential);
        addIdFunctionProperty(idFunctionObject, NUMBER_TAG, ConstructorId_isFinite, "isFinite", Id_constructor);
        addIdFunctionProperty(idFunctionObject, NUMBER_TAG, ConstructorId_isNaN, "isNaN", Id_constructor);
        addIdFunctionProperty(idFunctionObject, NUMBER_TAG, ConstructorId_isInteger, "isInteger", Id_constructor);
        addIdFunctionProperty(idFunctionObject, NUMBER_TAG, ConstructorId_isSafeInteger, "isSafeInteger", Id_constructor);
        addIdFunctionProperty(idFunctionObject, NUMBER_TAG, ConstructorId_parseFloat, "parseFloat", Id_constructor);
        addIdFunctionProperty(idFunctionObject, NUMBER_TAG, ConstructorId_parseInt, "parseInt", Id_constructor);
        super.fillConstructorProperties(idFunctionObject);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findPrototypeId(java.lang.String r7) {
        /*
        r6 = this;
        r2 = 3;
        r4 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        r0 = 0;
        r1 = 0;
        r3 = r7.length();
        switch(r3) {
            case 7: goto L_0x0019;
            case 8: goto L_0x0031;
            case 9: goto L_0x000c;
            case 10: goto L_0x000c;
            case 11: goto L_0x0049;
            case 12: goto L_0x000c;
            case 13: goto L_0x0062;
            case 14: goto L_0x0069;
            default: goto L_0x000c;
        };
    L_0x000c:
        r2 = r1;
        r1 = r0;
    L_0x000e:
        if (r2 == 0) goto L_0x006f;
    L_0x0010:
        if (r2 == r7) goto L_0x006f;
    L_0x0012:
        r2 = r2.equals(r7);
        if (r2 != 0) goto L_0x006f;
    L_0x0018:
        return r0;
    L_0x0019:
        r2 = r7.charAt(r0);
        if (r2 != r4) goto L_0x0026;
    L_0x001f:
        r1 = "toFixed";
        r2 = 6;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000e;
    L_0x0026:
        r3 = 118; // 0x76 float:1.65E-43 double:5.83E-322;
        if (r2 != r3) goto L_0x000c;
    L_0x002a:
        r1 = "valueOf";
        r2 = 5;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000e;
    L_0x0031:
        r2 = r7.charAt(r2);
        r3 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r2 != r3) goto L_0x0040;
    L_0x0039:
        r1 = "toSource";
        r2 = 4;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000e;
    L_0x0040:
        if (r2 != r4) goto L_0x000c;
    L_0x0042:
        r1 = "toString";
        r2 = 2;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000e;
    L_0x0049:
        r2 = r7.charAt(r0);
        r3 = 99;
        if (r2 != r3) goto L_0x0058;
    L_0x0051:
        r1 = "constructor";
        r2 = 1;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000e;
    L_0x0058:
        if (r2 != r4) goto L_0x000c;
    L_0x005a:
        r1 = "toPrecision";
        r2 = 8;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000e;
    L_0x0062:
        r1 = "toExponential";
        r2 = 7;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000e;
    L_0x0069:
        r1 = "toLocaleString";
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000e;
    L_0x006f:
        r0 = r1;
        goto L_0x0018;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeNumber.findPrototypeId(java.lang.String):int");
    }

    public String getClassName() {
        return "Number";
    }

    protected void initPrototypeId(int i) {
        String str;
        int i2 = Id_constructor;
        switch (i) {
            case Id_constructor /*1*/:
                str = "constructor";
                break;
            case Id_toString /*2*/:
                str = "toString";
                break;
            case Id_toLocaleString /*3*/:
                str = "toLocaleString";
                break;
            case Id_toSource /*4*/:
                str = "toSource";
                i2 = 0;
                break;
            case Id_valueOf /*5*/:
                str = "valueOf";
                i2 = 0;
                break;
            case Id_toFixed /*6*/:
                str = "toFixed";
                break;
            case Id_toExponential /*7*/:
                str = "toExponential";
                break;
            case MAX_PROTOTYPE_ID /*8*/:
                str = "toPrecision";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(NUMBER_TAG, i, str, i2);
    }

    public String toString() {
        return ScriptRuntime.numberToString(this.doubleValue, 10);
    }
}
