package org.simpleframework.xml.stream;

class InputAttribute implements InputNode {
    private String name;
    private InputNode parent;
    private String prefix;
    private String reference;
    private Object source;
    private String value;

    public InputAttribute(InputNode inputNode, String str, String str2) {
        this.parent = inputNode;
        this.value = str2;
        this.name = str;
    }

    public InputAttribute(InputNode inputNode, Attribute attribute) {
        this.reference = attribute.getReference();
        this.prefix = attribute.getPrefix();
        this.source = attribute.getSource();
        this.value = attribute.getValue();
        this.name = attribute.getName();
        this.parent = inputNode;
    }

    public InputNode getAttribute(String str) {
        return null;
    }

    public NodeMap<InputNode> getAttributes() {
        return new InputNodeMap(this);
    }

    public String getName() {
        return this.name;
    }

    public InputNode getNext() {
        return null;
    }

    public InputNode getNext(String str) {
        return null;
    }

    public InputNode getParent() {
        return this.parent;
    }

    public Position getPosition() {
        return this.parent.getPosition();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getReference() {
        return this.reference;
    }

    public Object getSource() {
        return this.source;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isElement() {
        return false;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean isRoot() {
        return false;
    }

    public void skip() {
    }

    public String toString() {
        return String.format("attribute %s='%s'", new Object[]{this.name, this.value});
    }
}
