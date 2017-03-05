package net.hockeyapp.android.tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StrictMode;
import android.os.StrictMode.VmPolicy;
import android.os.StrictMode.VmPolicy.Builder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import net.hockeyapp.android.R;
import net.hockeyapp.android.listeners.DownloadFileListener;
import net.zhuoweizhang.mcpelauncher.api.modpe.EnchantType;

public class DownloadFileTask extends AsyncTask<Void, Integer, Long> {
    protected static final int MAX_REDIRECTS = 6;
    protected Context mContext;
    private String mDownloadErrorMessage;
    protected String mFilePath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download");
    protected String mFilename = (UUID.randomUUID() + ".apk");
    protected DownloadFileListener mNotifier;
    protected ProgressDialog mProgressDialog;
    protected String mUrlString;

    public DownloadFileTask(Context context, String str, DownloadFileListener downloadFileListener) {
        this.mContext = context;
        this.mUrlString = str;
        this.mNotifier = downloadFileListener;
        this.mDownloadErrorMessage = null;
    }

    public void attach(Context context) {
        this.mContext = context;
    }

    protected URLConnection createConnection(URL url, int i) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        setConnectionProperties(httpURLConnection);
        int responseCode = httpURLConnection.getResponseCode();
        if ((responseCode != 301 && responseCode != 302 && responseCode != 303) || i == 0) {
            return httpURLConnection;
        }
        URL url2 = new URL(httpURLConnection.getHeaderField("Location"));
        if (url.getProtocol().equals(url2.getProtocol())) {
            return httpURLConnection;
        }
        httpURLConnection.disconnect();
        return createConnection(url2, i - 1);
    }

    public void detach() {
        this.mContext = null;
        this.mProgressDialog = null;
    }

    protected Long doInBackground(Void... voidArr) {
        OutputStream fileOutputStream;
        IOException e;
        InputStream inputStream;
        Long valueOf;
        Throwable th;
        Throwable th2;
        OutputStream outputStream = null;
        InputStream bufferedInputStream;
        try {
            URLConnection createConnection = createConnection(new URL(getURLString()), MAX_REDIRECTS);
            createConnection.connect();
            int contentLength = createConnection.getContentLength();
            String contentType = createConnection.getContentType();
            if (contentType == null || !contentType.contains("text")) {
                File file = new File(this.mFilePath);
                if (file.mkdirs() || file.exists()) {
                    File file2 = new File(file, this.mFilename);
                    bufferedInputStream = new BufferedInputStream(createConnection.getInputStream());
                    try {
                        fileOutputStream = new FileOutputStream(file2);
                    } catch (IOException e2) {
                        e = e2;
                        inputStream = bufferedInputStream;
                        try {
                            e.printStackTrace();
                            valueOf = Long.valueOf(0);
                            if (outputStream != null) {
                                try {
                                    outputStream.close();
                                } catch (IOException e3) {
                                    e3.printStackTrace();
                                    return valueOf;
                                }
                            }
                            if (inputStream != null) {
                                return valueOf;
                            }
                            inputStream.close();
                            return valueOf;
                        } catch (Throwable th3) {
                            th = th3;
                            bufferedInputStream = inputStream;
                            fileOutputStream = outputStream;
                            th2 = th;
                            if (fileOutputStream != null) {
                                try {
                                    fileOutputStream.close();
                                } catch (IOException e4) {
                                    e4.printStackTrace();
                                    throw th2;
                                }
                            }
                            if (bufferedInputStream != null) {
                                bufferedInputStream.close();
                            }
                            throw th2;
                        }
                    } catch (Throwable th4) {
                        fileOutputStream = null;
                        th2 = th4;
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (bufferedInputStream != null) {
                            bufferedInputStream.close();
                        }
                        throw th2;
                    }
                    try {
                        byte[] bArr = new byte[EnchantType.pickaxe];
                        long j = 0;
                        while (true) {
                            int read = bufferedInputStream.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            j += (long) read;
                            publishProgress(new Integer[]{Integer.valueOf(Math.round((100.0f * ((float) j)) / ((float) contentLength)))});
                            fileOutputStream.write(bArr, 0, read);
                        }
                        fileOutputStream.flush();
                        valueOf = Long.valueOf(j);
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e32) {
                                e32.printStackTrace();
                                return valueOf;
                            }
                        }
                        if (bufferedInputStream == null) {
                            return valueOf;
                        }
                        bufferedInputStream.close();
                        return valueOf;
                    } catch (IOException e5) {
                        e4 = e5;
                        outputStream = fileOutputStream;
                        inputStream = bufferedInputStream;
                        e4.printStackTrace();
                        valueOf = Long.valueOf(0);
                        if (outputStream != null) {
                            outputStream.close();
                        }
                        if (inputStream != null) {
                            return valueOf;
                        }
                        inputStream.close();
                        return valueOf;
                    } catch (Throwable th42) {
                        th2 = th42;
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (bufferedInputStream != null) {
                            bufferedInputStream.close();
                        }
                        throw th2;
                    }
                }
                throw new IOException("Could not create the dir(s):" + file.getAbsolutePath());
            }
            this.mDownloadErrorMessage = "The requested download does not appear to be a file.";
            return Long.valueOf(0);
        } catch (IOException e6) {
            e4 = e6;
            inputStream = null;
            e4.printStackTrace();
            valueOf = Long.valueOf(0);
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                return valueOf;
            }
            inputStream.close();
            return valueOf;
        } catch (Throwable th5) {
            th42 = th5;
            inputStream = null;
            bufferedInputStream = inputStream;
            fileOutputStream = outputStream;
            th2 = th42;
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            throw th2;
        }
    }

    protected String getURLString() {
        return this.mUrlString + "&type=apk";
    }

    protected void onPostExecute(Long l) {
        if (this.mProgressDialog != null) {
            try {
                this.mProgressDialog.dismiss();
            } catch (Exception e) {
            }
        }
        if (l.longValue() > 0) {
            this.mNotifier.downloadSuccessful(this);
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setDataAndType(Uri.fromFile(new File(this.mFilePath, this.mFilename)), "application/vnd.android.package-archive");
            intent.setFlags(268435456);
            VmPolicy vmPolicy = null;
            if (VERSION.SDK_INT >= 24) {
                vmPolicy = StrictMode.getVmPolicy();
                StrictMode.setVmPolicy(new Builder().penaltyLog().build());
            }
            this.mContext.startActivity(intent);
            if (vmPolicy != null) {
                StrictMode.setVmPolicy(vmPolicy);
                return;
            }
            return;
        }
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
            builder.setTitle(R.string.hockeyapp_download_failed_dialog_title);
            builder.setMessage(this.mDownloadErrorMessage == null ? this.mContext.getString(R.string.hockeyapp_download_failed_dialog_message) : this.mDownloadErrorMessage);
            builder.setNegativeButton(R.string.hockeyapp_download_failed_dialog_negative_button, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    DownloadFileTask.this.mNotifier.downloadFailed(DownloadFileTask.this, Boolean.valueOf(false));
                }
            });
            builder.setPositiveButton(R.string.hockeyapp_download_failed_dialog_positive_button, new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    DownloadFileTask.this.mNotifier.downloadFailed(DownloadFileTask.this, Boolean.valueOf(true));
                }
            });
            builder.create().show();
        } catch (Exception e2) {
        }
    }

    protected void onProgressUpdate(Integer... numArr) {
        try {
            if (this.mProgressDialog == null) {
                this.mProgressDialog = new ProgressDialog(this.mContext);
                this.mProgressDialog.setProgressStyle(1);
                this.mProgressDialog.setMessage("Loading...");
                this.mProgressDialog.setCancelable(false);
                this.mProgressDialog.show();
            }
            this.mProgressDialog.setProgress(numArr[0].intValue());
        } catch (Exception e) {
        }
    }

    protected void setConnectionProperties(HttpURLConnection httpURLConnection) {
        httpURLConnection.addRequestProperty("User-Agent", "HockeySDK/Android");
        httpURLConnection.setInstanceFollowRedirects(true);
        if (VERSION.SDK_INT <= 9) {
            httpURLConnection.setRequestProperty("connection", "close");
        }
    }
}
