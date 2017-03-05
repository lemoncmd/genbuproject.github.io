package com.microsoft.xbox.toolkit.anim;

public class BackEaseInterpolator extends XLEInterpolator {
    private float amplitude;

    public BackEaseInterpolator(float f, EasingMode easingMode) {
        super(easingMode);
        this.amplitude = f;
    }

    protected float getInterpolationCore(float f) {
        float max = (float) Math.max((double) f, 0.0d);
        return (float) (((double) ((max * max) * max)) - (Math.sin(((double) max) * 3.141592653589793d) * ((double) (this.amplitude * max))));
    }
}
