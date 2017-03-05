package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;

class ElementListUnionLabel extends TemplateLabel {
    private Contact contact;
    private GroupExtractor extractor;
    private Label label;
    private Expression path;

    public ElementListUnionLabel(Contact contact, ElementListUnion elementListUnion, ElementList elementList, Format format) throws Exception {
        this.label = new ElementListLabel(contact, elementList, format);
        this.extractor = new GroupExtractor(contact, elementListUnion, format);
        this.contact = contact;
    }

    public Annotation getAnnotation() {
        return this.label.getAnnotation();
    }

    public Contact getContact() {
        return this.contact;
    }

    public Converter getConverter(Context context) throws Exception {
        Expression expression = getExpression();
        Type contact = getContact();
        if (contact != null) {
            return new CompositeListUnion(context, this.extractor, expression, contact);
        }
        throw new UnionException("Union %s was not declared on a field or method", this.label);
    }

    public Decorator getDecorator() throws Exception {
        return this.label.getDecorator();
    }

    public Type getDependent() throws Exception {
        return this.label.getDependent();
    }

    public Object getEmpty(Context context) throws Exception {
        return this.label.getEmpty(context);
    }

    public String getEntry() throws Exception {
        return this.label.getEntry();
    }

    public Expression getExpression() throws Exception {
        if (this.path == null) {
            this.path = this.label.getExpression();
        }
        return this.path;
    }

    public Label getLabel(Class cls) {
        return this;
    }

    public String getName() throws Exception {
        return this.label.getName();
    }

    public String[] getNames() throws Exception {
        return this.extractor.getNames();
    }

    public String getOverride() {
        return this.label.getOverride();
    }

    public String getPath() throws Exception {
        return this.label.getPath();
    }

    public String[] getPaths() throws Exception {
        return this.extractor.getPaths();
    }

    public Class getType() {
        return this.label.getType();
    }

    public Type getType(Class cls) {
        return getContact();
    }

    public boolean isCollection() {
        return this.label.isCollection();
    }

    public boolean isData() {
        return this.label.isData();
    }

    public boolean isInline() {
        return this.label.isInline();
    }

    public boolean isRequired() {
        return this.label.isRequired();
    }

    public boolean isTextList() {
        return this.extractor.isTextList();
    }

    public boolean isUnion() {
        return true;
    }

    public String toString() {
        return this.label.toString();
    }
}
