package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import com.microsoft.telemetry.Base;
import java.util.EnumSet;
import java.util.HashMap;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.Token;

public class SettingsStore {
    protected static HashMap<Settings, Object> cllSettings = new HashMap();
    private static HashMap<String, String> hostEventSettings = new HashMap();
    private static UpdateListener updateListener;

    public interface UpdateListener {
        void OnCllSettingUpdate(String str, String str2);

        void OnHostSettingUpdate(String str, String str2);
    }

    public enum Settings {
        SYNCREFRESHINTERVAL,
        QUEUEDRAININTERVAL,
        SNAPSHOTSCHEDULEINTERVAL,
        MAXEVENTSIZEINBYTES,
        MAXEVENTSPERPOST,
        SAMPLERATE,
        MAXFILESSPACE,
        UPLOADENABLED,
        PERSISTENCE,
        LATENCY,
        HTTPTIMEOUTINTERVAL,
        THREADSTOUSEWITHEXECUTOR,
        MAXCORRELATIONVECTORLENGTH,
        MAXCRITICALCANADDATTEMPTS,
        MAXRETRYPERIOD,
        BASERETRYPERIOD,
        CONSTANTFORRETRYPERIOD,
        NORMALEVENTMEMORYQUEUESIZE,
        CLLSETTINGSURL,
        HOSTSETTINGSETAG,
        CLLSETTINGSETAG,
        VORTEXPRODURL
    }

    static {
        cllSettings.put(Settings.SYNCREFRESHINTERVAL, Integer.valueOf(1800));
        cllSettings.put(Settings.QUEUEDRAININTERVAL, Integer.valueOf(Token.FOR));
        cllSettings.put(Settings.SNAPSHOTSCHEDULEINTERVAL, Integer.valueOf(900));
        cllSettings.put(Settings.MAXEVENTSIZEINBYTES, Integer.valueOf(Parser.ARGC_LIMIT));
        cllSettings.put(Settings.MAXEVENTSPERPOST, Integer.valueOf(500));
        cllSettings.put(Settings.MAXFILESSPACE, Integer.valueOf(10485760));
        cllSettings.put(Settings.UPLOADENABLED, Boolean.valueOf(true));
        cllSettings.put(Settings.HTTPTIMEOUTINTERVAL, Integer.valueOf(60000));
        cllSettings.put(Settings.THREADSTOUSEWITHEXECUTOR, Integer.valueOf(3));
        cllSettings.put(Settings.MAXCORRELATIONVECTORLENGTH, Integer.valueOf(63));
        cllSettings.put(Settings.MAXCRITICALCANADDATTEMPTS, Integer.valueOf(5));
        cllSettings.put(Settings.MAXRETRYPERIOD, Integer.valueOf(Context.VERSION_1_8));
        cllSettings.put(Settings.BASERETRYPERIOD, Integer.valueOf(2));
        cllSettings.put(Settings.CONSTANTFORRETRYPERIOD, Integer.valueOf(5));
        cllSettings.put(Settings.NORMALEVENTMEMORYQUEUESIZE, Integer.valueOf(50));
        cllSettings.put(Settings.CLLSETTINGSURL, "https://settings.data.microsoft.com/settings/v2.0/androidLL/app");
        cllSettings.put(Settings.HOSTSETTINGSETAG, BuildConfig.FLAVOR);
        cllSettings.put(Settings.CLLSETTINGSETAG, BuildConfig.FLAVOR);
        cllSettings.put(Settings.VORTEXPRODURL, "https://vortex.data.microsoft.com/collect/v1");
    }

    protected static boolean getCllSettingsAsBoolean(Settings settings) {
        return Boolean.parseBoolean(cllSettings.get(settings).toString());
    }

    protected static int getCllSettingsAsInt(Settings settings) {
        return Integer.parseInt(cllSettings.get(settings).toString());
    }

    protected static long getCllSettingsAsLong(Settings settings) {
        return Long.parseLong(cllSettings.get(settings).toString());
    }

    protected static String getCllSettingsAsString(Settings settings) {
        return cllSettings.get(settings).toString();
    }

    public static Latency getLatencyForEvent(Base base, Latency latency) {
        String settingFromCloud = getSettingFromCloud(base, "LATENCY");
        if (settingFromCloud != null) {
            return Latency.FromString(settingFromCloud);
        }
        if (latency != null && latency != Latency.LatencyUnspecified) {
            return latency;
        }
        settingFromCloud = getSettingFromSchema(base, "LATENCY");
        if (settingFromCloud != null) {
            return Latency.FromString(settingFromCloud);
        }
        settingFromCloud = getSettingFromCloudDefaults("LATENCY");
        return settingFromCloud != null ? Latency.FromString(settingFromCloud) : Latency.LatencyNormal;
    }

