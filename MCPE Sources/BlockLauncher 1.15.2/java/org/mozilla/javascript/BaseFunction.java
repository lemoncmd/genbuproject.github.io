package org.mozilla.javascript;

import net.hockeyapp.android.BuildConfig;

public class BaseFunction extends IdScriptableObject implements Function {
    private static final Object FUNCTION_TAG = "Function";
    private static final int Id_apply = 4;
    private static final int Id_arguments = 5;
    private static final int Id_arity = 2;
    private static final int Id_bind = 6;
    private static final int Id_call = 5;
    private static final int Id_constructor = 1;
    private static final int Id_length = 1;
    private static final int Id_name = 3;
    private static final int Id_prototype = 4;
    private static final int Id_toSource = 3;
    private static final int Id_toString = 2;
    private static final int MAX_INSTANCE_ID = 5;
    private static final int MAX_PROTOTYPE_ID = 6;
    static final long serialVersionUID = 5311394446546053859L;
    private int argumentsAttributes = MAX_PROTOTYPE_ID;
    private Object argumentsObj = NOT_FOUND;
    private Object prototypeProperty;
    private int prototypePropertyAttributes = MAX_PROTOTYPE_ID;

    public BaseFunction(Scriptable scriptable, Scriptable scriptable2) {
        super(scriptable, scriptable2);
    }

    private Object getArguments() {
        Object defaultGet = defaultHas("arguments") ? defaultGet("arguments") : this.argumentsObj;
        if (defaultGet != NOT_FOUND) {
            return defaultGet;
        }
        Scriptable findFunctionActivation = ScriptRuntime.findFunctionActivation(Context.getContext(), this);
        return findFunctionActivation == null ? null : findFunctionActivation.get("arguments", findFunctionActivation);
    }

    static void init(Scriptable scriptable, boolean z) {
        BaseFunction baseFunction = new BaseFunction();
        baseFunction.prototypePropertyAttributes = 7;
        baseFunction.exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    static boolean isApply(IdFunctionObject idFunctionObject) {
        return idFunctionObject.hasTag(FUNCTION_TAG) && idFunctionObject.methodId() == Id_prototype;
    }

    static boolean isApplyOrCall(IdFunctionObject idFunctionObject) {
        if (idFunctionObject.hasTag(FUNCTION_TAG)) {
            switch (idFunctionObject.methodId()) {
                case Id_prototype /*4*/:
                case MAX_INSTANCE_ID /*5*/:
                    return true;
            }
        }
        return false;
    }

    private static Object jsConstructor(Context context, Scriptable scriptable, Object[] objArr) {
        int length = objArr.length;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("function ");
        if (context.getLanguageVersion() != Token.FOR) {
            stringBuilder.append("anonymous");
        }
        stringBuilder.append('(');
        for (int i = 0; i < length - 1; i += Id_length) {
            if (i > 0) {
                stringBuilder.append(',');
            }
            stringBuilder.append(ScriptRuntime.toString(objArr[i]));
        }
        stringBuilder.append(") {");
        if (length != 0) {
            stringBuilder.append(ScriptRuntime.toString(objArr[length - 1]));
        }
        stringBuilder.append("\n}");
        String stringBuilder2 = stringBuilder.toString();
        int[] iArr = new int[Id_length];
        String sourcePositionFromStack = Context.getSourcePositionFromStack(iArr);
        if (sourcePositionFromStack == null) {
            sourcePositionFromStack = "<eval'ed string>";
            iArr[0] = Id_length;
        }
        String makeUrlForGeneratedScript = ScriptRuntime.makeUrlForGeneratedScript(false, sourcePositionFromStack, iArr[0]);
        Scriptable topLevelScope = ScriptableObject.getTopLevelScope(scriptable);
        ErrorReporter forEval = DefaultErrorReporter.forEval(context.getErrorReporter());
        Evaluator createInterpreter = Context.createInterpreter();
        if (createInterpreter != null) {
            return context.compileFunction(topLevelScope, stringBuilder2, createInterpreter, forEval, makeUrlForGeneratedScript, Id_length, null);
        }
        throw new JavaScriptException("Interpreter not present", sourcePositionFromStack, iArr[0]);
    }

    private BaseFunction realFunction(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        Object defaultValue = scriptable.getDefaultValue(ScriptRuntime.FunctionClass);
        if (defaultValue instanceof Delegator) {
            defaultValue = ((Delegator) defaultValue).getDelegee();
        }
        if (defaultValue instanceof BaseFunction) {
            return (BaseFunction) defaultValue;
        }
        throw ScriptRuntime.typeError1("msg.incompat.call", idFunctionObject.getFunctionName());
    }

    private synchronized Object setupDefaultPrototype() {
        Object obj;
        if (this.prototypeProperty != null) {
            obj = this.prototypeProperty;
        } else {
            Scriptable nativeObject = new NativeObject();
            nativeObject.defineProperty("constructor", (Object) this, (int) Id_toString);
            this.prototypeProperty = nativeObject;
            Scriptable objectPrototype = ScriptableObject.getObjectPrototype(this);
            if (objectPrototype != nativeObject) {
                nativeObject.setPrototype(objectPrototype);
            }
        }
        return obj;
    }

    public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        return Undefined.instance;
    }

