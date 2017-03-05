package com.microsoft.onlineid.userdata;

import android.content.Context;

public class MeContactReader implements IPhoneNumberReader {

    public class FullName {
        public String _firstName;
        public String _lastName;

        public FullName(String firstName, String lastName) {
            this._firstName = firstName;
            this._lastName = lastName;
        }

        public String getFirstName() {
            return this._firstName;
        }

        public String getLastName() {
            return this._lastName;
        }
    }

    public MeContactReader(Context ctx) {
    }

    public FullName getFullName() {
        return new FullName(null, null);
    }

    public String getPhoneNumber() {
        return null;
    }
}
