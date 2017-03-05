package org.mozilla.javascript;

import net.hockeyapp.android.BuildConfig;

public class NativeCallSite extends IdScriptableObject {
    private static final String CALLSITE_TAG = "CallSite";
    private static final int Id_constructor = 1;
    private static final int Id_getColumnNumber = 9;
    private static final int Id_getEvalOrigin = 10;
    private static final int Id_getFileName = 7;
    private static final int Id_getFunction = 4;
    private static final int Id_getFunctionName = 5;
    private static final int Id_getLineNumber = 8;
    private static final int Id_getMethodName = 6;
    private static final int Id_getThis = 2;
    private static final int Id_getTypeName = 3;
    private static final int Id_isConstructor = 14;
    private static final int Id_isEval = 12;
    private static final int Id_isNative = 13;
    private static final int Id_isToplevel = 11;
    private static final int Id_toString = 15;
    private static final int MAX_PROTOTYPE_ID = 15;
    private ScriptStackElement element;

    private NativeCallSite() {
    }

    private Object getFalse() {
        return Boolean.FALSE;
    }

    private Object getFileName(Scriptable scriptable) {
        Scriptable scriptable2 = scriptable;
        while (scriptable2 != null && !(scriptable2 instanceof NativeCallSite)) {
            scriptable2 = scriptable2.getPrototype();
        }
        if (scriptable2 == null) {
            return NOT_FOUND;
        }
        NativeCallSite nativeCallSite = (NativeCallSite) scriptable2;
        return nativeCallSite.element == null ? null : nativeCallSite.element.fileName;
    }

    private Object getFunctionName(Scriptable scriptable) {
        Scriptable scriptable2 = scriptable;
        while (scriptable2 != null && !(scriptable2 instanceof NativeCallSite)) {
            scriptable2 = scriptable2.getPrototype();
        }
        if (scriptable2 == null) {
            return NOT_FOUND;
        }
        NativeCallSite nativeCallSite = (NativeCallSite) scriptable2;
        return nativeCallSite.element == null ? null : nativeCallSite.element.functionName;
    }

    private Object getLineNumber(Scriptable scriptable) {
        Scriptable scriptable2 = scriptable;
        while (scriptable2 != null && !(scriptable2 instanceof NativeCallSite)) {
            scriptable2 = scriptable2.getPrototype();
        }
        if (scriptable2 == null) {
            return NOT_FOUND;
        }
        NativeCallSite nativeCallSite = (NativeCallSite) scriptable2;
        return (nativeCallSite.element == null || nativeCallSite.element.lineNumber < 0) ? Undefined.instance : Integer.valueOf(nativeCallSite.element.lineNumber);
    }

    private Object getNull() {
        return null;
    }

    private Object getUndefined() {
        return Undefined.instance;
    }

