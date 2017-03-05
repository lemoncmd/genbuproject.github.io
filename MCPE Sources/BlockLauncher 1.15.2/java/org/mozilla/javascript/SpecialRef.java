package org.mozilla.javascript;

class SpecialRef extends Ref {
    private static final int SPECIAL_NONE = 0;
    private static final int SPECIAL_PARENT = 2;
    private static final int SPECIAL_PROTO = 1;
    static final long serialVersionUID = -7521596632456797847L;
    private String name;
    private Scriptable target;
    private int type;

    private SpecialRef(Scriptable scriptable, int i, String str) {
        this.target = scriptable;
        this.type = i;
        this.name = str;
    }

    static Ref createSpecial(Context context, Scriptable scriptable, Object obj, String str) {
        Scriptable toObjectOrNull = ScriptRuntime.toObjectOrNull(context, obj, scriptable);
        if (toObjectOrNull == null) {
            throw ScriptRuntime.undefReadError(obj, str);
        }
        int i;
        if (str.equals("__proto__")) {
            i = SPECIAL_PROTO;
        } else if (str.equals("__parent__")) {
            i = SPECIAL_PARENT;
        } else {
            throw new IllegalArgumentException(str);
        }
        if (!context.hasFeature(5)) {
            i = SPECIAL_NONE;
        }
        return new SpecialRef(toObjectOrNull, i, str);
    }

    public boolean delete(Context context) {
        return this.type == 0 ? ScriptRuntime.deleteObjectElem(this.target, this.name, context) : false;
    }

    public Object get(Context context) {
        switch (this.type) {
            case SPECIAL_NONE /*0*/:
                return ScriptRuntime.getObjectProp(this.target, this.name, context);
            case SPECIAL_PROTO /*1*/:
                return this.target.getPrototype();
            case SPECIAL_PARENT /*2*/:
                return this.target.getParentScope();
            default:
                throw Kit.codeBug();
        }
    }

    public boolean has(Context context) {
        return this.type == 0 ? ScriptRuntime.hasObjectElem(this.target, this.name, context) : true;
    }

    @Deprecated
    public Object set(Context context, Object obj) {
        throw new IllegalStateException();
    }

    public Object set(Context context, Scriptable scriptable, Object obj) {
        switch (this.type) {
            case SPECIAL_NONE /*0*/:
                return ScriptRuntime.setObjectProp(this.target, this.name, obj, context);
            case SPECIAL_PROTO /*1*/:
            case SPECIAL_PARENT /*2*/:
                Scriptable toObjectOrNull = ScriptRuntime.toObjectOrNull(context, obj, scriptable);
                if (toObjectOrNull != null) {
                    Scriptable scriptable2 = toObjectOrNull;
                    while (scriptable2 != this.target) {
                        if (this.type == SPECIAL_PROTO) {
                            scriptable2 = scriptable2.getPrototype();
                            continue;
                        } else {
                            scriptable2 = scriptable2.getParentScope();
                            continue;
                        }
                        if (scriptable2 == null) {
                        }
                    }
                    throw Context.reportRuntimeError1("msg.cyclic.value", this.name);
                }
                if (this.type == SPECIAL_PROTO) {
                    this.target.setPrototype(toObjectOrNull);
                    return toObjectOrNull;
                }
                this.target.setParentScope(toObjectOrNull);
                return toObjectOrNull;
            default:
                throw Kit.codeBug();
        }
    }
}
