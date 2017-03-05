package net.hockeyapp.android.listeners;

import net.hockeyapp.android.tasks.DownloadFileTask;

public abstract class DownloadFileListener {
    public void downloadFailed(DownloadFileTask downloadFileTask, Boolean bool) {
    }

    public void downloadSuccessful(DownloadFileTask downloadFileTask) {
    }
}
