package net.zhuoweizhang.mcpelauncher.ui;

import android.app.Activity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RefreshContentListThread implements Runnable {
    protected final Activity mContext;
    protected final OnRefreshContentList mListener;

    public interface OnRefreshContentList {
        List<File> getFolders();

        boolean isEnabled(File file);

        void onRefreshComplete(List<ContentListItem> list);
    }

    public RefreshContentListThread(Activity ctx, OnRefreshContentList listener) {
        this.mContext = ctx;
        this.mListener = listener;
    }

    public void run() {
        List<ContentListItem> items = new ArrayList();
        for (File folder : this.mListener.getFolders()) {
            combOneFolder(folder, items);
        }
        ContentListItem.sort(items);
        this.mListener.onRefreshComplete(items);
    }

    private void combOneFolder(File patchesFolder, List<ContentListItem> patches) {
        if (patchesFolder.exists()) {
            for (File patchFile : patchesFolder.listFiles()) {
                patches.add(new ContentListItem(patchFile, this.mListener.isEnabled(patchFile)));
            }
            return;
        }
        System.err.println("no storage folder");
    }
}
