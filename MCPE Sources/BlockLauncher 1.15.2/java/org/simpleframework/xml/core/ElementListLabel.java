package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Style;

class ElementListLabel extends TemplateLabel {
    private Expression cache;
    private boolean data;
    private Decorator decorator;
    private Introspector detail;
    private String entry;
    private Format format;
    private boolean inline;
    private Class item;
    private ElementList label;
    private String name;
    private String override;
    private String path;
    private boolean required;
    private Class type;

    public ElementListLabel(Contact contact, ElementList elementList, Format format) {
        this.detail = new Introspector(contact, this, format);
        this.decorator = new Qualifier(contact);
        this.required = elementList.required();
        this.type = contact.getType();
        this.override = elementList.name();
        this.inline = elementList.inline();
        this.entry = elementList.entry();
        this.data = elementList.data();
        this.item = elementList.type();
        this.format = format;
        this.label = elementList;
    }

    private Converter getConverter(Context context, String str) throws Exception {
        Type dependent = getDependent();
        Type contact = getContact();
        return !context.isPrimitive(dependent) ? new CompositeList(context, contact, dependent, str) : new PrimitiveList(context, contact, dependent, str);
    }

    private Converter getInlineConverter(Context context, String str) throws Exception {
        Type dependent = getDependent();
        Type contact = getContact();
        return !context.isPrimitive(dependent) ? new CompositeInlineList(context, contact, dependent, str) : new PrimitiveInlineList(context, contact, dependent, str);
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Contact getContact() {
        return this.detail.getContact();
    }

    public Converter getConverter(Context context) throws Exception {
        String entry = getEntry();
        return !this.label.inline() ? getConverter(context, entry) : getInlineConverter(context, entry);
    }

    public Decorator getDecorator() throws Exception {
        return this.decorator;
    }

    public Type getDependent() throws Exception {
        Contact contact = getContact();
        if (this.item == Void.TYPE) {
            this.item = contact.getDependent();
        }
        if (this.item != null) {
            return new ClassType(this.item);
        }
        throw new ElementException("Unable to determine generic type for %s", contact);
    }

    public Object getEmpty(Context context) throws Exception {
        return !this.label.empty() ? new CollectionFactory(context, new ClassType(this.type)).getInstance() : null;
    }

    public String getEntry() throws Exception {
        Style style = this.format.getStyle();
        if (this.detail.isEmpty(this.entry)) {
            this.entry = this.detail.getEntry();
        }
        return style.getElement(this.entry);
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
        return this.type;
    }

    public boolean isCollection() {
        return true;
    }

    public boolean isData() {
        return this.data;
    }

    public boolean isInline() {
        return this.inline;
    }

    public boolean isRequired() {
        return this.required;
    }

    public String toString() {
        return this.detail.toString();
    }
}
