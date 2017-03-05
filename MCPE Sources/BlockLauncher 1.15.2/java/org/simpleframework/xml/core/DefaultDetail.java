package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;
import org.simpleframework.xml.DefaultType;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

class DefaultDetail implements Detail {
    private final DefaultType access;
    private final Detail detail;

    public DefaultDetail(Detail detail, DefaultType defaultType) {
        this.detail = detail;
        this.access = defaultType;
    }

    public DefaultType getAccess() {
        return this.detail.getAccess();
    }

    public Annotation[] getAnnotations() {
        return this.detail.getAnnotations();
    }

    public Constructor[] getConstructors() {
        return this.detail.getConstructors();
    }

    public List<FieldDetail> getFields() {
        return this.detail.getFields();
    }

    public List<MethodDetail> getMethods() {
        return this.detail.getMethods();
    }

    public String getName() {
        return this.detail.getName();
    }

    public Namespace getNamespace() {
        return this.detail.getNamespace();
    }

    public NamespaceList getNamespaceList() {
        return this.detail.getNamespaceList();
    }

    public Order getOrder() {
        return this.detail.getOrder();
    }

    public DefaultType getOverride() {
        return this.access;
    }

    public Root getRoot() {
        return this.detail.getRoot();
    }

    public Class getSuper() {
        return this.detail.getSuper();
    }

    public Class getType() {
        return this.detail.getType();
    }

    public boolean isInstantiable() {
        return this.detail.isInstantiable();
    }

    public boolean isPrimitive() {
        return this.detail.isPrimitive();
    }

    public boolean isRequired() {
        return this.detail.isRequired();
    }

    public boolean isStrict() {
        return this.detail.isStrict();
    }

    public String toString() {
        return this.detail.toString();
    }
}
