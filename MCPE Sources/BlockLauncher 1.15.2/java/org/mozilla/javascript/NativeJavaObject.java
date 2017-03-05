package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import org.mozilla.javascript.TopLevel.Builtins;

public class NativeJavaObject implements Serializable, Scriptable, Wrapper {
    private static final Object COERCED_INTERFACE_KEY = "Coerced Interface";
    static final byte CONVERSION_NONE = (byte) 99;
    static final byte CONVERSION_NONTRIVIAL = (byte) 0;
    static final byte CONVERSION_TRIVIAL = (byte) 1;
    private static final int JSTYPE_BOOLEAN = 2;
    private static final int JSTYPE_JAVA_ARRAY = 7;
    private static final int JSTYPE_JAVA_CLASS = 5;
    private static final int JSTYPE_JAVA_OBJECT = 6;
    private static final int JSTYPE_NULL = 1;
    private static final int JSTYPE_NUMBER = 3;
    private static final int JSTYPE_OBJECT = 8;
    private static final int JSTYPE_STRING = 4;
    private static final int JSTYPE_UNDEFINED = 0;
    private static Method adapter_readAdapterObject = null;
    private static Method adapter_writeAdapterObject = null;
    static final long serialVersionUID = -6948590651130498591L;
    private transient Map<String, FieldAndMethods> fieldAndMethods;
    protected transient boolean isAdapter;
    protected transient Object javaObject;
    protected transient JavaMembers members;
    protected Scriptable parent;
    protected Scriptable prototype;
    protected transient Class<?> staticType;

    static {
        Class[] clsArr = new Class[JSTYPE_BOOLEAN];
        Class classOrNull = Kit.classOrNull("org.mozilla.javascript.JavaAdapter");
        if (classOrNull != null) {
            try {
                clsArr[JSTYPE_UNDEFINED] = ScriptRuntime.ObjectClass;
                clsArr[JSTYPE_NULL] = Kit.classOrNull("java.io.ObjectOutputStream");
                adapter_writeAdapterObject = classOrNull.getMethod("writeAdapterObject", clsArr);
                clsArr[JSTYPE_UNDEFINED] = ScriptRuntime.ScriptableClass;
                clsArr[JSTYPE_NULL] = Kit.classOrNull("java.io.ObjectInputStream");
                adapter_readAdapterObject = classOrNull.getMethod("readAdapterObject", clsArr);
            } catch (NoSuchMethodException e) {
                adapter_writeAdapterObject = null;
                adapter_readAdapterObject = null;
            }
        }
    }

    public NativeJavaObject(Scriptable scriptable, Object obj, Class<?> cls) {
        this(scriptable, obj, cls, false);
    }

    public NativeJavaObject(Scriptable scriptable, Object obj, Class<?> cls, boolean z) {
        this.parent = scriptable;
        this.javaObject = obj;
        this.staticType = cls;
        this.isAdapter = z;
        initMembers();
    }

    public static boolean canConvert(Object obj, Class<?> cls) {
        return getConversionWeight(obj, cls) < 99;
    }

