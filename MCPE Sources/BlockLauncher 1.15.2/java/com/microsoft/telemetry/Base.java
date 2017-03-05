package com.microsoft.telemetry;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class Base implements IJsonSerializable {
    public LinkedHashMap<String, String> Attributes = new LinkedHashMap();
    public String QualifiedName;
    private String baseType;

    public Base() {
        InitializeFields();
    }

    protected void InitializeFields() {
    }

    public String getBaseType() {
        return this.baseType;
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
        if (this.baseType == null) {
            return str;
        }
        writer.write(str + "\"baseType\":");
        writer.write(JsonHelper.convert(this.baseType));
        return ",";
    }

    public void setBaseType(String str) {
        this.baseType = str;
    }
}
