package com.microsoft.telemetry.extensions;

import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;

public class os extends Extension implements IJsonSerializable {
    private String expId;
    private String locale;

    public os() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public String getExpId() {
        return this.expId;
    }

    public String getLocale() {
        return this.locale;
    }

    protected String serializeContent(Writer writer) throws IOException {
        String serializeContent = super.serializeContent(writer);
        if (this.locale != null) {
            writer.write(serializeContent + "\"locale\":");
            writer.write(JsonHelper.convert(this.locale));
            serializeContent = ",";
        }
        if (this.expId == null) {
            return serializeContent;
        }
        writer.write(serializeContent + "\"expId\":");
        writer.write(JsonHelper.convert(this.expId));
        return ",";
    }

    public void setExpId(String str) {
        this.expId = str;
    }

    public void setLocale(String str) {
        this.locale = str;
    }
}
