package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.hockeyapp.android.BuildConfig;

public class FunctionObject extends BaseFunction {
    public static final int JAVA_BOOLEAN_TYPE = 3;
    public static final int JAVA_DOUBLE_TYPE = 4;
    public static final int JAVA_INT_TYPE = 2;
    public static final int JAVA_OBJECT_TYPE = 6;
    public static final int JAVA_SCRIPTABLE_TYPE = 5;
    public static final int JAVA_STRING_TYPE = 1;
    public static final int JAVA_UNSUPPORTED_TYPE = 0;
    private static final short VARARGS_CTOR = (short) -2;
    private static final short VARARGS_METHOD = (short) -1;
    private static boolean sawSecurityException = false;
    static final long serialVersionUID = -5332312783643935019L;
    private String functionName;
    private transient boolean hasVoidReturn;
    private boolean isStatic;
    MemberBox member;
    private int parmsLength;
    private transient int returnTypeTag;
    private transient byte[] typeTags;

    public FunctionObject(String str, Member member, Scriptable scriptable) {
        int i = JAVA_UNSUPPORTED_TYPE;
        if (member instanceof Constructor) {
            this.member = new MemberBox((Constructor) member);
            this.isStatic = true;
        } else {
            this.member = new MemberBox((Method) member);
            this.isStatic = this.member.isStatic();
        }
        String name = this.member.getName();
        this.functionName = str;
        Class[] clsArr = this.member.argTypes;
        int length = clsArr.length;
        if (length != JAVA_DOUBLE_TYPE || (!clsArr[JAVA_STRING_TYPE].isArray() && !clsArr[JAVA_INT_TYPE].isArray())) {
            this.parmsLength = length;
            if (length > 0) {
                this.typeTags = new byte[length];
                while (i != length) {
                    int typeTag = getTypeTag(clsArr[i]);
                    if (typeTag == 0) {
                        throw Context.reportRuntimeError2("msg.bad.parms", clsArr[i].getName(), name);
                    }
                    this.typeTags[i] = (byte) typeTag;
                    i += JAVA_STRING_TYPE;
                }
            }
        } else if (clsArr[JAVA_STRING_TYPE].isArray()) {
            if (this.isStatic && clsArr[JAVA_UNSUPPORTED_TYPE] == ScriptRuntime.ContextClass && clsArr[JAVA_STRING_TYPE].getComponentType() == ScriptRuntime.ObjectClass && clsArr[JAVA_INT_TYPE] == ScriptRuntime.FunctionClass && clsArr[JAVA_BOOLEAN_TYPE] == Boolean.TYPE) {
                this.parmsLength = -2;
            } else {
                throw Context.reportRuntimeError1("msg.varargs.ctor", name);
            }
        } else if (this.isStatic && clsArr[JAVA_UNSUPPORTED_TYPE] == ScriptRuntime.ContextClass && clsArr[JAVA_STRING_TYPE] == ScriptRuntime.ScriptableClass && clsArr[JAVA_INT_TYPE].getComponentType() == ScriptRuntime.ObjectClass && clsArr[JAVA_BOOLEAN_TYPE] == ScriptRuntime.FunctionClass) {
            this.parmsLength = -1;
        } else {
            throw Context.reportRuntimeError1("msg.varargs.fun", name);
        }
        Class returnType;
        if (this.member.isMethod()) {
            returnType = this.member.method().getReturnType();
            if (returnType == Void.TYPE) {
                this.hasVoidReturn = true;
            } else {
                this.returnTypeTag = getTypeTag(returnType);
            }
        } else {
            returnType = this.member.getDeclaringClass();
            if (!ScriptRuntime.ScriptableClass.isAssignableFrom(returnType)) {
                throw Context.reportRuntimeError1("msg.bad.ctor.return", returnType.getName());
            }
        }
        ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
    }

