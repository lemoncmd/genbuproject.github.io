package com.microsoft.bond.internal;

public class IntArrayStack {
    private static final int DEFAULT_CAPACITY = 32;
    private int size;
    private int[] values;

    public IntArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    public IntArrayStack(int i) {
        this.values = new int[i];
    }

    private void ensureExtraCapacity(int i) {
        int i2 = this.size + i;
        if (i2 > this.values.length) {
            Object obj = new int[(i2 * 2)];
            System.arraycopy(this.values, 0, obj, 0, this.values.length);
            this.values = obj;
        }
    }

    public void clear() {
        this.size = 0;
    }

    public int get(int i) {
        return this.values[i];
    }

    public int getSize() {
        return this.size;
    }

    public int pop() {
        this.size--;
        return this.values[this.size];
    }

    public void push(int i) {
        ensureExtraCapacity(1);
        this.values[this.size] = i;
        this.size++;
    }

    public void set(int i, int i2) {
        this.values[i] = i2;
    }
}
