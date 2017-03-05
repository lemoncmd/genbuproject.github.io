package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.Version;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;

class VersionLabel extends TemplateLabel {
    private Decorator decorator;
    private Introspector detail;
    private Format format;
    private Version label;
    private String name;
    private Expression path;
    private boolean required;
    private Class type;

    public VersionLabel(Contact contact, Version version, Format format) {
        this.detail = new Introspector(contact, this, format);
        this.decorator = new Qualifier(contact);
        this.required = version.required();
        this.type = contact.getType();
        this.name = version.name();
        this.format = format;
        this.label = version;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Contact getContact() {
        return this.detail.getContact();
    }

    public Converter getConverter(Context context) throws Exception {
        String empty = getEmpty(context);
        Type contact = getContact();
        if (context.isFloat(contact)) {
            return new Primitive(context, contact, empty);
        }
        throw new AttributeException("Cannot use %s to represent %s", this.label, contact);
    }

    public Decorator getDecorator() throws Exception {
        return this.decorator;
    }

    public String getEmpty(Context context) {
        return null;
    }

    public Expression getExpression() throws Exception {
        if (this.path == null) {
            this.path = this.detail.getExpression();
        }
        return this.path;
    }

    public String getName() throws Exception {
        return this.format.getStyle().getAttribute(this.detail.getName());
    }

    public String getOverride() {
        return this.name;
    }

    public String getPath() throws Exception {
        return getExpression().getAttribute(getName());
    }

    public Class getType() {
        return this.type;
    }

    public boolean isAttribute() {
        return true;
    }

    public boolean isData() {
        return false;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String toString() {
        return this.detail.toString();
    }
}
