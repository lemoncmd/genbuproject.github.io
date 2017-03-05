package com.microsoft.xbox.xle.anim;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.anim.AnimationFunctionType;
import com.microsoft.xbox.toolkit.anim.AnimationProperty;
import com.microsoft.xbox.toolkit.anim.BackEaseInterpolator;
import com.microsoft.xbox.toolkit.anim.EasingMode;
import com.microsoft.xbox.toolkit.anim.ExponentialInterpolator;
import com.microsoft.xbox.toolkit.anim.HeightAnimation;
import com.microsoft.xbox.toolkit.anim.SineInterpolator;
import com.microsoft.xbox.toolkit.anim.XLEInterpolator;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.regexp.NativeRegExp;
import org.simpleframework.xml.Attribute;

public class XLEAnimationDefinition {
    @Attribute(required = false)
    public int delayMs;
    @Attribute(required = false)
    public String dimen;
    @Attribute(required = false)
    public int durationMs;
    @Attribute(required = false)
    public EasingMode easing = EasingMode.EaseIn;
    @Attribute(required = false)
    public float from;
    @Attribute(required = false)
    public int fromXType = 1;
    @Attribute(required = false)
    public int fromYType = 1;
    @Attribute(required = false)
    public float parameter;
    @Attribute(required = false)
    public float pivotX = 0.5f;
    @Attribute(required = false)
    public float pivotY = 0.5f;
    @Attribute(required = false)
    public AnimationProperty property;
    @Attribute(required = false)
    public boolean scaleRelativeToSelf = true;
    @Attribute(required = false)
    public float to;
    @Attribute(required = false)
    public int toXType = 1;
    @Attribute(required = false)
    public int toYType = 1;
    @Attribute(required = false)
    public AnimationFunctionType type;

    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType = new int[AnimationFunctionType.values().length];
        static final /* synthetic */ int[] $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty = new int[AnimationProperty.values().length];

        static {
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType[AnimationFunctionType.Sine.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType[AnimationFunctionType.Exponential.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType[AnimationFunctionType.BackEase.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[AnimationProperty.Alpha.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[AnimationProperty.Scale.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[AnimationProperty.PositionX.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[AnimationProperty.PositionY.ordinal()] = 4;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[AnimationProperty.Height.ordinal()] = 5;
            } catch (NoSuchFieldError e8) {
            }
        }
    }

    private Interpolator getInterpolator() {
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationFunctionType[this.type.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                return new SineInterpolator(this.easing);
            case NativeRegExp.PREFIX /*2*/:
                return new ExponentialInterpolator(this.parameter, this.easing);
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                return new BackEaseInterpolator(this.parameter, this.easing);
            default:
                return new XLEInterpolator(this.easing);
        }
    }

    public Animation getAnimation() {
        Animation alphaAnimation;
        Interpolator interpolator = getInterpolator();
        switch (AnonymousClass1.$SwitchMap$com$microsoft$xbox$toolkit$anim$AnimationProperty[this.property.ordinal()]) {
            case NativeRegExp.MATCH /*1*/:
                alphaAnimation = new AlphaAnimation(this.from, this.to);
                break;
            case NativeRegExp.PREFIX /*2*/:
                alphaAnimation = new ScaleAnimation(this.from, this.to, this.from, this.to, this.scaleRelativeToSelf ? 1 : 2, this.pivotX, this.scaleRelativeToSelf ? 1 : 2, this.pivotY);
                break;
            case FunctionNode.FUNCTION_EXPRESSION_STATEMENT /*3*/:
                alphaAnimation = new TranslateAnimation(this.fromXType, this.from, this.toXType, this.to, 1, 0.0f, 1, 0.0f);
                break;
            case NativeRegExp.JSREG_MULTILINE /*4*/:
                alphaAnimation = new TranslateAnimation(1, 0.0f, 1, 0.0f, this.fromYType, this.from, this.toYType, this.to);
                break;
            case Token.GOTO /*5*/:
                int findDimensionIdByName = XLERValueHelper.findDimensionIdByName(this.dimen);
                alphaAnimation = new HeightAnimation(0, findDimensionIdByName >= 0 ? XboxTcuiSdk.getResources().getDimensionPixelSize(findDimensionIdByName) : 0);
                break;
            default:
                alphaAnimation = null;
                break;
        }
        if (alphaAnimation == null) {
            return null;
        }
        alphaAnimation.setDuration((long) this.durationMs);
        alphaAnimation.setInterpolator(interpolator);
        alphaAnimation.setStartOffset((long) this.delayMs);
        return alphaAnimation;
    }
}
