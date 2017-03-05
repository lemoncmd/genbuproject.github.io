package org.simpleframework.xml.stream;

import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class NodeExtractor extends LinkedList<Node> {
    public NodeExtractor(Document document) {
        extract(document);
    }

    private void extract(Document document) {
        Node documentElement = document.getDocumentElement();
        if (documentElement != null) {
            offer(documentElement);
            extract(documentElement);
        }
    }

    private void extract(Node node) {
        NodeList childNodes = node.getChildNodes();
        int length = childNodes.getLength();
        for (int i = 0; i < length; i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != (short) 8) {
                offer(item);
                extract(item);
            }
        }
    }
}