    private static Object coerceToNumber(Class<?> cls, Object obj) {
        double d = 0.0d;
        Class cls2 = obj.getClass();
        if (cls == Character.TYPE || cls == ScriptRuntime.CharacterClass) {
            if (cls2 == ScriptRuntime.CharacterClass) {
                return obj;
            }
            return Character.valueOf((char) ((int) toInteger(obj, ScriptRuntime.CharacterClass, 0.0d, 65535.0d)));
        } else if (cls == ScriptRuntime.ObjectClass || cls == ScriptRuntime.DoubleClass || cls == Double.TYPE) {
            return cls2 != ScriptRuntime.DoubleClass ? new Double(toDouble(obj)) : obj;
        } else {
            if (cls == ScriptRuntime.FloatClass || cls == Float.TYPE) {
                if (cls2 == ScriptRuntime.FloatClass) {
                    return obj;
                }
                double toDouble = toDouble(obj);
                if (Double.isInfinite(toDouble) || Double.isNaN(toDouble) || toDouble == 0.0d) {
                    return new Float((float) toDouble);
                }
                double abs = Math.abs(toDouble);
                if (abs < 1.401298464324817E-45d) {
                    if (toDouble <= 0.0d) {
                        d = -0.0d;
                    }
                    return new Float(d);
                } else if (abs <= 3.4028234663852886E38d) {
                    return new Float((float) toDouble);
                } else {
                    return new Float(toDouble > 0.0d ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY);
                }
            } else if (cls == ScriptRuntime.IntegerClass || cls == Integer.TYPE) {
                if (cls2 == ScriptRuntime.IntegerClass) {
                    return obj;
                }
                return Integer.valueOf((int) toInteger(obj, ScriptRuntime.IntegerClass, -2.147483648E9d, 2.147483647E9d));
            } else if (cls == ScriptRuntime.LongClass || cls == Long.TYPE) {
                if (cls2 == ScriptRuntime.LongClass) {
                    return obj;
                }
                return Long.valueOf(toInteger(obj, ScriptRuntime.LongClass, Double.longBitsToDouble(-4332462841530417152L), Double.longBitsToDouble(4890909195324358655L)));
            } else if (cls == ScriptRuntime.ShortClass || cls == Short.TYPE) {
                if (cls2 == ScriptRuntime.ShortClass) {
                    return obj;
                }
                return Short.valueOf((short) ((int) toInteger(obj, ScriptRuntime.ShortClass, -32768.0d, 32767.0d)));
            } else if (cls != ScriptRuntime.ByteClass && cls != Byte.TYPE) {
                return new Double(toDouble(obj));
            } else {
                if (cls2 == ScriptRuntime.ByteClass) {
                    return obj;
                }
                return Byte.valueOf((byte) ((int) toInteger(obj, ScriptRuntime.ByteClass, -128.0d, 127.0d)));
            }
        }
    }

    @Deprecated
    public static Object coerceType(Class<?> cls, Object obj) {
        return coerceTypeImpl(cls, obj);
    }

