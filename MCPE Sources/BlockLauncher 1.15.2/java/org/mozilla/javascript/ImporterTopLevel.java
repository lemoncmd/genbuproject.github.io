package org.mozilla.javascript;

public class ImporterTopLevel extends TopLevel {
    private static final Object IMPORTER_TAG = "Importer";
    private static final int Id_constructor = 1;
    private static final int Id_importClass = 2;
    private static final int Id_importPackage = 3;
    private static final int MAX_PROTOTYPE_ID = 3;
    static final long serialVersionUID = -9095380847465315412L;
    private ObjArray importedPackages;
    private boolean topScopeFlag;

    public ImporterTopLevel() {
        this.importedPackages = new ObjArray();
    }

    public ImporterTopLevel(Context context) {
        this(context, false);
    }

    public ImporterTopLevel(Context context, boolean z) {
        this.importedPackages = new ObjArray();
        initStandardObjects(context, z);
    }

    private Object getPackageProperty(String str, Scriptable scriptable) {
        Object obj = NOT_FOUND;
        synchronized (this.importedPackages) {
            Object[] toArray = this.importedPackages.toArray();
        }
        Object obj2 = obj;
        for (int i = 0; i < toArray.length; i += Id_constructor) {
            obj = ((NativeJavaPackage) toArray[i]).getPkgProperty(str, scriptable, false);
            if (!(obj == null || (obj instanceof NativeJavaPackage))) {
                if (obj2 == NOT_FOUND) {
                    obj2 = obj;
                } else {
                    throw Context.reportRuntimeError2("msg.ambig.import", obj2.toString(), obj.toString());
                }
            }
        }
        return obj2;
    }

    private void importClass(NativeJavaClass nativeJavaClass) {
        String name = nativeJavaClass.getClassObject().getName();
        name = name.substring(name.lastIndexOf(46) + Id_constructor);
        NativeJavaClass nativeJavaClass2 = get(name, this);
        if (nativeJavaClass2 == NOT_FOUND || nativeJavaClass2 == nativeJavaClass) {
            put(name, this, nativeJavaClass);
            return;
        }
        throw Context.reportRuntimeError1("msg.prop.defined", name);
    }

    private void importPackage(NativeJavaPackage nativeJavaPackage) {
        if (nativeJavaPackage != null) {
            synchronized (this.importedPackages) {
                for (int i = 0; i != this.importedPackages.size(); i += Id_constructor) {
                    if (nativeJavaPackage.equals(this.importedPackages.get(i))) {
                        return;
                    }
                }
                this.importedPackages.add(nativeJavaPackage);
            }
        }
    }

    public static void init(Context context, Scriptable scriptable, boolean z) {
        new ImporterTopLevel().exportAsJSClass(MAX_PROTOTYPE_ID, scriptable, z);
    }

    private Object js_construct(Scriptable scriptable, Object[] objArr) {
        ImporterTopLevel importerTopLevel = new ImporterTopLevel();
        for (int i = 0; i != objArr.length; i += Id_constructor) {
            Object obj = objArr[i];
            if (obj instanceof NativeJavaClass) {
                importerTopLevel.importClass((NativeJavaClass) obj);
            } else if (obj instanceof NativeJavaPackage) {
                importerTopLevel.importPackage((NativeJavaPackage) obj);
            } else {
                throw Context.reportRuntimeError1("msg.not.class.not.pkg", Context.toString(obj));
            }
        }
        importerTopLevel.setParentScope(scriptable);
        importerTopLevel.setPrototype(this);
        return importerTopLevel;
    }

    private Object js_importClass(Object[] objArr) {
        int i = 0;
        while (i != objArr.length) {
            Object obj = objArr[i];
            if (obj instanceof NativeJavaClass) {
                importClass((NativeJavaClass) obj);
                i += Id_constructor;
            } else {
                throw Context.reportRuntimeError1("msg.not.class", Context.toString(obj));
            }
        }
        return Undefined.instance;
    }

    private Object js_importPackage(Object[] objArr) {
        int i = 0;
        while (i != objArr.length) {
            Object obj = objArr[i];
            if (obj instanceof NativeJavaPackage) {
                importPackage((NativeJavaPackage) obj);
                i += Id_constructor;
            } else {
                throw Context.reportRuntimeError1("msg.not.pkg", Context.toString(obj));
            }
        }
        return Undefined.instance;
    }

    private ImporterTopLevel realThis(Scriptable scriptable, IdFunctionObject idFunctionObject) {
        if (this.topScopeFlag) {
            return this;
        }
        if (scriptable instanceof ImporterTopLevel) {
            return (ImporterTopLevel) scriptable;
        }
        throw IdScriptableObject.incompatibleCallError(idFunctionObject);
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        if (!idFunctionObject.hasTag(IMPORTER_TAG)) {
            return super.execIdCall(idFunctionObject, context, scriptable, scriptable2, objArr);
        }
        int methodId = idFunctionObject.methodId();
        switch (methodId) {
            case Id_constructor /*1*/:
                return js_construct(scriptable, objArr);
            case Id_importClass /*2*/:
                return realThis(scriptable2, idFunctionObject).js_importClass(objArr);
            case MAX_PROTOTYPE_ID /*3*/:
                return realThis(scriptable2, idFunctionObject).js_importPackage(objArr);
            default:
                throw new IllegalArgumentException(String.valueOf(methodId));
        }
    }

    protected int findPrototypeId(String str) {
        int i;
        String str2;
        int length = str.length();
        if (length == 11) {
            char charAt = str.charAt(0);
            if (charAt == 'c') {
                i = Id_constructor;
                str2 = "constructor";
            } else {
                if (charAt == 'i') {
                    i = Id_importClass;
                    str2 = "importClass";
                }
                str2 = null;
                i = 0;
            }
        } else {
            if (length == 13) {
                i = MAX_PROTOTYPE_ID;
                str2 = "importPackage";
            }
            str2 = null;
            i = 0;
        }
        return (str2 == null || str2 == str || str2.equals(str)) ? i : 0;
    }

    public Object get(String str, Scriptable scriptable) {
        Object obj = super.get(str, scriptable);
        return obj != NOT_FOUND ? obj : getPackageProperty(str, scriptable);
    }

    public String getClassName() {
        return this.topScopeFlag ? "global" : "JavaImporter";
    }

    public boolean has(String str, Scriptable scriptable) {
        return super.has(str, scriptable) || getPackageProperty(str, scriptable) != NOT_FOUND;
    }

    @Deprecated
    public void importPackage(Context context, Scriptable scriptable, Object[] objArr, Function function) {
        js_importPackage(objArr);
    }

    protected void initPrototypeId(int i) {
        String str;
        int i2 = Id_constructor;
        switch (i) {
            case Id_constructor /*1*/:
                i2 = 0;
                str = "constructor";
                break;
            case Id_importClass /*2*/:
                str = "importClass";
                break;
            case MAX_PROTOTYPE_ID /*3*/:
                str = "importPackage";
                break;
            default:
                throw new IllegalArgumentException(String.valueOf(i));
        }
        initPrototypeMethod(IMPORTER_TAG, i, str, i2);
    }

    public void initStandardObjects(Context context, boolean z) {
        context.initStandardObjects(this, z);
        this.topScopeFlag = true;
        IdFunctionObject exportAsJSClass = exportAsJSClass(MAX_PROTOTYPE_ID, this, false);
        if (z) {
            exportAsJSClass.sealObject();
        }
        delete("constructor");
    }
}
