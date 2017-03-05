package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.LinkedHashMap;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.stream.Format;

class GroupExtractor implements Group {
    private final LabelMap elements = new LabelMap();
    private final ExtractorFactory factory;
    private final Annotation label;
    private final Registry registry = new Registry(this.elements);

    private static class Registry extends LinkedHashMap<Class, Label> implements Iterable<Label> {
        private LabelMap elements;
        private Label text;

        public Registry(LabelMap labelMap) {
            this.elements = labelMap;
        }

        private void registerElement(Class cls, Label label) throws Exception {
            String name = label.getName();
            if (!this.elements.containsKey(name)) {
                this.elements.put(name, label);
            }
            if (!containsKey(cls)) {
                put(cls, label);
            }
        }

        private void registerText(Label label) throws Exception {
            Text text = (Text) label.getContact().getAnnotation(Text.class);
            if (text != null) {
                this.text = new TextListLabel(label, text);
            }
        }

        private Label resolveElement(Class cls) {
            while (cls != null) {
                Label label = (Label) get(cls);
                if (label != null) {
                    return label;
                }
                cls = cls.getSuperclass();
            }
            return null;
        }

        private Label resolveText(Class cls) {
            return (this.text == null || cls != String.class) ? null : this.text;
        }

        public boolean isText() {
            return this.text != null;
        }

        public Iterator<Label> iterator() {
            return values().iterator();
        }

        public void register(Class cls, Label label) throws Exception {
            Label cacheLabel = new CacheLabel(label);
            registerElement(cls, cacheLabel);
            registerText(cacheLabel);
        }

        public Label resolve(Class cls) {
            Label resolveText = resolveText(cls);
            return resolveText == null ? resolveElement(cls) : resolveText;
        }

        public Label resolveText() {
            return resolveText(String.class);
        }
    }

    public GroupExtractor(Contact contact, Annotation annotation, Format format) throws Exception {
        this.factory = new ExtractorFactory(contact, annotation, format);
        this.label = annotation;
        extract();
    }

    private void extract() throws Exception {
        Extractor instance = this.factory.getInstance();
        if (instance != null) {
            extract(instance);
        }
    }

    private void extract(Extractor extractor) throws Exception {
        for (Annotation extract : extractor.getAnnotations()) {
            extract(extractor, extract);
        }
    }

    private void extract(Extractor extractor, Annotation annotation) throws Exception {
        Label label = extractor.getLabel(annotation);
        Class type = extractor.getType(annotation);
        if (this.registry != null) {
            this.registry.register(type, label);
        }
    }

    public LabelMap getElements() throws Exception {
        return this.elements.getLabels();
    }

    public Label getLabel(Class cls) {
        return this.registry.resolve(cls);
    }

    public String[] getNames() throws Exception {
        return this.elements.getKeys();
    }

    public String[] getPaths() throws Exception {
        return this.elements.getPaths();
    }

    public Label getText() {
        return this.registry.resolveText();
    }

    public boolean isDeclared(Class cls) {
        return this.registry.containsKey(cls);
    }

    public boolean isInline() {
        Iterator it = this.registry.iterator();
        while (it.hasNext()) {
            if (!((Label) it.next()).isInline()) {
                break;
            }
        }
        return !this.registry.isEmpty();
    }

    public boolean isTextList() {
        return this.registry.isText();
    }

    public boolean isValid(Class cls) {
        return this.registry.resolve(cls) != null;
    }

    public String toString() {
        return this.label.toString();
    }
}
