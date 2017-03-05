package com.microsoft.telemetry;

import java.io.IOException;
import java.io.Writer;

public class Data<TDomain extends Domain> extends Base implements ITelemetryData {
    private TDomain baseData;

    public Data() {
        InitializeFields();
        SetupAttributes();
    }

    protected void InitializeFields() {
        this.QualifiedName = "com.microsoft.telemetry.Data";
    }

    public void SetupAttributes() {
        this.Attributes.put("Description", "Data struct to contain both B and C sections.");
    }

    public TDomain getBaseData() {
        return this.baseData;
    }

    protected String serializeContent(Writer writer) throws IOException {
        writer.write(super.serializeContent(writer) + "\"baseData\":");
        JsonHelper.writeJsonSerializable(writer, this.baseData);
        return ",";
    }

    public void setBaseData(TDomain tDomain) {
        this.baseData = tDomain;
    }
}
