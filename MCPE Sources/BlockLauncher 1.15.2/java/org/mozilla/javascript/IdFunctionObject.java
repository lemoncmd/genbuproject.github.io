package org.mozilla.javascript;

import net.hockeyapp.android.BuildConfig;

public class IdFunctionObject extends BaseFunction {
    static final long serialVersionUID = -5332312783643935019L;
    private int arity;
    private String functionName;
    private final IdFunctionCall idcall;
    private final int methodId;
    private final Object tag;
    private boolean useCallAsConstructor;

    public IdFunctionObject(IdFunctionCall idFunctionCall, Object obj, int i, int i2) {
        if (i2 < 0) {
            throw new IllegalArgumentException();
        }
        this.idcall = idFunctionCall;
        this.tag = obj;
        this.methodId = i;
        this.arity = i2;
        if (i2 < 0) {
            throw new IllegalArgumentException();
        }
    }

    public IdFunctionObject(IdFunctionCall idFunctionCall, Object obj, int i, String str, int i2, Scriptable scriptable) {
        super(scriptable, null);
        if (i2 < 0) {
            throw new IllegalArgumentException();
        } else if (str == null) {
            throw new IllegalArgumentException();
        } else {
            this.idcall = idFunctionCall;
            this.tag = obj;
            this.methodId = i;
            this.arity = i2;
            this.functionName = str;
        }
    }

    public final void addAsProperty(Scriptable scriptable) {
        ScriptableObject.defineProperty(scriptable, this.functionName, this, 2);
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        return this.idcall.execIdCall(this, context, scriptable, scriptable2, objArr);
    }

    public Scriptable createObject(Context context, Scriptable scriptable) {
        if (this.useCallAsConstructor) {
            return null;
        }
        throw ScriptRuntime.typeError1("msg.not.ctor", this.functionName);
    }

    String decompile(int i, int i2) {
        StringBuilder stringBuilder = new StringBuilder();
        Object obj = (i2 & 1) != 0 ? 1 : null;
        if (obj == null) {
            stringBuilder.append("function ");
            stringBuilder.append(getFunctionName());
            stringBuilder.append("() { ");
        }
        stringBuilder.append("[native code for ");
        if (this.idcall instanceof Scriptable) {
            stringBuilder.append(((Scriptable) this.idcall).getClassName());
            stringBuilder.append('.');
        }
        stringBuilder.append(getFunctionName());
        stringBuilder.append(", arity=");
        stringBuilder.append(getArity());
        stringBuilder.append(obj != null ? "]\n" : "] }\n");
        return stringBuilder.toString();
    }

    public void exportAsScopeProperty() {
        addAsProperty(getParentScope());
    }

    public int getArity() {
        return this.arity;
    }

    public String getFunctionName() {
        return this.functionName == null ? BuildConfig.FLAVOR : this.functionName;
    }

    public int getLength() {
        return getArity();
    }

    public Scriptable getPrototype() {
        Scriptable prototype = super.getPrototype();
        if (prototype != null) {
            return prototype;
        }
        prototype = ScriptableObject.getFunctionPrototype(getParentScope());
        setPrototype(prototype);
        return prototype;
    }

    public Object getTag() {
        return this.tag;
    }

    public final boolean hasTag(Object obj) {
        return obj == null ? this.tag == null : obj.equals(this.tag);
    }

    public void initFunction(String str, Scriptable scriptable) {
        if (str == null) {
            throw new IllegalArgumentException();
        } else if (scriptable == null) {
            throw new IllegalArgumentException();
        } else {
            this.functionName = str;
            setParentScope(scriptable);
        }
    }

    public final void markAsConstructor(Scriptable scriptable) {
        this.useCallAsConstructor = true;
        setImmunePrototypeProperty(scriptable);
    }

    public final int methodId() {
        return this.methodId;
    }

    public final RuntimeException unknown() {
        return new IllegalArgumentException("BAD FUNCTION ID=" + this.methodId + " MASTER=" + this.idcall);
    }
}
