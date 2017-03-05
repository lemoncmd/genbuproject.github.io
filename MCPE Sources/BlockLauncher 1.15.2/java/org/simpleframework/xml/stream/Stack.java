package org.simpleframework.xml.stream;

import java.util.ArrayList;

class Stack<T> extends ArrayList<T> {
    public Stack(int i) {
        super(i);
    }

    public T bottom() {
        return size() <= 0 ? null : get(0);
    }

    public T pop() {
        int size = size();
        return size <= 0 ? null : remove(size - 1);
    }

    public T push(T t) {
        add(t);
        return t;
    }

    public T top() {
        int size = size();
        return size <= 0 ? null : get(size - 1);
    }
}
