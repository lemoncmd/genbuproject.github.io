package com.microsoft.onlineid.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;
import com.microsoft.onlineid.internal.ui.Fonts;
import com.microsoft.onlineid.sdk.R;

public class StyledTextView extends TextView {
    public StyledTextView(Context context) {
        super(context);
    }

    public StyledTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        applyAttributes(context, attributeSet);
    }

    public StyledTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        applyAttributes(context, attributeSet);
    }

    private void applyAttributes(Context context, AttributeSet attributeSet) {
        TypedArray obtainStyledAttributes = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.StyledTextView, 0, 0);
        for (int i = 0; i < obtainStyledAttributes.getIndexCount(); i++) {
            int index = obtainStyledAttributes.getIndex(i);
            if (index == R.styleable.StyledTextView_font) {
                if (!isInEditMode()) {
                    String string = obtainStyledAttributes.getString(index);
                    if (string != null) {
                        setTypeface(Fonts.valueOf(string).getTypeface(context));
                    }
                }
            } else if (index == R.styleable.StyledTextView_isUnderlined) {
                if (obtainStyledAttributes.getBoolean(index, false)) {
                    setPaintFlags(getPaintFlags() | 8);
                } else {
                    setPaintFlags(getPaintFlags() & -9);
                }
            }
        }
        obtainStyledAttributes.recycle();
    }
}
