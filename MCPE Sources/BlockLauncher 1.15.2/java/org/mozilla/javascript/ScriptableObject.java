package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.TopLevel.Builtins;
import org.mozilla.javascript.annotations.JSConstructor;
import org.mozilla.javascript.annotations.JSFunction;
import org.mozilla.javascript.annotations.JSGetter;
import org.mozilla.javascript.annotations.JSSetter;
import org.mozilla.javascript.annotations.JSStaticFunction;
import org.mozilla.javascript.debug.DebuggableObject;

public abstract class ScriptableObject implements Serializable, ConstProperties, Scriptable, DebuggableObject {
    static final /* synthetic */ boolean $assertionsDisabled;
    public static final int CONST = 13;
    public static final int DONTENUM = 2;
    public static final int EMPTY = 0;
    private static final Method GET_ARRAY_LENGTH;
    private static final int INITIAL_SLOT_SIZE = 4;
    private static final Comparator<Object> KEY_COMPARATOR = new KeyComparator();
    public static final int PERMANENT = 4;
    public static final int READONLY = 1;
    private static final int SLOT_CONVERT_ACCESSOR_TO_DATA = 5;
    private static final int SLOT_MODIFY = 2;
    private static final int SLOT_MODIFY_CONST = 3;
    private static final int SLOT_MODIFY_GETTER_SETTER = 4;
    private static final int SLOT_QUERY = 1;
    public static final int UNINITIALIZED_CONST = 8;
    static final long serialVersionUID = 2829861078851942586L;
    private volatile Map<Object, Object> associatedValues;
    private int count;
    private transient ExternalArrayData externalData;
    private transient Slot firstAdded;
    private boolean isExtensible = true;
    private transient Slot lastAdded;
    private Scriptable parentScopeObject;
    private Scriptable prototypeObject;
    private transient Slot[] slots;

    private static class Slot implements Serializable {
        private static final long serialVersionUID = -6090581677123995491L;
        private volatile short attributes;
        int indexOrHash;
        String name;
        transient Slot next;
        volatile transient Slot orderedNext;
        volatile Object value;
        volatile transient boolean wasDeleted;

        Slot(String str, int i, int i2) {
            this.name = str;
            this.indexOrHash = i;
            this.attributes = (short) i2;
        }

        private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
            objectInputStream.defaultReadObject();
            if (this.name != null) {
                this.indexOrHash = this.name.hashCode();
            }
        }

        int getAttributes() {
            return this.attributes;
        }

        ScriptableObject getPropertyDescriptor(Context context, Scriptable scriptable) {
            return ScriptableObject.buildDataDescriptor(scriptable, this.value, this.attributes);
        }

        Object getValue(Scriptable scriptable) {
            return this.value;
        }

        void markDeleted() {
            this.wasDeleted = true;
            this.value = null;
            this.name = null;
        }

        synchronized void setAttributes(int i) {
            ScriptableObject.checkValidAttributes(i);
            this.attributes = (short) i;
        }

