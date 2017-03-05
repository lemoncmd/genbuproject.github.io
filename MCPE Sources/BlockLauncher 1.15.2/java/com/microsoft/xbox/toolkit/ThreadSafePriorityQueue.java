package com.microsoft.xbox.toolkit;

import java.util.HashSet;
import java.util.PriorityQueue;

public class ThreadSafePriorityQueue<T> {
    private HashSet<T> hashSet = new HashSet();
    private PriorityQueue<T> queue = new PriorityQueue();
    private Object syncObject = new Object();

    public T pop() {
        Throwable th;
        T t = null;
        try {
            synchronized (this.syncObject) {
                while (this.queue.isEmpty()) {
                    try {
                        this.syncObject.wait();
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
                T remove = this.queue.remove();
                try {
                    this.hashSet.remove(remove);
                    return remove;
                } catch (Throwable th3) {
                    Throwable th4 = th3;
                    t = remove;
                    th = th4;
                    try {
                        throw th;
                    } catch (InterruptedException e) {
                        return t;
                    }
                }
            }
        } catch (InterruptedException e2) {
            return null;
        }
    }

    public void push(T t) {
        synchronized (this.syncObject) {
            if (!this.hashSet.contains(t)) {
                this.queue.add(t);
                this.hashSet.add(t);
                this.syncObject.notifyAll();
            }
        }
    }
}