    static Object coerceTypeImpl(Class<?> cls, Object obj) {
        int i = JSTYPE_UNDEFINED;
        if (obj != null && obj.getClass() == cls) {
            return obj;
        }
        switch (getJSTypeCode(obj)) {
            case JSTYPE_UNDEFINED /*0*/:
                if (cls == ScriptRuntime.StringClass || cls == ScriptRuntime.ObjectClass) {
                    return "undefined";
                }
                reportConversionError("undefined", cls);
                return obj;
            case JSTYPE_NULL /*1*/:
                if (cls.isPrimitive()) {
                    reportConversionError(obj, cls);
                }
                return null;
            case JSTYPE_BOOLEAN /*2*/:
                if (cls == Boolean.TYPE || cls == ScriptRuntime.BooleanClass || cls == ScriptRuntime.ObjectClass) {
                    return obj;
                }
                if (cls == ScriptRuntime.StringClass) {
                    return obj.toString();
                }
                reportConversionError(obj, cls);
                return obj;
            case JSTYPE_NUMBER /*3*/:
                if (cls == ScriptRuntime.StringClass) {
                    return ScriptRuntime.toString(obj);
                }
                if (cls == ScriptRuntime.ObjectClass) {
                    return coerceToNumber(Double.TYPE, obj);
                }
                if ((cls.isPrimitive() && cls != Boolean.TYPE) || ScriptRuntime.NumberClass.isAssignableFrom(cls)) {
                    return coerceToNumber(cls, obj);
                }
                reportConversionError(obj, cls);
                return obj;
            case JSTYPE_STRING /*4*/:
                if (cls == ScriptRuntime.StringClass || cls.isInstance(obj)) {
                    return obj.toString();
                }
                if (cls == Character.TYPE || cls == ScriptRuntime.CharacterClass) {
                    return ((CharSequence) obj).length() == JSTYPE_NULL ? Character.valueOf(((CharSequence) obj).charAt(JSTYPE_UNDEFINED)) : coerceToNumber(cls, obj);
                } else {
                    if ((cls.isPrimitive() && cls != Boolean.TYPE) || ScriptRuntime.NumberClass.isAssignableFrom(cls)) {
                        return coerceToNumber(cls, obj);
                    }
                    reportConversionError(obj, cls);
                    return obj;
                }
            case JSTYPE_JAVA_CLASS /*5*/:
                if (obj instanceof Wrapper) {
                    obj = ((Wrapper) obj).unwrap();
                }
                if (cls == ScriptRuntime.ClassClass || cls == ScriptRuntime.ObjectClass) {
                    return obj;
                }
                if (cls == ScriptRuntime.StringClass) {
                    return obj.toString();
                }
                reportConversionError(obj, cls);
                return obj;
            case JSTYPE_JAVA_OBJECT /*6*/:
            case JSTYPE_JAVA_ARRAY /*7*/:
                if (obj instanceof Wrapper) {
                    obj = ((Wrapper) obj).unwrap();
                }
                if (cls.isPrimitive()) {
                    if (cls == Boolean.TYPE) {
                        reportConversionError(obj, cls);
                    }
                    return coerceToNumber(cls, obj);
                } else if (cls == ScriptRuntime.StringClass) {
                    return obj.toString();
                } else {
                    if (cls.isInstance(obj)) {
                        return obj;
                    }
                    reportConversionError(obj, cls);
                    return obj;
                }
            case JSTYPE_OBJECT /*8*/:
                if (cls == ScriptRuntime.StringClass) {
                    return ScriptRuntime.toString(obj);
                }
                if (cls.isPrimitive()) {
                    if (cls == Boolean.TYPE) {
                        reportConversionError(obj, cls);
                    }
                    return coerceToNumber(cls, obj);
                } else if (cls.isInstance(obj)) {
                    return obj;
                } else {
                    if (cls == ScriptRuntime.DateClass && (obj instanceof NativeDate)) {
                        return new Date((long) ((NativeDate) obj).getJSTimeValue());
                    }
                    if (cls.isArray() && (obj instanceof NativeArray)) {
                        NativeArray nativeArray = (NativeArray) obj;
                        long length = nativeArray.getLength();
                        Class componentType = cls.getComponentType();
                        Object newInstance = Array.newInstance(componentType, (int) length);
                        while (((long) i) < length) {
                            try {
                                Array.set(newInstance, i, coerceTypeImpl(componentType, nativeArray.get(i, nativeArray)));
                            } catch (EvaluatorException e) {
                                reportConversionError(obj, cls);
                            }
                            i += JSTYPE_NULL;
                        }
                        return newInstance;
                    } else if (obj instanceof Wrapper) {
                        obj = ((Wrapper) obj).unwrap();
                        if (cls.isInstance(obj)) {
                            return obj;
                        }
                        reportConversionError(obj, cls);
                        return obj;
                    } else if (cls.isInterface() && ((obj instanceof NativeObject) || (obj instanceof NativeFunction))) {
                        return createInterfaceAdapter(cls, (ScriptableObject) obj);
                    } else {
                        reportConversionError(obj, cls);
                        return obj;
                    }
                }
            default:
                return obj;
        }
    }

    protected static Object createInterfaceAdapter(Class<?> cls, ScriptableObject scriptableObject) {
        Object makeHashKeyFromPair = Kit.makeHashKeyFromPair(COERCED_INTERFACE_KEY, cls);
        Object associatedValue = scriptableObject.getAssociatedValue(makeHashKeyFromPair);
        return associatedValue != null ? associatedValue : scriptableObject.associateValue(makeHashKeyFromPair, InterfaceAdapter.create(Context.getContext(), cls, scriptableObject));
    }

