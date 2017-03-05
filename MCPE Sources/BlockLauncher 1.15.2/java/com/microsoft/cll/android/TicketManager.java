package com.microsoft.cll.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketManager {
    private final String TAG = "AndroidCll-TicketManager";
    private final ITicketCallback callback;
    private final ILogger logger;
    private boolean needDeviceTicket = true;
    private final Map<String, String> tickets;

    public TicketManager(ITicketCallback iTicketCallback, ILogger iLogger) {
        this.callback = iTicketCallback;
        this.logger = iLogger;
        this.tickets = new HashMap();
    }

    public void addTickets(List<String> list) {
        if (list != null && this.callback != null) {
            for (String str : list) {
                if (this.tickets.containsKey(str)) {
                    this.logger.info("AndroidCll-TicketManager", "We already have a ticket for this id, skipping.");
                } else {
                    this.logger.info("AndroidCll-TicketManager", "Getting ticket for " + str);
                    TicketObject xTicketForXuid = this.callback.getXTicketForXuid(str);
                    Object obj = xTicketForXuid.ticket;
                    if (xTicketForXuid.hasDeviceClaims) {
                        this.needDeviceTicket = false;
                        obj = "rp:" + obj;
                    }
                    this.tickets.put(str, obj);
                }
            }
        }
    }

    public void clean() {
        this.tickets.clear();
        this.needDeviceTicket = true;
    }

    public TicketHeaders getHeaders(boolean z) {
        if (this.callback == null || this.tickets.isEmpty()) {
            return null;
        }
        TicketHeaders ticketHeaders = new TicketHeaders();
        ticketHeaders.authXToken = this.callback.getAuthXToken(z);
        ticketHeaders.xtokens = this.tickets;
        if (!this.needDeviceTicket) {
            return ticketHeaders;
        }
        ticketHeaders.msaDeviceTicket = this.callback.getMsaDeviceTicket(z);
        return ticketHeaders;
    }
}
