package com.microsoft.xbox.toolkit.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import java.io.InputStream;

public class XLEBitmap {
    public static String ALLOCATION_TAG = "XLEBITMAP";
    private Bitmap bitmapSrc = null;

    public static class XLEBitmapDrawable {
        private BitmapDrawable drawable;

        public XLEBitmapDrawable(BitmapDrawable bitmapDrawable) {
            this.drawable = bitmapDrawable;
        }

        public BitmapDrawable getDrawable() {
            return this.drawable;
        }
    }

    private XLEBitmap(Bitmap bitmap) {
        this.bitmapSrc = bitmap;
    }

    public static XLEBitmap createBitmap(int i, int i2, Config config) {
        return createBitmap(Bitmap.createBitmap(i, i2, config));
    }

    public static XLEBitmap createBitmap(Bitmap bitmap) {
        return bitmap == null ? null : new XLEBitmap(bitmap);
    }

    public static XLEBitmap createScaledBitmap(XLEBitmap xLEBitmap, int i, int i2, boolean z) {
        return createBitmap(Bitmap.createScaledBitmap(xLEBitmap.bitmapSrc, i, i2, z));
    }

    public static XLEBitmap createScaledBitmap8888(XLEBitmap xLEBitmap, int i, int i2, boolean z) {
        return createBitmap(TextureResizer.createScaledBitmap8888(xLEBitmap.bitmapSrc, i, i2, z));
    }

    public static XLEBitmap decodeResource(Resources resources, int i) {
        return createBitmap(BitmapFactory.decodeResource(resources, i));
    }

    public static XLEBitmap decodeResource(Resources resources, int i, Options options) {
        return createBitmap(BitmapFactory.decodeResource(resources, i, options));
    }

    public static XLEBitmap decodeStream(InputStream inputStream) {
        return createBitmap(BitmapFactory.decodeStream(inputStream));
    }

    public static XLEBitmap decodeStream(InputStream inputStream, Options options) {
        return createBitmap(BitmapFactory.decodeStream(inputStream, null, options));
    }

    public void finalize() {
    }

    public Bitmap getBitmap() {
        return this.bitmapSrc;
    }

    public int getByteCount() {
        return this.bitmapSrc.getRowBytes() * this.bitmapSrc.getHeight();
    }

    public XLEBitmapDrawable getDrawable() {
        return new XLEBitmapDrawable(new BitmapDrawable(this.bitmapSrc));
    }
}
