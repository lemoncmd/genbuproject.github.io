package org.simpleframework.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class Comparer {
    private static final String NAME = "name";
    private final String[] ignore;

    public Comparer() {
        this(NAME);
    }

    public Comparer(String... strArr) {
        this.ignore = strArr;
    }

    private boolean isIgnore(Method method) {
        String name = method.getName();
        if (this.ignore == null) {
            return false;
        }
        for (Object equals : this.ignore) {
            if (name.equals(equals)) {
                return true;
            }
        }
        return false;
    }

    public boolean equals(Annotation annotation, Annotation annotation2) throws Exception {
        Class annotationType = annotation.annotationType();
        Class annotationType2 = annotation2.annotationType();
        Method[] declaredMethods = annotationType.getDeclaredMethods();
        if (!annotationType.equals(annotationType2)) {
            return false;
        }
        for (Method method : declaredMethods) {
            if (!isIgnore(method) && !method.invoke(annotation, new Object[0]).equals(method.invoke(annotation2, new Object[0]))) {
                return false;
            }
        }
        return true;
    }
}
