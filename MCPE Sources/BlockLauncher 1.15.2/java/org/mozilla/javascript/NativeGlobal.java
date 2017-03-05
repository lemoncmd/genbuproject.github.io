package org.mozilla.javascript;

import java.io.Serializable;
import net.hockeyapp.android.BuildConfig;
import net.zhuoweizhang.mcpelauncher.texture.tga.TGAImage.Header;
import org.mozilla.javascript.TopLevel.Builtins;
import org.mozilla.javascript.xml.XMLLib;

public class NativeGlobal implements Serializable, IdFunctionCall {
    private static final Object FTAG = "Global";
    private static final int INVALID_UTF8 = Integer.MAX_VALUE;
    private static final int Id_decodeURI = 1;
    private static final int Id_decodeURIComponent = 2;
    private static final int Id_encodeURI = 3;
    private static final int Id_encodeURIComponent = 4;
    private static final int Id_escape = 5;
    private static final int Id_eval = 6;
    private static final int Id_isFinite = 7;
    private static final int Id_isNaN = 8;
    private static final int Id_isXMLName = 9;
    private static final int Id_new_CommonError = 14;
    private static final int Id_parseFloat = 10;
    private static final int Id_parseInt = 11;
    private static final int Id_unescape = 12;
    private static final int Id_uneval = 13;
    private static final int LAST_SCOPE_FUNCTION_ID = 13;
    private static final String URI_DECODE_RESERVED = ";/?:@&=+$,#";
    static final long serialVersionUID = 6080442165748707530L;

    @Deprecated
    public static EcmaError constructError(Context context, String str, String str2, Scriptable scriptable) {
        return ScriptRuntime.constructError(str, str2);
    }

    @Deprecated
    public static EcmaError constructError(Context context, String str, String str2, Scriptable scriptable, String str3, int i, int i2, String str4) {
        return ScriptRuntime.constructError(str, str2, str3, i, str4, i2);
    }

    private static String decode(String str, boolean z) {
        char[] cArr = null;
        int length = str.length();
        int i = 0;
        int i2 = 0;
        while (i != length) {
            int i3;
            char charAt = str.charAt(i);
            if (charAt != '%') {
                if (cArr != null) {
                    i3 = i2 + Id_decodeURI;
                    cArr[i2] = charAt;
                } else {
                    i3 = i2;
                }
                i2 = i + Id_decodeURI;
            } else {
                int i4;
                if (cArr == null) {
                    cArr = new char[length];
                    str.getChars(0, i, cArr, 0);
                    i4 = i;
                } else {
                    i4 = i2;
                }
                if (i + Id_encodeURI > length) {
                    throw uriError();
                }
                i2 = unHex(str.charAt(i + Id_decodeURI), str.charAt(i + Id_decodeURIComponent));
                if (i2 < 0) {
                    throw uriError();
                }
                char c;
                i3 = i + Id_encodeURI;
                if ((i2 & Token.RESERVED) == 0) {
                    c = (char) i2;
                    i2 = i3;
                } else if ((i2 & Header.ID_INTERLEAVE) == Token.RESERVED) {
                    throw uriError();
                } else {
                    int i5;
                    int i6;
                    int i7;
                    if ((i2 & 32) == 0) {
                        i5 = i2 & 31;
                        i6 = 128;
                        i7 = Id_decodeURI;
                    } else if ((i2 & 16) == 0) {
                        i5 = i2 & 15;
                        i6 = 2048;
                        i7 = Id_decodeURIComponent;
                    } else if ((i2 & Id_isNaN) == 0) {
                        i5 = i2 & Id_isFinite;
                        i6 = 65536;
                        i7 = Id_encodeURI;
                    } else if ((i2 & Id_encodeURIComponent) == 0) {
                        i5 = i2 & Id_encodeURI;
                        i6 = 2097152;
                        i7 = Id_encodeURIComponent;
                    } else if ((i2 & Id_decodeURIComponent) == 0) {
                        i5 = i2 & Id_decodeURI;
                        i6 = 67108864;
                        i7 = Id_escape;
                    } else {
                        throw uriError();
                    }
                    if ((i7 * Id_encodeURI) + i3 > length) {
                        throw uriError();
                    }
                    i2 = i5;
                    i5 = 0;
                    while (i5 != i7) {
                        if (str.charAt(i3) != '%') {
                            throw uriError();
                        }
                        int unHex = unHex(str.charAt(i3 + Id_decodeURI), str.charAt(i3 + Id_decodeURIComponent));
                        if (unHex < 0 || (unHex & Header.ID_INTERLEAVE) != Token.RESERVED) {
                            throw uriError();
                        }
                        i3 += Id_encodeURI;
                        i5 += Id_decodeURI;
                        i2 = (unHex & 63) | (i2 << Id_eval);
                    }
                    if (i2 < i6 || (i2 >= 55296 && i2 <= 57343)) {
                        i2 = INVALID_UTF8;
                    } else if (i2 == 65534 || i2 == 65535) {
                        i2 = 65533;
                    }
                    if (i2 >= Parser.ARGC_LIMIT) {
                        i2 -= Parser.ARGC_LIMIT;
                        if (i2 > 1048575) {
                            throw uriError();
                        }
                        char c2 = (char) ((i2 >>> Id_parseFloat) + 55296);
                        char c3 = (char) ((i2 & 1023) + 56320);
                        i5 = i4 + Id_decodeURI;
                        cArr[i4] = c2;
                        i4 = i5;
                        c = c3;
                        i2 = i3;
                    } else {
                        c = (char) i2;
                        i2 = i3;
                    }
                }
                if (!z || URI_DECODE_RESERVED.indexOf(c) < 0) {
                    i3 = i4 + Id_decodeURI;
                    cArr[i4] = c;
                } else {
                    i3 = i4;
                    while (i != i2) {
                        i4 = i3 + Id_decodeURI;
                        cArr[i3] = str.charAt(i);
                        i += Id_decodeURI;
                        i3 = i4;
                    }
                }
            }
            i = i2;
            i2 = i3;
        }
        return cArr == null ? str : new String(cArr, 0, i2);
    }

