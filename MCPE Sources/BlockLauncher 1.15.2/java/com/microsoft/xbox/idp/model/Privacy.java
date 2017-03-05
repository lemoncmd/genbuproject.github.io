package com.microsoft.xbox.idp.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class Privacy {

    public enum Key {
        None,
        ShareFriendList,
        ShareGameHistory,
        CommunicateUsingTextAndVoice,
        SharePresence,
        ShareProfile,
        ShareVideoAndMusicStatus,
        CommunicateUsingVideo,
        CollectVoiceData,
        ShareXboxMusicActivity,
        ShareExerciseInfo,
        ShareIdentity,
        ShareRecordedGameSessions,
        ShareIdentityTransitively,
        CanShareIdentity
    }

    public static class Setting {
        public Key setting;
        public Value value;
    }

    public static class Settings {
        public Map<Key, Value> settings;

        public static Settings newWithMap() {
            Settings settings = new Settings();
            settings.settings = new HashMap();
            return settings;
        }

        public boolean isSettingSet(Key key) {
            if (this.settings != null) {
                Value value = (Value) this.settings.get(key);
                if (!(value == null || value == Value.NotSet)) {
                    return true;
                }
            }
            return false;
        }
    }

    private static class SettingsAdapter extends TypeAdapter<Map<Key, Value>> {
        private SettingsAdapter() {
        }

        public Map<Key, Value> read(JsonReader jsonReader) throws IOException {
            Setting[] settingArr = (Setting[]) new Gson().fromJson(jsonReader, Setting[].class);
            Map<Key, Value> hashMap = new HashMap();
            for (Setting setting : settingArr) {
                if (!(setting.setting == null || setting.value == null)) {
                    hashMap.put(setting.setting, setting.value);
                }
            }
            return hashMap;
        }

        public void write(JsonWriter jsonWriter, Map<Key, Value> map) throws IOException {
            Object obj = new Setting[map.size()];
            int i = -1;
            for (Entry entry : map.entrySet()) {
                Setting setting = new Setting();
                setting.setting = (Key) entry.getKey();
                setting.value = (Value) entry.getValue();
                int i2 = i + 1;
                obj[i2] = setting;
                i = i2;
            }
            new Gson().toJson(obj, Setting[].class, jsonWriter);
        }
    }

    public enum Value {
        NotSet,
        Everyone,
        PeopleOnMyList,
        FriendCategoryShareIdentity,
        Blocked
    }

    public static GsonBuilder registerAdapters(GsonBuilder gsonBuilder) {
        return gsonBuilder.registerTypeAdapter(new TypeToken<Map<Key, Value>>() {
        }.getType(), new SettingsAdapter());
    }
}
