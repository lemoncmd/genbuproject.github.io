package com.microsoft.onlineid.internal.ui;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.TouchDelegate;
import android.view.View;

public final class Dimensions {
    private static final float MinimumTouchableTargetDp = 48.0f;

    public static int convertDipToPixels(float f, DisplayMetrics displayMetrics) {
        return Math.round(TypedValue.applyDimension(1, f, displayMetrics));
    }

    public static void ensureMinimumTouchTarget(final View view, final View view2, final DisplayMetrics displayMetrics) {
        view2.post(new Runnable() {
            public void run() {
                Rect rect = new Rect();
                view.getHitRect(rect);
                int width = rect.width();
                int height = rect.height();
                int convertDipToPixels = Dimensions.convertDipToPixels(Dimensions.MinimumTouchableTargetDp, displayMetrics);
                if (width < convertDipToPixels) {
                    width = ((convertDipToPixels - width) + 1) / 2;
                    rect.left -= width;
                    rect.right = width + rect.right;
                }
                if (height < convertDipToPixels) {
                    width = ((convertDipToPixels - height) + 1) / 2;
                    rect.top -= width;
                    rect.bottom = width + rect.bottom;
                }
                view2.setTouchDelegate(new TouchDelegate(rect, view));
            }
        });
    }
}
