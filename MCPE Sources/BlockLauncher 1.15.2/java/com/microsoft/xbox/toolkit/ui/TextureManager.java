package com.microsoft.xbox.toolkit.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.BackgroundThreadWaitor;
import com.microsoft.xbox.toolkit.MemoryMonitor;
import com.microsoft.xbox.toolkit.MultiMap;
import com.microsoft.xbox.toolkit.StreamUtil;
import com.microsoft.xbox.toolkit.ThreadManager;
import com.microsoft.xbox.toolkit.ThreadSafePriorityQueue;
import com.microsoft.xbox.toolkit.TimeMonitor;
import com.microsoft.xbox.toolkit.XLEAssert;
import com.microsoft.xbox.toolkit.XLEFileCache;
import com.microsoft.xbox.toolkit.XLEFileCacheManager;
import com.microsoft.xbox.toolkit.XLEMemoryCache;
import com.microsoft.xbox.toolkit.XLEThread;
import com.microsoft.xbox.toolkit.network.HttpClientFactory;
import com.microsoft.xbox.toolkit.network.XLEHttpStatusAndStream;
import com.microsoft.xbox.toolkit.network.XLEThreadPool;
import com.microsoft.xbox.toolkit.ui.XLEBitmap.XLEBitmapDrawable;
import com.microsoft.xboxtcui.R;
import com.microsoft.xboxtcui.XboxTcuiSdk;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.http.client.methods.HttpGet;
import org.mozilla.javascript.Context;

public class TextureManager {
    private static final int ANIM_TIME = 100;
    private static final int BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES = 5242880;
    private static final String BMP_FILE_CACHE_DIR_NAME = "texture";
    private static final int BMP_FILE_CACHE_SIZE = 2000;
    private static final int DECODE_THREAD_WAIT_TIMEOUT_MS = 3000;
    private static final int TEXTURE_TIMEOUT_MS = 15000;
    private static final long TIME_TO_RETRY_MS = 300000;
    public static TextureManager instance = new TextureManager();
    private XLEMemoryCache<TextureManagerScaledNetworkBitmapRequest, XLEBitmap> bitmapCache = new XLEMemoryCache(MemoryMonitor.MB_TO_BYTES * Math.min(getNetworkBitmapCacheSizeInMB(), 50), BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES);
    private XLEFileCache bitmapFileCache = XLEFileCacheManager.createCache(BMP_FILE_CACHE_DIR_NAME, BMP_FILE_CACHE_SIZE);
    private Thread decodeThread = null;
    private HashSet<TextureManagerScaledNetworkBitmapRequest> inProgress = new HashSet();
    private Object listLock = new Object();
    private HashMap<TextureManagerScaledResourceBitmapRequest, XLEBitmap> resourceBitmapCache = new HashMap();
    private TimeMonitor stopwatch = new TimeMonitor();
    private HashMap<TextureManagerScaledNetworkBitmapRequest, RetryEntry> timeToRetryCache = new HashMap();
    private ThreadSafePriorityQueue<TextureManagerDownloadRequest> toDecode = new ThreadSafePriorityQueue();
    private MultiMap<TextureManagerScaledNetworkBitmapRequest, ImageView> waitingForImage = new MultiMap();

    private static class RetryEntry {
        private static final long SEC = 1000;
        private static final long[] TIMES_MS = new long[]{5000, 9000, 19000, 37000, 75000, 150000, TextureManager.TIME_TO_RETRY_MS};
        private int curIdx = 0;
        private long currStart = System.currentTimeMillis();

        public boolean isExpired() {
            return this.currStart + TIMES_MS[this.curIdx] < System.currentTimeMillis();
        }

        public void startNext() {
            if (this.curIdx < TIMES_MS.length - 1) {
                this.curIdx++;
            }
            this.currStart = System.currentTimeMillis();
        }
    }

    private class TextureManagerDecodeThread implements Runnable {
        private TextureManagerDecodeThread() {
        }

