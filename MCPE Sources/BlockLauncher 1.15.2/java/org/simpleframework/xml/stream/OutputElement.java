package org.simpleframework.xml.stream;

class OutputElement implements OutputNode {
    private String comment;
    private Mode mode = Mode.INHERIT;
    private String name;
    private OutputNode parent;
    private String reference;
    private NamespaceMap scope;
    private OutputNodeMap table = new OutputNodeMap(this);
    private String value;
    private NodeWriter writer;

    public OutputElement(OutputNode outputNode, NodeWriter nodeWriter, String str) {
        this.scope = new PrefixResolver(outputNode);
        this.writer = nodeWriter;
        this.parent = outputNode;
        this.name = str;
    }

    public void commit() throws Exception {
        this.writer.commit(this);
    }

    public OutputNodeMap getAttributes() {
        return this.table;
    }

    public OutputNode getChild(String str) throws Exception {
        return this.writer.writeElement(this, str);
    }

    public String getComment() {
        return this.comment;
    }

    public Mode getMode() {
        return this.mode;
    }

    public String getName() {
        return this.name;
    }

    public NamespaceMap getNamespaces() {
        return this.scope;
    }

    public OutputNode getParent() {
        return this.parent;
    }

    public String getPrefix() {
        return getPrefix(true);
    }

    public String getPrefix(boolean z) {
        String prefix = this.scope.getPrefix(this.reference);
        return (z && prefix == null) ? this.parent.getPrefix() : prefix;
    }

    public String getReference() {
        return this.reference;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isCommitted() {
        return this.writer.isCommitted(this);
    }

    public boolean isRoot() {
        return this.writer.isRoot(this);
    }

    public void remove() throws Exception {
        this.writer.remove(this);
    }

    public OutputNode setAttribute(String str, String str2) {
        return this.table.put(str, str2);
    }

    public void setComment(String str) {
        this.comment = str;
    }

    public void setData(boolean z) {
        if (z) {
            this.mode = Mode.DATA;
        } else {
            this.mode = Mode.ESCAPE;
        }
    }

    public void setMode(Mode mode) {
        this.mode = mode;
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
        return String.format("element %s", new Object[]{this.name});
    }
}
