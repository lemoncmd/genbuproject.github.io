package org.simpleframework.xml.core;

abstract class TemplateParameter implements Parameter {
    protected TemplateParameter() {
    }

    public boolean isAttribute() {
        return false;
    }

    public boolean isText() {
        return false;
    }
}
