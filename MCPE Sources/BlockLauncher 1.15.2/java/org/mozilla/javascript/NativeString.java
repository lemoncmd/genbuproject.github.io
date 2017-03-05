package org.mozilla.javascript;

import java.text.Collator;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.regexp.NativeRegExp;

final class NativeString extends IdScriptableObject {
    private static final int ConstructorId_charAt = -5;
    private static final int ConstructorId_charCodeAt = -6;
    private static final int ConstructorId_concat = -14;
    private static final int ConstructorId_equalsIgnoreCase = -30;
    private static final int ConstructorId_fromCharCode = -1;
    private static final int ConstructorId_indexOf = -7;
    private static final int ConstructorId_lastIndexOf = -8;
    private static final int ConstructorId_localeCompare = -34;
    private static final int ConstructorId_match = -31;
    private static final int ConstructorId_replace = -33;
    private static final int ConstructorId_search = -32;
    private static final int ConstructorId_slice = -15;
    private static final int ConstructorId_split = -9;
    private static final int ConstructorId_substr = -13;
    private static final int ConstructorId_substring = -10;
    private static final int ConstructorId_toLocaleLowerCase = -35;
    private static final int ConstructorId_toLowerCase = -11;
    private static final int ConstructorId_toUpperCase = -12;
    private static final int Id_anchor = 28;
    private static final int Id_big = 21;
    private static final int Id_blink = 22;
    private static final int Id_bold = 16;
    private static final int Id_charAt = 5;
    private static final int Id_charCodeAt = 6;
    private static final int Id_codePointAt = 45;
    private static final int Id_concat = 14;
    private static final int Id_constructor = 1;
    private static final int Id_endsWith = 42;
    private static final int Id_equals = 29;
    private static final int Id_equalsIgnoreCase = 30;
    private static final int Id_fixed = 18;
    private static final int Id_fontcolor = 26;
    private static final int Id_fontsize = 25;
    private static final int Id_includes = 40;
    private static final int Id_indexOf = 7;
    private static final int Id_italics = 17;
    private static final int Id_iterator = 46;
    private static final int Id_lastIndexOf = 8;
    private static final int Id_length = 1;
    private static final int Id_link = 27;
    private static final int Id_localeCompare = 34;
    private static final int Id_match = 31;
    private static final int Id_normalize = 43;
    private static final int Id_repeat = 44;
    private static final int Id_replace = 33;
    private static final int Id_search = 32;
    private static final int Id_slice = 15;
    private static final int Id_small = 20;
    private static final int Id_split = 9;
    private static final int Id_startsWith = 41;
    private static final int Id_strike = 19;
    private static final int Id_sub = 24;
    private static final int Id_substr = 13;
    private static final int Id_substring = 10;
    private static final int Id_sup = 23;
    private static final int Id_toLocaleLowerCase = 35;
    private static final int Id_toLocaleUpperCase = 36;
    private static final int Id_toLowerCase = 11;
    private static final int Id_toSource = 3;
    private static final int Id_toString = 2;
    private static final int Id_toUpperCase = 12;
    private static final int Id_trim = 37;
    private static final int Id_trimLeft = 38;
    private static final int Id_trimRight = 39;
    private static final int Id_valueOf = 4;
    private static final int MAX_INSTANCE_ID = 1;
    private static final int MAX_PROTOTYPE_ID = 46;
    private static final Object STRING_TAG = "String";
    static final long serialVersionUID = 920268368584188687L;
    private CharSequence string;

    NativeString(CharSequence charSequence) {
        this.string = charSequence;
    }

