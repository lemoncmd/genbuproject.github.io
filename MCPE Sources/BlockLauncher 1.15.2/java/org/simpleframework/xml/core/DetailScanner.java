package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import org.simpleframework.xml.Default;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

class DetailScanner implements Detail {
    private DefaultType access;
    private NamespaceList declaration;
    private List<FieldDetail> fields;
    private Annotation[] labels;
    private List<MethodDetail> methods;
    private String name;
    private Namespace namespace;
    private Order order;
    private DefaultType override;
    private boolean required;
    private Root root;
    private boolean strict;
    private Class type;

    public DetailScanner(Class cls) {
        this(cls, null);
    }

    public DetailScanner(Class cls, DefaultType defaultType) {
        this.methods = new LinkedList();
        this.fields = new LinkedList();
        this.labels = cls.getDeclaredAnnotations();
        this.override = defaultType;
        this.strict = true;
        this.type = cls;
        scan(cls);
    }

    private void access(Annotation annotation) {
        if (annotation != null) {
            Default defaultR = (Default) annotation;
            this.required = defaultR.required();
            this.access = defaultR.value();
        }
    }

    private void extract(Class cls) {
        for (Annotation annotation : this.labels) {
            if (annotation instanceof Namespace) {
                namespace(annotation);
            }
            if (annotation instanceof NamespaceList) {
                scope(annotation);
            }
            if (annotation instanceof Root) {
                root(annotation);
            }
            if (annotation instanceof Order) {
                order(annotation);
            }
            if (annotation instanceof Default) {
                access(annotation);
            }
        }
    }

    private void fields(Class cls) {
        for (Field fieldDetail : cls.getDeclaredFields()) {
            this.fields.add(new FieldDetail(fieldDetail));
        }
    }

    private boolean isEmpty(String str) {
        return str.length() == 0;
    }

    private void methods(Class cls) {
        for (Method methodDetail : cls.getDeclaredMethods()) {
            this.methods.add(new MethodDetail(methodDetail));
        }
    }

    private void namespace(Annotation annotation) {
        if (annotation != null) {
            this.namespace = (Namespace) annotation;
        }
    }

    private void order(Annotation annotation) {
        if (annotation != null) {
            this.order = (Order) annotation;
        }
    }

    private void root(Annotation annotation) {
        if (annotation != null) {
            Root root = (Root) annotation;
            String simpleName = this.type.getSimpleName();
            if (root != null) {
                String name = root.name();
                if (isEmpty(name)) {
                    name = Reflector.getName(simpleName);
                }
                this.strict = root.strict();
                this.root = root;
                this.name = name;
            }
        }
    }

    private void scan(Class cls) {
        methods(cls);
        fields(cls);
        extract(cls);
    }

    private void scope(Annotation annotation) {
        if (annotation != null) {
            this.declaration = (NamespaceList) annotation;
        }
    }

    public DefaultType getAccess() {
        return this.override != null ? this.override : this.access;
    }

    public Annotation[] getAnnotations() {
        return this.labels;
    }

    public Constructor[] getConstructors() {
        return this.type.getDeclaredConstructors();
    }

    public List<FieldDetail> getFields() {
        return this.fields;
    }

    public List<MethodDetail> getMethods() {
        return this.methods;
    }

    public String getName() {
        return this.name;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public NamespaceList getNamespaceList() {
        return this.declaration;
    }

    public Order getOrder() {
        return this.order;
    }

    public DefaultType getOverride() {
        return this.override;
    }

    public Root getRoot() {
        return this.root;
    }

    public Class getSuper() {
        Class superclass = this.type.getSuperclass();
        return superclass == Object.class ? null : superclass;
    }

    public Class getType() {
        return this.type;
    }

    public boolean isInstantiable() {
        return Modifier.isStatic(this.type.getModifiers()) || !this.type.isMemberClass();
    }

    public boolean isPrimitive() {
        return this.type.isPrimitive();
    }

    public boolean isRequired() {
        return this.required;
    }

    public boolean isStrict() {
        return this.strict;
    }

    public String toString() {
        return this.type.toString();
    }
}
