package org.mozilla.javascript;

import org.mozilla.javascript.debug.DebuggableScript;

public abstract class NativeFunction extends BaseFunction {
    static final long serialVersionUID = 8713897114082216401L;

    final String decompile(int i, int i2) {
        String encodedSource = getEncodedSource();
        if (encodedSource == null) {
            return super.decompile(i, i2);
        }
        UintMap uintMap = new UintMap(1);
        uintMap.put(1, i);
        return Decompiler.decompile(encodedSource, i2, uintMap);
    }

    public int getArity() {
        return getParamCount();
    }

    public DebuggableScript getDebuggableView() {
        return null;
    }

    public String getEncodedSource() {
        return null;
    }

    protected abstract int getLanguageVersion();

    public int getLength() {
        int paramCount = getParamCount();
        if (getLanguageVersion() != Token.FOR) {
            return paramCount;
        }
        NativeCall findFunctionActivation = ScriptRuntime.findFunctionActivation(Context.getContext(), this);
        return findFunctionActivation != null ? findFunctionActivation.originalArgs.length : paramCount;
    }

    protected abstract int getParamAndVarCount();

    protected abstract int getParamCount();

    protected boolean getParamOrVarConst(int i) {
        return false;
    }

    protected abstract String getParamOrVarName(int i);

    public final void initScriptFunction(Context context, Scriptable scriptable) {
        ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
    }

    @Deprecated
    public String jsGet_name() {
        return getFunctionName();
    }

    public Object resumeGenerator(Context context, Scriptable scriptable, int i, Object obj, Object obj2) {
        throw new EvaluatorException("resumeGenerator() not implemented");
    }
}
