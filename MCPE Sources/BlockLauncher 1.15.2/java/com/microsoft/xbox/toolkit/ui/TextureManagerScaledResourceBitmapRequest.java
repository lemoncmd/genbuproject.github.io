package com.microsoft.xbox.toolkit.ui;

public class TextureManagerScaledResourceBitmapRequest {
    public final TextureBindingOption bindingOption;
    public final int resourceId;

    public TextureManagerScaledResourceBitmapRequest(int i) {
        this(i, new TextureBindingOption());
    }

    public TextureManagerScaledResourceBitmapRequest(int i, TextureBindingOption textureBindingOption) {
        this.resourceId = i;
        this.bindingOption = textureBindingOption;
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (!(obj instanceof TextureManagerScaledResourceBitmapRequest)) {
                return false;
            }
            TextureManagerScaledResourceBitmapRequest textureManagerScaledResourceBitmapRequest = (TextureManagerScaledResourceBitmapRequest) obj;
            if (this.resourceId != textureManagerScaledResourceBitmapRequest.resourceId) {
                return false;
            }
            if (!this.bindingOption.equals(textureManagerScaledResourceBitmapRequest.bindingOption)) {
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return this.resourceId;
    }
}
