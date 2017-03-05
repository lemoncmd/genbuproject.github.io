package com.microsoft.xbox.toolkit.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor.WaitType;
import com.microsoft.xbox.toolkit.XLERValueHelper;

public class SwitchPanel extends LinearLayout {
    private static final int LAYOUT_BLOCK_TIMEOUT_MS = 150;
    private AnimatorListenerAdapter AnimateInListener = new AnimatorListenerAdapter() {
        public void onAnimationCancel(Animator animator) {
            SwitchPanel.this.onAnimateInEnd();
        }

        public void onAnimationEnd(Animator animator) {
            SwitchPanel.this.onAnimateInEnd();
        }

        public void onAnimationStart(Animator animator) {
            SwitchPanel.this.onAnimateInStart();
        }
    };
    private AnimatorListenerAdapter AnimateOutListener = new AnimatorListenerAdapter() {
        public void onAnimationCancel(Animator animator) {
            SwitchPanel.this.onAnimateOutEnd();
        }

        public void onAnimationEnd(Animator animator) {
            SwitchPanel.this.onAnimateOutEnd();
        }

        public void onAnimationStart(Animator animator) {
            SwitchPanel.this.onAnimateOutStart();
        }
    };
    private final int INVALID_STATE_ID = -1;
    private final int VALID_CONTENT_STATE = 0;
    private boolean active = false;
    private boolean blocking = false;
    private View newView = null;
    private View oldView = null;
    private int selectedState;
    private boolean shouldAnimate = true;

    public interface SwitchPanelChild {
        int getState();
    }

    public SwitchPanel(Context context) {
        super(context);
        throw new UnsupportedOperationException();
    }

    public SwitchPanel(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("SwitchPanel"));
        this.selectedState = obtainStyledAttributes.getInteger(XLERValueHelper.getStyleableRValue("SwitchPanel_selectedState"), -1);
        obtainStyledAttributes.recycle();
        if (this.selectedState < 0) {
            throw new IllegalArgumentException("You must specify the selectedState attribute in the xml, and the value must be positive.");
        }
        setLayoutParams(new LayoutParams(-1, -1));
    }

    private void onAnimateInEnd() {
        setBlocking(false);
        if (this.newView != null) {
            this.newView.setLayerType(0, null);
        }
    }

    private void onAnimateInStart() {
        if (this.newView != null) {
            this.newView.setLayerType(2, null);
            setBlocking(true);
        }
    }

    private void onAnimateOutEnd() {
        if (this.oldView != null) {
            this.oldView.setVisibility(8);
            this.oldView.setLayerType(0, null);
        }
    }

    private void onAnimateOutStart() {
        if (this.oldView != null) {
            this.oldView.setLayerType(2, null);
            setBlocking(true);
        }
    }

    private void updateVisibility(int i, int i2) {
        int childCount = getChildCount();
        int i3 = 0;
        while (i3 < childCount) {
            View childAt = getChildAt(i3);
            if (childAt instanceof SwitchPanelChild) {
                int state = ((SwitchPanelChild) childAt).getState();
                if (state == i) {
                    this.oldView = childAt;
                } else if (state == i2) {
                    this.newView = childAt;
                } else {
                    childAt.setVisibility(8);
                }
                i3++;
            } else {
                throw new UnsupportedOperationException("All children of SwitchPanel must implement the SwitchPanelChild interface. All other types are not supported and should be removed.");
            }
        }
        if (this.shouldAnimate && i2 == 0 && this.newView != null) {
            this.newView.setAlpha(0.0f);
            this.newView.setVisibility(0);
            requestLayout();
            if (this.oldView != null) {
                this.oldView.animate().alpha(0.0f).setDuration(150).setListener(this.AnimateOutListener);
            }
            this.newView.animate().alpha(1.0f).setDuration(150).setListener(this.AnimateInListener);
            return;
        }
        if (this.oldView != null) {
            this.oldView.setVisibility(8);
        }
        if (this.newView != null) {
            this.newView.setAlpha(1.0f);
            this.newView.setVisibility(0);
        }
        requestLayout();
    }

    public int getState() {
        return this.selectedState;
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        updateVisibility(-1, this.selectedState);
    }

    public void setActive(boolean z) {
        this.active = z;
    }

    public void setBlocking(boolean z) {
        if (this.blocking != z) {
            this.blocking = z;
            if (this.blocking) {
                BackgroundThreadWaitor.getInstance().setBlocking(WaitType.ListLayout, LAYOUT_BLOCK_TIMEOUT_MS);
            } else {
                BackgroundThreadWaitor.getInstance().clearBlocking(WaitType.ListLayout);
            }
        }
    }

    public void setShouldAnimate(boolean z) {
        this.shouldAnimate = z;
    }

    public void setState(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("New state must be a positive value.");
        } else if (this.selectedState != i) {
            int i2 = this.selectedState;
            this.selectedState = i;
            updateVisibility(i2, i);
        }
    }
}
