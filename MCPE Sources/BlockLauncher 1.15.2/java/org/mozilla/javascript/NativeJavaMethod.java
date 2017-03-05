package org.mozilla.javascript;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class NativeJavaMethod extends BaseFunction {
    private static final int PREFERENCE_AMBIGUOUS = 3;
    private static final int PREFERENCE_EQUAL = 0;
    private static final int PREFERENCE_FIRST_ARG = 1;
    private static final int PREFERENCE_SECOND_ARG = 2;
    private static MethodWatcher blockLauncherMethodWatcher = null;
    private static final boolean debug = false;
    static final long serialVersionUID = -3440381785576412928L;
    private String functionName;
    MemberBox[] methods;
    private transient CopyOnWriteArrayList<ResolvedOverload> overloadCache;

    public interface MethodWatcher {
        boolean canCall(Method method, Object obj);
    }

    public NativeJavaMethod(Method method, String str) {
        this(new MemberBox(method), str);
    }

    NativeJavaMethod(MemberBox memberBox, String str) {
        this.functionName = str;
        MemberBox[] memberBoxArr = new MemberBox[PREFERENCE_FIRST_ARG];
        memberBoxArr[PREFERENCE_EQUAL] = memberBox;
        this.methods = memberBoxArr;
    }

    NativeJavaMethod(MemberBox[] memberBoxArr) {
        this.functionName = memberBoxArr[PREFERENCE_EQUAL].getName();
        this.methods = memberBoxArr;
    }

    NativeJavaMethod(MemberBox[] memberBoxArr, String str) {
        this.functionName = str;
        this.methods = memberBoxArr;
    }

    static int findFunction(Context context, MemberBox[] memberBoxArr, Object[] objArr) {
        if (memberBoxArr.length == 0) {
            return -1;
        }
        int length;
        int i;
        if (memberBoxArr.length == PREFERENCE_FIRST_ARG) {
            MemberBox memberBox = memberBoxArr[PREFERENCE_EQUAL];
            Class[] clsArr = memberBox.argTypes;
            length = clsArr.length;
            if (memberBox.vararg) {
                length--;
                if (length > objArr.length) {
                    return -1;
                }
            } else if (length != objArr.length) {
                return -1;
            }
            for (i = PREFERENCE_EQUAL; i != length; i += PREFERENCE_FIRST_ARG) {
                if (!NativeJavaObject.canConvert(objArr[i], clsArr[i])) {
                    return -1;
                }
            }
            return PREFERENCE_EQUAL;
        }
        int i2 = -1;
        int[] iArr = null;
        i = PREFERENCE_EQUAL;
        int i3 = PREFERENCE_EQUAL;
        while (i3 < memberBoxArr.length) {
            int i4;
            int[] iArr2;
            MemberBox memberBox2 = memberBoxArr[i3];
            Class[] clsArr2 = memberBox2.argTypes;
            int length2 = clsArr2.length;
            int i5;
            int[] iArr3;
            int i6;
            int i7;
            MemberBox memberBox3;
            int preferSignature;
            if (memberBox2.vararg) {
                length2--;
                if (length2 > objArr.length) {
                    i4 = i;
                    iArr2 = iArr;
                    length = i4;
                }
                for (i5 = PREFERENCE_EQUAL; i5 < length2; i5 += PREFERENCE_FIRST_ARG) {
                    if (!NativeJavaObject.canConvert(objArr[i5], clsArr2[i5])) {
                        i4 = i;
                        iArr2 = iArr;
                        length = i4;
                        break;
                    }
                }
                if (i2 >= 0) {
                    i2 = i3;
                    iArr3 = iArr;
                    length = i;
                    iArr2 = iArr3;
                } else {
                    i6 = PREFERENCE_EQUAL;
                    i5 = PREFERENCE_EQUAL;
                    i7 = -1;
                    while (i7 != i) {
                        memberBox3 = memberBoxArr[i7 != -1 ? i2 : iArr[i7]];
                        if (context.hasFeature(13) || (memberBox3.member().getModifiers() & PREFERENCE_FIRST_ARG) == (memberBox2.member().getModifiers() & PREFERENCE_FIRST_ARG)) {
                            preferSignature = preferSignature(objArr, clsArr2, memberBox2.vararg, memberBox3.argTypes, memberBox3.vararg);
                            if (preferSignature == PREFERENCE_AMBIGUOUS) {
                                break;
                            } else if (preferSignature == PREFERENCE_FIRST_ARG) {
                                i4 = i5;
                                i5 = i6 + PREFERENCE_FIRST_ARG;
                                length2 = i4;
                            } else if (preferSignature != PREFERENCE_SECOND_ARG) {
                                length2 = i5 + PREFERENCE_FIRST_ARG;
                                i5 = i6;
                            } else {
                                if (preferSignature != 0) {
                                    Kit.codeBug();
                                }
                                if (memberBox3.isStatic() || !memberBox3.getDeclaringClass().isAssignableFrom(memberBox2.getDeclaringClass())) {
                                    i4 = i;
                                    iArr2 = iArr;
                                    length = i4;
                                } else if (i7 == -1) {
                                    i2 = i3;
                                    iArr3 = iArr;
                                    length = i;
                                    iArr2 = iArr3;
                                } else {
                                    iArr[i7] = i3;
                                    i4 = i;
                                    iArr2 = iArr;
                                    length = i4;
                                }
                            }
                        } else if ((memberBox3.member().getModifiers() & PREFERENCE_FIRST_ARG) == 0) {
                            i4 = i5;
                            i5 = i6 + PREFERENCE_FIRST_ARG;
                            length2 = i4;
                        } else {
                            length2 = i5 + PREFERENCE_FIRST_ARG;
                            i5 = i6;
                        }
                        i7 += PREFERENCE_FIRST_ARG;
                        i6 = i5;
                        i5 = length2;
                    }
                    if (i6 == i + PREFERENCE_FIRST_ARG) {
                        i2 = i3;
                        iArr3 = iArr;
                        length = PREFERENCE_EQUAL;
                        iArr2 = iArr3;
                    } else if (i5 != i + PREFERENCE_FIRST_ARG) {
                        i4 = i;
                        iArr2 = iArr;
                        length = i4;
                    } else {
                        if (iArr == null) {
                            iArr = new int[(memberBoxArr.length - 1)];
                        }
                        iArr[i] = i3;
                        i4 = i + PREFERENCE_FIRST_ARG;
                        iArr2 = iArr;
                        length = i4;
                    }
                }
            } else {
                if (length2 != objArr.length) {
                    i4 = i;
                    iArr2 = iArr;
                    length = i4;
                }
                for (i5 = PREFERENCE_EQUAL; i5 < length2; i5 += PREFERENCE_FIRST_ARG) {
                    if (!NativeJavaObject.canConvert(objArr[i5], clsArr2[i5])) {
                        i4 = i;
                        iArr2 = iArr;
                        length = i4;
                        break;
                    }
                }
                if (i2 >= 0) {
                    i6 = PREFERENCE_EQUAL;
                    i5 = PREFERENCE_EQUAL;
                    i7 = -1;
                    while (i7 != i) {
                        if (i7 != -1) {
                        }
                        memberBox3 = memberBoxArr[i7 != -1 ? i2 : iArr[i7]];
                        if (context.hasFeature(13)) {
                        }
                        preferSignature = preferSignature(objArr, clsArr2, memberBox2.vararg, memberBox3.argTypes, memberBox3.vararg);
                        if (preferSignature == PREFERENCE_AMBIGUOUS) {
                            break;
                        }
                        if (preferSignature == PREFERENCE_FIRST_ARG) {
                            i4 = i5;
                            i5 = i6 + PREFERENCE_FIRST_ARG;
                            length2 = i4;
                        } else if (preferSignature != PREFERENCE_SECOND_ARG) {
                            if (preferSignature != 0) {
                                Kit.codeBug();
                            }
                            if (memberBox3.isStatic()) {
                            }
                            i4 = i;
                            iArr2 = iArr;
                            length = i4;
                        } else {
                            length2 = i5 + PREFERENCE_FIRST_ARG;
                            i5 = i6;
                        }
                        i7 += PREFERENCE_FIRST_ARG;
                        i6 = i5;
                        i5 = length2;
                    }
                    if (i6 == i + PREFERENCE_FIRST_ARG) {
                        i2 = i3;
                        iArr3 = iArr;
                        length = PREFERENCE_EQUAL;
                        iArr2 = iArr3;
                    } else if (i5 != i + PREFERENCE_FIRST_ARG) {
                        if (iArr == null) {
                            iArr = new int[(memberBoxArr.length - 1)];
                        }
                        iArr[i] = i3;
                        i4 = i + PREFERENCE_FIRST_ARG;
                        iArr2 = iArr;
                        length = i4;
                    } else {
                        i4 = i;
                        iArr2 = iArr;
                        length = i4;
                    }
                } else {
                    i2 = i3;
                    iArr3 = iArr;
                    length = i;
                    iArr2 = iArr3;
                }
            }
            i3 += PREFERENCE_FIRST_ARG;
            i4 = length;
            iArr = iArr2;
            i = i4;
        }
        if (i2 < 0) {
            return -1;
        }
        if (i == 0) {
            return i2;
        }
        StringBuilder stringBuilder = new StringBuilder();
        length2 = -1;
        while (length2 != i) {
            i3 = length2 == -1 ? i2 : iArr[length2];
            stringBuilder.append("\n    ");
            stringBuilder.append(memberBoxArr[i3].toJavaDeclaration());
            length2 += PREFERENCE_FIRST_ARG;
        }
        MemberBox memberBox4 = memberBoxArr[i2];
        String name = memberBox4.getName();
        String name2 = memberBox4.getDeclaringClass().getName();
        if (memberBoxArr[PREFERENCE_EQUAL].isCtor()) {
            throw Context.reportRuntimeError3("msg.constructor.ambiguous", name, scriptSignature(objArr), stringBuilder.toString());
        }
        throw Context.reportRuntimeError4("msg.method.ambiguous", name2, name, scriptSignature(objArr), stringBuilder.toString());
    }

    private static int preferSignature(Object[] objArr, Class<?>[] clsArr, boolean z, Class<?>[] clsArr2, boolean z2) {
        int i = PREFERENCE_EQUAL;
        int i2 = PREFERENCE_EQUAL;
        while (i < objArr.length) {
            if (!z || i < clsArr.length) {
                Class<?> cls = clsArr[i];
            } else {
                Class cls2 = clsArr[clsArr.length - 1];
            }
            Class cls3 = (!z2 || i < clsArr2.length) ? clsArr2[i] : clsArr2[clsArr2.length - 1];
            if (cls2 != cls3) {
                Object obj = objArr[i];
                int conversionWeight = NativeJavaObject.getConversionWeight(obj, cls2);
                int conversionWeight2 = NativeJavaObject.getConversionWeight(obj, cls3);
                int i3 = conversionWeight < conversionWeight2 ? PREFERENCE_FIRST_ARG : conversionWeight > conversionWeight2 ? PREFERENCE_SECOND_ARG : conversionWeight == 0 ? cls2.isAssignableFrom(cls3) ? PREFERENCE_SECOND_ARG : cls3.isAssignableFrom(cls2) ? PREFERENCE_FIRST_ARG : PREFERENCE_AMBIGUOUS : PREFERENCE_AMBIGUOUS;
                i2 |= i3;
                if (i2 == PREFERENCE_AMBIGUOUS) {
                    break;
                }
            }
            i += PREFERENCE_FIRST_ARG;
        }
        return i2;
    }

    private static void printDebug(String str, MemberBox memberBox, Object[] objArr) {
    }

    static String scriptSignature(Object[] objArr) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = PREFERENCE_EQUAL; i != objArr.length; i += PREFERENCE_FIRST_ARG) {
            Object obj = objArr[i];
            String name = obj == null ? "null" : obj instanceof Boolean ? "boolean" : obj instanceof String ? "string" : obj instanceof Number ? "number" : obj instanceof Scriptable ? obj instanceof Undefined ? "undefined" : obj instanceof Wrapper ? ((Wrapper) obj).unwrap().getClass().getName() : obj instanceof Function ? "function" : "object" : JavaMembers.javaSignature(obj.getClass());
            if (i != 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(name);
        }
        return stringBuilder.toString();
    }

    public static void setMethodWatcher(MethodWatcher methodWatcher) {
        if (blockLauncherMethodWatcher == null) {
            blockLauncherMethodWatcher = methodWatcher;
        }
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        int i = PREFERENCE_EQUAL;
        if (this.methods.length == 0) {
            throw new RuntimeException("No methods defined for call");
        }
        int findCachedFunction = findCachedFunction(context, objArr);
        if (findCachedFunction < 0) {
            throw Context.reportRuntimeError1("msg.java.no_such_method", this.methods[PREFERENCE_EQUAL].method().getDeclaringClass().getName() + '.' + getFunctionName() + '(' + scriptSignature(objArr) + ')');
        }
        Object[] objArr2;
        Object jsToJava;
        MemberBox memberBox = this.methods[findCachedFunction];
        Class[] clsArr = memberBox.argTypes;
        if (memberBox.vararg) {
            objArr2 = new Object[clsArr.length];
            for (findCachedFunction = PREFERENCE_EQUAL; findCachedFunction < clsArr.length - 1; findCachedFunction += PREFERENCE_FIRST_ARG) {
                objArr2[findCachedFunction] = Context.jsToJava(objArr[findCachedFunction], clsArr[findCachedFunction]);
            }
            if (objArr.length == clsArr.length && (objArr[objArr.length - 1] == null || (objArr[objArr.length - 1] instanceof NativeArray) || (objArr[objArr.length - 1] instanceof NativeJavaArray))) {
                jsToJava = Context.jsToJava(objArr[objArr.length - 1], clsArr[clsArr.length - 1]);
            } else {
                Class componentType = clsArr[clsArr.length - 1].getComponentType();
                jsToJava = Array.newInstance(componentType, (objArr.length - clsArr.length) + PREFERENCE_FIRST_ARG);
                while (i < Array.getLength(jsToJava)) {
                    Array.set(jsToJava, i, Context.jsToJava(objArr[(clsArr.length - 1) + i], componentType));
                    i += PREFERENCE_FIRST_ARG;
                }
            }
            objArr2[clsArr.length - 1] = jsToJava;
        } else {
            Object obj;
            jsToJava = objArr;
            while (i < jsToJava.length) {
                obj = jsToJava[i];
                Object jsToJava2 = Context.jsToJava(obj, clsArr[i]);
                if (jsToJava2 != obj) {
                    if (objArr == jsToJava) {
                        jsToJava = (Object[]) jsToJava.clone();
                    }
                    jsToJava[i] = jsToJava2;
                }
                i += PREFERENCE_FIRST_ARG;
            }
            obj = jsToJava;
        }
        if (memberBox.isStatic()) {
            jsToJava = null;
        } else {
            Class declaringClass = memberBox.getDeclaringClass();
            for (Scriptable scriptable3 = scriptable2; scriptable3 != null; scriptable3 = scriptable3.getPrototype()) {
                if (scriptable3 instanceof Wrapper) {
                    jsToJava = ((Wrapper) scriptable3).unwrap();
                    if (declaringClass.isInstance(jsToJava)) {
                    }
                }
            }
            throw Context.reportRuntimeError3("msg.nonjava.method", getFunctionName(), ScriptRuntime.toString((Object) scriptable2), declaringClass.getName());
        }
        if (blockLauncherMethodWatcher != null && !blockLauncherMethodWatcher.canCall(memberBox.method(), jsToJava)) {
            return Undefined.instance;
        }
        jsToJava = memberBox.invoke(jsToJava, objArr2);
        Class returnType = memberBox.method().getReturnType();
        jsToJava = context.getWrapFactory().wrap(context, scriptable, jsToJava, returnType);
        return (jsToJava == null && returnType == Void.TYPE) ? Undefined.instance : jsToJava;
    }

    String decompile(int i, int i2) {
        StringBuilder stringBuilder = new StringBuilder();
        Object obj = (i2 & PREFERENCE_FIRST_ARG) != 0 ? PREFERENCE_FIRST_ARG : null;
        if (obj == null) {
            stringBuilder.append("function ");
            stringBuilder.append(getFunctionName());
            stringBuilder.append("() {");
        }
        stringBuilder.append("/*\n");
        stringBuilder.append(toString());
        stringBuilder.append(obj != null ? "*/\n" : "*/}\n");
        return stringBuilder.toString();
    }

    int findCachedFunction(Context context, Object[] objArr) {
        if (this.methods.length <= PREFERENCE_FIRST_ARG) {
            return findFunction(context, this.methods, objArr);
        }
        if (this.overloadCache != null) {
            Iterator it = this.overloadCache.iterator();
            while (it.hasNext()) {
                ResolvedOverload resolvedOverload = (ResolvedOverload) it.next();
                if (resolvedOverload.matches(objArr)) {
                    return resolvedOverload.index;
                }
            }
        }
        this.overloadCache = new CopyOnWriteArrayList();
        int findFunction = findFunction(context, this.methods, objArr);
        if (this.overloadCache.size() >= this.methods.length * PREFERENCE_SECOND_ARG) {
            return findFunction;
        }
        synchronized (this.overloadCache) {
            ResolvedOverload resolvedOverload2 = new ResolvedOverload(objArr, findFunction);
            if (!this.overloadCache.contains(resolvedOverload2)) {
                this.overloadCache.add(PREFERENCE_EQUAL, resolvedOverload2);
            }
        }
        return findFunction;
    }

    public String getFunctionName() {
        return this.functionName;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int length = this.methods.length;
        for (int i = PREFERENCE_EQUAL; i != length; i += PREFERENCE_FIRST_ARG) {
            if (this.methods[i].isMethod()) {
                Method method = this.methods[i].method();
                stringBuilder.append(JavaMembers.javaSignature(method.getReturnType()));
                stringBuilder.append(' ');
                stringBuilder.append(method.getName());
            } else {
                stringBuilder.append(this.methods[i].getName());
            }
            stringBuilder.append(JavaMembers.liveConnectSignature(this.methods[i].argTypes));
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }
}
