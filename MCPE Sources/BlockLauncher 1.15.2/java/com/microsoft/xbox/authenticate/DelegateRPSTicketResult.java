package com.microsoft.xbox.authenticate;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class DelegateRPSTicketResult implements Parcelable {
    public static final Creator<DelegateRPSTicketResult> CREATOR = new Creator<DelegateRPSTicketResult>() {
        public DelegateRPSTicketResult createFromParcel(Parcel parcel) {
            return new DelegateRPSTicketResult(parcel);
        }

        public DelegateRPSTicketResult[] newArray(int i) {
            return new DelegateRPSTicketResult[i];
        }
    };
    public static int RESULT_NOCID = 1;
    public static int RESULT_SUCCESS = 0;
    public static int RESULT_UNEXPECTED = 2;
    private int errorCode;
    private PendingIntent pendingIntent;
    private String ticket;

    private DelegateRPSTicketResult(Parcel parcel) {
        readFromParcel(parcel);
    }

    public int describeContents() {
        return 0;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public PendingIntent getPendingIntent() {
        return this.pendingIntent;
    }

    public String getTicket() {
        return this.ticket;
    }

    public void readFromParcel(Parcel parcel) {
        this.errorCode = parcel.readInt();
        this.ticket = parcel.readString();
        this.pendingIntent = PendingIntent.readPendingIntentOrNullFromParcel(parcel);
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.errorCode);
        parcel.writeString(this.ticket);
        PendingIntent.writePendingIntentOrNullToParcel(this.pendingIntent, parcel);
    }
}
