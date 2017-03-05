package com.microsoft.cll.android;

public class TicketObject {
    public boolean hasDeviceClaims;
    public String ticket;

    public TicketObject(String str, boolean z) {
        this.ticket = str;
        this.hasDeviceClaims = z;
    }
}
