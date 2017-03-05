package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Version;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.util.Cache;
import org.simpleframework.xml.util.ConcurrentCache;

class LabelExtractor {
    private final Cache<LabelGroup> cache = new ConcurrentCache();
    private final Format format;

    private static class LabelBuilder {
        private final Class entry;
        private final Class label;
        private final Class type;

        public LabelBuilder(Class cls, Class cls2) {
            this(cls, cls2, null);
        }

        public LabelBuilder(Class cls, Class cls2, Class cls3) {
            this.entry = cls3;
            this.label = cls2;
            this.type = cls;
        }

        private Constructor getConstructor(Class cls) throws Exception {
            return this.type.getConstructor(new Class[]{Contact.class, cls, Format.class});
        }

        private Constructor getConstructor(Class cls, Class cls2) throws Exception {
            return this.type.getConstructor(new Class[]{Contact.class, cls, cls2, Format.class});
        }

        public Constructor getConstructor() throws Exception {
            return this.entry != null ? getConstructor(this.label, this.entry) : getConstructor(this.label);
        }
    }

    public LabelExtractor(Format format) {
        this.format = format;
    }

    private Annotation[] getAnnotations(Annotation annotation) throws Exception {
        Method[] declaredMethods = annotation.annotationType().getDeclaredMethods();
        return declaredMethods.length > 0 ? (Annotation[]) declaredMethods[0].invoke(annotation, new Object[0]) : new Annotation[0];
    }

    private LabelBuilder getBuilder(Annotation annotation) throws Exception {
        if (annotation instanceof Element) {
            return new LabelBuilder(ElementLabel.class, Element.class);
        }
        if (annotation instanceof ElementList) {
            return new LabelBuilder(ElementListLabel.class, ElementList.class);
        }
        if (annotation instanceof ElementArray) {
            return new LabelBuilder(ElementArrayLabel.class, ElementArray.class);
        }
        if (annotation instanceof ElementMap) {
            return new LabelBuilder(ElementMapLabel.class, ElementMap.class);
        }
        if (annotation instanceof ElementUnion) {
            return new LabelBuilder(ElementUnionLabel.class, ElementUnion.class, Element.class);
        }
        if (annotation instanceof ElementListUnion) {
            return new LabelBuilder(ElementListUnionLabel.class, ElementListUnion.class, ElementList.class);
        }
        if (annotation instanceof ElementMapUnion) {
            return new LabelBuilder(ElementMapUnionLabel.class, ElementMapUnion.class, ElementMap.class);
        }
        if (annotation instanceof Attribute) {
            return new LabelBuilder(AttributeLabel.class, Attribute.class);
        }
        if (annotation instanceof Version) {
            return new LabelBuilder(VersionLabel.class, Version.class);
        }
        if (annotation instanceof Text) {
            return new LabelBuilder(TextLabel.class, Text.class);
        }
        throw new PersistenceException("Annotation %s not supported", annotation);
    }

    private Constructor getConstructor(Annotation annotation) throws Exception {
        Constructor constructor = getBuilder(annotation).getConstructor();
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor;
    }

    private LabelGroup getGroup(Contact contact, Annotation annotation, Object obj) throws Exception {
        LabelGroup labelGroup = (LabelGroup) this.cache.fetch(obj);
        if (labelGroup == null) {
            labelGroup = getLabels(contact, annotation);
            if (labelGroup != null) {
                this.cache.cache(obj, labelGroup);
            }
        }
        return labelGroup;
    }

    private Object getKey(Contact contact, Annotation annotation) {
        return new LabelKey(contact, annotation);
    }

    private Label getLabel(Contact contact, Annotation annotation, Annotation annotation2) throws Exception {
        Constructor constructor = getConstructor(annotation);
        if (annotation2 != null) {
            return (Label) constructor.newInstance(new Object[]{contact, annotation, annotation2, this.format});
        }
        return (Label) constructor.newInstance(new Object[]{contact, annotation, this.format});
    }

    private LabelGroup getLabels(Contact contact, Annotation annotation) throws Exception {
        return annotation instanceof ElementUnion ? getUnion(contact, annotation) : annotation instanceof ElementListUnion ? getUnion(contact, annotation) : annotation instanceof ElementMapUnion ? getUnion(contact, annotation) : getSingle(contact, annotation);
    }

    private LabelGroup getSingle(Contact contact, Annotation annotation) throws Exception {
        Label label = getLabel(contact, annotation, null);
        return new LabelGroup(label != null ? new CacheLabel(label) : label);
    }

    private LabelGroup getUnion(Contact contact, Annotation annotation) throws Exception {
        Annotation[] annotations = getAnnotations(annotation);
        if (annotations.length <= 0) {
            return null;
        }
        List linkedList = new LinkedList();
        for (Annotation label : annotations) {
            Object cacheLabel;
            Label label2 = getLabel(contact, annotation, label);
            if (label2 != null) {
                cacheLabel = new CacheLabel(label2);
            } else {
                Label label3 = label2;
            }
            linkedList.add(cacheLabel);
        }
        return new LabelGroup(linkedList);
    }

    public Label getLabel(Contact contact, Annotation annotation) throws Exception {
        LabelGroup group = getGroup(contact, annotation, getKey(contact, annotation));
        return group != null ? group.getPrimary() : null;
    }

    public List<Label> getList(Contact contact, Annotation annotation) throws Exception {
        LabelGroup group = getGroup(contact, annotation, getKey(contact, annotation));
        return group != null ? group.getList() : Collections.emptyList();
    }
}
