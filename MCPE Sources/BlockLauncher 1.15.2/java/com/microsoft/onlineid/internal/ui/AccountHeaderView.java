package com.microsoft.onlineid.internal.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import org.mozilla.javascript.Token;

public class AccountHeaderView {
    public static final float MarginLargeDip = 16.0f;
    public static final float MarginLogoDip = 9.3f;
    public static final float MarginMediumDip = 8.0f;
    public static final float SizeLogoDip = 32.0f;
    public static final int TextColorTitle = Color.rgb(88, 88, 88);
    public static final float TextSizeLargeSP = 16.0f;

    private static class CustomTypefaceSpan extends MetricAffectingSpan {
        private Typeface _typeface;

        public CustomTypefaceSpan(Typeface typeface) {
            this._typeface = typeface;
        }

        public void updateDrawState(TextPaint textPaint) {
            textPaint.setTypeface(this._typeface);
            textPaint.setFlags(textPaint.getFlags() | Token.RESERVED);
        }

        public void updateMeasureState(TextPaint textPaint) {
            textPaint.setTypeface(this._typeface);
            textPaint.setFlags(textPaint.getFlags() | Token.RESERVED);
        }
    }

    public static void applyStyle(Activity activity, CharSequence charSequence) {
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            Object charSequence2 = charSequence.toString();
            CharSequence spannableString = new SpannableString(charSequence2);
            spannableString.setSpan(new CustomTypefaceSpan(Fonts.SegoeUISemiBold.getTypeface(activity.getApplicationContext())), TextColorTitle, charSequence2.length(), 18);
            actionBar.setTitle(spannableString);
            View findViewById = activity.findViewById(16908332);
            findViewById.setPadding(TextColorTitle, TextColorTitle, TextColorTitle, TextColorTitle);
            int convertDipToPixels = Dimensions.convertDipToPixels(SizeLogoDip, activity.getResources().getDisplayMetrics());
            LayoutParams layoutParams = findViewById.getLayoutParams();
            layoutParams.height = convertDipToPixels;
            layoutParams.width = convertDipToPixels;
        }
    }
}
