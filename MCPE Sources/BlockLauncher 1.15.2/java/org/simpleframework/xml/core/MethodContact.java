package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;

class MethodContact implements Contact {
    private MethodPart get;
    private Class item;
    private Class[] items;
    private Annotation label;
    private String name;
    private Class owner;
    private MethodPart set;
    private Class type;

    public MethodContact(MethodPart methodPart) {
        this(methodPart, null);
    }

    public MethodContact(MethodPart methodPart, MethodPart methodPart2) {
        this.owner = methodPart.getDeclaringClass();
        this.label = methodPart.getAnnotation();
        this.items = methodPart.getDependents();
        this.item = methodPart.getDependent();
        this.type = methodPart.getType();
        this.name = methodPart.getName();
        this.set = methodPart2;
        this.get = methodPart;
    }

    public Object get(Object obj) throws Exception {
        return this.get.getMethod().invoke(obj, new Object[0]);
    }

    public Annotation getAnnotation() {
        return this.label;
    }

    public <T extends Annotation> T getAnnotation(Class<T> cls) {
        T annotation = this.get.getAnnotation(cls);
        return cls == this.label.annotationType() ? this.label : (annotation != null || this.set == null) ? annotation : this.set.getAnnotation(cls);
    }

    public Class getDeclaringClass() {
        return this.owner;
    }

    public Class getDependent() {
        return this.item;
    }

    public Class[] getDependents() {
        return this.items;
    }

    public String getName() {
        return this.name;
    }

    public MethodPart getRead() {
        return this.get;
    }

    public Class getType() {
        return this.type;
    }

    public MethodPart getWrite() {
        return this.set;
    }

    public boolean isReadOnly() {
        return this.set == null;
    }

    public void set(Object obj, Object obj2) throws Exception {
        Class declaringClass = this.get.getMethod().getDeclaringClass();
        if (this.set == null) {
            throw new MethodException("Property '%s' is read only in %s", this.name, declaringClass);
        }
        this.set.getMethod().invoke(obj, new Object[]{obj2});
    }

    public String toString() {
        return String.format("method '%s'", new Object[]{this.name});
    }
}
