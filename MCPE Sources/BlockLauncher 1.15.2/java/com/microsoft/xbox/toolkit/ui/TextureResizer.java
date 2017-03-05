package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class TextureResizer {
    public static Bitmap createScaledBitmap8888(Bitmap bitmap, int i, int i2, boolean z) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float f = ((float) i) / ((float) width);
        float f2 = ((float) i2) / ((float) height);
        Matrix matrix = new Matrix();
        matrix.setScale(f, f2);
        if (width + 0 > bitmap.getWidth()) {
            throw new IllegalArgumentException("x + width must be <= bitmap.width()");
        } else if (height + 0 > bitmap.getHeight()) {
            throw new IllegalArgumentException("y + height must be <= bitmap.height()");
        } else if (!bitmap.isMutable() && width == bitmap.getWidth() && height == bitmap.getHeight() && (matrix == null || matrix.isIdentity())) {
            return bitmap;
        } else {
            Bitmap createBitmap;
            Paint paint;
            Canvas canvas = new Canvas();
            Rect rect = new Rect(0, 0, width + 0, height + 0);
            RectF rectF = new RectF(0.0f, 0.0f, (float) width, (float) height);
            if (matrix == null || matrix.isIdentity()) {
                createBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                paint = null;
            } else {
                width = (bitmap.hasAlpha() || !matrix.rectStaysRect()) ? true : 0;
                RectF rectF2 = new RectF();
                matrix.mapRect(rectF2, rectF);
                Bitmap createBitmap2 = Bitmap.createBitmap(Math.round(rectF2.width()), Math.round(rectF2.height()), Config.ARGB_8888);
                if (width != 0) {
                    createBitmap2.eraseColor(0);
                }
                canvas.translate(-rectF2.left, -rectF2.top);
                canvas.concat(matrix);
                Paint paint2 = new Paint();
                paint2.setFilterBitmap(z);
                if (matrix.rectStaysRect()) {
                    paint = paint2;
                    createBitmap = createBitmap2;
                } else {
                    paint2.setAntiAlias(true);
                    paint = paint2;
                    createBitmap = createBitmap2;
                }
            }
            createBitmap.setDensity(bitmap.getDensity());
            canvas.setBitmap(createBitmap);
            canvas.drawBitmap(bitmap, rect, rectF, paint);
            return createBitmap;
        }
    }
}
