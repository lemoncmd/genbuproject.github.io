package net.hockeyapp.android.objects;

import java.io.File;
import java.io.FilenameFilter;
import java.io.Serializable;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.Constants;

public class FeedbackAttachment implements Serializable {
    private static final long serialVersionUID = 5059651319640956830L;
    private String mCreatedAt;
    private String mFilename;
    private int mId;
    private int mMessageId;
    private String mUpdatedAt;
    private String mUrl;

    public String getCacheId() {
        return BuildConfig.FLAVOR + this.mMessageId + this.mId;
    }

    public String getCreatedAt() {
        return this.mCreatedAt;
    }

    public String getFilename() {
        return this.mFilename;
    }

    public int getId() {
        return this.mId;
    }

    public int getMessageId() {
        return this.mMessageId;
    }

    public String getUpdatedAt() {
        return this.mUpdatedAt;
    }

    public String getUrl() {
        return this.mUrl;
    }

    public boolean isAvailableInCache() {
        File hockeyAppStorageDir = Constants.getHockeyAppStorageDir();
        if (!hockeyAppStorageDir.exists() || !hockeyAppStorageDir.isDirectory()) {
            return false;
        }
        File[] listFiles = hockeyAppStorageDir.listFiles(new FilenameFilter() {
            public boolean accept(File file, String str) {
                return str.equals(FeedbackAttachment.this.getCacheId());
            }
        });
        return listFiles != null && listFiles.length == 1;
    }

    public void setCreatedAt(String str) {
        this.mCreatedAt = str;
    }

    public void setFilename(String str) {
        this.mFilename = str;
    }

    public void setId(int i) {
        this.mId = i;
    }

    public void setMessageId(int i) {
        this.mMessageId = i;
    }

    public void setUpdatedAt(String str) {
        this.mUpdatedAt = str;
    }

    public void setUrl(String str) {
        this.mUrl = str;
    }

    public String toString() {
        return "\n" + FeedbackAttachment.class.getSimpleName() + "\n" + "id         " + this.mId + "\n" + "message id " + this.mMessageId + "\n" + "filename   " + this.mFilename + "\n" + "url        " + this.mUrl + "\n" + "createdAt  " + this.mCreatedAt + "\n" + "updatedAt  " + this.mUpdatedAt;
    }
}
