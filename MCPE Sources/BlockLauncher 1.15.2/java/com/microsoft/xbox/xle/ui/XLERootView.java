package com.microsoft.xbox.xle.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;
import com.microsoft.xbox.toolkit.system.SystemUtil;
import com.microsoft.xboxtcui.R;

public class XLERootView extends RelativeLayout {
    private static final int UNASSIGNED_ACTIVITY_BODY_ID = -1;
    private View activityBody;
    private int activityBodyIndex;
    private String headerName;
    private boolean isTopLevel = false;
    private long lastFps = 0;
    private long lastMs = 0;
    private int origPaddingBottom;
    private boolean showTitleBar = true;

    public XLERootView(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public XLERootView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XLERootView);
        if (obtainStyledAttributes != null) {
            try {
                this.activityBodyIndex = obtainStyledAttributes.getResourceId(R.styleable.XLERootView_activityBody, UNASSIGNED_ACTIVITY_BODY_ID);
                this.isTopLevel = obtainStyledAttributes.getBoolean(R.styleable.XLERootView_isTopLevel, false);
                this.showTitleBar = obtainStyledAttributes.getBoolean(R.styleable.XLERootView_showTitleBar, true);
                int i = obtainStyledAttributes.getInt(R.styleable.XLERootView_minScreenPercent, Integer.MIN_VALUE);
                if (i != Integer.MIN_VALUE) {
                    setMinimumWidth((Math.max(0, i) * SystemUtil.getScreenWidth()) / 100);
                }
                this.headerName = obtainStyledAttributes.getString(R.styleable.XLERootView_headerName);
            } finally {
                obtainStyledAttributes.recycle();
            }
        }
    }

    private void initialize() {
        if (this.activityBodyIndex != UNASSIGNED_ACTIVITY_BODY_ID) {
            this.activityBody = findViewById(this.activityBodyIndex);
        } else {
            this.activityBody = this;
        }
        this.origPaddingBottom = getPaddingBottom();
        if (this.activityBody != null && this.activityBody != this) {
            LayoutParams layoutParams = this.activityBody.getLayoutParams();
            LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(layoutParams);
            layoutParams2.width = UNASSIGNED_ACTIVITY_BODY_ID;
            layoutParams2.height = UNASSIGNED_ACTIVITY_BODY_ID;
            layoutParams2.addRule(10);
            if (layoutParams instanceof MarginLayoutParams) {
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams;
                setPadding(getPaddingLeft() + marginLayoutParams.leftMargin, getPaddingTop() + marginLayoutParams.topMargin, getPaddingRight() + marginLayoutParams.rightMargin, marginLayoutParams.bottomMargin + this.origPaddingBottom);
                layoutParams2.setMargins(0, 0, 0, 0);
            }
            removeView(this.activityBody);
            addView(this.activityBody, layoutParams2);
        }
    }

    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public String getHeaderName() {
        return this.headerName;
    }

    public boolean getIsTopLevel() {
        return this.isTopLevel;
    }

    public boolean getShowTitleBar() {
        return this.showTitleBar;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        initialize();
    }

    public void setBottomMargin(int i) {
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), this.origPaddingBottom + i);
    }
}
