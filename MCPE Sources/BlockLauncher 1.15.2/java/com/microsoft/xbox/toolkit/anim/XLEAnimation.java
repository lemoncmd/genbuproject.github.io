package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Interpolator;
import com.microsoft.xbox.toolkit.ThreadManager;

public abstract class XLEAnimation {
    protected Runnable endRunnable;

    public abstract void clear();

    public abstract void setInterpolator(Interpolator interpolator);

    public void setOnAnimationEnd(final Runnable runnable) {
        if (runnable != null) {
            this.endRunnable = new Runnable() {
                public void run() {
                    ThreadManager.UIThreadPost(runnable);
                }
            };
        } else {
            this.endRunnable = null;
        }
    }

    public abstract void setTargetView(View view);

    public abstract void start();
}
