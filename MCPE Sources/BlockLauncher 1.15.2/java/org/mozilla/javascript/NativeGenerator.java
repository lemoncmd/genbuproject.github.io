package org.mozilla.javascript;

public final class NativeGenerator extends IdScriptableObject {
    public static final int GENERATOR_CLOSE = 2;
    public static final int GENERATOR_SEND = 0;
    private static final Object GENERATOR_TAG = "Generator";
    public static final int GENERATOR_THROW = 1;
    private static final int Id___iterator__ = 5;
    private static final int Id_close = 1;
    private static final int Id_next = 2;
    private static final int Id_send = 3;
    private static final int Id_throw = 4;
    private static final int MAX_PROTOTYPE_ID = 5;
    private static final long serialVersionUID = 1645892441041347273L;
    private boolean firstTime = true;
    private NativeFunction function;
    private int lineNumber;
    private String lineSource;
    private boolean locked;
    private Object savedState;

    public static class GeneratorClosedException extends RuntimeException {
        private static final long serialVersionUID = 2561315658662379681L;
    }

    private NativeGenerator() {
    }

    public NativeGenerator(Scriptable scriptable, NativeFunction nativeFunction, Object obj) {
        this.function = nativeFunction;
        this.savedState = obj;
        Scriptable topLevelScope = ScriptableObject.getTopLevelScope(scriptable);
        setParentScope(topLevelScope);
        setPrototype((NativeGenerator) ScriptableObject.getTopScopeValue(topLevelScope, GENERATOR_TAG));
    }

    static NativeGenerator init(ScriptableObject scriptableObject, boolean z) {
        NativeGenerator nativeGenerator = new NativeGenerator();
        if (scriptableObject != null) {
            nativeGenerator.setParentScope(scriptableObject);
            nativeGenerator.setPrototype(ScriptableObject.getObjectPrototype(scriptableObject));
        }
        nativeGenerator.activatePrototypeMap(MAX_PROTOTYPE_ID);
        if (z) {
            nativeGenerator.sealObject();
        }
        if (scriptableObject != null) {
            scriptableObject.associateValue(GENERATOR_TAG, nativeGenerator);
        }
        return nativeGenerator;
    }

    private Object resume(Context context, Scriptable scriptable, int i, Object obj) {
        Object resumeGenerator;
        if (this.savedState != null) {
            try {
                synchronized (this) {
                    if (this.locked) {
                        throw ScriptRuntime.typeError0("msg.already.exec.gen");
                    }
                    this.locked = true;
                }
                resumeGenerator = this.function.resumeGenerator(context, scriptable, i, this.savedState, obj);
                synchronized (this) {
                    this.locked = false;
                }
                if (i != Id_next) {
                    return resumeGenerator;
                }
                this.savedState = null;
                return resumeGenerator;
            } catch (GeneratorClosedException e) {
                try {
                    resumeGenerator = Undefined.instance;
                    synchronized (this) {
                        this.locked = false;
                        if (i != Id_next) {
                            return resumeGenerator;
                        }
                        this.savedState = null;
                        return resumeGenerator;
                    }
                } catch (Throwable th) {
                    synchronized (this) {
                        this.locked = false;
                        if (i == Id_next) {
                            this.savedState = null;
                        }
                    }
                }
            } catch (RhinoException e2) {
                this.lineNumber = e2.lineNumber();
                this.lineSource = e2.lineSource();
                this.savedState = null;
                throw e2;
            }
        } else if (i == Id_next) {
            return Undefined.instance;
        } else {
            if (i != Id_close) {
                obj = NativeIterator.getStopIterationObject(scriptable);
            }
            throw new JavaScriptException(obj, this.lineSource, this.lineNumber);
        }
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(GENERATOR_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        if (scriptable2 instanceof NativeGenerator) {
            NativeGenerator nativeGenerator = (NativeGenerator) scriptable2;
            switch (methodId) {
                case Id_close /*1*/:
                    return nativeGenerator.resume(context, scriptable, Id_next, new GeneratorClosedException());
                case Id_next /*2*/:
                    nativeGenerator.firstTime = false;
                    return nativeGenerator.resume(context, scriptable, GENERATOR_SEND, Undefined.instance);
                case Id_send /*3*/:
                    Object obj = objArr.length > 0 ? objArr[GENERATOR_SEND] : Undefined.instance;
                    if (!nativeGenerator.firstTime || obj.equals(Undefined.instance)) {
                        return nativeGenerator.resume(context, scriptable, GENERATOR_SEND, obj);
                    }
                    throw ScriptRuntime.typeError0("msg.send.newborn");
                case Id_throw /*4*/:
                    return nativeGenerator.resume(context, scriptable, Id_close, objArr.length > 0 ? objArr[GENERATOR_SEND] : Undefined.instance);
                case MAX_PROTOTYPE_ID /*5*/:
                    return scriptable2;
                default:
                    throw new IllegalArgumentException(String.valueOf(methodId));
            }
        }
        throw IdScriptableObject.incompatibleCallError(idFunctionObject);
    }

    protected int findPrototypeId(String str) {
        int i;
        String str2;
        int length = str.length();
        if (length == Id_throw) {
            char charAt = str.charAt(GENERATOR_SEND);
            if (charAt == 'n') {
                String str3 = ES6Iterator.NEXT_METHOD;
                i = Id_next;
                str2 = str3;
            } else {
                if (charAt == 's') {
                    i = Id_send;
                    str2 = "send";
                }
                str2 = null;
                i = GENERATOR_SEND;
            }
        } else if (length == MAX_PROTOTYPE_ID) {
            char charAt2 = str.charAt(GENERATOR_SEND);
            if (charAt2 == 'c') {
                i = Id_close;
                str2 = "close";
            } else {
                if (charAt2 == 't') {
                    i = Id_throw;
                    str2 = "throw";
                }
                str2 = null;
                i = GENERATOR_SEND;
            }
        } else {
            if (length == 12) {
                str2 = NativeIterator.ITERATOR_PROPERTY_NAME;
                i = MAX_PROTOTYPE_ID;
            }
            str2 = null;
            i = GENERATOR_SEND;
        }
        return (str2 == null || str2 == str || str2.equals(str)) ? i : GENERATOR_SEND;
    }

    public String getClassName() {
        return "Generator";
    }

    protected void initPrototypeId(int i) {
        String str;
        int i2 = Id_close;
        switch (i) {
            case Id_close /*1*/:
                str = "close";
                break;
            case Id_next /*2*/:
                str = ES6Iterator.NEXT_METHOD;
                break;
            case Id_send /*3*/:
                str = "send";
                i2 = GENERATOR_SEND;
                break;
            case Id_throw /*4*/:
                str = "throw";
                i2 = GENERATOR_SEND;
                break;
            case MAX_PROTOTYPE_ID /*5*/:
                str = NativeIterator.ITERATOR_PROPERTY_NAME;
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(GENERATOR_TAG, i, str, i2);
    }
}
