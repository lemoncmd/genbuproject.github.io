package org.simpleframework.xml.transform;

class EmptyMatcher implements Matcher {
    EmptyMatcher() {
    }

    public Transform match(Class cls) throws Exception {
        return null;
    }
}
