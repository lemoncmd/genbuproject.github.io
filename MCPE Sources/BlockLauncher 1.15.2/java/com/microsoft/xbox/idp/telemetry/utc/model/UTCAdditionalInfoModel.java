package com.microsoft.xbox.idp.telemetry.utc.model;

import com.google.gson.GsonBuilder;
import com.microsoft.xbox.idp.telemetry.helpers.UTCError;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;
import java.util.HashMap;
import net.hockeyapp.android.BuildConfig;

public class UTCAdditionalInfoModel extends UTCJsonBase {
    private HashMap<String, Object> additionalInfo = new HashMap();

    public void addValue(String str, Object obj) {
        if (str != null && !this.additionalInfo.containsKey(str)) {
            this.additionalInfo.put(str, obj);
        }
    }

    public HashMap<String, Object> getAdditionalInfo() {
        return this.additionalInfo;
    }

    public void setAdditionalInfo(HashMap<String, Object> hashMap) {
        this.additionalInfo = hashMap;
    }

    public String toJson() {
        HashMap hashMap = this.additionalInfo;
        String str = BuildConfig.FLAVOR;
        try {
            str = new GsonBuilder().serializeNulls().create().toJson(hashMap);
        } catch (Exception e) {
            UTCError.trackException(e, "UTCAdditionalInfoModel.toJson");
            UTCLog.log("UTCJsonSerializer", "Error in json serialization" + e.toString());
        }
        return str;
    }

    public String toString() {
        return toJson();
    }
}
