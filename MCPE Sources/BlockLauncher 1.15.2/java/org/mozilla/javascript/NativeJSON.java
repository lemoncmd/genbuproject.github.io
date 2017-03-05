package org.mozilla.javascript;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.json.JsonParser;
import org.mozilla.javascript.json.JsonParser.ParseException;

public final class NativeJSON extends IdScriptableObject {
    private static final int Id_parse = 2;
    private static final int Id_stringify = 3;
    private static final int Id_toSource = 1;
    private static final Object JSON_TAG = "JSON";
    private static final int LAST_METHOD_ID = 3;
    private static final int MAX_ID = 3;
    private static final int MAX_STRINGIFY_GAP_LENGTH = 10;
    static final long serialVersionUID = -4567599697595654984L;

    private static class StringifyState {
        Context cx;
        String gap;
        String indent;
        List<Object> propertyList;
        Callable replacer;
        Scriptable scope;
        Object space;
        Stack<Scriptable> stack = new Stack();

        StringifyState(Context context, Scriptable scriptable, String str, String str2, Callable callable, List<Object> list, Object obj) {
            this.cx = context;
            this.scope = scriptable;
            this.indent = str;
            this.gap = str2;
            this.replacer = callable;
            this.propertyList = list;
            this.space = obj;
        }
    }

    private NativeJSON() {
    }

    static void init(Scriptable scriptable, boolean z) {
        NativeJSON nativeJSON = new NativeJSON();
        nativeJSON.activatePrototypeMap(MAX_ID);
        nativeJSON.setPrototype(ScriptableObject.getObjectPrototype(scriptable));
        nativeJSON.setParentScope(scriptable);
        if (z) {
            nativeJSON.sealObject();
        }
        ScriptableObject.defineProperty(scriptable, "JSON", nativeJSON, Id_parse);
    }

    private static String ja(NativeArray nativeArray, StringifyState stringifyState) {
        if (stringifyState.stack.search(nativeArray) != -1) {
            throw ScriptRuntime.typeError0("msg.cyclic.value");
        }
        stringifyState.stack.push(nativeArray);
        String str = stringifyState.indent;
        stringifyState.indent += stringifyState.gap;
        Collection linkedList = new LinkedList();
        long length = nativeArray.getLength();
        long j = 0;
        while (j < length) {
            Object str2 = j > 2147483647L ? str(Long.toString(j), nativeArray, stringifyState) : str(Integer.valueOf((int) j), nativeArray, stringifyState);
            if (str2 == Undefined.instance) {
                linkedList.add("null");
            } else {
                linkedList.add(str2);
            }
            j = 1 + j;
        }
        String str3 = linkedList.isEmpty() ? "[]" : stringifyState.gap.length() == 0 ? '[' + join(linkedList, ",") + ']' : "[\n" + stringifyState.indent + join(linkedList, ",\n" + stringifyState.indent) + '\n' + str + ']';
        stringifyState.stack.pop();
        stringifyState.indent = str;
        return str3;
    }

    private static String jo(Scriptable scriptable, StringifyState stringifyState) {
        if (stringifyState.stack.search(scriptable) != -1) {
            throw ScriptRuntime.typeError0("msg.cyclic.value");
        }
        stringifyState.stack.push(scriptable);
        String str = stringifyState.indent;
        stringifyState.indent += stringifyState.gap;
        Object[] toArray = stringifyState.propertyList != null ? stringifyState.propertyList.toArray() : scriptable.getIds();
        Collection linkedList = new LinkedList();
        int length = toArray.length;
        for (int i = 0; i < length; i += Id_toSource) {
            Object obj = toArray[i];
            Object str2 = str(obj, scriptable, stringifyState);
            if (str2 != Undefined.instance) {
                String str3 = quote(obj.toString()) + ":";
                if (stringifyState.gap.length() > 0) {
                    str3 = str3 + " ";
                }
                linkedList.add(str3 + str2);
            }
        }
        String str4 = linkedList.isEmpty() ? "{}" : stringifyState.gap.length() == 0 ? '{' + join(linkedList, ",") + '}' : "{\n" + stringifyState.indent + join(linkedList, ",\n" + stringifyState.indent) + '\n' + str + '}';
        stringifyState.stack.pop();
        stringifyState.indent = str;
        return str4;
    }