    static int getConversionWeight(Object obj, Class<?> cls) {
        int jSTypeCode = getJSTypeCode(obj);
        switch (jSTypeCode) {
            case JSTYPE_UNDEFINED /*0*/:
                return (cls == ScriptRuntime.StringClass || cls == ScriptRuntime.ObjectClass) ? JSTYPE_NULL : 99;
            case JSTYPE_NULL /*1*/:
                return !cls.isPrimitive() ? JSTYPE_NULL : 99;
            case JSTYPE_BOOLEAN /*2*/:
                return cls == Boolean.TYPE ? JSTYPE_NULL : cls == ScriptRuntime.BooleanClass ? JSTYPE_BOOLEAN : cls == ScriptRuntime.ObjectClass ? JSTYPE_NUMBER : cls == ScriptRuntime.StringClass ? JSTYPE_STRING : 99;
            case JSTYPE_NUMBER /*3*/:
                return cls.isPrimitive() ? cls == Double.TYPE ? JSTYPE_NULL : cls != Boolean.TYPE ? getSizeRank(cls) + JSTYPE_NULL : 99 : cls == ScriptRuntime.StringClass ? 9 : cls == ScriptRuntime.ObjectClass ? 10 : ScriptRuntime.NumberClass.isAssignableFrom(cls) ? JSTYPE_BOOLEAN : 99;
            case JSTYPE_STRING /*4*/:
                return cls == ScriptRuntime.StringClass ? JSTYPE_NULL : cls.isInstance(obj) ? JSTYPE_BOOLEAN : cls.isPrimitive() ? cls == Character.TYPE ? JSTYPE_NUMBER : cls != Boolean.TYPE ? JSTYPE_STRING : 99 : 99;
            case JSTYPE_JAVA_CLASS /*5*/:
                return cls == ScriptRuntime.ClassClass ? JSTYPE_NULL : cls == ScriptRuntime.ObjectClass ? JSTYPE_NUMBER : cls == ScriptRuntime.StringClass ? JSTYPE_STRING : 99;
            case JSTYPE_JAVA_OBJECT /*6*/:
            case JSTYPE_JAVA_ARRAY /*7*/:
                if (obj instanceof Wrapper) {
                    obj = ((Wrapper) obj).unwrap();
                }
                return cls.isInstance(obj) ? JSTYPE_UNDEFINED : cls == ScriptRuntime.StringClass ? JSTYPE_BOOLEAN : (!cls.isPrimitive() || cls == Boolean.TYPE || jSTypeCode == JSTYPE_JAVA_ARRAY) ? 99 : getSizeRank(cls) + JSTYPE_BOOLEAN;
            case JSTYPE_OBJECT /*8*/:
                return (cls == ScriptRuntime.ObjectClass || !cls.isInstance(obj)) ? cls.isArray() ? obj instanceof NativeArray ? JSTYPE_BOOLEAN : 99 : cls == ScriptRuntime.ObjectClass ? JSTYPE_NUMBER : cls == ScriptRuntime.StringClass ? JSTYPE_STRING : cls == ScriptRuntime.DateClass ? obj instanceof NativeDate ? JSTYPE_NULL : 99 : cls.isInterface() ? obj instanceof NativeFunction ? JSTYPE_NULL : obj instanceof NativeObject ? JSTYPE_BOOLEAN : 12 : (!cls.isPrimitive() || cls == Boolean.TYPE) ? 99 : getSizeRank(cls) + JSTYPE_STRING : JSTYPE_NULL;
            default:
                return 99;
        }
    }

