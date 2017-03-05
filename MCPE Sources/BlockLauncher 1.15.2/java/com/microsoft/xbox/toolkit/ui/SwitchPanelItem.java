package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import com.microsoft.xbox.toolkit.ui.SwitchPanel.SwitchPanelChild;

public class SwitchPanelItem extends FrameLayout implements SwitchPanelChild {
    private final int INVALID_STATE_ID = -1;
    private int state;

    public SwitchPanelItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("SwitchPanelItem"));
        this.state = obtainStyledAttributes.getInteger(XLERValueHelper.getStyleableRValue("SwitchPanelItem_state"), -1);
        obtainStyledAttributes.recycle();
        if (this.state < 0) {
            throw new IllegalArgumentException("You must specify the state attribute in the xml, and the value must be positive.");
        }
        setLayoutParams(new LayoutParams(-1, -1));
    }

    public int getState() {
        return this.state;
    }
}
