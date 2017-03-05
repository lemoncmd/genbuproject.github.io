package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.FontManager;
import com.microsoft.xboxtcui.R;

public class IconFontToggleButton extends LinearLayout implements Checkable {
    private boolean checked;
    private String checkedIcon;
    private String checkedText;
    private TextView iconTextView;
    private TextView labelTextView;
    private String uncheckedIcon;
    private String uncheckedText;

    public IconFontToggleButton(Context context) {
        super(context);
    }

    public IconFontToggleButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initViews(context, attributeSet);
    }

    public IconFontToggleButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initViews(context, attributeSet);
    }

    private void applyCustomTypeface(Context context, String str) {
        if (str != null && this.labelTextView != null) {
            this.labelTextView.setTypeface(FontManager.Instance().getTypeface(getContext(), str));
        }
    }

    private void initViews(Context context, AttributeSet attributeSet) {
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.iconfont_toggle_btn_view, this, true);
        this.iconTextView = (TextView) findViewById(R.id.iconfont_toggle_btn_icon);
        this.labelTextView = (TextView) findViewById(R.id.iconfont_toggle_btn_text);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("CustomTypeface"));
        String string = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("CustomTypeface_typefaceSource"));
        obtainStyledAttributes.recycle();
        obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("IconFontToggleButton"));
        this.checkedText = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_text_checked"));
        this.uncheckedText = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_text_unchecked"));
        this.checkedIcon = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_checked"));
        this.uncheckedIcon = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_unchecked"));
        float dimensionPixelSize = (float) obtainStyledAttributes.getDimensionPixelSize(XLERValueHelper.getStyleableRValue("IconFontToggleButton_icon_size"), -1);
        if (dimensionPixelSize != -1.0f) {
            this.iconTextView.setTextSize(0, dimensionPixelSize);
        }
        obtainStyledAttributes.recycle();
        applyCustomTypeface(context, string);
        setFocusable(true);
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClickable(true);
        accessibilityNodeInfo.setClassName(Button.class.getName());
    }

    public void setChecked(boolean z) {
        this.checked = z;
        sendAccessibilityEvent(1);
        if (this.labelTextView != null) {
            this.labelTextView.setText(this.checked ? this.checkedText : this.uncheckedText);
            this.labelTextView.setVisibility(0);
        }
        if (this.iconTextView != null) {
            this.iconTextView.setText(this.checked ? this.checkedIcon : this.uncheckedIcon);
            this.iconTextView.setVisibility(0);
        }
        invalidate();
    }

    public void setCheckedText(String str) {
        this.checkedText = str;
    }

    public void setUncheckedText(String str) {
        this.uncheckedText = str;
    }

    public void toggle() {
        setChecked(!this.checked);
    }
}
