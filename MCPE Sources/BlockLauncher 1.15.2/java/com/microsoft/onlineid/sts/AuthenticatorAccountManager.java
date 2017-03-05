package com.microsoft.onlineid.sts;

import android.content.Context;
import com.microsoft.onlineid.internal.profile.DownloadProfileImageTask;
import com.microsoft.onlineid.internal.storage.TypedStorage;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AuthenticatorAccountManager {
    private final Context _applicationContext;
    private final TypedStorage _typedStorage;

    public AuthenticatorAccountManager(Context context) {
        this._applicationContext = context;
        this._typedStorage = new TypedStorage(context);
    }

    public AuthenticatorAccountManager(TypedStorage typedStorage) {
        this._applicationContext = null;
        this._typedStorage = typedStorage;
    }

    public AuthenticatorUserAccount getAccountByCid(String str) {
        for (AuthenticatorUserAccount authenticatorUserAccount : this._typedStorage.readAllAccounts()) {
            if (str.equalsIgnoreCase(authenticatorUserAccount.getCid())) {
                return authenticatorUserAccount;
            }
        }
        return null;
    }

    public AuthenticatorUserAccount getAccountByPuid(String str) {
        return this._typedStorage.readAccount(str);
    }

    public Set<AuthenticatorUserAccount> getAccounts() {
        return this._typedStorage.readAllAccounts();
    }

    public Set<AuthenticatorUserAccount> getFilteredAccounts(Set<String> set) {
        Set<AuthenticatorUserAccount> readAllAccounts = this._typedStorage.readAllAccounts();
        if (!(set == null || set.isEmpty())) {
            Iterator it = readAllAccounts.iterator();
            while (it.hasNext()) {
                if (set.contains(((AuthenticatorUserAccount) it.next()).getCid())) {
                    it.remove();
                }
            }
        }
        return readAllAccounts;
    }

    public Set<AuthenticatorUserAccount> getSessionApprovalAccounts() {
        Set<AuthenticatorUserAccount> hashSet = new HashSet();
        for (AuthenticatorUserAccount authenticatorUserAccount : getAccounts()) {
            if (authenticatorUserAccount.isSessionApprover()) {
                hashSet.add(authenticatorUserAccount);
            }
        }
        return hashSet;
    }

    public boolean hasAccounts() {
        return this._typedStorage.hasAccounts();
    }

    public boolean hasNgcSessionApprovalAccounts() {
        for (AuthenticatorUserAccount hasNgcRegistrationSucceeded : getAccounts()) {
            if (hasNgcRegistrationSucceeded.hasNgcRegistrationSucceeded()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSessionApprovalAccounts() {
        for (AuthenticatorUserAccount isSessionApprover : getAccounts()) {
            if (isSessionApprover.isSessionApprover()) {
                return true;
            }
        }
        return false;
    }

    void removeLastSavedUserTileImage(AuthenticatorUserAccount authenticatorUserAccount) {
        File fileStreamPath = this._applicationContext.getFileStreamPath(authenticatorUserAccount.getPuid() + DownloadProfileImageTask.UserTileExtension);
        if (fileStreamPath.exists()) {
            fileStreamPath.delete();
        }
    }
}
