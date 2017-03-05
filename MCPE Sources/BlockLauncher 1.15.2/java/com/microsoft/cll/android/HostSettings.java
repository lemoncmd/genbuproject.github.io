package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore.Settings;
import java.util.Iterator;
import net.hockeyapp.android.BuildConfig;
import org.json.JSONObject;

public class HostSettings extends AbstractSettings {
    private final String baseUrl;

    public HostSettings(ClientTelemetry clientTelemetry, ILogger iLogger, String str, PartA partA) {
        super(clientTelemetry, iLogger, partA);
        this.baseUrl = "https://settings.data.microsoft.com/settings/v2.0/telemetry/";
        this.TAG = "AndroidCll-HostSettings";
        this.ETagSettingName = Settings.HOSTSETTINGSETAG;
        this.disableUploadOn404 = true;
        this.endpoint = "https://settings.data.microsoft.com/settings/v2.0/telemetry/" + str;
        this.queryParam = "?os=" + partA.osName + "&osVer=" + partA.osVer + "&deviceClass=" + partA.deviceExt.getDeviceClass() + "&deviceId=" + partA.deviceExt.getLocalId();
    }

    public void ParseSettings(JSONObject jSONObject) {
        if (jSONObject != null) {
            try {
                if (jSONObject.has("settings")) {
                    JSONObject jSONObject2 = (JSONObject) jSONObject.get("settings");
                    Iterator keys = jSONObject2.keys();
                    while (keys.hasNext()) {
                        String str = (String) keys.next();
                        String string = jSONObject2.getString(str);
                        if (str.split(":").length != 4) {
                            this.logger.error(this.TAG, "Bad Settings Format");
                        }
                        SettingsStore.updateHostSetting(str.toUpperCase(), string.replaceAll(" ", BuildConfig.FLAVOR).replaceAll("_", BuildConfig.FLAVOR).toUpperCase());
                    }
                    return;
                }
            } catch (Exception e) {
                this.logger.error(this.TAG, "An exception occurred while parsing settings");
                return;
            }
        }
        this.logger.info(this.TAG, "Json result did not contain a \"settings\" field!");
    }
}
