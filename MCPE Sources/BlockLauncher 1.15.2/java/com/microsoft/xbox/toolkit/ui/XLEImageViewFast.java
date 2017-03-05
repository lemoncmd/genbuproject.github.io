package com.microsoft.xbox.toolkit.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.XLERValueHelper;
import java.net.URI;

public class XLEImageViewFast extends XLEImageView {
    private TextureBindingOption option;
    protected int pendingBitmapResourceId = -1;
    private String pendingFilePath = null;
    protected URI pendingUri = null;
    private boolean useFileCache = true;

    public XLEImageViewFast(Context context) {
        super(context);
        setSoundEffectsEnabled(false);
    }

    public XLEImageViewFast(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (!isInEditMode()) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, XLERValueHelper.getStyleableRValueArray("XLEImageViewFast"));
            setImageResource(obtainStyledAttributes.getResourceId(XLERValueHelper.getStyleableRValue("XLEImageViewFast_src"), -1));
            obtainStyledAttributes.recycle();
            setSoundEffectsEnabled(false);
        }
    }

    private void bindToFilePath(String str) {
        this.pendingFilePath = null;
        TextureManager.Instance().bindToViewFromFile(str, this, getWidth(), getHeight());
    }

    private void bindToResourceId(int i) {
        this.pendingBitmapResourceId = -1;
        TextureManager.Instance().bindToView(i, (ImageView) this, getWidth(), getHeight());
    }

    private void bindToUri(URI uri, TextureBindingOption textureBindingOption) {
        this.pendingUri = null;
        this.option = null;
        TextureManager.Instance().bindToView(uri, this, textureBindingOption);
    }

    protected void bindToUri(URI uri) {
        this.pendingUri = null;
        bindToUri(uri, new TextureBindingOption(getWidth(), getHeight(), this.useFileCache));
    }

    protected boolean hasSize() {
        return getWidth() > 0 && getHeight() > 0;
    }

    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(resolveSize(0, i), resolveSize(0, i2));
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (hasSize()) {
            if (this.pendingBitmapResourceId >= 0) {
                bindToResourceId(this.pendingBitmapResourceId);
            }
            if (this.pendingUri != null || (this.pendingUri == null && this.option != null)) {
                if (this.option != null) {
                    bindToUri(this.pendingUri, new TextureBindingOption(getWidth(), getHeight(), this.option.resourceIdForLoading, this.option.resourceIdForError, this.option.useFileCache));
                } else {
                    bindToUri(this.pendingUri);
                }
            }
            if (this.pendingFilePath != null) {
                bindToFilePath(this.pendingFilePath);
            }
        }
    }

    public void setImageFilePath(String str) {
        if (hasSize()) {
            bindToFilePath(str);
        } else {
            this.pendingFilePath = str;
        }
    }

    public void setImageResource(int i) {
        if (hasSize()) {
            bindToResourceId(i);
        } else {
            this.pendingBitmapResourceId = i;
        }
    }

    public void setImageURI(Uri uri) {
        throw new UnsupportedOperationException();
    }

    public void setImageURI2(URI uri) {
        if (hasSize()) {
            bindToUri(uri);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, int i, int i2) {
        this.option = new TextureBindingOption(getWidth(), getHeight(), i, i2, this.useFileCache);
        if (hasSize()) {
            bindToUri(uri, this.option);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setImageURI2(URI uri, boolean z) {
        this.useFileCache = z;
        this.option = new TextureBindingOption(getWidth(), getHeight(), this.useFileCache);
        if (hasSize()) {
            bindToUri(uri, this.option);
        } else {
            this.pendingUri = uri;
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        super.setOnClickListener(TouchUtil.createOnClickListener(onClickListener));
    }
}
