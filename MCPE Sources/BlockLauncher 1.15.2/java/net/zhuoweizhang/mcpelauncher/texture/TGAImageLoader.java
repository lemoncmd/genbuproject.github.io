package net.zhuoweizhang.mcpelauncher.texture;

import android.graphics.Bitmap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import net.zhuoweizhang.mcpelauncher.texture.tga.TGAImage;
import net.zhuoweizhang.mcpelauncher.texture.tga.TGALoader;

public class TGAImageLoader implements ImageLoader {
    public Bitmap load(InputStream is) throws IOException {
        return TGALoader.load(is, false);
    }

    public void save(Bitmap outBmp, OutputStream os) throws IOException {
        ByteBuffer data = ByteBuffer.allocate((outBmp.getWidth() * outBmp.getHeight()) * 4);
        int[] tempArr = new int[(outBmp.getWidth() * outBmp.getHeight())];
        outBmp.getPixels(tempArr, 0, outBmp.getWidth(), 0, 0, outBmp.getWidth(), outBmp.getHeight());
        data.order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().put(tempArr);
        invertBuffer(data, outBmp.getWidth(), outBmp.getHeight());
        TGAImage.createFromData(outBmp.getWidth(), outBmp.getHeight(), true, false, data).write(Channels.newChannel(os));
    }

    private static void invertBuffer(ByteBuffer buf, int width, int height) {
        byte[] rowBuffer = new byte[((width * 4) * 2)];
        int stride = width * 4;
        for (int y = 0; y < height / 2; y++) {
            buf.position(y * stride);
            buf.get(rowBuffer, 0, stride);
            buf.position(((height - y) - 1) * stride);
            buf.get(rowBuffer, stride, stride);
            buf.position(((height - y) - 1) * stride);
            buf.put(rowBuffer, 0, stride);
            buf.position(y * stride);
            buf.put(rowBuffer, stride, stride);
        }
        buf.rewind();
    }
}
