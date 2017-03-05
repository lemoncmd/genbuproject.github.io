package com.microsoft.xbox.toolkit.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HeightAnimation extends Animation {
    private int fromValue;
    private int toValue;
    private View view;

    public HeightAnimation(int i, int i2) {
        this.fromValue = i;
        this.toValue = i2;
    }

    protected void applyTransformation(float f, Transformation transformation) {
        int i = (int) (((float) (this.toValue - this.fromValue)) * f);
        this.view.getLayoutParams().height = i + this.fromValue;
        this.view.requestLayout();
    }

    public void setTargetView(View view) {
        this.view = view;
        this.fromValue = view.getHeight();
    }

    public boolean willChangeBounds() {
        return true;
    }
}
