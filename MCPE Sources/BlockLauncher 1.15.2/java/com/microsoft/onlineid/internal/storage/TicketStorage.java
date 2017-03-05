package com.microsoft.onlineid.internal.storage;

import android.content.Context;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.sts.ClockSkewManager;
import java.util.Date;

public class TicketStorage {
    private final ClockSkewManager _clockSkewManager;
    private TypedStorage _typedStorage;

    public TicketStorage(Context context) {
        this._clockSkewManager = new ClockSkewManager(context);
        this._typedStorage = new TypedStorage(context);
    }

    static void checkCommonParameters(String str, String str2) {
        Strings.verifyArgumentNotNullOrEmpty(str, "accountId");
        Strings.verifyArgumentNotNullOrEmpty(str2, "appId");
    }

    public Ticket getTicket(String str, String str2, ISecurityScope iSecurityScope) {
        checkCommonParameters(str, str2);
        Objects.verifyArgumentNotNull(iSecurityScope, "scope");
        Ticket ticket = this._typedStorage.getTicket(str, str2, iSecurityScope);
        if (ticket == null || isTicketValid(ticket.getExpiry())) {
            return ticket;
        }
        this._typedStorage.removeTicket(str, str2, iSecurityScope);
        return null;
    }

    boolean isTicketValid(Date date) {
        return this._clockSkewManager.getCurrentServerTime().compareTo(date) < 0;
    }

    public void removeTickets(String str) {
        Strings.verifyArgumentNotNullOrEmpty(str, "accountId");
        this._typedStorage.removeTickets(str);
    }

    void setTypedStorage(TypedStorage typedStorage) {
        Objects.verifyArgumentNotNull(typedStorage, "typedStorage");
        this._typedStorage = typedStorage;
    }

    public void storeTicket(String str, String str2, Ticket ticket) {
        checkCommonParameters(str, str2);
        Objects.verifyArgumentNotNull(ticket, "ticket");
        if (isTicketValid(ticket.getExpiry())) {
            this._typedStorage.storeTicket(str, str2, ticket);
        }
    }
}
