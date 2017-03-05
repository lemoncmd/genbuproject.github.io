package com.microsoft.cll.android;

import com.microsoft.cll.android.EventEnums.Latency;
import com.microsoft.cll.android.EventEnums.Persistence;
import com.microsoft.cll.android.EventEnums.Sensitivity;
import com.microsoft.cll.android.Internal.BuildConfig;
import com.microsoft.onlineid.ui.AddAccountActivity;
import com.microsoft.telemetry.Base;
import com.microsoft.telemetry.Data;
import com.microsoft.telemetry.Envelope;
import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.extensions.android;
import com.microsoft.telemetry.extensions.app;
import com.microsoft.telemetry.extensions.device;
import com.microsoft.telemetry.extensions.os;
import com.microsoft.telemetry.extensions.user;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public abstract class PartA {
    private final String TAG = "AndroidCll-PartA";
    protected final app appExt;
    protected String appId;
    protected String appVer;
    private CorrelationVector correlationVector;
    private final String csVer = "2.1";
    protected final device deviceExt;
    private long epoch;
    private long flags;
    private final char[] hexArray = "0123456789ABCDEF".toCharArray();
    protected final String iKey;
    protected final ILogger logger;
    protected final os osExt;
    protected String osName;
    protected String osVer;
    private Random random;
    private final String salt = "oRq=MAHHHC~6CCe|JfEqRZ+gc0ESI||g2Jlb^PYjc5UYN2P 27z_+21xxd2n";
    protected final AtomicLong seqCounter;
    private EventSerializer serializer;
    protected String uniqueId;
    private boolean useLegacyCS = false;
    protected final user userExt;

    public PartA(ILogger iLogger, String str, CorrelationVector correlationVector) {
        this.logger = iLogger;
        this.iKey = str;
        this.correlationVector = correlationVector;
        this.seqCounter = new AtomicLong(0);
        this.serializer = new EventSerializer(iLogger);
        this.userExt = new user();
        this.deviceExt = new device();
        this.osExt = new os();
        this.appExt = new app();
        this.random = new Random();
        this.epoch = this.random.nextLong();
    }

    private String bytesToHex(byte[] bArr) {
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = bArr[i] & 255;
            cArr[i * 2] = this.hexArray[i2 >>> 4];
            cArr[(i * 2) + 1] = this.hexArray[i2 & 15];
        }
        return new String(cArr);
    }

    private LinkedHashMap<String, Extension> createExtensions(List<String> list) {
        LinkedHashMap<String, Extension> linkedHashMap = new LinkedHashMap();
        linkedHashMap.put("user", this.userExt);
        linkedHashMap.put("os", this.osExt);
        linkedHashMap.put("device", this.deviceExt);
        android com_microsoft_telemetry_extensions_android = new android();
        com_microsoft_telemetry_extensions_android.setLibVer(BuildConfig.VERSION_NAME);
        if (list != null && list.size() > 0) {
            com_microsoft_telemetry_extensions_android.setTickets(list);
        }
        linkedHashMap.put(AddAccountActivity.PlatformName, com_microsoft_telemetry_extensions_android);
        if (!(this.appExt.getExpId() == null && this.appExt.getUserId() == null)) {
            linkedHashMap.put("app", this.appExt);
        }
        return linkedHashMap;
    }

    private String getDateTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date()).toString();
    }

    private long getFlags(Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet) {
        long j;
        if (enumSet != null) {
            Iterator it = enumSet.iterator();
            j = 0;
            while (it.hasNext()) {
                Sensitivity sensitivity = (Sensitivity) it.next();
                if (sensitivity != Sensitivity.SensitivityUnspecified) {
                    j = ((long) sensitivity.id) | j;
                }
            }
        } else {
            j = 0;
        }
        return (((long) latency.id) | j) | ((long) persistence.id);
    }

    private Sensitivity getHighestSensitivityLevel(EnumSet<Sensitivity> enumSet) {
        return enumSet.contains(Sensitivity.SensitivityDrop) ? Sensitivity.SensitivityDrop : enumSet.contains(Sensitivity.SensitivityHash) ? Sensitivity.SensitivityHash : Sensitivity.SensitivityNone;
    }

    private long getSeqNum(EnumSet<Sensitivity> enumSet) {
        return enumSet.contains(Sensitivity.SensitivityDrop) ? 0 : this.seqCounter.incrementAndGet();
    }

    private SerializedEvent populateSerializedEvent(String str, Latency latency, Persistence persistence, double d, String str2) {
        SerializedEvent serializedEvent = new SerializedEvent();
        serializedEvent.setSerializedData(str);
        serializedEvent.setSampleRate(d);
        serializedEvent.setDeviceId(this.deviceExt.getLocalId());
        serializedEvent.setPersistence(persistence);
        serializedEvent.setLatency(latency);
        return serializedEvent;
    }

    private void scrubPII(Envelope envelope, EnumSet<Sensitivity> enumSet) {
        if (enumSet != null) {
            Sensitivity highestSensitivityLevel = getHighestSensitivityLevel(enumSet);
            if (highestSensitivityLevel != Sensitivity.SensitivityNone) {
                user com_microsoft_telemetry_extensions_user = (user) envelope.getExt().get("user");
                user com_microsoft_telemetry_extensions_user2 = new user();
                com_microsoft_telemetry_extensions_user2.setLocalId(com_microsoft_telemetry_extensions_user.getLocalId());
                com_microsoft_telemetry_extensions_user2.setAuthId(com_microsoft_telemetry_extensions_user.getAuthId());
                com_microsoft_telemetry_extensions_user2.setId(com_microsoft_telemetry_extensions_user.getId());
                com_microsoft_telemetry_extensions_user2.setVer(com_microsoft_telemetry_extensions_user.getVer());
                envelope.getExt().put("user", com_microsoft_telemetry_extensions_user2);
                device com_microsoft_telemetry_extensions_device = (device) envelope.getExt().get("device");
                device com_microsoft_telemetry_extensions_device2 = new device();
                com_microsoft_telemetry_extensions_device2.setLocalId(com_microsoft_telemetry_extensions_device.getLocalId());
                com_microsoft_telemetry_extensions_device2.setVer(com_microsoft_telemetry_extensions_device.getVer());
                com_microsoft_telemetry_extensions_device2.setId(com_microsoft_telemetry_extensions_device.getId());
                com_microsoft_telemetry_extensions_device2.setAuthId(com_microsoft_telemetry_extensions_device.getAuthId());
                com_microsoft_telemetry_extensions_device2.setAuthSecId(com_microsoft_telemetry_extensions_device.getAuthSecId());
                com_microsoft_telemetry_extensions_device2.setDeviceClass(com_microsoft_telemetry_extensions_device.getDeviceClass());
                envelope.getExt().put("device", com_microsoft_telemetry_extensions_device2);
                if (envelope.getExt().containsKey("app")) {
                    app com_microsoft_telemetry_extensions_app = (app) envelope.getExt().get("app");
                    app com_microsoft_telemetry_extensions_app2 = new app();
                    com_microsoft_telemetry_extensions_app2.setExpId(com_microsoft_telemetry_extensions_app.getExpId());
                    com_microsoft_telemetry_extensions_app2.setUserId(com_microsoft_telemetry_extensions_app.getUserId());
                    envelope.getExt().put("app", com_microsoft_telemetry_extensions_app2);
                }
                if (highestSensitivityLevel == Sensitivity.SensitivityDrop) {
                    ((user) envelope.getExt().get("user")).setLocalId(null);
                    ((device) envelope.getExt().get("device")).setLocalId("r:" + String.valueOf(Math.abs((long) this.random.nextInt())));
                    if (envelope.getExt().containsKey("app")) {
                        ((app) envelope.getExt().get("app")).setUserId(null);
                    }
                    if (this.correlationVector.isInitialized) {
                        envelope.setCV(null);
                    }
                    envelope.setEpoch(null);
                    envelope.setSeqNum(0);
                } else if (highestSensitivityLevel == Sensitivity.SensitivityHash) {
                    ((user) envelope.getExt().get("user")).setLocalId("d:" + HashStringSha256(((user) envelope.getExt().get("user")).getLocalId()));
                    ((device) envelope.getExt().get("device")).setLocalId("d:" + HashStringSha256(((device) envelope.getExt().get("device")).getLocalId()));
                    if (envelope.getExt().containsKey("app")) {
                        ((app) envelope.getExt().get("app")).setUserId("d:" + HashStringSha256(((app) envelope.getExt().get("app")).getUserId()));
                    }
                    if (this.correlationVector.isInitialized) {
                        envelope.setCV(HashStringSha256(envelope.getCV()));
                    }
                    envelope.setEpoch(HashStringSha256(envelope.getEpoch()));
                }
            }
        }
    }

    private void setBaseType(Base base) {
        try {
            base.setBaseType(((Data) base).getBaseData().QualifiedName);
        } catch (ClassCastException e) {
            this.logger.error("AndroidCll-PartA", "This event doesn't extend data");
        }
    }

    protected String HashStringSha256(String str) {
        if (str == null) {
            return net.hockeyapp.android.BuildConfig.FLAVOR;
        }
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA-256");
            instance.reset();
            instance.update(str.getBytes());
            instance.update("oRq=MAHHHC~6CCe|JfEqRZ+gc0ESI||g2Jlb^PYjc5UYN2P 27z_+21xxd2n".getBytes());
            return bytesToHex(instance.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected abstract void PopulateConstantValues();

    String getAppUserId() {
        return this.appExt.getUserId();
    }

    public SerializedEvent populate(Base base, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list) {
        Latency latencyForEvent = SettingsStore.getLatencyForEvent(base, latency);
        Persistence persistenceForEvent = SettingsStore.getPersistenceForEvent(base, persistence);
        EnumSet sensitivityForEvent = SettingsStore.getSensitivityForEvent(base, enumSet);
        double sampleRateForEvent = SettingsStore.getSampleRateForEvent(base, d);
        if (this.useLegacyCS) {
            Object populateLegacyEnvelope = populateLegacyEnvelope(base, this.correlationVector.GetValue(), latencyForEvent, persistenceForEvent, sensitivityForEvent, sampleRateForEvent, list);
            return populateSerializedEvent(this.serializer.serialize(populateLegacyEnvelope), latencyForEvent, persistenceForEvent, sampleRateForEvent, populateLegacyEnvelope.getDeviceId());
        }
        return populateSerializedEvent(this.serializer.serialize(populateEnvelope(base, this.correlationVector.GetValue(), latencyForEvent, persistenceForEvent, sensitivityForEvent, sampleRateForEvent, list)), latencyForEvent, persistenceForEvent, sampleRateForEvent, this.deviceExt.getLocalId());
    }

    public Envelope populateEnvelope(Base base, String str, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list) {
        Envelope envelope = new Envelope();
        setBaseType(base);
        envelope.setVer("2.1");
        envelope.setTime(getDateTime());
        envelope.setName(base.QualifiedName);
        envelope.setPopSample(d);
        envelope.setEpoch(String.valueOf(this.epoch));
        envelope.setSeqNum(getSeqNum(enumSet));
        envelope.setOs(this.osName);
        envelope.setOsVer(this.osVer);
        envelope.setData(base);
        envelope.setAppId(this.appId);
        envelope.setAppVer(this.appVer);
        if (this.correlationVector.isInitialized) {
            envelope.setCV(str);
        }
        envelope.setFlags(getFlags(latency, persistence, enumSet));
        envelope.setIKey(this.iKey);
        envelope.setExt(createExtensions(list));
        scrubPII(envelope, enumSet);
        return envelope;
    }

    public com.microsoft.telemetry.cs2.Envelope populateLegacyEnvelope(Base base, String str, Latency latency, Persistence persistence, EnumSet<Sensitivity> enumSet, double d, List<String> list) {
        Map hashMap = new HashMap();
        if (this.correlationVector.isInitialized) {
            hashMap.put("cV", str);
        }
        com.microsoft.telemetry.cs2.Envelope envelope = new com.microsoft.telemetry.cs2.Envelope();
        envelope.setVer(1);
        envelope.setTime(getDateTime());
        envelope.setName(base.QualifiedName);
        envelope.setSampleRate(d);
        envelope.setSeq(String.valueOf(this.epoch) + ":" + String.valueOf(getSeqNum(enumSet)));
        envelope.setOs(this.osName);
        envelope.setOsVer(this.osVer);
        envelope.setData(base);
        envelope.setAppId(this.appId);
        envelope.setAppVer(this.appVer);
        envelope.setTags(hashMap);
        envelope.setFlags(getFlags(latency, persistence, enumSet));
        envelope.setIKey(this.iKey);
        envelope.setUserId(this.userExt.getLocalId());
        envelope.setDeviceId(this.deviceExt.getLocalId());
        return envelope;
    }

    protected abstract void setAppInfo();

    void setAppUserId(String str) {
        if (str == null) {
            this.appExt.setUserId(null);
        } else if (Pattern.compile("^((c:)|(i:)|(w:)).*").matcher(str).find()) {
            this.appExt.setUserId(str);
        } else {
            this.appExt.setUserId(null);
            this.logger.warn("AndroidCll-PartA", "The userId supplied does not match the required format which requires the appId to start with 'c:', 'i:', or 'w:'.");
        }
    }

    protected abstract void setDeviceInfo();

    protected void setExpId(String str) {
        this.appExt.setExpId(str);
    }

    protected abstract void setOs();

    protected abstract void setUserId();

    void useLegacyCS(boolean z) {
        this.useLegacyCS = z;
    }
}
