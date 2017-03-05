package net.zhuoweizhang.mcpelauncher;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.opengl.GLES20;
import android.os.Environment;
import com.microsoft.onlineid.internal.profile.DownloadProfileImageTask;
import com.mojang.minecraftpe.MainActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScreenshotHelper {

    private static final class ScreenshotWriter implements Runnable {
        private ByteBuffer buf;
        private String fileName;
        private int[] screenDim;

        public ScreenshotWriter(int[] screenDim, ByteBuffer buf, String fileName) {
            this.screenDim = screenDim;
            this.buf = buf;
            this.fileName = fileName;
        }

        public void run() {
            IOException e;
            Throwable th;
            int width = this.screenDim[2];
            int height = this.screenDim[3];
            Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            this.buf.rewind();
            byte[] rowBuffer = new byte[((width * 4) * 2)];
            int stride = width * 4;
            for (int y = 0; y < height / 2; y++) {
                this.buf.position(y * stride);
                this.buf.get(rowBuffer, 0, stride);
                this.buf.position(((height - y) - 1) * stride);
                this.buf.get(rowBuffer, stride, stride);
                this.buf.position(((height - y) - 1) * stride);
                this.buf.put(rowBuffer, 0, stride);
                this.buf.position(y * stride);
                this.buf.put(rowBuffer, stride, stride);
            }
            this.buf.rewind();
            bmp.copyPixelsFromBuffer(this.buf);
            this.buf = null;
            File file = createOutputFile(this.fileName);
            FileOutputStream fos = null;
            try {
                FileOutputStream fos2 = new FileOutputStream(file);
                try {
                    bmp.compress(CompressFormat.PNG, 100, fos2);
                    if (fos2 != null) {
                        try {
                            fos2.close();
                            fos = fos2;
                        } catch (IOException e2) {
                            fos = fos2;
                        }
                    }
                } catch (IOException e3) {
                    e = e3;
                    fos = fos2;
                    try {
                        e.printStackTrace();
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e4) {
                            }
                        }
                        bmp.recycle();
                        System.gc();
                        runCallBack(file);
                    } catch (Throwable th2) {
                        th = th2;
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e5) {
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    fos = fos2;
                    if (fos != null) {
                        fos.close();
                    }
                    throw th;
                }
            } catch (IOException e6) {
                e = e6;
                e.printStackTrace();
                if (fos != null) {
                    fos.close();
                }
                bmp.recycle();
                System.gc();
                runCallBack(file);
            }
            bmp.recycle();
            System.gc();
            runCallBack(file);
        }

        private File createOutputFile(String prefix) {
            File picturesFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), KamcordConstants.GAME_NAME);
            picturesFolder.mkdirs();
            String currentTime = new SimpleDateFormat("yyyy-MM-dd-HH-mm", Locale.US).format(new Date());
            File retFile = new File(picturesFolder, prefix + "-" + currentTime + DownloadProfileImageTask.UserTileExtension);
            int postFix = 1;
            while (retFile.exists()) {
                postFix++;
                retFile = new File(picturesFolder, prefix + "-" + currentTime + "_" + postFix + DownloadProfileImageTask.UserTileExtension);
            }
            return retFile;
        }

        private void runCallBack(File file) {
            if (MainActivity.currentMainActivity != null) {
                MainActivity main = (MainActivity) MainActivity.currentMainActivity.get();
                if (main != null) {
                    main.screenshotCallback(file);
                }
            }
        }
    }

    public static void takeScreenshot(String fileName) {
        int[] screenDim = new int[4];
        GLES20.glGetIntegerv(2978, screenDim, 0);
        ByteBuffer buf = ByteBuffer.allocateDirect((screenDim[2] * screenDim[3]) * 4);
        GLES20.glReadPixels(screenDim[0], screenDim[1], screenDim[2], screenDim[3], 6408, 5121, buf);
        new Thread(new ScreenshotWriter(screenDim, buf, fileName)).start();
    }
}
