package org.simpleframework.xml.stream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

class OutputStack extends ArrayList<OutputNode> {
    private final Set active;

    private class Sequence implements Iterator<OutputNode> {
        private int cursor;

        public Sequence() {
            this.cursor = OutputStack.this.size();
        }

        public boolean hasNext() {
            return this.cursor > 0;
        }

        public OutputNode next() {
            if (!hasNext()) {
                return null;
            }
            OutputStack outputStack = OutputStack.this;
            int i = this.cursor - 1;
            this.cursor = i;
            return (OutputNode) outputStack.get(i);
        }

        public void remove() {
            OutputStack.this.purge(this.cursor);
        }
    }

    public OutputStack(Set set) {
        this.active = set;
    }

    public OutputNode bottom() {
        return size() <= 0 ? null : (OutputNode) get(0);
    }

    public Iterator<OutputNode> iterator() {
        return new Sequence();
    }

    public OutputNode pop() {
        int size = size();
        return size <= 0 ? null : purge(size - 1);
    }

    public OutputNode purge(int i) {
        OutputNode outputNode = (OutputNode) remove(i);
        if (outputNode != null) {
            this.active.remove(outputNode);
        }
        return outputNode;
    }

    public OutputNode push(OutputNode outputNode) {
        this.active.add(outputNode);
        add(outputNode);
        return outputNode;
    }

    public OutputNode top() {
        int size = size();
        return size <= 0 ? null : (OutputNode) get(size - 1);
    }
}
