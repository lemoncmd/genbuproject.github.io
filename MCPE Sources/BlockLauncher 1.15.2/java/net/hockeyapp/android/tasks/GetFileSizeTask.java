package net.hockeyapp.android.tasks;

import android.content.Context;
import java.io.IOException;
import java.net.URL;
import net.hockeyapp.android.listeners.DownloadFileListener;

public class GetFileSizeTask extends DownloadFileTask {
    private long mSize;

    public GetFileSizeTask(Context context, String str, DownloadFileListener downloadFileListener) {
        super(context, str, downloadFileListener);
    }

    protected Long doInBackground(Void... voidArr) {
        try {
            return Long.valueOf((long) createConnection(new URL(getURLString()), 6).getContentLength());
        } catch (IOException e) {
            e.printStackTrace();
            return Long.valueOf(0);
        }
    }

    public long getSize() {
        return this.mSize;
    }

    protected void onPostExecute(Long l) {
        this.mSize = l.longValue();
        if (this.mSize > 0) {
            this.mNotifier.downloadSuccessful(this);
        } else {
            this.mNotifier.downloadFailed(this, Boolean.valueOf(false));
        }
    }

    protected void onProgressUpdate(Integer... numArr) {
    }
}
