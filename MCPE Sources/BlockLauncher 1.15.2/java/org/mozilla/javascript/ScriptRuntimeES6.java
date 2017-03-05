package org.mozilla.javascript;

public class ScriptRuntimeES6 {
    public static Scriptable requireObjectCoercible(Context context, Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable != null && !Undefined.isUndefined(scriptable)) {
            return scriptable;
        }
        throw ScriptRuntime.typeError2("msg.called.null.or.undefined", idFunctionObject.getTag(), idFunctionObject.getFunctionName());
    }
}
