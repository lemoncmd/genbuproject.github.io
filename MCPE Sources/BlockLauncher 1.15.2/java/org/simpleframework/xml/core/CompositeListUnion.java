package org.simpleframework.xml.core;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;
import org.simpleframework.xml.stream.Style;

class CompositeListUnion implements Repeater {
    private final Context context;
    private final LabelMap elements;
    private final Group group;
    private final Expression path;
    private final Style style;
    private final Type type;

    public CompositeListUnion(Context context, Group group, Expression expression, Type type) throws Exception {
        this.elements = group.getElements();
        this.style = context.getStyle();
        this.context = context;
        this.group = group;
        this.type = type;
        this.path = expression;
    }

    private Object readElement(InputNode inputNode) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(inputNode.getName()))).getConverter(this.context).read(inputNode);
    }

    private Object readElement(InputNode inputNode, Object obj) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(inputNode.getName()))).getConverter(this.context).read(inputNode, obj);
    }

    private Object readText(InputNode inputNode) throws Exception {
        return this.group.getText().getConverter(this.context).read(inputNode);
    }

    private Object readText(InputNode inputNode, Object obj) throws Exception {
        return this.group.getText().getConverter(this.context).read(inputNode.getParent(), obj);
    }

    private void write(OutputNode outputNode, Object obj, Label label) throws Exception {
        Converter converter = label.getConverter(this.context);
        Set singleton = Collections.singleton(obj);
        if (!label.isInline()) {
            String element = this.style.getElement(label.getName());
            if (!outputNode.isCommitted()) {
                outputNode.setName(element);
            }
        }
        converter.write(outputNode, singleton);
    }

    private void write(OutputNode outputNode, Collection collection) throws Exception {
        for (Object next : collection) {
            if (next != null) {
                Label label = this.group.getLabel(next.getClass());
                if (label == null) {
                    throw new UnionException("Entry of %s not declared in %s with annotation %s", r2, this.type, this.group);
                }
                write(outputNode, next, label);
            }
        }
    }

    public Object read(InputNode inputNode) throws Exception {
        return this.group.getText() == null ? readElement(inputNode) : readText(inputNode);
    }

    public Object read(InputNode inputNode, Object obj) throws Exception {
        return this.group.getText() != null ? readText(inputNode, obj) : readElement(inputNode, obj);
    }

    public boolean validate(InputNode inputNode) throws Exception {
        return ((Label) this.elements.get(this.path.getElement(inputNode.getName()))).getConverter(this.context).validate(inputNode);
    }

    public void write(OutputNode outputNode, Object obj) throws Exception {
        Collection collection = (Collection) obj;
        if (!this.group.isInline()) {
            write(outputNode, collection);
        } else if (!collection.isEmpty()) {
            write(outputNode, collection);
        } else if (!outputNode.isCommitted()) {
            outputNode.remove();
        }
    }
}
