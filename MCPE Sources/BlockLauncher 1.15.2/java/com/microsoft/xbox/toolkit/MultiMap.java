package com.microsoft.xbox.toolkit;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public class MultiMap<K, V> {
    private Hashtable<K, HashSet<V>> data = new Hashtable();
    private Hashtable<V, K> dataInverse = new Hashtable();

    private void removeKeyIfEmpty(K k) {
        HashSet hashSet = get(k);
        if (hashSet != null && hashSet.isEmpty()) {
            this.data.remove(k);
        }
    }

    public int TESTsizeDegenerate() {
        int i = 0;
        for (Object obj : this.data.keySet()) {
            if (((HashSet) this.data.get(obj)).size() == 0) {
                i++;
            }
        }
        return i;
    }

    public void clear() {
        this.data.clear();
        this.dataInverse.clear();
    }

    public boolean containsKey(K k) {
        return this.data.containsKey(k);
    }

    public boolean containsValue(V v) {
        return getKey(v) != null;
    }

    public HashSet<V> get(K k) {
        return (HashSet) this.data.get(k);
    }

    public K getKey(V v) {
        return this.dataInverse.get(v);
    }

    public boolean keyValueMatches(K k, V v) {
        HashSet hashSet = get(k);
        return hashSet == null ? false : hashSet.contains(v);
    }

    public void put(K k, V v) {
        if (this.data.get(k) == null) {
            this.data.put(k, new HashSet());
        }
        XLEAssert.assertTrue(!this.dataInverse.containsKey(v));
        ((HashSet) this.data.get(k)).add(v);
        this.dataInverse.put(v, k);
    }

    public void removeKey(K k) {
        Iterator it = ((HashSet) this.data.get(k)).iterator();
        while (it.hasNext()) {
            Object next = it.next();
            XLEAssert.assertTrue(this.dataInverse.containsKey(next));
            this.dataInverse.remove(next);
        }
        this.data.remove(k);
    }

    public void removeValue(V v) {
        Object key = getKey(v);
        ((HashSet) this.data.get(key)).remove(v);
        this.dataInverse.remove(v);
        removeKeyIfEmpty(key);
    }

    public int size() {
        return this.data.size();
    }
}
