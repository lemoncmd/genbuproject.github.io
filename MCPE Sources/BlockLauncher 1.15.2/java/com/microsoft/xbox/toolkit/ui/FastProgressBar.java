package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class FastProgressBar extends ProgressBar {
    private boolean isEnabled;
    private int visibility;

    public FastProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setEnabled(true);
        setVisibility(0);
    }

    protected void onDraw(Canvas canvas) {
        synchronized (this) {
            super.onDraw(canvas);
            postInvalidateDelayed(33);
        }
    }

    public void setEnabled(boolean z) {
        if (this.isEnabled != z) {
            this.isEnabled = z;
            if (this.isEnabled) {
                super.setVisibility(this.visibility);
                return;
            }
            this.visibility = getVisibility();
            super.setVisibility(8);
        }
    }

    public void setVisibility(int i) {
        if (this.isEnabled) {
            super.setVisibility(i);
        } else {
            this.visibility = i;
        }
    }
}
