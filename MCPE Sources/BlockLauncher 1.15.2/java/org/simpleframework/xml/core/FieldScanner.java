package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Transient;
import org.simpleframework.xml.Version;

class FieldScanner extends ContactList {
    private final ContactMap done = new ContactMap();
    private final AnnotationFactory factory;
    private final Support support;

    private static class FieldKey {
        private final String name;
        private final Class type;

        public FieldKey(Field field) {
            this.type = field.getDeclaringClass();
            this.name = field.getName();
        }

        private boolean equals(FieldKey fieldKey) {
            return fieldKey.type != this.type ? false : fieldKey.name.equals(this.name);
        }

        public boolean equals(Object obj) {
            return obj instanceof FieldKey ? equals((FieldKey) obj) : false;
        }

        public int hashCode() {
            return this.name.hashCode();
        }
    }

    public FieldScanner(Detail detail, Support support) throws Exception {
        this.factory = new AnnotationFactory(detail, support);
        this.support = support;
        scan(detail);
    }

    private void build() {
        Iterator it = this.done.iterator();
        while (it.hasNext()) {
            add((Contact) it.next());
        }
    }

    private void extend(Class cls, DefaultType defaultType) throws Exception {
        Collection fields = this.support.getFields(cls, defaultType);
        if (fields != null) {
            addAll(fields);
        }
    }

    private void extract(Detail detail) {
        for (FieldDetail fieldDetail : detail.getFields()) {
            Annotation[] annotations = fieldDetail.getAnnotations();
            Field field = fieldDetail.getField();
            for (Annotation scan : annotations) {
                scan(field, scan, annotations);
            }
        }
    }

    private void extract(Detail detail, DefaultType defaultType) throws Exception {
        List<FieldDetail> fields = detail.getFields();
        if (defaultType == DefaultType.FIELD) {
            for (FieldDetail fieldDetail : fields) {
                Annotation[] annotations = fieldDetail.getAnnotations();
                Field field = fieldDetail.getField();
                Class type = field.getType();
                if (!(isStatic(field) || isTransient(field))) {
                    process(field, type, annotations);
                }
            }
        }
    }

    private void insert(Object obj, Contact contact) {
        Object obj2;
        Contact contact2 = (Contact) this.done.remove(obj);
        if (contact2 != null && isText(contact)) {
            obj2 = contact2;
        }
        this.done.put(obj, obj2);
    }

    private boolean isStatic(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }

    private boolean isText(Contact contact) {
        return contact.getAnnotation() instanceof Text;
    }

    private boolean isTransient(Field field) {
        return Modifier.isTransient(field.getModifiers());
    }

    private void process(Field field, Class cls, Annotation[] annotationArr) throws Exception {
        Annotation instance = this.factory.getInstance(cls, Reflector.getDependents(field));
        if (instance != null) {
            process(field, instance, annotationArr);
        }
    }

    private void process(Field field, Annotation annotation, Annotation[] annotationArr) {
        Contact fieldContact = new FieldContact(field, annotation, annotationArr);
        FieldKey fieldKey = new FieldKey(field);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        insert(fieldKey, fieldContact);
    }

    private void remove(Field field, Annotation annotation) {
        this.done.remove(new FieldKey(field));
    }

    private void scan(Field field, Annotation annotation, Annotation[] annotationArr) {
        if (annotation instanceof Attribute) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof ElementUnion) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof ElementListUnion) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof ElementMapUnion) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof ElementList) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof ElementArray) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof ElementMap) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof Element) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof Version) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof Text) {
            process(field, annotation, annotationArr);
        }
        if (annotation instanceof Transient) {
            remove(field, annotation);
        }
    }

    private void scan(Detail detail) throws Exception {
        DefaultType override = detail.getOverride();
        DefaultType access = detail.getAccess();
        Class cls = detail.getSuper();
        if (cls != null) {
            extend(cls, override);
        }
        extract(detail, access);
        extract(detail);
        build();
    }
}
