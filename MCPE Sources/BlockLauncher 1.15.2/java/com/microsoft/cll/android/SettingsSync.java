package com.microsoft.cll.android;

import com.microsoft.cll.android.SettingsStore.Settings;
import java.util.ArrayList;
import java.util.List;
import net.hockeyapp.android.BuildConfig;
import org.json.JSONObject;

public class SettingsSync extends ScheduledWorker {
    private final String TAG = "AndroidCll-SettingsSync";
    private final ClientTelemetry clientTelemetry;
    private final ILogger logger;
    private final List<AbstractSettings> settingsList;

    public SettingsSync(ClientTelemetry clientTelemetry, ILogger iLogger, String str, PartA partA) {
        super(SettingsStore.getCllSettingsAsLong(Settings.SYNCREFRESHINTERVAL));
        this.clientTelemetry = clientTelemetry;
        this.logger = iLogger;
        this.settingsList = new ArrayList();
        this.settingsList.add(new CllSettings(clientTelemetry, iLogger, this, partA));
        if (!str.equals(BuildConfig.FLAVOR)) {
            this.settingsList.add(new HostSettings(clientTelemetry, iLogger, str, partA));
        }
    }

    private void GetCloudSettings() {
        for (AbstractSettings abstractSettings : this.settingsList) {
            JSONObject settings = abstractSettings.getSettings();
            if (settings == null) {
                this.logger.error("AndroidCll-SettingsSync", "Could not get or parse settings");
            } else {
                abstractSettings.ParseSettings(settings);
            }
        }
    }

    public void run() {
        this.logger.info("AndroidCll-SettingsSync", "Cloud sync!");
        GetCloudSettings();
    }
}
