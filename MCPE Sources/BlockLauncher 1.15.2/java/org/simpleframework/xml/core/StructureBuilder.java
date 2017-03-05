package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementListUnion;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.ElementMapUnion;
import org.simpleframework.xml.ElementUnion;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.Version;

class StructureBuilder {
    private ModelAssembler assembler;
    private LabelMap attributes;
    private ExpressionBuilder builder;
    private LabelMap elements;
    private Instantiator factory;
    private boolean primitive;
    private InstantiatorBuilder resolver;
    private Model root;
    private Scanner scanner;
    private Support support;
    private Label text;
    private LabelMap texts;
    private Label version;

    public StructureBuilder(Scanner scanner, Detail detail, Support support) throws Exception {
        this.builder = new ExpressionBuilder(detail, support);
        this.assembler = new ModelAssembler(this.builder, detail, support);
        this.resolver = new InstantiatorBuilder(scanner, detail);
        this.root = new TreeModel(scanner, detail);
        this.attributes = new LabelMap(scanner);
        this.elements = new LabelMap(scanner);
        this.texts = new LabelMap(scanner);
        this.scanner = scanner;
        this.support = support;
    }

    private Model create(Expression expression) throws Exception {
        Model model = this.root;
        while (model != null) {
            String prefix = expression.getPrefix();
            String first = expression.getFirst();
            int index = expression.getIndex();
            if (first != null) {
                model = model.register(first, prefix, index);
            }
            if (!expression.isPath()) {
                break;
            }
            expression = expression.getPath(1);
        }
        return model;
    }

    private boolean isAttribute(String str) throws Exception {
        Expression build = this.builder.build(str);
        Model lookup = lookup(build);
        if (lookup == null) {
            return false;
        }
        return !build.isPath() ? lookup.isAttribute(str) : lookup.isAttribute(build.getLast());
    }