    static void init(Scriptable scriptable, boolean z) {
        new NativeString(BuildConfig.FLAVOR).exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    private static String js_concat(String str, Object[] objArr) {
        int i = 0;
        int length = objArr.length;
        if (length == 0) {
            return str;
        }
        if (length == MAX_INSTANCE_ID) {
            return str.concat(ScriptRuntime.toString(objArr[0]));
        }
        String[] strArr = new String[length];
        int length2 = str.length();
        for (int i2 = 0; i2 != length; i2 += MAX_INSTANCE_ID) {
            String scriptRuntime = ScriptRuntime.toString(objArr[i2]);
            strArr[i2] = scriptRuntime;
            length2 += scriptRuntime.length();
        }
        StringBuilder stringBuilder = new StringBuilder(length2);
        stringBuilder.append(str);
        while (i != length) {
            stringBuilder.append(strArr[i]);
            i += MAX_INSTANCE_ID;
        }
        return stringBuilder.toString();
    }

    private static int js_indexOf(int i, String str, Object[] objArr) {
        double d = 0.0d;
        int i2 = 0;
        String scriptRuntime = ScriptRuntime.toString(objArr, 0);
        double toInteger = ScriptRuntime.toInteger(objArr, MAX_INSTANCE_ID);
        if (toInteger > ((double) str.length()) && i != Id_startsWith && i != Id_endsWith) {
            return ConstructorId_fromCharCode;
        }
        if (toInteger >= 0.0d) {
            d = toInteger > ((double) str.length()) ? (double) str.length() : (i != Id_endsWith || (toInteger == toInteger && toInteger <= ((double) str.length()))) ? toInteger : (double) str.length();
        }
        if (Id_endsWith == i) {
            if (objArr.length == 0 || objArr.length == MAX_INSTANCE_ID || (objArr.length == Id_toString && objArr[MAX_INSTANCE_ID] == Undefined.instance)) {
                d = (double) str.length();
            }
            return str.substring(0, (int) d).endsWith(scriptRuntime) ? 0 : ConstructorId_fromCharCode;
        }
        if (i != Id_startsWith) {
            i2 = str.indexOf(scriptRuntime, (int) d);
        } else if (!str.startsWith(scriptRuntime, (int) d)) {
            i2 = ConstructorId_fromCharCode;
        }
        return i2;
    }

    private static int js_lastIndexOf(String str, Object[] objArr) {
        double d = 0.0d;
        String scriptRuntime = ScriptRuntime.toString(objArr, 0);
        double toNumber = ScriptRuntime.toNumber(objArr, MAX_INSTANCE_ID);
        if (toNumber != toNumber || toNumber > ((double) str.length())) {
            d = (double) str.length();
        } else if (toNumber >= 0.0d) {
            d = toNumber;
        }
        return str.lastIndexOf(scriptRuntime, (int) d);
    }

    private static String js_repeat(Context context, Scriptable scriptable, IdFunctionObject idFunctionObject, Object[] objArr) {
        String scriptRuntime = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(context, scriptable, idFunctionObject));
        double toInteger = ScriptRuntime.toInteger(objArr, 0);
        if (toInteger < 0.0d || toInteger == Double.POSITIVE_INFINITY) {
            throw ScriptRuntime.rangeError("Invalid count value");
        } else if (toInteger == 0.0d || scriptRuntime.length() == 0) {
            return BuildConfig.FLAVOR;
        } else {
            long length = ((long) scriptRuntime.length()) * ((long) toInteger);
            if (toInteger > 2.147483647E9d || length > 2147483647L) {
                throw ScriptRuntime.rangeError("Invalid size or count value");
            }
            StringBuilder stringBuilder = new StringBuilder((int) length);
            stringBuilder.append(scriptRuntime);
            int i = MAX_INSTANCE_ID;
            int i2 = (int) toInteger;
            while (i <= i2 / Id_toString) {
                stringBuilder.append(stringBuilder);
                i *= Id_toString;
            }
            if (i < i2) {
                stringBuilder.append(stringBuilder.substring(0, (i2 - i) * scriptRuntime.length()));
            }
            return stringBuilder.toString();
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.CharSequence js_slice(java.lang.CharSequence r8, java.lang.Object[] r9) {
        /*
        r7 = 1;
        r2 = 0;
        r0 = r9.length;
        if (r0 >= r7) goto L_0x0028;
    L_0x0006:
        r0 = r2;
    L_0x0007:
        r6 = r8.length();
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 >= 0) goto L_0x0030;
    L_0x000f:
        r4 = (double) r6;
        r0 = r0 + r4;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 >= 0) goto L_0x0016;
    L_0x0015:
        r0 = r2;
    L_0x0016:
        r4 = r9.length;
        r5 = 2;
        if (r4 < r5) goto L_0x0020;
    L_0x001a:
        r4 = r9[r7];
        r5 = org.mozilla.javascript.Undefined.instance;
        if (r4 != r5) goto L_0x0037;
    L_0x0020:
        r2 = (double) r6;
    L_0x0021:
        r0 = (int) r0;
        r1 = (int) r2;
        r0 = r8.subSequence(r0, r1);
        return r0;
    L_0x0028:
        r0 = 0;
        r0 = r9[r0];
        r0 = org.mozilla.javascript.ScriptRuntime.toInteger(r0);
        goto L_0x0007;
    L_0x0030:
        r4 = (double) r6;
        r4 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r4 <= 0) goto L_0x0016;
    L_0x0035:
        r0 = (double) r6;
        goto L_0x0016;
    L_0x0037:
        r4 = r9[r7];
        r4 = org.mozilla.javascript.ScriptRuntime.toInteger(r4);
        r7 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r7 >= 0) goto L_0x004d;
    L_0x0041:
        r6 = (double) r6;
        r4 = r4 + r6;
        r6 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r6 >= 0) goto L_0x0054;
    L_0x0047:
        r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1));
        if (r4 >= 0) goto L_0x0021;
    L_0x004b:
        r2 = r0;
        goto L_0x0021;
    L_0x004d:
        r2 = (double) r6;
        r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r2 <= 0) goto L_0x0054;
    L_0x0052:
        r2 = (double) r6;
        goto L_0x0047;
    L_0x0054:
        r2 = r4;
        goto L_0x0047;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeString.js_slice(java.lang.CharSequence, java.lang.Object[]):java.lang.CharSequence");
    }

    private static CharSequence js_substr(CharSequence charSequence, Object[] objArr) {
        double d = 0.0d;
        if (objArr.length < MAX_INSTANCE_ID) {
            return charSequence;
        }
        double toInteger = ScriptRuntime.toInteger(objArr[0]);
        int length = charSequence.length();
        if (toInteger < 0.0d) {
            toInteger += (double) length;
            if (toInteger < 0.0d) {
                toInteger = 0.0d;
            }
        } else if (toInteger > ((double) length)) {
            toInteger = (double) length;
        }
        if (objArr.length == MAX_INSTANCE_ID) {
            d = (double) length;
        } else {
            double toInteger2 = ScriptRuntime.toInteger(objArr[MAX_INSTANCE_ID]);
            if (toInteger2 >= 0.0d) {
                d = toInteger2;
            }
            d += toInteger;
            if (d > ((double) length)) {
                d = (double) length;
            }
        }
        return charSequence.subSequence((int) toInteger, (int) d);
    }

    private static CharSequence js_substring(Context context, CharSequence charSequence, Object[] objArr) {
        double d = 0.0d;
        int length = charSequence.length();
        double toInteger = ScriptRuntime.toInteger(objArr, 0);
        if (toInteger < 0.0d) {
            toInteger = 0.0d;
        } else if (toInteger > ((double) length)) {
            toInteger = (double) length;
        }
        if (objArr.length <= MAX_INSTANCE_ID || objArr[MAX_INSTANCE_ID] == Undefined.instance) {
            d = toInteger;
            toInteger = (double) length;
        } else {
            double toInteger2 = ScriptRuntime.toInteger(objArr[MAX_INSTANCE_ID]);
            if (toInteger2 >= 0.0d) {
                d = toInteger2 > ((double) length) ? (double) length : toInteger2;
            }
            if (d >= toInteger) {
                double d2 = d;
                d = toInteger;
                toInteger = d2;
            } else if (context.getLanguageVersion() == Token.FOR) {
                d = toInteger;
            }
        }
        return charSequence.subSequence((int) d, (int) toInteger);
    }

    private static NativeString realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (scriptable instanceof NativeString) {
            return (NativeString) scriptable;
        }
        throw IdScriptableObject.incompatibleCallError(idFunctionObject);
    }

    private static String tagify(Object obj, String str, String str2, Object[] objArr) {
        String scriptRuntime = ScriptRuntime.toString(obj);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('<');
        stringBuilder.append(str);
        if (str2 != null) {
            stringBuilder.append(' ');
            stringBuilder.append(str2);
            stringBuilder.append("=\"");
            stringBuilder.append(ScriptRuntime.toString(objArr, 0));
            stringBuilder.append('\"');
        }
        stringBuilder.append('>');
        stringBuilder.append(scriptRuntime);
        stringBuilder.append("</");
        stringBuilder.append(str);
        stringBuilder.append('>');
        return stringBuilder.toString();
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(STRING_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        Object[] objArr2 = objArr;
        Object obj = scriptable2;
        while (true) {
            int i;
            int length;
            CharSequence toCharSequence;
            double toInteger;
            String scriptRuntime;
            String scriptRuntime2;
            char[] toCharArray;
            switch (methodId) {
                case ConstructorId_toLocaleLowerCase /*-35*/:
                case ConstructorId_localeCompare /*-34*/:
                case ConstructorId_replace /*-33*/:
                case ConstructorId_search /*-32*/:
                case ConstructorId_match /*-31*/:
                case ConstructorId_equalsIgnoreCase /*-30*/:
                case ConstructorId_slice /*-15*/:
                case ConstructorId_concat /*-14*/:
                case ConstructorId_substr /*-13*/:
                case ConstructorId_toUpperCase /*-12*/:
                case ConstructorId_toLowerCase /*-11*/:
                case ConstructorId_substring /*-10*/:
                case ConstructorId_split /*-9*/:
                case ConstructorId_lastIndexOf /*-8*/:
                case ConstructorId_indexOf /*-7*/:
                case ConstructorId_charCodeAt /*-6*/:
                case ConstructorId_charAt /*-5*/:
                    Scriptable scriptable3;
                    if (objArr2.length > 0) {
                        Scriptable toObject = ScriptRuntime.toObject(context, scriptable, ScriptRuntime.toCharSequence(objArr2[0]));
                        Object[] objArr3 = new Object[(objArr2.length + ConstructorId_fromCharCode)];
                        for (i = 0; i < objArr3.length; i += MAX_INSTANCE_ID) {
                            objArr3[i] = objArr2[i + MAX_INSTANCE_ID];
                        }
                        objArr2 = objArr3;
                        scriptable3 = toObject;
                    } else {
                        scriptable3 = ScriptRuntime.toObject(context, scriptable, ScriptRuntime.toCharSequence(obj));
                    }
                    methodId = -methodId;
                    Scriptable scriptable4 = scriptable3;
                case ConstructorId_fromCharCode /*-1*/:
                    length = objArr2.length;
                    if (length < MAX_INSTANCE_ID) {
                        return BuildConfig.FLAVOR;
                    }
                    StringBuilder stringBuilder = new StringBuilder(length);
                    for (i = 0; i != length; i += MAX_INSTANCE_ID) {
                        stringBuilder.append(ScriptRuntime.toUint16(objArr2[i]));
                    }
                    return stringBuilder.toString();
                case MAX_INSTANCE_ID /*1*/:
                    toCharSequence = objArr2.length >= MAX_INSTANCE_ID ? ScriptRuntime.toCharSequence(objArr2[0]) : BuildConfig.FLAVOR;
                    return obj == null ? new NativeString(toCharSequence) : !(toCharSequence instanceof String) ? toCharSequence.toString() : toCharSequence;
                case Id_toString /*2*/:
                case Id_valueOf /*4*/:
                    toCharSequence = realThis(obj, idFunctionObject).string;
                    return !(toCharSequence instanceof String) ? toCharSequence.toString() : toCharSequence;
                case Id_toSource /*3*/:
                    return "(new String(\"" + ScriptRuntime.escapeString(realThis(obj, idFunctionObject).string.toString()) + "\"))";
                case Id_charAt /*5*/:
                case Id_charCodeAt /*6*/:
                    toCharSequence = ScriptRuntime.toCharSequence(obj);
                    toInteger = ScriptRuntime.toInteger(objArr2, 0);
                    if (toInteger < 0.0d || toInteger >= ((double) toCharSequence.length())) {
                        return methodId == Id_charAt ? BuildConfig.FLAVOR : ScriptRuntime.NaNobj;
                    } else {
                        char charAt = toCharSequence.charAt((int) toInteger);
                        return methodId == Id_charAt ? String.valueOf(charAt) : ScriptRuntime.wrapInt(charAt);
                    }
                case Id_indexOf /*7*/:
                    return ScriptRuntime.wrapInt(js_indexOf(Id_indexOf, ScriptRuntime.toString(obj), objArr2));
                case Id_lastIndexOf /*8*/:
                    break;
                case Id_split /*9*/:
                    return ScriptRuntime.checkRegExpProxy(context).js_split(context, scriptable, ScriptRuntime.toString(obj), objArr2);
                case Id_substring /*10*/:
                    return js_substring(context, ScriptRuntime.toCharSequence(obj), objArr2);
                case Id_toLowerCase /*11*/:
                    return ScriptRuntime.toString(obj).toLowerCase(ScriptRuntime.ROOT_LOCALE);
                case Id_toUpperCase /*12*/:
                    return ScriptRuntime.toString(obj).toUpperCase(ScriptRuntime.ROOT_LOCALE);
                case Id_substr /*13*/:
                    return js_substr(ScriptRuntime.toCharSequence(obj), objArr2);
                case Id_concat /*14*/:
                    return js_concat(ScriptRuntime.toString(obj), objArr2);
                case Id_slice /*15*/:
                    return js_slice(ScriptRuntime.toCharSequence(obj), objArr2);
                case Id_bold /*16*/:
                    return tagify(obj, "b", null, null);
                case Id_italics /*17*/:
                    return tagify(obj, "i", null, null);
                case Id_fixed /*18*/:
                    return tagify(obj, "tt", null, null);
                case Id_strike /*19*/:
                    return tagify(obj, "strike", null, null);
                case Id_small /*20*/:
                    return tagify(obj, "small", null, null);
                case Id_big /*21*/:
                    return tagify(obj, "big", null, null);
                case Id_blink /*22*/:
                    return tagify(obj, "blink", null, null);
                case Id_sup /*23*/:
                    return tagify(obj, "sup", null, null);
                case Id_sub /*24*/:
                    return tagify(obj, "sub", null, null);
                case Id_fontsize /*25*/:
                    return tagify(obj, "font", "size", objArr2);
                case Id_fontcolor /*26*/:
                    return tagify(obj, "font", "color", objArr2);
                case Id_link /*27*/:
                    return tagify(obj, "a", "href", objArr2);
                case Id_anchor /*28*/:
                    return tagify(obj, "a", "name", objArr2);
                case Id_equals /*29*/:
                case Id_equalsIgnoreCase /*30*/:
                    scriptRuntime = ScriptRuntime.toString(obj);
                    scriptRuntime2 = ScriptRuntime.toString(objArr2, 0);
                    return ScriptRuntime.wrapBoolean(methodId == Id_equals ? scriptRuntime.equals(scriptRuntime2) : scriptRuntime.equalsIgnoreCase(scriptRuntime2));
                case Id_match /*31*/:
                case Id_search /*32*/:
                case Id_replace /*33*/:
                    methodId = methodId == Id_match ? MAX_INSTANCE_ID : methodId == Id_search ? Id_toSource : Id_toString;
                    return ScriptRuntime.checkRegExpProxy(context).action(context, scriptable, obj, objArr2, methodId);
                case Id_localeCompare /*34*/:
                    Collator instance = Collator.getInstance(context.getLocale());
                    instance.setStrength(Id_toSource);
                    instance.setDecomposition(MAX_INSTANCE_ID);
                    return ScriptRuntime.wrapNumber((double) instance.compare(ScriptRuntime.toString(obj), ScriptRuntime.toString(objArr2, 0)));
                case Id_toLocaleLowerCase /*35*/:
                    return ScriptRuntime.toString(obj).toLowerCase(context.getLocale());
                case Id_toLocaleUpperCase /*36*/:
                    return ScriptRuntime.toString(obj).toUpperCase(context.getLocale());
                case Id_trim /*37*/:
                    String scriptRuntime3 = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(context, obj, idFunctionObject));
                    char[] toCharArray2 = scriptRuntime3.toCharArray();
                    i = 0;
                    while (i < toCharArray2.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(toCharArray2[i])) {
                        i += MAX_INSTANCE_ID;
                    }
                    length = toCharArray2.length;
                    while (length > i && ScriptRuntime.isJSWhitespaceOrLineTerminator(toCharArray2[length + ConstructorId_fromCharCode])) {
                        length += ConstructorId_fromCharCode;
                    }
                    return scriptRuntime3.substring(i, length);
                case Id_trimLeft /*38*/:
                    scriptRuntime2 = ScriptRuntime.toString(obj);
                    toCharArray = scriptRuntime2.toCharArray();
                    i = 0;
                    while (i < toCharArray.length && ScriptRuntime.isJSWhitespaceOrLineTerminator(toCharArray[i])) {
                        i += MAX_INSTANCE_ID;
                    }
                    return scriptRuntime2.substring(i, toCharArray.length);
                case Id_trimRight /*39*/:
                    scriptRuntime2 = ScriptRuntime.toString(obj);
                    toCharArray = scriptRuntime2.toCharArray();
                    i = toCharArray.length;
                    while (i > 0 && ScriptRuntime.isJSWhitespaceOrLineTerminator(toCharArray[i + ConstructorId_fromCharCode])) {
                        i += ConstructorId_fromCharCode;
                    }
                    return scriptRuntime2.substring(0, i);
                case Id_includes /*40*/:
                case Id_startsWith /*41*/:
                case Id_endsWith /*42*/:
                    scriptRuntime = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(context, obj, idFunctionObject));
                    if (objArr2.length <= 0 || !(objArr2[0] instanceof NativeRegExp)) {
                        i = js_indexOf(methodId, scriptRuntime, objArr2);
                        if (methodId == Id_includes) {
                            return Boolean.valueOf(i != ConstructorId_fromCharCode);
                        } else if (methodId == Id_startsWith) {
                            return Boolean.valueOf(i == 0);
                        } else if (methodId == Id_endsWith) {
                            return Boolean.valueOf(i != ConstructorId_fromCharCode);
                        }
                    }
                    throw ScriptRuntime.typeError2("msg.first.arg.not.regexp", String.class.getSimpleName(), idFunctionObject.getFunctionName());
                    break;
                case Id_normalize /*43*/:
                    Form form;
                    scriptRuntime = ScriptRuntime.toString(objArr2, 0);
                    if (Form.NFD.name().equals(scriptRuntime)) {
                        form = Form.NFD;
                    } else if (Form.NFKC.name().equals(scriptRuntime)) {
                        form = Form.NFKC;
                    } else if (Form.NFKD.name().equals(scriptRuntime)) {
                        form = Form.NFKD;
                    } else if (Form.NFC.name().equals(scriptRuntime) || objArr2.length == 0) {
                        form = Form.NFC;
                    } else {
                        throw ScriptRuntime.rangeError("The normalization form should be one of NFC, NFD, NFKC, NFKD");
                    }
                    return Normalizer.normalize(ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(context, obj, idFunctionObject)), form);
                case Id_repeat /*44*/:
                    return js_repeat(context, obj, idFunctionObject, objArr2);
                case Id_codePointAt /*45*/:
                    scriptRuntime = ScriptRuntime.toString(ScriptRuntimeES6.requireObjectCoercible(context, obj, idFunctionObject));
                    toInteger = ScriptRuntime.toInteger(objArr2, 0);
                    return (toInteger < 0.0d || toInteger >= ((double) scriptRuntime.length())) ? Undefined.instance : Integer.valueOf(scriptRuntime.codePointAt((int) toInteger));
                case MAX_PROTOTYPE_ID /*46*/:
                    return new NativeStringIterator(scriptable, obj);
                default:
                    throw new IllegalArgumentException("String.prototype has no method: " + idFunctionObject.getFunctionName());
            }
            return ScriptRuntime.wrapInt(js_lastIndexOf(ScriptRuntime.toString(obj), objArr2));
        }
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_fromCharCode, "fromCharCode", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_charAt, "charAt", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_charCodeAt, "charCodeAt", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_indexOf, "indexOf", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_lastIndexOf, "lastIndexOf", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_split, "split", Id_toSource);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_substring, "substring", Id_toSource);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_toLowerCase, "toLowerCase", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_toUpperCase, "toUpperCase", MAX_INSTANCE_ID);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_substr, "substr", Id_toSource);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_concat, "concat", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_slice, "slice", Id_toSource);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_equalsIgnoreCase, "equalsIgnoreCase", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_match, "match", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_search, "search", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_replace, "replace", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_localeCompare, "localeCompare", Id_toString);
        addIdFunctionProperty(idFunctionObject, STRING_TAG, ConstructorId_toLocaleLowerCase, "toLocaleLowerCase", MAX_INSTANCE_ID);
        super.fillConstructorProperties(idFunctionObject);
    }

    protected int findInstanceIdInfo(String str) {
        return str.equals(Name.LENGTH) ? IdScriptableObject.instanceIdInfo(Id_indexOf, MAX_INSTANCE_ID) : super.findInstanceIdInfo(str);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findPrototypeId(java.lang.String r9) {
        /*
        r8 = this;
        r6 = 98;
        r2 = 2;
        r5 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        r3 = 1;
        r0 = 0;
        r1 = 0;
        r4 = r9.length();
        switch(r4) {
            case 3: goto L_0x001c;
            case 4: goto L_0x005d;
            case 5: goto L_0x0083;
            case 6: goto L_0x00c4;
            case 7: goto L_0x0120;
            case 8: goto L_0x014d;
            case 9: goto L_0x018c;
            case 10: goto L_0x01bb;
            case 11: goto L_0x01e3;
            case 12: goto L_0x000f;
            case 13: goto L_0x0218;
            case 14: goto L_0x000f;
            case 15: goto L_0x000f;
            case 16: goto L_0x0221;
            case 17: goto L_0x022a;
            default: goto L_0x000f;
        };
    L_0x000f:
        r2 = r1;
        r1 = r0;
    L_0x0011:
        if (r2 == 0) goto L_0x024a;
    L_0x0013:
        if (r2 == r9) goto L_0x024a;
    L_0x0015:
        r2 = r2.equals(r9);
        if (r2 != 0) goto L_0x024a;
    L_0x001b:
        return r0;
    L_0x001c:
        r2 = r9.charAt(r2);
        if (r2 != r6) goto L_0x0033;
    L_0x0022:
        r2 = r9.charAt(r0);
        if (r2 != r5) goto L_0x000f;
    L_0x0028:
        r2 = r9.charAt(r3);
        r3 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r2 != r3) goto L_0x000f;
    L_0x0030:
        r0 = 24;
        goto L_0x001b;
    L_0x0033:
        r4 = 103; // 0x67 float:1.44E-43 double:5.1E-322;
        if (r2 != r4) goto L_0x0048;
    L_0x0037:
        r2 = r9.charAt(r0);
        if (r2 != r6) goto L_0x000f;
    L_0x003d:
        r2 = r9.charAt(r3);
        r3 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r2 != r3) goto L_0x000f;
    L_0x0045:
        r0 = 21;
        goto L_0x001b;
    L_0x0048:
        r4 = 112; // 0x70 float:1.57E-43 double:5.53E-322;
        if (r2 != r4) goto L_0x000f;
    L_0x004c:
        r2 = r9.charAt(r0);
        if (r2 != r5) goto L_0x000f;
    L_0x0052:
        r2 = r9.charAt(r3);
        r3 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        if (r2 != r3) goto L_0x000f;
    L_0x005a:
        r0 = 23;
        goto L_0x001b;
    L_0x005d:
        r2 = r9.charAt(r0);
        if (r2 != r6) goto L_0x006b;
    L_0x0063:
        r1 = "bold";
        r2 = 16;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x006b:
        r3 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        if (r2 != r3) goto L_0x0077;
    L_0x006f:
        r1 = "link";
        r2 = 27;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0077:
        r3 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r2 != r3) goto L_0x000f;
    L_0x007b:
        r1 = "trim";
        r2 = 37;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0083:
        r2 = 4;
        r2 = r9.charAt(r2);
        switch(r2) {
            case 100: goto L_0x008e;
            case 101: goto L_0x0097;
            case 104: goto L_0x00a0;
            case 107: goto L_0x00a9;
            case 108: goto L_0x00b2;
            case 116: goto L_0x00bb;
            default: goto L_0x008b;
        };
    L_0x008b:
        r2 = r1;
        r1 = r0;
        goto L_0x0011;
    L_0x008e:
        r1 = "fixed";
        r2 = 18;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0097:
        r1 = "slice";
        r2 = 15;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00a0:
        r1 = "match";
        r2 = 31;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00a9:
        r1 = "blink";
        r2 = 22;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00b2:
        r1 = "small";
        r2 = 20;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00bb:
        r1 = "split";
        r2 = 9;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00c4:
        r2 = r9.charAt(r3);
        switch(r2) {
            case 101: goto L_0x00cf;
            case 104: goto L_0x00eb;
            case 110: goto L_0x00f3;
            case 111: goto L_0x00fc;
            case 113: goto L_0x0105;
            case 116: goto L_0x010e;
            case 117: goto L_0x0117;
            default: goto L_0x00cb;
        };
    L_0x00cb:
        r2 = r1;
        r1 = r0;
        goto L_0x0011;
    L_0x00cf:
        r2 = r9.charAt(r0);
        r3 = 114; // 0x72 float:1.6E-43 double:5.63E-322;
        if (r2 != r3) goto L_0x00e0;
    L_0x00d7:
        r1 = "repeat";
        r2 = 44;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00e0:
        if (r2 != r5) goto L_0x000f;
    L_0x00e2:
        r1 = "search";
        r2 = 32;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00eb:
        r1 = "charAt";
        r2 = 5;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00f3:
        r1 = "anchor";
        r2 = 28;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x00fc:
        r1 = "concat";
        r2 = 14;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0105:
        r1 = "equals";
        r2 = 29;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x010e:
        r1 = "strike";
        r2 = 19;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0117:
        r1 = "substr";
        r2 = 13;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0120:
        r2 = r9.charAt(r3);
        switch(r2) {
            case 97: goto L_0x012b;
            case 101: goto L_0x0133;
            case 110: goto L_0x013c;
            case 116: goto L_0x0144;
            default: goto L_0x0127;
        };
    L_0x0127:
        r2 = r1;
        r1 = r0;
        goto L_0x0011;
    L_0x012b:
        r1 = "valueOf";
        r2 = 4;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0133:
        r1 = "replace";
        r2 = 33;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x013c:
        r1 = "indexOf";
        r2 = 7;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0144:
        r1 = "italics";
        r2 = 17;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x014d:
        r3 = 6;
        r3 = r9.charAt(r3);
        switch(r3) {
            case 99: goto L_0x0159;
            case 101: goto L_0x0161;
            case 102: goto L_0x016a;
            case 110: goto L_0x0173;
            case 116: goto L_0x017a;
            case 122: goto L_0x0183;
            default: goto L_0x0155;
        };
    L_0x0155:
        r2 = r1;
        r1 = r0;
        goto L_0x0011;
    L_0x0159:
        r1 = "toSource";
        r2 = 3;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0161:
        r1 = "includes";
        r2 = 40;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x016a:
        r1 = "trimLeft";
        r2 = 38;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0173:
        r1 = "toString";
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x017a:
        r1 = "endsWith";
        r2 = 42;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0183:
        r1 = "fontsize";
        r2 = 25;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x018c:
        r2 = r9.charAt(r0);
        switch(r2) {
            case 102: goto L_0x0197;
            case 110: goto L_0x01a0;
            case 115: goto L_0x01a9;
            case 116: goto L_0x01b2;
            default: goto L_0x0193;
        };
    L_0x0193:
        r2 = r1;
        r1 = r0;
        goto L_0x0011;
    L_0x0197:
        r1 = "fontcolor";
        r2 = 26;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x01a0:
        r1 = "normalize";
        r2 = 43;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x01a9:
        r1 = "substring";
        r2 = 10;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x01b2:
        r1 = "trimRight";
        r2 = 39;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x01bb:
        r2 = r9.charAt(r0);
        r3 = 99;
        if (r2 != r3) goto L_0x01cb;
    L_0x01c3:
        r1 = "charCodeAt";
        r2 = 6;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x01cb:
        if (r2 != r5) goto L_0x01d6;
    L_0x01cd:
        r1 = "startsWith";
        r2 = 41;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x01d6:
        r3 = 64;
        if (r2 != r3) goto L_0x000f;
    L_0x01da:
        r1 = "@@iterator";
        r2 = 46;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x01e3:
        r2 = r9.charAt(r2);
        switch(r2) {
            case 76: goto L_0x01ee;
            case 85: goto L_0x01f7;
            case 100: goto L_0x0200;
            case 110: goto L_0x0209;
            case 115: goto L_0x020f;
            default: goto L_0x01ea;
        };
    L_0x01ea:
        r2 = r1;
        r1 = r0;
        goto L_0x0011;
    L_0x01ee:
        r1 = "toLowerCase";
        r2 = 11;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x01f7:
        r1 = "toUpperCase";
        r2 = 12;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0200:
        r1 = "codePointAt";
        r2 = 45;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0209:
        r1 = "constructor";
        r2 = r1;
        r1 = r3;
        goto L_0x0011;
    L_0x020f:
        r1 = "lastIndexOf";
        r2 = 8;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0218:
        r1 = "localeCompare";
        r2 = 34;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x0221:
        r1 = "equalsIgnoreCase";
        r2 = 30;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x022a:
        r2 = 8;
        r2 = r9.charAt(r2);
        r3 = 76;
        if (r2 != r3) goto L_0x023d;
    L_0x0234:
        r1 = "toLocaleLowerCase";
        r2 = 35;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x023d:
        r3 = 85;
        if (r2 != r3) goto L_0x000f;
    L_0x0241:
        r1 = "toLocaleUpperCase";
        r2 = 36;
        r7 = r1;
        r1 = r2;
        r2 = r7;
        goto L_0x0011;
    L_0x024a:
        r0 = r1;
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeString.findPrototypeId(java.lang.String):int");
    }

    public Object get(int i, Scriptable scriptable) {
        return (i < 0 || i >= this.string.length()) ? super.get(i, scriptable) : String.valueOf(this.string.charAt(i));
    }

    public Object[] getAllIds() {
        Context currentContext = Context.getCurrentContext();
        if (currentContext == null || currentContext.getLanguageVersion() < Context.VERSION_ES6) {
            return super.getAllIds();
        }
        Object allIds = super.getAllIds();
        Object obj = new Object[(allIds.length + this.string.length())];
        int i = 0;
        while (i < this.string.length()) {
            obj[i] = Integer.valueOf(i);
            i += MAX_INSTANCE_ID;
        }
        System.arraycopy(allIds, 0, obj, i, allIds.length);
        return obj;
    }

    public String getClassName() {
        return "String";
    }

    protected String getInstanceIdName(int i) {
        return i == MAX_INSTANCE_ID ? Name.LENGTH : super.getInstanceIdName(i);
    }

    protected Object getInstanceIdValue(int i) {
        return i == MAX_INSTANCE_ID ? ScriptRuntime.wrapInt(this.string.length()) : super.getInstanceIdValue(i);
    }

    int getLength() {
        return this.string.length();
    }

    protected int getMaxInstanceId() {
        return MAX_INSTANCE_ID;
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
            case Id_toSource /*3*/:
                str = "toSource";
                i2 = 0;
                break;
            case Id_valueOf /*4*/:
                str = "valueOf";
                i2 = 0;
                break;
            case Id_charAt /*5*/:
                str = "charAt";
                break;
            case Id_charCodeAt /*6*/:
                str = "charCodeAt";
                break;
            case Id_indexOf /*7*/:
                str = "indexOf";
                break;
            case Id_lastIndexOf /*8*/:
                str = "lastIndexOf";
                break;
            case Id_split /*9*/:
                str = "split";
                i2 = Id_toString;
                break;
            case Id_substring /*10*/:
                str = "substring";
                i2 = Id_toString;
                break;
            case Id_toLowerCase /*11*/:
                str = "toLowerCase";
                i2 = 0;
                break;
            case Id_toUpperCase /*12*/:
                str = "toUpperCase";
                i2 = 0;
                break;
            case Id_substr /*13*/:
                str = "substr";
                i2 = Id_toString;
                break;
            case Id_concat /*14*/:
                str = "concat";
                break;
            case Id_slice /*15*/:
                str = "slice";
                i2 = Id_toString;
                break;
            case Id_bold /*16*/:
                str = "bold";
                i2 = 0;
                break;
            case Id_italics /*17*/:
                str = "italics";
                i2 = 0;
                break;
            case Id_fixed /*18*/:
                str = "fixed";
                i2 = 0;
                break;
            case Id_strike /*19*/:
                str = "strike";
                i2 = 0;
                break;
            case Id_small /*20*/:
                str = "small";
                i2 = 0;
                break;
            case Id_big /*21*/:
                str = "big";
                i2 = 0;
                break;
            case Id_blink /*22*/:
                str = "blink";
                i2 = 0;
                break;
            case Id_sup /*23*/:
                str = "sup";
                i2 = 0;
                break;
            case Id_sub /*24*/:
                str = "sub";
                i2 = 0;
                break;
            case Id_fontsize /*25*/:
                str = "fontsize";
                i2 = 0;
                break;
            case Id_fontcolor /*26*/:
                str = "fontcolor";
                i2 = 0;
                break;
            case Id_link /*27*/:
                str = "link";
                i2 = 0;
                break;
            case Id_anchor /*28*/:
                str = "anchor";
                i2 = 0;
                break;
            case Id_equals /*29*/:
                str = "equals";
                break;
            case Id_equalsIgnoreCase /*30*/:
                str = "equalsIgnoreCase";
                break;
            case Id_match /*31*/:
                str = "match";
                break;
            case Id_search /*32*/:
                str = "search";
                break;
            case Id_replace /*33*/:
                str = "replace";
                i2 = Id_toString;
                break;
            case Id_localeCompare /*34*/:
                str = "localeCompare";
                break;
            case Id_toLocaleLowerCase /*35*/:
                str = "toLocaleLowerCase";
                i2 = 0;
                break;
            case Id_toLocaleUpperCase /*36*/:
                str = "toLocaleUpperCase";
                i2 = 0;
                break;
            case Id_trim /*37*/:
                str = "trim";
                i2 = 0;
                break;
            case Id_trimLeft /*38*/:
                str = "trimLeft";
                i2 = 0;
                break;
            case Id_trimRight /*39*/:
                str = "trimRight";
                i2 = 0;
                break;
            case Id_includes /*40*/:
                str = "includes";
                break;
            case Id_startsWith /*41*/:
                str = "startsWith";
                break;
            case Id_endsWith /*42*/:
                str = "endsWith";
                break;
            case Id_normalize /*43*/:
                str = "normalize";
                i2 = 0;
                break;
            case Id_repeat /*44*/:
                str = "repeat";
                break;
            case Id_codePointAt /*45*/:
                str = "codePointAt";
                break;
            case MAX_PROTOTYPE_ID /*46*/:
                str = NativeSymbol.ITERATOR_PROPERTY;
                str2 = "[Symbol.iterator]";
                i2 = 0;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(STRING_TAG, i, str, str2, i2);
    }

    public void put(int i, Scriptable scriptable, Object obj) {
        if (i < 0 || i >= this.string.length()) {
            super.put(i, scriptable, obj);
        }
    }

    public CharSequence toCharSequence() {
        return this.string;
    }

    public String toString() {
        return this.string instanceof String ? (String) this.string : this.string.toString();
    }
}
