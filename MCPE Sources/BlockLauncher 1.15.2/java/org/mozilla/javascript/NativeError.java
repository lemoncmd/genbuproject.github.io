package org.mozilla.javascript;

import java.io.Serializable;
import java.lang.reflect.Method;
import net.hockeyapp.android.BuildConfig;

final class NativeError extends IdScriptableObject {
    private static final int ConstructorId_captureStackTrace = -1;
    public static final int DEFAULT_STACK_LIMIT = -1;
    private static final Method ERROR_DELEGATE_GET_STACK;
    private static final Method ERROR_DELEGATE_SET_STACK;
    private static final Object ERROR_TAG = "Error";
    private static final int Id_constructor = 1;
    private static final int Id_toSource = 3;
    private static final int Id_toString = 2;
    private static final int MAX_PROTOTYPE_ID = 3;
    private static final String STACK_HIDE_KEY = "_stackHide";
    static final long serialVersionUID = -5338413581437645187L;
    private RhinoException stackProvider;

    private static final class ProtoProps implements Serializable {
        static final Method GET_PREPARE_STACK;
        static final Method GET_STACK_LIMIT;
        static final String KEY = "_ErrorPrototypeProps";
        static final Method SET_PREPARE_STACK;
        static final Method SET_STACK_LIMIT;
        private static final long serialVersionUID = 1907180507775337939L;
        private Function prepareStackTrace;
        private int stackTraceLimit;

