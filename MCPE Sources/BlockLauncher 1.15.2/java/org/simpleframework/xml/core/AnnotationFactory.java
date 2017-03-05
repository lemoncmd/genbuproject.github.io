package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.stream.Verbosity;

class AnnotationFactory {
    private final Format format;
    private final boolean required;

    public AnnotationFactory(Detail detail, Support support) {
        this.required = detail.isRequired();
        this.format = support.getFormat();
    }

    private ClassLoader getClassLoader() throws Exception {
        return AnnotationFactory.class.getClassLoader();
    }

    private Annotation getInstance(Class cls) throws Exception {
        ClassLoader classLoader = getClassLoader();
        return cls.isArray() ? isPrimitive(cls.getComponentType()) ? getInstance(classLoader, Element.class) : getInstance(classLoader, ElementArray.class) : (isPrimitive(cls) && isAttribute()) ? getInstance(classLoader, Attribute.class) : getInstance(classLoader, Element.class);
    }

    private Annotation getInstance(ClassLoader classLoader, Class cls) throws Exception {
        return getInstance(classLoader, cls, false);
    }

    private Annotation getInstance(ClassLoader classLoader, Class cls, boolean z) throws Exception {
        return (Annotation) Proxy.newProxyInstance(classLoader, new Class[]{cls}, new AnnotationHandler(cls, this.required, z));
    }

    private boolean isAttribute() {
        Verbosity verbosity = this.format.getVerbosity();
        return verbosity != null && verbosity == Verbosity.LOW;
    }

    private boolean isPrimitive(Class cls) {
        return (Number.class.isAssignableFrom(cls) || cls == Boolean.class || cls == Character.class) ? true : cls.isPrimitive();
    }

    private boolean isPrimitiveKey(Class[] clsArr) {
        if (clsArr == null || clsArr.length <= 0) {
            return false;
        }
        Class superclass = clsArr[0].getSuperclass();
        Class cls = clsArr[0];
        return (superclass == null || !(superclass.isEnum() || cls.isEnum())) ? isPrimitive(cls) : true;
    }

    public Annotation getInstance(Class cls, Class[] clsArr) throws Exception {
        ClassLoader classLoader = getClassLoader();
        return Map.class.isAssignableFrom(cls) ? (isPrimitiveKey(clsArr) && isAttribute()) ? getInstance(classLoader, ElementMap.class, true) : getInstance(classLoader, ElementMap.class) : Collection.class.isAssignableFrom(cls) ? getInstance(classLoader, ElementList.class) : getInstance(cls);
    }
}
