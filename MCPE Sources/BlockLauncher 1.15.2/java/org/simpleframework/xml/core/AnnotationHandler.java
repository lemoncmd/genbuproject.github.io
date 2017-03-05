package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

class AnnotationHandler implements InvocationHandler {
    private static final String ATTRIBUTE = "attribute";
    private static final String CLASS = "annotationType";
    private static final String EQUAL = "equals";
    private static final String REQUIRED = "required";
    private static final String STRING = "toString";
    private final boolean attribute;
    private final Comparer comparer;
    private final boolean required;
    private final Class type;

    public AnnotationHandler(Class cls) {
        this(cls, true);
    }

    public AnnotationHandler(Class cls, boolean z) {
        this(cls, z, false);
    }

    public AnnotationHandler(Class cls, boolean z, boolean z2) {
        this.comparer = new Comparer();
        this.attribute = z2;
        this.required = z;
        this.type = cls;
    }

    private void attributes(StringBuilder stringBuilder) {
        Method[] declaredMethods = this.type.getDeclaredMethods();
        for (int i = 0; i < declaredMethods.length; i++) {
            String name = declaredMethods[i].getName();
            Object value = value(declaredMethods[i]);
            if (i > 0) {
                stringBuilder.append(',');
                stringBuilder.append(' ');
            }
            stringBuilder.append(name);
            stringBuilder.append('=');
            stringBuilder.append(value);
        }
        stringBuilder.append(')');
    }

    private boolean equals(Object obj, Object[] objArr) throws Throwable {
        Annotation annotation = (Annotation) obj;
        Annotation annotation2 = (Annotation) objArr[0];
        if (annotation.annotationType() == annotation2.annotationType()) {
            return this.comparer.equals(annotation, annotation2);
        }
        throw new PersistenceException("Annotation %s is not the same as %s", annotation, annotation2);
    }

    private void name(StringBuilder stringBuilder) {
        String name = this.type.getName();
        if (name != null) {
            stringBuilder.append('@');
            stringBuilder.append(name);
            stringBuilder.append('(');
        }
    }

    private Object value(Method method) {
        String name = method.getName();
        return name.equals(REQUIRED) ? Boolean.valueOf(this.required) : name.equals(ATTRIBUTE) ? Boolean.valueOf(this.attribute) : method.getDefaultValue();
    }

    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        String name = method.getName();
        return name.equals(STRING) ? toString() : name.equals(EQUAL) ? Boolean.valueOf(equals(obj, objArr)) : name.equals(CLASS) ? this.type : name.equals(REQUIRED) ? Boolean.valueOf(this.required) : name.equals(ATTRIBUTE) ? Boolean.valueOf(this.attribute) : method.getDefaultValue();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (this.type != null) {
            name(stringBuilder);
            attributes(stringBuilder);
        }
        return stringBuilder.toString();
    }
}