        public void run() {
            while (true) {
                XLEBitmap access$400;
                TextureManagerDownloadRequest textureManagerDownloadRequest = (TextureManagerDownloadRequest) TextureManager.this.toDecode.pop();
                if (textureManagerDownloadRequest.stream != null) {
                    BackgroundThreadWaitor.getInstance().waitForReady(TextureManager.DECODE_THREAD_WAIT_TIMEOUT_MS);
                    try {
                        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        StreamUtil.CopyStream(byteArrayOutputStream, textureManagerDownloadRequest.stream);
                        byte[] toByteArray = byteArrayOutputStream.toByteArray();
                        Options options = new Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(new ByteArrayInputStream(toByteArray), null, options);
                        Options access$200 = TextureManager.this.computeInSampleSizeOptions(textureManagerDownloadRequest.key.bindingOption.width, textureManagerDownloadRequest.key.bindingOption.height, options);
                        int i = ((options.outHeight / access$200.inSampleSize) * (options.outWidth / access$200.inSampleSize)) * 4;
                        XLEBitmap decodeStream = XLEBitmap.decodeStream(new ByteArrayInputStream(toByteArray), access$200);
                        if (textureManagerDownloadRequest.key.bindingOption.useFileCache && !TextureManager.this.bitmapFileCache.contains(textureManagerDownloadRequest.key)) {
                            TextureManager.this.bitmapFileCache.save(textureManagerDownloadRequest.key, new ByteArrayInputStream(toByteArray));
                        }
                        access$400 = TextureManager.this.createScaledBitmap(decodeStream, textureManagerDownloadRequest.key.bindingOption.width, textureManagerDownloadRequest.key.bindingOption.height);
                    } catch (Exception e) {
                        access$400 = null;
                    }
                } else {
                    access$400 = null;
                }
                BackgroundThreadWaitor.getInstance().waitForReady(TextureManager.DECODE_THREAD_WAIT_TIMEOUT_MS);
                synchronized (TextureManager.this.listLock) {
                    if (access$400 != null) {
                        TextureManager.this.bitmapCache.add(textureManagerDownloadRequest.key, access$400, access$400.getByteCount());
                        TextureManager.this.timeToRetryCache.remove(textureManagerDownloadRequest.key);
                    } else if (textureManagerDownloadRequest.key.bindingOption.resourceIdForError != -1) {
                        decodeStream = TextureManager.this.loadResource(textureManagerDownloadRequest.key.bindingOption.resourceIdForError);
                        RetryEntry retryEntry = (RetryEntry) TextureManager.this.timeToRetryCache.get(textureManagerDownloadRequest.key);
                        if (retryEntry != null) {
                            retryEntry.startNext();
                            access$400 = decodeStream;
                        } else {
                            TextureManager.this.timeToRetryCache.put(textureManagerDownloadRequest.key, new RetryEntry());
                            access$400 = decodeStream;
                        }
                    }
                    TextureManager.this.drainWaitingForImage(textureManagerDownloadRequest.key, access$400);
                    TextureManager.this.inProgress.remove(textureManagerDownloadRequest.key);
                }
            }
        }
    }

    private class TextureManagerDownloadThreadWorker implements Runnable {
        private TextureManagerDownloadRequest request;

        public TextureManagerDownloadThreadWorker(TextureManagerDownloadRequest textureManagerDownloadRequest) {
            this.request = textureManagerDownloadRequest;
        }

        private InputStream downloadFromAssets(String str) {
            try {
                return XboxTcuiSdk.getAssetManager().open(str);
            } catch (IOException e) {
                return null;
            }
        }

        private InputStream downloadFromWeb(String str) {
            try {
                XLEHttpStatusAndStream httpStatusAndStreamInternal = HttpClientFactory.textureFactory.getHttpClient(TextureManager.TEXTURE_TIMEOUT_MS).getHttpStatusAndStreamInternal(new HttpGet(URI.create(str)), false);
                return httpStatusAndStreamInternal.statusCode == Context.VERSION_ES6 ? httpStatusAndStreamInternal.stream : null;
            } catch (Exception e) {
                return null;
            }
        }