    public static Persistence getPersistenceForEvent(Base base, Persistence persistence) {
        String settingFromCloud = getSettingFromCloud(base, "PERSISTENCE");
        if (settingFromCloud != null) {
            return Persistence.FromString(settingFromCloud);
        }
        if (persistence != null && persistence != Persistence.PersistenceUnspecified) {
            return persistence;
        }
        settingFromCloud = getSettingFromSchema(base, "PERSISTENCE");
        if (settingFromCloud != null) {
            return Persistence.FromString(settingFromCloud);
        }
        settingFromCloud = getSettingFromCloudDefaults("PERSISTENCE");
        return settingFromCloud != null ? Persistence.FromString(settingFromCloud) : Persistence.PersistenceNormal;
    }

    public static double getSampleRateForEvent(Base base, double d) {
        String settingFromCloud = getSettingFromCloud(base, "SAMPLERATE");
        if (settingFromCloud != null) {
            return EventEnums.SampleRateFromString(settingFromCloud);
        }
        if (d >= -1.0E-5d) {
            return d;
        }
        settingFromCloud = getSettingFromSchema(base, "SAMPLERATE");
        if (settingFromCloud != null) {
            return EventEnums.SampleRateFromString(settingFromCloud);
        }
        settingFromCloud = getSettingFromCloudDefaults("SAMPLERATE");
        return settingFromCloud != null ? EventEnums.SampleRateFromString(settingFromCloud) : EventEnums.SampleRate_NoSampling;
    }

    public static EnumSet<Sensitivity> getSensitivityForEvent(Base base, EnumSet<Sensitivity> enumSet) {
        String settingFromCloud = getSettingFromCloud(base, "SENSITIVITY");
        if (settingFromCloud != null) {
            return Sensitivity.FromString(settingFromCloud);
        }
        if (enumSet != null && !enumSet.contains(Sensitivity.SensitivityUnspecified)) {
            return enumSet;
        }
        settingFromCloud = getSettingFromSchema(base, "SENSITIVITY");
        if (settingFromCloud != null) {
            return Sensitivity.FromString(settingFromCloud);
        }
        settingFromCloud = getSettingFromCloudDefaults("SENSITIVITY");
        return settingFromCloud != null ? Sensitivity.FromString(settingFromCloud) : EnumSet.of(Sensitivity.SensitivityNone);
    }

    private static String getSettingFromCloud(Base base, String str) {
        String str2;
        String toUpperCase = base.QualifiedName.toUpperCase();
        if (toUpperCase.lastIndexOf(".") == -1) {
            str2 = BuildConfig.FLAVOR;
        } else {
            str2 = toUpperCase.substring(0, toUpperCase.lastIndexOf("."));
            toUpperCase = toUpperCase.substring(toUpperCase.lastIndexOf(".") + 1);
        }
        return hostEventSettings.containsKey(new StringBuilder().append(str2).append(":").append(toUpperCase).append("::").append(str).toString()) ? (String) hostEventSettings.get(str2 + ":" + toUpperCase + "::" + str) : hostEventSettings.containsKey(new StringBuilder().append(":").append(toUpperCase).append("::").append(str).toString()) ? (String) hostEventSettings.get(":" + toUpperCase + "::" + str) : hostEventSettings.containsKey(new StringBuilder().append(str2).append(":::").append(str).toString()) ? (String) hostEventSettings.get(str2 + ":::" + str) : hostEventSettings.containsKey(new StringBuilder().append(":::").append(str).toString()) ? (String) hostEventSettings.get(":::" + str) : null;
    }

    private static String getSettingFromCloudDefaults(String str) {
        Object obj = cllSettings.get(str);
        return obj != null ? obj.toString() : null;
    }

    private static String getSettingFromSchema(Base base, String str) {
        return (String) base.Attributes.get(str);
    }

    public static void setUpdateListener(UpdateListener updateListener) {
        updateListener = updateListener;
    }

    public static void updateCllSetting(Settings settings, String str) {
        if (cllSettings.get(settings) == null || !cllSettings.get(settings).equals(str)) {
            cllSettings.put(settings, str);
            if (updateListener != null) {
                updateListener.OnCllSettingUpdate(settings.toString(), str);
            }
        }
    }

    public static void updateHostSetting(String str, String str2) {
        if (hostEventSettings.get(str) == null || !((String) hostEventSettings.get(str)).equals(str2)) {
            hostEventSettings.put(str, str2);
            if (updateListener != null) {
                updateListener.OnHostSettingUpdate(str, str2);
            }
        }
    }
}