    static void init(Scriptable scriptable, boolean z) {
        new NativeCallSite().exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    private Object js_toString(Scriptable scriptable) {
        Scriptable scriptable2 = scriptable;
        while (scriptable2 != null && !(scriptable2 instanceof NativeCallSite)) {
            scriptable2 = scriptable2.getPrototype();
        }
        if (scriptable2 == null) {
            return NOT_FOUND;
        }
        NativeCallSite nativeCallSite = (NativeCallSite) scriptable2;
        StringBuilder stringBuilder = new StringBuilder();
        nativeCallSite.element.renderJavaStyle(stringBuilder);
        return stringBuilder.toString();
    }

    static NativeCallSite make(Scriptable scriptable, Scriptable scriptable2) {
        NativeCallSite nativeCallSite = new NativeCallSite();
        Scriptable scriptable3 = (Scriptable) scriptable2.get("prototype", scriptable2);
        nativeCallSite.setParentScope(scriptable);
        nativeCallSite.setPrototype(scriptable3);
        return nativeCallSite;
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(CALLSITE_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        switch (methodId) {
            case Id_constructor /*1*/:
                return make(scriptable, idFunctionObject);
            case Id_getThis /*2*/:
            case Id_getTypeName /*3*/:
            case Id_getFunction /*4*/:
            case Id_getColumnNumber /*9*/:
                return getUndefined();
            case Id_getFunctionName /*5*/:
                return getFunctionName(scriptable2);
            case Id_getMethodName /*6*/:
                return getNull();
            case Id_getFileName /*7*/:
                return getFileName(scriptable2);
            case Id_getLineNumber /*8*/:
                return getLineNumber(scriptable2);
            case Id_getEvalOrigin /*10*/:
            case Id_isToplevel /*11*/:
            case Id_isEval /*12*/:
            case Id_isNative /*13*/:
            case Id_isConstructor /*14*/:
                return getFalse();
            case MAX_PROTOTYPE_ID /*15*/:
                return js_toString(scriptable2);
            default:
                throw new IllegalArgumentException(String.valueOf(methodId));
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findPrototypeId(java.lang.String r7) {
        /*
        r6 = this;
        r2 = 4;
        r3 = 3;
        r0 = 0;
        r1 = 0;
        r4 = r7.length();
        switch(r4) {
            case 6: goto L_0x0018;
            case 7: goto L_0x0020;
            case 8: goto L_0x0027;
            case 9: goto L_0x000b;
            case 10: goto L_0x0043;
            case 11: goto L_0x004b;
            case 12: goto L_0x000b;
            case 13: goto L_0x006e;
            case 14: goto L_0x000b;
            case 15: goto L_0x0099;
            default: goto L_0x000b;
        };
    L_0x000b:
        r2 = r1;
        r1 = r0;
    L_0x000d:
        if (r2 == 0) goto L_0x00b6;
    L_0x000f:
        if (r2 == r7) goto L_0x00b6;
    L_0x0011:
        r2 = r2.equals(r7);
        if (r2 != 0) goto L_0x00b6;
    L_0x0017:
        return r0;
    L_0x0018:
        r1 = "isEval";
        r2 = 12;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0020:
        r1 = "getThis";
        r2 = 2;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0027:
        r2 = r7.charAt(r0);
        r3 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r2 != r3) goto L_0x0037;
    L_0x002f:
        r1 = "isNative";
        r2 = 13;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0037:
        r3 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r2 != r3) goto L_0x000b;
    L_0x003b:
        r1 = "toString";
        r2 = 15;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0043:
        r1 = "isToplevel";
        r2 = 11;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x004b:
        r4 = r7.charAt(r2);
        switch(r4) {
            case 105: goto L_0x0055;
            case 116: goto L_0x005c;
            case 117: goto L_0x0063;
            case 121: goto L_0x0069;
            default: goto L_0x0052;
        };
    L_0x0052:
        r2 = r1;
        r1 = r0;
        goto L_0x000d;
    L_0x0055:
        r1 = "getFileName";
        r2 = 7;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x005c:
        r1 = "constructor";
        r2 = 1;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0063:
        r1 = "getFunction";
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0069:
        r1 = "getTypeName";
        r2 = r1;
        r1 = r3;
        goto L_0x000d;
    L_0x006e:
        r2 = r7.charAt(r3);
        switch(r2) {
            case 69: goto L_0x0078;
            case 76: goto L_0x0080;
            case 77: goto L_0x0088;
            case 111: goto L_0x0090;
            default: goto L_0x0075;
        };
    L_0x0075:
        r2 = r1;
        r1 = r0;
        goto L_0x000d;
    L_0x0078:
        r1 = "getEvalOrigin";
        r2 = 10;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0080:
        r1 = "getLineNumber";
        r2 = 8;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0088:
        r1 = "getMethodName";
        r2 = 6;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0090:
        r1 = "isConstructor";
        r2 = 14;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x0099:
        r2 = r7.charAt(r3);
        r3 = 67;
        if (r2 != r3) goto L_0x00aa;
    L_0x00a1:
        r1 = "getColumnNumber";
        r2 = 9;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x00aa:
        r3 = 70;
        if (r2 != r3) goto L_0x000b;
    L_0x00ae:
        r1 = "getFunctionName";
        r2 = 5;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000d;
    L_0x00b6:
        r0 = r1;
        goto L_0x0017;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeCallSite.findPrototypeId(java.lang.String):int");
    }

    public String getClassName() {
        return CALLSITE_TAG;
    }

    protected void initPrototypeId(int i) {
        String str;
        switch (i) {
            case Id_constructor /*1*/:
                str = "constructor";
                break;
            case Id_getThis /*2*/:
                str = "getThis";
                break;
            case Id_getTypeName /*3*/:
                str = "getTypeName";
                break;
            case Id_getFunction /*4*/:
                str = "getFunction";
                break;
            case Id_getFunctionName /*5*/:
                str = "getFunctionName";
                break;
            case Id_getMethodName /*6*/:
                str = "getMethodName";
                break;
            case Id_getFileName /*7*/:
                str = "getFileName";
                break;
            case Id_getLineNumber /*8*/:
                str = "getLineNumber";
                break;
            case Id_getColumnNumber /*9*/:
                str = "getColumnNumber";
                break;
            case Id_getEvalOrigin /*10*/:
                str = "getEvalOrigin";
                break;
            case Id_isToplevel /*11*/:
                str = "isToplevel";
                break;
            case Id_isEval /*12*/:
                str = "isEval";
                break;
            case Id_isNative /*13*/:
                str = "isNative";
                break;
            case Id_isConstructor /*14*/:
                str = "isConstructor";
                break;
            case MAX_PROTOTYPE_ID /*15*/:
                str = "toString";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(CALLSITE_TAG, i, str, 0);
    }

    void setElement(ScriptStackElement scriptStackElement) {
        this.element = scriptStackElement;
    }

    public String toString() {
        return this.element == null ? BuildConfig.FLAVOR : this.element.toString();
    }
}
