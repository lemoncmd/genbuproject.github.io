package org.simpleframework.xml.core;

class Template {
    protected char[] buf;
    protected String cache;
    protected int count;

    public Template() {
        this(16);
    }

    public Template(int i) {
        this.buf = new char[i];
    }

    public void append(char c) {
        ensureCapacity(this.count + 1);
        char[] cArr = this.buf;
        int i = this.count;
        this.count = i + 1;
        cArr[i] = c;
    }

    public void append(String str) {
        ensureCapacity(this.count + str.length());
        str.getChars(0, str.length(), this.buf, this.count);
        this.count += str.length();
    }

    public void append(String str, int i, int i2) {
        ensureCapacity(this.count + i2);
        str.getChars(i, i2, this.buf, this.count);
        this.count += i2;
    }

    public void append(Template template) {
        append(template.buf, 0, template.count);
    }

    public void append(Template template, int i, int i2) {
        append(template.buf, i, i2);
    }

    public void append(char[] cArr, int i, int i2) {
        ensureCapacity(this.count + i2);
        System.arraycopy(cArr, i, this.buf, this.count, i2);
        this.count += i2;
    }

    public void clear() {
        this.cache = null;
        this.count = 0;
    }

    protected void ensureCapacity(int i) {
        if (this.buf.length < i) {
            Object obj = new char[Math.max(i, this.buf.length * 2)];
            System.arraycopy(this.buf, 0, obj, 0, this.count);
            this.buf = obj;
        }
    }

    public int length() {
        return this.count;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }
}
