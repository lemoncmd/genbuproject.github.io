package org.simpleframework.xml.core;

import java.util.Collection;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

class TextList implements Repeater {
    private final CollectionFactory factory;
    private final Primitive primitive;
    private final Type type = new ClassType(String.class);

    public TextList(Context context, Type type, Label label) {
        this.factory = new CollectionFactory(context, type);
        this.primitive = new Primitive(context, this.type);
    }

    public Object read(InputNode inputNode) throws Exception {
        Instance instance = this.factory.getInstance(inputNode);
        return instance.isReference() ? instance.getInstance() : read(inputNode, instance.getInstance());
    }

    public Object read(InputNode inputNode, Object obj) throws Exception {
        Collection collection = (Collection) obj;
        Object read = this.primitive.read(inputNode);
        if (read != null) {
            collection.add(read);
        }
        return obj;
    }

    public boolean validate(InputNode inputNode) throws Exception {
        return true;
    }

    public void write(OutputNode outputNode, Object obj) throws Exception {
        Collection<Object> collection = (Collection) obj;
        OutputNode parent = outputNode.getParent();
        for (Object write : collection) {
            this.primitive.write(parent, write);
        }
    }
}
