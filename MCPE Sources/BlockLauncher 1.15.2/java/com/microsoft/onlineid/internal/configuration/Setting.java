package com.microsoft.onlineid.internal.configuration;

public class Setting<T> implements ISetting<T> {
    private final T _defaultValue;
    private final String _settingName;

    public Setting(String str, T t) {
        this._settingName = str;
        this._defaultValue = t;
    }

    public T getDefaultValue() {
        return this._defaultValue;
    }

    public String getSettingName() {
        return this._settingName;
    }
}