    public Scriptable construct(Context context, Scriptable scriptable, Object[] objArr) {
        Scriptable createObject = createObject(context, scriptable);
        Object call;
        if (createObject != null) {
            call = call(context, scriptable, createObject, objArr);
            return call instanceof Scriptable ? (Scriptable) call : createObject;
        } else {
            call = call(context, scriptable, null, objArr);
            if (call instanceof Scriptable) {
                Scriptable scriptable2 = (Scriptable) call;
                if (scriptable2.getPrototype() == null) {
                    createObject = getClassPrototype();
                    if (scriptable2 != createObject) {
                        scriptable2.setPrototype(createObject);
                    }
                }
                if (scriptable2.getParentScope() != null) {
                    return scriptable2;
                }
                createObject = getParentScope();
                if (scriptable2 == createObject) {
                    return scriptable2;
                }
                scriptable2.setParentScope(createObject);
                return scriptable2;
            }
            throw new IllegalStateException("Bad implementaion of call as constructor, name=" + getFunctionName() + " in " + getClass().getName());
        }
    }

    public Scriptable createObject(Context context, Scriptable scriptable) {
        Scriptable nativeObject = new NativeObject();
        nativeObject.setPrototype(getClassPrototype());
        nativeObject.setParentScope(getParentScope());
        return nativeObject;
    }

