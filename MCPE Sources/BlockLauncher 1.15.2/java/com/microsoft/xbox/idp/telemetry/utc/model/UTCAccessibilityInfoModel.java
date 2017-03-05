package com.microsoft.xbox.idp.telemetry.utc.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;
import java.util.HashMap;
import net.hockeyapp.android.BuildConfig;

public class UTCAccessibilityInfoModel extends UTCJsonBase {
    private HashMap<String, Object> info = new HashMap();

    public void addValue(String str, Object obj) {
        if (str != null && !this.info.containsKey(str)) {
            this.info.put(str, obj);
        }
    }

    public HashMap<String, Object> getInfo() {
        return this.info;
    }

    public void setInfo(HashMap<String, Object> hashMap) {
        this.info = hashMap;
    }

    public String toJson() {
        HashMap info = getInfo();
        String str = BuildConfig.FLAVOR;
        try {
            str = new GsonBuilder().serializeNulls().create().toJson(info);
        } catch (JsonIOException e) {
            UTCLog.log("UTCJsonSerializer", "Error in json serialization" + e.toString());
        }
        return str;
    }

    public String toString() {
        return toJson();
    }
}
