package org.mozilla.javascript;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;
import org.mozilla.javascript.TopLevel.Builtins;

public class NativeArray extends IdScriptableObject implements List {
    private static final Object ARRAY_TAG = "Array";
    private static final int ConstructorId_concat = -13;
    private static final int ConstructorId_every = -17;
    private static final int ConstructorId_filter = -18;
    private static final int ConstructorId_find = -22;
    private static final int ConstructorId_findIndex = -23;
    private static final int ConstructorId_forEach = -19;
    private static final int ConstructorId_indexOf = -15;
    private static final int ConstructorId_isArray = -26;
    private static final int ConstructorId_join = -5;
    private static final int ConstructorId_lastIndexOf = -16;
    private static final int ConstructorId_map = -20;
    private static final int ConstructorId_pop = -9;
    private static final int ConstructorId_push = -8;
    private static final int ConstructorId_reduce = -24;
    private static final int ConstructorId_reduceRight = -25;
    private static final int ConstructorId_reverse = -6;
    private static final int ConstructorId_shift = -10;
    private static final int ConstructorId_slice = -14;
    private static final int ConstructorId_some = -21;
    private static final int ConstructorId_sort = -7;
    private static final int ConstructorId_splice = -12;
    private static final int ConstructorId_unshift = -11;
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    private static final double GROW_FACTOR = 1.5d;
    private static final int Id_concat = 13;
    private static final int Id_constructor = 1;
    private static final int Id_every = 17;
    private static final int Id_filter = 18;
    private static final int Id_find = 22;
    private static final int Id_findIndex = 23;
    private static final int Id_forEach = 19;
    private static final int Id_indexOf = 15;
    private static final int Id_iterator = 26;
    private static final int Id_join = 5;
    private static final int Id_lastIndexOf = 16;
    private static final int Id_length = 1;
    private static final int Id_map = 20;
    private static final int Id_pop = 9;
    private static final int Id_push = 8;
    private static final int Id_reduce = 24;
    private static final int Id_reduceRight = 25;
    private static final int Id_reverse = 6;
    private static final int Id_shift = 10;
    private static final int Id_slice = 14;
    private static final int Id_some = 21;
    private static final int Id_sort = 7;
    private static final int Id_splice = 12;
    private static final int Id_toLocaleString = 3;
    private static final int Id_toSource = 4;
    private static final int Id_toString = 2;
    private static final int Id_unshift = 11;
    private static final int MAX_INSTANCE_ID = 1;
    private static final int MAX_PRE_GROW_SIZE = 1431655764;
    private static final int MAX_PROTOTYPE_ID = 26;
    private static final Integer NEGATIVE_ONE = Integer.valueOf(-1);
    private static int maximumInitialCapacity = 10000;
    static final long serialVersionUID = 7331366857676127338L;
    private Object[] dense;
    private boolean denseOnly;
    private long length;
    private int lengthAttr;

    public NativeArray(long j) {
        this.lengthAttr = Id_reverse;
        this.denseOnly = j <= ((long) maximumInitialCapacity);
        if (this.denseOnly) {
            int i = (int) j;
            if (i < Id_shift) {
                i = Id_shift;
            }
            this.dense = new Object[i];
            Arrays.fill(this.dense, Scriptable.NOT_FOUND);
        }
        this.length = j;
    }

    public NativeArray(Object[] objArr) {
        this.lengthAttr = Id_reverse;
        this.denseOnly = true;
        this.dense = objArr;
        this.length = (long) objArr.length;
    }

    private ScriptableObject defaultIndexPropertyDescriptor(Object obj) {
        Scriptable scriptable;
        Scriptable parentScope = getParentScope();
        if (parentScope != null) {
            scriptable = parentScope;
        }
        ScriptableObject nativeObject = new NativeObject();
        ScriptRuntime.setBuiltinProtoAndParent(nativeObject, scriptable, Builtins.Object);
        nativeObject.defineProperty(ES6Iterator.VALUE_PROPERTY, obj, 0);
        nativeObject.defineProperty("writable", Boolean.valueOf(true), 0);
        nativeObject.defineProperty("enumerable", Boolean.valueOf(true), 0);
        nativeObject.defineProperty("configurable", Boolean.valueOf(true), 0);
        return nativeObject;
    }

    private static void defineElem(Context context, Scriptable scriptable, long j, Object obj) {
        if (j > 2147483647L) {
            scriptable.put(Long.toString(j), scriptable, obj);
        } else {
            scriptable.put((int) j, scriptable, obj);
        }
    }

    private static void deleteElem(Scriptable scriptable, long j) {
        int i = (int) j;
        if (((long) i) == j) {
            scriptable.delete(i);
        } else {
            scriptable.delete(Long.toString(j));
        }
    }

    private boolean ensureCapacity(int i) {
        if (i > this.dense.length) {
            if (i > MAX_PRE_GROW_SIZE) {
                this.denseOnly = false;
                return false;
            }
            Object obj = new Object[Math.max(i, (int) (((double) this.dense.length) * GROW_FACTOR))];
            System.arraycopy(this.dense, 0, obj, 0, this.dense.length);
            Arrays.fill(obj, this.dense.length, obj.length, Scriptable.NOT_FOUND);
            this.dense = obj;
        }
        return true;
    }

    private static Object getElem(Context context, Scriptable scriptable, long j) {
        Object rawElem = getRawElem(scriptable, j);
        return rawElem != Scriptable.NOT_FOUND ? rawElem : Undefined.instance;
    }

    static long getLengthProperty(Context context, Scriptable scriptable) {
        if (scriptable instanceof NativeString) {
            return (long) ((NativeString) scriptable).getLength();
        }
        if (scriptable instanceof NativeArray) {
            return ((NativeArray) scriptable).getLength();
        }
        Object property = ScriptableObject.getProperty(scriptable, Name.LENGTH);
        return property == Scriptable.NOT_FOUND ? 0 : ScriptRuntime.toUint32(property);
    }

    static int getMaximumInitialCapacity() {
        return maximumInitialCapacity;
    }

    private static Object getRawElem(Scriptable scriptable, long j) {
        return j > 2147483647L ? ScriptableObject.getProperty(scriptable, Long.toString(j)) : ScriptableObject.getProperty(scriptable, (int) j);
    }

