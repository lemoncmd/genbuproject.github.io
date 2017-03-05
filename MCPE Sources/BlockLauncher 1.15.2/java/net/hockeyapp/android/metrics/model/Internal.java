package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.BuildConfig;
import net.hockeyapp.android.metrics.JsonHelper;
import org.mozilla.javascript.Token;

public class Internal implements Serializable, IJsonSerializable {
    private String accountId;
    private String agentVersion;
    private String applicationName;
    private String applicationType;
    private String dataCollectorReceivedTime;
    private String flowType;
    private String instrumentationKey;
    private String isAudit;
    private String profileClassId;
    private String profileId;
    private String requestSource;
    private String sdkVersion;
    private String telemetryItemId;
    private String trackingSourceId;
    private String trackingType;

    public Internal() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public void addToHashMap(Map<String, String> map) {
        if (this.sdkVersion != null) {
            map.put("ai.internal.sdkVersion", this.sdkVersion);
        }
        if (this.agentVersion != null) {
            map.put("ai.internal.agentVersion", this.agentVersion);
        }
        if (this.dataCollectorReceivedTime != null) {
            map.put("ai.internal.dataCollectorReceivedTime", this.dataCollectorReceivedTime);
        }
        if (this.profileId != null) {
            map.put("ai.internal.profileId", this.profileId);
        }
        if (this.profileClassId != null) {
            map.put("ai.internal.profileClassId", this.profileClassId);
        }
        if (this.accountId != null) {
            map.put("ai.internal.accountId", this.accountId);
        }
        if (this.applicationName != null) {
            map.put("ai.internal.applicationName", this.applicationName);
        }
        if (this.instrumentationKey != null) {
            map.put("ai.internal.instrumentationKey", this.instrumentationKey);
        }
        if (this.telemetryItemId != null) {
            map.put("ai.internal.telemetryItemId", this.telemetryItemId);
        }
        if (this.applicationType != null) {
            map.put("ai.internal.applicationType", this.applicationType);
        }
        if (this.requestSource != null) {
            map.put("ai.internal.requestSource", this.requestSource);
        }
        if (this.flowType != null) {
            map.put("ai.internal.flowType", this.flowType);
        }
        if (this.isAudit != null) {
            map.put("ai.internal.isAudit", this.isAudit);
        }
        if (this.trackingSourceId != null) {
            map.put("ai.internal.trackingSourceId", this.trackingSourceId);
        }
        if (this.trackingType != null) {
            map.put("ai.internal.trackingType", this.trackingType);
        }
    }

    public String getAccountId() {
        return this.accountId;
    }

