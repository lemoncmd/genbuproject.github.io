package com.microsoft.cll.android;

public interface ITicketCallback {
    String getAuthXToken(boolean z);

    String getMsaDeviceTicket(boolean z);

    TicketObject getXTicketForXuid(String str);
}
