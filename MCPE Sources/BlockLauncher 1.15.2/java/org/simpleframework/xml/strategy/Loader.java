package org.simpleframework.xml.strategy;

class Loader {
    Loader() {
    }

    private static ClassLoader getCallerClassLoader() throws Exception {
        return Loader.class.getClassLoader();
    }

    private static ClassLoader getClassLoader() throws Exception {
        return Thread.currentThread().getContextClassLoader();
    }

    public Class load(String str) throws Exception {
        ClassLoader classLoader = getClassLoader();
        if (classLoader == null) {
            classLoader = getCallerClassLoader();
        }
        return classLoader.loadClass(str);
    }
}
