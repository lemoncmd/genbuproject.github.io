package org.mozilla.javascript;

final class NativeBoolean extends IdScriptableObject {
    private static final Object BOOLEAN_TAG = "Boolean";
    private static final int Id_constructor = 1;
    private static final int Id_toSource = 3;
    private static final int Id_toString = 2;
    private static final int Id_valueOf = 4;
    private static final int MAX_PROTOTYPE_ID = 4;
    static final long serialVersionUID = -3716996899943880933L;
    private boolean booleanValue;

    NativeBoolean(boolean z) {
        this.booleanValue = z;
    }

    static void init(Scriptable scriptable, boolean z) {
        new NativeBoolean(false).exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(BOOLEAN_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        boolean z;
        if (methodId == Id_constructor) {
            if (objArr.length == 0) {
                z = false;
            } else {
                boolean toBoolean = ((objArr[0] instanceof ScriptableObject) && ((ScriptableObject) objArr[0]).avoidObjectDetection()) ? true : ScriptRuntime.toBoolean(objArr[0]);
                z = toBoolean;
            }
            return scriptable2 == null ? new NativeBoolean(z) : ScriptRuntime.wrapBoolean(z);
        } else if (scriptable2 instanceof NativeBoolean) {
            z = ((NativeBoolean) scriptable2).booleanValue;
            switch (methodId) {
                case Id_toString /*2*/:
                    return z ? "true" : "false";
                case Id_toSource /*3*/:
                    return z ? "(new Boolean(true))" : "(new Boolean(false))";
                case MAX_PROTOTYPE_ID /*4*/:
                    return ScriptRuntime.wrapBoolean(z);
                default:
                    throw new IllegalArgumentException(String.valueOf(methodId));
            }
        } else {
            throw IdScriptableObject.incompatibleCallError(idFunctionObject);
        }
    }

    protected int findPrototypeId(String str) {
        int i;
        String str2;
        int length = str.length();
        if (length == 7) {
            i = MAX_PROTOTYPE_ID;
            str2 = "valueOf";
        } else {
            if (length == 8) {
                char charAt = str.charAt(Id_toSource);
                if (charAt == 'o') {
                    i = Id_toSource;
                    str2 = "toSource";
                } else if (charAt == 't') {
                    i = Id_toString;
                    str2 = "toString";
                }
            } else if (length == 11) {
                i = Id_constructor;
                str2 = "constructor";
            }
            str2 = null;
            i = 0;
        }
        return (str2 == null || str2 == str || str2.equals(str)) ? i : 0;
    }

    public String getClassName() {
        return "Boolean";
    }

    public Object getDefaultValue(Class<?> cls) {
        return cls == ScriptRuntime.BooleanClass ? ScriptRuntime.wrapBoolean(this.booleanValue) : super.getDefaultValue(cls);
    }

    protected void initPrototypeId(int i) {
        String str;
        int i2 = 0;
        switch (i) {
            case Id_constructor /*1*/:
                i2 = Id_constructor;
                str = "constructor";
                break;
            case Id_toString /*2*/:
                str = "toString";
                break;
            case Id_toSource /*3*/:
                str = "toSource";
                break;
            case MAX_PROTOTYPE_ID /*4*/:
                str = "valueOf";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(BOOLEAN_TAG, i, str, i2);
    }
}
