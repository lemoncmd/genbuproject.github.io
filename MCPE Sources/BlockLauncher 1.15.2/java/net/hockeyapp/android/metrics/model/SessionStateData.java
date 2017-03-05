package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import net.hockeyapp.android.metrics.JsonHelper;

public class SessionStateData extends TelemetryData {
    private SessionState state = SessionState.START;
    private int ver = 2;

    public SessionStateData() {
        InitializeFields();
        SetupAttributes();
    }

    protected void InitializeFields() {
        this.QualifiedName = "com.microsoft.applicationinsights.contracts.SessionStateData";
    }

    public void SetupAttributes() {
    }

    public String getBaseType() {
        return "SessionStateData";
    }

    public String getEnvelopeName() {
        return "Microsoft.ApplicationInsights.SessionState";
    }

    public Map<String, String> getProperties() {
        return null;
    }

    public SessionState getState() {
        return this.state;
    }

    public int getVer() {
        return this.ver;
    }

    protected String serializeContent(Writer writer) throws IOException {
        writer.write(super.serializeContent(writer) + "\"ver\":");
        writer.write(JsonHelper.convert(Integer.valueOf(this.ver)));
        writer.write("," + "\"state\":");
        writer.write(JsonHelper.convert(Integer.valueOf(this.state.getValue())));
        return ",";
    }

    public void setProperties(Map<String, String> map) {
    }

    public void setState(SessionState sessionState) {
        this.state = sessionState;
    }

    public void setVer(int i) {
        this.ver = i;
    }
}
