package com.microsoft.xbox.toolkit.ui;

import com.microsoft.xbox.toolkit.XLEFileCacheItemKey;

public class TextureManagerScaledNetworkBitmapRequest implements XLEFileCacheItemKey {
    public final TextureBindingOption bindingOption;
    public final String url;

    public TextureManagerScaledNetworkBitmapRequest(String str) {
        this(str, new TextureBindingOption());
    }

    public TextureManagerScaledNetworkBitmapRequest(String str, TextureBindingOption textureBindingOption) {
        this.url = str;
        this.bindingOption = textureBindingOption;
    }

    public boolean equals(Object obj) {
        if (this != obj) {
            if (!(obj instanceof TextureManagerScaledNetworkBitmapRequest)) {
                return false;
            }
            TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest = (TextureManagerScaledNetworkBitmapRequest) obj;
            if (!this.url.equals(textureManagerScaledNetworkBitmapRequest.url)) {
                return false;
            }
            if (!this.bindingOption.equals(textureManagerScaledNetworkBitmapRequest.bindingOption)) {
                return false;
            }
        }
        return true;
    }

    public String getKeyString() {
        return this.url;
    }

    public int hashCode() {
        return this.url == null ? 0 : this.url.hashCode();
    }
}
