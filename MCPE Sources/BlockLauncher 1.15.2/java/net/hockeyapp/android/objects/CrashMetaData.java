package net.hockeyapp.android.objects;

public class CrashMetaData {
    private String mUserDescription;
    private String mUserEmail;
    private String mUserID;

    public String getUserDescription() {
        return this.mUserDescription;
    }

    public String getUserEmail() {
        return this.mUserEmail;
    }

    public String getUserID() {
        return this.mUserID;
    }

    public void setUserDescription(String str) {
        this.mUserDescription = str;
    }

    public void setUserEmail(String str) {
        this.mUserEmail = str;
    }

    public void setUserID(String str) {
        this.mUserID = str;
    }

    public String toString() {
        return "\n" + CrashMetaData.class.getSimpleName() + "\n" + "userDescription " + this.mUserDescription + "\n" + "userEmail       " + this.mUserEmail + "\n" + "userID          " + this.mUserID;
    }
}
