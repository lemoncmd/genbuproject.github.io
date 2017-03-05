package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore.Settings;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class CllSettings extends AbstractSettings {
    private final SettingsSync settingsSync;

    public CllSettings(ClientTelemetry clientTelemetry, ILogger iLogger, SettingsSync settingsSync, PartA partA) {
        super(clientTelemetry, iLogger, partA);
        this.settingsSync = settingsSync;
        this.TAG = "AndroidCll-CllSettings";
        this.ETagSettingName = Settings.CLLSETTINGSETAG;
        this.endpoint = SettingsStore.getCllSettingsAsString(Settings.CLLSETTINGSURL);
        this.queryParam = "?iKey=" + partA.iKey + "&os=" + partA.osName + "&osVer=" + partA.osVer + "&deviceClass=" + partA.deviceExt.getDeviceClass() + "&deviceId=" + partA.deviceExt.getLocalId();
    }

    public void ParseSettings(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                if (jSONObject.has("settings")) {
                    int i = jSONObject.getInt("refreshInterval") * 60;
                    if (i != SettingsStore.getCllSettingsAsInt(Settings.SYNCREFRESHINTERVAL)) {
                        SettingsStore.cllSettings.put(Settings.SYNCREFRESHINTERVAL, Integer.valueOf(i));
                        this.settingsSync.nextExecution.cancel(false);
                        this.settingsSync.nextExecution = this.settingsSync.executor.scheduleAtFixedRate(this.settingsSync, SettingsStore.getCllSettingsAsLong(Settings.SYNCREFRESHINTERVAL), SettingsStore.getCllSettingsAsLong(Settings.SYNCREFRESHINTERVAL), TimeUnit.SECONDS);
                    }
                    JSONObject jSONObject2 = (JSONObject) jSONObject.get("settings");
                    Iterator keys = jSONObject2.keys();
                    while (keys.hasNext()) {
                        String str = (String) keys.next();
                        String string = jSONObject2.getString(str);
                        try {
                            SettingsStore.updateCllSetting(Settings.valueOf(str), string);
                            this.logger.info(this.TAG, "Json Settings, Key: " + str + " Value: " + string);
                        } catch (Exception e) {
                            this.logger.warn(this.TAG, "Key: " + str + " was not found");
                        }
                    }
                }
            } catch (Exception e2) {
                this.logger.error(this.TAG, "An exception occurred while parsing settings");
            }
        }
    }
}
