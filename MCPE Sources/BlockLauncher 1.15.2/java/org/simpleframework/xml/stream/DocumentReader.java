package org.simpleframework.xml.stream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class DocumentReader implements EventReader {
    private static final String RESERVED = "xml";
    private EventNode peek;
    private NodeExtractor queue;
    private NodeStack stack = new NodeStack();

    private static class End extends EventToken {
        private End() {
        }

        public boolean isEnd() {
            return true;
        }
    }

    private static class Entry extends EventAttribute {
        private final Node node;

        public Entry(Node node) {
            this.node = node;
        }

        public String getName() {
            return this.node.getLocalName();
        }

        public String getPrefix() {
            return this.node.getPrefix();
        }

        public String getReference() {
            return this.node.getNamespaceURI();
        }

        public Object getSource() {
            return this.node;
        }

        public String getValue() {
            return this.node.getNodeValue();
        }

        public boolean isReserved() {
            String prefix = getPrefix();
            return prefix != null ? prefix.startsWith(DocumentReader.RESERVED) : getName().startsWith(DocumentReader.RESERVED);
        }
    }

    private static class Start extends EventElement {
        private final Element element;

        public Start(Node node) {
            this.element = (Element) node;
        }

        public NamedNodeMap getAttributes() {
            return this.element.getAttributes();
        }

        public String getName() {
            return this.element.getLocalName();
        }

        public String getPrefix() {
            return this.element.getPrefix();
        }

        public String getReference() {
            return this.element.getNamespaceURI();
        }

        public Object getSource() {
            return this.element;
        }
    }

    private static class Text extends EventToken {
        private final Node node;

        public Text(Node node) {
            this.node = node;
        }

        public Object getSource() {
            return this.node;
        }

        public String getValue() {
            return this.node.getNodeValue();
        }

        public boolean isText() {
            return true;
        }
    }

    public DocumentReader(Document document) {
        this.queue = new NodeExtractor(document);
        this.stack.push(document);
    }

    private Entry attribute(Node node) {
        return new Entry(node);
    }

    private Start build(Start start) {
        NamedNodeMap attributes = start.getAttributes();
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            Attribute attribute = attribute(attributes.item(i));
            if (!attribute.isReserved()) {
                start.add(attribute);
            }
        }
        return start;
    }

    private EventNode convert(Node node) throws Exception {
        if (node.getNodeType() != (short) 1) {
            return text(node);
        }
        if (node != null) {
            this.stack.push(node);
        }
        return start(node);
    }

    private End end() {
        return new End();
    }

    private EventNode read() throws Exception {
        Node node = (Node) this.queue.peek();
        return node == null ? end() : read(node);
    }

    private EventNode read(Node node) throws Exception {
        Node node2 = (Node) this.stack.top();
        if (node.getParentNode() != node2) {
            if (node2 != null) {
                this.stack.pop();
            }
            return end();
        }
        if (node != null) {
            this.queue.poll();
        }
        return convert(node);
    }

    private Start start(Node node) {
        Start start = new Start(node);
        return start.isEmpty() ? build(start) : start;
    }

    private Text text(Node node) {
        return new Text(node);
    }

    public EventNode next() throws Exception {
        EventNode eventNode = this.peek;
        if (eventNode == null) {
            return read();
        }
        this.peek = null;
        return eventNode;
    }

    public EventNode peek() throws Exception {
        if (this.peek == null) {
            this.peek = next();
        }
        return this.peek;
    }
}
