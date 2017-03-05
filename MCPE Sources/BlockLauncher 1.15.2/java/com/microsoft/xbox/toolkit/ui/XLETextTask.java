package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.ui.XLETextArg.Params;
import java.lang.ref.WeakReference;

public class XLETextTask extends AsyncTask<XLETextArg, Void, Bitmap> {
    private static final String TAG = XLETextTask.class.getSimpleName();
    private final WeakReference<ImageView> img;
    private final int imgHeight;
    private final int imgWidth;

    public XLETextTask(ImageView imageView) {
        this.img = new WeakReference(imageView);
        this.imgWidth = imageView.getWidth();
        this.imgHeight = imageView.getHeight();
    }

    protected Bitmap doInBackground(XLETextArg... xLETextArgArr) {
        if (xLETextArgArr.length <= 0) {
            return null;
        }
        int max;
        int max2;
        XLETextArg xLETextArg = xLETextArgArr[0];
        Params params = xLETextArg.getParams();
        String text = xLETextArg.getText();
        Paint textPaint = new TextPaint();
        textPaint.setTextSize(params.getTextSize());
        textPaint.setAntiAlias(true);
        textPaint.setColor(params.getColor());
        textPaint.setTypeface(params.getTypeface());
        int round = Math.round(textPaint.measureText(text));
        int round2 = Math.round(textPaint.descent() - textPaint.ascent());
        if (params.isAdjustForImageSize()) {
            max = Math.max(round, this.imgWidth);
            max2 = Math.max(round2, this.imgHeight);
        } else {
            max2 = round2;
            max = round;
        }
        if (params.hasTextAspectRatio()) {
            float floatValue = params.getTextAspectRatio().floatValue();
            if (floatValue > 0.0f) {
                if (((float) max2) > ((float) max) * floatValue) {
                    max = (int) (((float) max2) / floatValue);
                } else {
                    max2 = (int) (((float) max) * floatValue);
                }
            }
        }
        Bitmap createBitmap = Bitmap.createBitmap(max, max2, Config.ARGB_8888);
        if (params.hasEraseColor()) {
            createBitmap.eraseColor(params.getEraseColor());
        }
        new Canvas(createBitmap).drawText(text, (float) ((Math.max(0, max - round) / 2) + 0), ((float) (Math.max(0, max2 - round2) / 2)) + (-textPaint.ascent()), textPaint);
        return createBitmap;
    }

    protected void onPostExecute(Bitmap bitmap) {
        ImageView imageView = (ImageView) this.img.get();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
