package com.microsoft.xbox.idp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class Profile {

    public static final class GamerpicChangeRequest {
        public UserSetting userSetting;

        public GamerpicChangeRequest(String str) {
            this.userSetting = new UserSetting("PublicGamerpic", str);
        }
    }

    public static final class GamerpicChoiceList {
        public List<GamerpicListEntry> gamerpics;
    }

    public static final class GamerpicListEntry {
        public String id;
    }

    public static final class GamerpicUpdateResponse {
    }

    public static final class Response {
        public User[] profileUsers;
    }

    public static final class Setting {
        public SettingId id;
        public String value;
    }

    public enum SettingId {
        AppDisplayName,
        GameDisplayName,
        Gamertag,
        RealName,
        FirstName,
        LastName,
        AppDisplayPicRaw,
        GameDisplayPicRaw,
        AccountTier,
        TenureLevel,
        Gamerscore,
        PreferredColor,
        Watermarks,
        XboxOneRep,
        Background,
        PublicGamerpicType,
        ShowUserAsAvatar,
        TileTransparency
    }

    private static class SettingsAdapter extends TypeAdapter<Map<SettingId, String>> {
        private SettingsAdapter() {
        }

        public Map<SettingId, String> read(JsonReader jsonReader) throws IOException {
            Setting[] settingArr = (Setting[]) new Gson().fromJson(jsonReader, Setting[].class);
            Map<SettingId, String> hashMap = new HashMap();
            for (Setting setting : settingArr) {
                hashMap.put(setting.id, setting.value);
            }
            return hashMap;
        }

        public void write(JsonWriter jsonWriter, Map<SettingId, String> map) throws IOException {
            Object obj = new Setting[map.size()];
            int i = -1;
            for (Entry entry : map.entrySet()) {
                Setting setting = new Setting();
                setting.id = (SettingId) entry.getKey();
                setting.value = (String) entry.getValue();
                int i2 = i + 1;
                obj[i2] = setting;
                i = i2;
            }
            new Gson().toJson(obj, Setting[].class, jsonWriter);
        }
    }

    public static final class User {
        public String id;
        public boolean isSponsoredUser;
        public Map<SettingId, String> settings;
    }

    public static final class UserSetting {
        public String id;
        public String value;

        public UserSetting(String str, String str2) {
            this.id = str;
            this.value = str2;
        }
    }

    public static GsonBuilder registerAdapters(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(new TypeToken<Map<SettingId, String>>() {
        }.getType(), new SettingsAdapter());
    }
}
