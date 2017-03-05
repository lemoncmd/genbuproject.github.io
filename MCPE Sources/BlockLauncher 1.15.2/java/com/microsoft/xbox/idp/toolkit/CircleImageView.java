package com.microsoft.xbox.idp.toolkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {
    static final /* synthetic */ boolean $assertionsDisabled = (!CircleImageView.class.desiredAssertionStatus());

    public CircleImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public CircleImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    private Bitmap createBitmap(Drawable drawable) {
        if ($assertionsDisabled || (getWidth() > 0 && getHeight() > 0)) {
            Bitmap createBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return createBitmap;
        }
        throw new AssertionError();
    }

    private Bitmap createRoundBitmap(Bitmap bitmap, int i) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i, Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(((float) (bitmap.getWidth() / 2)) + 0.7f, ((float) (bitmap.getHeight() / 2)) + 0.7f, 0.1f + ((float) (bitmap.getWidth() / 2)), paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return createBitmap;
    }

    private void drawBitmap(Canvas canvas, Bitmap bitmap) {
        if (bitmap != null) {
            int min = Math.min(getWidth(), getHeight());
            if (bitmap.getWidth() == min && bitmap.getHeight() == min) {
                drawRoundBitmap(canvas, bitmap, min);
                return;
            }
            Bitmap createScaledBitmap = Bitmap.createScaledBitmap(bitmap, min, min, false);
            try {
                drawRoundBitmap(canvas, createScaledBitmap, min);
            } finally {
                createScaledBitmap.recycle();
            }
        }
    }

    private void drawRoundBitmap(Canvas canvas, Bitmap bitmap, int i) {
        Bitmap createRoundBitmap = createRoundBitmap(bitmap, i);
        try {
            canvas.drawBitmap(createRoundBitmap, 0.0f, 0.0f, null);
        } finally {
            createRoundBitmap.recycle();
        }
    }

    protected void onDraw(Canvas canvas) {
        if (getWidth() != 0 && getHeight() != 0) {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            if (drawable instanceof BitmapDrawable) {
                drawBitmap(canvas, ((BitmapDrawable) drawable).getBitmap());
                return;
            }
            Bitmap createBitmap = createBitmap(drawable);
            try {
                drawBitmap(canvas, createBitmap);
            } finally {
                createBitmap.recycle();
            }
        }
    }
}