        boolean setValue(Object obj, Scriptable scriptable, Scriptable scriptable2) {
            if ((this.attributes & ScriptableObject.SLOT_QUERY) != 0) {
                if (!Context.getContext().isStrictMode()) {
                    return true;
                }
                throw ScriptRuntime.typeError1("msg.modify.readonly", this.name);
            } else if (scriptable != scriptable2) {
                return ScriptableObject.$assertionsDisabled;
            } else {
                this.value = obj;
                return true;
            }
        }
    }

    private static final class GetterSlot extends Slot {
        static final long serialVersionUID = -4900574849788797588L;
        Object getter;
        Object setter;

        GetterSlot(String str, int i, int i2) {
            super(str, i, i2);
        }

        ScriptableObject getPropertyDescriptor(Context context, Scriptable scriptable) {
            boolean z = true;
            int attributes = getAttributes();
            ScriptableObject nativeObject = new NativeObject();
            ScriptRuntime.setBuiltinProtoAndParent(nativeObject, scriptable, Builtins.Object);
            nativeObject.defineProperty("enumerable", Boolean.valueOf((attributes & ScriptableObject.SLOT_MODIFY) == 0 ? true : ScriptableObject.$assertionsDisabled), (int) ScriptableObject.EMPTY);
            nativeObject.defineProperty("configurable", Boolean.valueOf((attributes & ScriptableObject.SLOT_MODIFY_GETTER_SETTER) == 0 ? true : ScriptableObject.$assertionsDisabled), (int) ScriptableObject.EMPTY);
            if (this.getter == null && this.setter == null) {
                String str = "writable";
                if ((attributes & ScriptableObject.SLOT_QUERY) != 0) {
                    z = ScriptableObject.$assertionsDisabled;
                }
                nativeObject.defineProperty(str, Boolean.valueOf(z), (int) ScriptableObject.EMPTY);
            }
            if (this.getter != null) {
                nativeObject.defineProperty("get", this.getter, (int) ScriptableObject.EMPTY);
            }
            if (this.setter != null) {
                nativeObject.defineProperty("set", this.setter, (int) ScriptableObject.EMPTY);
            }
            return nativeObject;
        }

        Object getValue(Scriptable scriptable) {
            if (this.getter != null) {
                if (this.getter instanceof MemberBox) {
                    Object[] objArr;
                    Object obj;
                    MemberBox memberBox = (MemberBox) this.getter;
                    if (memberBox.delegateTo == null) {
                        objArr = ScriptRuntime.emptyArgs;
                    } else {
                        Object obj2 = memberBox.delegateTo;
                        objArr = new Object[ScriptableObject.SLOT_QUERY];
                        objArr[ScriptableObject.EMPTY] = scriptable;
                        obj = obj2;
                    }
                    return memberBox.invoke(obj, objArr);
                } else if (this.getter instanceof Function) {
                    Function function = (Function) this.getter;
                    return function.call(Context.getContext(), function.getParentScope(), scriptable, ScriptRuntime.emptyArgs);
                }
            }
            Object obj3 = this.value;
            if (!(obj3 instanceof LazilyLoadedCtor)) {
                return obj3;
            }
            obj3 = (LazilyLoadedCtor) obj3;
            try {
                obj3.init();
                return obj3;
            } finally {
                obj3 = obj3.getValue();
                this.value = obj3;
            }
        }

        void markDeleted() {
            super.markDeleted();
            this.getter = null;
            this.setter = null;
        }

        boolean setValue(Object obj, Scriptable scriptable, Scriptable scriptable2) {
            if (this.setter != null) {
                Context context = Context.getContext();
                if (this.setter instanceof MemberBox) {
                    Object[] objArr;
                    Object obj2;
                    MemberBox memberBox = (MemberBox) this.setter;
                    Class[] clsArr = memberBox.argTypes;
                    Object convertArg = FunctionObject.convertArg(context, scriptable2, obj, FunctionObject.getTypeTag(clsArr[clsArr.length - 1]));
                    if (memberBox.delegateTo == null) {
                        objArr = new Object[ScriptableObject.SLOT_QUERY];
                        objArr[ScriptableObject.EMPTY] = convertArg;
                    } else {
                        Object obj3 = memberBox.delegateTo;
                        objArr = new Object[ScriptableObject.SLOT_MODIFY];
                        objArr[ScriptableObject.EMPTY] = scriptable2;
                        objArr[ScriptableObject.SLOT_QUERY] = convertArg;
                        obj2 = obj3;
                    }
                    memberBox.invoke(obj2, objArr);
                } else if (this.setter instanceof Function) {
                    Function function = (Function) this.setter;
                    Scriptable parentScope = function.getParentScope();
                    Object[] objArr2 = new Object[ScriptableObject.SLOT_QUERY];
                    objArr2[ScriptableObject.EMPTY] = obj;
                    function.call(context, parentScope, scriptable2, objArr2);
                }
                return true;
            } else if (this.getter == null) {
                return super.setValue(obj, scriptable, scriptable2);
            } else {
                Context context2 = Context.getContext();
                if (!context2.isStrictMode() && !context2.hasFeature(11)) {
                    return true;
                }
                throw ScriptRuntime.typeError1("msg.set.prop.no.setter", this.name);
            }
        }
    }

    private static final class KeyComparator implements Comparator<Object> {
        private KeyComparator() {
        }

        public int compare(Object obj, Object obj2) {
            if (!(obj instanceof Integer)) {
                return obj2 instanceof Integer ? ScriptableObject.SLOT_QUERY : ScriptableObject.EMPTY;
            } else {
                if (!(obj2 instanceof Integer)) {
                    return -1;
                }
                int intValue = ((Integer) obj).intValue();
                int intValue2 = ((Integer) obj2).intValue();
                return intValue < intValue2 ? -1 : intValue > intValue2 ? ScriptableObject.SLOT_QUERY : ScriptableObject.EMPTY;
            }
        }
    }

    private static class RelinkedSlot extends Slot {
        final Slot slot;

        RelinkedSlot(Slot slot) {
            super(slot.name, slot.indexOrHash, slot.attributes);
            this.slot = ScriptableObject.unwrapSlot(slot);
        }

        private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
            objectOutputStream.writeObject(this.slot);
        }

        int getAttributes() {
            return this.slot.getAttributes();
        }

        ScriptableObject getPropertyDescriptor(Context context, Scriptable scriptable) {
            return this.slot.getPropertyDescriptor(context, scriptable);
        }

        Object getValue(Scriptable scriptable) {
            return this.slot.getValue(scriptable);
        }

        void markDeleted() {
            super.markDeleted();
            this.slot.markDeleted();
        }

        void setAttributes(int i) {
            this.slot.setAttributes(i);
        }

        boolean setValue(Object obj, Scriptable scriptable, Scriptable scriptable2) {
            return this.slot.setValue(obj, scriptable, scriptable2);
        }
    }

    static {
        boolean z = $assertionsDisabled;
        if (!ScriptableObject.class.desiredAssertionStatus()) {
            z = true;
        }
        $assertionsDisabled = z;
        try {
            GET_ARRAY_LENGTH = ScriptableObject.class.getMethod("getExternalArrayLength", new Class[EMPTY]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public ScriptableObject(Scriptable scriptable, Scriptable scriptable2) {
        if (scriptable == null) {
            throw new IllegalArgumentException();
        }
        this.parentScopeObject = scriptable;
        this.prototypeObject = scriptable2;
    }

    private static void addKnownAbsentSlot(Slot[] slotArr, Slot slot, int i) {
        if (slotArr[i] == null) {
            slotArr[i] = slot;
            return;
        }
        Slot slot2 = slotArr[i];
        for (Slot slot3 = slot2.next; slot3 != null; slot3 = slot3.next) {
            slot2 = slot3;
        }
        slot2.next = slot;
    }

    static <T extends Scriptable> BaseFunction buildClassCtor(Scriptable scriptable, Class<T> cls, boolean z, boolean z2) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        int i;
        Constructor constructor;
        AccessibleObject[] methodList = FunctionObject.getMethodList(cls);
        for (i = EMPTY; i < methodList.length; i += SLOT_QUERY) {
            Method method = methodList[i];
            if (method.getName().equals("init")) {
                Class[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == SLOT_MODIFY_CONST && parameterTypes[EMPTY] == ScriptRuntime.ContextClass && parameterTypes[SLOT_QUERY] == ScriptRuntime.ScriptableClass && parameterTypes[SLOT_MODIFY] == Boolean.TYPE && Modifier.isStatic(method.getModifiers())) {
                    Object[] objArr = new Object[SLOT_MODIFY_CONST];
                    objArr[EMPTY] = Context.getContext();
                    objArr[SLOT_QUERY] = scriptable;
                    objArr[SLOT_MODIFY] = z ? Boolean.TRUE : Boolean.FALSE;
                    method.invoke(null, objArr);
                    return null;
                } else if (parameterTypes.length == SLOT_QUERY && parameterTypes[EMPTY] == ScriptRuntime.ScriptableClass && Modifier.isStatic(method.getModifiers())) {
                    Object[] objArr2 = new Object[SLOT_QUERY];
                    objArr2[EMPTY] = scriptable;
                    method.invoke(null, objArr2);
                    return null;
                }
            }
        }
        AccessibleObject[] constructors = cls.getConstructors();
        for (i = EMPTY; i < constructors.length; i += SLOT_QUERY) {
            if (constructors[i].getParameterTypes().length == 0) {
                constructor = constructors[i];
                break;
            }
        }
        constructor = null;
        if (constructor == null) {
            throw Context.reportRuntimeError1("msg.zero.arg.ctor", cls.getName());
        }
        String defineClass;
        constructor.setAccessible(true);
        Scriptable scriptable2 = (Scriptable) constructor.newInstance(ScriptRuntime.emptyArgs);
        String className = scriptable2.getClassName();
        Object property = getProperty(getTopLevelScope(scriptable), className);
        if (property instanceof BaseFunction) {
            Object prototypeProperty = ((BaseFunction) property).getPrototypeProperty();
            if (prototypeProperty != null) {
                if (cls.equals(prototypeProperty.getClass())) {
                    return (BaseFunction) property;
                }
            }
        }
        Scriptable scriptable3 = null;
        if (z2) {
            Class superclass = cls.getSuperclass();
            if (ScriptRuntime.ScriptableClass.isAssignableFrom(superclass) && !Modifier.isAbstract(superclass.getModifiers())) {
                defineClass = defineClass(scriptable, extendsScriptable(superclass), z, z2);
                if (defineClass != null) {
                    scriptable3 = getClassPrototype(scriptable, defineClass);
                }
            }
        }
        if (scriptable3 == null) {
            scriptable3 = getObjectPrototype(scriptable);
        }
        scriptable2.setPrototype(scriptable3);
        Member findAnnotatedMember = findAnnotatedMember(methodList, JSConstructor.class);
        if (findAnnotatedMember == null) {
            findAnnotatedMember = findAnnotatedMember(constructors, JSConstructor.class);
        }
        if (findAnnotatedMember == null) {
            findAnnotatedMember = FunctionObject.findSingleMethod(methodList, "jsConstructor");
        }
        if (findAnnotatedMember == null) {
            if (constructors.length == SLOT_QUERY) {
                findAnnotatedMember = constructors[EMPTY];
            } else if (constructors.length == SLOT_MODIFY) {
                if (constructors[EMPTY].getParameterTypes().length == 0) {
                    findAnnotatedMember = constructors[SLOT_QUERY];
                } else if (constructors[SLOT_QUERY].getParameterTypes().length == 0) {
                    findAnnotatedMember = constructors[EMPTY];
                }
            }
            if (findAnnotatedMember == null) {
                throw Context.reportRuntimeError1("msg.ctor.multiple.parms", cls.getName());
            }
        }
        Object obj = findAnnotatedMember;
        BaseFunction functionObject = new FunctionObject(className, obj, scriptable);
        if (functionObject.isVarArgsMethod()) {
            throw Context.reportRuntimeError1("msg.varargs.ctor", obj.getName());
        }
        functionObject.initAsConstructor(scriptable, scriptable2);
        Method method2 = null;
        HashSet hashSet = new HashSet();
        HashSet hashSet2 = new HashSet();
        int length = methodList.length;
        int i2 = EMPTY;
        while (i2 < length) {
            Method method3 = methodList[i2];
            if (method3 == obj) {
                method3 = method2;
            } else {
                String name = method3.getName();
                if (name.equals("finishInit")) {
                    Class[] parameterTypes2 = method3.getParameterTypes();
                    if (parameterTypes2.length == SLOT_MODIFY_CONST && parameterTypes2[EMPTY] == ScriptRuntime.ScriptableClass && parameterTypes2[SLOT_QUERY] == FunctionObject.class && parameterTypes2[SLOT_MODIFY] == ScriptRuntime.ScriptableClass && Modifier.isStatic(method3.getModifiers())) {
                    }
                }
                if (name.indexOf(36) != -1) {
                    method3 = method2;
                } else {
                    if (name.equals("jsConstructor")) {
                        method3 = method2;
                    } else {
                        Annotation annotation;
                        String str = null;
                        if (method3.isAnnotationPresent(JSFunction.class)) {
                            annotation = method3.getAnnotation(JSFunction.class);
                        } else if (method3.isAnnotationPresent(JSStaticFunction.class)) {
                            annotation = method3.getAnnotation(JSStaticFunction.class);
                        } else if (method3.isAnnotationPresent(JSGetter.class)) {
                            annotation = method3.getAnnotation(JSGetter.class);
                        } else if (method3.isAnnotationPresent(JSSetter.class)) {
                            method3 = method2;
                        } else {
                            annotation = null;
                        }
                        if (annotation == null) {
                            if (name.startsWith("jsFunction_")) {
                                str = "jsFunction_";
                            } else {
                                if (name.startsWith("jsStaticFunction_")) {
                                    str = "jsStaticFunction_";
                                } else {
                                    if (name.startsWith("jsGet_")) {
                                        str = "jsGet_";
                                    } else if (annotation == null) {
                                        method3 = method2;
                                    }
                                }
                            }
                        }
                        Object obj2 = ((annotation instanceof JSStaticFunction) || str == "jsStaticFunction_") ? SLOT_QUERY : EMPTY;
                        HashSet hashSet3 = obj2 != null ? hashSet : hashSet2;
                        defineClass = getPropertyName(name, str, annotation);
                        if (hashSet3.contains(defineClass)) {
                            throw Context.reportRuntimeError2("duplicate.defineClass.name", name, defineClass);
                        }
                        hashSet3.add(defineClass);
                        if ((annotation instanceof JSGetter) || str == "jsGet_") {
                            if (scriptable2 instanceof ScriptableObject) {
                                Method findSetterMethod = findSetterMethod(methodList, defineClass, "jsSet_");
                                ((ScriptableObject) scriptable2).defineProperty(defineClass, null, method3, findSetterMethod, (findSetterMethod != null ? EMPTY : SLOT_QUERY) | 6);
                                method3 = method2;
                            } else {
                                throw Context.reportRuntimeError2("msg.extend.scriptable", scriptable2.getClass().toString(), defineClass);
                            }
                        } else if (obj2 == null || Modifier.isStatic(method3.getModifiers())) {
                            FunctionObject functionObject2 = new FunctionObject(defineClass, method3, scriptable2);
                            if (functionObject2.isVarArgsConstructor()) {
                                throw Context.reportRuntimeError1("msg.varargs.fun", obj.getName());
                            }
                            defineProperty(obj2 != null ? functionObject : scriptable2, defineClass, functionObject2, SLOT_MODIFY);
                            if (z) {
                                functionObject2.sealObject();
                            }
                            method3 = method2;
                        } else {
                            throw Context.reportRuntimeError("jsStaticFunction must be used with static method.");
                        }
                    }
                }
            }
            i2 += SLOT_QUERY;
            method2 = method3;
        }
        if (method2 != null) {
            objArr2 = new Object[SLOT_MODIFY_CONST];
            objArr2[EMPTY] = scriptable;
            objArr2[SLOT_QUERY] = functionObject;
            objArr2[SLOT_MODIFY] = scriptable2;
            method2.invoke(null, objArr2);
        }
        if (z) {
            functionObject.sealObject();
            if (scriptable2 instanceof ScriptableObject) {
                ((ScriptableObject) scriptable2).sealObject();
            }
        }
        return functionObject;
    }

    protected static ScriptableObject buildDataDescriptor(Scriptable scriptable, Object obj, int i) {
        boolean z = true;
        ScriptableObject nativeObject = new NativeObject();
        ScriptRuntime.setBuiltinProtoAndParent(nativeObject, scriptable, Builtins.Object);
        nativeObject.defineProperty(ES6Iterator.VALUE_PROPERTY, obj, (int) EMPTY);
        nativeObject.defineProperty("writable", Boolean.valueOf((i & SLOT_QUERY) == 0 ? true : $assertionsDisabled), (int) EMPTY);
        nativeObject.defineProperty("enumerable", Boolean.valueOf((i & SLOT_MODIFY) == 0 ? true : $assertionsDisabled), (int) EMPTY);
        String str = "configurable";
        if ((i & SLOT_MODIFY_GETTER_SETTER) != 0) {
            z = $assertionsDisabled;
        }
        nativeObject.defineProperty(str, Boolean.valueOf(z), (int) EMPTY);
        return nativeObject;
    }

    public static Object callMethod(Context context, Scriptable scriptable, String str, Object[] objArr) {
        Object property = getProperty(scriptable, str);
        if (property instanceof Function) {
            Function function = (Function) property;
            Scriptable topLevelScope = getTopLevelScope(scriptable);
            return context != null ? function.call(context, topLevelScope, scriptable, objArr) : Context.call(null, function, topLevelScope, scriptable, objArr);
        } else {
            throw ScriptRuntime.notFunctionError(scriptable, str);
        }
    }

    public static Object callMethod(Scriptable scriptable, String str, Object[] objArr) {
        return callMethod(null, scriptable, str, objArr);
    }

    private void checkNotSealed(String str, int i) {
        if (isSealed()) {
            Object num;
            if (str == null) {
                num = Integer.toString(i);
            }
            throw Context.reportRuntimeError1("msg.modify.sealed", num);
        }
    }

    static void checkValidAttributes(int i) {
        if ((i & -16) != 0) {
            throw new IllegalArgumentException(String.valueOf(i));
        }
    }

    private static void copyTable(Slot[] slotArr, Slot[] slotArr2, int i) {
        if (i == 0) {
            throw Kit.codeBug();
        }
        int length = slotArr2.length;
        int length2 = slotArr.length;
        while (true) {
            length2--;
            Slot slot = slotArr[length2];
            while (slot != null) {
                addKnownAbsentSlot(slotArr2, slot.next == null ? slot : new RelinkedSlot(slot), getSlotIndex(length, slot.indexOrHash));
                slot = slot.next;
                i--;
                if (i == 0) {
                    return;
                }
            }
        }
    }

    private synchronized Slot createSlot(String str, int i, int i2) {
        Slot slot;
        Slot[] slotArr;
        int slotIndex;
        Slot[] slotArr2 = this.slots;
        Slot[] slotArr3;
        if (this.count == 0) {
            slotArr3 = new Slot[SLOT_MODIFY_GETTER_SETTER];
            this.slots = slotArr3;
            slotArr = slotArr3;
            slotIndex = getSlotIndex(slotArr3.length, i);
        } else {
            int slotIndex2 = getSlotIndex(slotArr2.length, i);
            slot = slotArr2[slotIndex2];
            Slot slot2 = slot;
            Slot slot3 = slot;
            while (slot2 != null && (slot2.indexOrHash != i || (slot2.name != str && (str == null || !str.equals(slot2.name))))) {
                slot3 = slot2;
                slot2 = slot2.next;
            }
            if (slot2 != null) {
                Slot unwrapSlot = unwrapSlot(slot2);
                slot = (i2 != SLOT_MODIFY_GETTER_SETTER || (unwrapSlot instanceof GetterSlot)) ? (i2 == SLOT_CONVERT_ACCESSOR_TO_DATA && (unwrapSlot instanceof GetterSlot)) ? new Slot(str, i, unwrapSlot.getAttributes()) : i2 == SLOT_MODIFY_CONST ? null : unwrapSlot : new GetterSlot(str, i, unwrapSlot.getAttributes());
                slot.value = unwrapSlot.value;
                slot.next = slot2.next;
                if (this.lastAdded != null) {
                    this.lastAdded.orderedNext = slot;
                }
                if (this.firstAdded == null) {
                    this.firstAdded = slot;
                }
                this.lastAdded = slot;
                if (slot3 == slot2) {
                    slotArr2[slotIndex2] = slot;
                } else {
                    slot3.next = slot;
                }
                slot2.markDeleted();
            } else if ((this.count + SLOT_QUERY) * SLOT_MODIFY_GETTER_SETTER > slotArr2.length * SLOT_MODIFY_CONST) {
                slotArr3 = new Slot[(slotArr2.length * SLOT_MODIFY)];
                copyTable(this.slots, slotArr3, this.count);
                this.slots = slotArr3;
                slotArr = slotArr3;
                slotIndex = getSlotIndex(slotArr3.length, i);
            } else {
                slotIndex = slotIndex2;
                slotArr = slotArr2;
            }
        }
        slot = i2 == SLOT_MODIFY_GETTER_SETTER ? new GetterSlot(str, i, EMPTY) : new Slot(str, i, EMPTY);
        if (i2 == SLOT_MODIFY_CONST) {
            slot.setAttributes(CONST);
        }
        this.count += SLOT_QUERY;
        if (this.lastAdded != null) {
            this.lastAdded.orderedNext = slot;
        }
        if (this.firstAdded == null) {
            this.firstAdded = slot;
        }
        this.lastAdded = slot;
        addKnownAbsentSlot(slotArr, slot, slotIndex);
        return slot;
    }

    public static <T extends Scriptable> String defineClass(Scriptable scriptable, Class<T> cls, boolean z, boolean z2) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        BaseFunction buildClassCtor = buildClassCtor(scriptable, cls, z, z2);
        if (buildClassCtor == null) {
            return null;
        }
        String className = buildClassCtor.getClassPrototype().getClassName();
        defineProperty(scriptable, className, buildClassCtor, SLOT_MODIFY);
        return className;
    }

    public static <T extends Scriptable> void defineClass(Scriptable scriptable, Class<T> cls) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        defineClass(scriptable, cls, $assertionsDisabled, $assertionsDisabled);
    }

    public static <T extends Scriptable> void defineClass(Scriptable scriptable, Class<T> cls, boolean z) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        defineClass(scriptable, cls, z, $assertionsDisabled);
    }

    public static void defineConstProperty(Scriptable scriptable, String str) {
        if (scriptable instanceof ConstProperties) {
            ((ConstProperties) scriptable).defineConst(str, scriptable);
        } else {
            defineProperty(scriptable, str, Undefined.instance, CONST);
        }
    }

    public static void defineProperty(Scriptable scriptable, String str, Object obj, int i) {
        if (scriptable instanceof ScriptableObject) {
            ((ScriptableObject) scriptable).defineProperty(str, obj, i);
        } else {
            scriptable.put(str, scriptable, obj);
        }
    }

    public static boolean deleteProperty(Scriptable scriptable, int i) {
        Scriptable base = getBase(scriptable, i);
        if (base == null) {
            return true;
        }
        base.delete(i);
        return base.has(i, scriptable) ? $assertionsDisabled : true;
    }

    public static boolean deleteProperty(Scriptable scriptable, String str) {
        Scriptable base = getBase(scriptable, str);
        if (base == null) {
            return true;
        }
        base.delete(str);
        return base.has(str, scriptable) ? $assertionsDisabled : true;
    }

    protected static Scriptable ensureScriptable(Object obj) {
        if (obj instanceof Scriptable) {
            return (Scriptable) obj;
        }
        throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(obj));
    }

    protected static ScriptableObject ensureScriptableObject(Object obj) {
        if (obj instanceof ScriptableObject) {
            return (ScriptableObject) obj;
        }
        throw ScriptRuntime.typeError1("msg.arg.not.object", ScriptRuntime.typeof(obj));
    }

    private static <T extends Scriptable> Class<T> extendsScriptable(Class<?> cls) {
        return ScriptRuntime.ScriptableClass.isAssignableFrom(cls) ? cls : null;
    }

    private static Member findAnnotatedMember(AccessibleObject[] accessibleObjectArr, Class<? extends Annotation> cls) {
        int length = accessibleObjectArr.length;
        for (int i = EMPTY; i < length; i += SLOT_QUERY) {
            AccessibleObject accessibleObject = accessibleObjectArr[i];
            if (accessibleObject.isAnnotationPresent(cls)) {
                return (Member) accessibleObject;
            }
        }
        return null;
    }

    private Slot findAttributeSlot(String str, int i, int i2) {
        Slot slot = getSlot(str, i, i2);
        if (slot != null) {
            return slot;
        }
        Object num;
        if (str == null) {
            num = Integer.toString(i);
        }
        throw Context.reportRuntimeError1("msg.prop.not.found", num);
    }

    private static Method findSetterMethod(Method[] methodArr, String str, String str2) {
        String str3 = "set" + Character.toUpperCase(str.charAt(EMPTY)) + str.substring(SLOT_QUERY);
        int length = methodArr.length;
        for (int i = EMPTY; i < length; i += SLOT_QUERY) {
            Method method = methodArr[i];
            JSSetter jSSetter = (JSSetter) method.getAnnotation(JSSetter.class);
            if (jSSetter != null && (str.equals(jSSetter.value()) || (BuildConfig.FLAVOR.equals(jSSetter.value()) && str3.equals(method.getName())))) {
                return method;
            }
        }
        String str4 = str2 + str;
        int length2 = methodArr.length;
        for (int i2 = EMPTY; i2 < length2; i2 += SLOT_QUERY) {
            Method method2 = methodArr[i2];
            if (str4.equals(method2.getName())) {
                return method2;
            }
        }
        return null;
    }

    public static Scriptable getArrayPrototype(Scriptable scriptable) {
        return TopLevel.getBuiltinPrototype(getTopLevelScope(scriptable), Builtins.Array);
    }

    private static Scriptable getBase(Scriptable scriptable, int i) {
        while (!scriptable.has(i, scriptable)) {
            scriptable = scriptable.getPrototype();
            if (scriptable == null) {
                break;
            }
        }
        return scriptable;
    }

    private static Scriptable getBase(Scriptable scriptable, String str) {
        while (!scriptable.has(str, scriptable)) {
            scriptable = scriptable.getPrototype();
            if (scriptable == null) {
                break;
            }
        }
        return scriptable;
    }

    public static Scriptable getClassPrototype(Scriptable scriptable, String str) {
        Object property = getProperty(getTopLevelScope(scriptable), str);
        if (property instanceof BaseFunction) {
            property = ((BaseFunction) property).getPrototypeProperty();
        } else if (!(property instanceof Scriptable)) {
            return null;
        } else {
            Scriptable scriptable2 = (Scriptable) property;
            property = scriptable2.get("prototype", scriptable2);
        }
        return property instanceof Scriptable ? (Scriptable) property : null;
    }

    public static Object getDefaultValue(Scriptable scriptable, Class<?> cls) {
        Context context = null;
        int i = EMPTY;
        while (i < SLOT_MODIFY) {
            int i2;
            String str;
            Object[] objArr;
            if (cls == ScriptRuntime.StringClass) {
                i2 = i == 0 ? SLOT_QUERY : EMPTY;
            } else {
                i2 = i == SLOT_QUERY ? SLOT_QUERY : EMPTY;
            }
            if (i2 != 0) {
                str = "toString";
                objArr = ScriptRuntime.emptyArgs;
            } else {
                String str2 = "valueOf";
                objArr = new Object[SLOT_QUERY];
                if (cls == null) {
                    str = "undefined";
                } else if (cls == ScriptRuntime.StringClass) {
                    str = "string";
                } else if (cls == ScriptRuntime.ScriptableClass) {
                    str = "object";
                } else if (cls == ScriptRuntime.FunctionClass) {
                    str = "function";
                } else if (cls == ScriptRuntime.BooleanClass || cls == Boolean.TYPE) {
                    str = "boolean";
                } else if (cls == ScriptRuntime.NumberClass || cls == ScriptRuntime.ByteClass || cls == Byte.TYPE || cls == ScriptRuntime.ShortClass || cls == Short.TYPE || cls == ScriptRuntime.IntegerClass || cls == Integer.TYPE || cls == ScriptRuntime.FloatClass || cls == Float.TYPE || cls == ScriptRuntime.DoubleClass || cls == Double.TYPE) {
                    str = "number";
                } else {
                    throw Context.reportRuntimeError1("msg.invalid.type", cls.toString());
                }
                objArr[EMPTY] = str;
                str = str2;
            }
            Object property = getProperty(scriptable, str);
            if (property instanceof Function) {
                Function function = (Function) property;
                if (context == null) {
                    context = Context.getContext();
                }
                property = function.call(context, function.getParentScope(), scriptable, objArr);
                if (property == null) {
                    continue;
                } else {
                    if (!(!(property instanceof Scriptable) || cls == ScriptRuntime.ScriptableClass || cls == ScriptRuntime.FunctionClass)) {
                        if (i2 != 0 && (property instanceof Wrapper)) {
                            property = ((Wrapper) property).unwrap();
                            if (property instanceof String) {
                            }
                        }
                    }
                    return property;
                }
            }
            i += SLOT_QUERY;
        }
        throw ScriptRuntime.typeError1("msg.default.value", cls == null ? "undefined" : cls.getName());
    }

    public static Scriptable getFunctionPrototype(Scriptable scriptable) {
        return TopLevel.getBuiltinPrototype(getTopLevelScope(scriptable), Builtins.Function);
    }

    public static Scriptable getObjectPrototype(Scriptable scriptable) {
        return TopLevel.getBuiltinPrototype(getTopLevelScope(scriptable), Builtins.Object);
    }

    public static Object getProperty(Scriptable scriptable, int i) {
        Object obj;
        Scriptable scriptable2 = scriptable;
        do {
            obj = scriptable2.get(i, scriptable);
            if (obj != Scriptable.NOT_FOUND) {
                break;
            }
            scriptable2 = scriptable2.getPrototype();
        } while (scriptable2 != null);
        return obj;
    }

    public static Object getProperty(Scriptable scriptable, String str) {
        Object obj;
        Scriptable scriptable2 = scriptable;
        do {
            obj = scriptable2.get(str, scriptable);
            if (obj != Scriptable.NOT_FOUND) {
                break;
            }
            scriptable2 = scriptable2.getPrototype();
        } while (scriptable2 != null);
        return obj;
    }

    public static Object[] getPropertyIds(Scriptable scriptable) {
        if (scriptable == null) {
            return ScriptRuntime.emptyArgs;
        }
        Object[] ids = scriptable.getIds();
        ObjToIntMap objToIntMap = null;
        while (true) {
            scriptable = scriptable.getPrototype();
            if (scriptable == null) {
                break;
            }
            Object[] ids2 = scriptable.getIds();
            if (ids2.length != 0) {
                Object[] objArr;
                if (objToIntMap != null) {
                    objArr = ids;
                } else if (ids.length == 0) {
                    ids = ids2;
                } else {
                    ObjToIntMap objToIntMap2 = new ObjToIntMap(ids.length + ids2.length);
                    for (int i = EMPTY; i != ids.length; i += SLOT_QUERY) {
                        objToIntMap2.intern(ids[i]);
                    }
                    objToIntMap = objToIntMap2;
                    objArr = null;
                }
                for (int i2 = EMPTY; i2 != ids2.length; i2 += SLOT_QUERY) {
                    objToIntMap.intern(ids2[i2]);
                }
                ids = objArr;
            }
        }
        return objToIntMap != null ? objToIntMap.getKeys() : ids;
    }

    private static String getPropertyName(String str, String str2, Annotation annotation) {
        if (str2 != null) {
            return str.substring(str2.length());
        }
        String str3 = null;
        if (annotation instanceof JSGetter) {
            str3 = ((JSGetter) annotation).value();
            if ((str3 == null || str3.length() == 0) && str.length() > SLOT_MODIFY_CONST && str.startsWith("get")) {
                str3 = str.substring(SLOT_MODIFY_CONST);
                if (Character.isUpperCase(str3.charAt(EMPTY))) {
                    if (str3.length() == SLOT_QUERY) {
                        str3 = str3.toLowerCase();
                    } else if (!Character.isUpperCase(str3.charAt(SLOT_QUERY))) {
                        str3 = Character.toLowerCase(str3.charAt(EMPTY)) + str3.substring(SLOT_QUERY);
                    }
                }
            }
        } else if (annotation instanceof JSFunction) {
            str3 = ((JSFunction) annotation).value();
        } else if (annotation instanceof JSStaticFunction) {
            str3 = ((JSStaticFunction) annotation).value();
        }
        return (str3 == null || str3.length() == 0) ? str : str3;
    }

    private Slot getSlot(String str, int i, int i2) {
        Slot[] slotArr = this.slots;
        if (slotArr == null && i2 == SLOT_QUERY) {
            return null;
        }
        if (str != null) {
            i = str.hashCode();
        }
        if (slotArr != null) {
            Slot slot = slotArr[getSlotIndex(slotArr.length, i)];
            while (slot != null) {
                String str2 = slot.name;
                if (i == slot.indexOrHash && (str2 == str || (str != null && str.equals(str2)))) {
                    break;
                }
                slot = slot.next;
            }
            switch (i2) {
                case SLOT_QUERY /*1*/:
                    return slot;
                case SLOT_MODIFY /*2*/:
                case SLOT_MODIFY_CONST /*3*/:
                    if (slot != null) {
                        return slot;
                    }
                    break;
                case SLOT_MODIFY_GETTER_SETTER /*4*/:
                    slot = unwrapSlot(slot);
                    if (slot instanceof GetterSlot) {
                        return slot;
                    }
                    break;
                case SLOT_CONVERT_ACCESSOR_TO_DATA /*5*/:
                    slot = unwrapSlot(slot);
                    if (!(slot instanceof GetterSlot)) {
                        return slot;
                    }
                    break;
            }
        }
        return createSlot(str, i, i2);
    }

    private static int getSlotIndex(int i, int i2) {
        return (i - 1) & i2;
    }

    public static Scriptable getTopLevelScope(Scriptable scriptable) {
        while (true) {
            Scriptable parentScope = scriptable.getParentScope();
            if (parentScope == null) {
                return scriptable;
            }
            scriptable = parentScope;
        }
    }

    public static Object getTopScopeValue(Scriptable scriptable, Object obj) {
        Scriptable topLevelScope = getTopLevelScope(scriptable);
        do {
            if (topLevelScope instanceof ScriptableObject) {
                Object associatedValue = ((ScriptableObject) topLevelScope).getAssociatedValue(obj);
                if (associatedValue != null) {
                    return associatedValue;
                }
            }
            topLevelScope = topLevelScope.getPrototype();
        } while (topLevelScope != null);
        return null;
    }

    public static <T> T getTypedProperty(Scriptable scriptable, int i, Class<T> cls) {
        Object property = getProperty(scriptable, i);
        if (property == Scriptable.NOT_FOUND) {
            property = null;
        }
        return cls.cast(Context.jsToJava(property, cls));
    }

    public static <T> T getTypedProperty(Scriptable scriptable, String str, Class<T> cls) {
        Object property = getProperty(scriptable, str);
        if (property == Scriptable.NOT_FOUND) {
            property = null;
        }
        return cls.cast(Context.jsToJava(property, cls));
    }

    public static boolean hasProperty(Scriptable scriptable, int i) {
        return getBase(scriptable, i) != null ? true : $assertionsDisabled;
    }

    public static boolean hasProperty(Scriptable scriptable, String str) {
        return getBase(scriptable, str) != null ? true : $assertionsDisabled;
    }

    protected static boolean isFalse(Object obj) {
        return !isTrue(obj) ? true : $assertionsDisabled;
    }

    protected static boolean isTrue(Object obj) {
        return (obj == NOT_FOUND || !ScriptRuntime.toBoolean(obj)) ? $assertionsDisabled : true;
    }

    private boolean putConstImpl(String str, int i, Scriptable scriptable, Object obj, int i2) {
        if (!$assertionsDisabled && i2 == 0) {
            throw new AssertionError();
        } else if (this.isExtensible || !Context.getContext().isStrictMode()) {
            Slot slot;
            if (this != scriptable) {
                slot = getSlot(str, i, (int) SLOT_QUERY);
                if (slot == null) {
                    return $assertionsDisabled;
                }
            } else if (isExtensible()) {
                checkNotSealed(str, i);
                slot = unwrapSlot(getSlot(str, i, (int) SLOT_MODIFY_CONST));
                int attributes = slot.getAttributes();
                if ((attributes & SLOT_QUERY) == 0) {
                    throw Context.reportRuntimeError1("msg.var.redecl", str);
                }
                if ((attributes & UNINITIALIZED_CONST) != 0) {
                    slot.value = obj;
                    if (i2 != UNINITIALIZED_CONST) {
                        slot.setAttributes(attributes & -9);
                    }
                }
                return true;
            } else {
                slot = getSlot(str, i, (int) SLOT_QUERY);
                if (slot == null) {
                    return true;
                }
            }
            return slot.setValue(obj, this, scriptable);
        } else {
            throw ScriptRuntime.typeError0("msg.not.extensible");
        }
    }

    public static void putConstProperty(Scriptable scriptable, String str, Object obj) {
        Scriptable base = getBase(scriptable, str);
        if (base == null) {
            base = scriptable;
        }
        if (base instanceof ConstProperties) {
            ((ConstProperties) base).putConst(str, scriptable, obj);
        }
    }

    private boolean putImpl(String str, int i, Scriptable scriptable, Object obj) {
        if (this.isExtensible || !Context.getContext().isStrictMode()) {
            Slot slot;
            if (this != scriptable) {
                slot = getSlot(str, i, (int) SLOT_QUERY);
                if (slot == null) {
                    return $assertionsDisabled;
                }
            } else if (this.isExtensible) {
                if (this.count < 0) {
                    checkNotSealed(str, i);
                }
                slot = getSlot(str, i, (int) SLOT_MODIFY);
            } else {
                slot = getSlot(str, i, (int) SLOT_QUERY);
                if (slot == null) {
                    return true;
                }
            }
            return slot.setValue(obj, this, scriptable);
        }
        throw ScriptRuntime.typeError0("msg.not.extensible");
    }

    public static void putProperty(Scriptable scriptable, int i, Object obj) {
        Scriptable base = getBase(scriptable, i);
        if (base == null) {
            base = scriptable;
        }
        base.put(i, scriptable, obj);
    }

    public static void putProperty(Scriptable scriptable, String str, Object obj) {
        Scriptable base = getBase(scriptable, str);
        if (base == null) {
            base = scriptable;
        }
        base.put(str, scriptable, obj);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        int readInt = objectInputStream.readInt();
        if (readInt != 0) {
            int i;
            if (((readInt - 1) & readInt) != 0) {
                if (readInt > 1073741824) {
                    throw new RuntimeException("Property table overflow");
                }
                i = SLOT_MODIFY_GETTER_SETTER;
                while (i < readInt) {
                    i <<= SLOT_QUERY;
                }
                readInt = i;
            }
            this.slots = new Slot[readInt];
            i = this.count;
            int i2 = i < 0 ? i ^ -1 : i;
            Slot slot = null;
            for (int i3 = EMPTY; i3 != i2; i3 += SLOT_QUERY) {
                this.lastAdded = (Slot) objectInputStream.readObject();
                if (i3 == 0) {
                    this.firstAdded = this.lastAdded;
                } else {
                    slot.orderedNext = this.lastAdded;
                }
                addKnownAbsentSlot(this.slots, this.lastAdded, getSlotIndex(readInt, this.lastAdded.indexOrHash));
                slot = this.lastAdded;
            }
        }
    }

    public static void redefineProperty(Scriptable scriptable, String str, boolean z) {
        Scriptable base = getBase(scriptable, str);
        if (base != null) {
            if ((base instanceof ConstProperties) && ((ConstProperties) base).isConst(str)) {
                throw ScriptRuntime.typeError1("msg.const.redecl", str);
            } else if (z) {
                throw ScriptRuntime.typeError1("msg.var.redecl", str);
            }
        }
    }

    private synchronized void removeSlot(String str, int i) {
        if (str != null) {
            i = str.hashCode();
        }
        Slot[] slotArr = this.slots;
        if (this.count != 0) {
            int slotIndex = getSlotIndex(slotArr.length, i);
            Slot slot = slotArr[slotIndex];
            Slot slot2 = slot;
            while (slot2 != null && (slot2.indexOrHash != i || (slot2.name != str && (str == null || !str.equals(slot2.name))))) {
                slot = slot2;
                slot2 = slot2.next;
            }
            if (slot2 != null) {
                if ((slot2.getAttributes() & SLOT_MODIFY_GETTER_SETTER) == 0) {
                    this.count--;
                    if (slot == slot2) {
                        slotArr[slotIndex] = slot2.next;
                    } else {
                        slot.next = slot2.next;
                    }
                    Slot unwrapSlot = unwrapSlot(slot2);
                    if (unwrapSlot == this.firstAdded) {
                        slot = null;
                        this.firstAdded = unwrapSlot.orderedNext;
                    } else {
                        slot = this.firstAdded;
                        while (slot.orderedNext != unwrapSlot) {
                            slot = slot.orderedNext;
                        }
                        slot.orderedNext = unwrapSlot.orderedNext;
                    }
                    if (unwrapSlot == this.lastAdded) {
                        this.lastAdded = slot;
                    }
                    slot2.markDeleted();
                } else if (Context.getContext().isStrictMode()) {
                    throw ScriptRuntime.typeError1("msg.delete.property.with.configurable.false", str);
                }
            }
        }
    }

    private void setGetterOrSetter(String str, int i, Callable callable, boolean z, boolean z2) {
        if (str == null || i == 0) {
            GetterSlot getterSlot;
            if (!z2) {
                checkNotSealed(str, i);
            }
            if (isExtensible()) {
                getterSlot = (GetterSlot) getSlot(str, i, (int) SLOT_MODIFY_GETTER_SETTER);
            } else {
                Slot unwrapSlot = unwrapSlot(getSlot(str, i, (int) SLOT_QUERY));
                if (unwrapSlot instanceof GetterSlot) {
                    getterSlot = (GetterSlot) unwrapSlot;
                } else {
                    return;
                }
            }
            if (z2 || (getterSlot.getAttributes() & SLOT_QUERY) == 0) {
                if (z) {
                    getterSlot.setter = callable;
                } else {
                    getterSlot.getter = callable;
                }
                getterSlot.value = Undefined.instance;
                return;
            }
            throw Context.reportRuntimeError1("msg.modify.readonly", str);
        }
        throw new IllegalArgumentException(str);
    }

    private static Slot unwrapSlot(Slot slot) {
        return slot instanceof RelinkedSlot ? ((RelinkedSlot) slot).slot : slot;
    }

    private synchronized void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        int i = this.count;
        if (i < 0) {
            i ^= -1;
        }
        if (i == 0) {
            objectOutputStream.writeInt(EMPTY);
        } else {
            objectOutputStream.writeInt(this.slots.length);
            Slot slot = this.firstAdded;
            while (slot != null && slot.wasDeleted) {
                slot = slot.orderedNext;
            }
            this.firstAdded = slot;
            Slot slot2 = slot;
            while (slot2 != null) {
                objectOutputStream.writeObject(slot2);
                slot = slot2.orderedNext;
                while (slot != null && slot.wasDeleted) {
                    slot = slot.orderedNext;
                }
                slot2.orderedNext = slot;
                slot2 = slot;
            }
        }
    }

    void addLazilyInitializedValue(String str, int i, LazilyLoadedCtor lazilyLoadedCtor, int i2) {
        if (str == null || i == 0) {
            checkNotSealed(str, i);
            GetterSlot getterSlot = (GetterSlot) getSlot(str, i, (int) SLOT_MODIFY_GETTER_SETTER);
            getterSlot.setAttributes(i2);
            getterSlot.getter = null;
            getterSlot.setter = null;
            getterSlot.value = lazilyLoadedCtor;
            return;
        }
        throw new IllegalArgumentException(str);
    }

    protected int applyDescriptorToAttributeBitset(int i, ScriptableObject scriptableObject) {
        Object property = getProperty((Scriptable) scriptableObject, "enumerable");
        int i2 = property != NOT_FOUND ? ScriptRuntime.toBoolean(property) ? i & -3 : i | SLOT_MODIFY : i;
        Object property2 = getProperty((Scriptable) scriptableObject, "writable");
        if (property2 != NOT_FOUND) {
            i2 = ScriptRuntime.toBoolean(property2) ? i2 & -2 : i2 | SLOT_QUERY;
        }
        property2 = getProperty((Scriptable) scriptableObject, "configurable");
        return property2 != NOT_FOUND ? ScriptRuntime.toBoolean(property2) ? i2 & -5 : i2 | SLOT_MODIFY_GETTER_SETTER : i2;
    }

    public final synchronized Object associateValue(Object obj, Object obj2) {
        Map map;
        if (obj2 == null) {
            throw new IllegalArgumentException();
        }
        map = this.associatedValues;
        if (map == null) {
            map = new HashMap();
            this.associatedValues = map;
        }
        return Kit.initHash(map, obj, obj2);
    }

    public boolean avoidObjectDetection() {
        return $assertionsDisabled;
    }

    protected void checkPropertyChange(String str, ScriptableObject scriptableObject, ScriptableObject scriptableObject2) {
        if (scriptableObject == null) {
            if (!isExtensible()) {
                throw ScriptRuntime.typeError0("msg.not.extensible");
            }
        } else if (!isFalse(scriptableObject.get("configurable", (Scriptable) scriptableObject))) {
        } else {
            if (isTrue(getProperty((Scriptable) scriptableObject2, "configurable"))) {
                throw ScriptRuntime.typeError1("msg.change.configurable.false.to.true", str);
            } else if (isTrue(scriptableObject.get("enumerable", (Scriptable) scriptableObject)) != isTrue(getProperty((Scriptable) scriptableObject2, "enumerable"))) {
                throw ScriptRuntime.typeError1("msg.change.enumerable.with.configurable.false", str);
            } else {
                boolean isDataDescriptor = isDataDescriptor(scriptableObject2);
                boolean isAccessorDescriptor = isAccessorDescriptor(scriptableObject2);
                if (!isDataDescriptor && !isAccessorDescriptor) {
                    return;
                }
                if (isDataDescriptor && isDataDescriptor(scriptableObject)) {
                    if (!isFalse(scriptableObject.get("writable", (Scriptable) scriptableObject))) {
                        return;
                    }
                    if (isTrue(getProperty((Scriptable) scriptableObject2, "writable"))) {
                        throw ScriptRuntime.typeError1("msg.change.writable.false.to.true.with.configurable.false", str);
                    } else if (!sameValue(getProperty((Scriptable) scriptableObject2, ES6Iterator.VALUE_PROPERTY), scriptableObject.get(ES6Iterator.VALUE_PROPERTY, (Scriptable) scriptableObject))) {
                        throw ScriptRuntime.typeError1("msg.change.value.with.writable.false", str);
                    }
                } else if (isAccessorDescriptor && isAccessorDescriptor(scriptableObject)) {
                    if (!sameValue(getProperty((Scriptable) scriptableObject2, "set"), scriptableObject.get("set", (Scriptable) scriptableObject))) {
                        throw ScriptRuntime.typeError1("msg.change.setter.with.configurable.false", str);
                    } else if (!sameValue(getProperty((Scriptable) scriptableObject2, "get"), scriptableObject.get("get", (Scriptable) scriptableObject))) {
                        throw ScriptRuntime.typeError1("msg.change.getter.with.configurable.false", str);
                    }
                } else if (isDataDescriptor(scriptableObject)) {
                    throw ScriptRuntime.typeError1("msg.change.property.data.to.accessor.with.configurable.false", str);
                } else {
                    throw ScriptRuntime.typeError1("msg.change.property.accessor.to.data.with.configurable.false", str);
                }
            }
        }
    }

    protected void checkPropertyDefinition(ScriptableObject scriptableObject) {
        Object property = getProperty((Scriptable) scriptableObject, "get");
        if (property == NOT_FOUND || property == Undefined.instance || (property instanceof Callable)) {
            property = getProperty((Scriptable) scriptableObject, "set");
            if (property != NOT_FOUND && property != Undefined.instance && !(property instanceof Callable)) {
                throw ScriptRuntime.notFunctionError(property);
            } else if (isDataDescriptor(scriptableObject) && isAccessorDescriptor(scriptableObject)) {
                throw ScriptRuntime.typeError0("msg.both.data.and.accessor.desc");
            } else {
                return;
            }
        }
        throw ScriptRuntime.notFunctionError(property);
    }

    public void defineConst(String str, Scriptable scriptable) {
        if (!putConstImpl(str, EMPTY, scriptable, Undefined.instance, UNINITIALIZED_CONST)) {
            if (scriptable == this) {
                throw Kit.codeBug();
            } else if (scriptable instanceof ConstProperties) {
                ((ConstProperties) scriptable).defineConst(str, scriptable);
            }
        }
    }

    public void defineFunctionProperties(String[] strArr, Class<?> cls, int i) {
        Method[] methodList = FunctionObject.getMethodList(cls);
        for (int i2 = EMPTY; i2 < strArr.length; i2 += SLOT_QUERY) {
            String str = strArr[i2];
            Member findSingleMethod = FunctionObject.findSingleMethod(methodList, str);
            if (findSingleMethod == null) {
                throw Context.reportRuntimeError2("msg.method.not.found", str, cls.getName());
            }
            defineProperty(str, new FunctionObject(str, findSingleMethod, this), i);
        }
    }

    public void defineOwnProperties(Context context, ScriptableObject scriptableObject) {
        int i;
        int i2 = EMPTY;
        Object[] ids = scriptableObject.getIds();
        ScriptableObject[] scriptableObjectArr = new ScriptableObject[ids.length];
        int length = ids.length;
        for (i = EMPTY; i < length; i += SLOT_QUERY) {
            ScriptableObject ensureScriptableObject = ensureScriptableObject(ScriptRuntime.getObjectElem((Scriptable) scriptableObject, ids[i], context));
            checkPropertyDefinition(ensureScriptableObject);
            scriptableObjectArr[i] = ensureScriptableObject;
        }
        i = ids.length;
        while (i2 < i) {
            defineOwnProperty(context, ids[i2], scriptableObjectArr[i2]);
            i2 += SLOT_QUERY;
        }
    }

    public void defineOwnProperty(Context context, Object obj, ScriptableObject scriptableObject) {
        checkPropertyDefinition(scriptableObject);
        defineOwnProperty(context, obj, scriptableObject, true);
    }

    protected void defineOwnProperty(Context context, Object obj, ScriptableObject scriptableObject, boolean z) {
        Slot slot;
        int applyDescriptorToAttributeBitset;
        Slot slot2 = getSlot(context, obj, (int) SLOT_QUERY);
        int i = slot2 == null ? SLOT_QUERY : EMPTY;
        if (z) {
            checkPropertyChange(ScriptRuntime.toString(obj), slot2 == null ? null : slot2.getPropertyDescriptor(context, this), scriptableObject);
        }
        boolean isAccessorDescriptor = isAccessorDescriptor(scriptableObject);
        if (slot2 == null) {
            slot = getSlot(context, obj, isAccessorDescriptor ? SLOT_MODIFY_GETTER_SETTER : SLOT_MODIFY);
            applyDescriptorToAttributeBitset = applyDescriptorToAttributeBitset(7, scriptableObject);
        } else {
            slot = slot2;
            applyDescriptorToAttributeBitset = applyDescriptorToAttributeBitset(slot2.getAttributes(), scriptableObject);
        }
        slot = unwrapSlot(slot);
        Object property;
        if (isAccessorDescriptor) {
            if (!(slot instanceof GetterSlot)) {
                slot = getSlot(context, obj, (int) SLOT_MODIFY_GETTER_SETTER);
            }
            GetterSlot getterSlot = (GetterSlot) slot;
            property = getProperty((Scriptable) scriptableObject, "get");
            if (property != NOT_FOUND) {
                getterSlot.getter = property;
            }
            property = getProperty((Scriptable) scriptableObject, "set");
            if (property != NOT_FOUND) {
                getterSlot.setter = property;
            }
            getterSlot.value = Undefined.instance;
            getterSlot.setAttributes(applyDescriptorToAttributeBitset);
            return;
        }
        if ((slot instanceof GetterSlot) && isDataDescriptor(scriptableObject)) {
            slot = getSlot(context, obj, (int) SLOT_CONVERT_ACCESSOR_TO_DATA);
        }
        property = getProperty((Scriptable) scriptableObject, ES6Iterator.VALUE_PROPERTY);
        if (property != NOT_FOUND) {
            slot.value = property;
        } else if (i != 0) {
            slot.value = Undefined.instance;
        }
        slot.setAttributes(applyDescriptorToAttributeBitset);
    }

    public void defineProperty(String str, Class<?> cls, int i) {
        int length = str.length();
        if (length == 0) {
            throw new IllegalArgumentException();
        }
        char[] cArr = new char[(length + SLOT_MODIFY_CONST)];
        str.getChars(EMPTY, length, cArr, SLOT_MODIFY_CONST);
        cArr[SLOT_MODIFY_CONST] = Character.toUpperCase(cArr[SLOT_MODIFY_CONST]);
        cArr[EMPTY] = 'g';
        cArr[SLOT_QUERY] = 'e';
        cArr[SLOT_MODIFY] = 't';
        String str2 = new String(cArr);
        cArr[EMPTY] = 's';
        String str3 = new String(cArr);
        Method[] methodList = FunctionObject.getMethodList(cls);
        Method findSingleMethod = FunctionObject.findSingleMethod(methodList, str2);
        Method findSingleMethod2 = FunctionObject.findSingleMethod(methodList, str3);
        int i2 = findSingleMethod2 == null ? i | SLOT_QUERY : i;
        if (findSingleMethod2 == null) {
            findSingleMethod2 = null;
        }
        defineProperty(str, null, findSingleMethod, findSingleMethod2, i2);
    }

    public void defineProperty(String str, Object obj, int i) {
        checkNotSealed(str, EMPTY);
        put(str, (Scriptable) this, obj);
        setAttributes(str, i);
    }

    public void defineProperty(String str, Object obj, Method method, Method method2, int i) {
        MemberBox memberBox;
        int i2;
        Object obj2;
        Object obj3 = null;
        if (method != null) {
            String str2;
            memberBox = new MemberBox(method);
            if (Modifier.isStatic(method.getModifiers())) {
                memberBox.delegateTo = Void.TYPE;
                i2 = SLOT_QUERY;
            } else {
                i2 = obj != null ? SLOT_QUERY : EMPTY;
                memberBox.delegateTo = obj;
            }
            Class[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length == 0) {
                if (i2 != 0) {
                    str2 = "msg.obj.getter.parms";
                }
                str2 = null;
            } else if (parameterTypes.length == SLOT_QUERY) {
                Class cls = parameterTypes[EMPTY];
                if (cls == ScriptRuntime.ScriptableClass || cls == ScriptRuntime.ScriptableObjectClass) {
                    if (i2 == 0) {
                        str2 = "msg.bad.getter.parms";
                    }
                    str2 = null;
                } else {
                    str2 = "msg.bad.getter.parms";
                }
            } else {
                str2 = "msg.bad.getter.parms";
            }
            if (str2 != null) {
                throw Context.reportRuntimeError1(str2, method.toString());
            }
            obj2 = memberBox;
        } else {
            obj2 = null;
        }
        if (method2 != null) {
            if (method2.getReturnType() != Void.TYPE) {
                throw Context.reportRuntimeError1("msg.setter.return", method2.toString());
            }
            String str3;
            memberBox = new MemberBox(method2);
            if (Modifier.isStatic(method2.getModifiers())) {
                memberBox.delegateTo = Void.TYPE;
                i2 = SLOT_QUERY;
            } else {
                i2 = obj != null ? SLOT_QUERY : EMPTY;
                memberBox.delegateTo = obj;
            }
            Class[] parameterTypes2 = method2.getParameterTypes();
            if (parameterTypes2.length == SLOT_QUERY) {
                if (i2 != 0) {
                    str3 = "msg.setter2.expected";
                }
            } else if (parameterTypes2.length == SLOT_MODIFY) {
                Class cls2 = parameterTypes2[EMPTY];
                if (cls2 != ScriptRuntime.ScriptableClass && cls2 != ScriptRuntime.ScriptableObjectClass) {
                    str3 = "msg.setter2.parms";
                } else if (i2 == 0) {
                    str3 = "msg.setter1.parms";
                }
            } else {
                str3 = "msg.setter.parms";
            }
            if (str3 != null) {
                throw Context.reportRuntimeError1(str3, method2.toString());
            }
            obj3 = memberBox;
        }
        GetterSlot getterSlot = (GetterSlot) getSlot(str, (int) EMPTY, (int) SLOT_MODIFY_GETTER_SETTER);
        getterSlot.setAttributes(i);
        getterSlot.getter = obj2;
        getterSlot.setter = obj3;
    }

    public void delete(int i) {
        checkNotSealed(null, i);
        removeSlot(null, i);
    }

    public void delete(String str) {
        checkNotSealed(str, EMPTY);
        removeSlot(str, EMPTY);
    }

    protected Object equivalentValues(Object obj) {
        return this == obj ? Boolean.TRUE : Scriptable.NOT_FOUND;
    }

    public Object get(int i, Scriptable scriptable) {
        if (this.externalData != null) {
            return i < this.externalData.getArrayLength() ? this.externalData.getArrayElement(i) : Scriptable.NOT_FOUND;
        } else {
            Slot slot = getSlot(null, i, (int) SLOT_QUERY);
            return slot == null ? Scriptable.NOT_FOUND : slot.getValue(scriptable);
        }
    }

    public Object get(Object obj) {
        Object obj2 = obj instanceof String ? get((String) obj, (Scriptable) this) : obj instanceof Number ? get(((Number) obj).intValue(), (Scriptable) this) : null;
        return (obj2 == Scriptable.NOT_FOUND || obj2 == Undefined.instance) ? null : obj2 instanceof Wrapper ? ((Wrapper) obj2).unwrap() : obj2;
    }

    public Object get(String str, Scriptable scriptable) {
        Slot slot = getSlot(str, (int) EMPTY, (int) SLOT_QUERY);
        return slot == null ? Scriptable.NOT_FOUND : slot.getValue(scriptable);
    }

    public Object[] getAllIds() {
        return getIds(true);
    }

    public final Object getAssociatedValue(Object obj) {
        Map map = this.associatedValues;
        return map == null ? null : map.get(obj);
    }

    public int getAttributes(int i) {
        return findAttributeSlot(null, i, SLOT_QUERY).getAttributes();
    }

    @Deprecated
    public final int getAttributes(int i, Scriptable scriptable) {
        return getAttributes(i);
    }

    public int getAttributes(String str) {
        return findAttributeSlot(str, EMPTY, SLOT_QUERY).getAttributes();
    }

    @Deprecated
    public final int getAttributes(String str, Scriptable scriptable) {
        return getAttributes(str);
    }

    public abstract String getClassName();

    public Object getDefaultValue(Class<?> cls) {
        return getDefaultValue(this, cls);
    }

    public ExternalArrayData getExternalArrayData() {
        return this.externalData;
    }

    public Object getExternalArrayLength() {
        return Integer.valueOf(this.externalData == null ? EMPTY : this.externalData.getArrayLength());
    }

    public Object getGetterOrSetter(String str, int i, boolean z) {
        if (str == null || i == 0) {
            Slot unwrapSlot = unwrapSlot(getSlot(str, i, (int) SLOT_QUERY));
            if (unwrapSlot == null) {
                return null;
            }
            if (!(unwrapSlot instanceof GetterSlot)) {
                return Undefined.instance;
            }
            GetterSlot getterSlot = (GetterSlot) unwrapSlot;
            Object obj = z ? getterSlot.setter : getterSlot.getter;
            return obj == null ? Undefined.instance : obj;
        } else {
            throw new IllegalArgumentException(str);
        }
    }

    public Object[] getIds() {
        return getIds($assertionsDisabled);
    }

    Object[] getIds(boolean z) {
        Object obj;
        int i;
        Slot[] slotArr = this.slots;
        int arrayLength = this.externalData == null ? EMPTY : this.externalData.getArrayLength();
        if (arrayLength == 0) {
            obj = ScriptRuntime.emptyArgs;
        } else {
            obj = new Object[arrayLength];
            for (i = EMPTY; i < arrayLength; i += SLOT_QUERY) {
                obj[i] = Integer.valueOf(i);
            }
        }
        if (slotArr == null) {
            return obj;
        }
        Slot slot = this.firstAdded;
        while (slot != null && slot.wasDeleted) {
            slot = slot.orderedNext;
        }
        Slot slot2 = slot;
        int i2 = arrayLength;
        while (slot2 != null) {
            Object obj2;
            if (z || (slot2.getAttributes() & SLOT_MODIFY) == 0) {
                if (i2 == arrayLength) {
                    obj2 = new Object[(slotArr.length + arrayLength)];
                    if (obj != null) {
                        System.arraycopy(obj, EMPTY, obj2, EMPTY, arrayLength);
                    }
                } else {
                    obj2 = obj;
                }
                int i3 = i2 + SLOT_QUERY;
                obj2[i2] = slot2.name != null ? slot2.name : Integer.valueOf(slot2.indexOrHash);
                obj = obj2;
                i = i3;
            } else {
                i = i2;
            }
            slot = slot2.orderedNext;
            while (slot != null && slot.wasDeleted) {
                slot = slot.orderedNext;
            }
            slot2 = slot;
            i2 = i;
        }
        if (i2 == obj.length + arrayLength) {
            return obj;
        }
        obj2 = new Object[i2];
        System.arraycopy(obj, EMPTY, obj2, EMPTY, i2);
        Context currentContext = Context.getCurrentContext();
        if (currentContext != null && currentContext.hasFeature(16)) {
            Arrays.sort(obj2, KEY_COMPARATOR);
        }
        return obj2;
    }

    protected ScriptableObject getOwnPropertyDescriptor(Context context, Object obj) {
        Slot slot = getSlot(context, obj, (int) SLOT_QUERY);
        if (slot == null) {
            return null;
        }
        Scriptable scriptable;
        Scriptable parentScope = getParentScope();
        if (parentScope != null) {
            scriptable = parentScope;
        }
        return slot.getPropertyDescriptor(context, scriptable);
    }

    public Scriptable getParentScope() {
        return this.parentScopeObject;
    }

    public Scriptable getPrototype() {
        return this.prototypeObject;
    }

    protected Slot getSlot(Context context, Object obj, int i) {
        String toStringIdOrIndex = ScriptRuntime.toStringIdOrIndex(context, obj);
        return toStringIdOrIndex == null ? getSlot(null, ScriptRuntime.lastIndexResult(context), i) : getSlot(toStringIdOrIndex, (int) EMPTY, i);
    }

    public String getTypeOf() {
        return avoidObjectDetection() ? "undefined" : "object";
    }

    public boolean has(int i, Scriptable scriptable) {
        return this.externalData != null ? i < this.externalData.getArrayLength() ? true : $assertionsDisabled : getSlot(null, i, (int) SLOT_QUERY) == null ? $assertionsDisabled : true;
    }

    public boolean has(String str, Scriptable scriptable) {
        return getSlot(str, (int) EMPTY, (int) SLOT_QUERY) != null ? true : $assertionsDisabled;
    }

    public boolean hasInstance(Scriptable scriptable) {
        return ScriptRuntime.jsDelegatesTo(scriptable, this);
    }

    protected boolean isAccessorDescriptor(ScriptableObject scriptableObject) {
        return (hasProperty((Scriptable) scriptableObject, "get") || hasProperty((Scriptable) scriptableObject, "set")) ? true : $assertionsDisabled;
    }

    public boolean isConst(String str) {
        boolean z = true;
        Slot slot = getSlot(str, (int) EMPTY, (int) SLOT_QUERY);
        if (slot == null) {
            return $assertionsDisabled;
        }
        if ((slot.getAttributes() & SLOT_CONVERT_ACCESSOR_TO_DATA) != SLOT_CONVERT_ACCESSOR_TO_DATA) {
            z = EMPTY;
        }
        return z;
    }

    protected boolean isDataDescriptor(ScriptableObject scriptableObject) {
        return (hasProperty((Scriptable) scriptableObject, ES6Iterator.VALUE_PROPERTY) || hasProperty((Scriptable) scriptableObject, "writable")) ? true : $assertionsDisabled;
    }

    public boolean isEmpty() {
        return (this.count == 0 || this.count == -1) ? true : $assertionsDisabled;
    }

    public boolean isExtensible() {
        return this.isExtensible;
    }

    protected boolean isGenericDescriptor(ScriptableObject scriptableObject) {
        return (isDataDescriptor(scriptableObject) || isAccessorDescriptor(scriptableObject)) ? $assertionsDisabled : true;
    }

    protected boolean isGetterOrSetter(String str, int i, boolean z) {
        Slot unwrapSlot = unwrapSlot(getSlot(str, i, (int) SLOT_QUERY));
        if (unwrapSlot instanceof GetterSlot) {
            if (z && ((GetterSlot) unwrapSlot).setter != null) {
                return true;
            }
            if (!(z || ((GetterSlot) unwrapSlot).getter == null)) {
                return true;
            }
        }
        return $assertionsDisabled;
    }

    public final boolean isSealed() {
        return this.count < 0 ? true : $assertionsDisabled;
    }

    public void preventExtensions() {
        this.isExtensible = $assertionsDisabled;
    }

    public void put(int i, Scriptable scriptable, Object obj) {
        if (this.externalData != null) {
            if (i < this.externalData.getArrayLength()) {
                this.externalData.setArrayElement(i, obj);
                return;
            }
            Context currentContext = Context.getCurrentContext();
            NativeErrors nativeErrors = NativeErrors.RangeError;
            Object[] objArr = new Object[SLOT_QUERY];
            objArr[EMPTY] = "External array index out of bounds ";
            throw new JavaScriptException(ScriptRuntime.newNativeError(currentContext, this, nativeErrors, objArr), null, EMPTY);
        } else if (!putImpl(null, i, scriptable, obj)) {
            if (scriptable == this) {
                throw Kit.codeBug();
            }
            scriptable.put(i, scriptable, obj);
        }
    }

    public void put(String str, Scriptable scriptable, Object obj) {
        if (!putImpl(str, EMPTY, scriptable, obj)) {
            if (scriptable == this) {
                throw Kit.codeBug();
            }
            scriptable.put(str, scriptable, obj);
        }
    }

    public void putConst(String str, Scriptable scriptable, Object obj) {
        if (!putConstImpl(str, EMPTY, scriptable, obj, SLOT_QUERY)) {
            if (scriptable == this) {
                throw Kit.codeBug();
            } else if (scriptable instanceof ConstProperties) {
                ((ConstProperties) scriptable).putConst(str, scriptable, obj);
            } else {
                scriptable.put(str, scriptable, obj);
            }
        }
    }

    protected boolean sameValue(Object obj, Object obj2) {
        if (obj == NOT_FOUND) {
            return true;
        }
        Object obj3 = obj2 == NOT_FOUND ? Undefined.instance : obj2;
        if ((obj3 instanceof Number) && (obj instanceof Number)) {
            double doubleValue = ((Number) obj3).doubleValue();
            double doubleValue2 = ((Number) obj).doubleValue();
            if (Double.isNaN(doubleValue) && Double.isNaN(doubleValue2)) {
                return true;
            }
            if (doubleValue == 0.0d && Double.doubleToLongBits(doubleValue) != Double.doubleToLongBits(doubleValue2)) {
                return $assertionsDisabled;
            }
        }
        return ScriptRuntime.shallowEq(obj3, obj);
    }

    public synchronized void sealObject() {
        if (this.count >= 0) {
            for (Slot slot = this.firstAdded; slot != null; slot = slot.orderedNext) {
                Object obj = slot.value;
                if (obj instanceof LazilyLoadedCtor) {
                    LazilyLoadedCtor lazilyLoadedCtor = (LazilyLoadedCtor) obj;
                    try {
                        lazilyLoadedCtor.init();
                    } finally {
                        slot.value = lazilyLoadedCtor.getValue();
                    }
                }
            }
            this.count ^= -1;
        }
    }

    public void setAttributes(int i, int i2) {
        checkNotSealed(null, i);
        findAttributeSlot(null, i, SLOT_MODIFY).setAttributes(i2);
    }

    @Deprecated
    public void setAttributes(int i, Scriptable scriptable, int i2) {
        setAttributes(i, i2);
    }

    public void setAttributes(String str, int i) {
        checkNotSealed(str, EMPTY);
        findAttributeSlot(str, EMPTY, SLOT_MODIFY).setAttributes(i);
    }

    @Deprecated
    public final void setAttributes(String str, Scriptable scriptable, int i) {
        setAttributes(str, i);
    }

    public void setExternalArrayData(ExternalArrayData externalArrayData) {
        this.externalData = externalArrayData;
        if (externalArrayData == null) {
            delete(Name.LENGTH);
        } else {
            defineProperty(Name.LENGTH, null, GET_ARRAY_LENGTH, null, SLOT_MODIFY_CONST);
        }
    }

    public void setGetterOrSetter(String str, int i, Callable callable, boolean z) {
        setGetterOrSetter(str, i, callable, z, $assertionsDisabled);
    }

    public void setParentScope(Scriptable scriptable) {
        this.parentScopeObject = scriptable;
    }

    public void setPrototype(Scriptable scriptable) {
        this.prototypeObject = scriptable;
    }

    public int size() {
        return this.count < 0 ? this.count ^ -1 : this.count;
    }
}
