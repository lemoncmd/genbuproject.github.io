package com.microsoft.onlineid;

import android.os.Bundle;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;

public class UserAccount {
    private final AccountManager _accountManager;
    private final String _cid;
    private final String _puid;
    private final String _username;

    UserAccount(AccountManager accountManager, AuthenticatorUserAccount authenticatorUserAccount) {
        this(accountManager, authenticatorUserAccount.getCid(), authenticatorUserAccount.getPuid(), authenticatorUserAccount.getUsername());
    }

    UserAccount(AccountManager accountManager, String str, String str2, String str3) {
        this._accountManager = accountManager;
        this._cid = str;
        this._puid = str2;
        this._username = str3;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof UserAccount)) {
            return false;
        }
        UserAccount userAccount = (UserAccount) obj;
        return Objects.equals(this._puid, userAccount._puid) && Objects.equals(this._cid, userAccount._cid);
    }

    public String getCid() {
        return this._cid;
    }

    String getPuid() {
        return this._puid;
    }

    public void getTicket(ISecurityScope iSecurityScope, Bundle bundle) {
        this._accountManager.getTicket(this, iSecurityScope, bundle);
    }

    public String getUsername() {
        return this._username;
    }

    public int hashCode() {
        return Objects.hashCode(this._puid) + Objects.hashCode(this._cid);
    }
}
