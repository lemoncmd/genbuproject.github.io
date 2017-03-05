package com.microsoft.telemetry.extensions;

import com.microsoft.telemetry.Extension;
import com.microsoft.telemetry.IJsonSerializable;
import com.microsoft.telemetry.JsonHelper;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class android extends Extension implements IJsonSerializable {
    private String libVer;
    private List<String> tickets;

    public android() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public String getLibVer() {
        return this.libVer;
    }

    public List<String> getTickets() {
        if (this.tickets == null) {
            this.tickets = new ArrayList();
        }
        return this.tickets;
    }

    protected String serializeContent(Writer writer) throws IOException {
        String serializeContent = super.serializeContent(writer);
        if (this.libVer != null) {
            writer.write(serializeContent + "\"libVer\":");
            writer.write(JsonHelper.convert(this.libVer));
            serializeContent = ",";
        }
        if (this.tickets == null) {
            return serializeContent;
        }
        writer.write(serializeContent + "\"tickets\":");
        JsonHelper.writeListString(writer, this.tickets);
        return ",";
    }

    public void setLibVer(String str) {
        this.libVer = str;
    }

    public void setTickets(List<String> list) {
        this.tickets = list;
    }
}
