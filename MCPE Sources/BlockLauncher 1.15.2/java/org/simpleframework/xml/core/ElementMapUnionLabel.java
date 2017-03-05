package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;

class ElementMapUnionLabel extends TemplateLabel {
    private Contact contact;
    private GroupExtractor extractor;
    private Label label;
    private Expression path;

    public ElementMapUnionLabel(Contact contact, ElementMapUnion elementMapUnion, ElementMap elementMap, Format format) throws Exception {
        this.extractor = new GroupExtractor(contact, elementMapUnion, format);
        this.label = new ElementMapLabel(contact, elementMap, format);
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
            return new CompositeMapUnion(context, this.extractor, expression, contact);
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

    public boolean isUnion() {
        return true;
    }

    public String toString() {
        return this.label.toString();
    }
}
