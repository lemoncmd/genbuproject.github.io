package com.microsoft.cll.android;

import java.util.ArrayList;

public class SynchronizedArrayList<T> extends ArrayList<T> {
    public boolean add(T t) {
        boolean add;
        synchronized (this) {
            add = contains(t) ? false : super.add(t);
        }
        return add;
    }

    public boolean remove(Object obj) {
        boolean remove;
        synchronized (this) {
            remove = super.remove(obj);
        }
        return remove;
    }
}
