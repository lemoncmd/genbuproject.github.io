package com.microsoft.xbox.idp.util;

import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class FragmentLoaderKey implements Parcelable {
    static final /* synthetic */ boolean $assertionsDisabled = (!FragmentLoaderKey.class.desiredAssertionStatus());
    public static final Creator<FragmentLoaderKey> CREATOR = new Creator<FragmentLoaderKey>() {
        public FragmentLoaderKey createFromParcel(Parcel parcel) {
            return new FragmentLoaderKey(parcel);
        }

        public FragmentLoaderKey[] newArray(int i) {
            return new FragmentLoaderKey[i];
        }
    };
    private final String className;
    private final int loaderId;

    protected FragmentLoaderKey(Parcel parcel) {
        this.className = parcel.readString();
        this.loaderId = parcel.readInt();
    }

    public FragmentLoaderKey(Class<? extends Fragment> cls, int i) {
        if ($assertionsDisabled || cls != null) {
            this.className = cls.getName();
            this.loaderId = i;
            return;
        }
        throw new AssertionError();
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        FragmentLoaderKey fragmentLoaderKey = (FragmentLoaderKey) obj;
        return this.loaderId == fragmentLoaderKey.loaderId ? this.className.equals(fragmentLoaderKey.className) : false;
    }

    public int hashCode() {
        return (this.className.hashCode() * 31) + this.loaderId;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.className);
        parcel.writeInt(this.loaderId);
    }
}
