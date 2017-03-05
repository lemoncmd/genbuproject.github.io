package com.microsoft.cll.android;

import Microsoft.Telemetry.Base;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import com.microsoft.cll.android.SettingsStore.Settings;
import com.microsoft.cll.android.SettingsStore.UpdateListener;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;

public class AndroidCll implements ICll, UpdateListener {
    private final String TAG;
    protected ISingletonCll cll;
    private final SharedPreferences cllPreferences;
    private final SharedPreferences hostPreferences;
    private final ILogger logger;
    private final String sharedCllPreferencesName;
    private final String sharedHostPreferencesName;

    protected AndroidCll() {
        this.TAG = "AndroidCll-AndroidCll";
        this.logger = AndroidLogger.getInstance();
        this.sharedCllPreferencesName = "AndroidCllSettingsSharedPreferences";
        this.sharedHostPreferencesName = "AndroidHostSettingsSharedPreferences";
        this.cllPreferences = null;
        this.hostPreferences = null;
    }

    public AndroidCll(String str, Context context) {
        this.TAG = "AndroidCll-AndroidCll";
        this.logger = AndroidLogger.getInstance();
        this.sharedCllPreferencesName = "AndroidCllSettingsSharedPreferences";
        this.sharedHostPreferencesName = "AndroidHostSettingsSharedPreferences";
        CorrelationVector correlationVector = new CorrelationVector();
        this.cll = SingletonCll.getInstance(str, AndroidLogger.getInstance(), context.getFilesDir().getPath(), new AndroidPartA(AndroidLogger.getInstance(), str, context, correlationVector), correlationVector);
        this.cllPreferences = context.getSharedPreferences("AndroidCllSettingsSharedPreferences", 0);
        this.hostPreferences = context.getSharedPreferences("AndroidHostSettingsSharedPreferences", 0);
        SettingsStore.setUpdateListener(this);
        setSettingsStoreValues();
    }

    private void setSettingsStoreValues() {
        for (Entry entry : this.cllPreferences.getAll().entrySet()) {
            try {
                SettingsStore.updateCllSetting(Settings.valueOf((String) entry.getKey()), (String) entry.getValue());
            } catch (Exception e) {
                Editor edit = this.cllPreferences.edit();
                edit.remove((String) entry.getKey());
                edit.apply();
            }
        }
        for (Entry entry2 : this.hostPreferences.getAll().entrySet()) {
            SettingsStore.updateHostSetting((String) entry2.getKey(), (String) entry2.getValue());
        }
    }

    public void OnCllSettingUpdate(String str, String str2) {
        Editor edit = this.cllPreferences.edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public void OnHostSettingUpdate(String str, String str2) {
        Editor edit = this.hostPreferences.edit();
        edit.putString(str, str2);
        edit.apply();
    }

    public void SubscribeCllEvents(ICllEvents iCllEvents) {
        this.cll.SubscribeCllEvents(iCllEvents);
    }

    public String getAppUserId() {
        return this.cll.getAppUserId();
    }

    public CorrelationVector getCorrelationVector() {
        return ((SingletonCll) this.cll).correlationVector;
    }

    public void log(Base base) {
        log(base, null);
    }

    public void log(Base base, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list) {
        this.cll.log(PreSerializedEvent.createFromStaticEvent(this.logger, base), latency, persistence, enumSet, d, list);
    }

    public void log(Base base, List<String> list) {
        log(base, Latency.LatencyUnspecified, Persistence.PersistenceUnspecified, EnumSet.of(Sensitivity.SensitivityUnspecified), EventEnums.SampleRate_Unspecified, list);
    }

    public void log(String str, String str2, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list) {
        if (str.contains(".")) {
            this.cll.log(PreSerializedEvent.createFromDynamicEvent(str, str2), latency, persistence, enumSet, d, list);
            return;
        }
        this.logger.error("AndroidCll-AndroidCll", "Event Name does not follow a valid format. Your event must have at least one . between two words. E.g. Microsoft.MyEvent");
    }

    public void logInternal(com.microsoft.telemetry.Base base) {
        this.cll.log(base, null, null, null, EventEnums.SampleRate_Unspecified, null);
    }

    public void pause() {
        this.cll.pause();
    }

    public void resume() {
        this.cll.resume();
    }

    public void send() {
        this.cll.send();
    }

    public void setAppUserId(String str) {
        this.cll.setAppUserId(str);
    }

    public void setDebugVerbosity(Verbosity verbosity) {
        this.cll.setDebugVerbosity(verbosity);
    }

    public void setEndpointUrl(String str) {
        this.cll.setEndpointUrl(str);
    }

    public void setExperimentId(String str) {
        this.cll.setExperimentId(str);
    }

    public void setXuidCallback(ITicketCallback iTicketCallback) {
        this.cll.setXuidCallback(iTicketCallback);
    }

    public void start() {
        this.cll.start();
    }

    public void stop() {
        this.cll.stop();
    }

    public void synchronize() {
        this.cll.synchronize();
    }

    public void useLegacyCS(boolean z) {
        this.cll.useLegacyCS(z);
    }
}
