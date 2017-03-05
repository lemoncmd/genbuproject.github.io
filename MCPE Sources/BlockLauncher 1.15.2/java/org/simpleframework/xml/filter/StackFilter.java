package org.simpleframework.xml.filter;

import java.util.Stack;

public class StackFilter implements Filter {
    private Stack<Filter> stack = new Stack();

    public void push(Filter filter) {
        this.stack.push(filter);
    }

    public String replace(String str) {
        int size = this.stack.size();
        while (true) {
            int i = size - 1;
            if (i < 0) {
                return null;
            }
            String replace = ((Filter) this.stack.get(i)).replace(str);
            if (replace != null) {
                return replace;
            }
            size = i;
        }
    }
}
