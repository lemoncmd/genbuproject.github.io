package org.mozilla.javascript;

public class ArrowFunction extends BaseFunction {
    static final long serialVersionUID = -7377989503697220633L;
    private final Scriptable boundThis;
    private final Callable targetFunction;

    public ArrowFunction(Context context, Scriptable scriptable, Callable callable, Scriptable scriptable2) {
        this.targetFunction = callable;
        this.boundThis = scriptable2;
        ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
        BaseFunction typeErrorThrower = ScriptRuntime.typeErrorThrower();
        ScriptableObject nativeObject = new NativeObject();
        nativeObject.put("get", nativeObject, typeErrorThrower);
        nativeObject.put("set", nativeObject, typeErrorThrower);
        nativeObject.put("enumerable", nativeObject, Boolean.valueOf(false));
        nativeObject.put("configurable", nativeObject, Boolean.valueOf(false));
        nativeObject.preventExtensions();
        defineOwnProperty(context, "caller", nativeObject, false);
        defineOwnProperty(context, "arguments", nativeObject, false);
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        return this.targetFunction.call(context, scriptable, this.boundThis != null ? this.boundThis : ScriptRuntime.getTopCallScope(context), objArr);
    }

    public Scriptable construct(Context context, Scriptable scriptable, Object[] objArr) {
        throw ScriptRuntime.typeError1("msg.not.ctor", decompile(0, 0));
    }

    String decompile(int i, int i2) {
        return this.targetFunction instanceof BaseFunction ? ((BaseFunction) this.targetFunction).decompile(i, i2) : super.decompile(i, i2);
    }

    public int getLength() {
        return this.targetFunction instanceof BaseFunction ? ((BaseFunction) this.targetFunction).getLength() : 0;
    }

    public boolean hasInstance(Scriptable scriptable) {
        if (this.targetFunction instanceof Function) {
            return ((Function) this.targetFunction).hasInstance(scriptable);
        }
        throw ScriptRuntime.typeError0("msg.not.ctor");
    }
}
