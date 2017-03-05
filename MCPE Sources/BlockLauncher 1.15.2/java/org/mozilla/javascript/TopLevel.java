package org.mozilla.javascript;

import java.util.EnumMap;

public class TopLevel extends IdScriptableObject {
    static final /* synthetic */ boolean $assertionsDisabled = (!TopLevel.class.desiredAssertionStatus() ? true : $assertionsDisabled);
    static final long serialVersionUID = -4648046356662472260L;
    private EnumMap<Builtins, BaseFunction> ctors;
    private EnumMap<NativeErrors, BaseFunction> errors;

    public enum Builtins {
        Object,
        Array,
        Function,
        String,
        Number,
        Boolean,
        RegExp,
        Error
    }

    enum NativeErrors {
        Error,
        EvalError,
        RangeError,
        ReferenceError,
        SyntaxError,
        TypeError,
        URIError,
        InternalError,
        JavaException
    }

    public static Function getBuiltinCtor(Context context, Scriptable scriptable, Builtins builtins) {
        if ($assertionsDisabled || scriptable.getParentScope() == null) {
            if (scriptable instanceof TopLevel) {
                Function builtinCtor = ((TopLevel) scriptable).getBuiltinCtor(builtins);
                if (builtinCtor != null) {
                    return builtinCtor;
                }
            }
            return ScriptRuntime.getExistingCtor(context, scriptable, builtins.name());
        }
        throw new AssertionError();
    }

    public static Scriptable getBuiltinPrototype(Scriptable scriptable, Builtins builtins) {
        if ($assertionsDisabled || scriptable.getParentScope() == null) {
            if (scriptable instanceof TopLevel) {
                Scriptable builtinPrototype = ((TopLevel) scriptable).getBuiltinPrototype(builtins);
                if (builtinPrototype != null) {
                    return builtinPrototype;
                }
            }
            return ScriptableObject.getClassPrototype(scriptable, builtins.name());
        }
        throw new AssertionError();
    }

    static Function getNativeErrorCtor(Context context, Scriptable scriptable, NativeErrors nativeErrors) {
        if ($assertionsDisabled || scriptable.getParentScope() == null) {
            if (scriptable instanceof TopLevel) {
                Function nativeErrorCtor = ((TopLevel) scriptable).getNativeErrorCtor(nativeErrors);
                if (nativeErrorCtor != null) {
                    return nativeErrorCtor;
                }
            }
            return ScriptRuntime.getExistingCtor(context, scriptable, nativeErrors.name());
        }
        throw new AssertionError();
    }

    public void cacheBuiltins() {
        int i = 0;
        this.ctors = new EnumMap(Builtins.class);
        for (Enum enumR : Builtins.values()) {
            Object property = ScriptableObject.getProperty((Scriptable) this, enumR.name());
            if (property instanceof BaseFunction) {
                this.ctors.put(enumR, (BaseFunction) property);
            }
        }
        this.errors = new EnumMap(NativeErrors.class);
        NativeErrors[] values = NativeErrors.values();
        int length = values.length;
        while (i < length) {
            Enum enumR2 = values[i];
            property = ScriptableObject.getProperty((Scriptable) this, enumR2.name());
            if (property instanceof BaseFunction) {
                this.errors.put(enumR2, (BaseFunction) property);
            }
            i++;
        }
    }

    public BaseFunction getBuiltinCtor(Builtins builtins) {
        return this.ctors != null ? (BaseFunction) this.ctors.get(builtins) : null;
    }

    public Scriptable getBuiltinPrototype(Builtins builtins) {
        BaseFunction builtinCtor = getBuiltinCtor(builtins);
        Object prototypeProperty = builtinCtor != null ? builtinCtor.getPrototypeProperty() : null;
        return prototypeProperty instanceof Scriptable ? (Scriptable) prototypeProperty : null;
    }

    public String getClassName() {
        return "global";
    }

    BaseFunction getNativeErrorCtor(NativeErrors nativeErrors) {
        return this.errors != null ? (BaseFunction) this.errors.get(nativeErrors) : null;
    }
}
