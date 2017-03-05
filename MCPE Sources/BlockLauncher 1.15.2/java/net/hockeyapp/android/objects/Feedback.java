package net.hockeyapp.android.objects;

import java.io.Serializable;
import java.util.ArrayList;

public class Feedback implements Serializable {
    private static final long serialVersionUID = 2590172806951065320L;
    private String mCreatedAt;
    private String mEmail;
    private int mId;
    private ArrayList<FeedbackMessage> mMessages;
    private String mName;

    public String getCreatedAt() {
        return this.mCreatedAt;
    }

    public String getEmail() {
        return this.mEmail;
    }

    public int getId() {
        return this.mId;
    }

    public ArrayList<FeedbackMessage> getMessages() {
        return this.mMessages;
    }

    public String getName() {
        return this.mName;
    }

    public void setCreatedAt(String str) {
        this.mCreatedAt = str;
    }

    public void setEmail(String str) {
        this.mEmail = str;
    }

    public void setId(int i) {
        this.mId = i;
    }

    public void setMessages(ArrayList<FeedbackMessage> arrayList) {
        this.mMessages = arrayList;
    }

    public void setName(String str) {
        this.mName = str;
    }
}
