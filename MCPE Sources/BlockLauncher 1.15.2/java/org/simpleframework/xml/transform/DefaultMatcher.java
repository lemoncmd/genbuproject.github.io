package org.simpleframework.xml.transform;

class DefaultMatcher implements Matcher {
    private Matcher array = new ArrayMatcher(this);
    private Matcher matcher;
    private Matcher primitive = new PrimitiveMatcher();
    private Matcher stock = new PackageMatcher();

    public DefaultMatcher(Matcher matcher) {
        this.matcher = matcher;
    }

    private Transform matchType(Class cls) throws Exception {
        return cls.isArray() ? this.array.match(cls) : cls.isPrimitive() ? this.primitive.match(cls) : this.stock.match(cls);
    }

    public Transform match(Class cls) throws Exception {
        Transform match = this.matcher.match(cls);
        return match != null ? match : matchType(cls);
    }
}