    static void init(Scriptable scriptable, boolean z) {
        new NativeArray(0).exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.Object iterativeMethod(org.mozilla.javascript.Context r19, org.mozilla.javascript.IdFunctionObject r20, org.mozilla.javascript.Scriptable r21, org.mozilla.javascript.Scriptable r22, java.lang.Object[] r23) {
        /*
        r14 = r20.methodId();
        r4 = 22;
        if (r4 == r14) goto L_0x000c;
    L_0x0008:
        r4 = 23;
        if (r4 != r14) goto L_0x0016;
    L_0x000c:
        r0 = r19;
        r1 = r22;
        r2 = r20;
        r22 = org.mozilla.javascript.ScriptRuntimeES6.requireObjectCoercible(r0, r1, r2);
    L_0x0016:
        r0 = r19;
        r1 = r22;
        r16 = getLengthProperty(r0, r1);
        r0 = r23;
        r4 = r0.length;
        if (r4 <= 0) goto L_0x0031;
    L_0x0023:
        r4 = 0;
        r4 = r23[r4];
    L_0x0026:
        if (r4 == 0) goto L_0x002c;
    L_0x0028:
        r5 = r4 instanceof org.mozilla.javascript.Function;
        if (r5 != 0) goto L_0x0034;
    L_0x002c:
        r4 = org.mozilla.javascript.ScriptRuntime.notFunctionError(r4);
        throw r4;
    L_0x0031:
        r4 = org.mozilla.javascript.Undefined.instance;
        goto L_0x0026;
    L_0x0034:
        r5 = r19.getLanguageVersion();
        r6 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        if (r5 < r6) goto L_0x0045;
    L_0x003c:
        r5 = r4 instanceof org.mozilla.javascript.regexp.NativeRegExp;
        if (r5 == 0) goto L_0x0045;
    L_0x0040:
        r4 = org.mozilla.javascript.ScriptRuntime.notFunctionError(r4);
        throw r4;
    L_0x0045:
        r4 = (org.mozilla.javascript.Function) r4;
        r13 = org.mozilla.javascript.ScriptableObject.getTopLevelScope(r4);
        r0 = r23;
        r5 = r0.length;
        r6 = 2;
        if (r5 < r6) goto L_0x005d;
    L_0x0051:
        r5 = 1;
        r5 = r23[r5];
        if (r5 == 0) goto L_0x005d;
    L_0x0056:
        r5 = 1;
        r5 = r23[r5];
        r6 = org.mozilla.javascript.Undefined.instance;
        if (r5 != r6) goto L_0x00b3;
    L_0x005d:
        r12 = r13;
    L_0x005e:
        r5 = 0;
        r6 = 18;
        if (r14 == r6) goto L_0x0067;
    L_0x0063:
        r6 = 20;
        if (r14 != r6) goto L_0x0076;
    L_0x0067:
        r5 = 20;
        if (r14 != r5) goto L_0x00c0;
    L_0x006b:
        r0 = r16;
        r5 = (int) r0;
    L_0x006e:
        r0 = r19;
        r1 = r21;
        r5 = r0.newArray(r1, r5);
    L_0x0076:
        r8 = 0;
        r6 = 0;
        r10 = r6;
    L_0x007b:
        r6 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1));
        if (r6 >= 0) goto L_0x0103;
    L_0x007f:
        r6 = 3;
        r15 = new java.lang.Object[r6];
        r0 = r22;
        r6 = getRawElem(r0, r10);
        r7 = org.mozilla.javascript.Scriptable.NOT_FOUND;
        if (r6 != r7) goto L_0x0096;
    L_0x008c:
        r6 = 22;
        if (r14 == r6) goto L_0x0094;
    L_0x0090:
        r6 = 23;
        if (r14 != r6) goto L_0x00ac;
    L_0x0094:
        r6 = org.mozilla.javascript.Undefined.instance;
    L_0x0096:
        r7 = 0;
        r15[r7] = r6;
        r7 = 1;
        r18 = java.lang.Long.valueOf(r10);
        r15[r7] = r18;
        r7 = 2;
        r15[r7] = r22;
        r0 = r19;
        r7 = r4.call(r0, r13, r12, r15);
        switch(r14) {
            case 17: goto L_0x00c2;
            case 18: goto L_0x00cb;
            case 19: goto L_0x00de;
            case 20: goto L_0x00e0;
            case 21: goto L_0x00e7;
            case 22: goto L_0x00f0;
            case 23: goto L_0x00f7;
            default: goto L_0x00ac;
        };
    L_0x00ac:
        r6 = r8;
    L_0x00ad:
        r8 = 1;
        r8 = r8 + r10;
        r10 = r8;
        r8 = r6;
        goto L_0x007b;
    L_0x00b3:
        r5 = 1;
        r5 = r23[r5];
        r0 = r19;
        r1 = r21;
        r5 = org.mozilla.javascript.ScriptRuntime.toObject(r0, r1, r5);
        r12 = r5;
        goto L_0x005e;
    L_0x00c0:
        r5 = 0;
        goto L_0x006e;
    L_0x00c2:
        r6 = org.mozilla.javascript.ScriptRuntime.toBoolean(r7);
        if (r6 != 0) goto L_0x00ac;
    L_0x00c8:
        r6 = java.lang.Boolean.FALSE;
    L_0x00ca:
        return r6;
    L_0x00cb:
        r6 = org.mozilla.javascript.ScriptRuntime.toBoolean(r7);
        if (r6 == 0) goto L_0x00ac;
    L_0x00d1:
        r6 = 1;
        r6 = r6 + r8;
        r18 = 0;
        r15 = r15[r18];
        r0 = r19;
        defineElem(r0, r5, r8, r15);
        goto L_0x00ad;
    L_0x00de:
        r6 = r8;
        goto L_0x00ad;
    L_0x00e0:
        r0 = r19;
        defineElem(r0, r5, r10, r7);
        r6 = r8;
        goto L_0x00ad;
    L_0x00e7:
        r6 = org.mozilla.javascript.ScriptRuntime.toBoolean(r7);
        if (r6 == 0) goto L_0x00ac;
    L_0x00ed:
        r6 = java.lang.Boolean.TRUE;
        goto L_0x00ca;
    L_0x00f0:
        r7 = org.mozilla.javascript.ScriptRuntime.toBoolean(r7);
        if (r7 == 0) goto L_0x00ac;
    L_0x00f6:
        goto L_0x00ca;
    L_0x00f7:
        r6 = org.mozilla.javascript.ScriptRuntime.toBoolean(r7);
        if (r6 == 0) goto L_0x00ac;
    L_0x00fd:
        r4 = (double) r10;
        r6 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r4);
        goto L_0x00ca;
    L_0x0103:
        switch(r14) {
            case 17: goto L_0x0109;
            case 18: goto L_0x010c;
            case 19: goto L_0x0106;
            case 20: goto L_0x010c;
            case 21: goto L_0x010e;
            case 22: goto L_0x0106;
            case 23: goto L_0x0111;
            default: goto L_0x0106;
        };
    L_0x0106:
        r6 = org.mozilla.javascript.Undefined.instance;
        goto L_0x00ca;
    L_0x0109:
        r6 = java.lang.Boolean.TRUE;
        goto L_0x00ca;
    L_0x010c:
        r6 = r5;
        goto L_0x00ca;
    L_0x010e:
        r6 = java.lang.Boolean.FALSE;
        goto L_0x00ca;
    L_0x0111:
        r4 = -4616189618054758400; // 0xbff0000000000000 float:0.0 double:-1.0;
        r6 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r4);
        goto L_0x00ca;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeArray.iterativeMethod(org.mozilla.javascript.Context, org.mozilla.javascript.IdFunctionObject, org.mozilla.javascript.Scriptable, org.mozilla.javascript.Scriptable, java.lang.Object[]):java.lang.Object");
    }

    private static Object jsConstructor(Context context, Scriptable scriptable, Object[] objArr) {
        if (objArr.length == 0) {
            return new NativeArray(0);
        }
        if (context.getLanguageVersion() == Token.FOR) {
            return new NativeArray(objArr);
        }
        Object obj = objArr[0];
        if (objArr.length > MAX_INSTANCE_ID || !(obj instanceof Number)) {
            return new NativeArray(objArr);
        }
        long toUint32 = ScriptRuntime.toUint32(obj);
        if (((double) toUint32) == ((Number) obj).doubleValue()) {
            return new NativeArray(toUint32);
        }
        throw ScriptRuntime.constructError("RangeError", ScriptRuntime.getMessage0("msg.arraylength.bad"));
    }

    private static Scriptable js_concat(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        long lengthProperty;
        long j;
        Object rawElem;
        Scriptable newArray = context.newArray(ScriptableObject.getTopLevelScope(scriptable), 0);
        if ((scriptable2 instanceof NativeArray) && (newArray instanceof NativeArray)) {
            NativeArray nativeArray = (NativeArray) scriptable2;
            NativeArray nativeArray2 = (NativeArray) newArray;
            if (nativeArray.denseOnly && nativeArray2.denseOnly) {
                boolean z = MAX_INSTANCE_ID;
                int i = (int) nativeArray.length;
                int i2 = 0;
                while (i2 < objArr.length && z) {
                    boolean z2;
                    if (objArr[i2] instanceof NativeArray) {
                        NativeArray nativeArray3 = (NativeArray) objArr[i2];
                        z2 = nativeArray3.denseOnly;
                        i = (int) (((long) i) + nativeArray3.length);
                    } else {
                        i += MAX_INSTANCE_ID;
                        z2 = z;
                    }
                    i2 += MAX_INSTANCE_ID;
                    z = z2;
                }
                if (z && nativeArray2.ensureCapacity(i)) {
                    System.arraycopy(nativeArray.dense, 0, nativeArray2.dense, 0, (int) nativeArray.length);
                    int i3 = (int) nativeArray.length;
                    for (i2 = 0; i2 < objArr.length && z; i2 += MAX_INSTANCE_ID) {
                        if (objArr[i2] instanceof NativeArray) {
                            nativeArray = (NativeArray) objArr[i2];
                            System.arraycopy(nativeArray.dense, 0, nativeArray2.dense, i3, (int) nativeArray.length);
                            i3 += (int) nativeArray.length;
                        } else {
                            int i4 = i3 + MAX_INSTANCE_ID;
                            nativeArray2.dense[i3] = objArr[i2];
                            i3 = i4;
                        }
                    }
                    nativeArray2.length = (long) i;
                    return newArray;
                }
            }
        }
        if (js_isArray(scriptable2)) {
            lengthProperty = getLengthProperty(context, scriptable2);
            j = 0;
            while (j < lengthProperty) {
                rawElem = getRawElem(scriptable2, j);
                if (rawElem != NOT_FOUND) {
                    defineElem(context, newArray, j, rawElem);
                }
                j++;
            }
        } else {
            j = 1 + 0;
            defineElem(context, newArray, 0, scriptable2);
        }
        lengthProperty = j;
        for (int i5 = 0; i5 < objArr.length; i5 += MAX_INSTANCE_ID) {
            long j2;
            if (js_isArray(objArr[i5])) {
                Scriptable scriptable3 = (Scriptable) objArr[i5];
                long lengthProperty2 = getLengthProperty(context, scriptable3);
                j2 = 0;
                while (j2 < lengthProperty2) {
                    rawElem = getRawElem(scriptable3, j2);
                    if (rawElem != NOT_FOUND) {
                        defineElem(context, newArray, lengthProperty, rawElem);
                    }
                    j2++;
                    lengthProperty++;
                }
            } else {
                j2 = 1 + lengthProperty;
                defineElem(context, newArray, lengthProperty, objArr[i5]);
                lengthProperty = j2;
            }
        }
        setLengthProperty(context, newArray, lengthProperty);
        return newArray;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.Object js_indexOf(org.mozilla.javascript.Context r10, org.mozilla.javascript.Scriptable r11, java.lang.Object[] r12) {
        /*
        r8 = 1;
        r2 = 0;
        r0 = r12.length;
        if (r0 <= 0) goto L_0x0047;
    L_0x0007:
        r0 = 0;
        r0 = r12[r0];
        r1 = r0;
    L_0x000b:
        r6 = getLengthProperty(r10, r11);
        r0 = r12.length;
        r4 = 2;
        if (r0 >= r4) goto L_0x004b;
    L_0x0013:
        r0 = r11 instanceof org.mozilla.javascript.NativeArray;
        if (r0 == 0) goto L_0x006d;
    L_0x0017:
        r0 = r11;
        r0 = (org.mozilla.javascript.NativeArray) r0;
        r4 = r0.denseOnly;
        if (r4 == 0) goto L_0x006d;
    L_0x001e:
        r4 = r0.getPrototype();
        r2 = (int) r2;
        r3 = r2;
    L_0x0024:
        r8 = (long) r3;
        r2 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r2 >= 0) goto L_0x0069;
    L_0x0029:
        r2 = r0.dense;
        r2 = r2[r3];
        r5 = NOT_FOUND;
        if (r2 != r5) goto L_0x0037;
    L_0x0031:
        if (r4 == 0) goto L_0x0037;
    L_0x0033:
        r2 = org.mozilla.javascript.ScriptableObject.getProperty(r4, r3);
    L_0x0037:
        r5 = NOT_FOUND;
        if (r2 == r5) goto L_0x0065;
    L_0x003b:
        r2 = org.mozilla.javascript.ScriptRuntime.shallowEq(r2, r1);
        if (r2 == 0) goto L_0x0065;
    L_0x0041:
        r0 = (long) r3;
        r0 = java.lang.Long.valueOf(r0);
    L_0x0046:
        return r0;
    L_0x0047:
        r0 = org.mozilla.javascript.Undefined.instance;
        r1 = r0;
        goto L_0x000b;
    L_0x004b:
        r0 = 1;
        r0 = r12[r0];
        r4 = org.mozilla.javascript.ScriptRuntime.toInteger(r0);
        r4 = (long) r4;
        r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x0087;
    L_0x0057:
        r4 = r4 + r6;
        r0 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x0087;
    L_0x005c:
        r4 = r6 - r8;
        r0 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r0 <= 0) goto L_0x0013;
    L_0x0062:
        r0 = NEGATIVE_ONE;
        goto L_0x0046;
    L_0x0065:
        r2 = r3 + 1;
        r3 = r2;
        goto L_0x0024;
    L_0x0069:
        r0 = NEGATIVE_ONE;
        goto L_0x0046;
    L_0x006c:
        r2 = r2 + r8;
    L_0x006d:
        r0 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1));
        if (r0 >= 0) goto L_0x0084;
    L_0x0071:
        r0 = getRawElem(r11, r2);
        r4 = NOT_FOUND;
        if (r0 == r4) goto L_0x006c;
    L_0x0079:
        r0 = org.mozilla.javascript.ScriptRuntime.shallowEq(r0, r1);
        if (r0 == 0) goto L_0x006c;
    L_0x007f:
        r0 = java.lang.Long.valueOf(r2);
        goto L_0x0046;
    L_0x0084:
        r0 = NEGATIVE_ONE;
        goto L_0x0046;
    L_0x0087:
        r2 = r4;
        goto L_0x005c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeArray.js_indexOf(org.mozilla.javascript.Context, org.mozilla.javascript.Scriptable, java.lang.Object[]):java.lang.Object");
    }

    private static boolean js_isArray(Object obj) {
        return !(obj instanceof Scriptable) ? false : "Array".equals(((Scriptable) obj).getClassName());
    }

    private static String js_join(Context context, Scriptable scriptable, Object[] objArr) {
        int i = 0;
        long lengthProperty = getLengthProperty(context, scriptable);
        int i2 = (int) lengthProperty;
        if (lengthProperty != ((long) i2)) {
            throw Context.reportRuntimeError1("msg.arraylength.too.big", String.valueOf(lengthProperty));
        }
        StringBuilder stringBuilder;
        String scriptRuntime = (objArr.length < MAX_INSTANCE_ID || objArr[0] == Undefined.instance) ? "," : ScriptRuntime.toString(objArr[0]);
        if (scriptable instanceof NativeArray) {
            NativeArray nativeArray = (NativeArray) scriptable;
            if (nativeArray.denseOnly) {
                stringBuilder = new StringBuilder();
                while (i < i2) {
                    if (i != 0) {
                        stringBuilder.append(scriptRuntime);
                    }
                    if (i < nativeArray.dense.length) {
                        Object obj = nativeArray.dense[i];
                        if (!(obj == null || obj == Undefined.instance || obj == Scriptable.NOT_FOUND)) {
                            stringBuilder.append(ScriptRuntime.toString(obj));
                        }
                    }
                    i += MAX_INSTANCE_ID;
                }
                return stringBuilder.toString();
            }
        }
        if (i2 == 0) {
            return BuildConfig.FLAVOR;
        }
        String[] strArr = new String[i2];
        int i3 = 0;
        for (int i4 = 0; i4 != i2; i4 += MAX_INSTANCE_ID) {
            Object elem = getElem(context, scriptable, (long) i4);
            if (!(elem == null || elem == Undefined.instance)) {
                String scriptRuntime2 = ScriptRuntime.toString(elem);
                i3 += scriptRuntime2.length();
                strArr[i4] = scriptRuntime2;
            }
        }
        stringBuilder = new StringBuilder(i3 + ((i2 - 1) * scriptRuntime.length()));
        for (i3 = 0; i3 != i2; i3 += MAX_INSTANCE_ID) {
            if (i3 != 0) {
                stringBuilder.append(scriptRuntime);
            }
            String str = strArr[i3];
            if (str != null) {
                stringBuilder.append(str);
            }
        }
        return stringBuilder.toString();
    }

    private static Object js_lastIndexOf(Context context, Scriptable scriptable, Object[] objArr) {
        long j;
        Object obj = objArr.length > 0 ? objArr[0] : Undefined.instance;
        long lengthProperty = getLengthProperty(context, scriptable);
        if (objArr.length < Id_toString) {
            j = lengthProperty - 1;
        } else {
            j = (long) ScriptRuntime.toInteger(objArr[MAX_INSTANCE_ID]);
            if (j >= lengthProperty) {
                j = lengthProperty - 1;
            } else if (j < 0) {
                j += lengthProperty;
            }
            if (j < 0) {
                return NEGATIVE_ONE;
            }
        }
        if (scriptable instanceof NativeArray) {
            NativeArray nativeArray = (NativeArray) scriptable;
            if (nativeArray.denseOnly) {
                Scriptable prototype = nativeArray.getPrototype();
                for (int i = (int) j; i >= 0; i--) {
                    Object obj2 = nativeArray.dense[i];
                    if (obj2 == NOT_FOUND && prototype != null) {
                        obj2 = ScriptableObject.getProperty(prototype, i);
                    }
                    if (obj2 != NOT_FOUND && ScriptRuntime.shallowEq(obj2, obj)) {
                        return Long.valueOf((long) i);
                    }
                }
                return NEGATIVE_ONE;
            }
        }
        while (j >= 0) {
            Object rawElem = getRawElem(scriptable, j);
            if (rawElem != NOT_FOUND && ScriptRuntime.shallowEq(rawElem, obj)) {
                return Long.valueOf(j);
            }
            j--;
        }
        return NEGATIVE_ONE;
    }

    private static Object js_pop(Context context, Scriptable scriptable, Object[] objArr) {
        Object obj;
        if (scriptable instanceof NativeArray) {
            NativeArray nativeArray = (NativeArray) scriptable;
            if (nativeArray.denseOnly && nativeArray.length > 0) {
                nativeArray.length--;
                obj = nativeArray.dense[(int) nativeArray.length];
                nativeArray.dense[(int) nativeArray.length] = NOT_FOUND;
                return obj;
            }
        }
        long lengthProperty = getLengthProperty(context, scriptable);
        if (lengthProperty > 0) {
            lengthProperty--;
            obj = getElem(context, scriptable, lengthProperty);
            deleteElem(scriptable, lengthProperty);
        } else {
            obj = Undefined.instance;
        }
        setLengthProperty(context, scriptable, lengthProperty);
        return obj;
    }

    private static Object js_push(Context context, Scriptable scriptable, Object[] objArr) {
        int i = 0;
        if (scriptable instanceof NativeArray) {
            NativeArray nativeArray = (NativeArray) scriptable;
            if (nativeArray.denseOnly && nativeArray.ensureCapacity(((int) nativeArray.length) + objArr.length)) {
                while (i < objArr.length) {
                    Object[] objArr2 = nativeArray.dense;
                    long j = nativeArray.length;
                    nativeArray.length = 1 + j;
                    objArr2[(int) j] = objArr[i];
                    i += MAX_INSTANCE_ID;
                }
                return ScriptRuntime.wrapNumber((double) nativeArray.length);
            }
        }
        long lengthProperty = getLengthProperty(context, scriptable);
        while (i < objArr.length) {
            setElem(context, scriptable, ((long) i) + lengthProperty, objArr[i]);
            i += MAX_INSTANCE_ID;
        }
        return context.getLanguageVersion() == Token.FOR ? objArr.length == 0 ? Undefined.instance : objArr[objArr.length - 1] : setLengthProperty(context, scriptable, ((long) objArr.length) + lengthProperty);
    }

    private static Scriptable js_reverse(Context context, Scriptable scriptable, Object[] objArr) {
        if (scriptable instanceof NativeArray) {
            NativeArray nativeArray = (NativeArray) scriptable;
            if (nativeArray.denseOnly) {
                int i = 0;
                for (int i2 = ((int) nativeArray.length) - 1; i < i2; i2--) {
                    Object obj = nativeArray.dense[i];
                    nativeArray.dense[i] = nativeArray.dense[i2];
                    nativeArray.dense[i2] = obj;
                    i += MAX_INSTANCE_ID;
                }
                return scriptable;
            }
        }
        long lengthProperty = getLengthProperty(context, scriptable);
        long j = lengthProperty / 2;
        for (long j2 = 0; j2 < j; j2++) {
            long j3 = (lengthProperty - j2) - 1;
            Object rawElem = getRawElem(scriptable, j2);
            setRawElem(context, scriptable, j2, getRawElem(scriptable, j3));
            setRawElem(context, scriptable, j3, rawElem);
        }
        return scriptable;
    }

    private static Object js_shift(Context context, Scriptable scriptable, Object[] objArr) {
        Object elem;
        if (scriptable instanceof NativeArray) {
            NativeArray nativeArray = (NativeArray) scriptable;
            if (nativeArray.denseOnly && nativeArray.length > 0) {
                nativeArray.length--;
                Object obj = nativeArray.dense[0];
                System.arraycopy(nativeArray.dense, MAX_INSTANCE_ID, nativeArray.dense, 0, (int) nativeArray.length);
                nativeArray.dense[(int) nativeArray.length] = NOT_FOUND;
                return obj == NOT_FOUND ? Undefined.instance : obj;
            }
        }
        long lengthProperty = getLengthProperty(context, scriptable);
        if (lengthProperty > 0) {
            lengthProperty--;
            elem = getElem(context, scriptable, 0);
            if (lengthProperty > 0) {
                for (long j = 1; j <= lengthProperty; j++) {
                    setRawElem(context, scriptable, j - 1, getRawElem(scriptable, j));
                }
            }
            deleteElem(scriptable, lengthProperty);
        } else {
            elem = Undefined.instance;
        }
        setLengthProperty(context, scriptable, lengthProperty);
        return elem;
    }

    private Scriptable js_slice(Context context, Scriptable scriptable, Object[] objArr) {
        long j;
        Scriptable newArray = context.newArray(ScriptableObject.getTopLevelScope(this), 0);
        long lengthProperty = getLengthProperty(context, scriptable);
        if (objArr.length == 0) {
            j = 0;
        } else {
            j = toSliceIndex(ScriptRuntime.toInteger(objArr[0]), lengthProperty);
            if (!(objArr.length == MAX_INSTANCE_ID || objArr[MAX_INSTANCE_ID] == Undefined.instance)) {
                lengthProperty = toSliceIndex(ScriptRuntime.toInteger(objArr[MAX_INSTANCE_ID]), lengthProperty);
            }
        }
        for (long j2 = j; j2 < lengthProperty; j2++) {
            Object rawElem = getRawElem(scriptable, j2);
            if (rawElem != NOT_FOUND) {
                defineElem(context, newArray, j2 - j, rawElem);
            }
        }
        setLengthProperty(context, newArray, Math.max(0, lengthProperty - j));
        return newArray;
    }

    private static Scriptable js_sort(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        Comparator anonymousClass2;
        if (objArr.length <= 0 || Undefined.instance == objArr[0]) {
            anonymousClass2 = new Comparator<Object>() {
                public int compare(Object obj, Object obj2) {
                    return obj == Scriptable.NOT_FOUND ? obj2 == Scriptable.NOT_FOUND ? 0 : NativeArray.MAX_INSTANCE_ID : obj2 == Scriptable.NOT_FOUND ? -1 : obj == Undefined.instance ? obj2 != Undefined.instance ? NativeArray.MAX_INSTANCE_ID : 0 : obj2 == Undefined.instance ? -1 : ScriptRuntime.toString(obj).compareTo(ScriptRuntime.toString(obj2));
                }
            };
        } else {
            final Callable valueFunctionAndThis = ScriptRuntime.getValueFunctionAndThis(objArr[0], context);
            final Scriptable lastStoredScriptable = ScriptRuntime.lastStoredScriptable(context);
            final Object[] objArr2 = new Object[Id_toString];
            final Context context2 = context;
            final Scriptable scriptable3 = scriptable;
            anonymousClass2 = new Comparator<Object>() {
                public int compare(Object obj, Object obj2) {
                    if (obj == Scriptable.NOT_FOUND) {
                        return obj2 == Scriptable.NOT_FOUND ? 0 : NativeArray.MAX_INSTANCE_ID;
                    } else {
                        if (obj2 == Scriptable.NOT_FOUND) {
                            return -1;
                        }
                        if (obj == Undefined.instance) {
                            return obj2 != Undefined.instance ? NativeArray.MAX_INSTANCE_ID : 0;
                        } else {
                            if (obj2 == Undefined.instance) {
                                return -1;
                            }
                            objArr2[0] = obj;
                            objArr2[NativeArray.MAX_INSTANCE_ID] = obj2;
                            double toNumber = ScriptRuntime.toNumber(valueFunctionAndThis.call(context2, scriptable3, lastStoredScriptable, objArr2));
                            return toNumber < 0.0d ? -1 : toNumber > 0.0d ? NativeArray.MAX_INSTANCE_ID : 0;
                        }
                    }
                }
            };
        }
        long lengthProperty = getLengthProperty(context, scriptable2);
        int i = (int) lengthProperty;
        if (lengthProperty != ((long) i)) {
            throw Context.reportRuntimeError1("msg.arraylength.too.big", String.valueOf(lengthProperty));
        }
        Object[] objArr3 = new Object[i];
        for (int i2 = 0; i2 != i; i2 += MAX_INSTANCE_ID) {
            objArr3[i2] = getRawElem(scriptable2, (long) i2);
        }
        Arrays.sort(objArr3, anonymousClass2);
        for (int i3 = 0; i3 < i; i3 += MAX_INSTANCE_ID) {
            setRawElem(context, scriptable2, (long) i3, objArr3[i3]);
        }
        return scriptable2;
    }

    private static Object js_splice(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        NativeArray nativeArray = null;
        boolean z = false;
        if (scriptable2 instanceof NativeArray) {
            NativeArray nativeArray2 = (NativeArray) scriptable2;
            nativeArray = nativeArray2;
            z = nativeArray2.denseOnly;
        }
        Scriptable topLevelScope = ScriptableObject.getTopLevelScope(scriptable);
        int length = objArr.length;
        if (length == 0) {
            return context.newArray(topLevelScope, 0);
        }
        long j;
        int i;
        Object newArray;
        long j2;
        long lengthProperty = getLengthProperty(context, scriptable2);
        long toSliceIndex = toSliceIndex(ScriptRuntime.toInteger(objArr[0]), lengthProperty);
        int i2 = length - 1;
        if (objArr.length == MAX_INSTANCE_ID) {
            j = lengthProperty - toSliceIndex;
            i = i2;
        } else {
            double toInteger = ScriptRuntime.toInteger(objArr[MAX_INSTANCE_ID]);
            long j3 = toInteger < 0.0d ? 0 : toInteger > ((double) (lengthProperty - toSliceIndex)) ? lengthProperty - toSliceIndex : (long) toInteger;
            j = j3;
            i = i2 - 1;
        }
        long j4 = toSliceIndex + j;
        if (j == 0) {
            newArray = context.getLanguageVersion() == Token.FOR ? Undefined.instance : context.newArray(topLevelScope, 0);
        } else if (j == 1 && context.getLanguageVersion() == Token.FOR) {
            newArray = getElem(context, scriptable2, toSliceIndex);
        } else if (z) {
            length = (int) (j4 - toSliceIndex);
            Object[] objArr2 = new Object[length];
            System.arraycopy(nativeArray.dense, (int) toSliceIndex, objArr2, 0, length);
            newArray = context.newArray(topLevelScope, objArr2);
        } else {
            newArray = context.newArray(topLevelScope, 0);
            for (j2 = toSliceIndex; j2 != j4; j2++) {
                Object rawElem = getRawElem(scriptable2, j2);
                if (rawElem != NOT_FOUND) {
                    setElem(context, newArray, j2 - toSliceIndex, rawElem);
                }
            }
            setLengthProperty(context, newArray, j4 - toSliceIndex);
        }
        j2 = ((long) i) - j;
        if (z && lengthProperty + j2 < 2147483647L && nativeArray.ensureCapacity((int) (lengthProperty + j2))) {
            System.arraycopy(nativeArray.dense, (int) j4, nativeArray.dense, (int) (((long) i) + toSliceIndex), (int) (lengthProperty - j4));
            if (i > 0) {
                System.arraycopy(objArr, Id_toString, nativeArray.dense, (int) toSliceIndex, i);
            }
            if (j2 < 0) {
                Arrays.fill(nativeArray.dense, (int) (lengthProperty + j2), (int) lengthProperty, NOT_FOUND);
            }
            nativeArray.length = lengthProperty + j2;
            return newArray;
        }
        long j5;
        if (j2 > 0) {
            for (j5 = lengthProperty - 1; j5 >= j4; j5--) {
                setRawElem(context, scriptable2, j5 + j2, getRawElem(scriptable2, j5));
            }
        } else if (j2 < 0) {
            for (j5 = j4; j5 < lengthProperty; j5++) {
                setRawElem(context, scriptable2, j5 + j2, getRawElem(scriptable2, j5));
            }
            for (j5 = lengthProperty + j2; j5 < lengthProperty; j5++) {
                deleteElem(scriptable2, j5);
            }
        }
        int length2 = objArr.length - i;
        for (int i3 = 0; i3 < i; i3 += MAX_INSTANCE_ID) {
            setElem(context, scriptable2, ((long) i3) + toSliceIndex, objArr[i3 + length2]);
        }
        setLengthProperty(context, scriptable2, lengthProperty + j2);
        return newArray;
    }

    private static Object js_unshift(Context context, Scriptable scriptable, Object[] objArr) {
        if (scriptable instanceof NativeArray) {
            NativeArray nativeArray = (NativeArray) scriptable;
            if (nativeArray.denseOnly && nativeArray.ensureCapacity(((int) nativeArray.length) + objArr.length)) {
                System.arraycopy(nativeArray.dense, 0, nativeArray.dense, objArr.length, (int) nativeArray.length);
                for (int i = 0; i < objArr.length; i += MAX_INSTANCE_ID) {
                    nativeArray.dense[i] = objArr[i];
                }
                nativeArray.length += (long) objArr.length;
                return ScriptRuntime.wrapNumber((double) nativeArray.length);
            }
        }
        long lengthProperty = getLengthProperty(context, scriptable);
        int length = objArr.length;
        if (objArr.length > 0) {
            if (lengthProperty > 0) {
                for (long j = lengthProperty - 1; j >= 0; j--) {
                    setRawElem(context, scriptable, ((long) length) + j, getRawElem(scriptable, j));
                }
            }
            for (int i2 = 0; i2 < objArr.length; i2 += MAX_INSTANCE_ID) {
                setElem(context, scriptable, (long) i2, objArr[i2]);
            }
        }
        return setLengthProperty(context, scriptable, ((long) objArr.length) + lengthProperty);
    }

    private static Object reduceMethod(Context context, int i, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        long lengthProperty = getLengthProperty(context, scriptable2);
        Object obj = objArr.length > 0 ? objArr[0] : Undefined.instance;
        if (obj == null || !(obj instanceof Function)) {
            throw ScriptRuntime.notFunctionError(obj);
        }
        Function function = (Function) obj;
        Scriptable topLevelScope = ScriptableObject.getTopLevelScope(function);
        Object obj2 = i == Id_reduce ? MAX_INSTANCE_ID : null;
        long j = 0;
        Object obj3 = objArr.length > MAX_INSTANCE_ID ? objArr[MAX_INSTANCE_ID] : Scriptable.NOT_FOUND;
        while (j < lengthProperty) {
            Object obj4;
            long j2 = obj2 != null ? j : (lengthProperty - 1) - j;
            Object rawElem = getRawElem(scriptable2, j2);
            if (rawElem == Scriptable.NOT_FOUND) {
                obj4 = obj3;
            } else if (obj3 == Scriptable.NOT_FOUND) {
                obj4 = rawElem;
            } else {
                Object[] objArr2 = new Object[Id_toSource];
                objArr2[0] = obj3;
                objArr2[MAX_INSTANCE_ID] = rawElem;
                objArr2[Id_toString] = Long.valueOf(j2);
                objArr2[Id_toLocaleString] = scriptable2;
                obj4 = function.call(context, topLevelScope, topLevelScope, objArr2);
            }
            j++;
            obj3 = obj4;
        }
        if (obj3 != Scriptable.NOT_FOUND) {
            return obj3;
        }
        throw ScriptRuntime.typeError0("msg.empty.array.reduce");
    }

    private static void setElem(Context context, Scriptable scriptable, long j, Object obj) {
        if (j > 2147483647L) {
            ScriptableObject.putProperty(scriptable, Long.toString(j), obj);
        } else {
            ScriptableObject.putProperty(scriptable, (int) j, obj);
        }
    }

    private void setLength(Object obj) {
        if ((this.lengthAttr & MAX_INSTANCE_ID) == 0) {
            double toNumber = ScriptRuntime.toNumber(obj);
            long toUint32 = ScriptRuntime.toUint32(toNumber);
            if (((double) toUint32) != toNumber) {
                throw ScriptRuntime.constructError("RangeError", ScriptRuntime.getMessage0("msg.arraylength.bad"));
            }
            if (this.denseOnly) {
                if (toUint32 < this.length) {
                    Arrays.fill(this.dense, (int) toUint32, this.dense.length, NOT_FOUND);
                    this.length = toUint32;
                    return;
                } else if (toUint32 >= 1431655764 || ((double) toUint32) >= ((double) this.length) * GROW_FACTOR || !ensureCapacity((int) toUint32)) {
                    this.denseOnly = false;
                } else {
                    this.length = toUint32;
                    return;
                }
            }
            if (toUint32 < this.length) {
                if (this.length - toUint32 > 4096) {
                    Object[] ids = getIds();
                    for (int i = 0; i < ids.length; i += MAX_INSTANCE_ID) {
                        Object obj2 = ids[i];
                        if (obj2 instanceof String) {
                            String str = (String) obj2;
                            if (toArrayIndex(str) >= toUint32) {
                                delete(str);
                            }
                        } else {
                            int intValue = ((Integer) obj2).intValue();
                            if (((long) intValue) >= toUint32) {
                                delete(intValue);
                            }
                        }
                    }
                } else {
                    for (long j = toUint32; j < this.length; j++) {
                        deleteElem(this, j);
                    }
                }
            }
            this.length = toUint32;
        }
    }

    private static Object setLengthProperty(Context context, Scriptable scriptable, long j) {
        Object wrapNumber = ScriptRuntime.wrapNumber((double) j);
        ScriptableObject.putProperty(scriptable, Name.LENGTH, wrapNumber);
        return wrapNumber;
    }

    static void setMaximumInitialCapacity(int i) {
        maximumInitialCapacity = i;
    }

    private static void setRawElem(Context context, Scriptable scriptable, long j, Object obj) {
        if (obj == NOT_FOUND) {
            deleteElem(scriptable, j);
        } else {
            setElem(context, scriptable, j, obj);
        }
    }

    private static long toArrayIndex(double d) {
        if (d == d) {
            long toUint32 = ScriptRuntime.toUint32(d);
            if (((double) toUint32) == d && toUint32 != 4294967295L) {
                return toUint32;
            }
        }
        return -1;
    }

    private static long toArrayIndex(Object obj) {
        return obj instanceof String ? toArrayIndex((String) obj) : obj instanceof Number ? toArrayIndex(((Number) obj).doubleValue()) : -1;
    }

    private static long toArrayIndex(String str) {
        long toArrayIndex = toArrayIndex(ScriptRuntime.toNumber(str));
        return Long.toString(toArrayIndex).equals(str) ? toArrayIndex : -1;
    }

    private static int toDenseIndex(Object obj) {
        long toArrayIndex = toArrayIndex(obj);
        return (0 > toArrayIndex || toArrayIndex >= 2147483647L) ? -1 : (int) toArrayIndex;
    }

    private static long toSliceIndex(double d, long j) {
        return d < 0.0d ? ((double) j) + d < 0.0d ? 0 : (long) (((double) j) + d) : d <= ((double) j) ? (long) d : j;
    }

    private static String toStringHelper(Context context, Scriptable scriptable, Scriptable scriptable2, boolean z, boolean z2) {
        String str;
        boolean z3;
        Object obj;
        Object obj2;
        long lengthProperty = getLengthProperty(context, scriptable2);
        StringBuilder stringBuilder = new StringBuilder(EnchantType.flintAndSteel);
        if (z) {
            stringBuilder.append('[');
            str = ", ";
        } else {
            str = ",";
        }
        if (context.iterating == null) {
            z3 = false;
            context.iterating = new ObjToIntMap(31);
            obj = MAX_INSTANCE_ID;
        } else {
            z3 = context.iterating.has(scriptable2);
            obj = null;
        }
        long j;
        if (z3) {
            j = 0;
            obj2 = null;
        } else {
            context.iterating.put(scriptable2, 0);
            obj2 = (!z || context.getLanguageVersion() < Token.TO_OBJECT) ? MAX_INSTANCE_ID : null;
            Object obj3 = null;
            long j2 = 0;
            while (j2 < lengthProperty) {
                if (j2 > 0) {
                    stringBuilder.append(str);
                }
                obj3 = getRawElem(scriptable2, j2);
                if (obj3 == NOT_FOUND || (obj2 != null && (obj3 == null || obj3 == Undefined.instance))) {
                    obj3 = null;
                } else if (z) {
                    try {
                        stringBuilder.append(ScriptRuntime.uneval(context, scriptable, obj3));
                        obj3 = MAX_INSTANCE_ID;
                    } catch (Throwable th) {
                        if (obj != null) {
                            context.iterating = null;
                        }
                    }
                } else if (obj3 instanceof String) {
                    String str2 = (String) obj3;
                    if (z) {
                        stringBuilder.append('\"');
                        stringBuilder.append(ScriptRuntime.escapeString(str2));
                        stringBuilder.append('\"');
                    } else {
                        stringBuilder.append(str2);
                    }
                    obj3 = MAX_INSTANCE_ID;
                } else {
                    if (z2) {
                        obj3 = ScriptRuntime.getPropFunctionAndThis(obj3, "toLocaleString", context, scriptable).call(context, scriptable, ScriptRuntime.lastStoredScriptable(context), ScriptRuntime.emptyArgs);
                    }
                    stringBuilder.append(ScriptRuntime.toString(obj3));
                    obj3 = MAX_INSTANCE_ID;
                }
                j2++;
            }
            obj2 = obj3;
            j = j2;
        }
        if (obj != null) {
            context.iterating = null;
        }
        if (z) {
            if (obj2 != null || r0 <= 0) {
                stringBuilder.append(']');
            } else {
                stringBuilder.append(", ]");
            }
        }
        return stringBuilder.toString();
    }

    public void add(int i, Object obj) {
        throw new UnsupportedOperationException();
    }

    public boolean add(Object obj) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int i, Collection collection) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection collection) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object obj) {
        return indexOf(obj) > -1;
    }

    public boolean containsAll(Collection collection) {
        for (Object contains : collection) {
            if (!contains(contains)) {
                return false;
            }
        }
        return true;
    }

    protected void defineOwnProperty(Context context, Object obj, ScriptableObject scriptableObject, boolean z) {
        int i = 0;
        if (this.dense != null) {
            Object[] objArr = this.dense;
            this.dense = null;
            this.denseOnly = false;
            while (i < objArr.length) {
                if (objArr[i] != NOT_FOUND) {
                    put(i, (Scriptable) this, objArr[i]);
                }
                i += MAX_INSTANCE_ID;
            }
        }
        long toArrayIndex = toArrayIndex(obj);
        if (toArrayIndex >= this.length) {
            this.length = toArrayIndex + 1;
        }
        super.defineOwnProperty(context, obj, scriptableObject, z);
    }

    public void delete(int i) {
        if (this.dense == null || i < 0 || i >= this.dense.length || isSealed() || (!this.denseOnly && isGetterOrSetter(null, i, true))) {
            super.delete(i);
        } else {
            this.dense[i] = NOT_FOUND;
        }
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        boolean z = true;
        boolean z2 = false;
        if (!idFunctionObject.hasTag(ARRAY_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        Object[] objArr2 = objArr;
        Scriptable scriptable3 = scriptable2;
        while (true) {
            switch (methodId) {
                case ConstructorId_isArray /*-26*/:
                    if (objArr2.length > 0 && js_isArray(objArr2[0])) {
                        z2 = true;
                    }
                    return Boolean.valueOf(z2);
                case ConstructorId_reduceRight /*-25*/:
                case ConstructorId_reduce /*-24*/:
                case ConstructorId_findIndex /*-23*/:
                case ConstructorId_find /*-22*/:
                case ConstructorId_some /*-21*/:
                case ConstructorId_map /*-20*/:
                case ConstructorId_forEach /*-19*/:
                case ConstructorId_filter /*-18*/:
                case ConstructorId_every /*-17*/:
                case ConstructorId_lastIndexOf /*-16*/:
                case ConstructorId_indexOf /*-15*/:
                case ConstructorId_slice /*-14*/:
                case ConstructorId_concat /*-13*/:
                case ConstructorId_splice /*-12*/:
                case ConstructorId_unshift /*-11*/:
                case ConstructorId_shift /*-10*/:
                case ConstructorId_pop /*-9*/:
                case ConstructorId_push /*-8*/:
                case ConstructorId_sort /*-7*/:
                case ConstructorId_reverse /*-6*/:
                case ConstructorId_join /*-5*/:
                    Object[] objArr3;
                    Scriptable scriptable4;
                    if (objArr2.length > 0) {
                        Scriptable toObject = ScriptRuntime.toObject(context, scriptable, objArr2[0]);
                        Object[] objArr4 = new Object[(objArr2.length - 1)];
                        for (int i = 0; i < objArr4.length; i += MAX_INSTANCE_ID) {
                            objArr4[i] = objArr2[i + MAX_INSTANCE_ID];
                        }
                        objArr3 = objArr4;
                        scriptable4 = toObject;
                    } else {
                        scriptable4 = scriptable3;
                        objArr3 = objArr2;
                    }
                    methodId = -methodId;
                    objArr2 = objArr3;
                    scriptable3 = scriptable4;
                case MAX_INSTANCE_ID /*1*/:
                    if (scriptable3 != null) {
                        z = false;
                    }
                    return !z ? idFunctionObject.construct(context, scriptable, objArr2) : jsConstructor(context, scriptable, objArr2);
                case Id_toString /*2*/:
                    return toStringHelper(context, scriptable, scriptable3, context.hasFeature(Id_toSource), false);
                case Id_toLocaleString /*3*/:
                    return toStringHelper(context, scriptable, scriptable3, false, true);
                case Id_toSource /*4*/:
                    return toStringHelper(context, scriptable, scriptable3, true, false);
                case Id_join /*5*/:
                    return js_join(context, scriptable3, objArr2);
                case Id_reverse /*6*/:
                    return js_reverse(context, scriptable3, objArr2);
                case Id_sort /*7*/:
                    return js_sort(context, scriptable, scriptable3, objArr2);
                case Id_push /*8*/:
                    return js_push(context, scriptable3, objArr2);
                case Id_pop /*9*/:
                    return js_pop(context, scriptable3, objArr2);
                case Id_shift /*10*/:
                    return js_shift(context, scriptable3, objArr2);
                case Id_unshift /*11*/:
                    return js_unshift(context, scriptable3, objArr2);
                case Id_splice /*12*/:
                    return js_splice(context, scriptable, scriptable3, objArr2);
                case Id_concat /*13*/:
                    return js_concat(context, scriptable, scriptable3, objArr2);
                case Id_slice /*14*/:
                    return js_slice(context, scriptable3, objArr2);
                case Id_indexOf /*15*/:
                    return js_indexOf(context, scriptable3, objArr2);
                case Id_lastIndexOf /*16*/:
                    return js_lastIndexOf(context, scriptable3, objArr2);
                case Id_every /*17*/:
                case Id_filter /*18*/:
                case Id_forEach /*19*/:
                case Id_map /*20*/:
                case Id_some /*21*/:
                case Id_find /*22*/:
                case Id_findIndex /*23*/:
                    return iterativeMethod(context, idFunctionObject, scriptable, scriptable3, objArr2);
                case Id_reduce /*24*/:
                case Id_reduceRight /*25*/:
                    return reduceMethod(context, methodId, scriptable, scriptable3, objArr2);
                case MAX_PROTOTYPE_ID /*26*/:
                    return new NativeArrayIterator(scriptable, scriptable3);
                default:
                    throw new IllegalArgumentException("Array.prototype has no method: " + idFunctionObject.getFunctionName());
            }
        }
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_join, "join", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_reverse, "reverse", 0);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_sort, "sort", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_push, "push", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_pop, "pop", 0);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_shift, "shift", 0);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_unshift, "unshift", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_splice, "splice", Id_toString);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_concat, "concat", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_slice, "slice", Id_toString);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_indexOf, "indexOf", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_lastIndexOf, "lastIndexOf", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_every, "every", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_filter, "filter", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_forEach, "forEach", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_map, "map", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_some, "some", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_find, "find", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_findIndex, "findIndex", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_reduce, "reduce", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_reduceRight, "reduceRight", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, ARRAY_TAG, ConstructorId_isArray, "isArray", MAX_INSTANCE_ID);
        super.fillConstructorProperties(idFunctionObject);
    }

    protected int findInstanceIdInfo(String str) {
        return str.equals(Name.LENGTH) ? IdScriptableObject.instanceIdInfo(this.lengthAttr, MAX_INSTANCE_ID) : super.findInstanceIdInfo(str);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findPrototypeId(java.lang.String r9) {
        /*
        r8 = this;
        r4 = 3;
        r6 = 112; // 0x70 float:1.57E-43 double:5.53E-322;
        r2 = 2;
        r3 = 1;
        r0 = 0;
        r1 = 0;
        r5 = r9.length();
        switch(r5) {
            case 3: goto L_0x001b;
            case 4: goto L_0x0047;
            case 5: goto L_0x0077;
            case 6: goto L_0x00a1;
            case 7: goto L_0x00d0;
            case 8: goto L_0x00fe;
            case 9: goto L_0x0119;
            case 10: goto L_0x0122;
            case 11: goto L_0x012b;
            case 12: goto L_0x000e;
            case 13: goto L_0x000e;
            case 14: goto L_0x0153;
            default: goto L_0x000e;
        };
    L_0x000e:
        r2 = r1;
        r1 = r0;
    L_0x0010:
        if (r2 == 0) goto L_0x0159;
    L_0x0012:
        if (r2 == r9) goto L_0x0159;
    L_0x0014:
        r2 = r2.equals(r9);
        if (r2 != 0) goto L_0x0159;
    L_0x001a:
        return r0;
    L_0x001b:
        r4 = r9.charAt(r0);
        r5 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r4 != r5) goto L_0x0034;
    L_0x0023:
        r2 = r9.charAt(r2);
        if (r2 != r6) goto L_0x000e;
    L_0x0029:
        r2 = r9.charAt(r3);
        r3 = 97;
        if (r2 != r3) goto L_0x000e;
    L_0x0031:
        r0 = 20;
        goto L_0x001a;
    L_0x0034:
        if (r4 != r6) goto L_0x000e;
    L_0x0036:
        r2 = r9.charAt(r2);
        if (r2 != r6) goto L_0x000e;
    L_0x003c:
        r2 = r9.charAt(r3);
        r3 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r2 != r3) goto L_0x000e;
    L_0x0044:
        r0 = 9;
        goto L_0x001a;
    L_0x0047:
        r2 = r9.charAt(r2);
        switch(r2) {
            case 105: goto L_0x0051;
            case 106: goto L_0x004e;
            case 107: goto L_0x004e;
            case 108: goto L_0x004e;
            case 109: goto L_0x0058;
            case 110: goto L_0x0060;
            case 111: goto L_0x004e;
            case 112: goto L_0x004e;
            case 113: goto L_0x004e;
            case 114: goto L_0x0068;
            case 115: goto L_0x006f;
            default: goto L_0x004e;
        };
    L_0x004e:
        r2 = r1;
        r1 = r0;
        goto L_0x0010;
    L_0x0051:
        r1 = "join";
        r2 = 5;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0058:
        r1 = "some";
        r2 = 21;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0060:
        r1 = "find";
        r2 = 22;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0068:
        r1 = "sort";
        r2 = 7;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x006f:
        r1 = "push";
        r2 = 8;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0077:
        r2 = r9.charAt(r3);
        r3 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r2 != r3) goto L_0x0087;
    L_0x007f:
        r1 = "shift";
        r2 = 10;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0087:
        r3 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r2 != r3) goto L_0x0094;
    L_0x008b:
        r1 = "slice";
        r2 = 14;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0094:
        r3 = 118; // 0x76 float:1.65E-43 double:5.83E-322;
        if (r2 != r3) goto L_0x000e;
    L_0x0098:
        r1 = "every";
        r2 = 17;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00a1:
        r2 = r9.charAt(r0);
        switch(r2) {
            case 99: goto L_0x00ac;
            case 102: goto L_0x00b5;
            case 114: goto L_0x00be;
            case 115: goto L_0x00c7;
            default: goto L_0x00a8;
        };
    L_0x00a8:
        r2 = r1;
        r1 = r0;
        goto L_0x0010;
    L_0x00ac:
        r1 = "concat";
        r2 = 13;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00b5:
        r1 = "filter";
        r2 = 18;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00be:
        r1 = "reduce";
        r2 = 24;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00c7:
        r1 = "splice";
        r2 = 12;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00d0:
        r2 = r9.charAt(r0);
        switch(r2) {
            case 102: goto L_0x00db;
            case 105: goto L_0x00e4;
            case 114: goto L_0x00ed;
            case 117: goto L_0x00f5;
            default: goto L_0x00d7;
        };
    L_0x00d7:
        r2 = r1;
        r1 = r0;
        goto L_0x0010;
    L_0x00db:
        r1 = "forEach";
        r2 = 19;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00e4:
        r1 = "indexOf";
        r2 = 15;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00ed:
        r1 = "reverse";
        r2 = 6;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00f5:
        r1 = "unshift";
        r2 = 11;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x00fe:
        r3 = r9.charAt(r4);
        r4 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r3 != r4) goto L_0x010e;
    L_0x0106:
        r1 = "toSource";
        r2 = 4;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x010e:
        r4 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r3 != r4) goto L_0x000e;
    L_0x0112:
        r1 = "toString";
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0119:
        r1 = "findIndex";
        r2 = 23;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0122:
        r1 = "@@iterator";
        r2 = 26;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x012b:
        r2 = r9.charAt(r0);
        r4 = 99;
        if (r2 != r4) goto L_0x0139;
    L_0x0133:
        r1 = "constructor";
        r2 = r1;
        r1 = r3;
        goto L_0x0010;
    L_0x0139:
        r3 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r2 != r3) goto L_0x0146;
    L_0x013d:
        r1 = "lastIndexOf";
        r2 = 16;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0146:
        r3 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        if (r2 != r3) goto L_0x000e;
    L_0x014a:
        r1 = "reduceRight";
        r2 = 25;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0010;
    L_0x0153:
        r1 = "toLocaleString";
        r2 = r1;
        r1 = r4;
        goto L_0x0010;
    L_0x0159:
        r0 = r1;
        goto L_0x001a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeArray.findPrototypeId(java.lang.String):int");
    }

    public Object get(int i) {
        return get((long) i);
    }

    public Object get(int i, Scriptable scriptable) {
        return (this.denseOnly || !isGetterOrSetter(null, i, false)) ? (this.dense == null || i < 0 || i >= this.dense.length) ? super.get(i, scriptable) : this.dense[i] : super.get(i, scriptable);
    }

    public Object get(long j) {
        if (j < 0 || j >= this.length) {
            throw new IndexOutOfBoundsException();
        }
        Object rawElem = getRawElem(this, j);
        return (rawElem == Scriptable.NOT_FOUND || rawElem == Undefined.instance) ? null : rawElem instanceof Wrapper ? ((Wrapper) rawElem).unwrap() : rawElem;
    }

    public Object[] getAllIds() {
        Set linkedHashSet = new LinkedHashSet(Arrays.asList(getIds()));
        linkedHashSet.addAll(Arrays.asList(super.getAllIds()));
        return linkedHashSet.toArray();
    }

    public int getAttributes(int i) {
        return (this.dense == null || i < 0 || i >= this.dense.length || this.dense[i] == NOT_FOUND) ? super.getAttributes(i) : 0;
    }

    public String getClassName() {
        return "Array";
    }

    public Object getDefaultValue(Class<?> cls) {
        return (cls == ScriptRuntime.NumberClass && Context.getContext().getLanguageVersion() == Token.FOR) ? Long.valueOf(this.length) : super.getDefaultValue(cls);
    }

    public Object[] getIds() {
        Object ids = super.getIds();
        if (this.dense == null) {
            return ids;
        }
        int length = this.dense.length;
        long j = this.length;
        int i = ((long) length) > j ? (int) j : length;
        if (i == 0) {
            return ids;
        }
        Object obj;
        int length2 = ids.length;
        Object obj2 = new Object[(i + length2)];
        length = 0;
        for (int i2 = 0; i2 != i; i2 += MAX_INSTANCE_ID) {
            if (this.dense[i2] != NOT_FOUND) {
                obj2[length] = Integer.valueOf(i2);
                length += MAX_INSTANCE_ID;
            }
        }
        if (length != i) {
            obj = new Object[(length + length2)];
            System.arraycopy(obj2, 0, obj, 0, length);
        } else {
            obj = obj2;
        }
        System.arraycopy(ids, 0, obj, length, length2);
        return obj;
    }

    public Integer[] getIndexIds() {
        Object[] ids = getIds();
        List arrayList = new ArrayList(ids.length);
        int length = ids.length;
        for (int i = 0; i < length; i += MAX_INSTANCE_ID) {
            Object obj = ids[i];
            int toInt32 = ScriptRuntime.toInt32(obj);
            if (toInt32 >= 0 && ScriptRuntime.toString((double) toInt32).equals(ScriptRuntime.toString(obj))) {
                arrayList.add(Integer.valueOf(toInt32));
            }
        }
        return (Integer[]) arrayList.toArray(new Integer[arrayList.size()]);
    }

    protected String getInstanceIdName(int i) {
        return i == MAX_INSTANCE_ID ? Name.LENGTH : super.getInstanceIdName(i);
    }

    protected Object getInstanceIdValue(int i) {
        return i == MAX_INSTANCE_ID ? ScriptRuntime.wrapNumber((double) this.length) : super.getInstanceIdValue(i);
    }

    public long getLength() {
        return this.length;
    }

    protected int getMaxInstanceId() {
        return MAX_INSTANCE_ID;
    }

    protected ScriptableObject getOwnPropertyDescriptor(Context context, Object obj) {
        if (this.dense != null) {
            int toDenseIndex = toDenseIndex(obj);
            if (toDenseIndex >= 0 && toDenseIndex < this.dense.length && this.dense[toDenseIndex] != NOT_FOUND) {
                return defaultIndexPropertyDescriptor(this.dense[toDenseIndex]);
            }
        }
        return super.getOwnPropertyDescriptor(context, obj);
    }

    public boolean has(int i, Scriptable scriptable) {
        return (this.denseOnly || !isGetterOrSetter(null, i, false)) ? (this.dense == null || i < 0 || i >= this.dense.length) ? super.has(i, scriptable) : this.dense[i] != NOT_FOUND : super.has(i, scriptable);
    }

    public int indexOf(Object obj) {
        int i = 0;
        long j = this.length;
        if (j > 2147483647L) {
            throw new IllegalStateException();
        }
        int i2 = (int) j;
        if (obj == null) {
            while (i < i2) {
                if (get(i) == null) {
                    return i;
                }
                i += MAX_INSTANCE_ID;
            }
        } else {
            while (i < i2) {
                if (obj.equals(get(i))) {
                    return i;
                }
                i += MAX_INSTANCE_ID;
            }
        }
        return -1;
    }

    protected void initPrototypeId(int i) {
        String str;
        int i2 = MAX_INSTANCE_ID;
        String str2 = null;
        switch (i) {
            case MAX_INSTANCE_ID /*1*/:
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
            case Id_toSource /*4*/:
                str = "toSource";
                i2 = 0;
                break;
            case Id_join /*5*/:
                str = "join";
                break;
            case Id_reverse /*6*/:
                str = "reverse";
                i2 = 0;
                break;
            case Id_sort /*7*/:
                str = "sort";
                break;
            case Id_push /*8*/:
                str = "push";
                break;
            case Id_pop /*9*/:
                str = "pop";
                i2 = 0;
                break;
            case Id_shift /*10*/:
                str = "shift";
                i2 = 0;
                break;
            case Id_unshift /*11*/:
                str = "unshift";
                break;
            case Id_splice /*12*/:
                str = "splice";
                i2 = Id_toString;
                break;
            case Id_concat /*13*/:
                str = "concat";
                break;
            case Id_slice /*14*/:
                str = "slice";
                i2 = Id_toString;
                break;
            case Id_indexOf /*15*/:
                str = "indexOf";
                break;
            case Id_lastIndexOf /*16*/:
                str = "lastIndexOf";
                break;
            case Id_every /*17*/:
                str = "every";
                break;
            case Id_filter /*18*/:
                str = "filter";
                break;
            case Id_forEach /*19*/:
                str = "forEach";
                break;
            case Id_map /*20*/:
                str = "map";
                break;
            case Id_some /*21*/:
                str = "some";
                break;
            case Id_find /*22*/:
                str = "find";
                break;
            case Id_findIndex /*23*/:
                str = "findIndex";
                break;
            case Id_reduce /*24*/:
                str = "reduce";
                break;
            case Id_reduceRight /*25*/:
                str = "reduceRight";
                break;
            case MAX_PROTOTYPE_ID /*26*/:
                str = NativeSymbol.ITERATOR_PROPERTY;
                str2 = "[Symbol.iterator]";
                i2 = 0;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(ARRAY_TAG, i, str, str2, i2);
    }

    public boolean isEmpty() {
        return this.length == 0;
    }

    public Iterator iterator() {
        return listIterator(0);
    }

    @Deprecated
    public long jsGet_length() {
        return getLength();
    }

    public int lastIndexOf(Object obj) {
        long j = this.length;
        if (j > 2147483647L) {
            throw new IllegalStateException();
        }
        int i = (int) j;
        if (obj == null) {
            for (i--; i >= 0; i--) {
                if (get(i) == null) {
                    return i;
                }
            }
        } else {
            for (i--; i >= 0; i--) {
                if (obj.equals(get(i))) {
                    return i;
                }
            }
        }
        return -1;
    }

    public ListIterator listIterator() {
        return listIterator(0);
    }

    public ListIterator listIterator(final int i) {
        long j = this.length;
        if (j > 2147483647L) {
            throw new IllegalStateException();
        }
        final int i2 = (int) j;
        if (i >= 0 && i <= i2) {
            return new ListIterator() {
                int cursor = i;

                public void add(Object obj) {
                    throw new UnsupportedOperationException();
                }

                public boolean hasNext() {
                    return this.cursor < i2;
                }

                public boolean hasPrevious() {
                    return this.cursor > 0;
                }

                public Object next() {
                    if (this.cursor == i2) {
                        throw new NoSuchElementException();
                    }
                    NativeArray nativeArray = NativeArray.this;
                    int i = this.cursor;
                    this.cursor = i + NativeArray.MAX_INSTANCE_ID;
                    return nativeArray.get(i);
                }

                public int nextIndex() {
                    return this.cursor;
                }

                public Object previous() {
                    if (this.cursor == 0) {
                        throw new NoSuchElementException();
                    }
                    NativeArray nativeArray = NativeArray.this;
                    int i = this.cursor - 1;
                    this.cursor = i;
                    return nativeArray.get(i);
                }

                public int previousIndex() {
                    return this.cursor - 1;
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public void set(Object obj) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        throw new IndexOutOfBoundsException("Index: " + i);
    }

    public void put(int i, Scriptable scriptable, Object obj) {
        if (scriptable == this && !isSealed() && this.dense != null && i >= 0 && (this.denseOnly || !isGetterOrSetter(null, i, true))) {
            if (!isExtensible() && this.length <= ((long) i)) {
                return;
            }
            if (i < this.dense.length) {
                this.dense[i] = obj;
                if (this.length <= ((long) i)) {
                    this.length = ((long) i) + 1;
                    return;
                }
                return;
            } else if (this.denseOnly && ((double) i) < ((double) this.dense.length) * GROW_FACTOR && ensureCapacity(i + MAX_INSTANCE_ID)) {
                this.dense[i] = obj;
                this.length = ((long) i) + 1;
                return;
            } else {
                this.denseOnly = false;
            }
        }
        super.put(i, scriptable, obj);
        if (scriptable == this && (this.lengthAttr & MAX_INSTANCE_ID) == 0 && this.length <= ((long) i)) {
            this.length = ((long) i) + 1;
        }
    }

    public void put(String str, Scriptable scriptable, Object obj) {
        super.put(str, scriptable, obj);
        if (scriptable == this) {
            long toArrayIndex = toArrayIndex(str);
            if (toArrayIndex >= this.length) {
                this.length = toArrayIndex + 1;
                this.denseOnly = false;
            }
        }
    }

    public Object remove(int i) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object obj) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection collection) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection collection) {
        throw new UnsupportedOperationException();
    }

    public Object set(int i, Object obj) {
        throw new UnsupportedOperationException();
    }

    void setDenseOnly(boolean z) {
        if (!z || this.denseOnly) {
            this.denseOnly = z;
            return;
        }
        throw new IllegalArgumentException();
    }

    protected void setInstanceIdAttributes(int i, int i2) {
        if (i == MAX_INSTANCE_ID) {
            this.lengthAttr = i2;
        }
    }

    protected void setInstanceIdValue(int i, Object obj) {
        if (i == MAX_INSTANCE_ID) {
            setLength(obj);
        } else {
            super.setInstanceIdValue(i, obj);
        }
    }

    public int size() {
        long j = this.length;
        if (j <= 2147483647L) {
            return (int) j;
        }
        throw new IllegalStateException();
    }

    public List subList(int i, int i2) {
        throw new UnsupportedOperationException();
    }

    public Object[] toArray() {
        return toArray(ScriptRuntime.emptyArgs);
    }

    public Object[] toArray(Object[] objArr) {
        long j = this.length;
        if (j > 2147483647L) {
            throw new IllegalStateException();
        }
        int i = (int) j;
        if (objArr.length < i) {
            objArr = (Object[]) Array.newInstance(objArr.getClass().getComponentType(), i);
        }
        for (int i2 = 0; i2 < i; i2 += MAX_INSTANCE_ID) {
            objArr[i2] = get(i2);
        }
        return objArr;
    }
}
