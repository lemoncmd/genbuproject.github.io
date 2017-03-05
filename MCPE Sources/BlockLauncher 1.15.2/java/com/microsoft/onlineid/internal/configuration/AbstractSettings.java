package com.microsoft.onlineid.internal.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

public abstract class AbstractSettings {
    protected final SharedPreferences _preferences;

    public static class Editor {
        protected final android.content.SharedPreferences.Editor _editor;

        protected Editor(android.content.SharedPreferences.Editor editor) {
            this._editor = editor;
        }

        protected Editor clear() {
            this._editor.clear();
            return this;
        }

        public boolean commit() {
            return this._editor.commit();
        }

        protected Editor setBoolean(ISetting<? extends Boolean> iSetting, boolean z) {
            this._editor.putBoolean(iSetting.getSettingName(), z);
            return this;
        }

        protected Editor setInt(ISetting<? extends Integer> iSetting, int i) {
            this._editor.putInt(iSetting.getSettingName(), i);
            return this;
        }

        protected Editor setString(ISetting<? extends String> iSetting, String str) {
            this._editor.putString(iSetting.getSettingName(), str);
            return this;
        }

        protected Editor setStringSet(ISetting<? extends Set<String>> iSetting, Set<String> set) {
            this._editor.putStringSet(iSetting.getSettingName(), set);
            return this;
        }
    }

    protected AbstractSettings(Context context, String str) {
        this._preferences = context.getSharedPreferences(str, 0);
    }

    protected abstract Editor edit();

    protected boolean getBoolean(ISetting<? extends Boolean> iSetting) {
        return this._preferences.getBoolean(iSetting.getSettingName(), ((Boolean) iSetting.getDefaultValue()).booleanValue());
    }

    protected int getInt(ISetting<? extends Integer> iSetting) {
        return this._preferences.getInt(iSetting.getSettingName(), ((Integer) iSetting.getDefaultValue()).intValue());
    }

    protected String getString(ISetting<? extends String> iSetting) {
        return this._preferences.getString(iSetting.getSettingName(), (String) iSetting.getDefaultValue());
    }

    protected Set<String> getStringSet(ISetting<? extends Set<String>> iSetting) {
        return this._preferences.getStringSet(iSetting.getSettingName(), (Set) iSetting.getDefaultValue());
    }
}
