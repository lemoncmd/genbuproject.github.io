package com.microsoft.xbox.idp.telemetry.utc.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.microsoft.xbox.idp.telemetry.helpers.UTCLog;
import net.hockeyapp.android.BuildConfig;

public abstract class UTCJsonBase {
    public String toJson() {
        Gson create = new GsonBuilder().serializeNulls().create();
        String str = BuildConfig.FLAVOR;
        try {
            str = create.toJson(this);
        } catch (JsonIOException e) {
            UTCLog.log("UTCJsonSerializer", "Error in json serialization" + e.toString());
        }
        return str;
    }
}
