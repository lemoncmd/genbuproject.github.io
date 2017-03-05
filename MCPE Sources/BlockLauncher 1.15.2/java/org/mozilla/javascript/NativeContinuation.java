package org.mozilla.javascript;

public final class NativeContinuation extends IdScriptableObject implements Function {
    private static final Object FTAG = "Continuation";
    private static final int Id_constructor = 1;
    private static final int MAX_PROTOTYPE_ID = 1;
    static final long serialVersionUID = 1794167133757605367L;
    private Object implementation;

    public static void init(Context context, Scriptable scriptable, boolean z) {
        new NativeContinuation().exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    public static boolean isContinuationConstructor(IdFunctionObject idFunctionObject) {
        return idFunctionObject.hasTag(FTAG) && idFunctionObject.methodId() == MAX_PROTOTYPE_ID;
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        return Interpreter.restartContinuation(this, context, scriptable, objArr);
    }

    public Scriptable construct(Context context, Scriptable scriptable, Object[] objArr) {
        throw Context.reportRuntimeError("Direct call is not supported");
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(FTAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        switch (methodId) {
            case MAX_PROTOTYPE_ID /*1*/:
                throw Context.reportRuntimeError("Direct call is not supported");
            default:
                throw new IllegalArgumentException(String.valueOf(methodId));
        }
    }

    protected int findPrototypeId(String str) {
        int i;
        String str2;
        if (str.length() == 11) {
            i = MAX_PROTOTYPE_ID;
            str2 = "constructor";
        } else {
            str2 = null;
            i = 0;
        }
        return (str2 == null || str2 == str || str2.equals(str)) ? i : 0;
    }

    public String getClassName() {
        return "Continuation";
    }

    public Object getImplementation() {
        return this.implementation;
    }

    public void initImplementation(Object obj) {
        this.implementation = obj;
    }

    protected void initPrototypeId(int i) {
        switch (i) {
            case MAX_PROTOTYPE_ID /*1*/:
                initPrototypeMethod(FTAG, i, "constructor", 0);
                return;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
    }
}