    private static int getJSTypeCode(Object obj) {
        return obj == null ? JSTYPE_NULL : obj == Undefined.instance ? JSTYPE_UNDEFINED : obj instanceof CharSequence ? JSTYPE_STRING : obj instanceof Number ? JSTYPE_NUMBER : obj instanceof Boolean ? JSTYPE_BOOLEAN : obj instanceof Scriptable ? !(obj instanceof NativeJavaClass) ? obj instanceof NativeJavaArray ? JSTYPE_JAVA_ARRAY : obj instanceof Wrapper ? JSTYPE_JAVA_OBJECT : JSTYPE_OBJECT : JSTYPE_JAVA_CLASS : !(obj instanceof Class) ? obj.getClass().isArray() ? JSTYPE_JAVA_ARRAY : JSTYPE_JAVA_OBJECT : JSTYPE_JAVA_CLASS;
    }

    static int getSizeRank(Class<?> cls) {
        return cls == Double.TYPE ? JSTYPE_NULL : cls == Float.TYPE ? JSTYPE_BOOLEAN : cls == Long.TYPE ? JSTYPE_NUMBER : cls == Integer.TYPE ? JSTYPE_STRING : cls == Short.TYPE ? JSTYPE_JAVA_CLASS : cls == Character.TYPE ? JSTYPE_JAVA_OBJECT : cls == Byte.TYPE ? JSTYPE_JAVA_ARRAY : cls == Boolean.TYPE ? 99 : JSTYPE_OBJECT;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.isAdapter = objectInputStream.readBoolean();
        if (!this.isAdapter) {
            this.javaObject = objectInputStream.readObject();
        } else if (adapter_readAdapterObject == null) {
            throw new ClassNotFoundException();
        } else {
            Object[] objArr = new Object[JSTYPE_BOOLEAN];
            objArr[JSTYPE_UNDEFINED] = this;
            objArr[JSTYPE_NULL] = objectInputStream;
            try {
                this.javaObject = adapter_readAdapterObject.invoke(null, objArr);
            } catch (Exception e) {
                throw new IOException();
            }
        }
        String str = (String) objectInputStream.readObject();
        if (str != null) {
            this.staticType = Class.forName(str);
        } else {
            this.staticType = null;
        }
        initMembers();
    }

    static void reportConversionError(Object obj, Class<?> cls) {
        throw Context.reportRuntimeError2("msg.conversion.not.allowed", String.valueOf(obj), JavaMembers.javaSignature(cls));
    }

