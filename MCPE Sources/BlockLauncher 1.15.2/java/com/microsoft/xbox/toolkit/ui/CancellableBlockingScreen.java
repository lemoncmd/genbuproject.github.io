package com.microsoft.xbox.toolkit.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import com.microsoft.xbox.toolkit.XLERValueHelper;

public class CancellableBlockingScreen extends Dialog {
    private XLEButton cancelButton = ((XLEButton) findViewById(XLERValueHelper.getIdRValue("blocking_dialog_cancel")));
    private View container = findViewById(XLERValueHelper.getIdRValue("blocking_dialog_container"));
    private TextView statusText = ((TextView) findViewById(XLERValueHelper.getIdRValue("blocking_dialog_status_text")));

    public CancellableBlockingScreen(Context context) {
        super(context, XLERValueHelper.getStyleRValue("cancellable_dialog_style"));
        setCancelable(false);
        setOnCancelListener(null);
        requestWindowFeature(1);
        setContentView(XLERValueHelper.getLayoutRValue("cancellable_blocking_dialog"));
    }

    public void setCancelButtonAction(OnClickListener onClickListener) {
        if (onClickListener != null) {
            this.cancelButton.setOnClickListener(null);
        }
        this.cancelButton.setOnClickListener(onClickListener);
    }

    public void setMessage(CharSequence charSequence) {
        this.statusText.setText(charSequence);
    }

    public void show(Context context, CharSequence charSequence) {
        boolean isShowing = isShowing();
        setMessage(charSequence);
        show();
        if (!isShowing) {
            Animation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            alphaAnimation.setFillAfter(true);
            alphaAnimation.setStartOffset(1000);
            alphaAnimation.setDuration(1000);
            this.container.startAnimation(alphaAnimation);
        }
    }
}
