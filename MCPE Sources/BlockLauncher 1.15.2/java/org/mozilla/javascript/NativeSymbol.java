package org.mozilla.javascript;

public class NativeSymbol extends IdScriptableObject {
    public static final String CLASS_NAME = "Symbol";
    public static final String ITERATOR_PROPERTY = "@@iterator";
    private static final int Id_constructor = 1;
    private static final int MAX_PROTOTYPE_ID = 1;
    public static final String SPECIES_PROPERTY = "@@species";
    public static final String TO_STRING_TAG_PROPERTY = "@@toStringTag";

    private NativeSymbol() {
    }

    public static void init(Context context, Scriptable scriptable, boolean z) {
        new NativeSymbol().exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(CLASS_NAME)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        switch (idFunctionObject.methodId()) {
            case MAX_PROTOTYPE_ID /*1*/:
                return new NativeSymbol();
            default:
                return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        super.fillConstructorProperties(idFunctionObject);
        idFunctionObject.defineProperty("iterator", (Object) ITERATOR_PROPERTY, 7);
        idFunctionObject.defineProperty("species", (Object) SPECIES_PROPERTY, 7);
        idFunctionObject.defineProperty("toStringTag", (Object) TO_STRING_TAG_PROPERTY, 7);
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
        return CLASS_NAME;
    }

    public String getTypeOf() {
        return "symbol";
    }

    protected void initPrototypeId(int i) {
        String str = null;
        int i2 = -1;
        switch (i) {
            case MAX_PROTOTYPE_ID /*1*/:
                i2 = 0;
                str = "constructor";
                break;
            default:
                super.initPrototypeId(i);
                break;
        }
        initPrototypeMethod(CLASS_NAME, i, str, i2);
    }
}
