package com.microsoft.xbox.idp.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class AuthFlowResult implements Parcelable {
    public static final Creator<AuthFlowResult> CREATOR = new Creator<AuthFlowResult>() {
        public AuthFlowResult createFromParcel(Parcel parcel) {
            return new AuthFlowResult(parcel);
        }

        public AuthFlowResult[] newArray(int i) {
            return new AuthFlowResult[i];
        }
    };
    private final boolean deleteOnFinalize;
    private final long id;

    public AuthFlowResult(long j) {
        this(j, false);
    }

    public AuthFlowResult(long j, boolean z) {
        this.id = j;
        this.deleteOnFinalize = z;
    }

    protected AuthFlowResult(Parcel parcel) {
        this.id = parcel.readLong();
        this.deleteOnFinalize = parcel.readByte() != (byte) 0;
    }

    private static native void delete(long j);

    private static native String getAgeGroup(long j);

    private static native String getGamerTag(long j);

    private static native String getPrivileges(long j);

    private static native String getRpsTicket(long j);

    private static native String getUserId(long j);

    public int describeContents() {
        return 0;
    }

    protected void finalize() throws Throwable {
        if (this.deleteOnFinalize) {
            delete(this.id);
        }
        super.finalize();
    }

    public String getAgeGroup() {
        return getAgeGroup(this.id);
    }

    public String getGamerTag() {
        return getGamerTag(this.id);
    }

    public String getPrivileges() {
        return getPrivileges(this.id);
    }

    public String getRpsTicket() {
        return getRpsTicket(this.id);
    }

    public String getUserId() {
        return getUserId(this.id);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.id);
        parcel.writeByte((byte) (this.deleteOnFinalize ? 1 : 0));
    }
}
