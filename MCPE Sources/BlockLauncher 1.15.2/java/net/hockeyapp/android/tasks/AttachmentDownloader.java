package net.hockeyapp.android.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Message;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Queue;
import net.hockeyapp.android.Constants;
import net.hockeyapp.android.objects.FeedbackAttachment;
import net.hockeyapp.android.utils.AsyncTaskUtils;
import net.hockeyapp.android.utils.HockeyLog;
import net.hockeyapp.android.utils.ImageUtils;
import net.hockeyapp.android.views.AttachmentView;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class AttachmentDownloader {
    private boolean downloadRunning;
    private Queue<DownloadJob> queue;

    private static class AttachmentDownloaderHolder {
        public static final AttachmentDownloader INSTANCE = new AttachmentDownloader();

        private AttachmentDownloaderHolder() {
        }
    }

    private static class DownloadJob {
        private final AttachmentView attachmentView;
        private final FeedbackAttachment feedbackAttachment;
        private int remainingRetries;
        private boolean success;

        private DownloadJob(FeedbackAttachment feedbackAttachment, AttachmentView attachmentView) {
            this.feedbackAttachment = feedbackAttachment;
            this.attachmentView = attachmentView;
            this.success = false;
            this.remainingRetries = 2;
        }

        public boolean consumeRetry() {
            int i = this.remainingRetries - 1;
            this.remainingRetries = i;
            return i >= 0;
        }

        public AttachmentView getAttachmentView() {
            return this.attachmentView;
        }

        public FeedbackAttachment getFeedbackAttachment() {
            return this.feedbackAttachment;
        }

        public boolean hasRetry() {
            return this.remainingRetries > 0;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public void setSuccess(boolean z) {
            this.success = z;
        }
    }

    private static class DownloadTask extends AsyncTask<Void, Integer, Boolean> {
        private Bitmap bitmap = null;
        private int bitmapOrientation = 0;
        private final DownloadJob downloadJob;
        private File dropFolder = Constants.getHockeyAppStorageDir();
        private final Handler handler;

        public DownloadTask(DownloadJob downloadJob, Handler handler) {
            this.downloadJob = downloadJob;
            this.handler = handler;
        }

        private URLConnection createConnection(URL url) throws IOException {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.addRequestProperty("User-Agent", "HockeySDK/Android");
            httpURLConnection.setInstanceFollowRedirects(true);
            if (VERSION.SDK_INT <= 9) {
                httpURLConnection.setRequestProperty("connection", "close");
            }
            return httpURLConnection;
        }

        private boolean downloadAttachment(String str, String str2) {
            try {
                URLConnection createConnection = createConnection(new URL(str));
                createConnection.connect();
                int contentLength = createConnection.getContentLength();
                String headerField = createConnection.getHeaderField("Status");
                if (headerField != null && !headerField.startsWith("200")) {
                    return false;
                }
                File file = new File(this.dropFolder, str2);
                InputStream bufferedInputStream = new BufferedInputStream(createConnection.getInputStream());
                OutputStream fileOutputStream = new FileOutputStream(file);
                byte[] bArr = new byte[EnchantType.pickaxe];
                long j = 0;
                while (true) {
                    int read = bufferedInputStream.read(bArr);
                    if (read == -1) {
                        break;
                    }
                    j += (long) read;
                    publishProgress(new Integer[]{Integer.valueOf((int) ((100 * j) / ((long) contentLength)))});
                    fileOutputStream.write(bArr, 0, read);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                bufferedInputStream.close();
                return j > 0;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        private void loadImageThumbnail() {
            try {
                String cacheId = this.downloadJob.getFeedbackAttachment().getCacheId();
                AttachmentView attachmentView = this.downloadJob.getAttachmentView();
                this.bitmapOrientation = ImageUtils.determineOrientation(new File(this.dropFolder, cacheId));
                this.bitmap = ImageUtils.decodeSampledBitmap(new File(this.dropFolder, cacheId), this.bitmapOrientation == 1 ? attachmentView.getWidthLandscape() : attachmentView.getWidthPortrait(), this.bitmapOrientation == 1 ? attachmentView.getMaxHeightLandscape() : attachmentView.getMaxHeightPortrait());
            } catch (IOException e) {
                e.printStackTrace();
                this.bitmap = null;
            }
        }

        protected Boolean doInBackground(Void... voidArr) {
            FeedbackAttachment feedbackAttachment = this.downloadJob.getFeedbackAttachment();
            if (feedbackAttachment.isAvailableInCache()) {
                HockeyLog.error("Cached...");
                loadImageThumbnail();
                return Boolean.valueOf(true);
            }
            HockeyLog.error("Downloading...");
            boolean downloadAttachment = downloadAttachment(feedbackAttachment.getUrl(), feedbackAttachment.getCacheId());
            if (downloadAttachment) {
                loadImageThumbnail();
            }
            return Boolean.valueOf(downloadAttachment);
        }

        protected void onPostExecute(Boolean bool) {
            AttachmentView attachmentView = this.downloadJob.getAttachmentView();
            this.downloadJob.setSuccess(bool.booleanValue());
            if (bool.booleanValue()) {
                attachmentView.setImage(this.bitmap, this.bitmapOrientation);
            } else if (!this.downloadJob.hasRetry()) {
                attachmentView.signalImageLoadingError();
            }
            this.handler.sendEmptyMessage(0);
        }

        protected void onPreExecute() {
        }

        protected void onProgressUpdate(Integer... numArr) {
        }
    }

    private AttachmentDownloader() {
        this.queue = new LinkedList();
        this.downloadRunning = false;
    }

    private void downloadNext() {
        if (!this.downloadRunning) {
            DownloadJob downloadJob = (DownloadJob) this.queue.peek();
            if (downloadJob != null) {
                AsyncTask downloadTask = new DownloadTask(downloadJob, new Handler() {
                    public void handleMessage(Message message) {
                        final DownloadJob downloadJob = (DownloadJob) AttachmentDownloader.this.queue.poll();
                        if (!downloadJob.isSuccess() && downloadJob.consumeRetry()) {
                            postDelayed(new Runnable() {
                                public void run() {
                                    AttachmentDownloader.this.queue.add(downloadJob);
                                    AttachmentDownloader.this.downloadNext();
                                }
                            }, 3000);
                        }
                        AttachmentDownloader.this.downloadRunning = false;
                        AttachmentDownloader.this.downloadNext();
                    }
                });
                this.downloadRunning = true;
                AsyncTaskUtils.execute(downloadTask);
            }
        }
    }

    public static AttachmentDownloader getInstance() {
        return AttachmentDownloaderHolder.INSTANCE;
    }

    public void download(FeedbackAttachment feedbackAttachment, AttachmentView attachmentView) {
        this.queue.add(new DownloadJob(feedbackAttachment, attachmentView));
        downloadNext();
    }
}
