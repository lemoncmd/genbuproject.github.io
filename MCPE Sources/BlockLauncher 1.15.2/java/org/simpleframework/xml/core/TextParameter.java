package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import net.hockeyapp.android.BuildConfig;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.stream.Format;

class TextParameter extends TemplateParameter {
    private final Contact contact;
    private final Expression expression = this.label.getExpression();
    private final int index;
    private final Object key = this.label.getKey();
    private final Label label;
    private final String name = this.label.getName();
    private final String path = this.label.getPath();
    private final Class type = this.label.getType();

    private static class Contact extends ParameterContact<Text> {
        public Contact(Text text, Constructor constructor, int i) {
            super(text, constructor, i);
        }

        public String getName() {
            return BuildConfig.FLAVOR;
        }
    }

    public TextParameter(Constructor constructor, Text text, Format format, int i) throws Exception {
        this.contact = new Contact(text, constructor, i);
        this.label = new TextLabel(this.contact, text, format);
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

    public String getName(Context context) {
        return getName();
    }

    public String getPath() {
        return this.path;
    }

    public String getPath(Context context) {
        return getPath();
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

    public boolean isText() {
        return true;
    }

    public String toString() {
        return this.contact.toString();
    }
}
