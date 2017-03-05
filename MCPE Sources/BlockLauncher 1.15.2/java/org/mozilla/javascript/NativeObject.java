package org.mozilla.javascript;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

public class NativeObject extends IdScriptableObject implements Map {
    private static final int ConstructorId_create = -9;
    private static final int ConstructorId_defineProperties = -8;
    private static final int ConstructorId_defineProperty = -5;
    private static final int ConstructorId_freeze = -13;
    private static final int ConstructorId_getOwnPropertyDescriptor = -4;
    private static final int ConstructorId_getOwnPropertyNames = -3;
    private static final int ConstructorId_getPrototypeOf = -1;
    private static final int ConstructorId_isExtensible = -6;
    private static final int ConstructorId_isFrozen = -11;
    private static final int ConstructorId_isSealed = -10;
    private static final int ConstructorId_keys = -2;
    private static final int ConstructorId_preventExtensions = -7;
    private static final int ConstructorId_seal = -12;
    private static final int Id___defineGetter__ = 9;
    private static final int Id___defineSetter__ = 10;
    private static final int Id___lookupGetter__ = 11;
    private static final int Id___lookupSetter__ = 12;
    private static final int Id_constructor = 1;
    private static final int Id_hasOwnProperty = 5;
    private static final int Id_isPrototypeOf = 7;
    private static final int Id_propertyIsEnumerable = 6;
    private static final int Id_toLocaleString = 3;
    private static final int Id_toSource = 8;
    private static final int Id_toString = 2;
    private static final int Id_valueOf = 4;
    private static final int MAX_PROTOTYPE_ID = 12;
    private static final Object OBJECT_TAG = "Object";
    static final long serialVersionUID = -6345305608474346996L;

    class EntrySet extends AbstractSet<Entry<Object, Object>> {
        EntrySet() {
        }

