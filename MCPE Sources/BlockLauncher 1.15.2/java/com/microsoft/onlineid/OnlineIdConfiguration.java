package com.microsoft.onlineid;

public class OnlineIdConfiguration {
    private String _cobrandingId;
    private final PreferredSignUpMemberNameType _preferredSignUpMemberNameType;
    private boolean _requestWebTelemetry;

    public enum PreferredSignUpMemberNameType {
        None(null),
        Email("easi2"),
        Outlook("wld2"),
        Telephone("phone2"),
        TelephoneOnly("phone"),
        TelephoneEvenIfBlank("phone3");
        
        private final String _qsValue;

        private PreferredSignUpMemberNameType(String str) {
            this._qsValue = str;
        }

        public String toString() {
            return this._qsValue;
        }
    }

    public OnlineIdConfiguration() {
        this(PreferredSignUpMemberNameType.None);
    }

    public OnlineIdConfiguration(PreferredSignUpMemberNameType preferredSignUpMemberNameType) {
        this._preferredSignUpMemberNameType = preferredSignUpMemberNameType;
        this._requestWebTelemetry = false;
    }

    public String getCobrandingId() {
        return this._cobrandingId;
    }

    public PreferredSignUpMemberNameType getPreferredSignUpMemberNameType() {
        return this._preferredSignUpMemberNameType;
    }

    public boolean getShouldGatherWebTelemetry() {
        return this._requestWebTelemetry;
    }

    public OnlineIdConfiguration setCobrandingId(String str) {
        this._cobrandingId = str;
        return this;
    }

    public OnlineIdConfiguration setShouldGatherWebTelemetry(boolean z) {
        this._requestWebTelemetry = z;
        return this;
    }
}
