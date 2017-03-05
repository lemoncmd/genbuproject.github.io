package org.simpleframework.xml.strategy;

class ObjectValue implements Value {
    private Class type;
    private Object value;

    public ObjectValue(Class cls) {
        this.type = cls;
    }

    public int getLength() {
        return 0;
    }

    public Class getType() {
        return this.type;
    }

    public Object getValue() {
        return this.value;
    }

    public boolean isReference() {
        return false;
    }

    public void setValue(Object obj) {
        this.value = obj;
    }
}
