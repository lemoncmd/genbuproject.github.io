package com.microsoft.xbox.toolkit;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.PriorityQueue;

public class ThreadSafeFixedSizeHashtable<K, V> {
    private int count = 0;
    private PriorityQueue<KeyTuple> fifo = new PriorityQueue();
    private Hashtable<K, V> hashtable = new Hashtable();
    private final int maxSize;
    private Object syncObject = new Object();

    private class KeyTuple implements Comparable<KeyTuple> {
        private int index = 0;
        private K key;

        public KeyTuple(K k, int i) {
            this.key = k;
            this.index = i;
        }

        public int compareTo(KeyTuple keyTuple) {
            return this.index - keyTuple.index;
        }

        public K getKey() {
            return this.key;
        }
    }

    public ThreadSafeFixedSizeHashtable(int i) {
        this.maxSize = i;
        if (i <= 0) {
            throw new IllegalArgumentException();
        }
    }

    private void cleanupIfNecessary() {
        XLEAssert.assertTrue(this.hashtable.size() == this.fifo.size());
        while (this.hashtable.size() > this.maxSize) {
            this.hashtable.remove(((KeyTuple) this.fifo.remove()).getKey());
            XLEAssert.assertTrue(this.hashtable.size() == this.fifo.size());
        }
    }

    public Enumeration<V> elements() {
        return this.hashtable.elements();
    }

    public V get(K k) {
        if (k == null) {
            return null;
        }
        V v;
        synchronized (this.syncObject) {
            v = this.hashtable.get(k);
        }
        return v;
    }

    public Enumeration<K> keys() {
        return this.hashtable.keys();
    }

    public void put(K k, V v) {
        if (k != null && v != null) {
            synchronized (this.syncObject) {
                if (this.hashtable.containsKey(k)) {
                    return;
                }
                this.count++;
                this.fifo.add(new KeyTuple(k, this.count));
                this.hashtable.put(k, v);
                cleanupIfNecessary();
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void remove(K r6) {
        /*
        r5 = this;
        if (r6 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        r2 = r5.syncObject;
        monitor-enter(r2);
        r0 = r5.hashtable;	 Catch:{ all -> 0x0010 }
        r0 = r0.containsKey(r6);	 Catch:{ all -> 0x0010 }
        if (r0 != 0) goto L_0x0013;
    L_0x000e:
        monitor-exit(r2);	 Catch:{ all -> 0x0010 }
        goto L_0x0002;
    L_0x0010:
        r0 = move-exception;
        monitor-exit(r2);	 Catch:{ all -> 0x0010 }
        throw r0;
    L_0x0013:
        r0 = r5.hashtable;	 Catch:{ all -> 0x0010 }
        r0.remove(r6);	 Catch:{ all -> 0x0010 }
        r1 = 0;
        r0 = r5.fifo;	 Catch:{ all -> 0x0010 }
        r3 = r0.iterator();	 Catch:{ all -> 0x0010 }
    L_0x001f:
        r0 = r3.hasNext();	 Catch:{ all -> 0x0010 }
        if (r0 == 0) goto L_0x003e;
    L_0x0025:
        r0 = r3.next();	 Catch:{ all -> 0x0010 }
        r0 = (com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable.KeyTuple) r0;	 Catch:{ all -> 0x0010 }
        r4 = r0.key;	 Catch:{ all -> 0x0010 }
        r4 = r4.equals(r6);	 Catch:{ all -> 0x0010 }
        if (r4 == 0) goto L_0x001f;
    L_0x0035:
        if (r0 == 0) goto L_0x003c;
    L_0x0037:
        r1 = r5.fifo;	 Catch:{ all -> 0x0010 }
        r1.remove(r0);	 Catch:{ all -> 0x0010 }
    L_0x003c:
        monitor-exit(r2);	 Catch:{ all -> 0x0010 }
        goto L_0x0002;
    L_0x003e:
        r0 = r1;
        goto L_0x0035;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.microsoft.xbox.toolkit.ThreadSafeFixedSizeHashtable.remove(java.lang.Object):void");
    }
}
