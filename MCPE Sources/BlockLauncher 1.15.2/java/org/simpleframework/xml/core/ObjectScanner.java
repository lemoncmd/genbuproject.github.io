package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Version;

class ObjectScanner implements Scanner {
    private StructureBuilder builder;
    private Detail detail;
    private ClassScanner scanner;
    private Structure structure;
    private Support support;

    public ObjectScanner(Detail detail, Support support) throws Exception {
        this.scanner = new ClassScanner(detail, support);
        this.builder = new StructureBuilder(this, detail, support);
        this.support = support;
        this.detail = detail;
        scan(detail);
    }

    private void commit(Detail detail) throws Exception {
        Class type = detail.getType();
        if (this.structure == null) {
            this.structure = this.builder.build(type);
        }
        this.builder = null;
    }

    private void field(Detail detail) throws Exception {
        Iterator it = this.support.getFields(detail.getType(), detail.getOverride()).iterator();
        while (it.hasNext()) {
            Contact contact = (Contact) it.next();
            Annotation annotation = contact.getAnnotation();
            if (annotation != null) {
                this.builder.process(contact, annotation);
            }
        }
    }

    private void method(Detail detail) throws Exception {
        Iterator it = this.support.getMethods(detail.getType(), detail.getOverride()).iterator();
        while (it.hasNext()) {
            Contact contact = (Contact) it.next();
            Annotation annotation = contact.getAnnotation();
            if (annotation != null) {
                this.builder.process(contact, annotation);
            }
        }
    }

    private void order(Detail detail) throws Exception {
        this.builder.assemble(detail.getType());
    }

    private void scan(Detail detail) throws Exception {
        order(detail);
        field(detail);
        method(detail);
        validate(detail);
        commit(detail);
    }

    private void validate(Detail detail) throws Exception {
        Class type = detail.getType();
        this.builder.commit(type);
        this.builder.validate(type);
    }

    public Caller getCaller(Context context) {
        return new Caller(this, context);
    }

    public Function getCommit() {
        return this.scanner.getCommit();
    }

    public Function getComplete() {
        return this.scanner.getComplete();
    }

    public Decorator getDecorator() {
        return this.scanner.getDecorator();
    }

    public Instantiator getInstantiator() {
        return this.structure.getInstantiator();
    }

    public String getName() {
        return this.detail.getName();
    }

    public Order getOrder() {
        return this.scanner.getOrder();
    }

    public ParameterMap getParameters() {
        return this.scanner.getParameters();
    }

    public Function getPersist() {
        return this.scanner.getPersist();
    }

    public Function getReplace() {
        return this.scanner.getReplace();
    }

    public Function getResolve() {
        return this.scanner.getResolve();
    }

    public Version getRevision() {
        return this.structure.getRevision();
    }

    public Section getSection() {
        return this.structure.getSection();
    }

    public Signature getSignature() {
        return this.scanner.getSignature();
    }

    public List<Signature> getSignatures() {
        return this.scanner.getSignatures();
    }

    public Label getText() {
        return this.structure.getText();
    }

    public Class getType() {
        return this.detail.getType();
    }

    public Function getValidate() {
        return this.scanner.getValidate();
    }

    public Label getVersion() {
        return this.structure.getVersion();
    }

    public boolean isEmpty() {
        return this.scanner.getRoot() == null;
    }

    public boolean isPrimitive() {
        return this.structure.isPrimitive();
    }

    public boolean isStrict() {
        return this.detail.isStrict();
    }
}
