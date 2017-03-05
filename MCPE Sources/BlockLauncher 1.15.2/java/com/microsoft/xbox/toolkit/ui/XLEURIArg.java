package com.microsoft.xbox.toolkit.ui;

import java.net.URI;

public class XLEURIArg {
    private final int errorResourceId;
    private final int loadingResourceId;
    private final URI uri;

    public XLEURIArg(URI uri) {
        this(uri, -1, -1);
    }

    public XLEURIArg(URI uri, int i, int i2) {
        this.uri = uri;
        this.loadingResourceId = i;
        this.errorResourceId = i2;
    }

    public boolean equals(Object obj) {
        if (obj != null) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof XLEURIArg) {
                XLEURIArg xLEURIArg = (XLEURIArg) obj;
                if (this.loadingResourceId == xLEURIArg.loadingResourceId && this.errorResourceId == xLEURIArg.errorResourceId) {
                    if (this.uri == xLEURIArg.uri) {
                        return true;
                    }
                    if (this.uri != null && this.uri.equals(xLEURIArg.uri)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getErrorResourceId() {
        return this.errorResourceId;
    }

    public int getLoadingResourceId() {
        return this.loadingResourceId;
    }

    public TextureBindingOption getTextureBindingOption() {
        return new TextureBindingOption(-1, -1, this.loadingResourceId, this.errorResourceId, false);
    }

    public URI getUri() {
        return this.uri;
    }

    public int hashCode() {
        int i = ((this.loadingResourceId + 13) * 17) + this.errorResourceId;
        return this.uri != null ? (i * 23) + this.uri.hashCode() : i;
    }
}
