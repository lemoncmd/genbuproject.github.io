package com.microsoft.onlineid.internal.storage;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import com.microsoft.onlineid.ISecurityScope;
import com.microsoft.onlineid.Ticket;
import com.microsoft.onlineid.analytics.ClientAnalytics;
import com.microsoft.onlineid.internal.Assertion;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import com.microsoft.onlineid.internal.sso.BundleMarshaller;
import com.microsoft.onlineid.internal.sso.BundleMarshallerException;
import com.microsoft.onlineid.internal.storage.Storage.Editor;
import com.microsoft.onlineid.sts.AuthenticatorUserAccount;
import com.microsoft.onlineid.sts.DeviceIdentity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TypedStorage {
    protected static final String AccountKeyToken = "Account";
    protected static final String AccountsCollectionKey = "Accounts";
    protected static final String ClockSkewKey = "ClockSkew";
    protected static final Object CollectionLock = new Object();
    protected static final String ConfigLastDownloadedTimeKey = "ConfigLastDownloadedTime";
    protected static final String DeviceBasedFlightsKey = "DeviceBasedFlights";
    protected static final String DeviceFlightOverrideKey = "DeviceFlightOverride";
    protected static final String DeviceIdentityKey = "Device";
    protected static final String FormatSeparator = "|";
    protected static final String LastBackupPushedTimeKey = "LastBackupPushedTime";
    protected static final String LastBackupReceivedTimeKey = "LastBackupReceivedTime";
    protected static final String SdkVersionKey = "SdkVersion";
    protected static final String TicketCollectionKeyToken = "Tickets";
    protected static final String TicketKeyToken = "Ticket";
    protected final Storage _storage;

    public TypedStorage(Context context) {
        Objects.verifyArgumentNotNull(context, "applicationContext");
        this._storage = new Storage(context);
    }

    protected TypedStorage(Storage storage) {
        this._storage = storage;
    }

    protected static String constructAccountKey(String str) {
        return constructKey(AccountKeyToken, str.toLowerCase(Locale.US));
    }

    protected static String constructKey(Object... objArr) {
        return TextUtils.join(FormatSeparator, objArr);
    }

    protected static String constructTicketCollectionKey(String str) {
        return constructKey(TicketCollectionKeyToken, str.toLowerCase(Locale.US));
    }

    protected static String constructTicketCollectionKeyFromAccountKey(String str) {
        return str.replace(AccountKeyToken, TicketCollectionKeyToken);
    }

    protected static String constructTicketKey(String str, String str2, ISecurityScope iSecurityScope) {
        Objects.verifyArgumentNotNull(iSecurityScope.getTarget(), "Ticket target");
        Objects.verifyArgumentNotNull(iSecurityScope.getPolicy(), "Ticket policy");
        return constructKey(TicketKeyToken, str.toLowerCase(Locale.US), str2.toLowerCase(Locale.US), iSecurityScope.getTarget().toLowerCase(Locale.US), iSecurityScope.getPolicy().toLowerCase(Locale.US));
    }

    private boolean hasTickets(String str) {
        return hasCollection(constructTicketCollectionKey(str));
    }

    private <T> void replaceCollection(String str, Map<String, String> map, Editor editor) {
        for (String remove : this._storage.readStringSet(str)) {
            editor.remove(remove);
        }
        if (map != null) {
            for (Entry entry : map.entrySet()) {
                editor.writeString((String) entry.getKey(), (String) entry.getValue());
            }
            editor.writeStringSet(str, map.keySet());
            return;
        }
        editor.remove(str);
    }

    public boolean clearSynchronous() {
        boolean commit;
        synchronized (CollectionLock) {
            commit = this._storage.edit().clear().commit();
        }
        return commit;
    }

    public void deleteDeviceIdentity() {
        this._storage.edit().remove(DeviceIdentityKey).apply();
    }

    protected ISerializer<AuthenticatorUserAccount> getAccountSerializer() {
        return new ObjectStreamSerializer();
    }

    protected ISerializer<DeviceIdentity> getDeviceIdentitySerializer() {
        return new ObjectStreamSerializer();
    }

    protected Ticket getTicket(String str, String str2, ISecurityScope iSecurityScope) {
        return (Ticket) readFromCollection(constructTicketCollectionKey(str), constructTicketKey(str, str2, iSecurityScope), getTicketSerializer());
    }

    protected ISerializer<Ticket> getTicketSerializer() {
        return new ObjectStreamSerializer();
    }

    public boolean hasAccounts() {
        return hasCollection(AccountsCollectionKey);
    }

    protected boolean hasCollection(String str) {
        boolean z;
        synchronized (CollectionLock) {
            z = !this._storage.readStringSet(str).isEmpty();
        }
        return z;
    }

    public AuthenticatorUserAccount readAccount(String str) {
        return (AuthenticatorUserAccount) readFromCollection(AccountsCollectionKey, constructAccountKey(str), getAccountSerializer());
    }

    public Set<AuthenticatorUserAccount> readAllAccounts() {
        return readCollection(AccountsCollectionKey, getAccountSerializer());
    }

    public long readClockSkew() {
        return this._storage.readLong(ClockSkewKey, 0);
    }

    protected <T> Set<T> readCollection(String str, ISerializer<T> iSerializer) {
        Map hashMap = new HashMap();
        synchronized (CollectionLock) {
            Set<String> readStringSet = this._storage.readStringSet(str);
            Set hashSet = new HashSet(readStringSet);
            for (String str2 : readStringSet) {
                String readString = this._storage.readString(str2);
                if (readString != null) {
                    hashMap.put(str2, readString);
                } else {
                    Assertion.check(false, "Stored collection value was null.");
                    hashSet.remove(str2);
                }
            }
            if (hashSet.size() != readStringSet.size()) {
                Logger.error("Key set was out of sync for collection: " + str);
                int indexOf = str.indexOf(FormatSeparator);
                ClientAnalytics.get().logEvent(ClientAnalytics.StorageCategory, ClientAnalytics.CollectionConsistencyError, indexOf > 0 ? str.substring(0, indexOf) : str);
                this._storage.edit().writeStringSet(str, hashSet).apply();
            }
        }
        Set<T> emptySet = Collections.emptySet();
        try {
            emptySet = iSerializer.deserializeAll(hashMap);
        } catch (Throwable e) {
            Logger.error("Unable to deserialize indexed collection.", e);
        }
        return emptySet;
    }

    public long readConfigLastDownloadedTime() {
        return this._storage.readLong(ConfigLastDownloadedTimeKey, 0);
    }

    public Set<Integer> readDeviceBasedFlights() {
        Set<String> readStringSet = this._storage.readStringSet(DeviceBasedFlightsKey);
        Set hashSet = new HashSet(readStringSet.size());
        for (String parseInt : readStringSet) {
            hashSet.add(Integer.valueOf(Integer.parseInt(parseInt)));
        }
        return hashSet;
    }

    public boolean readDeviceFlightOverrideEnabled() {
        return this._storage.readBoolean(DeviceFlightOverrideKey, false);
    }

    public DeviceIdentity readDeviceIdentity() {
        return (DeviceIdentity) this._storage.readObject(DeviceIdentityKey, getDeviceIdentitySerializer());
    }

    protected <T> T readFromCollection(String str, String str2, ISerializer<T> iSerializer) {
        String readString = this._storage.readString(str2);
        T t = null;
        if (readString != null) {
            try {
                t = iSerializer.deserialize(readString);
                if (t == null) {
                    removeFromCollection(str, str2);
                }
            } catch (IOException e) {
                Logger.warning(String.format(Locale.US, "Value in storage at '%s' was corrupt.", new Object[]{str2}));
                removeFromCollection(str, str2);
            } catch (Throwable th) {
                removeFromCollection(str, str2);
            }
        }
        return t;
    }

    public long readLastBackupPushedTime() {
        return this._storage.readLong(LastBackupPushedTimeKey, 0);
    }

    public long readLastBackupReceivedTime() {
        return this._storage.readLong(LastBackupReceivedTimeKey, 0);
    }

    public String readSdkVersion() {
        return this._storage.readString(SdkVersionKey);
    }

    public void removeAccount(String str) {
        removeFromCollection(AccountsCollectionKey, constructAccountKey(str));
        removeTickets(str);
    }

    protected void removeCollection(String str) {
        synchronized (CollectionLock) {
            Editor edit = this._storage.edit();
            replaceCollection(str, null, edit);
            edit.apply();
        }
    }

    protected void removeFromCollection(String str, Collection<String> collection) {
        if (!collection.isEmpty()) {
            Editor edit = this._storage.edit();
            for (String remove : collection) {
                edit.remove(remove);
            }
            synchronized (CollectionLock) {
                Set hashSet = new HashSet(this._storage.readStringSet(str));
                hashSet.removeAll(collection);
                if (hashSet.isEmpty()) {
                    edit.remove(str);
                } else {
                    edit.writeStringSet(str, hashSet);
                }
                edit.apply();
            }
        }
    }

    protected void removeFromCollection(String str, String... strArr) {
        removeFromCollection(str, Arrays.asList(strArr));
    }

    protected void removeTicket(String str, String str2, ISecurityScope iSecurityScope) {
        removeFromCollection(constructTicketCollectionKey(str), constructTicketKey(str, str2, iSecurityScope));
    }

    protected void removeTickets(String str) {
        removeCollection(constructTicketCollectionKey(str));
    }

    protected <T> void replaceCollection(String str, Map<String, T> map, ISerializer<T> iSerializer) {
        try {
            Map serializeAll = iSerializer.serializeAll(map);
            synchronized (CollectionLock) {
                Editor edit = this._storage.edit();
                replaceCollection(str, serializeAll, edit);
                edit.apply();
            }
        } catch (Throwable e) {
            throw new StorageException(e);
        }
    }

    public Bundle retrieveBackup() {
        Bundle bundle = new Bundle();
        DeviceIdentity readDeviceIdentity = readDeviceIdentity();
        if (readDeviceIdentity != null) {
            bundle.putBundle(BundleMarshaller.BackupDeviceKey, BundleMarshaller.deviceAccountToBundle(readDeviceIdentity));
        }
        ArrayList arrayList = new ArrayList();
        for (AuthenticatorUserAccount authenticatorUserAccount : readAllAccounts()) {
            arrayList.add(BundleMarshaller.userAccountToBundle(authenticatorUserAccount));
            if (readDeviceIdentity == null) {
                Assertion.check(!hasTickets(authenticatorUserAccount.getPuid()));
            }
        }
        if (!arrayList.isEmpty()) {
            bundle.putParcelableArrayList(BundleMarshaller.BackupUsersKey, arrayList);
        }
        return bundle;
    }

    public void storeBackup(Bundle bundle) throws BundleMarshallerException {
        Bundle bundle2 = bundle.getBundle(BundleMarshaller.BackupDeviceKey);
        if (bundle2 != null) {
            try {
                String serialize = getDeviceIdentitySerializer().serialize(BundleMarshaller.deviceAccountFromBundle(bundle2));
            } catch (Throwable e) {
                throw new StorageException(e);
            }
        }
        serialize = null;
        List<Bundle> parcelableArrayList = bundle.getParcelableArrayList(BundleMarshaller.BackupUsersKey);
        Map hashMap = new HashMap();
        ISerializer accountSerializer = getAccountSerializer();
        if (parcelableArrayList != null) {
            for (Bundle userAccountFromBundle : parcelableArrayList) {
                try {
                    AuthenticatorUserAccount userAccountFromBundle2 = BundleMarshaller.userAccountFromBundle(userAccountFromBundle);
                    hashMap.put(constructAccountKey(userAccountFromBundle2.getPuid()), accountSerializer.serialize(userAccountFromBundle2));
                } catch (Throwable e2) {
                    throw new StorageException(e2);
                } catch (Throwable e22) {
                    Logger.error("Encountered an error while trying to unbundle accounts.", e22);
                    ClientAnalytics.get().logException(e22);
                }
            }
        }
        synchronized (CollectionLock) {
            Editor edit = this._storage.edit();
            if (serialize != null) {
                edit.writeString(DeviceIdentityKey, serialize);
            }
            for (String constructTicketCollectionKeyFromAccountKey : this._storage.readStringSet(AccountsCollectionKey)) {
                replaceCollection(constructTicketCollectionKeyFromAccountKey(constructTicketCollectionKeyFromAccountKey), null, edit);
            }
            replaceCollection(AccountsCollectionKey, hashMap, edit);
            edit.writeLong(LastBackupReceivedTimeKey, System.currentTimeMillis());
            edit.apply();
        }
    }

    protected void storeTicket(String str, String str2, Ticket ticket) {
        writeToCollection(constructTicketCollectionKey(str), constructTicketKey(str, str2, ticket.getScope()), ticket, getTicketSerializer());
    }

    public void writeAccount(AuthenticatorUserAccount authenticatorUserAccount) {
        Strings.verifyArgumentNotNullOrEmpty(authenticatorUserAccount.getPuid(), "account.PUID");
        writeToCollection(AccountsCollectionKey, constructAccountKey(authenticatorUserAccount.getPuid()), authenticatorUserAccount, getAccountSerializer());
    }

    public void writeClockSkew(long j) {
        this._storage.edit().writeLong(ClockSkewKey, j).apply();
    }

    public void writeConfigLastDownloadedTime() {
        this._storage.edit().writeLong(ConfigLastDownloadedTimeKey, System.currentTimeMillis()).apply();
    }

    public void writeDeviceBasedFlights(Set<Integer> set) {
        Set hashSet = new HashSet(set.size());
        for (Integer num : set) {
            hashSet.add(num.toString());
        }
        this._storage.edit().writeStringSet(DeviceBasedFlightsKey, hashSet).apply();
    }

    public void writeDeviceFlightOverrideEnabled(boolean z) {
        this._storage.edit().writeBoolean(DeviceFlightOverrideKey, z).apply();
    }

    public void writeDeviceIdentity(DeviceIdentity deviceIdentity) {
        this._storage.edit().writeObject(DeviceIdentityKey, deviceIdentity, getDeviceIdentitySerializer()).apply();
    }

    public void writeLastBackupPushedTime() {
        this._storage.edit().writeLong(LastBackupPushedTimeKey, System.currentTimeMillis()).apply();
    }

    public void writeLastBackupReceivedTime() {
        this._storage.edit().writeLong(LastBackupReceivedTimeKey, System.currentTimeMillis()).apply();
    }

    public void writeSdkVersion(String str) {
        this._storage.edit().writeString(SdkVersionKey, str).apply();
    }

    protected <T> void writeToCollection(String str, String str2, T t, ISerializer<T> iSerializer) {
        Assertion.check(t != null, "Attempted to write null value to collection.");
        try {
            String serialize = iSerializer.serialize(t);
            synchronized (CollectionLock) {
                Collection readStringSet = this._storage.readStringSet(str);
                Editor edit = this._storage.edit();
                if (!readStringSet.contains(str2)) {
                    Set hashSet = new HashSet(readStringSet);
                    hashSet.add(str2);
                    edit.writeStringSet(str, hashSet);
                }
                edit.writeString(str2, serialize).apply();
            }
        } catch (Throwable e) {
            throw new StorageException(e);
        }
    }
}