    private static double toDouble(Object obj) {
        Method method = null;
        if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        }
        if (obj instanceof String) {
            return ScriptRuntime.toNumber((String) obj);
        }
        if (obj instanceof Scriptable) {
            return obj instanceof Wrapper ? toDouble(((Wrapper) obj).unwrap()) : ScriptRuntime.toNumber(obj);
        } else {
            try {
                method = obj.getClass().getMethod("doubleValue", (Class[]) null);
            } catch (NoSuchMethodException e) {
            } catch (SecurityException e2) {
            }
            if (method != null) {
                try {
                    return ((Number) method.invoke(obj, (Object[]) null)).doubleValue();
                } catch (IllegalAccessException e3) {
                    reportConversionError(obj, Double.TYPE);
                } catch (InvocationTargetException e4) {
                    reportConversionError(obj, Double.TYPE);
                }
            }
            return ScriptRuntime.toNumber(obj.toString());
        }
    }

    private static long toInteger(Object obj, Class<?> cls, double d, double d2) {
        double toDouble = toDouble(obj);
        if (Double.isInfinite(toDouble) || Double.isNaN(toDouble)) {
            reportConversionError(ScriptRuntime.toString(obj), cls);
        }
        toDouble = toDouble > 0.0d ? Math.floor(toDouble) : Math.ceil(toDouble);
        if (toDouble < d || toDouble > d2) {
            reportConversionError(ScriptRuntime.toString(obj), cls);
        }
        return (long) toDouble;
    }

    @Deprecated
    public static Object wrap(Scriptable scriptable, Object obj, Class<?> cls) {
        Context context = Context.getContext();
        return context.getWrapFactory().wrap(context, scriptable, obj, cls);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeBoolean(this.isAdapter);
        if (!this.isAdapter) {
            objectOutputStream.writeObject(this.javaObject);
        } else if (adapter_writeAdapterObject == null) {
            throw new IOException();
        } else {
            Object[] objArr = new Object[JSTYPE_BOOLEAN];
            objArr[JSTYPE_UNDEFINED] = this.javaObject;
            objArr[JSTYPE_NULL] = objectOutputStream;
            try {
                adapter_writeAdapterObject.invoke(null, objArr);
            } catch (Exception e) {
                throw new IOException();
            }
        }
        if (this.staticType != null) {
            objectOutputStream.writeObject(this.staticType.getClass().getName());
        } else {
            objectOutputStream.writeObject(null);
        }
    }

    public void delete(int i) {
    }

    public void delete(String str) {
    }

    public Object get(int i, Scriptable scriptable) {
        throw this.members.reportMemberNotFound(Integer.toString(i));
    }

    public Object get(String str, Scriptable scriptable) {
        if (this.fieldAndMethods != null) {
            Object obj = this.fieldAndMethods.get(str);
            if (obj != null) {
                return obj;
            }
        }
        return this.members.get(this, str, this.javaObject, false);
    }

    public String getClassName() {
        return "JavaObject";
    }

    public Object getDefaultValue(Class<?> cls) {
        if (cls == null && (this.javaObject instanceof Boolean)) {
            cls = ScriptRuntime.BooleanClass;
        }
        if (cls == null || cls == ScriptRuntime.StringClass) {
            return this.javaObject.toString();
        }
        String str;
        if (cls == ScriptRuntime.BooleanClass) {
            str = "booleanValue";
        } else if (cls == ScriptRuntime.NumberClass) {
            str = "doubleValue";
        } else {
            throw Context.reportRuntimeError0("msg.default.value");
        }
        Object obj = get(str, (Scriptable) this);
        if (obj instanceof Function) {
            Function function = (Function) obj;
            return function.call(Context.getContext(), function.getParentScope(), this, ScriptRuntime.emptyArgs);
        } else if (cls != ScriptRuntime.NumberClass || !(this.javaObject instanceof Boolean)) {
            return this.javaObject.toString();
        } else {
            return ScriptRuntime.wrapNumber(((Boolean) this.javaObject).booleanValue() ? 1.0d : 0.0d);
        }
    }

    public Object[] getIds() {
        return this.members.getIds(false);
    }

    public Scriptable getParentScope() {
        return this.parent;
    }

    public Scriptable getPrototype() {
        return (this.prototype == null && (this.javaObject instanceof String)) ? TopLevel.getBuiltinPrototype(ScriptableObject.getTopLevelScope(this.parent), Builtins.String) : this.prototype;
    }

    public boolean has(int i, Scriptable scriptable) {
        return false;
    }

    public boolean has(String str, Scriptable scriptable) {
        return this.members.has(str, false);
    }

    public boolean hasInstance(Scriptable scriptable) {
        return false;
    }

    protected void initMembers() {
        this.members = JavaMembers.lookupClass(this.parent, this.javaObject != null ? this.javaObject.getClass() : this.staticType, this.staticType, this.isAdapter);
        this.fieldAndMethods = this.members.getFieldAndMethodsObjects(this, this.javaObject, false);
    }

    public void put(int i, Scriptable scriptable, Object obj) {
        throw this.members.reportMemberNotFound(Integer.toString(i));
    }

    public void put(String str, Scriptable scriptable, Object obj) {
        if (this.prototype == null || this.members.has(str, false)) {
            this.members.put(this, str, this.javaObject, obj, false);
            return;
        }
        this.prototype.put(str, this.prototype, obj);
    }

    public void setParentScope(Scriptable scriptable) {
        this.parent = scriptable;
    }

    public void setPrototype(Scriptable scriptable) {
        this.prototype = scriptable;
    }

    public Object unwrap() {
        return this.javaObject;
    }
}
