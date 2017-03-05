package Ms.Telemetry;

import com.microsoft.telemetry.Domain;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;

public class CllHeartBeat extends Domain implements IJsonSerializable {
    private int avgSettingsLatencyMs;
    private int avgVortexLatencyMs;
    private double cacheUsagePercent;
    private int eventsQueued;
    private String lastHeartBeat;
    private int logFailures;
    private int maxSettingsLatencyMs;
    private int maxVortexLatencyMs;
    private int quotaDropCount;
    private int rejectDropCount;
    private int settingsFailures4xx;
    private int settingsFailures5xx;
    private int settingsFailuresTimeout;
    private int settingsHttpAttempts;
    private int settingsHttpFailures;
    private int vortexFailures4xx;
    private int vortexFailures5xx;
    private int vortexFailuresTimeout;
    private int vortexHttpAttempts;
    private int vortexHttpFailures;

    public CllHeartBeat() {
        InitializeFields();
        SetupAttributes();
    }

    protected void InitializeFields() {
        this.QualifiedName = "Ms.Telemetry.CllHeartBeat";
    }

    public void SetupAttributes() {
        this.Attributes.put("Description", "This event is meant to be sent on a regular basis by all persistent in-process and out-of-process Logging Libraries.");
    }

    public int getAvgSettingsLatencyMs() {
        return this.avgSettingsLatencyMs;
    }

    public int getAvgVortexLatencyMs() {
        return this.avgVortexLatencyMs;
    }

    public double getCacheUsagePercent() {
        return this.cacheUsagePercent;
    }

    public int getEventsQueued() {
        return this.eventsQueued;
    }

    public String getLastHeartBeat() {
        return this.lastHeartBeat;
    }

    public int getLogFailures() {
        return this.logFailures;
    }

    public int getMaxSettingsLatencyMs() {
        return this.maxSettingsLatencyMs;
    }

    public int getMaxVortexLatencyMs() {
        return this.maxVortexLatencyMs;
    }

    public int getQuotaDropCount() {
        return this.quotaDropCount;
    }

    public int getRejectDropCount() {
        return this.rejectDropCount;
    }

    public int getSettingsFailures4xx() {
        return this.settingsFailures4xx;
    }

    public int getSettingsFailures5xx() {
        return this.settingsFailures5xx;
    }

    public int getSettingsFailuresTimeout() {
        return this.settingsFailuresTimeout;
    }

    public int getSettingsHttpAttempts() {
        return this.settingsHttpAttempts;
    }

    public int getSettingsHttpFailures() {
        return this.settingsHttpFailures;
    }

    public int getVortexFailures4xx() {
        return this.vortexFailures4xx;
    }

    public int getVortexFailures5xx() {
        return this.vortexFailures5xx;
    }

    public int getVortexFailuresTimeout() {
        return this.vortexFailuresTimeout;
    }

    public int getVortexHttpAttempts() {
        return this.vortexHttpAttempts;
    }

    public int getVortexHttpFailures() {
        return this.vortexHttpFailures;
    }