    private static String encode(String str, boolean z) {
        StringBuilder stringBuilder = null;
        int length = str.length();
        int i = 0;
        byte[] bArr = null;
        while (i != length) {
            StringBuilder stringBuilder2;
            byte[] bArr2;
            int charAt = str.charAt(i);
            if (encodeUnescaped(charAt, z)) {
                if (stringBuilder != null) {
                    stringBuilder.append(charAt);
                    stringBuilder2 = stringBuilder;
                    bArr2 = bArr;
                }
                stringBuilder2 = stringBuilder;
                bArr2 = bArr;
            } else {
                if (stringBuilder == null) {
                    stringBuilder = new StringBuilder(length + Id_encodeURI);
                    stringBuilder.append(str);
                    stringBuilder.setLength(i);
                    bArr = new byte[Id_eval];
                }
                if (56320 > charAt || charAt > 57343) {
                    if (charAt >= 55296 && 56319 >= charAt) {
                        i += Id_decodeURI;
                        if (i == length) {
                            throw uriError();
                        }
                        char charAt2 = str.charAt(i);
                        if ('\udc00' > charAt2 || charAt2 > '\udfff') {
                            throw uriError();
                        }
                        charAt = (((charAt - 55296) << Id_parseFloat) + (charAt2 - 56320)) + Parser.ARGC_LIMIT;
                    }
                    int oneUcs4ToUtf8Char = oneUcs4ToUtf8Char(bArr, charAt);
                    for (charAt = 0; charAt < oneUcs4ToUtf8Char; charAt += Id_decodeURI) {
                        int i2 = bArr[charAt] & 255;
                        stringBuilder.append('%');
                        stringBuilder.append(toHexChar(i2 >>> Id_encodeURIComponent));
                        stringBuilder.append(toHexChar(i2 & 15));
                    }
                    stringBuilder2 = stringBuilder;
                    bArr2 = bArr;
                } else {
                    throw uriError();
                }
            }
            i += Id_decodeURI;
            bArr = bArr2;
            stringBuilder = stringBuilder2;
        }
        return stringBuilder == null ? str : stringBuilder.toString();
    }