        public void run() {
            boolean z = (this.request.key == null || this.request.key.url == null) ? false : true;
            XLEAssert.assertTrue(z);
            this.request.stream = null;
            try {
                if (!this.request.key.url.startsWith("http")) {
                    this.request.stream = downloadFromAssets(this.request.key.url);
                } else if (this.request.key.bindingOption.useFileCache) {
                    this.request.stream = TextureManager.this.bitmapFileCache.getInputStreamForRead(this.request.key);
                    if (this.request.stream == null) {
                        this.request.stream = downloadFromWeb(this.request.key.url);
                    }
                } else {
                    this.request.stream = downloadFromWeb(this.request.key.url);
                }
            } catch (Exception e) {
            }
            synchronized (TextureManager.this.listLock) {
                TextureManager.this.toDecode.push(this.request);
            }
        }
    }

    public TextureManager() {
        this.stopwatch.start();
        this.decodeThread = new XLEThread(new TextureManagerDecodeThread(), "XLETextureDecodeThread");
        this.decodeThread.setDaemon(true);
        this.decodeThread.setPriority(4);
        this.decodeThread.start();
    }

    public static TextureManager Instance() {
        return instance;
    }

    private void bindToViewInternal(String str, ImageView imageView, TextureBindingOption textureBindingOption) {
        TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest = new TextureManagerScaledNetworkBitmapRequest(str, textureBindingOption);
        XLEBitmap xLEBitmap = null;
        synchronized (this.listLock) {
            Object obj;
            if (this.waitingForImage.containsValue(imageView)) {
                this.waitingForImage.removeValue(imageView);
            }
            if (!invalidUrl(str)) {
                xLEBitmap = (XLEBitmap) this.bitmapCache.get(textureManagerScaledNetworkBitmapRequest);
                if (xLEBitmap == null) {
                    RetryEntry retryEntry = (RetryEntry) this.timeToRetryCache.get(textureManagerScaledNetworkBitmapRequest);
                    if (retryEntry == null) {
                        int i = 1;
                    } else if (retryEntry.isExpired()) {
                        obj = 1;
                    } else {
                        if (textureBindingOption.resourceIdForError != -1) {
                            xLEBitmap = loadResource(textureBindingOption.resourceIdForError);
                        }
                        obj = null;
                    }
                } else {
                    obj = null;
                }
            } else if (textureBindingOption.resourceIdForError != -1) {
                xLEBitmap = loadResource(textureBindingOption.resourceIdForError);
                XLEAssert.assertNotNull(xLEBitmap);
                obj = null;
            } else {
                obj = null;
            }
            if (obj != null) {
                if (textureBindingOption.resourceIdForLoading != -1) {
                    xLEBitmap = loadResource(textureBindingOption.resourceIdForLoading);
                    XLEAssert.assertTrue(xLEBitmap != null);
                }
                this.waitingForImage.put(textureManagerScaledNetworkBitmapRequest, imageView);
                if (!this.inProgress.contains(textureManagerScaledNetworkBitmapRequest)) {
                    this.inProgress.add(textureManagerScaledNetworkBitmapRequest);
                    load(textureManagerScaledNetworkBitmapRequest);
                }
            }
        }
        setImage(imageView, xLEBitmap);
        if (imageView instanceof XLEImageView) {
            ((XLEImageView) imageView).TEST_loadingOrLoadedImageUrl = str;
        }
    }

    private Options computeInSampleSizeOptions(int i, int i2, Options options) {
        int i3 = true;
        Options options2 = new Options();
        if (validResizeDimention(i, i2) && options.outWidth > i && options.outHeight > i2) {
            boolean z;
            int pow = (int) Math.pow(2.0d, (double) Math.min((int) Math.floor(Math.log((double) (((float) options.outWidth) / ((float) i))) / Math.log(2.0d)), (int) Math.floor(Math.log((double) (((float) options.outHeight) / ((float) i2))) / Math.log(2.0d))));
            if (pow < 1) {
                z = false;
            }
            XLEAssert.assertTrue(z);
            i3 = pow;
        }
        options2.inSampleSize = i3;
        return options2;
    }

