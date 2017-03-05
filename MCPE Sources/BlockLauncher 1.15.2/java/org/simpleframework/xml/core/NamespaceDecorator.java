package org.simpleframework.xml.core;

import java.util.ArrayList;
import java.util.List;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.stream.NamespaceMap;
import org.simpleframework.xml.stream.OutputNode;

class NamespaceDecorator implements Decorator {
    private Namespace primary;
    private List<Namespace> scope = new ArrayList();

    private void namespace(OutputNode outputNode) {
        if (this.primary != null) {
            outputNode.setReference(this.primary.reference());
        }
    }

    private void scope(OutputNode outputNode) {
        NamespaceMap namespaces = outputNode.getNamespaces();
        for (Namespace namespace : this.scope) {
            namespaces.setReference(namespace.reference(), namespace.prefix());
        }
    }

    public void add(Namespace namespace) {
        this.scope.add(namespace);
    }

    public void decorate(OutputNode outputNode) {
        decorate(outputNode, null);
    }

    public void decorate(OutputNode outputNode, Decorator decorator) {
        if (decorator != null) {
            decorator.decorate(outputNode);
        }
        scope(outputNode);
        namespace(outputNode);
    }

    public void set(Namespace namespace) {
        if (namespace != null) {
            add(namespace);
        }
        this.primary = namespace;
    }
}
