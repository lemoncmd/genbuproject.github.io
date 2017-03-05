package org.simpleframework.xml.transform;

class ClassTransform implements Transform<Class> {
    private static final String BOOLEAN = "boolean";
    private static final String BYTE = "byte";
    private static final String CHARACTER = "char";
    private static final String DOUBLE = "double";
    private static final String FLOAT = "float";
    private static final String INTEGER = "int";
    private static final String LONG = "long";
    private static final String SHORT = "short";
    private static final String VOID = "void";

    ClassTransform() {
    }

    private ClassLoader getCallerClassLoader() {
        return getClass().getClassLoader();
    }

    private static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private Class readPrimitive(String str) throws Exception {
        return str.equals(BYTE) ? Byte.TYPE : str.equals(SHORT) ? Short.TYPE : str.equals(INTEGER) ? Integer.TYPE : str.equals(LONG) ? Long.TYPE : str.equals(CHARACTER) ? Character.TYPE : str.equals(FLOAT) ? Float.TYPE : str.equals(DOUBLE) ? Double.TYPE : str.equals(BOOLEAN) ? Boolean.TYPE : str.equals(VOID) ? Void.TYPE : null;
    }

    public Class read(String str) throws Exception {
        Class readPrimitive = readPrimitive(str);
        if (readPrimitive != null) {
            return readPrimitive;
        }
        ClassLoader classLoader = getClassLoader();
        if (classLoader == null) {
            classLoader = getCallerClassLoader();
        }
        return classLoader.loadClass(str);
    }

    public String write(Class cls) throws Exception {
        return cls.getName();
    }
}
