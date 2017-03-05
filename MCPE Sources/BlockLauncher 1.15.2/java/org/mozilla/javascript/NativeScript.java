package org.mozilla.javascript;

import net.hockeyapp.android.BuildConfig;

class NativeScript extends BaseFunction {
    private static final int Id_compile = 3;
    private static final int Id_constructor = 1;
    private static final int Id_exec = 4;
    private static final int Id_toString = 2;
    private static final int MAX_PROTOTYPE_ID = 4;
    private static final Object SCRIPT_TAG = "Script";
    static final long serialVersionUID = -6795101161980121700L;
    private Script script;

    private NativeScript(Script script) {
        this.script = script;
    }

    private static Script compile(Context context, String str) {
        int[] iArr = new int[Id_constructor];
        iArr[0] = 0;
        String sourcePositionFromStack = Context.getSourcePositionFromStack(iArr);
        if (sourcePositionFromStack == null) {
            sourcePositionFromStack = "<Script object>";
            iArr[0] = Id_constructor;
        }
        return context.compileString(str, null, DefaultErrorReporter.forEval(context.getErrorReporter()), sourcePositionFromStack, iArr[0], null);
    }

    static void init(Scriptable scriptable, boolean z) {
        new NativeScript(null).exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    private static NativeScript realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeScript) {
            return (NativeScript) scriptable;
        }
        throw IdScriptableObject.incompatibleCallError(idFunctionObject);
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        return this.script != null ? this.script.exec(context, scriptable) : Undefined.instance;
    }

    public Scriptable construct(Context context, Scriptable scriptable, Object[] objArr) {
        throw Context.reportRuntimeError0("msg.script.is.not.constructor");
    }

    String decompile(int i, int i2) {
        return this.script instanceof NativeFunction ? ((NativeFunction) this.script).decompile(i, i2) : super.decompile(i, i2);
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(SCRIPT_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        switch (methodId) {
            case Id_constructor /*1*/:
                ScriptableObject nativeScript = new NativeScript(compile(context, objArr.length == 0 ? BuildConfig.FLAVOR : ScriptRuntime.toString(objArr[0])));
                ScriptRuntime.setObjectProtoAndParent(nativeScript, scriptable);
                return nativeScript;
            case Id_toString /*2*/:
                Script script = realThis(scriptable2, idFunctionObject).script;
                return script == null ? BuildConfig.FLAVOR : context.decompileScript(script, 0);
            case Id_compile /*3*/:
                NativeScript realThis = realThis(scriptable2, idFunctionObject);
                realThis.script = compile(context, ScriptRuntime.toString(objArr, 0));
                return realThis;
            case MAX_PROTOTYPE_ID /*4*/:
                throw Context.reportRuntimeError1("msg.cant.call.indirect", "exec");
            default:
                throw new IllegalArgumentException(String.valueOf(methodId));
        }
    }

    protected int findPrototypeId(String str) {
        int i;
        String str2;
        switch (str.length()) {
            case MAX_PROTOTYPE_ID /*4*/:
                i = MAX_PROTOTYPE_ID;
                str2 = "exec";
                break;
            case Token.IFNE /*7*/:
                i = Id_compile;
                str2 = "compile";
                break;
            case Token.SETNAME /*8*/:
                i = Id_toString;
                str2 = "toString";
                break;
            case Token.BITAND /*11*/:
                i = Id_constructor;
                str2 = "constructor";
                break;
            default:
                str2 = null;
                i = 0;
                break;
        }
        return (str2 == null || str2 == str || str2.equals(str)) ? i : 0;
    }

    public int getArity() {
        return 0;
    }

    public String getClassName() {
        return "Script";
    }

    public int getLength() {
        return 0;
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
                i2 = 0;
                break;
            case Id_compile /*3*/:
                str = "compile";
                break;
            case MAX_PROTOTYPE_ID /*4*/:
                str = "exec";
                i2 = 0;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(SCRIPT_TAG, i, str, i2);
    }
}
