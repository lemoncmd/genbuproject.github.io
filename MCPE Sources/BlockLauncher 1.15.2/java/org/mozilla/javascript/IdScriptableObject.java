package org.mozilla.javascript;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class IdScriptableObject extends ScriptableObject implements IdFunctionCall {
    private transient PrototypeValues prototypeValues;

    private static final class PrototypeValues implements Serializable {
        private static final int NAME_SLOT = 1;
        private static final int SLOT_SPAN = 2;
        static final long serialVersionUID = 3038645279153854371L;
        private short[] attributeArray;
        private IdFunctionObject constructor;
        private short constructorAttrs;
        int constructorId;
        private int maxId;
        private IdScriptableObject obj;
        private Object[] valueArray;

        PrototypeValues(IdScriptableObject idScriptableObject, int i) {
            if (idScriptableObject == null) {
                throw new IllegalArgumentException();
            } else if (i < NAME_SLOT) {
                throw new IllegalArgumentException();
            } else {
                this.obj = idScriptableObject;
                this.maxId = i;
            }
        }

        private Object ensureId(int i) {
            Object[] objArr = this.valueArray;
            if (objArr == null) {
                synchronized (this) {
                    objArr = this.valueArray;
                    if (objArr == null) {
                        objArr = new Object[(this.maxId * SLOT_SPAN)];
                        this.valueArray = objArr;
                        this.attributeArray = new short[this.maxId];
                    }
                }
            }
            int i2 = (i - 1) * SLOT_SPAN;
            Object obj = objArr[i2];
            if (obj != null) {
                return obj;
            }
            if (i == this.constructorId) {
                initSlot(this.constructorId, "constructor", this.constructor, this.constructorAttrs);
                this.constructor = null;
            } else {
                this.obj.initPrototypeId(i);
            }
            Object obj2 = objArr[i2];
            if (obj2 != null) {
                return obj2;
            }
            throw new IllegalStateException(this.obj.getClass().getName() + ".initPrototypeId(int id) " + "did not initialize id=" + i);
        }

        private void initSlot(int i, String str, Object obj, int i2) {
            Object[] objArr = this.valueArray;
            if (objArr == null) {
                throw new IllegalStateException();
            }
            if (obj == null) {
                obj = UniqueTag.NULL_VALUE;
            }
            int i3 = (i - 1) * SLOT_SPAN;
            synchronized (this) {
                if (objArr[i3] == null) {
                    objArr[i3] = obj;
                    objArr[i3 + NAME_SLOT] = str;
                    this.attributeArray[i - 1] = (short) i2;
                } else if (!str.equals(objArr[i3 + NAME_SLOT])) {
                    throw new IllegalStateException();
                }
            }
        }

        final IdFunctionObject createPrecachedConstructor() {
            if (this.constructorId != 0) {
                throw new IllegalStateException();
            }
            this.constructorId = this.obj.findPrototypeId("constructor");
            if (this.constructorId == 0) {
                throw new IllegalStateException("No id for constructor property");
            }
            this.obj.initPrototypeId(this.constructorId);
            if (this.constructor == null) {
                throw new IllegalStateException(this.obj.getClass().getName() + ".initPrototypeId() did not " + "initialize id=" + this.constructorId);
            }
            this.constructor.initFunction(this.obj.getClassName(), ScriptableObject.getTopLevelScope(this.obj));
            this.constructor.markAsConstructor(this.obj);
            return this.constructor;
        }

        final void delete(int i) {
            ensureId(i);
            if ((this.attributeArray[i - 1] & 4) == 0) {
                int i2 = (i - 1) * SLOT_SPAN;
                synchronized (this) {
                    this.valueArray[i2] = Scriptable.NOT_FOUND;
                    this.attributeArray[i - 1] = (short) 0;
                }
            } else if (Context.getContext().isStrictMode()) {
                throw ScriptRuntime.typeError1("msg.delete.property.with.configurable.false", (String) this.valueArray[((i - 1) * SLOT_SPAN) + NAME_SLOT]);
            }
        }

        final int findId(String str) {
            return this.obj.findPrototypeId(str);
        }

        final Object get(int i) {
            UniqueTag ensureId = ensureId(i);
            return ensureId == UniqueTag.NULL_VALUE ? null : ensureId;
        }

        final int getAttributes(int i) {
            ensureId(i);
            return this.attributeArray[i - 1];
        }

        final int getMaxId() {
            return this.maxId;
        }

        final Object[] getNames(boolean z, Object[] objArr) {
            Object obj = null;
            int i = NAME_SLOT;
            int i2 = 0;
            while (i <= this.maxId) {
                int i3;
                Object ensureId = ensureId(i);
                if ((z || (this.attributeArray[i - 1] & SLOT_SPAN) == 0) && ensureId != Scriptable.NOT_FOUND) {
                    String str = (String) this.valueArray[((i - 1) * SLOT_SPAN) + NAME_SLOT];
                    if (obj == null) {
                        obj = new Object[this.maxId];
                    }
                    int i4 = i2 + NAME_SLOT;
                    obj[i2] = str;
                    i3 = i4;
                } else {
                    i3 = i2;
                }
                i += NAME_SLOT;
                i2 = i3;
            }
            if (i2 == 0) {
                return objArr;
            }
            if (objArr == null || objArr.length == 0) {
                if (i2 != obj.length) {
                    ensureId = new Object[i2];
                    System.arraycopy(obj, 0, ensureId, 0, i2);
                    obj = ensureId;
                }
                return obj;
            }
            i4 = objArr.length;
            ensureId = new Object[(i4 + i2)];
            System.arraycopy(objArr, 0, ensureId, 0, i4);
            System.arraycopy(obj, 0, ensureId, i4, i2);
            return ensureId;
        }

        final boolean has(int i) {
            Object[] objArr = this.valueArray;
            if (objArr == null) {
                return true;
            }
            Object obj = objArr[(i - 1) * SLOT_SPAN];
            return obj == null || obj != Scriptable.NOT_FOUND;
        }

        final void initValue(int i, String str, Object obj, int i2) {
            if (NAME_SLOT > i || i > this.maxId) {
                throw new IllegalArgumentException();
            } else if (str == null) {
                throw new IllegalArgumentException();
            } else if (obj == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException();
            } else {
                ScriptableObject.checkValidAttributes(i2);
                if (this.obj.findPrototypeId(str) != i) {
                    throw new IllegalArgumentException(str);
                } else if (i != this.constructorId) {
                    initSlot(i, str, obj, i2);
                } else if (obj instanceof IdFunctionObject) {
                    this.constructor = (IdFunctionObject) obj;
                    this.constructorAttrs = (short) i2;
                } else {
                    throw new IllegalArgumentException("consructor should be initialized with IdFunctionObject");
                }
            }
        }

        final void set(int i, Scriptable scriptable, Object obj) {
            if (obj == Scriptable.NOT_FOUND) {
                throw new IllegalArgumentException();
            }
            ensureId(i);
            if ((this.attributeArray[i - 1] & NAME_SLOT) != 0) {
                return;
            }
            if (scriptable == this.obj) {
                if (obj == null) {
                    obj = UniqueTag.NULL_VALUE;
                }
                int i2 = (i - 1) * SLOT_SPAN;
                synchronized (this) {
                    this.valueArray[i2] = obj;
                }
                return;
            }
            scriptable.put((String) this.valueArray[((i - 1) * SLOT_SPAN) + NAME_SLOT], scriptable, obj);
        }

        final void setAttributes(int i, int i2) {
            ScriptableObject.checkValidAttributes(i2);
            ensureId(i);
            synchronized (this) {
                this.attributeArray[i - 1] = (short) i2;
            }
        }
    }

    public IdScriptableObject(Scriptable scriptable, Scriptable scriptable2) {
        super(scriptable, scriptable2);
    }

    private ScriptableObject getBuiltInDescriptor(String str) {
        Scriptable parentScope = getParentScope();
        if (parentScope == null) {
            parentScope = this;
        }
        int findInstanceIdInfo = findInstanceIdInfo(str);
        if (findInstanceIdInfo != 0) {
            return ScriptableObject.buildDataDescriptor(parentScope, getInstanceIdValue(65535 & findInstanceIdInfo), findInstanceIdInfo >>> 16);
        }
        if (this.prototypeValues != null) {
            findInstanceIdInfo = this.prototypeValues.findId(str);
            if (findInstanceIdInfo != 0) {
                return ScriptableObject.buildDataDescriptor(parentScope, this.prototypeValues.get(findInstanceIdInfo), this.prototypeValues.getAttributes(findInstanceIdInfo));
            }
        }
        return null;
    }

    protected static EcmaError incompatibleCallError(IdFunctionObject idFunctionObject) {
        throw ScriptRuntime.typeError1("msg.incompat.call", idFunctionObject.getFunctionName());
    }

    protected static int instanceIdInfo(int i, int i2) {
        return (i << 16) | i2;
    }

    private IdFunctionObject newIdFunction(Object obj, int i, String str, int i2, Scriptable scriptable) {
        IdFunctionObject idFunctionObject = Context.getContext().getLanguageVersion() < Context.VERSION_ES6 ? new IdFunctionObject(this, obj, i, str, i2, scriptable) : new IdFunctionObjectES6(this, obj, i, str, i2, scriptable);
        if (isSealed()) {
            idFunctionObject.sealObject();
        }
        return idFunctionObject;
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        int readInt = objectInputStream.readInt();
        if (readInt != 0) {
            activatePrototypeMap(readInt);
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        int i = 0;
        if (this.prototypeValues != null) {
            i = this.prototypeValues.getMaxId();
        }
        objectOutputStream.writeInt(i);
    }

    public final void activatePrototypeMap(int i) {
        PrototypeValues prototypeValues = new PrototypeValues(this, i);
        synchronized (this) {
            if (this.prototypeValues != null) {
                throw new IllegalStateException();
            }
            this.prototypeValues = prototypeValues;
        }
    }

    protected void addIdFunctionProperty(Scriptable scriptable, Object obj, int i, String str, int i2) {
        newIdFunction(obj, i, str, i2, ScriptableObject.getTopLevelScope(scriptable)).addAsProperty(scriptable);
    }

    protected final Object defaultGet(String str) {
        return super.get(str, (Scriptable) this);
    }

    protected final boolean defaultHas(String str) {
        return super.has(str, (Scriptable) this);
    }

    protected final void defaultPut(String str, Object obj) {
        super.put(str, (Scriptable) this, obj);
    }

    public void defineOwnProperty(Context context, Object obj, ScriptableObject scriptableObject) {
        if (obj instanceof String) {
            String str = (String) obj;
            int findInstanceIdInfo = findInstanceIdInfo(str);
            if (findInstanceIdInfo != 0) {
                int i = 65535 & findInstanceIdInfo;
                if (isAccessorDescriptor(scriptableObject)) {
                    delete(i);
                } else {
                    checkPropertyDefinition(scriptableObject);
                    checkPropertyChange(str, getOwnPropertyDescriptor(context, obj), scriptableObject);
                    findInstanceIdInfo >>>= 16;
                    Object property = ScriptableObject.getProperty((Scriptable) scriptableObject, ES6Iterator.VALUE_PROPERTY);
                    if (!(property == NOT_FOUND || (findInstanceIdInfo & 1) != 0 || sameValue(property, getInstanceIdValue(i)))) {
                        setInstanceIdValue(i, property);
                    }
                    setAttributes(str, applyDescriptorToAttributeBitset(findInstanceIdInfo, scriptableObject));
                    return;
                }
            }
            if (this.prototypeValues != null) {
                findInstanceIdInfo = this.prototypeValues.findId(str);
                if (findInstanceIdInfo != 0) {
                    if (isAccessorDescriptor(scriptableObject)) {
                        this.prototypeValues.delete(findInstanceIdInfo);
                    } else {
                        checkPropertyDefinition(scriptableObject);
                        checkPropertyChange(str, getOwnPropertyDescriptor(context, obj), scriptableObject);
                        int attributes = this.prototypeValues.getAttributes(findInstanceIdInfo);
                        Object property2 = ScriptableObject.getProperty((Scriptable) scriptableObject, ES6Iterator.VALUE_PROPERTY);
                        if (!(property2 == NOT_FOUND || (attributes & 1) != 0 || sameValue(property2, this.prototypeValues.get(findInstanceIdInfo)))) {
                            this.prototypeValues.set(findInstanceIdInfo, this, property2);
                        }
                        this.prototypeValues.setAttributes(findInstanceIdInfo, applyDescriptorToAttributeBitset(attributes, scriptableObject));
                        return;
                    }
                }
            }
        }
        super.defineOwnProperty(context, obj, scriptableObject);
    }

    public void delete(String str) {
        int findInstanceIdInfo = findInstanceIdInfo(str);
        if (findInstanceIdInfo == 0 || isSealed()) {
            if (this.prototypeValues != null) {
                findInstanceIdInfo = this.prototypeValues.findId(str);
                if (findInstanceIdInfo != 0) {
                    if (!isSealed()) {
                        this.prototypeValues.delete(findInstanceIdInfo);
                        return;
                    }
                    return;
                }
            }
            super.delete(str);
        } else if (((findInstanceIdInfo >>> 16) & 4) == 0) {
            setInstanceIdValue(findInstanceIdInfo & 65535, NOT_FOUND);
        } else if (Context.getContext().isStrictMode()) {
            throw ScriptRuntime.typeError1("msg.delete.property.with.configurable.false", str);
        }
    }

    public Object execIdCall(IdFunctionObject idFunctionObject, Context context, Scriptable scriptable, Scriptable scriptable2, Object[] objArr) {
        throw idFunctionObject.unknown();
    }

    public final IdFunctionObject exportAsJSClass(int i, Scriptable scriptable, boolean z) {
        if (!(scriptable == this || scriptable == null)) {
            setParentScope(scriptable);
            setPrototype(ScriptableObject.getObjectPrototype(scriptable));
        }
        activatePrototypeMap(i);
        IdFunctionObject createPrecachedConstructor = this.prototypeValues.createPrecachedConstructor();
        if (z) {
            sealObject();
        }
        fillConstructorProperties(createPrecachedConstructor);
        if (z) {
            createPrecachedConstructor.sealObject();
        }
        createPrecachedConstructor.exportAsScopeProperty();
        return createPrecachedConstructor;
    }

    protected void fillConstructorProperties(IdFunctionObject idFunctionObject) {
    }

    protected int findInstanceIdInfo(String str) {
        return 0;
    }

    protected int findPrototypeId(String str) {
        throw new IllegalStateException(str);
    }

    public Object get(String str, Scriptable scriptable) {
        Object obj = super.get(str, scriptable);
        if (obj != NOT_FOUND) {
            return obj;
        }
        int findInstanceIdInfo = findInstanceIdInfo(str);
        if (findInstanceIdInfo != 0) {
            obj = getInstanceIdValue(findInstanceIdInfo & 65535);
            if (obj != NOT_FOUND) {
                return obj;
            }
        }
        if (this.prototypeValues != null) {
            findInstanceIdInfo = this.prototypeValues.findId(str);
            if (findInstanceIdInfo != 0) {
                obj = this.prototypeValues.get(findInstanceIdInfo);
                if (obj != NOT_FOUND) {
                    return obj;
                }
            }
        }
        return NOT_FOUND;
    }

    public int getAttributes(String str) {
        int findInstanceIdInfo = findInstanceIdInfo(str);
        if (findInstanceIdInfo != 0) {
            return findInstanceIdInfo >>> 16;
        }
        if (this.prototypeValues != null) {
            findInstanceIdInfo = this.prototypeValues.findId(str);
            if (findInstanceIdInfo != 0) {
                return this.prototypeValues.getAttributes(findInstanceIdInfo);
            }
        }
        return super.getAttributes(str);
    }

    Object[] getIds(boolean z) {
        Object ids = super.getIds(z);
        if (this.prototypeValues != null) {
            ids = this.prototypeValues.getNames(z, ids);
        }
        int maxInstanceId = getMaxInstanceId();
        if (maxInstanceId == 0) {
            return ids;
        }
        int i = maxInstanceId;
        Object obj = null;
        int i2 = 0;
        while (i != 0) {
            int i3;
            Object obj2;
            String instanceIdName = getInstanceIdName(i);
            int findInstanceIdInfo = findInstanceIdInfo(instanceIdName);
            if (findInstanceIdInfo != 0) {
                findInstanceIdInfo >>>= 16;
                if ((findInstanceIdInfo & 4) == 0 && NOT_FOUND == getInstanceIdValue(i)) {
                    i3 = i2;
                    obj2 = obj;
                    maxInstanceId = i3;
                    i--;
                    i3 = maxInstanceId;
                    obj = obj2;
                    i2 = i3;
                } else if (z || (findInstanceIdInfo & 2) == 0) {
                    if (i2 == 0) {
                        obj = new Object[i];
                    }
                    findInstanceIdInfo = i2 + 1;
                    obj[i2] = instanceIdName;
                    obj2 = obj;
                    maxInstanceId = findInstanceIdInfo;
                    i--;
                    i3 = maxInstanceId;
                    obj = obj2;
                    i2 = i3;
                }
            }
            i3 = i2;
            obj2 = obj;
            maxInstanceId = i3;
            i--;
            i3 = maxInstanceId;
            obj = obj2;
            i2 = i3;
        }
        if (i2 == 0) {
            return ids;
        }
        if (ids.length == 0 && obj.length == i2) {
            return obj;
        }
        Object obj3 = new Object[(ids.length + i2)];
        System.arraycopy(ids, 0, obj3, 0, ids.length);
        System.arraycopy(obj, 0, obj3, ids.length, i2);
        return obj3;
    }

    protected String getInstanceIdName(int i) {
        throw new IllegalArgumentException(String.valueOf(i));
    }

    protected Object getInstanceIdValue(int i) {
        throw new IllegalStateException(String.valueOf(i));
    }

    protected int getMaxInstanceId() {
        return 0;
    }

    protected ScriptableObject getOwnPropertyDescriptor(Context context, Object obj) {
        ScriptableObject ownPropertyDescriptor = super.getOwnPropertyDescriptor(context, obj);
        return (ownPropertyDescriptor == null && (obj instanceof String)) ? getBuiltInDescriptor((String) obj) : ownPropertyDescriptor;
    }

    public boolean has(String str, Scriptable scriptable) {
        int findInstanceIdInfo = findInstanceIdInfo(str);
        if (findInstanceIdInfo == 0) {
            if (this.prototypeValues != null) {
                int findId = this.prototypeValues.findId(str);
                if (findId != 0) {
                    return this.prototypeValues.has(findId);
                }
            }
            return super.has(str, scriptable);
        } else if (((findInstanceIdInfo >>> 16) & 4) != 0) {
            return true;
        } else {
            return NOT_FOUND != getInstanceIdValue(findInstanceIdInfo & 65535);
        }
    }

    public final boolean hasPrototypeMap() {
        return this.prototypeValues != null;
    }

    public final void initPrototypeConstructor(IdFunctionObject idFunctionObject) {
        int i = this.prototypeValues.constructorId;
        if (i == 0) {
            throw new IllegalStateException();
        } else if (idFunctionObject.methodId() != i) {
            throw new IllegalArgumentException();
        } else {
            if (isSealed()) {
                idFunctionObject.sealObject();
            }
            this.prototypeValues.initValue(i, "constructor", idFunctionObject, 2);
        }
    }

    protected void initPrototypeId(int i) {
        throw new IllegalStateException(String.valueOf(i));
    }

    public final IdFunctionObject initPrototypeMethod(Object obj, int i, String str, int i2) {
        return initPrototypeMethod(obj, i, str, str, i2);
    }

    public final IdFunctionObject initPrototypeMethod(Object obj, int i, String str, String str2, int i2) {
        IdFunctionObject newIdFunction = newIdFunction(obj, i, str2 != null ? str2 : str, i2, ScriptableObject.getTopLevelScope(this));
        this.prototypeValues.initValue(i, str, newIdFunction, 2);
        return newIdFunction;
    }

    public final void initPrototypeValue(int i, String str, Object obj, int i2) {
        this.prototypeValues.initValue(i, str, obj, i2);
    }

    public void put(String str, Scriptable scriptable, Object obj) {
        int findInstanceIdInfo = findInstanceIdInfo(str);
        if (findInstanceIdInfo == 0) {
            if (this.prototypeValues != null) {
                findInstanceIdInfo = this.prototypeValues.findId(str);
                if (findInstanceIdInfo != 0) {
                    if (scriptable == this && isSealed()) {
                        throw Context.reportRuntimeError1("msg.modify.sealed", str);
                    }
                    this.prototypeValues.set(findInstanceIdInfo, scriptable, obj);
                    return;
                }
            }
            super.put(str, scriptable, obj);
        } else if (scriptable == this && isSealed()) {
            throw Context.reportRuntimeError1("msg.modify.sealed", str);
        } else if (((findInstanceIdInfo >>> 16) & 1) != 0) {
        } else {
            if (scriptable == this) {
                setInstanceIdValue(findInstanceIdInfo & 65535, obj);
            } else {
                scriptable.put(str, scriptable, obj);
            }
        }
    }

    public void setAttributes(String str, int i) {
        ScriptableObject.checkValidAttributes(i);
        int findInstanceIdInfo = findInstanceIdInfo(str);
        if (findInstanceIdInfo != 0) {
            int i2 = 65535 & findInstanceIdInfo;
            if (i != (findInstanceIdInfo >>> 16)) {
                setInstanceIdAttributes(i2, i);
                return;
            }
            return;
        }
        if (this.prototypeValues != null) {
            findInstanceIdInfo = this.prototypeValues.findId(str);
            if (findInstanceIdInfo != 0) {
                this.prototypeValues.setAttributes(findInstanceIdInfo, i);
                return;
            }
        }
        super.setAttributes(str, i);
    }

    protected void setInstanceIdAttributes(int i, int i2) {
        throw ScriptRuntime.constructError("InternalError", "Changing attributes not supported for " + getClassName() + " " + getInstanceIdName(i) + " property");
    }

    protected void setInstanceIdValue(int i, Object obj) {
        throw new IllegalStateException(String.valueOf(i));
    }
}