    private XLEBitmap createScaledBitmap(XLEBitmap xLEBitmap, int i, int i2) {
        if (!validResizeDimention(i, i2) || xLEBitmap.getBitmap() == null) {
            return xLEBitmap;
        }
        float height = ((float) xLEBitmap.getBitmap().getHeight()) / ((float) xLEBitmap.getBitmap().getWidth());
        if (((float) i2) / ((float) i) < height) {
            i = Math.max(1, (int) (((float) i2) / height));
        } else {
            i2 = Math.max(1, (int) (height * ((float) i)));
        }
        return XLEBitmap.createScaledBitmap8888(xLEBitmap, i, i2, true);
    }

    private void drainWaitingForImage(TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest, XLEBitmap xLEBitmap) {
        if (this.waitingForImage.containsKey(textureManagerScaledNetworkBitmapRequest)) {
            Iterator it = this.waitingForImage.get(textureManagerScaledNetworkBitmapRequest).iterator();
            while (it.hasNext()) {
                ImageView imageView = (ImageView) it.next();
                if (imageView != null) {
                    if (imageView instanceof XLEImageView) {
                        setXLEImageView(textureManagerScaledNetworkBitmapRequest, (XLEImageView) imageView, xLEBitmap);
                    } else {
                        setView(textureManagerScaledNetworkBitmapRequest, imageView, xLEBitmap);
                    }
                }
            }
        }
    }

    private int getNetworkBitmapCacheSizeInMB() {
        return (Math.max(0, MemoryMonitor.instance().getMemoryClass() - 64) / 2) + 12;
    }

    private static boolean invalidUrl(String str) {
        return str == null || str.length() == 0;
    }

    private void load(TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest) {
        if (!invalidUrl(textureManagerScaledNetworkBitmapRequest.url)) {
            XLEThreadPool.textureThreadPool.run(new TextureManagerDownloadThreadWorker(new TextureManagerDownloadRequest(textureManagerScaledNetworkBitmapRequest)));
        }
    }

    private void setImage(ImageView imageView, XLEBitmap xLEBitmap) {
        Bitmap bitmap = xLEBitmap == null ? null : xLEBitmap.getBitmap();
        OnBitmapSetListener onBitmapSetListener = (OnBitmapSetListener) imageView.getTag(R.id.image_callback);
        if (onBitmapSetListener != null) {
            onBitmapSetListener.onBeforeImageSet(imageView, bitmap);
        }
        imageView.setImageBitmap(bitmap);
        imageView.setTag(R.id.image_bound, Boolean.valueOf(true));
        if (onBitmapSetListener != null) {
            onBitmapSetListener.onAfterImageSet(imageView, bitmap);
        }
    }