    private static String join(Collection<Object> collection, String str) {
        if (collection == null || collection.isEmpty()) {
            return BuildConfig.FLAVOR;
        }
        Iterator it = collection.iterator();
        if (!it.hasNext()) {
            return BuildConfig.FLAVOR;
        }
        StringBuilder stringBuilder = new StringBuilder(it.next().toString());
        while (it.hasNext()) {
            stringBuilder.append(str).append(it.next().toString());
        }
        return stringBuilder.toString();
    }

    private static Object parse(Context context, Scriptable scriptable, String str) {
        try {
            return new JsonParser(context, scriptable).parseValue(str);
        } catch (ParseException e) {
            throw ScriptRuntime.constructError("SyntaxError", e.getMessage());
        }
    }

    public static Object parse(Context context, Scriptable scriptable, String str, Callable callable) {
        Object parse = parse(context, scriptable, str);
        Scriptable newObject = context.newObject(scriptable);
        newObject.put(BuildConfig.FLAVOR, newObject, parse);
        return walk(context, scriptable, callable, newObject, BuildConfig.FLAVOR);
    }

    private static String quote(String str) {
        StringBuilder stringBuilder = new StringBuilder(str.length() + Id_parse);
        stringBuilder.append('\"');
        int length = str.length();
        for (int i = 0; i < length; i += Id_toSource) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case Token.SETNAME /*8*/:
                    stringBuilder.append("\\b");
                    break;
                case Token.BITOR /*9*/:
                    stringBuilder.append("\\t");
                    break;
                case MAX_STRINGIFY_GAP_LENGTH /*10*/:
                    stringBuilder.append("\\n");
                    break;
                case Token.EQ /*12*/:
                    stringBuilder.append("\\f");
                    break;
                case Token.NE /*13*/:
                    stringBuilder.append("\\r");
                    break;
                case Token.GETPROPNOWARN /*34*/:
                    stringBuilder.append("\\\"");
                    break;
                case Token.ASSIGN_BITOR /*92*/:
                    stringBuilder.append("\\\\");
                    break;
                default:
                    if (charAt >= ' ') {
                        stringBuilder.append(charAt);
                        break;
                    }
                    stringBuilder.append("\\u");
                    Object[] objArr = new Object[Id_toSource];
                    objArr[0] = Integer.valueOf(charAt);
                    stringBuilder.append(String.format("%04x", objArr));
                    break;
            }
        }
        stringBuilder.append('\"');
        return stringBuilder.toString();
    }

    private static String repeat(char c, int i) {
        char[] cArr = new char[i];
        Arrays.fill(cArr, c);
        return new String(cArr);
    }

    private static Object str(Object obj, Scriptable scriptable, StringifyState stringifyState) {
        Object call;
        Object property = obj instanceof String ? ScriptableObject.getProperty(scriptable, (String) obj) : ScriptableObject.getProperty(scriptable, ((Number) obj).intValue());
        if ((property instanceof Scriptable) && (ScriptableObject.getProperty((Scriptable) property, "toJSON") instanceof Callable)) {
            Object[] objArr = new Object[Id_toSource];
            objArr[0] = obj;
            property = ScriptableObject.callMethod(stringifyState.cx, (Scriptable) property, "toJSON", objArr);
        }
        if (stringifyState.replacer != null) {
            Callable callable = stringifyState.replacer;
            Context context = stringifyState.cx;
            Scriptable scriptable2 = stringifyState.scope;
            Object[] objArr2 = new Object[Id_parse];
            objArr2[0] = obj;
            objArr2[Id_toSource] = property;
            call = callable.call(context, scriptable2, scriptable, objArr2);
        } else {
            call = property;
        }
        property = call instanceof NativeNumber ? Double.valueOf(ScriptRuntime.toNumber(call)) : call instanceof NativeString ? ScriptRuntime.toString(call) : call instanceof NativeBoolean ? ((NativeBoolean) call).getDefaultValue(ScriptRuntime.BooleanClass) : call;
        if (property == null) {
            return "null";
        }
        if (property.equals(Boolean.TRUE)) {
            return "true";
        }
        if (property.equals(Boolean.FALSE)) {
            return "false";
        }
        if (property instanceof CharSequence) {
            return quote(property.toString());
        }
        if (!(property instanceof Number)) {
            return (!(property instanceof Scriptable) || (property instanceof Callable)) ? Undefined.instance : property instanceof NativeArray ? ja((NativeArray) property, stringifyState) : jo((Scriptable) property, stringifyState);
        } else {
            double doubleValue = ((Number) property).doubleValue();
            return (doubleValue != doubleValue || doubleValue == Double.POSITIVE_INFINITY || doubleValue == Double.NEGATIVE_INFINITY) ? "null" : ScriptRuntime.toString(property);
        }
    }

    public static Object stringify(Context context, Scriptable scriptable, Object obj, Object obj2, Object obj3) {
        int i;
        Object obj4;
        String repeat;
        String str = BuildConfig.FLAVOR;
        String str2 = BuildConfig.FLAVOR;
        List list = null;
        Callable callable = null;
        if (obj2 instanceof Callable) {
            callable = (Callable) obj2;
        } else if (obj2 instanceof NativeArray) {
            list = new LinkedList();
            NativeArray nativeArray = (NativeArray) obj2;
            Integer[] indexIds = nativeArray.getIndexIds();
            int length = indexIds.length;
            for (i = 0; i < length; i += Id_toSource) {
                obj4 = nativeArray.get(indexIds[i].intValue(), nativeArray);
                if ((obj4 instanceof String) || (obj4 instanceof Number)) {
                    list.add(obj4);
                } else if ((obj4 instanceof NativeString) || (obj4 instanceof NativeNumber)) {
                    list.add(ScriptRuntime.toString(obj4));
                }
            }
        }
        Object valueOf = obj3 instanceof NativeNumber ? Double.valueOf(ScriptRuntime.toNumber(obj3)) : obj3 instanceof NativeString ? ScriptRuntime.toString(obj3) : obj3;
        if (valueOf instanceof Number) {
            i = Math.min(MAX_STRINGIFY_GAP_LENGTH, (int) ScriptRuntime.toInteger(valueOf));
            repeat = i > 0 ? repeat(' ', i) : BuildConfig.FLAVOR;
            obj4 = Integer.valueOf(i);
        } else {
            if (valueOf instanceof String) {
                str2 = (String) valueOf;
                if (str2.length() > MAX_STRINGIFY_GAP_LENGTH) {
                    repeat = str2.substring(0, MAX_STRINGIFY_GAP_LENGTH);
                    obj4 = valueOf;
                }
            }
            repeat = str2;
            obj4 = valueOf;
        }
        StringifyState stringifyState = new StringifyState(context, scriptable, str, repeat, callable, list, obj4);
        Scriptable nativeObject = new NativeObject();
        nativeObject.setParentScope(scriptable);
        nativeObject.setPrototype(ScriptableObject.getObjectPrototype(scriptable));
        nativeObject.defineProperty(BuildConfig.FLAVOR, obj, 0);
        return str(BuildConfig.FLAVOR, nativeObject, stringifyState);
    }

    private static Object walk(Context context, Scriptable scriptable, Callable callable, Scriptable scriptable2, Object obj) {
        Object obj2 = obj instanceof Number ? scriptable2.get(((Number) obj).intValue(), scriptable2) : scriptable2.get((String) obj, scriptable2);
        if (obj2 instanceof Scriptable) {
            Scriptable scriptable3 = (Scriptable) obj2;
            if (scriptable3 instanceof NativeArray) {
                long length = ((NativeArray) scriptable3).getLength();
                for (long j = 0; j < length; j++) {
                    Object walk;
                    if (j > 2147483647L) {
                        String l = Long.toString(j);
                        walk = walk(context, scriptable, callable, scriptable3, l);
                        if (walk == Undefined.instance) {
                            scriptable3.delete(l);
                        } else {
                            scriptable3.put(l, scriptable3, walk);
                        }
                    } else {
                        int i = (int) j;
                        walk = walk(context, scriptable, callable, scriptable3, Integer.valueOf(i));
                        if (walk == Undefined.instance) {
                            scriptable3.delete(i);
                        } else {
                            scriptable3.put(i, scriptable3, walk);
                        }
                    }
                }
            } else {
                Object[] ids = scriptable3.getIds();
                int length2 = ids.length;
                for (int i2 = 0; i2 < length2; i2 += Id_toSource) {
                    Object obj3 = ids[i2];
                    Object walk2 = walk(context, scriptable, callable, scriptable3, obj3);
                    if (walk2 == Undefined.instance) {
                        if (obj3 instanceof Number) {
                            scriptable3.delete(((Number) obj3).intValue());
                        } else {
                            scriptable3.delete((String) obj3);
                        }
                    } else if (obj3 instanceof Number) {
                        scriptable3.put(((Number) obj3).intValue(), scriptable3, walk2);
                    } else {
                        scriptable3.put((String) obj3, scriptable3, walk2);
                    }
                }
            }
        }
        Object[] objArr = new Object[Id_parse];
        objArr[0] = obj;
        objArr[Id_toSource] = obj2;
        return callable.call(context, scriptable, scriptable2, objArr);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.Object execIdCall(org.mozilla.javascript.IdFunctionObject r5, org.mozilla.javascript.Context r6, org.mozilla.javascript.Scriptable r7, org.mozilla.javascript.Scriptable r8, java.lang.Object[] r9) {
        /*
        r4 = this;
        r2 = 0;
        r3 = 1;
        r0 = 0;
        r1 = JSON_TAG;
        r1 = r5.hasTag(r1);
        if (r1 != 0) goto L_0x0010;
    L_0x000b:
        r0 = super.execIdCall(r5, r6, r7, r8, r9);
    L_0x000f:
        return r0;
    L_0x0010:
        r1 = r5.methodId();
        switch(r1) {
            case 1: goto L_0x0021;
            case 2: goto L_0x0024;
            case 3: goto L_0x003d;
            default: goto L_0x0017;
        };
    L_0x0017:
        r0 = new java.lang.IllegalStateException;
        r1 = java.lang.String.valueOf(r1);
        r0.<init>(r1);
        throw r0;
    L_0x0021:
        r0 = "JSON";
        goto L_0x000f;
    L_0x0024:
        r1 = org.mozilla.javascript.ScriptRuntime.toString(r9, r2);
        r2 = r9.length;
        if (r2 <= r3) goto L_0x002d;
    L_0x002b:
        r0 = r9[r3];
    L_0x002d:
        r2 = r0 instanceof org.mozilla.javascript.Callable;
        if (r2 == 0) goto L_0x0038;
    L_0x0031:
        r0 = (org.mozilla.javascript.Callable) r0;
        r0 = parse(r6, r7, r1, r0);
        goto L_0x000f;
    L_0x0038:
        r0 = parse(r6, r7, r1);
        goto L_0x000f;
    L_0x003d:
        r1 = r9.length;
        switch(r1) {
            case 0: goto L_0x004f;
            case 1: goto L_0x004d;
            case 2: goto L_0x0044;
            default: goto L_0x0041;
        };
    L_0x0041:
        r0 = 2;
        r0 = r9[r0];
    L_0x0044:
        r1 = r9[r3];
    L_0x0046:
        r2 = r9[r2];
    L_0x0048:
        r0 = stringify(r6, r7, r2, r1, r0);
        goto L_0x000f;
    L_0x004d:
        r1 = r0;
        goto L_0x0046;
    L_0x004f:
        r1 = r0;
        r2 = r0;
        goto L_0x0048;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeJSON.execIdCall(org.mozilla.javascript.IdFunctionObject, org.mozilla.javascript.Context, org.mozilla.javascript.Scriptable, org.mozilla.javascript.Scriptable, java.lang.Object[]):java.lang.Object");
    }

    protected int findPrototypeId(String str) {
        int i;
        String str2;
        switch (str.length()) {
            case Token.GOTO /*5*/:
                i = Id_parse;
                str2 = "parse";
                break;
            case Token.SETNAME /*8*/:
                i = Id_toSource;
                str2 = "toSource";
                break;
            case Token.BITOR /*9*/:
                i = MAX_ID;
                str2 = "stringify";
                break;
            default:
                str2 = null;
                i = 0;
                break;
        }
        return (str2 == null || str2 == str || str2.equals(str)) ? i : 0;
    }

    public String getClassName() {
        return "JSON";
    }

    protected void initPrototypeId(int i) {
        int i2 = MAX_ID;
        if (i <= MAX_ID) {
            String str;
            switch (i) {
                case Id_toSource /*1*/:
                    i2 = 0;
                    str = "toSource";
                    break;
                case Id_parse /*2*/:
                    i2 = Id_parse;
                    str = "parse";
                    break;
                case MAX_ID /*3*/:
                    str = "stringify";
                    break;
                default:
                    throw new IllegalStateException(String.valueOf(i));
            }
            initPrototypeMethod(JSON_TAG, i, str, i2);
            return;
        }
        throw new IllegalStateException(String.valueOf(i));
    }
}