    private boolean isElement(String str) throws Exception {
        Expression build = this.builder.build(str);
        Model lookup = lookup(build);
        if (lookup == null) {
            return false;
        }
        String last = build.getLast();
        int index = build.getIndex();
        if (!lookup.isElement(last)) {
            if (!lookup.isModel(last)) {
                return false;
            }
            if (lookup.lookup(last, index).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmpty() {
        return this.text != null ? false : this.root.isEmpty();
    }

    private Model lookup(Expression expression) throws Exception {
        return expression.isPath() ? this.root.lookup(expression.getPath(0, 1)) : this.root;
    }

    private void process(Contact contact, Annotation annotation, LabelMap labelMap) throws Exception {
        Label label = this.support.getLabel(contact, annotation);
        String path = label.getPath();
        String name = label.getName();
        if (labelMap.get(path) != null) {
            throw new PersistenceException("Duplicate annotation of name '%s' on %s", name, contact);
        } else {
            process(contact, label, labelMap);
        }
    }

    private void process(Contact contact, Label label, LabelMap labelMap) throws Exception {
        Expression expression = label.getExpression();
        String path = label.getPath();
        Model model = this.root;
        if (!expression.isEmpty()) {
            model = register(expression);
        }
        this.resolver.register(label);
        model.register(label);
        labelMap.put(path, label);
    }

    private Model register(Expression expression) throws Exception {
        Model lookup = this.root.lookup(expression);
        return lookup != null ? lookup : create(expression);
    }

    private void text(Contact contact, Annotation annotation) throws Exception {
        Label label = this.support.getLabel(contact, annotation);
        Expression expression = label.getExpression();
        String path = label.getPath();
        Model model = this.root;
        if (!expression.isEmpty()) {
            model = register(expression);
        }
        if (this.texts.get(path) != null) {
            throw new TextException("Multiple text annotations in %s", annotation);
        }
        this.resolver.register(label);
        model.register(label);
        this.texts.put(path, label);
    }

    private void union(Contact contact, Annotation annotation, LabelMap labelMap) throws Exception {
        for (Label label : this.support.getLabels(contact, annotation)) {
            String path = label.getPath();
            String name = label.getName();
            if (labelMap.get(path) != null) {
                throw new PersistenceException("Duplicate annotation of name '%s' on %s", name, label);
            }
            process(contact, label, labelMap);
        }
    }

    private void validateAttributes(Class cls, Order order) throws Exception {
        if (order != null) {
            String[] attributes = order.attributes();
            int length = attributes.length;
            int i = 0;
            while (i < length) {
                if (isAttribute(attributes[i])) {
                    i++;
                } else {
                    throw new AttributeException("Ordered attribute '%s' missing in %s", attributes[i], cls);
                }
            }
        }
    }

    private void validateElements(Class cls, Order order) throws Exception {
        if (order != null) {
            String[] elements = order.elements();
            int length = elements.length;
            int i = 0;
            while (i < length) {
                if (isElement(elements[i])) {
                    i++;
                } else {
                    throw new ElementException("Ordered element '%s' missing for %s", elements[i], cls);
                }
            }
        }
    }

    private void validateModel(Class cls) throws Exception {
        if (!this.root.isEmpty()) {
            this.root.validate(cls);
        }
    }

    private void validateText(Class cls) throws Exception {
        Label text = this.root.getText();
        if (text != null) {
            if (!text.isTextList()) {
                if (!this.elements.isEmpty()) {
                    throw new TextException("Elements used with %s in %s", text, cls);
                } else if (this.root.isComposite()) {
                    throw new TextException("Paths used with %s in %s", text, cls);
                }
            }
        } else if (this.scanner.isEmpty()) {
            this.primitive = isEmpty();
        }
    }

    private void validateTextList(Class cls) throws Exception {
        Label text = this.root.getText();
        if (text != null && text.isTextList()) {
            Object key = text.getKey();
            Iterator it = this.elements.iterator();
            while (it.hasNext()) {
                Label label = (Label) it.next();
                if (label.getKey().equals(key)) {
                    if (label.getDependent().getType() == String.class) {
                        throw new TextException("Illegal entry of %s with text annotations on %s in %s", label.getDependent().getType(), text, cls);
                    }
                }
                throw new TextException("Elements used with %s in %s", text, cls);
            }
            if (this.root.isComposite()) {
                throw new TextException("Paths used with %s in %s", text, cls);
            }
        }
    }

    private void validateUnions(Class cls) throws Exception {
        Iterator it = this.elements.iterator();
        while (it.hasNext()) {
            Label label = (Label) it.next();
            String[] paths = label.getPaths();
            Contact contact = label.getContact();
            int length = paths.length;
            int i = 0;
            while (i < length) {
                Object obj = paths[i];
                Annotation annotation = contact.getAnnotation();
                Label label2 = (Label) this.elements.get(obj);
                if (label.isInline() != label2.isInline()) {
                    throw new UnionException("Inline must be consistent in %s for %s", annotation, contact);
                } else if (label.isRequired() != label2.isRequired()) {
                    throw new UnionException("Required must be consistent in %s for %s", annotation, contact);
                } else {
                    i++;
                }
            }
        }
    }

    private void version(Contact contact, Annotation annotation) throws Exception {
        Label label = this.support.getLabel(contact, annotation);
        if (this.version != null) {
            throw new AttributeException("Multiple version annotations in %s", annotation);
        } else {
            this.version = label;
        }
    }

    public void assemble(Class cls) throws Exception {
        Order order = this.scanner.getOrder();
        if (order != null) {
            this.assembler.assemble(this.root, order);
        }
    }

    public Structure build(Class cls) throws Exception {
        return new Structure(this.factory, this.root, this.version, this.text, this.primitive);
    }

    public void commit(Class cls) throws Exception {
        if (this.factory == null) {
            this.factory = this.resolver.build();
        }
    }

    public void process(Contact contact, Annotation annotation) throws Exception {
        if (annotation instanceof Attribute) {
            process(contact, annotation, this.attributes);
        }
        if (annotation instanceof ElementUnion) {
            union(contact, annotation, this.elements);
        }
        if (annotation instanceof ElementListUnion) {
            union(contact, annotation, this.elements);
        }
        if (annotation instanceof ElementMapUnion) {
            union(contact, annotation, this.elements);
        }
        if (annotation instanceof ElementList) {
            process(contact, annotation, this.elements);
        }
        if (annotation instanceof ElementArray) {
            process(contact, annotation, this.elements);
        }
        if (annotation instanceof ElementMap) {
            process(contact, annotation, this.elements);
        }
        if (annotation instanceof Element) {
            process(contact, annotation, this.elements);
        }
        if (annotation instanceof Version) {
            version(contact, annotation);
        }
        if (annotation instanceof Text) {
            text(contact, annotation);
        }
    }

    public void validate(Class cls) throws Exception {
        Order order = this.scanner.getOrder();
        validateUnions(cls);
        validateElements(cls, order);
        validateAttributes(cls, order);
        validateModel(cls);
        validateText(cls);
        validateTextList(cls);
    }
}
