package org.simpleframework.xml.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

class LabelMap extends LinkedHashMap<String, Label> implements Iterable<Label> {
    private final Policy policy;

    public LabelMap() {
        this(null);
    }

    public LabelMap(Policy policy) {
        this.policy = policy;
    }

    private String[] getArray(Set<String> set) {
        return (String[]) set.toArray(new String[0]);
    }

    public String[] getKeys() throws Exception {
        Set hashSet = new HashSet();
        Iterator it = iterator();
        while (it.hasNext()) {
            Label label = (Label) it.next();
            if (label != null) {
                String path = label.getPath();
                String name = label.getName();
                hashSet.add(path);
                hashSet.add(name);
            }
        }
        return getArray(hashSet);
    }

    public Label getLabel(String str) {
        return (Label) remove(str);
    }

    public LabelMap getLabels() throws Exception {
        LabelMap labelMap = new LabelMap(this.policy);
        Iterator it = iterator();
        while (it.hasNext()) {
            Label label = (Label) it.next();
            if (label != null) {
                labelMap.put(label.getPath(), label);
            }
        }
        return labelMap;
    }

    public String[] getPaths() throws Exception {
        Set hashSet = new HashSet();
        Iterator it = iterator();
        while (it.hasNext()) {
            Label label = (Label) it.next();
            if (label != null) {
                hashSet.add(label.getPath());
            }
        }
        return getArray(hashSet);
    }

    public boolean isStrict(Context context) {
        return this.policy == null ? context.isStrict() : context.isStrict() && this.policy.isStrict();
    }

    public Iterator<Label> iterator() {
        return values().iterator();
    }
}
