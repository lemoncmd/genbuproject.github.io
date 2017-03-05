package de.ankri.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.CompoundButton;
import de.ankri.text.method.AllCapsTransformationMethod;
import de.ankri.text.method.TransformationMethodCompat2;
import net.zhuoweizhang.mcpelauncher.R;
import org.mozilla.javascript.Token;
import org.mozilla.javascript.regexp.NativeRegExp;

public class Switch extends CompoundButton {
    private static final int[] CHECKED_STATE_SET;
    private static final int MONOSPACE = 3;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int TOUCH_MODE_DOWN = 1;
    private static final int TOUCH_MODE_DRAGGING = 2;
    private static final int TOUCH_MODE_IDLE = 0;
    private int mMinFlingVelocity;
    private Layout mOffLayout;
    private Layout mOnLayout;
    private int mSwitchBottom;
    private int mSwitchHeight;
    private int mSwitchLeft;
    private int mSwitchMinWidth;
    private int mSwitchPadding;
    private int mSwitchRight;
    private int mSwitchTop;
    private TransformationMethodCompat2 mSwitchTransformationMethod;
    private int mSwitchWidth;
    private final Rect mTempRect;
    private ColorStateList mTextColors;
    private CharSequence mTextOff;
    private CharSequence mTextOn;
    private TextPaint mTextPaint;
    private Drawable mThumbDrawable;
    private float mThumbPosition;
    private int mThumbTextPadding;
    private int mThumbWidth;
    private int mTouchMode;
    private int mTouchSlop;
    private float mTouchX;
    private float mTouchY;
    private Drawable mTrackDrawable;
    private VelocityTracker mVelocityTracker;

    static {
        int[] iArr = new int[TOUCH_MODE_DOWN];
        iArr[0] = 16842912;
        CHECKED_STATE_SET = iArr;
    }

    public Switch(Context context) {
        this(context, null);
    }

