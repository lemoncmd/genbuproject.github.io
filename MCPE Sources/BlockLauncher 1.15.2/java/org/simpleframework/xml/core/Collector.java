package org.simpleframework.xml.core;

import java.util.Iterator;
import java.util.LinkedHashMap;

class Collector implements Criteria {
    private final Registry alias = new Registry();
    private final Registry registry = new Registry();

    private static class Registry extends LinkedHashMap<Object, Variable> {
        private Registry() {
        }

        public Iterator<Object> iterator() {
            return keySet().iterator();
        }
    }

    public void commit(Object obj) throws Exception {
        for (Variable variable : this.registry.values()) {
            variable.getContact().set(obj, variable.getValue());
        }
    }

    public Variable get(Object obj) {
        return (Variable) this.registry.get(obj);
    }

    public Variable get(Label label) throws Exception {
        if (label == null) {
            return null;
        }
        return (Variable) this.registry.get(label.getKey());
    }

    public Iterator<Object> iterator() {
        return this.registry.iterator();
    }

    public Variable remove(Object obj) throws Exception {
        return (Variable) this.registry.remove(obj);
    }

    public Variable resolve(String str) {
        return (Variable) this.alias.get(str);
    }

    public void set(Label label, Object obj) throws Exception {
        Variable variable = new Variable(label, obj);
        if (label != null) {
            String[] paths = label.getPaths();
            Object key = label.getKey();
            for (Object put : paths) {
                this.alias.put(put, variable);
            }
            this.registry.put(key, variable);
        }
    }
}