    protected String serializeContent(Writer writer) throws IOException {
        String serializeContent = super.serializeContent(writer);
        if (this.lastHeartBeat != null) {
            writer.write(serializeContent + "\"lastHeartBeat\":");
            writer.write(JsonHelper.convert(this.lastHeartBeat));
            serializeContent = ",";
        }
        if (this.eventsQueued != 0) {
            writer.write(serializeContent + "\"eventsQueued\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.eventsQueued)));
            serializeContent = ",";
        }
        if (this.logFailures != 0) {
            writer.write(serializeContent + "\"logFailures\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.logFailures)));
            serializeContent = ",";
        }
        if (this.quotaDropCount != 0) {
            writer.write(serializeContent + "\"quotaDropCount\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.quotaDropCount)));
            serializeContent = ",";
        }
        if (this.rejectDropCount != 0) {
            writer.write(serializeContent + "\"rejectDropCount\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.rejectDropCount)));
            serializeContent = ",";
        }
        if (this.vortexHttpAttempts != 0) {
            writer.write(serializeContent + "\"vortexHttpAttempts\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.vortexHttpAttempts)));
            serializeContent = ",";
        }
        if (this.vortexHttpFailures != 0) {
            writer.write(serializeContent + "\"vortexHttpFailures\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.vortexHttpFailures)));
            serializeContent = ",";
        }
        if (this.cacheUsagePercent > 0.0d) {
            writer.write(serializeContent + "\"cacheUsagePercent\":");
            writer.write(JsonHelper.convert(Double.valueOf(this.cacheUsagePercent)));
            serializeContent = ",";
        }
        if (this.avgVortexLatencyMs != 0) {
            writer.write(serializeContent + "\"avgVortexLatencyMs\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.avgVortexLatencyMs)));
            serializeContent = ",";
        }
        if (this.maxVortexLatencyMs != 0) {
            writer.write(serializeContent + "\"maxVortexLatencyMs\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.maxVortexLatencyMs)));
            serializeContent = ",";
        }
        if (this.settingsHttpAttempts != 0) {
            writer.write(serializeContent + "\"settingsHttpAttempts\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.settingsHttpAttempts)));
            serializeContent = ",";
        }
        if (this.settingsHttpFailures != 0) {
            writer.write(serializeContent + "\"settingsHttpFailures\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.settingsHttpFailures)));
            serializeContent = ",";
        }
        if (this.avgSettingsLatencyMs != 0) {
            writer.write(serializeContent + "\"avgSettingsLatencyMs\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.avgSettingsLatencyMs)));
            serializeContent = ",";
        }
        if (this.maxSettingsLatencyMs != 0) {
            writer.write(serializeContent + "\"maxSettingsLatencyMs\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.maxSettingsLatencyMs)));
            serializeContent = ",";
        }
        if (this.vortexFailures5xx != 0) {
            writer.write(serializeContent + "\"vortexFailures5xx\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.vortexFailures5xx)));
            serializeContent = ",";
        }
        if (this.vortexFailures4xx != 0) {
            writer.write(serializeContent + "\"vortexFailures4xx\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.vortexFailures4xx)));
            serializeContent = ",";
        }
        if (this.vortexFailuresTimeout != 0) {
            writer.write(serializeContent + "\"vortexFailuresTimeout\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.vortexFailuresTimeout)));
            serializeContent = ",";
        }
        if (this.settingsFailures5xx != 0) {
            writer.write(serializeContent + "\"settingsFailures5xx\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.settingsFailures5xx)));
            serializeContent = ",";
        }
        if (this.settingsFailures4xx != 0) {
            writer.write(serializeContent + "\"settingsFailures4xx\":");
            writer.write(JsonHelper.convert(Integer.valueOf(this.settingsFailures4xx)));
            serializeContent = ",";
        }
        if (this.settingsFailuresTimeout == 0) {
            return serializeContent;
        }
        writer.write(serializeContent + "\"settingsFailuresTimeout\":");
        writer.write(JsonHelper.convert(Integer.valueOf(this.settingsFailuresTimeout)));
        return ",";
    }

    public void setAvgSettingsLatencyMs(int i) {
        this.avgSettingsLatencyMs = i;
    }

    public void setAvgVortexLatencyMs(int i) {
        this.avgVortexLatencyMs = i;
    }

    public void setCacheUsagePercent(double d) {
        this.cacheUsagePercent = d;
    }

    public void setEventsQueued(int i) {
        this.eventsQueued = i;
    }

    public void setLastHeartBeat(String str) {
        this.lastHeartBeat = str;
    }

    public void setLogFailures(int i) {
        this.logFailures = i;
    }

    public void setMaxSettingsLatencyMs(int i) {
        this.maxSettingsLatencyMs = i;
    }

    public void setMaxVortexLatencyMs(int i) {
        this.maxVortexLatencyMs = i;
    }

    public void setQuotaDropCount(int i) {
        this.quotaDropCount = i;
    }

    public void setRejectDropCount(int i) {
        this.rejectDropCount = i;
    }

    public void setSettingsFailures4xx(int i) {
        this.settingsFailures4xx = i;
    }

    public void setSettingsFailures5xx(int i) {
        this.settingsFailures5xx = i;
    }

    public void setSettingsFailuresTimeout(int i) {
        this.settingsFailuresTimeout = i;
    }

    public void setSettingsHttpAttempts(int i) {
        this.settingsHttpAttempts = i;
    }

    public void setSettingsHttpFailures(int i) {
        this.settingsHttpFailures = i;
    }

    public void setVortexFailures4xx(int i) {
        this.vortexFailures4xx = i;
    }

    public void setVortexFailures5xx(int i) {
        this.vortexFailures5xx = i;
    }

    public void setVortexFailuresTimeout(int i) {
        this.vortexFailuresTimeout = i;
    }

    public void setVortexHttpAttempts(int i) {
        this.vortexHttpAttempts = i;
    }

    public void setVortexHttpFailures(int i) {
        this.vortexHttpFailures = i;
    }
}
