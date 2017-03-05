package org.simpleframework.xml.stream;

class NodeReader {
    private final EventReader reader;
    private final InputStack stack = new InputStack();
    private final StringBuilder text = new StringBuilder();

    public NodeReader(EventReader eventReader) {
        this.reader = eventReader;
    }

    private void fillText(InputNode inputNode) throws Exception {
        EventNode peek = this.reader.peek();
        if (peek.isText()) {
            this.text.append(peek.getValue());
        }
    }

    private boolean isName(EventNode eventNode, String str) {
        String name = eventNode.getName();
        return name == null ? false : name.equals(str);
    }

    private String readBuffer(InputNode inputNode) throws Exception {
        if (this.text.length() <= 0) {
            return null;
        }
        String stringBuilder = this.text.toString();
        this.text.setLength(0);
        return stringBuilder;
    }

    private InputNode readStart(InputNode inputNode, EventNode eventNode) throws Exception {
        InputNode inputElement = new InputElement(inputNode, this, eventNode);
        if (this.text.length() > 0) {
            this.text.setLength(0);
        }
        return eventNode.isStart() ? (InputNode) this.stack.push(inputElement) : inputElement;
    }

    private String readText(InputNode inputNode) throws Exception {
        EventNode peek = this.reader.peek();
        while (this.stack.top() == inputNode && r0.isText()) {
            fillText(inputNode);
            this.reader.next();
            peek = this.reader.peek();
        }
        return readBuffer(inputNode);
    }

    public boolean isEmpty(InputNode inputNode) throws Exception {
        return this.stack.top() == inputNode && this.reader.peek().isEnd();
    }

    public boolean isRoot(InputNode inputNode) {
        return this.stack.bottom() == inputNode;
    }

    public InputNode readElement(InputNode inputNode) throws Exception {
        if (!this.stack.isRelevant(inputNode)) {
            return null;
        }
        EventNode next = this.reader.next();
        while (next != null) {
            if (next.isEnd()) {
                if (this.stack.pop() == inputNode) {
                    return null;
                }
            } else if (next.isStart()) {
                return readStart(inputNode, next);
            }
            next = this.reader.next();
        }
        return null;
    }

    public InputNode readElement(InputNode inputNode, String str) throws Exception {
        if (!this.stack.isRelevant(inputNode)) {
            return null;
        }
        EventNode peek = this.reader.peek();
        while (peek != null) {
            if (peek.isText()) {
                fillText(inputNode);
            } else if (peek.isEnd()) {
                if (this.stack.top() == inputNode) {
                    return null;
                }
                this.stack.pop();
            } else if (peek.isStart()) {
                return isName(peek, str) ? readElement(inputNode) : null;
            }
            this.reader.next();
            peek = this.reader.peek();
        }
        return null;
    }

    public InputNode readRoot() throws Exception {
        InputNode inputNode = null;
        if (this.stack.isEmpty()) {
            inputNode = readElement(null);
            if (inputNode == null) {
                throw new NodeException("Document has no root element");
            }
        }
        return inputNode;
    }

    public String readValue(InputNode inputNode) throws Exception {
        if (!this.stack.isRelevant(inputNode)) {
            return null;
        }
        if (this.text.length() <= 0 && this.reader.peek().isEnd()) {
            if (this.stack.top() == inputNode) {
                return null;
            }
            this.stack.pop();
            this.reader.next();
        }
        return readText(inputNode);
    }

    public void skipElement(InputNode inputNode) throws Exception {
        do {
        } while (readElement(inputNode) != null);
    }
}