        static {
            try {
                Class[] clsArr = new Class[NativeError.Id_constructor];
                clsArr[0] = Scriptable.class;
                GET_STACK_LIMIT = ProtoProps.class.getMethod("getStackTraceLimit", clsArr);
                clsArr = new Class[NativeError.Id_toString];
                clsArr[0] = Scriptable.class;
                clsArr[NativeError.Id_constructor] = Object.class;
                SET_STACK_LIMIT = ProtoProps.class.getMethod("setStackTraceLimit", clsArr);
                clsArr = new Class[NativeError.Id_constructor];
                clsArr[0] = Scriptable.class;
                GET_PREPARE_STACK = ProtoProps.class.getMethod("getPrepareStackTrace", clsArr);
                clsArr = new Class[NativeError.Id_toString];
                clsArr[0] = Scriptable.class;
                clsArr[NativeError.Id_constructor] = Object.class;
                SET_PREPARE_STACK = ProtoProps.class.getMethod("setPrepareStackTrace", clsArr);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        private ProtoProps() {
            this.stackTraceLimit = NativeError.DEFAULT_STACK_LIMIT;
        }

        public Object getPrepareStackTrace(Scriptable scriptable) {
            Function prepareStackTrace = getPrepareStackTrace();
            return prepareStackTrace == null ? Undefined.instance : prepareStackTrace;
        }

        public Function getPrepareStackTrace() {
            return this.prepareStackTrace;
        }

        public int getStackTraceLimit() {
            return this.stackTraceLimit;
        }

        public Object getStackTraceLimit(Scriptable scriptable) {
            return this.stackTraceLimit >= 0 ? Integer.valueOf(this.stackTraceLimit) : Double.valueOf(Double.POSITIVE_INFINITY);
        }

        public void setPrepareStackTrace(Scriptable scriptable, Object obj) {
            if (obj == null || Undefined.instance.equals(obj)) {
                this.prepareStackTrace = null;
            } else if (obj instanceof Function) {
                this.prepareStackTrace = (Function) obj;
            }
        }

        public void setStackTraceLimit(Scriptable scriptable, Object obj) {
            double toNumber = Context.toNumber(obj);
            if (Double.isNaN(toNumber) || Double.isInfinite(toNumber)) {
                this.stackTraceLimit = NativeError.DEFAULT_STACK_LIMIT;
            } else {
                this.stackTraceLimit = (int) toNumber;
            }
        }
    }

    static {
        try {
            Class[] clsArr = new Class[Id_constructor];
            clsArr[0] = Scriptable.class;
            ERROR_DELEGATE_GET_STACK = NativeError.class.getMethod("getStackDelegated", clsArr);
            clsArr = new Class[Id_toString];
            clsArr[0] = Scriptable.class;
            clsArr[Id_constructor] = Object.class;
            ERROR_DELEGATE_SET_STACK = NativeError.class.getMethod("setStackDelegated", clsArr);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    NativeError() {
    }

    private Object callPrepareStack(Function function, ScriptStackElement[] scriptStackElementArr) {
        Context currentContext = Context.getCurrentContext();
        Object[] objArr = new Object[scriptStackElementArr.length];
        for (int i = 0; i < scriptStackElementArr.length; i += Id_constructor) {
            NativeCallSite nativeCallSite = (NativeCallSite) currentContext.newObject(this, "CallSite");
            nativeCallSite.setElement(scriptStackElementArr[i]);
            objArr[i] = nativeCallSite;
        }
        Scriptable newArray = currentContext.newArray((Scriptable) this, objArr);
        Object[] objArr2 = new Object[Id_toString];
        objArr2[0] = this;
        objArr2[Id_constructor] = newArray;
        return function.call(currentContext, function, this, objArr2);
    }

    static void init(Scriptable scriptable, boolean z) {
        Scriptable nativeError = new NativeError();
        ScriptableObject.putProperty(nativeError, "name", (Object) "Error");
        ScriptableObject.putProperty(nativeError, "message", BuildConfig.FLAVOR);
        ScriptableObject.putProperty(nativeError, "fileName", BuildConfig.FLAVOR);
        ScriptableObject.putProperty(nativeError, "lineNumber", Integer.valueOf(0));
        nativeError.setAttributes("name", Id_toString);
        nativeError.setAttributes("message", Id_toString);
        nativeError.exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
        NativeCallSite.init(nativeError, z);
    }

    private static void js_captureStackTrace(Context context, Scriptable scriptable, Object[] objArr) {
        ScriptableObject scriptableObject = (ScriptableObject) ScriptRuntime.toObjectOrNull(context, objArr[0], scriptable);
        Scriptable scriptable2 = null;
        if (objArr.length > Id_constructor) {
            scriptable2 = (Function) ScriptRuntime.toObjectOrNull(context, objArr[Id_constructor], scriptable);
        }
        NativeError nativeError = (NativeError) context.newObject(scriptable, "Error");
        nativeError.setStackProvider(new EvaluatorException("[object Object]"));
        if (scriptable2 != null) {
            Object obj = scriptable2.get("name", scriptable2);
            if (!(obj == null || Undefined.instance.equals(obj))) {
                nativeError.associateValue(STACK_HIDE_KEY, Context.toString(obj));
            }
        }
        scriptableObject.defineProperty("stack", nativeError, ERROR_DELEGATE_GET_STACK, ERROR_DELEGATE_SET_STACK, 0);
    }

    private static String js_toSource(Context context, Scriptable scriptable, Scriptable scriptable2) {
        Object property = ScriptableObject.getProperty(scriptable2, "name");
        Object property2 = ScriptableObject.getProperty(scriptable2, "message");
        Object property3 = ScriptableObject.getProperty(scriptable2, "fileName");
        Object property4 = ScriptableObject.getProperty(scriptable2, "lineNumber");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(new ");
        if (property == NOT_FOUND) {
            property = Undefined.instance;
        }
        stringBuilder.append(ScriptRuntime.toString(property));
        stringBuilder.append("(");
        if (!(property2 == NOT_FOUND && property3 == NOT_FOUND && property4 == NOT_FOUND)) {
            stringBuilder.append(ScriptRuntime.uneval(context, scriptable, property2 == NOT_FOUND ? BuildConfig.FLAVOR : property2));
            if (!(property3 == NOT_FOUND && property4 == NOT_FOUND)) {
                stringBuilder.append(", ");
                stringBuilder.append(ScriptRuntime.uneval(context, scriptable, property3 == NOT_FOUND ? BuildConfig.FLAVOR : property3));
                if (property4 != NOT_FOUND) {
                    int toInt32 = ScriptRuntime.toInt32(property4);
                    if (toInt32 != 0) {
                        stringBuilder.append(", ");
                        stringBuilder.append(ScriptRuntime.toString((double) toInt32));
                    }
                }
            }
        }
        stringBuilder.append("))");
        return stringBuilder.toString();
    }

    private static Object js_toString(Scriptable scriptable) {
        Object property = ScriptableObject.getProperty(scriptable, "name");
        property = (property == NOT_FOUND || property == Undefined.instance) ? "Error" : ScriptRuntime.toString(property);
        Object property2 = ScriptableObject.getProperty(scriptable, "message");
        property2 = (property2 == NOT_FOUND || property2 == Undefined.instance) ? BuildConfig.FLAVOR : ScriptRuntime.toString(property2);
        return property.toString().length() == 0 ? property2 : property2.toString().length() == 0 ? property : ((String) property) + ": " + ((String) property2);
    }

    static NativeError make(Context context, Scriptable scriptable, IdFunctionObject idFunctionObject, Object[] objArr) {
        Scriptable scriptable2 = (Scriptable) idFunctionObject.get("prototype", idFunctionObject);
        Scriptable nativeError = new NativeError();
        nativeError.setPrototype(scriptable2);
        nativeError.setParentScope(scriptable);
        int length = objArr.length;
        if (length >= Id_constructor) {
            if (objArr[0] != Undefined.instance) {
                ScriptableObject.putProperty(nativeError, "message", ScriptRuntime.toString(objArr[0]));
            }
            if (length >= Id_toString) {
                ScriptableObject.putProperty(nativeError, "fileName", objArr[Id_constructor]);
                if (length >= MAX_PROTOTYPE_ID) {
                    ScriptableObject.putProperty(nativeError, "lineNumber", Integer.valueOf(ScriptRuntime.toInt32(objArr[Id_toString])));
                }
            }
        }
        return nativeError;
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(ERROR_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        switch (methodId) {
            case DEFAULT_STACK_LIMIT /*-1*/:
                js_captureStackTrace(context, scriptable2, objArr);
                return Undefined.instance;
            case Id_constructor /*1*/:
                return make(context, scriptable, idFunctionObject, objArr);
            case Id_toString /*2*/:
                return js_toString(scriptable2);
            case MAX_PROTOTYPE_ID /*3*/:
                return js_toSource(context, scriptable, scriptable2);
            default:
                throw new IllegalArgumentException(String.valueOf(methodId));
        }
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
        addIdFunctionProperty(idFunctionObject, ERROR_TAG, DEFAULT_STACK_LIMIT, "captureStackTrace", Id_toString);
        ProtoProps protoProps = new ProtoProps();
        associateValue("_ErrorPrototypeProps", protoProps);
        idFunctionObject.defineProperty("stackTraceLimit", protoProps, ProtoProps.GET_STACK_LIMIT, ProtoProps.SET_STACK_LIMIT, 0);
        idFunctionObject.defineProperty("prepareStackTrace", protoProps, ProtoProps.GET_PREPARE_STACK, ProtoProps.SET_PREPARE_STACK, 0);
        super.fillConstructorProperties(idFunctionObject);
    }

    protected int findPrototypeId(String str) {
        int i;
        String str2;
        int length = str.length();
        if (length == 8) {
            char charAt = str.charAt(MAX_PROTOTYPE_ID);
            if (charAt == 'o') {
                i = MAX_PROTOTYPE_ID;
                str2 = "toSource";
            } else {
                if (charAt == 't') {
                    i = Id_toString;
                    str2 = "toString";
                }
                str2 = null;
                i = 0;
            }
        } else {
            if (length == 11) {
                i = Id_constructor;
                str2 = "constructor";
            }
            str2 = null;
            i = 0;
        }
        return (str2 == null || str2 == str || str2.equals(str)) ? i : 0;
    }

    public String getClassName() {
        return "Error";
    }

    public Object getStackDelegated(Scriptable scriptable) {
        if (this.stackProvider == null) {
            return NOT_FOUND;
        }
        int i = DEFAULT_STACK_LIMIT;
        Function function = null;
        ProtoProps protoProps = (ProtoProps) ((NativeError) getPrototype()).getAssociatedValue("_ErrorPrototypeProps");
        if (protoProps != null) {
            i = protoProps.getStackTraceLimit();
            function = protoProps.getPrepareStackTrace();
        }
        ScriptStackElement[] scriptStack = this.stackProvider.getScriptStack(i, (String) getAssociatedValue(STACK_HIDE_KEY));
        Object formatStackTrace = function == null ? RhinoException.formatStackTrace(scriptStack, this.stackProvider.details()) : callPrepareStack(function, scriptStack);
        setStackDelegated(scriptable, formatStackTrace);
        return formatStackTrace;
    }

    protected void initPrototypeId(int i) {
        String str;
        int i2 = 0;
        switch (i) {
            case Id_constructor /*1*/:
                i2 = Id_constructor;
                str = "constructor";
                break;
            case Id_toString /*2*/:
                str = "toString";
                break;
            case MAX_PROTOTYPE_ID /*3*/:
                str = "toSource";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(ERROR_TAG, i, str, i2);
    }

    public void setStackDelegated(Scriptable scriptable, Object obj) {
        scriptable.delete("stack");
        this.stackProvider = null;
        scriptable.put("stack", scriptable, obj);
    }

    public void setStackProvider(RhinoException rhinoException) {
        if (this.stackProvider == null) {
            this.stackProvider = rhinoException;
            defineProperty("stack", this, ERROR_DELEGATE_GET_STACK, ERROR_DELEGATE_SET_STACK, Id_toString);
        }
    }

    public String toString() {
        Object js_toString = js_toString(this);
        return js_toString instanceof String ? (String) js_toString : super.toString();
    }
}
