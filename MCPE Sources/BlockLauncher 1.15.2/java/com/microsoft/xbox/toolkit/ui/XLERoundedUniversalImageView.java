package com.microsoft.xbox.toolkit.ui;

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
import com.microsoft.xbox.toolkit.ui.XLEUniversalImageView.Params;

public class XLERoundedUniversalImageView extends XLEUniversalImageView {
    public XLERoundedUniversalImageView(Context context) {
        super(context, new Params());
    }

    public XLERoundedUniversalImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public XLERoundedUniversalImageView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int i) {
        if (!(bitmap.getWidth() == i && bitmap.getHeight() == i)) {
            bitmap = Bitmap.createScaledBitmap(bitmap, i, i, false);
        }
        Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
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

    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable != null && getWidth() != 0 && getHeight() != 0) {
            Bitmap copy = ((BitmapDrawable) drawable).getBitmap().copy(Config.ARGB_8888, true);
            int width = getWidth();
            getHeight();
            canvas.drawBitmap(getRoundedCroppedBitmap(copy, width), 0.0f, 0.0f, null);
        }
    }
}
