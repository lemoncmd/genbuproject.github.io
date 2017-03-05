package net.hockeyapp.android.objects;

import java.io.Serializable;
import java.util.List;

public class FeedbackMessage implements Serializable {
    private static final long serialVersionUID = -8773015828853994624L;
    private String mAppId;
    private String mCleanText;
    private String mCreatedAt;
    private String mDeviceModel;
    private String mDeviceOem;
    private String mDeviceOsVersion;
    private List<FeedbackAttachment> mFeedbackAttachments;
    private int mId;
    private String mName;
    private String mSubject;
    private String mText;
    private String mToken;
    private String mUserString;
    private int mVia;

    public String getAppId() {
        return this.mAppId;
    }

    public String getCleanText() {
        return this.mCleanText;
    }

    public String getCreatedAt() {
        return this.mCreatedAt;
    }

    public List<FeedbackAttachment> getFeedbackAttachments() {
        return this.mFeedbackAttachments;
    }

    public int getId() {
        return this.mId;
    }

    public String getModel() {
        return this.mDeviceModel;
    }

    public String getName() {
        return this.mName;
    }

    public String getOem() {
        return this.mDeviceOem;
    }

    public String getOsVersion() {
        return this.mDeviceOsVersion;
    }

    @Deprecated
    public String getSubjec() {
        return this.mSubject;
    }

    public String getSubject() {
        return this.mSubject;
    }

    public String getText() {
        return this.mText;
    }

    public String getToken() {
        return this.mToken;
    }

    public String getUserString() {
        return this.mUserString;
    }

    public int getVia() {
        return this.mVia;
    }

    public void setAppId(String str) {
        this.mAppId = str;
    }

    public void setCleanText(String str) {
        this.mCleanText = str;
    }

    public void setCreatedAt(String str) {
        this.mCreatedAt = str;
    }

    public void setFeedbackAttachments(List<FeedbackAttachment> list) {
        this.mFeedbackAttachments = list;
    }

    public void setId(int i) {
        this.mId = i;
    }

    public void setModel(String str) {
        this.mDeviceModel = str;
    }

    public void setName(String str) {
        this.mName = str;
    }

    public void setOem(String str) {
        this.mDeviceOem = str;
    }

    public void setOsVersion(String str) {
        this.mDeviceOsVersion = str;
    }

    @Deprecated
    public void setSubjec(String str) {
        this.mSubject = str;
    }

    public void setSubject(String str) {
        this.mSubject = str;
    }

    public void setText(String str) {
        this.mText = str;
    }

    public void setToken(String str) {
        this.mToken = str;
    }

    public void setUserString(String str) {
        this.mUserString = str;
    }

    public void setVia(int i) {
        this.mVia = i;
    }
}
