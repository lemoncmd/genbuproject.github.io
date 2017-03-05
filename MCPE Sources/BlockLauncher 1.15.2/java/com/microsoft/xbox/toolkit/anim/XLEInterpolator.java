package com.microsoft.xbox.toolkit.anim;

import android.view.animation.Interpolator;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;

public class XLEInterpolator implements Interpolator {
    private EasingMode easingMode;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode = new int[EasingMode.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode[EasingMode.EaseIn.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode[EasingMode.EaseOut.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode[EasingMode.EaseInOut.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public XLEInterpolator(EasingMode easingMode) {
        this.easingMode = easingMode;
    }

    public float getInterpolation(float f) {
        if (f < 0.0f || f > 1.0f) {
            throw new IllegalArgumentException("should respect 0<=normalizedTime<=1");
        }
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$anim$EasingMode[this.easingMode.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return getInterpolationCore(f);
            case NativeRegExp.PREFIX /*2*/:
                return 1.0f - getInterpolationCore(1.0f - f);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return ((double) f) < 0.5d ? getInterpolationCore(f * 2.0f) / 2.0f : 0.5f + ((1.0f - getInterpolationCore(2.0f - (f * 2.0f))) / 2.0f);
            default:
                return f;
        }
    }

    protected float getInterpolationCore(float f) {
        return f;
    }
}
