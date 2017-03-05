package com.microsoft.xbox.idp.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;

public class Suggestions {

    public static class Request {
        public int Algorithm;
        public int Count;
        public String Locale;
        public String Seed;
    }

    public static class Response implements Parcelable {
        public static final Creator<Response> CREATOR = new Creator<Response>() {
            public Response createFromParcel(Parcel parcel) {
                return new Response(parcel);
            }

            public Response[] newArray(int i) {
                return new Response[i];
            }
        };
        public ArrayList<String> Gamertags;

        protected Response(Parcel parcel) {
            this.Gamertags = parcel.createStringArrayList();
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeStringList(this.Gamertags);
        }
    }
}
