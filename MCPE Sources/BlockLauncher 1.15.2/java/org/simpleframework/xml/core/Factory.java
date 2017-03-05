package org.simpleframework.xml.core;

import java.lang.reflect.Modifier;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.strategy.Value;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Position;

abstract class Factory {
    protected Context context;
    protected Class override;
    protected Support support;
    protected Type type;

    protected Factory(Context context, Type type) {
        this(context, type, null);
    }

    protected Factory(Context context, Type type, Class cls) {
        this.support = context.getSupport();
        this.override = cls;
        this.context = context;
        this.type = type;
    }

    private Type getPrimitive(Type type, Class cls) throws Exception {
        Support support = this.support;
        Class primitive = Support.getPrimitive(cls);
        return primitive != cls ? new OverrideType(type, primitive) : type;
    }

    public static boolean isCompatible(Class cls, Class cls2) {
        if (cls.isArray()) {
            cls = cls.getComponentType();
        }
        return cls.isAssignableFrom(cls2);
    }

    public static boolean isInstantiable(Class cls) {
        int modifiers = cls.getModifiers();
        return (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) ? false : true;
    }

    public Value getConversion(InputNode inputNode) throws Exception {
        Value override = this.context.getOverride(this.type, inputNode);
        if (!(override == null || this.override == null)) {
            if (!isCompatible(this.override, override.getType())) {
                return new OverrideValue(override, this.override);
            }
        }
        return override;
    }

    public Object getInstance() throws Exception {
        Class type = getType();
        if (isInstantiable(type)) {
            return type.newInstance();
        }
        throw new InstantiationException("Type %s can not be instantiated", type);
    }

    protected Value getOverride(InputNode inputNode) throws Exception {
        Value conversion = getConversion(inputNode);
        if (conversion != null) {
            Position position = inputNode.getPosition();
            if (!isCompatible(getType(), conversion.getType())) {
                throw new InstantiationException("Incompatible %s for %s at %s", conversion.getType(), this.type, position);
            }
        }
        return conversion;
    }

    public Class getType() {
        return this.override != null ? this.override : this.type.getType();
    }

    public boolean setOverride(Type type, Object obj, OutputNode outputNode) throws Exception {
        Class type2 = type.getType();
        if (type2.isPrimitive()) {
            type = getPrimitive(type, type2);
        }
        return this.context.setOverride(type, obj, outputNode);
    }
}
