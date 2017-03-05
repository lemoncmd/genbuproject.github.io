package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import net.hockeyapp.android.BuildConfig;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.strategy.Type;
import org.simpleframework.xml.stream.Format;

class TextLabel extends TemplateLabel {
    private Contact contact;
    private boolean data;
    private Introspector detail;
    private String empty;
    private Text label;
    private Expression path;
    private boolean required;
    private Class type;

    public TextLabel(Contact contact, Text text, Format format) {
        this.detail = new Introspector(contact, this, format);
        this.required = text.required();
        this.type = contact.getType();
        this.empty = text.empty();
        this.data = text.data();
        this.contact = contact;
        this.label = text;
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public Contact getContact() {
        return this.contact;
    }

    public Converter getConverter(Context context) throws Exception {
        String empty = getEmpty(context);
        Type contact = getContact();
        if (context.isPrimitive(contact)) {
            return new Primitive(context, contact, empty);
        }
        throw new TextException("Cannot use %s to represent %s", contact, this.label);
    }

    public Decorator getDecorator() throws Exception {
        return null;
    }

    public String getEmpty(Context context) {
        return this.detail.isEmpty(this.empty) ? null : this.empty;
    }

    public Expression getExpression() throws Exception {
        if (this.path == null) {
            this.path = this.detail.getExpression();
        }
        return this.path;
    }

    public String getName() {
        return BuildConfig.FLAVOR;
    }

    public String getOverride() {
        return this.contact.toString();
    }

    public String getPath() throws Exception {
        return getExpression().getPath();
    }

    public Class getType() {
        return this.type;
    }

    public boolean isData() {
        return this.data;
    }

    public boolean isInline() {
        return true;
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isText() {
        return true;
    }

    public String toString() {
        return this.detail.toString();
    }
}
