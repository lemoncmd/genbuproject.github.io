package net.zhuoweizhang.mcpelauncher.texture;

import android.graphics.Bitmap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ImageLoader {
    Bitmap load(InputStream inputStream) throws IOException;

    void save(Bitmap bitmap, OutputStream outputStream) throws IOException;
}
