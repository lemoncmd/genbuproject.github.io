package com.microsoft.onlineid.userdata;

import android.accounts.Account;
import android.content.Context;
import android.util.Patterns;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;

public class AccountManagerReader {
    private final AccountManagerWrapper _accountManager;

    public AccountManagerReader(Context context) {
        this(new AccountManagerWrapper(context));
    }

    AccountManagerReader(AccountManagerWrapper accountManagerWrapper) {
        this._accountManager = accountManagerWrapper;
    }

    public String getDeviceEmail() {
        Account[] accountsByType = this._accountManager.getAccountsByType("com.google");
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.GoogleEmail, accountsByType.length == 0 ? ClientAnalytics.DoesntExistInAccountManager : ClientAnalytics.ExistsInAccountManager);
        if (accountsByType.length == 0) {
            return null;
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.GoogleEmailCount, ClientAnalytics.ExistsInAccountManager, Long.valueOf((long) accountsByType.length));
        return accountsByType[0].name;
    }

    public Set<String> getEmails() {
        Set<String> hashSet = new HashSet();
        for (Account account : this._accountManager.getAccounts()) {
            if (Patterns.EMAIL_ADDRESS.matcher(account.name).matches()) {
                hashSet.add(account.name);
            }
        }
        ClientAnalytics.get().logEvent(ClientAnalytics.UserDataCategory, ClientAnalytics.UniqueEmailCount, ClientAnalytics.ExistsInAccountManager, Long.valueOf((long) hashSet.size()));
        return hashSet;
    }

    public String getEmailsAsJsonArray() {
        Set<String> emails = getEmails();
        JSONArray jSONArray = new JSONArray();
        for (String put : emails) {
            jSONArray.put(put);
        }
        return jSONArray.toString();
    }
}
