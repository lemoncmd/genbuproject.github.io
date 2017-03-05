package org.mozilla.javascript;

public final class NativeCall extends IdScriptableObject {
    private static final Object CALL_TAG = "Call";
    private static final int Id_constructor = 1;
    private static final int MAX_PROTOTYPE_ID = 1;
    static final long serialVersionUID = -7471457301304454454L;
    private Arguments arguments;
    NativeFunction function;
    boolean isStrict;
    Object[] originalArgs;
    transient NativeCall parentActivationCall;

    NativeCall() {
    }

    NativeCall(NativeFunction nativeFunction, Scriptable scriptable, Object[] objArr, boolean z, boolean z2) {
        this.function = nativeFunction;
        setParentScope(scriptable);
        this.originalArgs = objArr == null ? ScriptRuntime.emptyArgs : objArr;
        this.isStrict = z2;
        int paramAndVarCount = nativeFunction.getParamAndVarCount();
        int paramCount = nativeFunction.getParamCount();
        if (paramAndVarCount != 0) {
            int i = 0;
            while (i < paramCount) {
                defineProperty(nativeFunction.getParamOrVarName(i), i < objArr.length ? objArr[i] : Undefined.instance, 4);
                i += MAX_PROTOTYPE_ID;
            }
        }
        if (!(super.has("arguments", this) || z)) {
            this.arguments = new Arguments(this);
            defineProperty("arguments", (Object) this.arguments, 4);
        }
        if (paramAndVarCount != 0) {
            for (int i2 = paramCount; i2 < paramAndVarCount; i2 += MAX_PROTOTYPE_ID) {
                String paramOrVarName = nativeFunction.getParamOrVarName(i2);
                if (!super.has(paramOrVarName, this)) {
                    if (nativeFunction.getParamOrVarConst(i2)) {
                        defineProperty(paramOrVarName, Undefined.instance, 13);
                    } else {
                        defineProperty(paramOrVarName, Undefined.instance, 4);
                    }
                }
            }
        }
    }

    static void init(Scriptable scriptable, boolean z) {
        new NativeCall().exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    public void defineAttributesForArguments() {
        if (this.arguments != null) {
            this.arguments.defineAttributesForStrictMode();
        }
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(CALL_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        if (methodId != MAX_PROTOTYPE_ID) {
            throw new IllegalArgumentException(String.valueOf(methodId));
        } else if (scriptable2 != null) {
            throw Context.reportRuntimeError1("msg.only.from.new", "Call");
        } else {
            ScriptRuntime.checkDeprecated(context, "Call");
            NativeCall nativeCall = new NativeCall();
            nativeCall.setPrototype(ScriptableObject.getObjectPrototype(scriptable));
            return nativeCall;
        }
    }

    protected int findPrototypeId(String str) {
        return str.equals("constructor") ? MAX_PROTOTYPE_ID : 0;
    }

    public String getClassName() {
        return "Call";
    }

    protected void initPrototypeId(int i) {
        if (i == MAX_PROTOTYPE_ID) {
            initPrototypeMethod(CALL_TAG, i, "constructor", MAX_PROTOTYPE_ID);
            return;
        }
        throw new IllegalArgumentException(String.valueOf(i));
    }
}