    public static Object convertArg(Context context, Scriptable scriptable, Object obj, int i) {
        switch (i) {
            case JAVA_STRING_TYPE /*1*/:
                return obj instanceof String ? obj : ScriptRuntime.toString(obj);
            case JAVA_INT_TYPE /*2*/:
                return !(obj instanceof Integer) ? Integer.valueOf(ScriptRuntime.toInt32(obj)) : obj;
            case JAVA_BOOLEAN_TYPE /*3*/:
                if (obj instanceof Boolean) {
                    return obj;
                }
                return ScriptRuntime.toBoolean(obj) ? Boolean.TRUE : Boolean.FALSE;
            case JAVA_DOUBLE_TYPE /*4*/:
                return !(obj instanceof Double) ? new Double(ScriptRuntime.toNumber(obj)) : obj;
            case JAVA_SCRIPTABLE_TYPE /*5*/:
                return ScriptRuntime.toObjectOrNull(context, obj, scriptable);
            case JAVA_OBJECT_TYPE /*6*/:
                return obj;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Deprecated
    public static Object convertArg(Context context, Scriptable scriptable, Object obj, Class<?> cls) {
        int typeTag = getTypeTag(cls);
        if (typeTag != 0) {
            return convertArg(context, scriptable, obj, typeTag);
        }
        throw Context.reportRuntimeError1("msg.cant.convert", cls.getName());
    }

    static Method findSingleMethod(Method[] methodArr, String str) {
        Method method = null;
        int length = methodArr.length;
        int i = JAVA_UNSUPPORTED_TYPE;
        while (i != length) {
            Method method2 = methodArr[i];
            if (method2 == null || !str.equals(method2.getName())) {
                method2 = method;
            } else if (method != null) {
                throw Context.reportRuntimeError2("msg.no.overload", str, method2.getDeclaringClass().getName());
            }
            i += JAVA_STRING_TYPE;
            method = method2;
        }
        return method;
    }

    static Method[] getMethodList(Class<?> cls) {
        Method[] declaredMethods;
        int i;
        int i2;
        Method[] methodArr;
        int i3;
        int i4 = JAVA_UNSUPPORTED_TYPE;
        try {
            if (!sawSecurityException) {
                declaredMethods = cls.getDeclaredMethods();
                if (declaredMethods == null) {
                    declaredMethods = cls.getMethods();
                }
                i = JAVA_UNSUPPORTED_TYPE;
                i2 = JAVA_UNSUPPORTED_TYPE;
                while (i < declaredMethods.length) {
                    if (sawSecurityException ? !Modifier.isPublic(declaredMethods[i].getModifiers()) : declaredMethods[i].getDeclaringClass() != cls) {
                        declaredMethods[i] = null;
                    } else {
                        i2 += JAVA_STRING_TYPE;
                    }
                    i += JAVA_STRING_TYPE;
                }
                methodArr = new Method[i2];
                i3 = JAVA_UNSUPPORTED_TYPE;
                while (i4 < declaredMethods.length) {
                    if (declaredMethods[i4] != null) {
                        i = i3 + JAVA_STRING_TYPE;
                        methodArr[i3] = declaredMethods[i4];
                        i3 = i;
                    }
                    i4 += JAVA_STRING_TYPE;
                }
                return methodArr;
            }
        } catch (SecurityException e) {
            sawSecurityException = true;
        }
        declaredMethods = null;
        if (declaredMethods == null) {
            declaredMethods = cls.getMethods();
        }
        i = JAVA_UNSUPPORTED_TYPE;
        i2 = JAVA_UNSUPPORTED_TYPE;
        while (i < declaredMethods.length) {
            if (sawSecurityException) {
            }
            declaredMethods[i] = null;
            i += JAVA_STRING_TYPE;
        }
        methodArr = new Method[i2];
        i3 = JAVA_UNSUPPORTED_TYPE;
        while (i4 < declaredMethods.length) {
            if (declaredMethods[i4] != null) {
                i = i3 + JAVA_STRING_TYPE;
                methodArr[i3] = declaredMethods[i4];
                i3 = i;
            }
            i4 += JAVA_STRING_TYPE;
        }
        return methodArr;
    }

    public static int getTypeTag(Class<?> cls) {
        return cls == ScriptRuntime.StringClass ? JAVA_STRING_TYPE : (cls == ScriptRuntime.IntegerClass || cls == Integer.TYPE) ? JAVA_INT_TYPE : (cls == ScriptRuntime.BooleanClass || cls == Boolean.TYPE) ? JAVA_BOOLEAN_TYPE : (cls == ScriptRuntime.DoubleClass || cls == Double.TYPE) ? JAVA_DOUBLE_TYPE : ScriptRuntime.ScriptableClass.isAssignableFrom(cls) ? JAVA_SCRIPTABLE_TYPE : cls == ScriptRuntime.ObjectClass ? JAVA_OBJECT_TYPE : JAVA_UNSUPPORTED_TYPE;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.parmsLength > 0) {
            Class[] clsArr = this.member.argTypes;
            this.typeTags = new byte[this.parmsLength];
            for (int i = JAVA_UNSUPPORTED_TYPE; i != this.parmsLength; i += JAVA_STRING_TYPE) {
                this.typeTags[i] = (byte) getTypeTag(clsArr[i]);
            }
        }
        if (this.member.isMethod()) {
            Class returnType = this.member.method().getReturnType();
            if (returnType == Void.TYPE) {
                this.hasVoidReturn = true;
            } else {
                this.returnTypeTag = getTypeTag(returnType);
            }
        }
    }

    public void addAsConstructor(Scriptable scriptable, Scriptable scriptable2) {
        initAsConstructor(scriptable, scriptable2);
        ScriptableObject.defineProperty(scriptable, scriptable2.getClassName(), this, JAVA_INT_TYPE);
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        int i;
        Object invoke;
        int i2 = JAVA_UNSUPPORTED_TYPE;
        int length = objArr.length;
        for (i = JAVA_UNSUPPORTED_TYPE; i < length; i += JAVA_STRING_TYPE) {
            if (objArr[i] instanceof ConsString) {
                objArr[i] = objArr[i].toString();
            }
        }
        Object[] objArr2;
        if (this.parmsLength >= 0) {
            Object obj;
            if (!this.isStatic) {
                Class declaringClass = this.member.getDeclaringClass();
                if (!declaringClass.isInstance(scriptable2)) {
                    boolean isInstance;
                    if (scriptable2 == scriptable) {
                        Scriptable parentScope = getParentScope();
                        if (scriptable != parentScope) {
                            isInstance = declaringClass.isInstance(parentScope);
                            if (isInstance) {
                                obj = parentScope;
                            }
                            if (!isInstance) {
                                throw ScriptRuntime.typeError1("msg.incompat.call", this.functionName);
                            }
                        }
                    }
                    isInstance = false;
                    if (isInstance) {
                        throw ScriptRuntime.typeError1("msg.incompat.call", this.functionName);
                    }
                }
            }
            if (this.parmsLength == length) {
                int i3 = JAVA_UNSUPPORTED_TYPE;
                objArr2 = objArr;
                while (i3 != this.parmsLength) {
                    Object obj2 = objArr[i3];
                    Object convertArg = convertArg(context, scriptable, obj2, this.typeTags[i3]);
                    if (obj2 != convertArg) {
                        if (objArr2 == objArr) {
                            objArr2 = (Object[]) objArr.clone();
                        }
                        objArr2[i3] = convertArg;
                    }
                    i3 += JAVA_STRING_TYPE;
                    objArr2 = objArr2;
                }
            } else if (this.parmsLength == 0) {
                objArr2 = ScriptRuntime.emptyArgs;
            } else {
                Object[] objArr3 = new Object[this.parmsLength];
                i = JAVA_UNSUPPORTED_TYPE;
                while (i != this.parmsLength) {
                    objArr3[i] = convertArg(context, scriptable, i < length ? objArr[i] : Undefined.instance, this.typeTags[i]);
                    i += JAVA_STRING_TYPE;
                }
                objArr2 = objArr3;
            }
            if (this.member.isMethod()) {
                invoke = this.member.invoke(obj, objArr2);
                i2 = JAVA_STRING_TYPE;
            } else {
                invoke = this.member.newInstance(objArr2);
            }
        } else if (this.parmsLength == -1) {
            objArr2 = new Object[JAVA_DOUBLE_TYPE];
            objArr2[JAVA_UNSUPPORTED_TYPE] = context;
            objArr2[JAVA_STRING_TYPE] = scriptable2;
            objArr2[JAVA_INT_TYPE] = objArr;
            objArr2[JAVA_BOOLEAN_TYPE] = this;
            invoke = this.member.invoke(null, objArr2);
            i2 = JAVA_STRING_TYPE;
        } else {
            Boolean bool = (scriptable2 == null ? JAVA_STRING_TYPE : JAVA_UNSUPPORTED_TYPE) != 0 ? Boolean.TRUE : Boolean.FALSE;
            Object[] objArr4 = new Object[JAVA_DOUBLE_TYPE];
            objArr4[JAVA_UNSUPPORTED_TYPE] = context;
            objArr4[JAVA_STRING_TYPE] = objArr;
            objArr4[JAVA_INT_TYPE] = this;
            objArr4[JAVA_BOOLEAN_TYPE] = bool;
            invoke = this.member.isCtor() ? this.member.newInstance(objArr4) : this.member.invoke(null, objArr4);
        }
        return i2 != 0 ? this.hasVoidReturn ? Undefined.instance : this.returnTypeTag == 0 ? context.getWrapFactory().wrap(context, scriptable, invoke, null) : invoke : invoke;
    }

    public Scriptable createObject(Context context, Scriptable scriptable) {
        if (this.member.isCtor() || this.parmsLength == -2) {
            return null;
        }
        try {
            Scriptable scriptable2 = (Scriptable) this.member.getDeclaringClass().newInstance();
            scriptable2.setPrototype(getClassPrototype());
            scriptable2.setParentScope(getParentScope());
            return scriptable2;
        } catch (Throwable e) {
            throw Context.throwAsScriptRuntimeEx(e);
        }
    }

    public int getArity() {
        return this.parmsLength < 0 ? JAVA_STRING_TYPE : this.parmsLength;
    }

    public String getFunctionName() {
        return this.functionName == null ? BuildConfig.FLAVOR : this.functionName;
    }

    public int getLength() {
        return getArity();
    }

    public Member getMethodOrConstructor() {
        return this.member.isMethod() ? this.member.method() : this.member.ctor();
    }

    void initAsConstructor(Scriptable scriptable, Scriptable scriptable2) {
        ScriptRuntime.setFunctionProtoAndParent(this, scriptable);
        setImmunePrototypeProperty(scriptable2);
        scriptable2.setParentScope(this);
        ScriptableObject.defineProperty(scriptable2, "constructor", this, 7);
        setParentScope(scriptable);
    }

    boolean isVarArgsConstructor() {
        return this.parmsLength == -2;
    }

    boolean isVarArgsMethod() {
        return this.parmsLength == -1;
    }
}
