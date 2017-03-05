package org.mozilla.javascript;

public abstract class ES6Iterator extends IdScriptableObject {
    public static final String DONE_PROPERTY = "done";
    private static final int Id_iterator = 2;
    private static final int Id_next = 1;
    private static final int Id_toStringTag = 3;
    private static final int MAX_PROTOTYPE_ID = 3;
    public static final String NEXT_METHOD = "next";
    public static final String VALUE_PROPERTY = "value";
    protected boolean exhausted = false;

    ES6Iterator() {
    }

    ES6Iterator(Scriptable scriptable) {
        Scriptable topLevelScope = ScriptableObject.getTopLevelScope(scriptable);
        setParentScope(topLevelScope);
        setPrototype((IdScriptableObject) ScriptableObject.getTopScopeValue(topLevelScope, getTag()));
    }

    static void init(ScriptableObject scriptableObject, boolean z, IdScriptableObject idScriptableObject, String str) {
        if (scriptableObject != null) {
            idScriptableObject.setParentScope(scriptableObject);
            idScriptableObject.setPrototype(ScriptableObject.getObjectPrototype(scriptableObject));
        }
        idScriptableObject.activatePrototypeMap(MAX_PROTOTYPE_ID);
        if (z) {
            idScriptableObject.sealObject();
        }
        if (scriptableObject != null) {
            scriptableObject.associateValue(str, idScriptableObject);
        }
    }

    private Scriptable makeIteratorResult(Context context, Scriptable scriptable, boolean z, Object obj) {
        Scriptable newObject = context.newObject(scriptable);
        ScriptableObject.putProperty(newObject, VALUE_PROPERTY, obj);
        ScriptableObject.putProperty(newObject, DONE_PROPERTY, Boolean.valueOf(z));
        return newObject;
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(getTag())) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        if (scriptable2 instanceof ES6Iterator) {
            ES6Iterator eS6Iterator = (ES6Iterator) scriptable2;
            switch (methodId) {
                case Id_next /*1*/:
                    return eS6Iterator.next(context, scriptable);
                case Id_iterator /*2*/:
                    return eS6Iterator;
                default:
                    throw new IllegalArgumentException(String.valueOf(methodId));
            }
        }
        throw IdScriptableObject.incompatibleCallError(idFunctionObject);
    }

    protected int findPrototypeId(String str) {
        return str.charAt(0) == 'n' ? Id_next : NativeSymbol.ITERATOR_PROPERTY.equals(str) ? Id_iterator : NativeSymbol.TO_STRING_TAG_PROPERTY.equals(str) ? MAX_PROTOTYPE_ID : 0;
    }

    protected abstract String getTag();

    protected void initPrototypeId(int i) {
        switch (i) {
            case Id_next /*1*/:
                initPrototypeMethod(getTag(), i, NEXT_METHOD, 0);
                return;
            case Id_iterator /*2*/:
                initPrototypeMethod(getTag(), i, NativeSymbol.ITERATOR_PROPERTY, "[Symbol.iterator]", 0);
                return;
            case MAX_PROTOTYPE_ID /*3*/:
                initPrototypeValue(MAX_PROTOTYPE_ID, NativeSymbol.TO_STRING_TAG_PROPERTY, getClassName(), MAX_PROTOTYPE_ID);
                return;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
    }

    protected abstract boolean isDone(Context context, Scriptable scriptable);

    protected Object next(Context context, Scriptable scriptable) {
        Object obj = Undefined.instance;
        boolean z = isDone(context, scriptable) || this.exhausted;
        if (z) {
            this.exhausted = true;
        } else {
            obj = nextValue(context, scriptable);
        }
        return makeIteratorResult(context, scriptable, z, obj);
    }

    protected abstract Object nextValue(Context context, Scriptable scriptable);
}
