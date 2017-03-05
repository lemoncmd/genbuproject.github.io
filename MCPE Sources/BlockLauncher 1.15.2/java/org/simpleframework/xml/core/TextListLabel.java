package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.strategy.Type;

class TextListLabel extends TemplateLabel {
    private final String empty;
    private final Label label;
    private final Text text;

    public TextListLabel(Label label, Text text) {
        this.empty = text.empty();
        this.label = label;
        this.text = text;
    }

    public Annotation getAnnotation() {
        return this.label.getAnnotation();
    }

    public Contact getContact() {
        return this.label.getContact();
    }

    public Converter getConverter(Context context) throws Exception {
        Type contact = getContact();
        if (this.label.isCollection()) {
            return new TextList(context, contact, this.label);
        }
        throw new TextException("Cannot use %s to represent %s", contact, this.label);
    }

    public Decorator getDecorator() throws Exception {
        return null;
    }

    public Type getDependent() throws Exception {
        return this.label.getDependent();
    }

    public String getEmpty(Context context) throws Exception {
        return this.empty;
    }

    public String getEntry() throws Exception {
        return this.label.getEntry();
    }

    public Expression getExpression() throws Exception {
        return this.label.getExpression();
    }

    public Object getKey() throws Exception {
        return this.label.getKey();
    }

    public String getName() throws Exception {
        return this.label.getName();
    }

    public String[] getNames() throws Exception {
        return this.label.getNames();
    }

    public String getOverride() {
        return this.label.getOverride();
    }

    public String getPath() throws Exception {
        return this.label.getPath();
    }

    public String[] getPaths() throws Exception {
        return this.label.getPaths();
    }

    public Class getType() {
        return this.label.getType();
    }

    public boolean isCollection() {
        return true;
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
        return true;
    }

    public String toString() {
        return String.format("%s %s", new Object[]{this.text, this.label});
    }
}
