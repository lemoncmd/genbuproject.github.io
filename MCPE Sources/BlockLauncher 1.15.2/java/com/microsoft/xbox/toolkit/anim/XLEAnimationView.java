package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.XLEAssert;

public class XLEAnimationView extends XLEAnimation {
    private Animation anim;
    private View animtarget;

    public XLEAnimationView(Animation animation) {
        this.anim = animation;
        this.anim.setFillAfter(true);
        this.anim.setAnimationListener(new AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                XLEAnimationView.this.onViewAnimationEnd();
                if (XLEAnimationView.this.endRunnable != null) {
                    XLEAnimationView.this.endRunnable.run();
                }
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
                XLEAnimationView.this.onViewAnimationStart();
            }
        });
    }

    private void onViewAnimationEnd() {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                XLEAnimationView.this.animtarget.setLayerType(0, null);
            }
        });
    }

    private void onViewAnimationStart() {
        this.animtarget.setLayerType(2, null);
    }

    public void clear() {
        this.anim.setAnimationListener(null);
        this.animtarget.clearAnimation();
    }

    public void setFillAfter(boolean z) {
        this.anim.setFillAfter(z);
    }

    public void setInterpolator(Interpolator interpolator) {
        this.anim.setInterpolator(interpolator);
    }

    public void setTargetView(View view) {
        XLEAssert.assertNotNull(view);
        this.animtarget = view;
        if (this.anim instanceof AnimationSet) {
            for (Animation animation : ((AnimationSet) this.anim).getAnimations()) {
                if (animation instanceof HeightAnimation) {
                    ((HeightAnimation) animation).setTargetView(view);
                }
            }
        }
    }

    public void start() {
        this.animtarget.startAnimation(this.anim);
    }
}
