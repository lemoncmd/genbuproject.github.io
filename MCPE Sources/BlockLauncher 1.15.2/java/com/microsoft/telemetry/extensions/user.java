package com.microsoft.telemetry.extensions;

import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;

public class user extends Extension implements IJsonSerializable {
    private String authId;
    private String id;
    private String localId;

    public user() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public String getAuthId() {
        return this.authId;
    }

    public String getId() {
        return this.id;
    }

    public String getLocalId() {
        return this.localId;
    }

    protected String serializeContent(Writer writer) throws IOException {
        String serializeContent = super.serializeContent(writer);
        if (this.id != null) {
            writer.write(serializeContent + "\"id\":");
            writer.write(JsonHelper.convert(this.id));
            serializeContent = ",";
        }
        if (this.localId != null) {
            writer.write(serializeContent + "\"localId\":");
            writer.write(JsonHelper.convert(this.localId));
            serializeContent = ",";
        }
        if (this.authId == null) {
            return serializeContent;
        }
        writer.write(serializeContent + "\"authId\":");
        writer.write(JsonHelper.convert(this.authId));
        return ",";
    }

    public void setAuthId(String str) {
        this.authId = str;
    }

    public void setId(String str) {
        this.id = str;
    }

    public void setLocalId(String str) {
        this.localId = str;
    }
}