    private static boolean encodeUnescaped(char c, boolean z) {
        return ('A' > c || c > 'Z') ? ('a' > c || c > 'z') ? (('0' > c || c > '9') && "-_.!~*'()".indexOf(c) < 0) ? z ? URI_DECODE_RESERVED.indexOf(c) >= 0 : false : true : true : true;
    }

    public static void init(Context context, Scriptable scriptable, boolean z) {
        IdFunctionCall nativeGlobal = new NativeGlobal();
        for (int i = Id_decodeURI; i <= LAST_SCOPE_FUNCTION_ID; i += Id_decodeURI) {
            String str;
            int i2;
            switch (i) {
                case Id_decodeURI /*1*/:
                    str = "decodeURI";
                    i2 = Id_decodeURI;
                    break;
                case Id_decodeURIComponent /*2*/:
                    str = "decodeURIComponent";
                    i2 = Id_decodeURI;
                    break;
                case Id_encodeURI /*3*/:
                    str = "encodeURI";
                    i2 = Id_decodeURI;
                    break;
                case Id_encodeURIComponent /*4*/:
                    str = "encodeURIComponent";
                    i2 = Id_decodeURI;
                    break;
                case Id_escape /*5*/:
                    str = "escape";
                    i2 = Id_decodeURI;
                    break;
                case Id_eval /*6*/:
                    str = "eval";
                    i2 = Id_decodeURI;
                    break;
                case Id_isFinite /*7*/:
                    str = "isFinite";
                    i2 = Id_decodeURI;
                    break;
                case Id_isNaN /*8*/:
                    str = "isNaN";
                    i2 = Id_decodeURI;
                    break;
                case Id_isXMLName /*9*/:
                    str = "isXMLName";
                    i2 = Id_decodeURI;
                    break;
                case Id_parseFloat /*10*/:
                    str = "parseFloat";
                    i2 = Id_decodeURI;
                    break;
                case Id_parseInt /*11*/:
                    str = "parseInt";
                    i2 = Id_decodeURIComponent;
                    break;
                case Id_unescape /*12*/:
                    str = "unescape";
                    i2 = Id_decodeURI;
                    break;
                case LAST_SCOPE_FUNCTION_ID /*13*/:
                    str = "uneval";
                    i2 = Id_decodeURI;
                    break;
                default:
                    throw Kit.codeBug();
            }
            IdFunctionObject idFunctionObject = new IdFunctionObject(nativeGlobal, FTAG, i, str, i2, scriptable);
            if (z) {
                idFunctionObject.sealObject();
            }
            idFunctionObject.exportAsScopeProperty();
        }
        ScriptableObject.defineProperty(scriptable, "NaN", ScriptRuntime.NaNobj, Id_isFinite);
        ScriptableObject.defineProperty(scriptable, "Infinity", ScriptRuntime.wrapNumber(Double.POSITIVE_INFINITY), Id_isFinite);
        ScriptableObject.defineProperty(scriptable, "undefined", Undefined.instance, Id_isFinite);
        NativeErrors[] values = NativeErrors.values();
        int length = values.length;
        for (int i3 = 0; i3 < length; i3 += Id_decodeURI) {
            NativeErrors nativeErrors = values[i3];
            if (nativeErrors != NativeErrors.Error) {
                Object name = nativeErrors.name();
                Scriptable scriptable2 = (ScriptableObject) ScriptRuntime.newBuiltinObject(context, scriptable, Builtins.Error, ScriptRuntime.emptyArgs);
                scriptable2.put("name", scriptable2, name);
                scriptable2.put("message", scriptable2, BuildConfig.FLAVOR);
                Object idFunctionObject2 = new IdFunctionObject(nativeGlobal, FTAG, Id_new_CommonError, name, Id_decodeURI, scriptable);
                idFunctionObject2.markAsConstructor(scriptable2);
                scriptable2.put("constructor", scriptable2, idFunctionObject2);
                scriptable2.setAttributes("constructor", (int) Id_decodeURIComponent);
                if (z) {
                    scriptable2.sealObject();
                    idFunctionObject2.sealObject();
                }
                idFunctionObject2.exportAsScopeProperty();
            }
        }
    }

