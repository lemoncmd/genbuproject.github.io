package com.microsoft.onlineid.userdata;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class AccountManagerWrapper {
    private final AccountManager _accountManager;

    public AccountManagerWrapper(Context context) {
        this._accountManager = AccountManager.get(context);
    }

    public Account[] getAccounts() {
        return this._accountManager.getAccounts();
    }

    public Account[] getAccountsByType(String str) {
        return this._accountManager.getAccountsByType(str);
    }
}
