package org.simpleframework.xml.stream;

import net.hockeyapp.android.BuildConfig;

class Indenter {
    private Cache cache;
    private int count;
    private int indent;
    private int index;

    private static class Cache {
        private int count;
        private String[] list;

        public Cache(int i) {
            this.list = new String[i];
        }

        private void resize(int i) {
            String[] strArr = new String[i];
            for (int i2 = 0; i2 < this.list.length; i2++) {
                strArr[i2] = this.list[i2];
            }
            this.list = strArr;
        }

        public String get(int i) {
            return i < this.list.length ? this.list[i] : null;
        }

        public void set(int i, String str) {
            if (i >= this.list.length) {
                resize(i * 2);
            }
            if (i > this.count) {
                this.count = i;
            }
            this.list[i] = str;
        }

        public int size() {
            return this.count;
        }
    }

    public Indenter() {
        this(new Format());
    }

    public Indenter(Format format) {
        this(format, 16);
    }

    private Indenter(Format format, int i) {
        this.indent = format.getIndent();
        this.cache = new Cache(i);
    }

    private String create() {
        char[] cArr = new char[(this.count + 1)];
        if (this.count <= 0) {
            return "\n";
        }
        cArr[0] = '\n';
        for (int i = 1; i <= this.count; i++) {
            cArr[i] = ' ';
        }
        return new String(cArr);
    }

    private String indent(int i) {
        if (this.indent > 0) {
            String str = this.cache.get(i);
            if (str == null) {
                str = create();
                this.cache.set(i, str);
            }
            if (this.cache.size() > 0) {
                return str;
            }
        }
        return BuildConfig.FLAVOR;
    }

    public String pop() {
        int i = this.index - 1;
        this.index = i;
        String indent = indent(i);
        if (this.indent > 0) {
            this.count -= this.indent;
        }
        return indent;
    }

    public String push() {
        int i = this.index;
        this.index = i + 1;
        String indent = indent(i);
        if (this.indent > 0) {
            this.count += this.indent;
        }
        return indent;
    }

    public String top() {
        return indent(this.index);
    }
}
