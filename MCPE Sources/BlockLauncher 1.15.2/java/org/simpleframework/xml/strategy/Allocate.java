package org.simpleframework.xml.strategy;

import java.util.Map;

class Allocate implements Value {
    private String key;
    private Map map;
    private Value value;

    public Allocate(Value value, Map map, String str) {
        this.value = value;
        this.map = map;
        this.key = str;
    }

    public int getLength() {
        return this.value.getLength();
    }

    public Class getType() {
        return this.value.getType();
    }

    public Object getValue() {
        return this.map.get(this.key);
    }

    public boolean isReference() {
        return false;
    }

    public void setValue(Object obj) {
        if (this.key != null) {
            this.map.put(this.key, obj);
        }
        this.value.setValue(obj);
    }
}
