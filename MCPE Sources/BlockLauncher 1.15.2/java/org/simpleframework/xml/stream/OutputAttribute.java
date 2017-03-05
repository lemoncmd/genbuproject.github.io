package org.simpleframework.xml.stream;

class OutputAttribute implements OutputNode {
    private String name;
    private String reference;
    private NamespaceMap scope;
    private OutputNode source;
    private String value;

    public OutputAttribute(OutputNode outputNode, String str, String str2) {
        this.scope = outputNode.getNamespaces();
        this.source = outputNode;
        this.value = str2;
        this.name = str;
    }

    public void commit() {
    }

    public NodeMap<OutputNode> getAttributes() {
        return new OutputNodeMap(this);
    }

    public OutputNode getChild(String str) {
        return null;
    }

    public String getComment() {
        return null;
    }

    public Mode getMode() {
        return Mode.INHERIT;
    }

    public String getName() {
        return this.name;
    }

    public NamespaceMap getNamespaces() {
        return this.scope;
    }

    public OutputNode getParent() {
        return this.source;
    }

    public String getPrefix() {
        return this.scope.getPrefix(this.reference);
    }

    public String getPrefix(boolean z) {
        return this.scope.getPrefix(this.reference);
    }

    public String getReference() {
        return this.reference;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isCommitted() {
        return true;
    }

    public boolean isRoot() {
        return false;
    }

    public void remove() {
    }

    public OutputNode setAttribute(String str, String str2) {
        return null;
    }

    public void setComment(String str) {
    }

    public void setData(boolean z) {
    }

    public void setMode(Mode mode) {
    }

    public void setName(String str) {
        this.name = str;
    }

    public void setReference(String str) {
        this.reference = str;
    }

    public void setValue(String str) {
        this.value = str;
    }

    public String toString() {
        return String.format("attribute %s='%s'", new Object[]{this.name, this.value});
    }
}
