package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;

class LabelKey {
    private final Class label;
    private final String name;
    private final Class owner;
    private final Class type;

    public LabelKey(Contact contact, Annotation annotation) {
        this.owner = contact.getDeclaringClass();
        this.label = annotation.annotationType();
        this.name = contact.getName();
        this.type = contact.getType();
    }

    private boolean equals(LabelKey labelKey) {
        return labelKey == this ? true : (labelKey.label == this.label && labelKey.owner == this.owner && labelKey.type == this.type) ? labelKey.name.equals(this.name) : false;
    }

    public boolean equals(Object obj) {
        return obj instanceof LabelKey ? equals((LabelKey) obj) : false;
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.owner.hashCode();
    }

    public String toString() {
        return String.format("key '%s' for %s", new Object[]{this.name, this.owner});
    }
}
