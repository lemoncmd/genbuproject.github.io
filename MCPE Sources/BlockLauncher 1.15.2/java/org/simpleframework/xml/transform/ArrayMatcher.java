package org.simpleframework.xml.transform;

class ArrayMatcher implements Matcher {
    private final Matcher primary;

    public ArrayMatcher(Matcher matcher) {
        this.primary = matcher;
    }

    private Transform matchArray(Class cls) throws Exception {
        Transform match = this.primary.match(cls);
        return match == null ? null : new ArrayTransform(match, cls);
    }

    public Transform match(Class cls) throws Exception {
        Class componentType = cls.getComponentType();
        return componentType == Character.TYPE ? new CharacterArrayTransform(componentType) : componentType == Character.class ? new CharacterArrayTransform(componentType) : componentType == String.class ? new StringArrayTransform() : matchArray(componentType);
    }
}
