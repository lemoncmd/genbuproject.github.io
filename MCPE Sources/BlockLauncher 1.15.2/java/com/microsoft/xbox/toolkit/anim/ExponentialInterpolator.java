package com.microsoft.xbox.toolkit.anim;

public class ExponentialInterpolator extends XLEInterpolator {
    private float exponent;

    public ExponentialInterpolator(float f, EasingMode easingMode) {
        super(easingMode);
        this.exponent = f;
    }

    protected float getInterpolationCore(float f) {
        return (float) ((Math.pow(2.718281828459045d, (double) (this.exponent * f)) - 1.0d) / (Math.pow(2.718281828459045d, (double) this.exponent) - 1.0d));
    }
}
