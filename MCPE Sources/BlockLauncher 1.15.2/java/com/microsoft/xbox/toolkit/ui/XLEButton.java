package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.ButtonStateHandler.ButtonStateHandlerRunnable;

public class XLEButton extends Button {
    private boolean alwaysClickable;
    protected boolean disableSound;
    private int disabledTextColor;
    private int enabledTextColor;
    protected ButtonStateHandler stateHandler;

    public XLEButton(Context context) {
        super(context);
        this.stateHandler = new ButtonStateHandler();
        this.disableSound = false;
        setSoundEffectsEnabled(false);
    }

    public XLEButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XLEButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.stateHandler = new ButtonStateHandler();
        this.disableSound = false;
        if (!isInEditMode()) {
            setSoundEffectsEnabled(false);
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("XLEButton"));
            try {
                this.stateHandler.setDisabled(obtainStyledAttributes.getBoolean(XLERValueHelper.getStyleableRValue("XLEButton_disabled"), false));
                this.stateHandler.setDisabledImageHandle(obtainStyledAttributes.getResourceId(XLERValueHelper.getStyleableRValue("XLEButton_disabledImage"), -1));
                this.stateHandler.setEnabledImageHandle(obtainStyledAttributes.getResourceId(XLERValueHelper.getStyleableRValue("XLEButton_enabledImage"), -1));
                this.stateHandler.setPressedImageHandle(obtainStyledAttributes.getResourceId(XLERValueHelper.getStyleableRValue("XLEButton_pressedImage"), -1));
                this.disableSound = obtainStyledAttributes.getBoolean(XLERValueHelper.getStyleableRValue("XLEButton_disableSound"), false);
                setLayoutParams(new LayoutParams(-2, -2));
                TypedArray obtainStyledAttributes2 = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("CustomTypeface"));
                String string = obtainStyledAttributes2.getString(XLERValueHelper.getStyleableRValue("CustomTypeface_typefaceSource"));
                obtainStyledAttributes2.recycle();
                if (string != null && string.length() > 0) {
                    applyCustomTypeface(context, string);
                }
                this.enabledTextColor = getCurrentTextColor();
                this.disabledTextColor = obtainStyledAttributes.getColor(XLERValueHelper.getStyleableRValue("XLEButton_disabledTextColor"), this.enabledTextColor);
                this.alwaysClickable = obtainStyledAttributes.getBoolean(XLERValueHelper.getStyleableRValue("XLEButton_alwaysClickable"), false);
                if (this.alwaysClickable) {
                    super.setEnabled(true);
                    super.setClickable(true);
                }
                obtainStyledAttributes.recycle();
            } catch (Throwable th) {
                obtainStyledAttributes.recycle();
            }
        }
    }

    private void applyCustomTypeface(Context context, String str) {
        if (str != null) {
            setTypeface(FontManager.Instance().getTypeface(getContext(), str));
        }
    }

    private boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    protected void onFinishInflate() {
        updateImage();
        setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean onTouch = XLEButton.this.stateHandler.onTouch(motionEvent);
                XLEButton.this.updateImage();
                return onTouch;
            }
        });
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        boolean z = false;
        if (hasSize()) {
            z = this.stateHandler.onSizeChanged(getWidth(), getHeight());
        }
        if (z) {
            updateImage();
        }
    }

    public void setEnabled(boolean z) {
        if (!this.alwaysClickable) {
            super.setEnabled(z);
        }
        if (this.stateHandler == null) {
            this.stateHandler = new ButtonStateHandler();
        }
        this.stateHandler.setEnabled(z);
        updateImage();
        updateTextColor();
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        if (this.disableSound) {
            super.setOnClickListener(onClickListener);
        } else {
            super.setOnClickListener(TouchUtil.createOnClickListener(onClickListener));
        }
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        if (this.disableSound) {
            super.setOnLongClickListener(onLongClickListener);
        } else {
            super.setOnLongClickListener(TouchUtil.createOnLongClickListener(onLongClickListener));
        }
    }

    public void setPressedStateRunnable(ButtonStateHandlerRunnable buttonStateHandlerRunnable) {
        this.stateHandler.setPressedStateRunnable(buttonStateHandlerRunnable);
    }

    public void setTypeFace(String str) {
        applyCustomTypeface(getContext(), str);
    }

    protected void updateImage() {
        if (this.stateHandler.getImageDrawable() != null) {
            setBackgroundDrawable(this.stateHandler.getImageDrawable());
        }
    }

    protected void updateTextColor() {
        if (this.enabledTextColor != this.disabledTextColor) {
            setTextColor(this.stateHandler.getDisabled() ? this.disabledTextColor : this.enabledTextColor);
        }
    }
}