    static boolean isEvalFunction(Object obj) {
        if (obj instanceof IdFunctionObject) {
            IdFunctionObject idFunctionObject = (IdFunctionObject) obj;
            if (idFunctionObject.hasTag(FTAG) && idFunctionObject.methodId() == Id_eval) {
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.Object js_escape(java.lang.Object[] r13) {
        /*
        r12 = this;
        r11 = 43;
        r10 = 37;
        r2 = 2;
        r5 = 1;
        r1 = 0;
        r4 = org.mozilla.javascript.ScriptRuntime.toString(r13, r1);
        r0 = 7;
        r3 = r13.length;
        if (r3 <= r5) goto L_0x002a;
    L_0x000f:
        r0 = r13[r5];
        r6 = org.mozilla.javascript.ScriptRuntime.toNumber(r0);
        r0 = (r6 > r6 ? 1 : (r6 == r6 ? 0 : -1));
        if (r0 != 0) goto L_0x0023;
    L_0x0019:
        r0 = (int) r6;
        r8 = (double) r0;
        r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r3 != 0) goto L_0x0023;
    L_0x001f:
        r3 = r0 & -8;
        if (r3 == 0) goto L_0x002a;
    L_0x0023:
        r0 = "msg.bad.esc.mask";
        r0 = org.mozilla.javascript.Context.reportRuntimeError0(r0);
        throw r0;
    L_0x002a:
        r3 = 0;
        r7 = r4.length();
        r5 = r1;
        r1 = r3;
    L_0x0031:
        if (r5 == r7) goto L_0x00c1;
    L_0x0033:
        r8 = r4.charAt(r5);
        if (r0 == 0) goto L_0x0079;
    L_0x0039:
        r3 = 48;
        if (r8 < r3) goto L_0x0041;
    L_0x003d:
        r3 = 57;
        if (r8 <= r3) goto L_0x006f;
    L_0x0041:
        r3 = 65;
        if (r8 < r3) goto L_0x0049;
    L_0x0045:
        r3 = 90;
        if (r8 <= r3) goto L_0x006f;
    L_0x0049:
        r3 = 97;
        if (r8 < r3) goto L_0x0051;
    L_0x004d:
        r3 = 122; // 0x7a float:1.71E-43 double:6.03E-322;
        if (r8 <= r3) goto L_0x006f;
    L_0x0051:
        r3 = 64;
        if (r8 == r3) goto L_0x006f;
    L_0x0055:
        r3 = 42;
        if (r8 == r3) goto L_0x006f;
    L_0x0059:
        r3 = 95;
        if (r8 == r3) goto L_0x006f;
    L_0x005d:
        r3 = 45;
        if (r8 == r3) goto L_0x006f;
    L_0x0061:
        r3 = 46;
        if (r8 == r3) goto L_0x006f;
    L_0x0065:
        r3 = r0 & 4;
        if (r3 == 0) goto L_0x0079;
    L_0x0069:
        r3 = 47;
        if (r8 == r3) goto L_0x006f;
    L_0x006d:
        if (r8 != r11) goto L_0x0079;
    L_0x006f:
        if (r1 == 0) goto L_0x0075;
    L_0x0071:
        r3 = (char) r8;
        r1.append(r3);
    L_0x0075:
        r3 = r5 + 1;
        r5 = r3;
        goto L_0x0031;
    L_0x0079:
        if (r1 != 0) goto L_0x00ca;
    L_0x007b:
        r3 = new java.lang.StringBuilder;
        r1 = r7 + 3;
        r3.<init>(r1);
        r3.append(r4);
        r3.setLength(r5);
    L_0x0088:
        r1 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
        if (r8 >= r1) goto L_0x00b4;
    L_0x008c:
        r1 = 32;
        if (r8 != r1) goto L_0x0097;
    L_0x0090:
        if (r0 != r2) goto L_0x0097;
    L_0x0092:
        r3.append(r11);
        r1 = r3;
        goto L_0x0075;
    L_0x0097:
        r3.append(r10);
        r1 = r2;
    L_0x009b:
        r1 = r1 + -1;
        r1 = r1 * 4;
        r6 = r1;
    L_0x00a0:
        if (r6 < 0) goto L_0x00cc;
    L_0x00a2:
        r1 = r8 >> r6;
        r1 = r1 & 15;
        r9 = 10;
        if (r1 >= r9) goto L_0x00be;
    L_0x00aa:
        r1 = r1 + 48;
    L_0x00ac:
        r1 = (char) r1;
        r3.append(r1);
        r1 = r6 + -4;
        r6 = r1;
        goto L_0x00a0;
    L_0x00b4:
        r3.append(r10);
        r1 = 117; // 0x75 float:1.64E-43 double:5.8E-322;
        r3.append(r1);
        r1 = 4;
        goto L_0x009b;
    L_0x00be:
        r1 = r1 + 55;
        goto L_0x00ac;
    L_0x00c1:
        if (r1 != 0) goto L_0x00c5;
    L_0x00c3:
        r0 = r4;
    L_0x00c4:
        return r0;
    L_0x00c5:
        r0 = r1.toString();
        goto L_0x00c4;
    L_0x00ca:
        r3 = r1;
        goto L_0x0088;
    L_0x00cc:
        r1 = r3;
        goto L_0x0075;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeGlobal.js_escape(java.lang.Object[]):java.lang.Object");
    }

    private Object js_eval(Context context, Scriptable scriptable, Object[] objArr) {
        Scriptable topLevelScope = ScriptableObject.getTopLevelScope(scriptable);
        return ScriptRuntime.evalSpecial(context, topLevelScope, topLevelScope, objArr, "eval code", Id_decodeURI);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static java.lang.Object js_parseFloat(java.lang.Object[] r10) {
        /*
        r9 = 45;
        r1 = 1;
        r0 = 0;
        r5 = -1;
        r2 = r10.length;
        if (r2 >= r1) goto L_0x000b;
    L_0x0008:
        r0 = org.mozilla.javascript.ScriptRuntime.NaNobj;
    L_0x000a:
        return r0;
    L_0x000b:
        r2 = r10[r0];
        r7 = org.mozilla.javascript.ScriptRuntime.toString(r2);
        r8 = r7.length();
        r6 = r0;
    L_0x0016:
        if (r6 != r8) goto L_0x001b;
    L_0x0018:
        r0 = org.mozilla.javascript.ScriptRuntime.NaNobj;
        goto L_0x000a;
    L_0x001b:
        r2 = r7.charAt(r6);
        r3 = org.mozilla.javascript.ScriptRuntime.isStrWhiteSpaceChar(r2);
        if (r3 != 0) goto L_0x0032;
    L_0x0025:
        r3 = 43;
        if (r2 == r3) goto L_0x002b;
    L_0x0029:
        if (r2 != r9) goto L_0x00a6;
    L_0x002b:
        r3 = r6 + 1;
        if (r3 != r8) goto L_0x0035;
    L_0x002f:
        r0 = org.mozilla.javascript.ScriptRuntime.NaNobj;
        goto L_0x000a;
    L_0x0032:
        r6 = r6 + 1;
        goto L_0x0016;
    L_0x0035:
        r2 = r7.charAt(r3);
    L_0x0039:
        r4 = 73;
        if (r2 != r4) goto L_0x005e;
    L_0x003d:
        r1 = r3 + 8;
        if (r1 > r8) goto L_0x005b;
    L_0x0041:
        r1 = "Infinity";
        r2 = 8;
        r0 = r7.regionMatches(r3, r1, r0, r2);
        if (r0 == 0) goto L_0x005b;
    L_0x004b:
        r0 = r7.charAt(r6);
        if (r0 != r9) goto L_0x0058;
    L_0x0051:
        r0 = -4503599627370496; // 0xfff0000000000000 float:0.0 double:-Infinity;
    L_0x0053:
        r0 = org.mozilla.javascript.ScriptRuntime.wrapNumber(r0);
        goto L_0x000a;
    L_0x0058:
        r0 = 9218868437227405312; // 0x7ff0000000000000 float:0.0 double:Infinity;
        goto L_0x0053;
    L_0x005b:
        r0 = org.mozilla.javascript.ScriptRuntime.NaNobj;
        goto L_0x000a;
    L_0x005e:
        r2 = r5;
        r4 = r5;
    L_0x0060:
        if (r3 >= r8) goto L_0x00a4;
    L_0x0062:
        r9 = r7.charAt(r3);
        switch(r9) {
            case 43: goto L_0x008b;
            case 45: goto L_0x008b;
            case 46: goto L_0x0077;
            case 48: goto L_0x0099;
            case 49: goto L_0x0099;
            case 50: goto L_0x0099;
            case 51: goto L_0x0099;
            case 52: goto L_0x0099;
            case 53: goto L_0x0099;
            case 54: goto L_0x0099;
            case 55: goto L_0x0099;
            case 56: goto L_0x0099;
            case 57: goto L_0x0099;
            case 69: goto L_0x007f;
            case 101: goto L_0x007f;
            default: goto L_0x0069;
        };
    L_0x0069:
        r1 = r3;
    L_0x006a:
        if (r2 == r5) goto L_0x00a2;
    L_0x006c:
        if (r0 != 0) goto L_0x00a2;
    L_0x006e:
        r0 = r7.substring(r6, r2);
        r0 = java.lang.Double.valueOf(r0);	 Catch:{ NumberFormatException -> 0x009d }
        goto L_0x000a;
    L_0x0077:
        if (r4 == r5) goto L_0x007b;
    L_0x0079:
        r1 = r3;
        goto L_0x006a;
    L_0x007b:
        r4 = r3;
    L_0x007c:
        r3 = r3 + 1;
        goto L_0x0060;
    L_0x007f:
        if (r2 == r5) goto L_0x0083;
    L_0x0081:
        r1 = r3;
        goto L_0x006a;
    L_0x0083:
        r9 = r8 + -1;
        if (r3 != r9) goto L_0x0089;
    L_0x0087:
        r1 = r3;
        goto L_0x006a;
    L_0x0089:
        r2 = r3;
        goto L_0x007c;
    L_0x008b:
        r9 = r3 + -1;
        if (r2 == r9) goto L_0x0091;
    L_0x008f:
        r1 = r3;
        goto L_0x006a;
    L_0x0091:
        r9 = r8 + -1;
        if (r3 != r9) goto L_0x007c;
    L_0x0095:
        r3 = r3 + -1;
        r1 = r3;
        goto L_0x006a;
    L_0x0099:
        if (r2 == r5) goto L_0x007c;
    L_0x009b:
        r0 = r1;
        goto L_0x007c;
    L_0x009d:
        r0 = move-exception;
        r0 = org.mozilla.javascript.ScriptRuntime.NaNobj;
        goto L_0x000a;
    L_0x00a2:
        r2 = r1;
        goto L_0x006e;
    L_0x00a4:
        r1 = r3;
        goto L_0x006a;
    L_0x00a6:
        r3 = r6;
        goto L_0x0039;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.NativeGlobal.js_parseFloat(java.lang.Object[]):java.lang.Object");
    }

    static Object js_parseInt(Object[] objArr) {
        int i = 0;
        String scriptRuntime = ScriptRuntime.toString(objArr, 0);
        int toInt32 = ScriptRuntime.toInt32(objArr, Id_decodeURI);
        int length = scriptRuntime.length();
        if (length == 0) {
            return ScriptRuntime.NaNobj;
        }
        int i2;
        char charAt;
        double stringToNumber;
        int i3 = 0;
        do {
            char charAt2 = scriptRuntime.charAt(i3);
            if (!ScriptRuntime.isStrWhiteSpaceChar(charAt2)) {
                break;
            }
            i3 += Id_decodeURI;
        } while (i3 < length);
        if (charAt2 != '+') {
            if (charAt2 == '-') {
                i = Id_decodeURI;
            }
            if (i == 0) {
                i2 = i;
                if (toInt32 != 0) {
                    i = -1;
                } else if (toInt32 >= Id_decodeURIComponent || toInt32 > 36) {
                    return ScriptRuntime.NaNobj;
                } else {
                    if (toInt32 == 16 && length - i3 > Id_decodeURI && scriptRuntime.charAt(i3) == '0') {
                        char charAt3 = scriptRuntime.charAt(i3 + Id_decodeURI);
                        if (charAt3 == 'x' || charAt3 == 'X') {
                            i3 += Id_decodeURIComponent;
                            i = toInt32;
                        }
                    }
                    i = toInt32;
                }
                if (i == -1) {
                    i = Id_parseFloat;
                    if (length - i3 > Id_decodeURI && scriptRuntime.charAt(i3) == '0') {
                        charAt = scriptRuntime.charAt(i3 + Id_decodeURI);
                        if (charAt != 'x' || charAt == 'X') {
                            i3 += Id_decodeURIComponent;
                            i = 16;
                        } else if ('0' <= charAt && charAt <= '9') {
                            i = Id_isNaN;
                            i3 += Id_decodeURI;
                        }
                    }
                }
                stringToNumber = ScriptRuntime.stringToNumber(scriptRuntime, i3, i);
                if (i2 != 0) {
                    stringToNumber = -stringToNumber;
                }
                return ScriptRuntime.wrapNumber(stringToNumber);
            }
        }
        i3 += Id_decodeURI;
        i2 = i;
        if (toInt32 != 0) {
            if (toInt32 >= Id_decodeURIComponent) {
            }
            return ScriptRuntime.NaNobj;
        }
        i = -1;
        if (i == -1) {
            i = Id_parseFloat;
            charAt = scriptRuntime.charAt(i3 + Id_decodeURI);
            if (charAt != 'x') {
            }
            i3 += Id_decodeURIComponent;
            i = 16;
        }
        stringToNumber = ScriptRuntime.stringToNumber(scriptRuntime, i3, i);
        if (i2 != 0) {
            stringToNumber = -stringToNumber;
        }
        return ScriptRuntime.wrapNumber(stringToNumber);
    }

    private Object js_unescape(Object[] objArr) {
        String scriptRuntime = ScriptRuntime.toString(objArr, 0);
        int indexOf = scriptRuntime.indexOf(37);
        if (indexOf < 0) {
            return scriptRuntime;
        }
        int length = scriptRuntime.length();
        char[] toCharArray = scriptRuntime.toCharArray();
        int i = indexOf;
        while (indexOf != length) {
            char c = toCharArray[indexOf];
            int i2 = indexOf + Id_decodeURI;
            if (c == '%' && i2 != length) {
                int i3;
                if (toCharArray[i2] == 'u') {
                    i3 = i2 + Id_decodeURI;
                    indexOf = i2 + Id_escape;
                } else {
                    indexOf = i2 + Id_decodeURIComponent;
                    i3 = i2;
                }
                if (indexOf <= length) {
                    int i4 = 0;
                    for (i3 = 
/*
Method generation error in method: org.mozilla.javascript.NativeGlobal.js_unescape(java.lang.Object[]):java.lang.Object
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r3_2 'i3' int) = (r3_1 'i3' int), (r3_5 'i3' int) binds: {(r3_5 'i3' int)=B:14:0x0038, (r3_1 'i3' int)=B:9:0x0026} in method: org.mozilla.javascript.NativeGlobal.js_unescape(java.lang.Object[]):java.lang.Object
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:230)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:184)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:118)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:57)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:93)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:217)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:61)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:87)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:53)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:328)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:265)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:228)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:118)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:83)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:19)
	at jadx.core.ProcessClass.process(ProcessClass.java:43)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:286)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:173)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:534)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:518)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:224)
	... 34 more

*/

                    private static int oneUcs4ToUtf8Char(byte[] bArr, int i) {
                        if ((i & -128) == 0) {
                            bArr[0] = (byte) i;
                            return Id_decodeURI;
                        }
                        int i2 = i >>> Id_parseInt;
                        int i3 = Id_decodeURIComponent;
                        int i4 = i2;
                        while (i4 != 0) {
                            i4 >>>= Id_escape;
                            i3 += Id_decodeURI;
                        }
                        i4 = i3;
                        while (true) {
                            i4--;
                            if (i4 > 0) {
                                bArr[i4] = (byte) ((i & 63) | Token.RESERVED);
                                i >>>= Id_eval;
                            } else {
                                bArr[0] = (byte) ((256 - (Id_decodeURI << (8 - i3))) + i);
                                return i3;
                            }
                        }
                    }

                    private static char toHexChar(int i) {
                        if ((i >> Id_encodeURIComponent) != 0) {
                            Kit.codeBug();
                        }
                        return (char) (i < Id_parseFloat ? i + 48 : (i - 10) + 65);
                    }

                    private static int unHex(char c) {
                        return ('A' > c || c > 'F') ? ('a' > c || c > 'f') ? ('0' > c || c > '9') ? -1 : c - 48 : (c - 97) + Id_parseFloat : (c - 65) + Id_parseFloat;
                    }

                    private static int unHex(char c, char c2) {
                        int unHex = unHex(c);
                        int unHex2 = unHex(c2);
                        return (unHex < 0 || unHex2 < 0) ? -1 : (unHex << Id_encodeURIComponent) | unHex2;
                    }

                    private static EcmaError uriError() {
                        return ScriptRuntime.constructError("URIError", ScriptRuntime.getMessage0("msg.bad.uri"));
                    }

                    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
                        boolean z = true;
                        if (idFunctionObject.hasTag(FTAG)) {
                            int methodId = idFunctionObject.methodId();
                            String scriptRuntime;
                            switch (methodId) {
                                case Id_decodeURI /*1*/:
                                case Id_decodeURIComponent /*2*/:
                                    scriptRuntime = ScriptRuntime.toString(objArr, 0);
                                    if (methodId != Id_decodeURI) {
                                        z = false;
                                    }
                                    return decode(scriptRuntime, z);
                                case Id_encodeURI /*3*/:
                                case Id_encodeURIComponent /*4*/:
                                    scriptRuntime = ScriptRuntime.toString(objArr, 0);
                                    if (methodId != Id_encodeURI) {
                                        z = false;
                                    }
                                    return encode(scriptRuntime, z);
                                case Id_escape /*5*/:
                                    return js_escape(objArr);
                                case Id_eval /*6*/:
                                    return js_eval(context, scriptable, objArr);
                                case Id_isFinite /*7*/:
                                    return objArr.length < Id_decodeURI ? Boolean.FALSE : NativeNumber.isFinite(objArr[0]);
                                case Id_isNaN /*8*/:
                                    if (objArr.length >= Id_decodeURI) {
                                        double toNumber = ScriptRuntime.toNumber(objArr[0]);
                                        if (toNumber == toNumber) {
                                            z = false;
                                        }
                                    }
                                    return ScriptRuntime.wrapBoolean(z);
                                case Id_isXMLName /*9*/:
                                    return ScriptRuntime.wrapBoolean(XMLLib.extractFromScope(scriptable).isXMLName(context, objArr.length == 0 ? Undefined.instance : objArr[0]));
                                case Id_parseFloat /*10*/:
                                    return js_parseFloat(objArr);
                                case Id_parseInt /*11*/:
                                    return js_parseInt(objArr);
                                case Id_unescape /*12*/:
                                    return js_unescape(objArr);
                                case LAST_SCOPE_FUNCTION_ID /*13*/:
                                    return ScriptRuntime.uneval(context, scriptable, objArr.length != 0 ? objArr[0] : Undefined.instance);
                                case Id_new_CommonError /*14*/:
                                    return NativeError.make(context, scriptable, idFunctionObject, objArr);
                            }
                        }
                        throw idFunctionObject.unknown();
                    }
                }
