package org.simpleframework.xml.stream;

public class CamelCaseStyle implements Style {
    private final Builder builder;
    private final Style style;

    public CamelCaseStyle() {
        this(true, false);
    }

    public CamelCaseStyle(boolean z) {
        this(z, false);
    }

    public CamelCaseStyle(boolean z, boolean z2) {
        this.style = new CamelCaseBuilder(z, z2);
        this.builder = new Builder(this.style);
    }

    public String getAttribute(String str) {
        return this.builder.getAttribute(str);
    }

    public String getElement(String str) {
        return this.builder.getElement(str);
    }

    public void setAttribute(String str, String str2) {
        this.builder.setAttribute(str, str2);
    }

    public void setElement(String str, String str2) {
        this.builder.setElement(str, str2);
    }
}
