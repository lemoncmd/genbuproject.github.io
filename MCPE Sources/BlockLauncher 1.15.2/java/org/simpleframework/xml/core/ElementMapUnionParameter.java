package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.stream.Format;

class ElementMapUnionParameter extends TemplateParameter {
    private final Contact contact;
    private final Expression expression = this.label.getExpression();
    private final int index;
    private final Object key = this.label.getKey();
    private final Label label;
    private final String name = this.label.getName();
    private final String path = this.label.getPath();
    private final Class type = this.label.getType();

    private static class Contact extends ParameterContact<ElementMap> {
        public Contact(ElementMap elementMap, Constructor constructor, int i) {
            super(elementMap, constructor, i);
        }

        public String getName() {
            return ((ElementMap) this.label).name();
        }
    }

    public ElementMapUnionParameter(Constructor constructor, ElementMapUnion elementMapUnion, ElementMap elementMap, Format format, int i) throws Exception {
        this.contact = new Contact(elementMap, constructor, i);
        this.label = new ElementMapUnionLabel(this.contact, elementMapUnion, elementMap, format);
        this.index = i;
    }

    public Annotation getAnnotation() {
        return this.contact.getAnnotation();
    }

    public Expression getExpression() {
        return this.expression;
    }

    public int getIndex() {
        return this.index;
    }

    public Object getKey() {
        return this.key;
    }

    public String getName() {
        return this.name;
    }

    public String getPath() {
        return this.path;
    }

    public Class getType() {
        return this.type;
    }

    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    public boolean isRequired() {
        return this.label.isRequired();
    }

    public String toString() {
        return this.contact.toString();
    }
}
