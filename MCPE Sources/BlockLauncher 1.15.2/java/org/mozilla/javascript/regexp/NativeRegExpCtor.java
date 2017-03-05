package org.mozilla.javascript.regexp;

import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel.Builtins;
import org.mozilla.javascript.Undefined;

class NativeRegExpCtor extends BaseFunction {
    private static final int DOLLAR_ID_BASE = 12;
    private static final int Id_AMPERSAND = 6;
    private static final int Id_BACK_QUOTE = 10;
    private static final int Id_DOLLAR_1 = 13;
    private static final int Id_DOLLAR_2 = 14;
    private static final int Id_DOLLAR_3 = 15;
    private static final int Id_DOLLAR_4 = 16;
    private static final int Id_DOLLAR_5 = 17;
    private static final int Id_DOLLAR_6 = 18;
    private static final int Id_DOLLAR_7 = 19;
    private static final int Id_DOLLAR_8 = 20;
    private static final int Id_DOLLAR_9 = 21;
    private static final int Id_PLUS = 8;
    private static final int Id_QUOTE = 12;
    private static final int Id_STAR = 2;
    private static final int Id_UNDERSCORE = 4;
    private static final int Id_input = 3;
    private static final int Id_lastMatch = 5;
    private static final int Id_lastParen = 7;
    private static final int Id_leftContext = 9;
    private static final int Id_multiline = 1;
    private static final int Id_rightContext = 11;
    private static final int MAX_INSTANCE_ID = 21;
    static final long serialVersionUID = -5733330028285400526L;
    private int inputAttr = Id_UNDERSCORE;
    private int multilineAttr = Id_UNDERSCORE;
    private int starAttr = Id_UNDERSCORE;
    private int underscoreAttr = Id_UNDERSCORE;

    NativeRegExpCtor() {
    }

    private static RegExpImpl getImpl() {
        return (RegExpImpl) ScriptRuntime.getRegExpProxy(Context.getCurrentContext());
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        return (objArr.length > 0 && (objArr[0] instanceof NativeRegExp) && (objArr.length == Id_multiline || objArr[Id_multiline] == Undefined.instance)) ? objArr[0] : construct(context, scriptable, objArr);
    }

