package com.microsoft.onlineid.internal.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.microsoft.onlineid.internal.Objects;
import com.microsoft.onlineid.internal.Strings;
import com.microsoft.onlineid.internal.log.Logger;
import java.util.Collections;
import java.util.Set;

public class Storage {
    static final String DefaultStorageName = "com.microsoft.onlineid";
    private final SharedPreferences _preferences;

    public static class Editor {
        private final android.content.SharedPreferences.Editor _editor;

        public Editor(android.content.SharedPreferences.Editor editor) {
            this._editor = editor;
        }

        public void apply() {
            this._editor.apply();
        }

        public Editor clear() {
            this._editor.clear();
            return this;
        }

        public boolean commit() {
            return this._editor.commit();
        }

        public Editor remove(String str) {
            this._editor.remove(str);
            return this;
        }

        public Editor writeBoolean(String str, boolean z) {
            this._editor.putBoolean(str, z);
            return this;
        }

        public Editor writeLong(String str, long j) {
            this._editor.putLong(str, j);
            return this;
        }

        public <T> Editor writeObject(String str, T t, ISerializer<T> iSerializer) throws StorageException {
            if (t != null) {
                try {
                    this._editor.putString(str, iSerializer.serialize(t));
                } catch (Throwable e) {
                    throw new StorageException(e);
                }
            }
            return this;
        }

        public Editor writeString(String str, String str2) {
            this._editor.putString(str, str2);
            return this;
        }

        public Editor writeStringSet(String str, Set<String> set) {
            this._editor.putStringSet(str, set);
            return this;
        }
    }

    public Storage(Context context) {
        Objects.verifyArgumentNotNull(context, "applicationContext");
        this._preferences = context.getSharedPreferences(DefaultStorageName, 0);
    }

    public Storage(Context context, String str) {
        Objects.verifyArgumentNotNull(context, "applicationContext");
        Strings.verifyArgumentNotNullOrEmpty(str, "name");
        this._preferences = context.getSharedPreferences(str, 0);
    }

    public Editor edit() {
        return new Editor(this._preferences.edit());
    }

    public boolean readBoolean(String str, boolean z) {
        return this._preferences.getBoolean(str, z);
    }

    public long readLong(String str, long j) {
        return this._preferences.getLong(str, j);
    }

    public <T> T readObject(String str, ISerializer<T> iSerializer) {
        T t;
        Object obj;
        T t2 = null;
        try {
            String readString = readString(str, null);
            if (readString != null) {
                t2 = iSerializer.deserialize(readString);
            }
            t = t2;
            obj = 1;
        } catch (Throwable e) {
            Logger.warning("Object in storage was not of expected type.", e);
            t = null;
            obj = null;
        } catch (Throwable e2) {
            Logger.warning("Object in storage was corrupt.", e2);
            t = null;
            obj = null;
        }
        if (obj == null) {
            edit().remove(str).apply();
        }
        return t;
    }

    public String readString(String str) {
        return readString(str, null);
    }

    public String readString(String str, String str2) {
        return this._preferences.getString(str, str2);
    }

    public Set<String> readStringSet(String str) {
        return this._preferences.getStringSet(str, Collections.emptySet());
    }
}