    public String getAgentVersion() {
        return this.agentVersion;
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public String getApplicationType() {
        return this.applicationType;
    }

    public String getDataCollectorReceivedTime() {
        return this.dataCollectorReceivedTime;
    }

    public String getFlowType() {
        return this.flowType;
    }

    public String getInstrumentationKey() {
        return this.instrumentationKey;
    }

    public String getIsAudit() {
        return this.isAudit;
    }

    public String getProfileClassId() {
        return this.profileClassId;
    }

    public String getProfileId() {
        return this.profileId;
    }

    public String getRequestSource() {
        return this.requestSource;
    }

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public String getTelemetryItemId() {
        return this.telemetryItemId;
    }

    public String getTrackingSourceId() {
        return this.trackingSourceId;
    }

    public String getTrackingType() {
        return this.trackingType;
    }

    public void serialize(Writer writer) throws IOException {
        if (writer == null) {
            throw new IllegalArgumentException("writer");
        }
        writer.write(Token.VAR);
        serializeContent(writer);
        writer.write(Token.CATCH);
    }

    protected String serializeContent(Writer writer) throws IOException {
        String str = BuildConfig.FLAVOR;
        if (this.sdkVersion != null) {
            writer.write(str + "\"ai.internal.sdkVersion\":");
            writer.write(JsonHelper.convert(this.sdkVersion));
            str = ",";
        }
        if (this.agentVersion != null) {
            writer.write(str + "\"ai.internal.agentVersion\":");
            writer.write(JsonHelper.convert(this.agentVersion));
            str = ",";
        }
        if (this.dataCollectorReceivedTime != null) {
            writer.write(str + "\"ai.internal.dataCollectorReceivedTime\":");
            writer.write(JsonHelper.convert(this.dataCollectorReceivedTime));
            str = ",";
        }
        if (this.profileId != null) {
            writer.write(str + "\"ai.internal.profileId\":");
            writer.write(JsonHelper.convert(this.profileId));
            str = ",";
        }
        if (this.profileClassId != null) {
            writer.write(str + "\"ai.internal.profileClassId\":");
            writer.write(JsonHelper.convert(this.profileClassId));
            str = ",";
        }
        if (this.accountId != null) {
            writer.write(str + "\"ai.internal.accountId\":");
            writer.write(JsonHelper.convert(this.accountId));
            str = ",";
        }
        if (this.applicationName != null) {
            writer.write(str + "\"ai.internal.applicationName\":");
            writer.write(JsonHelper.convert(this.applicationName));
            str = ",";
        }
        if (this.instrumentationKey != null) {
            writer.write(str + "\"ai.internal.instrumentationKey\":");
            writer.write(JsonHelper.convert(this.instrumentationKey));
            str = ",";
        }
        if (this.telemetryItemId != null) {
            writer.write(str + "\"ai.internal.telemetryItemId\":");
            writer.write(JsonHelper.convert(this.telemetryItemId));
            str = ",";
        }
        if (this.applicationType != null) {
            writer.write(str + "\"ai.internal.applicationType\":");
            writer.write(JsonHelper.convert(this.applicationType));
            str = ",";
        }
        if (this.requestSource != null) {
            writer.write(str + "\"ai.internal.requestSource\":");
            writer.write(JsonHelper.convert(this.requestSource));
            str = ",";
        }
        if (this.flowType != null) {
            writer.write(str + "\"ai.internal.flowType\":");
            writer.write(JsonHelper.convert(this.flowType));
            str = ",";
        }
        if (this.isAudit != null) {
            writer.write(str + "\"ai.internal.isAudit\":");
            writer.write(JsonHelper.convert(this.isAudit));
            str = ",";
        }
        if (this.trackingSourceId != null) {
            writer.write(str + "\"ai.internal.trackingSourceId\":");
            writer.write(JsonHelper.convert(this.trackingSourceId));
            str = ",";
        }
        if (this.trackingType == null) {
            return str;
        }
        writer.write(str + "\"ai.internal.trackingType\":");
        writer.write(JsonHelper.convert(this.trackingType));
        return ",";
    }

    public void setAccountId(String str) {
        this.accountId = str;
    }

    public void setAgentVersion(String str) {
        this.agentVersion = str;
    }

    public void setApplicationName(String str) {
        this.applicationName = str;
    }

    public void setApplicationType(String str) {
        this.applicationType = str;
    }

    public void setDataCollectorReceivedTime(String str) {
        this.dataCollectorReceivedTime = str;
    }

    public void setFlowType(String str) {
        this.flowType = str;
    }

    public void setInstrumentationKey(String str) {
        this.instrumentationKey = str;
    }

    public void setIsAudit(String str) {
        this.isAudit = str;
    }

    public void setProfileClassId(String str) {
        this.profileClassId = str;
    }

    public void setProfileId(String str) {
        this.profileId = str;
    }

    public void setRequestSource(String str) {
        this.requestSource = str;
    }

    public void setSdkVersion(String str) {
        this.sdkVersion = str;
    }

    public void setTelemetryItemId(String str) {
        this.telemetryItemId = str;
    }

    public void setTrackingSourceId(String str) {
        this.trackingSourceId = str;
    }

    public void setTrackingType(String str) {
        this.trackingType = str;
    }
}
