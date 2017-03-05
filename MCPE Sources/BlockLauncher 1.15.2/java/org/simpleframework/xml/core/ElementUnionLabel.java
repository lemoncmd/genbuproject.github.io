package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;

class ElementUnionLabel extends TemplateLabel {
    private Contact contact;
    private GroupExtractor extractor;
    private Label label;
    private Expression path;
    private ElementUnion union;

    public ElementUnionLabel(Contact contact, ElementUnion elementUnion, Element element, Format format) throws Exception {
        this.extractor = new GroupExtractor(contact, elementUnion, format);
        this.label = new ElementLabel(contact, element, format);
        this.contact = contact;
        this.union = elementUnion;
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
            return new CompositeUnion(context, this.extractor, expression, contact);
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

    public Label getLabel(Class cls) throws Exception {
        Contact contact = getContact();
        if (this.extractor.isValid(cls)) {
            return this.extractor.getLabel(cls);
        }
        throw new UnionException("No type matches %s in %s for %s", cls, this.union, contact);
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

    public Type getType(Class cls) throws Exception {
        Type contact = getContact();
        if (this.extractor.isValid(cls)) {
            return this.extractor.isDeclared(cls) ? new OverrideType(contact, cls) : contact;
        } else {
            throw new UnionException("No type matches %s in %s for %s", cls, this.union, contact);
        }
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