    private void setView(final TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest, final ImageView imageView, final XLEBitmap xLEBitmap) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
                synchronized (TextureManager.this.listLock) {
                    boolean keyValueMatches = TextureManager.this.waitingForImage.keyValueMatches(textureManagerScaledNetworkBitmapRequest, imageView);
                }
                if (keyValueMatches) {
                    TextureManager.this.setImage(imageView, xLEBitmap);
                    synchronized (TextureManager.this.listLock) {
                        TextureManager.this.waitingForImage.removeValue(imageView);
                    }
                }
            }
        });
    }

    private void setXLEImageView(final TextureManagerScaledNetworkBitmapRequest textureManagerScaledNetworkBitmapRequest, final XLEImageView xLEImageView, final XLEBitmap xLEBitmap) {
        ThreadManager.UIThreadPost(new Runnable() {
            public void run() {
                XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
                synchronized (TextureManager.this.listLock) {
                    boolean keyValueMatches = TextureManager.this.waitingForImage.keyValueMatches(textureManagerScaledNetworkBitmapRequest, xLEImageView);
                }
                if (keyValueMatches) {
                    final float alpha = xLEImageView.getAlpha();
                    if (xLEImageView.getShouldAnimate()) {
                        xLEImageView.animate().alpha(0.0f).setDuration(100).setListener(new AnimatorListenerAdapter() {
                            public void onAnimationEnd(Animator animator) {
                                xLEImageView.setFinal(true);
                                TextureManager.this.setImage(xLEImageView, xLEBitmap);
                                xLEImageView.animate().alpha(alpha).setDuration(100).setListener(null);
                            }
                        });
                    } else {
                        TextureManager.this.setImage(xLEImageView, xLEBitmap);
                    }
                    synchronized (TextureManager.this.listLock) {
                        TextureManager.this.waitingForImage.removeValue(xLEImageView);
                    }
                }
            }
        });
    }

    private static boolean validResizeDimention(int i, int i2) {
        if (i != 0 && i2 != 0) {
            return i > 0 && i2 > 0;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void bindToView(int i, ImageView imageView, int i2, int i3) {
        bindToView(i, imageView, i2, i3, null);
    }

    public void bindToView(int i, ImageView imageView, int i2, int i3, OnBitmapSetListener onBitmapSetListener) {
        boolean z = false;
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        XLEBitmap loadResource = loadResource(i);
        if (loadResource != null) {
            z = true;
        }
        XLEAssert.assertTrue(z);
        if (imageView instanceof XLEImageView) {
            ((XLEImageView) imageView).TEST_loadingOrLoadedImageUrl = Integer.toString(i);
        }
        setImage(imageView, loadResource);
    }

    public void bindToView(URI uri, ImageView imageView, int i, int i2) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (i == 0 || i2 == 0) {
            throw new UnsupportedOperationException();
        }
        bindToViewInternal(uri == null ? null : uri.toString(), imageView, new TextureBindingOption(i, i2));
    }

    public void bindToView(URI uri, ImageView imageView, TextureBindingOption textureBindingOption) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        bindToViewInternal(uri == null ? null : uri.toString(), imageView, textureBindingOption);
    }

    public void bindToViewFromFile(String str, ImageView imageView, int i, int i2) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        if (i == 0 || i2 == 0) {
            throw new UnsupportedOperationException();
        }
        bindToViewInternal(str, imageView, new TextureBindingOption(i, i2));
    }

    public void bindToViewFromFile(String str, ImageView imageView, TextureBindingOption textureBindingOption) {
        XLEAssert.assertTrue(Thread.currentThread() == ThreadManager.UIThread);
        bindToViewInternal(str, imageView, textureBindingOption);
    }

    public boolean isBusy() {
        boolean z;
        synchronized (this.listLock) {
            z = !this.inProgress.isEmpty();
        }
        return z;
    }

    public XLEBitmap loadResource(int i) {
        TextureManagerScaledResourceBitmapRequest textureManagerScaledResourceBitmapRequest = new TextureManagerScaledResourceBitmapRequest(i);
        XLEBitmap xLEBitmap = (XLEBitmap) this.resourceBitmapCache.get(textureManagerScaledResourceBitmapRequest);
        if (xLEBitmap == null) {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(XboxTcuiSdk.getResources(), textureManagerScaledResourceBitmapRequest.resourceId, options);
            xLEBitmap = XLEBitmap.decodeResource(XboxTcuiSdk.getResources(), textureManagerScaledResourceBitmapRequest.resourceId);
            this.resourceBitmapCache.put(textureManagerScaledResourceBitmapRequest, xLEBitmap);
        }
        XLEAssert.assertNotNull(xLEBitmap);
        return xLEBitmap;
    }

    public XLEBitmapDrawable loadScaledResourceDrawable(int i) {
        XLEBitmap loadResource = loadResource(i);
        return loadResource == null ? null : loadResource.getDrawable();
    }

    public void logMemoryUsage() {
    }

    public void preload(int i) {
    }

    public void preload(URI uri) {
    }

    public void preloadFromFile(String str) {
    }

    public void purgeResourceBitmapCache() {
        this.resourceBitmapCache.clear();
    }

    public void setCachingEnabled(boolean z) {
        this.bitmapCache = new XLEMemoryCache(z ? getNetworkBitmapCacheSizeInMB() : 0, BITMAP_CACHE_MAX_FILE_SIZE_IN_BYTES);
        this.bitmapFileCache = XLEFileCacheManager.createCache(BMP_FILE_CACHE_DIR_NAME, BMP_FILE_CACHE_SIZE, z);
        this.resourceBitmapCache = new HashMap();
    }

    public void unsafeClearBitmapCache() {
    }
}
