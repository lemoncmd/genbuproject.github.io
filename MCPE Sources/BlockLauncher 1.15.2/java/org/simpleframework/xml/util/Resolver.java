package org.simpleframework.xml.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class Resolver<M extends Match> extends AbstractSet<M> {
    protected final Cache cache = new Cache();
    protected final Stack stack = new Stack();

    private class Cache extends LimitedCache<List<M>> {
        public Cache() {
            super(EnchantType.pickaxe);
        }
    }

    private class Stack extends LinkedList<M> {

        private class Sequence implements Iterator<M> {
            private int cursor;

            public Sequence() {
                this.cursor = Stack.this.size();
            }

            public boolean hasNext() {
                return this.cursor > 0;
            }

            public M next() {
                if (!hasNext()) {
                    return null;
                }
                Stack stack = Stack.this;
                int i = this.cursor - 1;
                this.cursor = i;
                return (Match) stack.get(i);
            }

            public void remove() {
                Stack.this.purge(this.cursor);
            }
        }

        private Stack() {
        }

        public void purge(int i) {
            Resolver.this.cache.clear();
            remove(i);
        }

        public void push(M m) {
            Resolver.this.cache.clear();
            addFirst(m);
        }

        public Iterator<M> sequence() {
            return new Sequence();
        }
    }

    private boolean match(char[] cArr, int i, char[] cArr2, int i2) {
        loop0:
        while (i2 < cArr2.length && i < cArr.length) {
            if (cArr2[i2] == '*') {
                while (cArr2[i2] == '*') {
                    i2++;
                    if (i2 >= cArr2.length) {
                        break loop0;
                    }
                }
                if (cArr2[i2] == '?') {
                    i2++;
                    if (i2 >= cArr2.length) {
                        break;
                    }
                }
                while (i < cArr.length) {
                    if (cArr[i] == cArr2[i2] || cArr2[i2] == '?') {
                        if (cArr2[i2 - 1] != '?') {
                            if (match(cArr, i, cArr2, i2)) {
                                break loop0;
                            }
                        } else {
                            break;
                        }
                    }
                    i++;
                }
                if (cArr.length == i) {
                    return false;
                }
            }
            int i3 = i2;
            int i4 = i;
            i = i4 + 1;
            i2 = i3 + 1;
            if (cArr[i4] != cArr2[i3] && cArr2[i2 - 1] != '?') {
                return false;
            }
        }
        if (cArr2.length == i2) {
            if (cArr.length != i) {
                return false;
            }
            return true;
        }
        while (cArr2[i2] == '*') {
            i2++;
            if (i2 >= cArr2.length) {
                return true;
            }
        }
        return false;
    }

    private boolean match(char[] cArr, char[] cArr2) {
        return match(cArr, 0, cArr2, 0);
    }

    private List<M> resolveAll(String str, char[] cArr) {
        List<M> arrayList = new ArrayList();
        Iterator it = this.stack.iterator();
        while (it.hasNext()) {
            Match match = (Match) it.next();
            if (match(cArr, match.getPattern().toCharArray())) {
                this.cache.put(str, arrayList);
                arrayList.add(match);
            }
        }
        return arrayList;
    }

    public boolean add(M m) {
        this.stack.push((Match) m);
        return true;
    }

    public void clear() {
        this.cache.clear();
        this.stack.clear();
    }

    public Iterator<M> iterator() {
        return this.stack.sequence();
    }

    public boolean remove(M m) {
        this.cache.clear();
        return this.stack.remove(m);
    }

    public M resolve(String str) {
        List list = (List) this.cache.get(str);
        if (list == null) {
            list = resolveAll(str);
        }
        return list.isEmpty() ? null : (Match) list.get(0);
    }

    public List<M> resolveAll(String str) {
        List<M> list = (List) this.cache.get(str);
        if (list != null) {
            return list;
        }
        char[] toCharArray = str.toCharArray();
        return toCharArray == null ? null : resolveAll(str, toCharArray);
    }

    public int size() {
        return this.stack.size();
    }
}