    public Scriptable construct(Context context, Scriptable scriptable, Object[] objArr) {
        Scriptable nativeRegExp = new NativeRegExp();
        nativeRegExp.compile(context, scriptable, objArr);
        ScriptRuntime.setBuiltinProtoAndParent(nativeRegExp, scriptable, Builtins.RegExp);
        return nativeRegExp;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findInstanceIdInfo(java.lang.String r9) {
        /*
        r8 = this;
        r0 = 5;
        r1 = 4;
        r4 = 1;
        r6 = 36;
        r2 = 0;
        r3 = 0;
        r5 = r9.length();
        switch(r5) {
            case 2: goto L_0x0021;
            case 3: goto L_0x000e;
            case 4: goto L_0x000e;
            case 5: goto L_0x00b0;
            case 6: goto L_0x000e;
            case 7: goto L_0x000e;
            case 8: goto L_0x000e;
            case 9: goto L_0x00b8;
            case 10: goto L_0x000e;
            case 11: goto L_0x00dc;
            case 12: goto L_0x00e5;
            default: goto L_0x000e;
        };
    L_0x000e:
        r1 = r2;
    L_0x000f:
        if (r3 == 0) goto L_0x001a;
    L_0x0011:
        if (r3 == r9) goto L_0x001a;
    L_0x0013:
        r3 = r3.equals(r9);
        if (r3 != 0) goto L_0x001a;
    L_0x0019:
        r1 = r2;
    L_0x001a:
        if (r1 != 0) goto L_0x00ee;
    L_0x001c:
        r0 = super.findInstanceIdInfo(r9);
    L_0x0020:
        return r0;
    L_0x0021:
        r4 = r9.charAt(r4);
        switch(r4) {
            case 38: goto L_0x002a;
            case 39: goto L_0x0032;
            case 42: goto L_0x003b;
            case 43: goto L_0x0043;
            case 49: goto L_0x004c;
            case 50: goto L_0x0055;
            case 51: goto L_0x005e;
            case 52: goto L_0x0067;
            case 53: goto L_0x0070;
            case 54: goto L_0x0079;
            case 55: goto L_0x0082;
            case 56: goto L_0x008b;
            case 57: goto L_0x0094;
            case 95: goto L_0x009e;
            case 96: goto L_0x00a6;
            default: goto L_0x0028;
        };
    L_0x0028:
        r1 = r2;
        goto L_0x000f;
    L_0x002a:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0030:
        r1 = 6;
        goto L_0x001a;
    L_0x0032:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0038:
        r1 = 12;
        goto L_0x001a;
    L_0x003b:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0041:
        r1 = 2;
        goto L_0x001a;
    L_0x0043:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0049:
        r1 = 8;
        goto L_0x001a;
    L_0x004c:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0052:
        r1 = 13;
        goto L_0x001a;
    L_0x0055:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x005b:
        r1 = 14;
        goto L_0x001a;
    L_0x005e:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0064:
        r1 = 15;
        goto L_0x001a;
    L_0x0067:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x006d:
        r1 = 16;
        goto L_0x001a;
    L_0x0070:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0076:
        r1 = 17;
        goto L_0x001a;
    L_0x0079:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x007f:
        r1 = 18;
        goto L_0x001a;
    L_0x0082:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0088:
        r1 = 19;
        goto L_0x001a;
    L_0x008b:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x0091:
        r1 = 20;
        goto L_0x001a;
    L_0x0094:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x009a:
        r1 = 21;
        goto L_0x001a;
    L_0x009e:
        r4 = r9.charAt(r2);
        if (r4 != r6) goto L_0x000e;
    L_0x00a4:
        goto L_0x001a;
    L_0x00a6:
        r1 = r9.charAt(r2);
        if (r1 != r6) goto L_0x000e;
    L_0x00ac:
        r1 = 10;
        goto L_0x001a;
    L_0x00b0:
        r1 = "input";
        r3 = 3;
        r7 = r1;
        r1 = r3;
        r3 = r7;
        goto L_0x000f;
    L_0x00b8:
        r1 = r9.charAt(r1);
        r5 = 77;
        if (r1 != r5) goto L_0x00c6;
    L_0x00c0:
        r1 = "lastMatch";
        r3 = r1;
        r1 = r0;
        goto L_0x000f;
    L_0x00c6:
        r5 = 80;
        if (r1 != r5) goto L_0x00d2;
    L_0x00ca:
        r1 = "lastParen";
        r3 = 7;
        r7 = r1;
        r1 = r3;
        r3 = r7;
        goto L_0x000f;
    L_0x00d2:
        r5 = 105; // 0x69 float:1.47E-43 double:5.2E-322;
        if (r1 != r5) goto L_0x000e;
    L_0x00d6:
        r1 = "multiline";
        r3 = r1;
        r1 = r4;
        goto L_0x000f;
    L_0x00dc:
        r1 = "leftContext";
        r3 = 9;
        r7 = r1;
        r1 = r3;
        r3 = r7;
        goto L_0x000f;
    L_0x00e5:
        r1 = "rightContext";
        r3 = 11;
        r7 = r1;
        r1 = r3;
        r3 = r7;
        goto L_0x000f;
    L_0x00ee:
        switch(r1) {
            case 1: goto L_0x00fc;
            case 2: goto L_0x00ff;
            case 3: goto L_0x0102;
            case 4: goto L_0x0105;
            default: goto L_0x00f1;
        };
    L_0x00f1:
        r2 = super.getMaxInstanceId();
        r1 = r1 + r2;
        r0 = org.mozilla.javascript.IdScriptableObject.instanceIdInfo(r0, r1);
        goto L_0x0020;
    L_0x00fc:
        r0 = r8.multilineAttr;
        goto L_0x00f1;
    L_0x00ff:
        r0 = r8.starAttr;
        goto L_0x00f1;
    L_0x0102:
        r0 = r8.inputAttr;
        goto L_0x00f1;
    L_0x0105:
        r0 = r8.underscoreAttr;
        goto L_0x00f1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.regexp.NativeRegExpCtor.findInstanceIdInfo(java.lang.String):int");
    }

    public int getArity() {
        return Id_STAR;
    }

    public String getFunctionName() {
        return "RegExp";
    }

    protected String getInstanceIdName(int i) {
        int maxInstanceId = i - super.getMaxInstanceId();
        if (Id_multiline > maxInstanceId || maxInstanceId > MAX_INSTANCE_ID) {
            return super.getInstanceIdName(i);
        }
        switch (maxInstanceId) {
            case Id_multiline /*1*/:
                return "multiline";
            case Id_STAR /*2*/:
                return "$*";
            case Id_input /*3*/:
                return "input";
            case Id_UNDERSCORE /*4*/:
                return "$_";
            case Id_lastMatch /*5*/:
                return "lastMatch";
            case Id_AMPERSAND /*6*/:
                return "$&";
            case Id_lastParen /*7*/:
                return "lastParen";
            case Id_PLUS /*8*/:
                return "$+";
            case Id_leftContext /*9*/:
                return "leftContext";
            case Id_BACK_QUOTE /*10*/:
                return "$`";
            case Id_rightContext /*11*/:
                return "rightContext";
            case Id_QUOTE /*12*/:
                return "$'";
            default:
                maxInstanceId = (maxInstanceId - 12) - 1;
                char[] cArr = new char[Id_STAR];
                cArr[0] = '$';
                cArr[Id_multiline] = (char) (maxInstanceId + 49);
                return new String(cArr);
        }
    }

