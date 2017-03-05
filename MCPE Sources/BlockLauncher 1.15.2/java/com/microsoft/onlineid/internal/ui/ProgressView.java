package com.microsoft.onlineid.internal.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import org.mozilla.javascript.Token;

public class ProgressView extends RelativeLayout {
    public static final int NumberOfDots = 5;
    private static final int ProgressColor = Color.rgb(Token.BREAK, Token.BREAK, Token.BREAK);
    private static final float ProgressDotSizeDip = 2.6f;
    private static final float ProgressPaddingDip = 2.6f;
    private int _dotSize;
    private ProgressAnimation _progressAnimation;

    private class ProgressAnimation {
        private int _animationDuration;
        private int[] _dotDelays;
        private int[] _keyframeXTranslations;
        private AnimatorSet _progressAnimator = generateAnimation();

        public ProgressAnimation(int i) {
            setAnimationParams(i);
        }

        private PropertyValuesHolder createKeyframes() {
            int i = ProgressView.ProgressColor;
            float[] fArr = new float[ProgressView.NumberOfDots];
            fArr[ProgressView.ProgressColor] = 0.0f;
            fArr[1] = 0.15f;
            fArr[2] = 0.65f;
            fArr[3] = 0.8f;
            fArr[4] = 1.0f;
            Keyframe[] keyframeArr = new Keyframe[this._keyframeXTranslations.length];
            while (i < this._keyframeXTranslations.length) {
                keyframeArr[i] = Keyframe.ofFloat(fArr[i], (float) this._keyframeXTranslations[i]);
                i++;
            }
            keyframeArr[this._keyframeXTranslations.length - 1].setInterpolator(new OvershootInterpolator(1.0f));
            return PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, keyframeArr);
        }

        private AnimatorSet generateAnimation() {
            PropertyValuesHolder createKeyframes = createKeyframes();
            Animator[] animatorArr = new ObjectAnimator[ProgressView.NumberOfDots];
            for (int i = ProgressView.ProgressColor; i < ProgressView.NumberOfDots; i++) {
                ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(ProgressView.this.getChildAt(i), new PropertyValuesHolder[]{createKeyframes});
                ofPropertyValuesHolder.setDuration((long) this._animationDuration);
                ofPropertyValuesHolder.setRepeatCount(-1);
                ofPropertyValuesHolder.setStartDelay((long) this._dotDelays[i]);
                animatorArr[i] = ofPropertyValuesHolder;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animatorArr);
            return animatorSet;
        }

        private void setAnimationParams(int i) {
            int round = Math.round(((float) i) / 25.0f);
            float f = 30.0f + (((float) i) / 10.0f);
            int round2 = Math.round(((float) i) * 0.416666f);
            int round3 = Math.round(((float) i) * (1.0f - (0.416666f * 2.0f)));
            float f2 = ((float) (round3 * 1000)) / f;
            float f3 = ((f2 / 0.3333f) - f2) / 2.0f;
            round = Math.round(((float) (round * 1000)) / f);
            if (round > 333) {
                round = 333;
            }
            int[] iArr = new int[ProgressView.NumberOfDots];
            iArr[ProgressView.ProgressColor] = ProgressView.ProgressColor;
            iArr[1] = round;
            iArr[2] = round * 2;
            iArr[3] = round * 3;
            iArr[4] = round * 4;
            this._dotDelays = iArr;
            this._animationDuration = Math.round(((((float) round) * 4.0f) + (f2 + (f3 * 2.0f))) + 250.0f);
            int[] iArr2 = new int[ProgressView.NumberOfDots];
            iArr2[ProgressView.ProgressColor] = ProgressView.this._dotSize * -1;
            iArr2[1] = round2;
            iArr2[2] = round2 + round3;
            iArr2[3] = ProgressView.this._dotSize + i;
            iArr2[4] = ProgressView.this._dotSize * -1;
            this._keyframeXTranslations = iArr2;
        }

        public boolean isAnimating() {
            return this._progressAnimator.isRunning();
        }

        public boolean startAnimation() {
            if (this._progressAnimator.isRunning()) {
                return false;
            }
            this._progressAnimator.start();
            return true;
        }

        public boolean stopAnimation() {
            if (!this._progressAnimator.isRunning()) {
                return false;
            }
            this._progressAnimator.end();
            return true;
        }
    }

    public ProgressView(Context context) {
        super(context);
        initialize();
    }

    public ProgressView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize();
    }

    public ProgressView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initialize();
    }

    private View addDot() {
        View view = new View(getContext());
        view.setLayoutParams(new LayoutParams(this._dotSize, this._dotSize));
        view.setBackgroundColor(ProgressColor);
        view.setX((float) (this._dotSize * -1));
        addView(view);
        return view;
    }

    private void initialize() {
        int i = ProgressColor;
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        this._dotSize = Dimensions.convertDipToPixels(ProgressPaddingDip, displayMetrics);
        int convertDipToPixels = Dimensions.convertDipToPixels(ProgressPaddingDip, displayMetrics);
        setPadding(ProgressColor, convertDipToPixels, ProgressColor, convertDipToPixels);
        while (i < NumberOfDots) {
            addDot();
            i++;
        }
        this._progressAnimation = new ProgressAnimation(getWidth());
    }

    public boolean isAnimating() {
        return this._progressAnimation != null && this._progressAnimation.isAnimating();
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        boolean stopAnimation = stopAnimation();
        this._progressAnimation = new ProgressAnimation(i);
        if (stopAnimation) {
            startAnimation();
        }
    }

    public void overrideDefaultPadding(float f) {
        int convertDipToPixels = Dimensions.convertDipToPixels(ProgressPaddingDip, getContext().getResources().getDisplayMetrics());
        setPadding(ProgressColor, convertDipToPixels, ProgressColor, convertDipToPixels);
    }

    public boolean startAnimation() {
        return this._progressAnimation != null && this._progressAnimation.startAnimation();
    }

    public boolean stopAnimation() {
        return this._progressAnimation != null && this._progressAnimation.stopAnimation();
    }
}
