package com.microsoft.xbox.idp.ui;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.util.Log;
import com.microsoft.xbox.idp.R;

public class AccountProvisioningResult implements Parcelable {
    public static final Creator<AccountProvisioningResult> CREATOR = new Creator<AccountProvisioningResult>() {
        public AccountProvisioningResult createFromParcel(Parcel parcel) {
            return new AccountProvisioningResult(parcel);
        }

        public AccountProvisioningResult[] newArray(int i) {
            return new AccountProvisioningResult[i];
        }
    };
    private static final String TAG = AccountProvisioningResult.class.getSimpleName();
    private AgeGroup ageGroup;
    private final String gamerTag;
    private final String xuid;

    public enum AgeGroup {
        Adult(R.string.xbid_age_group_adult, R.string.xbid_age_group_adult_details_android),
        Teen(R.string.xbid_age_group_teen, R.string.xbid_age_group_teen_details_android),
        Child(R.string.xbid_age_group_child, R.string.xbid_age_group_child_details_android);
        
        public final int resIdAgeGroup;
        public final int resIdAgeGroupDetails;

        private AgeGroup(int i, int i2) {
            this.resIdAgeGroup = i;
            this.resIdAgeGroupDetails = i2;
        }

        public static AgeGroup fromServiceString(String str) {
            Log.d(AccountProvisioningResult.TAG, "Creating AgeGroup from '" + str + "'");
            if (!TextUtils.isEmpty(str)) {
                if ("adult".compareToIgnoreCase(str) == 0) {
                    return Adult;
                }
                if ("teen".compareToIgnoreCase(str) == 0) {
                    return Teen;
                }
                if ("child".compareToIgnoreCase(str) == 0) {
                    return Child;
                }
            }
            return null;
        }
    }

    protected AccountProvisioningResult(Parcel parcel) {
        this.gamerTag = parcel.readString();
        this.xuid = parcel.readString();
        int readInt = parcel.readInt();
        this.ageGroup = readInt != -1 ? AgeGroup.values()[readInt] : null;
    }

    public AccountProvisioningResult(String str, String str2) {
        this.gamerTag = str;
        this.xuid = str2;
    }

    public int describeContents() {
        return 0;
    }

    public AgeGroup getAgeGroup() {
        return this.ageGroup;
    }

    public String getGamerTag() {
        return this.gamerTag;
    }

    public String getXuid() {
        return this.xuid;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.gamerTag);
        parcel.writeString(this.xuid);
        parcel.writeInt(this.ageGroup != null ? this.ageGroup.ordinal() : -1);
    }
}
