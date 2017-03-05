package com.microsoft.xbox.toolkit.ui;

import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import com.microsoft.xbox.toolkit.ui.XLEBitmap.XLEBitmapDrawable;

public class ButtonStateHandler {
    protected boolean disabled = false;
    private XLEBitmapDrawable disabledImage = null;
    private int disabledImageHandle = -1;
    private XLEBitmapDrawable enabledImage = null;
    private int enabledImageHandle = -1;
    protected boolean pressed = false;
    private XLEBitmapDrawable pressedImage = null;
    private int pressedImageHandle = -1;
    private ButtonStateHandlerRunnable pressedStateRunnable = null;

    public interface ButtonStateHandlerRunnable {
        void onPressStateChanged(boolean z);
    }

    public boolean getDisabled() {
        return this.disabled;
    }

    public Drawable getImageDrawable() {
        return (!this.pressed || this.pressedImageHandle == -1) ? (!this.disabled || this.disabledImageHandle == -1) ? (this.enabledImageHandle == -1 || this.enabledImage == null) ? null : this.enabledImage.getDrawable() : this.disabledImage != null ? this.disabledImage.getDrawable() : null : this.pressedImage == null ? null : this.pressedImage.getDrawable();
    }

    public boolean onSizeChanged(int i, int i2) {
        boolean z = false;
        if (this.disabledImage == null && this.disabledImageHandle != -1) {
            this.disabledImage = TextureManager.Instance().loadScaledResourceDrawable(this.disabledImageHandle);
            z = true;
        }
        if (this.enabledImage == null && this.enabledImageHandle != -1) {
            this.enabledImage = TextureManager.Instance().loadScaledResourceDrawable(this.enabledImageHandle);
            z = true;
        }
        if (this.pressedImage != null || this.pressedImageHandle == -1) {
            return z;
        }
        this.pressedImage = TextureManager.Instance().loadScaledResourceDrawable(this.pressedImageHandle);
        return true;
    }

    public boolean onTouch(MotionEvent motionEvent) {
        boolean z = this.pressed;
        if (motionEvent.getAction() == 0) {
            this.pressed = true;
        } else if (motionEvent.getAction() == 1) {
            this.pressed = false;
        } else if (motionEvent.getAction() == 3) {
            this.pressed = false;
        }
        if (!(this.pressedStateRunnable == null || z == this.pressed)) {
            this.pressedStateRunnable.onPressStateChanged(this.pressed);
        }
        return false;
    }

    public void setDisabled(boolean z) {
        this.disabled = z;
    }

    public void setDisabledImageHandle(int i) {
        this.disabledImageHandle = i;
    }

    public void setEnabled(boolean z) {
        this.disabled = !z;
    }

    public void setEnabledImageHandle(int i) {
        this.enabledImageHandle = i;
    }

    public void setPressedImageHandle(int i) {
        this.pressedImageHandle = i;
    }

    public void setPressedStateRunnable(ButtonStateHandlerRunnable buttonStateHandlerRunnable) {
        this.pressedStateRunnable = buttonStateHandlerRunnable;
    }
}
