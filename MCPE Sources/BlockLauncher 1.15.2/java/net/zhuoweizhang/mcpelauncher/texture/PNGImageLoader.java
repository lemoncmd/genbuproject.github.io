package net.zhuoweizhang.mcpelauncher.texture;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import java.io.InputStream;
import java.io.OutputStream;

public class PNGImageLoader implements ImageLoader {
    public Bitmap load(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    public void save(Bitmap bmp, OutputStream os) {
        bmp.compress(CompressFormat.PNG, 100, os);
    }
}
