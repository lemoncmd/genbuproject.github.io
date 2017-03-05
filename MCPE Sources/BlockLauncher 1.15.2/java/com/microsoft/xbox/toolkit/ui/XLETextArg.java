package com.microsoft.xbox.toolkit.ui;

import android.graphics.Typeface;
import com.microsoft.onlineid.internal.ui.AccountHeaderView;
import com.microsoft.xbox.toolkit.system.SystemUtil;

public class XLETextArg {
    private final Params params;
    private final String text;

    public static class Params {
        private final boolean adjustForImageSize;
        private final int color;
        private final int eraseColor;
        private final Float textAspectRatio;
        private final float textSize;
        private final Typeface typeface;

        public Params() {
            this((float) SystemUtil.SPtoPixels(AccountHeaderView.MarginMediumDip), -1, Typeface.DEFAULT, 0, false, null);
        }

        public Params(float f, int i, Typeface typeface, int i2, boolean z, Float f2) {
            this.textSize = f;
            this.color = i;
            this.typeface = typeface;
            this.eraseColor = i2;
            this.adjustForImageSize = z;
            this.textAspectRatio = f2;
        }

        public int getColor() {
            return this.color;
        }

        public int getEraseColor() {
            return this.eraseColor;
        }

        public Float getTextAspectRatio() {
            return this.textAspectRatio;
        }

        public float getTextSize() {
            return this.textSize;
        }

        public Typeface getTypeface() {
            return this.typeface;
        }

        public boolean hasEraseColor() {
            return this.eraseColor != 0;
        }

        public boolean hasTextAspectRatio() {
            return this.textAspectRatio != null;
        }

        public boolean isAdjustForImageSize() {
            return this.adjustForImageSize;
        }
    }

    public XLETextArg(Params params) {
        this(null, params);
    }

    public XLETextArg(String str, Params params) {
        this.text = str;
        this.params = params;
    }

    public Params getParams() {
        return this.params;
    }

    public String getText() {
        return this.text;
    }

    public boolean hasText() {
        return this.text != null;
    }
}
