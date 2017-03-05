package com.microsoft.xbox.toolkit;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

public class BackgroundThreadWaitor {
    private static BackgroundThreadWaitor instance = new BackgroundThreadWaitor();
    private BackgroundThreadWaitorChangedCallback blockingChangedCallback = null;
    private Hashtable<WaitType, WaitObject> blockingTable = new Hashtable();
    private Ready waitReady = new Ready();
    private ArrayList<Runnable> waitingRunnables = new ArrayList();

    public interface BackgroundThreadWaitorChangedCallback {
        void run(EnumSet<WaitType> enumSet, boolean z);
    }

    private class WaitObject {
        private long expires;
        private WaitType type;

        public WaitObject(WaitType waitType, long j) {
            this.type = waitType;
            this.expires = SystemClock.uptimeMillis() + j;
        }

        public boolean isExpired() {
            return this.expires < SystemClock.uptimeMillis();
        }
    }

    public enum WaitType {
        Navigation,
        ApplicationBar,
        ListScroll,
        ListLayout,
        PivotScroll
    }

    private void drainWaitingRunnables() {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        Iterator it = this.waitingRunnables.iterator();
        while (it.hasNext()) {
            ((Runnable) it.next()).run();
        }
        this.waitingRunnables.clear();
    }

    public static BackgroundThreadWaitor getInstance() {
        if (instance == null) {
            instance = new BackgroundThreadWaitor();
        }
        return instance;
    }

    private void updateWaitReady() {
        boolean z = false;
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        HashSet hashSet = new HashSet();
        EnumSet noneOf = EnumSet.noneOf(WaitType.class);
        Enumeration elements = this.blockingTable.elements();
        while (elements.hasMoreElements()) {
            WaitObject waitObject = (WaitObject) elements.nextElement();
            if (waitObject.isExpired()) {
                hashSet.add(waitObject.type);
            } else {
                noneOf.add(waitObject.type);
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            this.blockingTable.remove((WaitType) it.next());
        }
        if (this.blockingTable.size() == 0) {
            this.waitReady.setReady();
            drainWaitingRunnables();
        } else {
            this.waitReady.reset();
            z = true;
        }
        if (this.blockingChangedCallback != null) {
            this.blockingChangedCallback.run(noneOf, z);
        }
    }

    public void clearBlocking(WaitType waitType) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.blockingTable.remove(waitType);
        updateWaitReady();
    }

    public boolean isBlocking() {
        return !this.waitReady.getIsReady();
    }

    public void postRunnableAfterReady(Runnable runnable) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        if (runnable != null) {
            if (isBlocking()) {
                this.waitingRunnables.add(runnable);
            } else {
                runnable.run();
            }
        }
    }

    public void setBlocking(WaitType waitType, int i) {
        XLEAssert.assertTrue(ThreadManager.UIThread == Thread.currentThread());
        this.blockingTable.put(waitType, new WaitObject(waitType, (long) i));
        updateWaitReady();
    }

    public void setChangedCallback(BackgroundThreadWaitorChangedCallback backgroundThreadWaitorChangedCallback) {
        this.blockingChangedCallback = backgroundThreadWaitorChangedCallback;
    }

    public void waitForReady(int i) {
        XLEAssert.assertTrue(ThreadManager.UIThread != Thread.currentThread());
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                BackgroundThreadWaitor.this.updateWaitReady();
            }
        });
        this.waitReady.waitForReady(i);
    }
}
