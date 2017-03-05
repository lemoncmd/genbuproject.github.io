package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.CustomTypefaceTextView;
import com.microsoft.xbox.xle.app.XLEUtil;
import com.microsoft.xboxtcui.R;

public class IconFontSubTextButton extends LinearLayout {
    private FrameLayout iconFrameLayout;
    private CustomTypefaceTextView iconTextView;
    private CustomTypefaceTextView subtitleTextView;
    private CustomTypefaceTextView titleTextView;

    public IconFontSubTextButton(Context context) {
        this(context, null);
    }

    public IconFontSubTextButton(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public IconFontSubTextButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        LayoutInflater.from(context).inflate(R.layout.icon_font_subtext_button, this, true);
        this.iconTextView = (CustomTypefaceTextView) findViewById(R.id.icon_font_subtext_icon);
        this.iconFrameLayout = (FrameLayout) findViewById(R.id.icon_font_subtext_btn_icon_bg);
        this.titleTextView = (CustomTypefaceTextView) findViewById(R.id.icon_font_subtext_btn_title);
        this.subtitleTextView = (CustomTypefaceTextView) findViewById(R.id.icon_font_subtext_btn_subtitle);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("IconFontSubTextButton"));
        CharSequence string = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("IconFontSubTextButton_icon_uri"));
        CharSequence string2 = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("IconFontSubTextButton_text_title"));
        CharSequence string3 = obtainStyledAttributes.getString(XLERValueHelper.getStyleableRValue("IconFontSubTextButton_text_subtitle"));
        this.iconFrameLayout.setBackgroundColor(obtainStyledAttributes.getColor(XLERValueHelper.getStyleableRValue("IconFontSubTextButton_icon_bg"), 0));
        obtainStyledAttributes.recycle();
        XLEUtil.updateTextAndVisibilityIfNotNull(this.iconTextView, string, 0);
        XLEUtil.updateTextAndVisibilityIfNotNull(this.titleTextView, string2, 0);
        XLEUtil.updateTextAndVisibilityIfNotNull(this.subtitleTextView, string3, 0);
        setFocusable(true);
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClickable(true);
        accessibilityNodeInfo.setClassName(Button.class.getName());
    }
}
