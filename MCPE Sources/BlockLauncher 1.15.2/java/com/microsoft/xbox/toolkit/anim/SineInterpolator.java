package com.microsoft.xbox.toolkit.anim;

public class SineInterpolator extends XLEInterpolator {
    public SineInterpolator(EasingMode easingMode) {
        super(easingMode);
    }

    protected float getInterpolationCore(float f) {
        return (float) (1.0d - Math.sin(1.5707963267948966d * (1.0d - ((double) f))));
    }
}
