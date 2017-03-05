package com.microsoft.telemetry.extensions;

import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;

public class app extends Extension implements IJsonSerializable {
    private String expId;
    private String userId;

    public app() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public String getExpId() {
        return this.expId;
    }

    public String getUserId() {
        return this.userId;
    }

    protected String serializeContent(Writer writer) throws IOException {
        String serializeContent = super.serializeContent(writer);
        if (this.expId != null) {
            writer.write(serializeContent + "\"expId\":");
            writer.write(JsonHelper.convert(this.expId));
            serializeContent = ",";
        }
        if (this.userId == null) {
            return serializeContent;
        }
        writer.write(serializeContent + "\"userId\":");
        writer.write(JsonHelper.convert(this.userId));
        return ",";
    }

    public void setExpId(String str) {
        this.expId = str;
    }

    public void setUserId(String str) {
        this.userId = str;
    }
}
