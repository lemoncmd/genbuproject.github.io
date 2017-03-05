package org.simpleframework.xml.stream;

class IdentityStyle implements Style {
    IdentityStyle() {
    }

    public String getAttribute(String str) {
        return str;
    }

    public String getElement(String str) {
        return str;
    }
}
