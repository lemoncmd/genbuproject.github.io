package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class InstantiatorBuilder {
    private LabelMap attributes = new LabelMap();
    private Comparer comparer = new Comparer();
    private Detail detail;
    private LabelMap elements = new LabelMap();
    private Instantiator factory;
    private List<Creator> options = new ArrayList();
    private Scanner scanner;
    private LabelMap texts = new LabelMap();

    public InstantiatorBuilder(Scanner scanner, Detail detail) {
        this.scanner = scanner;
        this.detail = detail;
    }

    private Instantiator build(Detail detail) throws Exception {
        if (this.factory == null) {
            this.factory = create(detail);
        }
        return this.factory;
    }

    private boolean contains(String[] strArr, String str) throws Exception {
        for (String str2 : strArr) {
            if (str2 == str || str2.equals(str)) {
                return true;
            }
        }
        return false;
    }

    private Creator create(Signature signature) {
        Creator signatureCreator = new SignatureCreator(signature);
        if (signature != null) {
            this.options.add(signatureCreator);
        }
        return signatureCreator;
    }

    private Instantiator create(Detail detail) throws Exception {
        Signature signature = this.scanner.getSignature();
        ParameterMap parameters = this.scanner.getParameters();
        Creator creator = null;
        if (signature != null) {
            creator = new SignatureCreator(signature);
        }
        return new ClassInstantiator(this.options, creator, parameters, detail);
    }

    private Parameter create(Parameter parameter) throws Exception {
        Label resolve = resolve(parameter);
        return resolve != null ? new CacheParameter(parameter, resolve) : null;
    }

    private void populate(Detail detail) throws Exception {
        for (Signature populate : this.scanner.getSignatures()) {
            populate(populate);
        }
    }

    private void populate(Signature signature) throws Exception {
        Signature signature2 = new Signature(signature);
        Iterator it = signature.iterator();
        while (it.hasNext()) {
            Parameter create = create((Parameter) it.next());
            if (create != null) {
                signature2.add(create);
            }
        }
        create(signature2);
    }

    private void register(Label label, LabelMap labelMap) throws Exception {
        String name = label.getName();
        String path = label.getPath();
        if (!labelMap.containsKey(name)) {
            labelMap.put(name, label);
        } else if (!((Label) labelMap.get(name)).getPath().equals(name)) {
            labelMap.remove(name);
        }
        labelMap.put(path, label);
    }

    private Label resolve(Parameter parameter) throws Exception {
        return parameter.isAttribute() ? resolve(parameter, this.attributes) : parameter.isText() ? resolve(parameter, this.texts) : resolve(parameter, this.elements);
    }

    private Label resolve(Parameter parameter, LabelMap labelMap) throws Exception {
        Label label = (Label) labelMap.get(parameter.getPath());
        return label == null ? (Label) labelMap.get(parameter.getName()) : label;
    }

    private void validate(Detail detail) throws Exception {
        for (Parameter parameter : this.scanner.getParameters().getAll()) {
            Label resolve = resolve(parameter);
            String path = parameter.getPath();
            if (resolve == null) {
                throw new ConstructorException("Parameter '%s' does not have a match in %s", path, detail);
            }
            validateParameter(resolve, parameter);
        }
        validateConstructors();
    }

    private void validateAnnotations(Label label, Parameter parameter) throws Exception {
        Annotation annotation = label.getAnnotation();
        Annotation annotation2 = parameter.getAnnotation();
        String name = parameter.getName();
        if (!this.comparer.equals(annotation, annotation2)) {
            if (!annotation.annotationType().equals(annotation2.annotationType())) {
                throw new ConstructorException("Annotation %s does not match %s for '%s' in %s", annotation2.annotationType(), annotation.annotationType(), name, parameter);
            }
        }
    }

    private void validateConstructor(Label label, List<Creator> list) throws Exception {
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Signature signature = ((Creator) it.next()).getSignature();
            Contact contact = label.getContact();
            Object key = label.getKey();
            if (contact.isReadOnly() && signature.get(key) == null) {
                it.remove();
            }
        }
    }

    private void validateConstructors() throws Exception {
        List creators = this.factory.getCreators();
        if (this.factory.isDefault()) {
            validateConstructors(this.elements);
            validateConstructors(this.attributes);
        }
        if (!creators.isEmpty()) {
            validateConstructors(this.elements, creators);
            validateConstructors(this.attributes, creators);
        }
    }

    private void validateConstructors(LabelMap labelMap) throws Exception {
        Iterator it = labelMap.iterator();
        while (it.hasNext()) {
            Label label = (Label) it.next();
            if (label != null && label.getContact().isReadOnly()) {
                throw new ConstructorException("Default constructor can not accept read only %s in %s", label, this.detail);
            }
        }
    }

    private void validateConstructors(LabelMap labelMap, List<Creator> list) throws Exception {
        Iterator it = labelMap.iterator();
        while (it.hasNext()) {
            Label label = (Label) it.next();
            if (label != null) {
                validateConstructor(label, list);
            }
        }
        if (list.isEmpty()) {
            throw new ConstructorException("No constructor accepts all read only values in %s", this.detail);
        }
    }

    private void validateNames(Label label, Parameter parameter) throws Exception {
        String[] names = label.getNames();
        String name = parameter.getName();
        if (!contains(names, name)) {
            String name2 = label.getName();
            if (name == name2) {
                return;
            }
            if (name == null || name2 == null) {
                throw new ConstructorException("Annotation does not match %s for '%s' in %s", label, name, parameter);
            } else if (!name.equals(name2)) {
                throw new ConstructorException("Annotation does not match %s for '%s' in %s", label, name, parameter);
            }
        }
    }

    private void validateParameter(Label label, Parameter parameter) throws Exception {
        Contact contact = label.getContact();
        String name = parameter.getName();
        if (Support.isAssignable(parameter.getType(), contact.getType())) {
            validateNames(label, parameter);
            validateAnnotations(label, parameter);
            return;
        }
        throw new ConstructorException("Type is not compatible with %s for '%s' in %s", label, name, parameter);
    }

    public Instantiator build() throws Exception {
        if (this.factory == null) {
            populate(this.detail);
            build(this.detail);
            validate(this.detail);
        }
        return this.factory;
    }

    public void register(Label label) throws Exception {
        if (label.isAttribute()) {
            register(label, this.attributes);
        } else if (label.isText()) {
            register(label, this.texts);
        } else {
            register(label, this.elements);
        }
    }
}
