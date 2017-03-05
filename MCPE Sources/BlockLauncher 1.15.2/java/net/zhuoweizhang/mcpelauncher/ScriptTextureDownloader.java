package net.zhuoweizhang.mcpelauncher;

import android.content.Context;
import com.integralblue.httpresponsecache.HttpResponseCache;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ResponseCache;
import java.net.URL;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class ScriptTextureDownloader implements Runnable {
    public Runnable afterDownloadAction;
    public boolean canUseStale;
    public File file;
    public URL url;

    public ScriptTextureDownloader(URL url, File file) {
        this(url, file, null, true);
    }

    public ScriptTextureDownloader(URL url, File file, Runnable afterDownloadAction, boolean canUseStale) {
        this.url = url;
        this.file = file;
        this.afterDownloadAction = afterDownloadAction;
        this.canUseStale = canUseStale;
    }

    public void run() {
        try {
            fetch();
            if (this.afterDownloadAction == null) {
                ScriptManager.requestGraphicsReset();
            } else {
                ScriptManager.runOnMainThread(this.afterDownloadAction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void fetch() throws Exception {
        InputStream is = null;
        FileOutputStream fos = null;
        int response = 0;
        try {
            System.out.println(this.url);
            String urlPath = this.url.getPath();
            HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:24.0) Gecko/20100101 Firefox/24.0 BlockLauncher");
            if (this.canUseStale) {
                conn.setRequestProperty("Cache-Control", "max-stale=" + 2419200);
            }
            conn.setUseCaches(true);
            conn.setDoInput(true);
            conn.connect();
            response = conn.getResponseCode();
            is = conn.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable th) {
            Throwable th2 = th;
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e2) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e3) {
                }
            }
            throw th2;
        }
        if (response >= 400 || is == null) {
            System.err.println("Failed to load " + this.url + " " + response + " :(");
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e4) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                    return;
                } catch (Exception e5) {
                    return;
                }
            }
            return;
        }
        this.file.getParentFile().mkdirs();
        FileOutputStream fos2 = new FileOutputStream(this.file);
        try {
            byte[] buffer = new byte[EnchantType.fishingRod];
            while (true) {
                int count = is.read(buffer);
                if (count == -1) {
                    break;
                }
                fos2.write(buffer, 0, count);
            }
            fos2.flush();
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e6) {
                }
            }
            if (fos2 != null) {
                try {
                    fos2.close();
                } catch (Exception e7) {
                }
            }
            fos = fos2;
        } catch (Throwable th3) {
            th2 = th3;
            fos = fos2;
            if (is != null) {
                is.close();
            }
            if (fos != null) {
                fos.close();
            }
            throw th2;
        }
    }

    public static void attachCache(Context context) {
        File httpCacheDir = new File(context.getExternalCacheDir(), "http");
        try {
            Class.forName("android.net.http.HttpResponseCache").getMethod("install", new Class[]{File.class, Long.TYPE}).invoke(null, new Object[]{httpCacheDir, Long.valueOf(10485760)});
        } catch (Exception e) {
            try {
                HttpResponseCache.install(httpCacheDir, 10485760);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public static void flushCache() {
        try {
            ResponseCache cache = ResponseCache.getDefault();
            if (cache != null) {
                cache.getClass().getMethod("flush", new Class[0]).invoke(cache, new Object[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
