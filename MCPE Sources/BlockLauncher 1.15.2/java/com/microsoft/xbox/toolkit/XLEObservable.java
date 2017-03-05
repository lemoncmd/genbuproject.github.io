package com.microsoft.xbox.toolkit;

import java.util.ArrayList;
import java.util.HashSet;

public abstract class XLEObservable<T> {
    private HashSet<XLEObserver<T>> data = new HashSet();

    public void addObserver(XLEObserver<T> xLEObserver) {
        synchronized (this) {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            this.data.add(xLEObserver);
        }
    }

    public void addUniqueObserver(XLEObserver<T> xLEObserver) {
        synchronized (this) {
            if (!this.data.contains(xLEObserver)) {
                addObserver(xLEObserver);
            }
        }
    }

    protected void clearObserver() {
        synchronized (this) {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            this.data.clear();
        }
    }

    protected ArrayList<XLEObserver<T>> getObservers() {
        ArrayList<XLEObserver<T>> arrayList;
        synchronized (this) {
            arrayList = new ArrayList(this.data);
        }
        return arrayList;
    }

    public void notifyObservers(AsyncResult<T> asyncResult) {
        synchronized (this) {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            for (XLEObserver update : new ArrayList(this.data)) {
                update.update(asyncResult);
            }
        }
    }

    public void removeObserver(XLEObserver<T> xLEObserver) {
        synchronized (this) {
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            this.data.remove(xLEObserver);
        }
    }
}