    protected Object getInstanceIdValue(int i) {
        int maxInstanceId = i - super.getMaxInstanceId();
        if (Id_multiline > maxInstanceId || maxInstanceId > MAX_INSTANCE_ID) {
            return super.getInstanceIdValue(i);
        }
        Object obj;
        RegExpImpl impl = getImpl();
        switch (maxInstanceId) {
            case Id_multiline /*1*/:
            case Id_STAR /*2*/:
                return ScriptRuntime.wrapBoolean(impl.multiline);
            case Id_input /*3*/:
            case Id_UNDERSCORE /*4*/:
                obj = impl.input;
                break;
            case Id_lastMatch /*5*/:
            case Id_AMPERSAND /*6*/:
                obj = impl.lastMatch;
                break;
            case Id_lastParen /*7*/:
            case Id_PLUS /*8*/:
                obj = impl.lastParen;
                break;
            case Id_leftContext /*9*/:
            case Id_BACK_QUOTE /*10*/:
                obj = impl.leftContext;
                break;
            case Id_rightContext /*11*/:
            case Id_QUOTE /*12*/:
                obj = impl.rightContext;
                break;
            default:
                obj = impl.getParenSubString((maxInstanceId - 12) - 1);
                break;
        }
        return obj == null ? BuildConfig.FLAVOR : obj.toString();
    }

    public int getLength() {
        return Id_STAR;
    }

    protected int getMaxInstanceId() {
        return super.getMaxInstanceId() + MAX_INSTANCE_ID;
    }

    protected void setInstanceIdAttributes(int i, int i2) {
        int maxInstanceId = i - super.getMaxInstanceId();
        switch (maxInstanceId) {
            case Id_multiline /*1*/:
                this.multilineAttr = i2;
                return;
            case Id_STAR /*2*/:
                this.starAttr = i2;
                return;
            case Id_input /*3*/:
                this.inputAttr = i2;
                return;
            case Id_UNDERSCORE /*4*/:
                this.underscoreAttr = i2;
                return;
            case Id_lastMatch /*5*/:
            case Id_AMPERSAND /*6*/:
            case Id_lastParen /*7*/:
            case Id_PLUS /*8*/:
            case Id_leftContext /*9*/:
            case Id_BACK_QUOTE /*10*/:
            case Id_rightContext /*11*/:
            case Id_QUOTE /*12*/:
                return;
            default:
                maxInstanceId = (maxInstanceId - 12) - 1;
                if (maxInstanceId < 0 || maxInstanceId > Id_PLUS) {
                    super.setInstanceIdAttributes(i, i2);
                    return;
                }
                return;
        }
    }

    protected void setInstanceIdValue(int i, Object obj) {
        int maxInstanceId = i - super.getMaxInstanceId();
        switch (maxInstanceId) {
            case Id_multiline /*1*/:
            case Id_STAR /*2*/:
                getImpl().multiline = ScriptRuntime.toBoolean(obj);
                return;
            case Id_input /*3*/:
            case Id_UNDERSCORE /*4*/:
                getImpl().input = ScriptRuntime.toString(obj);
                return;
            case Id_lastMatch /*5*/:
            case Id_AMPERSAND /*6*/:
            case Id_lastParen /*7*/:
            case Id_PLUS /*8*/:
            case Id_leftContext /*9*/:
            case Id_BACK_QUOTE /*10*/:
            case Id_rightContext /*11*/:
            case Id_QUOTE /*12*/:
                return;
            default:
                maxInstanceId = (maxInstanceId - 12) - 1;
                if (maxInstanceId < 0 || maxInstanceId > Id_PLUS) {
                    super.setInstanceIdValue(i, obj);
                    return;
                }
                return;
        }
    }
}
