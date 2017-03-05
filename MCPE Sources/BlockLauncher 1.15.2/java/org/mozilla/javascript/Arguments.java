package org.mozilla.javascript;

final class Arguments extends IdScriptableObject {
    private static final String FTAG = "Arguments";
    private static final int Id_callee = 1;
    private static final int Id_caller = 3;
    private static final int Id_length = 2;
    private static final int MAX_INSTANCE_ID = 3;
    private static BaseFunction iteratorMethod = new BaseFunction() {
        public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
            return new NativeArrayIterator(scriptable, scriptable2);
        }
    };
    static final long serialVersionUID = 4275508002492040609L;
    private NativeCall activation;
    private Object[] args;
    private int calleeAttr = Id_length;
    private Object calleeObj;
    private int callerAttr = Id_length;
    private Object callerObj;
    private int lengthAttr = Id_length;
    private Object lengthObj;

    private static class ThrowTypeError extends BaseFunction {
        private String propertyName;

        ThrowTypeError(String str) {
            this.propertyName = str;
        }

        public Object call(Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
            throw ScriptRuntime.typeError1("msg.arguments.not.access.strict", this.propertyName);
        }
    }

    public Arguments(NativeCall nativeCall) {
        this.activation = nativeCall;
        Scriptable parentScope = nativeCall.getParentScope();
        setParentScope(parentScope);
        setPrototype(ScriptableObject.getObjectPrototype(parentScope));
        this.args = nativeCall.originalArgs;
        this.lengthObj = Integer.valueOf(this.args.length);
        NativeFunction nativeFunction = nativeCall.function;
        this.calleeObj = nativeFunction;
        int languageVersion = nativeFunction.getLanguageVersion();
        if (languageVersion > Token.BLOCK || languageVersion == 0) {
            this.callerObj = NOT_FOUND;
        } else {
            this.callerObj = null;
        }
        defineProperty(NativeSymbol.ITERATOR_PROPERTY, (Object) iteratorMethod, (int) Id_length);
    }

    private Object arg(int i) {
        return (i < 0 || this.args.length <= i) ? NOT_FOUND : this.args[i];
    }

    private Object getFromActivation(int i) {
        return this.activation.get(this.activation.function.getParamOrVarName(i), this.activation);
    }

    private void putIntoActivation(int i, Object obj) {
        this.activation.put(this.activation.function.getParamOrVarName(i), this.activation, obj);
    }

    private void removeArg(int i) {
        synchronized (this) {
            if (this.args[i] != NOT_FOUND) {
                if (this.args == this.activation.originalArgs) {
                    this.args = (Object[]) this.args.clone();
                }
                this.args[i] = NOT_FOUND;
            }
        }
    }

    private void replaceArg(int i, Object obj) {
        if (sharedWithActivation(i)) {
            putIntoActivation(i, obj);
        }
        synchronized (this) {
            if (this.args == this.activation.originalArgs) {
                this.args = (Object[]) this.args.clone();
            }
            this.args[i] = obj;
        }
    }

    private boolean sharedWithActivation(int i) {
        if (Context.getContext().isStrictMode()) {
            return false;
        }
        NativeFunction nativeFunction = this.activation.function;
        int paramCount = nativeFunction.getParamCount();
        if (i >= paramCount) {
            return false;
        }
        if (i < paramCount - 1) {
            String paramOrVarName = nativeFunction.getParamOrVarName(i);
            for (int i2 = i + Id_callee; i2 < paramCount; i2 += Id_callee) {
                if (paramOrVarName.equals(nativeFunction.getParamOrVarName(i2))) {
                    return false;
                }
            }
        }
        return true;
    }

    void defineAttributesForStrictMode() {
        if (Context.getContext().isStrictMode()) {
            setGetterOrSetter("caller", 0, new ThrowTypeError("caller"), true);
            setGetterOrSetter("caller", 0, new ThrowTypeError("caller"), false);
            setGetterOrSetter("callee", 0, new ThrowTypeError("callee"), true);
            setGetterOrSetter("callee", 0, new ThrowTypeError("callee"), false);
            setAttributes("caller", 6);
            setAttributes("callee", 6);
            this.callerObj = null;
            this.calleeObj = null;
        }
    }

    protected void defineOwnProperty(Context context, Object obj, ScriptableObject scriptableObject, boolean z) {
        super.defineOwnProperty(context, obj, scriptableObject, z);
        double toNumber = ScriptRuntime.toNumber(obj);
        int i = (int) toNumber;
        if (toNumber != ((double) i) || arg(i) == NOT_FOUND) {
            return;
        }
        if (isAccessorDescriptor(scriptableObject)) {
            removeArg(i);
            return;
        }
        Object property = ScriptableObject.getProperty((Scriptable) scriptableObject, ES6Iterator.VALUE_PROPERTY);
        if (property != NOT_FOUND) {
            replaceArg(i, property);
            if (ScriptableObject.isFalse(ScriptableObject.getProperty((Scriptable) scriptableObject, "writable"))) {
                removeArg(i);
            }
        }
    }

    public void delete(int i) {
        if (i >= 0 && i < this.args.length) {
            removeArg(i);
        }
        super.delete(i);
    }

    protected int findInstanceIdInfo(String str) {
        String str2;
        int i;
        int i2 = 0;
        if (str.length() == 6) {
            char charAt = str.charAt(5);
            if (charAt == 'e') {
                str2 = "callee";
                i = Id_callee;
            } else if (charAt == 'h') {
                String str3 = Name.LENGTH;
                i = Id_length;
                str2 = str3;
            } else if (charAt == 'r') {
                str2 = "caller";
                i = MAX_INSTANCE_ID;
            }
            if (str2 == null || str2 == str || str2.equals(str)) {
                i2 = i;
            }
            if (!Context.getContext().isStrictMode() && (i2 == Id_callee || i2 == MAX_INSTANCE_ID)) {
                return super.findInstanceIdInfo(str);
            }
            if (i2 == 0) {
                return super.findInstanceIdInfo(str);
            }
            switch (i2) {
                case Id_callee /*1*/:
                    i = this.calleeAttr;
                    break;
                case Id_length /*2*/:
                    i = this.lengthAttr;
                    break;
                case MAX_INSTANCE_ID /*3*/:
                    i = this.callerAttr;
                    break;
                default:
                    throw new IllegalStateException();
            }
            return IdScriptableObject.instanceIdInfo(i, i2);
        }
        str2 = null;
        i = 0;
        i2 = i;
        if (!Context.getContext().isStrictMode()) {
        }
        if (i2 == 0) {
            return super.findInstanceIdInfo(str);
        }
        switch (i2) {
            case Id_callee /*1*/:
                i = this.calleeAttr;
                break;
            case Id_length /*2*/:
                i = this.lengthAttr;
                break;
            case MAX_INSTANCE_ID /*3*/:
                i = this.callerAttr;
                break;
            default:
                throw new IllegalStateException();
        }
        return IdScriptableObject.instanceIdInfo(i, i2);
    }

    public Object get(int i, Scriptable scriptable) {
        Object arg = arg(i);
        return arg == NOT_FOUND ? super.get(i, scriptable) : sharedWithActivation(i) ? getFromActivation(i) : arg;
    }

    public String getClassName() {
        return FTAG;
    }

    Object[] getIds(boolean z) {
        int i = 0;
        Object ids = super.getIds(z);
        if (this.args.length != 0) {
            int intValue;
            boolean[] zArr = new boolean[this.args.length];
            int length = this.args.length;
            for (int i2 = 0; i2 != ids.length; i2 += Id_callee) {
                Object obj = ids[i2];
                if (obj instanceof Integer) {
                    intValue = ((Integer) obj).intValue();
                    if (intValue >= 0 && intValue < this.args.length && !zArr[intValue]) {
                        zArr[intValue] = true;
                        length--;
                    }
                }
            }
            if (!z) {
                intValue = 0;
                while (intValue < zArr.length) {
                    if (!zArr[intValue] && super.has(intValue, (Scriptable) this)) {
                        zArr[intValue] = true;
                        length--;
                    }
                    intValue += Id_callee;
                }
            }
            if (length != 0) {
                Object obj2 = new Object[(ids.length + length)];
                System.arraycopy(ids, 0, obj2, length, ids.length);
                intValue = 0;
                while (i != this.args.length) {
                    if (zArr == null || !zArr[i]) {
                        obj2[intValue] = Integer.valueOf(i);
                        intValue += Id_callee;
                    }
                    i += Id_callee;
                }
                if (intValue != length) {
                    Kit.codeBug();
                }
                return obj2;
            }
        }
        return ids;
    }

    protected String getInstanceIdName(int i) {
        switch (i) {
            case Id_callee /*1*/:
                return "callee";
            case Id_length /*2*/:
                return Name.LENGTH;
            case MAX_INSTANCE_ID /*3*/:
                return "caller";
            default:
                return null;
        }
    }

    protected Object getInstanceIdValue(int i) {
        switch (i) {
            case Id_callee /*1*/:
                return this.calleeObj;
            case Id_length /*2*/:
                return this.lengthObj;
            case MAX_INSTANCE_ID /*3*/:
                UniqueTag uniqueTag = this.callerObj;
                if (uniqueTag == UniqueTag.NULL_VALUE) {
                    return null;
                }
                if (uniqueTag != null) {
                    return uniqueTag;
                }
                Scriptable scriptable = this.activation.parentActivationCall;
                return scriptable != null ? scriptable.get("arguments", scriptable) : uniqueTag;
            default:
                return super.getInstanceIdValue(i);
        }
    }

    protected int getMaxInstanceId() {
        return MAX_INSTANCE_ID;
    }

    protected ScriptableObject getOwnPropertyDescriptor(Context context, Object obj) {
        double toNumber = ScriptRuntime.toNumber(obj);
        int i = (int) toNumber;
        if (toNumber != ((double) i)) {
            return super.getOwnPropertyDescriptor(context, obj);
        }
        Object arg = arg(i);
        if (arg == NOT_FOUND) {
            return super.getOwnPropertyDescriptor(context, obj);
        }
        if (sharedWithActivation(i)) {
            arg = getFromActivation(i);
        }
        if (super.has(i, (Scriptable) this)) {
            Scriptable ownPropertyDescriptor = super.getOwnPropertyDescriptor(context, obj);
            ownPropertyDescriptor.put(ES6Iterator.VALUE_PROPERTY, ownPropertyDescriptor, arg);
            return ownPropertyDescriptor;
        }
        Scriptable scriptable;
        ownPropertyDescriptor = getParentScope();
        if (ownPropertyDescriptor != null) {
            scriptable = ownPropertyDescriptor;
        }
        return ScriptableObject.buildDataDescriptor(scriptable, arg, 0);
    }

    public boolean has(int i, Scriptable scriptable) {
        return arg(i) != NOT_FOUND ? true : super.has(i, scriptable);
    }

    public void put(int i, Scriptable scriptable, Object obj) {
        if (arg(i) == NOT_FOUND) {
            super.put(i, scriptable, obj);
        } else {
            replaceArg(i, obj);
        }
    }

    public void put(String str, Scriptable scriptable, Object obj) {
        super.put(str, scriptable, obj);
    }

    protected void setInstanceIdAttributes(int i, int i2) {
        switch (i) {
            case Id_callee /*1*/:
                this.calleeAttr = i2;
                return;
            case Id_length /*2*/:
                this.lengthAttr = i2;
                return;
            case MAX_INSTANCE_ID /*3*/:
                this.callerAttr = i2;
                return;
            default:
                super.setInstanceIdAttributes(i, i2);
                return;
        }
    }

    protected void setInstanceIdValue(int i, Object obj) {
        switch (i) {
            case Id_callee /*1*/:
                this.calleeObj = obj;
                return;
            case Id_length /*2*/:
                this.lengthObj = obj;
                return;
            case MAX_INSTANCE_ID /*3*/:
                if (obj == null) {
                    obj = UniqueTag.NULL_VALUE;
                }
                this.callerObj = obj;
                return;
            default:
                super.setInstanceIdValue(i, obj);
                return;
        }
    }
}
