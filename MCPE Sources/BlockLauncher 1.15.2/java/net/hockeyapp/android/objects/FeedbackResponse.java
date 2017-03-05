package net.hockeyapp.android.objects;

import java.io.Serializable;

public class FeedbackResponse implements Serializable {
    private static final long serialVersionUID = -1093570359639034766L;
    private Feedback mFeedback;
    private String mStatus;
    private String mToken;

    public Feedback getFeedback() {
        return this.mFeedback;
    }

    public String getStatus() {
        return this.mStatus;
    }

    public String getToken() {
        return this.mToken;
    }

    public void setFeedback(Feedback feedback) {
        this.mFeedback = feedback;
    }

    public void setStatus(String str) {
        this.mStatus = str;
    }

    public void setToken(String str) {
        this.mToken = str;
    }
}
