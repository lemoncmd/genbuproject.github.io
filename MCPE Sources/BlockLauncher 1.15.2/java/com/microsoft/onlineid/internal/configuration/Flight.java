package com.microsoft.onlineid.internal.configuration;

public enum Flight {
    QRCode(11, "qr_code");
    
    private final int _flightID;
    private final String _flightName;

    private Flight(int i, String str) {
        this._flightID = i;
        this._flightName = str;
    }

    public int getFlightID() {
        return this._flightID;
    }

    public String getFlightName() {
        return this._flightName;
    }
}
