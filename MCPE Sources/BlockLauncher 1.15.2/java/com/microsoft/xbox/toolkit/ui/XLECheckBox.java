package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.ui.util.LibCompat;
import com.microsoft.xboxtcui.R;

public class XLECheckBox extends ViewGroup {
    private final CheckBox checkBox;
    private final TextView subText;
    private final TextView text;

    public XLECheckBox(Context context) {
        super(context);
        this.checkBox = new CheckBox(context);
        this.text = new TextView(context);
        this.subText = new TextView(context);
    }

    public XLECheckBox(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.checkBox = new CheckBox(context, attributeSet);
        this.text = new TextView(context, attributeSet);
        this.subText = new TextView(context, attributeSet);
        initialize(context, attributeSet);
    }

    public XLECheckBox(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.checkBox = new CheckBox(context, attributeSet);
        this.text = new TextView(context, attributeSet);
        this.subText = new TextView(context, attributeSet);
        initialize(context, attributeSet);
    }

    private void initialize(Context context, AttributeSet attributeSet) {
        this.checkBox.setButtonDrawable(R.drawable.apptheme_btn_check_holo_light);
        addView(this.checkBox, new LayoutParams(-2, -2));
        this.text.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                XLECheckBox.this.checkBox.toggle();
            }
        });
        addView(this.text, new LayoutParams(-2, -2));
        addView(this.subText, new LayoutParams(-2, -2));
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XLECheckBox);
        try {
            if (!isInEditMode()) {
                LibCompat.setTextAppearance(this.text, obtainStyledAttributes.getResourceId(R.styleable.XLECheckBox_textStyle, -1));
                this.text.setTypeface(FontManager.Instance().getTypeface(context, obtainStyledAttributes.getString(R.styleable.XLECheckBox_textTypefaceSource)));
                LibCompat.setTextAppearance(this.subText, obtainStyledAttributes.getResourceId(R.styleable.XLECheckBox_subTextStyle, -1));
                this.subText.setTypeface(FontManager.Instance().getTypeface(context, obtainStyledAttributes.getString(R.styleable.XLECheckBox_subTextTypefaceSource)));
            }
            this.text.setText(obtainStyledAttributes.getString(R.styleable.XLECheckBox_text));
            this.subText.setText(obtainStyledAttributes.getString(R.styleable.XLECheckBox_subText));
        } finally {
            obtainStyledAttributes.recycle();
        }
    }

    public CharSequence getSubText() {
        return this.subText.getText();
    }

    public CharSequence getText() {
        return this.text.getText();
    }

    public boolean isChecked() {
        return this.checkBox.isChecked();
    }

    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop() + Math.max(this.checkBox.getMeasuredHeight() / 2, this.text.getMeasuredHeight() / 2);
        int measuredWidth = paddingTop - (this.checkBox.getMeasuredWidth() / 2);
        this.checkBox.layout(paddingLeft, measuredWidth, this.checkBox.getMeasuredWidth() + paddingLeft, this.checkBox.getMeasuredHeight() + measuredWidth);
        paddingLeft += this.checkBox.getMeasuredWidth();
        paddingTop -= this.text.getMeasuredHeight() / 2;
        this.text.layout(paddingLeft, paddingTop, this.text.getMeasuredWidth() + paddingLeft, this.text.getMeasuredHeight() + paddingTop);
        paddingTop += this.text.getMeasuredHeight();
        this.subText.layout(paddingLeft, paddingTop, this.subText.getMeasuredWidth() + paddingLeft, this.subText.getMeasuredHeight() + paddingTop);
    }

    protected void onMeasure(int i, int i2) {
        int i3 = Integer.MIN_VALUE;
        int size = MeasureSpec.getSize(i);
        int mode = MeasureSpec.getMode(i);
        int i4 = mode == 0 ? 0 : Integer.MIN_VALUE;
        int size2 = MeasureSpec.getSize(i2);
        int mode2 = MeasureSpec.getMode(i2);
        if (mode2 == 0) {
            i3 = 0;
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        this.checkBox.measure(MeasureSpec.makeMeasureSpec(Math.max((size - paddingLeft) - getPaddingRight(), 0), i4), MeasureSpec.makeMeasureSpec(Math.max((size2 - paddingTop) - getPaddingBottom(), 0), i3));
        paddingLeft += this.checkBox.getMeasuredWidth();
        this.text.measure(MeasureSpec.makeMeasureSpec(Math.max((size - paddingLeft) - getPaddingRight(), 0), i4), MeasureSpec.makeMeasureSpec(Math.max((size2 - paddingTop) - getPaddingBottom(), 0), i3));
        paddingTop += Math.max(this.checkBox.getMeasuredHeight(), this.text.getMeasuredHeight());
        this.subText.measure(MeasureSpec.makeMeasureSpec(Math.max((size - paddingLeft) - getPaddingRight(), 0), i4), MeasureSpec.makeMeasureSpec(Math.max((size2 - paddingTop) - getPaddingBottom(), 0), i3));
        int paddingRight = getPaddingRight() + (Math.max(this.text.getMeasuredWidth(), this.subText.getMeasuredWidth()) + paddingLeft);
        i4 = getPaddingBottom() + (paddingTop + this.subText.getMeasuredHeight());
        if (mode != 0) {
            paddingRight = Math.min(paddingRight, size);
        }
        if (mode2 != 0) {
            i4 = Math.min(i4, size2);
        }
        setMeasuredDimension(paddingRight, i4);
    }

    public void setChecked(boolean z) {
        this.checkBox.setChecked(z);
    }

    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.checkBox.setEnabled(z);
        this.text.setEnabled(z);
        this.subText.setEnabled(z);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void setSubText(CharSequence charSequence) {
        this.subText.setText(charSequence);
    }

    public void setText(CharSequence charSequence) {
        this.text.setText(charSequence);
    }

    public void toggle() {
        this.checkBox.toggle();
    }
}
