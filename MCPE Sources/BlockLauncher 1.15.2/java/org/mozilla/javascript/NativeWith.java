package org.mozilla.javascript;

import java.io.Serializable;

public class NativeWith implements Serializable, IdFunctionCall, Scriptable {
    private static final Object FTAG = "With";
    private static final int Id_constructor = 1;
    private static final long serialVersionUID = 1;
    protected Scriptable parent;
    protected Scriptable prototype;

    private NativeWith() {
    }

    protected NativeWith(Scriptable scriptable, Scriptable scriptable2) {
        this.parent = scriptable;
        this.prototype = scriptable2;
    }

    static void init(Scriptable scriptable, boolean z) {
        Object nativeWith = new NativeWith();
        nativeWith.setParentScope(scriptable);
        nativeWith.setPrototype(ScriptableObject.getObjectPrototype(scriptable));
        IdFunctionObject idFunctionObject = new IdFunctionObject(nativeWith, FTAG, Id_constructor, "With", 0, scriptable);
        idFunctionObject.markAsConstructor(nativeWith);
        if (z) {
            idFunctionObject.sealObject();
        }
        idFunctionObject.exportAsScopeProperty();
    }

    static boolean isWithFunction(Object obj) {
        if (!(obj instanceof IdFunctionObject)) {
            return false;
        }
        IdFunctionObject idFunctionObject = (IdFunctionObject) obj;
        return idFunctionObject.hasTag(FTAG) && idFunctionObject.methodId() == Id_constructor;
    }

    static Object newWithSpecial(Context context, Scriptable scriptable, Object[] objArr) {
        ScriptRuntime.checkDeprecated(context, "With");
        Scriptable topLevelScope = ScriptableObject.getTopLevelScope(scriptable);
        NativeWith nativeWith = new NativeWith();
        nativeWith.setPrototype(objArr.length == 0 ? ScriptableObject.getObjectPrototype(topLevelScope) : ScriptRuntime.toObject(context, topLevelScope, objArr[0]));
        nativeWith.setParentScope(topLevelScope);
        return nativeWith;
    }

    public void delete(int i) {
        this.prototype.delete(i);
    }

    public void delete(String str) {
        this.prototype.delete(str);
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (idFunctionObject.hasTag(FTAG) && idFunctionObject.methodId() == Id_constructor) {
            throw Context.reportRuntimeError1("msg.cant.call.indirect", "With");
        }
        throw idFunctionObject.unknown();
    }

    public Object get(int i, Scriptable scriptable) {
        if (scriptable == this) {
            scriptable = this.prototype;
        }
        return this.prototype.get(i, scriptable);
    }

    public Object get(String str, Scriptable scriptable) {
        if (scriptable == this) {
            scriptable = this.prototype;
        }
        return this.prototype.get(str, scriptable);
    }

    public String getClassName() {
        return "With";
    }

    public Object getDefaultValue(Class<?> cls) {
        return this.prototype.getDefaultValue(cls);
    }

    public Object[] getIds() {
        return this.prototype.getIds();
    }

    public Scriptable getParentScope() {
        return this.parent;
    }

    public Scriptable getPrototype() {
        return this.prototype;
    }

    public boolean has(int i, Scriptable scriptable) {
        return this.prototype.has(i, this.prototype);
    }

    public boolean has(String str, Scriptable scriptable) {
        return this.prototype.has(str, this.prototype);
    }

    public boolean hasInstance(Scriptable scriptable) {
        return this.prototype.hasInstance(scriptable);
    }

    public void put(int i, Scriptable scriptable, Object obj) {
        if (scriptable == this) {
            scriptable = this.prototype;
        }
        this.prototype.put(i, scriptable, obj);
    }

    public void put(String str, Scriptable scriptable, Object obj) {
        if (scriptable == this) {
            scriptable = this.prototype;
        }
        this.prototype.put(str, scriptable, obj);
    }

    public void setParentScope(Scriptable scriptable) {
        this.parent = scriptable;
    }

    public void setPrototype(Scriptable scriptable) {
        this.prototype = scriptable;
    }

    protected Object updateDotQuery(boolean z) {
        throw new IllegalStateException();
    }
}
