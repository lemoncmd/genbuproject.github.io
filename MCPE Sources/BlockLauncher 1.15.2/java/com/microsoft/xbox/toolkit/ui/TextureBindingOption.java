package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.toolkit.XLERValueHelper;

public class TextureBindingOption {
    public static final int DO_NOT_SCALE = -1;
    public static final int DO_NOT_USE_PLACEHOLDER = -1;
    public static final TextureBindingOption DefaultBindingOption = new TextureBindingOption();
    public static final int DefaultResourceIdForEmpty = XLERValueHelper.getDrawableRValue("empty");
    public static final int DefaultResourceIdForError = XLERValueHelper.getDrawableRValue("error");
    public static final int DefaultResourceIdForLoading = XLERValueHelper.getDrawableRValue("empty");
    public static final TextureBindingOption KeepAsIsBindingOption = new TextureBindingOption(DO_NOT_USE_PLACEHOLDER, DO_NOT_USE_PLACEHOLDER, DO_NOT_USE_PLACEHOLDER, DO_NOT_USE_PLACEHOLDER, false);
    public final int height;
    public final int resourceIdForError;
    public final int resourceIdForLoading;
    public final boolean useFileCache;
    public final int width;

    public TextureBindingOption() {
        this(DO_NOT_USE_PLACEHOLDER, DO_NOT_USE_PLACEHOLDER, DefaultResourceIdForLoading, DefaultResourceIdForError, false);
    }

    public TextureBindingOption(int i, int i2) {
        this(i, i2, true);
    }

    public TextureBindingOption(int i, int i2, int i3, int i4, boolean z) {
        this.width = i;
        this.height = i2;
        this.resourceIdForLoading = i3;
        this.resourceIdForError = i4;
        this.useFileCache = z;
    }

    public TextureBindingOption(int i, int i2, boolean z) {
        this(i, i2, DefaultResourceIdForLoading, DefaultResourceIdForError, z);
    }

    public static TextureBindingOption createDoNotScale(int i, int i2, boolean z) {
        return new TextureBindingOption(DO_NOT_USE_PLACEHOLDER, DO_NOT_USE_PLACEHOLDER, i, i2, z);
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (!(obj instanceof TextureBindingOption)) {
                return false;
            }
            TextureBindingOption textureBindingOption = (TextureBindingOption) obj;
            if (this.width != textureBindingOption.width || this.height != textureBindingOption.height || this.resourceIdForError != textureBindingOption.resourceIdForError) {
                return false;
            }
            if (this.resourceIdForLoading != textureBindingOption.resourceIdForLoading) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        throw new UnsupportedOperationException();
    }
}