    public Switch(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.switchStyle);
    }

    public Switch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mVelocityTracker = VelocityTracker.obtain();
        this.mTempRect = new Rect();
        this.mTextPaint = new TextPaint(TOUCH_MODE_DOWN);
        Resources res = getResources();
        this.mTextPaint.density = res.getDisplayMetrics().density;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Switch, defStyle, 0);
        this.mThumbDrawable = a.getDrawable(R.styleable.Switch_thumb);
        this.mTrackDrawable = a.getDrawable(R.styleable.Switch_track);
        this.mTextOn = a.getText(R.styleable.Switch_textOn);
        this.mTextOff = a.getText(R.styleable.Switch_textOff);
        this.mThumbTextPadding = a.getDimensionPixelSize(R.styleable.Switch_thumbTextPadding, 0);
        this.mSwitchMinWidth = a.getDimensionPixelSize(R.styleable.Switch_switchMinWidth, 0);
        this.mSwitchPadding = a.getDimensionPixelSize(R.styleable.Switch_switchPadding, 0);
        int appearance = a.getResourceId(R.styleable.Switch_switchTextAppearance, 0);
        if (appearance != 0) {
            setSwitchTextAppearance(context, appearance);
        }
        a.recycle();
        ViewConfiguration config = ViewConfiguration.get(context);
        this.mTouchSlop = config.getScaledTouchSlop();
        this.mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
        refreshDrawableState();
        setChecked(isChecked());
        setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            }
        });
    }

    public void setSwitchTextAppearance(Context context, int resid) {
        TypedArray appearance = context.obtainStyledAttributes(resid, R.styleable.TextAppearanceSwitch);
        ColorStateList colors = appearance.getColorStateList(R.styleable.TextAppearanceSwitch_textColor);
        if (colors != null) {
            this.mTextColors = colors;
        } else {
            this.mTextColors = getTextColors();
        }
        int ts = appearance.getDimensionPixelSize(R.styleable.TextAppearanceSwitch_textSize, 0);
        if (!(ts == 0 || ((float) ts) == this.mTextPaint.getTextSize())) {
            this.mTextPaint.setTextSize((float) ts);
            requestLayout();
        }
        setSwitchTypefaceByIndex(appearance.getInt(R.styleable.TextAppearanceSwitch_typeface, -1), appearance.getInt(R.styleable.TextAppearanceSwitch_switchTextStyle, -1));
        if (appearance.getBoolean(R.styleable.TextAppearanceSwitch_textAllCaps, false)) {
            this.mSwitchTransformationMethod = new AllCapsTransformationMethod(getContext());
            this.mSwitchTransformationMethod.setLengthChangesAllowed(true);
        } else {
            this.mSwitchTransformationMethod = null;
        }
        appearance.recycle();
    }

    private void setSwitchTypefaceByIndex(int typefaceIndex, int styleIndex) {
        Typeface tf = null;
        switch (typefaceIndex) {
            case TOUCH_MODE_DOWN /*1*/:
                tf = Typeface.SANS_SERIF;
                break;
            case TOUCH_MODE_DRAGGING /*2*/:
                tf = Typeface.SERIF;
                break;
            case MONOSPACE /*3*/:
                tf = Typeface.MONOSPACE;
                break;
        }
        setSwitchTypeface(tf, styleIndex);
    }

    public void setSwitchTypeface(Typeface tf, int style) {
        boolean z = false;
        if (style > 0) {
            int typefaceStyle;
            float f;
            if (tf == null) {
                tf = Typeface.defaultFromStyle(style);
            } else {
                tf = Typeface.create(tf, style);
            }
            setSwitchTypeface(tf);
            if (tf != null) {
                typefaceStyle = tf.getStyle();
            } else {
                typefaceStyle = 0;
            }
            int need = style & (typefaceStyle ^ -1);
            TextPaint textPaint = this.mTextPaint;
            if ((need & TOUCH_MODE_DOWN) != 0) {
                z = true;
            }
            textPaint.setFakeBoldText(z);
            textPaint = this.mTextPaint;
            if ((need & TOUCH_MODE_DRAGGING) != 0) {
                f = -0.25f;
            } else {
                f = 0.0f;
            }
            textPaint.setTextSkewX(f);
            return;
        }
        this.mTextPaint.setFakeBoldText(false);
        this.mTextPaint.setTextSkewX(0.0f);
        setSwitchTypeface(tf);
    }

    public void setSwitchTypeface(Typeface tf) {
        if (this.mTextPaint.getTypeface() != tf) {
            this.mTextPaint.setTypeface(tf);
            requestLayout();
            invalidate();
        }
    }

    public void setSwitchPadding(int pixels) {
        this.mSwitchPadding = pixels;
        requestLayout();
    }

    public int getSwitchPadding() {
        return this.mSwitchPadding;
    }

    public void setSwitchMinWidth(int pixels) {
        this.mSwitchMinWidth = pixels;
        requestLayout();
    }

    public int getSwitchMinWidth() {
        return this.mSwitchMinWidth;
    }

    public void setThumbTextPadding(int pixels) {
        this.mThumbTextPadding = pixels;
        requestLayout();
    }

    public int getThumbTextPadding() {
        return this.mThumbTextPadding;
    }

    public void setTrackDrawable(Drawable track) {
        this.mTrackDrawable = track;
        requestLayout();
    }

    public void setTrackResource(int resId) {
        setTrackDrawable(getContext().getResources().getDrawable(resId));
    }

    public Drawable getTrackDrawable() {
        return this.mTrackDrawable;
    }

    public void setThumbDrawable(Drawable thumb) {
        this.mThumbDrawable = thumb;
        requestLayout();
    }

    public void setThumbResource(int resId) {
        setThumbDrawable(getContext().getResources().getDrawable(resId));
    }

    public Drawable getThumbDrawable() {
        return this.mThumbDrawable;
    }

    public CharSequence getTextOn() {
        return this.mTextOn;
    }

    public void setTextOn(CharSequence textOn) {
        this.mTextOn = textOn;
        requestLayout();
    }

    public CharSequence getTextOff() {
        return this.mTextOff;
    }

    public void setTextOff(CharSequence textOff) {
        this.mTextOff = textOff;
        requestLayout();
    }

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (this.mOnLayout == null) {
            this.mOnLayout = makeLayout(this.mTextOn);
        }
        if (this.mOffLayout == null) {
            this.mOffLayout = makeLayout(this.mTextOff);
        }
        this.mTrackDrawable.getPadding(this.mTempRect);
        int maxTextWidth = Math.max(this.mOnLayout.getWidth(), this.mOffLayout.getWidth());
        int switchWidth = Math.max(this.mSwitchMinWidth, (((maxTextWidth * TOUCH_MODE_DRAGGING) + (this.mThumbTextPadding * 4)) + this.mTempRect.left) + this.mTempRect.right);
        int switchHeight = this.mTrackDrawable.getIntrinsicHeight();
        this.mThumbWidth = (this.mThumbTextPadding * TOUCH_MODE_DRAGGING) + maxTextWidth;
        this.mSwitchWidth = switchWidth;
        this.mSwitchHeight = switchHeight;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() < switchHeight) {
            setMeasuredDimension(getMeasuredWidth(), switchHeight);
        }
    }

    private Layout makeLayout(CharSequence text) {
        CharSequence transformed;
        if (this.mSwitchTransformationMethod != null) {
            transformed = this.mSwitchTransformationMethod.getTransformation(text, this);
        } else {
            transformed = text;
        }
        return new StaticLayout(transformed, this.mTextPaint, (int) FloatMath.ceil(Layout.getDesiredWidth(transformed, this.mTextPaint)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);
    }

    private boolean hitThumb(float x, float y) {
        this.mThumbDrawable.getPadding(this.mTempRect);
        int thumbLeft = (this.mSwitchLeft + ((int) (this.mThumbPosition + 0.5f))) - this.mTouchSlop;
        return x > ((float) thumbLeft) && x < ((float) ((((this.mThumbWidth + thumbLeft) + this.mTempRect.left) + this.mTempRect.right) + this.mTouchSlop)) && y > ((float) (this.mSwitchTop - this.mTouchSlop)) && y < ((float) (this.mSwitchBottom + this.mTouchSlop));
    }

    public boolean onTouchEvent(MotionEvent ev) {
        this.mVelocityTracker.addMovement(ev);
        float x;
        float y;
        switch (ev.getActionMasked()) {
            case NativeRegExp.TEST /*0*/:
                x = ev.getX();
                y = ev.getY();
                if (isEnabled() && hitThumb(x, y)) {
                    this.mTouchMode = TOUCH_MODE_DOWN;
                    this.mTouchX = x;
                    this.mTouchY = y;
                    break;
                }
            case TOUCH_MODE_DOWN /*1*/:
            case MONOSPACE /*3*/:
                if (this.mTouchMode != TOUCH_MODE_DRAGGING) {
                    this.mTouchMode = 0;
                    this.mVelocityTracker.clear();
                    break;
                }
                stopDrag(ev);
                return true;
            case TOUCH_MODE_DRAGGING /*2*/:
                switch (this.mTouchMode) {
                    case NativeRegExp.TEST /*0*/:
                        break;
                    case TOUCH_MODE_DOWN /*1*/:
                        x = ev.getX();
                        y = ev.getY();
                        if (Math.abs(x - this.mTouchX) > ((float) this.mTouchSlop) || Math.abs(y - this.mTouchY) > ((float) this.mTouchSlop)) {
                            this.mTouchMode = TOUCH_MODE_DRAGGING;
                            getParent().requestDisallowInterceptTouchEvent(true);
                            this.mTouchX = x;
                            this.mTouchY = y;
                            return true;
                        }
                    case TOUCH_MODE_DRAGGING /*2*/:
                        x = ev.getX();
                        float newPos = Math.max(0.0f, Math.min(this.mThumbPosition + (x - this.mTouchX), (float) getThumbScrollRange()));
                        if (newPos == this.mThumbPosition) {
                            return true;
                        }
                        this.mThumbPosition = newPos;
                        this.mTouchX = x;
                        invalidate();
                        return true;
                    default:
                        break;
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void cancelSuperTouch(MotionEvent ev) {
        MotionEvent cancel = MotionEvent.obtain(ev);
        cancel.setAction(MONOSPACE);
        super.onTouchEvent(cancel);
        cancel.recycle();
    }

    private void stopDrag(MotionEvent ev) {
        boolean commitChange;
        this.mTouchMode = 0;
        if (ev.getAction() == TOUCH_MODE_DOWN && isEnabled()) {
            commitChange = true;
        } else {
            commitChange = false;
        }
        cancelSuperTouch(ev);
        if (commitChange) {
            boolean newState;
            this.mVelocityTracker.computeCurrentVelocity(1000);
            float xvel = this.mVelocityTracker.getXVelocity();
            if (Math.abs(xvel) <= ((float) this.mMinFlingVelocity)) {
                newState = getTargetCheckedState();
            } else if (xvel > 0.0f) {
                newState = true;
            } else {
                newState = false;
            }
            animateThumbToCheckedState(newState);
            return;
        }
        animateThumbToCheckedState(isChecked());
    }

    private void animateThumbToCheckedState(boolean newCheckedState) {
        setChecked(newCheckedState);
    }

    private boolean getTargetCheckedState() {
        return this.mThumbPosition >= ((float) (getThumbScrollRange() / TOUCH_MODE_DRAGGING));
    }

    private void setThumbPosition(boolean checked) {
        this.mThumbPosition = checked ? (float) getThumbScrollRange() : 0.0f;
    }

    public void setChecked(boolean checked) {
        super.setChecked(checked);
        setThumbPosition(isChecked());
        invalidate();
    }

    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int switchTop;
        int switchBottom;
        super.onLayout(changed, left, top, right, bottom);
        setThumbPosition(isChecked());
        int switchRight = getWidth() - getPaddingRight();
        int switchLeft = switchRight - this.mSwitchWidth;
        switch (getGravity() & Token.IMPORT) {
            case Token.GT /*16*/:
                switchTop = (((getPaddingTop() + getHeight()) - getPaddingBottom()) / TOUCH_MODE_DRAGGING) - (this.mSwitchHeight / TOUCH_MODE_DRAGGING);
                switchBottom = switchTop + this.mSwitchHeight;
                break;
            case Token.REF_NAME /*80*/:
                switchBottom = getHeight() - getPaddingBottom();
                switchTop = switchBottom - this.mSwitchHeight;
                break;
            default:
                switchTop = getPaddingTop();
                switchBottom = switchTop + this.mSwitchHeight;
                break;
        }
        this.mSwitchLeft = switchLeft;
        this.mSwitchTop = switchTop;
        this.mSwitchBottom = switchBottom;
        this.mSwitchRight = switchRight;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int switchLeft = this.mSwitchLeft;
        int switchTop = this.mSwitchTop;
        int switchRight = this.mSwitchRight;
        int switchBottom = this.mSwitchBottom;
        this.mTrackDrawable.setBounds(switchLeft, switchTop, switchRight, switchBottom);
        this.mTrackDrawable.draw(canvas);
        canvas.save();
        this.mTrackDrawable.getPadding(this.mTempRect);
        int switchInnerLeft = switchLeft + this.mTempRect.left;
        int switchInnerTop = switchTop + this.mTempRect.top;
        int switchInnerBottom = switchBottom - this.mTempRect.bottom;
        canvas.clipRect(switchInnerLeft, switchTop, switchRight - this.mTempRect.right, switchBottom);
        this.mThumbDrawable.getPadding(this.mTempRect);
        int thumbPos = (int) (this.mThumbPosition + 0.5f);
        int thumbLeft = (switchInnerLeft - this.mTempRect.left) + thumbPos;
        int thumbRight = ((switchInnerLeft + thumbPos) + this.mThumbWidth) + this.mTempRect.right;
        this.mThumbDrawable.setBounds(thumbLeft, switchTop, thumbRight, switchBottom);
        this.mThumbDrawable.draw(canvas);
        if (this.mTextColors != null) {
            this.mTextPaint.setColor(this.mTextColors.getColorForState(getDrawableState(), this.mTextColors.getDefaultColor()));
        }
        this.mTextPaint.drawableState = getDrawableState();
        Layout switchText = getTargetCheckedState() ? this.mOnLayout : this.mOffLayout;
        if (switchText != null) {
            canvas.translate((float) (((thumbLeft + thumbRight) / TOUCH_MODE_DRAGGING) - (switchText.getWidth() / TOUCH_MODE_DRAGGING)), (float) (((switchInnerTop + switchInnerBottom) / TOUCH_MODE_DRAGGING) - (switchText.getHeight() / TOUCH_MODE_DRAGGING)));
            switchText.draw(canvas);
        }
        canvas.restore();
    }

    public int getCompoundPaddingRight() {
        int padding = super.getCompoundPaddingRight() + this.mSwitchWidth;
        if (TextUtils.isEmpty(getText())) {
            return padding;
        }
        return padding + this.mSwitchPadding;
    }

    private int getThumbScrollRange() {
        if (this.mTrackDrawable == null) {
            return 0;
        }
        this.mTrackDrawable.getPadding(this.mTempRect);
        return ((this.mSwitchWidth - this.mThumbWidth) - this.mTempRect.left) - this.mTempRect.right;
    }

    protected int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + TOUCH_MODE_DOWN);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        int[] myDrawableState = getDrawableState();
        if (this.mThumbDrawable != null) {
            this.mThumbDrawable.setState(myDrawableState);
        }
        if (this.mTrackDrawable != null) {
            this.mTrackDrawable.setState(myDrawableState);
        }
        invalidate();
    }

    protected boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mThumbDrawable || who == this.mTrackDrawable;
    }
}
