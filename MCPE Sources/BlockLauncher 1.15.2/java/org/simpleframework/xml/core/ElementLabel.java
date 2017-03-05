package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;

class ElementLabel extends TemplateLabel {
    private Expression cache;
    private boolean data;
    private Decorator decorator;
    private Introspector detail;
    private Class expect;
    private Format format;
    private Element label;
    private String name;
    private String override;
    private String path;
    private boolean required;
    private Class type;

    public ElementLabel(Contact contact, Element element, Format format) {
        this.detail = new Introspector(contact, this, format);
        this.decorator = new Qualifier(contact);
        this.required = element.required();
        this.type = contact.getType();
        this.override = element.name();
        this.expect = element.type();
        this.data = element.data();
        this.format = format;
        this.label = element;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Contact getContact() {
        return this.detail.getContact();
    }

    public Converter getConverter(Context context) throws Exception {
        Type contact = getContact();
        return context.isPrimitive(contact) ? new Primitive(context, contact) : this.expect == Void.TYPE ? new Composite(context, contact) : new Composite(context, contact, this.expect);
    }

    public Decorator getDecorator() throws Exception {
        return this.decorator;
    }

    public Object getEmpty(Context context) {
        return null;
    }

    public Expression getExpression() throws Exception {
        if (this.cache == null) {
            this.cache = this.detail.getExpression();
        }
        return this.cache;
    }

    public String getName() throws Exception {
        if (this.name == null) {
            this.name = this.format.getStyle().getElement(this.detail.getName());
        }
        return this.name;
    }

    public String getOverride() {
        return this.override;
    }

    public String getPath() throws Exception {
        if (this.path == null) {
            this.path = getExpression().getElement(getName());
        }
        return this.path;
    }

    public Class getType() {
        return this.expect == Void.TYPE ? this.type : this.expect;
    }

    public Type getType(Class cls) {
        Type contact = getContact();
        return this.expect == Void.TYPE ? contact : new OverrideType(contact, this.expect);
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