    String decompile(int i, int i2) {
        StringBuilder stringBuilder = new StringBuilder();
        Object obj = (i2 & Id_length) != 0 ? Id_length : null;
        if (obj == null) {
            stringBuilder.append("function ");
            stringBuilder.append(getFunctionName());
            stringBuilder.append("() {\n\t");
        }
        stringBuilder.append("[native code, arity=");
        stringBuilder.append(getArity());
        stringBuilder.append("]\n");
        if (obj == null) {
            stringBuilder.append("}\n");
        }
        return stringBuilder.toString();
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        boolean z = false;
        if (!idFunctionObject.hasTag(FUNCTION_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        switch (methodId) {
            case Id_length /*1*/:
                return jsConstructor(context, scriptable, objArr);
            case Id_toString /*2*/:
                return realFunction(scriptable2, idFunctionObject).decompile(ScriptRuntime.toInt32(objArr, 0), 0);
            case Id_toSource /*3*/:
                int i;
                int i2;
                BaseFunction realFunction = realFunction(scriptable2, idFunctionObject);
                if (objArr.length != 0) {
                    methodId = ScriptRuntime.toInt32(objArr[0]);
                    if (methodId >= 0) {
                        i = methodId;
                    } else {
                        i = 0;
                        i2 = Id_toString;
                    }
                } else {
                    i = 0;
                    i2 = Id_toString;
                }
                return realFunction.decompile(i, i2);
            case Id_prototype /*4*/:
            case MAX_INSTANCE_ID /*5*/:
                if (methodId == Id_prototype) {
                    z = true;
                }
                return ScriptRuntime.applyOrCall(z, context, scriptable, scriptable2, objArr);
            case MAX_PROTOTYPE_ID /*6*/:
                if (scriptable2 instanceof Callable) {
                    Scriptable toObjectOrNull;
                    Object[] objArr2;
                    Callable callable = (Callable) scriptable2;
                    methodId = objArr.length;
                    if (methodId > 0) {
                        toObjectOrNull = ScriptRuntime.toObjectOrNull(context, objArr[0], scriptable);
                        objArr2 = new Object[(methodId - 1)];
                        System.arraycopy(objArr, Id_length, objArr2, 0, methodId - 1);
                    } else {
                        toObjectOrNull = null;
                        objArr2 = ScriptRuntime.emptyArgs;
                    }
                    return new BoundFunction(context, scriptable, callable, toObjectOrNull, objArr2);
                }
                throw ScriptRuntime.notFunctionError(scriptable2);
            default:
                throw new IllegalArgumentException(String.valueOf(methodId));
        }
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        idFunctionObject.setPrototype(this);
        super.fillConstructorProperties(idFunctionObject);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findInstanceIdInfo(java.lang.String r6) {
        /*
        r5 = this;
        r0 = 0;
        r1 = 0;
        r2 = r6.length();
        switch(r2) {
            case 4: goto L_0x001d;
            case 5: goto L_0x0024;
            case 6: goto L_0x002b;
            case 7: goto L_0x0009;
            case 8: goto L_0x0009;
            case 9: goto L_0x0032;
            default: goto L_0x0009;
        };
    L_0x0009:
        r2 = r1;
        r1 = r0;
    L_0x000b:
        if (r2 == 0) goto L_0x0016;
    L_0x000d:
        if (r2 == r6) goto L_0x0016;
    L_0x000f:
        r2 = r2.equals(r6);
        if (r2 != 0) goto L_0x0016;
    L_0x0015:
        r1 = r0;
    L_0x0016:
        if (r1 != 0) goto L_0x004c;
    L_0x0018:
        r0 = super.findInstanceIdInfo(r6);
    L_0x001c:
        return r0;
    L_0x001d:
        r1 = "name";
        r2 = 3;
        r4 = r1;
        r1 = r2;
        r2 = r4;
        goto L_0x000b;
    L_0x0024:
        r1 = "arity";
        r2 = 2;
        r4 = r1;
        r1 = r2;
        r2 = r4;
        goto L_0x000b;
    L_0x002b:
        r1 = "length";
        r2 = 1;
        r4 = r1;
        r1 = r2;
        r2 = r4;
        goto L_0x000b;
    L_0x0032:
        r2 = r6.charAt(r0);
        r3 = 97;
        if (r2 != r3) goto L_0x0041;
    L_0x003a:
        r1 = "arguments";
        r2 = 5;
        r4 = r1;
        r1 = r2;
        r2 = r4;
        goto L_0x000b;
    L_0x0041:
        r3 = 112; // 0x70 float:1.57E-43 double:5.53E-322;
        if (r2 != r3) goto L_0x0009;
    L_0x0045:
        r1 = "prototype";
        r2 = 4;
        r4 = r1;
        r1 = r2;
        r2 = r4;
        goto L_0x000b;
    L_0x004c:
        switch(r1) {
            case 1: goto L_0x0055;
            case 2: goto L_0x0055;
            case 3: goto L_0x0055;
            case 4: goto L_0x005b;
            case 5: goto L_0x0064;
            default: goto L_0x004f;
        };
    L_0x004f:
        r0 = new java.lang.IllegalStateException;
        r0.<init>();
        throw r0;
    L_0x0055:
        r0 = 7;
    L_0x0056:
        r0 = org.mozilla.javascript.IdScriptableObject.instanceIdInfo(r0, r1);
        goto L_0x001c;
    L_0x005b:
        r2 = r5.hasPrototypeProperty();
        if (r2 == 0) goto L_0x001c;
    L_0x0061:
        r0 = r5.prototypePropertyAttributes;
        goto L_0x0056;
    L_0x0064:
        r0 = r5.argumentsAttributes;
        goto L_0x0056;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.BaseFunction.findInstanceIdInfo(java.lang.String):int");
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected int findPrototypeId(java.lang.String r7) {
        /*
        r6 = this;
        r2 = 3;
        r0 = 0;
        r1 = 0;
        r3 = r7.length();
        switch(r3) {
            case 4: goto L_0x0017;
            case 5: goto L_0x0031;
            case 6: goto L_0x000a;
            case 7: goto L_0x000a;
            case 8: goto L_0x0038;
            case 9: goto L_0x000a;
            case 10: goto L_0x000a;
            case 11: goto L_0x0051;
            default: goto L_0x000a;
        };
    L_0x000a:
        r2 = r1;
        r1 = r0;
    L_0x000c:
        if (r2 == 0) goto L_0x0058;
    L_0x000e:
        if (r2 == r7) goto L_0x0058;
    L_0x0010:
        r2 = r2.equals(r7);
        if (r2 != 0) goto L_0x0058;
    L_0x0016:
        return r0;
    L_0x0017:
        r2 = r7.charAt(r0);
        r3 = 98;
        if (r2 != r3) goto L_0x0026;
    L_0x001f:
        r1 = "bind";
        r2 = 6;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000c;
    L_0x0026:
        r3 = 99;
        if (r2 != r3) goto L_0x000a;
    L_0x002a:
        r1 = "call";
        r2 = 5;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000c;
    L_0x0031:
        r1 = "apply";
        r2 = 4;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000c;
    L_0x0038:
        r3 = r7.charAt(r2);
        r4 = 111; // 0x6f float:1.56E-43 double:5.5E-322;
        if (r3 != r4) goto L_0x0046;
    L_0x0040:
        r1 = "toSource";
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000c;
    L_0x0046:
        r2 = 116; // 0x74 float:1.63E-43 double:5.73E-322;
        if (r3 != r2) goto L_0x000a;
    L_0x004a:
        r1 = "toString";
        r2 = 2;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000c;
    L_0x0051:
        r1 = "constructor";
        r2 = 1;
        r5 = r1;
        r1 = r2;
        r2 = r5;
        goto L_0x000c;
    L_0x0058:
        r0 = r1;
        goto L_0x0016;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.mozilla.javascript.BaseFunction.findPrototypeId(java.lang.String):int");
    }

    public int getArity() {
        return 0;
    }

    public String getClassName() {
        return "Function";
    }

    protected Scriptable getClassPrototype() {
        Object prototypeProperty = getPrototypeProperty();
        return prototypeProperty instanceof Scriptable ? (Scriptable) prototypeProperty : ScriptableObject.getObjectPrototype(this);
    }

    public String getFunctionName() {
        return BuildConfig.FLAVOR;
    }

    protected String getInstanceIdName(int i) {
        switch (i) {
            case Id_length /*1*/:
                return Name.LENGTH;
            case Id_toString /*2*/:
                return "arity";
            case Id_toSource /*3*/:
                return "name";
            case Id_prototype /*4*/:
                return "prototype";
            case MAX_INSTANCE_ID /*5*/:
                return "arguments";
            default:
                return super.getInstanceIdName(i);
        }
    }

    protected Object getInstanceIdValue(int i) {
        switch (i) {
            case Id_length /*1*/:
                return ScriptRuntime.wrapInt(getLength());
            case Id_toString /*2*/:
                return ScriptRuntime.wrapInt(getArity());
            case Id_toSource /*3*/:
                return getFunctionName();
            case Id_prototype /*4*/:
                return getPrototypeProperty();
            case MAX_INSTANCE_ID /*5*/:
                return getArguments();
            default:
                return super.getInstanceIdValue(i);
        }
    }

    public int getLength() {
        return 0;
    }

    protected int getMaxInstanceId() {
        return MAX_INSTANCE_ID;
    }

    protected Object getPrototypeProperty() {
        UniqueTag uniqueTag = this.prototypeProperty;
        return uniqueTag == null ? this instanceof NativeFunction ? setupDefaultPrototype() : Undefined.instance : uniqueTag == UniqueTag.NULL_VALUE ? null : uniqueTag;
    }

    public String getTypeOf() {
        return avoidObjectDetection() ? "undefined" : "function";
    }

    public boolean hasInstance(Scriptable scriptable) {
        Object property = ScriptableObject.getProperty((Scriptable) this, "prototype");
        if (property instanceof Scriptable) {
            return ScriptRuntime.jsDelegatesTo(scriptable, (Scriptable) property);
        }
        throw ScriptRuntime.typeError1("msg.instanceof.bad.prototype", getFunctionName());
    }

    protected boolean hasPrototypeProperty() {
        return this.prototypeProperty != null || (this instanceof NativeFunction);
    }

    protected void initPrototypeId(int i) {
        String str;
        int i2 = Id_length;
        switch (i) {
            case Id_length /*1*/:
                str = "constructor";
                break;
            case Id_toString /*2*/:
                i2 = 0;
                str = "toString";
                break;
            case Id_toSource /*3*/:
                str = "toSource";
                break;
            case Id_prototype /*4*/:
                i2 = Id_toString;
                str = "apply";
                break;
            case MAX_INSTANCE_ID /*5*/:
                str = "call";
                break;
            case MAX_PROTOTYPE_ID /*6*/:
                str = "bind";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(FUNCTION_TAG, i, str, i2);
    }

    public void setImmunePrototypeProperty(Object obj) {
        if ((this.prototypePropertyAttributes & Id_length) != 0) {
            throw new IllegalStateException();
        }
        if (obj == null) {
            obj = UniqueTag.NULL_VALUE;
        }
        this.prototypeProperty = obj;
        this.prototypePropertyAttributes = 7;
    }

    protected void setInstanceIdAttributes(int i, int i2) {
        switch (i) {
            case Id_prototype /*4*/:
                this.prototypePropertyAttributes = i2;
                return;
            case MAX_INSTANCE_ID /*5*/:
                this.argumentsAttributes = i2;
                return;
            default:
                super.setInstanceIdAttributes(i, i2);
                return;
        }
    }

    protected void setInstanceIdValue(int i, Object obj) {
        switch (i) {
            case Id_length /*1*/:
            case Id_toString /*2*/:
            case Id_toSource /*3*/:
                return;
            case Id_prototype /*4*/:
                if ((this.prototypePropertyAttributes & Id_length) == 0) {
                    if (obj == null) {
                        obj = UniqueTag.NULL_VALUE;
                    }
                    this.prototypeProperty = obj;
                    return;
                }
                return;
            case MAX_INSTANCE_ID /*5*/:
                if (obj == NOT_FOUND) {
                    Kit.codeBug();
                }
                if (defaultHas("arguments")) {
                    defaultPut("arguments", obj);
                    return;
                } else if ((this.argumentsAttributes & Id_length) == 0) {
                    this.argumentsObj = obj;
                    return;
                } else {
                    return;
                }
            default:
                super.setInstanceIdValue(i, obj);
                return;
        }
    }
}