        public Iterator<Entry<Object, Object>> iterator() {
            return new Iterator<Entry<Object, Object>>() {
                Object[] ids = NativeObject.this.getIds();
                int index = 0;
                Object key = null;

                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                public Entry<Object, Object> next() {
                    Object[] objArr = this.ids;
                    int i = this.index;
                    this.index = i + NativeObject.Id_constructor;
                    final Object obj = objArr[i];
                    this.key = obj;
                    final Object obj2 = NativeObject.this.get(this.key);
                    return new Entry<Object, Object>() {
                        public boolean equals(Object obj) {
                            if (!(obj instanceof Entry)) {
                                return false;
                            }
                            Entry entry = (Entry) obj;
                            if (obj == null) {
                                if (entry.getKey() != null) {
                                    return false;
                                }
                            } else if (!obj.equals(entry.getKey())) {
                                return false;
                            }
                            if (obj2 == null) {
                                if (entry.getValue() != null) {
                                    return false;
                                }
                            } else if (!obj2.equals(entry.getValue())) {
                                return false;
                            }
                            return true;
                        }

                        public Object getKey() {
                            return obj;
                        }

                        public Object getValue() {
                            return obj2;
                        }

                        public int hashCode() {
                            int i = 0;
                            int hashCode = obj == null ? 0 : obj.hashCode();
                            if (obj2 != null) {
                                i = obj2.hashCode();
                            }
                            return hashCode ^ i;
                        }

                        public Object setValue(Object obj) {
                            throw new UnsupportedOperationException();
                        }

                        public String toString() {
                            return obj + "=" + obj2;
                        }
                    };
                }

                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        public int size() {
            return NativeObject.this.size();
        }
    }

    class KeySet extends AbstractSet<Object> {
        KeySet() {
        }

        public boolean contains(Object obj) {
            return NativeObject.this.containsKey(obj);
        }

        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                Object[] ids = NativeObject.this.getIds();
                int index = 0;
                Object key;

                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                public Object next() {
                    try {
                        Object[] objArr = this.ids;
                        int i = this.index;
                        this.index = i + NativeObject.Id_constructor;
                        Object obj = objArr[i];
                        this.key = obj;
                        return obj;
                    } catch (ArrayIndexOutOfBoundsException e) {
                        this.key = null;
                        throw new NoSuchElementException();
                    }
                }

                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        public int size() {
            return NativeObject.this.size();
        }
    }

    class ValueCollection extends AbstractCollection<Object> {
        ValueCollection() {
        }

        public Iterator<Object> iterator() {
            return new Iterator<Object>() {
                Object[] ids = NativeObject.this.getIds();
                int index = 0;
                Object key;

                public boolean hasNext() {
                    return this.index < this.ids.length;
                }

                public Object next() {
                    NativeObject nativeObject = NativeObject.this;
                    Object[] objArr = this.ids;
                    int i = this.index;
                    this.index = i + NativeObject.Id_constructor;
                    Object obj = objArr[i];
                    this.key = obj;
                    return nativeObject.get(obj);
                }

                public void remove() {
                    if (this.key == null) {
                        throw new IllegalStateException();
                    }
                    NativeObject.this.remove(this.key);
                    this.key = null;
                }
            };
        }

        public int size() {
            return NativeObject.this.size();
        }
    }

    private Scriptable getCompatibleObject(Context context, Scriptable scriptable, Object obj) {
        return context.getLanguageVersion() >= Context.VERSION_ES6 ? ScriptableObject.ensureScriptable(ScriptRuntime.toObject(context, scriptable, obj)) : ScriptableObject.ensureScriptable(obj);
    }

    static void init(Scriptable scriptable, boolean z) {
        new NativeObject().exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object obj) {
        return obj instanceof String ? has((String) obj, this) : obj instanceof Number ? has(((Number) obj).intValue(), (Scriptable) this) : false;
    }

    public boolean containsValue(Object obj) {
        for (Object next : values()) {
            if (obj == next || (obj != null && obj.equals(next))) {
                return true;
            }
        }
        return false;
    }

    public Set<Entry<Object, Object>> entrySet() {
        return new EntrySet();
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        boolean z = true;
        int i = 0;
        if (!idFunctionObject.hasTag(OBJECT_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        ScriptableObject ensureScriptableObject;
        Object[] allIds;
        int i2;
        Object obj;
        Scriptable ownPropertyDescriptor;
        Object[] allIds2;
        int length;
        Object obj2;
        Scriptable ensureScriptable;
        ScriptableObject nativeObject;
        Object[] allIds3;
        String defaultObjectToSource;
        String toStringIdOrIndex;
        switch (methodId) {
            case ConstructorId_freeze /*-13*/:
                ensureScriptableObject = ScriptableObject.ensureScriptableObject(objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                allIds = ensureScriptableObject.getAllIds();
                methodId = allIds.length;
                for (i2 = 0; i2 < methodId; i2 += Id_constructor) {
                    obj = allIds[i2];
                    ownPropertyDescriptor = ensureScriptableObject.getOwnPropertyDescriptor(context, obj);
                    if (isDataDescriptor(ownPropertyDescriptor) && Boolean.TRUE.equals(ownPropertyDescriptor.get("writable"))) {
                        ownPropertyDescriptor.put("writable", ownPropertyDescriptor, Boolean.FALSE);
                    }
                    if (Boolean.TRUE.equals(ownPropertyDescriptor.get("configurable"))) {
                        ownPropertyDescriptor.put("configurable", ownPropertyDescriptor, Boolean.FALSE);
                    }
                    ensureScriptableObject.defineOwnProperty(context, obj, ownPropertyDescriptor, false);
                }
                ensureScriptableObject.preventExtensions();
                return ensureScriptableObject;
            case ConstructorId_seal /*-12*/:
                ensureScriptableObject = ScriptableObject.ensureScriptableObject(objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                allIds = ensureScriptableObject.getAllIds();
                methodId = allIds.length;
                for (i2 = 0; i2 < methodId; i2 += Id_constructor) {
                    obj = allIds[i2];
                    ownPropertyDescriptor = ensureScriptableObject.getOwnPropertyDescriptor(context, obj);
                    if (Boolean.TRUE.equals(ownPropertyDescriptor.get("configurable"))) {
                        ownPropertyDescriptor.put("configurable", ownPropertyDescriptor, Boolean.FALSE);
                        ensureScriptableObject.defineOwnProperty(context, obj, ownPropertyDescriptor, false);
                    }
                }
                ensureScriptableObject.preventExtensions();
                return ensureScriptableObject;
            case ConstructorId_isFrozen /*-11*/:
                ensureScriptableObject = ScriptableObject.ensureScriptableObject(objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                if (ensureScriptableObject.isExtensible()) {
                    return Boolean.FALSE;
                }
                allIds2 = ensureScriptableObject.getAllIds();
                length = allIds2.length;
                while (i < length) {
                    ScriptableObject ownPropertyDescriptor2 = ensureScriptableObject.getOwnPropertyDescriptor(context, allIds2[i]);
                    if (Boolean.TRUE.equals(ownPropertyDescriptor2.get("configurable"))) {
                        return Boolean.FALSE;
                    }
                    if (isDataDescriptor(ownPropertyDescriptor2) && Boolean.TRUE.equals(ownPropertyDescriptor2.get("writable"))) {
                        return Boolean.FALSE;
                    }
                    i += Id_constructor;
                }
                return Boolean.TRUE;
            case ConstructorId_isSealed /*-10*/:
                ensureScriptableObject = ScriptableObject.ensureScriptableObject(objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                if (ensureScriptableObject.isExtensible()) {
                    return Boolean.FALSE;
                }
                allIds2 = ensureScriptableObject.getAllIds();
                length = allIds2.length;
                while (i < length) {
                    if (Boolean.TRUE.equals(ensureScriptableObject.getOwnPropertyDescriptor(context, allIds2[i]).get("configurable"))) {
                        return Boolean.FALSE;
                    }
                    i += Id_constructor;
                }
                return Boolean.TRUE;
            case ConstructorId_create /*-9*/:
                obj2 = objArr.length < Id_constructor ? Undefined.instance : objArr[0];
                ensureScriptable = obj2 == null ? null : ScriptableObject.ensureScriptable(obj2);
                nativeObject = new NativeObject();
                nativeObject.setParentScope(getParentScope());
                nativeObject.setPrototype(ensureScriptable);
                if (objArr.length > Id_constructor && objArr[Id_constructor] != Undefined.instance) {
                    nativeObject.defineOwnProperties(context, ScriptableObject.ensureScriptableObject(Context.toObject(objArr[Id_constructor], getParentScope())));
                }
                return nativeObject;
            case ConstructorId_defineProperties /*-8*/:
                nativeObject = ScriptableObject.ensureScriptableObject(objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                nativeObject.defineOwnProperties(context, ScriptableObject.ensureScriptableObject(Context.toObject(objArr.length < Id_toString ? Undefined.instance : objArr[Id_constructor], getParentScope())));
                return nativeObject;
            case ConstructorId_preventExtensions /*-7*/:
                ensureScriptableObject = ScriptableObject.ensureScriptableObject(objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                ensureScriptableObject.preventExtensions();
                return ensureScriptableObject;
            case ConstructorId_isExtensible /*-6*/:
                return Boolean.valueOf(ScriptableObject.ensureScriptableObject(objArr.length < Id_constructor ? Undefined.instance : objArr[0]).isExtensible());
            case ConstructorId_defineProperty /*-5*/:
                nativeObject = ScriptableObject.ensureScriptableObject(objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                nativeObject.defineOwnProperty(context, objArr.length < Id_toString ? Undefined.instance : objArr[Id_constructor], ScriptableObject.ensureScriptableObject(objArr.length < Id_toLocaleString ? Undefined.instance : objArr[Id_toString]));
                return nativeObject;
            case ConstructorId_getOwnPropertyDescriptor /*-4*/:
                ensureScriptableObject = ScriptableObject.ensureScriptableObject(getCompatibleObject(context, scriptable, objArr.length < Id_constructor ? Undefined.instance : objArr[0])).getOwnPropertyDescriptor(context, ScriptRuntime.toString(objArr.length < Id_toString ? Undefined.instance : objArr[Id_constructor]));
                return ensureScriptableObject == null ? Undefined.instance : ensureScriptableObject;
            case ConstructorId_getOwnPropertyNames /*-3*/:
                allIds3 = ScriptableObject.ensureScriptableObject(getCompatibleObject(context, scriptable, objArr.length < Id_constructor ? Undefined.instance : objArr[0])).getAllIds();
                while (i < allIds3.length) {
                    allIds3[i] = ScriptRuntime.toString(allIds3[i]);
                    i += Id_constructor;
                }
                return context.newArray(scriptable, allIds3);
            case ConstructorId_keys /*-2*/:
                allIds3 = getCompatibleObject(context, scriptable, objArr.length < Id_constructor ? Undefined.instance : objArr[0]).getIds();
                while (i < allIds3.length) {
                    allIds3[i] = ScriptRuntime.toString(allIds3[i]);
                    i += Id_constructor;
                }
                return context.newArray(scriptable, allIds3);
            case ConstructorId_getPrototypeOf /*-1*/:
                return getCompatibleObject(context, scriptable, objArr.length < Id_constructor ? Undefined.instance : objArr[0]).getPrototype();
            case Id_constructor /*1*/:
                return scriptable2 != null ? idFunctionObject.construct(context, scriptable, objArr) : (objArr.length == 0 || objArr[0] == null || objArr[0] == Undefined.instance) ? new NativeObject() : ScriptRuntime.toObject(context, scriptable, objArr[0]);
            case Id_toString /*2*/:
                if (!context.hasFeature(Id_valueOf)) {
                    return ScriptRuntime.defaultObjectToString(scriptable2);
                }
                defaultObjectToSource = ScriptRuntime.defaultObjectToSource(context, scriptable, scriptable2, objArr);
                length = defaultObjectToSource.length();
                return (length != 0 && defaultObjectToSource.charAt(0) == '(' && defaultObjectToSource.charAt(length + ConstructorId_getPrototypeOf) == ')') ? defaultObjectToSource.substring(Id_constructor, length + ConstructorId_getPrototypeOf) : defaultObjectToSource;
            case Id_toLocaleString /*3*/:
                obj2 = ScriptableObject.getProperty(scriptable2, "toString");
                if (obj2 instanceof Callable) {
                    return ((Callable) obj2).call(context, scriptable, scriptable2, ScriptRuntime.emptyArgs);
                }
                throw ScriptRuntime.notFunctionError(obj2);
            case Id_valueOf /*4*/:
                return scriptable2;
            case Id_hasOwnProperty /*5*/:
                defaultObjectToSource = ScriptRuntime.toStringIdOrIndex(context, objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                return ScriptRuntime.wrapBoolean(defaultObjectToSource == null ? scriptable2.has(ScriptRuntime.lastIndexResult(context), scriptable2) : scriptable2.has(defaultObjectToSource, scriptable2));
            case Id_propertyIsEnumerable /*6*/:
                boolean has;
                String toStringIdOrIndex2 = ScriptRuntime.toStringIdOrIndex(context, objArr.length < Id_constructor ? Undefined.instance : objArr[0]);
                if (toStringIdOrIndex2 == null) {
                    length = ScriptRuntime.lastIndexResult(context);
                    has = scriptable2.has(length, scriptable2);
                    if (has && (scriptable2 instanceof ScriptableObject)) {
                        has = (((ScriptableObject) scriptable2).getAttributes(length) & Id_toString) == 0;
                    }
                } else {
                    has = scriptable2.has(toStringIdOrIndex2, scriptable2);
                    if (has && (scriptable2 instanceof ScriptableObject)) {
                        if ((((ScriptableObject) scriptable2).getAttributes(toStringIdOrIndex2) & Id_toString) != 0) {
                            z = false;
                        }
                        has = z;
                    }
                }
                return ScriptRuntime.wrapBoolean(has);
            case Id_isPrototypeOf /*7*/:
                boolean z2;
                if (objArr.length != 0 && (objArr[0] instanceof Scriptable)) {
                    ensureScriptable = (Scriptable) objArr[0];
                    do {
                        ensureScriptable = ensureScriptable.getPrototype();
                        if (ensureScriptable == scriptable2) {
                            z2 = true;
                        }
                    } while (ensureScriptable != null);
                }
                return ScriptRuntime.wrapBoolean(z2);
            case Id_toSource /*8*/:
                return ScriptRuntime.defaultObjectToSource(context, scriptable, scriptable2, objArr);
            case Id___defineGetter__ /*9*/:
            case Id___defineSetter__ /*10*/:
                if (objArr.length < Id_toString || !(objArr[Id_constructor] instanceof Callable)) {
                    throw ScriptRuntime.notFunctionError(objArr.length >= Id_toString ? objArr[Id_constructor] : Undefined.instance);
                } else if (scriptable2 instanceof ScriptableObject) {
                    nativeObject = (ScriptableObject) scriptable2;
                    toStringIdOrIndex = ScriptRuntime.toStringIdOrIndex(context, objArr[0]);
                    length = toStringIdOrIndex != null ? 0 : ScriptRuntime.lastIndexResult(context);
                    Callable callable = (Callable) objArr[Id_constructor];
                    if (methodId != Id___defineSetter__) {
                        z = false;
                    }
                    nativeObject.setGetterOrSetter(toStringIdOrIndex, length, callable, z);
                    if (nativeObject instanceof NativeArray) {
                        ((NativeArray) nativeObject).setDenseOnly(false);
                    }
                    return Undefined.instance;
                } else {
                    throw Context.reportRuntimeError2("msg.extend.scriptable", scriptable2.getClass().getName(), String.valueOf(objArr[0]));
                }
            case Id___lookupGetter__ /*11*/:
            case MAX_PROTOTYPE_ID /*12*/:
                if (objArr.length < Id_constructor || !(scriptable2 instanceof ScriptableObject)) {
                    return Undefined.instance;
                }
                nativeObject = (ScriptableObject) scriptable2;
                toStringIdOrIndex = ScriptRuntime.toStringIdOrIndex(context, objArr[0]);
                length = toStringIdOrIndex != null ? 0 : ScriptRuntime.lastIndexResult(context);
                if (methodId != MAX_PROTOTYPE_ID) {
                    z = false;
                }
                while (true) {
                    Object getterOrSetter = nativeObject.getGetterOrSetter(toStringIdOrIndex, length, z);
                    if (getterOrSetter == null) {
                        ensureScriptable = nativeObject.getPrototype();
                        if (ensureScriptable != null && (ensureScriptable instanceof ScriptableObject)) {
                            nativeObject = (ScriptableObject) ensureScriptable;
                        }
                    }
                    return getterOrSetter != null ? getterOrSetter : Undefined.instance;
                }
            default:
                throw new IllegalArgumentException(String.valueOf(methodId));
        }
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_getPrototypeOf, "getPrototypeOf", Id_constructor);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_keys, "keys", Id_constructor);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_getOwnPropertyNames, "getOwnPropertyNames", Id_constructor);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_getOwnPropertyDescriptor, "getOwnPropertyDescriptor", Id_toString);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_defineProperty, "defineProperty", Id_toLocaleString);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_isExtensible, "isExtensible", Id_constructor);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_preventExtensions, "preventExtensions", Id_constructor);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_defineProperties, "defineProperties", Id_toString);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_create, "create", Id_toString);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_isSealed, "isSealed", Id_constructor);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_isFrozen, "isFrozen", Id_constructor);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_seal, "seal", Id_constructor);
        addIdFunctionProperty(idFunctionObject, OBJECT_TAG, ConstructorId_freeze, "freeze", Id_constructor);
        super.fillConstructorProperties(idFunctionObject);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findPrototypeId(java.lang.String r9) {
        /*
        r8 = this;
        r6 = 71;
        r4 = 3;
        r3 = 2;
        r2 = 8;
        r0 = 0;
        r1 = 0;
        r5 = r9.length();
        switch(r5) {
            case 7: goto L_0x001c;
            case 8: goto L_0x0023;
            case 9: goto L_0x000f;
            case 10: goto L_0x000f;
            case 11: goto L_0x003a;
            case 12: goto L_0x000f;
            case 13: goto L_0x0041;
            case 14: goto L_0x0048;
            case 15: goto L_0x000f;
            case 16: goto L_0x0060;
            case 17: goto L_0x000f;
            case 18: goto L_0x000f;
            case 19: goto L_0x000f;
            case 20: goto L_0x00a2;
            default: goto L_0x000f;
        };
    L_0x000f:
        r2 = r1;
        r1 = r0;
    L_0x0011:
        if (r2 == 0) goto L_0x00aa;
    L_0x0013:
        if (r2 == r9) goto L_0x00aa;
    L_0x0015:
        r2 = r2.equals(r9);
        if (r2 != 0) goto L_0x00aa;
    L_0x001b:
        return r0;
    L_0x001c:
        r1 = "valueOf";
        r2 = 4;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0023:
        r4 = r9.charAt(r4);
        r5 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r4 != r5) goto L_0x0031;
    L_0x002b:
        r1 = "toSource";
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0031:
        r2 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r4 != r2) goto L_0x000f;
    L_0x0035:
        r1 = "toString";
        r2 = r1;
        r1 = r3;
        goto L_0x0011;
    L_0x003a:
        r1 = "constructor";
        r2 = 1;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0041:
        r1 = "isPrototypeOf";
        r2 = 7;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0048:
        r2 = r9.charAt(r0);
        r3 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r2 != r3) goto L_0x0057;
    L_0x0050:
        r1 = "hasOwnProperty";
        r2 = 5;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0057:
        r3 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r2 != r3) goto L_0x000f;
    L_0x005b:
        r1 = "toLocaleString";
        r2 = r1;
        r1 = r4;
        goto L_0x0011;
    L_0x0060:
        r3 = r9.charAt(r3);
        r4 = 100;
        if (r3 != r4) goto L_0x0082;
    L_0x0068:
        r2 = r9.charAt(r2);
        if (r2 != r6) goto L_0x0076;
    L_0x006e:
        r1 = "__defineGetter__";
        r2 = 9;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0076:
        r3 = 83;
        if (r2 != r3) goto L_0x000f;
    L_0x007a:
        r1 = "__defineSetter__";
        r2 = 10;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0082:
        r4 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r3 != r4) goto L_0x000f;
    L_0x0086:
        r2 = r9.charAt(r2);
        if (r2 != r6) goto L_0x0095;
    L_0x008c:
        r1 = "__lookupGetter__";
        r2 = 11;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0095:
        r3 = 83;
        if (r2 != r3) goto L_0x000f;
    L_0x0099:
        r1 = "__lookupSetter__";
        r2 = 12;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00a2:
        r1 = "propertyIsEnumerable";
        r2 = 6;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00aa:
        r0 = r1;
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeObject.findPrototypeId(java.lang.String):int");
    }

    public String getClassName() {
        return "Object";
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
            case Id_toLocaleString /*3*/:
                str = "toLocaleString";
                i2 = 0;
                break;
            case Id_valueOf /*4*/:
                str = "valueOf";
                i2 = 0;
                break;
            case Id_hasOwnProperty /*5*/:
                str = "hasOwnProperty";
                break;
            case Id_propertyIsEnumerable /*6*/:
                str = "propertyIsEnumerable";
                break;
            case Id_isPrototypeOf /*7*/:
                str = "isPrototypeOf";
                break;
            case Id_toSource /*8*/:
                str = "toSource";
                i2 = 0;
                break;
            case Id___defineGetter__ /*9*/:
                str = "__defineGetter__";
                i2 = Id_toString;
                break;
            case Id___defineSetter__ /*10*/:
                str = "__defineSetter__";
                i2 = Id_toString;
                break;
            case Id___lookupGetter__ /*11*/:
                str = "__lookupGetter__";
                break;
            case MAX_PROTOTYPE_ID /*12*/:
                str = "__lookupSetter__";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(OBJECT_TAG, i, str, i2);
    }

    public Set<Object> keySet() {
        return new KeySet();
    }

    public Object put(Object obj, Object obj2) {
        throw new UnsupportedOperationException();
    }

    public void putAll(Map map) {
        throw new UnsupportedOperationException();
    }

    public Object remove(Object obj) {
        Object obj2 = get(obj);
        if (obj instanceof String) {
            delete((String) obj);
        } else if (obj instanceof Number) {
            delete(((Number) obj).intValue());
        }
        return obj2;
    }

    public String toString() {
        return ScriptRuntime.defaultObjectToString(this);
    }

    public Collection<Object> values() {
        return new ValueCollection();
    }
}
