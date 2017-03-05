package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class Primitive implements Converter {
    private final Context context;
    private final String empty;
    private final Class expect;
    private final PrimitiveFactory factory;
    private final Type type;

    public Primitive(Context context, Type type) {
        this(context, type, null);
    }

    public Primitive(Context context, Type type, String str) {
        this.factory = new PrimitiveFactory(context, type);
        this.expect = type.getType();
        this.context = context;
        this.empty = str;
        this.type = type;
    }

    private Object readElement(InputNode inputNode) throws Exception {
        Instance instance = this.factory.getInstance(inputNode);
        return !instance.isReference() ? readElement(inputNode, instance) : instance.getInstance();
    }

    private Object readElement(InputNode inputNode, Instance instance) throws Exception {
        Object read = read(inputNode, this.expect);
        if (instance != null) {
            instance.setInstance(read);
        }
        return read;
    }

    private Object readTemplate(String str, Class cls) throws Exception {
        String property = this.context.getProperty(str);
        return property != null ? this.factory.getInstance(property, cls) : null;
    }

    private boolean validateElement(InputNode inputNode) throws Exception {
        Instance instance = this.factory.getInstance(inputNode);
        if (!instance.isReference()) {
            instance.setInstance(null);
        }
        return true;
    }

    public Object read(InputNode inputNode) throws Exception {
        return inputNode.isElement() ? readElement(inputNode) : read(inputNode, this.expect);
    }

    public Object read(InputNode inputNode, Class cls) throws Exception {
        String value = inputNode.getValue();
        return value == null ? null : (this.empty == null || !value.equals(this.empty)) ? readTemplate(value, cls) : this.empty;
    }

    public Object read(InputNode inputNode, Object obj) throws Exception {
        if (obj == null) {
            return read(inputNode);
        }
        throw new PersistenceException("Can not read existing %s for %s", this.expect, this.type);
    }

    public boolean validate(InputNode inputNode) throws Exception {
        if (inputNode.isElement()) {
            validateElement(inputNode);
        } else {
            inputNode.getValue();
        }
        return true;
    }

    public void write(OutputNode outputNode, Object obj) throws Exception {
        String text = this.factory.getText(obj);
        if (text != null) {
            outputNode.setValue(text);
        }
    }
}
