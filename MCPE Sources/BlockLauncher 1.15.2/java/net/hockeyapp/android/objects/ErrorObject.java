package net.hockeyapp.android.objects;

import java.io.Serializable;

public class ErrorObject implements Serializable {
    private static final long serialVersionUID = 1508110658372169868L;
    private int mCode;
    private String mMessage;

    public int getCode() {
        return this.mCode;
    }

    public String getMessage() {
        return this.mMessage;
    }

    public void setCode(int i) {
        this.mCode = i;
    }

    public void setMessage(String str) {
        this.mMessage = str;
    }
}
