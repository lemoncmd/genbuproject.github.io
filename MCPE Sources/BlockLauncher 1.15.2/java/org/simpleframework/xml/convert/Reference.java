package org.simpleframework.xml.convert;

import org.simpleframework.xml.strategy.Value;

class Reference implements Value {
    private Class actual;
    private Object data;
    private Value value;

    public Reference(Value value, Object obj, Class cls) {
        this.actual = cls;
        this.value = value;
        this.data = obj;
    }

    public int getLength() {
        return 0;
    }

    public Class getType() {
        return this.data != null ? this.data.getClass() : this.actual;
    }

    public Object getValue() {
        return this.data;
    }

    public boolean isReference() {
        return true;
    }

    public void setValue(Object obj) {
        if (this.value != null) {
            this.value.setValue(obj);
        }
        this.data = obj;
    }
}
