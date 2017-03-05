package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.stream.Format;

class ElementUnionParameter extends TemplateParameter {
    private final Contact contact;
    private final Expression expression = this.label.getExpression();
    private final int index;
    private final Object key = this.label.getKey();
    private final Label label;
    private final String name = this.label.getName();
    private final String path = this.label.getPath();
    private final Class type = this.label.getType();

    private static class Contact extends ParameterContact<Element> {
        public Contact(Element element, Constructor constructor, int i) {
            super(element, constructor, i);
        }

        public String getName() {
            return ((Element) this.label).name();
        }
    }

    public ElementUnionParameter(Constructor constructor, ElementUnion elementUnion, Element element, Format format, int i) throws Exception {
        this.contact = new Contact(element, constructor, i);
        this.label = new ElementUnionLabel(this.contact, elementUnion, element, format);
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
