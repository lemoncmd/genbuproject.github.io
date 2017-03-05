package com.microsoft.xbox.toolkit.anim;

import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;
import java.util.Iterator;
import java.util.LinkedList;

public class XLEAnimationPackage {
    private LinkedList<XLEAnimationEntry> animations = new LinkedList();
    private Runnable onAnimationEndRunnable;
    private boolean running = false;

    private class XLEAnimationEntry {
        public XLEAnimation animation;
        public boolean done = false;
        public int iterationID = 0;

        public XLEAnimationEntry(XLEAnimation xLEAnimation) {
            this.animation = xLEAnimation;
            xLEAnimation.setOnAnimationEnd(new Runnable(XLEAnimationPackage.this) {
                public void run() {
                    XLEAnimationEntry.this.onAnimationEnded();
                }
            });
        }

        private void finish() {
            this.done = true;
            XLEAnimationPackage.this.tryFinishAll();
        }

        private void onAnimationEnded() {
            boolean z = false;
            XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
            if (XLEAnimationPackage.this.onAnimationEndRunnable != null) {
                z = true;
            }
            XLEAssert.assertTrue(z);
            final int i = this.iterationID;
            ThreadManager.UIThreadPost(new Runnable() {
                public void run() {
                    if (i == XLEAnimationEntry.this.iterationID) {
                        XLEAnimationEntry.this.finish();
                    }
                }
            });
        }

        public void clearAnimation() {
            this.iterationID++;
            this.animation.clear();
        }

        public void startAnimation() {
            this.animation.start();
        }
    }

    private int getRemainingAnimations() {
        Iterator it = this.animations.iterator();
        int i = 0;
        while (it.hasNext()) {
            if (!((XLEAnimationEntry) it.next()).done) {
                i++;
            }
        }
        return i;
    }

    private void tryFinishAll() {
        if (getRemainingAnimations() == 0) {
            XLEAssert.assertTrue(this.running);
            this.running = false;
            this.onAnimationEndRunnable.run();
        }
    }

    public XLEAnimationPackage add(XLEAnimationPackage xLEAnimationPackage) {
        if (xLEAnimationPackage != null) {
            Iterator it = xLEAnimationPackage.animations.iterator();
            while (it.hasNext()) {
                add(((XLEAnimationEntry) it.next()).animation);
            }
        }
        return this;
    }

    public void add(XLEAnimation xLEAnimation) {
        this.animations.add(new XLEAnimationEntry(xLEAnimation));
    }

    public void clearAnimation() {
        Iterator it = this.animations.iterator();
        while (it.hasNext()) {
            ((XLEAnimationEntry) it.next()).clearAnimation();
        }
    }

    public void setOnAnimationEndRunnable(Runnable runnable) {
        this.onAnimationEndRunnable = runnable;
    }

    public void startAnimation() {
        XLEAssert.assertTrue(!this.running);
        this.running = true;
        Iterator it = this.animations.iterator();
        while (it.hasNext()) {
            ((XLEAnimationEntry) it.next()).startAnimation();
        }
    }
}
