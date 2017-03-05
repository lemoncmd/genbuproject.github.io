package org.simpleframework.xml.core;

import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class CompositeValue implements Converter {
    private final Context context;
    private final Entry entry;
    private final Traverser root;
    private final Style style;
    private final Type type;

    public CompositeValue(Context context, Entry entry, Type type) throws Exception {
        this.root = new Traverser(context);
        this.style = context.getStyle();
        this.context = context;
        this.entry = entry;
        this.type = type;
    }

    private boolean validate(InputNode inputNode, String str) throws Exception {
        InputNode next = inputNode.getNext(this.style.getElement(str));
        return (next == null || next.isEmpty()) ? true : this.root.validate(next, this.type.getType());
    }

    public Object read(InputNode inputNode) throws Exception {
        InputNode next = inputNode.getNext();
        return (next == null || next.isEmpty()) ? null : this.root.read(next, this.type.getType());
    }

    public Object read(InputNode inputNode, Object obj) throws Exception {
        Class type = this.type.getType();
        if (obj == null) {
            return read(inputNode);
        }
        throw new PersistenceException("Can not read value of %s for %s", type, this.entry);
    }

    public boolean validate(InputNode inputNode) throws Exception {
        Class type = this.type.getType();
        String value = this.entry.getValue();
        if (value == null) {
            value = this.context.getName(type);
        }
        return validate(inputNode, value);
    }

    public void write(OutputNode outputNode, Object obj) throws Exception {
        Class type = this.type.getType();
        String value = this.entry.getValue();
        if (value == null) {
            value = this.context.getName(type);
        }
        this.root.write(outputNode, obj, type, this.style.getElement(value));
    }
}
