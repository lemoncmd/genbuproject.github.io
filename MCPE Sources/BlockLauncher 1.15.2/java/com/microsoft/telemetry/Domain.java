package com.microsoft.telemetry;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import net.hockeyapp.android.BuildConfig;
import org.mozilla.javascript.Token;

public class Domain implements IJsonSerializable {
    public LinkedHashMap<String, String> Attributes = new LinkedHashMap();
    public String QualifiedName;

    public Domain() {
        InitializeFields();
    }

    protected void InitializeFields() {
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
        return BuildConfig.FLAVOR;
    }
}
