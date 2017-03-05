package org.simpleframework.xml.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class Session implements Map {
    private final Map map;
    private final boolean strict;

    public Session() {
        this(true);
    }

    public Session(boolean z) {
        this.map = new HashMap();
        this.strict = z;
    }

    public void clear() {
        this.map.clear();
    }

    public boolean containsKey(Object obj) {
        return this.map.containsKey(obj);
    }

    public boolean containsValue(Object obj) {
        return this.map.containsValue(obj);
    }

    public Set entrySet() {
        return this.map.entrySet();
    }

    public Object get(Object obj) {
        return this.map.get(obj);
    }

    public Map getMap() {
        return this.map;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean isStrict() {
        return this.strict;
    }

    public Set keySet() {
        return this.map.keySet();
    }

    public Object put(Object obj, Object obj2) {
        return this.map.put(obj, obj2);
    }

    public void putAll(Map map) {
        this.map.putAll(map);
    }

    public Object remove(Object obj) {
        return this.map.remove(obj);
    }

    public int size() {
        return this.map.size();
    }

    public Collection values() {
        return this.map.values();
    }
}
