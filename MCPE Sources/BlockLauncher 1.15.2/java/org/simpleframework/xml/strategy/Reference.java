package org.simpleframework.xml.strategy;

class Reference implements Value {
    private Class type;
    private Object value;

    public Reference(Object obj, Class cls) {
        this.value = obj;
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
        return true;
    }

    public void setValue(Object obj) {
        this.value = obj;
    }
}
