package com.microsoft.xbox.toolkit;

public class XLEMemoryCacheEntry<V> {
    private int byteCount;
    private V data;

    public XLEMemoryCacheEntry(V v, int i) {
        if (v == null) {
            throw new IllegalArgumentException("data");
        } else if (i <= 0) {
            throw new IllegalArgumentException("byteCount");
        } else {
            this.data = v;
            this.byteCount = i;
        }
    }

    public int getByteCount() {
        return this.byteCount;
    }

    public V getValue() {
        return this.data;
    }
}
