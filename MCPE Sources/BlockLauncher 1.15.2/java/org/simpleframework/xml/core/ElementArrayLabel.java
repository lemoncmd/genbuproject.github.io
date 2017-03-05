package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Style;

class ElementArrayLabel extends TemplateLabel {
    private boolean data;
    private Decorator decorator;
    private Introspector detail;
    private String entry;
    private Format format;
    private ElementArray label;
    private String name;
    private Expression path;
    private boolean required;
    private Class type;

    public ElementArrayLabel(Contact contact, ElementArray elementArray, Format format) {
        this.detail = new Introspector(contact, this, format);
        this.decorator = new Qualifier(contact);
        this.required = elementArray.required();
        this.type = contact.getType();
        this.entry = elementArray.entry();
        this.data = elementArray.data();
        this.name = elementArray.name();
        this.format = format;
        this.label = elementArray;
    }

    private Converter getConverter(Context context, String str) throws Exception {
        Type dependent = getDependent();
        Type contact = getContact();
        return !context.isPrimitive(dependent) ? new CompositeArray(context, contact, dependent, str) : new PrimitiveArray(context, contact, dependent, str);
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Contact getContact() {
        return this.detail.getContact();
    }

    public Converter getConverter(Context context) throws Exception {
        Contact contact = getContact();
        String entry = getEntry();
        if (this.type.isArray()) {
            return getConverter(context, entry);
        }
        throw new InstantiationException("Type is not an array %s for %s", this.type, contact);
    }

    public Decorator getDecorator() throws Exception {
        return this.decorator;
    }

    public Type getDependent() {
        Class componentType = this.type.getComponentType();
        return componentType == null ? new ClassType(this.type) : new ClassType(componentType);
    }

    public Object getEmpty(Context context) throws Exception {
        return !this.label.empty() ? new ArrayFactory(context, new ClassType(this.type)).getInstance() : null;
    }

    public String getEntry() throws Exception {
        Style style = this.format.getStyle();
        if (this.detail.isEmpty(this.entry)) {
            this.entry = this.detail.getEntry();
        }
        return style.getElement(this.entry);
    }

    public Expression getExpression() throws Exception {
        if (this.path == null) {
            this.path = this.detail.getExpression();
        }
        return this.path;
    }

    public String getName() throws Exception {
        return this.format.getStyle().getElement(this.detail.getName());
    }

    public String getOverride() {
        return this.name;
    }

    public String getPath() throws Exception {
        return getExpression().getElement(getName());
    }

    public Class getType() {
        return this.type;
    }

    public boolean isData() {
        return this.data;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String toString() {
        return this.detail.toString();
    }
}
