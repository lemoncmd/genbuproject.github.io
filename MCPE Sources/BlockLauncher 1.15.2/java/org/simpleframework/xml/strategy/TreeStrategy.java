package org.simpleframework.xml.strategy;

import java.lang.reflect.Array;
import java.util.Map;
import org.simpleframework.xml.stream.Node;
import org.simpleframework.xml.stream.NodeMap;

public class TreeStrategy implements Strategy {
    private final String label;
    private final String length;
    private final Loader loader;

    public TreeStrategy() {
        this(Name.LABEL, Name.LENGTH);
    }

    public TreeStrategy(String str, String str2) {
        this.loader = new Loader();
        this.length = str2;
        this.label = str;
    }

    private Value readArray(Class cls, NodeMap nodeMap) throws Exception {
        Node remove = nodeMap.remove(this.length);
        int i = 0;
        if (remove != null) {
            i = Integer.parseInt(remove.getValue());
        }
        return new ArrayValue(cls, i);
    }

    private Class readValue(Type type, NodeMap nodeMap) throws Exception {
        Node remove = nodeMap.remove(this.label);
        Class type2 = type.getType();
        if (type2.isArray()) {
            type2 = type2.getComponentType();
        }
        if (remove == null) {
            return type2;
        }
        return this.loader.load(remove.getValue());
    }

    private Class writeArray(Class cls, Object obj, NodeMap nodeMap) {
        int length = Array.getLength(obj);
        if (this.length != null) {
            nodeMap.put(this.length, String.valueOf(length));
        }
        return cls.getComponentType();
    }

    public Value read(Type type, NodeMap nodeMap, Map map) throws Exception {
        Class readValue = readValue(type, nodeMap);
        Class type2 = type.getType();
        return type2.isArray() ? readArray(readValue, nodeMap) : type2 != readValue ? new ObjectValue(readValue) : null;
    }

    public boolean write(Type type, Object obj, NodeMap nodeMap, Map map) {
        Class cls = obj.getClass();
        Class type2 = type.getType();
        Class writeArray = cls.isArray() ? writeArray(type2, obj, nodeMap) : cls;
        if (cls != type2) {
            nodeMap.put(this.label, writeArray.getName());
        }
        return false;
    }
}
